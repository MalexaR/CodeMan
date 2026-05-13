#!/usr/bin/env bash
# build-scorm.sh — Genera el paquete SCORM 1.2 de CodeMan.
#
# Uso:
#   CODEMAN_URL=https://codeman.example.com bash build-scorm.sh
#
# Si no se pasa CODEMAN_URL, pide la URL interactivamente.

set -euo pipefail

# ── Configuración ─────────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
STATIC_DIR="$SCRIPT_DIR/src/main/resources/static"
MANIFEST_SRC="$STATIC_DIR/imsmanifest.xml"
OUT_DIR="$SCRIPT_DIR/dist"
PACKAGE_DIR="$SCRIPT_DIR/codeman-scorm"
ZIP_NAME="CodeMan-SCORM.zip"

# ── URL del servidor ──────────────────────────────────────────────────────────
if [ -z "${CODEMAN_URL:-}" ]; then
    if [ -t 0 ]; then
        read -rp "URL del servidor CodeMan (ej: https://codeman.example.com): " CODEMAN_URL
    else
        echo "ERROR: Define la variable CODEMAN_URL antes de ejecutar el script."
        echo "  Ejemplo: CODEMAN_URL=https://codeman.example.com bash build-scorm.sh"
        exit 1
    fi
fi
CODEMAN_URL="${CODEMAN_URL%/}"

echo ""
echo "=============================="
echo " CodeMan SCORM Package Builder"
echo "=============================="
echo "  URL del servidor : $CODEMAN_URL"
echo "  Salida           : $OUT_DIR/$ZIP_NAME"
echo ""

# ── Limpiar y preparar directorios ────────────────────────────────────────────
rm -rf "$PACKAGE_DIR"
mkdir -p "$PACKAGE_DIR/js" "$PACKAGE_DIR/css"
mkdir -p "$OUT_DIR"

# ── imsmanifest.xml (con URL real) ────────────────────────────────────────────
sed "s|{{CODEMAN_URL}}|${CODEMAN_URL}|g" "$MANIFEST_SRC" > "$PACKAGE_DIR/imsmanifest.xml"
echo "✓ imsmanifest.xml generado"

# ── Copiar scorm-bridge.js (requerido por el manifest) ───────────────────────
cp "$STATIC_DIR/js/scorm-bridge.js" "$PACKAGE_DIR/js/"
echo "✓ js/scorm-bridge.js copiado"

# ── Copiar CSS (referenciado como asset en el manifest) ──────────────────────
if [ -f "$STATIC_DIR/css/styles.css" ]; then
    cp "$STATIC_DIR/css/styles.css" "$PACKAGE_DIR/css/"
    echo "✓ css/styles.css copiado"
fi

# ── Página de lanzamiento (HTML autocontenido que hace redirect) ──────────────
cat > "$PACKAGE_DIR/index.html" <<HTML
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>CodeMan — Cargando…</title>
    <script src="js/scorm-bridge.js"></script>
    <style>
        body { margin:0; display:flex; align-items:center; justify-content:center;
               min-height:100vh; font-family:sans-serif; background:#07111f; color:#f8fafc; }
        .msg { text-align:center; }
        .msg h2 { margin:0 0 12px; font-size:1.4rem; }
        .msg p  { color:#94a3b8; }
    </style>
</head>
<body>
<div class="msg">
    <h2>⏳ Iniciando CodeMan…</h2>
    <p>Serás redirigido automáticamente.</p>
</div>
<script>
    // El bridge ya llamó initScorm() al cargarse.
    // Redirigir al servidor real donde corre CodeMan.
    window.location.replace('${CODEMAN_URL}/');
</script>
</body>
</html>
HTML
echo "✓ index.html de lanzamiento creado"

# ── Crear ZIP ─────────────────────────────────────────────────────────────────
cd "$PACKAGE_DIR"
zip -r "$OUT_DIR/$ZIP_NAME" . -x "*.DS_Store" -x "__MACOSX/*"
cd "$SCRIPT_DIR"

echo ""
echo "✅ Paquete SCORM generado: dist/$ZIP_NAME"
echo ""
echo "Para importarlo en Moodle:"
echo "  Activar edición → Añadir actividad → SCORM → subir $ZIP_NAME"
echo ""

# Limpiar carpeta temporal
rm -rf "$PACKAGE_DIR"
