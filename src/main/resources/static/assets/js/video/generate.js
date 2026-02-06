// 동영상 생성
let videoData = {};

$(document).ready(function () {
  // 선택되어 넘어오는 노래 표시
  const preSelectedSongId = $("#selected-song-id").val();
  if (preSelectedSongId) {
    const selectedSongItem = $(
      `.song-item[data-song-id="${preSelectedSongId}"]`,
    );
    if (selectedSongItem.length > 0) {
      selectedSongItem.addClass("selected");
      const songTitle = selectedSongItem.data("song-title");
      const songThumbnailSrc = selectedSongItem
        .find(".song-thumbnail")
        .attr("src");
      $(".selected-song")
        .html(
          `
        <img class="song-thumbnail" src="${songThumbnailSrc}" alt="Song Thumbnail" />
        <span class="song-title">${songTitle}</span>
      `,
        )
        .css("display", "flex");
    }
  }

  // 노래 선택 모달 open/close
  $("#open-song-select-modal").on("click", function () {
    $(".song-select-modal").css("display", "block");
  });

  $(".close-button").on("click", function () {
    $(".song-select-modal").css("display", "none");
  });

  $(".song-item").on("click", function () {
    $(".song-item").removeClass("selected");
    $(this).addClass("selected");
  });

  $("#confirm-song-selection").on("click", function () {
    const selectedSong = $(".song-item.selected");
    if (selectedSong.length > 0) {
      const songId = selectedSong.data("song-id");
      const songTitle = selectedSong.data("song-title");
      const songThumbnailSrc = selectedSong.find(".song-thumbnail").attr("src");
      $("#selected-song-id").val(songId);
      $(".selected-song").html(`
                <img class="song-thumbnail" src="${songThumbnailSrc}" alt="Song Thumbnail" />
                <span class="song-title">${songTitle}</span>
            `);
      $(".song-select-modal").css("display", "none");

      $(".selected-song").css("display", "flex");
    } else {
      Swal.fire({
        icon: "info",
        title: "노래를 선택해주세요.",
        confirmButtonText: "확인",
        backdrop: "rgba(0, 0, 0, 0.7)",
      });
    }
  });

  // 이미지 업로드
  $("#video-image").on("change", function (event) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = function (e) {
        $("#preview-image").attr("src", e.target.result).show();
        $(".video-image-upload").addClass("has-image");
      };
      reader.readAsDataURL(file);

      const formData = new FormData();
      formData.append("file", file);
      formData.append("purpose", "video");

      $.ajax({
        url: "/media/upload",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
          videoData.inputImageMediaId = response.mediaId;
        },
        error: function (xhr) {
          Swal.fire({
            icon: "error",
            title: "이미지 업로드에 실패했습니다.",
            confirmButtonText: "확인",
            backdrop: "rgba(0, 0, 0, 0.7)",
          });
          console.error(xhr);
          return;
        },
      });
    }
  });

  // 동영상 생성하기
  $("#generate-video-button").on("click", function () {
    // 데이터 수집
    videoData.songId = $("#selected-song-id").val();
    videoData.title = $("#video-title").val();

    // 유효성 검사
    if (!videoData.songId || !videoData.title || !videoData.inputImageMediaId) {
      Swal.fire({
        icon: "info",
        title: "노래, 제목, 이미지를 모두 입력해주세요.",
        confirmButtonText: "확인",
        backdrop: "rgba(0, 0, 0, 0.7)",
      });
      return;
    }

    // 로딩 화면 표시
    $(".loading-overlay").addClass("active");

    // 동영상 생성 요청
    $.ajax({
      url: "/video/generate/new",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(videoData),
      success: function (response) {
        $(".loading-overlay").removeClass("active");
        Swal.fire({
          icon: "success",
          title: "동영상 생성 완료!",
          confirmButtonText: "확인",
          backdrop: "rgba(0, 0, 0, 0.7)",
        }).then((result) => {
          if (result.isConfirmed) {
            window.history.back();
          }
        });
      },
      error: function (xhr) {
        $(".loading-overlay").removeClass("active");
        Swal.fire({
          icon: "error",
          title: "동영상 생성에 실패했습니다.",
          text: "다시 한번 시도해주세요.",
          confirmButtonText: "확인",
          backdrop: "rgba(0, 0, 0, 0.7)",
        });
        console.error(xhr);
        return;
      },
    });
  });
});
