$(document).ready(function () {
  let currentStep = 1;
  let songData = {};

  // 초기 step 1 표시
  showStep(1);

  // step 표시 함수
  function showStep(step) {
    $(".generate-step").removeClass("active");
    $(`.generate-step[data-step="${step}"]`).addClass("active");

    // stepper 표시 업데이트
    $(".stepper-list li").removeClass("active");
    $(`.step${step}`).addClass("active");
    // 5단계일 때 song-info 숨기기
    if (step === 5) {
      $(".song-info").hide();
      $(".song-info-description").hide();
      $(".lyrics-display-container").removeClass("step5-active");
      $(".generate-container").removeClass("step5-active");
    } else {
      $(".song-info").show();
      $(".song-info-description").show();
      $(".lyrics-display-container").addClass("step5-active");
      $(".generate-container").addClass("step5-active");
    }
    currentStep = step;
  }

  // 분위기 선택
  $(".song-mood ul").on("click", function (e) {
    const btn = $(e.target).closest("button.mood-select");
    if (!btn.length) return;

    $(".song-mood .mood-select").attr("aria-pressed", "false");
    btn.attr("aria-pressed", "true");
    songData.mood = btn.data("mood");
  });

  // 카테고리 선택
  $(".song-category ul").on("click", function (e) {
    const btn = $(e.target).closest("button.category-select");
    if (!btn.length) return;

    $(".song-category .category-select").attr("aria-pressed", "false");
    btn.attr("aria-pressed", "true");
    songData.category = btn.data("category");
  });

  // 커버 이미지 미리보기
  $("#song-media").on("change", function (e) {
    const file = e.target.files && e.target.files[0];
    if (!file) return;

    // 미리보기
    const reader = new FileReader();
    reader.onload = function (event) {
      $("#cover-image").attr("src", event.target.result).show();
      $(".song-media-upload").addClass("uploaded");
    };
    reader.readAsDataURL(file);
  });

  // Step 1 -> Step 2 (곡 기초 정보 입력 완료)
  $("#to-step-2").on("click", function () {
    // 데이터 수집
    songData.title = $("#song-title").val();
    songData.subject = $("#song-subject").val();

    const minutes = parseInt($("#audio-minutes").val(), 10) || 0;
    const seconds = parseInt($("#audio-seconds").val(), 10) || 0;
    const totalSeconds = minutes * 60 + seconds;

    // 유효성 검사
    if (!songData.title || !songData.subject) {
      Swal.fire({
        icon: "info",
        title: "제목과 주제를 입력해주세요.",
        confirmButtonText: "확인",
      });
      return;
    }

    if (totalSeconds === 0) {
      Swal.fire({
        icon: "info",
        title: "오디오 길이를 입력해주세요.",
        confirmButtonText: "확인",
      });
      return;
    }
    if (totalSeconds < 30 || totalSeconds > 120) {
      Swal.fire({
        icon: "info",
        title: "오디오 길이는 30초에서 2분 사이여야 합니다.",
        confirmButtonText: "확인",
      });
      return;
    }

    songData.audioLength = minutes + "분 " + seconds + "초";
    songData.totalSeconds = totalSeconds;
    songData.runSegments = Math.round(totalSeconds / 15);

    songData.gender =
      $('input[name="vocal-gender"]:checked').attr("id") === "male"
        ? "남자아이"
        : "여자아이";

    const verseNo = $('input[name="verse-no"]:checked').attr("id");
    songData.verseNo = verseNo ? verseNo.replace("verse", "") + "절" : "1절";

    // 정보 표시 업데이트
    $("#info-title").text("제목: " + songData.title);
    $("#info-subject").text("주제: " + songData.subject);
    $("#info-length").text("오디오 길이: " + songData.audioLength);
    $("#info-gender").text("보컬 성별: " + songData.gender);

    showStep(2);
  });

  // Step 2 -> Step 3
  $("#to-step-3").on("click", function () {
    // 유효성 검사
    if (!songData.mood || !songData.category) {
      Swal.fire({
        icon: "info",
        title: "분위기와 카테고리를 선택해주세요.",
        confirmButtonText: "확인",
      });
      return;
    }

    // 정보 표시 업데이트
    $("#info-mood").text("분위기: " + songData.mood);
    $("#info-category").text("카테고리: " + songData.category);

    showStep(3);
  });

  $("#generate-lyrics").on("click", function () {
    const $button = $(this);
    const $lyricsDisplay = $("#lyrics-display");
    $(".loading-overlay").addClass("active");

    $button.prop("disabled", true).text("가사 생성 중...");
    $lyricsDisplay.val("가사를 생성하고 있습니다. 잠시만 기다려주세요...");

    // 가사 생성 API 호출
    $.ajax({
      url: "/api/gemini/lyrics/generate",
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        title: songData.title,
        subject: songData.subject,
        mood: songData.mood,
        category: songData.category,
        gender: songData.gender === "남자아이" ? "MALE" : "FEMALE",
        verseNo: parseInt(songData.verseNo.replace("절", "")),
        run_n_segments: songData.runSegments
          ? songData.runSegments.toString()
          : undefined,
      }),
      success: function (lyrics) {
        $lyricsDisplay.val(lyrics);
        $(".loading-overlay").removeClass("active");
      },
      error: function (xhr, status, error) {
        $lyricsDisplay.val("");
        Swal.fire({
          icon: "error",
          title: "가사 생성에 실패했습니다",
          text: xhr.responseJSON?.message || error,
          confirmButtonText: "확인",
        });
      },
      complete: function () {
        $button.prop("disabled", false).text("가사 생성하기");
      },
    });
  });

  // Step 3 -> Step 4
  $("#to-step-4").on("click", function () {
    const lyrics = $("#lyrics-display").val();

    if (!lyrics) {
      Swal.fire({
        icon: "info",
        title: "가사를 입력하거나 생성해주세요.",
        confirmButtonText: "확인",
      });
      return;
    }

    songData.lyrics = lyrics;
    showStep(4);
  });

  // Step 4 -> Step 5
  $("#to-step-5").on("click", function () {
    const file = $("#song-media")[0].files[0];

    // 커버 이미지 업로드
    if (file) {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("purpose", "song");

      $.ajax({
        url: "/media/upload",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
          songData.mediaId = response.mediaId;
          generateSong();
        },
        error: function (xhr) {
          Swal.fire({
            icon: "error",
            title: "커버 이미지 업로드에 실패했습니다.",
            confirmButtonText: "확인",
          });
          console.error(xhr);
        },
      });
    } else {
      generateSong();
    }
  });

  // 노래 생성 함수
  function generateSong() {
    const runSegments =
      typeof songData.runSegments === "number"
        ? songData.runSegments
        : Math.max(1, Math.round(songData.totalSeconds / 15));

    if (!runSegments || runSegments <= 0) {
      Swal.fire({
        icon: "error",
        title: "오디오 시간을 다시 확인해주세요.",
        confirmButtonText: "확인",
      });
      return;
    }

    // 노래 생성 API 호출
    $.ajax({
      url: "/song/generate/new",
      method: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        title: songData.title,
        subject: songData.subject,
        mood: songData.mood,
        category: songData.category,
        gender: songData.gender === "남자아이" ? "MALE" : "FEMALE",
        lyrics: songData.lyrics,
        run_n_segments: runSegments.toString(),
        coverMediaId: songData.mediaId,
      }),
      success: function (jobId) {
        showStep(5);
      },
      error: function (error) {
        Swal.fire({
          icon: "error",
          title: "노래 생성에 실패했습니다.",
          confirmButtonText: "확인",
        });
        if (songData.mediaId) {
          $.ajax({
            url: `/media/delete/${songData.mediaId}`,
            type: "DELETE",
          });
        }
      },
    });
  }

  // 홈으로 이동
  $("#go-home").on("click", function () {
    window.location.href = "/";
  });
});
