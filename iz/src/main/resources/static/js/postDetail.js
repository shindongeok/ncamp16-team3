
// 게시글 삭제, 수정 모달 창
function OpenDeleteModal() {
    document.getElementById("communityDeleteModal").classList.remove("hidden");
}

function CloseDeleteModal() {
    document.getElementById("communityDeleteModal").classList.add("hidden");
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


// 댓글 작성
function saveComment() {
    const content = document.getElementById('commentContent').value;
    const boardId = document.getElementById('boardIdd').value;
    const boardType = document.getElementById('boardTypee').value;  // 추가

    if (!content.trim()) {
        alert('댓글 내용을 입력해주세요.');
        return;
    }

    $.ajax({
        url: `/board/${boardId}/comment`,  // boardId를 동적으로 삽입
        type: 'POST',
        data: {
            comment_content: content,
            board_type: boardType  // 추가
        },
        success: function(response) {
            if (response.success) {
                const newComment = `
                <div class="border-b border-gray-200">
                    <div class="flex justify-between p-2">
                        <div class="text-gray-500">${response.nickname}</div>
                        
                        <div class="text-gray-500 reg_date" data-reg-date="${response.reg_date}">방금 전</div>
                    </div>
                    <div class="flex justify-between">
                        <div class="p-2 text-xl font-black">${response.comment_content}</div>
                        <div class="p-2" >
                               <button type="button" onclick="OpenUpdateModal()" class="px-2 py-1 bg-main-sky-highlight text-black rounded hover:bg-main-sky-basic rounded-xl cursor-pointer">수정</button>
                            <input type="hidden" id="commentId" value="${response.comment_id}"/>
                            <input type="hidden" id="boardIde" value="${boardId}"/>
                            <input type="hidden" id="boardTypec" value="${boardType}"/>
                            <input type="hidden" id="writerId" value="${response.writer_id}"/>
                            <button type="button" onclick="OpenDeleteModal(this)" class="px-2 py-1 bg-main-red-highlight text-black rounded hover:bg-main-red-basic rounded-xl cursor-pointer">삭제</button>
                        </div>
                    </div>
                </div>`;

                // 댓글 목록의 최상단에 새 댓글 추가
                $('.comment-list').prepend(newComment);

                // 입력창 초기화
                $('#commentContent').val('');

                // 성공 메시지 표시 (선택사항)
                // alert('댓글이 등록되었습니다.');
            } else {
                alert('댓글 등록에 실패했습니다.');
            }
        },
        error: function(xhr, status, error) {
            alert('댓글 등록에 실패했습니다.');
        }
    });
}

//댓글 삭제
function deleteComment() {
    const commentId = document.getElementById('commentId').value;
    const boardId = document.getElementById('boardIde').value;
    const boardType = document.getElementById('boardTypec').value;
    const writerId = document.getElementById('writerId').value;

    console.log(commentId, boardId, boardType, writerId);

    if (!commentId || !boardId || !boardType || !writerId) {
        alert('필수 값이 누락되었습니다.');
        return;
    }

    const url = `/board/deleteComment?commentId=${commentId}&boardId=${boardId}&boardType=${boardType}&writerId=${writerId}`;

    fetch(url, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);

            if (data.message === "댓글이 삭제되었습니다.") {
                // 모달 창 닫기
                CloseDeleteModal();
                // 페이지 새로고침
                location.reload();
            } else {
                alert('댓글 삭제에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('서버와의 통신 오류가 발생했습니다.');
        });
}

