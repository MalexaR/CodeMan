# Refactor CodeMan: eliminar lógica de `Ficha` y `FichaRevisada`

## Contexto del proyecto

CodeMan es una aplicación educativa en **Java 17 + Spring Boot 3 + Thymeleaf + Spring Security + Spring Data JPA**. La base de datos es **PostgreSQL en Supabase**. El ORM es JPA/Hibernate con anotaciones.

El proyecto usa inyección de dependencias por constructor (no `@Autowired`). Los templates usan Thymeleaf con el namespace `xmlns:th`.

---

## Problema a resolver

El modelo tenía una arquitectura redundante con tres tablas para gestionar el contenido teórico:

```
patrones → fichas → fichas_revisadas
```

- `Ficha` era un duplicado innecesario de `Patron`: tenía exactamente los mismos campos de contenido (`contenido`, `diagrama_uml_url`, `codigo_ejemplo`) que ya existían en `Patron`.
- `FichaRevisada` referenciaba a `Ficha`, obligando a una cadena de tres saltos para saber si un usuario había estudiado un patrón.
- El campo `revisada BOOLEAN = true` en `FichaRevisada` era redundante: la existencia de la fila ya implica que fue revisada.

**La solución correcta** es:
- Eliminar completamente las clases `Ficha` y `FichaRevisada`.
- Crear una nueva entidad `PatronRevisado` que relaciona directamente `Usuario` ↔ `Patron`.
- Adaptar todos los servicios, repositorios, controladores y templates que usaban `Ficha`/`FichaRevisada`.

---

## Esquema de base de datos nuevo (ya aplicado en PostgreSQL)

La BD **ya fue migrada**. Las tablas `fichas` y `fichas_revisadas` ya no existen. El esquema actual relevante es:

```sql
-- patrones contiene todo el contenido, sin tabla fichas separada
CREATE TABLE patrones (
    id               BIGSERIAL      PRIMARY KEY,
    nombre           VARCHAR(100)   NOT NULL UNIQUE,
    categoria        categoria_enum NOT NULL,  -- 'CREACIONAL','ESTRUCTURAL','COMPORTAMIENTO','DESAFIO'
    descripcion      TEXT,
    proposito        TEXT,
    problema_resuelto TEXT,
    cuando_usar      TEXT,
    estructura       TEXT,
    diagrama_uml_url VARCHAR(500),
    codigo_ejemplo   TEXT,
    activo           BOOLEAN        NOT NULL DEFAULT TRUE,
    creado_por_id    BIGINT         REFERENCES usuarios(id) ON DELETE SET NULL,
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

-- Reemplaza fichas_revisadas: relación directa usuario→patron
CREATE TABLE patrones_revisados (
    id              BIGSERIAL   PRIMARY KEY,
    usuario_id      BIGINT      NOT NULL REFERENCES usuarios(id)  ON DELETE CASCADE,
    patron_id       BIGINT      NOT NULL REFERENCES patrones(id)  ON DELETE CASCADE,
    fecha_revision  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_patron_revisado UNIQUE (usuario_id, patron_id)
);

-- progreso usa patrones_revisados_cache en lugar de fichas_revisadas
CREATE TABLE progreso (
    id                       BIGSERIAL   PRIMARY KEY,
    usuario_id               BIGINT      NOT NULL UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
    patrones_revisados_cache INT         NOT NULL DEFAULT 0,
    partidas_jugadas_cache   INT         NOT NULL DEFAULT 0,
    aciertos_cache           INT         NOT NULL DEFAULT 0,
    errores_cache            INT         NOT NULL DEFAULT 0,
    desafio_final_completado BOOLEAN     NOT NULL DEFAULT FALSE,
    ultimo_acceso            TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

## Estado actual del código (lo que existe y hay que cambiar)

### Clases modelo actuales

**`Patron.java`** — entidad JPA mapeada a `patrones`. Tiene los campos:
`id`, `nombre`, `categoria` (enum `Categoria`), `descripcion`, `proposito`, `problema`, `cuandoUsar`, `estructura`, `uml`, `codigoEjemplo`, `problemaResuelto`, `cuandoUsarlo`, `codigoEjemploJava`.

> Nótese que tiene campos duplicados (`problema`/`problemaResuelto`, `cuandoUsar`/`cuandoUsarlo`, `codigoEjemplo`/`codigoEjemploJava`) que también deben fusionarse.

**`Ficha.java`** — entidad JPA mapeada a `fichas`. Tiene: `id`, `patron` (OneToOne), `contenido`, `diagramaUmlUrl`, `ejemploCodigo`, `fechaCreacion`, `creadaPor`. **ELIMINAR.**

**`FichaRevisada.java`** — entidad JPA mapeada a `fichas_revisadas`. Tiene: `id`, `usuario` (ManyToOne), `ficha` (ManyToOne), `revisada` (boolean), `fechaRevision`. **ELIMINAR.**

**`Usuario.java`** — entidad JPA mapeada a `usuarios`. Campos: `id`, `email`, `passwordHash`, `name`, `role` (enum `Rol`), `createdAt`, `updatedAt`. No cambia estructuralmente.

**`Progreso.java`** — entidad JPA mapeada a `progreso`. Campos actuales: `id`, `usuario` (OneToOne), `fichasRevisadas`, `partidasJugadas`, `aciertos`, `errores`, `desafioFinalCompletado`, `ultimoAcceso`, `totalFichas` (@Transient). Tiene métodos calculados `getPorcentajeFichas()` y `getPorcentajeAciertos()`.

### Repositorios actuales

**`FichaRepository`** — métodos: `findByPatronId(Long)`, `findByPatronCategoria(Categoria)`. **ELIMINAR.**

**`FichaRevisadaRepository`** — métodos: `findByUsuarioId(Long)`, `existsByUsuarioIdAndFichaId(Long, Long)`, `countByUsuarioId(Long)`, `findByUsuarioAndFicha(Usuario, Ficha)`, `existsByUsuarioAndFicha(Usuario, Ficha)`, `countByUsuario(Usuario)`. **ELIMINAR.**

**`PatronRepository`** — métodos actuales: `findByCategoria(Categoria)`. Necesita métodos adicionales.

### Servicios actuales

**`FichaService.java`** — métodos todos en TODO. **ELIMINAR.**

**`PatronService.java`** — inyecta `PatronRepository`, `FichaRepository`, `FichaRevisadaRepository`. Métodos:
- `listar()` → `patronRepository.findAll()`
- `porCategoria(Categoria)` → `patronRepository.findByCategoria(categoria)`
- `buscar(Long)` → busca por id o lanza excepción
- `total()` → **actualmente hace `fichaRepository.count()`** ← bug: debe contar patrones
- `revisadasPor(Usuario)` → `fichaRevisadaRepository.countByUsuario(usuario)` ← debe cambiar
- `estaRevisado(Usuario, Patron)` → busca Ficha del patron, luego FichaRevisada ← debe simplificarse
- `marcarRevisado(Usuario, Long patronId)` → busca Ficha, luego crea FichaRevisada ← debe simplificarse
- `guardar(Patron)` → `patronRepository.save(patron)`

**`ProgresoService.java`** — inyecta `FichaRepository`, `FichaRevisadaRepository`, `PartidaRepository`, `RespuestaPartidaRepository`, `ProgresoRepository`. Método `resumen(Usuario)`:
- Usa `fichaRevisadaRepository.countByUsuario(usuario)` para `fichasRevisadas`
- Usa `fichaRepository.count()` para `totalFichas`
- Ambas referencias deben cambiar a `PatronRevisadoRepository`/`PatronRepository`

### Controladores actuales

**`AdminFichaController.java`** — mapeado en `/admin/fichas`, inyecta `FichaService` y `PatronService`. Todos los métodos en TODO. **ELIMINAR o reconvertir en `AdminPatronController`.**

**`TeoriaController.java`** — mapeado en `/teoria`. Inyecta `PatronService`, `UsuarioService`. Métodos:
- `GET /teoria` → pasa listas por categoría al modelo
- `GET /teoria/{id}` → pasa `patron` y `revisado` (boolean) al modelo
- `POST /teoria/{id}/revisar` → llama `patronService.marcarRevisado()`
- Tres endpoints REST en TODO (`/api/teoria/patrones`, etc.)

**`ProgresoController.java`** — mapeado en `/progreso`. Inyecta `ProgresoService`, `UsuarioService`. El método `GET /progreso` pasa `progreso` al template. El endpoint `/api/progreso/me` está en TODO.

### Templates Thymeleaf actuales

**`teoria/detalle.html`** — usa `${patron.*}` directamente. Referencia campos con nombres viejos:
- `${patron.uml}` → debe ser `${patron.diagramaUmlUrl}`
- `${patron.problema}` → debe ser `${patron.problemaResuelto}`
- `${patron.cuandoUsarlo}` → debe ser `${patron.cuandoUsar}`
- `${patron.codigoEjemploJava}` → bloque duplicado a eliminar
- Texto de UI dice "Ficha revisada" / "Marcar como revisada" → cambiar a "Patrón revisado" / "Marcar como revisado"

**`progreso/index.html`** — usa `${progreso.fichasRevisadas}` y `${progreso.totalFichas}`. Deben cambiarse a los nuevos nombres de campo.

**`admin/fichas.html`** — solo tiene un TODO en el body. **Eliminar o convertir en `admin/patrones.html`.**

---

## Cambios requeridos (instrucciones exactas)

### 1. Eliminar archivos

Elimina estos archivos completamente:
- `src/main/java/com/example/CodeMan/model/Ficha.java`
- `src/main/java/com/example/CodeMan/model/FichaRevisada.java`
- `src/main/java/com/example/CodeMan/repository/FichaRepository.java`
- `src/main/java/com/example/CodeMan/repository/FichaRevisadaRepository.java`
- `src/main/java/com/example/CodeMan/service/FichaService.java`
- `src/main/java/com/example/CodeMan/controller/AdminFichaController.java`
- `src/main/resources/templates/admin/fichas.html`

---

### 2. Crear `PatronRevisado.java`

Crear en `src/main/java/com/example/CodeMan/model/PatronRevisado.java`:

```java
@Entity
@Table(
    name = "patrones_revisados",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "patron_id"})
)
public class PatronRevisado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patron_id", nullable = false)
    private Patron patron;

    @Column(name = "fecha_revision", nullable = false)
    private LocalDateTime fechaRevision;

    @PrePersist
    public void prePersist() {
        if (fechaRevision == null) fechaRevision = LocalDateTime.now();
    }

    // getters y setters para: id, usuario, patron, fechaRevision
}
```

---

### 3. Crear `PatronRevisadoRepository.java`

Crear en `src/main/java/com/example/CodeMan/repository/PatronRevisadoRepository.java`:

```java
public interface PatronRevisadoRepository extends JpaRepository<PatronRevisado, Long> {

    boolean existsByUsuarioAndPatron(Usuario usuario, Patron patron);

    long countByUsuario(Usuario usuario);

    List<PatronRevisado> findByUsuarioId(Long usuarioId);
}
```

---

### 4. Modificar `Patron.java`

Fusionar los campos duplicados. El resultado final debe tener **estos campos** (eliminar los viejos):

| Campo viejo a eliminar | Campo unificado a conservar |
|---|---|
| `problema` | eliminar (usar `problemaResuelto`) |
| `cuandoUsar` | conservar, eliminar `cuandoUsarlo` |
| `codigoEjemplo` | conservar, eliminar `codigoEjemploJava` |
| `uml` | renombrar a `diagramaUmlUrl` con `@Column(name = "diagrama_uml_url")` |

Agregar también los campos nuevos que están en la BD:
```java
@Column(nullable = false)
private boolean activo = true;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "creado_por_id")
private Usuario creadoPor;

@Column(name = "created_at", nullable = false)
private LocalDateTime createdAt;

@PrePersist
public void prePersist() {
    if (createdAt == null) createdAt = LocalDateTime.now();
}
```

---

### 5. Modificar `Progreso.java`

Renombrar el campo `fichasRevisadas` → `patronesRevisadosCache` y `totalFichas` → `totalPatrones`. Actualizar los métodos calculados:

```java
// Renombrar en la entidad:
@Column(name = "patrones_revisados_cache", nullable = false)
private int patronesRevisadosCache = 0;

@Transient
private long totalPatrones;

// Renombrar métodos:
public int getPorcentajePatrones() {
    return totalPatrones == 0 ? 0 : (int) Math.round((patronesRevisadosCache * 100.0) / totalPatrones);
}
// getPorcentajeAciertos() no cambia
```

También renombrar los campos del cache para alinear con la BD:
- `fichasRevisadas` → `patronesRevisadosCache`
- `partidasJugadas` → `partidasJugadasCache`
- `aciertos` → `aciertosCache`
- `errores` → `erroresCache`

---

### 6. Modificar `PatronService.java`

Reemplazar completamente. Eliminar las inyecciones de `FichaRepository` y `FichaRevisadaRepository`. Inyectar `PatronRevisadoRepository`. El nuevo servicio:

```java
@Service
public class PatronService {

    private final PatronRepository patronRepository;
    private final PatronRevisadoRepository patronRevisadoRepository;

    // constructor con ambos parámetros

    public List<Patron> listar() { return patronRepository.findAll(); }

    public List<Patron> porCategoria(Categoria categoria) {
        return patronRepository.findByCategoria(categoria);
    }

    public Patron buscar(Long id) {
        return patronRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Patrón no encontrado"));
    }

    public long total() {
        return patronRepository.count();  // antes usaba fichaRepository.count() — era un bug
    }

    public long revisadosPor(Usuario usuario) {
        return patronRevisadoRepository.countByUsuario(usuario);
    }

    public boolean estaRevisado(Usuario usuario, Patron patron) {
        return patronRevisadoRepository.existsByUsuarioAndPatron(usuario, patron);
    }

    @Transactional
    public void marcarRevisado(Usuario usuario, Long patronId) {
        Patron patron = buscar(patronId);
        if (!patronRevisadoRepository.existsByUsuarioAndPatron(usuario, patron)) {
            PatronRevisado registro = new PatronRevisado();
            registro.setUsuario(usuario);
            registro.setPatron(patron);
            patronRevisadoRepository.save(registro);
        }
    }

    @Transactional
    public Patron guardar(Patron patron) {
        return patronRepository.save(patron);
    }
}
```

---

### 7. Modificar `ProgresoService.java`

Eliminar inyecciones de `FichaRepository` y `FichaRevisadaRepository`. Inyectar `PatronRepository` y `PatronRevisadoRepository`.

En el método `resumen(Usuario usuario)`:
- `fichaRevisadaRepository.countByUsuario(usuario)` → `patronRevisadoRepository.countByUsuario(usuario)`
- `fichaRepository.count()` → `patronRepository.count()`
- `progreso.setFichasRevisadas(...)` → `progreso.setPatronesRevisadosCache(...)`
- `progreso.setTotalFichas(...)` → `progreso.setTotalPatrones(...)`
- Igual para `setPartidasJugadas` → `setPartidasJugadasCache`, `setAciertos` → `setAciertosCache`, `setErrores` → `setErroresCache`

---

### 8. Modificar `TeoriaController.java`

No cambia la estructura. Solo verificar que llama a los métodos correctos del servicio actualizado. El método `POST /teoria/{id}/revisar` ya llama a `patronService.marcarRevisado()` — no cambia.

---

### 9. Modificar `teoria/detalle.html`

Corregir las referencias a campos viejos:

| Expresión vieja | Expresión nueva |
|---|---|
| `${patron.uml}` | `${patron.diagramaUmlUrl}` |
| `th:if="${patron.uml != null...}"` | `th:if="${patron.diagramaUmlUrl != null...}"` |
| `${patron.problema}` | `${patron.problemaResuelto}` |
| `th:if="${patron.problema != null...}"` | `th:if="${patron.problemaResuelto != null...}"` |
| `${patron.cuandoUsarlo}` | `${patron.cuandoUsar}` |
| `th:if="${patron.cuandoUsarlo != null...}"` | `th:if="${patron.cuandoUsar != null...}"` |

Eliminar el bloque completo de "Código Java — variante" que referenciaba `patron.codigoEjemploJava`.

Cambiar los textos de UI:
- `"✓ Ficha revisada"` → `"✓ Patrón revisado"`
- `"Ya revisaste esta ficha."` → `"Ya revisaste este patrón."`
- `"Marcar como revisada"` → `"Marcar como revisado"`
- `"✓ Revisada"` → `"✓ Revisado"`
- `"Todas las fichas"` (nav) → `"Todos los patrones"`

---

### 10. Modificar `progreso/index.html`

| Expresión vieja | Expresión nueva |
|---|---|
| `${progreso.fichasRevisadas}` | `${progreso.patronesRevisadosCache}` |
| `${progreso.totalFichas}` | `${progreso.totalPatrones}` |
| `${progreso.porcentajeFichas}` | `${progreso.porcentajePatrones}` |
| `${progreso.partidasJugadas}` | `${progreso.partidasJugadasCache}` |
| `${progreso.aciertos}` | `${progreso.aciertosCache}` |
| `${progreso.errores}` | `${progreso.erroresCache}` |
| `"Fichas revisadas"` (texto) | `"Patrones revisados"` |

---

### 11. Verificar que no hay referencias huérfanas

Después de hacer todos los cambios, buscar en todo el proyecto cualquier referencia residual a:
- `Ficha` (clase)
- `FichaRevisada` (clase)
- `FichaRepository`
- `FichaRevisadaRepository`
- `FichaService`
- `fichaRepository`
- `fichaRevisadaRepository`
- `fichasRevisadas` (campo)
- `AdminFichaController`

Si aparece alguna, eliminarla o reemplazarla por el equivalente nuevo.

---

## Resumen de archivos afectados

| Acción | Archivo |
|---|---|
| ELIMINAR | `model/Ficha.java` |
| ELIMINAR | `model/FichaRevisada.java` |
| ELIMINAR | `repository/FichaRepository.java` |
| ELIMINAR | `repository/FichaRevisadaRepository.java` |
| ELIMINAR | `service/FichaService.java` |
| ELIMINAR | `controller/AdminFichaController.java` |
| ELIMINAR | `templates/admin/fichas.html` |
| CREAR | `model/PatronRevisado.java` |
| CREAR | `repository/PatronRevisadoRepository.java` |
| MODIFICAR | `model/Patron.java` — fusionar campos duplicados, agregar nuevos |
| MODIFICAR | `model/Progreso.java` — renombrar campos cache |
| MODIFICAR | `service/PatronService.java` — reemplazar lógica de fichas |
| MODIFICAR | `service/ProgresoService.java` — reemplazar repositorios |
| MODIFICAR | `templates/teoria/detalle.html` — actualizar nombres de campos y textos |
| MODIFICAR | `templates/progreso/index.html` — actualizar nombres de campos |

---

## Restricciones importantes

- No usar `@Autowired`. Toda inyección de dependencias debe ser **por constructor**.
- No cambiar el paquete base `com.example.CodeMan`.
- No modificar `application.properties` ni la configuración de Spring Security.
- No tocar `GameService.java`, `GameApiController.java`, `AuthController.java`, `AuthService.java` ni ningún archivo no listado arriba.
- Los enums `Categoria`, `Rol`, `ModoJuego`, `Dificultad`, `OpcionRespuesta`, `TipoEvento`, `TipoPowerUp` no cambian.
- La tabla `patrones_revisados` ya existe en la BD. No generar ningún script SQL adicional.
