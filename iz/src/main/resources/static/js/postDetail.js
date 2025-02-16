
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

//수정,삭제 버튼 띄우기
document.addEventListener("DOMContentLoaded", function (){
    const member_id = document.getElementById("member_id").value;
    const writer_id = document.getElementById("writer_id").value;
    const updateDeleteBtn = document.getElementById("updateDeleteBtn");

    if(writer_id === member_id ){
        updateDeleteBtn.classList.remove("hidden");
    }
});



