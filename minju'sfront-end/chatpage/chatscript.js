    window.addEventListener('scroll', () => {
      const header = document.getElementById('header');
      header.classList.toggle('scrolled', window.scrollY > 50);
    });

    const myJoinedRoomIds = [1, 4, 7];
    let nextRoomId = 9; // 새 채팅방 ID

    const rooms = [
    {id: 1, name: '벼농사 초보자 모임', desc: '초보의 질문 환영', members: 45 },
    {id: 2, name: '스마트팜 기술 공유', desc: 'IoT/자동화 노하우', members: 67 },
    {id: 3, name: '토마토 재배 노하우', desc: '품종/양액/환경', members: 32 },
    {id: 4, name: '귀농 준비 모임', desc: '정책/정착 이야기', members: 89 },
    {id: 5, name: '과수원 운영 꿀팁', desc: '사과/배/포도', members: 51 },
    {id: 6, name: '버섯 재배 연구소', desc: '표고/느타리', members: 28 },
    {id: 7, name: '친환경 농업 실천', desc: '유기자재/인증', members: 73 },
    {id: 8, name: '농기계 정보 교환', desc: '구매/수리/팁', members: 41 }
    ];

    const sampleMessages = {
        1: [
    {me: false, author: '김농부', text: '어서오세요! 벼 파종은 어디서부터 시작했나요?' },
    {me: true, author: '나', text: '육묘부터 준비 중이에요.' }
    ],
    4: [{me: false, author: '관리자', text: '11월 청년 귀농 설명회 공지 확인해주세요.' }],
    7: [
    {me: false, author: '그린', text: '퇴비 살포 시기 공유합니다.' },
    {me: true, author: '나', text: '자료 감사합니다!' }
    ]
    };

    let selectedRoom = null, pendingJoinRoom = null;

    renderRoomLists();

    function renderRoomLists() {
      const joinedList = document.getElementById('joinedList'), otherList = document.getElementById('otherList');
    joinedList.innerHTML = otherList.innerHTML = '';
      rooms.forEach(r => {
        const joined = myJoinedRoomIds.includes(r.id), isActive = selectedRoom && selectedRoom.id === r.id;
    const el = document.createElement('div');
    el.className = `room-item ${isActive ? 'active' : ''}`;
    el.dataset.roomId = r.id;
    el.innerHTML = `
    <div class="room-avatar">${r.name.slice(0, 1)}</div>
    <div class="room-body">
        <div class="room-title">
            <span>${r.name}</span>
            ${joined ? '<span class="badge-joined">참여중</span>' : ''}
        </div>
        <div class="room-meta">${r.desc} · ${r.members}명</div>
    </div>
    `;
        el.addEventListener('click', () => joined ? openRoom(r.id) : openJoinModal(r.id));
    (joined ? joinedList : otherList).appendChild(el);
      });
    }

    function openRoom(roomId) {
        selectedRoom = rooms.find(r => r.id === roomId);
      document.querySelectorAll('.room-item').forEach(el => el.classList.remove('active'));
    const activeItem = document.querySelector(`.room-item[data-room-id="${roomId}"]`);
    if (activeItem) activeItem.classList.add('active');

    document.getElementById('activeRoomName').textContent = selectedRoom.name;
    const leaveBtn = document.getElementById('leaveBtn'), isJoined = myJoinedRoomIds.includes(roomId);
    leaveBtn.style.display = isJoined ? 'block' : 'none';

    const view = document.getElementById('chatView');
    view.innerHTML = '';
      (sampleMessages[roomId] || []).forEach(m => {
        const div = document.createElement('div');
    div.className = `msg ${m.me ? 'me' : 'other'}`;
    div.innerHTML = `${m.me ? '' : '<div class="author">' + m.author + '</div>'}${m.text}`;
    view.appendChild(div);
      });

    const input = document.getElementById('chatInput'), send = document.getElementById('sendBtn');
    input.disabled = send.disabled = !isJoined;
    input.value = '';
    view.scrollTop = view.scrollHeight;
    }

    function leaveRoom() {
      if (!selectedRoom || !confirm(`"${selectedRoom.name}" 채팅방에서 나가시겠습니까?`)) return;

    const index = myJoinedRoomIds.indexOf(selectedRoom.id);
      if (index > -1) myJoinedRoomIds.splice(index, 1);

    selectedRoom = null;
    document.getElementById('activeRoomName').textContent = '채팅방을 선택하세요';
    document.getElementById('leaveBtn').style.display = 'none';
    document.getElementById('chatView').innerHTML = '';
    document.getElementById('chatInput').disabled = true;
    document.getElementById('sendBtn').disabled = true;

    renderRoomLists();
    alert('채팅방에서 나갔습니다.');
    }

    document.getElementById('sendBtn').addEventListener('click', sendMessage);
    document.getElementById('chatInput').addEventListener('keydown', e => {
      if (e.key === 'Enter' && !e.shiftKey) {e.preventDefault(); sendMessage() }
    });

    function sendMessage() {
      if (!selectedRoom || !myJoinedRoomIds.includes(selectedRoom.id)) return;
    const input = document.getElementById('chatInput'), text = input.value.trim();
    if (!text) return;
    const view = document.getElementById('chatView'), div = document.createElement('div');
    div.className = 'msg me';
    div.textContent = text;
    view.appendChild(div);
    input.value = '';
    view.scrollTop = view.scrollHeight;
    }

    function openJoinModal(roomId) {
        pendingJoinRoom = rooms.find(r => r.id === roomId);
    document.querySelector('.modal-room-name').textContent = pendingJoinRoom.name;
    document.querySelector('.modal-room-desc').textContent = pendingJoinRoom.desc;
    document.getElementById('joinModal').classList.add('active');
    }

    function closeModal() {
        pendingJoinRoom = null;
    document.getElementById('joinModal').classList.remove('active');
    }

    function confirmJoin() {
      if (!pendingJoinRoom) return;
    if (!myJoinedRoomIds.includes(pendingJoinRoom.id)) myJoinedRoomIds.push(pendingJoinRoom.id);
    closeModal();
    renderRoomLists();
    openRoom(pendingJoinRoom.id);
    }

    // 채팅방 생성 모달
    function openCreateModal() {
        document.getElementById('newRoomName').value = '';
    document.getElementById('newRoomDesc').value = '';
    document.getElementById('createModal').classList.add('active');
    }

    function closeCreateModal() {
        document.getElementById('createModal').classList.remove('active');
    }

    function confirmCreate() {
      const name = document.getElementById('newRoomName').value.trim();
    const desc = document.getElementById('newRoomDesc').value.trim();

    if (!name) {
        alert('채팅방 이름을 입력해주세요.');
    return;
      }
    if (!desc) {
        alert('채팅방 설명을 입력해주세요.');
    return;
      }

    // 새 채팅방 생성
    const newRoom = {
        id: nextRoomId++,
    name: name,
    desc: desc,
    members: 1
      };

    rooms.push(newRoom);
    myJoinedRoomIds.push(newRoom.id);
    sampleMessages[newRoom.id] = [];

    closeCreateModal();
    renderRoomLists();
    openRoom(newRoom.id);

    alert('채팅방이 생성되었습니다!');
    }

    function renderUsersFromState(onlineUsers = [], offlineUsers = []) {
      const online = document.getElementById('onlineList');
    const offline = document.getElementById('offlineList');
    online.innerHTML = '';
    offline.innerHTML = '';

      const makeRow = (u) => {
        const row = document.createElement('div');
    row.className = 'user-item';
    // 백엔드가 id, name, nickname 등 어떤 키를 주든 name 필드가 없으면 fallback
    const displayName = u.name || u.nickname || u.username || '사용자';
    const isOnline = !!u.online;
    row.innerHTML = `
    <span class="dot ${isOnline ? 'online' : 'offline'}"></span>
    <div class="user-name">${displayName}</div>
    <span class="pill">${isOnline ? '온라인' : '오프라인'}</span>
    `;
    return row;
      };

      onlineUsers.forEach(u => online.appendChild(makeRow({...u, online: true })));
      offlineUsers.forEach(u => offline.appendChild(makeRow({...u, online: false })));
    }
    // ---- 온라인/오프라인 상태 가져오기 ----
    // 계약(예시):
    // GET /api/presence -> {online: [{id, name, ...}], offline: [{id, name, ...}] }
    //
    // 실시간(SSE):
    // GET /api/presence/stream -> text/event-stream
    //   message.data: {online: [...], offline: [...] }

    const presenceState = {online: [], offline: [] };
    let presencePollTimer = null;
    let presenceEventSource = null;

    async function fetchPresenceOnce() {
      try {
        const res = await fetch('/api/presence', {
        method: 'GET',
    credentials: 'include', // 세션/쿠키 기반이면 유지
    headers: {'Accept': 'application/json' }
        });
    if (!res.ok) throw new Error('presence fetch failed');
    const data = await res.json();

    // 백엔드가 online/offline을 하나의 배열로 주는 경우 대응
    if (Array.isArray(data)) {
          const online = data.filter(u => u.online);
          const offline = data.filter(u => !u.online);
    presenceState.online = online;
    presenceState.offline = offline;
        } else {
        presenceState.online = data.online || [];
    presenceState.offline = data.offline || [];
        }
    renderUsersFromState(presenceState.online, presenceState.offline);
      } catch (e) {
        console.error('[presence] fetch error:', e);
      }
    }

    function startPresencePolling(intervalMs = 10000) {
        stopPresencePolling();
    fetchPresenceOnce(); // 즉시 1회
    presencePollTimer = setInterval(fetchPresenceOnce, intervalMs);
    }

    function stopPresencePolling() {
      if (presencePollTimer) {
        clearInterval(presencePollTimer);
    presencePollTimer = null;
      }
    }

    function startPresenceSSE() {
        // 기존 EventSource 종료
        stopPresenceSSE();

    // CORS/인증(쿠키) 이슈가 있으면 /sse/presence 같이 같은 오리진 경로로 매핑 권장
    presenceEventSource = new EventSource('/api/presence/stream', {withCredentials: true });

      presenceEventSource.onmessage = (evt) => {
        try {
          const payload = JSON.parse(evt.data);
    const online = payload.online || [];
    const offline = payload.offline || [];
    presenceState.online = online;
    presenceState.offline = offline;
    renderUsersFromState(online, offline);
        } catch (e) {
        console.error('[presence] SSE parse error:', e);
        }
      };

      presenceEventSource.onerror = (err) => {
        console.warn('[presence] SSE error, fallback to polling:', err);
    // SSE 끊기면 폴백
    stopPresenceSSE();
    startPresencePolling(10000);
      };
    }

    function stopPresenceSSE() {
      if (presenceEventSource) {
        presenceEventSource.close();
    presenceEventSource = null;
      }
    }

    // 초기 시작: SSE 우선, 미지원/실패 시 폴링 폴백
    function startPresence() {
        fetchPresenceOnce(); // 초기 화면 빨리 채우기
    if ('EventSource' in window) {
        startPresenceSSE();
      } else {
        startPresencePolling(10000);
      }
    }

    // 페이지 로드 시 시작
    document.addEventListener('DOMContentLoaded', startPresence);