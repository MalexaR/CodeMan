-- Patrones de Diseño GoF — CodeMan
-- 9 patrones: 3 por categoría
-- Referencia base: Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994).
--   Design Patterns: Elements of Reusable Object-Oriented Software.
--   Addison-Wesley Professional. ISBN: 978-0-201-63361-0.
-- Ejecutar en Supabase → SQL Editor

ALTER TABLE patrones ADD COLUMN IF NOT EXISTS referencia TEXT;

INSERT INTO patrones (nombre, categoria, descripcion, proposito, problema_resuelto, cuando_usar, estructura, codigo_ejemplo, referencia, activo, created_at) VALUES

-- ─────────────────────────────────────────
-- CREACIONALES
-- ─────────────────────────────────────────

('Singleton',
 'CREACIONAL',
 'Garantiza que una clase tenga una única instancia y proporciona un punto de acceso global a ella.',
 'Asegurar que solo exista una instancia de una clase y ofrecer un punto de acceso global a dicha instancia.',
 'Varios módulos necesitan compartir un único recurso (conexión a base de datos, configuración, registro de eventos). Crear múltiples instancias causaría inconsistencias o desperdicio de recursos.',
 'Úsalo cuando exactamente un objeto deba coordinar acciones en todo el sistema: gestores de configuración, pools de conexiones, servicios de logging.',
 'Constructor privado que impide la instanciación directa. Atributo estático privado que almacena la única instancia. Método estático getInstance() que crea la instancia solo la primera vez y la retorna siempre.',
 'public class Configuracion {
    private static Configuracion instancia;
    private String urlBD;

    private Configuracion() {
        this.urlBD = "jdbc:postgresql://localhost/app";
    }

    public static synchronized Configuracion getInstance() {
        if (instancia == null) {
            instancia = new Configuracion();
        }
        return instancia;
    }

    public String getUrlBD() { return urlBD; }
}

// Uso
Configuracion a = Configuracion.getInstance();
Configuracion b = Configuracion.getInstance();
System.out.println(a == b); // true — misma instancia',
 'Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Singleton. En Design Patterns: Elements of Reusable Object-Oriented Software (pp. 127-134). Addison-Wesley Professional. ISBN: 978-0-201-63361-0.',
 true, NOW()),

('Factory Method',
 'CREACIONAL',
 'Define una interfaz para crear un objeto, pero deja que las subclases decidan qué clase instanciar. Delega la instanciación a las subclases.',
 'Separar la creación de objetos de su uso, permitiendo que las subclases controlen qué tipo concreto se instancia sin modificar el código cliente.',
 'El código debe crear objetos pero no debe depender de clases concretas. Diferentes contextos requieren diferentes implementaciones de un mismo concepto.',
 'Úsalo cuando no conozcas de antemano la clase exacta a instanciar, cuando quieras dar a las subclases control sobre la creación, o cuando necesites reutilizar objetos existentes.',
 'Interfaz Producto define la operación. ProductoConcreto la implementa. Clase Creador abstracta declara el factoryMethod() y lo usa en su lógica. SubclasesCreador sobrescriben factoryMethod() para retornar un ProductoConcreto.',
 'interface Notificacion {
    void enviar(String mensaje);
}

class NotificacionEmail implements Notificacion {
    public void enviar(String mensaje) {
        System.out.println("Email: " + mensaje);
    }
}

class NotificacionSMS implements Notificacion {
    public void enviar(String mensaje) {
        System.out.println("SMS: " + mensaje);
    }
}

abstract class Notificador {
    protected abstract Notificacion crearNotificacion(); // Factory Method

    public void notificar(String mensaje) {
        Notificacion n = crearNotificacion();
        n.enviar(mensaje);
    }
}

class NotificadorEmail extends Notificador {
    protected Notificacion crearNotificacion() {
        return new NotificacionEmail();
    }
}

// Uso
Notificador n = new NotificadorEmail();
n.notificar("Bienvenido a CodeMan");',
 'Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Factory Method. En Design Patterns: Elements of Reusable Object-Oriented Software (pp. 107-116). Addison-Wesley Professional. ISBN: 978-0-201-63361-0.',
 true, NOW()),

('Builder',
 'CREACIONAL',
 'Separa la construcción de un objeto complejo de su representación, de modo que el mismo proceso pueda crear diferentes representaciones.',
 'Construir objetos complejos paso a paso con control total sobre el proceso, sin exponer la representación interna ni requerir constructores con múltiples parámetros.',
 'La creación de un objeto requiere muchos parámetros opcionales o pasos encadenados. Un constructor con doce parámetros es ilegible y propenso a errores.',
 'Úsalo cuando los objetos tengan muchos atributos opcionales, cuando quieras construir diferentes variantes del mismo objeto con el mismo proceso, o cuando la construcción deba hacerse paso a paso.',
 'Clase Producto con atributos. Clase Builder interna con métodos fluidos por cada atributo (retornan this). Método build() que construye y retorna el Producto. Opcionalmente un Director que orquesta la secuencia.',
 'class Reporte {
    private String titulo;
    private String formato;
    private boolean incluirGraficos;
    private int paginas;

    private Reporte() {}

    public static class Builder {
        private Reporte r = new Reporte();

        public Builder titulo(String titulo) {
            r.titulo = titulo; return this;
        }
        public Builder formato(String formato) {
            r.formato = formato; return this;
        }
        public Builder conGraficos() {
            r.incluirGraficos = true; return this;
        }
        public Builder paginas(int n) {
            r.paginas = n; return this;
        }
        public Reporte build() { return r; }
    }

    public String toString() {
        return titulo + " [" + formato + "] " +
               paginas + "p." + (incluirGraficos ? " +gráficos" : "");
    }
}

// Uso
Reporte r = new Reporte.Builder()
    .titulo("Reporte Mensual")
    .formato("PDF")
    .paginas(10)
    .conGraficos()
    .build();
System.out.println(r);',
 'Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Builder. En Design Patterns: Elements of Reusable Object-Oriented Software (pp. 97-106). Addison-Wesley Professional. ISBN: 978-0-201-63361-0.',
 true, NOW()),

-- ─────────────────────────────────────────
-- ESTRUCTURALES
-- ─────────────────────────────────────────

('Adapter',
 'ESTRUCTURAL',
 'Convierte la interfaz de una clase en otra interfaz que el cliente espera. Permite que clases con interfaces incompatibles trabajen juntas.',
 'Hacer colaborar clases con interfaces incompatibles sin modificar su código fuente, actuando como traductor entre ambas.',
 'Se necesita integrar una librería externa o código legado cuya interfaz no coincide con la que el sistema espera, y no es posible modificar ninguno de los dos lados.',
 'Úsalo para integrar APIs de terceros, código legado o cualquier componente cuya interfaz no puedas modificar y que difiera de lo que tu sistema necesita.',
 'Interfaz Target que el cliente conoce. Clase Adaptee existente con interfaz incompatible. Clase Adapter implementa Target, contiene una instancia de Adaptee y traduce cada llamada.',
 '// Interfaz que el sistema espera
interface ReproductorAudio {
    void reproducir(String archivo);
}

// Clase existente con interfaz diferente (no modificable)
class ReproductorLegacy {
    public void play(String ruta, String formato) {
        System.out.println("Reproduciendo " + formato + ": " + ruta);
    }
}

// Adapter: adapta ReproductorLegacy a ReproductorAudio
class AudioAdapter implements ReproductorAudio {
    private ReproductorLegacy legacy = new ReproductorLegacy();

    public void reproducir(String archivo) {
        String ext = archivo.substring(archivo.lastIndexOf(".") + 1);
        legacy.play(archivo, ext.toUpperCase());
    }
}

// Uso: el cliente solo conoce ReproductorAudio
ReproductorAudio player = new AudioAdapter();
player.reproducir("tema.mp3"); // MP3: tema.mp3',
 'Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Adapter. En Design Patterns: Elements of Reusable Object-Oriented Software (pp. 139-150). Addison-Wesley Professional. ISBN: 978-0-201-63361-0.',
 true, NOW()),

('Decorator',
 'ESTRUCTURAL',
 'Adjunta responsabilidades adicionales a un objeto de forma dinámica. Proporciona una alternativa flexible a la herencia para extender funcionalidad.',
 'Añadir comportamiento a objetos individuales en tiempo de ejecución sin afectar a otros objetos de la misma clase ni modificar la clase original.',
 'Se necesita extender el comportamiento de clases de manera flexible y combinable. Usar herencia generaría una explosión de subclases para cada combinación posible de funcionalidades.',
 'Úsalo cuando necesites añadir responsabilidades a objetos individuales sin afectar a los demás, cuando la extensión por herencia sea impráctica, o cuando quieras combinar comportamientos en tiempo de ejecución.',
 'Interfaz Componente con la operación base. ComponenteConcreto implementa el comportamiento básico. Decorador abstracto implementa Componente y contiene una referencia a otro Componente. Decoradores concretos añaden el comportamiento extra.',
 'interface Cafe {
    String descripcion();
    double precio();
}

class CafeBase implements Cafe {
    public String descripcion() { return "Café"; }
    public double precio() { return 1.50; }
}

abstract class DecoradorCafe implements Cafe {
    protected Cafe cafe;
    public DecoradorCafe(Cafe cafe) { this.cafe = cafe; }
}

class ConLeche extends DecoradorCafe {
    public ConLeche(Cafe c) { super(c); }
    public String descripcion() { return cafe.descripcion() + ", leche"; }
    public double precio() { return cafe.precio() + 0.50; }
}

class ConCaramelo extends DecoradorCafe {
    public ConCaramelo(Cafe c) { super(c); }
    public String descripcion() { return cafe.descripcion() + ", caramelo"; }
    public double precio() { return cafe.precio() + 0.75; }
}

// Uso: combinaciones flexibles en tiempo de ejecución
Cafe pedido = new ConCaramelo(new ConLeche(new CafeBase()));
System.out.println(pedido.descripcion()); // Café, leche, caramelo
System.out.println("$" + pedido.precio()); // $2.75',
 'Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Decorator. En Design Patterns: Elements of Reusable Object-Oriented Software (pp. 175-184). Addison-Wesley Professional. ISBN: 978-0-201-63361-0.',
 true, NOW()),

('Facade',
 'ESTRUCTURAL',
 'Proporciona una interfaz simplificada a un conjunto complejo de interfaces en un subsistema.',
 'Ocultar la complejidad de un subsistema detrás de una interfaz sencilla, facilitando su uso sin que el cliente conozca los detalles internos.',
 'Un subsistema tiene muchas clases con dependencias complejas. El código cliente debe conocerlas todas, lo que genera alto acoplamiento y dificulta el mantenimiento.',
 'Úsalo cuando necesites una interfaz simple sobre un subsistema complejo, para estructurar sistemas en capas, o cuando haya demasiadas dependencias entre clientes e implementación.',
 'Clases del Subsistema con lógica específica y detallada. Clase Facade que conoce las clases del subsistema, delega en ellas y ofrece métodos de alto nivel. El cliente interactúa solo con la Facade.',
 'class CPU {
    public void encender() { System.out.println("CPU: encendida"); }
    public void ejecutar() { System.out.println("CPU: ejecutando"); }
}

class Memoria {
    public void cargar() { System.out.println("Memoria: cargada"); }
}

class Disco {
    public void leer() { System.out.println("Disco: datos leídos"); }
}

// Facade: interfaz simple sobre el subsistema complejo
class Computadora {
    private CPU cpu = new CPU();
    private Memoria memoria = new Memoria();
    private Disco disco = new Disco();

    public void encender() {
        System.out.println("--- Iniciando sistema ---");
        cpu.encender();
        memoria.cargar();
        disco.leer();
        cpu.ejecutar();
        System.out.println("--- Sistema listo ---");
    }
}

// Uso: el cliente no conoce CPU, Memoria ni Disco
Computadora pc = new Computadora();
pc.encender();',
 'Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Facade. En Design Patterns: Elements of Reusable Object-Oriented Software (pp. 185-193). Addison-Wesley Professional. ISBN: 978-0-201-63361-0.',
 true, NOW()),

-- ─────────────────────────────────────────
-- COMPORTAMIENTO
-- ─────────────────────────────────────────

('Observer',
 'COMPORTAMIENTO',
 'Define una dependencia de uno a muchos entre objetos, de modo que cuando un objeto cambia de estado, todos sus dependientes son notificados y actualizados automáticamente.',
 'Establecer un mecanismo de notificación automática para que múltiples objetos reaccionen a cambios en otro objeto sin crear dependencias rígidas entre ellos.',
 'Cuando el cambio de estado de un objeto debe desencadenar cambios en otros objetos y no se sabe de antemano cuántos ni cuáles serán esos objetos.',
 'Úsalo para implementar sistemas de eventos, el patrón MVC (modelo notifica a las vistas), notificaciones en tiempo real, suscripciones a cambios de datos, o cualquier sistema con múltiples oyentes.',
 'Interfaz Observador con método actualizar(). Interfaz Sujeto con métodos suscribir(), cancelar() y notificar(). SujetoConcreto almacena el estado y la lista de observadores. ObservadoresConcretos reaccionan al cambio.',
 'interface Observador {
    void actualizar(String evento, Object dato);
}

class TiendaOnline {
    private List<Observador> suscriptores = new ArrayList<>();
    private String ultimoProducto;

    public void suscribir(Observador o) { suscriptores.add(o); }

    public void nuevoProducto(String producto) {
        this.ultimoProducto = producto;
        notificar("NUEVO_PRODUCTO", producto);
    }

    private void notificar(String evento, Object dato) {
        for (Observador o : suscriptores) o.actualizar(evento, dato);
    }
}

class Cliente implements Observador {
    private String nombre;
    public Cliente(String nombre) { this.nombre = nombre; }

    public void actualizar(String evento, Object dato) {
        System.out.println(nombre + " notificado [" + evento + "]: " + dato);
    }
}

// Uso
TiendaOnline tienda = new TiendaOnline();
tienda.suscribir(new Cliente("Ana"));
tienda.suscribir(new Cliente("Bob"));
tienda.nuevoProducto("Teclado mecánico"); // Ana y Bob son notificados',
 'Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Observer. En Design Patterns: Elements of Reusable Object-Oriented Software (pp. 293-303). Addison-Wesley Professional. ISBN: 978-0-201-63361-0.',
 true, NOW()),

('Strategy',
 'COMPORTAMIENTO',
 'Define una familia de algoritmos, encapsula cada uno y los hace intercambiables. Permite que el algoritmo varíe independientemente de los clientes que lo usan.',
 'Separar la definición de algoritmos de su uso, permitiendo intercambiarlos en tiempo de ejecución sin modificar el código cliente.',
 'Una clase implementa múltiples variantes de un comportamiento mediante grandes bloques condicionales. Añadir nuevas variantes obliga a modificar la clase y los condicionales.',
 'Úsalo cuando existan múltiples variantes de un algoritmo que deban intercambiarse, para reemplazar condicionales que seleccionan un comportamiento, o cuando una clase defina muchos comportamientos como múltiples ramas if/else.',
 'Interfaz Estrategia con el método del algoritmo. EstrategiasConcretas implementan distintas variantes. Contexto mantiene referencia a una Estrategia y delega en ella; permite cambiarla en tiempo de ejecución.',
 'interface EstrategiaPago {
    void pagar(double monto);
}

class PagoTarjeta implements EstrategiaPago {
    public void pagar(double monto) {
        System.out.println("Pagando $" + monto + " con tarjeta de crédito.");
    }
}

class PagoPayPal implements EstrategiaPago {
    public void pagar(double monto) {
        System.out.println("Pagando $" + monto + " con PayPal.");
    }
}

class PagoEfectivo implements EstrategiaPago {
    public void pagar(double monto) {
        System.out.println("Pagando $" + monto + " en efectivo.");
    }
}

class Carrito {
    private EstrategiaPago estrategia;

    public void setEstrategia(EstrategiaPago e) { this.estrategia = e; }

    public void checkout(double total) {
        estrategia.pagar(total);
    }
}

// Uso: el algoritmo cambia en tiempo de ejecución
Carrito carrito = new Carrito();
carrito.setEstrategia(new PagoPayPal());
carrito.checkout(59.99);

carrito.setEstrategia(new PagoTarjeta());
carrito.checkout(59.99);',
 'Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Strategy. En Design Patterns: Elements of Reusable Object-Oriented Software (pp. 315-323). Addison-Wesley Professional. ISBN: 978-0-201-63361-0.',
 true, NOW()),

('Template Method',
 'COMPORTAMIENTO',
 'Define el esqueleto de un algoritmo en una operación, difiriendo algunos pasos a las subclases. Permite que las subclases redefinan ciertos pasos sin cambiar la estructura del algoritmo.',
 'Definir la estructura invariante de un algoritmo en la clase base y dejar que las subclases implementen solo las partes que varían.',
 'Múltiples clases implementan algoritmos casi idénticos con pequeñas variaciones, produciendo duplicación de código. Cualquier cambio en la lógica común debe replicarse en cada clase.',
 'Úsalo cuando varias clases tengan algoritmos con la misma estructura pero pasos distintos, para eliminar código duplicado extrayendo la estructura común a una clase base.',
 'Clase abstracta con el método plantilla (final) que define el esqueleto del algoritmo y llama a pasos abstractos. Subclases concretas implementan solo los pasos variables. Opcionalmente, hooks con implementación vacía para personalización optativa.',
 'abstract class ProcesadorDatos {

    // Método plantilla: define el esqueleto — no se sobreescribe
    public final void procesar() {
        leerDatos();
        validarDatos();
        transformarDatos();
        guardarResultado();
    }

    // Pasos abstractos: cada subclase los implementa a su manera
    protected abstract void leerDatos();
    protected abstract void transformarDatos();

    // Paso común a todas las subclases
    private void validarDatos() {
        System.out.println("Validando integridad de los datos...");
    }

    // Hook: implementación por defecto, subclases pueden sobreescribir
    protected void guardarResultado() {
        System.out.println("Guardando en almacenamiento por defecto.");
    }
}

class ProcesadorCSV extends ProcesadorDatos {
    protected void leerDatos() {
        System.out.println("Leyendo archivo CSV...");
    }
    protected void transformarDatos() {
        System.out.println("Transformando columnas CSV a objetos.");
    }
    protected void guardarResultado() {
        System.out.println("Guardando en base de datos relacional.");
    }
}

class ProcesadorJSON extends ProcesadorDatos {
    protected void leerDatos() {
        System.out.println("Consumiendo API REST (JSON)...");
    }
    protected void transformarDatos() {
        System.out.println("Deserializando JSON a objetos.");
    }
}

// Uso
new ProcesadorCSV().procesar();',
 'Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Template Method. En Design Patterns: Elements of Reusable Object-Oriented Software (pp. 325-330). Addison-Wesley Professional. ISBN: 978-0-201-63361-0.',
 true, NOW());
