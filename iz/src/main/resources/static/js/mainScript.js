// 전역 시간 변수들 선언
let globalStartTime = startTime;
let globalLunchTime = lunchTime;
let globalEndTime = endTime;

// 캘린더 관련 함수들
function updateCalendar(monthlyStressList = []) {
    const today = new Date();
    const monday = new Date(today);
    const diff = today.getDay() === 0 ? 6 : today.getDay() - 1;
    monday.setDate(today.getDate() - diff);

    const monthNames = ["1월", "2월", "3월", "4월", "5월", "6월",
        "7월", "8월", "9월", "10월", "11월", "12월"];
    document.querySelector('.today-date').textContent =
        `${monday.getFullYear()}년 ${monthNames[monday.getMonth()]}`;

    const dateGrid = document.querySelector('.calendar-grid:last-child');
    dateGrid.innerHTML = '';

    // 툴팁이 이미 존재한다면 제거
    const existingTooltip = document.getElementById('stress-tooltip');
    if (existingTooltip) {
        existingTooltip.remove();
    }

    for (let i = 0; i < 7; i++) {
        const currentDate = new Date(monday);
        currentDate.setDate(monday.getDate() + i);

        const dateSpan = document.createElement('span');
        dateSpan.className = 'calendar-date cursor-pointer transition-transform duration-200 hover:scale-110 hover:z-10';
        dateSpan.textContent = currentDate.getDate();

        if (currentDate.toDateString() === today.toDateString()) {
            dateSpan.classList.add('active');
        }

        const formattedDate = currentDate.toISOString().split('T')[0];
        const stressData = monthlyStressList.find(item => {
            const itemDate = new Date(item.date).toISOString().split('T')[0];
            return itemDate === formattedDate;
        });

        if (stressData) {
            dateSpan.classList.remove('verybad', 'bad', 'soso', 'good', 'verygood');
            const stressNum = stressData.stress_num;
            if (stressNum >= 80) dateSpan.classList.add('verybad');
            else if (stressNum >= 60 && stressNum < 80) dateSpan.classList.add('bad');
            else if (stressNum >= 40 && stressNum < 60) dateSpan.classList.add('soso');
            else if (stressNum >= 20 && stressNum < 40) dateSpan.classList.add('good');
            else if (stressNum < 20) dateSpan.classList.add('verygood');

            // 날짜에 스트레스 데이터 저장
            dateSpan.dataset.stressNum = stressNum;
            dateSpan.dataset.date = formattedDate;

            // 클릭 이벤트 추가
            dateSpan.addEventListener('click', showStressTooltip);
        }

        dateGrid.appendChild(dateSpan);
    }
}

// 스트레스 툴팁을 표시하는 함수 (Tailwind CSS 사용)
function showStressTooltip(event) {
    // 기존 툴팁 제거
    const existingTooltip = document.getElementById('stress-tooltip');
    if (existingTooltip) {
        existingTooltip.remove();
    }

    const dateElement = event.currentTarget;
    const stressNum = dateElement.dataset.stressNum;
    const dateStr = dateElement.dataset.date;

    if (!stressNum) return;

    // 날짜 포맷팅
    const dateObj = new Date(dateStr);
    const formattedDate = `${dateObj.getFullYear()}년 ${dateObj.getMonth() + 1}월 ${dateObj.getDate()}일`;

    // 스트레스 레벨 텍스트와 색상 결정
    let stressLevel, textColorClass;
    if (stressNum >= 80) {
        stressLevel = '매우 높음';
        textColorClass = 'text-[#ff5353]';
    } else if (stressNum >= 60) {
        stressLevel = '높음';
        textColorClass = 'text-[#ff9898]';
    } else if (stressNum >= 40) {
        stressLevel = '보통';
        textColorClass = 'text-[#ffa500]';
    } else if (stressNum >= 20) {
        stressLevel = '낮음';
        textColorClass = 'text-[#9bdcfd]';
    } else {
        stressLevel = '매우 낮음';
        textColorClass = 'text-[#00a3ed]';
    }

    // 툴팁 생성
    const tooltip = document.createElement('div');
    tooltip.id = 'stress-tooltip';
    tooltip.className = 'absolute z-50 bg-white rounded-xl shadow-md w-40 p-3 animate-fade-in';
    tooltip.innerHTML = `
        <div class="relative">
            <div class="absolute -top-5 left-1/2 transform -translate-x-1/2 w-0 h-0 border-8 border-transparent border-b-white"></div>
            <div class="text-center text-gray-800 mb-1">${formattedDate}</div>
            <div class="text-center font-medium mb-1">퇴사지수: <span class="${textColorClass} font-bold">${stressNum}%</span></div>
            <div class="text-center text-sm text-gray-600">(${stressLevel})</div>
        </div>
    `;

    // 툴팁 위치 계산
    const rect = dateElement.getBoundingClientRect();

    tooltip.style.left = `${rect.left + rect.width/2 - 80}px`; // 툴팁 중앙 정렬
    tooltip.style.top = `${rect.bottom + window.scrollY + 10}px`; // 날짜 아래에 표시

    // body에 툴팁 추가
    document.body.appendChild(tooltip);

    // 툴팁 외부 클릭 시 닫기
    document.addEventListener('click', closeTooltip);

    // 이벤트 전파 중지
    event.stopPropagation();
}

// 툴팁 닫기 함수
function closeTooltip(event) {
    const tooltip = document.getElementById('stress-tooltip');
    if (tooltip && !tooltip.contains(event.target) &&
        !event.target.classList.contains('calendar-date')) {
        tooltip.remove();
        document.removeEventListener('click', closeTooltip);
    }
}

function initializeCalendar(monthlyStressList = []) {
    updateCalendar(monthlyStressList);

    function scheduleNextUpdate() {
        const now = new Date();
        const tomorrow = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1);
        const timeUntilMidnight = tomorrow - now;

        setTimeout(() => {
            updateCalendar(monthlyStressList);
            scheduleNextUpdate();
        }, timeUntilMidnight);
    }

    scheduleNextUpdate();
}

// 스트레스 그래프 관련 함수들
function updateCircleProgress(circle, progress) {
    // 진행률을 0-100 사이로 제한
    progress = Math.min(100, Math.max(0, progress));

    const circumference = parseFloat(circle.getAttribute('stroke-dasharray'));
    const offset = circumference - (progress / 100) * circumference;
    circle.setAttribute('stroke-dashoffset', offset);
}

function calculateTimeProgress(startTime, endTime) {
    const now = new Date();
    const [startHour, startMinute] = startTime.split(':').map(Number);
    const [endHour, endMinute] = endTime.split(':').map(Number);

    const start = startHour * 60 + startMinute;
    const end = endHour * 60 + endMinute;
    const current = now.getHours() * 60 + now.getMinutes();

    if (current < start) return 0;
    if (current > end) return 100;

    return ((current - start) / (end - start)) * 100;
}

function calculateWeeklyAverageStress(monthlyStressList) {
    const today = new Date();
    const monday = new Date(today);
    const diff = today.getDay() === 0 ? 6 : today.getDay() - 1;
    monday.setDate(today.getDate() - diff);

    const mondayStr = monday.toISOString().split('T')[0];
    const todayStr = today.toISOString().split('T')[0];

    const weeklyStress = monthlyStressList.filter(item => {
        const itemDate = item.date;
        return itemDate >= mondayStr && itemDate <= todayStr;
    });

    if (weeklyStress.length === 0) return null;

    const sum = weeklyStress.reduce((acc, item) => acc + Number(item.stress_num), 0);
    const average = sum / weeklyStress.length;

    return Math.round(average);
}

function formatTimeRemaining(start, end) {
    const [startHour, startMinute] = start.split(':').map(Number);
    const [endHour, endMinute] = end.split(':').map(Number);
    const now = new Date();
    const currentHour = now.getHours();
    const currentMinute = now.getMinutes();

    const endTimeMinutes = endHour * 60 + endMinute;
    const currentTimeMinutes = currentHour * 60 + currentMinute;

    let remainingMinutes = endTimeMinutes - currentTimeMinutes;
    if (remainingMinutes <= 0) return ' 완료!';

    const hours = Math.floor(remainingMinutes / 60);
    const minutes = remainingMinutes % 60;

    return ` ${hours}시간 ${minutes}분 남았어요`;
}

// Modify the updateAllProgress function to handle the complete day text
function updateAllProgress() {
    const circles = document.querySelectorAll('.progress-circle');
    const timeValues = document.querySelectorAll('.time-value');
    const encouragementText = document.querySelector('.mt-3.text-center.text-sm');

    // 퇴근까지 남은 시간 (큰 원)
    const workProgress = calculateTimeProgress(globalStartTime, globalEndTime);
    updateCircleProgress(circles[0], workProgress);

    // 점심까지 남은 시간 (작은 원)
    const lunchProgress = calculateTimeProgress(globalStartTime, globalLunchTime);
    updateCircleProgress(circles[1], lunchProgress);

    // time-value 업데이트
    const lunchTimeText = formatTimeRemaining(globalStartTime, globalLunchTime);
    const workTimeText = formatTimeRemaining(globalStartTime, globalEndTime);

    timeValues[0].textContent = lunchTimeText;  // 점심시간 텍스트
    timeValues[1].textContent = workTimeText;   // 퇴근시간 텍스트

    // Check if both lunch and work times are completed
    if (lunchTimeText === ' 완료!' && workTimeText === ' 완료!') {
        encouragementText.textContent = '오늘 하루도 수고하셨습니다!';
    } else {
        encouragementText.textContent = '남은 시간도 힘내세요! 💪';
    }
}

// 초기화 및 이벤트 리스너
document.addEventListener('DOMContentLoaded', () => {
    // 캘린더 초기화
    initializeCalendar(monthlyStressList);

    // 캘린더 외부 클릭 시 툴팁 닫기
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.calendar-grid')) {
            const tooltip = document.getElementById('stress-tooltip');
            if (tooltip) tooltip.remove();
        }
    });

    // 애니메이션 스타일 추가
    const style = document.createElement('style');
    style.textContent = `
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        .animate-fade-in {
            animation: fadeIn 0.3s ease;
        }
    `;
    document.head.appendChild(style);

    // 월급일 D-day 계산 및 표시
    const paydayElement = document.getElementById('payday');
    if (paydayElement) {
        const today = new Date();
        const currentYear = today.getFullYear();
        const currentMonth = today.getMonth();
        let payDate = new Date(currentYear, currentMonth, payday);

        if (payDate <= today) {
            payDate = new Date(currentYear, currentMonth + 1, payday);
        }

        const timeDiff = payDate.getTime() - today.getTime();
        const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24));
        paydayElement.innerHTML = `D-${daysDiff}`;
    }

    // 주간 평균 스트레스 계산 및 표시
    const weeklyAverageStress = calculateWeeklyAverageStress(monthlyStressList);
    const stressElement = document.getElementById('weeklyStressNum');
    const overviewStressNoData = document.querySelector('.stress-no-data');  // .overview-card .stress-no-data 대신

    if (stressElement && overviewStressNoData) {
        if (weeklyAverageStress === null) {
            overviewStressNoData.style.display = 'block';
            stressElement.style.display = 'none';
        } else {
            overviewStressNoData.style.display = 'none';
            stressElement.style.display = 'block';
            const sign = weeklyAverageStress > 0 ? '+' : '';
            stressElement.innerHTML = `${sign}${weeklyAverageStress}%`;
        }
    }

    // 모달 관련 코드
    const modal = document.getElementById('timeSettingsModal');
    const settingsBtn = document.querySelector('.time-settings-btn');
    const closeBtn = document.querySelector('.close-modal-btn');
    const saveBtn = document.querySelector('.save-time-btn');

    if (settingsBtn) {
        settingsBtn.addEventListener('click', () => {
            if (modal) {
                // 현재 설정된 시간 값들을 input에 설정
                document.getElementById('workStartTime').value = globalStartTime;
                document.getElementById('lunchTime').value = globalLunchTime;
                document.getElementById('workEndTime').value = globalEndTime;
                modal.style.display = 'flex';
            }
        });
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            if (modal) modal.style.display = 'none';
        });
    }

    if (modal) {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) modal.style.display = 'none';
        });
    }

    if (saveBtn) {
        saveBtn.addEventListener('click', () => {
            const workStartInput = document.getElementById('workStartTime');
            const lunchInput = document.getElementById('lunchTime');
            const workEndInput = document.getElementById('workEndTime');

            if (!workStartInput || !lunchInput || !workEndInput) {
                console.error('Some time inputs are missing');
                return;
            }

            const newStartTime = workStartInput.value;
            const newLunchTime = lunchInput.value;
            const newEndTime = workEndInput.value;

            if (!newStartTime || !newLunchTime || !newEndTime) {
                alert('모든 시간을 입력해주세요.');
                return;
            }

            // 전역 변수 업데이트
            globalStartTime = newStartTime;
            globalLunchTime = newLunchTime;
            globalEndTime = newEndTime;

            // 즉시 화면 업데이트
            updateAllProgress();

            // 모달 닫기
            modal.style.display = 'none';
        });
    }

    // 진행 상태 업데이트 시작
    updateAllProgress();
    setInterval(updateAllProgress, 60000); // 1분마다 업데이트
});
