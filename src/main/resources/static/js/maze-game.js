// Movimiento centro-a-centro: el bicho viaja de tile a tile completos.
// El teclado solo guarda la direccion que se quiere; el cambio real ocurre cuando llega al siguiente tile.
(() => {
  'use strict';

  const TILE = 28;
  const COLS = 21, ROWS = 21;
  const WALL = 1, DOT = 2, QUESTION = 3, EMPTY = 0;

  const PAC_SPEED   = 150; // px/s
  const GHOST_SPEED = 115;

  // 1 = pared, 2 = punto, 3 = pregunta
  const BASE_MAP = [
    [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1],
    [1,2,2,2,2,2,2,2,2,1,1,1,2,2,2,2,2,2,2,2,1],
    [1,3,1,1,1,2,1,1,2,1,1,1,2,1,1,1,2,1,1,3,1],
    [1,2,1,2,2,2,2,1,2,2,2,2,2,1,2,2,2,2,1,2,1],
    [1,2,1,2,1,1,1,1,1,1,2,1,1,1,1,1,1,2,1,2,1],
    [1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1],
    [1,1,1,1,2,1,1,1,2,1,1,1,2,1,1,1,2,1,1,1,1],
    [1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1],
    [1,2,1,1,1,2,1,1,1,1,2,1,1,1,1,2,1,1,1,2,1],
    [1,2,2,2,1,2,2,2,2,2,2,2,2,2,2,2,1,2,2,2,1],
    [1,1,1,2,1,1,1,1,2,1,1,1,2,1,1,1,1,2,1,1,1],
    [1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1],
    [1,2,1,1,1,2,1,1,1,1,2,1,1,1,1,2,1,1,1,2,1],
    [1,2,2,2,1,2,2,2,2,2,2,2,2,2,2,2,1,2,2,2,1],
    [1,1,1,2,1,1,1,1,2,1,1,1,2,1,1,1,1,2,1,1,1],
    [1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1],
    [1,2,1,1,1,2,1,1,1,1,2,1,1,1,1,2,1,1,1,2,1],
    [1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1],
    [1,3,1,1,1,2,1,1,1,1,2,1,1,1,1,2,1,1,1,3,1],
    [1,2,2,2,2,2,2,2,2,1,1,1,2,2,2,2,2,2,2,2,1],
    [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
  ];

  const canvas = document.getElementById('mazeCanvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  const gameContainer = document.querySelector('.game-container');
  if (!gameContainer) return;
  const categoria = gameContainer.dataset.categoria;
  const $id = id => document.getElementById(id);

  const tcx = col => col * TILE + TILE / 2;
  const tcy = row => row * TILE + TILE / 2;
  const nearCol = px => Math.round((px - TILE / 2) / TILE);
  const nearRow = py => Math.round((py - TILE / 2) / TILE);
  const walkable = (col, row) =>
    col >= 0 && col < COLS && row >= 0 && row < ROWS && map[row][col] !== WALL;

  // copia del mapa base para no mutar el original cuando se comen los puntos
  let map = BASE_MAP.map(r => [...r]);
  const collected = new Set();

  let pac = {
    px:  tcx(1), py:  tcy(1),
    dx:  1,      dy:  0,
    ndx: 1,      ndy: 0,
    targetCol: 2, targetRow: 1,
    speed: PAC_SPEED,
    stopped: false,
    mouth: 0, mouthDir: 1
  };

  const ALL_DIRS = [
    { dx: 1, dy: 0 }, { dx: -1, dy: 0 },
    { dx: 0, dy: 1 }, { dx: 0, dy: -1 }
  ];

  function makeGhost(col, row, dx, dy, color, speed) {
    return { px: tcx(col), py: tcy(row), dx, dy,
             targetCol: col + dx, targetRow: row + dy, color, speed };
  }

  let ghosts = [
    makeGhost(9,  9, -1, 0, '#ef4444', GHOST_SPEED),
    makeGhost(11, 9,  1, 0, '#f97316', GHOST_SPEED)
  ];

  let stats      = { vidas: 3, puntos: 0, tiempo: 0, errores: 0 };
  let state      = 'playing';
  let partidaId  = null;
  let preguntas  = [];
  let eventos    = [];
  let eventosActivados = 0;
  let currentQ   = null;
  let currentEvento = null;
  let qTimer     = 20;
  let qInterval  = null;
  let lastTime   = 0;
  let rafId      = null;
  let protection = 0;
  let slowModeTimer = 0;
  let savedGhostSpeeds = null;
  let frightTimer   = 0;   // fantasmas comestibles (azules)
  let pacBoostTimer = 0;   // pac acelerado
  let pacSlowTimer  = 0;   // pac lento
  let feedbackMsg   = '';
  let feedbackTimer = 0;
  let feedbackGood  = true;

  const api = async (url, method = 'GET', body = null) => {
    const opts = { method, headers: { 'Content-Type': 'application/json' } };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(url, opts);
    if (!res.ok) throw new Error(`API ${method} ${url} → ${res.status}`);
    return res.json();
  };

  async function init() {
    preguntas = await api(`/api/game/questions?categoria=${categoria}`);
    try { eventos = await api(`/api/game/eventos?categoria=${categoria}`); } catch(_) { eventos = []; }
    const start = await api('/api/game/start', 'POST', { categoria, modoJuego: 'PRACTICA' });
    partidaId = start.partidaId;
    lastTime = performance.now();
    rafId = requestAnimationFrame(loop);
  }

  function pacArrive(col, row) {
    pac.px = tcx(col);
    pac.py = tcy(row);
    if (walkable(col + pac.ndx, row + pac.ndy)) {
      pac.dx = pac.ndx; pac.dy = pac.ndy;
      pac.targetCol = col + pac.dx; pac.targetRow = row + pac.dy;
      pac.stopped = false; return;
    }
    if (walkable(col + pac.dx, row + pac.dy)) {
      pac.targetCol = col + pac.dx; pac.targetRow = row + pac.dy;
      pac.stopped = false; return;
    }
    pac.stopped = true; pac.targetCol = col; pac.targetRow = row;
  }

  function updatePac(dt) {
    pac.mouth += dt * 8 * pac.mouthDir;
    // acotamos a [0,1]: si se pasa, ademas de invertir la direccion fijamos el
    // valor al limite. Sin esto, con un dt grande la boca se pasaba por debajo de 0
    // y openAngle quedaba negativo -> el arco se cerraba y la boca "desaparecia".
    if (pac.mouth >= 1)      { pac.mouth = 1; pac.mouthDir = -1; }
    else if (pac.mouth <= 0) { pac.mouth = 0; pac.mouthDir =  1; }
    if (pac.stopped) {
      const col = pac.targetCol, row = pac.targetRow;
      if (walkable(col + pac.ndx, row + pac.ndy)) {
        pac.dx = pac.ndx; pac.dy = pac.ndy;
        pac.targetCol = col + pac.dx; pac.targetRow = row + pac.dy;
        pac.stopped = false;
      } else { return; }
    }
    const step = pac.speed * dt;
    const tx = tcx(pac.targetCol), ty = tcy(pac.targetRow);
    const dist = Math.abs(pac.px - tx) + Math.abs(pac.py - ty);
    if (dist <= step) {
      const remainder = step - dist;
      pacArrive(pac.targetCol, pac.targetRow);
      if (!pac.stopped) { pac.px += pac.dx * remainder; pac.py += pac.dy * remainder; }
    } else { pac.px += pac.dx * step; pac.py += pac.dy * step; }
    collectItem();
  }

  function ghostArrive(g, col, row) {
    g.px = tcx(col); g.py = tcy(row);
    const valid = ALL_DIRS.filter(d =>
      !(d.dx === -g.dx && d.dy === -g.dy) && walkable(col + d.dx, row + d.dy));
    if (valid.length === 0) { g.dx = -g.dx; g.dy = -g.dy; }
    else {
      const pc = nearCol(pac.px), pr = nearRow(pac.py);
      valid.sort((a, b) =>
        (Math.abs(col + a.dx - pc) + Math.abs(row + a.dy - pr)) -
        (Math.abs(col + b.dx - pc) + Math.abs(row + b.dy - pr)));
      const chosen = (valid.length > 1 && Math.random() >= 0.65)
        ? valid[Math.floor(Math.random() * valid.length)] : valid[0];
      g.dx = chosen.dx; g.dy = chosen.dy;
    }
    g.targetCol = col + g.dx; g.targetRow = row + g.dy;
  }

  function updateGhost(g, dt) {
    const step = g.speed * dt;
    const tx = tcx(g.targetCol), ty = tcy(g.targetRow);
    const dist = Math.abs(g.px - tx) + Math.abs(g.py - ty);
    if (dist <= step) {
      const remainder = step - dist;
      ghostArrive(g, g.targetCol, g.targetRow);
      g.px += g.dx * remainder; g.py += g.dy * remainder;
    } else { g.px += g.dx * step; g.py += g.dy * step; }
  }

  function collectItem() {
    const col = nearCol(pac.px), row = nearRow(pac.py);
    if (col < 0 || col >= COLS || row < 0 || row >= ROWS) return;
    const key = `${row},${col}`;
    if (collected.has(key)) return;
    if (map[row][col] === DOT) {
      collected.add(key); map[row][col] = EMPTY; stats.puntos += 10;
    } else if (map[row][col] === QUESTION) {
      collected.add(key); map[row][col] = EMPTY;
      if (eventosActivados < 2 && eventos.length > 0 && Math.random() < 0.2) {
        activateEvento();
      } else {
        activateQuestion();
      }
    }
  }

  function checkCollisions() {
    if (protection > 0) return;
    for (let i = ghosts.length - 1; i >= 0; i--) {
      const g = ghosts[i];
      if (Math.hypot(pac.px - g.px, pac.py - g.py) < TILE * 0.78) {
        if (frightTimer > 0) {
          stats.puntos += 200;
          ghosts.splice(i, 1);
        } else {
          stats.vidas--; protection = 2.5;
          api('/api/game/lives', 'POST', { partidaId, vidas: stats.vidas }).catch(() => {});
          resetPac();
          if (stats.vidas <= 0) { endGame(false); return; }
          break;
        }
      }
    }
  }

  function resetPac() {
    pac.px = tcx(1); pac.py = tcy(1);
    pac.dx = 1; pac.dy = 0; pac.ndx = 1; pac.ndy = 0;
    pac.targetCol = 2; pac.targetRow = 1; pac.stopped = false;
  }

  function checkWin() {
    if (!map.some(r => r.some(t => t === DOT || t === QUESTION))) endGame(true);
  }

  function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = '#020617';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS; c++) {
        const t = map[r][c];
        const x = c * TILE, y = r * TILE;
        if (t === WALL) {
          const grd = ctx.createLinearGradient(x, y, x + TILE, y + TILE);
          grd.addColorStop(0, '#0ea5e9'); grd.addColorStop(1, '#1d4ed8');
          ctx.fillStyle = grd;
          ctx.fillRect(x + 2, y + 2, TILE - 4, TILE - 4);
          ctx.strokeStyle = 'rgba(191,219,254,.35)';
          ctx.strokeRect(x + 2, y + 2, TILE - 4, TILE - 4);
        } else if (t === DOT) {
          ctx.fillStyle = '#f8fafc';
          ctx.beginPath(); ctx.arc(x + TILE/2, y + TILE/2, 3, 0, Math.PI*2); ctx.fill();
        } else if (t === QUESTION) {
          ctx.fillStyle = '#22c55e';
          ctx.beginPath(); ctx.arc(x + TILE/2, y + TILE/2, 8, 0, Math.PI*2); ctx.fill();
          ctx.fillStyle = '#052e16';
          ctx.font = 'bold 14px monospace'; ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
          ctx.fillText('?', x + TILE/2, y + TILE/2 + 1);
        }
      }
    }
    if (state === 'paused') {
      ctx.fillStyle = 'rgba(2,6,23,0.75)';
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      ctx.fillStyle = '#22c55e'; ctx.font = 'bold 38px monospace';
      ctx.textAlign = 'center'; ctx.textBaseline = 'middle';
      ctx.fillText('PAUSA', canvas.width/2, canvas.height/2); return;
    }
    drawPac(); ghosts.forEach(g => drawGhost(g));
    if (feedbackTimer > 0) {
      ctx.save();
      ctx.globalAlpha = Math.min(1, feedbackTimer);
      ctx.font = 'bold 15px monospace';
      ctx.textAlign = 'center'; ctx.textBaseline = 'top';
      ctx.lineWidth = 3; ctx.strokeStyle = '#020617';
      ctx.fillStyle = feedbackGood ? '#22c55e' : '#ef4444';
      ctx.strokeText(feedbackMsg, canvas.width / 2, 6);
      ctx.fillText(feedbackMsg, canvas.width / 2, 6);
      ctx.restore();
    }
  }

  function drawPac() {
    const openAngle = pac.mouth * 0.38 + 0.05;
    let rot = 0;
    if (pac.dx === -1) rot = Math.PI;
    else if (pac.dy === -1) rot = -Math.PI/2;
    else if (pac.dy === 1) rot = Math.PI/2;
    ctx.save(); ctx.translate(pac.px, pac.py); ctx.rotate(rot);
    ctx.fillStyle = '#facc15';
    ctx.beginPath(); ctx.moveTo(0, 0);
    ctx.arc(0, 0, TILE * 0.42, openAngle, Math.PI*2 - openAngle);
    ctx.closePath(); ctx.fill();
    ctx.fillStyle = '#111827';
    ctx.beginPath(); ctx.arc(3, -8, 2.5, 0, Math.PI*2); ctx.fill();
    ctx.restore();
  }

  function drawGhost(g) {
    const cx = g.px, cy = g.py, r = TILE * 0.38;
    const scared = frightTimer > 0;
    // parpadea en los últimos 2 segundos
    const blink = scared && frightTimer < 2 && Math.floor(frightTimer * 5) % 2 === 0;
    ctx.fillStyle = scared ? (blink ? '#e2e8f0' : '#3b82f6') : g.color;
    ctx.beginPath(); ctx.arc(cx, cy - 2, r, Math.PI, 0);
    ctx.lineTo(cx + r, cy + r*0.9); ctx.lineTo(cx + r*0.5, cy + r*0.6);
    ctx.lineTo(cx, cy + r*0.9); ctx.lineTo(cx - r*0.5, cy + r*0.6);
    ctx.lineTo(cx - r, cy + r*0.9); ctx.closePath(); ctx.fill();
    if (!scared) {
      ctx.fillStyle = '#fff';
      ctx.beginPath(); ctx.arc(cx - 5, cy - 3, 3.5, 0, Math.PI*2); ctx.fill();
      ctx.beginPath(); ctx.arc(cx + 5, cy - 3, 3.5, 0, Math.PI*2); ctx.fill();
      ctx.fillStyle = '#111827';
      ctx.beginPath(); ctx.arc(cx - 4, cy - 3, 1.5, 0, Math.PI*2); ctx.fill();
      ctx.beginPath(); ctx.arc(cx + 6, cy - 3, 1.5, 0, Math.PI*2); ctx.fill();
    } else {
      // modo asustado: ojitos chicos y boca zigzag
      ctx.fillStyle = '#1e3a8a';
      ctx.beginPath(); ctx.arc(cx - 4, cy - 3, 2, 0, Math.PI*2); ctx.fill();
      ctx.beginPath(); ctx.arc(cx + 4, cy - 3, 2, 0, Math.PI*2); ctx.fill();
      ctx.strokeStyle = '#1e3a8a'; ctx.lineWidth = 1.5;
      ctx.beginPath();
      ctx.moveTo(cx - 6, cy + 3);
      ctx.lineTo(cx - 3, cy + 1); ctx.lineTo(cx, cy + 3);
      ctx.lineTo(cx + 3, cy + 1); ctx.lineTo(cx + 6, cy + 3);
      ctx.stroke();
    }
  }

  function renderHud() {
    $id('vidas').textContent   = stats.vidas;
    $id('puntos').textContent  = stats.puntos;
    $id('tiempo').textContent  = Math.floor(stats.tiempo);
    $id('errores').textContent = stats.errores;
  }

  const modalTitle = () => document.querySelector('#questionModal .modal-head h2');

  function activateQuestion() {
    if (!preguntas.length) return;
    state = 'question';
    currentQ = preguntas.shift();
    if (modalTitle()) modalTitle().textContent = 'Pregunta de código';
    qTimer = 20;
    $id('questionTimer').textContent = qTimer;
    $id('questionText').textContent  = currentQ.enunciado;
    const box = $id('answers');
    box.innerHTML = '';
    [['A', currentQ.opcionA], ['B', currentQ.opcionB],
     ['C', currentQ.opcionC], ['D', currentQ.opcionD]].forEach(([l, t]) => {
      const btn = document.createElement('button');
      btn.className = 'answer-btn'; btn.textContent = `${l}. ${t}`;
      btn.onclick = () => handleAnswer(l); box.appendChild(btn);
    });
    $id('questionModal').classList.remove('hidden');
    clearInterval(qInterval);
    qInterval = setInterval(() => {
      qTimer--; $id('questionTimer').textContent = qTimer;
      if (qTimer <= 0) handleAnswer('TIMEOUT');
    }, 1000);
  }

  async function handleAnswer(opcion) {
    clearInterval(qInterval);
    $id('questionModal').classList.add('hidden');
    const correct = opcion !== 'TIMEOUT' && opcion === String(currentQ.respuestaCorrecta);
    if (correct) {
      stats.puntos += 100;
      if (Math.random() < 0.5) activateFrightMode();
      else                     activatePacBoost();
    } else {
      stats.errores += 1;
      if (Math.random() < 0.5 && ghosts.length < 4) { spawnExtraGhost(); showFeedback('Aparece un fantasma extra', false); }
      else                                            activatePacSlow();
    }
    try {
      await api('/api/game/answer', 'POST', {
        partidaId, preguntaId: currentQ.id,
        opcionElegida: opcion, tiempoRespuesta: 20 - qTimer, esCorrecta: correct
      });
    } catch(e) { console.error('Error al enviar respuesta:', e); }
    state = 'playing';
  }

  function activateFrightMode() {
    frightTimer = 6;
    showFeedback('Power-up: come fantasmas (6s)', true);
  }

  function activatePacBoost() {
    pac.speed = PAC_SPEED * 1.8;
    pacBoostTimer = 5;
    showFeedback('Power-up: velocidad (5s)', true);
  }

  function activatePacSlow() {
    pac.speed = PAC_SPEED * 0.45;
    pacSlowTimer = 3;
    showFeedback('Penalización: velocidad reducida (3s)', false);
  }

  function showFeedback(msg, good) {
    feedbackMsg = msg; feedbackGood = good; feedbackTimer = 2.5;
  }

  function activateEvento() {
    if (!eventos.length) { activateQuestion(); return; }
    state = 'question';
    currentEvento = eventos.shift();
    eventosActivados++;
    if (modalTitle()) modalTitle().textContent = 'Decisión crítica';
    qTimer = 30;
    $id('questionTimer').textContent = qTimer;
    $id('questionText').textContent = currentEvento.escenario;
    const box = $id('answers');
    box.innerHTML = '';
    [['A', currentEvento.opcionA], ['B', currentEvento.opcionB], ['C', currentEvento.opcionC]]
        .filter(([, t]) => t)
        .forEach(([l, t]) => {
          const btn = document.createElement('button');
          btn.className = 'answer-btn'; btn.textContent = `${l}. ${t}`;
          btn.onclick = () => handleEventoAnswer(l); box.appendChild(btn);
        });
    $id('questionModal').classList.remove('hidden');
    clearInterval(qInterval);
    qInterval = setInterval(() => {
      qTimer--; $id('questionTimer').textContent = qTimer;
      if (qTimer <= 0) handleEventoAnswer('TIMEOUT');
    }, 1000);
  }

  async function handleEventoAnswer(opcion) {
    clearInterval(qInterval);
    $id('questionModal').classList.add('hidden');
    try {
      const res = await api('/api/game/evento', 'POST', {
        partidaId, eventoId: currentEvento.id, opcionElegida: opcion
      });
      if (res.esCorrecta) {
        if (res.powerUpAplicado === 'RALENTIZAR_FANTASMAS') {
          savedGhostSpeeds = ghosts.map(g => g.speed);
          ghosts.forEach(g => { g.speed = g.speed * 0.5; });
          slowModeTimer = 10;
        } else if (res.powerUpAplicado === 'VIDA_ADICIONAL') {
          stats.vidas++;
          api('/api/game/lives', 'POST', { partidaId, vidas: stats.vidas }).catch(() => {});
        }
      } else {
        stats.vidas--;
        api('/api/game/lives', 'POST', { partidaId, vidas: stats.vidas }).catch(() => {});
        if (stats.vidas <= 0) { endGame(false); return; }
      }
    } catch(e) { console.error('Error al resolver evento:', e); }
    state = 'playing';
  }

  function spawnExtraGhost() {
    ghosts.push(makeGhost(10, 10, 0, -1, '#ec4899', GHOST_SPEED * 0.9));
  }

  async function endGame(won) {
    if (state === 'won' || state === 'gameover') return;
    state = won ? 'won' : 'gameover';
    clearInterval(qInterval);
    if (rafId) { cancelAnimationFrame(rafId); rafId = null; }
    try {
      await api('/api/game/finish', 'POST', { partidaId, puntuacion: stats.puntos, vidas: stats.vidas });
    } catch(e) { console.error('Error al finalizar partida:', e); }
    try {
      const resultado = await api(`/api/game/${partidaId}/resultado`);
      const pct = resultado.porcentajeDesempeno || 0;
      window.scormBridge.setScore(pct);
      if (won) window.scormBridge.setCompletion();
      window.scormBridge.finishScorm();
    } catch(e) { /* corriendo sin LMS, no pasa nada */ }
    window.location.href = `/resultado/${partidaId}`;
  }

  function loop(now) {
    if (state === 'won' || state === 'gameover') return;
    const dt = Math.min((now - lastTime) / 1000, 0.05);
    lastTime = now;
    if (state === 'playing') {
      stats.tiempo += dt; protection = Math.max(0, protection - dt);
      if (slowModeTimer > 0) {
        slowModeTimer = Math.max(0, slowModeTimer - dt);
        if (slowModeTimer === 0 && savedGhostSpeeds) {
          ghosts.forEach((g, i) => { g.speed = savedGhostSpeeds[i]; });
          savedGhostSpeeds = null;
        }
      }
      if (frightTimer > 0) frightTimer = Math.max(0, frightTimer - dt);
      if (pacBoostTimer > 0) {
        pacBoostTimer = Math.max(0, pacBoostTimer - dt);
        if (pacBoostTimer === 0) pac.speed = PAC_SPEED;
      }
      if (pacSlowTimer > 0) {
        pacSlowTimer = Math.max(0, pacSlowTimer - dt);
        if (pacSlowTimer === 0) pac.speed = PAC_SPEED;
      }
      if (feedbackTimer > 0) feedbackTimer = Math.max(0, feedbackTimer - dt);
      updatePac(dt); ghosts.forEach(g => updateGhost(g, dt));
      checkCollisions(); checkWin(); renderHud();
    }
    draw(); rafId = requestAnimationFrame(loop);
  }

  window.addEventListener('keydown', e => {
    const k = e.key.toLowerCase();
    if      (k === 'arrowup'    || k === 'w') { pac.ndx =  0; pac.ndy = -1; }
    else if (k === 'arrowdown'  || k === 's') { pac.ndx =  0; pac.ndy =  1; }
    else if (k === 'arrowleft'  || k === 'a') { pac.ndx = -1; pac.ndy =  0; }
    else if (k === 'arrowright' || k === 'd') { pac.ndx =  1; pac.ndy =  0; }
    else if (k === 'p' || k === 'escape') {
      if (state === 'playing')     { state = 'paused'; draw(); }
      else if (state === 'paused') { state = 'playing'; }
    }
    if (['arrowup','arrowdown','arrowleft','arrowright'].includes(k)) e.preventDefault();
  });

  // swipe sobre el canvas para movil
  let touchStart = null;
  canvas.addEventListener('touchstart', e => { touchStart = e.touches[0]; }, { passive: true });
  canvas.addEventListener('touchmove',  e => e.preventDefault(), { passive: false });
  canvas.addEventListener('touchend', e => {
    if (!touchStart) return;
    const dx = e.changedTouches[0].clientX - touchStart.clientX;
    const dy = e.changedTouches[0].clientY - touchStart.clientY;
    if (Math.abs(dx) < 8 && Math.abs(dy) < 8) { touchStart = null; return; }
    if (Math.abs(dx) > Math.abs(dy)) { pac.ndx = dx > 0 ? 1 : -1; pac.ndy =  0; }
    else                             { pac.ndx =  0; pac.ndy = dy > 0 ? 1 : -1; }
    touchStart = null;
  }, { passive: true });

  // botones de direccion en pantalla
  const DPAD = {
    'dpad-up':    { ndx:  0, ndy: -1 },
    'dpad-down':  { ndx:  0, ndy:  1 },
    'dpad-left':  { ndx: -1, ndy:  0 },
    'dpad-right': { ndx:  1, ndy:  0 }
  };
  Object.entries(DPAD).forEach(([cls, dir]) => {
    const btn = document.querySelector(`.${cls}`);
    if (!btn) return;
    const move = e => { e.preventDefault(); if (state === 'playing' || state === 'paused') { pac.ndx = dir.ndx; pac.ndy = dir.ndy; } };
    btn.addEventListener('touchstart', move, { passive: false });
    btn.addEventListener('mousedown',  move);
  });

  const pauseBtn = $id('pauseBtn');
  if (pauseBtn) {
    pauseBtn.addEventListener('click', () => {
      if      (state === 'playing') { state = 'paused';  draw(); pauseBtn.textContent = 'Continuar'; }
      else if (state === 'paused')  { state = 'playing';         pauseBtn.textContent = 'Pausa'; }
    });
  }

  init().catch(err => {
    console.error('Error al iniciar el juego:', err);
    alert('No se pudo iniciar el juego. Revisa la consola del navegador.');
  });
})();
