$(document).ready(function () {
  // 전역 AJAX 설정
  $.ajaxSetup({
    beforeSend: function (xhr) {
      // CSRF 토큰이 있다면 추가
      const token = $('meta[name="_csrf"]').attr("content");
      const header = $('meta[name="_csrf_header"]').attr("content");
      if (token && header) {
        xhr.setRequestHeader(header, token);
      }
    },
  });

  // 전역 에러 핸들러
  $(document).ajaxError(function (event, jqXHR, settings, thrownError) {
    console.error("AJAX 에러:", thrownError);
    if (jqXHR.status === 401) {
      alert("로그인이 필요합니다.");
      window.location.href = "/auth/login";
    }
  });

  // 유틸리티 함수들
  window.utils = {
    // 이미지 로드 실패시 기본 이미지로 대체
    handleImageError: function (img) {
      $(img).attr("src", "/assets/img/default.png");
    },
  };
});
