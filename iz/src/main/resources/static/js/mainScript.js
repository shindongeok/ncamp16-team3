// 캘린더 관련 함수들
function updateCalendar(feelingList = []) {
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

    for (let i = 0; i < 7; i++) {
        const currentDate = new Date(monday);
        currentDate.setDate(monday.getDate() + i);

        const dateSpan = document.createElement('span');
        dateSpan.className = 'calendar-date';
        dateSpan.textContent = currentDate.getDate();

        if (currentDate.toDateString() === today.toDateString()) {
            dateSpan.classList.add('active');
        }

        const formattedDate = currentDate.toISOString().split('T')[0];
        const feelingData = feelingList.find(item => {
            const itemDate = new Date(item.date).toISOString().split('T')[0];
            return itemDate === formattedDate;
        });

        if (feelingData) {
            dateSpan.classList.remove('verybad', 'bad', 'good', 'verygood');
            const feelingNum = feelingData.feeling_num;
            if (feelingNum <= -6) dateSpan.classList.add('verybad');
            else if (feelingNum >= -5 && feelingNum <= -2) dateSpan.classList.add('bad');
            else if (feelingNum >= 2 && feelingNum <= 5) dateSpan.classList.add('good');
            else if (feelingNum >= 6) dateSpan.classList.add('verygood');
        }

        dateGrid.appendChild(dateSpan);
    }
}

function initializeCalendar(feelingList = []) {
    updateCalendar(feelingList);

    function scheduleNextUpdate() {
        const now = new Date();
        const tomorrow = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 1);
        const timeUntilMidnight = tomorrow - now;

        setTimeout(() => {
            updateCalendar(feelingList);
            scheduleNextUpdate();
        }, timeUntilMidnight);
    }

    scheduleNextUpdate();
}

// 시간 계산 관련 함수들
function calculateTimeRemaining(startHour, endHour) {
    const now = new Date();
    const currentHour = now.getHours();
    const currentMinute = now.getMinutes();

    const endTime = endHour * 60;
    const currentTime = currentHour * 60 + currentMinute;

    let remainingMinutes = endTime - currentTime;
    if (remainingMinutes < 0) remainingMinutes = 0;

    const hours = Math.floor(remainingMinutes / 60);
    const minutes = remainingMinutes % 60;

    return `${hours}h ${minutes}min`;
}

function calculateProgress(startHour, endHour) {
    const now = new Date();
    const currentHour = now.getHours();
    const currentMinute = now.getMinutes();
    const currentTime = currentHour + currentMinute / 60;

    if (currentTime < startHour) return 0;
    if (currentTime > endHour) return 100;

    const duration = endHour - startHour;
    const elapsed = currentTime - startHour;
    return Math.min(100, Math.max(0, (elapsed / duration) * 100));
}

function updateCircleProgress(circle, progress) {
    const circumference = parseFloat(circle.getAttribute('stroke-dasharray'));
    const offset = circumference - (progress / 100) * circumference;
    circle.setAttribute('stroke-dashoffset', offset);
    circle.setAttribute('data-progress', progress);

    const dot = circle.nextElementSibling;
    const angle = (progress / 100) * 360 - 90;
    const radius = parseFloat(circle.getAttribute('r'));
    const cx = 100;
    const cy = 100;
    const x = cx + radius * Math.cos(angle * Math.PI / 180);
    const y = cy + radius * Math.sin(angle * Math.PI / 180);
    dot.setAttribute('cx', x);
    dot.setAttribute('cy', y);
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

function updateAllProgress() {
    const [startHour, startMinute] = startTime.split(':').map(Number);
    const [lunchHour, lunchMinute] = lunchTime.split(':').map(Number);
    const [endHour, endMinute] = endTime.split(':').map(Number);

    const circles = document.querySelectorAll('.progress-circle');
    const timeTexts = document.querySelectorAll('.time-text');

    timeTexts[0].textContent = calculateTimeRemaining(startHour, endHour);
    timeTexts[2].textContent = calculateTimeRemaining(startHour, lunchHour);

    circles.forEach((circle, index) => {
        let progress;
        if (index === 0) {
            progress = Math.round(calculateProgress(startHour, endHour));
        } else if (index === 1) {
            progress = stressNum;
        } else if (index === 2) {
            progress = Math.round(calculateProgress(startHour, lunchHour));
        }

        updateCircleProgress(circle, progress);
    });

    const stressText = document.querySelectorAll('.time-text')[1];
    stressText.textContent = `${stressNum > 0 ? '+' : ''}${stressNum}%`;
}

// 초기화 및 이벤트 리스너
document.addEventListener('DOMContentLoaded', () => {
    // 캘린더 초기화
    initializeCalendar(feelingList);

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
    const noDataMessage = stressElement.parentElement.querySelector('.no-data-message');

    if (stressElement) {
        if (weeklyAverageStress === null) {
            noDataMessage.style.display = 'block';
            stressElement.innerHTML = '0%';
        } else {
            noDataMessage.style.display = 'none';
            const sign = weeklyAverageStress > 0 ? '+' : '';
            stressElement.innerHTML = `${sign}${weeklyAverageStress}%`;
        }
    }

    // 진행 상태 업데이트 시작
    updateAllProgress();
    setInterval(updateAllProgress, 60000);
});

// 모달 관련 코드
const modal = document.getElementById('timeSettingsModal');
const settingsBtn = document.querySelector('.time-settings-btn');
const closeBtn = document.querySelector('.close-modal-btn');
const saveBtn = document.querySelector('.save-time-btn');
const timeInputs = {
    workStart: document.getElementById('workStartTime'),
    lunch: document.getElementById('lunchTime'),
    workEnd: document.getElementById('workEndTime')
};

settingsBtn?.addEventListener('click', () => modal.style.display = 'flex');
closeBtn?.addEventListener('click', () => modal.style.display = 'none');
modal?.addEventListener('click', (e) => {
    if (e.target === modal) modal.style.display = 'none';
});

saveBtn?.addEventListener('click', () => {
    const workStartTime = timeInputs.workStart.value;
    const lunchTime = timeInputs.lunch.value;
    const workEndTime = timeInputs.workEnd.value;

    if (!workStartTime || !lunchTime || !workEndTime) {
        alert('모든 시간을 입력해주세요.');
        return;
    }

    const circles = document.querySelectorAll('.progress-circle');
    circles.forEach((circle, index) => {
        circle.setAttribute('data-start', workStartTime.split(':')[0]);
        circle.setAttribute('data-end',
            index === 2 ? lunchTime.split(':')[0] : workEndTime.split(':')[0]);
    });

    updateAllProgress();
    modal.style.display = 'none';
});