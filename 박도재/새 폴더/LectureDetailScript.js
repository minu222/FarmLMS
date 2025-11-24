// 1. HTML에서 사용자 ID 가져오기
const userIdElement = document.getElementById('currentUserId');
const CURRENT_USER_ID = userIdElement ? parseInt(userIdElement.value) : -1;
const isLoggedIn = CURRENT_USER_ID > 0;

// 2. HTML에서 강사 여부 가져오기
const isInstructorElement = document.getElementById('isInstructor');
const isInstructor = isInstructorElement && isInstructorElement.value === 'true';

// 강의 ID
const lectureIdElement = document.getElementById('lectureId');
const LECTURE_ID = lectureIdElement ? parseInt(lectureIdElement.value) : -1;

// QnA 페이징 설정
const ITEMS_PER_PAGE = 2; // 서버 페이징 사이즈와 일치해야 합니다.
let currentQnaPage = 0;
let totalQnaPages = 0;
let qnaData = [];

// Video.js 인스턴스 변수 선언 및 현재 비디오 데이터 저장
let videoJsPlayer = null;
let currentVideoData = null;

// Video Playback 변수
let maxWatchedTime = 0; // **[핵심 변수]** 사용자가 시청한 최대 진도 시간 (초 단위)
let lastSavedTime = 0; // 마지막으로 서버에 저장된 시간
let progressInterval = null; // 진도율 저장 인터벌 ID

// -----------------------------------------------------------
// --- 유틸리티 함수 ---
// -----------------------------------------------------------

/**
 * 시간을 'MM:SS' 형식으로 포맷합니다.
 */
function formatDuration(totalSeconds) {
    if (typeof totalSeconds !== 'number' || totalSeconds < 0) {
        return '00:00';
    }
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    const formattedMinutes = String(minutes).padStart(2, '0');
    const formattedSeconds = String(seconds).padStart(2, '0');
    return `${formattedMinutes}:${formattedSeconds}`;
}

/**
 * 날짜 문자열을 포맷합니다.
 */
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toISOString().slice(0, 16).replace('T', ' ');
}

// -----------------------------------------------------------
// --- DOMContentLoaded & 초기화 ---
// -----------------------------------------------------------

document.addEventListener('DOMContentLoaded', function() {
    // 1. Video.js 인스턴스 초기화 (HTML의 data-setup으로 이미 초기화되므로 인스턴스만 가져옵니다.)
    const videoElement = document.getElementById('videoPlayer');
    if (videoElement) {
        // 이미 HTML에서 초기화되어 있으므로 인스턴스를 가져옵니다.
        videoJsPlayer = videojs(videoElement.id);
    }

    // 2. 초기 비디오 로드
    const initialActiveVideo = document.querySelector('.chapter-list .chapter-item.active');
    if (initialActiveVideo && videoJsPlayer) {
        loadVideo(initialActiveVideo);
    }

    // 3. 로그인 상태에 따라 진도율 섹션 표시
    if (isLoggedIn) {
        const progressSection = document.getElementById('currentLessonProgress');
        if (progressSection) {
            progressSection.style.display = 'block';
        }
    }

    // 4. 헤더 스크롤 효과 (기존 로직 유지)
    window.addEventListener('scroll', () => {
        const header = document.querySelector('header');
        if (window.scrollY > 50) {
            header && header.classList.add('scrolled'); // header는 fragments에서 가져오므로 확인 필요
        } else {
            header && header.classList.remove('scrolled');
        }
    });
});


// -----------------------------------------------------------
// --- Video.js 로직 (핵심) ---
// -----------------------------------------------------------

/**
 * 비디오 플레이어의 이벤트 및 인터벌을 정리합니다.
 */
function cleanupVideoEvents() {
    if (!videoJsPlayer) return;

    // 1. 인터벌 제거
    if (progressInterval) {
        clearInterval(progressInterval);
        progressInterval = null;
    }

    // 2. Video.js 이벤트 리스너 제거
    // 새로운 핸들러 등록을 위해 기존 핸들러 제거
    videoJsPlayer.off('loadedmetadata', videoJsPlayer.metadataHandler);
    videoJsPlayer.off('timeupdate', videoJsPlayer.timeUpdateHandler);
    videoJsPlayer.off('ended', videoJsPlayer.endHandler);

    // 3. window.onbeforeunload 제거
    window.onbeforeunload = null;

    // 4. 변수 초기화
    maxWatchedTime = 0;
    lastSavedTime = 0;
}

/**
 * [제거됨] 썸네일 캡처 로직이 제거되었습니다.
 * 대신 loadVideo 함수 내에서 직접 소스를 설정하고 로드합니다.
 */


/**
 * 비디오 이벤트 핸들러를 등록합니다.
 */
function initializeVideoEvents(videoData) {
    if (!videoJsPlayer) return;
    cleanupVideoEvents();

    const videoId = videoData.video_id;
    const totalSec = videoData.video_time;
    let userDurationSec = videoData.user_duration_sec || 0;

    // 최대 시청 시간 설정
    maxWatchedTime = userDurationSec;
    lastSavedTime = userDurationSec;

    // Video.js 'loadedmetadata' 이벤트: 메타데이터 로드 후 시작 시간 설정
    videoJsPlayer.metadataHandler = function() {
        if (maxWatchedTime > 0) {
            videoJsPlayer.currentTime(maxWatchedTime);
        }
    };

    // B. 시청 시간 업데이트 핸들러: maxWatchedTime 갱신만 수행하도록 단순화
    videoJsPlayer.timeUpdateHandler = function() {
        const currentTime = videoJsPlayer.currentTime();

        //  현재 시점이 최대 시청 시간보다 길면 maxWatchedTime 갱신
        if (currentTime > maxWatchedTime) {
            maxWatchedTime = currentTime;
        }
    };

    // D. 비디오 종료 시 최종 진도율 저장 및 정리 핸들러
    videoJsPlayer.endHandler = function() {
        const finalDuration = totalSec;
        // totalSec 전달
        updateVideoProgress(videoId, finalDuration, totalSec);
        cleanupVideoEvents();
    };

    // 이벤트 리스너 등록
    videoJsPlayer.on('loadedmetadata', videoJsPlayer.metadataHandler);
    videoJsPlayer.on('timeupdate', videoJsPlayer.timeUpdateHandler);
    videoJsPlayer.on('ended', videoJsPlayer.endHandler);


    // 1초마다 진도율 저장 인터벌
    progressInterval = setInterval(() => {
        if (!videoJsPlayer.paused() && videoJsPlayer.currentTime() > 0) {
            // maxWatchedTime (최대 도달 시간)을 저장 기준으로 사용합니다.
            const currentMaxTime = Math.min(Math.floor(maxWatchedTime), totalSec);

            // 5초 이상 진행했거나, 끝까지 도달했다면 저장
            if (currentMaxTime >= lastSavedTime + 3 || (currentMaxTime >= totalSec && totalSec > 0)) {
                // 서버에 저장할 때는 maxWatchedTime과 totalSec을 보냅니다.
                updateVideoProgress(videoId, currentMaxTime, totalSec);
                lastSavedTime = currentMaxTime;
            }
        }
    }, 5000); // 5초마다 저장 (서버 부하 감소)

    // 창 닫기/이동 시 진도율 최종 저장
    window.onbeforeunload = function() {
        if (videoJsPlayer && !videoJsPlayer.paused() && videoJsPlayer.currentTime() > 0) {
            const finalDuration = Math.min(Math.floor(videoJsPlayer.currentTime()), totalSec);
            if (finalDuration > lastSavedTime || finalDuration === totalSec) {
                // 비동기 요청이 완료되지 않고 페이지가 닫힐 수 있으므로, 최대한 빠르게 시도합니다.
                // totalSec 전달
                updateVideoProgress(videoId, finalDuration, totalSec);
            }
        }
    };
}


/**
 * 서버에 비디오 진도율을 저장합니다.
 */
function updateVideoProgress(videoId, durationSec, totalSec) {
    if (!isLoggedIn) {
        return;
    }

    const progressPercentage = (totalSec > 0)
        ? Math.min(100, Math.round(((durationSec / totalSec) * 100)))
        : 0;

    fetch(`/api/video/${videoId}/progress`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            duration_sec: durationSec, // 시청 시간
            total_sec: totalSec       // 비디오 전체 길이 (추가)
        }),
    })
        .then(response => {
            if (!response.ok) {
                console.error('진도율 저장 실패');
            } else {
                // UI 업데이트
                const progressPercentSpan = document.querySelector('#currentLessonProgress .current-progress-percent');
                const progressFill = document.querySelector('#currentLessonProgress .current-progress-fill');

                if (progressPercentSpan) {
                    progressPercentSpan.textContent = `${progressPercentage}%`;
                }
                if (progressFill) {
                    progressFill.style.width = `${progressPercentage}%`;
                }
                if (currentVideoData && currentVideoData.video_id === videoId) {
                    currentVideoData.user_duration_sec = durationSec;
                }
                // saveVideoHistory(videoId, progressPercentage);
            }
        })
        .catch(error => console.error('Error saving progress:', error));
}

/**
 * 비디오 시청 히스토리를 저장합니다.
 */
function saveVideoHistory(videoId, progressRate) {
    if (!isLoggedIn) {
        return;
    }
    fetch(`/api/video-history`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            videoId: videoId,
            progressRate: progressRate
        }),
    })
        .then(response => {
            if (!response.ok) {
                console.error('비디오 히스토리 저장 실패');
            }
        })
        .catch(error => console.error('Error saving video history:', error));
}

/**
 * 목차에서 비디오를 클릭했을 때 호출됩니다.
 * [수정됨]: 썸네일 함수 호출이 제거되고 소스 설정으로 대체되었습니다.
 */
function loadVideo(element) {
    const videoId = element.getAttribute('data-video-id');
    if (!videoId) return;

    // 1. UI 업데이트: Active 클래스 전환
    document.querySelectorAll('.chapter-item').forEach(i => i.classList.remove('active'));
    element.classList.add('active');

    // 2. AJAX 요청
    fetch(`/api/video/${videoId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('비디오 데이터를 불러오는 데 실패했습니다: ' + response.statusText);
            }
            return response.json();
        })
        .then(videoData => {
            if (!videoData || !videoJsPlayer) {
                alert('해당 비디오 정보를 찾을 수 없습니다.');
                return;
            }

            currentVideoData = videoData;
            const totalSec = videoData.video_time;
            const userDurationSec = videoData.user_duration_sec || 0;
            const progressPercentage = Math.min(100, Math.round(((userDurationSec / totalSec) * 100))) || 0;

            cleanupVideoEvents();

            // -------------------------------------------------------------
            //  비디오 URL 존재 여부 확인 로직
            // -------------------------------------------------------------
            if (!videoData.video_url || videoData.video_url.trim() === '') {
                // URL이 없는 경우 처리
                console.error(`비디오 ID ${videoId}: URL이 존재하지 않아 로드할 수 없습니다.`);
                alert(`⚠️ 해당 강의(${videoData.video_title})의 비디오 URL이 아직 등록되지 않았습니다.`);

                // 비디오 플레이어를 초기화(빈 상태)로 설정합니다.
                videoJsPlayer.reset();

                // 플레이스홀더를 다시 표시 (HTML에 해당 요소가 있다면)
                const placeholder = document.getElementById('initialPlaceholder');
                if (placeholder) {
                    placeholder.style.display = 'flex';
                }

                // UI 업데이트는 하되, 진도율은 0%로 초기화합니다.
                updateVideoUI(videoData, 0, 0);
                return; // 로드 프로세스 중단
            }
            // -------------------------------------------------------------


            // 1. Video.js 소스 설정 및 로드
            videoJsPlayer.src({ src: videoData.video_url, type: 'video/mp4' });
            videoJsPlayer.load();
            videoJsPlayer.poster('');

            // 2. 이벤트 초기화
            initializeVideoEvents(videoData);

            // 3. UI 텍스트 업데이트 함수 호출로 통합
            updateVideoUI(videoData, userDurationSec, totalSec);

            // 4. 초기 플레이스홀더 숨기기 (URL이 있을 경우)
            const placeholder = document.getElementById('initialPlaceholder');
            if (placeholder) {
                placeholder.style.display = 'none';
            }
        })
        .catch(error => {
            console.error('AJAX 오류 발생:', error);
            alert('비디오 로드 중 오류가 발생했습니다. 콘솔을 확인해주세요.');
        });
}

function updateVideoUI(videoData, userDurationSec, totalSec) {
    const progressPercentage = Math.min(100, Math.round(((userDurationSec / totalSec) * 100))) || 0;

    const mainTitleElement = document.getElementById('videoTitle');
    const mainDurationElement = document.getElementById('videoDuration');
    const progressTitle = document.querySelector('.current-lesson-title');
    const progressPercentSpan = document.querySelector('#currentLessonProgress .current-progress-percent');
    const progressFill = document.querySelector('#currentLessonProgress .current-progress-fill');

    if (mainTitleElement) {
        mainTitleElement.textContent = videoData.video_title;
    }
    if (mainDurationElement) {
        // 비디오 시간이 0이면 '00:00'을 표시하도록 수정
        mainDurationElement.textContent = formatDuration(videoData.video_time || 0);
    }
    if (progressTitle) {
        progressTitle.textContent = videoData.video_title;
    }
    if (progressPercentSpan) {
        progressPercentSpan.textContent = `${progressPercentage}%`;
    }
    if (progressFill) {
        progressFill.style.width = `${progressPercentage}%`;
    }
}


// -------------------------------------------------------------------------
// --- 탭 전환
// -------------------------------------------------------------------------
const tabButtons = document.querySelectorAll('.tab-btn');
const tabPanels = document.querySelectorAll('.tab-panel');

tabButtons.forEach(button => {
    button.addEventListener('click', function() {
        const targetTab = this.dataset.tab;

        // 1. UI 업데이트
        tabButtons.forEach(btn => btn.classList.remove('active'));
        this.classList.add('active');
        tabPanels.forEach(panel => panel.classList.remove('active'));
        document.getElementById(targetTab + '-panel').classList.add('active');

        // 2. QnA 탭 클릭 시 데이터 로드
        if (targetTab === 'qna') {
            fetchQna(0);
        }
    });
});


// -------------------------------------------------------------------------
// --- QnA
// -------------------------------------------------------------------------

/**
 * QnA 데이터를 서버에서 불러옵니다.
 * (중략... 원본 코드와 동일)
 */
function fetchQna(page) {
    if (LECTURE_ID === -1) return;

    fetch(`/api/qna/list?lectureId=${LECTURE_ID}&page=${page}`)
        .then(response => {
            if (!response.ok) throw new Error('QnA 데이터를 불러오는 데 실패했습니다.');
            return response.json();
        })
        .then(pageData => {
            qnaData = pageData.content;
            currentQnaPage = pageData.number;
            totalQnaPages = pageData.totalPages;

            renderQna();
            renderQnaPagination();
        })
        .catch(error => {
            console.error('QnA 로드 실패:', error);
            document.getElementById('qnaList').innerHTML = '<li style="text-align: center; color: gray;">QnA 데이터를 불러오지 못했습니다. (서버 연결 확인)</li>';
        });
}

/**
 * QnA 목록을 렌더링합니다. (수정/삭제 버튼 포함)
 * (중략... 원본 코드와 동일)
 */
function renderQna() {
    const qnaList = document.getElementById('qnaList');

    // 디버깅용 로그 (F12 콘솔에서 확인 가능)
    console.log("=== QnA 렌더링 상태 확인 ===");
    console.log("현재 로그인한 유저 ID:", CURRENT_USER_ID);
    console.log("강사 권한 여부(isInstructor):", isInstructor);

    if (qnaData.length === 0) {
        qnaList.innerHTML = '<li style="text-align: center; color: gray;">등록된 질문이 없습니다.</li>';
        return;
    }

    qnaList.innerHTML = qnaData.map(item => {
        // 답변 개수 확인
        const replyCount = item.replies ? item.replies.length : 0;

        // 버튼 노출 조건: 강사여야 함 AND 답변이 없어야 함
        const showReplyButton = isInstructor && (replyCount === 0);

        // 로그로 각 질문별 버튼 노출 여부 확인
        console.log(`질문ID ${item.qnaId}: 답변수=${replyCount}, 버튼보임=${showReplyButton}`);

        return `
            <li class="qna-item">
                <div class="qna-question-header">
                    <div class="qna-question-info">
                        <div class="qna-question-author">👤 ${item.authorNickname}</div>
                        <div class="qna-question">${item.content.replace(/\n/g, '<br>')}</div>
                        <div class="qna-date" style="font-size: 0.8em; color: #888;">${formatDate(item.createdAt)}</div>
                    </div>
                    
                    ${item.isCurrentUserAuthor ? `
                    <div class="qna-question-actions">
                        <button class="qna-action-btn" onclick="editQna(${item.qnaId}, '${item.content.replace(/'/g, "\\'").replace(/\n/g, "\\n")}')">수정</button>
                        <button class="qna-action-btn delete" onclick="deleteQna(${item.qnaId})">삭제</button>
                    </div>
                    ` : ''}
                </div>

                ${/* 답변 리스트 렌더링 (수정/삭제 버튼 추가) */
            item.replies && item.replies.length > 0 ? `
                <div class="qna-replies">
                    ${item.replies.map(reply => {
                // 답변 수정/삭제 버튼 노출 조건: 답변 작성자 본인이면서 강사일 때만
                const showReplyActions = reply.isCurrentUserAuthor && isInstructor;

                return `
                        <div class="qna-reply-item">
                            <div class="qna-reply-header">
                                <span class="qna-reply-author">👨‍🏫 ${reply.authorNickname}</span>
                                <span class="qna-date" style="font-size: 0.8em; color: #888;">${formatDate(reply.createdAt)}</span>
                            </div>
                            <div class="qna-reply-content-wrapper">
                                <div class="qna-reply-content">${reply.content.replace(/\n/g, '<br>')}</div>
                                
                                ${showReplyActions ? `
                                <div class="qna-reply-actions">
                                    <button class="qna-action-btn" 
                                            onclick="editQna(${reply.qnaId}, '${reply.content.replace(/'/g, "\\'").replace(/\n/g, "\\n")}')">수정</button>
                                    <button class="qna-action-btn delete" 
                                            onclick="deleteQna(${reply.qnaId})">삭제</button>
                                </div>
                                ` : ''}
                            </div>
                        </div>
                        `;
            }).join('')}
                </div>
                ` : ''}

                ${/* 강사이고 + 답변이 없을 때만 버튼 노출 */
            showReplyButton ? `
                <button class="qna-action-btn" style="margin-top: 1rem;" onclick="toggleReplyInput(${item.qnaId})">답글 달기</button>
                <div class="qna-reply-input-area" id="reply-input-${item.qnaId}">
                    <textarea class="qna-reply-input" placeholder="답글을 작성하세요..." id="reply-content-${item.qnaId}"></textarea>
                    <button class="qna-reply-btn" onclick="submitReply(${item.qnaId})">답글 등록</button>
                </div>
                ` : ''}
            </li>
        `;
    }).join('');
}


function renderQnaPagination() {
    const pagination = document.getElementById('qnaPagination');
    const totalPages = totalQnaPages;

    if (totalPages <= 1) {
        pagination.innerHTML = '';
        return;
    }

    let paginationHTML = '';

    // 이전 버튼
    paginationHTML += `
                <button class="qna-page-btn ${currentQnaPage === 0 ? 'disabled' : ''}"
                        onclick="goToQnaPage(${currentQnaPage - 1})"
                        ${currentQnaPage === 0 ? 'disabled' : ''}>
                    ‹
                </button>
            `;

    // 페이지 번호 (최대 5개 표시)
    let startPage = Math.max(0, currentQnaPage - 2);
    let endPage = Math.min(totalPages - 1, startPage + 4);

    if (endPage - startPage < 4) {
        startPage = Math.max(0, endPage - 4);
    }

    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
                    <button class="qna-page-btn ${i === currentQnaPage ? 'active' : ''}"
                            onclick="goToQnaPage(${i})">
                        ${i + 1}
                    </button>
                `;
    }

    // 다음 버튼
    paginationHTML += `
                <button class="qna-page-btn ${currentQnaPage === totalPages - 1 ? 'disabled' : ''}"
                        onclick="goToQnaPage(${currentQnaPage + 1})"
                        ${currentQnaPage === totalPages - 1 ? 'disabled' : ''}>
                    ›
                </button>
            `;

    pagination.innerHTML = paginationHTML;
}

function goToQnaPage(page) {
    if (page < 0 || page >= totalQnaPages) return;
    fetchQna(page);
}

function toggleReplyInput(questionId) {
    const replyInput = document.getElementById(`reply-input-${questionId}`);
    replyInput.classList.toggle('active');
}

/**
 * 질문 등록 API 호출
 */
function submitQuestion() {
    if (!isLoggedIn) {
        alert("로그인 후 이용해 주세요.");
        return;
    }
    const questionContent = document.getElementById('questionInput').value;
    if (!questionContent.trim()) {
        alert('질문 내용을 입력해주세요.');
        return;
    }

    fetch(`/api/qna/question`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            lectureId: LECTURE_ID,
            content: questionContent.trim(),
            userId: CURRENT_USER_ID
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('질문 등록 실패');
            }
            return;
        })
        .then(() => {
            alert('질문이 등록되었습니다.');
            document.getElementById('questionInput').value = '';
            fetchQna(0);
        })
        .catch(error => {
            console.error('질문 등록 오류:', error);
            alert('질문 등록 중 오류가 발생했습니다. (서버 로그 확인)');
        });
}

/**
 * 답글 등록 API 호출
 */
function submitReply(pQnaId) {
    const lectureId = document.getElementById('lectureId').value;
    const content = document.getElementById(`reply-content-${pQnaId}`).value;

    if (!content.trim()) {
        alert("내용을 입력해주세요.");
        return;
    }

    if (!pQnaId || pQnaId <= 0) {
        console.error("오류: 유효하지 않은 부모 질문 ID입니다.", pQnaId);
        alert("답변을 등록할 질문 ID가 유효하지 않습니다.");
        return;
    }

    const data = {
        lectureId: parseInt(lectureId),
        pQnaId: pQnaId,
        content: content
    };

    fetch('/api/qna/reply', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                alert('답변이 성공적으로 등록되었습니다.');
                fetchQna(0);
            } else {
                return response.json().then(err => {
                    alert(`답변 등록 중 오류가 발생했습니다: ${err.message || '서버 오류'}`);
                }).catch(() => {
                    alert('답변 등록 중 오류가 발생했습니다. (서버 로그 확인)');
                });
            }
        })
        .catch(error => {
            console.error('Fetch error:', error);
            alert('네트워크 오류가 발생했습니다.');
        });
}

/**
 * 질문 또는 답변 (QnA) 수정 API 호출
 */
function editQna(qnaId, currentContent) {
    if (!isLoggedIn) {
        alert("로그인 후 이용해 주세요.");
        return;
    }

    const newContent = prompt("수정할 내용을 입력해주세요:", currentContent);

    if (newContent === null || newContent.trim() === "") return;

    fetch(`/api/qna/${qnaId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content: newContent })
    })
        .then(response => {
            if (response.status === 403) {
                throw new Error('수정 권한이 없습니다.');
            }
            if (!response.ok) {
                throw new Error('수정 실패');
            }
            return;
        })
        .then(() => {
            alert("수정되었습니다.");
            fetchQna(currentQnaPage);
        })
        .catch(error => {
            console.error("수정 오류:", error);
            alert(`수정 중 오류가 발생했습니다: ${error.message}`);
        });
}

/**
 * 질문 또는 답변 (QnA) 삭제 API 호출
 */
function deleteQna(qnaId) {
    if (!isLoggedIn) {
        alert("로그인 후 이용해 주세요.");
        return;
    }

    if (confirm('정말 삭제하시겠습니까?')) {
        fetch(`/api/qna/${qnaId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.status === 403) {
                    throw new Error('삭제 권한이 없습니다.');
                }
                if (!response.ok) {
                    throw new Error('삭제 실패');
                }
                alert("삭제되었습니다.");
                fetchQna(currentQnaPage);
            })
            .catch(error => {
                console.error('삭제 오류:', error);
                alert(`삭제 중 오류가 발생했습니다: ${error.message}`);
            });
    }
}