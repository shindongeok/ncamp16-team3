let conversationHistory = [];
let userHasSpoken = false;
let feelChatStarted = false;

window.addEventListener('load', () => {
    // 페이지 로드 시 입력란 숨기기
    document.getElementById('inputSection').classList.add('hidden');
    scrollToBottom();
    initButtonGroupState();
});

window.addEventListener('resize', scrollToBottom);

function initButtonGroupState() {
    const container = document.getElementById('buttonGroupContainer');
    if (!container) return;

    const buttonsWrapper = container.querySelector('div');
    const toggleIcon = container.querySelector('svg');

    // 애니메이션을 위한 전환 클래스 추가
    container.classList.add('transition-all', 'duration-500', 'ease-in-out');
    toggleIcon.classList.add('transition-transform', 'duration-500', 'ease-in-out');
    buttonsWrapper.classList.add('transition-all', 'duration-500', 'ease-in-out');

    // 항상 펼쳐진 상태로 시작
    container.style.maxHeight = '200px'; // 고정된 픽셀 값 사용
    toggleIcon.classList.remove('rotate-180');
    buttonsWrapper.style.opacity = '1';
    buttonsWrapper.style.transform = 'translateY(0)';
    buttonsWrapper.style.visibility = 'visible';

    // 명시적으로 localStorage에 펼쳐진 상태 저장
    localStorage.setItem('buttonGroupCollapsed', 'false');
}

function handleGeneralChat(isEndingChat = false) {
    const userInput = document.getElementById('userInput');
    if (userInput.disabled) return;
    userInput.disabled = true;

    const message = userInput.value.trim();

    if (!message) {
        userInput.disabled = false;
        return;
    }

    appendUserMessage(message);
    userInput.value = '';

    if (feelChatStarted) {
        if (!userHasSpoken) {
            userHasSpoken = true;

            // 사용자가 첫 메시지를 보냈을 때 대화종료 버튼 표시
            const endChatBtn = document.getElementById('endChatBtn');
            if (endChatBtn) {
                endChatBtn.classList.remove('hidden');
            }
        }
    }

    showLoading('답변을 준비중입니다...');

    if (conversationHistory.length === 0) {
        conversationHistory.push({
            "role": "system",
            "content": "역할 설정: 저는 직장인들의 감정을 전문적으로 케어하는 AI 상담사로서, 사용자의 감정을 깊이 이해하고 공감하는 상담을 제공하겠습니다. 저는 항상 친절하고 따뜻한 어조로 대화하며, 전문적이고 세심하게 접근합니다.\n\n감정 분석 프로세스: 사용자의 모든 대화를 감정적으로 분석하여, 현재 사용자의 감정 상태를 정확하게 파악합니다. 사용자의 감정 상태를 0~100 사이의 퇴사지수로 평가합니다. 이 퇴사지수는 사용자가 직장에서 얼마나 힘들어하는지를 나타내며, 대화 종료 시 제공됩니다.\n\n대화 가이드라인: 사용자의 감정 상태를 세심하게 살펴보며, 필요할 경우 실질적인 조언과 심리적 지지를 제공합니다.\n 대화종료가 되기전까지 주고받은 내용을 기억하며 대화\n 대화가 종료될 때만 퇴사지수를 제공하며, \"대화종료\"라는 말이 나오면 대화가 종료됩니다.\n 대화에서는 과도하게 긍정적이거나 부정적이지 않게 균형을 잡아가며 대화를 이어갑니다.\n 사용자의 프라이버시와 감정을 최우선으로 존중하며, 필요한 경우 실질적인 대처 방안도 제안.\n\n퇴사지수 계산 방식: 대화 중에 사용자의 발언을 감정적으로 분석. 부정적인 단어와 긍정적인 단어의 빈도에 따라 퇴사지수를 계산.\n 긍정적인 단어가 많이 사용되면 퇴사지수가 낮아지고, 부정적인 단어가 많이 사용되면 퇴사지수가 높아진다.\n 대화 종료 시 \"오늘의 퇴사지수는 [퇴사지수]입니다"
        });
    }

    conversationHistory.push({ role: 'user', content: message });

    fetch('/chat/feelchat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            messages: conversationHistory
        })
    })
        .then(response => response.json())
        .then(data => {
            removeLoading();
            conversationHistory.push({ role: 'assistant', content: data.responseMessage });
            appendBotMessage(data.responseMessage);
            scrollToBottom();
        })
        .catch(error => {
            removeLoading();
            appendBotMessage('죄송합니다. 답변 생성 중 오류가 발생했습니다.');
            scrollToBottom();
        })
        .finally(() => {
            userInput.disabled = false;
        });
}

function scrollToBottom() {
    const messagesDiv = document.getElementById('messages');
    if (messagesDiv) {
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
    }
}

function toggleButtonGroup() {
    const container = document.getElementById('buttonGroupContainer');
    const buttonsWrapper = container.querySelector('div');
    const toggleIcon = container.querySelector('svg');

    // 현재 상태 확인
    const isCurrentlyExpanded = !container.classList.contains('max-h-10');

    if (isCurrentlyExpanded) {
        // 접기
        // 먼저 버튼들 페이드 아웃
        buttonsWrapper.style.opacity = '0';
        buttonsWrapper.style.transform = 'translateY(-20px)';
        buttonsWrapper.style.pointerEvents = 'none';

        // 아이콘 회전
        toggleIcon.style.transform = 'rotate(180deg)';

        // 버튼이 사라진 후 컨테이너 높이 변경
        setTimeout(() => {
            container.classList.remove('max-h-[200px]');
            container.classList.add('max-h-10');
            buttonsWrapper.classList.add('hidden');
        }, 400);

        localStorage.setItem('buttonGroupCollapsed', 'true');
    } else {
        // 펼치기
        // 먼저 버튼 보이게 하기 (높이는 아직 그대로)
        buttonsWrapper.classList.remove('hidden');

        // 약간의 지연 후에 컨테이너 높이 변경
        setTimeout(() => {
            container.classList.add('max-h-[200px]');
            container.classList.remove('max-h-10');

            // 아이콘 회전
            toggleIcon.style.transform = 'rotate(0deg)';

            // 버튼 페이드 인 (컨테이너가 커지는 동안)
            setTimeout(() => {
                buttonsWrapper.style.opacity = '1';
                buttonsWrapper.style.transform = 'translateY(0)';
                buttonsWrapper.style.pointerEvents = 'auto';
            }, 100);
        }, 50);

        localStorage.setItem('buttonGroupCollapsed', 'false');
    }
}function toggleButtonGroup() {
    const container = document.getElementById('buttonGroupContainer');
    const buttonsWrapper = container.querySelector('div');
    const toggleIcon = container.querySelector('svg');

    // 현재 상태 확인
    const isCurrentlyExpanded = container.style.maxHeight !== '40px';

    if (isCurrentlyExpanded) {
        // 접기
        // 버튼 그룹 페이드 아웃
        buttonsWrapper.style.opacity = '0';
        buttonsWrapper.style.transform = 'translateY(-20px)';

        // 아이콘 회전
        toggleIcon.classList.add('rotate-180');

        // 약간의 지연 후 높이 변경 및 버튼 숨김
        setTimeout(() => {
            buttonsWrapper.style.visibility = 'hidden'; // display:none 대신 visibility 사용
            container.style.maxHeight = '40px';
        }, 250); // 절반의 시간 후 높이 변경

        localStorage.setItem('buttonGroupCollapsed', 'true');
    } else {
        // 펼치기
        // 먼저 컨테이너 높이 변경
        container.style.maxHeight = '200px';

        // 아이콘 회전
        toggleIcon.classList.remove('rotate-180');

        // 약간의 지연 후 버튼 표시
        setTimeout(() => {
            buttonsWrapper.style.visibility = 'visible';

            // 약간의 추가 지연 후 버튼 페이드 인
            setTimeout(() => {
                buttonsWrapper.style.opacity = '1';
                buttonsWrapper.style.transform = 'translateY(0)';
            }, 50);
        }, 100);

        localStorage.setItem('buttonGroupCollapsed', 'false');
    }
}

function checkEnter(event) {
    if (event.key === 'Enter') {
        handleGeneralChat();
    }
}

function formatTimestamp(timestamp) {
    const date = new Date(timestamp * 1000);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function appendUserMessage(text) {
    const messagesDiv = document.getElementById('messages');
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('flex', 'justify-end', 'items-start', 'mb-2', 'user-message-container');

    const userMessageDiv = document.createElement('div');
    userMessageDiv.classList.add('bg-main-sky-highlight', 'text-white', 'px-3', 'py-2', 'rounded-lg', 'max-w-[80%]', 'break-words', 'text-sm');
    userMessageDiv.innerHTML = text.replace(/\n/g, '<br>');

    messageContainer.appendChild(userMessageDiv);
    messagesDiv.appendChild(messageContainer);

    scrollToBottom();

    return messagesDiv;
}

function appendBotMessage(text) {
    const messagesDiv = document.getElementById('messages');
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('flex', 'justify-start', 'items-start', 'mb-2');

    const botMessageDiv = document.createElement('div');
    botMessageDiv.classList.add('bg-gray-100', 'text-black', 'px-3', 'py-2', 'rounded-lg', 'max-w-[80%]', 'break-words', 'text-sm');
    botMessageDiv.innerHTML = text.replace(/\n/g, '<br>');

    messageContainer.appendChild(botMessageDiv);
    messagesDiv.appendChild(messageContainer);

    scrollToBottom();
}

function showLoading(message = '처리 중입니다...') {
    const messagesDiv = document.getElementById('messages');
    const loadingContainer = document.createElement('div');
    loadingContainer.classList.add('flex', 'justify-start', 'items-center');

    const loadingDiv = document.createElement('div');
    loadingDiv.id = 'loadingMessage';
    loadingDiv.classList.add('bg-gray-100', 'text-black', 'px-3', 'py-2', 'rounded-lg', 'flex', 'items-center');

    loadingDiv.innerHTML = `
        <div class="flex space-x-1">
            <div class="animate-bounce h-2 w-2 bg-gray-500 rounded-full"></div>
            <div class="animate-bounce h-2 w-2 bg-gray-500 rounded-full" style="animation-delay: 0.2s;"></div>
            <div class="animate-bounce h-2 w-2 bg-gray-500 rounded-full" style="animation-delay: 0.4s;"></div>
        </div>
    `;

    loadingContainer.appendChild(loadingDiv);
    messagesDiv.appendChild(loadingContainer);

    scrollToBottom();
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

function addEndChatButton() {
    const messagesDiv = document.getElementById('messages');

    const existingButtons = document.querySelectorAll('#userMessageEndChatBtn');
    existingButtons.forEach(button => {
        button.parentElement.remove();
    });

    const buttonContainer = document.createElement('div');
    buttonContainer.classList.add('flex', 'justify-end', 'mt-1', 'mb-2');

    const endChatButton = document.createElement('button');
    endChatButton.classList.add('text-xs', 'text-gray-500', 'hover:underline', 'cursor-pointer');
    endChatButton.textContent = '대화종료';
    endChatButton.onclick = finishchat;
    endChatButton.id = 'userMessageEndChatBtn';

    buttonContainer.appendChild(endChatButton);

    const userMessages = document.querySelectorAll('.user-message-container');
    if (userMessages.length > 0) {
        const lastUserMessage = userMessages[userMessages.length - 1];
        lastUserMessage.after(buttonContainer);
    } else {
        messagesDiv.appendChild(buttonContainer);
    }

    scrollToBottom();
}

function handleGeneralChat(isEndingChat = false) {
    const userInput = document.getElementById('userInput');
    if (userInput.disabled) return;
    userInput.disabled = true;

    const message = userInput.value.trim();

    if (!message) {
        userInput.disabled = false;
        return;
    }

    appendUserMessage(message);
    userInput.value = '';

    if (feelChatStarted) {
        if (!userHasSpoken) {
            userHasSpoken = true;
        }

        if (message !== "대화종료") {
            addEndChatButton();
        }
    }

    showLoading('답변을 준비중입니다...');

    if (conversationHistory.length === 0) {
        conversationHistory.push({
            "role": "system",
            "content": "역할 설정: 저는 직장인들의 감정을 전문적으로 케어하는 AI 상담사로서, 사용자의 감정을 깊이 이해하고 공감하는 상담을 제공하겠습니다. 저는 항상 친절하고 따뜻한 어조로 대화하며, 전문적이고 세심하게 접근합니다.\n\n감정 분석 프로세스: 사용자의 모든 대화를 감정적으로 분석하여, 현재 사용자의 감정 상태를 정확하게 파악합니다. 사용자의 감정 상태를 0~100 사이의 퇴사지수로 평가합니다. 이 퇴사지수는 사용자가 직장에서 얼마나 힘들어하는지를 나타내며, 대화 종료 시 제공됩니다.\n\n대화 가이드라인: 사용자의 감정 상태를 세심하게 살펴보며, 필요할 경우 실질적인 조언과 심리적 지지를 제공합니다.\n 대화종료가 되기전까지 주고받은 내용을 기억하며 대화\n 대화가 종료될 때만 퇴사지수를 제공하며, \"대화종료\"라는 말이 나오면 대화가 종료됩니다.\n 대화에서는 과도하게 긍정적이거나 부정적이지 않게 균형을 잡아가며 대화를 이어갑니다.\n 사용자의 프라이버시와 감정을 최우선으로 존중하며, 필요한 경우 실질적인 대처 방안도 제안.\n\n퇴사지수 계산 방식: 대화 중에 사용자의 발언을 감정적으로 분석. 부정적인 단어와 긍정적인 단어의 빈도에 따라 퇴사지수를 계산.\n 긍정적인 단어가 많이 사용되면 퇴사지수가 낮아지고, 부정적인 단어가 많이 사용되면 퇴사지수가 높아진다.\n 대화 종료 시 \"오늘의 퇴사지수는 [퇴사지수]입니다"
        });
    }

    conversationHistory.push({ role: 'user', content: message });

    fetch('/chat/feelchat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            messages: conversationHistory
        })
    })
        .then(response => response.json())
        .then(data => {
            removeLoading();
            conversationHistory.push({ role: 'assistant', content: data.responseMessage });
            appendBotMessage(data.responseMessage);
            scrollToBottom();
        })
        .catch(error => {
            removeLoading();
            appendBotMessage('죄송합니다. 답변 생성 중 오류가 발생했습니다.');
            scrollToBottom();
        })
        .finally(() => {
            userInput.disabled = false;
        });
}

function feelchat() {
    // 하소연하기 버튼 클릭 시 입력란 표시
    document.getElementById('inputSection').classList.remove('hidden');
    appendUserMessage("하소연하기");
    appendBotMessage(`안녕하세요. 오늘 하루 어떠셨나요? 말씀해주신 내용을 바탕으로 함께 해결책을 찾아보겠습니다.`);

    feelChatStarted = true;
    userHasSpoken = false;

    document.getElementById('finishChatBtn').style.display = 'none';
    conversationHistory = [];
}

function stresschat() {
    document.getElementById('inputSection').classList.add('hidden');
    document.getElementById('finishChatBtn').style.display = 'none';

    feelChatStarted = false;
    userHasSpoken = false;

    appendUserMessage("오늘의 퇴사 수치");
    showLoading('퇴사 통계를 분석중입니다...');

    setTimeout(() => {
        const stressStr = document.getElementById('stressData').getAttribute('data-stress');
        console.log(stressStr);

        if (!stressStr) {
            removeLoading();
            appendBotMessage("퇴사 지수가 없습니다.");
            return;
        }

        removeLoading();
        appendBotMessage(`오늘의 퇴사지수는 ${stressStr}% 입니다.`);
    }, 500);

    scrollToBottom();
}

function paydaychat() {
    document.getElementById('inputSection').classList.add('hidden');
    document.getElementById('finishChatBtn').style.display = 'none';

    feelChatStarted = false;
    userHasSpoken = false;

    appendUserMessage("월급까지 남은 날짜");
    showLoading("급여일 정보를 확인하고 있습니다...");

    const paydayStr = document.getElementById('paydayData').getAttribute('data-payday');

    if (!paydayStr) {
        removeLoading();
        appendBotMessage("급여일 정보를 찾을 수 없습니다.");
        return;
    }

    const today = new Date();
    const payday = new Date(today.getFullYear(), today.getMonth(), parseInt(paydayStr));

    if (payday < today) {
        payday.setMonth(payday.getMonth() + 1);
    }

    setTimeout(() => {
        const timeDiff = payday - today;
        const daysLeft = Math.ceil(timeDiff / (1000 * 60 * 60 * 24));

        removeLoading();
        appendBotMessage(`월급일까지 ${daysLeft}일 남았습니다.`);
    }, 500);

    scrollToBottom();
}

function endtimechat() {
    document.getElementById('inputSection').classList.add('hidden');
    document.getElementById('finishChatBtn').style.display = 'none';

    feelChatStarted = false;
    userHasSpoken = false;

    appendUserMessage("남은 퇴근시간");
    showLoading("퇴근시간 정보를 확인하고 있습니다...");

    setTimeout(() => {
        const endtimeElement = document.getElementById('endtimeData');
        const endtimeStr = endtimeElement.getAttribute('data-endtime');

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

        const diffHours = Math.floor(diffTime / (1000 * 60 * 60));
        const diffMinutes = Math.floor((diffTime % (1000 * 60 * 60)) / (1000 * 60));

        removeLoading();
        appendBotMessage(`퇴근까지 ${diffHours}시간 ${diffMinutes}분 남았습니다.`);
    }, 500);

    scrollToBottom();
}

function finishchat() {
    const endChatButtons = document.querySelectorAll('#userMessageEndChatBtn');
    endChatButtons.forEach(button => {
        button.parentElement.remove();
    });

    appendUserMessage("대화종료");
    conversationHistory.push({ role: 'user', content: "대화종료" });
    showLoading('대화를 종료중입니다...');

    fetch('/chat/feelchat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            messages: conversationHistory
        })
    })
        .then(response => response.json())
        .then(data => {
            removeLoading();
            appendBotMessage(data.responseMessage);
            scrollToBottom();
        })
        .catch(error => {
            removeLoading();
            appendBotMessage('죄송합니다. 대화 종료 중 오류가 발생했습니다.');
            scrollToBottom();
        });

    document.getElementById('inputSection').classList.add('hidden');
    feelChatStarted = false;
    userHasSpoken = false;
    document.getElementById('finishChatBtn').style.display = 'none';
}

function handleJobPostings() {
    document.getElementById('inputSection').classList.add('hidden');
    document.getElementById('finishChatBtn').style.display = 'none';

    feelChatStarted = false;
    userHasSpoken = false;

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

                const contentDiv = document.createElement('div');
                contentDiv.innerHTML = '<p class="mb-3 text-sm">맞춤형 채용공고를 찾았습니다.</p>';

                recommendedJobs.forEach((job, index) => {
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

                const viewMoreBtn = document.createElement('a');
                viewMoreBtn.href = '/job/hire';
                viewMoreBtn.className = 'block w-full text-center text-sm text-blue-600 hover:underline mt-2';
                viewMoreBtn.innerHTML = '더 많은 채용 공고 보기';
                contentDiv.appendChild(viewMoreBtn);

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

    scrollToBottom();
}