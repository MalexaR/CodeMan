# CodeMan — Guía de instalación SCORM 1.2

## Qué es este paquete

CodeMan puede empaquetarse como una actividad **SCORM 1.2** compatible con Moodle y cualquier LMS que soporte este estándar. El paquete ZIP contiene un `imsmanifest.xml` que describe la estructura del curso y un launcher HTML mínimo; la app en sí sigue corriendo en tu servidor Spring Boot habitual.

---

## Prerrequisitos

| Requisito | Detalle |
|-----------|---------|
| CodeMan desplegado | La app Spring Boot debe estar accesible públicamente (ej. `https://codeman.ejemplo.com`) |
| Bash + zip | Para ejecutar el script de empaquetado en Linux/macOS. En Windows usa WSL o Git Bash |
| Moodle 3.x o 4.x | Cualquier versión con el módulo SCORM activado (viene por defecto) |

---

## 1. Generar el paquete SCORM

```bash
# Desde la raíz del proyecto
CODEMAN_URL=https://codeman.ejemplo.com bash build-scorm.sh
```

El script crea `dist/CodeMan-SCORM.zip`. Si no defines `CODEMAN_URL`, te pedirá la URL interactivamente.

**¿Qué contiene el ZIP?**

```
CodeMan-SCORM.zip
├── imsmanifest.xml      ← descriptor del paquete (SCORM 1.2)
├── index.html           ← launcher que redirige al servidor
└── js/
    └── scorm-bridge.js  ← bridge de comunicación SCORM
```

---

## 2. Importar en Moodle

1. Entra como profesor/administrador al curso.
2. Activa el modo edición.
3. **Añadir una actividad o recurso** → selecciona **SCORM**.
4. En "Fichero del paquete", sube `dist/CodeMan-SCORM.zip`.
5. Moodle leerá el `imsmanifest.xml` automáticamente.
6. Configura la calificación: **Calificación → Método de calificación → Puntuación más alta**.
7. Guarda y muestra.

---

## 3. Flujo de datos SCORM

Cuando un estudiante abre la actividad SCORM desde Moodle:

1. Moodle abre `index.html` en un iframe e inyecta la API SCORM en `window.API`.
2. `scorm-bridge.js` detecta `window.parent.API` y llama a `LMSInitialize("")`.
3. El launcher redirige al servidor CodeMan (`https://codeman.ejemplo.com/jugar`).
4. El estudiante juega. Al terminar una partida, el juego llama:
   - `scormBridge.setScore(porcentaje)` → `cmi.core.score.raw`
   - `scormBridge.setCompletion()` → `cmi.core.lesson_status = "completed"` (si ganó)
   - `scormBridge.finishScorm()` → `LMSCommit + LMSFinish`
5. Moodle registra la puntuación y el estado en su gradebook.

### Datos reportados

| Campo SCORM | Valor |
|-------------|-------|
| `cmi.core.score.raw` | Porcentaje de aciertos de la partida (0–100) |
| `cmi.core.score.min` | 0 |
| `cmi.core.score.max` | 100 |
| `cmi.core.lesson_status` | `completed` si ganó, `incomplete` en caso contrario |
| `cmi.core.lesson_location` | No se usa en esta versión |

### Umbral de aprobación

En el `imsmanifest.xml`, `<adlcp:masteryscore>60</adlcp:masteryscore>` indica a Moodle que se necesita al menos 60% para aprobar. Moodle convierte automáticamente el estado en "passed" o "failed" según este umbral.

---

## 4. Modo standalone (sin LMS)

Si `scorm-bridge.js` no detecta `window.API` (ejecución directa sin Moodle), todos los métodos del bridge son no-ops silenciosos. La app funciona exactamente igual que siempre.

---

## 5. Limitaciones conocidas

- **Autenticación**: CodeMan usa su propio sistema de login. Moodle no gestiona las credenciales de los estudiantes dentro de la app. El estudiante debe ingresar sus credenciales de CodeMan al abrir la actividad.
- **Sesión SCORM por partida**: Cada vez que el estudiante completa una partida, se llama a `LMSFinish`. Si el estudiante juega varias partidas en la misma sesión Moodle, Moodle registra la última puntuación reportada (o la más alta, según la configuración de calificación elegida).
- **CORS**: Si Moodle está en un dominio diferente al de CodeMan, asegúrate de que el servidor CodeMan permita el origen de Moodle en sus cabeceras CORS (Spring Security lo maneja via `SecurityConfig`).
- **HTTPS obligatorio**: Los navegadores modernos bloquean contenido mixto. Tanto Moodle como CodeMan deben servirse por HTTPS.

---

## 6. Actualizar el paquete

Si cambias la URL del servidor CodeMan, regenera el ZIP:

```bash
CODEMAN_URL=https://nuevo-servidor.com bash build-scorm.sh
```

Luego actualiza el archivo del paquete en Moodle: **Editar actividad SCORM → Fichero del paquete → Eliminar → Subir nuevo ZIP**.

---

## 7. Versión SCORM

Este paquete implementa **SCORM 1.2** (CAM 1.3 / RTE 1.2), la versión más ampliamente soportada por Moodle (3.x y 4.x) y LMS comerciales. No usa SCORM 2004.
