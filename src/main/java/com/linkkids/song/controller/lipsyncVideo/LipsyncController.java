package com.linkkids.song.controller.lipsyncVideo;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import com.linkkids.song.common.model.user.UserModel;
import com.linkkids.song.controller.media.MediaService;
import com.linkkids.song.dto.song.SongListDTO;
import com.linkkids.song.dto.video.CheckFaceRequestDTO;
import com.linkkids.song.dto.video.GenerateVideoRequestDTO;
import com.linkkids.song.dto.video.UserVideoRequestDTO;
import com.linkkids.song.dto.video.VideoListDTO;

@Controller
@RequestMapping("/video")
public class LipsyncController {

    @Autowired
    private MediaService mediaService;
    @Autowired
    private LipsyncService lipsyncService;
    @Value("${generate.api}")
    private String api;

    // *** 동영상 생성 관련 *** //

    // 동영상 생성 페이지
    @GetMapping("/generate")
    public ModelAndView generateVideo(Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("video/generate");

        List<SongListDTO> userSongs = lipsyncService.getUserSongs(UserModel.getCurrentUser(true).getUserSeq());

        modelAndView.addObject("username", UserModel.getCurrentUser(true).getUsername());
        modelAndView.addObject("userSongs", userSongs);
        modelAndView.addObject("selectedSongId", model.getAttribute("selectedSongId"));

        return modelAndView;
    }

    @PostMapping("/generate/select")
    public String selectSongAndRedirect(@RequestParam("songId") Long songId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("selectedSongId", songId);
        return "redirect:/video/generate";
    }

    // 동영상 생성 호출
    @PostMapping("/generate/new")
    public ResponseEntity<String> createVideo(@RequestBody UserVideoRequestDTO requestDTO) {
        CheckFaceRequestDTO checkFaceDTO = new CheckFaceRequestDTO();

        checkFaceDTO.setUserId(UserModel.getCurrentUser(true).getUserSeq().toString());
        checkFaceDTO.setWorkId(requestDTO.getSongId().toString());
        checkFaceDTO.setImageUrl(api + mediaService.getMediaPathById(requestDTO.getInputImageMediaId()));
        checkFaceDTO.setCharacterType("human");

        Long vocalMediaId = lipsyncService.getFullMediaId(requestDTO.getSongId());
        Long bgmMediaId = lipsyncService.getVocalMediaId(requestDTO.getSongId());

        GenerateVideoRequestDTO generateVideoRequestDTO = lipsyncService.checkFace(checkFaceDTO, vocalMediaId,
                bgmMediaId);
        if (generateVideoRequestDTO == null) {
            return ResponseEntity.status(500).body("얼굴 확인 실패");
        }

        String videoUrl = lipsyncService.createVideo(generateVideoRequestDTO);

        // 비디오 다운로드
        if (videoUrl == null) {
            return ResponseEntity.status(500).body("비디오 생성 실패");
        }

        lipsyncService.downloadVideo(videoUrl, requestDTO.getSongId(),
                UserModel.getCurrentUser(true).getUserSeq(), requestDTO.getInputImageMediaId(), requestDTO.getTitle());

        return ResponseEntity.ok("success");
    }

    // *** 동영상 리스트 관련 *** //

    // 동영상 리스트 페이지 (초기 로드)
    // 월별 정렬 기준
    @GetMapping("/list")
    public ModelAndView getVideoList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("video/list");

        Map<String, Long> monthList = lipsyncService.getMonthList();

        modelAndView.addObject("headerList", monthList);
        modelAndView.addObject("username", UserModel.getCurrentUser(true).getUsername());

        return modelAndView;
    }

    // 정렬 기준에 따른 header list 가져오기
    @GetMapping("/list/headers")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getHeaderList(@RequestParam String sort) {
        Long userSeq = UserModel.getCurrentUser(true).getUserSeq();
        Map<String, Long> headerList;
        if (sort.equals("song")) {
            headerList = lipsyncService.getSongTitleList(userSeq);
        } else {
            headerList = lipsyncService.getMonthList();
        }
        return ResponseEntity.ok(headerList);
    }

    // 월별 비디오 content 가져오기
    @GetMapping("/list/month")
    public String getVideosByMonth(@RequestParam Long m, Model model) {
        int monthInt;
        if (m == -1L) {
            monthInt = 0;
        } else {
            monthInt = (int) (long) m;
        }
        Long userSeq = UserModel.getCurrentUser(true).getUserSeq();
        List<VideoListDTO> videos = lipsyncService.getVideosByMonth(userSeq, monthInt);
        model.addAttribute("videos", videos);
        return "video/scroll-content :: videoItems";
    }

    // 노래별 비디오 content 가져오기
    @GetMapping("/list/song")
    public String getVideoListBySong(@RequestParam Long songId, Model model) {
        List<VideoListDTO> videos = lipsyncService.getVideosBySong(songId);
        model.addAttribute("videos", videos);
        return "video/scroll-content :: videoItems";
    }

    // 동영상 다운로드
    @GetMapping("/download/{lipsyncId}")
    public ResponseEntity<Resource> downloadVideo(@PathVariable Long lipsyncId) throws MalformedURLException {

        boolean check = lipsyncService.checkUserVideoAccess(lipsyncId);
        if (!check) {
            return ResponseEntity.status(403).build();
        }

        Path videoPath = Path.of(lipsyncService.getVideoFilePath(lipsyncId)).toAbsolutePath();
        if (videoPath == null) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(videoPath.toUri());

        String downloadName = "link_kids_songs.mp4";
        String encodedDownloadName = UriUtils.encode(downloadName, StandardCharsets.UTF_8);

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + encodedDownloadName + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 동영상 삭제하기
    @DeleteMapping("/delete/{lipsyncId}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long lipsyncId) {
        boolean check = lipsyncService.checkUserVideoAccess(lipsyncId);
        if (!check) {
            return ResponseEntity.status(403).body("권한 없음");
        }
        lipsyncService.deleteVideo(lipsyncId);
        return ResponseEntity.ok().build();
    }

    // *** 동영상 full screen 재생 *** //
    @GetMapping("/play")
    public ModelAndView playVideo(@RequestParam String videoTitle, @RequestParam Long lipsyncId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("video/fullplayer-video");

        boolean check = lipsyncService.checkUserVideoAccess(lipsyncId);
        if (!check) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        String videoFilePath = lipsyncService.getVideoFilePath(lipsyncId);
        videoFilePath = videoFilePath.replace("\\", "/");
        modelAndView.addObject("videoFilePath", videoFilePath);
        modelAndView.addObject("videoTitle", videoTitle);
        modelAndView.addObject("lipsyncId", lipsyncId);
        modelAndView.addObject("username", UserModel.getCurrentUser(true).getUsername());
        return modelAndView;
    }
}
