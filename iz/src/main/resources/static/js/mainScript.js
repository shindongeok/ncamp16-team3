function updateCalendar(feelingList = []) {
    // 오늘 날짜 가져오기
    const today = new Date();

    // 이번 주의 월요일 찾기
    const monday = new Date(today);
    const diff = today.getDay() === 0 ? 6 : today.getDay() - 1; // 일요일 처리
    monday.setDate(today.getDate() - diff);

    // 월/연도 표시 업데이트
    const monthNames = ["1월", "2월", "3월", "4월", "5월", "6월",
        "7월", "8월", "9월", "10월", "11월", "12월"];
    document.querySelector('.today-date').textContent = `${monday.getFullYear()}년 ${monthNames[monday.getMonth()]}`;

    // 날짜 그리드 업데이트
    const dateGrid = document.querySelector('.calendar-grid:last-child');
    dateGrid.innerHTML = ''; // 기존 날짜 초기화

    // 월요일부터 일요일까지 날짜 추가
    for (let i = 0; i < 7; i++) {
        const currentDate = new Date(monday);
        currentDate.setDate(monday.getDate() + i);

        const dateSpan = document.createElement('span');
        dateSpan.className = 'calendar-date';
        dateSpan.textContent = currentDate.getDate();

        // 오늘 날짜인 경우 active 클래스 추가
        if (currentDate.toDateString() === today.toDateString()) {
            dateSpan.classList.add('active');
        }

        // 감정 데이터에 따라 배경색 설정
        const formattedDate = currentDate.toISOString().split('T')[0];
        const feelingData = feelingList.find(item => {
            const itemDate = new Date(item.date).toISOString().split('T')[0];
            return itemDate === formattedDate;
        });

        if (feelingData) {
            // 기존 클래스들 제거
            dateSpan.classList.remove('verybad', 'bad', 'good', 'verygood');

            // 감정 번호에 따라 클래스 추가
            const feelingNum = feelingData.feeling_num;
            if (feelingNum <= -6) {
                dateSpan.classList.add('verybad');
            } else if (feelingNum >= -5 && feelingNum <= -2) {
                dateSpan.classList.add('bad');
            } else if (feelingNum >= -1 && feelingNum <= 1) {
                // 보통인 경우 기본 스타일 유지 (클래스 추가 없음)
                // 여기서는 다음으로 넘어감
            } else if (feelingNum >= 2 && feelingNum <= 5) {
                dateSpan.classList.add('good');
            } else if (feelingNum >= 6) {
                dateSpan.classList.add('verygood');
            }
        }

        dateGrid.appendChild(dateSpan);
    }
}

// 페이지 로드 시 캘린더 업데이트
function initializeCalendar(feelingList = []) {
    updateCalendar(feelingList);

    // 매일 자정에 캘린더 업데이트 (선택사항)
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

document.addEventListener('DOMContentLoaded', () => {
    // Thymeleaf의 feelingList 변수 사용
    initializeCalendar(feelingList);
});

function calculatePaydayDday(payday) {
    const today = new Date();
    const currentYear = today.getFullYear();
    const currentMonth = today.getMonth();

    // 이번 달 월급일
    let payDate = new Date(currentYear, currentMonth, payday);

    // 만약 월급일이 오늘보다 이전이라면 다음 달로 설정
    if (payDate <= today) {
        payDate = new Date(currentYear, currentMonth + 1, payday);
    }

    // D-day 계산
    const timeDiff = payDate.getTime() - today.getTime();
    const daysDiff = Math.ceil(timeDiff / (1000 * 3600 * 24));

    return `D-${daysDiff}`;
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', () => {
    const paydayElement = document.querySelector('.overview-content');
    if (paydayElement && typeof payday !== 'undefined') {
        paydayElement.innerHTML = calculatePaydayDday(payday);
    }
});

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

function updateAllProgress() {
    const circles = document.querySelectorAll('.progress-circle');
    const timeTexts = document.querySelectorAll('.time-text');

    timeTexts[0].textContent = calculateTimeRemaining(9, 18);
    timeTexts[2].textContent = calculateTimeRemaining(9, 12);

    circles.forEach((circle, index) => {
        const startHour = parseInt(circle.getAttribute('data-start'));
        const endHour = parseInt(circle.getAttribute('data-end'));
        const progress = Math.round(calculateProgress(startHour, endHour));

        updateCircleProgress(circle, progress);

        if (index === 1) {
            updateCircleProgress(circle, 71);
        }
    });
}

updateAllProgress();
setInterval(updateAllProgress, 60000);

// 모달 관련 요소들
const modal = document.getElementById('timeSettingsModal');
const settingsBtn = document.querySelector('.time-settings-btn');
const closeBtn = document.querySelector('.close-modal-btn');
const saveBtn = document.querySelector('.save-time-btn');
const timeInputs = {
    workStart: document.getElementById('workStartTime'),
    lunch: document.getElementById('lunchTime'),
    workEnd: document.getElementById('workEndTime')
};

// 모달 열기
settingsBtn.addEventListener('click', () => {
    modal.style.display = 'flex';
});

// 모달 닫기
closeBtn.addEventListener('click', () => {
    modal.style.display = 'none';
});

// 모달 외부 클릭 시 닫기
modal.addEventListener('click', (e) => {
    if (e.target === modal) {
        modal.style.display = 'none';
    }
});

// 시간 저장
saveBtn.addEventListener('click', () => {
    // 시간 값 가져오기
    const workStartTime = timeInputs.workStart.value;
    const lunchTime = timeInputs.lunch.value;
    const workEndTime = timeInputs.workEnd.value;

    // 시간 유효성 검사
    if (!workStartTime || !lunchTime || !workEndTime) {
        alert('모든 시간을 입력해주세요.');
        return;
    }

    // 원형 프로그레스바의 시작/종료 시간 업데이트
    const circles = document.querySelectorAll('.progress-circle');
    circles[0].setAttribute('data-start', workStartTime.split(':')[0]);
    circles[0].setAttribute('data-end', workEndTime.split(':')[0]);
    circles[1].setAttribute('data-start', workStartTime.split(':')[0]);
    circles[1].setAttribute('data-end', workEndTime.split(':')[0]);
    circles[2].setAttribute('data-start', workStartTime.split(':')[0]);
    circles[2].setAttribute('data-end', lunchTime.split(':')[0]);

    // 진행률 다시 계산
    updateAllProgress();

    // 모달 닫기
    modal.style.display = 'none';
});