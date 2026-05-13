-- ============================================================
-- data-preguntas.sql
-- Preguntas PREGUNTA_NORMAL (4 opciones) y DECISION_CRITICA (3 opciones)
-- Ejecutar en Supabase → SQL Editor DESPUÉS de data-patrones.sql
-- ============================================================

-- ============================================================
-- CREACIONAL — PREGUNTA_NORMAL
-- ============================================================

-- Singleton BAJA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cuál es la característica principal del patrón Singleton?',
  'Permite crear múltiples instancias de una clase según se necesiten',
  'Garantiza que una clase tenga una sola instancia y proporciona un punto de acceso global a ella',
  'Define una interfaz para crear objetos en una jerarquía de clases',
  'Separa la construcción de un objeto complejo de su representación final',
  'B',
  'El Singleton asegura que solo exista una instancia de la clase y ofrece un punto de acceso global, típicamente a través de un método estático getInstance().',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Singleton' LIMIT 1),
  'BAJA', 'PREGUNTA_NORMAL', true
);

-- Singleton MEDIA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'En Java, ¿qué mecanismo garantiza que un Singleton sea seguro en entornos multihilo?',
  'Usar herencia de la clase Thread',
  'Hacer el constructor público para visibilidad completa',
  'Usar synchronized en getInstance() o inicializar la instancia directamente en la declaración del campo',
  'Implementar la interfaz Runnable en la clase Singleton',
  'C',
  'En entornos multihilo dos hilos podrían crear instancias simultáneamente. Usar synchronized o la inicialización estática (eager initialization) previene la doble creación.',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Singleton' LIMIT 1),
  'MEDIA', 'PREGUNTA_NORMAL', true
);

-- Singleton ALTA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cuál es el principal problema del patrón Singleton en el contexto de pruebas unitarias?',
  'Permite demasiadas instancias en tiempo de ejecución',
  'Dificulta las pruebas porque introduce estado global y dependencias ocultas imposibles de aislar',
  'No puede usarse en aplicaciones web modernas',
  'Solo funciona correctamente con clases abstractas',
  'B',
  'El Singleton introduce estado global que persiste entre pruebas, haciendo difícil aislar el comportamiento. Los mocks no pueden sustituir fácilmente una dependencia Singleton.',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Singleton' LIMIT 1),
  'ALTA', 'PREGUNTA_NORMAL', true
);

-- Factory Method BAJA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Qué problema resuelve principalmente el patrón Factory Method?',
  'Reduce el número de objetos creados en memoria compartida',
  'Delega la responsabilidad de instanciación a subclases, desacoplando el cliente del tipo concreto creado',
  'Combina múltiples objetos en uno solo para ahorrar recursos',
  'Organiza objetos en jerarquías de herencia profundas',
  'B',
  'Factory Method define un método para crear objetos en la clase base, pero permite que las subclases decidan qué clase concreta instanciar, desacoplando la creación del uso.',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Factory Method' LIMIT 1),
  'BAJA', 'PREGUNTA_NORMAL', true
);

-- Factory Method MEDIA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'En el patrón Factory Method, ¿qué rol cumple factoryMethod() en la clase creadora abstracta?',
  'Crea directamente la instancia final del producto sin delegar',
  'Declara la firma del método de creación pero delega la instanciación concreta a las subclases',
  'Destruye objetos al final de su ciclo de vida',
  'Conecta dos interfaces incompatibles entre sí',
  'B',
  'El factoryMethod() en la clase abstracta define la interfaz de creación. Las subclases concretas lo sobreescriben y deciden qué objeto específico crear, manteniendo al cliente ignorante del tipo concreto.',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Factory Method' LIMIT 1),
  'MEDIA', 'PREGUNTA_NORMAL', true
);

-- Factory Method ALTA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cuál es la diferencia principal entre Factory Method y Abstract Factory?',
  'Factory Method trabaja con una familia de productos; Abstract Factory crea un solo tipo de objeto',
  'Abstract Factory solo crea un tipo de objeto; Factory Method puede crear muchos tipos',
  'No existe diferencia real, son el mismo patrón con distinto nombre',
  'Factory Method crea un tipo de producto vía herencia; Abstract Factory proporciona interfaz para crear familias de objetos relacionados',
  'D',
  'Factory Method usa herencia para crear un producto. Abstract Factory usa composición para crear familias de productos relacionados (ej. botones + checkboxes de un mismo tema visual) sin especificar clases concretas.',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Factory Method' LIMIT 1),
  'ALTA', 'PREGUNTA_NORMAL', true
);

-- Builder BAJA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Para qué tipo de objeto es más apropiado usar el patrón Builder?',
  'Objetos simples con un único atributo de configuración',
  'Objetos que son inmutables y no requieren ningún atributo',
  'Objetos complejos con muchos parámetros opcionales de construcción',
  'Objetos que deben compartir una única instancia en toda la aplicación',
  'C',
  'Builder es ideal cuando un objeto tiene muchos parámetros de configuración, especialmente opcionales, evitando el antipatrón del constructor telescópico con docenas de parámetros.',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Builder' LIMIT 1),
  'BAJA', 'PREGUNTA_NORMAL', true
);

-- Builder MEDIA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cuál es el rol del Director en el patrón Builder?',
  'Define la interfaz del producto final que se va a construir',
  'Construye el objeto usando la interfaz del Builder en un orden específico y encapsula las recetas de construcción',
  'Es la implementación concreta del Builder para un producto específico',
  'Gestiona el ciclo de vida del Builder y libera recursos al terminar',
  'B',
  'El Director conoce el orden en que deben ejecutarse los pasos de construcción. No es obligatorio, pero encapsula las configuraciones de construcción reutilizables y el cliente solo debe asociar un Builder al Director.',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Builder' LIMIT 1),
  'MEDIA', 'PREGUNTA_NORMAL', true
);

-- Builder ALTA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'En el Builder con fluent interface, ¿cómo logra cada método de configuración permitir el encadenamiento de llamadas?',
  'Heredando de una clase base que implementa el encadenamiento automáticamente',
  'Usando synchronized en cada método setter del Builder',
  'Retornando ''this'' (la referencia al propio Builder) al final de cada método setter',
  'Implementando la interfaz Comparable en la clase Builder',
  'C',
  'Cada método setter retorna ''this'', permitiendo encadenar llamadas: builder.setA().setB().setC().build(). Esto produce código más legible y elimina la necesidad de variables intermedias.',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Builder' LIMIT 1),
  'ALTA', 'PREGUNTA_NORMAL', true
);

-- ============================================================
-- ESTRUCTURAL — PREGUNTA_NORMAL
-- ============================================================

-- Adapter BAJA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Qué problema resuelve el patrón Adapter?',
  'Reduce la memoria usada por objetos en tiempo de ejecución',
  'Permite que clases con interfaces incompatibles trabajen juntas sin modificar su código fuente',
  'Garantiza una sola instancia de una clase en todo el sistema',
  'Simplifica una interfaz compleja detrás de una más sencilla',
  'B',
  'El Adapter actúa como un convertidor o traductor entre dos interfaces incompatibles, permitiendo que clases existentes colaboren sin necesidad de modificar su código original.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Adapter' LIMIT 1),
  'BAJA', 'PREGUNTA_NORMAL', true
);

-- Adapter MEDIA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cuál es la diferencia entre el adaptador de clase y el adaptador de objeto?',
  'No hay diferencia, son equivalentes en estructura y comportamiento',
  'El adaptador de clase usa herencia múltiple; el adaptador de objeto usa composición',
  'El adaptador de objeto solo funciona en Java; el de clase solo en C++',
  'El adaptador de clase es siempre más flexible que el de objeto',
  'B',
  'El adaptador de clase hereda tanto del adaptado como del objetivo (requiere herencia múltiple o interfaces). El de objeto contiene una instancia del adaptado y delega llamadas, siendo más flexible y portable.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Adapter' LIMIT 1),
  'MEDIA', 'PREGUNTA_NORMAL', true
);

-- Adapter ALTA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cuándo preferirías usar un Adapter de objeto sobre un Adapter de clase?',
  'Cuando necesitas adaptar múltiples subclases del adaptado con un único adaptador',
  'Cuando la interfaz objetivo es final y no puede extenderse',
  'Cuando el lenguaje de programación no soporta interfaces',
  'Cuando necesitas el máximo rendimiento posible en la llamada adaptada',
  'A',
  'El adaptador de objeto puede adaptarse al adaptado y a todas sus subclases usando composición, mientras que el adaptador de clase está atado a la clase concreta que hereda y no puede adaptarse a variantes.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Adapter' LIMIT 1),
  'ALTA', 'PREGUNTA_NORMAL', true
);

-- Decorator BAJA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cuál es el propósito principal del patrón Decorator?',
  'Crear objetos sin especificar su clase concreta en tiempo de compilación',
  'Añadir responsabilidades a un objeto dinámicamente sin modificar otras instancias de la misma clase',
  'Proporcionar un sustituto que controla el acceso a otro objeto',
  'Convertir la interfaz de una clase en otra interfaz esperada por el cliente',
  'B',
  'El Decorator envuelve un objeto en otro del mismo tipo, añadiendo comportamiento antes o después de delegar al objeto original, sin afectar a otros objetos ni requerir herencia.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Decorator' LIMIT 1),
  'BAJA', 'PREGUNTA_NORMAL', true
);

-- Decorator MEDIA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Qué ventaja tiene el patrón Decorator sobre la herencia para extender comportamiento?',
  'El Decorator es siempre más rápido en ejecución que la herencia',
  'El Decorator permite añadir responsabilidades en tiempo de ejecución de forma dinámica y combinable',
  'El Decorator solo funciona con clases abstractas e interfaces',
  'No hay ventaja real, la herencia es siempre preferible al Decorator',
  'B',
  'La herencia es estática (definida en compilación) y puede provocar explosión de subclases. El Decorator permite combinar comportamientos en tiempo de ejecución componiendo decoradores.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Decorator' LIMIT 1),
  'MEDIA', 'PREGUNTA_NORMAL', true
);

-- Decorator ALTA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'Para una implementación correcta de Decorator, ¿qué debe cumplir el decorador?',
  'Debe heredar de una clase diferente al componente que decora',
  'Debe implementar la misma interfaz que el componente y contener una referencia a otro componente',
  'Debe ser una clase final que no puede extenderse más',
  'No debe tener estado propio para mantener la inmutabilidad',
  'B',
  'El Decorator implementa la misma interfaz del Componente (para ser intercambiable con él) y contiene una referencia al componente que decora. Esta doble relación es la clave estructural del patrón.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Decorator' LIMIT 1),
  'ALTA', 'PREGUNTA_NORMAL', true
);

-- Facade BAJA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Qué ventaja ofrece el patrón Facade al cliente que lo usa?',
  'Permite crear objetos sin conocer su clase concreta',
  'Proporciona una interfaz simplificada a un conjunto complejo de clases o subsistemas',
  'Garantiza que solo haya una instancia de cada clase del subsistema',
  'Convierte datos entre formatos o interfaces incompatibles',
  'B',
  'El Facade oculta la complejidad del subsistema detrás de una interfaz simple. El cliente interactúa solo con el Facade y no necesita conocer los detalles internos del sistema.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Facade' LIMIT 1),
  'BAJA', 'PREGUNTA_NORMAL', true
);

-- Facade MEDIA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cuál es la relación de dependencia entre el Facade y las clases del subsistema?',
  'El Facade hereda de todas las clases del subsistema',
  'El Facade implementa las mismas interfaces que las clases del subsistema',
  'El Facade depende del subsistema, pero el subsistema no conoce ni depende del Facade',
  'El Facade reemplaza completamente a las clases del subsistema',
  'C',
  'La dependencia es unidireccional: el Facade conoce y usa el subsistema, pero las clases del subsistema son independientes y pueden usarse directamente sin el Facade si fuese necesario.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Facade' LIMIT 1),
  'MEDIA', 'PREGUNTA_NORMAL', true
);

-- Facade ALTA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿En qué se diferencia el patrón Facade del patrón Mediator?',
  'Facade reduce dependencias entre subsistemas; Mediator proporciona una interfaz simplificada al cliente',
  'Facade simplifica la interfaz de un subsistema existente; Mediator centraliza la comunicación entre objetos pares para reducir dependencias entre ellos',
  'Son idénticos en estructura y propósito, solo difieren en el nombre',
  'Mediator es un patrón creacional; Facade es un patrón de comportamiento',
  'B',
  'Facade reduce la complejidad de uso de un subsistema hacia el cliente. Mediator coordina interacciones complejas entre objetos del mismo nivel para evitar que se referencien directamente entre sí.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Facade' LIMIT 1),
  'ALTA', 'PREGUNTA_NORMAL', true
);

-- ============================================================
-- COMPORTAMIENTO — PREGUNTA_NORMAL
-- ============================================================

-- Observer BAJA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cuáles son los dos roles principales en el patrón Observer?',
  'Creador y Destructor de objetos',
  'Sujeto (Subject/Observable) y Observador (Observer)',
  'Contexto y Estrategia intercambiable',
  'Componente base y Decorador envolvente',
  'B',
  'El Sujeto mantiene la lista de observadores y los notifica ante cambios de estado. Los Observadores se suscriben y actualizan su estado cuando reciben la notificación.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Observer' LIMIT 1),
  'BAJA', 'PREGUNTA_NORMAL', true
);

-- Observer MEDIA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Qué principio de diseño fundamenta principalmente el patrón Observer?',
  'Abierto/Cerrado: extender sin modificar clases existentes',
  'Responsabilidad única: cada clase tiene un solo motivo de cambio',
  'Inversión de dependencias: depender de abstracciones',
  'Bajo acoplamiento entre objetos que interactúan: el Sujeto solo conoce la interfaz Observer',
  'D',
  'Observer fomenta el bajo acoplamiento porque el Sujeto solo conoce la interfaz Observer, no las clases concretas. Los observadores pueden añadirse o eliminarse sin modificar el Sujeto.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Observer' LIMIT 1),
  'MEDIA', 'PREGUNTA_NORMAL', true
);

-- Observer ALTA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Qué problema puede surgir si un Observador modifica el estado del Sujeto durante su método update()?',
  'El rendimiento del sistema mejora notablemente',
  'Actualizaciones en cascada inesperadas o ciclos infinitos de notificación',
  'El Sujeto pierde automáticamente su referencia a todos los observadores',
  'Los observadores restantes se multiplican creando instancias adicionales',
  'B',
  'Si un observador modifica el sujeto al ser notificado, el sujeto puede disparar otra ronda de notificaciones, creando ciclos potencialmente infinitos. Es necesario gestionar el orden y evitar modificaciones durante la notificación.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Observer' LIMIT 1),
  'ALTA', 'PREGUNTA_NORMAL', true
);

-- Strategy BAJA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Qué permite el patrón Strategy en tiempo de ejecución?',
  'Crear objetos sin conocer su tipo concreto de antemano',
  'Cambiar el algoritmo o comportamiento utilizado por un objeto de forma dinámica',
  'Añadir métodos nuevos a una clase sin modificar su código fuente',
  'Garantizar que solo exista una única instancia del algoritmo en uso',
  'B',
  'Strategy encapsula algoritmos en clases separadas intercambiables. El contexto puede cambiar la estrategia en tiempo de ejecución, alterando su comportamiento sin modificar su propio código.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Strategy' LIMIT 1),
  'BAJA', 'PREGUNTA_NORMAL', true
);

-- Strategy MEDIA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cómo evita el patrón Strategy el uso excesivo de condicionales (if/else o switch)?',
  'Eliminando el soporte para operadores condicionales en la clase contexto',
  'Usando reflexión para determinar el algoritmo correcto en tiempo de compilación',
  'Extrayendo cada rama del condicional en una clase Strategy separada y seleccionable en ejecución',
  'Usando herencia múltiple para combinar todos los comportamientos en una sola clase',
  'C',
  'Cada variante del algoritmo (cada rama del if/switch) se convierte en una clase Strategy concreta. El cliente elige cuál usar, eliminando los condicionales del contexto y abriendo la extensión sin modificación.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Strategy' LIMIT 1),
  'MEDIA', 'PREGUNTA_NORMAL', true
);

-- Strategy ALTA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cuál es la diferencia clave entre Strategy y Template Method para definir variantes de algoritmos?',
  'Strategy usa composición para intercambiar el algoritmo completo; Template Method usa herencia para definir el esqueleto con pasos variables',
  'Template Method es más flexible porque permite cambiar el algoritmo completo en tiempo de ejecución',
  'Strategy requiere herencia obligatoriamente; Template Method siempre usa composición',
  'No hay diferencia conceptual real entre ambos patrones de comportamiento',
  'A',
  'Strategy delega el algoritmo completo a objetos separados (composición), permitiendo cambio dinámico. Template Method define el esqueleto en la clase base (herencia) y las subclases solo completan pasos específicos.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Strategy' LIMIT 1),
  'ALTA', 'PREGUNTA_NORMAL', true
);

-- Template Method BAJA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Qué define el método plantilla (templateMethod) en el patrón Template Method?',
  'Una instancia estática compartida de la clase abstracta',
  'El esqueleto de un algoritmo, delegando algunos pasos concretos a las subclases',
  'La interfaz pública simplificada de un subsistema complejo',
  'La estrategia de instanciación de objetos en la jerarquía',
  'B',
  'El templateMethod() define los pasos del algoritmo en la clase base y llama a métodos abstractos o hooks que las subclases implementan para personalizar los pasos específicos.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Template Method' LIMIT 1),
  'BAJA', 'PREGUNTA_NORMAL', true
);

-- Template Method MEDIA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Cuál es la diferencia entre un método abstracto y un método hook en Template Method?',
  'No hay diferencia, cumplen exactamente la misma función',
  'Los métodos abstractos obligan a implementarse en subclases; los hooks tienen implementación vacía por defecto y son opcionales',
  'Los hooks son siempre estáticos; los abstractos son siempre de instancia',
  'Los métodos abstractos son privados; los hooks son siempre públicos',
  'B',
  'Los métodos abstractos obligan a las subclases a proporcionar implementación. Los hooks tienen un cuerpo vacío o por defecto en la clase base; las subclases los sobreescriben solo si necesitan personalización.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Template Method' LIMIT 1),
  'MEDIA', 'PREGUNTA_NORMAL', true
);

-- Template Method ALTA
INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  '¿Por qué se recomienda declarar final el método templateMethod() en buenas implementaciones?',
  'Para mejorar el rendimiento de compilación JIT de la JVM',
  'Para evitar que las subclases sobreescriban el esqueleto del algoritmo y alteren su estructura general',
  'Porque es un requisito sintáctico del lenguaje Java para métodos de plantilla',
  'Para permitir la serialización correcta del objeto en flujos de datos',
  'B',
  'Declarar final el templateMethod() protege la estructura del algoritmo. Las subclases solo pueden personalizar los pasos abstractos/hooks, sin poder cambiar el orden o la lógica general del proceso.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Template Method' LIMIT 1),
  'ALTA', 'PREGUNTA_NORMAL', true
);

-- ============================================================
-- CREACIONAL — DECISION_CRITICA
-- (3 opciones; opcion_d = 'N/A' porque la columna es NOT NULL)
-- ============================================================

INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'Múltiples módulos de una aplicación bancaria (transferencias, autenticación, auditoría) deben escribir en el mismo archivo de log. Si cada módulo crea su propia instancia del Logger, se producen conflictos de escritura. ¿Qué patrón creacional aplicas?',
  'Singleton — garantiza una única instancia del Logger compartida y segura para todos los módulos',
  'Factory Method — cada módulo crea su propio logger a través de un método de fábrica especializado',
  'Builder — construye el logger configurado paso a paso para cada módulo independientemente',
  'N/A',
  'A',
  'Singleton es la solución: garantiza que exista una única instancia del Logger en la aplicación, eliminando los conflictos de escritura concurrente y asegurando consistencia en todos los registros.',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Singleton' LIMIT 1),
  'ALTA', 'DECISION_CRITICA', true
);

INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'Tu aplicación debe crear objetos de conexión a distintas bases de datos (MySQL, PostgreSQL, MongoDB) según la configuración del entorno. El código cliente no debe acoplarse a ninguna implementación específica. ¿Qué patrón usarías?',
  'Singleton — para que solo exista una conexión a base de datos en el sistema',
  'Builder — para construir paso a paso todos los parámetros de conexión de forma fluida',
  'Factory Method — define un método que subclases concretas sobreescriben para crear el tipo de conexión apropiado',
  'N/A',
  'C',
  'Factory Method permite definir la interfaz de creación (createConnection()) en la clase base y delegar en subclases concretas (MySQLConnectionFactory, PostgreSQLConnectionFactory) la decisión de qué objeto crear.',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Factory Method' LIMIT 1),
  'ALTA', 'DECISION_CRITICA', true
);

INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'Debes construir objetos ''Reporte'' con hasta 12 secciones opcionales (resumen, gráficos, tablas, firmas digitales, etc.). Un constructor con todos los parámetros es inmanejable. ¿Qué patrón resuelve esto elegantemente?',
  'Singleton — para compartir el mismo reporte inmutable entre todos los usuarios del sistema',
  'Builder — permite configurar solo las secciones necesarias con encadenamiento fluido y build() al final',
  'Factory Method — crea el reporte completo con todas las secciones en una sola llamada al método',
  'N/A',
  'B',
  'Builder es ideal para objetos con muchos parámetros opcionales. El cliente encadena solo los métodos que necesita (setResumen().setGraficos().setFirma()) y llama build() para obtener el objeto configurado correctamente.',
  'CREACIONAL',
  (SELECT id FROM patrones WHERE nombre = 'Builder' LIMIT 1),
  'ALTA', 'DECISION_CRITICA', true
);

-- ============================================================
-- ESTRUCTURAL — DECISION_CRITICA
-- ============================================================

INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'Tu app usa una biblioteca moderna de pagos que espera objetos ''ModernPayment''. Tienes código legado ''OldPaymentGateway'' que no puedes modificar. Necesitas que ambos trabajen juntos. ¿Qué patrón aplicas?',
  'Facade — crea una interfaz simplificada que oculta tanto la implementación moderna como la legada',
  'Adapter — envuelve OldPaymentGateway en una clase que implementa la interfaz ModernPayment',
  'Decorator — añade funcionalidades de pago moderno encima del gateway antiguo en capas',
  'N/A',
  'B',
  'Adapter es la solución: crea una clase que implementa ModernPayment (interfaz objetivo) y contiene una instancia de OldPaymentGateway (adaptado), traduciendo cada llamada nueva a la API del código legado.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Adapter' LIMIT 1),
  'ALTA', 'DECISION_CRITICA', true
);

INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'Un servicio de streaming de video necesita procesar videos con combinaciones opcionales: comprimir, añadir marca de agua, cifrar y añadir subtítulos. Usar herencia generaría docenas de subclases. ¿Qué patrón es más apropiado?',
  'Adapter — adapta el video a diferentes formatos de salida según el dispositivo del usuario',
  'Facade — simplifica el API completo de procesamiento de video en una sola clase',
  'Decorator — cada proceso es un decorador componible: new Cifrado(new MarcaAgua(new Compresion(video)))',
  'N/A',
  'C',
  'Decorator permite combinar funcionalidades en tiempo de ejecución. Cada proceso (compresión, marca de agua, cifrado) es un decorador que envuelve al video y añade su función, sin afectar a las otras capas.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Decorator' LIMIT 1),
  'ALTA', 'DECISION_CRITICA', true
);

INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'Desarrollas un SDK de reservas de vuelos con 15 clases internas (FlightSearch, SeatSelector, PricingEngine, LoyaltyPoints, etc.). Los clientes del SDK solo necesitan 3 operaciones: buscar, reservar y cancelar. ¿Qué patrón usarías?',
  'Adapter — adapta cada clase interna a la interfaz que espera el cliente del SDK',
  'Decorator — añade funcionalidades de búsqueda avanzada sobre las clases existentes en capas',
  'Facade — proporciona una clase con los 3 métodos que orquesta internamente las 15 clases del subsistema',
  'N/A',
  'C',
  'Facade oculta la complejidad detrás de una interfaz simple con buscar(), reservar() y cancelar(). El cliente no necesita saber cómo interactúan FlightSearch, SeatSelector, PricingEngine ni las demás clases internas.',
  'ESTRUCTURAL',
  (SELECT id FROM patrones WHERE nombre = 'Facade' LIMIT 1),
  'ALTA', 'DECISION_CRITICA', true
);

-- ============================================================
-- COMPORTAMIENTO — DECISION_CRITICA
-- ============================================================

INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'En un e-commerce, cuando el precio de un producto cambia, el carrito actualiza su total, el módulo de alertas notifica a usuarios suscritos y el analytics registra el cambio. ¿Qué patrón organiza esto mejor?',
  'Strategy — cambia la estrategia de cálculo de precios en tiempo de ejecución según el contexto',
  'Observer — el Producto es el Sujeto; Carrito, Alertas y Analytics son Observadores que reaccionan al cambio',
  'Template Method — define los pasos del proceso de actualización de precios en la clase base',
  'N/A',
  'B',
  'Observer es perfecto: el Producto notifica a todos sus observadores (Carrito, AlertaModule, Analytics) cuando cambia el precio. Cada observador reacciona de forma independiente sin que el Producto los conozca directamente.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Observer' LIMIT 1),
  'ALTA', 'DECISION_CRITICA', true
);

INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'Tu plataforma de comercio procesa pagos con tarjeta de crédito, PayPal, criptomoneda y transferencia bancaria. La lógica varía completamente entre métodos y el usuario elige en tiempo de ejecución. ¿Qué patrón encaja?',
  'Observer — los métodos de pago observan al carrito de compras y reaccionan a cambios',
  'Template Method — define los pasos del proceso de pago en la clase base con variantes por subclase',
  'Strategy — cada método de pago es una estrategia intercambiable que el Checkout selecciona en ejecución',
  'N/A',
  'C',
  'Strategy encapsula cada algoritmo de pago (CreditCardStrategy, PayPalStrategy, CryptoStrategy) como clases intercambiables. El Checkout cambia la estrategia según la elección del usuario sin modificar su propio código.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Strategy' LIMIT 1),
  'ALTA', 'DECISION_CRITICA', true
);

INSERT INTO preguntas (enunciado, opcion_a, opcion_b, opcion_c, opcion_d, respuesta_correcta, explicacion, categoria, patron_id, dificultad, tipo_evento, activa)
VALUES (
  'Un generador de reportes siempre sigue estos pasos: (1) abrir conexión, (2) consultar datos, (3) formatear, (4) exportar, (5) cerrar. Los pasos 3 y 4 varían según el formato (PDF, Excel, HTML) pero el resto es siempre igual. ¿Qué patrón aplicas?',
  'Observer — el generador notifica a los exportadores cuando los datos están listos para procesar',
  'Template Method — define el esqueleto del proceso en la base; las subclases implementan solo formatear() y exportar()',
  'Strategy — intercambia el algoritmo completo de generación según el formato solicitado',
  'N/A',
  'B',
  'Template Method es ideal: AbstractReportGenerator define templateMethod() con los 5 pasos fijos. Los pasos invariantes (abrir, consultar, cerrar) están en la base; formatear() y exportar() son abstractos para PdfReport, ExcelReport, HtmlReport.',
  'COMPORTAMIENTO',
  (SELECT id FROM patrones WHERE nombre = 'Template Method' LIMIT 1),
  'ALTA', 'DECISION_CRITICA', true
);
