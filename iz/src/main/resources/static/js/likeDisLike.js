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