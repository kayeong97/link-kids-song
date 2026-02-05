// 로그인

$(document).ready(function () {
  // 로그인 버튼 클릭 이벤트
  $('button[type="submit"]').on("click", function (e) {
    e.preventDefault();

    const id = $("#id").val();
    const password = $("#password").val();

    // 입력 검증
    if (!id || !password) {
      alert("아이디와 비밀번호를 입력해주세요.");
      return;
    }

    // login 요청
    $.ajax({
      url: "/user/login",
      method: "POST",
      data: {
        id: id,
        password: password,
      },
      success: function (response) {
        if (response === true) {
          window.location.href = "/home";
        } else {
          alert("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
      },
      error: function () {
        alert("로그인을 다시 시도해 주세요.");
      },
    });
  });

  // 엔터키로 로그인
  $("#password").on("keypress", function (e) {
    if (e.which === 13) {
      $('button[type="submit"]').click();
    }
  });
});
