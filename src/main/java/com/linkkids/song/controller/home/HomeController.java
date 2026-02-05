package com.linkkids.song.controller.home;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.linkkids.song.common.model.user.UserModel;
import com.linkkids.song.dto.song.SongListDTO;
import com.linkkids.song.dto.video.VideoListDTO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    HomeService homeService;

    // 홈 화면 로딩
    @GetMapping("")
    public ModelAndView home(HttpSession session) {
        ModelAndView model = new ModelAndView();
        model.setViewName("home/home");

        Long userSeq = UserModel.getCurrentUser(true).getUserSeq();
        if (userSeq == null || userSeq <= 0) {
            model.setViewName("redirect:/");
            return model;
        }
        String username = UserModel.getCurrentUser(true).getUsername();

        // 유저 정보
        model.addObject("username", username);

        // 생성 대기중 노래 목록 조회
        List<String> generationSongReadyList = homeService.getGenerationSongReadyList(userSeq);
        model.addObject("generationSongReadyList", generationSongReadyList);

        // 생성 대기중 개수 조회
        int generationSongReadyCount = generationSongReadyList.size();

        // 최근 노래 목록 조회
        List<SongListDTO> recentSongs = homeService.getRecentSongs(userSeq, 5);
        model.addObject("recentSongs", recentSongs);

        // 최근 영상 목록 조회
        List<VideoListDTO> recentVideos = homeService.getRecentVideos(userSeq, 5);
        model.addObject("recentVideos", recentVideos);

        model.addObject("generationSongReadyCount", generationSongReadyCount);

        return model;
    }
}
