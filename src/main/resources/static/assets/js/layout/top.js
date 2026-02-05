$(document).ready(function () {
  // 사용자 메뉴 토글
  document
    .getElementById("user-menu-button")
    .addEventListener("click", function (e) {
      e.stopPropagation();
      const menu = document.getElementById("user-menu");
      menu.classList.toggle("show");
    });

  // 메뉴 외부 클릭 시 닫기
  document.addEventListener("click", function (e) {
    const menu = document.getElementById("user-menu");
    const button = document.getElementById("user-menu-button");
    if (!menu.contains(e.target) && !button.contains(e.target)) {
      menu.classList.remove("show");
    }
  });
});
