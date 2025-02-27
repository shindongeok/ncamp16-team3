$(document).ready(function() {
    // 매핑 객체들
    const locationMap = {
        '117000': '전국', '101000': '서울', '102000': '경기', '118000': '세종',
        '103000': '광주', '104000': '대구', '105000': '대전', '106000': '부산',
        '107000': '울산', '108000': '인천', '109000': '강원', '110000': '경남',
        '111000': '경북', '112000': '전남', '113000': '전북', '114000': '충북',
        '115000': '충남', '116000': '제주'
    };

    const industryMap = {
        '1': '서비스업', '2': '제조·화학', '3': 'IT·웹·통신', '4': '은행·금융업',
        '5': '미디어·디자인', '6': '교육업', '7': '의료·제약·복지', '8': '판매·유통',
        '9': '건설업', '10': '기관·협회'
    };

    const educationMap = {
        '0': '학력무관', '1': '고등학교졸업', '2': '대학졸업(2,3년)', '3': '대학교졸업(4년)',
        '4': '석사졸업', '5': '박사졸업', '6': '고등학교졸업이상', '7': '대학졸업(2,3년)이상',
        '8': '대학교졸업(4년)이상', '9': '석사졸업이상'
    };

    // URL에서 파라미터 가져오기
    function getUrlParameter(name) {
        name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
        var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
        var results = regex.exec(location.search);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    }

    // URL 파라미터 값 가져오기
    const urlLocMcd = getUrlParameter('loc_mcd');
    const urlIndCd = getUrlParameter('ind_cd');
    const urlEduLv = getUrlParameter('edu_lv');

    // select 박스 초기화
    $('.relative select').each(function(index) {
        const $firstOption = $(this).find('option:first-child');
        const defaultValue = $firstOption.text();

        switch(index) {
            case 0: // 지역 선택
                // URL 파라미터가 있으면 해당 값 선택, 없으면 사용자 기본값 유지
                if (urlLocMcd) {
                    $firstOption.text(locationMap[urlLocMcd] || '지역 선택');
                    $firstOption.val(urlLocMcd);
                    $(this).val(urlLocMcd);
                } else {
                    $firstOption.text(locationMap[defaultValue] || '지역 선택');
                    $firstOption.val(defaultValue);
                }
                $(this).find(`option[value="${defaultValue}"]`).not(':first').remove();
                break;
            case 1: // 업종 선택
                if (urlIndCd) {
                    $firstOption.text(industryMap[urlIndCd] || '업종 선택');
                    $firstOption.val(urlIndCd);
                    $(this).val(urlIndCd);
                } else {
                    $firstOption.text(industryMap[defaultValue] || '업종 선택');
                    $firstOption.val(defaultValue);
                }
                $(this).find(`option[value="${defaultValue}"]`).not(':first').remove();
                break;
            case 2: // 학력 선택
                if (urlEduLv) {
                    $firstOption.text(educationMap[urlEduLv] || '학력 선택');
                    $firstOption.val(urlEduLv);
                    $(this).val(urlEduLv);
                } else {
                    $firstOption.text(educationMap[defaultValue] || '학력 선택');
                    $firstOption.val(defaultValue);
                }
                $(this).find(`option[value="${defaultValue}"]`).not(':first').remove();
                break;
        }
    });

    // 검색 버튼 클릭 이벤트
    $('.search-button').click(function() {
        const loc_mcd = $('.relative select').eq(0).val();
        const ind_cd = $('.relative select').eq(1).val();
        const edu_lv = $('.relative select').eq(2).val();
        const baseUrl = window.location.pathname;
        const searchUrl = `${baseUrl}?loc_mcd=${loc_mcd}&ind_cd=${ind_cd}&edu_lv=${edu_lv}`;
        window.location.href = searchUrl;
    });

    // 스크랩 버튼 클릭 이벤트
    $(document).on('click', '.scrap-btn', function(e) {
        e.preventDefault();
        const $btn = $(this);
        const jobId = $btn.data('jobId');  // data-job-id
        const companyName = $btn.data('jobCompanyName');  // data-company-name
        const expirationTimestamp = $btn.data('jobExpirationTimestamp');
        const url = $btn.data('jobUrl');

        $.ajax({
            url: '/job/scrap/' + jobId,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                id: jobId,
                companyName: companyName,
                expirationTimestamp: expirationTimestamp,
                url: url
            }),
            success: function(response) {
                if (response.success) {
                    if (response.isScraped) {
                        $btn.addClass('text-yellow-400').removeClass('text-gray-300');
                    } else {
                        $btn.addClass('text-gray-300').removeClass('text-yellow-400');
                    }
                } else {
                    alert('스크랩 처리 중 오류가 발생했습니다.');
                }
            },
            error: function() {
                alert('서버 통신 중 오류가 발생했습니다.');
            }
        });
    });

    // 더보기 관련 변수
    let deadlineOffset = 5;
    let recentOffset = 5;

    $('#loadMoreDeadline').click(function() {
        const $button = $(this).parent();
        const loc_mcd = $('.relative select').eq(0).val();
        const ind_cd = $('.relative select').eq(1).val();
        const edu_lv = $('.relative select').eq(2).val();

        $.get(`/job/hire?loc_mcd=${loc_mcd}&ind_cd=${ind_cd}&edu_lv=${edu_lv}&offset=${deadlineOffset}`)
            .done(function(response) {
                const $temp = $('<div>').html(response);
                const $newContent = $temp.find('#box-container .box');

                if ($newContent.length > 0) {
                    $newContent.each(function() {
                        const $box = $(this);
                        const jobId = $box.find('[data-job-id]').data('job-id');

                        // 스크랩 상태 확인
                        $.get(`/job/scrap/check/${jobId}`, function(response) {
                            const isScraped = response.isScraped;
                            const $scrapBtn = $box.find('.scrap-btn');

                            if (isScraped) {
                                $scrapBtn.addClass('text-yellow-400').removeClass('text-gray-300');
                            } else {
                                $scrapBtn.addClass('text-gray-300').removeClass('text-yellow-400');
                            }
                        });

                        // 타임스탬프 변환 적용
                        $box.find('.timestamp').each(function() {
                            const timestamp = parseFloat($(this).text());
                            if (!isNaN(timestamp)) {
                                $(this).text(formatTimestamp(timestamp));
                            }
                        });
                    });

                    $button.detach();
                    $('#box-container').append($newContent);
                    $('#box-container').append($button);

                    deadlineOffset += 5;

                    if ($newContent.length < 5) {
                        $button.hide();
                    }
                } else {
                    $button.hide();
                }
            });
    });

// 최근 공고 더보기 함수도 동일하게 수정
    $('#loadMoreRecent').click(function() {
        const $button = $(this).parent();
        const loc_mcd = $('.relative select').eq(0).val();
        const ind_cd = $('.relative select').eq(1).val();
        const edu_lv = $('.relative select').eq(2).val();

        $.get(`/job/hire?loc_mcd=${loc_mcd}&ind_cd=${ind_cd}&edu_lv=${edu_lv}&offset=${recentOffset}`)
            .done(function(response) {
                const $temp = $('<div>').html(response);
                const $newContent = $temp.find('#box-container2 .box');

                if ($newContent.length > 0) {
                    $newContent.each(function() {
                        const $box = $(this);
                        // relative 클래스가 없다면 추가
                        if (!$box.hasClass('relative')) {
                            $box.addClass('relative');
                        }
                        const jobId = $box.find('[data-job-id]').data('job-id');

                        // 스크랩 상태 확인
                        $.get(`/job/scrap/check/${jobId}`, function(response) {
                            const isScraped = response.isScraped;
                            const $scrapBtn = $box.find('.scrap-btn');

                            if (isScraped) {
                                $scrapBtn.addClass('text-yellow-400').removeClass('text-gray-300');
                            } else {
                                $scrapBtn.addClass('text-gray-300').removeClass('text-yellow-400');
                            }
                        });

                        // 타임스탬프 변환 적용
                        $box.find('.timestamp').each(function() {
                            const timestamp = parseFloat($(this).text());
                            if (!isNaN(timestamp)) {
                                $(this).text(formatTimestamp(timestamp));
                            }
                        });
                    });

                    $button.detach();
                    $('#box-container2').append($newContent);
                    $('#box-container2').append($button);

                    recentOffset += 5;

                    if ($newContent.length < 5) {
                        $button.hide();
                    }
                } else {
                    $button.hide();
                }
            });
    });

    // 드래그 스크롤 기능
    const containers = [document.getElementById('box-container'), document.getElementById('box-container2')];

    containers.forEach(container => {
        let isDragging = false;
        let startX = 0;
        let scrollLeft = 0;

        container.addEventListener('mousedown', (e) => {
            isDragging = true;
            startX = e.clientX;
            scrollLeft = container.scrollLeft;
            container.style.cursor = 'grabbing';
        });

        container.addEventListener('mousemove', (e) => {
            if (!isDragging) return;
            const moveX = e.clientX - startX;
            const maxMove = container.scrollWidth - container.clientWidth;
            let newPosition = scrollLeft - moveX;
            newPosition = Math.max(0, Math.min(newPosition, maxMove));
            container.scrollLeft = newPosition;
        });

        container.addEventListener('mouseup', () => {
            isDragging = false;
            container.style.cursor = 'grab';
        });

        container.addEventListener('mouseleave', () => {
            isDragging = false;
            container.style.cursor = 'grab';
        });
    });

    // 타임스탬프 포맷팅
    function formatTimestamp(timestamp) {
        const date = new Date(timestamp * 1000);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    // 타임스탬프 변환 적용
    $('.timestamp').each(function() {
        const timestamp = parseFloat($(this).text());
        if (!isNaN(timestamp)) {
            $(this).text(formatTimestamp(timestamp));
        }
    });
});
