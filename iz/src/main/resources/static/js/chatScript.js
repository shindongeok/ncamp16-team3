// 대화 기록 저장용 배열
let conversationHistory = [];

// 공통 유틸리티 함수들
function appendUserMessage(text) {
    const messagesDiv = document.getElementById('messages');
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('flex', 'justify-end', 'items-start');

    const userMessageDiv = document.createElement('div');
    userMessageDiv.classList.add('chat-bubble-user');
    userMessageDiv.innerHTML = text.replace(/\n/g, '<br>');

    messageContainer.appendChild(userMessageDiv);

    messagesDiv.appendChild(messageContainer);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
    return messagesDiv;
}

function appendBotMessage(text) {
    const messagesDiv = document.getElementById('messages');
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('flex', 'justify-start', 'items-start');

    const botMessageDiv = document.createElement('div');
    botMessageDiv.classList.add('chat-bubble-bot');
    botMessageDiv.innerHTML = text.replace(/\n/g, '<br>');

    messageContainer.appendChild(botMessageDiv);

    messagesDiv.appendChild(messageContainer);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

function showLoading(message = '처리 중입니다...') {
    const messagesDiv = document.getElementById('messages');
    const loadingContainer = document.createElement('div');
    loadingContainer.classList.add('flex', 'justify-start', 'items-center');

    const loadingDiv = document.createElement('div');
    loadingDiv.id = 'loadingMessage';
    loadingDiv.classList.add('chat-bubble-bot', 'flex', 'items-center');

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
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

function removeLoading() {
    const loadingMessage = document.getElementById('loadingMessage');
    if (loadingMessage) {
        const container = loadingMessage.parentElement;
        if (container) {
            container.remove();
        }
    }
}

// 각 버튼별 핸들러 함수들
async function paydaychat() {
    appendUserMessage("월급까지 남은 일수?");
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
}

async function endtimechat() {
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
}

async function stresschat() {
    appendUserMessage("오늘의 퇴사 수치");
    showLoading('퇴사 통계를 분석중입니다...');

    // 500ms 후에 stress 수치 가져오기
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
}

function feelchat() {
    // 입력창 표시
    document.getElementById('inputSection').classList.remove('hidden');

    // 하소연하기 문장을 화면에 추가
    appendUserMessage("하소연하기");
    appendBotMessage(`안녕하세요. 오늘 하루 어떠셨나요? 말씀해주신 내용을 바탕으로 함께 해결책을 찾아보겠습니다.`);
}

// 대화 종료 처리 함수
function finishchat() {
    // 대화 종료 메시지 추가
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
}

// handleGeneralChat 함수
async function handleGeneralChat(isEndingChat = false) {
    const userInput = document.getElementById('userInput');
    const message = isEndingChat ? "대화종료" : userInput.value.trim();

    // 메시지가 비어있고 대화종료가 아니면 함수 종료
    if (!message && !isEndingChat) return;

    if (!isEndingChat) {
        appendUserMessage(message);
    }

    showLoading('답변을 준비중입니다...');

    // 시스템 메시지 (초기 설정 메시지) - 대화 시작시 한 번만 설정됨
    if (conversationHistory.length === 0) {
        conversationHistory.push({
            "role": "system",
            "content": "역할 설정: 저는 직장인들의 감정을 전문적으로 케어하는 AI 상담사로서, 사용자의 감정을 깊이 이해하고 공감하는 상담을 제공하겠습니다. 저는 항상 친절하고 따뜻한 어조로 대화하며, 전문적이고 세심하게 접근합니다.\\n\n감정 분석 프로세스: 사용자의 모든 대화를 감정적으로 분석하여, 현재 사용자의 감정 상태를 정확하게 파악합니다. 사용자의 감정 상태를 0~100 사이의 퇴사지수로 평가합니다. 이 퇴사지수는 사용자가 직장에서 얼마나 힘들어하는지를 나타내며, 대화 종료 시 제공됩니다.\\n\n대화 가이드라인: 사용자의 감정 상태를 세심하게 살펴보며, 필요할 경우 실질적인 조언과 심리적 지지를 제공합니다.\n 대화종료가 되기전까진 주고받은 내용을 기억하며 대화\n 대화가 종료될 때만 퇴사지수를 제공하며, \"대화종료\"라는 말이 나오면 대화가 종료됩니다.\n 대화에서는 과도하게 긍정적이거나 부정적이지 않게 균형을 잡아가며 대화를 이어갑니다.\n 사용자의 프라이버시와 감정을 최우선으로 존중하며, 필요한 경우 실질적인 대처 방안도 제안.\\n\n퇴사지수 계산 방식: 대화 중에 사용자의 발언을 감정적으로 분석. 부정적인 단어와 긍정적인 단어의 빈도에 따라 퇴사지수를 계산.\n 긍정적인 단어가 많이 사용되면 퇴사지수가 낮아지고, 부정적인 단어가 많이 사용되면 퇴사지수가 높아진다.\n 대화 종료 시 \"오늘의 퇴사지수는 [퇴사지수]입니다"
        });
    }

    // API 호출의 일관성을 위해 여기서도 확인)
    if (isEndingChat && conversationHistory.length > 0 &&
        conversationHistory[conversationHistory.length - 1].content === "대화종료") {
        // 이미 추가되어 있으면 다시 추가하지 않음
    } else {
        conversationHistory.push({ role: 'user', content: message });
    }

    try {
        const response = await fetch('/chat/feelchat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                messages: conversationHistory  // 전체 대화 기록 전송
            })
        });

        const data = await response.json();
        removeLoading();

        // 챗봇 응답을 대화 기록에 추가
        conversationHistory.push({ role: 'assistant', content: data.responseMessage });

        appendBotMessage(data.responseMessage); // 챗봇 응답 메시지 처리
    } catch (error) {
        removeLoading();
        appendBotMessage('죄송합니다. 답변 생성 중 오류가 발생했습니다.');
    }

    // 대화종료 모드가 아닌 경우에만 입력창 초기화
    if (!isEndingChat) {
        userInput.value = '';  // 입력창 초기화
    }
}

function formatTimestamp(timestamp) {
    const date = new Date(timestamp * 1000); // Unix timestamp를 밀리초로 변환
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 맞춤형 이직 공고 핸들러
async function handleJobPostings() {
    appendUserMessage("맞춤형 이직 공고");
    showLoading("맞춤형 채용공고를 찾고 있습니다...");

    try {
        const response = await fetch('/user/analyze/recommend-jobs', {
            method: 'POST'
        });

        const recommendedJobs = await response.json();
        removeLoading();

        if (recommendedJobs.length === 0) {
            appendBotMessage("현재 맞춤형 채용공고가 없습니다.");
            return;
        }

        // 챗봇 메시지 컨테이너 생성
        const messagesDiv = document.getElementById('messages');
        const messageContainer = document.createElement('div');
        messageContainer.classList.add('flex', 'justify-start', 'items-start');

        const botMessageDiv = document.createElement('div');
        botMessageDiv.classList.add('chat-bubble-bot');
        botMessageDiv.style.maxWidth = '90%';  // 채용공고 카드를 위해 더 넓게

        // 챗봇 메시지와 채용공고 카드들을 담을 컨테이너
        const contentDiv = document.createElement('div');
        contentDiv.innerHTML = '<p class="mb-3">맞춤형 채용공고를 찾았습니다:</p>';

        // 채용공고 카드들 생성
        recommendedJobs.forEach((job, index) => {
            const experience = job.experienceMax === 0 && job.experienceMin === 0
                ? '신입'
                : (job.experienceMax === job.experienceMin
                    ? `경력 ${job.experienceMax}년`
                    : `경력 ${Math.min(job.experienceMax, job.experienceMin)}~${Math.max(job.experienceMax, job.experienceMin)}년`);

            const expirationDate = formatTimestamp(parseFloat(job.expirationTimestamp));

            const jobCard = document.createElement('div');
            jobCard.className = 'job-card mb-3';
            jobCard.innerHTML = `
                <div>
                    <h3 class="text-base font-bold mb-1 text-gray-800 break-words">${job.companyName}</h3>
                    <p class="text-sm text-gray-700 mb-2 break-words">${job.title}</p>
                    <div class="space-y-1 text-xs text-gray-600">
                        <div class="flex items-start gap-2">
                            <i class="fas fa-map-marker-alt w-4 text-center flex-shrink-0"></i>
                            <span class="break-words">${job.locationName}</span>
                        </div>
                        <div class="flex items-start gap-2">
                            <i class="fas fa-building w-4 text-center flex-shrink-0"></i>
                            <span class="break-words">${job.industryName}</span>
                        </div>
                        <div class="flex items-start gap-2">
                            <i class="fas fa-users w-4 text-center flex-shrink-0"></i>
                            <span class="break-words">${experience}</span>
                        </div>
                    </div>
                </div>
                <div class="mt-2 pt-2 border-t border-gray-200">
                    <p class="text-xs text-gray-500 break-words">
                        마감일: ${expirationDate}
                    </p>
                    <a href="${job.url}" target="_blank" class="mt-1 text-xs text-blue-600 hover:text-blue-800 flex items-center gap-1">
                        <i class="fas fa-external-link-alt flex-shrink-0"></i>
                        <span class="break-words">상세 페이지 바로가기</span>
                    </a>
                </div>
            `;

            contentDiv.appendChild(jobCard);
        });

        // 더보기 버튼 추가
        const viewMoreBtn = document.createElement('a');
        viewMoreBtn.href = '/job/hire';
        viewMoreBtn.className = 'w-full flex justify-center items-center py-2 mt-1 bg-gray-100 hover:bg-gray-200 rounded-lg text-sm text-gray-700 transition-colors';
        viewMoreBtn.innerHTML = '더 많은 채용 공고 보기 <i class="fas fa-chevron-right ml-1"></i>';
        contentDiv.appendChild(viewMoreBtn);

        botMessageDiv.appendChild(contentDiv);
        messageContainer.appendChild(botMessageDiv);

        messagesDiv.appendChild(messageContainer);
        messagesDiv.scrollTop = messagesDiv.scrollHeight;

    } catch (error) {
        removeLoading();
        appendBotMessage('맞춤형 채용공고를 불러오는 중 오류가 발생했습니다.');
        console.error('Error fetching recommended jobs:', error);
    }
}

// Enter 키 이벤트 핸들러
function checkEnter(event) {
    if (event.key === 'Enter') {
        handleGeneralChat();
    }
}