(() => {
  'use strict';

  const KEY = 'codeman_a11y';

  // ── Persist helpers ──────────────────────────────────────────────────────────
  function load() {
    try { return JSON.parse(localStorage.getItem(KEY)) || {}; }
    catch { return {}; }
  }
  function save(patch) {
    const p = load();
    localStorage.setItem(KEY, JSON.stringify(Object.assign(p, patch)));
  }

  // ── Apply preference to <html> element ───────────────────────────────────────
  function applyTheme(t) {
    document.documentElement.setAttribute('data-theme', t || 'dark');
  }
  function applyHC(on) {
    if (on) document.documentElement.setAttribute('data-hc', '1');
    else    document.documentElement.removeAttribute('data-hc');
  }
  function applyDyslexia(on) {
    if (on) document.documentElement.setAttribute('data-dyslexia', '1');
    else    document.documentElement.removeAttribute('data-dyslexia');
  }
  function applySize(pct) {
    document.documentElement.style.fontSize = pct + '%';
  }

  // ── Apply saved prefs immediately (before panel render) ──────────────────────
  const prefs = load();
  applyTheme(prefs.theme || 'dark');
  applyHC(!!prefs.hc);
  applyDyslexia(!!prefs.dyslexia);
  applySize(prefs.size || 100);

  // ── Inject panel HTML ────────────────────────────────────────────────────────
  function buildPanel() {
    const wrapper = document.createElement('div');
    wrapper.id = 'a11y-wrapper';
    wrapper.innerHTML = `
<div id="a11y-panel" hidden role="dialog" aria-modal="false" aria-label="Panel de accesibilidad">
  <div class="a11y-header">
    <h2 class="a11y-title">Accesibilidad</h2>
    <button class="a11y-close" id="a11y-close" aria-label="Cerrar panel de accesibilidad">✕</button>
  </div>
  <div class="a11y-section">
    <span class="a11y-lbl" id="lbl-theme">Modo de color</span>
    <div class="a11y-seg" role="group" aria-labelledby="lbl-theme">
      <button class="a11y-seg-btn" data-theme="auto"  aria-pressed="false">Auto</button>
      <button class="a11y-seg-btn" data-theme="light" aria-pressed="false">Claro</button>
      <button class="a11y-seg-btn" data-theme="dark"  aria-pressed="false">Oscuro</button>
    </div>
  </div>
  <div class="a11y-section">
    <div class="a11y-row">
      <span class="a11y-lbl" id="lbl-hc">Alto contraste</span>
      <button class="a11y-toggle" id="a11y-hc" role="switch" aria-checked="false" aria-labelledby="lbl-hc">
        <span class="a11y-thumb"></span>
      </button>
    </div>
  </div>
  <div class="a11y-section">
    <div class="a11y-row">
      <span class="a11y-lbl" id="lbl-dyslexia">Fuente para dislexia</span>
      <button class="a11y-toggle" id="a11y-dyslexia" role="switch" aria-checked="false" aria-labelledby="lbl-dyslexia">
        <span class="a11y-thumb"></span>
      </button>
    </div>
  </div>
  <div class="a11y-section">
    <div class="a11y-slider-row">
      <span class="a11y-lbl">Tamaño de texto</span>
      <span class="a11y-size-val" id="a11y-size-val">100%</span>
    </div>
    <input type="range" id="a11y-size" min="80" max="200" step="5" value="100"
           aria-label="Tamaño de texto, de 80 a 200 por ciento"
           aria-valuemin="80" aria-valuemax="200" aria-valuenow="100">
  </div>
  <div class="a11y-section">
    <button id="a11y-tts" type="button">&#128266; Leer página en voz alta</button>
  </div>
</div>
<button id="a11y-btn" type="button"
        aria-label="Abrir panel de accesibilidad"
        aria-expanded="false"
        aria-controls="a11y-panel">&#9855;</button>`;
    document.body.appendChild(wrapper);
  }

  // ── Wire interactions ────────────────────────────────────────────────────────
  function init() {
    buildPanel();

    const p      = load();
    const panel  = document.getElementById('a11y-panel');
    const btn    = document.getElementById('a11y-btn');
    const close  = document.getElementById('a11y-close');
    const hcBtn  = document.getElementById('a11y-hc');
    const dyxBtn = document.getElementById('a11y-dyslexia');
    const slider = document.getElementById('a11y-size');
    const sizeVl = document.getElementById('a11y-size-val');
    const ttsBtn = document.getElementById('a11y-tts');
    const segs   = document.querySelectorAll('.a11y-seg-btn');

    // Restore UI state
    const theme = p.theme || 'dark';
    segs.forEach(b => {
      const on = b.dataset.theme === theme;
      b.classList.toggle('on', on);
      b.setAttribute('aria-pressed', String(on));
    });
    if (p.hc)      { hcBtn.classList.add('on');  hcBtn.setAttribute('aria-checked', 'true'); }
    if (p.dyslexia){ dyxBtn.classList.add('on'); dyxBtn.setAttribute('aria-checked', 'true'); }
    const size = p.size || 100;
    slider.value = size;
    sizeVl.textContent = size + '%';
    slider.setAttribute('aria-valuenow', size);

    // Open / close panel
    function openPanel()  { panel.hidden = false; btn.setAttribute('aria-expanded', 'true');  close.focus(); }
    function closePanel() { panel.hidden = true;  btn.setAttribute('aria-expanded', 'false'); btn.focus(); }

    btn.addEventListener('click', () => panel.hidden ? openPanel() : closePanel());
    close.addEventListener('click', closePanel);

    // Escape closes only when focus is inside the panel — avoids capturing game keys
    panel.addEventListener('keydown', e => {
      if (e.key === 'Escape') {
        e.stopPropagation();
        closePanel();
      }
    });

    // Theme selector
    segs.forEach(b => {
      b.addEventListener('click', () => {
        const t = b.dataset.theme;
        applyTheme(t);
        segs.forEach(x => {
          const on = x.dataset.theme === t;
          x.classList.toggle('on', on);
          x.setAttribute('aria-pressed', String(on));
        });
        save({ theme: t });
      });
    });

    // HC toggle
    hcBtn.addEventListener('click', () => {
      const on = !hcBtn.classList.contains('on');
      hcBtn.classList.toggle('on', on);
      hcBtn.setAttribute('aria-checked', String(on));
      applyHC(on);
      save({ hc: on });
    });

    // Dyslexia toggle
    dyxBtn.addEventListener('click', () => {
      const on = !dyxBtn.classList.contains('on');
      dyxBtn.classList.toggle('on', on);
      dyxBtn.setAttribute('aria-checked', String(on));
      applyDyslexia(on);
      save({ dyslexia: on });
    });

    // Font size slider
    slider.addEventListener('input', () => {
      const v = +slider.value;
      sizeVl.textContent = v + '%';
      slider.setAttribute('aria-valuenow', v);
      applySize(v);
      save({ size: v });
    });

    // TTS — Web Speech API
    ttsBtn.addEventListener('click', () => {
      if (!window.speechSynthesis) return;
      if (window.speechSynthesis.speaking) {
        window.speechSynthesis.cancel();
        ttsBtn.classList.remove('speaking');
        ttsBtn.innerHTML = '&#128266; Leer página en voz alta';
        return;
      }
      const text = (document.getElementById('main-content') || document.body).innerText;
      const utt = new SpeechSynthesisUtterance(text);
      utt.lang = 'es-ES';
      utt.rate = 0.95;
      utt.onend = utt.onerror = () => {
        ttsBtn.classList.remove('speaking');
        ttsBtn.innerHTML = '&#128266; Leer página en voz alta';
      };
      window.speechSynthesis.speak(utt);
      ttsBtn.classList.add('speaking');
      ttsBtn.innerHTML = '&#9209; Detener lectura';
    });

    // Stop TTS on page unload
    window.addEventListener('beforeunload', () => {
      if (window.speechSynthesis) window.speechSynthesis.cancel();
    });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
