// 동영상 플레이어

$(document).ready(function () {
  // URL에서 lipsyncId 가져오기
  const urlParams = new URLSearchParams(window.location.search);
  const lipsyncId = urlParams.get("lipsyncId");

  const videoElement = document.getElementById("video-player");
  if (videoElement) {
    videoElement.addEventListener("ended", function () {
      videoElement.currentTime = 0;
      videoElement.pause();
    });
  }

  // 뒤로가기 버튼
  const goBackBtn = document.getElementById("back-btn");
  if (goBackBtn) {
    goBackBtn.addEventListener("click", function () {
      window.history.back();
    });
  }

  // 삭제 버튼
  const deleteBtn = document.getElementById("delete-video-btn");
  if (deleteBtn) {
    deleteBtn.addEventListener("click", function () {
      Swal.fire({
        title: "동영상을 삭제하시겠습니까?",
        text: "삭제된 동영상은 복구할 수 없습니다.",
        icon: "warning",
        input: "text",
        inputPlaceholder: "삭제하려면 동영상의 제목을 입력하세요.",
        inputAttributes: {
          autocapitalize: "off",
        },
        showCancelButton: true,
        confirmButtonColor: "#d33",
        cancelButtonColor: "#3085d6",
        confirmButtonText: "삭제",
        cancelButtonText: "취소",
      }).then((result) => {
        if (result.isConfirmed) {
          // 동영상 제목 가져오기
          const videoTitle = document.querySelector(".video-title").innerText;

          // 입력한 값과 제목 비교
          if (result.value !== videoTitle) {
            Swal.fire({
              title: "삭제 실패",
              text: "제목이 일치하지 않습니다. 다시 시도해주세요.",
              icon: "error",
            });
            return;
          }

          // 삭제 요청
          $.ajax({
            url: `/video/delete/${lipsyncId}`,
            type: "DELETE",
            success: function (result) {
              Swal.fire({
                title: "삭제 완료!",
                text: "동영상이 성공적으로 삭제되어 목록 페이지로 이동합니다.",
                icon: "success",
              }).then(() => {
                window.location.href = "/video/list"; // 동영상 목록 페이지로 이동
              });
            },
            error: function (err) {
              Swal.fire({
                title: "삭제 실패",
                text: "제목이 일치하지 않습니다. 다시 시도해주세요.",
                icon: "error",
              });
              return;
            },
          });
        }
      });
    });
  }
});
