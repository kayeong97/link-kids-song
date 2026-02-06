// 전체 화면 노래 재생 페이지

// *** 노래 재생 *** //
let currentAudio = null;
let isPlaying = false;

// 시간 포맷 함수 (초 -> mm:ss)
function formatTime(seconds) {
  if (isNaN(seconds) || seconds === null || seconds === undefined)
    return "00:00";
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
}

// 노래 로드
function loadSong(songUrl) {
  if (currentAudio) {
    currentAudio.pause();
    currentAudio.currentTime = 0;
    currentAudio.src = "";
  }

  // 새 오디오 생성
  currentAudio = new Audio(songUrl);
  const totalTimeSpan = document.getElementById("total-time");
  const currentTimeSpan = document.getElementById("current-time");
  const progressBar = document.getElementById("progress-bar");

  // 초기화
  if (currentTimeSpan) currentTimeSpan.textContent = "00:00";
  if (totalTimeSpan) totalTimeSpan.textContent = "00:00";
  if (progressBar) {
    progressBar.value = 0;
    progressBar.max = 0;
  }

  // 전체 시간 설정
  currentAudio.addEventListener("loadedmetadata", () => {
    if (totalTimeSpan)
      totalTimeSpan.textContent = formatTime(currentAudio.duration);
    if (progressBar) progressBar.max = Math.floor(currentAudio.duration);
  });

  // 재생 중 progress bar 업데이트
  currentAudio.addEventListener("timeupdate", () => {
    if (currentTimeSpan)
      currentTimeSpan.textContent = formatTime(currentAudio.currentTime);
    if (progressBar) progressBar.value = Math.floor(currentAudio.currentTime);
  });

  // 에러 처리
  currentAudio.addEventListener("error", (e) => {
    alert("노래를 재생할 수 없습니다. URL: " + songUrl);
  });
}

// 재생
function playSong() {
  if (currentAudio) {
    const playIcon = document.getElementById("play-icon");
    const pauseIcon = document.getElementById("pause-icon");

    currentAudio
      .play()
      .then(() => {
        isPlaying = true;
        if (playIcon) playIcon.style.display = "none";
        if (pauseIcon) pauseIcon.style.display = "block";
      })
      .catch((error) => {
        alert("재생에 실패했습니다: " + error.message);
      });
  }
}

// 일시 정지
function pauseSong() {
  if (currentAudio) {
    const playIcon = document.getElementById("play-icon");
    const pauseIcon = document.getElementById("pause-icon");

    currentAudio.pause();
    isPlaying = false;
    if (playIcon) playIcon.style.display = "block";
    if (pauseIcon) pauseIcon.style.display = "none";
  }
}

// 재생/일시정지 토글
function togglePlayPause() {
  if (isPlaying) {
    pauseSong();
  } else {
    playSong();
  }
}

// 이전곡 재생 - 제일 앞으로 이동
function playPreviousSong() {
  currentAudio.currentTime = 0;
}

// 다음곡 재생 - 노래 종료
function playNextSong() {
  pauseSong();
  currentAudio.currentTime = 0;
}

$(function () {
  const songUrl = window.songUrl;

  // fullplayer에서는 재생 컨트롤을 항상 표시
  $(".play-song").addClass("active");

  if (songUrl) {
    loadSong(songUrl);
    setTimeout(() => playSong(), 500);
  } else {
    alert("재생할 수 없습니다");
  }

  const playPausebutton = document.getElementById("play-pause-button");
  const preSongPlaybutton = document.getElementById("pre-song-play-button");
  const nextSongPlaybutton = document.getElementById("next-song-play-button");
  const progressBar = document.getElementById("progress-bar");

  if (playPausebutton) {
    playPausebutton.addEventListener("click", togglePlayPause);
  }
  if (preSongPlaybutton) {
    preSongPlaybutton.addEventListener("click", playPreviousSong);
  }
  if (nextSongPlaybutton) {
    nextSongPlaybutton.addEventListener("click", playNextSong);
  }
  if (progressBar) {
    progressBar.addEventListener("input", () => {
      if (currentAudio) {
        currentAudio.currentTime = progressBar.value;
      }
    });
  }

  /* 동영상 생성하기 */
  $(document).on("click", "#generate-video-button", function () {
    const songId = $(this).data("songId");
    if (songId) {
      const form = $("<form>", {
        method: "POST",
        action: "/video/generate/select",
      });
      form.append(
        $("<input>", {
          type: "hidden",
          name: "songId",
          value: songId,
        }),
      );
      $("body").append(form);
      form.submit();
    }
  });

  /* 뒤로가기 버튼 */
  const goBackbutton = document.getElementById("back-button");
  if (goBackbutton) {
    goBackbutton.addEventListener("click", () => {
      window.history.back();
    });
  }

  /* 가사 표시/숨기기 */
  const lyricsShowbutton = document.getElementById("lyrics-show");
  const lyricsHidebutton = document.getElementById("lyrics-hide");
  const lyricsContainer = document.getElementById("song-lyrics");

  if (lyricsHidebutton) {
    lyricsHidebutton.classList.add("hidden");
  }

  if (lyricsShowbutton && lyricsHidebutton && lyricsContainer) {
    lyricsShowbutton.addEventListener("click", () => {
      lyricsContainer.style.display = "flex";
      lyricsContainer.style.alignContent = "center";
      lyricsContainer.style.justifyContent = "center";
      lyricsShowbutton.classList.add("hidden");
      lyricsHidebutton.classList.remove("hidden");
    });
    lyricsHidebutton.addEventListener("click", () => {
      lyricsContainer.style.display = "none";
      lyricsHidebutton.classList.add("hidden");
      lyricsShowbutton.classList.remove("hidden");
    });
  }
});
