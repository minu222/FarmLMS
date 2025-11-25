/*<![CDATA[*/
const lecturesApiUrl = /*[[@{/admin/comments/api/lectures}]]*/ "/admin/comments/api/lectures";
const commentsApiBaseUrl = /*[[@{/admin/comments/api/lectures}]]*/ "/admin/comments/api/lectures";
const addReplyApiBaseUrl = /*[[@{/admin/comments/api/comments}]]*/ "/admin/comments/api/comments";
const deleteCommentApiBaseUrl = /*[[@{/admin/comments/api/comments}]]*/ "/admin/comments/api/comments";
const deleteReplyApiBaseUrl = /*[[@{/admin/comments/api/replies}]]*/ "/admin/comments/api/replies";

// 영문 -> 한글 변환 맵
const categoryMap = {
    'gardening': '텃밭',
    'field': '노지',
    'house': '하우스'
};

const levelMap = {
    'seed': '모종',
    'grow': '재배',
    'ship': '출하'
};

(function () {
    let lectures = [];
    let filteredLectures = [];
    let currentLecture = null;
    let currentPage = 1;
    const COMMENTS_PER_PAGE = 5;

    const lectureListEl = document.getElementById("lectureList");
    const lectureInfoEl = document.getElementById("lectureInfo");
    const commentsContainerEl = document.getElementById("commentsContainer");
    const paginationEl = document.getElementById("pagination");
    const commentCountLabelEl = document.getElementById("commentCountLabel");
    const searchInputEl = document.getElementById("searchLecture");
    const categoryFilterEl = document.getElementById("categoryFilter");
    const levelFilterEl = document.getElementById("levelFilter");

    function escapeHtml(str) {
        if (!str) return "";
        return str.replace(/[&<>"']/g, function (m) {
            switch (m) {
                case "&": return "&amp;";
                case "<": return "&lt;";
                case ">": return "&gt;";
                case "\"": return "&quot;";
                case "'": return "&#39;";
                default: return m;
            }
        });
    }

    function renderLectureList() {
        if (!filteredLectures.length) {
            lectureListEl.innerHTML = '<li class="lecture-item"><span class="lecture-item-title">조건에 맞는 강의가 없습니다.</span></li>';
            return;
        }

        lectureListEl.innerHTML = filteredLectures.map(lec =>
            `<li class="lecture-item${currentLecture && currentLecture.id === lec.id ? " active" : ""}" data-id="${lec.id}">
                    <div class="lecture-item-title">${escapeHtml(lec.title)}</div>
                    <div class="lecture-item-meta">
                        ${escapeHtml(lec.instructor || "")} · ${categoryMap[lec.category] || lec.category || ""} · ${levelMap[lec.level] || lec.level || ""} · 댓글 ${lec.commentCount ?? 0}개
                    </div>
                 </li>`
        ).join("");
    }

    function renderLectureInfo() {
        if (!currentLecture) {
            lectureInfoEl.innerHTML = "<p>왼쪽에서 강의를 선택하세요.</p>";
            return;
        }

        const lec = currentLecture;
        lectureInfoEl.innerHTML = `
                <h3>${escapeHtml(lec.title)}</h3>
                <p>
                    <span class="tag">${categoryMap[lec.category] || lec.category || ""}</span>
                    <span class="tag">${levelMap[lec.level] || lec.level || ""}</span>
                    <span class="tag">${escapeHtml(lec.status || "공개")}</span>
                </p>
                <p><strong>강사</strong> : ${escapeHtml(lec.instructor || "")}</p>
                <p><strong>설명</strong> : ${escapeHtml(lec.description || "")}</p>
            `;
    }

    function renderComments() {
        if (!currentLecture) {
            commentsContainerEl.innerHTML = '<p class="empty-message">강의를 먼저 선택하세요.</p>';
            paginationEl.innerHTML = "";
            commentCountLabelEl.textContent = "";
            return;
        }

        const comments = currentLecture.comments || [];
        const totalCount = comments.length;
        const totalPages = Math.max(1, Math.ceil(totalCount / COMMENTS_PER_PAGE));

        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;

        const startIndex = (currentPage - 1) * COMMENTS_PER_PAGE;
        const pageComments = comments.slice(startIndex, startIndex + COMMENTS_PER_PAGE);

        commentCountLabelEl.textContent = totalCount ? `총 ${totalCount}개` : "댓글 없음";

        if (!comments.length) {
            commentsContainerEl.innerHTML = '<p class="empty-message">아직 댓글이 없습니다.</p>';
        } else if (!pageComments.length) {
            commentsContainerEl.innerHTML = '<p class="empty-message">이 페이지에는 댓글이 없습니다.</p>';
        } else {
            commentsContainerEl.innerHTML = pageComments.map(c => renderCommentHtml(c)).join("");
        }

        let paginationHtml = "";
        for (let p = 1; p <= totalPages; p++) {
            paginationHtml += `<button class="page-btn${p === currentPage ? " active" : ""}" data-page="${p}">${p}</button>`;
        }
        if (currentPage < totalPages) {
            paginationHtml += `<button class="page-btn next" data-page="next">&gt;</button>`;
        }
        paginationEl.innerHTML = paginationHtml;
    }

    function renderCommentHtml(comment) {
        const repliesHtml = (comment.replies || []).map(reply => `
                <div class="reply" data-reply-id="${reply.id}">
                  <div class="reply-header">
                    <span class="reply-author">${escapeHtml(reply.authorName)}</span>
                    <span class="reply-date">${escapeHtml(formatDate(reply.createdAt))}</span>
                  </div>
                  <div class="reply-content">${escapeHtml(reply.content)}</div>
                  <div class="reply-actions">
                    <button type="button" class="delete-reply-btn">답글 삭제</button>
                  </div>
                </div>
            `).join("");

        return `
              <div class="comment" data-comment-id="${comment.id}">
                <div class="comment-header">
                  <span class="comment-author">${escapeHtml(comment.authorName)}</span>
                  <span class="comment-date">${escapeHtml(formatDate(comment.createdAt))}</span>
                </div>
                <div class="comment-content">${escapeHtml(comment.content)}</div>
                <div class="comment-actions">
                  <button type="button" class="reply-btn">답글</button>
                  <button type="button" class="delete-comment-btn">댓글 삭제</button>
                </div>
                <div class="reply-form hidden">
                  <textarea class="reply-textarea" rows="2" placeholder="답글을 입력하세요."></textarea>
                  <div class="reply-form-actions">
                    <button type="button" class="submit-reply-btn primary">등록</button>
                    <button type="button" class="cancel-reply-btn">취소</button>
                  </div>
                </div>
                <div class="replies">${repliesHtml}</div>
              </div>
            `;
    }

    function formatDate(dateStr) {
        if (!dateStr) return "";
        return String(dateStr).replace('T', ' ').substring(0, 16);
    }

    function applyFilters() {
        const searchTerm = searchInputEl.value.trim().toLowerCase();
        const category = categoryFilterEl.value;
        const level = levelFilterEl.value;

        filteredLectures = lectures.filter(lec => {
            const text = ((lec.title || "") + " " + (lec.instructor || "")).toLowerCase();
            const matchesSearch = !searchTerm || text.includes(searchTerm);
            const matchesCategory = !category || (lec.category || "") === category;
            const matchesLevel = !level || (lec.level || "") === level;
            return matchesSearch && matchesCategory && matchesLevel;
        });

        // 필터링된 결과가 없거나, 현재 선택된 강의가 필터 결과에 없으면
        if (!filteredLectures.length) {
            currentLecture = null;
            currentPage = 1;
            renderLectureList();
            renderLectureInfo();
            renderComments();
        } else if (!currentLecture || !filteredLectures.find(l => l.id === currentLecture.id)) {
            // 현재 강의가 없거나 필터 결과에 없으면 첫 번째 강의 선택
            currentLecture = filteredLectures[0];
            currentPage = 1;
            renderLectureList();
            renderLectureInfo();
            loadComments(currentLecture.id);
        } else {
            // 현재 강의가 필터 결과에 포함되어 있으면 목록만 다시 렌더링
            renderLectureList();
        }
    }



    function loadLectures() {
        fetch(lecturesApiUrl)
            .then(res => {
                if (!res.ok) throw new Error("강의 목록 조회 실패");
                return res.json();
            })
            .then(data => {
                lectures = data || [];
                filteredLectures = [...lectures];
                currentLecture = filteredLectures[0] || null;
                currentPage = 1;

                renderLectureList();
                renderLectureInfo();

                if (currentLecture) {
                    loadComments(currentLecture.id);
                } else {
                    renderComments();
                }
            })
            .catch(err => {
                console.error(err);
                lectureListEl.innerHTML = '<li class="lecture-item"><span class="lecture-item-title">강의 목록을 불러오는 중 오류가 발생했습니다.</span></li>';
            });
    }

    function loadComments(lectureId) {
        fetch(`${commentsApiBaseUrl}/${lectureId}/comments`)
            .then(res => {
                if (!res.ok) throw new Error("댓글 조회 실패");
                return res.json();
            })
            .then(data => {
                if (!currentLecture || currentLecture.id !== lectureId) return;
                currentLecture.comments = data || [];
                currentPage = 1;
                renderLectureInfo();
                renderComments();
            })
            .catch(err => {
                console.error(err);
                commentsContainerEl.innerHTML = '<p class="empty-message">댓글을 불러오는 중 오류가 발생했습니다.</p>';
                commentCountLabelEl.textContent = "";
            });
    }

    function addReply(commentId, content) {
        return fetch(`${addReplyApiBaseUrl}/${commentId}/replies`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({content: content})
        }).then(res => {
            if (!res.ok) throw new Error("답글 등록 실패");
        });
    }

    function deleteComment(commentId) {
        return fetch(`${deleteCommentApiBaseUrl}/${commentId}`, {
            method: "DELETE"
        }).then(res => {
            if (!res.ok) throw new Error("댓글 삭제 실패");
        });
    }

    function deleteReply(replyId) {
        return fetch(`${deleteReplyApiBaseUrl}/${replyId}`, {
            method: "DELETE"
        }).then(res => {
            if (!res.ok) throw new Error("답글 삭제 실패");
        });
    }

    function bindEvents() {
        lectureListEl.addEventListener("click", (e) => {
            const item = e.target.closest(".lecture-item");
            if (!item) return;
            const id = Number(item.dataset.id);
            const selected = filteredLectures.find(lec => lec.id === id);
            if (!selected) return;

            currentLecture = selected;
            currentPage = 1;

            renderLectureList();
            renderLectureInfo();
            loadComments(currentLecture.id);
        });

        searchInputEl.addEventListener("input", applyFilters);
        categoryFilterEl.addEventListener("change", applyFilters);
        levelFilterEl.addEventListener("change", applyFilters);

        commentsContainerEl.addEventListener("click", (e) => {
            if (!currentLecture) return;

            const commentEl = e.target.closest(".comment");
            const isReplyDeleteBtn = e.target.classList.contains("delete-reply-btn");

            if (isReplyDeleteBtn) {
                const replyEl = e.target.closest(".reply");
                if (!replyEl) return;
                const replyId = Number(replyEl.dataset.replyId);
                if (!confirm("이 답글을 삭제하시겠습니까?")) return;

                deleteReply(replyId)
                    .then(() => loadComments(currentLecture.id))
                    .catch(err => {
                        console.error(err);
                        alert("답글 삭제 중 오류가 발생했습니다.");
                    });
                return;
            }

            if (!commentEl) return;
            const commentId = Number(commentEl.dataset.commentId);
            const comment = (currentLecture.comments || []).find(c => c.id === commentId);
            if (!comment) return;

            if (e.target.classList.contains("reply-btn")) {
                const replyForm = commentEl.querySelector(".reply-form");
                if (replyForm) {
                    replyForm.classList.toggle("hidden");
                    const textarea = replyForm.querySelector(".reply-textarea");
                    if (!replyForm.classList.contains("hidden") && textarea) {
                        textarea.focus();
                    }
                }
                return;
            }

            if (e.target.classList.contains("delete-comment-btn")) {
                if (!confirm("이 댓글과 연결된 답글들이 모두 삭제됩니다. 계속하시겠습니까?")) return;

                deleteComment(commentId)
                    .then(() => loadComments(currentLecture.id))
                    .catch(err => {
                        console.error(err);
                        alert("댓글 삭제 중 오류가 발생했습니다.");
                    });
                return;
            }

            if (e.target.classList.contains("submit-reply-btn")) {
                const replyForm = commentEl.querySelector(".reply-form");
                const textarea = replyForm && replyForm.querySelector(".reply-textarea");
                if (!textarea) return;
                const content = textarea.value.trim();
                if (!content) {
                    alert("답글 내용을 입력하세요.");
                    return;
                }

                addReply(commentId, content)
                    .then(() => {
                        textarea.value = "";
                        replyForm.classList.add("hidden");
                        loadComments(currentLecture.id);
                    })
                    .catch(err => {
                        console.error(err);
                        alert("답글 등록 중 오류가 발생했습니다.");
                    });
                return;
            }

            if (e.target.classList.contains("cancel-reply-btn")) {
                const replyForm = commentEl.querySelector(".reply-form");
                if (replyForm) {
                    replyForm.classList.add("hidden");
                    const textarea = replyForm.querySelector(".reply-textarea");
                    if (textarea) textarea.value = "";
                }
                return;
            }
        });

        paginationEl.addEventListener("click", (e) => {
            const btn = e.target.closest(".page-btn");
            if (!btn || !currentLecture) return;

            const totalPages = Math.max(1, Math.ceil((currentLecture.comments || []).length / COMMENTS_PER_PAGE));
            const page = btn.dataset.page;

            if (page === "next") {
                if (currentPage < totalPages) {
                    currentPage++;
                    renderComments();
                }
            } else {
                const num = Number(page);
                if (!isNaN(num) && num >= 1 && num <= totalPages) {
                    currentPage = num;
                    renderComments();
                }
            }
        });
    }

    function init() {
        bindEvents();
        loadLectures();
    }

    document.addEventListener("DOMContentLoaded", init);
})();
/*]]>*/