package com.linkkids.song.controller.song;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriUtils;

import com.linkkids.song.common.model.user.UserModel;
import com.linkkids.song.common.util.CategoryUtil;
import com.linkkids.song.common.util.MoodUtil;
import com.linkkids.song.dto.song.SongListDTO;
import com.linkkids.song.dto.song.UserSongRequestDTO;

@Controller
@RequestMapping("/song")
public class SongController {
    @Autowired
    private SongService songService;

    // *** 노래 리스트 관련 *** //

    // 노래 리스트 페이지 (초기 로드)
    @GetMapping("/list")
    public ModelAndView getSongList() {
        ModelAndView model = new ModelAndView();
        model.setViewName("song/list");

        Long userSeq = UserModel.getCurrentUser(true).getUserSeq();
        if (userSeq == 0) {
            model.setViewName("redirect:/");
            return model;
        }

        int pageSize = 5;
        int totalCount = songService.getAllSongCount(userSeq);
        int totalPage = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPage == 0)
            totalPage = 1;

        List<SongListDTO> songs = songService.getSongsByPage(1, "newest");
        model.addObject("songs", songs);
        model.addObject("username", UserModel.getCurrentUser(true).getUsername());
        model.addObject("totalPage", totalPage);

        return model;
    }

    // 노래 리스트 로드
    @GetMapping("/list/page")
    public String getSongsByPage(@RequestParam int page, @RequestParam String sortType, Model model) {
        Long userSeq = UserModel.getCurrentUser(true).getUserSeq();
        int pageSize = 5;
        int totalCount = songService.getAllSongCount(userSeq);
        int maxPage = (int) Math.ceil((double) totalCount / pageSize);
        if (maxPage == 0)
            maxPage = 1;

        // 페이지 범위 검증
        if (page < 1)
            page = 1;
        if (page > maxPage)
            page = maxPage;

        List<SongListDTO> songs = songService.getSongsByPage(page, sortType);
        model.addAttribute("songs", songs);
        return "song/list-content :: songList";
    }

    // 전체 노래 개수 (현재 사용자)
    @GetMapping("/count/all")
    public ResponseEntity<Integer> getAllSongCount() {
        Long userSeq = UserModel.getCurrentUser(true).getUserSeq();
        int count = songService.getAllSongCount(userSeq);
        return ResponseEntity.ok(count);
    }

    // 노래 삭제하기
    @DeleteMapping("/delete/{songId}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long songId) {
        songService.deleteSong(songId);
        return ResponseEntity.ok().build();
    }

    // 노래 다운로드하기
    @GetMapping("/download/{songId}")
    public ResponseEntity<Resource> downloadSong(@PathVariable Long songId) throws MalformedURLException {
        Path filePath = Path.of(songService.getSongFilePath(songId)).toAbsolutePath();
        Resource resource = new UrlResource(filePath.toUri());

        String downloadName = "link_kids_song.mp3";
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

    // *** 노래 생성 관련 *** //

    // 노래 생성 페이지
    @GetMapping("/generate")
    public ModelAndView getSongGeneration() {
        ModelAndView model = new ModelAndView();
        model.setViewName("song/generate");

        Long userSeq = UserModel.getCurrentUser(true).getUserSeq();
        if (userSeq <= 0) {
            model.setViewName("redirect:/");
            return model;
        }

        model.addObject("userSeq", userSeq);
        model.addObject("username", UserModel.getCurrentUser(true).getUsername());

        // 분위기 리스트
        model.addObject("moodList", MoodUtil.getMoodList());

        // 카테고리 리스트
        model.addObject("categoryList", CategoryUtil.getCategoryList());

        return model;
    }

    // 노래 생성하기
    @PostMapping("/generate/new")
    public ResponseEntity<String> createSong(@RequestBody UserSongRequestDTO requestDTO) {
        Long userSeq = UserModel.getCurrentUser(true).getUserSeq();
        requestDTO.setUserSeq(userSeq); // userSeq 설정
        String jobId = songService.createSong(requestDTO, userSeq);
        return ResponseEntity.ok(jobId);
    }

    // *** 노래 full screen 재생 ***//

    // 노래 전체 재생 페이지
    @GetMapping("/play")
    public ModelAndView playSong(@RequestParam String songTitle, @RequestParam Long songId) {
        ModelAndView model = new ModelAndView();
        model.setViewName("song/fullplayer");

        SongListDTO songInfo = songService.getSongInfo(songId);
        String thumbnailUrl = songInfo.getCoverMediaUrl();
        String fullAudioUrl = songInfo.getFullAudioUrl();
        String songLyrics = songService.getSongLyrics(songId);

        model.addObject("username", UserModel.getCurrentUser(true).getUsername());
        model.addObject("songId", songId);
        model.addObject("songTitle", songTitle);
        model.addObject("thumbnailUrl", thumbnailUrl);
        model.addObject("fullAudioUrl", fullAudioUrl);
        model.addObject("songLyrics", songLyrics);

        return model;
    }
}
