const userIdElement = document.getElementById('currentUserId');
// HTML에서 사용자 ID를 읽어옵니다. (없으면 -1, 즉 비로그인으로 간주)
const CURRENT_USER_ID = userIdElement ? parseInt(userIdElement.value) : -1;

// currentUserId 값이 -1보다 크면 로그인 상태로 간주
const isLoggedIn = CURRENT_USER_ID > 0;
const isInstructor = false; // 이 값은 실제 서버에서 세션 또는 Thymeleaf를 통해 받아와야 합니다.
// -----------------------------------------------------------

// 강의 ID를 HTML에서 가져옵니다.
const lectureIdElement = document.getElementById('lectureId');
const LECTURE_ID = lectureIdElement ? parseInt(lectureIdElement.value) : -1;

// QnA 페이징 설정
const ITEMS_PER_PAGE = 2; // 서버 페이징 사이즈와 일치해야 합니다.
let currentQnaPage = 0; // 0부터 시작 (Spring Pageable)
let totalQnaPages = 0;
let qnaData = []; // 서버에서 받아온 QnA 데이터 저장


// 로그인 상태에 따라 진도율 섹션 표시 (display:none 초기값 유지)
if (isLoggedIn) {
    const progressSection = document.getElementById('currentLessonProgress');
    if (progressSection) {
        progressSection.style.display = 'block';
    }
}

// 헤더 스크롤 효과 (기존 로직 유지)
window.addEventListener('scroll', () => {
    const header = document.getElementById('header');
    if (window.scrollY > 50) {
        // ...
    } else {
        // ...
    }
});

// -----------------------------------------------------------
// 탭 전환 (수정된 부분: QnA 탭 클릭 시 fetchQna 호출)
// -----------------------------------------------------------
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
            // QnA 탭을 클릭하면 0페이지(첫 페이지) 데이터를 새로 가져옵니다.
            fetchQna(0);
        }
    });
});

function formatDuration(totalSeconds) {
    if (typeof totalSeconds !== 'number' || totalSeconds < 0) {
        return '00:00';
    }
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;

    // 두 자리로 포맷팅 (예: 5 -> 05)
    const formattedMinutes = String(minutes).padStart(2, '0');
    const formattedSeconds = String(seconds).padStart(2, '0');

    return `${formattedMinutes}:${formattedSeconds}`;
}

// =========================================================================
// --- Video (기존 로직 유지) ---
// =========================================================================
let currentVideoData = null; // 현재 재생 중인 비디오 DTO 저장
const videoPlayer = document.getElementById('videoPlayer');

function updateVideoProgress(videoId, durationSec, totalSec) {
    if (!isLoggedIn) {
        // 비로그인 시 진도율 저장 방지
        return;
    }

    const progressPercentage = Math.min(100, Math.round(((durationSec / totalSec) * 100))) || 0;

    fetch(`/api/video/${videoId}/progress`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ duration_sec: durationSec }),
    })
        .then(response => {
            if (!response.ok) {
                console.error('진도율 저장 실패');
            } else {
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
                saveVideoHistory(videoId, progressPercentage);
            }
        })
        .catch(error => console.error('Error saving progress:', error));
}

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


function cleanupVideoEvents() {
    if (!videoPlayer) return;

    if (videoPlayer.progressInterval) {
        clearInterval(videoPlayer.progressInterval);
        videoPlayer.progressInterval = null;
    }

    videoPlayer.removeEventListener('timeupdate', videoPlayer.timeUpdateHandler);
    videoPlayer.removeEventListener('ended', videoPlayer.endHandler);
}

function initializeVideoEvents(videoData) {
    if (!videoPlayer) return;
    cleanupVideoEvents();

    const videoId = videoData.video_id;
    const totalSec = videoData.video_time;
    let userDurationSec = videoData.user_duration_sec || 0;

    let lastTime = userDurationSec;
    let lastSavedTime = userDurationSec;

    // A. 타임라인 건너뛰기 방지 핸들러
    videoPlayer.timeUpdateHandler = () => {
        if (videoPlayer.currentTime > lastTime + 0.5) {
            videoPlayer.currentTime = lastTime;
        } else {
            lastTime = videoPlayer.currentTime;
        }
    };

    // B. 비디오 종료 시 최종 진도율 저장 및 정리 핸들러
    videoPlayer.endHandler = () => {
        const finalDuration = totalSec;
        updateVideoProgress(videoId, finalDuration, totalSec);
        cleanupVideoEvents();
    };

    // 이벤트 리스너 등록
    videoPlayer.addEventListener('timeupdate', videoPlayer.timeUpdateHandler);
    videoPlayer.addEventListener('ended', videoPlayer.endHandler);

    // 1초마다 진도율 저장 인터벌
    videoPlayer.progressInterval = setInterval(() => {
        if (!videoPlayer.paused && videoPlayer.currentTime > 0) {
            const currentTime = Math.min(Math.floor(videoPlayer.currentTime), totalSec);

            if (currentTime >= lastSavedTime + 5 || (currentTime >= totalSec && totalSec > 0)) {
                updateVideoProgress(videoId, currentTime, totalSec);
                lastSavedTime = currentTime;
            }
        }
    }, 1000);

    // 창 닫기/이동 시 진도율 최종 저장 (페이지를 벗어날 때)
    window.onbeforeunload = function() {
        if (videoPlayer && !videoPlayer.paused && videoPlayer.currentTime > 0) {
            const finalDuration = Math.min(Math.floor(videoPlayer.currentTime), totalSec);
            if (finalDuration > lastSavedTime || finalDuration === totalSec) {
                updateVideoProgress(videoId, finalDuration, totalSec);
            }
        }
    };
}


// 비디오 로드 (기존 함수 유지)
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
            if (!videoData) {
                alert('해당 비디오 정보를 찾을 수 없습니다.');
                return;
            }

            currentVideoData = videoData;

            const player = document.getElementById('videoPlayer');
            const mainTitleElement = document.getElementById('videoTitle');
            const mainDurationElement = document.getElementById('videoDuration');
            const progressTitle = document.querySelector('.current-lesson-title');
            const progressPercentSpan = document.querySelector('#currentLessonProgress .current-progress-percent');
            const progressFill = document.querySelector('#currentLessonProgress .current-progress-fill');

            const totalSec = videoData.video_time;
            const userDurationSec = videoData.user_duration_sec || 0;
            const progressPercentage = Math.min(100, Math.round(((userDurationSec / totalSec) * 100))) || 0;


            if (player) {
                cleanupVideoEvents();

                // 1. 비디오 URL 설정
                player.src = videoData.video_url;

                // 2. 비디오 플레이어 표시 (CSS에서 display:none을 덮어씀)
                player.style.display = 'block';

                player.load();

                if (userDurationSec > 0) {
                    player.currentTime = userDurationSec;
                }
                player.play();

                initializeVideoEvents(videoData);

                // 3.초기 플레이스홀더(있다면) 숨기기
                const placeholder = document.getElementById('initialPlaceholder');
                if (placeholder) {
                    placeholder.style.display = 'none';
                }
            }

            if (mainTitleElement) {
                mainTitleElement.textContent = videoData.video_title;
            }
            if (mainDurationElement) {
                mainDurationElement.textContent = formatDuration(videoData.video_time);
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
        })
        .catch(error => {
            console.error('AJAX 오류 발생:', error);
            alert('비디오 로드 중 오류가 발생했습니다. 콘솔을 확인해주세요.');
        });
}

// 페이지 로드 시, 이미 HTML에 로드된 비디오가 있다면 이벤트 초기화
document.addEventListener('DOMContentLoaded', function() {
    const initialActiveVideo = document.querySelector('.chapter-list .chapter-item.active');
    if (initialActiveVideo && videoPlayer) {
        loadVideo(initialActiveVideo);
    }
});
// =========================================================================
// --- Video --- 끝
// =========================================================================


// -------------------------------------------------------------------------
// --- QnA (백엔드 연동 로직) ---
// -------------------------------------------------------------------------

/**
 * QnA 데이터를 서버에서 불러옵니다.
 * @param {number} page - 요청할 페이지 번호 (0부터 시작)
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
 * QnA 목록을 렌더링합니다. (서버 응답 구조 기반)
 */
function renderQna() {
    const qnaList = document.getElementById('qnaList');

    if (qnaData.length === 0) {
        qnaList.innerHTML = '<li style="text-align: center; color: gray;">등록된 질문이 없습니다.</li>';
        return;
    }

    qnaList.innerHTML = qnaData.map(item => `
                <li class="qna-item">
                    <div class="qna-question-header">
                        <div class="qna-question-info">
                            <div class="qna-question-author">👤 ${item.authorNickname}</div>
                            <div class="qna-question">${item.content}</div>
                        </div>
                        
                        ${item.isCurrentUserAuthor ? `
                        <div class="qna-question-actions">
                            <button class="qna-action-btn" onclick="editQuestion(${item.qnaId}, '${item.content.replace(/'/g, "\\'")}')">수정</button>
                            <button class="qna-action-btn delete" onclick="deleteQuestion(${item.qnaId})">삭제</button>
                        </div>
                        ` : ''}
                    </div>

                    ${item.replies && item.replies.length > 0 ? `
                    <div class="qna-replies">
                        ${item.replies.map(reply => `
                            <div class="qna-reply-item">
                                <div class="qna-reply-header">
                                    <span class="qna-reply-author">👨‍🏫 ${reply.authorNickname}</span>
                                </div>
                                <div class="qna-reply-content">${reply.content}</div>
                            </div>
                        `).join('')}
                    </div>
                    ` : ''}

                    ${isInstructor && item.replies && item.replies.length === 0 ? `
                    <button class="qna-action-btn" style="margin-top: 1rem;" onclick="toggleReplyInput(${item.qnaId})">답글 달기</button>
                    <div class="qna-reply-input-area" id="reply-input-${item.qnaId}">
                        <textarea class="qna-reply-input" placeholder="답글을 작성하세요..." id="reply-content-${item.qnaId}"></textarea>
                        <button class="qna-reply-btn" onclick="submitReply(${item.qnaId})">답글 등록</button>
                    </div>
                    ` : ''}
                </li>
            `).join('');
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

    // 💡 [수정된 부분] userId 대신 CURRENT_USER_ID 사용
    fetch(`/api/qna/question`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            lectureId: LECTURE_ID,
            content: questionContent.trim(),
            // 현재 로그인된 사용자 ID를 사용
            userId: CURRENT_USER_ID
        })
    })
        .then(response => {
            if (!response.ok) throw new Error('질문 등록 실패');
            return response.json();
        })
        .then(() => {
            alert('질문이 등록되었습니다.');
            document.getElementById('questionInput').value = '';
            fetchQna(0); // 첫 페이지로 이동하여 목록 갱신
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
    if (!isLoggedIn || !isInstructor) {
        // 실제로는 서버에서 강사 권한 체크를 하지만, 프론트엔드 유효성 검사 추가
        alert("답글은 강사만 등록할 수 있습니다.");
        return;
    }
    const replyContent = document.getElementById(`reply-content-${pQnaId}`).value;
    if (!replyContent.trim()) {
        alert('답글 내용을 입력해주세요.');
        return;
    }

    fetch(`/api/qna/reply`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            lectureId: LECTURE_ID,
            pQnaId: pQnaId,
            content: replyContent.trim()
        })
    })
        .then(response => {
            if (!response.ok) throw new Error('답변 등록 실패');
            return response.json();
        })
        .then(() => {
            alert('답변이 등록되었습니다.');
            document.getElementById(`reply-content-${pQnaId}`).value = '';
            document.getElementById(`reply-input-${pQnaId}`).classList.remove('active');
            fetchQna(currentQnaPage); // 현재 페이지 목록 갱신
        })
        .catch(error => {
            console.error('답변 등록 오류:', error);
            alert('답변 등록 중 오류가 발생했습니다. (서버 로그 확인)');
        });
}

function editQuestion(qnaId, currentContent) {
    if (!isLoggedIn) {
        alert("로그인 후 이용해 주세요.");
        return;
    }
    // 실제 수정 로직은 생략되었습니다. (API 호출, 모달 팝업 등 필요)
    alert(`질문 ${qnaId} 수정 기능 (백엔드 연동 필요)\n기존 내용: ${currentContent}`);
}

function deleteQuestion(qnaId) {
    if (!isLoggedIn) {
        alert("로그인 후 이용해 주세요.");
        return;
    }
    if (confirm('정말 삭제하시겠습니까?')) {
        // 실제 삭제 API 호출 로직 (생략)
        fetch(`/api/qna/${qnaId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) throw new Error('삭제 실패');
                alert(`질문 ${qnaId} 삭제됨`);
                fetchQna(currentQnaPage); // 목록 갱신
            })
            .catch(error => {
                console.error('삭제 오류:', error);
                alert('삭제 중 오류가 발생했습니다.');
            });
    }
}