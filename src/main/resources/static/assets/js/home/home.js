let currentIndex = 0;
let items;

$(document).ready(function () {
  // DOM 로드 후 items 선택
  items = document.querySelectorAll(".slide-item");

  // 초기 첫 번째 아이템 표시
  if (items.length > 0) {
    items[0].classList.add("active");
  }

  // 글자 slide
  setInterval(function () {
    showSlide();
  }, 2000);
});

// 글자 slide 함수
function showSlide() {
  if (items.length === 0) return;

  items[currentIndex].classList.remove("active");
  items[currentIndex].classList.add("prev");

  currentIndex = (currentIndex + 1) % items.length;

  items[currentIndex].classList.remove("prev");
  items[currentIndex].classList.add("active");
}
