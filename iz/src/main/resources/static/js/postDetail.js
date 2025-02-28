
//게시글 작성 널값 확인
function OpenUpdateModal(message) {
    const modal = document.getElementById("postFormModal");
    const modalMessage = document.getElementById("postFormModalMessage");

    modalMessage.textContent = message;
    modal.classList.remove("hidden");

    setTimeout(CloseModal, 1000);
}
function CloseModal() {
    document.getElementById("postFormModal").classList.add("hidden");
}

function PostFormInsertBut(){
    const title = document.getElementById("title").value.trim();
    const content = document.getElementById("content").value.trim();

    if(!title){
        // alert("제목을 입력해주세요!");
        OpenUpdateModal("제목 입력해야쥐~")
        return false;
    }

    if(!content){
        // alert("내용을 입력해주세요!!");
        OpenUpdateModal("게시글 내용입력해야쥐~")
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
        // alert('댓글 내용을 입력해주세요.');
        OpenDupdateModal("댓글을 입력하세요.");
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
                <div class="idBoard p-4 border rounded-lg bg-gray-50">
                    <div class="flex justify-between text-gray-600 text-sm font-semibold">
                        <span >${response.nickname}</span>
                        <span class="reg_date" data-reg-date="${response.reg_date}">방금 전</span>
                    </div>
                    <div class="flex justify-between items-center mt-2">
                        <input id="inpuu" class="comment-input focus:outline-none p-2 text-sm font-black w-100" 
                               value="${response.comment_content}" readonly>
                        <div class="space-x-2 w-40 text-right" >
                            <button type="button" id="dd" onclick="editComment(event)" 
                                    class="text-blue-500 hover:text-blue-600 cursor-pointer">
                                    수정
                            </button>
                            <input type="hidden" id="commentId" class="comment-id" value="${response.comment_id}"/>
                            <input type="hidden" id="boardIde" class="board-id"  value="${boardId}"/>
                            <input type="hidden" id="boardTypec"  class="board-type" value="${boardType}"/>
                            <input type="hidden" id="writerId" class="writer-id" value="${response.writer_id}"/>
                            <button type="button" onclick="OpenDeleteModal(this)" class="text-red-500 hover:text-red-600 cursor-pointer">삭제</button>
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
    selectedComment = button.closest('.idBoard');
    document.getElementById("communityDeleteModal").classList.remove("hidden");
}

function CloseDeleteModal() {
    document.getElementById("communityDeleteModal").classList.add("hidden");
    selectedComment = null;
}

// 게시글 삭제모달창
function OpenDeleteModalBoard() {
    document.getElementById("boardDeleteModal").classList.remove("hidden");
}

function CloseDeleteModalBoard() {
    document.getElementById("boardDeleteModal").classList.add("hidden");
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

                CloseDeleteModal();

                const selectedComment = document.querySelector(`#commentId[value='${commentId}']`).closest('.idBoard');
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
function OpenDupdateModal(message) {
    const modal = document.getElementById("updateModal");
    const modalMessage = document.getElementById("alarmModalM");

    modalMessage.textContent = message;
    modal.classList.remove("hidden");

    setTimeout(CloseUpdateModal, 1000);
}

function CloseUpdateModal() {
    document.getElementById("updateModal").classList.add("hidden");
}


function editComment(event) {
    const button = event.target;  // 클릭된 버튼
    const commentDiv = button.closest('.idBoard'); // 해당 댓글 요소 찾기

    if (!commentDiv) {
        console.error('댓글 요소를 찾을 수 없습니다.');
        return;
    }

    const inputField = commentDiv.querySelector('.comment-input'); // 댓글 내용 입력 필드 찾기
    const commentId = commentDiv.querySelector('.comment-id');
    const boardIde = commentDiv.querySelector('.board-id');
    const boardTypec = commentDiv.querySelector('.board-type');
    const writerId = commentDiv.querySelector('.writer-id');

    if (!inputField || !commentId || !boardIde || !boardTypec || !writerId) {

        console.error('필수 요소가 누락되었습니다.');
        return;
    }

    console.log("comment_id: " + commentId.value + ", board_id: " + boardIde.value + ", board_type: " + boardTypec.value + ", writer_id: " + writerId.value);

    // 원래 배경색 저장
    const originalBorder = inputField.style.border;

    // 읽기 전용 속성 제거 및 포커스
    inputField.removeAttribute('readonly');
    inputField.style.border = "2px solid #9bdcfd";
    inputField.style.borderTop = "none";  // 상단 테두리 없애기
    inputField.style.borderLeft = "none"; // 왼쪽 테두리 없애기
    inputField.style.borderRight = "none"; // 오른쪽 테두리 없애기


    setTimeout(() => {
        inputField.focus();
        const length = inputField.value.length;
        inputField.setSelectionRange(length, length);
    }, 10);

    button.innerText = '저장';

    button.onclick = function() {
        const updatedContent = inputField.value;


        if (!updatedContent) {
            // alert("댓글을 입력하세요!");
            OpenDupdateModal("댓글을 입력하세요.");
            inputField.focus(); // 다시 입력할 수 있도록 포커스
            return; // 함수 종료
        }

        fetch('/board/comment/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                comment_id: commentId.value,
                board_id: boardIde.value,
                board_type: boardTypec.value,
                writerId: writerId.value,
                comment_content: updatedContent  // 수정된 댓글 내용 전송
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // 다시 읽기 전용 설정
                    inputField.setAttribute('readonly', 'true');
                    inputField.style.border = originalBorder;

                    button.innerText = '수정';
                    button.onclick = editComment;


                    OpenDupdateModal("수정했습니다.");
                    // alert('댓글이 수정되었습니다.');
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



// 게시글 수정



