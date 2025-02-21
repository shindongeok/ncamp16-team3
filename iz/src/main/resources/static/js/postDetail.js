
//게시글 작성 널값 확인
function PostFormInsertBut(){
    const title = document.getElementById("writer_id").value.trim();
    const content = document.getElementById("content").value.trim();

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
function timeAgo(dateString) {
    const now = new Date();
    const date = new Date(dateString);

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

    $(regDateList).each((a, b) => {
        const regDate = $(b).data('reg-date');
        const timeAgoText = timeAgo(regDate);
        $(b).text(timeAgoText);
    });
});

// 댓글 등록
function checkEnter(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        saveComment();
    }
}

// 댓글 작성
function saveComment() {
    const content = document.getElementById('commentContent').value;
    const boardId = document.getElementById('boardIdd').value;
    const boardType = document.getElementById('boardTypee').value;

    if (!content.trim()) {
        alert('댓글 내용을 입력해주세요.');
        return;
    }

    $.ajax({
        url: `/board/${boardId}/comment`,
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
                        <input id="inpuu" class="comment-input focus:outline-none h-10 p-2 text-xl font-black" value="${response.comment_content}" readonly></input>
                        <div class="p-2" >
                            <button type="button" id="dd" onclick="editComment(event)" class="px-2 py-1 bg-main-sky-highlight text-black rounded hover:bg-main-sky-basic rounded-xl cursor-pointer">수정</button>
                            <input type="hidden" id="commentId" class="comment-id" value="${response.comment_id}"/>
                            <input type="hidden" id="boardIde" class="board-id"  value="${boardId}"/>
                            <input type="hidden" id="boardTypec"  class="board-type" value="${boardType}"/>
                            <input type="hidden" id="writerId" class="writer-id" value="${response.writer_id}"/>
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
                alert('댓글 등록 실패!');
            }
        },
        error: function(xhr, status, error) {
            alert('서버 에러!');
        }
    });
}

// 게시글 삭제 창
function OpenDeleteModal(button) {
    selectedComment = button.closest('.border-b');
    document.getElementById("communityDeleteModal").classList.remove("hidden");
}

function CloseDeleteModal() {
    document.getElementById("communityDeleteModal").classList.add("hidden");
    selectedComment = null;
}

//댓글 삭제
function deleteComment() {
    const commentId = selectedComment.querySelector("input[id='commentId']").value;
    const boardId = selectedComment.querySelector("input[id='boardIde']").value;
    const boardType = selectedComment.querySelector("input[id='boardTypec']").value;
    const writerId = selectedComment.querySelector("input[id='writerId']").value;

    console.log(commentId, boardId, boardType, writerId);

    if (!commentId || !boardId || !boardType || !writerId) {
        alert('필요한 값 안넘어옴.');
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

                const selectedComment = document.querySelector(`#commentId[value='${commentId}']`).closest('.border-b');
                if (selectedComment) {
                    selectedComment.remove();
                }

            } else {
                alert('댓글 삭제 실패!');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('서버와 통신 문제');
        });
}

//댓글 수정
function editComment(event) {
    const button = event.target;  // 클릭된 버튼
    const commentDiv = button.closest('.border-b'); // 해당 댓글 요소 찾기
    const inputField = commentDiv.querySelector('.comment-input'); // 댓글 내용 입력 필드 찾기
    const commentId = commentDiv.querySelector('.comment-id').value;
    const boardIde = commentDiv.querySelector('.board-id').value;
    const boardTypec = commentDiv.querySelector('.board-type').value;
    const writerId = commentDiv.querySelector('.writer-id').value;

    console.log("comment_id: " + commentId + ", board_id: " + boardIde + ", board_type: " + boardTypec + ", writer_id: " + writerId);

    // 읽기 전용 속성 제거 및 포커스
    inputField.removeAttribute('readonly');
    inputField.focus();

    // 버튼 텍스트 변경
    button.innerText = '저장';

    // 저장 버튼 클릭 시 이벤트 변경
    button.onclick = function() {
        const updatedContent = inputField.value;

        fetch('/board/comment/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                comment_id: commentId,
                board_id: boardIde,
                board_type: boardTypec,
                writerId: writerId,
                comment_content: updatedContent  // 수정된 댓글 내용 전송
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 다시 읽기 전용 설정
                    inputField.setAttribute('readonly', 'true');
                    button.innerText = '수정';
                    button.onclick = editComment; // 다시 수정 가능하도록 원래 이벤트로 복구
                    alert('댓글이 수정되었습니다.');
                } else {
                    alert('댓글 수정 실패!');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('댓글 수정 중 오류가 발생했습니다.');
            });
    };
}

function OpenDeleteModalBoard() {
    document.getElementById("boardDeleteModal").classList.remove("hidden");
}

function CloseDeleteModalBoard() {
    document.getElementById("boardDeleteModal").classList.add("hidden");
}

