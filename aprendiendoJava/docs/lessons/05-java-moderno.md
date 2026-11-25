#      Lección 05 — Java moderno: `record`, `enum` con comportamiento, `switch`, `Optional`, `var`

> Ruta: Fase 1 (Java moderno), semana 5 del roadmap.
> Viene de: Lección 04 (ordenar). Prepara: Lección 06 (excepciones y fechas), Streams (semanas 9 y 10, que
> devuelven `Optional`) y Spring (los DTO de una API se escriben con `record`).
> Todos los comportamientos descritos aquí fueron comprobados ejecutando código en Java 21.
> Las salidas que ves en los ejemplos (`// → ...`) son **reales**, copiadas del terminal.

---

## Cómo usar esta lección

Igual que las lecciones 03 y 04: está escrita **desde cero** y no supone que recuerdes nada.

Esta lección trae **cinco herramientas nuevas**. No son difíciles una por una; lo difícil es no mezclarlas. Por
eso cada sección empieza con **🧠 En una frase**: si después de leer la sección no puedes repetir esa frase con
tus palabras, vuelve a leerla antes de seguir.

| Parte | Qué es |
|-------|--------|
| 🧠 **En una frase** | la idea de la sección resumida. Si solo recuerdas una cosa, que sea esta |
| 📖 **Explicación** | la idea en palabras simples |
| 💻 **Ejemplo** | código pequeño con su salida real |
| 🧪 **Pruébalo** | un experimento corto que **tú** escribes y ejecutas (3–8 min). **No te lo saltes** |
| ✅ **¿Lo entendiste?** | 2 o 3 preguntas. La respuesta está escondida debajo: piensa primero y después ábrela |

**Mapa de la lección (para no perderte):**

| Herramienta | Para qué sirve, en palabras de tienda | Sección |
|-------------|---------------------------------------|---------|
| inmutabilidad | objetos que, una vez creados, **no cambian** | 2 |
| `record` | una clase de **solo datos** en una línea (como `Money`) | 3 |
| `enum` con comportamiento | una **lista cerrada** de valores que además **saben cosas** (estados de un pedido) | 4 |
| `switch` moderno | elegir entre varios casos **sin olvidar ninguno** | 5 |
| `Optional` | decir "**puede que no haya resultado**" sin devolver `null` | 6 |
| `var` | dejar que Java **adivine el tipo** de una variable local | 7 |

**Plan de lectura (unas 2 h 15 min en total, en 3 sesiones):**

| Sesión | Lee | Tiempo |
|--------|-----|--------|
| 1 | Secciones 0 (repaso) + 1 + 2 + 3 | 55 min |
| 2 | Secciones 4 + 5 | 45 min |
| 3 | Secciones 6 + 7 + 8 + 9 | 35 min |

Después vienen las **tareas** (sección 10). Están todas publicadas, pero hazlas **una por vez, en orden**.

**Dónde hacer los 🧪:** crea **un solo archivo** `src/practical/modern/ModernPlayground.java` (paquete nuevo
`practical.modern`) con un `main`. Los `record` y `enum` de los 🧪 los puedes escribir **dentro** de esa misma
clase (Java lo permite: se llaman tipos anidados), así no llenas el proyecto de archivos de prueba:
```java
public class ModernPlayground {

    record Libro(String isbn, String titulo, int paginas) {}   // ← un record dentro de la clase

    public static void main(String[] args) {
        System.out.println("---- 🧪 Sección 3 ----");
        // ...
    }
}
```

**La plantilla de siempre:**
```java
System.out.println(algo);
// espero: ...
// real:   ...   ← solo si fallaste
// porque: ...   ← solo si fallaste
```

**Sintaxis nueva y una advertencia importante:** en la sección 5 vas a ver la flecha `->` dentro de un `switch`.
**No es una lambda.** Es la misma flecha dibujada, pero significa otra cosa ("en este caso, haz esto"). Las
lambdas siguen sin pedirse en ninguna tarea (semana 8).

**Si algo no se entiende:** pregúntame por el número de sección ("no entiendo la sección 3.3").

---

## 0. Repaso: lo que necesitas de las lecciones anteriores

Esta lección usa cuatro cosas que ya viste. Las cuatro son **puntos donde te has equivocado antes**, así que
léelas despacio.

### 0.1 Lo que un método **devuelve** vs lo que **hace** (lecciones 01, 03 y 04)

Algunos métodos **no cambian** el objeto: **devuelven uno nuevo**. Si no guardas lo que devuelven, se pierde.
```java
BigDecimal total = new BigDecimal("10");
total.add(new BigDecimal("5"));          // devuelve 15... y nadie lo guarda
System.out.println(total);               // → 10

String nombre = "ana";
nombre.toUpperCase();                    // devuelve "ANA"... y nadie lo guarda
System.out.println(nombre);              // → ana
nombre = nombre.toUpperCase();           // ahora sí: guardas lo que devuelve
System.out.println(nombre);              // → ANA
```
`BigDecimal` y `String` son **inmutables**: ningún método los cambia por dentro. En esta lección **tú** vas a
escribir clases así (sección 2), y en la lección 06 las fechas funcionan igual.

### 0.2 Mismo objeto vs objeto igual (lección 02)

- `a == b` → ¿son **el mismo** objeto (la misma caja en memoria)?
- `a.equals(b)` → ¿**valen lo mismo** según la regla de la clase?

Hasta ahora escribías `equals` y `hashCode` **a mano**. En la sección 3 Java los escribe por ti.

### 0.3 `BigDecimal`: `equals` mira la escala, `compareTo` no (lecciones 03 y 04)

```java
new BigDecimal("2").equals(new BigDecimal("2.00"))      // → false  (escala 0 vs escala 2)
new BigDecimal("2").compareTo(new BigDecimal("2.00"))   // → 0      (mismo valor)
```
Esto vuelve en la sección 3.4 y es la trampa principal de la Tarea 1. **Guárdalo.**

### 0.4 Copia vs vista no modificable (lecciones 02, 03 y 04)

```java
List<String> orig  = new ArrayList<>(List.of("a"));
List<String> vista = Collections.unmodifiableList(orig);   // ventana de solo lectura a orig
List<String> copia = List.copyOf(orig);                     // foto independiente, no modificable
orig.add("b");
System.out.println(vista + " " + copia);                    // → [a, b] [a]
```
La **vista** ve los cambios de la original; la **copia** no. Esto vuelve en la sección 3.5.

---

## 1. El problema: clases que solo guardan datos

### 🧠 En una frase
Muchas clases solo **guardan datos**, y escribirlas a mano (campos, constructor, getters, `equals`, `hashCode`,
`toString`) es largo y fácil de hacer mal. Java moderno trae herramientas para eso.

### 📖 Explicación

Mira lo que escribiste en las lecciones 02 y 03 para `Product` y `Customer`: campos, constructor, getters,
`equals`, `hashCode`, `toString`… **unas 50 líneas**, y en las revisiones encontramos errores en casi todas
(`this == o` olvidado, `id` que podía ser `null`, `hashCode` con un campo distinto que `equals`).

Ahora piensa en un **precio**: `12.50`. ¿Qué hace un precio? **Nada**: solo es un valor. Dos precios de `12.50`
son el mismo precio, aunque sean dos objetos distintos. No tiene `id`, no cambia, no tiene historia. Para eso
existe el `record` (sección 3).

Y piensa en el **estado de un pedido**: pendiente, pagado, enviado, entregado, cancelado. No puede haber un
sexto estado inventado (`"pagadoo"` con un error de dedo). Para eso existe el `enum` (sección 4).

### Puente desde PHP

Si usas PHP 8, ya conoces casi todo esto con otro nombre:

| PHP 8 | Java | Sección |
|-------|------|---------|
| `readonly class Price { public function __construct(public string $amount) {} }` | `record Price(BigDecimal amount) {}` | 3 |
| `enum Status: string { case Paid = 'paid'; }` (PHP 8.1) | `enum Status { PAID }` | 4 |
| `match($x) { 1, 2 => 'a', default => 'b' }` | `switch (x) { case 1, 2 -> "a"; default -> "b"; }` | 5 |
| `?Product` / devolver `null` | `Optional<Product>` | 6 |
| `$x = 5;` (sin tipo) | `var x = 5;` (pero **el tipo existe y no cambia**) | 7 |

> ⚠️ Una diferencia grande con PHP: en PHP, `match` **sin** `default` falla **al ejecutar** si no encuentra el
> caso (`UnhandledMatchError`). En Java, un `switch` que devuelve valor y no cubre todos los casos **no
> compila**. Java te avisa **antes**. Lo vas a ver en la sección 5.5.

---

## 2. Inmutabilidad: objetos que no cambian

### 🧠 En una frase
Un objeto **inmutable** no se puede cambiar después de crearlo; si quieres "otro valor", creas **otro objeto**.

### 📖 Explicación

Ya usas objetos inmutables todos los días sin darte cuenta: `String`, `BigDecimal`, `Integer`, `BigInteger`. Por
eso `total.add(...)` no cambia `total` (sección 0.1).

**¿Por qué querríamos objetos que no cambian?** Tres razones muy concretas:

1. **Nadie te los cambia por la espalda.** Si le pasas un precio a otro método, sabes que cuando vuelve sigue
   valiendo lo mismo.
2. **Son seguros en un `HashSet` / `HashMap`.** En la lección 02 viste que si el `hashCode` de un objeto cambia
   después de meterlo en un `HashSet`, el set lo "pierde". Un objeto inmutable nunca cambia su `hashCode`.
3. **Son más fáciles de entender.** Un objeto que nunca cambia tiene un solo estado. No tienes que preguntarte
   "¿quién lo modificó?".

### 2.1 Receta para que una clase sea inmutable

| # | Regla | Por qué |
|---|-------|---------|
| 1 | todos los campos `private final` | nadie puede reasignarlos, ni siquiera la propia clase |
| 2 | **sin setters** | no hay forma de cambiar un campo desde fuera |
| 3 | validar en el constructor | si no puede cambiar, tiene que nacer correcto |
| 4 | los métodos que "cambian" devuelven un **objeto nuevo** | como `BigDecimal.add` |
| 5 | si un campo es una colección, guardar una **copia no modificable** (`List.copyOf`) | si no, la lista de dentro sí cambia (sección 2.2) |

💻 Ejemplo, a mano (en la sección 3 verás la versión corta):
```java
public final class Temperatura {
    private final BigDecimal grados;                       // regla 1

    public Temperatura(BigDecimal grados) {
        if (grados == null) {                              // regla 3
            throw new IllegalArgumentException("grados no puede ser null");
        }
        this.grados = grados;
    }

    public BigDecimal getGrados() { return grados; }       // getter sí; setter no (regla 2)

    public Temperatura subir(BigDecimal cuanto) {          // regla 4: devuelve OTRA temperatura
        return new Temperatura(grados.add(cuanto));
    }
}
```

### 2.2 ⚠️ `final` protege la **variable**, no el **contenido**

Esta es la confusión más común (y la pregunta P4.2 de la lección 01 que quedó pendiente):
```java
final List<String> l = new ArrayList<>();
l.add("x");                    // ✅ compila y funciona: cambias el CONTENIDO de la lista
System.out.println(l);         // → [x]

l = new ArrayList<>();         // ❌ error: cannot assign a value to final variable l
```
📖 `final` significa *"esta variable siempre apunta a la **misma** lista"*. No dice nada de lo que hay **dentro**
de la lista. Imagina una caja con candado en la tapa pero sin fondo: no puedes cambiar la caja, pero sí meter y
sacar cosas por abajo.

Para que el **contenido** tampoco cambie necesitas una lista no modificable: `List.copyOf(...)` (regla 5).

### 🧪 Pruébalo · sección 2 (4 min)

1. Crea `BigDecimal precio = new BigDecimal("20.00");` y escribe `precio.multiply(new BigDecimal("2"));` sin
   guardar el resultado. Imprime `precio`. `// espero:`
2. Crea `final List<Integer> numeros = new ArrayList<>();`, agrégale `1` y `2`, e imprime. `// espero:` ¿compila?
3. Escribe `numeros = new ArrayList<>();` **sin ejecutar**. Copia el error de IntelliJ en un comentario y borra
   la línea.

### ✅ ¿Lo entendiste?
1. `final List<String> nombres = new ArrayList<>();` ¿Puedo hacer `nombres.add("Luis")`?
2. ¿Por qué un objeto inmutable es más seguro dentro de un `HashSet`?
3. Si `Temperatura` es inmutable, ¿cómo "subes" 2 grados?

<details><summary>Respuestas</summary>

1. **Sí.** `final` impide reasignar la variable (`nombres = otra`), no modificar la lista.
2. Porque su `hashCode` nunca cambia, así que el `HashSet` siempre lo encuentra en su cajón.
3. Creando **otra** temperatura: `Temperatura nueva = t.subir(new BigDecimal("2"));`. La vieja sigue igual.
</details>

---

## 3. `record`: una clase de datos en una línea

### 🧠 En una frase
Un `record` es una clase **inmutable de solo datos**: escribes los campos entre paréntesis y Java genera el
constructor, los "getters", `equals`, `hashCode` y `toString`.

### 3.1 La sintaxis y lo que Java genera por ti

💻
```java
public record Libro(String isbn, String titulo, int paginas) {}
```
Esa **única línea** equivale, más o menos, a todo esto escrito a mano:

| Lo que Java genera | Cómo es |
|--------------------|---------|
| campos | `private final String isbn; private final String titulo; private final int paginas;` |
| constructor | `Libro(String isbn, String titulo, int paginas)` que asigna los tres |
| "getters" (se llaman **accesores**) | `isbn()`, `titulo()`, `paginas()` ← **sin** `get` |
| `equals` | dos `Libro` son iguales si **los tres campos** son iguales (usa `equals` de cada campo) |
| `hashCode` | calculado con **los tres campos** (consistente con `equals`, como pide la lección 02) |
| `toString` | `Libro[isbn=..., titulo=..., paginas=...]` |

Los nombres entre paréntesis se llaman **componentes** del record.

💻 Usándolo (salidas reales):
```java
Libro l1 = new Libro("978-1", "Dune", 412);
Libro l2 = new Libro("978-1", "Dune", 412);

System.out.println(l1);                              // → Libro[isbn=978-1, titulo=Dune, paginas=412]
System.out.println(l1.titulo());                     // → Dune
System.out.println(l1 == l2);                        // → false   (dos objetos distintos)
System.out.println(l1.equals(l2));                   // → true    (mismos datos)
System.out.println(l1.hashCode() == l2.hashCode());  // → true

Set<Libro> s = new HashSet<>();
s.add(l1);
s.add(l2);                                           // "ya está": mismo equals y hashCode
s.add(new Libro("978-1", "Dune", 413));              // otro número de páginas → otro libro
System.out.println(s.size());                        // → 2
```
📖 Fíjate: **no escribiste ni `equals` ni `hashCode`**, y el `HashSet` funciona perfecto. Todo lo que te costó
en la lección 02, el `record` lo hace solo y sin errores.

### 3.2 ⚠️ Los accesores no llevan `get`

```java
l1.getTitulo();
// → error: cannot find symbol
//     symbol:   method getTitulo()
```
El accesor se llama **igual que el componente**: `titulo()`. Es el error más común al empezar con records.

### 3.3 Validar: el constructor compacto

Un `record` acepta cualquier cosa por defecto: `new Libro(null, "", -5)` compila y funciona. Para validar, se
escribe un **constructor compacto**: se llama como el record, **sin paréntesis** después del nombre.

💻
```java
public record Edad(int valor) {

    public Edad {                                    // ← constructor compacto: sin (int valor)
        if (valor < 0) {
            throw new IllegalArgumentException("Edad negativa: " + valor);
        }
    }                                                // ← al terminar, Java asigna this.valor = valor solo

    public boolean esMayor() {                       // un record puede tener métodos normales
        return valor >= 18;
    }

    public Edad cumplir() {                          // "cambiar" = devolver uno nuevo (sección 2.1, regla 4)
        return new Edad(valor + 1);
    }
}
```
```java
Edad e = new Edad(17);
Edad e2 = e.cumplir();
System.out.println(e + " " + e2 + " " + e.esMayor() + " " + e2.esMayor());
// → Edad[valor=17] Edad[valor=18] false true          ← e sigue en 17

new Edad(-3);
// → IllegalArgumentException: Edad negativa: -3
```

**Cómo funciona el constructor compacto, paso a paso:**
1. Java recibe los parámetros (`valor`).
2. Ejecuta **tu** código (las validaciones).
3. Al final, **él solo** copia los parámetros a los campos: `this.valor = valor`.

Por eso **no** puedes escribir `this.valor = ...` dentro:
```java
public record Libro(String titulo) {
    public Libro {
        this.titulo = titulo.trim();
        // → error: cannot assign a value to final variable titulo
    }
}
```
Lo que **sí** puedes hacer es **cambiar el parámetro**, y Java copiará el valor ya cambiado:
```java
public Libro {
    titulo = titulo.trim();        // ✅ cambias el parámetro; al final Java hace this.titulo = titulo
}
```
Esto se llama **normalizar**, y lo vas a necesitar ahora mismo.

### 3.4 ⚠️ La trampa de `BigDecimal` dentro de un record

El `equals` que genera el record usa el **`equals` de cada campo**. Y el `equals` de `BigDecimal` mira la escala
(sección 0.3). Resultado:

💻
```java
public record Medida(BigDecimal metros) {}

new Medida(new BigDecimal("1.5")).equals(new Medida(new BigDecimal("1.50")))
// → false     ← ¡1.5 metros y 1.50 metros "no son iguales"!
```
Para un valor de dinero o una medida, eso está **mal**: `1.5` y `1.50` son lo mismo. La solución es
**normalizar** en el constructor compacto, para que todos los valores tengan la **misma escala**:
```java
public record Medida(BigDecimal metros) {
    public Medida {
        if (metros == null) {
            throw new IllegalArgumentException("metros no puede ser null");
        }
        metros = metros.setScale(2, RoundingMode.HALF_UP);   // siempre 2 decimales
    }
}

new Medida(new BigDecimal("1.5")).equals(new Medida(new BigDecimal("1.50")))   // → true
System.out.println(new Medida(new BigDecimal("1.5")));     // → Medida[metros=1.50]
System.out.println(new Medida(new BigDecimal("2.345")));   // → Medida[metros=2.35]   (redondeó)
```

📖 **`setScale(2, RoundingMode.HALF_UP)`** = "déjalo con 2 decimales; si sobran, redondea como en el colegio
(5 o más sube)". Necesitas `import java.math.RoundingMode;`.

> ⚠️ Si escribes solo `setScale(2)` (sin el modo de redondeo) y el número tiene **más** de 2 decimales:
> `new BigDecimal("2.345").setScale(2)` → `ArithmeticException: Rounding necessary`. Java se niega a redondear
> si no le dices **cómo**.

### 3.5 ⚠️ Inmutable… pero solo por fuera (colecciones dentro de un record)

Los campos de un record son `final`. Pero ya sabes (sección 2.2) que `final` **no protege el contenido** de una
lista:

💻
```java
public record Playlist(String nombre, List<String> canciones) {}

List<String> canciones = new ArrayList<>(List.of("Intro", "Lento"));
Playlist p = new Playlist("Domingo", canciones);

canciones.add("Bonus");          // ← modifico MI lista, la de afuera
System.out.println(p);           // → Playlist[nombre=Domingo, canciones=[Intro, Lento, Bonus]]   😱

p.canciones().add("Hack");       // ← modifico la lista a través del accesor
System.out.println(p);           // → Playlist[nombre=Domingo, canciones=[Intro, Lento, Bonus, Hack]]   😱
```
📖 El record guardó **la misma lista** que le pasaste (no una copia). Cualquiera que tenga esa lista puede
cambiar el record "por dentro". Se dice que el record es inmutable **superficialmente**.

**La solución:** guardar una copia no modificable en el constructor compacto (sección 2.1, regla 5):
```java
public record PlaylistOk(String nombre, List<String> canciones) {
    public PlaylistOk {
        canciones = List.copyOf(canciones);      // copia independiente y no modificable
    }
}

List<String> c2 = new ArrayList<>(List.of("Intro", "Lento"));
PlaylistOk po = new PlaylistOk("Domingo", c2);
c2.add("Bonus");
System.out.println(po);           // → PlaylistOk[nombre=Domingo, canciones=[Intro, Lento]]   ✅ no cambió

po.canciones().add("Hack");       // → UnsupportedOperationException                         ✅ no se puede
```

### 3.6 Lo que un record **no** puede hacer

| Intento | Qué dice Java |
|---------|---------------|
| un setter: `void cambiar() { this.titulo = "b"; }` | `error: cannot assign a value to final variable titulo` |
| un campo extra: `private int ventas;` | `error: field declaration must be static` (sugiere: *consider replacing field with record component*) |
| heredar: `record Libro(...) extends Base {}` | `error: '{' expected` (un record no puede usar `extends`) |
| que otro herede de él | un record es `final`: nadie puede extenderlo |

Lo que **sí** puede: tener métodos, constantes `static` (`public static final Money ZERO = ...`), constructores
compactos e implementar interfaces (`implements Comparable<Libro>`).

### 3.7 ¿`record` o clase? Entidad vs valor

Esta es **la** pregunta de diseño de la lección, y conecta con algo que quedó pendiente en la lección 02 (criterio
de salida 4: entidad vs objeto de valor).

| | **Entidad** | **Objeto de valor** |
|---|-------------|---------------------|
| ¿Qué la identifica? | un **id**: dos con el mismo id son la misma, aunque cambien sus datos | **todos sus datos**: si los datos son iguales, son iguales |
| ¿Cambia con el tiempo? | sí (un cliente cambia de email y sigue siendo el mismo cliente) | no: si cambia, es **otro** valor |
| `equals` | solo por `id` (lo escribiste a mano en `Product`) | por todos los campos (lo que genera el record) |
| Ejemplos | `Product`, `Customer`, `Order` | un precio (`Money`), una dirección, un rango de fechas, un resumen para mostrar |
| En Java | **clase** normal | **`record`** |

📖 Truco para decidir: pregúntate *"si cambio un dato, ¿sigue siendo **el mismo**?"*.
- Un cliente cambia de email → **sigue siendo** el mismo cliente → entidad → clase.
- Un precio pasa de 10 a 12 → **ya no es** el mismo precio, es otro → valor → `record`.

> **Por qué `Product` NO debe ser un record:** el `equals` del record compararía **todos** los campos (id, nombre
> y precio). Si cambias el precio de la laptop, dejaría de ser "igual" a sí misma de antes, y un `HashSet` o
> `HashMap` que la tenga la perdería (lección 02). Además, `Product` tiene subclases (`Laptop`), y un record no
> se puede heredar.

### 🧪 Pruébalo · sección 3 (8 min)

Dentro de `ModernPlayground`, escribe `record Ciudad(String nombre, int habitantes) {}`.
1. Crea `c1 = new Ciudad("Arequipa", 1000000)` y `c2` con los mismos datos. Imprime `c1`, `c1 == c2` y
   `c1.equals(c2)`. `// espero:` en los tres (fíjate en el **formato** del `toString`).
2. Imprime `c1.nombre()`. Después escribe `c1.getNombre()` **sin ejecutar** y copia el error.
3. Agrega un constructor compacto que lance `IllegalArgumentException("habitantes negativos")` si `habitantes < 0`.
   Crea `new Ciudad("Error", -1)` dentro de un `try/catch` e imprime `e` en el `catch`. `// espero:`
4. Escribe `record Precio(BigDecimal valor) {}` **sin** constructor compacto. Imprime
   `new Precio(new BigDecimal("3.0")).equals(new Precio(new BigDecimal("3.00")))`. `// espero:`

### ✅ ¿Lo entendiste?
1. `record Punto(int x, int y) {}`. ¿Cómo obtienes la `x` de un punto `p`?
2. Dentro de un constructor compacto, ¿por qué no puedes escribir `this.x = ...` pero sí `x = ...`?
3. ¿Por qué un record con un `List` dentro no es totalmente inmutable, y cómo se arregla?
4. `Customer`: ¿record o clase? ¿Por qué?

<details><summary>Respuestas</summary>

1. `p.x()` (sin `get`).
2. Porque los campos son `final` y Java los asigna **él solo al final** del constructor compacto. Tú solo puedes
   cambiar el **parámetro** `x`, y Java copia ese valor al campo.
3. Porque el record guarda **la misma** lista que le pasaron: quien tenga esa lista puede cambiarla. Se arregla con
   `lista = List.copyOf(lista);` en el constructor compacto.
4. **Clase**: es una entidad. Un cliente que cambia de email sigue siendo el mismo cliente (mismo `id`).
</details>

---

## 4. `enum` con comportamiento

### 🧠 En una frase
Un `enum` es una **lista cerrada** de valores posibles (no puede haber otros), y cada valor puede tener **datos y
métodos** propios.

### 4.1 Repaso: el `enum` básico (ya tienes uno)

En tu proyecto ya existe `RenewalStatus`:
```java
public enum RenewalStatus { ACTIVE, EXPIRED, CANCELLED }
```
Eso significa: *"un `RenewalStatus` solo puede ser uno de estos tres. No hay un cuarto."*

¿Por qué no usar un `String` como `"active"`? Porque con un `String` nada impide escribir `"actvie"` (error de
dedo) o `"ACTIVE"` en un lado y `"active"` en otro. Con un `enum`, **escribir mal el valor no compila**.

💻 Lo básico (salidas reales, con `enum Talla { S, M, L, XL }`):
```java
System.out.println(Talla.M);                          // → M
System.out.println(Talla.M.name());                   // → M         (el nombre como String)
System.out.println(Talla.M.ordinal());                // → 1         (la posición, empezando en 0)
System.out.println(Arrays.toString(Talla.values()));  // → [S, M, L, XL]   (todos, en orden)
System.out.println(Talla.valueOf("XL"));              // → XL        (de String a enum)

Talla.valueOf("xl");
// → IllegalArgumentException: No enum constant Talla.xl     ← distingue mayúsculas
```

**Para comparar enums se usa `==`**, y está bien (es la excepción a la regla de "objetos con `equals`"): cada
valor de un enum existe **una sola vez** en todo el programa, así que "el mismo valor" = "el mismo objeto".
```java
Talla t1 = Talla.valueOf("L");
System.out.println(t1 == Talla.L);                    // → true
```

**Y no se pueden crear con `new`:**
```java
Talla t = new Talla();
// → error: enum classes may not be instantiated
```

**Orden natural:** los enums ya implementan `Comparable` (lección 04): su orden natural es **el orden en que los
declaraste**.
```java
List<Talla> ts = new ArrayList<>(List.of(Talla.XL, Talla.S, Talla.L, Talla.M));
Collections.sort(ts);
System.out.println(ts);                               // → [S, M, L, XL]   (orden de declaración, no alfabético)
```

### 4.2 Datos y métodos en cada valor

Un enum es **una clase**, así que puede tener campos, un constructor y métodos. La sintaxis tiene 3 partes:

💻
```java
public enum Prioridad {
    BAJA("Baja", 72),                      // ① los valores, cada uno con sus datos entre paréntesis
    MEDIA("Media", 24),
    ALTA("Alta", 4),
    URGENTE("Urgente", 1);                 //    ← ojo: PUNTO Y COMA después del último

    private final String etiqueta;         // ② los campos (final: un enum debe ser inmutable)
    private final int horasMaximas;

    Prioridad(String etiqueta, int horasMaximas) {   // ③ el constructor (sin public)
        this.etiqueta = etiqueta;
        this.horasMaximas = horasMaximas;
    }

    public String getEtiqueta() { return etiqueta; }
    public int getHorasMaximas() { return horasMaximas; }

    public boolean esCritica() {           // un método con lógica
        return this == ALTA || this == URGENTE;
    }
}
```
```java
Prioridad p = Prioridad.ALTA;
System.out.println(p + " " + p.getEtiqueta() + " " + p.getHorasMaximas() + " " + p.esCritica());
// → ALTA Alta 4 true
System.out.println(Prioridad.BAJA.esCritica());       // → false

for (Prioridad x : Prioridad.values()) {
    System.out.println(x.ordinal() + " " + x + " " + x.getHorasMaximas() + "h");
}
// → 0 BAJA 72h
//   1 MEDIA 24h
//   2 ALTA 4h
//   3 URGENTE 1h
```
📖 Cómo leerlo: `BAJA("Baja", 72)` significa *"crea el valor `BAJA` llamando al constructor con `"Baja"` y
`72`"*. Java lo hace **una sola vez**, al cargar el enum. Tú nunca llamas al constructor.

> El constructor de un enum **no puede ser `public`**: `public Prioridad(...)` → `error: modifier public not
> allowed here`. Tiene sentido: nadie de fuera puede crear valores nuevos.

**Dentro** del enum escribes `ALTA` a secas. **Fuera** del enum escribes `Prioridad.ALTA`
(con una excepción: las etiquetas `case` de un `switch`, sección 5).

### 4.3 ⚠️ No uses `ordinal()` para nada importante

`ordinal()` es la **posición** del valor en la declaración. Si mañana alguien agrega `MUY_BAJA` al principio,
todos los `ordinal()` cambian. Si guardaste `ordinal()` en una base de datos o hiciste cuentas con él, todo se
rompe sin ningún error. **Si un valor necesita un número, ponlo en un campo** (como `horasMaximas`).

> **Relación con Spring/JPA (solo para que lo sepas):** por esta razón, cuando guardes un enum en la base de
> datos (semana 18) se usa `@Enumerated(EnumType.STRING)`, que guarda el **nombre** (`"ALTA"`), nunca la posición.

### 4.4 Reglas de negocio dentro del enum: transiciones de estado

Este es el uso más profesional de un enum, y lo que harás en la Tarea 3 con los estados de un pedido. Ejemplo con
**tickets de soporte**: un ticket solo puede pasar a ciertos estados.

```
ABIERTO ──▶ EN_PROGRESO ──▶ RESUELTO ──▶ CERRADO
   │             │              │
   │             └──▶ ABIERTO   └──▶ EN_PROGRESO  (se reabre)
   └──────────────────────────────▶ CERRADO      (se cierra sin trabajar)
CERRADO ──▶ (nada: es final)
```

💻 Las reglas viven **dentro** del enum, en un método (usa el `switch` de la sección 5; vuelve aquí después de
leerla si quieres entender cada línea):
```java
public enum EstadoTicket {
    ABIERTO, EN_PROGRESO, RESUELTO, CERRADO;

    public boolean puedePasarA(EstadoTicket siguiente) {
        return switch (this) {
            case ABIERTO     -> siguiente == EN_PROGRESO || siguiente == CERRADO;
            case EN_PROGRESO -> siguiente == RESUELTO || siguiente == ABIERTO;
            case RESUELTO    -> siguiente == CERRADO || siguiente == EN_PROGRESO;
            case CERRADO     -> false;
        };
    }
}
```
```java
EstadoTicket.ABIERTO.puedePasarA(EstadoTicket.RESUELTO)       // → false  (no se puede saltar EN_PROGRESO)
EstadoTicket.EN_PROGRESO.puedePasarA(EstadoTicket.RESUELTO)   // → true
EstadoTicket.CERRADO.puedePasarA(EstadoTicket.ABIERTO)        // → false
```
📖 **¿Por qué dentro del enum y no en otra clase?** Porque la regla "de qué estado a qué estado se puede pasar" es
**del estado mismo**. Si mañana agregas un estado, el `switch` te obligará a decir sus reglas (sección 5.5) y no
se te olvidará en ningún otro lado. A esto se le llama **cohesión**: los datos y las reglas que los usan, juntos.

### 🧪 Pruébalo · sección 4 (6 min)

Dentro de `ModernPlayground`, escribe `enum Dia { LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO }`.
1. Imprime `Dia.values().length`, `Dia.VIERNES.ordinal()` y `Dia.valueOf("SABADO")`. `// espero:` en los tres.
2. `Dia.valueOf("Sabado")` dentro de un `try/catch (IllegalArgumentException e)`, imprimiendo `e`. `// espero:`
3. Agrega al enum el método `public boolean esFinDeSemana()` (sin `switch`: usa `==` con `||`). Imprime
   `Dia.DOMINGO.esFinDeSemana()` y `Dia.LUNES.esFinDeSemana()`. `// espero:`

### ✅ ¿Lo entendiste?
1. ¿Por qué un `enum` es mejor que un `String` para guardar un estado?
2. ¿Con qué se comparan dos valores de un enum: `==` o `equals`? ¿Por qué está bien?
3. Necesito que cada `Prioridad` tenga un número de horas. ¿Uso `ordinal()`?

<details><summary>Respuestas</summary>

1. Porque solo existen los valores declarados: un error de dedo (`PAIDD`) **no compila**. Con un `String`, compila
   y falla en silencio.
2. `==` (también funciona `equals`). Está bien porque cada valor existe **una sola vez**: el mismo valor es el
   mismo objeto.
3. **No.** `ordinal()` cambia si alguien reordena o agrega valores. Pon las horas en un **campo** con constructor.
</details>

---

## 5. `switch` moderno

### 🧠 En una frase
El `switch` moderno usa `->`, **no "se cae"** al caso siguiente, puede **devolver un valor**, y si le falta un caso
**no compila**.

### 5.1 El problema del `switch` viejo: se "cae" al siguiente caso

El `switch` de siempre (igual que en PHP) tiene una trampa: si olvidas el `break`, **sigue ejecutando** los casos
de abajo. Eso se llama *fall-through*.

💻
```java
static String viejo(int dia) {
    String r = "";
    switch (dia) {
        case 6:
            r += "sábado ";
            // ← falta el break
        case 7:
            r += "domingo ";
            break;
        default:
            r += "laboral ";
    }
    return r;
}

System.out.println(viejo(6) + "|" + viejo(7) + "|" + viejo(3));
// → sábado domingo |domingo |laboral
```
📖 `viejo(6)` entró en `case 6`, agregó `"sábado "`, **no encontró `break`** y siguió con `case 7`. Nadie lo
quería. En PHP pasa exactamente lo mismo.

### 5.2 La flecha `->`: un caso, una acción, sin caerse

💻 El mismo `switch`, moderno:
```java
switch (dia) {
    case 6, 7 -> System.out.println("fin de semana");      // varios valores separados por coma
    case 1, 2, 3, 4, 5 -> System.out.println("laboral");
    default -> System.out.println("día inválido");
}
```
- **No hay `break`** y **nunca se cae** al caso siguiente: cada `->` ejecuta **solo** lo suyo.
- Varios valores en un mismo `case`: `case 6, 7`.

> ⚠️ **Otra vez: esta `->` NO es una lambda.** En un `switch`, `case X -> algo` se lee *"en el caso X, haz
> algo"*. La lambda (semana 8) es otra cosa que comparte el mismo dibujo.

### 5.3 `switch` como expresión: devuelve un valor

La parte más útil: el `switch` puede **dar un resultado**, como el `match` de PHP.

💻
```java
static String tipoDeDia(int dia) {
    return switch (dia) {                      // ← el switch entero ES un valor
        case 6, 7 -> "fin de semana";
        case 1, 2, 3, 4, 5 -> "laboral";
        default -> throw new IllegalArgumentException("día inválido: " + dia);
    };                                         // ← punto y coma: termina la instrucción return
}

System.out.println(tipoDeDia(6) + "|" + tipoDeDia(3));   // → fin de semana|laboral
tipoDeDia(9);                                            // → IllegalArgumentException: día inválido: 9
```
📖 Tres detalles:
1. A la derecha de `->` va **el valor** que devuelve ese caso (sin `return`).
2. Un caso también puede **lanzar** una excepción en vez de dar un valor (`default -> throw ...`).
3. Termina con **`};`**, porque todo el `switch` es parte de la instrucción `return ...;`.

También funciona con `String`:
```java
static int diasDeEntrega(String ciudad) {
    return switch (ciudad) {
        case "Trujillo" -> 1;
        case "Piura", "Chiclayo" -> 2;
        default -> 5;
    };
}
```
> ⚠️ Con `String`, el `case` distingue mayúsculas: `"trujillo"` cae en `default`. Y si el `String` es `null`, el
> `switch` lanza `NullPointerException`.

### 5.4 Un caso con varias líneas: `yield`

Si un caso necesita **más de una línea** de cálculo, se abren llaves `{ }` y el valor se devuelve con **`yield`**:

💻
```java
static int horas(Prioridad p) {
    return switch (p) {
        case BAJA -> 72;
        case MEDIA -> 24;
        case ALTA, URGENTE -> {
            int base = 4;
            if (p == Prioridad.URGENTE) {      // ← fuera del enum: Prioridad.URGENTE (no URGENTE a secas)
                base = 1;
            }
            yield base;                        // ← "este es el valor de este caso"
        }
    };
}
System.out.println(horas(Prioridad.URGENTE) + " " + horas(Prioridad.ALTA));   // → 1 4
```
📖 **`yield` = el `return` de un caso del `switch`.** No se usa `return` ahí, porque `return` saldría del
**método** entero. Si te olvidas el `yield` y solo escribes el valor:
```java
case 1 -> { int x = 5; x; }
// → error: not a statement
```

Fíjate en los `case BAJA`, `case ALTA`: en las etiquetas `case` de un enum se escribe el valor **a secas**, sin
`Prioridad.`; Java ya sabe de qué enum se trata. Pero en el `if (p == Prioridad.URGENTE)` sí hace falta
(probado: sin `Prioridad.` da `error: cannot find symbol`).

### 5.5 Exhaustividad: si falta un caso, no compila

Cuando el `switch` **devuelve un valor**, Java exige que **todos** los valores posibles tengan respuesta. Con un
`enum`, Java sabe cuáles son, así que **no hace falta `default`**… pero si te falta uno, te avisa:

💻
```java
enum Talla { S, M, L, XL }

static String describir(Talla t) {
    return switch (t) {
        case S -> "chica";
        case M -> "mediana";
        // faltan L y XL
    };
}
// → error: the switch expression does not cover all possible input values
```
```java
static String describir(Talla t) {
    return switch (t) {
        case S -> "chica";
        case M -> "mediana";
        case L, XL -> "grande";                // ✅ todos cubiertos, sin default
    };
}
```
📖 **Por qué esto es oro:** si mañana agregas `XXL` al enum, **todos** los `switch` que no la contemplen dejan de
compilar, y el compilador te lleva de la mano a cada lugar que tienes que actualizar. Con un `default`, el `XXL`
caería ahí en silencio. Por eso, **con enums, no pongas `default`** en un switch que devuelve valor.

Con `String` o `int` no hay forma de cubrir "todos" los valores, así que ahí el `default` es obligatorio:
```java
switch (s) { case "a" -> "uno"; case "b" -> "dos"; }   // (como expresión)
// → error: the switch expression does not cover all possible input values
```

### 🧪 Pruébalo · sección 5 (8 min)

Usa el `enum Dia` de la sección 4.
1. Escribe un `switch` **viejo** (con `:`) que, para `SABADO` agregue `"compras "` **sin `break`**, para `DOMINGO`
   agregue `"descanso "` con `break`, y en `default` agregue `"trabajo "`. Imprime el resultado para `SABADO`,
   `DOMINGO` y `LUNES`. `// espero:` en los tres (¡ojo con el `SABADO`!).
2. Escribe `static String tipo(Dia d)` con un `switch` **expresión** (`->`), sin `default`, que devuelva
   `"fin de semana"` o `"laboral"`. Imprime `tipo(Dia.MIERCOLES)`. `// espero:`
3. Borra `SABADO, DOMINGO` de tu `switch` y mira el error. Cópialo en un comentario y vuelve a ponerlos.
4. Escribe `static int horasDeTrabajo(Dia d)`: `VIERNES` → un bloque con `yield` que empiece en `8` y le reste
   `2` (sale temprano); los otros días laborales → `8`; fin de semana → `0`. Imprime para `VIERNES` y `LUNES`.

### ✅ ¿Lo entendiste?
1. ¿Qué pasa en un `switch` viejo si olvidas el `break`?
2. ¿Por qué un `switch` expresión sobre un `enum` no debería tener `default`?
3. Dentro de un caso con llaves `{ }`, ¿por qué se usa `yield` y no `return`?

<details><summary>Respuestas</summary>

1. Se "cae": ejecuta también los casos de abajo hasta encontrar un `break` o el final.
2. Porque si cubres todos los valores sin `default`, cuando alguien agregue un valor nuevo al enum el `switch`
   **dejará de compilar** y te obligará a decidir qué hacer con él. Con `default`, caería ahí sin avisar.
3. Porque `return` saldría de **todo el método**. `yield` solo da el valor de **ese caso** del `switch`.
</details>

---

## 6. `Optional`: "puede que no haya resultado"

### 🧠 En una frase
Un `Optional<T>` es una **cajita** que tiene **un** valor o está **vacía**; un método que devuelve `Optional`
avisa en su firma que **puede no encontrar nada**, y te obliga a pensar qué hacer en ese caso.

### 6.1 El problema: `null` no avisa

En la lección 04 (Tarea 6), `getCheapest()` con el catálogo vacío te obligó a decidir: ¿excepción? ¿`null`? Y en
la lección 01, tu `findByName` lanzaba una excepción con la lista vacía.

El problema de devolver `null`:
```java
Product p = catalog.findByNameViejo("tablet");   // devuelve null si no existe
System.out.println(p.getPrice());                 // → NullPointerException
```
Nada en la firma `Product findByNameViejo(String name)` te avisa de que puede devolver `null`. Te enteras cuando
explota, normalmente en producción.

Con `Optional`, la firma **lo dice**:
```java
Optional<Product> findByName(String name)    // "puede que no encuentre nada; tú decides qué hacer"
```

### 6.2 Crear un `Optional` (esto lo escribes **dentro** del método que busca)

| Método | Cuándo | Ejemplo |
|--------|--------|---------|
| `Optional.of(valor)` | tienes un valor y **seguro no es `null`** | `return Optional.of(producto);` |
| `Optional.empty()` | no encontraste nada | `return Optional.empty();` |
| `Optional.ofNullable(valor)` | tienes algo que **puede** ser `null` (ej. lo que devuelve `map.get`) | `return Optional.ofNullable(mapa.get(k));` |

💻 (salidas reales)
```java
Optional<String> o1 = Optional.of("ana");
Optional<String> o2 = Optional.empty();
Optional<String> o3 = Optional.ofNullable(null);
System.out.println(o1 + " " + o2 + " " + o3);     // → Optional[ana] Optional.empty Optional.empty

Optional.of(null);                                 // → NullPointerException
```
📖 `Optional.of(null)` explota: `of` significa *"te prometo que hay valor"*. Si no estás seguro, usa
`ofNullable`.

💻 Un método de búsqueda típico (con un `Map`, como en la lección 03):
```java
static Optional<String> buscarCiudad(Map<String, String> ciudadPorDni, String dni) {
    return Optional.ofNullable(ciudadPorDni.get(dni));    // get devuelve null si no está
}
```
Y con una lista, recorriendo (la forma que usarás en la Tarea 5):
```java
static Optional<String> buscarEmpiezaCon(List<String> nombres, String prefijo) {
    for (String n : nombres) {
        if (n.startsWith(prefijo)) {
            return Optional.of(n);        // encontré: lo devuelvo en la cajita y salgo
        }
    }
    return Optional.empty();              // terminé de recorrer y no encontré
}
```

### 6.3 Usar un `Optional` (esto lo escribes en **quien llama** al método), sin lambdas

| Método | Qué hace | Si está vacío |
|--------|----------|---------------|
| `isPresent()` | `true` si hay valor | `false` |
| `isEmpty()` | `true` si **no** hay valor | `true` |
| `get()` | saca el valor | ⚠️ lanza `NoSuchElementException: No value present` |
| `orElse(otro)` | saca el valor, o `otro` si está vacío | devuelve `otro` |
| `orElseThrow()` | igual que `get()`, pero el nombre deja claro que puede lanzar | ⚠️ lanza `NoSuchElementException` |

💻 (salidas reales, con el `Map` de arriba: `{"111" → "Trujillo"}`)
```java
Optional<String> r1 = buscarCiudad(ciudades, "111");
Optional<String> r2 = buscarCiudad(ciudades, "999");
System.out.println(r1 + " " + r2);                 // → Optional[Trujillo] Optional.empty

// Forma 1: preguntar y después sacar (la más clara al empezar)
if (r1.isPresent()) {
    System.out.println("vive en " + r1.get());     // → vive en Trujillo
}

// Forma 2: un valor por defecto
String ciudad = r2.orElse("desconocida");
System.out.println(ciudad);                        // → desconocida

// ⚠️ get() sin preguntar antes
r2.get();                                          // → NoSuchElementException: No value present
```
📖 **La regla:** **nunca** llames `get()` sin haber preguntado antes `isPresent()` (o sin estar 100 % seguro). Si
lo haces, cambiaste un `NullPointerException` por un `NoSuchElementException`: no ganaste nada.

> **NO RELEVANTE AHORA (semana 8):** en código profesional verás `opt.ifPresent(...)`, `opt.map(...)` u
> `opt.orElseThrow(() -> new MiExcepcion())`. Todos usan lambdas. Con `isPresent` / `get` / `orElse` puedes
> hacer todo lo de esta lección.

### 6.4 ⚠️ Trampas de `Optional`

**Trampa 1 — `orElse(...)` siempre calcula su argumento**, aunque haya valor:
```java
static String valorPorDefecto() {
    System.out.println("  (se ejecutó valorPorDefecto)");
    return "nadie";
}

Optional<String> o1 = Optional.of("ana");
System.out.println(o1.orElse(valorPorDefecto()));
// →   (se ejecutó valorPorDefecto)          ← ¡se ejecutó aunque o1 tenía "ana"!
//   ana
```
📖 Java primero calcula **todos los argumentos** de un método y después lo llama. `orElse` recibe un valor ya
calculado. Si ese cálculo es caro (consultar la base de datos), lo pagas siempre. Con valores simples
(`"desconocida"`, `0`) no importa.

**Trampa 2 — un método `Optional` que devuelve `null`:** compila, y es lo peor que puedes hacer:
```java
static Optional<String> buscarMal(String x) { return null; }   // compila 😱

Optional<String> resultado = buscarMal("x");
resultado.isPresent();
// → NullPointerException: Cannot invoke "java.util.Optional.isPresent()" because "resultado" is null
```
Un método que devuelve `Optional` **jamás** devuelve `null`. Si no hay nada, `Optional.empty()`.

### 6.5 Cuándo **NO** usar `Optional` (pregunta de entrevista)

`Optional` se pensó para **una sola cosa**: el **tipo de retorno** de un método que puede no encontrar nada.

| ¿Dónde? | ¿`Optional`? | Qué hacer en su lugar |
|---------|--------------|-----------------------|
| retorno de un método de búsqueda (`findByName`, `findById`) | ✅ **sí** | — |
| un **campo** de una clase (`private Optional<String> telefono`) | ❌ no | campo normal que puede ser `null`, y un getter que devuelva `Optional.ofNullable(telefono)` |
| un **parámetro** (`void enviar(Optional<String> nota)`) | ❌ no | dos métodos (sobrecarga) o aceptar `null` y documentarlo |
| una **colección** (`Optional<List<Product>>`) | ❌ no | devolver una **lista vacía**: "no hay resultados" ya se dice con `isEmpty()` |
| `Optional<Optional<...>>` | ❌ jamás | — |

> **Relación con Spring (semana 18):** el `JpaRepository` que vas a usar tiene `Optional<T> findById(ID id)`.
> Exactamente esto. Y un buscador que devuelve muchos, `List<T> findByName(...)`, devuelve una lista vacía, no
> un `Optional`.

### 🧪 Pruébalo · sección 6 (6 min)

1. Crea `List<String> correos = List.of("ana@mail.com", "luis@mail.com", "bea@mail.com");` y escribe
   `static Optional<String> buscarPorInicial(List<String> correos, String inicial)` (recorriendo, como en la
   sección 6.2).
2. Imprime `buscarPorInicial(correos, "l")` y `buscarPorInicial(correos, "z")`. `// espero:` (el formato exacto).
3. Con el resultado de `"z"`, imprime `orElse("sin correo")`. `// espero:`
4. Con el resultado de `"z"`, llama `get()` dentro de un `try/catch (NoSuchElementException e)` e imprime `e`.
   `// espero:`

### ✅ ¿Lo entendiste?
1. ¿Qué diferencia hay entre `Optional.of(x)` y `Optional.ofNullable(x)`?
2. ¿Qué tiene de malo `opt.get()` sin preguntar antes?
3. Un método busca **todos** los productos de una categoría. ¿Devuelve `Optional<List<Product>>`?

<details><summary>Respuestas</summary>

1. `of` exige que `x` no sea `null` (si lo es, `NullPointerException`); `ofNullable` acepta `null` y da un
   `Optional.empty()`.
2. Si está vacío lanza `NoSuchElementException`. Cambiaste un error por otro: hay que preguntar `isPresent()` o
   usar `orElse`.
3. **No.** Devuelve `List<Product>`, y si no hay ninguno, una lista **vacía**.
</details>

---

## 7. `var`: que Java adivine el tipo

### 🧠 En una frase
`var` deja que Java **deduzca** el tipo de una variable **local** a partir de lo que le asignas; el tipo **sigue
existiendo y no cambia** (no es PHP).

### 📖 Explicación

```java
var precio = new BigDecimal("9.90");          // Java deduce: BigDecimal
var cantidad = 3;                             // int
var nombres = new ArrayList<String>();        // ArrayList<String>
```
Es **lo mismo** que escribir el tipo: Java lo deduce **al compilar** y después la variable tiene ese tipo para
siempre. No es como PHP, donde `$x` puede ser un número y después un texto:
```java
var total = 0;        // int
total = 2.5;
// → error: incompatible types: possible lossy conversion from double to int
```

### 7.1 Dónde **no** se puede usar

| Intento | Error real |
|---------|------------|
| `var x;` (sin valor) | `cannot infer type for local variable x` (`cannot use 'var' on variable without initializer`) |
| `var x = null;` | `cannot infer type for local variable x` (`variable initializer is 'null'`) |
| como campo: `private var nombre = "a";` | `'var' is not allowed here` |
| como parámetro o tipo de retorno | no compila: `var` es **solo para variables locales** |

### 7.2 ⚠️ `var` con `<>` vacío pierde el tipo

```java
var l = new ArrayList<>();          // ¿ArrayList de qué? Java no tiene pistas → ArrayList<Object>
l.add("hola");
String s = l.get(0);
// → error: incompatible types: Object cannot be converted to String
```
Con `var`, escribe el tipo **en el lado derecho**: `var l = new ArrayList<String>();`.

### 7.3 ¿Cuándo usarlo? (opinión profesional)

- ✅ **Sí**, cuando el tipo se ve en la misma línea: `var cliente = new Customer(...)`,
  `var mapa = new LinkedHashMap<String, Integer>()`.
- ❌ **No**, cuando el tipo **no** se ve: `var x = catalog.find(...)` (¿qué devuelve? ¿un `Product`? ¿un
  `Optional`?). Quien lea tu código tendría que ir a buscarlo.
- ❌ **No** con interfaces: `var lista = new ArrayList<String>()` declara un `ArrayList`, no una `List`. En la
  lección 01 aprendiste a declarar con la interfaz (`List<String> lista = new ArrayList<>()`). Para campos y
  parámetros, eso sigue siendo lo correcto.

> En las tareas, `var` es **opcional**: úsalo solo en el paso donde se pide.

### 🧪 Pruébalo · sección 7 (3 min)

1. Escribe `var mitad = 7 / 2;` y `var mitad2 = 7 / 2.0;`. Imprime los dos. `// espero:` (¿qué tipo deduce Java
   en cada uno?)
2. Escribe `var vacio;` **sin ejecutar**, copia el error y borra la línea.

### ✅ ¿Lo entendiste?
1. `var x = 10;` y después `x = "diez";`. ¿Compila?
2. ¿Puedo usar `var` en un campo de una clase?

<details><summary>Respuestas</summary>

1. **No.** `x` es `int` para siempre: `incompatible types: String cannot be converted to int`.
2. No: solo en variables **locales** (dentro de un método).
</details>

---

## 8. Resumen

### ¿Qué uso?

| Quiero… | Uso | Sección |
|---------|-----|---------|
| una clase de solo datos, sin id, que no cambia (precio, dirección, resumen) | `record` | 3 |
| validar o normalizar los datos de un record | constructor compacto (`public Nombre { ... }`) | 3.3 |
| que `1.5` y `1.50` sean iguales en un record | `setScale(2, RoundingMode.HALF_UP)` en el constructor compacto | 3.4 |
| que un record con lista no se pueda cambiar por dentro | `lista = List.copyOf(lista);` en el constructor compacto | 3.5 |
| algo con id que cambia con el tiempo (producto, cliente, pedido) | **clase** normal | 3.7 |
| una lista cerrada de valores | `enum` | 4 |
| que cada valor del enum tenga datos | campos `final` + constructor (sin `public`) | 4.2 |
| reglas de "de qué estado a qué estado" | un método en el enum con `switch` | 4.4 |
| elegir entre casos y devolver un valor | `switch` expresión con `->` | 5.3 |
| varias líneas en un caso | `{ ... yield valor; }` | 5.4 |
| un método que puede no encontrar nada | devolver `Optional<T>` | 6 |
| sacar el valor de un `Optional` | `isPresent()` + `get()`, u `orElse(...)` | 6.3 |
| no repetir un tipo largo en una variable local | `var` (si el tipo se ve en la misma línea) | 7 |

### Errores comunes

| Error | Qué pasa | Solución |
|-------|----------|----------|
| `libro.getTitulo()` en un record | `cannot find symbol` | `libro.titulo()` |
| `this.campo = ...` en el constructor compacto | `cannot assign a value to final variable` | asignar al **parámetro**: `campo = ...` |
| `BigDecimal` en un record sin normalizar | `1.5` no es `equals` a `1.50` | `setScale(2, RoundingMode.HALF_UP)` |
| `setScale(2)` sin modo de redondeo con 3 decimales | `ArithmeticException: Rounding necessary` | pasar `RoundingMode.HALF_UP` |
| record con `List` sin copiar | lo cambian desde fuera | `List.copyOf` en el constructor compacto |
| hacer `record` una entidad (`Product`) | `equals` por todos los campos; se pierde en un `HashSet` al cambiar el precio | entidad = clase con `equals` por id |
| `m.add(otro);` sin guardar | el objeto inmutable no cambió | `m = m.add(otro);` o `Money total = m.add(otro);` |
| `Talla.valueOf("xl")` | `IllegalArgumentException: No enum constant ...` | el nombre exacto, en mayúsculas |
| usar `ordinal()` para lógica o para guardar | se rompe al reordenar el enum | un campo propio |
| `switch` viejo sin `break` | se "cae" al caso siguiente | `switch` con `->` |
| `default` en un switch de enum | un valor nuevo cae ahí sin avisar | cubrir todos los casos, sin `default` |
| `return` dentro de `{ }` de un caso | sale del método | `yield` |
| `Optional.of(null)` | `NullPointerException` | `Optional.ofNullable` |
| `opt.get()` sin preguntar | `NoSuchElementException: No value present` | `isPresent()` antes, u `orElse` |
| método `Optional` que devuelve `null` | `NullPointerException` en quien llama | `Optional.empty()` |
| `Optional` en campos, parámetros o listas | código raro, sin ganar nada | solo como **retorno**; listas vacías |
| `var l = new ArrayList<>();` | `ArrayList<Object>` | `new ArrayList<String>()` |

### Relación con Spring Boot (solo para que sepas que existe)

- **DTO con `record`:** en una API REST, lo que entra (`CreateProductRequest`) y lo que sale (`ProductResponse`)
  se escriben como records. Spring los convierte a JSON y desde JSON solo. Es exactamente el `OrderSummary` de la
  Tarea 6.
- **`Optional` en repositorios:** `productRepository.findById(id)` devuelve `Optional<Product>`.
- **Enums en entidades:** `@Enumerated(EnumType.STRING) private OrderStatus status;` guarda `"PAID"` en la base de
  datos (nunca el `ordinal`).
- **Reglas en el enum:** el `canTransitionTo` de la Tarea 3 es lo que un `OrderService` llamará antes de cambiar
  el estado de un pedido y guardarlo.

### Glosario

| Palabra | Significado |
|---------|-------------|
| **inmutable** | que no puede cambiar después de creado; para "cambiarlo" se crea otro objeto |
| **record** | clase inmutable de solo datos; Java genera constructor, accesores, `equals`, `hashCode`, `toString` |
| **componente** | cada dato declarado entre los paréntesis de un record |
| **accesor** | el "getter" de un record: se llama igual que el componente, sin `get` |
| **constructor compacto** | el constructor de un record sin lista de parámetros; sirve para validar y normalizar |
| **normalizar** | dejar un dato siempre en la misma forma (ej. siempre 2 decimales) |
| **inmutable superficialmente** | los campos no se reasignan, pero el contenido de una lista dentro sí podría cambiar |
| **entidad** | objeto con identidad (`id`) que puede cambiar sus datos y seguir siendo el mismo |
| **objeto de valor** | objeto definido solo por sus datos; si cambian los datos, es otro valor |
| **fall-through** | en el `switch` viejo, seguir ejecutando el caso de abajo por falta de `break` |
| **exhaustivo** | un `switch` que cubre todos los valores posibles |
| **`yield`** | da el valor de un caso con llaves en un `switch` expresión |
| **cohesión** | poner juntos los datos y las reglas que los usan (las transiciones dentro del enum) |

---

## 9. Autoevaluación final (sin mirar)

Respóndelas en un comentario al final de `ModernPlayground.java`. **Cada pregunta tiene dos partes**: responde
**las dos** (es lo que más se te escapa). Si **8 de 10** te salen solas, pasa a las tareas. Si no, dime cuáles
fallaste.

1. ¿Qué genera Java por ti cuando escribes `record Punto(int x, int y) {}`? Nombra **cuatro** cosas y di cómo se
   llama el accesor de `x`.
2. En un constructor compacto, ¿qué **no** puedes escribir y qué **sí** puedes escribir para cambiar un dato?
3. `record Precio(BigDecimal valor)`: ¿`new Precio(new BigDecimal("5.0")).equals(new Precio(new BigDecimal("5.00")))`
   da `true` o `false`? ¿Por qué, y cómo se arregla?
4. Un record tiene un campo `List<String>`. ¿Cómo lo pueden cambiar desde fuera (**dos** formas) y cómo se
   impide?
5. Entidad vs objeto de valor: da **un ejemplo de cada uno** de tu proyecto y di cuál es `record`.
6. ¿Por qué un `enum` es mejor que un `String` para un estado, y por qué no se usa `ordinal()` para la lógica?
7. ¿Qué es el *fall-through* del `switch` viejo, y por qué el `switch` con `->` no lo tiene?
8. ¿Por qué un `switch` expresión sobre un enum no debería tener `default`? ¿Qué pasa si agregas un valor nuevo
   al enum?
9. ¿Cuándo devuelves `Optional` y en qué **tres** lugares **no** se usa?
10. ¿Qué pasa con `opt.get()` si está vacío, y qué dos alternativas seguras tienes?

> ## 🔍 REVISIÓN AUTOEVALUACIÓN + 🧪 — 2026-11-21 · **5/10** · 🔁 repite 6 preguntas cortas y 2 🧪 antes de la Tarea 1
> Historial: **v1 5/10**. Revisado leyendo `ModernPlayground` y ejecutándolo con Java 21.

**✅ Ya está bien:** los 🧪 de las secciones 2, 3, 4, 6 y 7 están hechos y compilan. Acertaste `c1 == c2` false /
`equals` true, el `IllegalArgumentException` del constructor compacto, el `false` de `Precio` 3.0 vs 3.00, el
`ordinal()` 4, el `NoSuchElementException` y los dos `var` (3 y 3.5). En la autoevaluación, las preguntas 6, 8 y 10
están bien entendidas.

### Autoevaluación

| # | Tu respuesta | Nota | Qué falta |
|---|--------------|------|-----------|
| 1 | toString, hashCode, equals, getters; accesor `record.x` | ½ | las 4 cosas ✅ (aunque se llaman **accesores**, no getters). El accesor de `x` es **`p.x()`**: un método, con paréntesis y sin `get` (sección 3.2) |
| 2 | "no puedo escribir el constructor completo; sí validar" | ❌ 0 | ver IMPORTANTE 1 |
| 3 | `false`; 5.0 y 5.00 son distintos; quitar ceros al comparar | ¾ | el **por qué** exacto: el `equals` del record usa el `equals` de `BigDecimal`, que mira la **escala**. El arreglo no es "al comparar" sino **al crear**: en el constructor compacto, `valor = valor.setScale(2, RoundingMode.HALF_UP);` (sección 3.4) |
| 4 | por el get; `Collections.unmodifiableList` | ½ | ver IMPORTANTE 2 |
| 5 | "nose!!" | ❌ 0 | ver IMPORTANTE 3 |
| 6 | no compila si te equivocas; ordinal cambia (sobre todo en la BD) | ✅ 1 | |
| 7 | "no necesita break" | ½ | falta la **primera** parte: ¿qué *es* el fall-through? |
| 8 | con `default` el valor nuevo cae ahí y el compilador no avisa | ¾ | ✅ la idea. Falta decir qué pasa **sin** `default`: el `switch` **deja de compilar** y te obliga a cubrir el valor nuevo |
| 9 | "cuando son list vacíos; cuando el método devuelve valores diferentes de null" | ❌ 0 | ver IMPORTANTE 4 |
| 10 | sale un error; preguntar si está vacío u `orElse` | ✅ 1 | el error tiene nombre: `NoSuchElementException: No value present` (lo imprimiste tú en el 🧪 6) |

**Total: 5/10.** Para pasar a las tareas hacen falta 8.

---

#### 🟠 IMPORTANTE 1 — Pregunta 2: qué se puede escribir dentro del constructor compacto

**Qué pasa:** la pregunta no es "¿qué constructor puedo escribir?", sino "dentro del constructor compacto, ¿qué
**línea** no compila y cuál sí, para cambiar un dato?".

**Teoría (sección 3.3):** al terminar el constructor compacto, Java hace solo `this.titulo = titulo`. Por eso:
```java
public Libro {
    this.titulo = titulo.trim();   // ❌ cannot assign a value to final variable titulo
    titulo = titulo.trim();        // ✅ cambias el PARÁMETRO; Java copia el valor ya cambiado al campo
}
```
**Respuesta modelo (llena tú los huecos):** *"No puedo escribir `this.____ = ...` porque el campo es ____. Sí
puedo escribir `____ = ...`, que cambia el ____, y Java lo copia al campo al final."*

#### 🟠 IMPORTANTE 2 — Pregunta 4: dos formas de cambiarlo, y `unmodifiableList` es una **vista**

**Qué pasa:** diste una forma (por el accesor). La otra es **con la lista original**: quien creó el record
todavía tiene esa lista en una variable y puede hacerle `add`.

Y `Collections.unmodifiableList` **no basta**: es una *vista* (sección 0.4), no una copia. Bloquea el `add` por el
accesor, pero si cambian la lista original, el record también cambia. Comprobado:
```java
record L(List<String> xs) { L { xs = Collections.unmodifiableList(xs); } }

List<String> src = new ArrayList<>(List.of("pan"));
L l = new L(src);
src.add("huevos");
System.out.println(l);    // → L[xs=[pan, huevos]]   😱 cambió
```
**La solución es `List.copyOf(...)`** en el constructor compacto: copia independiente **y** no modificable (sección
3.5). Este es el mismo punto débil de la lección 02 (copia vs vista), y la Tarea 2 lo va a probar.

#### 🟠 IMPORTANTE 3 — Pregunta 5: entidad vs valor

Está en la sección 3.7, con tu propio proyecto. Aplica el truco *"si cambio un dato, ¿sigue siendo el mismo?"*:
- Un `Customer` cambia de email → ¿sigue siendo el mismo cliente? → ______ → ¿clase o record?
- Un precio `Money` pasa de 10 a 12 → ¿sigue siendo el mismo precio? → ______ → ¿clase o record?

Responde con **un** ejemplo de cada uno y di cuál es `record`.

#### 🟠 IMPORTANTE 4 — Pregunta 9: dónde sí y dónde no va `Optional`

**Qué pasa:** se mezclaron las ideas. `Optional` tiene **un solo** uso (sección 6.5): el **tipo de retorno** de
un método que **busca un solo resultado** y puede no encontrarlo (`findById`, `findByName`).

Y **no** se usa en **tres** lugares. Lo de "lista vacía" que escribiste es uno de ellos, pero al revés: un método
que devuelve **muchos** devuelve `List`, y si no hay nada, una lista **vacía**, nunca `Optional<List<...>>`. Los
otros dos están en la tabla de la sección 6.5 (pista: son dos cosas que se declaran en una clase o en un método).

#### 🟡 MENOR — Pregunta 7

Respuesta en dos partes: *"Fall-through es cuando el `switch` viejo, al no encontrar `break`, ______. El `switch`
con `->` no lo tiene porque ______."*

---

### Los 🧪

| Sección | Estado | Qué falta |
|---------|--------|-----------|
| 2 | ½ | paso 1: el experimento era **no guardar** el resultado e imprimir **`precio`**. Tú imprimiste `precio.multiply(...)` (40.00). Imprime `precio` y verás `20.00`: eso es lo que demuestra que `BigDecimal` es inmutable |
| 3 | ✅ casi | falta imprimir `c1` (el formato del `toString` era lo importante). Escribiste `c1.nombre` (el **campo**, compila solo porque el record está **dentro** de tu clase); el paso pedía el accesor `c1.nombre()` |
| 4 | ½ | paso 1 pedía `Dia.values().length` (→ 7), no `values()`. Paso 3: `esFinDeSemana()` **sin parámetro**: usa `this` (`this == SABADO \|\| this == DOMINGO`). El tuyo recibe otro `Dia` y no tiene sentido (`Dia.SABADO.esFinDeSemana(Dia.LUNES)` → false). Y falta imprimir `DOMINGO` y `LUNES` |
| 5 | ❌ | no está hecho (dejaste `// toca: switch modermo`) |
| 6 | ❌ bug | ver CRÍTICO abajo |
| 7 | ✅ casi | falta el paso 2 (`var vacio;` y copiar el error) |

#### 🔴 CRÍTICO — 🧪 6: tu `buscarPorInicial` nunca encuentra nada, y tu `espero` acertó por culpa del bug

```java
for (String correo : correos) {
    if (correos.equals(inicial)) {        // ← compara la LISTA entera con "l"
        return Optional.of(inicial);      // ← devolvería "l", no el correo
    }
}
```
- `correos.equals("l")` compara una `List` con un `String` → **siempre false** (comprobado). Por eso `"l"` dio
  `Optional.empty`.
- Lo correcto para `"l"` es **`Optional[luis@mail.com]`**: `luis@mail.com` empieza con `l`. Tu `espero:
  optional.empty` estaba mal, pero el bug hizo que coincidiera. **Cuando tu predicción y la salida coinciden,
  igual revisa si el código hace lo que pide el enunciado.**
- Dos arreglos: usa la variable del `for` (`correo`, singular) con `startsWith(inicial)`, y devuelve **el correo**,
  no la inicial.
- Falta el `espero` del paso 3 (`orElse`).

---

### Para llegar a 8/10 (y empezar la Tarea 1)
- [ ] Rehacer preguntas **1, 2, 4, 5, 7 y 9** debajo de las tuyas, como `v2` (no borres las de v1)
- [ ] 🧪 6: arreglar `buscarPorInicial`, volver a predecir `"l"` y ejecutar
- [ ] 🧪 5 completo (switch viejo sin `break`, `tipo(Dia)`, error al quitar casos, `yield`)
- [ ] 🧪 2 paso 1 e 🧪 4 pasos 1 y 3 (los arreglos cortos de la tabla)

---

## 10. Tareas

### Antes de empezar: lo que queda abierto de la lección 04

Esta lección usa el `Catalog` de la **Tarea 6 de la lección 04** (en la Tarea 5 de aquí). Si saltaste esa tarea
(como se decidió el 2026-11-17), **antes de la Tarea 5** crea un `Catalog` **mínimo** (10 min) en
`src/Models/Catalog.java`, sin `main`:
- un campo `private final List<Product> products = new ArrayList<>();`
- `boolean addProduct(Product product)`: agrega y devuelve lo que devuelve `add`
- `List<Product> getProducts()`: devuelve una **copia no modificable** (`List.copyOf`, sección 0.4)
- `Product getCheapest()`: el más barato con `Collections.min(products, new ProductPriceComparator())`

Con eso la Tarea 5 funciona igual. Donde la Tarea 5 menciona `CatalogMain`, ignóralo: no lo tienes. Las correcciones de las tareas 1 a 4
de la lección 04 siguen en sus checklists; no bloquean esta lección, pero vuelven a aparecer aquí (lo que
devuelve vs lo que hace, `BigDecimal` con escala, `equals` vs comparación).

### Reglas para todas las tareas

1. **Una tarea por vez, en orden.** Al terminar cada una, pídeme "revisa tarea N de la lección 05".
2. **Plantilla obligatoria** en cada `println`:
   ```java
   System.out.println("---- T1 · Paso 3 ----");   // separador antes de cada paso
   System.out.println(algo);
   // espero: ...                                 // ANTES de ejecutar
   // real: ...    porque: ...                    // SOLO si fallaste
   ```
3. **Compara letra por letra** el `espero` con la salida. **Solo pon ✅ si coinciden exactamente**
   (`Money[amount=10.5]` no es `Money[amount=10.50]`). Un ✅ equivocado cuenta más que un fallo honesto.
4. **Usa los datos exactos** del enunciado. Están elegidos para que aparezcan los casos raros.
5. **Las preguntas** (P1.1, P1.2…) van en un comentario al final del archivo, completando la frase. **Un hueco
   `___` = una parte de la respuesta**: llena **todos** los huecos.
6. **Si un paso no se entiende, no lo adivines.** Pregúntame "no entiendo T3 paso 4".
7. **Checklist** al final de cada tarea: revísala antes de pedir la revisión.
8. **Sin lambdas ni `::`**, sin streams. La flecha `->` **solo** dentro de un `switch` (sección 5). Para sacar un
   valor de un `Optional`: `isPresent()` + `get()`, u `orElse(...)`.
9. **Si cambias el código después de un paso** (por ejemplo, agregas el constructor compacto), **no borres** el
   `espero`/`real` del paso anterior: agrega el paso nuevo debajo. La diferencia entre "antes" y "después" es
   justo lo que se quiere ver.

**Tiempo total aproximado:** 3 h 50 min.

| Tarea | Tema | Lee antes | Archivo(s) | Tiempo |
|-------|------|-----------|------------|--------|
| 1 | `record Money`: igualdad, escala, inmutabilidad | Secciones 0.1, 0.3, 2 y 3.1–3.4 | `Models/Money`, `RecordLab` | 40 min |
| 2 | Inmutable por fuera, mutable por dentro + ¿record o clase? | Secciones 2.2, 3.5–3.7 | `ImmutabilityLab` | 30 min |
| 3 | `enum OrderStatus` con reglas de transición | Sección 4 | `Models/OrderStatus`, `EnumLab` | 40 min |
| 4 | `switch` viejo vs moderno, `yield`, exhaustividad + `var` | Secciones 5 y 7 | `SwitchLab` | 30 min |
| 5 | `Optional` en el `Catalog` | Sección 6 | `Models/Catalog`, `OptionalLab` | 40 min |
| 6 | **Entregable:** `Order` con estado, `findItem` y resumen `record` | todo | `Order`, `OrderSummary`, `OrderStatusLab` | 50 min |

Todos los laboratorios van en `src/practical/modern/` (paquete `practical.modern`). Las clases del dominio van en
`src/Models/` (paquete `Models`).

---

### Tarea 1 — `record Money`: igualdad, escala e inmutabilidad (fácil-medio · 40 min)

**🎯 Qué vas a hacer, en una frase:** crear un `record` para el dinero y descubrir, con tus propios ojos, por qué
hay que **normalizar** el `BigDecimal` que lleva dentro.

**Antes:** relee las secciones 0.1, 0.3 y de la 3.1 a la 3.4.

**📁 Archivos:**

| Archivo | Qué es | ¿Tiene `main`? |
|---------|--------|----------------|
| `src/Models/Money.java` | el **record** (objeto de valor) | ❌ no |
| `src/practical/modern/RecordLab.java` | el laboratorio | ✅ sí |

> `Money` todavía **no** reemplaza el `BigDecimal price` de `Product`. Por ahora vive solo. (Cambiar `Product`
> rompería todas las lecciones anteriores.)

**Parte A — el record "crudo", sin validar (pasos 1 a 3)**

**Paso 1.** Crea `Money` con **una sola línea** de cuerpo vacío:
```java
public record Money(BigDecimal amount) {}
```

**Paso 2.** En `RecordLab`, crea **exactamente**:
```java
Money m1 = new Money(new BigDecimal("10.5"));
Money m2 = new Money(new BigDecimal("10.50"));
Money m3 = new Money(new BigDecimal("10.5"));
```

**Paso 3.** Imprime cada fila de esta tabla y llena la columna **"espero (crudo)"** en un comentario **antes** de
ejecutar:

| # | Expresión | espero (crudo) | real (crudo) |
|---|-----------|----------------|--------------|
| a | `m1` | | |
| b | `m1.amount()` | | |
| c | `m1 == m3` | | |
| d | `m1.equals(m3)` | | |
| e | `m1.equals(m2)` | | |
| f | `m1.amount().compareTo(m2.amount())` | | |
| g | `new HashSet<>(List.of(m1, m2)).size()` | | |

Pistas para no perderte:
- (a) el `toString` de un record tiene el formato de la sección 3.1. ¿Con cuántos decimales sale?
- (c) y (d): mismo objeto vs mismos datos (sección 0.2).
- (e) el `equals` del record usa el `equals` de `BigDecimal` (sección 3.4).
- (g) si `m1` y `m2` no son `equals`, el `HashSet` los guarda a los dos.

**Parte B — validar y normalizar (pasos 4 a 6)**

**Paso 4.** Agrega a `Money` un **constructor compacto** que haga, en este orden:
1. si `amount` es `null` → `throw new IllegalArgumentException("amount cannot be null");`
2. si `amount` es negativo → `throw new IllegalArgumentException("amount cannot be negative: " + amount);`
   (compara con `compareTo(BigDecimal.ZERO) < 0`, como en `Product`);
3. **normaliza**: `amount = amount.setScale(2, RoundingMode.HALF_UP);`

Esqueleto:
```java
public record Money(BigDecimal amount) {

    public Money {
        // 1. null
        // 2. negativo
        // 3. normalizar (asigna al PARÁMETRO amount, no a this.amount)
    }
}
```

**Paso 5.** **Sin cambiar `RecordLab`**, vuelve a ejecutar el paso 3. Agrega a tu comentario dos columnas nuevas:
**"espero (normalizado)"** (antes de ejecutar) y **"real (normalizado)"**. Marca qué filas **cambiaron** y por qué.

**Paso 6.** Imprime cada uno (los que lanzan excepción, dentro de un `try/catch (IllegalArgumentException e)`
imprimiendo `e.getMessage()`). `// espero:` en cada uno:
- `new Money(new BigDecimal("3.333"))`
- `new Money(new BigDecimal("3.335"))` ← ¿sube o baja con `HALF_UP`?
- `new Money(new BigDecimal("7"))`
- `new Money(new BigDecimal("-1"))`
- `new Money(null)`

**Parte C — comportamiento y la trampa de siempre (pasos 7 a 9)**

**Paso 7.** Agrega a `Money` (dentro del record, debajo del constructor compacto):

| Miembro | Qué hace |
|---------|----------|
| `public static final Money ZERO = new Money(BigDecimal.ZERO);` | una constante (los records sí pueden tener `static`) |
| `public Money add(Money other)` | devuelve un **`Money` nuevo** con la suma |
| `public Money multiply(int quantity)` | devuelve un **`Money` nuevo** con el producto (`BigDecimal.valueOf(quantity)`) |
| `public boolean isGreaterThan(Money other)` | `true` si este es mayor (`compareTo(...) > 0`, **no** `== 1`) |

**Paso 8 — ¿lo que devuelve o lo que hace?** Escribe **exactamente** esto e imprime:
```java
m1.add(m2);
System.out.println(m1);                 // espero:
Money total = m1.add(m2);
System.out.println(total);              // espero:
System.out.println(m1.multiply(3));     // espero:
System.out.println(total.isGreaterThan(m1) + " " + m1.isGreaterThan(m2));   // espero:
System.out.println(Money.ZERO);         // espero:
```
La primera línea es la trampa de las lecciones 01, 03 y 04. Piensa antes: ¿`m1` cambió?

**Paso 9 — lo que un record no deja hacer.** Escribe cada línea **sin ejecutar**, copia el error de IntelliJ en
un comentario y **comenta la línea** para que compile:
- en `RecordLab`: `m1.getAmount();`
- dentro de `Money`, un método `void setAmount(BigDecimal a) { this.amount = a; }`

**Preguntas** (al final de `RecordLab`):
- **P1.1** *"`m1 == m3` dio ___ porque compara ___; `m1.equals(m3)` dio ___ porque compara ___."*
- **P1.2** *"Sin normalizar, `m1.equals(m2)` dio ___ porque el `equals` del record usa el `equals` de ___, que
  mira ___. Después de `setScale(2, ...)` dio ___ porque ___."*
- **P1.3** *"En el paso 8, después de `m1.add(m2);` `m1` vale ___, porque `add` ___ y yo no ___."*
- **P1.4** *"`Money` es un objeto de ___ (no una entidad) porque ___. Por eso está bien que sea `record`, y
  `Product` no, porque ___."*

**Checklist:**
- [ ] tabla del paso 3 con las **cuatro** columnas (crudo y normalizado), `espero` antes de ejecutar
- [ ] constructor compacto asigna al **parámetro** (`amount = ...`), no a `this.amount`
- [ ] paso 6: los 5 casos, mensajes con `getMessage()`
- [ ] paso 8 escrito **exactamente** como está (con la línea `m1.add(m2);` sola)
- [ ] paso 9: dos errores copiados, líneas comentadas
- [ ] P1.1–P1.4, todos los huecos

> ## 🔍 REVISIÓN TAREA 1 — 2026-11-21 · **6/10** · 🔁 corrige 4 cosas y vuelve a pedir revisión
> Historial: **v1 6/10**. Revisado compilando `Money` + `RecordLab` con Java 21, con el `Money` final **y** con
> un `Money` crudo (`record Money(BigDecimal amount) {}`) para comprobar tu tabla del paso 3.

**✅ Lo bueno:** `Money` está **bien hecho**: constructor compacto con las 3 reglas en orden, asigna al
**parámetro** (`amount = amount.setScale(...)`), `add` y `multiply` devuelven un `Money` nuevo, `isGreaterThan`
usa `> 0` (¡ya no `== 1`!). La tabla del paso 3 está casi toda acertada en crudo y normalizado, y en la fila (g)
escribiste un `porque` honesto y correcto (hashCode distinto de 10.5 y 10.50). P1.3 está perfecta.

| Paso | Espero | Real | ¿Bien? |
|------|--------|------|--------|
| 3 crudo a–f | `Money[amount=10.5]`, 10.5, false, true, false, 0 | igual | ✅ |
| 3 crudo g | "m1 Money{amount=10.5}" | 2 | ❌ (corregido con `porque` ✅) |
| 5 normalizado | `10.50`, 10.50, false, true, **true**, 0, 1 | igual | ✅ |
| 6 3.333 / 3.335 / 7 | 3.33 / 3.34 / 7.00 | igual | ✅ |
| 6 -1 y null | "en ambos casos entra por aquí" | **solo** sale el de -1 | ❌ ver IMPORTANTE 2 |
| 8 `m1` tras `m1.add(m2);` | 10.5 ✅ | `Money[amount=10.50]` | ❌ ✅ mal puesto: es **10.50** (está normalizado) |
| 8 resto | 21.00, 31.50, true false, 0.00 | igual | ✅ |
| 9 | — | errores no copiados | ❌ |

---

#### 🟠 IMPORTANTE 1 — P1.1, P1.2 y P1.4: las respuestas contradicen tu propia tabla

Tu tabla está bien; las frases no. Compáralas:

| | Tu tabla dice | Tu frase dice |
|---|---------------|---------------|
| P1.1 `m1.equals(m3)` | **true** ✅ | "dio false porque compara 10.5 y 10.50" ← eso es `m2`, no `m3` (m3 también es `"10.5"`) |
| P1.2 crudo `m1.equals(m2)` | **false** ✅ | "dio true" |
| P1.2 normalizado | **true** ✅ | "dio 10.50" ← la pregunta es qué devolvió `equals` (true/false) |

**Teoría (secciones 0.3 y 3.4):** el `equals` del record compara campo por campo con el `equals` de cada campo.
El `equals` de `BigDecimal` mira el valor **y la escala** (cuántos decimales): `10.5` tiene escala 1 y `10.50`
escala 2 → false. Con `setScale(2, ...)` los dos quedan con escala 2 → true.

**P1.4:** "objeto de **record**" no existe como categoría: es objeto de **valor** (sección 3.7). Usa el truco:
*"si cambio un dato, ¿sigue siendo el mismo?"*. Un `Money` de 10 que pasa a 12 ya **no es** el mismo dinero → valor.
Un `Product` que cambia de precio **sigue siendo** el mismo producto (mismo `id`) → entidad → clase con `equals`
por `id`. Si fuera record, su `equals` miraría también el precio, y al cambiarlo se perdería en un `HashSet`.
"Es más complejo" no es la razón.

**Qué hacer:** rehaz P1.1, P1.2 y P1.4 mirando tu tabla mientras llenas cada hueco.

#### 🟠 IMPORTANTE 2 — Paso 6: `new Money(null)` nunca se ejecutó

```java
try {
    print(new Money(new BigDecimal("-1")));   // ← lanza aquí y salta al catch
    print(new Money(null));                   // ← esta línea NUNCA corre
} catch (IllegalArgumentException e) { ... }
```
Salida real: solo `amount cannot be negative: -1`. En cuanto una línea del `try` lanza, Java salta al `catch` y
**no vuelve**. Tu `espero` ("en ambos casos entra por aquí") tiene ✅ pero solo se probó un caso.

**Qué hacer:** **dos** `try/catch`, uno por caso, y en el `catch` imprime `e.getMessage()` (lo pide el paso),
no `e`. `// espero:` con el mensaje exacto de cada uno.

#### 🟠 IMPORTANTE 3 — Paso 9: faltan los dos errores

- `m1.getAmount();` lo pusiste en `ModernPlayground`, donde **no existe** `m1` (el error ahí sería otro). Va en
  `RecordLab`, debajo del paso 8.
- `setAmount` está comentado en `Money`, pero sin el error copiado.

Escribe cada línea, copia el mensaje de IntelliJ en un comentario y luego comenta la línea.

#### 🟡 MENOR
- Paso 8, primera línea: pusiste `espero: 10.5 ✅`, pero sale `Money[amount=10.50]`. La idea (no cambió) es
  correcta y P1.3 lo dice bien; solo que el ✅ no se compara carácter por carácter.
- `multiply`: el paso pedía `BigDecimal.valueOf(quantity)`. El `.setScale(2, ...)` que agregaste sobra: el
  constructor compacto ya normaliza el resultado.
- Paso 5 pedía marcar **qué filas cambiaron** y por qué: escribe una línea, por ejemplo *"cambiaron a, b, e y g
  porque..."*.

### Para llegar a 10/10
- [ ] P1.1, P1.2 y P1.4 rehechas con tu tabla al lado (como `v2`, sin borrar las de v1)
- [ ] Paso 6: dos `try/catch` separados, `e.getMessage()`, `espero` de cada mensaje
- [ ] Paso 9: los dos errores copiados (`getAmount` en `RecordLab`)
- [ ] Paso 8: corregir el ✅ de la primera línea
- [ ] Paso 5: una línea con las filas que cambiaron

---

### Tarea 2 — Inmutable por fuera, mutable por dentro + ¿record o clase? (medio · 30 min)

**🎯 Qué vas a hacer, en una frase:** ver cómo un record con una lista **sí** cambia si no lo proteges, arreglarlo,
y decidir qué clases de tu proyecto deberían ser `record`.

**Antes:** relee las secciones 2.2, 3.5, 3.6 y 3.7.
**Archivo:** `src/practical/modern/ImmutabilityLab.java` (con `main`). Los records de esta tarea van **dentro** de
`ImmutabilityLab` (anidados), como en el playground.

**Parte A — la lista que se escapa (pasos 1 a 4)**

**Paso 1.** Dentro de `ImmutabilityLab`, escribe:
```java
record ShoppingList(String owner, List<String> items) {}
```

**Paso 2.** En `main`, **exactamente**:
```java
List<String> items = new ArrayList<>(List.of("pan", "leche"));
ShoppingList s = new ShoppingList("Lucía", items);
```
Llena esta tabla **antes** de ejecutar e imprime `s` después de cada acción:

| # | Acción | `espero` (lo que imprime `s`) | real |
|---|--------|-------------------------------|------|
| a | (ninguna, recién creado) | | |
| b | `items.add("huevos");` | | |
| c | `s.items().add("café");` | | |

Después de (c), imprime también `items`. `// espero:` ¿qué tiene la lista **de afuera**?

**Paso 3.** Escribe un segundo record, **arreglado**:
```java
record SafeShoppingList(String owner, List<String> items) {
    // constructor compacto: guarda una copia NO modificable de items (sección 3.5)
}
```
Repite el paso 2 con `SafeShoppingList` y una lista **nueva** (`items2`). Para la acción (c), usa un
`try/catch (UnsupportedOperationException e)` e imprime `e` en el `catch`. Llena la misma tabla.

**Paso 4.** Una pregunta de "¿qué pasaría?" (escríbela y ejecútala): en el constructor compacto, ¿qué pasa si
usas `Collections.unmodifiableList(items)` en vez de `List.copyOf(items)`? Cambia temporalmente el constructor,
repite **solo** la acción (b) con una lista nueva, e imprime el record. `// espero:` (sección 0.4: vista vs copia).
Después **vuelve a `List.copyOf`**.

**Parte B — ¿`record` o clase? (paso 5)**

**Paso 5.** En un comentario, llena esta tabla para tu proyecto. En la última columna usa la pregunta de la
sección 3.7: *"si cambio un dato, ¿sigue siendo el mismo?"*.

| Tipo | ¿Tiene id? | Si cambia un dato, ¿sigue siendo el mismo? | ¿Entidad o valor? | ¿`record` o clase? |
|------|------------|--------------------------------------------|-------------------|--------------------|
| `Product` | | | | |
| `Customer` | | | | |
| `Order` | | | | |
| `Money` | | | | |
| una dirección de envío (calle, ciudad, código postal) | | | | |
| `OrderItem` (producto + cantidad) | | | | |
| un resumen de pedido para mostrar en pantalla | | | | |

> `OrderItem` es el difícil: piensa si **cambia** (en la lección 03 querías **acumular** la cantidad) y si tiene
> sentido decir "este item es el mismo que aquel". No hay una única respuesta correcta; lo que cuenta es el
> **porqué**.

**Preguntas:**
- **P2.1** *"`ShoppingList` cambió aunque sus campos son `final`, porque `final` impide ___ pero no ___. El
  record guardó ___ lista que yo le pasé, no ___."*
- **P2.2** *"Con `List.copyOf`, la acción (b) ___ porque el record tiene ___; la acción (c) ___ porque la copia
  es ___."*
- **P2.3** *"Con `Collections.unmodifiableList`, la acción (b) ___, porque una vista ___."*
- **P2.4** *"`Customer` es ___ porque ___. Una dirección de envío es ___ porque ___."*

**Checklist:**
- [ ] tabla del paso 2 para **los dos** records, con `espero` antes de ejecutar
- [ ] imprimiste `items` (la de afuera) después de la acción (c)
- [ ] paso 4 probado y vuelto a `List.copyOf`
- [ ] tabla del paso 5 completa, con la columna del "¿sigue siendo el mismo?"
- [ ] P2.1–P2.4

> ## 🔍 REVISIÓN TAREA 2 — 2026-11-24 · **6/10** · 🔁 ejecuta el paso 4, corrige las columnas `real` y vuelve a pedir revisión
> Historial: **v1 6/10**. Revisado compilando `ImmutabilityLab` con Java 21. El paso 4 lo probé aparte (tu archivo
> no tiene código para ese paso).

**✅ Lo bueno:** el código de la Parte A está **bien**. `SafeShoppingList` tiene un constructor compacto correcto
(`items = List.copyOf(items);`: asigna al **parámetro**, como se corrigió en la Tarea 1). Tus predicciones de la
Parte A son correctas en el contenido, **también la del paso 4** (`huevos` se ve en el record). En P2.1 la segunda
parte está perfecta (*"guardó la referencia de la lista, no una copia"*) y P2.2 está bien. En la tabla del paso 5
aciertas `Product`, `Customer`, `Order`, la dirección y el resumen.

**Salida real de tu programa:**
```
ShoppingList[owner=Lucía, items=[pan, leche]]
ShoppingList[owner=Lucía, items=[pan, leche, huevos]]
ShoppingList[owner=Lucía, items=[pan, leche, huevos, cafe]]
---------paso3-------
SafeShoppingList[owner=Lucía, items=[pan, leche]]
SafeShoppingList[owner=Lucía, items=[pan, leche]]
java.lang.UnsupportedOperationException
------Paso 4.---------
-------Paso 5------
```

| Paso | Tu `espero` | Tu `real` | Real de verdad | ¿Bien? |
|------|-------------|-----------|----------------|--------|
| 2a | `[pan, leche]` | igual | igual | ✅ (el formato era `ShoppingList[owner=Lucía, items=[pan, leche]]`) |
| 2b | `[pan, leche, huevos]` | igual | igual | ✅ |
| 2c | `[pan, leche, huevos, cafe]` | `[pan, leche, huevos]` | `[pan, leche, huevos, cafe]` | espero ✅, **`real` mal copiado** |
| 2 `items` de afuera | — | — | no se imprimió | ❌ falta |
| 3a / 3b | `[pan, leche]` | "`ShoppingList[...]`" | `SafeShoppingList[...]` | espero ✅, `real` copiado del espero |
| 3c | UOE | "UnsupportedUoerationException" | `java.lang.UnsupportedOperationException` | espero ✅, `real` no copiado de la consola |
| 4b | `[pan, leche, huevos]` | `[pan, leche]` | **`[pan, leche, huevos]`** (no ejecutado) | espero ✅, **`real` inventado** |

---

#### ❌ CRÍTICO — La columna `real` no salió de la consola

En 3 sitios la columna `real` **no coincide** con lo que imprime tu programa:
- **2c**: escribiste `[pan, leche, huevos]`, pero la consola dice `[pan, leche, huevos, cafe]`. Tu `espero` era
  el correcto.
- **Paso 3**: el `real` dice `ShoppingList[Lucia,...` (copiado del `espero`), pero la consola dice
  `SafeShoppingList[owner=Lucía, ...]`, y el error trae una errata (`Uoeration`) que en la consola no existe.
- **Paso 4**: **no hay código**, pero la tabla tiene un `real`. Además ese `real` (`[pan, leche]`) es
  **falso**: lo comprobé y sale `[pan, leche, huevos]`. Tu `espero` estaba bien, y el `real` inventado lo
  contradice.

**Por qué importa:** el `real` es la prueba. Si lo escribes sin ejecutar, el paso 4 te "demuestra" lo contrario
de la sección 0.4 (que una vista **sí** refleja los cambios). Es la misma trampa de lecciones anteriores: tabla
llena con el paso sin hacer.

**Profesionalmente:** en un ticket o en un PR, "probado: funciona" cuando no se probó es lo que más confianza
quita. Mejor `real: no lo ejecuté` que un `real` inventado.

**Qué hacer:**
1. Paso 4 de verdad, en código: cambia **temporalmente** el constructor a `Collections.unmodifiableList(items)`,
   crea `items3`, haz `items3.add("huevos")` e imprime el record. Copia la salida. Después vuelve a `List.copyOf`
   y deja un comentario con lo que viste.
2. Rehaz las columnas `real` de los pasos 2, 3 y 4 **copiando y pegando** de la consola.

#### 🟠 IMPORTANTE 1 — Paso 5: la fila de `Money` se contradice, y `OrderItem` no tiene porqué

| Tipo | Tú | Problema |
|------|----|----------|
| `Money` | "si cambia, ¿sigue siendo el mismo?" → **si** · valor | Si sigue siendo el mismo, sería **entidad**. Un `Money` de 10 que pasa a 20 **ya no es** el mismo dinero → **no** → valor ✅ |
| `OrderItem` | id **si** · sigue siendo el mismo **si** · **valor** · clase | Tres columnas que no encajan: con id y "sigue siendo el mismo" sería entidad. Y el `OrderItem` de tu proyecto **no tiene** id |

**Teoría (sección 3.7):** la columna "¿sigue siendo el mismo?" **decide** la siguiente:
**sí → entidad → clase** (con `equals` por id); **no → valor → `record`**. Si no coinciden, una de las dos
está mal.

`OrderItem` admite dos respuestas, pero cada una necesita su porqué:
- **valor → `record`**: "2 × Mouse" y "3 × Mouse" son items distintos; para acumular, se crea uno nuevo
  (`new OrderItem(product, cantidad + 2)`), igual que `Money.add`.
- **clase mutable**: si quieres hacer `item.addQuantity(2)` en el mismo objeto. Pero sin id no es una entidad;
  es una clase de valor mutable, la opción más frágil.

**Qué hacer:** corrige la fila de `Money` y rehaz la de `OrderItem` con una línea de **porqué** (la tarea lo pide).

#### 🟠 IMPORTANTE 2 — P2.1 (primera parte), P2.3 y P2.4: los huecos no dicen lo que hay que decir

- **P2.1** *"final impide el cambio de valor luego de ser inicializada pero no con objetos"*: es vago. Con
  objetos `final` **sí** hace algo: impide **reasignar** el campo (que apunte a **otra** lista). Lo que no
  impide es **modificar el contenido** del objeto al que apunta (`add`, `remove`).
  → *"`final` impide **reasignar el campo / apuntar a otra lista** pero no **cambiar lo que hay dentro de la
  lista**."*
- **P2.3** *"la acción (b) la agrego, porque una vista de la lista"*: falta decir si **se ve en el record**, y la
  frase no termina. → *"la acción (b) **sí aparece en el record**, porque una vista **no copia: mira la lista
  original y muestra sus cambios**"*. Solo bloquea los cambios hechos **a través de la vista** (la acción c).
- **P2.4** *Customer*: "viene de la BD y tiene valores importantes" no es la razón (un `Money` también puede venir
  de la BD). La razón es la **identidad**: si el cliente cambia de email, **sigue siendo el mismo cliente** (mismo
  `id`). *Dirección*: el primer hueco pedía **entidad o valor** (pusiste "record"), y "porque **puede tener**
  id" va al revés. Una dirección es **valor** porque si cambias la calle **ya es otra dirección**, y no necesita id.

**Qué hacer:** rehaz P2.1, P2.3 y P2.4 como `v2`, debajo de las de v1, llenando **cada** hueco.

#### 🟡 MENOR
- Datos exactos: el enunciado dice `"café"` (con tilde), tú usaste `"cafe"`.
- En el paso 3, `items2.add("huevos")` no necesita `try/catch`: `items2` es un `ArrayList` normal y no puede
  lanzar UOE. Solo la acción (c) va dentro del `try`. (Tu P2.2 lo dice bien.)
- Separadores: la plantilla es `---- T2 · Paso N ----` antes de **cada** paso (faltan los de los pasos 1 y 2).
- El helper `print(Object...)` está bien, buena idea.

### Para llegar a 10/10
- [ ] Paso 4 **en código**: `unmodifiableList` temporal, `items3`, salida copiada, vuelta a `List.copyOf`
- [ ] Columnas `real` de los pasos 2, 3 y 4 copiadas de la consola (2c con `cafe`, 3 con `SafeShoppingList`)
- [ ] Imprimir `items` (la de afuera) después de (c), con su `espero`
- [ ] Paso 5: fila `Money` corregida, `OrderItem` con su porqué
- [ ] P2.1, P2.3 y P2.4 como `v2`, todos los huecos
- [ ] `"café"` con tilde y separadores `---- T2 · Paso N ----`

---

### Tarea 3 — `enum OrderStatus` con reglas de transición (medio · 40 min)

**🎯 Qué vas a hacer, en una frase:** un enum con los estados de un pedido que **sabe** a qué estado puede pasar
cada uno.

**Antes:** relee la sección 4 completa (sobre todo la 4.4) y la 5.3.

**📁 Archivos:**

| Archivo | Qué es | ¿Tiene `main`? |
|---------|--------|----------------|
| `src/Models/OrderStatus.java` | el **enum** | ❌ no |
| `src/practical/modern/EnumLab.java` | el laboratorio | ✅ sí |

**Las reglas del negocio** (léelas con calma, son el corazón de la tarea):

```
PENDING ──pagar──▶ PAID ──enviar──▶ SHIPPED ──entregar──▶ DELIVERED
   │                 │
   └──cancelar──▶ CANCELLED ◀──cancelar──┘

DELIVERED y CANCELLED son FINALES: desde ahí no se puede ir a ningún lado.
Un pedido ENVIADO (SHIPPED) ya no se puede cancelar.
Nunca se puede "volver atrás" ni quedarse en el mismo estado.
```

| Desde | Puede pasar a |
|-------|---------------|
| `PENDING` | `PAID`, `CANCELLED` |
| `PAID` | `SHIPPED`, `CANCELLED` |
| `SHIPPED` | `DELIVERED` |
| `DELIVERED` | (ninguno) |
| `CANCELLED` | (ninguno) |

**Paso 1 — los valores y su etiqueta.** Crea `OrderStatus` con estos 5 valores, **en este orden**, cada uno con
una etiqueta en español:

| Valor | Etiqueta |
|-------|----------|
| `PENDING` | `"Pendiente"` |
| `PAID` | `"Pagado"` |
| `SHIPPED` | `"Enviado"` |
| `DELIVERED` | `"Entregado"` |
| `CANCELLED` | `"Cancelado"` |

Necesitas: un campo `private final String label`, un constructor (sin `public`) y `getLabel()` (sección 4.2).

**Paso 2 — los métodos con reglas.** Agrega:
```java
public boolean canTransitionTo(OrderStatus next) {
    // un switch EXPRESIÓN sobre this, SIN default, un case por cada estado (sección 4.4)
}

public boolean isFinal() {
    // true solo para DELIVERED y CANCELLED (con == y ||, sin switch)
}
```
> Truco: los dos estados finales tienen la misma respuesta. Puedes juntarlos: `case DELIVERED, CANCELLED -> false;`

**Paso 3 — recorrer.** En `EnumLab`, recorre `OrderStatus.values()` e imprime en cada vuelta:
`ordinal + " " + name() + " " + getLabel() + " " + isFinal()`. `// espero:` las 5 líneas.

**Paso 4 — de texto a enum.** Imprime (los que fallan, dentro de `try/catch (IllegalArgumentException e)`
imprimiendo `e`). `// espero:` en cada uno:
- `OrderStatus.valueOf("PAID")`
- `OrderStatus.valueOf("PAID") == OrderStatus.PAID`
- `OrderStatus.valueOf("paid")`
- `OrderStatus.valueOf("Pagado")` ← ¿busca por la etiqueta o por el nombre?

**Paso 5 — la matriz de transiciones.** Este es el paso importante. **Antes de ejecutar**, llena esta tabla en
un comentario con `si`/`no`, usando **solo** las reglas de arriba (fila = desde, columna = hacia):

| desde \ hacia | PENDING | PAID | SHIPPED | DELIVERED | CANCELLED |
|---------------|---------|------|---------|-----------|-----------|
| **PENDING** | | | | | |
| **PAID** | | | | | |
| **SHIPPED** | | | | | |
| **DELIVERED** | | | | | |
| **CANCELLED** | | | | | |

Después imprímela con **dos for-each anidados** (uno para la fila, otro para la columna). Para que salga en forma
de tabla, en el `for` de adentro usa `System.out.print(...)` (sin `ln`) y al terminar cada fila un
`System.out.println()`. Ejemplo del formato (con otros datos):
```
PENDING   no  si  no  no  si
```
Compara tu tabla con la salida **celda por celda**. Debe haber exactamente **5** `si`.

**Paso 6 — orden natural.** Imprime `OrderStatus.PENDING.compareTo(OrderStatus.SHIPPED)` y
`OrderStatus.CANCELLED.compareTo(OrderStatus.PAID)`. `// espero:` **solo el signo** (sección 4.1: ¿qué orden
usa un enum?).

**Paso 7 — exhaustividad.** En `canTransitionTo`, **comenta** temporalmente el `case SHIPPED`. Copia el error
que da IntelliJ en un comentario y vuelve a ponerlo.

**Preguntas:**
- **P3.1** *"`valueOf("Pagado")` falla porque `valueOf` busca por ___, no por ___."*
- **P3.2** *"Las reglas de transición van dentro del enum (y no en `Order` ni en `Main`) porque ___. Si mañana
  agrego un estado `RETURNED`, el compilador ___."*
- **P3.3** *"No uso `default` en el `switch` de `canTransitionTo` porque ___."*
- **P3.4** *"No usaría `ordinal()` para decidir si se puede avanzar (por ejemplo, `next.ordinal() > ordinal()`)
  porque ___ y además ___."* ← pista para la segunda parte: ¿`CANCELLED` está "después" de `SHIPPED` en la
  declaración? ¿Se puede pasar de `SHIPPED` a `CANCELLED`?

**Checklist:**
- [ ] 5 valores en el orden dado, con etiqueta y constructor sin `public`
- [ ] `canTransitionTo` con `switch` expresión **sin `default`**
- [ ] matriz del paso 5 llenada **antes** de ejecutar, comparada celda por celda
- [ ] paso 7: error copiado y `case` restaurado
- [ ] P3.1–P3.4

> ## 🔍 REVISIÓN TAREA 3 — 2026-11-24 · **7,5/10** · 🔁 rehaz 3 preguntas y agrega el `ordinal` del paso 3 (rápido)
> Historial: **v1 7,5/10**. Revisado compilando todo `src` con Java 21. El error del paso 7 lo comprobé quitando
> el `case SHIPPED` en una copia: `the switch expression does not cover all possible input values`.

**✅ Lo bueno:** la mejor tarea de la lección hasta ahora. El enum está **correcto**: 5 valores en orden, campo
`final`, constructor sin `public`, `canTransitionTo` con `switch` expresión **sin `default`**, y juntaste los
finales en un solo `case`. **La matriz del paso 5 está perfecta**, celda por celda, con exactamente 5 `si`.
**Todas tus predicciones son correctas** (pasos 3, 4, 5 y 6) y en el paso 7 copiaste el error y restauraste el `case`.
En P3.2, la parte del compilador es justo lo que había que ver.

| Paso | Espero | Real | ¿Bien? |
|------|--------|------|--------|
| 3 | `PENDING Pendiente false` … `CANCELLED Cancelado true` | igual | ✅ pero **falta el `ordinal`** (ver MENOR) |
| 4 `valueOf("PAID")` / `== PAID` | `PAID` / `true` | igual | ✅ |
| 4 `"paid"` / `"Pagado"` | "illegalArgumentException" | `java.lang.IllegalArgumentException: No enum constant Models.OrderStatus.paid` (y `.Pagado`) | ✅ la idea; el mensaje completo no lo escribiste |
| 5 matriz | 5 `si`: P→PAID, P→CANC, PAID→SHIP, PAID→CANC, SHIP→DELIV | igual | ✅ |
| 6 | `-` / `+` | `-2` / `3` | ✅ |
| 7 | error copiado | igual que el de `javac` | ✅ |

---

#### 🟠 IMPORTANTE — P3.1, P3.3 y P3.4: la idea va bien, pero los huecos no dicen lo que hay que decir

- **P3.1** *"busca por estado, no por valor"*: los dos son "estados", así que la frase no distingue nada. La
  diferencia está entre las dos cosas que tiene cada constante: `valueOf` busca por el **nombre de la constante**
  (`name()`, exacto, con mayúsculas: `"PAID"`), **no por la etiqueta** (`getLabel()`, `"Pagado"`). Por eso
  `"paid"` también falla.
- **P3.3** *"porque todos tienen un siguiente estado"*: **no es cierto**: `DELIVERED` y `CANCELLED` no tienen
  ninguno (tu `case` devuelve `false`). La razón es la de la segunda parte, pero al revés de como la dices:
  **con** `default`, si agrego `RETURNED` **no hay error**: entra al `default` sin avisar (bug silencioso). **Sin**
  `default`, el compilador me obliga a decidir qué hace `RETURNED` (el error que viste en el paso 7).
- **P3.4** la primera parte está bien (si reordeno las constantes, cambian los `ordinal`). La segunda, *"en
  nuestro caso no está por ordinal"*, es vaga. Usa la pista: `CANCELLED` (4) está **después** de `SHIPPED` (2), así
  que `next.ordinal() > ordinal()` diría **sí**, pero la regla dice **no** (un pedido enviado no se cancela).
  Y al revés: `PENDING → DELIVERED` también daría sí (se salta pasos).

**Profesionalmente:** esto es lo que vas a defender en una entrevista ("¿por qué no `default`?", "¿por qué no
`ordinal`?"). La respuesta corta tiene que salir con el ejemplo concreto.

**Qué hacer:** P3.1, P3.3 y P3.4 como `v2`, debajo de las de v1.

#### 🟡 MENOR
- **Paso 3:** la línea pedida era `ordinal + " " + name() + ...`. Falta `status.ordinal()` y su `espero`
  (`0 PENDING Pendiente false` … `4 CANCELLED Cancelado true`). Te sirve para P3.4.
- **`isFinal`:** el paso pedía `==` y `||`, sin `switch`. Tu `switch` funciona y además es exhaustivo, así que no
  está mal; solo que no practicaste la otra forma. Opcional: escríbela en un comentario.
- **Paso 4:** en los `catch`, el `espero` puede ser el mensaje completo
  (`No enum constant Models.OrderStatus.paid`). Fíjate que el mensaje trae el **nombre** que buscó: eso responde P3.1.
- Imports sin usar en `EnumLab` (`PrinterInfo`, `Order`, `ArrayList`, `List`): IntelliJ los pone en gris;
  `Ctrl+Alt+O` los limpia.
- Separadores `---- T3 · Paso N ----` (falta el del paso 3).

### Para llegar a 10/10
- [ ] P3.1, P3.3 y P3.4 como `v2`
- [ ] Paso 3 con `ordinal()` y su `espero`
- [ ] (opcional) `isFinal` con `==` y `||` en un comentario
- [ ] Imports limpios y separador del paso 3

---

### Tarea 4 — `switch` viejo vs moderno, `yield`, exhaustividad + `var` (medio · 30 min)

**🎯 Qué vas a hacer, en una frase:** ver el *fall-through* del `switch` viejo con tus estados de pedido,
reescribirlo moderno, y probar `yield` y `var`.

**Antes:** relee las secciones 5 y 7.
**Archivo:** `src/practical/modern/SwitchLab.java` (con `main`). Usa el `OrderStatus` de la Tarea 3.

**Paso 1 — el `switch` viejo con trampa.** Copia **exactamente** este método (los `break` que faltan, faltan a
propósito):
```java
static String oldStyle(OrderStatus s) {
    String r = "";
    switch (s) {
        case PENDING:
            r += "esperando pago; ";
        case PAID:
            r += "preparando; ";
            break;
        case SHIPPED:
            r += "en camino; ";
        case DELIVERED:
            r += "recibido; ";
        default:
            r += "sin acciones; ";
    }
    return r;
}
```
Recorre `OrderStatus.values()` e imprime `s + ": " + oldStyle(s)`. **Antes de ejecutar**, llena:

| Estado | ¿en qué `case` entra? | ¿dónde para (break o fin)? | espero | real |
|--------|-----------------------|----------------------------|--------|------|
| PENDING | | | | |
| PAID | | | | |
| SHIPPED | | | | |
| DELIVERED | | | | |
| CANCELLED | | | | |

**Paso 2 — moderno.** Escribe `static String describe(OrderStatus s)` como `switch` **expresión**, sin
`default`, que devuelva:

| Estado | Texto |
|--------|-------|
| PENDING | `"Esperando el pago"` |
| PAID | `"Pagado, preparando el envío"` |
| SHIPPED | `"En camino"` |
| DELIVERED | `"Entregado al cliente"` |
| CANCELLED | `"Cancelado"` |

Recorre los estados e imprime `s + ": " + describe(s)`. `// espero:` ¿se "cae" alguno?

**Paso 3 — `switch` con `String` y `default`.** Escribe `static int deliveryDays(String city)`: `"Lima"` → `1`;
`"Cusco"` y `"Arequipa"` (en el **mismo** `case`) → `3`; cualquier otra → `7`. Imprime en una línea:
`deliveryDays("Cusco")`, `deliveryDays("lima")`, `deliveryDays("Lima")`, `deliveryDays("Tacna")`.
`// espero:` (¡ojo con `"lima"` en minúscula!). Después, **quita el `default`**, copia el error y vuelve a ponerlo.

**Paso 4 — `yield`.** Escribe `static int shippingCost(String city, boolean express)`:

| Ciudad | Normal | Express |
|--------|--------|---------|
| `"Lima"` | 8 | 15 |
| `"Cusco"`, `"Arequipa"` | 20 | el doble |
| otra | 35 | 35 + 25 |

- Para Lima usa el ternario en una línea: `case "Lima" -> express ? 15 : 8;`
- Para las otras dos filas usa un **bloque con `yield`**: guarda la base en un `int`, si es express cámbiala con
  un `if`, y `yield`.

Imprime en una línea: `shippingCost("Lima", true)`, `shippingCost("Arequipa", true)`,
`shippingCost("Arequipa", false)`, `shippingCost("Tacna", true)`. `// espero:`

**Paso 5 — `var`.** Escribe estas líneas e imprime cada variable. `// espero:` el valor, y en un comentario el
**tipo** que deduce Java:
```java
var precio = new BigDecimal("9.90");
var cantidad = 3;
var mitad = 7 / 2;
var nombres = new ArrayList<String>();
var estado = OrderStatus.valueOf("SHIPPED");
```
Después escribe `var total = 0;` y en la línea siguiente `total = 2.5;`. **Sin ejecutar**, copia el error y
comenta la segunda línea.

**Preguntas:**
- **P4.1** *"En el paso 1, `PENDING` imprimió ___ porque en su `case` no había ___ y la ejecución ___ hasta el
  `break` de ___."*
- **P4.2** *"`deliveryDays("lima")` dio ___ porque el `case` de un `String` ___."*
- **P4.3** *"En un bloque `{ }` de un `switch` expresión se usa `yield` y no `return` porque `return` ___."*
- **P4.4** *"`var mitad = 7 / 2;` es de tipo ___ y vale ___ porque ___."*

**Checklist:**
- [ ] `oldStyle` copiado **exacto**, tabla del paso 1 llenada antes de ejecutar
- [ ] `describe` sin `default`
- [ ] error de quitar el `default` copiado (paso 3)
- [ ] `shippingCost` con dos bloques `yield`
- [ ] tipos de `var` escritos en comentarios
- [ ] P4.1–P4.4

---

### Tarea 5 — `Optional` en el `Catalog` (medio · 40 min)

**🎯 Qué vas a hacer, en una frase:** que el `Catalog` de la lección 04 tenga búsquedas que devuelvan `Optional`, y
cambiar `getCheapest()` para que el caso "catálogo vacío" deje de ser un problema.

**Antes:** relee la sección 6 completa. Necesitas el `Catalog` de la **Tarea 6 de la lección 04** terminado.

**📁 Archivos:**

| Archivo | Qué es | ¿Tiene `main`? |
|---------|--------|----------------|
| `src/Models/Catalog.java` | **modificas** la clase de la lección 04 | ❌ no |
| `src/practical/modern/OptionalLab.java` | el laboratorio nuevo | ✅ sí |

> Al cambiar `getCheapest()`, el `CatalogMain` de la lección 04 dejará de compilar en los pasos 7 y 8. Arréglalo
> ahí mismo (con `.get()` en el paso 7 y `isPresent()` en el 8) y anota `// actualizado en L05 T5`.

**Paso 1 — los métodos nuevos.** Agrega a `Catalog`:

| Método | Qué hace |
|--------|----------|
| `Optional<Product> findByName(String name)` | el primer producto cuyo nombre sea igual **sin importar mayúsculas** (`equalsIgnoreCase`); si no hay, vacío |
| `Optional<Product> findById(BigInteger id)` | el producto con ese id (compara con `equals`, no con `==`: es un `BigInteger`); si no hay, vacío |
| `Optional<Product> getCheapest()` | **cambia** el de la lección 04: si el catálogo está vacío → `Optional.empty()`; si no → el más barato dentro de un `Optional.of(...)` |

Forma de los dos `find...` (es la receta de la sección 6.2): recorrer con for-each; si encuentras, `return
Optional.of(p);`; al terminar el `for`, `return Optional.empty();`.

**Paso 2 — los datos.** En `OptionalLab`, crea un `Catalog` y agrégale los 6 productos de la **Tarea 3 de la
lección 04** (datos exactos: `"curso java"` y `"audífonos"` en minúsculas).

**Paso 3 — buscar.** Llena **antes** de ejecutar e imprime cada fila:

| # | Expresión | espero | real |
|---|-----------|--------|------|
| a | `catalog.findByName("mouse").isPresent()` | | |
| b | `catalog.findByName("tablet").isPresent()` | | |
| c | `catalog.findByName("tablet").isEmpty()` | | |
| d | `catalog.findByName("mouse").get().getPrice()` | | |
| e | `catalog.findByName("AUDÍFONOS").isPresent()` | | |
| f | `catalog.findByName("audifonos").isPresent()` ← sin tilde | | |
| g | `catalog.findById(BigInteger.valueOf(25)).get().getName()` | | |
| h | `catalog.findById(BigInteger.valueOf(99))` | | |
| i | `catalog.getCheapest().get().getName()` | | |
| j | `new Catalog().getCheapest()` | | |

Para (h) y (j), fíjate en **cómo se imprime** un `Optional` vacío (sección 6.2).

**Paso 4 — usar el resultado sin `get()` a ciegas.** Escribe un método:
```java
static void mostrarPrecio(Catalog catalog, String name) {
    // busca con findByName
    // si hay producto → imprime "<nombre>: <precio>"
    // si no          → imprime "No existe: <name>"
    // usa isPresent() + get()
}
```
Llámalo con `"Silla"`, `"silla"` y `"Teclado"`. `// espero:` las 3 líneas.

**Paso 5 — `orElse`.** Crea un método que **avisa cuando se ejecuta**:
```java
static Product porDefecto() {
    System.out.println("  (porDefecto se ejecutó)");
    return new Product(BigInteger.ZERO, "Sin producto", BigDecimal.ZERO);
}
```
Imprime `catalog.findByName("tablet").orElse(porDefecto()).getName()` y después
`catalog.findByName("mouse").orElse(porDefecto()).getName()`. `// espero:` **todas** las líneas que salen
(incluidas las del aviso). La segunda es la trampa de la sección 6.4.

**Paso 6 — los errores.** Cada uno en su `try/catch`, imprimiendo `e`. `// espero:`
- `catalog.findByName("tablet").get()` (`NoSuchElementException`)
- `Optional.of(null)` (`NullPointerException`)

**Paso 7 — pensar como diseñador.** En un comentario, responde con una línea cada uno: ¿qué devolverías en estos
métodos? (`Optional<...>`, `List<...>` o el tipo directo):
- `findBySku(String sku)` (el SKU es único)
- `findByPriceLowerThan(BigDecimal max)` (pueden ser varios)
- `getProducts()`
- `countProducts()`

**Preguntas:**
- **P5.1** *"`findByName` devuelve `Optional<Product>` en vez de `Product` porque así ___. Quien lo llama tiene que
  ___."*
- **P5.2** *"`findByName("audifonos")` dio ___ porque `equalsIgnoreCase` ignora ___ pero no ___."*
- **P5.3** *"En el paso 5, `porDefecto()` se ejecutó ___ veces, porque `orElse` ___."*
- **P5.4** *"`getCheapest()` con `Optional` es mejor que la versión de la lección 04 porque ___."*
- **P5.5** *"`findByPriceLowerThan` no devuelve `Optional<List<Product>>` porque ___."*

**Checklist:**
- [ ] `findByName`, `findById` y `getCheapest` devuelven `Optional`, **nunca** `null`
- [ ] `findById` compara con `equals`
- [ ] `CatalogMain` (L04) actualizado y compilando
- [ ] tabla del paso 3 antes de ejecutar (10 filas)
- [ ] `mostrarPrecio` sin `get()` a ciegas
- [ ] paso 5: todas las líneas del aviso en el `espero`
- [ ] P5.1–P5.5

---

### Tarea 6 — Entregable: `Order` con estado, `findItem` y resumen `record` (avanzado · 50 min)

**🎯 Qué vas a hacer, en una frase:** que tu `Order` real use **todo** lo de la lección: un estado `enum` con
reglas, una búsqueda con `Optional` y un resumen `record` para "mostrar en pantalla".

**Antes:** relee la sección 4.4, la 6.3 y la 3.7. Necesitas el `OrderStatus` de la Tarea 3.

**📁 Archivos:**

| Archivo | Qué es | ¿Tiene `main`? |
|---------|--------|----------------|
| `src/Models/Order.java` | **modificas** tu clase | ❌ no |
| `src/Models/OrderSummary.java` | **record** nuevo (el "DTO") | ❌ no |
| `src/practical/modern/OrderStatusLab.java` | el laboratorio | ✅ sí |

**🧠 La idea antes de empezar.** Un pedido es una **entidad** (tiene id y cambia: se paga, se envía…), así que
sigue siendo una **clase**. Pero:
- su **estado** es una lista cerrada con reglas → el `enum OrderStatus`;
- cuando una pantalla quiere "ver" el pedido, no le das el `Order` entero (podría modificarlo): le das una
  **foto** de solo lectura → un `record OrderSummary`.

```
Order (clase, cambia)                        OrderSummary (record, foto que no cambia)
 ├─ id                          summary()     ├─ orderId
 ├─ customer          ─────────────────────▶  ├─ customerName
 ├─ items (Map)                               ├─ itemCount, totalUnits
 └─ status: OrderStatus                       ├─ total
                                              └─ status
```

**Requisitos:**

| Miembro de `Order` | Qué hace |
|--------------------|----------|
| campo `private OrderStatus status` | empieza en `OrderStatus.PENDING` (en el constructor o al declararlo) |
| `OrderStatus getStatus()` | devuelve el estado |
| `void changeStatus(OrderStatus next)` | si `status.canTransitionTo(next)` es `false` → `throw new IllegalStateException("No se puede pasar de " + status + " a " + next);` Si es `true`, cambia el estado |
| `addItem(...)` (el que ya tienes) | **antes de agregar**: si el estado **no** es `PENDING` → `throw new IllegalStateException("No se pueden agregar productos a un pedido en estado " + status);` |
| `Optional<OrderItem> findItem(Product product)` | el item de ese producto, o vacío. Pista: `orderItems.get(product)` devuelve `null` si no está (sección 6.2, `ofNullable`) |
| `OrderSummary summary()` | crea y devuelve un `OrderSummary` con los datos actuales |

```java
// src/Models/OrderSummary.java
public record OrderSummary(BigInteger orderId, String customerName, int itemCount,
                           int totalUnits, BigDecimal total, OrderStatus status) {}
```
> `IllegalStateException` es como la `IllegalArgumentException` que ya usas, pero significa *"el objeto está en
> un estado en el que no se puede hacer eso"*. En la lección 06 la vas a entender a fondo.

> Si tu `addItem` todavía tiene el `try/catch` que devuelve `false` (de la lección 03), **no lo toques ahora**:
> agrega la comprobación del estado **antes** del `try`. En la lección 06 vas a ver por qué ese `try/catch` es un
> problema.

**Datos para `OrderStatusLab`** (productos de la Tarea 3 de la lección 04):
```java
Customer lucia = new Customer(BigInteger.ONE, "Lucía Ramos", "lucia@mail.com");
Order order = new Order(BigInteger.valueOf(1001), lucia);
order.addItem(thinkpad, 1);
order.addItem(mouse, 2);
order.addItem(curso, 1);
```
> No agregues el mismo producto dos veces (eso depende de la Tarea 6 de la lección 03, que sigue pendiente).

Para no repetir `try/catch` en cada paso, haz este método en el lab:
```java
static void intentar(Order order, OrderStatus next) {
    // try: order.changeStatus(next) e imprime "ok → " + order.getStatus()
    // catch (IllegalStateException e): imprime "rechazado: " + e.getMessage()
}
```

**Pasos:**

**Paso 1.** Imprime `order.summary()`. `// espero:` la línea completa (formato de `toString` de un record). Calcula
el total a mano: `1200.00×1 + 49.00×2 + 49.00×1`.

**Paso 2.** Imprime en una línea `order.findItem(mouse).isPresent()`, `order.findItem(mouse).get().getQuantity()`
y `order.findItem(audifonos)`. `// espero:`

**Paso 3 — el recorrido de un pedido.** Llena la tabla **antes** de ejecutar y después ejecuta las líneas **en
este orden**:

| # | Línea | Estado antes | espero (`ok → ...` o `rechazado: ...`) | real |
|---|-------|--------------|----------------------------------------|------|
| a | `intentar(order, OrderStatus.PAID);` | PENDING | | |
| b | `order.addItem(audifonos, 1);` (en su `try/catch`, imprime `e.getMessage()`) | | | |
| c | `intentar(order, OrderStatus.PENDING);` | | | |
| d | `intentar(order, OrderStatus.PAID);` | | | |
| e | `intentar(order, OrderStatus.SHIPPED);` | | | |
| f | `intentar(order, OrderStatus.CANCELLED);` | | | |
| g | `intentar(order, OrderStatus.DELIVERED);` | | | |

> La columna "estado antes" es la clave: un intento **rechazado** no cambia el estado. Llénala fila por fila.

**Paso 4.** Imprime `order.summary()` y `order.getStatus().isFinal()`. `// espero:` ¿qué cambió en el resumen
respecto al paso 1?

**Paso 5 — otro pedido que se cancela.** Crea `order2` (id `1002`, mismo cliente) con `audifonos` × 3. Ejecuta
`intentar(order2, OrderStatus.CANCELLED)` y después `intentar(order2, OrderStatus.PAID)`. Imprime
`order2.summary()`. `// espero:` las 3 líneas (calcula el total: `89.90 × 3`).

**Paso 6 — el resumen es una foto.** Guarda `OrderSummary foto = order2.summary();`. Imprime
`foto.equals(order2.summary())` y `foto.total()`. `// espero:` ¿Por qué el `equals` da lo que da, si son dos
objetos distintos?

**Preguntas:**
- **P6.1** *"`Order` es una clase y no un record porque ___. `OrderSummary` es un record porque ___."*
- **P6.2** *"En el paso 3, la fila (c) fue rechazada porque ___, y el estado siguió siendo ___."*
- **P6.3** *"`changeStatus` no tiene ningún `if` sobre estados concretos (como `if (status == PAID && ...)`)
  porque la regla la decide ___. Si agrego un estado nuevo, tengo que cambiar ___ y no ___."*
- **P6.4** *"`findItem` usa `Optional.ofNullable` y no `Optional.of` porque `get` del `Map` ___."*
- **P6.5** *"`summary()` devuelve un `OrderSummary` en vez del propio `Order` porque quien lo recibe ___."*

**Checklist:**
- [ ] `status` empieza en `PENDING`; `changeStatus` usa `canTransitionTo` (sin `if` por estado)
- [ ] `addItem` rechaza si no está en `PENDING`
- [ ] `findItem` con `Optional.ofNullable`
- [ ] `OrderSummary` como record en su propio archivo
- [ ] tabla del paso 3 con la columna "estado antes", llenada **antes** de ejecutar
- [ ] pasos 1 y 5 con el total calculado a mano
- [ ] P6.1–P6.5

---

## 11. Resumen de la lección (se llena al cerrar)

| Tarea | Nota | Estado |
|-------|------|--------|
| Teoría + autoevaluación | | |
| 1 | | |
| 2 | | |
| 3 | | |
| 4 | | |
| 5 | | |
| 6 | | |
