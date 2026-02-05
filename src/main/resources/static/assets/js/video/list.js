// 동영상 리스트

$(document).ready(function () {
  let currentSort = "date";

  // 정렬 버튼 클릭
  $("#sort-date").on("click", function () {
    if (currentSort === "date") return;

    // 버튼 active 상태 변경
    $("#sort-title").removeClass("active");
    $(this).addClass("active");

    currentSort = "date";
    loadHeaders("date");
  });

  $("#sort-title").on("click", function () {
    if (currentSort === "song") return;

    // 버튼 active 상태 변경
    $("#sort-date").removeClass("active");
    $(this).addClass("active");

    currentSort = "song";
    loadHeaders("song");
  });

  // toggle 헤더 불러오기
  function loadHeaders(sortType) {
    $.ajax({
      url: "/video/list/headers",
      method: "GET",
      data: { sort: sortType },
      success: function (headerList) {
        renderHeaders(headerList);
      },
      error: function () {
        alert("정보 불러오기를 실패했습니다.");
      },
    });
  }

  function renderHeaders(headerList) {
    const videoListContainer = $(".video-list");
    videoListContainer.empty();

    const header = Object.keys(headerList);

    header.forEach(function (key) {
      const detailsHtml = `
        <details class="video-list-details" data-key="${key}" data-value="${headerList[key]}">
          <summary class="video-list-summary">
            <span class="arrow-icon">▶</span>
            <span class="video-list-summary-text">${key}</span>
          </summary>
          <div class="video-items-container">
          </div>
        </details>
      `;
      videoListContainer.append(detailsHtml);
    });
  }

  // 토글 열고 닫기
  $(document).on("click", ".video-list-summary", function (e) {
    const $details = $(this).closest(".video-list-details");
    const $container = $details.find(".video-items-container");
    setTimeout(function () {
      if ($details.prop("open") && $container.children().length === 0) {
        loadVideos($details, $container);
      }
    }, 10);
  });

  // 동영상 content
  function loadVideos($details, $container) {
    let apiUrl, params;
    const key = $details.attr("data-key");
    const value = $details.attr("data-value");

    if (currentSort === "date") {
      apiUrl = "/video/list/month";
      params = { m: value };
    } else {
      apiUrl = "/video/list/song";
      params = { songId: value };
    }

    $.ajax({
      url: apiUrl,
      method: "GET",
      data: params,
      success: function (html) {
        $container.html(html);
      },
      error: function (xhr, status, error) {
        console.error("동영상 로드 실패:", status, error, xhr.responseText);
        $container.html(
          '<p style="padding: 20px; text-align: center;">동영상을 불러오는데 실패했습니다.</p>',
        );
      },
    });
  }

  // 재생 버튼 클릭
  $(document).on("click", ".play-video-button", function (e) {
    e.preventDefault();
    e.stopPropagation();

    const lipsyncId = $(this).attr("data-lipsync-id");
    const title = $(this).attr("data-title");

    window.location.href = `/video/play?videoTitle=${encodeURIComponent(title)}&lipsyncId=${lipsyncId}`;
  });

  // 다운로드 버튼 클릭
  $(document).on("click", ".download-video-icon", function (e) {
    e.preventDefault();
    e.stopPropagation();

    const lipsyncId = $(this).attr("data-lipsync-id");
    if (lipsyncId) {
      // 다운로드 링크로 이동
      window.location.href = `/video/download/${lipsyncId}`;
    }
  });
});
