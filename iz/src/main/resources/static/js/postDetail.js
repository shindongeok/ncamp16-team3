
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

// 댓글 작성

//댓글 입력할때 높이 조절
function autoResize(textarea) {
    // 텍스트 내용에 맞춰서 높이를 자동으로 변경
    textarea.style.height = 'auto';  // 먼저 높이를 'auto'로 리셋한 뒤
    textarea.style.height = (textarea.scrollHeight) + 'px';  // 텍스트의 높이에 맞게 설정
}

function checkEnter(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        saveComment();
    }
}

// // 더보기 기능
// 더보기 기능
function toggleComment(button) {
    const textarea = button.closest('.comment-content').querySelector('textarea');  // textarea를 선택
    const moreButton = button;

    if (textarea.classList.contains('overflow-hidden')) {
        // 댓글 확장
        textarea.classList.remove('overflow-hidden');
        textarea.style.height = textarea.scrollHeight + 'px';  // 댓글 높이를 내용에 맞게 확장
        moreButton.textContent = '닫기';  // 버튼 텍스트 변경
    } else {
        // 댓글 접기
        textarea.classList.add('overflow-hidden');
        textarea.style.height = '4rem';  // 기본 높이로 설정
        moreButton.textContent = '더보기';  // 버튼 텍스트 변경
    }
}



// 댓글 등록시 나오는 댓글
function saveComment() {
    const content = document.getElementById('commentContent').value;
    const boardId = document.getElementById('boardIdd').value;
    const boardType = document.getElementById('boardTypee').value;
    const commentHeight = document.getElementById('commentContent').scrollHeight;  // 댓글 높이 정보

    if (!content.trim()) {
        // 댓글 내용이 없으면 경고
        OpenDupdateModal("댓글을 입력하세요.");
        return;
    }

    $.ajax({
        url: `/board/${boardId}/comment`,
        type: 'POST',
        data: {
            comment_content: content,
            comment_height: commentHeight,  // 댓글 높이 정보 추가
            board_type: boardType
        },
        success: function(response) {
            if (response.success) {
                const newComment = `
                <div class="idBoard p-4 border rounded-lg bg-gray-50" style="height: ${response.comment_height}px;">
                    <div class="flex justify-between text-gray-600 text-sm font-semibold">
                        <span>${response.nickname}</span>
                        <span class="reg_date" data-reg-date="${response.reg_date}">방금 전</span>
                    </div>
                    <div class="flex justify-between items-center mt-2">
                        <div class="comment-content w-full" style="height: ${response.comment_height}px;">
                            <textarea class="comment-input focus:outline-none p-2 text-sm font-black w-full resize-none overflow-hidden" readonly>${response.comment_content}</textarea>
                            <!-- 더보기 버튼 -->
                        <button type="button" class="text-blue-500 hover:text-blue-600 cursor-pointer" id="moreBtn" onclick="toggleComment(this)">
                            더보기
                        </button>
                        </div>

                        <div class="space-x-2 w-40 text-right">
                            <button type="button" onclick="editComment(event)" class="text-blue-500 hover:text-blue-600 cursor-pointer">
                                수정
                            </button>
                            <input type="hidden" id="commentId" class="comment-id" value="${response.comment_id}"/>
                            <input type="hidden" id="boardIde" class="board-id" value="${boardId}"/>
                            <input type="hidden" id="boardTypeD" class="board-type" value="${boardType}"/>
                            <input type="hidden" id="writerId" class="writer-id" value="${response.writer_id}"/>
                            <button type="button" onclick="OpenDeleteModal(this)" class="text-red-500 hover:text-red-600 cursor-pointer">삭제</button>
                        </div>
                    </div>
                </div>`;

                // 댓글 목록의 최상단에 새 댓글 추가
                $('.comment-list').prepend(newComment);

                // 입력창 초기화
                $('#commentContent').val('');  // 입력된 내용 초기화
                $('#commentContent').css('height', 'auto');  // 텍스트박스 높이 초기화
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
    const boardType = selectedComment.querySelector("input[id='boardTypeD']").value;
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
    const button = event.target;
    const commentDiv = button.closest('.idBoard');

    if (!commentDiv) {
        console.error('댓글 요소를 찾을 수 없습니다.');
        return;
    }

    const inputField = commentDiv.querySelector('.comment-input');
    const commentId = commentDiv.querySelector('.comment-id');
    const boardIde = commentDiv.querySelector('.board-id');
    const boardTypeD = commentDiv.querySelector('.board-type');
    const writerId = commentDiv.querySelector('.writer-id');

    if (!inputField || !commentId || !boardIde || !boardTypeD || !writerId) {
        console.error('필수 요소가 누락되었습니다.');
        return;
    }

    console.log("comment_id: " + commentId.value + ", board_id: " + boardIde.value + ", board_type: " + boardTypeD.value + ", writer_id: " + writerId.value);

    // 원래 스타일 저장
    const originalBorder = inputField.style.border;
    const originalOverflow = inputField.style.overflow; // 원래 overflow 값 저장

    // 스크롤바 없애기
    inputField.style.overflow = 'hidden'; // 스크롤바 숨기기

    // 읽기 전용 속성 제거 및 스타일 변경
    inputField.removeAttribute('readonly');
    inputField.style.border = "2px solid #9bdcfd";
    inputField.style.borderTop = "none";
    inputField.style.borderLeft = "none";
    inputField.style.borderRight = "none";

    setTimeout(() => {
        inputField.focus();
        const length = inputField.value.length;
        inputField.setSelectionRange(length, length);
    }, 10);

    button.innerText = '저장';

    function saveEditedComment() {
        const updatedContent = inputField.value;

        if (!updatedContent.trim()) {
            OpenDupdateModal("댓글을 입력하세요.");
            inputField.focus();
            return;
        }

        fetch('/board/comment/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                comment_id: commentId.value,
                board_id: boardIde.value,
                board_type: boardTypeD.value,
                writerId: writerId.value,
                comment_content: updatedContent
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    inputField.setAttribute('readonly', 'true');
                    inputField.style.border = originalBorder;

                    // 스크롤바 원상복구
                    inputField.style.overflow = originalOverflow;

                    button.innerText = '수정';
                    button.onclick = editComment;

                    // Enter 키 이벤트 제거
                    inputField.removeEventListener('keydown', handleEnterKey);

                    OpenDupdateModal("수정했습니다.");
                } else {
                    alert('댓글 수정 실패!');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('댓글 수정 중 오류가 발생했습니다.');
            });
    }

    function handleEnterKey(event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault(); // 기본 동작(줄 바꿈) 방지
            saveEditedComment(); // 댓글 저장
        }
    }

    inputField.addEventListener('keydown', handleEnterKey);
    button.onclick = saveEditedComment;
}

