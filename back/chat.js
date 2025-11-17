// ===== 전역 상태 =====
let stompClient = null;
let currentRoomId = null;
let currentSubscription = null;
let currentRoomOwnerId = null;
let userId = null;
let onlineListEl, offlineListEl;
let presenceSubscription = null;
let userNickname = null;


let appEl,
    joinedListEl,
    otherListEl,
    chatViewEl,
    chatInputEl,
    sendBtnEl,
    activeRoomNameEl,
    leaveBtnEl,
    deleteBtnEl;

let joinModalEl,
    modalRoomNameEl,
    createModalEl,
    newRoomNameInputEl;

let pendingJoinRoomId = null;
let pendingJoinRoomName = "";
let pendingJoinRoomOwnerId = null;

// ===== 초기화 =====
document.addEventListener("DOMContentLoaded", initChatPage);

function initChatPage() {
    appEl = document.getElementById("app");
    if (!appEl) {
        console.warn("#app 엘리먼트를 찾을 수 없습니다.");
        return;
    }

    userId = Number(appEl.dataset.userId);
    userNickname = appEl.dataset.userNickname || "";

    joinedListEl = document.getElementById("joinedList");
    otherListEl = document.getElementById("otherList");
    chatViewEl = document.getElementById("chatView");
    chatInputEl = document.getElementById("chatInput");
    sendBtnEl = document.getElementById("sendBtn");
    activeRoomNameEl = document.getElementById("activeRoomName");
    leaveBtnEl = document.getElementById("leaveBtn");
    deleteBtnEl = document.getElementById("deleteBtn");

    // ★ 온라인/오프라인 영역
    onlineListEl = document.getElementById("onlineList");
    offlineListEl = document.getElementById("offlineList");

    joinModalEl = document.getElementById("joinModal");
    modalRoomNameEl = document.getElementById("modalRoomName");
    createModalEl = document.getElementById("createModal");
    newRoomNameInputEl = document.getElementById("newRoomName");

    bindEvents();
    connectWebSocket();
    loadRoomList();
}


// ===== 이벤트 바인딩 =====
function bindEvents() {
    if (sendBtnEl) {
        sendBtnEl.addEventListener("click", sendMessage);
    }

    if (chatInputEl) {
        chatInputEl.addEventListener("keydown", function (e) {
            // Enter만 누르면 전송, Shift+Enter는 줄바꿈
            if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
    }
}

// ===== WebSocket / STOMP 연결 =====
function connectWebSocket() {
    console.log("웹소켓 연결 시도...");

    if (typeof SockJS === "undefined") {
        console.error("SockJS 라이브러리가 로드되지 않았습니다.");
        alert("실시간 채팅 모듈(SockJS)이 로드되지 않았습니다.\n스크립트 경로를 확인해주세요.");
        return;
    }
    if (typeof Stomp === "undefined") {
        console.error("Stomp 라이브러리가 로드되지 않았습니다.");
        alert("실시간 채팅 모듈(STOMP)이 로드되지 않았습니다.\n스크립트 경로를 확인해주세요.");
        return;
    }

    const socket = new SockJS("/ws-chat");
    stompClient = Stomp.over(socket);

    // 디버그 로그 (원하면 주석 처리 가능)
    stompClient.debug = function (str) {
        console.log("[STOMP]", str);
    };

    stompClient.connect(
        {},
        function (frame) {
            console.log("STOMP Connected:", frame);
            if (currentRoomId) {
                subscribeRoom(currentRoomId);
                subscribePresence(currentRoomId);
                sendPresence("JOIN");
            }
        },
        function (error) {
            console.error("STOMP 연결 실패:", error);
        }
    );

}

function subscribeRoom(roomId) {
    if (!stompClient || !stompClient.connected) return;

    if (currentSubscription) {
        currentSubscription.unsubscribe();
    }

    // 서버는 항상 /topic/chat 으로만 브로드캐스트
    currentSubscription = stompClient.subscribe(
        "/topic/chat",
        function (message) {
            const body = JSON.parse(message.body);

            // 🔥 현재 선택된 방의 메시지만 표시
            if (!currentRoomId || Number(body.roomId) !== Number(roomId)) {
                return;
            }
            appendMessage(body);
        }
    );
}

// ===== 방 목록 로드 =====
async function loadRoomList() {
    if (!userId) return;

    try {
        const res = await fetch(`/api/chat/rooms?userId=${userId}`);
        if (!res.ok) throw new Error("채팅방 목록 조회 실패");

        const data = await res.json();
        renderRoomList(joinedListEl, data.joined, true);
        renderRoomList(otherListEl, data.others, false);
    } catch (e) {
        console.error(e);
    }
}

function renderRoomList(container, rooms, joined) {
    if (!container) return;
    container.innerHTML = "";

    if (!rooms || rooms.length === 0) {
        const empty = document.createElement("div");
        empty.className = "empty";
        empty.textContent = joined
            ? "참여 중인 채팅방이 없습니다."
            : "생성된 채팅방이 없습니다.";
        container.appendChild(empty);
        return;
    }

    rooms.forEach((room) => {
        const ownerId = room.userId;

        const item = document.createElement("div");
        item.className = "room-item";
        item.dataset.roomId = room.roomId;
        item.dataset.roomName = room.roomName || `방 #${room.roomId}`;
        item.dataset.ownerId = ownerId;

        item.addEventListener("click", () => {
            if (joined) {
                enterRoom(room.roomId, room.roomName, ownerId);
            } else {
                openJoinModal(room.roomId, room.roomName, ownerId);
            }
        });

        const title = document.createElement("div");
        title.className = "room-name";
        title.textContent = room.roomName || "(이름 없음)";

        const meta = document.createElement("div");
        meta.className = "room-meta";
        meta.textContent = room.createdAt ? formatDate(room.createdAt) : "";

        item.appendChild(title);
        item.appendChild(meta);
        container.appendChild(item);
    });
}

// ===== 방 입장 / 나가기 =====
async function enterRoom(roomId, roomName, ownerUserId) {
    currentRoomId = roomId;
    currentRoomOwnerId = ownerUserId;

    if (activeRoomNameEl) {
        activeRoomNameEl.textContent = roomName || "채팅방";
    }

    if (chatViewEl) {
        chatViewEl.innerHTML = "";
    }

    enableChat(true);

    if (leaveBtnEl) {
        leaveBtnEl.style.display = "inline-block";
    }

    // 방장인 경우에만 삭제 버튼 노출
    if (deleteBtnEl) {
        if (ownerUserId && Number(ownerUserId) === Number(userId)) {
            deleteBtnEl.style.display = "inline-block";
        } else {
            deleteBtnEl.style.display = "none";
        }
    }

    // 과거 메시지 로드
    try {
        const res = await fetch(`/api/chat/rooms/${roomId}/messages?limit=100`);
        if (res.ok) {
            const messages = await res.json();
            messages.forEach((msg) => {
                appendMessage({
                    messageId: msg.messageId,
                    roomId: msg.roomId,
                    userId: msg.userId,
                    content: msg.content,
                    createdAt: msg.createdAt,
                });
            });
        }
    } catch (e) {
        console.error(e);
    }

    // STOMP 구독
    if (stompClient && stompClient.connected) {
        subscribeRoom(roomId);
        subscribePresence(roomId);
        sendPresence("JOIN");
    }

}

async function leaveRoom() {
    if (!currentRoomId || !userId) return;
    if (!confirm("이 채팅방에서 나가시겠습니까?")) return;

    try {
        const res = await fetch(
            `/api/chat/rooms/${currentRoomId}/leave?userId=${userId}`,
            { method: "POST" }
        );
        if (!res.ok) throw new Error("채팅방 나가기 실패");
    } catch (e) {
        console.error(e);
        alert("채팅방 나가기 중 오류가 발생했습니다.");
    }

    // ★ presence LEAVE
    sendPresence("LEAVE");

    if (currentSubscription) {
        currentSubscription.unsubscribe();
        currentSubscription = null;
    }
    if (presenceSubscription) {
        presenceSubscription.unsubscribe();
        presenceSubscription = null;
    }

    currentRoomId = null;
    currentRoomOwnerId = null;

    if (activeRoomNameEl) {
        activeRoomNameEl.textContent = "채팅방을 선택하세요";
    }
    if (chatViewEl) {
        chatViewEl.innerHTML = "";
    }
    enableChat(false);

    if (leaveBtnEl) {
        leaveBtnEl.style.display = "none";
    }
    if (deleteBtnEl) {
        deleteBtnEl.style.display = "none";
    }

    // 온라인 목록도 초기화
    if (onlineListEl) onlineListEl.innerHTML = "";
    if (offlineListEl) offlineListEl.innerHTML = "";

    await loadRoomList();
}

window.leaveRoom = leaveRoom;

// ===== 방 삭제 =====
async function deleteRoom() {
    if (!currentRoomId || !userId) return;

    if (!currentRoomOwnerId || Number(currentRoomOwnerId) !== Number(userId)) {
        alert("방 생성자만 삭제할 수 있습니다.");
        return;
    }

    if (
        !confirm(
            "이 채팅방을 삭제하시겠습니까?\n(모든 메시지와 멤버 기록이 함께 삭제됩니다)"
        )
    ) {
        return;
    }

    try {
        const res = await fetch(
            `/api/chat/rooms/${currentRoomId}?userId=${userId}`,
            { method: "DELETE" }
        );

        if (!res.ok) {
            console.error("삭제 요청 실패:", res.status);
            alert("채팅방 삭제 중 오류가 발생했습니다.");
            return;
        }

        // 성공 시 상태 초기화
        // ★ 삭제 전 LEAVE 전송
        sendPresence("LEAVE");

        if (currentSubscription) {
            currentSubscription.unsubscribe();
            currentSubscription = null;
        }
        if (presenceSubscription) {
            presenceSubscription.unsubscribe();
            presenceSubscription = null;
        }

// ... 나머지 기존 초기화 로직 동일


        currentRoomId = null;
        currentRoomOwnerId = null;

        if (activeRoomNameEl) {
            activeRoomNameEl.textContent = "채팅방을 선택하세요";
        }
        if (chatViewEl) {
            chatViewEl.innerHTML = "";
        }
        enableChat(false);

        if (leaveBtnEl) {
            leaveBtnEl.style.display = "none";
        }
        if (deleteBtnEl) {
            deleteBtnEl.style.display = "none";
        }

        await loadRoomList();
    } catch (e) {
        console.error(e);
        alert("채팅방 삭제 중 오류가 발생했습니다.");
    }
}
window.deleteRoom = deleteRoom;

// ===== 메시지 전송 =====
function sendMessage() {
    if (!currentRoomId) {
        alert("채팅방을 먼저 선택하세요.");
        return;
    }
    if (!stompClient) {
        alert("웹소켓 클라이언트가 초기화되지 않았습니다.");
        return;
    }
    if (!stompClient.connected) {
        alert("서버에 연결되지 않았습니다. 잠시 후 다시 시도해주세요.");
        return;
    }
    if (!chatInputEl) return;

    const text = chatInputEl.value.trim();
    if (!text) return;

    const payload = {
        roomId: currentRoomId,
        userId: userId,
        content: text,
    };

    stompClient.send("/app/chat.send", {}, JSON.stringify(payload));

    chatInputEl.value = "";
}

// ===== 메시지 렌더링 =====
function appendMessage(msg) {
    if (!chatViewEl) return;

    const wrap = document.createElement("div");
    wrap.classList.add("chat-message");

    if (Number(msg.userId) === Number(userId)) {
        wrap.classList.add("mine");
    } else {
        wrap.classList.add("others");
    }

    const textEl = document.createElement("div");
    textEl.classList.add("msg-text");
    textEl.textContent = msg.content;

    const metaEl = document.createElement("div");
    metaEl.classList.add("msg-meta");
    metaEl.textContent = formatTime(msg.createdAt);

    wrap.appendChild(textEl);
    wrap.appendChild(metaEl);

    chatViewEl.appendChild(wrap);
    chatViewEl.scrollTop = chatViewEl.scrollHeight;
}

// ===== 모달(입장) =====
function openJoinModal(roomId, roomName, ownerUserId) {
    pendingJoinRoomId = roomId;
    pendingJoinRoomName = roomName;
    pendingJoinRoomOwnerId = ownerUserId;

    if (modalRoomNameEl) {
        modalRoomNameEl.textContent = roomName || "";
    }
    if (joinModalEl) {
        joinModalEl.style.display = "flex";
    }
}

function closeModal() {
    pendingJoinRoomId = null;
    pendingJoinRoomName = "";
    pendingJoinRoomOwnerId = null;
    if (joinModalEl) {
        joinModalEl.style.display = "none";
    }
}
window.closeModal = closeModal;

async function confirmJoin() {
    if (!pendingJoinRoomId || !userId) {
        closeModal();
        return;
    }

    try {
        const res = await fetch(
            `/api/chat/rooms/${pendingJoinRoomId}/join?userId=${userId}`,
            { method: "POST" }
        );
        if (!res.ok) throw new Error("채팅방 입장 실패");

        closeModal();
        await loadRoomList();
        enterRoom(
            pendingJoinRoomId,
            pendingJoinRoomName,
            pendingJoinRoomOwnerId
        );
    } catch (e) {
        console.error(e);
        alert("채팅방 입장 중 오류가 발생했습니다.");
    }
}
window.confirmJoin = confirmJoin;

// ===== 모달(방 생성) =====
function openCreateModal() {
    if (createModalEl) {
        createModalEl.style.display = "flex";
        if (newRoomNameInputEl) {
            newRoomNameInputEl.value = "";
            newRoomNameInputEl.focus();
        }
    }
}
window.openCreateModal = openCreateModal;

function closeCreateModal() {
    if (createModalEl) {
        createModalEl.style.display = "none";
    }
}
window.closeCreateModal = closeCreateModal;

async function confirmCreate() {
    if (!userId) {
        alert("로그인이 필요합니다.");
        return;
    }
    if (!newRoomNameInputEl) return;

    const roomName = newRoomNameInputEl.value.trim();
    if (!roomName) {
        alert("채팅방 이름을 입력해주세요.");
        return;
    }

    try {
        const res = await fetch(`/api/chat/rooms?userId=${userId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ roomName }),
        });
        if (!res.ok) throw new Error("채팅방 생성 실패");

        const room = await res.json();
        closeCreateModal();
        await loadRoomList();
        // 생성자는 무조건 방장
        enterRoom(room.roomId, room.roomName, userId);
    } catch (e) {
        console.error(e);
        alert("채팅방 생성 중 오류가 발생했습니다.");
    }
}
window.confirmCreate = confirmCreate;

// ===== 유틸 =====
function enableChat(enable) {
    if (chatInputEl) {
        chatInputEl.disabled = !enable;
        if (enable) {
            chatInputEl.focus();
        }
    }
    if (sendBtnEl) {
        sendBtnEl.disabled = !enable;
    }
}

function formatDate(str) {
    if (!str) return "";
    const d = new Date(str);
    if (Number.isNaN(d.getTime())) return "";
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const dd = String(d.getDate()).padStart(2, "0");
    const hh = String(d.getHours()).padStart(2, "0");
    const mi = String(d.getMinutes()).padStart(2, "0");
    return `${mm}/${dd} ${hh}:${mi}`;
}

function formatTime(str) {
    if (!str) return "";
    const d = new Date(str);
    if (Number.isNaN(d.getTime())) return "";
    const hh = String(d.getHours()).padStart(2, "0");
    const mi = String(d.getMinutes()).padStart(2, "0");
    return `${hh}:${mi}`;
}

function subscribePresence(roomId) {
    if (!stompClient || !stompClient.connected) return;

    if (presenceSubscription) {
        presenceSubscription.unsubscribe();
    }

    presenceSubscription = stompClient.subscribe(
        "/topic/presence." + roomId,
        function (message) {
            const users = JSON.parse(message.body); // [{userId, nickname}, ...]
            renderOnlineUsers(users);
        }
    );
}

function sendPresence(type) {
    if (!stompClient || !stompClient.connected || !currentRoomId || !userId) return;

    const payload = {
        roomId: currentRoomId,
        userId: userId,
        nickname: userNickname,
        type: type
    };

    stompClient.send(`/app/presence/${currentRoomId}`, {}, JSON.stringify(payload));
}

function renderOnlineUsers(users) {
    if (!onlineListEl) return;
    onlineListEl.innerHTML = "";

    if (!users || users.length === 0) {
        const empty = document.createElement("div");
        empty.className = "empty";
        empty.textContent = "온라인 사용자가 없습니다.";
        onlineListEl.appendChild(empty);
        if (offlineListEl) {
            offlineListEl.innerHTML = "";
        }
        return;
    }

    users.forEach((u) => {
        const item = document.createElement("div");
        item.className = "user-item";
        item.textContent = u.nickname || `User #${u.userId}`;
        onlineListEl.appendChild(item);
    });

    // 오프라인 영역은 데모에서는 비워둠
    if (offlineListEl) {
        offlineListEl.innerHTML = "";
    }
}
