/**
 * scorm-bridge.js — Abstracción SCORM 1.2 para CodeMan.
 *
 * Expone window.scormBridge con métodos seguros:
 *   initScorm()            — inicia sesión con el LMS (no-op si no hay API)
 *   setScore(raw)          — reporta puntuación 0-100
 *   setCompletion()        — marca la lección como completada
 *   setLessonLocation(loc) — guarda punto de reanudación
 *   finishScorm()          — confirma y cierra la sesión SCORM
 *   isActive()             — true si la API SCORM está conectada
 *
 * Funciona tanto dentro de un LMS Moodle (iframe con window.parent.API)
 * como en modo standalone (los métodos son no-ops silenciosos).
 */
window.scormBridge = (function () {
    'use strict';

    // ── Descubrimiento de la API SCORM 1.2 ──────────────────────────────────────
    // Moodle inyecta la API en el frame padre. Subimos hasta 7 niveles.
    function findAPI(win) {
        let tries = 0;
        while (win.API == null && win.parent != null && win.parent !== win) {
            if (++tries > 7) return null;
            win = win.parent;
        }
        return win.API || null;
    }

    // ── Estado interno ───────────────────────────────────────────────────────────
    const SESSION_KEY = 'scorm_initialized';
    let api         = null;
    let initialized = false;

    // ── Helpers internos ─────────────────────────────────────────────────────────
    function lmsSet(key, value) {
        if (!initialized || !api) return false;
        try {
            const r = api.LMSSetValue(key, String(value));
            return r === 'true' || r === true;
        } catch (e) {
            console.warn('[SCORM] SetValue error (' + key + '):', e);
            return false;
        }
    }

    function lmsCommit() {
        if (!initialized || !api) return false;
        try {
            const r = api.LMSCommit('');
            return r === 'true' || r === true;
        } catch (e) {
            return false;
        }
    }

    // ── API pública ──────────────────────────────────────────────────────────────
    return {
        initScorm: function () {
            // Evitar inicializar más de una vez por carga de página
            if (initialized) return true;

            api = findAPI(window);
            if (!api) {
                console.info('[SCORM] No API encontrada — modo standalone');
                return false;
            }
            try {
                const result = api.LMSInitialize('');
                initialized = result === 'true' || result === true;
                if (initialized) {
                    console.info('[SCORM] Sesión iniciada correctamente');
                    sessionStorage.setItem(SESSION_KEY, '1');
                } else {
                    console.warn('[SCORM] LMSInitialize retornó false');
                }
                return initialized;
            } catch (e) {
                console.warn('[SCORM] Error al inicializar:', e);
                return false;
            }
        },

        setScore: function (raw) {
            const score = Math.max(0, Math.min(100, Math.round(raw)));
            lmsSet('cmi.core.score.raw', score);
            lmsSet('cmi.core.score.min', 0);
            lmsSet('cmi.core.score.max', 100);
            lmsCommit();
        },

        setCompletion: function () {
            lmsSet('cmi.core.lesson_status', 'completed');
            lmsCommit();
        },

        setLessonLocation: function (loc) {
            lmsSet('cmi.core.lesson_location', String(loc));
            lmsCommit();
        },

        finishScorm: function () {
            if (!initialized || !api) return false;
            try {
                lmsCommit();
                const r = api.LMSFinish('');
                initialized = false;
                sessionStorage.removeItem(SESSION_KEY);
                console.info('[SCORM] Sesión finalizada');
                return r === 'true' || r === true;
            } catch (e) {
                initialized = false;
                console.warn('[SCORM] Error al finalizar:', e);
                return false;
            }
        },

        isActive: function () {
            return initialized;
        }
    };
})();

// Auto-inicializar en cuanto el script carga
window.scormBridge.initScorm();
