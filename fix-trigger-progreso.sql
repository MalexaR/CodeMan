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
--   que siguen tratando de escribir a esas columnas, asi que
--   cualquier INSERT en respuestas_partida o UPDATE en partidas
--   falla con:
--
--     ERROR: column "aciertos_cache" of relation "progreso"
--     does not exist
--
--   El error rompe la transaccion y bloquea la grabacion de
--   fecha_fin (la pantalla /game/resultado muestra "—" en
--   tiempo) o de las respuestas mismas.
--
-- Solucion:
--   Eliminar todos los triggers cuyo cuerpo de funcion mencione
--   las columnas obsoletas, y luego eliminar las funciones.
-- ============================================================

-- 1. Listar lo que hay ANTES del fix (para inspeccion)
SELECT 'ANTES:' AS etiqueta,
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

-- 2. Eliminar cualquier trigger cuya funcion mencione alguna
--    de las columnas cache eliminadas o tenga un nombre tipico
--    relacionado con esa logica.
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT t.tgname,
               c.relname AS tablename,
               n.nspname AS schemaname,
               p.proname AS funcname,
               p.oid     AS funcoid
        FROM pg_trigger t
        JOIN pg_class    c ON c.oid = t.tgrelid
        JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc     p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal
          AND n.nspname = 'public'
          AND (
                p.proname IN ('fn_cache_respuesta',
                              'fn_actualizar_progreso',
                              'fn_actualizar_cache_progreso',
                              'fn_cache_progreso',
                              'fn_progreso_cache',
                              'fn_partidas_progreso',
                              'trg_cache_respuesta')
             OR pg_get_functiondef(p.oid) ~* 'aciertos_cache'
             OR pg_get_functiondef(p.oid) ~* 'errores_cache'
             OR pg_get_functiondef(p.oid) ~* 'partidas_jugadas_cache'
             OR pg_get_functiondef(p.oid) ~* 'patrones_revisados_cache'
              )
    LOOP
        RAISE NOTICE 'Eliminando trigger % en %.% (funcion %)',
                     r.tgname, r.schemaname, r.tablename, r.funcname;
        EXECUTE format('DROP TRIGGER IF EXISTS %I ON %I.%I',
                       r.tgname, r.schemaname, r.tablename);
    END LOOP;
END$$;

-- 3. Eliminar las funciones obsoletas (CASCADE por si quedan
--    referencias residuales).
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT n.nspname AS schemaname,
               p.proname AS funcname,
               pg_get_function_identity_arguments(p.oid) AS args
        FROM   pg_proc p
        JOIN   pg_namespace n ON n.oid = p.pronamespace
        WHERE  n.nspname = 'public'
          AND (p.proname IN ('fn_cache_respuesta',
                             'fn_actualizar_progreso',
                             'fn_actualizar_cache_progreso',
                             'fn_cache_progreso',
                             'fn_progreso_cache',
                             'fn_partidas_progreso')
              OR pg_get_functiondef(p.oid) ~* 'aciertos_cache'
              OR pg_get_functiondef(p.oid) ~* 'errores_cache'
              OR pg_get_functiondef(p.oid) ~* 'partidas_jugadas_cache'
              OR pg_get_functiondef(p.oid) ~* 'patrones_revisados_cache')
    LOOP
        RAISE NOTICE 'Eliminando funcion %.%(%)', r.schemaname, r.funcname, r.args;
        EXECUTE format('DROP FUNCTION IF EXISTS %I.%I(%s) CASCADE',
                       r.schemaname, r.funcname, r.args);
    END LOOP;
END$$;

-- 4. Validacion: listar triggers que aun apunten a esas tablas.
--    Idealmente queda vacio (o solo internos del sistema).
SELECT 'DESPUES:' AS etiqueta,
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
