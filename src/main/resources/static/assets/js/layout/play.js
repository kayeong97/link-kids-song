// 노래 재생
let currentAudio = null;
let isPlaying = false;
let currentSongIndex = -1;
let songsList = [];

// DOM 요소
const playPausebutton = document.getElementById("play-pause-button");
const preSongPlaybutton = document.getElementById("pre-song-play-button");
const nextSongPlaybutton = document.getElementById("next-song-play-button");
const progressBar = document.getElementById("progress-bar");
const currentTimeSpan = document.getElementById("current-time");
const totalTimeSpan = document.getElementById("total-time");
const playIcon = document.getElementById("play-icon");
const pauseIcon = document.getElementById("pause-icon");

// 시간 포맷 함수 (초 -> mm:ss)
function formatTime(seconds) {
  if (isNaN(seconds)) return "00:00";
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
}

// 노래 로드
function loadSong(songUrl, songIndex) {
  // 기존 오디오 정리
  if (currentAudio) {
    currentAudio.pause();
    currentAudio.src = "";
  }

  // 새 오디오 생성
  currentAudio = new Audio(songUrl);
  currentSongIndex = songIndex;

  // 메타데이터 로드 완료 시 전체 시간 설정
  currentAudio.addEventListener("loadedmetadata", () => {
    totalTimeSpan.textContent = formatTime(currentAudio.duration);
    progressBar.max = Math.floor(currentAudio.duration);
  });

  // 재생 중 progress bar 업데이트
  currentAudio.addEventListener("timeupdate", () => {
    currentTimeSpan.textContent = formatTime(currentAudio.currentTime);
    progressBar.value = Math.floor(currentAudio.currentTime);
  });

  // 노래 종료 시 다음 곡 재생
  currentAudio.addEventListener("ended", () => {
    playNextSong();
  });

  // 에러 처리
  currentAudio.addEventListener("error", (e) => {
    console.error("오디오 로드 에러:", e);
    alert("노래를 재생할 수 없습니다.");
  });
}

// 재생
function playSong() {
  if (currentAudio) {
    currentAudio.play();
    isPlaying = true;
    if (playIcon) playIcon.style.display = "none";
    if (pauseIcon) pauseIcon.style.display = "block";
  }
}

// 일시정지
function pauseSong() {
  if (currentAudio) {
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

// 이전 곡
function playPreviousSong() {
  if (currentSongIndex > 0) {
    currentSongIndex--;
    const prevSong = songsList[currentSongIndex];
    loadSong(prevSong.url, currentSongIndex);
    playSong();
  }
}

// 다음 곡
function playNextSong() {
  if (currentSongIndex < songsList.length - 1) {
    currentSongIndex++;
    const nextSong = songsList[currentSongIndex];
    loadSong(nextSong.url, currentSongIndex);
    playSong();
  } else {
    // 마지막 곡이면 정지
    pauseSong();
    currentAudio.currentTime = 0;
  }
}

// 재생/일시정지 버튼 클릭
if (playPausebutton) {
  playPausebutton.addEventListener("click", togglePlayPause);
}

// 이전 곡 버튼 클릭
if (preSongPlaybutton) {
  preSongPlaybutton.addEventListener("click", playPreviousSong);
}

// 다음 곡 버튼 클릭
if (nextSongPlaybutton) {
  nextSongPlaybutton.addEventListener("click", playNextSong);
}

// progress bar 클릭/드래그
if (progressBar) {
  progressBar.addEventListener("input", () => {
    if (currentAudio) {
      currentAudio.currentTime = progressBar.value;
    }
  });
}
