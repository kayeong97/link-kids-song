// 회원가입

$(document).ready(function () {
  let isIdChecked = false;
  let isEmailChecked = false;

  // 비밀번호 보기/숨기기 토글
  $("#togglePassword").on("click", function () {
    const passwordInput = $("#password");
    const currentType = passwordInput.attr("type");
    const newType = currentType === "password" ? "text" : "password";
    $(this).find(".eye-icon").toggleClass("visible hidden");
    passwordInput.attr("type", newType);
  });

  // ID 입력 시 중복확인 상태 초기화
  $("#userId").on("input", function () {
    isIdChecked = false;
    $("#idErrorMsg").text("").hide();
    $(this).removeClass("success error");
  });

  // Email 입력 시 인증 상태 초기화
  $("#email").on("input", function () {
    isEmailChecked = false;
    $("#emailErrorMsg").text("").hide();
    $(this).removeClass("success error");
  });

  // ID 중복확인
  $("#checkIdbutton").on("click", function (e) {
    e.preventDefault();
    const userId = $("#userId").val().trim();

    if (!userId) {
      $("#idErrorMsg").text("아이디를 입력해주세요.").show();
      $("#userId").addClass("error");
      return;
    }

    $.ajax({
      url: "/user/signup/id-dup-check",
      type: "GET",
      data: { id: userId },
      dataType: "text",
      success: function (response) {
        if (response === "duplicated") {
          $("#idErrorMsg").text("이미 사용 중인 아이디입니다.").show();
          $("#userId").removeClass("success").addClass("error");
          isIdChecked = false;
        } else {
          $("#idErrorMsg")
            .text("사용 가능한 아이디입니다.")
            .css("color", "#4caf50")
            .show();
          $("#userId").removeClass("error").addClass("success");
          isIdChecked = true;
        }
      },
      error: function () {
        $("#idErrorMsg").text("중복확인 중 오류가 발생했습니다.").show();
        $("#userId").addClass("error");
      },
    });
  });

  // Email 중복 확인
  $("#checkEmailbutton").on("click", function (e) {
    e.preventDefault();
    const email = $("#email").val().trim();

    if (!email) {
      $("#emailErrorMsg").text("이메일을 입력해주세요.").show();
      $("#email").addClass("error");
      return;
    }

    // 이메일 형식 검증
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      $("#emailErrorMsg").text("올바른 이메일 형식이 아닙니다.").show();
      $("#email").addClass("error");
      return;
    }

    $.ajax({
      url: "/user/signup/email-dup-check",
      type: "GET",
      data: { email: email },
      dataType: "text",
      success: function (response) {
        if (response === "duplicated") {
          $("#emailErrorMsg").text("이미 사용 중인 이메일입니다.").show();
          $("#email").removeClass("success").addClass("error");
          isEmailChecked = false;
        } else {
          $("#emailErrorMsg")
            .text("사용 가능한 이메일입니다.")
            .css("color", "#4caf50")
            .show();
          $("#email").removeClass("error").addClass("success");
          isEmailChecked = true;
        }
      },
      error: function () {
        $("#emailErrorMsg").text("중복확인 중 오류가 발생했습니다.").show();
        $("#email").addClass("error");
      },
    });
  });

  // 비밀번호 실시간 검증
  $("#password").on("input", function () {
    const password = $(this).val();
    const confirmPassword = $("#confirmPassword").val();

    if (password.length > 0 && password.length < 8) {
      $("#passwordErrorMsg").text("비밀번호는 8자 이상이어야 합니다.").show();
      $(this).addClass("error").removeClass("success");
    } else if (password.length >= 8) {
      $("#passwordErrorMsg").text("").hide();
      $(this).removeClass("error").addClass("success");
    }

    // 비밀번호 확인 필드가 입력되어 있으면 일치 여부 확인
    if (confirmPassword) {
      checkPasswordMatch();
    }
  });

  // 비밀번호 확인 실시간 검증
  $("#confirmPassword").on("input", checkPasswordMatch);

  function checkPasswordMatch() {
    const password = $("#password").val();
    const confirmPassword = $("#confirmPassword").val();

    if (confirmPassword.length > 0) {
      if (password !== confirmPassword) {
        $("#confirmPasswordErrorMsg")
          .text("비밀번호가 일치하지 않습니다.")
          .show();
        $("#confirmPassword").addClass("error").removeClass("success");
      } else {
        $("#confirmPasswordErrorMsg").text("").hide();
        $("#confirmPassword").removeClass("error").addClass("success");
      }
    }
  }

  // 회원가입 폼 제출
  $("#signup-button").on("click", function (e) {
    e.preventDefault();

    // 유효성 검사
    if (!isIdChecked) {
      alert("아이디 중복확인을 해주세요.");
      $("#userId").focus();
      return;
    }

    if (!isEmailChecked) {
      alert("이메일 인증을 해주세요.");
      $("#email").focus();
      return;
    }

    const password = $("#password").val();
    const confirmPassword = $("#confirmPassword").val();

    if (password.length < 8) {
      alert("비밀번호는 8자 이상이어야 합니다.");
      $("#password").focus();
      return;
    }

    if (password.length > 20) {
      alert("비밀번호는 20자 이하이어야 합니다.");
      $("#password").focus();
      return;
    }

    if (password !== confirmPassword) {
      alert("비밀번호가 일치하지 않습니다.");
      $("#confirmPassword").focus();
      return;
    }

    const year = $("#birthYear").val();
    const month = $("#birthMonth").val();
    const day = $("#birthDay").val();

    if (!year || !month || !day) {
      alert("생년월일을 모두 입력해주세요.");
      return;
    }

    const birth = year + month.padStart(2, "0") + day.padStart(2, "0");
    const gender = $("#gender").val();

    // 회원가입 요청
    $.ajax({
      url: "/user/signup",
      type: "POST",
      data: JSON.stringify({
        id: $("#userId").val(),
        password: password,
        username: $("#name").val(), // name -> username으로 변경
        email: $("#email").val(),
        birthday: birth, // birth -> birthday로 변경
        gender: gender,
        phoneNumber: $("#phoneNumber").val(),
      }),
      contentType: "application/json",
      dataType: "text",
      success: function (response) {
        alert("회원가입이 완료되었습니다!");
        window.location.href = "/";
      },
      error: function (xhr) {
        const errorMsg = xhr.responseText || "회원가입 중 오류가 발생했습니다.";
        alert(errorMsg + "\n\n다시 시도해주세요.");
        console.error("회원가입 오류:", xhr);
      },
    });
  });
});
