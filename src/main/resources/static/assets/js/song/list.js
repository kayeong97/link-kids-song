// 노래 리스트 페이지

// *** play 전역 변수 *** //
let currentAudio = null;
let isPlaying = false;
let currentSongIndex = -1;
let songsList = [];

// *** play 함수들 *** //
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
    currentAudio.currentTime = 0;
  }

  // 새 오디오 생성
  currentAudio = new Audio(songUrl);
  currentSongIndex = songIndex;

  // DOM 요소 가져오기
  const totalTimeSpan = document.getElementById("total-time");
  const currentTimeSpan = document.getElementById("current-time");
  const progressBar = document.getElementById("progress-bar");

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

  // 노래 종료 시 다음 곡 재생
  currentAudio.addEventListener("ended", () => {
    playNextSong();
  });

  // 에러 처리
  currentAudio.addEventListener("error", (e) => {
    alert("노래를 재생할 수 없습니다.");
  });

  return new Promise((resolve, reject) => {
    const onReady = () => {
      cleanup();
      resolve();
    };
    const onError = (e) => {
      cleanup();
      reject(e);
    };
    const cleanup = () => {
      currentAudio.removeEventListener("canplay", onReady);
      currentAudio.removeEventListener("error", onError);
    };
    currentAudio.addEventListener("canplay", onReady, { once: true });
    currentAudio.addEventListener("error", onError, { once: true });

    currentAudio.load();
  });
}

// 재생
function playSong() {
  if (currentAudio) {
    const playIcon = document.getElementById("play-icon");
    const pauseIcon = document.getElementById("pause-icon");

    currentAudio.play();
    isPlaying = true;
    if (playIcon) playIcon.style.display = "none";
    if (pauseIcon) pauseIcon.style.display = "block";
  }
}

// 일시정지
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

// *** list *** //

$(function () {
  const playPausebutton = document.getElementById("play-pause-button");
  const preSongPlaybutton = document.getElementById("pre-song-play-button");
  const nextSongPlaybutton = document.getElementById("next-song-play-button");
  const progressBar = document.getElementById("progress-bar");

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

  // 초기 정렬 버튼 활성화
  updateSortButtons();
  updatePagination();

  // 정렬 버튼 이벤트
  $("#sort-newest").on("click", function () {
    loadPage(1, "newest");
  });

  $("#sort-oldest").on("click", function () {
    loadPage(1, "oldest");
  });

  $("#sort-title").on("click", function () {
    loadPage(1, "title");
  });

  // 이전 페이지
  $("#prev-page").on("click", function () {
    if (currentPage > 1) {
      loadPage(currentPage - 1, currentSortType);
    }
  });

  // 다음 페이지
  $("#next-page").on("click", function () {
    if (currentPage < totalPages) {
      loadPage(currentPage + 1, currentSortType);
    }
  });

  // 페이지 번호 클릭
  $(document).on("click", ".page-number", function () {
    const page = parseInt($(this).data("page"));
    loadPage(page, currentSortType);
  });

  // 노래 다운로드
  $(document).on("click", ".save-song-button", function () {
    const songUrl = $(this).data("song-url");
    const songTitle = $(this).data("song-title");
    if (songUrl && songTitle) {
      const link = document.createElement("a");
      link.href = songUrl;
      link.download = songTitle + ".mp3";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  });

  // 노래 제목 클릭 (전체화면 재생)
  $(document).on("click", ".play-song-fullscreen", function (e) {
    e.preventDefault();
    const songId = $(this).data("song-id");

    const form = $("<form>", {
      method: "POST",
      action: "/song/prepare-play",
    });
    $("<input>")
      .attr({
        type: "hidden",
        name: "songId",
        value: songId,
      })
      .appendTo(form);
    $("body").append(form);
    form.submit();
  });

  // 노래 재생
  $(document).on("click", ".play-song-button", function () {
    if (isPlaying) {
      pauseSong();
    }
    const songUrl = $(this).data("song-url");
    const songIndex = $(this).data("song-index");
    const songTitle = $(this).data("song-title");

    if (songUrl) {
      updateSongsList();
      loadSong(songUrl, songIndex);
      playSong();
      $(".play-song").addClass("active");
    }
  });

  // 더보기 버튼
  $(document).on("click", ".more-options-button", function () {
    const menuLayout = $(".menu-layout");
    const button = $(this);
    const songId = button.data("song-id");
    const songTitle = button.data("song-title");

    if (menuLayout.hasClass("active")) {
      menuLayout.removeClass("active");
    } else {
      const buttonOffset = button.offset();
      const buttonHeight = button.outerHeight();

      menuLayout.css({
        top: buttonOffset.top + buttonHeight + 5 + "px",
        left: buttonOffset.left + "px",
      });

      $("#remove-button").data("songId", songId);
      $("#remove-button").data("songTitle", songTitle);
      $("#generate-video-button").data("songId", songId);

      menuLayout.addClass("active");
    }
  });

  // 노래 다운로드
  $(document).on("click", "#save-song-button", function () {
    $.ajax({
      url: `/song/download/${$(this).data("songId")}`,
      type: "GET",
      xhrFields: {
        responseType: "blob",
      },
      success: function (data, status, xhr) {
        let fileName = "downloaded_song.mp3";
        var blob = new Blob([data], { type: "audio/mpeg" });
        var link = document.createElement("a");
        var url = window.URL.createObjectURL(blob);

        link.href = url;
        link.download = fileName;

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      },
      error: function (xhr, status, error) {
        alert("노래 다운로드에 실패했습니다.");
      },
    });
  });

  // 동영상 생성하기
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

  // 노래 삭제
  $(document).on("click", "#remove-button", function () {
    const songId = $(this).data("songId");
    const songTitle = $(this).data("songTitle");

    Swal.fire({
      title: "노래를 삭제하시겠습니까?",
      text: "삭제된 노래는 복구할 수 없습니다.",
      icon: "warning",
      input: "text",
      inputPlaceholder: "삭제하려면 노래의 제목을 입력하세요.",
      inputAttributes: {
        autocapitalize: "off",
      },
      backdrop: "rgba(0, 0, 0, 0.7)",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "삭제",
      cancelButtonText: "취소",
    }).then((result) => {
      if (result.isConfirmed) {
        // 입력한 제목과 실제 제목 비교
        if (result.value !== songTitle) {
          Swal.fire({
            title: "삭제 실패",
            text: "제목이 일치하지 않습니다. 다시 시도해주세요.",
            backdrop: "rgba(0, 0, 0, 0.7)",
            icon: "error",
          });
          return;
        }
        $.ajax({
          url: `/song/delete/${songId}`,
          type: "DELETE",
          success: function () {
            Swal.fire({
              title: "삭제 완료!",
              text: "노래가 성공적으로 삭제되었습니다.",
              backdrop: "rgba(0, 0, 0, 0.7)",
              icon: "success",
            });
            loadPage(currentPage, currentSortType);
          },
          error: function (xhr, status, error) {
            Swal.fire({
              title: "삭제 실패",
              text: "제목이 일치하지 않습니다. 다시 시도해주세요.",
              backdrop: "rgba(0, 0, 0, 0.7)",
              icon: "error",
            });
          },
        });
      }
    });
    $(".menu-layout").removeClass("active");
  });

  // 메뉴 및 재생바 닫기
  $(document).on("click", function (e) {
    if (!$(e.target).closest(".more-options-button, .menu-layout").length) {
      $(".menu-layout").removeClass("active");
    }
    if (!$(e.target).closest(".play-song, .play-song-button").length) {
      pauseSong();
      currentAudio = null;
      isPlaying = false;
      $(".play-song").removeClass("active");
    }
  });
});

// 페이지 로드
function loadPage(page, sortType) {
  $.ajax({
    url: "/song/list/page",
    type: "GET",
    data: { page: page, sortType: sortType },
    success: function (data) {
      $(".song-list-table tbody").html(data);
      currentPage = page;
      currentSortType = sortType;
      updatePagination();
      updateSortButtons();
      updateSongsList();
    },
    error: function (xhr, status, error) {
      console.error("페이지 로드 오류:", error);
    },
  });
}

// 페이지
function updatePagination() {
  $(".page-number").removeClass("active");
  $('.page-number[data-page="' + currentPage + '"]').addClass("active");
  $("#prev-page").prop("disabled", currentPage === 1);
  $("#next-page").prop("disabled", currentPage >= totalPages);
}

function updateSortButtons() {
  $("#sort-newest, #sort-oldest, #sort-title").removeClass("active");
  if (currentSortType === "newest") {
    $("#sort-newest").addClass("active");
  } else if (currentSortType === "oldest") {
    $("#sort-oldest").addClass("active");
  } else if (currentSortType === "title") {
    $("#sort-title").addClass("active");
  }
}

function updateSongsList() {
  songsList = [];
  $(".play-song-button").each(function () {
    songsList.push({
      url: $(this).data("song-url"),
      title: $(this).data("song-title"),
    });
  });
}
