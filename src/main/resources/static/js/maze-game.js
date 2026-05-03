/**
 * CodeMan – Motor del laberinto
 * Movimiento continuo centro-a-centro: cada personaje viaja de un tile center
 * al siguiente. Las decisiones de dirección ocurren exactamente al llegar.
 * El teclado solo cambia la dirección deseada; nunca mueve directamente.
 */
(() => {
  'use strict';

  // ── Constantes ───────────────────────────────────────────────────────────────
  const TILE = 28;                  // píxeles por tile
  const COLS = 21, ROWS = 21;
  const WALL = 1, DOT = 2, QUESTION = 3, EMPTY = 0;

  const PAC_SPEED   = 150;          // px / segundo
  const GHOST_SPEED = 115;          // px / segundo (base)

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
  const canvas = document.getElementById('mazeCanvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  const gameContainer = document.querySelector('.game-container');
  if (!gameContainer) return;
  const categoria = gameContainer.dataset.categoria;
  const $id = id => document.getElementById(id);

  // ── Utilidades de grid ────────────────────────────────────────────────────────
  /** Coordenada X en píxeles del centro del tile (col). */
  const tcx = col => col * TILE + TILE / 2;
  /** Coordenada Y en píxeles del centro del tile (row). */
  const tcy = row => row * TILE + TILE / 2;
  /** Tile column más cercana a la posición px. */
  const nearCol = px => Math.round((px - TILE / 2) / TILE);
  /** Tile row más cercana a la posición py. */
  const nearRow = py => Math.round((py - TILE / 2) / TILE);
  /** ¿Es el tile (col, row) transitable? */
  const walkable = (col, row) =>
    col >= 0 && col < COLS && row >= 0 && row < ROWS && map[row][col] !== WALL;

  // ── Estado del mapa ───────────────────────────────────────────────────────────
  let map = BASE_MAP.map(r => [...r]);
  const collected = new Set();

  // ── Pac-Man ───────────────────────────────────────────────────────────────────
  // El personaje viaja de tile center en tile center.
  // targetCol/Row = próximo tile center al que se dirige.
  // ndx/ndy       = dirección deseada por el jugador (puede cambiar en cualquier momento).
  // dx/dy         = dirección actual de movimiento.
  let pac = {
    px:  tcx(1), py:  tcy(1),   // posición en píxeles
    dx:  1,      dy:  0,        // dirección actual
    ndx: 1,      ndy: 0,        // dirección deseada
    targetCol: 2, targetRow: 1, // primer destino
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
    makeGhost(9,  9, -1, 0, '#ef4444', GHOST_SPEED),
    makeGhost(11, 9,  1, 0, '#f97316', GHOST_SPEED)
  ];

  // ── Estado general ────────────────────────────────────────────────────────────
  let stats      = { vidas: 3, puntos: 0, tiempo: 0, errores: 0 };
  let state      = 'playing';   // 'playing' | 'paused' | 'question' | 'won' | 'gameover'
  let partidaId  = null;
  let preguntas  = [];
  let currentQ   = null;
  let qTimer     = 20;
  let qInterval  = null;
  let lastTime   = 0;
  let rafId      = null;
  let protection = 0;

  // ── API ───────────────────────────────────────────────────────────────────────
  const api = async (url, method = 'GET', body = null) => {
    const opts = { method, headers: { 'Content-Type': 'application/json' } };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(url, opts);
    if (!res.ok) throw new Error(`API ${method} ${url} → ${res.status}`);
    return res.json();
  };

  async function init() {
    preguntas = await api(`/api/game/questions?categoria=${categoria}`);
    const start = await api('/api/game/start', 'POST', { categoria, modoJuego: 'PRACTICA' });
    partidaId = start.partidaId;
    lastTime = performance.now();
    rafId = requestAnimationFrame(loop);
  }

  // ── Lógica de Pac-Man ─────────────────────────────────────────────────────────

  /**
   * Llamado cuando Pac-Man llega al tile (col, row).
   * Decide la próxima dirección y fija el siguiente targetCol/Row.
   */
  function pacArrive(col, row) {
    // Snap exacto al centro para eliminar deriva flotante
    pac.px = tcx(col);
    pac.py = tcy(row);

    // 1. Intentar la dirección deseada primero
    if (walkable(col + pac.ndx, row + pac.ndy)) {
      pac.dx = pac.ndx;
      pac.dy = pac.ndy;
      pac.targetCol = col + pac.dx;
      pac.targetRow = row + pac.dy;
      pac.stopped = false;
      return;
    }

    // 2. Continuar en la dirección actual si es posible
    if (walkable(col + pac.dx, row + pac.dy)) {
      pac.targetCol = col + pac.dx;
      pac.targetRow = row + pac.dy;
      pac.stopped = false;
      return;
    }

    // 3. Pared adelante y no puede girar: se detiene aquí
    pac.stopped = true;
    pac.targetCol = col;
    pac.targetRow = row;
  }

  function updatePac(dt) {
    // Animar boca
    pac.mouth += dt * 8 * pac.mouthDir;
    if (pac.mouth >= 1 || pac.mouth <= 0) pac.mouthDir *= -1;

    // Si está detenido, ver si la dirección deseada ya es válida
    if (pac.stopped) {
      const col = pac.targetCol, row = pac.targetRow;
      if (walkable(col + pac.ndx, row + pac.ndy)) {
        pac.dx = pac.ndx;
        pac.dy = pac.ndy;
        pac.targetCol = col + pac.dx;
        pac.targetRow = row + pac.dy;
        pac.stopped = false;
      } else {
        return; // aún bloqueado
      }
    }

    const step = pac.speed * dt;
    const tx = tcx(pac.targetCol), ty = tcy(pac.targetRow);

    // Distancia al tile destino (solo un eje es no-cero)
    const dist = Math.abs(pac.px - tx) + Math.abs(pac.py - ty);

    if (dist <= step) {
      // Llegó al tile destino: tomar decisión y mover el resto del paso
      const remainder = step - dist;
      pacArrive(pac.targetCol, pac.targetRow);
      if (!pac.stopped) {
        pac.px += pac.dx * remainder;
        pac.py += pac.dy * remainder;
      }
    } else {
      // Avanzar hacia el destino
      pac.px += pac.dx * step;
      pac.py += pac.dy * step;
    }

    collectItem();
  }

  // ── Lógica de fantasmas ───────────────────────────────────────────────────────

  /**
   * Llamado cuando un fantasma llega al tile (col, row).
   * Elige la próxima dirección con IA simple.
   */
  function ghostArrive(g, col, row) {
    g.px = tcx(col);
    g.py = tcy(row);

    // Direcciones válidas: transitables y sin revertir (no U-turn)
    const valid = ALL_DIRS.filter(d =>
      !(d.dx === -g.dx && d.dy === -g.dy) &&
      walkable(col + d.dx, row + d.dy)
    );

    if (valid.length === 0) {
      // Callejón sin salida: dar la vuelta
      g.dx = -g.dx;
      g.dy = -g.dy;
    } else {
      // Perseguir a Pac-Man con 65 % de probabilidad; 35 % aleatorio
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
    ctx.fillStyle = '#020617';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    // Tiles
    for (let r = 0; r < ROWS; r++) {
      for (let c = 0; c < COLS; c++) {
        const t = map[r][c];
        const x = c * TILE, y = r * TILE;
        if (t === WALL) {
          const grd = ctx.createLinearGradient(x, y, x + TILE, y + TILE);
          grd.addColorStop(0, '#0ea5e9');
          grd.addColorStop(1, '#1d4ed8');
          ctx.fillStyle = grd;
          ctx.fillRect(x + 2, y + 2, TILE - 4, TILE - 4);
          ctx.strokeStyle = 'rgba(191,219,254,.35)';
          ctx.strokeRect(x + 2, y + 2, TILE - 4, TILE - 4);
        } else if (t === DOT) {
          ctx.fillStyle = '#f8fafc';
          ctx.beginPath();
          ctx.arc(x + TILE / 2, y + TILE / 2, 3, 0, Math.PI * 2);
          ctx.fill();
        } else if (t === QUESTION) {
          ctx.fillStyle = '#22c55e';
          ctx.beginPath();
          ctx.arc(x + TILE / 2, y + TILE / 2, 8, 0, Math.PI * 2);
          ctx.fill();
          ctx.fillStyle = '#052e16';
          ctx.font = 'bold 14px monospace';
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle';
          ctx.fillText('?', x + TILE / 2, y + TILE / 2 + 1);
        }
      }
    }

    // Overlay de pausa
    if (state === 'paused') {
      ctx.fillStyle = 'rgba(2,6,23,0.75)';
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      ctx.fillStyle = '#22c55e';
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
    // Ángulo de apertura oscila entre 0.05 y 0.43 radianes
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

    // Ojo
    ctx.fillStyle = '#111827';
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

    // Ojos blancos
    ctx.fillStyle = '#fff';
    ctx.beginPath(); ctx.arc(cx - 5, cy - 3, 3.5, 0, Math.PI * 2); ctx.fill();
    ctx.beginPath(); ctx.arc(cx + 5, cy - 3, 3.5, 0, Math.PI * 2); ctx.fill();
    // Pupilas
    ctx.fillStyle = '#111827';
    ctx.beginPath(); ctx.arc(cx - 4, cy - 3, 1.5, 0, Math.PI * 2); ctx.fill();
    ctx.beginPath(); ctx.arc(cx + 6, cy - 3, 1.5, 0, Math.PI * 2); ctx.fill();
  }

  // ── HUD ───────────────────────────────────────────────────────────────────────
  function renderHud() {
    $id('vidas').textContent   = stats.vidas;
    $id('puntos').textContent  = stats.puntos;
    $id('tiempo').textContent  = Math.floor(stats.tiempo);
    $id('errores').textContent = stats.errores;
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
      // Aumentar velocidad de fantasmas con cada error (máx. +50 %)
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
    const col = 10, row = 10;
    ghosts.push(makeGhost(col, row, 0, -1, '#ec4899', GHOST_SPEED * 0.9));
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

    $id('endTitle').textContent   = won ? '¡Laberinto completado!' : 'Fin del juego';
    $id('endMessage').textContent = won
      ? `Puntuación final: ${stats.puntos}. ¡Excelente dominio de patrones!`
      : `Puntuación final: ${stats.puntos}. Revisa la teoría e inténtalo de nuevo.`;
    $id('endModal').classList.remove('hidden');
  }

  // ── Game loop ─────────────────────────────────────────────────────────────────
  function loop(now) {
    if (state === 'won' || state === 'gameover') return;

    // dt acotado a 50 ms para evitar saltos grandes tras foco perdido
    const dt = Math.min((now - lastTime) / 1000, 0.05);
    lastTime = now;

    if (state === 'playing') {
      stats.tiempo += dt;
      protection = Math.max(0, protection - dt);
      updatePac(dt);
      ghosts.forEach(g => updateGhost(g, dt));
      checkCollisions();
      checkWin();
      renderHud();
    }

    draw();
    rafId = requestAnimationFrame(loop);
  }

  // ── Teclado ───────────────────────────────────────────────────────────────────
  // Las teclas SOLO cambian pac.ndx / pac.ndy.
  // Nunca mueven directamente al personaje.
  window.addEventListener('keydown', e => {
    const k = e.key.toLowerCase();

    if      (k === 'arrowup'    || k === 'w') { pac.ndx =  0; pac.ndy = -1; }
    else if (k === 'arrowdown'  || k === 's') { pac.ndx =  0; pac.ndy =  1; }
    else if (k === 'arrowleft'  || k === 'a') { pac.ndx = -1; pac.ndy =  0; }
    else if (k === 'arrowright' || k === 'd') { pac.ndx =  1; pac.ndy =  0; }
    else if (k === 'p' || k === 'escape') {
      if (state === 'playing')       { state = 'paused'; draw(); }
      else if (state === 'paused')   { state = 'playing'; }
    }

    // Evitar scroll de página con las flechas
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
    console.error('Error al iniciar el juego:', err);
    alert('No se pudo iniciar el juego. Revisa la consola del navegador.');
  });
})();
