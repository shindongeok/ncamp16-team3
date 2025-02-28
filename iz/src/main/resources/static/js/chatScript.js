// 대화 기록 저장용 배열
let conversationHistory = [];

// 스크롤을 가장 아래로 이동시키는 함수
function scrollToBottom() {
    const messagesDiv = document.getElementById('messages');
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

// 사용자 메시지 추가 함수 수정
function appendUserMessage(text) {
    const messagesDiv = document.getElementById('messages');
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('flex', 'justify-end', 'items-start');

    const userMessageDiv = document.createElement('div');
    userMessageDiv.classList.add('bg-blue-500', 'text-white', 'px-3', 'py-2', 'rounded-lg', 'max-w-[80%]', 'break-words', 'text-sm');
    userMessageDiv.innerHTML = text.replace(/\n/g, '<br>');

    messageContainer.appendChild(userMessageDiv);
    messagesDiv.appendChild(messageContainer);

    // 메시지 추가 후 스크롤을 아래로 이동
    scrollToBottom();

    return messagesDiv;
}

// 봇 메시지 추가 함수 수정
function appendBotMessage(text) {
    const messagesDiv = document.getElementById('messages');
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('flex', 'justify-start', 'items-start');

    const botMessageDiv = document.createElement('div');
    botMessageDiv.classList.add('bg-gray-100', 'text-black', 'px-3', 'py-2', 'rounded-lg', 'max-w-[80%]', 'break-words', 'text-sm');
    botMessageDiv.innerHTML = text.replace(/\n/g, '<br>');

    messageContainer.appendChild(botMessageDiv);
    messagesDiv.appendChild(messageContainer);

    // 메시지 추가 후 스크롤을 아래로 이동
    scrollToBottom();
}

// 로딩 메시지 추가 함수 수정
function showLoading(message = '처리 중입니다...') {
    const messagesDiv = document.getElementById('messages');
    const loadingContainer = document.createElement('div');
    loadingContainer.classList.add('flex', 'justify-start', 'items-center');

    const loadingDiv = document.createElement('div');
    loadingDiv.id = 'loadingMessage';
    loadingDiv.classList.add('bg-gray-100', 'text-black', 'px-3', 'py-2', 'rounded-lg', 'flex', 'items-center');

    // 로딩 애니메이션 추가
    loadingDiv.innerHTML = `
        <div class="flex space-x-1">
            <div class="animate-bounce h-2 w-2 bg-gray-500 rounded-full"></div>
            <div class="animate-bounce h-2 w-2 bg-gray-500 rounded-full" style="animation-delay: 0.2s;"></div>
            <div class="animate-bounce h-2 w-2 bg-gray-500 rounded-full" style="animation-delay: 0.4s;"></div>
        </div>
    `;

    loadingContainer.appendChild(loadingDiv);
    messagesDiv.appendChild(loadingContainer);

    // 로딩 메시지 추가 후 스크롤을 아래로 이동
    scrollToBottom();
}

// 로딩 메시지 제거 함수
function removeLoading() {
    const loadingMessage = document.getElementById('loadingMessage');
    if (loadingMessage) {
        const container = loadingMessage.parentElement;
        if (container) {
            container.remove();
        }
    }
}

// 대화종료 버튼 표시/숨김 상태를 관리하는 변수 추가
let userHasSpoken = false;
let feelChatStarted = false;

function feelchat() {
    // 입력창 표시
    document.getElementById('inputSection').classList.remove('hidden');

    // 하소연하기 문장을 화면에 추가
    appendUserMessage("하소연하기");
    appendBotMessage(`안녕하세요. 오늘 하루 어떠셨나요? 말씀해주신 내용을 바탕으로 함께 해결책을 찾아보겠습니다.`);

    // 대화 시작 플래그 설정
    feelChatStarted = true;
    userHasSpoken = false;

    // 대화종료 버튼 숨기기
    document.getElementById('finishChatBtn').style.display = 'none';
}

// 다른 버튼 함수들에 입력창 숨기기 로직 추가
function stresschat() {
    // 입력창 숨기기
    document.getElementById('inputSection').classList.add('hidden');

    // 대화종료 버튼 숨기기
    document.getElementById('finishChatBtn').style.display = 'none';

    // 대화 상태 초기화
    feelChatStarted = false;
    userHasSpoken = false;

    // 기존 stresschat 로직
    appendUserMessage("오늘의 퇴사 수치");
    showLoading('퇴사 통계를 분석중입니다...');

    setTimeout(() => {
        const stressStr = document.getElementById('stressData').getAttribute('data-stress');

        console.log(stressStr);  // 스트레스 값이 잘 나오는지 확인하기 위해 콘솔에 출력

        if (!stressStr) {
            // 데이터가 없으면 해당 메시지 출력
            removeLoading();
            appendBotMessage("퇴사 지수가 없습니다.");
            return;
        }

        // 데이터가 있으면 퇴사 지수 출력
        removeLoading();
        appendBotMessage(`오늘의 퇴사지수는 ${stressStr}% 입니다.`);
    }, 500);

    // 결과 표시 후 스크롤을 아래로 이동
    scrollToBottom();
}

function paydaychat() {
    // 입력창 숨기기
    document.getElementById('inputSection').classList.add('hidden');

    // 대화종료 버튼 숨기기
    document.getElementById('finishChatBtn').style.display = 'none';

    // 대화 상태 초기화
    feelChatStarted = false;
    userHasSpoken = false;

    // 기존 paydaychat 로직
    appendUserMessage("월급까지 남은 날짜");
    showLoading("급여일 정보를 확인하고 있습니다...");

    // Thymeleaf를 통해 HTML에 주입된 데이터 가져오기
    const paydayStr = document.getElementById('paydayData').getAttribute('data-payday');

    if (!paydayStr) {
        removeLoading();
        appendBotMessage("급여일 정보를 찾을 수 없습니다.");
        return;
    }

    // 오늘 날짜
    const today = new Date();
    const payday = new Date(today.getFullYear(), today.getMonth(), parseInt(paydayStr));

    // 월급날이 이미 지났으면 다음 달로 변경
    if (payday < today) {
        payday.setMonth(payday.getMonth() + 1);
    }

    // D-DAY 계산
    setTimeout(() => {
        const timeDiff = payday - today;
        const daysLeft = Math.ceil(timeDiff / (1000 * 60 * 60 * 24));

        // 로딩 메시지 제거 후 챗봇 응답 추가
        removeLoading();
        appendBotMessage(`월급일까지 ${daysLeft}일 남았습니다.`);
    }, 500);

    // 결과 표시 후 스크롤을 아래로 이동
    scrollToBottom();
}

function endtimechat() {
    // 입력창 숨기기
    document.getElementById('inputSection').classList.add('hidden');

    // 대화종료 버튼 숨기기
    document.getElementById('finishChatBtn').style.display = 'none';

    // 대화 상태 초기화
    feelChatStarted = false;
    userHasSpoken = false;

    // 기존 endtimechat 로직
    appendUserMessage("남은 퇴근시간");
    showLoading("퇴근시간 정보를 확인하고 있습니다...");

    setTimeout(() => {
        const endtimeElement = document.getElementById('endtimeData');
        const endtimeStr = endtimeElement.getAttribute('data-endtime'); // "2025-02-21 18:00" 같은 형태

        if (!endtimeStr) {
            removeLoading();
            appendBotMessage("퇴근시간 정보를 불러올 수 없습니다.");
            return;
        }

        const today = new Date();
        const [endHour, endMinute] = endtimeStr.split(":").map(Number);
        const endtime = new Date(today.getFullYear(), today.getMonth(), today.getDate(), endHour, endMinute);

        const diffTime = endtime - today;

        if (diffTime <= 0) {
            removeLoading();
            appendBotMessage("퇴근 시간이 지났습니다!");
            return;
        }

        const diffHours = Math.floor(diffTime / (1000 * 60 * 60)); // 남은 시간
        const diffMinutes = Math.floor((diffTime % (1000 * 60 * 60)) / (1000 * 60)); // 남은 분

        removeLoading();
        appendBotMessage(`퇴근까지 ${diffHours}시간 ${diffMinutes}분 남았습니다.`);
    }, 500); // 0.5초 대기 후 처리

    // 결과 표시 후 스크롤을 아래로 이동
    scrollToBottom();
}

function finishchat() {
    // 입력창 숨기기
    document.getElementById('inputSection').classList.add('hidden');

    // 대화 상태 초기화
    feelChatStarted = false;
    userHasSpoken = false;

    // 기존 finishchat 로직 유지
    const message = "대화종료";

    // 대화 종료 메시지를 화면에 추가
    appendUserMessage(message);

    // 임시로 userInput 값을 설정하고 handleGeneralChat 호출
    const userInput = document.getElementById('userInput');
    const originalValue = userInput.value; // 기존 값 저장

    userInput.value = message; // userInput 값을 "대화종료"로 변경

    // handleGeneralChat 호출 (이로써 "대화종료" 메시지가 API로 전송됨)
    handleGeneralChat(true); // true 파라미터로 대화종료 모드 표시

    // 원래 값으로 복원 (필요한 경우)
    userInput.value = originalValue;

    // 대화종료 버튼 숨기기
    document.getElementById('finishChatBtn').style.display = 'none';

    // 결과 표시 후 스크롤을 아래로 이동
    scrollToBottom();
}

function handleJobPostings() {
    // 입력창 숨기기
    document.getElementById('inputSection').classList.add('hidden');

    // 대화종료 버튼 숨기기
    document.getElementById('finishChatBtn').style.display = 'none';

    // 대화 상태 초기화
    feelChatStarted = false;
    userHasSpoken = false;

    // 기존 handleJobPostings 로직
    appendUserMessage("맞춤형 이직 공고");
    showLoading("맞춤형 채용공고를 찾고 있습니다...");

    try {
        fetch('/user/analyze/recommend-jobs', {
            method: 'POST'
        })
            .then(response => response.json())
            .then(recommendedJobs => {
                removeLoading();

                if (recommendedJobs.length === 0) {
                    appendBotMessage("현재 맞춤형 채용공고가 없습니다.");
                    return;
                }

                // 채용공고 카드 생성을 위한 contentDiv 생성
                const contentDiv = document.createElement('div');
                contentDiv.innerHTML = '<p class="mb-3 text-sm">맞춤형 채용공고를 찾았습니다.</p>';

                recommendedJobs.forEach((job, index) => {
                    // 경력 계산 (신입/경력 표시)
                    const experience = job.experienceMax === 0 && job.experienceMin === 0
                        ? '신입'
                        : (job.experienceMax === job.experienceMin
                            ? `${job.experienceMax}년 경력`
                            : `${Math.min(job.experienceMax, job.experienceMin)}~${Math.max(job.experienceMax, job.experienceMin)}년 경력`);

                    const jobCard = document.createElement('div');
                    jobCard.className = 'rounded-lg p-4 mb-3 bg-white shadow-sm';

                    jobCard.innerHTML = `
                    <div class="mb-2">
                        <h3 class="text-base text-gray-800">${job.companyName}</h3>
                    </div>
                    
                    <div class="mb-2">
                        <h4 class="text-sm text-gray-700">${job.title}</h4>
                    </div>
                    
                    <div class="text-xs text-gray-600 space-y-1">
                        <div class="flex items-center">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                            </svg>
                            ${job.locationName}
                        </div>
                        <div class="flex items-center">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                            </svg>
                            ${job.industryName}
                        </div>
                        <div class="flex items-center">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                            </svg>
                            ${experience}
                        </div>
                        <div class="flex items-center">
                            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 text-gray-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                            </svg>
                            ${job.educationLevel || '학력 무관'}
                        </div>
                    </div>
                    
                    <div class="mt-3 flex justify-between items-center">
                        <span class="text-xs text-gray-500">마감일: ${formatTimestamp(parseFloat(job.expirationTimestamp))}</span>
                        <a href="${job.url}" target="_blank" class="text-xs text-blue-600 hover:underline">상세보기</a>
                    </div>
                `;

                    contentDiv.appendChild(jobCard);
                });

                // 더보기 버튼 추가
                const viewMoreBtn = document.createElement('a');
                viewMoreBtn.href = '/job/hire';
                viewMoreBtn.className = 'block w-full text-center text-sm text-blue-600 hover:underline mt-2';
                viewMoreBtn.innerHTML = '더 많은 채용 공고 보기';
                contentDiv.appendChild(viewMoreBtn);

                // 메시지 영역에 추가
                const messagesDiv = document.getElementById('messages');
                const botMessageContainer = document.createElement('div');
                botMessageContainer.classList.add('flex', 'justify-start', 'items-start');

                const botMessageDiv = document.createElement('div');
                botMessageDiv.classList.add('bg-gray-100', 'text-black', 'px-3', 'py-2', 'rounded-lg', 'max-w-[90%]', 'break-words');
                botMessageDiv.appendChild(contentDiv);

                botMessageContainer.appendChild(botMessageDiv);
                messagesDiv.appendChild(botMessageContainer);
                messagesDiv.scrollTop = messagesDiv.scrollHeight;
            })
            .catch(error => {
                removeLoading();
                appendBotMessage('맞춤형 채용공고를 불러오는 중 오류가 발생했습니다.');
                console.error('Error fetching recommended jobs:', error);
            });
    } catch (error) {
        removeLoading();
        appendBotMessage('맞춤형 채용공고를 불러오는 중 오류가 발생했습니다.');
        console.error('Error fetching recommended jobs:', error);
    }

    // 결과 표시 후 스크롤을 아래로 이동
    scrollToBottom();
}

// handleGeneralChat 함수
// 입력창 클리어 기능 추가
function handleGeneralChat(isEndingChat = false) {
    const userInput = document.getElementById('userInput');
    if (userInput.disabled) return; // 이미 전송 중이면 실행 방지
    userInput.disabled = true; // 입력창 비활성화 (중복 입력 방지)

    const message = isEndingChat ? "대화종료" : userInput.value.trim();

    // 메시지가 비어있고 대화종료가 아니면 함수 종료
    if (!message && !isEndingChat) {
        userInput.disabled = false;
        return;
    }

    if (!isEndingChat) {
        appendUserMessage(message);
        userInput.value = '';

        // 메시지 전송 즉시 입력창 초기화 (API 응답을 기다리지 않고)
        userInput.value = '';

        // 하소연하기 모드에서 첫 메시지 입력 시 대화종료 버튼 표시
        if (feelChatStarted && !userHasSpoken) {
            userHasSpoken = true;
            document.getElementById('finishChatBtn').style.display = 'block';
        }
    }

    // 기존 handleGeneralChat 로직
    showLoading('답변을 준비중입니다...');

    // 시스템 메시지 (초기 설정 메시지)
    if (conversationHistory.length === 0) {
        conversationHistory.push({
            "role": "system",
            "content": "역할 설정: 저는 직장인들의 감정을 전문적으로 케어하는 AI 상담사로서, 사용자의 감정을 깊이 이해하고 공감하는 상담을 제공하겠습니다. 저는 항상 친절하고 따뜻한 어조로 대화하며, 전문적이고 세심하게 접근합니다.\n\n감정 분석 프로세스: 사용자의 모든 대화를 감정적으로 분석하여, 현재 사용자의 감정 상태를 정확하게 파악합니다. 사용자의 감정 상태를 0~100 사이의 퇴사지수로 평가합니다. 이 퇴사지수는 사용자가 직장에서 얼마나 힘들어하는지를 나타내며, 대화 종료 시 제공됩니다.\n\n대화 가이드라인: 사용자의 감정 상태를 세심하게 살펴보며, 필요할 경우 실질적인 조언과 심리적 지지를 제공합니다.\n 대화종료가 되기전까지 주고받은 내용을 기억하며 대화\n 대화가 종료될 때만 퇴사지수를 제공하며, \"대화종료\"라는 말이 나오면 대화가 종료됩니다.\n 대화에서는 과도하게 긍정적이거나 부정적이지 않게 균형을 잡아가며 대화를 이어갑니다.\n 사용자의 프라이버시와 감정을 최우선으로 존중하며, 필요한 경우 실질적인 대처 방안도 제안.\n\n퇴사지수 계산 방식: 대화 중에 사용자의 발언을 감정적으로 분석. 부정적인 단어와 긍정적인 단어의 빈도에 따라 퇴사지수를 계산.\n 긍정적인 단어가 많이 사용되면 퇴사지수가 낮아지고, 부정적인 단어가 많이 사용되면 퇴사지수가 높아진다.\n 대화 종료 시 \"오늘의 퇴사지수는 [퇴사지수]입니다"
        });
    }

    // API 호출 일관성 확인
    if (isEndingChat && conversationHistory.length > 0 &&
        conversationHistory[conversationHistory.length - 1].content === "대화종료") {
        // 이미 추가되어 있으면 다시 추가하지 않음
    } else {
        conversationHistory.push({ role: 'user', content: message });
    }

    fetch('/chat/feelchat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            messages: conversationHistory  // 전체 대화 기록 전송
        })
    })
        .then(response => response.json())
        .then(data => {
            removeLoading();

            // 챗봇 응답을 대화 기록에 추가
            conversationHistory.push({ role: 'assistant', content: data.responseMessage });

            appendBotMessage(data.responseMessage); // 챗봇 응답 메시지 처리
      
            // 응답 후 스크롤을 아래로 이동
            scrollToBottom();
        })
        .catch(error => {
            removeLoading();
            appendBotMessage('죄송합니다. 답변 생성 중 오류가 발생했습니다.');

            // 오류 메시지 후에도 스크롤을 아래로 이동
            scrollToBottom();
        });
}

// 페이지 로드 시 또는 창 크기 변경 시 스크롤을 아래로 이동
window.addEventListener('load', scrollToBottom);
window.addEventListener('resize', scrollToBottom);

function formatTimestamp(timestamp) {
    const date = new Date(timestamp * 1000); // Unix timestamp를 밀리초로 변환
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// Enter 키 이벤트 핸들러
function checkEnter(event) {
    if (event.key === 'Enter') {
        handleGeneralChat();
    }
}