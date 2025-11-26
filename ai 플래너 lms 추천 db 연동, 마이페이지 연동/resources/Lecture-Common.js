/**
 * Lecture-Common.js
 * - 강의 링크 클릭 시 로그인 상태 및 구독 여부를 확인하고 처리합니다.
 */

function handleCourseClick(lectureId, lectureTitle, isSubscribed) {
    // 1. 로그인 확인 (isLoggedIn 변수는 뷰 템플릿에서 정의됨)
    // 이 변수가 정의되어 있지 않다면 (ReferenceError), 템플릿 로드 순서를 재확인해야 합니다.
    if (!isLoggedIn) {
        alert("강의를 시청하거나 구독하려면 로그인이 필요합니다.");
        window.location.href = '/login';
        return;
    }

    // 2. 이미 구독했으면 바로 상세 페이지로 이동
    // AI 플래너 결과나 마이페이지 목록에서 이 함수가 호출된 경우,
    // isSubscribed 플래그가 true이면 구독 API 호출 없이 바로 이동합니다.
    if (isSubscribed) {
        window.location.href = `/lecture/lectureDetail?lectureId=${lectureId}`;
        return;
    } else {
        // 3. 구독 안 했으면 알림 및 구독 요청
        const confirmSub = confirm(`[${lectureTitle}] 강의를 구독하시겠습니까?\n구독 후 강의 시청이 가능합니다.`);

        if (confirmSub) {
            // 구독 API 호출 (LectureSubController의 POST 요청)
            fetch(`/api/subscribe/${lectureId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                    // Spring Security를 사용하지 않으므로 CSRF 토큰은 추가하지 않음
                }
            })
                .then(response => {
                    // 401 Unauthorized 처리 (컨트롤러에서 명시적으로 반환)
                    if (response.status === 401) {
                        alert("로그인 세션이 만료되었습니다. 다시 로그인해주세요.");
                        window.location.href = '/login';
                        // 다음 then 블록이 실행되지 않도록 Promise.reject로 실패 처리
                        return Promise.reject(new Error('인증 실패'));
                    }

                    // 200 (구독 성공) 또는 409 (이미 구독됨) 응답을 다음 then 블록에서 처리하기 위해 반환
                    if (response.status === 200 || response.status === 409) {
                        return response.text().then(text => ({ status: response.status, text: text }));
                    }

                    // 400, 500 등 예상치 못한 오류
                    return response.text().then(text => {
                        // 오류 메시지가 너무 길 경우를 대비해 일부만 추출
                        const errorMsg = text.length > 200 ? text.substring(0, 200) + '...' : text;
                        throw new Error(`구독 요청 실패 [HTTP ${response.status}]: ${errorMsg}`);
                    });
                })
                .then(result => {
                    let message = "";
                    if (result.status === 200) {
                        message = "강의 구독이 완료되었습니다. 강의를 시작합니다.";
                    } else if (result.status === 409) {
                        // 409 Conflict: 이미 구독됨 (정상적인 흐름으로 처리)
                        message = "이미 구독된 강의입니다. 강의를 시작합니다.";
                    }

                    alert(message);
                    // 200이든 409든, 구독 상태가 확인되었으므로 상세 페이지로 이동
                    window.location.href = `/lecture/lectureDetail?lectureId=${lectureId}`;
                })
                .catch(error => {
                    // Promise.reject()로 던져진 모든 오류와 fetch 자체 오류를 여기서 처리
                    console.error('구독 오류:', error);

                    // 인증 실패 오류(401)는 이미 위에서 처리했으므로, 여기는 일반적인 오류 메시지를 표시
                    if (error.message && !error.message.includes('인증 실패')) {
                        alert(`구독 요청 중 오류가 발생했습니다. 상세 오류: ${error.message || '알 수 없는 오류'}`);
                    }
                });
        }
    }
}