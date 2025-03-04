// 이직해 존버해 버튼
const boardType = document.getElementById("boardType").value;
// console.log("dfdf: "+ boardType)

// console.log(boardId);
// console.log(boardType);

let trueChecked01 = document.getElementById("checkLike01").value;
let falseChecked01 = document.getElementById("checkDislike01").value;
let trueChecked02 = document.getElementById("checkLike02").value;
let falseChecked02 = document.getElementById("checkDislike02").value;
// console.log("trueChecked01", trueChecked01);
// console.log("falseChecked01", falseChecked01);

let thumbsUp = document.getElementById("thumbsUp");
let thumbsDown = document.getElementById("thumbsDown");

// 좋아요/싫어요 토글 처리수정
function toggleLikeDislike(boardId, type) {
    // console.log("좋아요/싫어요 요청");

    const isLiked = type === "like";

    $.ajax({
        url: '/board/' + boardId + '/' + boardType + '/update', // URL 요청
        method: 'POST',
        data: {
            isLiked: isLiked  // TODO true or false
        },
        success: function(response) {
            // console.log("좋아요, 싫어요 개수 가져오기 성공!");
            // console.log("isLiked: " + isLiked);
            // console.log("trueChecked01", trueChecked01);
            // console.log("falseChecked01", falseChecked01);

            if (boardType == 1) {
                if (isLiked && trueChecked01 == "true") {
                    trueChecked01 = "false";
                    thumbsUp.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"holeLike\" class=\"w-4 h-4\">";
                } else if (isLiked && trueChecked01 == "false") {
                    trueChecked01 = "true";
                    if (falseChecked01 == "true") {
                        falseChecked01 = "false";
                        thumbsDown.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"holeDislike\" class=\"w-4 h-4 rotate-180 inline-block transform\">";
                    }
                    thumbsUp.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=83166&format=png&color=000000\" alt=\"coloredLike\" class=\"w-4 h-4\">";
                } else if (!isLiked && falseChecked01 == "true") {
                    falseChecked01 = "false";
                    thumbsDown.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"holeDislike\" class=\"w-4 h-4 rotate-180 inline-block transform\">";
                } else if (!isLiked && falseChecked01 == "false") {
                    falseChecked01 = "true";
                    if (trueChecked01 == "true") {
                        trueChecked01 = "false";
                        thumbsUp.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"holeLike\" class=\"w-4 h-4\">";
                    }
                    thumbsDown.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=83166&format=png&color=000000\" alt=\"coloredDislike\" class=\"w-4 h-4 rotate-180 inline-block transform\">";
                }
            } else if (boardType == 2) {
                if (isLiked && trueChecked02 == "true") {
                    trueChecked02 = "false";
                    thumbsUp.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"holeLike\" class=\"w-4 h-4\">";
                } else if (isLiked && trueChecked02 == "false") {
                    trueChecked02 = "true";
                    if (falseChecked02 == "true") {
                        falseChecked02 = "false";
                        thumbsDown.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"holeDislike\" class=\"w-4 h-4 rotate-180 inline-block transform\">";
                    }
                    thumbsUp.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=83166&format=png&color=000000\" alt=\"coloredLike\" class=\"w-4 h-4\">";
                } else if (!isLiked && falseChecked02 == "true") {
                    falseChecked02 = "false";
                    thumbsDown.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"holeDislike\" class=\"w-4 h-4 rotate-180 inline-block transform\">";
                } else if (!isLiked && falseChecked02 == "false") {
                    falseChecked02 = "true";
                    if (trueChecked02 == "true") {
                        trueChecked02 = "false";
                        thumbsUp.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=82788&format=png&color=000000\" alt=\"holeLike\" class=\"w-4 h-4\">";
                    }
                    thumbsDown.innerHTML = "<img src=\"https://img.icons8.com/?size=100&id=83166&format=png&color=000000\" alt=\"coloredDislike\" class=\"w-4 h-4 rotate-180 inline-block transform\">";
                }
            }


            // 화면 배치
            let likeEl = document.getElementById('likeCount');
            let dislikeEl = document.getElementById('dislikeCount');

            // console.log(response.like);
            // console.log(response.dislike);

            // console.log(likeEl);
            // console.log(dislikeEl);

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