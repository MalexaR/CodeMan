-- Vincular diagramas UML a los patrones
-- Ejecutar en Supabase → SQL Editor DESPUÉS de data-patrones.sql

UPDATE patrones SET diagrama_uml_url = '/images/uml/singleton.svg'       WHERE nombre = 'Singleton';
UPDATE patrones SET diagrama_uml_url = '/images/uml/factory-method.svg'  WHERE nombre = 'Factory Method';
UPDATE patrones SET diagrama_uml_url = '/images/uml/builder.svg'         WHERE nombre = 'Builder';
UPDATE patrones SET diagrama_uml_url = '/images/uml/adapter.svg'         WHERE nombre = 'Adapter';
UPDATE patrones SET diagrama_uml_url = '/images/uml/decorator.svg'       WHERE nombre = 'Decorator';
UPDATE patrones SET diagrama_uml_url = '/images/uml/facade.svg'          WHERE nombre = 'Facade';
UPDATE patrones SET diagrama_uml_url = '/images/uml/observer.svg'        WHERE nombre = 'Observer';
UPDATE patrones SET diagrama_uml_url = '/images/uml/strategy.svg'        WHERE nombre = 'Strategy';
UPDATE patrones SET diagrama_uml_url = '/images/uml/template-method.svg' WHERE nombre = 'Template Method';
