
// 게시글 삭제, 수정 모달 창
function OpenDeleteModal() {
    document.getElementById("communityDeleteModal").classList.remove("hidden");
}

function CloseDeleteModal() {
    document.getElementById("communityDeleteModal").classList.add("hidden");
}

function OpenUpdateModal() {
    document.getElementById("communityUpdateModal").classList.remove("hidden");
}

function CloseUpdateModal() {
    document.getElementById("communityUpdateModal").classList.add("hidden");
}



//게시글 작성 널값 확인?
function PostFormInsertBut(){
    let title = document.getElementById("writer_id").value.trim();
    let content = document.getElementById("content").value.trim();

    // 일단 alert로 띄었습니다..
    if(!title){
        alert("제목을 입력해주세요!");
        return false;
    }

    if(!content){
        alert("내용을 입력해주세요!!");
        return false;
    }
}


//날짜 형식 변경
// 날짜를 "몇일 전", "몇시간 전", "몇분 전" 형식으로 변환하는 함수
function timeAgo(dateString) {
    const now = new Date();
    const date = new Date(dateString);  // 게시글의 등록일

    const diffInSeconds = Math.floor((now - date) / 1000);
    const diffInMinutes = Math.floor(diffInSeconds / 60);
    const diffInHours = Math.floor(diffInMinutes / 60);
    const diffInDays = Math.floor(diffInHours / 24);

    if (diffInDays > 0) {
        return `${diffInDays}일 전`;
    } else if (diffInHours > 0) {
        return `${diffInHours}시간 전`;
    } else if (diffInMinutes > 0) {
        return `${diffInMinutes}분 전`;
    } else {
        return `방금 전`;
    }
}

$(document).ready(function() {
    const regDateList = $(".reg_date");

    // 각 reg_date에 대해 날짜 변환 적용
    $(regDateList).each((a, b) => {
        const regDate = $(b).data('reg-date');  // data-reg-date 속성에서 날짜를 가져옴
        const timeAgoText = timeAgo(regDate);  // 변환된 시간 형식
        $(b).text(timeAgoText);  // 해당 요소에 변경된 시간 넣기
    });
});

// 이직해 존버해 버튼
const boardType = document.getElementById("boardType").value;

console.log(boardId);
console.log(boardType);


// 좋아요/싫어요 토글 처리
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
