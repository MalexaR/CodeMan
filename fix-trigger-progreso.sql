-- ============================================================
-- fix-trigger-progreso.sql
-- Ejecutar UNA VEZ en Supabase → SQL Editor.
--
-- Problema:
--   Tras el refactor de normalizacion, las columnas
--   aciertos_cache, errores_cache, partidas_jugadas_cache y
--   patrones_revisados_cache fueron eliminadas de la tabla
--   "progreso". Esos valores ahora se calculan en runtime en
--   ProgresoService (campos @Transient en Java).
--
--   Sin embargo, quedaron en la BD una o mas funciones-trigger
--   que siguen tratando de escribir/leer esas columnas, asi
--   que cualquier INSERT en respuestas_partida o UPDATE en
--   partidas falla con:
--
--     ERROR: column "aciertos_cache" of relation "progreso"
--     does not exist
--
--   El error rompe la transaccion entera y bloquea la
--   grabacion (de la respuesta o de fecha_fin), por eso la
--   pantalla /game/resultado se ve sin datos o sin tiempo.
--
-- Solucion:
--   Identificar funciones de trigger (prorettype = 'trigger')
--   en el esquema public cuyo cuerpo (prosrc) mencione alguna
--   de las columnas obsoletas, eliminar sus triggers y luego
--   eliminar las funciones.
-- ============================================================

-- 1. ANTES: lo que hay en las tablas relevantes (para inspeccion).
SELECT 'ANTES' AS etiqueta,
       t.tgname AS trigger_name,
       p.proname AS function_name,
       c.relname AS table_name
FROM   pg_trigger t
JOIN   pg_class    c ON c.oid = t.tgrelid
JOIN   pg_proc     p ON p.oid = t.tgfoid
WHERE  NOT t.tgisinternal
  AND  c.relnamespace = 'public'::regnamespace
  AND  c.relname IN ('respuestas_partida', 'partidas', 'progreso',
                     'patrones_revisados');

-- 2. Eliminar TRIGGERS cuya funcion sea de trigger en public y
--    su codigo fuente mencione las columnas eliminadas, o tenga
--    un nombre tipico.
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT t.tgname,
               n_tab.nspname AS table_schema,
               c.relname     AS table_name,
               p.proname     AS func_name
        FROM   pg_trigger t
        JOIN   pg_class     c     ON c.oid = t.tgrelid
        JOIN   pg_namespace n_tab ON n_tab.oid = c.relnamespace
        JOIN   pg_proc      p     ON p.oid = t.tgfoid
        JOIN   pg_namespace n_fn  ON n_fn.oid = p.pronamespace
        WHERE  NOT t.tgisinternal
          AND  n_fn.nspname    = 'public'
          AND  p.prokind       = 'f'
          AND  p.prorettype    = 'trigger'::regtype
          AND  (
                  p.proname IN ('fn_cache_respuesta',
                                'fn_actualizar_progreso',
                                'fn_actualizar_cache_progreso',
                                'fn_cache_progreso',
                                'fn_progreso_cache',
                                'fn_partidas_progreso',
                                'trg_cache_respuesta')
               OR p.prosrc ~* '\maciertos_cache\M'
               OR p.prosrc ~* '\merrores_cache\M'
               OR p.prosrc ~* '\mpartidas_jugadas_cache\M'
               OR p.prosrc ~* '\mpatrones_revisados_cache\M'
               )
    LOOP
        RAISE NOTICE 'DROP TRIGGER % en %.% (funcion %)',
                     r.tgname, r.table_schema, r.table_name, r.func_name;
        EXECUTE format('DROP TRIGGER IF EXISTS %I ON %I.%I',
                       r.tgname, r.table_schema, r.table_name);
    END LOOP;
END$$;

-- 3. Eliminar las FUNCIONES de trigger que ahora quedaron
--    huerfanas o cuyo codigo aun menciona columnas eliminadas.
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT n.nspname AS sch,
               p.proname AS fname,
               p.oid     AS foid
        FROM   pg_proc p
        JOIN   pg_namespace n ON n.oid = p.pronamespace
        WHERE  n.nspname    = 'public'
          AND  p.prokind    = 'f'
          AND  p.prorettype = 'trigger'::regtype
          AND  (
                  p.proname IN ('fn_cache_respuesta',
                                'fn_actualizar_progreso',
                                'fn_actualizar_cache_progreso',
                                'fn_cache_progreso',
                                'fn_progreso_cache',
                                'fn_partidas_progreso')
               OR p.prosrc ~* '\maciertos_cache\M'
               OR p.prosrc ~* '\merrores_cache\M'
               OR p.prosrc ~* '\mpartidas_jugadas_cache\M'
               OR p.prosrc ~* '\mpatrones_revisados_cache\M'
               )
    LOOP
        RAISE NOTICE 'DROP FUNCTION %.%() CASCADE', r.sch, r.fname;
        EXECUTE format('DROP FUNCTION IF EXISTS %I.%I() CASCADE',
                       r.sch, r.fname);
    END LOOP;
END$$;

-- 4. DESPUES: lo que quedo enganchado a esas tablas.
--    Idealmente la lista queda vacia.
SELECT 'DESPUES' AS etiqueta,
       t.tgname AS trigger_name,
       p.proname AS function_name,
       c.relname AS table_name
FROM   pg_trigger t
JOIN   pg_class    c ON c.oid = t.tgrelid
JOIN   pg_proc     p ON p.oid = t.tgfoid
WHERE  NOT t.tgisinternal
  AND  c.relnamespace = 'public'::regnamespace
  AND  c.relname IN ('respuestas_partida', 'partidas', 'progreso',
                     'patrones_revisados');
