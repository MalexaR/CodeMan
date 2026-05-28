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
--   PERO el trigger fn_cache_respuesta() quedo huerfano en la
--   BD y sigue disparandose despues de cada INSERT en
--   respuestas_partida, fallando con:
--
--     ERROR: column "aciertos_cache" of relation "progreso"
--     does not exist
--
--   Ese error rompe la transaccion y bloquea TODA la grabacion
--   de respuestas, por lo que /game/resultado siempre muestra
--   0 aciertos, 0 errores, 0%.
--
-- Solucion:
--   Eliminar todos los triggers que invoquen fn_cache_respuesta
--   y luego eliminar la funcion. Tambien se eliminan triggers
--   similares por si quedaron de migraciones anteriores.
-- ============================================================

-- 1. Eliminar cualquier trigger que invoque fn_cache_respuesta()
DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT t.tgname,
               c.relname AS tablename,
               n.nspname AS schemaname
        FROM pg_trigger t
        JOIN pg_class    c ON c.oid = t.tgrelid
        JOIN pg_namespace n ON n.oid = c.relnamespace
        JOIN pg_proc     p ON p.oid = t.tgfoid
        WHERE NOT t.tgisinternal
          AND p.proname IN ('fn_cache_respuesta',
                            'fn_actualizar_progreso',
                            'fn_actualizar_cache_progreso',
                            'trg_cache_respuesta')
    LOOP
        EXECUTE format('DROP TRIGGER IF EXISTS %I ON %I.%I',
                       r.tgname, r.schemaname, r.tablename);
    END LOOP;
END$$;

-- 2. Eliminar las funciones obsoletas (CASCADE por si quedan
--    referencias residuales).
DROP FUNCTION IF EXISTS fn_cache_respuesta()           CASCADE;
DROP FUNCTION IF EXISTS fn_actualizar_progreso()       CASCADE;
DROP FUNCTION IF EXISTS fn_actualizar_cache_progreso() CASCADE;

-- 3. Validacion: listar triggers que aun apunten a la tabla
--    respuestas_partida. Tras el fix deberia salir vacio.
SELECT t.tgname AS trigger_name,
       p.proname AS function_name,
       c.relname AS table_name
FROM   pg_trigger t
JOIN   pg_class    c ON c.oid = t.tgrelid
JOIN   pg_proc     p ON p.oid = t.tgfoid
WHERE  NOT t.tgisinternal
  AND  c.relname = 'respuestas_partida';
