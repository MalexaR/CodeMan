/**
 * CodeMan – Motor del Desafío Final
 * Variante de maze-game.js para el modo DESAFIO.
 * Diferencias clave:
 *  - Canvas id: "desafio-canvas", contenedor clase "desafio-container"
 *  - Preguntas de /api/game/questions/desafio (todas las categorías mezcladas)
 *  - Temporizador descendente 30:00 → naranja a 5min → rojo+parpadeo a 1min → derrota a 0
 *  - Paleta dorada/ámbar en lugar de azul
 *  - ESC muestra diálogo de confirmación antes de abandonar
 *  - Al terminar redirige a /resultado/{partidaId} en lugar de mostrar modal
 */
(() => {
  'use strict';

  // ── Constantes ───────────────────────────────────────────────────────────────
  const TILE = 28;
  const COLS = 21, ROWS = 21;
  const WALL = 1, DOT = 2, QUESTION = 3, EMPTY = 0;

  const PAC_SPEED   = 150;
  const GHOST_SPEED = 115;

  const TIEMPO_INICIAL = 1800; // 30 minutos en segundos

  // ── Mapa ─────────────────────────────────────────────────────────────────────
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

  // ── DOM ───────────────────────────────────────────────────────────────────────
  const canvas = document.getElementById('desafio-canvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  const gameContainer = document.querySelector('.desafio-container');
  if (!gameContainer) return;
  const $id = id => document.getElementById(id);

  // ── Utilidades de grid ────────────────────────────────────────────────────────
  const tcx = col => col * TILE + TILE / 2;
  const tcy = row => row * TILE + TILE / 2;
  const nearCol = px => Math.round((px - TILE / 2) / TILE);
  const nearRow = py => Math.round((py - TILE / 2) / TILE);
  const walkable = (col, row) =>
    col >= 0 && col < COLS && row >= 0 && row < ROWS && map[row][col] !== WALL;

  // ── Estado del mapa ───────────────────────────────────────────────────────────
  let map = BASE_MAP.map(r => [...r]);
  const collected = new Set();

  // ── Pac-Man ───────────────────────────────────────────────────────────────────
  let pac = {
    px:  tcx(1), py:  tcy(1),
    dx:  1,      dy:  0,
    ndx: 1,      ndy: 0,
    targetCol: 2, targetRow: 1,
    speed: PAC_SPEED,
    stopped: false,
    mouth: 0, mouthDir: 1
  };

  // ── Fantasmas ────────────────────────────────────────────────────────────────
  const ALL_DIRS = [
    { dx: 1, dy: 0 }, { dx: -1, dy: 0 },
    { dx: 0, dy: 1 }, { dx: 0, dy: -1 }
  ];

  function makeGhost(col, row, dx, dy, color, speed) {
    return {
      px: tcx(col), py: tcy(row),
      dx, dy,
      targetCol: col + dx, targetRow: row + dy,
      color, speed
    };
  }

  let ghosts = [
    makeGhost(9,  9, -1, 0, '#b45309', GHOST_SPEED),
    makeGhost(11, 9,  1, 0, '#92400e', GHOST_SPEED)
  ];

  // ── Estado general ────────────────────────────────────────────────────────────
  let stats      = { vidas: 3, puntos: 0, tiempo: TIEMPO_INICIAL, errores: 0 };
  let state      = 'playing';
  let partidaId  = null;
  let preguntas  = [];
  let currentQ   = null;
  let qTimer     = 20;
  let qInterval  = null;
  let lastTime   = 0;
  let rafId      = null;
  let protection = 0;
  let confirmandoAbandon = false;

  // ── API ───────────────────────────────────────────────────────────────────────
  const api = async (url, method = 'GET', body = null) => {
    const opts = { method, headers: { 'Content-Type': 'application/json' } };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(url, opts);
    if (!res.ok) throw new Error(`API ${method} ${url} → ${res.status}`);
    return res.json();
  };

  async function init() {
    preguntas = await api('/api/game/questions/desafio');
    const start = await api('/api/game/start', 'POST', { categoria: 'DESAFIO', modoJuego: 'DESAFIO' });
    partidaId = start.partidaId;
    lastTime = performance.now();
    rafId = requestAnimationFrame(loop);
  }

  // ── Lógica de Pac-Man ─────────────────────────────────────────────────────────
  function pacArrive(col, row) {
    pac.px = tcx(col);
    pac.py = tcy(row);

    if (walkable(col + pac.ndx, row + pac.ndy)) {
      pac.dx = pac.ndx;
      pac.dy = pac.ndy;
      pac.targetCol = col + pac.dx;
      pac.targetRow = row + pac.dy;
      pac.stopped = false;
      return;
    }

    if (walkable(col + pac.dx, row + pac.dy)) {
      pac.targetCol = col + pac.dx;
      pac.targetRow = row + pac.dy;
      pac.stopped = false;
      return;
    }

    pac.stopped = true;
    pac.targetCol = col;
    pac.targetRow = row;
  }

  function updatePac(dt) {
    pac.mouth += dt * 8 * pac.mouthDir;
    if (pac.mouth >= 1 || pac.mouth <= 0) pac.mouthDir *= -1;

    if (pac.stopped) {
      const col = pac.targetCol, row = pac.targetRow;
      if (walkable(col + pac.ndx, row + pac.ndy)) {
        pac.dx = pac.ndx;
        pac.dy = pac.ndy;
        pac.targetCol = col + pac.dx;
        pac.targetRow = row + pac.dy;
        pac.stopped = false;
      } else {
        return;
      }
    }

    const step = pac.speed * dt;
    const tx = tcx(pac.targetCol), ty = tcy(pac.targetRow);
    const dist = Math.abs(pac.px - tx) + Math.abs(pac.py - ty);

    if (dist <= step) {
      const remainder = step - dist;
      pacArrive(pac.targetCol, pac.targetRow);
      if (!pac.stopped) {
        pac.px += pac.dx * remainder;
        pac.py += pac.dy * remainder;
      }
    } else {
      pac.px += pac.dx * step;
      pac.py += pac.dy * step;
    }

    collectItem();
  }

  // ── Lógica de fantasmas ───────────────────────────────────────────────────────
  function ghostArrive(g, col, row) {
    g.px = tcx(col);
    g.py = tcy(row);

    const valid = ALL_DIRS.filter(d =>
      !(d.dx === -g.dx && d.dy === -g.dy) &&
      walkable(col + d.dx, row + d.dy)
    );

    if (valid.length === 0) {
      g.dx = -g.dx;
      g.dy = -g.dy;
    } else {
      const pc = nearCol(pac.px), pr = nearRow(pac.py);
      valid.sort((a, b) =>
        (Math.abs(col + a.dx - pc) + Math.abs(row + a.dy - pr)) -
        (Math.abs(col + b.dx - pc) + Math.abs(row + b.dy - pr))
      );
      const chosen = (valid.length > 1 && Math.random() >= 0.65)
        ? valid[Math.floor(Math.random() * valid.length)]
        : valid[0];
      g.dx = chosen.dx;
      g.dy = chosen.dy;
    }

    g.targetCol = col + g.dx;
    g.targetRow = row + g.dy;
  }

  function updateGhost(g, dt) {
    const step = g.speed * dt;
    const tx = tcx(g.targetCol), ty = tcy(g.targetRow);
    const dist = Math.abs(g.px - tx) + Math.abs(g.py - ty);

    if (dist <= step) {
      const remainder = step - dist;
      ghostArrive(g, g.targetCol, g.targetRow);
      g.px += g.dx * remainder;
      g.py += g.dy * remainder;
    } else {
      g.px += g.dx * step;
      g.py += g.dy * step;
    }
  }

  // ── Recolección de ítems ──────────────────────────────────────────────────────
  function collectItem() {
    const col = nearCol(pac.px), row = nearRow(pac.py);
    if (col < 0 || col >= COLS || row < 0 || row >= ROWS) return;
    const key = `${row},${col}`;
    if (collected.has(key)) return;
    if (map[row][col] === DOT) {
      collected.add(key);
      map[row][col] = EMPTY;
      stats.puntos += 10;
    } else if (map[row][col] === QUESTION) {
      collected.add(key);
      map[row][col] = EMPTY;
      activateQuestion();
    }
  }

  // ── Colisiones & victoria ─────────────────────────────────────────────────────
  function checkCollisions() {
    if (protection > 0) return;
    for (const g of ghosts) {
      if (Math.hypot(pac.px - g.px, pac.py - g.py) < TILE * 0.78) {
        stats.vidas--;
        protection = 2.5;
        api('/api/game/lives', 'POST', { partidaId, vidas: stats.vidas }).catch(() => {});
        resetPac();
        if (stats.vidas <= 0) { endGame(false); return; }
        break;
      }
    }
  }

  function resetPac() {
    pac.px = tcx(1); pac.py = tcy(1);
    pac.dx = 1; pac.dy = 0;
    pac.ndx = 1; pac.ndy = 0;
    pac.targetCol = 2; pac.targetRow = 1;
    pac.stopped = false;
  }

  function checkWin() {
    if (!map.some(r => r.some(t => t === DOT || t === QUESTION))) endGame(true);
  }

  // ── Renderizado ───────────────────────────────────────────────────────────────
  function draw() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = '#1a0f00';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS; c++) {
        const t = map[r][c];
        const x = c * TILE, y = r * TILE;
        if (t === WALL) {
          const grd = ctx.createLinearGradient(x, y, x + TILE, y + TILE);
          grd.addColorStop(0, '#d97706');
          grd.addColorStop(1, '#92400e');
          ctx.fillStyle = grd;
          ctx.fillRect(x + 2, y + 2, TILE - 4, TILE - 4);
          ctx.strokeStyle = 'rgba(251,191,36,0.35)';
          ctx.strokeRect(x + 2, y + 2, TILE - 4, TILE - 4);
        } else if (t === DOT) {
          ctx.fillStyle = '#fb923c';
          ctx.beginPath();
          ctx.arc(x + TILE / 2, y + TILE / 2, 3, 0, Math.PI * 2);
          ctx.fill();
        } else if (t === QUESTION) {
          ctx.fillStyle = '#fbbf24';
          ctx.beginPath();
          ctx.arc(x + TILE / 2, y + TILE / 2, 8, 0, Math.PI * 2);
          ctx.fill();
          ctx.fillStyle = '#1a0f00';
          ctx.font = 'bold 14px monospace';
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle';
          ctx.fillText('?', x + TILE / 2, y + TILE / 2 + 1);
        }
      }
    }

    if (state === 'paused') {
      ctx.fillStyle = 'rgba(26,15,0,0.80)';
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      ctx.fillStyle = '#fbbf24';
      ctx.font = 'bold 38px monospace';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText('PAUSA', canvas.width / 2, canvas.height / 2);
      return;
    }

    drawPac();
    ghosts.forEach(g => drawGhost(g));
  }

  function drawPac() {
    const openAngle = pac.mouth * 0.38 + 0.05;
    let rot = 0;
    if      (pac.dx === -1) rot = Math.PI;
    else if (pac.dy === -1) rot = -Math.PI / 2;
    else if (pac.dy ===  1) rot =  Math.PI / 2;

    ctx.save();
    ctx.translate(pac.px, pac.py);
    ctx.rotate(rot);

    ctx.fillStyle = '#facc15';
    ctx.beginPath();
    ctx.moveTo(0, 0);
    ctx.arc(0, 0, TILE * 0.42, openAngle, Math.PI * 2 - openAngle);
    ctx.closePath();
    ctx.fill();

    ctx.fillStyle = '#1a0f00';
    ctx.beginPath();
    ctx.arc(3, -8, 2.5, 0, Math.PI * 2);
    ctx.fill();

    ctx.restore();
  }

  function drawGhost(g) {
    const cx = g.px, cy = g.py;
    const r = TILE * 0.38;

    ctx.fillStyle = g.color;
    ctx.beginPath();
    ctx.arc(cx, cy - 2, r, Math.PI, 0);
    ctx.lineTo(cx + r,       cy + r * 0.9);
    ctx.lineTo(cx + r * 0.5, cy + r * 0.6);
    ctx.lineTo(cx,           cy + r * 0.9);
    ctx.lineTo(cx - r * 0.5, cy + r * 0.6);
    ctx.lineTo(cx - r,       cy + r * 0.9);
    ctx.closePath();
    ctx.fill();

    ctx.fillStyle = '#fff';
    ctx.beginPath(); ctx.arc(cx - 5, cy - 3, 3.5, 0, Math.PI * 2); ctx.fill();
    ctx.beginPath(); ctx.arc(cx + 5, cy - 3, 3.5, 0, Math.PI * 2); ctx.fill();
    ctx.fillStyle = '#1a0f00';
    ctx.beginPath(); ctx.arc(cx - 4, cy - 3, 1.5, 0, Math.PI * 2); ctx.fill();
    ctx.beginPath(); ctx.arc(cx + 6, cy - 3, 1.5, 0, Math.PI * 2); ctx.fill();
  }

  // ── HUD ───────────────────────────────────────────────────────────────────────
  function renderHud(now) {
    $id('vidas').textContent   = stats.vidas;
    $id('puntos').textContent  = stats.puntos;
    $id('errores').textContent = stats.errores;

    const t = Math.max(0, Math.floor(stats.tiempo));
    const mm = Math.floor(t / 60).toString().padStart(2, '0');
    const ss = (t % 60).toString().padStart(2, '0');
    const tiempoEl = $id('tiempo');
    tiempoEl.textContent = `${mm}:${ss}`;

    if (stats.tiempo < 60) {
      tiempoEl.style.color = '#ef4444';
      tiempoEl.style.opacity = Math.floor(now / 500) % 2 === 0 ? '1' : '0.2';
    } else if (stats.tiempo < 300) {
      tiempoEl.style.color = '#f97316';
      tiempoEl.style.opacity = '1';
    } else {
      tiempoEl.style.color = '';
      tiempoEl.style.opacity = '1';
    }
  }

  // ── Preguntas ─────────────────────────────────────────────────────────────────
  function activateQuestion() {
    if (!preguntas.length) return;
    state = 'question';
    currentQ = preguntas.shift();
    qTimer = 20;
    $id('questionTimer').textContent = qTimer;
    $id('questionText').textContent  = currentQ.enunciado;

    const box = $id('answers');
    box.innerHTML = '';
    [['A', currentQ.opcionA], ['B', currentQ.opcionB],
     ['C', currentQ.opcionC], ['D', currentQ.opcionD]].forEach(([l, t]) => {
      const btn = document.createElement('button');
      btn.className = 'answer-btn';
      btn.textContent = `${l}. ${t}`;
      btn.onclick = () => handleAnswer(l);
      box.appendChild(btn);
    });

    $id('questionModal').classList.remove('hidden');
    clearInterval(qInterval);
    qInterval = setInterval(() => {
      qTimer--;
      $id('questionTimer').textContent = qTimer;
      if (qTimer <= 0) handleAnswer('TIMEOUT');
    }, 1000);
  }

  async function handleAnswer(opcion) {
    clearInterval(qInterval);
    $id('questionModal').classList.add('hidden');

    const correct = opcion !== 'TIMEOUT' && opcion === String(currentQ.respuestaCorrecta);
    if (correct) {
      stats.puntos += 100;
    } else {
      stats.errores += 1;
      ghosts.forEach(g => { g.speed = Math.min(g.speed * 1.08, GHOST_SPEED * 1.5); });
    }

    try {
      const res = await api('/api/game/answer', 'POST', {
        partidaId,
        preguntaId: currentQ.id,
        opcionElegida: opcion,
        tiempoRespuesta: 20 - qTimer,
        esCorrecta: correct
      });
      if (res.fantasmaExtraActivo && ghosts.length < 3) spawnExtraGhost();
    } catch (e) {
      console.error('Error al enviar respuesta:', e);
    }

    state = 'playing';
  }

  function spawnExtraGhost() {
    ghosts.push(makeGhost(10, 10, 0, -1, '#7c2d12', GHOST_SPEED * 0.9));
  }

  // ── Fin de partida ────────────────────────────────────────────────────────────
  async function endGame(won) {
    if (state === 'won' || state === 'gameover') return;
    state = won ? 'won' : 'gameover';
    clearInterval(qInterval);
    if (rafId) { cancelAnimationFrame(rafId); rafId = null; }

    try {
      await api('/api/game/finish', 'POST', { partidaId, puntuacion: stats.puntos, vidas: stats.vidas });
    } catch (e) {
      console.error('Error al finalizar partida:', e);
    }

    try {
      const resultado = await api(`/api/game/${partidaId}/resultado`);
      const pct = resultado.porcentajeDesempeno || 0;
      window.scormBridge.setScore(pct);
      if (won && pct >= 60) window.scormBridge.setCompletion();
      window.scormBridge.finishScorm();
    } catch (e) { /* modo standalone sin SCORM */ }

    window.location.href = `/resultado/${partidaId}`;
  }

  async function abandonarDesafio() {
    if (state === 'won' || state === 'gameover') return;
    state = 'gameover';
    clearInterval(qInterval);
    if (rafId) { cancelAnimationFrame(rafId); rafId = null; }

    try {
      await api('/api/game/finish', 'POST', { partidaId, puntuacion: stats.puntos, vidas: stats.vidas });
    } catch (e) {
      console.error('Error al abandonar partida:', e);
    }

    try { window.scormBridge.finishScorm(); } catch (e) { /* standalone */ }

    window.location.href = '/jugar';
  }

  // ── Game loop ─────────────────────────────────────────────────────────────────
  function loop(now) {
    if (state === 'won' || state === 'gameover') return;

    const dt = Math.min((now - lastTime) / 1000, 0.05);
    lastTime = now;

    if (state === 'playing') {
      stats.tiempo -= dt;
      protection = Math.max(0, protection - dt);

      if (stats.tiempo <= 0) {
        stats.tiempo = 0;
        renderHud(now);
        draw();
        endGame(false);
        return;
      }

      updatePac(dt);
      ghosts.forEach(g => updateGhost(g, dt));
      checkCollisions();
      checkWin();
      renderHud(now);
    }

    draw();
    rafId = requestAnimationFrame(loop);
  }

  // ── Teclado ───────────────────────────────────────────────────────────────────
  window.addEventListener('keydown', e => {
    const k = e.key.toLowerCase();

    if      (k === 'arrowup'    || k === 'w') { pac.ndx =  0; pac.ndy = -1; }
    else if (k === 'arrowdown'  || k === 's') { pac.ndx =  0; pac.ndy =  1; }
    else if (k === 'arrowleft'  || k === 'a') { pac.ndx = -1; pac.ndy =  0; }
    else if (k === 'arrowright' || k === 'd') { pac.ndx =  1; pac.ndy =  0; }
    else if (k === 'p') {
      if (state === 'playing')     { state = 'paused'; draw(); }
      else if (state === 'paused') { state = 'playing'; }
    }
    else if (k === 'escape') {
      if (confirmandoAbandon) return;
      if (state !== 'playing' && state !== 'paused') return;

      const prevState = state;
      state = 'paused';
      draw();
      confirmandoAbandon = true;

      const confirmar = window.confirm(
        '¿Abandonar el Desafío Final?\n\n' +
        'Si sales ahora, la partida no se marcará como completada y no recibirás el logro de desafío.'
      );
      confirmandoAbandon = false;

      if (confirmar) {
        abandonarDesafio();
      } else {
        state = prevState;
      }
    }

    if (['arrowup','arrowdown','arrowleft','arrowright'].includes(k)) e.preventDefault();
  });

  // ── Controles táctiles ────────────────────────────────────────────────────────
  let touchStart = null;
  canvas.addEventListener('touchstart', e => { touchStart = e.touches[0]; }, { passive: true });
  canvas.addEventListener('touchend', e => {
    if (!touchStart) return;
    const dx = e.changedTouches[0].clientX - touchStart.clientX;
    const dy = e.changedTouches[0].clientY - touchStart.clientY;
    if (Math.abs(dx) > Math.abs(dy)) { pac.ndx = dx > 0 ? 1 : -1; pac.ndy =  0; }
    else                             { pac.ndx =  0; pac.ndy = dy > 0 ? 1 : -1; }
    touchStart = null;
  }, { passive: true });

  // ── Arranque ──────────────────────────────────────────────────────────────────
  init().catch(err => {
    console.error('Error al iniciar el desafío:', err);
    alert('No se pudo iniciar el desafío. Revisa la consola del navegador.');
  });
})();
