// 이직해 존버해 버튼
const boardType = document.getElementById("boardType").value;
console.log("dfdf: "+ boardType)

console.log(boardId);
console.log(boardType);


// 좋아요/싫어요 토글 처리수정
function toggleLikeDislike(boardId, type) {
    console.log("좋아요/싫어요 요청");

    const isLiked = type === "like";

    $.ajax({
        url: '/board/' + boardId + '/' + boardType + '/update', // URL 요청
        method: 'POST',
        data: {
            isLiked: isLiked  // TODO true or false
        },
        success: function(response) {
            console.log("좋아요, 싫어요 개수 가져오기 성공!");

            // let thumbsUp = document.getElementById("thumbsUp");
            // let thumbsDown = document.getElementById("thumbsDown");
            // if (boardType == 1) {
            //     if (isLiked) {
            //         thumbsUp.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=83166&format=png&color=000000\" alt=\"like\" class=\"w-4 h-4\">";
            //     } else {
            //         thumbsUp.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"like\" class=\"w-4 h-4\">";
            //     }
            // } else {
            //     if (isLiked) {
            //         thumbsUp.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=83166&format=png&color=000000\" alt=\"like\" class=\"w-4 h-4\">";
            //     } else {
            //         thumbsUp.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"like\" class=\"w-4 h-4\">";
            //     }
            // }
            //
            // if (boardType == 1) {
            //     if (isLiked) {
            //         thumbsDown.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"like\" class=\"w-4 h-4 rotate-180 inline-block transform\">";
            //     } else {
            //         thumbsDown.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=83166&format=png&color=000000\" alt=\"like\" class=\"w-4 h-4 rotate-180 inline-block transform\">";
            //     }
            // } else {
            //     if (isLiked) {
            //         thumbsDown.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"like\" class=\"w-4 h-4 rotate-180 inline-block transform\">";
            //     } else {
            //         thumbsDown.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=83166&format=png&color=000000\" alt=\"like\" class=\"w-4 h-4 rotate-180 inline-block transform\">";
            //     }
            // }

            // 화면 배치
            let likeEl = document.getElementById('likeCount');
            let dislikeEl = document.getElementById('dislikeCount');

            console.log(response.like);
            console.log(response.dislike);

            console.log(likeEl);
            console.log(dislikeEl);

            $("#likeCount").text(response.like);
            $("#dislikeCount").text(response.dislike);

            // likeEl.innerText(response.like);
            // dislikeEl.innerText(response.dislike);
        },
        error: function(xhr, status, error) {
            console.error("Error:", error);
        }
    });
}