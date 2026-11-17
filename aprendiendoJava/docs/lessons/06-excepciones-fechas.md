# Lección 06 — Excepciones y fechas (`java.time`)

> Ruta: Fase 1 (Java moderno), semana 6 del roadmap.
> Viene de: Lección 05 (Java moderno: inmutabilidad, `record`, `enum`, `switch`, `Optional`, `var`).
> Prepara: el manejo de errores de Spring (`@RestControllerAdvice` y `ProblemDetail`, semana 17), `@Transactional`
> y el rollback (semana 20), y las fechas en las entidades JPA (semana 18).
> Todos los comportamientos descritos aquí fueron comprobados ejecutando código en Java 21.
> Las salidas que ves en los ejemplos (`// → ...`) son **reales**, copiadas del terminal.

---

## Cómo usar esta lección

Igual que las lecciones 03 y 04: está escrita **desde cero** y no supone que recuerdes nada.

Cada sección tiene la misma forma:

| Parte | Qué es |
|-------|--------|
| 🧠 **En una frase** | la idea de toda la sección en una línea. Si solo te quedas con algo, que sea esto |
| 📖 **Explicación** | la idea en palabras simples |
| 💻 **Ejemplo** | código pequeño con su salida real |
| 🧪 **Pruébalo** | un experimento corto que **tú** escribes y ejecutas (3–8 min). **No te lo saltes** |
| ✅ **¿Lo entendiste?** | 2 o 3 preguntas. La respuesta está escondida debajo: piensa primero y después ábrela |

La lección tiene **dos partes**:
- **Parte 1 — Excepciones** (secciones 1 a 6): qué hacer cuando algo sale mal.
- **Parte 2 — Fechas** (sección 7): cómo trabajar con días, meses y horas sin errores.

Están juntas porque las fechas **lanzan** excepciones (una fecha mal escrita, el 30 de febrero…) y porque las dos
cosas aparecen juntas en la Tarea 6 (`Renewal`).

**Plan de lectura (unas 2 h 20 min en total, en 3 sesiones):**

| Sesión | Lee | Tiempo |
|--------|-----|--------|
| 1 | Secciones 0 (repaso) + 1 + 2 + 3 | 50 min |
| 2 | Secciones 4 + 5 + 6 | 45 min |
| 3 | Secciones 7 + 8 + 9 | 45 min |

Después vienen las **tareas** (sección 10). Hazlas **una por vez, en orden**.

**Dónde hacer los 🧪:** crea **un solo archivo** `src/practical/exceptions/ExceptionPlayground.java` (paquete
nuevo `practical.exceptions`) con un `main`. Ahí van todos los experimentos, uno debajo del otro, con un separador:
```java
System.out.println("---- 🧪 Sección 3 ----");
```

**La plantilla de siempre:**
```java
System.out.println(algo);
// espero: ...
// real:   ...   ← solo si fallaste
// porque: ...   ← solo si fallaste
```

**Novedad de esta lección:** muchas predicciones no son "qué valor imprime", sino **"en qué orden se imprimen
las líneas"** y **"¿compila o no compila?"**. Para el orden, escribe el `espero` como una lista: `// espero: A, B, E, F`.

**Sintaxis:** esta lección **no usa lambdas (`->`), ni `::`, ni streams**. La única flecha permitida es la del
`switch` moderno de la lección 05 (que no es una lambda).

**Si algo no se entiende:** pregúntame por el número de sección ("no entiendo la sección 3.2").

---

## 0. Repaso: lo que necesitas de las lecciones anteriores

Estas cinco cosas vuelven a salir. Léelas aunque creas que las sabes.

### 0.1 Ya lanzas excepciones (sin haberlo estudiado)

En tu `Product` y en tu `OrderItem` ya escribiste esto:
```java
if (quantity <= 0) {
    throw new IllegalArgumentException("Tiene que existe un cantidad de productos mayor a 0");
}
```
Eso es **lanzar** una excepción. En esta lección vas a entender qué pasa **después** de esa línea: a dónde
"viaja" la excepción, quién la puede atrapar y qué ve el usuario si nadie la atrapa.

### 0.2 `e.getMessage()` puede ser `null`

En la Tarea 1 de la lección 04 escribiste `"no se puede ordenar: " + e.getMessage()` y salió `null`. Por qué:
```java
try {
    List.of("rojo", "azul").remove("rojo");
} catch (UnsupportedOperationException e) {
    System.out.println(e.getMessage());                 // → null
    System.out.println(e);                              // → java.lang.UnsupportedOperationException
    System.out.println(e.getClass().getSimpleName());   // → UnsupportedOperationException
}
```
- `getMessage()` devuelve **solo el texto** que alguien escribió al crear la excepción. Si nadie escribió texto → `null`.
- `System.out.println(e)` imprime **el nombre de la clase + el texto** (si hay). Nunca sale `null` solo.

Con texto, se ve la diferencia:
```java
IllegalArgumentException e = new IllegalArgumentException("El precio no puede ser negativo");
System.out.println(e.getMessage());   // → El precio no puede ser negativo
System.out.println(e);                // → java.lang.IllegalArgumentException: El precio no puede ser negativo
```
**Regla:** para depurar, imprime `e` (no `e.getMessage()`). Para mostrar un mensaje bonito a un usuario, usa
`getMessage()` de **tus** excepciones (sección 5), que siempre tendrán texto.

### 0.3 Lo que un método **devuelve** vs lo que **hace** (tu punto débil de siempre)

- `lista.sort(...)` **hace** (ordena) y **no devuelve** nada.
- `map.put(k, v)` **hace** (guarda) y **devuelve** el valor viejo.

En las fechas aparece el caso **contrario**: `fecha.plusDays(1)` **no hace** nada a `fecha` y **devuelve** una
fecha nueva. Si no guardas lo que devuelve, se pierde (sección 7.3). Tenlo en mente desde ya.

### 0.4 `instanceof`: de lo más específico a lo más general (lección 04, sección 10)

```java
if (e instanceof Empleado) ...   // ← un Gerente también entra aquí
if (e instanceof Gerente)  ...   // ← nunca se llega
```
Con los `catch` pasa **exactamente lo mismo** (sección 3.4). Si entiendes esto, ya entiendes el orden de los
`catch`.

### 0.5 `Optional` (lección 05, sección 6)

`Optional<T>` es "una caja que puede tener un valor o estar vacía". Lo usaste para que `findByName` no devuelva
`null`. En la sección 4.4 vas a ver **cuándo** usar `Optional` y **cuándo** lanzar una excepción.

---

## 1. Qué es una excepción y cómo leer un *stack trace*

🧠 **En una frase:** una excepción es un objeto que "sale volando" desde donde ocurrió el problema, hacia arriba
por los métodos que se llamaron, hasta que alguien la atrapa; si nadie la atrapa, el programa se detiene y te
muestra el camino (*stack trace*).

### 📖 Explicación

Imagina una cadena de llamadas: `main` llama a `procesarPedido`, que llama a `leerCantidad`. Es como una pila de
platos: el último que se llamó está arriba.

```
leerCantidad      ← arriba: el último llamado (aquí explota)
procesarPedido
main              ← abajo: el primero (donde empezó todo)
```

Cuando en `leerCantidad` ocurre un problema, Java:
1. **crea un objeto** excepción (por ejemplo `NumberFormatException`) con un mensaje;
2. **deja de ejecutar** `leerCantidad` en esa línea (lo que viene después **no** se ejecuta);
3. **sube** al método que lo llamó (`procesarPedido`) y pregunta: *"¿alguien atrapa esto aquí?"*;
4. si no, sigue subiendo (`main`)…;
5. si llega arriba del todo sin que nadie la atrape, **el programa termina** e imprime el *stack trace*.

**Puente desde PHP:** es exactamente igual que en PHP. Un `throw new InvalidArgumentException(...)` sube por las
funciones hasta un `catch`; si no hay ninguno, PHP muestra "Fatal error: Uncaught InvalidArgumentException" con la
lista de llamadas. En Laravel no lo ves así porque el *Handler* de Laravel atrapa todo y muestra la página de error.
(Spring tiene su propio "Handler": semana 17.)

### 💻 Ejemplo

```java
package demo;

public class Tienda {
    public static void main(String[] args) {
        System.out.println("inicio");
        procesarPedido("3 cajas");                 // línea 6
        System.out.println("fin");
    }

    static void procesarPedido(String texto) {
        int cantidad = leerCantidad(texto);        // línea 11
        System.out.println("cantidad: " + cantidad);
    }

    static int leerCantidad(String texto) {
        return Integer.parseInt(texto);            // línea 16
    }
}
```
Salida real:
```
inicio
Exception in thread "main" java.lang.NumberFormatException: For input string: "3 cajas"
	at java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:67)
	at java.base/java.lang.Integer.parseInt(Integer.java:662)
	at java.base/java.lang.Integer.parseInt(Integer.java:778)
	at demo.Tienda.leerCantidad(Tienda.java:16)
	at demo.Tienda.procesarPedido(Tienda.java:11)
	at demo.Tienda.main(Tienda.java:6)
```
📖 Fíjate: **`fin` nunca se imprime**, y `cantidad: ...` tampoco. La excepción cortó todo.

### 1.1 Cómo leer un *stack trace*, línea por línea

| Parte | Qué te dice |
|-------|-------------|
| `Exception in thread "main"` | nadie la atrapó; el programa murió |
| `java.lang.NumberFormatException` | **qué** pasó (la clase de la excepción) |
| `: For input string: "3 cajas"` | el **mensaje** (`getMessage()`) |
| líneas `at java.base/...` | código **de Java** (no es tuyo). Sáltalas |
| **la primera línea `at` con TU paquete** (`demo.Tienda.leerCantidad(Tienda.java:16)`) | **dónde explotó en tu código**: archivo y número de línea. **Empieza a buscar aquí** |
| las líneas siguientes | quién llamó a quién, de abajo hacia arriba: `main` (línea 6) → `procesarPedido` (línea 11) → `leerCantidad` (línea 16) |

**El método para leerlo siempre igual (3 pasos):**
1. Lee la **primera línea**: qué excepción y qué mensaje.
2. Baja hasta la **primera línea `at` que tenga tu paquete**: ahí está el problema.
3. Si no entiendes por qué llegó ese dato malo, sigue bajando: ¿quién le pasó `"3 cajas"`?

> En IntelliJ, el `(Tienda.java:16)` sale **en azul**: haz clic y te lleva a la línea.
> Los números de las líneas `java.base/...` pueden cambiar según tu versión de Java (tú usas 24). No importan.

### 1.2 Un *stack trace* con "Caused by"

A veces una excepción **envuelve** a otra (lo harás en la sección 5.3). Se ve así:
```
Exception in thread "main" java.lang.IllegalStateException: No se pudo cargar el precio '9,99'
	at demo.Caused.cargarPrecio(Caused.java:12)
	at demo.Caused.main(Caused.java:5)
Caused by: java.lang.NumberFormatException: Character , is neither a decimal digit number, decimal point, nor "e" notation exponential mark.
	at java.base/java.math.BigDecimal.<init>(BigDecimal.java:608)
	at java.base/java.math.BigDecimal.<init>(BigDecimal.java:497)
	at java.base/java.math.BigDecimal.<init>(BigDecimal.java:903)
	at demo.Caused.cargarPrecio(Caused.java:10)
	... 1 more
```
📖 Se lee así: *"pasó un `IllegalStateException` (no se pudo cargar el precio), **causado por** un
`NumberFormatException` (el `BigDecimal` no acepta la coma)"*. **El problema de verdad suele estar en el último
`Caused by`.** El `... 1 more` significa "el resto del camino es igual al de arriba".

### 1.3 Las excepciones que más vas a ver

Todas comprobadas:

| Código | Excepción + mensaje real |
|--------|--------------------------|
| `Integer.parseInt("12a")` | `NumberFormatException: For input string: "12a"` |
| `new BigDecimal("12,50")` | `NumberFormatException: Character , is neither a decimal digit number, ...` |
| `String s = null; s.length();` | `NullPointerException: Cannot invoke "String.length()" because "s" is null` |
| `new ArrayList<String>().get(0)` | `IndexOutOfBoundsException: Index 0 out of bounds for length 0` |
| `10 / 0` | `ArithmeticException: / by zero` |
| `(Integer) objetoQueEsString` | `ClassCastException: class java.lang.String cannot be cast to class java.lang.Integer ...` |
| `List.of(1, 2).add(3)` | `UnsupportedOperationException` (sin mensaje) |
| `Map.of("k", 1, "k", 2)` | `IllegalArgumentException: duplicate key: k` |

📖 El mensaje del `NullPointerException` es muy útil desde Java 14: te dice **qué variable** era `null`. Léelo
entero.

### 🧪 Pruébalo · sección 1 (6 min)

En `ExceptionPlayground`:
1. Escribe un método `static int edad(String texto)` que haga `return Integer.parseInt(texto);`, y otro
   `static void registrar(String texto)` que llame a `edad(texto)` e imprima `"edad: " + ...`.
2. En `main`, imprime `"A"`, llama a `registrar("treinta")` e imprime `"B"`.
   `// espero:` ¿se imprime `B`? ¿qué excepción y qué mensaje?
3. Ejecuta. En un comentario, copia **solo** las líneas `at` que tienen **tu** clase, y escribe al lado de cada
   una el nombre del método.
4. Después **comenta** la llamada a `registrar` (si no, el resto de los 🧪 no se ejecuta).

### ✅ ¿Lo entendiste?
1. En un *stack trace*, ¿qué línea miras primero para encontrar el error en tu código?
2. Una excepción ocurre en la línea 20 de un método. ¿Se ejecuta la línea 21?
3. ¿Dónde suele estar el problema real cuando hay un `Caused by`?

<details><summary>Respuestas</summary>

1. La primera línea `at` que tenga **tu** paquete/clase (después de leer la primera línea con el tipo y el mensaje).
2. No. La excepción corta el método en la línea 20 y sube al que lo llamó.
3. En el último `Caused by`: es la excepción original.
</details>

---

## 2. Dos familias: *checked* y *unchecked*

🧠 **En una frase:** las excepciones *unchecked* (`RuntimeException` y sus hijas) son errores de programación o de
datos y el compilador no te obliga a nada; las *checked* (el resto de `Exception`) son problemas "externos" que el
compilador **te obliga** a atrapar o a declarar con `throws`.

### 📖 Explicación

Todas las excepciones son clases, y forman un árbol de herencia (como `Product` → `PhysicalProduct` → `Laptop`):

```
Throwable
├── Error                         ← problemas graves de la JVM. NO se atrapan.
│   └── StackOverflowError, OutOfMemoryError
└── Exception                     ← CHECKED (el compilador te obliga)
    ├── IOException               ← CHECKED: archivos, red
    │   └── FileSystemException
    │       └── NoSuchFileException
    ├── InterruptedException      ← CHECKED
    └── RuntimeException          ← UNCHECKED (el compilador no dice nada)
        ├── IllegalArgumentException
        │   └── NumberFormatException      ← ¡es hija de IllegalArgumentException!
        ├── IllegalStateException
        ├── NullPointerException
        ├── ArithmeticException
        ├── IndexOutOfBoundsException
        ├── ClassCastException
        ├── UnsupportedOperationException
        ├── NoSuchElementException          ← la de Optional.orElseThrow()
        └── DateTimeException
            └── DateTimeParseException      ← las de fechas (sección 7)
```
(Comprobado imprimiendo `getSuperclass()` de cada una.)

| Familia | Quién | ¿El compilador te obliga? | Cuándo pasa | Ejemplos |
|---------|-------|---------------------------|-------------|----------|
| **unchecked** | `RuntimeException` y sus hijas | **no** | un **bug** o un **dato inválido**: lo podrías haber evitado | `NullPointerException`, `IllegalArgumentException`, `NumberFormatException` |
| **checked** | `Exception` y sus hijas, **menos** `RuntimeException` | **sí**: atrapar (`try/catch`) o declarar (`throws`) | algo **externo** que puede fallar aunque tu código esté perfecto | `IOException` (el archivo no existe, el disco falla) |
| **error** | `Error` | no | la JVM está en problemas | `StackOverflowError` (recursión infinita) |

**Puente desde PHP:** en PHP **no existen** las checked: ninguna función te obliga a poner `try/catch`. En Java
sí, y es lo más nuevo de esta sección.

### 💻 Ejemplo: el compilador te obliga (checked)

```java
import java.nio.file.*;

public class Checked1 {
    public static void main(String[] args) {
        String texto = Files.readString(Path.of("notas.txt"));
        System.out.println(texto);
    }
}
```
**No compila:**
```
Checked1.java:4: error: unreported exception IOException; must be caught or declared to be thrown
        String texto = Files.readString(Path.of("notas.txt"));
                                       ^
```
📖 Traducción: *"esta línea puede lanzar `IOException` (checked) y no lo resolviste: **atrápala** o **declárala**"*.

Lanzar tú una `Exception` "pelada" también es checked:
```java
static void validar(int edad) {
    if (edad < 0) {
        throw new Exception("edad negativa");
        // → error: unreported exception Exception; must be caught or declared to be thrown
    }
}
```
Con `IllegalArgumentException` (unchecked), el mismo método **compila** sin quejarse:
```java
static void validar(int edad) {
    if (edad < 0) {
        throw new IllegalArgumentException("edad negativa");   // ✅ compila
    }
}
```

### 2.1 Cómo saber si una excepción es checked

Pregúntate: **¿es hija de `RuntimeException`?**
- **Sí** → unchecked. Puedes atraparla si quieres, pero el compilador no te obliga.
- **No** (y es hija de `Exception`) → checked. El compilador te obliga.

En IntelliJ: `Ctrl + clic` sobre el nombre de la excepción y mira su `extends`. O deja que IntelliJ te lo diga:
si lo subraya en rojo con *"Unhandled exception"*, es checked.

### 2.2 Un dato curioso que vas a usar: `NumberFormatException` **es** un `IllegalArgumentException`

```java
try {
    Integer.parseInt("x");
} catch (IllegalArgumentException e) {
    System.out.println("atrapado como IAE: " + e.getClass().getSimpleName());
}
// → atrapado como IAE: NumberFormatException
```
📖 Un `catch` de la clase padre atrapa a **todas** las hijas (igual que `instanceof`). Esto vuelve en la Tarea 4.

### 🧪 Pruébalo · sección 2 (5 min)

Para cada línea, escribe en un comentario `// ¿compila? espero: sí/no`. Después escríbela de verdad en un
método `static void prueba2()` y mira si IntelliJ la subraya en rojo. Si no compila, copia el error en el
comentario y **comenta la línea**.
1. `throw new IllegalStateException("caja cerrada");`
2. `throw new Exception("caja cerrada");`
3. `Integer.parseInt("cien");`
4. `Files.readString(Path.of("x.txt"));` (necesitas `import java.nio.file.*;`)
5. `Thread.sleep(100);` (pausa 100 ms; `Ctrl + clic` en `sleep` y mira qué declara)

### ✅ ¿Lo entendiste?
1. ¿Qué diferencia hay entre checked y unchecked, en una línea?
2. `NoSuchFileException`, ¿es checked o unchecked? ¿Por qué?
3. ¿Un `catch (IllegalArgumentException e)` atrapa un `NumberFormatException`?

<details><summary>Respuestas</summary>

1. Checked: el compilador te obliga a atraparla o declararla. Unchecked (`RuntimeException`): no te obliga.
2. Checked: es hija de `IOException` → `Exception`, y no pasa por `RuntimeException`.
3. Sí: `NumberFormatException extends IllegalArgumentException`. El `catch` del padre atrapa a los hijos.
</details>

---

## 3. `try` / `catch` / `finally`: el flujo

🧠 **En una frase:** el `try` se ejecuta hasta la primera excepción; si alguna ocurre, salta al `catch` que la
atrape; el `finally` se ejecuta **siempre**, pase lo que pase, incluso con `return`.

### 📖 Explicación

```java
try {
    // código que puede fallar
} catch (TipoDeExcepcion e) {
    // qué hacer si falla con ese tipo
} finally {
    // se ejecuta SIEMPRE (haya fallo o no). Es opcional.
}
// el programa sigue aquí
```
Es igual que en PHP (`try { } catch (Exception $e) { } finally { }`).

### 3.1 💻 El orden exacto en que se ejecutan las líneas

```java
static void dividir(int a, int b) {
    System.out.println("A");
    try {
        System.out.println("B");
        int r = a / b;
        System.out.println("C " + r);
    } catch (ArithmeticException e) {
        System.out.println("D " + e.getMessage());
    } finally {
        System.out.println("E");
    }
    System.out.println("F");
}
```
| Llamada | Salida real | Qué pasó |
|---------|-------------|----------|
| `dividir(10, 2)` | `A`, `B`, `C 5`, `E`, `F` | sin error: el `catch` se **salta** |
| `dividir(10, 0)` | `A`, `B`, `D / by zero`, `E`, `F` | error en `a / b`: **`C` no se imprime**, salta al `catch` |

📖 Las dos reglas:
1. Lo que está en el `try` **después** de la línea que falla **no se ejecuta** (`C`).
2. El `catch` solo se ejecuta si hubo una excepción **de ese tipo**. `finally` y lo de después, siempre.

### 3.2 💻 `finally` corre incluso con `return`

```java
static String conReturn() {
    try {
        System.out.println("en try");
        return "del try";
    } finally {
        System.out.println("en finally");
    }
}

System.out.println(conReturn());
// → en try
//   en finally
//   del try
```
📖 ¿Por qué `en finally` sale **antes** que `del try`? Porque el `return` "prepara" el valor, después corre el
`finally`, y **recién entonces** el método termina y `println` recibe `"del try"` para imprimirlo.

> ⚠️ Nunca pongas un `return` **dentro** del `finally`: pisaría el `return` del `try` y escondería excepciones.

### 3.3 💻 Si el `catch` no es del tipo correcto, la excepción sigue subiendo

Un `try` puede no tener `catch` (solo `finally`), o tener un `catch` de otro tipo. En los dos casos, el `finally`
corre y la excepción **sigue subiendo**:
```java
static void sinCatch(String t) {
    try {
        System.out.println("try " + Integer.parseInt(t));
    } finally {
        System.out.println("finally de sinCatch");
    }
    System.out.println("despues de sinCatch");
}

// en main:
try {
    sinCatch("7");
    sinCatch("siete");
} catch (NumberFormatException e) {
    System.out.println("main atrapó: " + e.getMessage());
}
// → try 7
//   finally de sinCatch
//   despues de sinCatch
//   finally de sinCatch               ← con "siete": corre el finally...
//   main atrapó: For input string: "siete"   ← ...y la excepción sube a main
```
📖 Con `"siete"`, **`despues de sinCatch` no se imprime**: la excepción salió del método.

### 3.4 Varios `catch`: el orden importa (como `instanceof`)

Puedes poner varios `catch`. Java los prueba **de arriba hacia abajo** y entra al **primero** que encaje:
```java
try { ... }
catch (NumberFormatException e)    { System.out.println("NFE"); }
catch (IllegalArgumentException e) { System.out.println("IAE"); }
catch (RuntimeException e)         { System.out.println("Runtime: " + e.getMessage()); }
```
| Lo que se lanza | Entra a | Por qué |
|-----------------|---------|---------|
| `Integer.parseInt("x")` | `NFE` | encaja en el primero |
| `throw new IllegalArgumentException("y")` | `IAE` | no es `NumberFormatException`; encaja en el segundo |
| `throw new IllegalStateException("z")` | `Runtime: z` | solo encaja en el tercero |

**Si pones el padre primero, no compila** (el hijo nunca se alcanzaría, igual que el `instanceof` de la lección 04):
```java
try {
    Integer.parseInt("x");
} catch (RuntimeException e) {
    System.out.println("runtime");
} catch (NumberFormatException e) {      // ← nunca se llegaría
    System.out.println("formato");
}
// → error: exception NumberFormatException has already been caught
```
**Regla:** `catch` de **lo más específico a lo más general**. Hijo antes que padre.

### 3.5 *Multi-catch*: un `catch` para dos tipos

Si haces lo mismo para dos excepciones, únelas con `|`:
```java
static void convertir(Object dato) {
    try {
        String s = (String) dato;
        System.out.println(Integer.parseInt(s) * 2);
    } catch (ClassCastException | NumberFormatException e) {
        System.out.println("dato malo: " + e.getClass().getSimpleName());
    }
}
```
⚠️ Los tipos del `|` **no pueden ser padre e hijo**:
```java
catch (NumberFormatException | IllegalArgumentException e)
// → error: Alternatives in a multi-catch statement cannot be related by subclassing
//     Alternative NumberFormatException is a subclass of alternative IllegalArgumentException
```
Tiene sentido: si atrapas al padre, ya atrapaste al hijo. Basta con `catch (IllegalArgumentException e)`.

### 3.6 No puedes atrapar una checked que nunca se lanza

```java
try {
    System.out.println("hola");
} catch (IOException e) {
    System.out.println("nunca");
}
// → error: exception IOException is never thrown in body of corresponding try statement
```
📖 El compilador sabe que `println` no lanza `IOException`. (Con las unchecked no se queja: cualquier línea podría
lanzar una.)

### 🧪 Pruébalo · sección 3 (8 min)

Copia este método **tal cual** en `ExceptionPlayground`:
```java
static void pagar(String monto) {
    System.out.println("1");
    try {
        System.out.println("2");
        BigDecimal m = new BigDecimal(monto);
        System.out.println("3 " + m);
    } catch (NumberFormatException e) {
        System.out.println("4");
    } finally {
        System.out.println("5");
    }
    System.out.println("6");
}
```
1. Llama `pagar("15.90")`. `// espero:` la lista de números en orden.
2. Llama `pagar("15,90")`. `// espero:`
3. Cambia el `catch` a `catch (ArithmeticException e)` y llama `pagar("15,90")` dentro de un `try/catch
   (NumberFormatException e)` en `main` que imprima `"main"`. `// espero:` ¿se imprime `6`?
4. Vuelve a poner `NumberFormatException` en el `catch`.

### ✅ ¿Lo entendiste?
1. Un `try` tiene 5 líneas y falla la 2. ¿Se ejecutan la 3, 4 y 5?
2. ¿Cuándo **no** se ejecuta el `finally`? (Pista: casi nunca.)
3. ¿Por qué no compila `catch (Exception e)` seguido de `catch (IOException e)`?

<details><summary>Respuestas</summary>

1. No. Desde la línea que falla salta directo al `catch` (o sube, si no hay `catch` del tipo).
2. Prácticamente siempre se ejecuta: con error, sin error, con `return`. (Solo no corre si la JVM se apaga, por
   ejemplo con `System.exit`.)
3. Porque `IOException` es hija de `Exception`: el primer `catch` ya la atrapa y el segundo nunca se alcanzaría
   ("has already been caught"). El hijo va primero.
</details>

---

## 4. `throw` vs `throws`, y ¿atrapar o dejar pasar?

🧠 **En una frase:** `throw` **lanza** una excepción ahora; `throws` en la firma **avisa** que el método puede
lanzarla; y solo atrapas una excepción si **sabes qué hacer con ella**, si no, la dejas subir.

### 4.1 `throw` y `throws`: se parecen pero no son lo mismo

| Palabra | Dónde va | Qué hace | Ejemplo |
|---------|----------|----------|---------|
| **`throw`** (sin s) | dentro del método, como una instrucción | **lanza** una excepción **ahora** | `throw new IllegalArgumentException("precio negativo");` |
| **`throws`** (con s) | en la **firma** del método | **avisa** al que llama: "puedo lanzar esto, encárgate tú" | `static String leer() throws IOException { ... }` |

💻
```java
static String leerNotas() throws IOException {          // ← avisa
    return Files.readString(Path.of("notas.txt"));      //   puede lanzar IOException
}

public static void main(String[] args) {
    System.out.println(leerNotas());
    // → error: unreported exception IOException; must be caught or declared to be thrown
}
```
📖 `throws` **no resuelve** el problema: lo **pasa** al que llama. Ahora es `main` el que tiene que atraparla (o
declarar `throws IOException` también, y así sucesivamente).

- Con **checked**, `throws` es obligatorio si no la atrapas.
- Con **unchecked**, `throws` es opcional (casi nunca se escribe; a veces se documenta).

### 4.2 ¿Atrapo o la dejo pasar?

La pregunta correcta es: **¿en este método sé qué hacer si falla?**

| Si en este método… | Entonces… | Ejemplo |
|--------------------|-----------|---------|
| puedes **arreglarlo** o tienes un **plan B** | atrápala | leer un archivo de configuración; si no existe, usar valores por defecto |
| puedes **seguir con lo demás** | atrápala, regístrala, sigue | importar 1000 líneas de un CSV: una línea mala no debe parar las otras 999 (Tarea 4) |
| **no** sabes qué hacer | **déjala subir** | `Inventory.reserve` no sabe si mostrar un mensaje, cancelar el pedido o reintentar: eso lo decide quien lo llamó |

En un backend real, la mayoría de las excepciones **suben** hasta un único lugar que las convierte en una
respuesta HTTP (en Laravel, el `Handler`; en Spring, `@RestControllerAdvice`, semana 17). Por eso en el código de
negocio se escriben **pocos** `try/catch`.

### 4.3 ⚠️ Los cuatro errores más comunes

**Error 1 — "Tragarse" la excepción y devolver `false`**

💻 Un préstamo de libros (otros datos que tus tareas):
```java
static Map<String, Integer> copias = new HashMap<>(Map.of("Quijote", 1));

static boolean prestarMal(String titulo) {
    try {
        int c = copias.get(titulo);
        if (c == 0) throw new IllegalStateException("sin copias");
        copias.put(titulo, c - 1);
        return true;
    } catch (Exception e) {
        return false;                 // ← ¿por qué falló? nadie lo sabrá
    }
}

System.out.println(prestarMal("Quijote") + " " + prestarMal("Quijote") + " "
        + prestarMal("Hamlet") + " " + prestarMal(null));
// → true false false false
```
📖 Tres `false` con **tres causas distintas**: no quedan copias, el libro no existe (`NullPointerException` al
convertir el `null` a `int`) y el título es `null`. El que llama **no puede distinguirlas**. Peor: un bug de
verdad (el `NullPointerException`) quedó **escondido**.

La versión buena lanza excepciones **con mensaje** y no atrapa nada:
```java
static void prestar(String titulo) {
    Integer c = copias.get(titulo);
    if (c == null) throw new IllegalArgumentException("No existe el libro: " + titulo);
    if (c == 0) throw new IllegalStateException("No quedan copias de: " + titulo);
    copias.put(titulo, c - 1);
}
// prestar("Quijote")  → OK
// prestar("Quijote")  → IllegalStateException: No quedan copias de: Quijote
// prestar("Hamlet")   → IllegalArgumentException: No existe el libro: Hamlet
```

**EN MI CÓDIGO:** tu `Order` (versión de hoy) tiene este error, y además otro:
```java
public boolean addItem(Product product, int quantity) {
    OrderItem orderItem = new OrderItem(product, quantity);   // ← esto SÍ puede lanzar (cantidad 0, null)
    try {
        this.orderItems.put(product, orderItem);              // ← esto NO lanza nunca
    } catch (Exception e) {
        return false;
    }
    return true;
}
```
Lo ejecuté con tu código:
```
addItem(mouse, 2)    → true
removeItem(mouse)    → true
removeItem(mouse)    → false
addItem(mouse, 0)    → se escapó: Tiene que existe un cantidad de productos mayor a 0
addItem(null, 1)     → se escapó: No puede ser un producto nullo
```
📖 El `try` protege la línea que **no** puede fallar, y la que **sí** falla está **fuera** del `try`. Resultado:
el método promete devolver `false` cuando algo sale mal, pero en realidad lanza una excepción. Lo mismo en
`removeItem`: `map.remove` no lanza nada, el `try/catch` sobra. (No lo arregles ahora: lo harás en la Tarea 3,
cuando tengas excepciones propias.)

**Error 2 — `catch` vacío**
```java
try { ... } catch (Exception e) { }      // ← el error desaparece sin dejar rastro
```
Es el peor de todos: el programa sigue con datos rotos y nadie sabe por qué.

**Error 3 — Atrapar solo para imprimir y seguir como si nada**
```java
try {
    inventario.reservar(mouse, 5);
} catch (Exception e) {
    System.out.println(e);   // imprime... y el pedido se confirma igual
}
confirmarPedido();
```
Si no sabes qué hacer, **no la atrapes**: déjala subir.

**Error 4 — Atrapar `Exception` (todo) cuando esperas una cosa concreta**

`catch (Exception e)` atrapa **también** los `NullPointerException` de tus bugs. Atrapa el tipo concreto que
esperas: `catch (NumberFormatException e)`.

### 4.4 ¿Excepción u `Optional`? (lección 05)

| Situación | Es… | Usa |
|-----------|-----|-----|
| buscar un producto por nombre y que no exista | **normal** (el usuario busca cosas que no hay) | `Optional<Product>` |
| reservar 5 unidades cuando hay 3 | un **error de negocio**: la operación no se puede hacer | excepción |
| construir un `Product` con precio negativo | un **dato inválido** | excepción (`IllegalArgumentException`) |
| leer un archivo que no existe | un **problema externo** | excepción (`IOException`) |

**Regla:** si "no hay resultado" es una respuesta **normal**, usa `Optional`. Si significa que **algo salió mal**,
lanza una excepción. Nunca uses excepciones para el flujo normal ("lanzo una excepción para salir del bucle").

💻 Las dos formas, con los libros:
```java
static Optional<Integer> buscarCopias(String titulo) {
    return Optional.ofNullable(copias.get(titulo));
}
buscarCopias("Hamlet").isPresent()     // → false   (no existe: respuesta normal)
buscarCopias("Quijote").orElse(-1)     // → 0
```

### 🧪 Pruébalo · sección 4 (5 min)

1. En `ExceptionPlayground`, escribe `static int dividirSeguro(int a, int b)` con un `try/catch
   (ArithmeticException e)` que devuelva `0` si falla. Imprime `dividirSeguro(10, 0)` y `dividirSeguro(0, 10)`.
   `// espero:`
2. En un comentario responde: viendo solo el resultado, ¿puedes saber cuál de los dos falló? ¿Es el **error 1**
   de la sección 4.3?
3. Abre tu `Order` y marca con un comentario `// TODO L06:` las líneas `try` que protegen código que no puede
   fallar. (No cambies nada más todavía.)

### ✅ ¿Lo entendiste?
1. Diferencia entre `throw` y `throws`.
2. `Inventory.reserve` detecta que no hay stock. ¿Debe atrapar su propia excepción y devolver `false`? ¿Por qué?
3. `findByName("teclado")` y no existe. ¿Excepción u `Optional`?

<details><summary>Respuestas</summary>

1. `throw` lanza una excepción ahora (instrucción dentro del método). `throws` en la firma avisa al que llama que
   el método puede lanzarla.
2. No. Con `false` se pierde el motivo, y `reserve` no sabe qué hacer en ese caso. Lanza una excepción con mensaje y
   que decida quien lo llamó.
3. `Optional`: que una búsqueda no encuentre nada es normal.
</details>

---

## 5. Excepciones propias del dominio

🧠 **En una frase:** crea tus propias excepciones (`extends RuntimeException`) cuando un error de negocio tiene
nombre propio ("stock insuficiente", "saldo insuficiente"), con un mensaje claro y, si sirve, campos con los datos
del problema.

### 📖 Explicación

`IllegalArgumentException("...")` sirve para datos inválidos en general. Pero para los errores **del negocio** es
mejor una clase propia:
- el **nombre** ya explica el problema: `InsufficientBalanceException` se entiende sin leer el mensaje;
- quien la atrapa puede atrapar **solo esa**: `catch (InsufficientBalanceException e)`;
- puede llevar **datos**: cuánto pediste, cuánto había;
- en Spring, cada una se puede convertir en un código HTTP distinto (semana 17).

**Puente desde PHP:** es igual que `class InsufficientBalanceException extends RuntimeException {}` en PHP/Laravel.

### 5.1 💻 Ejemplo completo: una billetera (como las del agregador de casino)

```java
package bank;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {   // ① unchecked
    private final BigDecimal requested;                                 // ② datos del problema
    private final BigDecimal available;

    public InsufficientBalanceException(BigDecimal requested, BigDecimal available) {
        super("Saldo insuficiente: pediste " + requested + " y solo hay " + available);   // ③ el mensaje
        this.requested = requested;
        this.available = available;
    }

    public BigDecimal getRequested() { return requested; }
    public BigDecimal getAvailable() { return available; }
}
```
📖 Las tres partes:
1. **`extends RuntimeException`**: es unchecked (sección 5.4 explica por qué).
2. **Campos `final`** con los datos, y sus getters. No hay setters: una excepción no cambia.
3. **`super(mensaje)`**: le pasa el texto al padre. Es lo que después devuelve `getMessage()`. **Si no llamas a
   `super(...)` con un texto, `getMessage()` será `null`** (sección 0.2).

La clase que la lanza:
```java
public class Wallet {
    private BigDecimal balance;

    public Wallet(BigDecimal initial) {
        this.balance = initial;
    }

    public void bet(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La apuesta debe ser mayor que 0: " + amount);   // dato inválido
        }
        if (amount.compareTo(balance) > 0) {
            throw new InsufficientBalanceException(amount, balance);                          // regla de negocio
        }
        balance = balance.subtract(amount);    // ← solo llega aquí si todo está bien
    }

    public BigDecimal getBalance() { return balance; }
}
```
Y quien la usa:
```java
Wallet w = new Wallet(new BigDecimal("50.00"));
w.bet(new BigDecimal("20.00"));
System.out.println(w.getBalance());                  // → 30.00

try {
    w.bet(new BigDecimal("45.00"));
    System.out.println("apuesta aceptada");          // ← no se imprime
} catch (InsufficientBalanceException e) {
    System.out.println(e.getMessage());              // → Saldo insuficiente: pediste 45.00 y solo hay 30.00
    System.out.println("faltan: " + e.getRequested().subtract(e.getAvailable()));   // → faltan: 15.00
}
System.out.println(w.getBalance());                  // → 30.00   ← ¡no cambió!
```
📖 **Lo más importante:** el saldo sigue en `30.00`. Como la excepción se lanza **antes** del `subtract`, el
objeto queda **igual que antes**. Valida **todo primero**, modifica **al final**. Así una operación que falla no
deja el objeto a medias.

Y si nadie la atrapa, el *stack trace* muestra **tu** clase:
```
Exception in thread "main" bank.InsufficientBalanceException: Saldo insuficiente: pediste 100 y solo hay 30.00
	at bank.Wallet.bet(Wallet.java:17)
	at bank.WalletDemo.main(WalletDemo.java:24)
```

### 5.2 El orden de las validaciones importa

En `bet`, primero se valida el **dato** (`> 0`) y después la **regla de negocio** (saldo). Si lo haces al revés,
una apuesta de `-5` podría "pasar" la regla del saldo antes de detectarse que es inválida. **Primero lo básico
(null, rangos), después las reglas de negocio.**

### 5.3 Envolver una excepción: la "causa"

A veces atrapas una excepción técnica y la conviertes en una de tu dominio, **sin perder la original**. Para eso
el constructor recibe la causa y se la pasa a `super(mensaje, causa)`:
```java
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}

static BigDecimal parseAmount(String texto) {
    try {
        return new BigDecimal(texto);
    } catch (NumberFormatException e) {
        throw new InvalidAmountException("Monto inválido: '" + texto + "'", e);   // ← e = la causa
    }
}

try {
    parseAmount("10,5");
} catch (InvalidAmountException e) {
    System.out.println(e.getMessage());   // → Monto inválido: '10,5'
    System.out.println(e.getCause());     // → java.lang.NumberFormatException: Character , is neither a decimal digit number, decimal point, nor "e" notation exponential mark.
}
```
📖 Si no la atrapa nadie, el *stack trace* muestra los dos, con `Caused by` (sección 1.2). **Nunca** escribas
`throw new InvalidAmountException("Monto inválido")` sin pasar `e`: perderías la pista del error original.

### 5.4 ¿Checked o unchecked para los errores de negocio?

**Unchecked (`extends RuntimeException`).** Es lo que hace hoy casi todo el código Java profesional, por tres
razones:
1. Con checked, **cada** método entre el que lanza y el que atrapa tendría que escribir `throws
   InsufficientStockException`. Si `reserve` la lanza y la atrapa el controlador, 4 o 5 firmas cambian.
2. La mayoría de las veces el código intermedio **no puede hacer nada** con ella: solo dejarla subir.
3. **Spring:** `@Transactional` hace *rollback* (deshace los cambios en la base de datos) automáticamente solo con
   las **unchecked**. Con una checked, por defecto, **guarda** los cambios aunque haya fallado (semana 20).

Las checked se usan para problemas **externos** que el que llama sí puede resolver (archivo, red): es lo que
hace la propia librería de Java con `IOException`.

### 5.5 Nombres y lugar

- Nombre terminado en **`Exception`**, que diga **qué** pasó: `InsufficientStockException`,
  `ProductNotFoundException`. No `StockError` ni `MyException`.
- Paquete propio: `src/Models/exceptions/` (paquete `Models.exceptions`).
- Una excepción no necesita ser `Serializable`, ni `equals`, ni `toString`. Un constructor con mensaje y los campos
  que sirvan. Nada más (no sobreingenierizar).

### 🧪 Pruébalo · sección 5 (8 min)

1. En el paquete `practical.exceptions`, crea `AgeException extends RuntimeException` con un campo `int age` y un
   constructor que reciba la edad y llame a `super("Edad no permitida: " + age)`.
2. En `ExceptionPlayground`, escribe `static void entrarAlCasino(int age)` que lance `AgeException` si `age < 18`.
3. Llama `entrarAlCasino(16)` dentro de un `try/catch (AgeException e)`. Imprime `e.getMessage()`, `e` y el
   campo `age` con su getter. `// espero:` las tres líneas (¿con qué paquete sale el nombre en `e`?).
4. **Experimento:** quita el `super(...)` del constructor (deja el constructor vacío salvo el campo). Ejecuta.
   `// espero:` ¿qué imprime `e.getMessage()` ahora? Después **vuelve a ponerlo**.

### ✅ ¿Lo entendiste?
1. ¿De qué clase hereda una excepción de negocio y por qué?
2. ¿Qué pasa con `getMessage()` si el constructor no llama a `super(mensaje)`?
3. ¿Por qué en `bet` el `subtract` va al final?

<details><summary>Respuestas</summary>

1. De `RuntimeException` (unchecked): no obliga a poner `throws` en toda la cadena, y Spring hace rollback
   automático con ellas.
2. Devuelve `null`: nadie le dio texto al padre.
3. Para que, si una validación falla, el objeto quede igual que antes (no a medias).
</details>

---

## 6. `try`-with-resources: cerrar lo que abres

🧠 **En una frase:** si abres algo que hay que cerrar (un archivo, una conexión), ábrelo dentro de los paréntesis
de `try ( ... )` y Java lo cierra solo al terminar, falle o no, en orden inverso a como lo abriste.

### 📖 Explicación

Un archivo abierto ocupa un recurso del sistema operativo. Si no lo cierras (porque una excepción saltó antes del
`close()`), queda abierto. En un servidor que corre semanas, eso termina en "too many open files".

**Puente desde PHP:** en PHP haces `$f = fopen(...)` y después `fclose($f)`, normalmente en un `finally`. En Java,
`try`-with-resources escribe ese `finally` por ti.

```java
try (TipoRecurso r = new TipoRecurso()) {   // ① se abre
    // usar r                               // ②
}                                           // ③ aquí Java llama r.close() SIEMPRE
```
Solo funciona con clases que implementan la interfaz **`AutoCloseable`** (un solo método: `void close()`). Los
lectores de archivos de Java ya la implementan.

### 6.1 💻 Ver el orden con un recurso propio

```java
public class Lampara implements AutoCloseable {
    private final String nombre;

    public Lampara(String nombre) {
        this.nombre = nombre;
        System.out.println("enciendo " + nombre);
    }

    public void usar()   { System.out.println("uso " + nombre); }
    public void romper() { throw new IllegalStateException(nombre + " se rompió"); }

    @Override
    public void close() { System.out.println("apago " + nombre); }
}
```
| Código | Salida real |
|--------|-------------|
| `try (Lampara a = new Lampara("cocina")) { a.usar(); }` | `enciendo cocina`, `uso cocina`, `apago cocina` |
| `try (Lampara a = new Lampara("sala"); Lampara b = new Lampara("baño")) { a.usar(); b.usar(); }` | `enciendo sala`, `enciendo baño`, `uso sala`, `uso baño`, **`apago baño`, `apago sala`** |

📖 Con dos recursos (separados por `;`), se cierran **al revés**: el último que se abrió se cierra primero. Como
una pila de platos: el de arriba sale primero. (Tiene sentido: `b` podría depender de `a`.)

**Con una excepción dentro:**
```java
try (Lampara a = new Lampara("garaje")) {
    a.romper();
    System.out.println("no llega");
} catch (IllegalStateException e) {
    System.out.println("catch: " + e.getMessage());
} finally {
    System.out.println("finally");
}
// → enciendo garaje
//   apago garaje                ← ① primero se CIERRA el recurso
//   catch: garaje se rompió     ← ② después el catch
//   finally                     ← ③ y al final el finally
```
📖 **El orden es: cerrar → `catch` → `finally`.** Cuando llegas al `catch`, el recurso ya está cerrado.

### 6.2 💻 Leer un archivo línea por línea

Archivo `twr/precios.txt`:
```
té verde;3.50
café;abc
mate
```
```java
try (BufferedReader reader = Files.newBufferedReader(Path.of("twr/precios.txt"))) {
    String linea;
    int numero = 0;
    while ((linea = reader.readLine()) != null) {     // readLine devuelve null cuando no hay más líneas
        numero++;
        System.out.println(numero + ": " + linea);
    }
} catch (IOException e) {                             // obligatorio: IOException es checked
    System.out.println("error leyendo: " + e);
}
// → 1: té verde;3.50
//   2: café;abc
//   3: mate
```
📖 Paso a paso:
- `Path.of("twr/precios.txt")` = la **ruta**. Es **relativa**: Java la busca a partir de la **carpeta de trabajo**
  (la carpeta desde donde se ejecuta el programa; en IntelliJ, normalmente la raíz del proyecto).
- `Files.newBufferedReader(...)` abre el archivo (en UTF-8, así que las tildes salen bien).
- `while ((linea = reader.readLine()) != null)` se lee así: *"lee una línea, guárdala en `linea`, y si no es
  `null`, entra al bucle"*. Es la forma estándar; cópiala tal cual.

**Separar las columnas** con `split`:
```java
String[] partes = "té verde;3.50".split(";");
System.out.println(partes.length + " " + partes[0] + " " + partes[1]);   // → 2 té verde 3.50
System.out.println("mate".split(";").length);                            // → 1   ← solo una columna
```
Después conviertes cada parte: `new BigDecimal(partes[1])` puede lanzar `NumberFormatException` (`"abc"`), y
`partes[1]` en `"mate"` lanzaría `ArrayIndexOutOfBoundsException`. Por eso conviene **comprobar `partes.length`
antes**.

**Si el archivo no existe:**
```java
try (BufferedReader reader = Files.newBufferedReader(Path.of("no-existe.txt"))) { ... }
catch (IOException e) {
    System.out.println("error leyendo: " + e);
}
// → error leyendo: java.nio.file.NoSuchFileException: no-existe.txt
```
**Truco para saber dónde está buscando Java:**
```java
System.out.println(Path.of("no-existe.txt").toAbsolutePath());
// → /home/.../aprendiendoJava/no-existe.txt     ← la carpeta de trabajo + tu ruta
```
Si ves `NoSuchFileException` y estás seguro de que el archivo existe, imprime `toAbsolutePath()`: casi siempre la
carpeta de trabajo no es la que creías.

> **NO RELEVANTE AHORA:** si falla el cuerpo del `try` **y también** el `close()`, la segunda excepción queda
> "suprimida" dentro de la primera (`e.getSuppressed()`). Lo verás si algún día escribes un `close()` que falla.

### 🧪 Pruébalo · sección 6 (6 min)

1. Copia la clase `Lampara` (como clase aparte en `practical.exceptions`, cambiando el nombre a `Linterna` y los
   textos a `"prendo"` / `"apago"`).
2. Con **tres** linternas en el mismo `try` (`"roja"`, `"verde"`, `"azul"`), usa las tres. `// espero:` las 9
   líneas en orden.
3. Con una linterna `"negra"` que llama a `romper()`, con `catch` y `finally`. `// espero:` el orden de
   `apago`, `catch` y `finally`.

### ✅ ¿Lo entendiste?
1. ¿Qué tiene que implementar una clase para usarse en `try (...)`?
2. Abres `a` y después `b` en el mismo `try`. ¿En qué orden se cierran?
3. Hay una excepción dentro del `try`. ¿Qué se ejecuta primero: el `close()` o el `catch`?

<details><summary>Respuestas</summary>

1. `AutoCloseable` (el método `close()`).
2. Al revés: primero `b`, después `a`.
3. El `close()`. Después el `catch`, y al final el `finally`.
</details>

---

## 7. Fechas con `java.time`

🧠 **En una frase:** usa `LocalDate` para días sin hora (vencimientos, cumpleaños), sus objetos **nunca cambian**
(cada `plus...` devuelve una fecha **nueva** que tienes que guardar), y compáralas con `isBefore` / `isAfter`.

### 📖 Explicación

Java tuvo clases de fechas antiguas (`java.util.Date`, `Calendar`) que tenían muchos problemas: eran
**modificables**, los meses empezaban en **0** (enero = 0), y mezclaban fecha, hora y zona horaria. Desde Java 8
existe **`java.time`**, que es lo que se usa hoy. Si ves `Date` o `Calendar` en código nuevo, es una señal de
alarma.

**Puente desde PHP:** `java.time` se parece a **`DateTimeImmutable`** / **`CarbonImmutable`**, no a `DateTime` /
`Carbon`. En PHP, `$fecha->modify('+1 day')` con `DateTime` **cambia** `$fecha`; con `DateTimeImmutable`
devuelve una nueva. En Java, **todas** son inmutables, siempre.

### 7.1 ¿Qué clase uso?

| Clase | Qué guarda | Ejemplo de `toString()` | Úsala para |
|-------|------------|-------------------------|------------|
| **`LocalDate`** | día, mes, año | `2026-03-10` | vencimientos, cumpleaños, fecha de un pedido. **La más usada** |
| `LocalTime` | hora, minutos | `09:30` | horario de apertura de una tienda |
| `LocalDateTime` | fecha + hora, **sin** zona | `2026-11-16T20:45` | "la reunión es el 16 a las 20:45" (en la zona de cada uno) |
| `ZonedDateTime` | fecha + hora + **zona** | `2026-11-16T20:45-05:00[America/Lima]` | mostrar una hora a un usuario de un país concreto |
| `Instant` | un **instante** exacto en UTC | `2026-11-17T01:45:00Z` | el momento en que pasó algo (logs, `createdAt` en la base de datos) |

📖 "Local" significa **sin zona horaria**: `LocalDate.of(2026, 3, 10)` es "el 10 de marzo" en cualquier país.

💻 La misma hora en tres formas (comprobado):
```java
LocalDateTime ldt = LocalDateTime.of(2026, 11, 16, 20, 45);
ZonedDateTime lima = ZonedDateTime.of(ldt, ZoneId.of("America/Lima"));
System.out.println(lima);                                              // → 2026-11-16T20:45-05:00[America/Lima]
System.out.println(lima.withZoneSameInstant(ZoneId.of("Europe/Rome"))); // → 2026-11-17T02:45+01:00[Europe/Rome]
System.out.println(lima.toInstant());                                  // → 2026-11-17T01:45:00Z
```
En esta lección y en las tareas usarás casi siempre **`LocalDate`**. Las demás, solo para que las reconozcas.

### 7.2 Crear fechas

```java
LocalDate.of(2026, 3, 10)              // → 2026-03-10    ← el mes es 3 = marzo (empieza en 1, no en 0)
LocalDate.of(2026, Month.MARCH, 10)    // → 2026-03-10    ← lo mismo con el enum Month (más legible)
LocalDate.parse("2026-12-24")          // → 2026-12-24    ← desde texto en formato ISO (año-mes-día)
LocalDate.now()                        // → la fecha de hoy (ver sección 7.8: cuidado)
```
Si la fecha no existe, **lanza una excepción** (unchecked, `DateTimeException`):
```java
LocalDate.of(2026, 2, 30)    // → java.time.DateTimeException: Invalid date 'FEBRUARY 30'
LocalDate.of(2026, 13, 1)    // → java.time.DateTimeException: Invalid value for MonthOfYear (valid values 1 - 12): 13
```
Leer partes de una fecha:
```java
LocalDate d = LocalDate.of(2026, 3, 10);
d.getMonth()        // → MARCH      (un enum Month, como los de la lección 05)
d.getMonthValue()   // → 3          (el número)
d.getDayOfWeek()    // → TUESDAY    (otro enum: DayOfWeek)
d.lengthOfMonth()   // → 31
d.isLeapYear()      // → false
```

### 7.3 ⚠️ Inmutables: `plusDays` **no cambia** la fecha, **devuelve** otra

Este es **el** error de las fechas, y es tu punto débil de siempre (sección 0.3):
```java
LocalDate d = LocalDate.of(2026, 3, 10);
d.plusDays(5);                        // ← devuelve 2026-03-15... y nadie lo guarda
System.out.println(d);                // → 2026-03-10   ← ¡no cambió!

LocalDate e = d.plusDays(5);          // ← guardar lo que devuelve
System.out.println(d + " " + e);      // → 2026-03-10 2026-03-15

d = d.plusDays(5);                    // ← o reasignar la misma variable
System.out.println(d);                // → 2026-03-15
```
📖 `plusDays` es como `BigDecimal.add`: **devuelve** el resultado y no toca el original. Ya lo sabes con
`total = total.add(precio)`. Con las fechas es igual: `d = d.plusDays(5)`.

¿Por qué es bueno que sean inmutables? Porque puedes pasar una fecha a cualquier método sin miedo a que te la
cambie (lección 05, sección 2). Con `DateTime` de PHP, pasar `$fecha` a una función que hace `->modify()` te
cambia la tuya.

Todos los métodos que "modifican" devuelven una nueva: `plusDays`, `plusWeeks`, `plusMonths`, `plusYears`,
`minusDays`… y `withDayOfMonth(1)` (el primer día del mes: `2026-03-10` → `2026-03-01`).

### 7.4 ⚠️ Sumar meses: el fin de mes y los bisiestos

Un mes no tiene siempre los mismos días. ¿Qué es "31 de agosto + 1 mes"? No existe el 31 de septiembre. Java
elige **el último día válido** del mes:
```java
LocalDate.of(2026, 8, 31).plusMonths(1)                  // → 2026-09-30   ← septiembre tiene 30
LocalDate.of(2026, 8, 31).plusMonths(1).plusMonths(1)    // → 2026-10-30   ← ¡no 31! (sale del 30)
LocalDate.of(2026, 8, 31).plusMonths(2)                  // → 2026-10-31   ← sumado de una vez, sí 31
LocalDate.of(2024, 2, 29).plusYears(1)                   // → 2025-02-28   ← 2025 no es bisiesto
```
📖 Dos ideas:
1. **Ajusta al último día del mes** cuando el día no existe.
2. **`plusMonths(1).plusMonths(1)` no es lo mismo que `plusMonths(2)`**: después del primer ajuste, el día "31"
   ya se perdió. Esto importa en las suscripciones que se renuevan mes a mes (Tarea 6).

**Diferencia con PHP (comprobado en PHP):** `(new DateTime("2027-01-31"))->modify("+1 month")` da
**`2027-03-03`**: PHP "se pasa" al mes siguiente. Java da `2027-02-28`. Si traduces lógica de PHP a Java, los
resultados **cambian**.

### 7.5 Comparar fechas

```java
LocalDate x = LocalDate.of(2026, 5, 1);
LocalDate y = LocalDate.of(2026, 7, 15);
x.isBefore(y)                               // → true
x.isAfter(y)                                // → false
x.isEqual(LocalDate.of(2026, 5, 1))         // → true
x.equals(LocalDate.parse("2026-05-01"))     // → true
LocalDate.of(2026, 3, 10) == LocalDate.of(2026, 3, 10)   // → false   ← ¡objetos distintos! (lección 02)
LocalDate.of(2026, 3, 10).compareTo(LocalDate.of(2026, 3, 12))   // → -2   ← solo importa el signo (lección 04)
```
📖 Reglas:
- **Nunca `==`** con fechas: compara si es el **mismo objeto**, no la misma fecha. Usa `equals` o `isEqual`.
- Para "antes/después", **`isBefore` / `isAfter`** se leen mucho mejor que `compareTo(...) < 0`.
- ⚠️ `isAfter` es **estricto**: el mismo día **no** es "después". `hoy.isAfter(hoy)` → `false`. Piensa siempre
  en el día límite: ¿una suscripción que vence el 30 está activa **el** 30? (Tarea 6.)

`LocalDate` implementa `Comparable`, así que una `List<LocalDate>` se ordena con `sort(null)` (lección 04).

### 7.6 La distancia entre dos fechas: `ChronoUnit`, `Period`, `Duration`

```java
LocalDate x = LocalDate.of(2026, 5, 1), y = LocalDate.of(2026, 7, 15);

ChronoUnit.DAYS.between(x, y)      // → 75      ← días totales
ChronoUnit.DAYS.between(y, x)      // → -75     ← al revés, negativo
ChronoUnit.MONTHS.between(x, y)    // → 2       ← meses completos

Period p = Period.between(x, y);
System.out.println(p);             // → P2M14D  ← "2 meses y 14 días"
p.getMonths()                      // → 2
p.getDays()                        // → 14      ← ¡no 75! solo los días que sobran después de los meses
```
| Herramienta | Qué da | Para |
|-------------|--------|------|
| `ChronoUnit.DAYS.between(a, b)` | un `long`: el **total** en una unidad | "faltan 75 días" |
| `Period.between(a, b)` | años + meses + días, por separado | la edad: `Period.between(nacimiento, hoy).getYears()` |
| `Duration.between(a, b)` | horas, minutos, segundos (con `LocalTime`/`LocalDateTime`) | "el turno duró 8 h 45 min" |

⚠️ La trampa del `Period`: `p.getDays()` **no** es el total de días. Para el total, `ChronoUnit.DAYS.between`.

💻 `Duration`:
```java
Duration du = Duration.between(LocalTime.of(9, 30), LocalTime.of(18, 15));
System.out.println(du);              // → PT8H45M  ("Period of Time: 8 Hours 45 Minutes")
du.toMinutes()                       // → 525
du.toHours()                         // → 8
```

### 7.7 Formatear y leer fechas con otro formato: `DateTimeFormatter`

`toString()` y `parse()` usan el formato ISO (`2026-12-24`). Para mostrar `24/12/2026`:
```java
DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");

LocalDate.of(2026, 3, 5).format(f)      // → 05/03/2026        (fecha → texto)
LocalDate.parse("24/12/2026", f)        // → 2026-12-24        (texto → fecha)
```
| Letra | Significa | Ejemplo para el 5 de marzo de 2026 |
|-------|-----------|-----------------------------------|
| `dd` / `d` | día, con / sin cero | `05` / `5` |
| `MM` / `M` | **mes** (mayúscula), con / sin cero | `03` / `3` |
| `yyyy` | año | `2026` |
| `HH:mm` | hora (0–23) : **minutos** (minúscula) | `20:45` |

⚠️ **`MM` = mes, `mm` = minutos.** Si escribes `"dd/mm/yyyy"` para una `LocalDate`:
```java
LocalDate.of(2026, 3, 5).format(DateTimeFormatter.ofPattern("dd/mm/yyyy"))
// → java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: MinuteOfHour
```
(Una `LocalDate` no tiene minutos.) **Ojo, viniendo de PHP:** en `date('d/m/Y')` la `m` minúscula **es** el
mes. En Java es al revés.

**Si el texto no coincide con el formato** → `DateTimeParseException` (unchecked):
```java
LocalDate.parse("24/12/2026")      // sin formatter espera ISO
// → java.time.format.DateTimeParseException: Text '24/12/2026' could not be parsed at index 0
LocalDate.parse("2026-12-24", f)   // con formatter dd/MM/yyyy
// → java.time.format.DateTimeParseException: Text '2026-12-24' could not be parsed at index 2
LocalDate.parse("5/3/2026", f)     // dd exige dos dígitos
// → java.time.format.DateTimeParseException: Text '5/3/2026' could not be parsed at index 0
```
📖 "at index N" = en qué **posición** del texto (empezando en 0) dejó de coincidir. En `2026-12-24` con `dd/MM`, el
índice 2 es el `2` de `2026`, donde esperaba `/`.

Un caso raro (comprobado): con `ofPattern`, un día que no existe en ese mes **se ajusta** al último día válido:
`LocalDate.parse("31/02/2026", f)` → `2026-02-28`. Pero un día imposible falla: `"32/02/2026"` →
`DateTimeParseException`. Si necesitas rechazar el 31 de febrero, valida aparte (NO RELEVANTE AHORA).

### 7.8 ⚠️ `LocalDate.now()` dentro de tus clases: no

```java
public boolean isExpired() {
    return LocalDate.now().isAfter(endDate);     // ← funciona... pero ¿cómo lo pruebas?
}
```
El resultado depende **del día en que lo ejecutes**. Hoy da `false`, en tres meses da `true`. No puedes escribir
un test que diga "el 1 de marzo de 2027, esto está vencido" (los tests llegan en la semana 11).

**La solución:** que el método **reciba** la fecha de hoy como parámetro:
```java
public boolean isExpiredOn(LocalDate today) {
    return today.isAfter(endDate);
}
// en main:       sub.isExpiredOn(LocalDate.now())               ← en producción
// en una prueba: sub.isExpiredOn(LocalDate.of(2027, 3, 1))      ← siempre da lo mismo
```
📖 `LocalDate.now()` se llama **una vez, afuera** (en `main` o en el controlador), y se pasa hacia adentro. Así tu
clase de dominio no depende del reloj. (Profesionalmente se usa un `Clock` inyectado; es la misma idea.)

### 🧪 Pruébalo · sección 7 (8 min)

En `ExceptionPlayground` (agrega `import java.time.*;`, `import java.time.format.*;` y
`import java.time.temporal.ChronoUnit;`):
1. `LocalDate cumple = LocalDate.of(2026, 10, 31);` Llama `cumple.plusMonths(1);` **sin** guardar e imprime
   `cumple`. `// espero:` Después guárdalo en otra variable e imprime las dos. `// espero:`
2. Imprime `LocalDate.of(2026, 10, 31).plusMonths(4)`. `// espero:` (¿febrero de 2027 es bisiesto?)
3. Imprime `ChronoUnit.DAYS.between(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 25))` y `Period.between`
   de las mismas fechas. `// espero:`
4. Formatea `LocalDate.of(2026, 7, 9)` con `"dd/MM/yyyy"` y con `"d/M/yyyy"`. `// espero:`
5. Parsea `"09-07-2026"` con el formatter `"dd/MM/yyyy"` dentro de un `try/catch (DateTimeParseException e)` e
   imprime `e.getMessage()`. `// espero:` ¿en qué índice falla?

### ✅ ¿Lo entendiste?
1. ¿Qué imprime `LocalDate d = LocalDate.of(2026, 1, 1); d.plusDays(10); System.out.println(d);`?
2. ¿Cuánto es `LocalDate.of(2027, 1, 31).plusMonths(1)`?
3. ¿Por qué no se escribe `LocalDate.now()` dentro de un método de dominio?
4. `Period.between(1 de mayo, 15 de julio).getDays()` da 14. ¿Por qué no 75?

<details><summary>Respuestas</summary>

1. `2026-01-01`: `plusDays` devuelve una fecha nueva y no se guardó.
2. `2027-02-28`: no existe el 31 de febrero, ajusta al último día del mes.
3. Porque el resultado dependería del día en que se ejecuta y no se podría probar. Se recibe `today` como parámetro.
4. Porque `Period` separa en meses + días (2 meses y 14 días). El total de días es `ChronoUnit.DAYS.between`.
</details>

---

## 8. Resumen

### ¿Qué uso?

| Quiero… | Uso |
|---------|-----|
| rechazar un dato inválido | `throw new IllegalArgumentException("mensaje claro")` |
| rechazar una operación por el estado del objeto | `throw new IllegalStateException("...")` |
| un error de negocio con nombre propio | `class XxxException extends RuntimeException` con `super(mensaje)` |
| convertir una excepción técnica en una del dominio | `throw new MiException("mensaje", e)` (con la causa) |
| hacer algo pase lo que pase | `finally` |
| abrir y cerrar un archivo/recurso | `try (Recurso r = ...) { }` |
| el mismo `catch` para dos tipos | `catch (TipoA \| TipoB e)` (no padre e hijo) |
| depurar una excepción | `System.out.println(e)` (no `getMessage()`, que puede ser `null`) |
| "no hay resultado" normal | `Optional` (lección 05), no una excepción |
| una fecha sin hora | `LocalDate.of(...)` / `LocalDate.parse("2026-12-24")` |
| sumar días/meses | `d = d.plusDays(n)` (¡guardar el resultado!) |
| antes / después | `isBefore` / `isAfter` (el mismo día no es "after") |
| días entre dos fechas | `ChronoUnit.DAYS.between(a, b)` |
| la edad | `Period.between(nacimiento, hoy).getYears()` |
| mostrar `24/12/2026` | `DateTimeFormatter.ofPattern("dd/MM/yyyy")` |
| "hoy" en una clase de dominio | recibir `LocalDate today` como parámetro |

### Errores comunes

| Error | Qué pasa | Solución |
|-------|----------|----------|
| `catch (Exception e) { return false; }` | se pierde el motivo y se esconden bugs | lanzar una excepción concreta con mensaje |
| `catch` vacío | el error desaparece sin rastro | atrapar solo si sabes qué hacer; si no, dejarla subir |
| `catch` del padre antes que el del hijo | no compila: "has already been caught" | de lo más específico a lo más general |
| `NumberFormatException \| IllegalArgumentException` | no compila: son padre e hijo | `catch (IllegalArgumentException e)` |
| imprimir `e.getMessage()` y ver `null` | la excepción no tenía texto | imprimir `e`; en tus excepciones, `super(mensaje)` |
| `throw new MiException("...")` dentro de un `catch`, sin `e` | se pierde el error original | pasar la causa: `new MiException("...", e)` |
| modificar el objeto y **después** validar | queda a medias si falla | validar todo primero, modificar al final |
| excepción de negocio `extends Exception` | `throws` por todas partes; sin rollback en Spring | `extends RuntimeException` |
| olvidar cerrar un archivo | recursos abiertos | `try`-with-resources |
| `NoSuchFileException` con el archivo ahí | la carpeta de trabajo no es la que crees | imprimir `Path.of(...).toAbsolutePath()` |
| `d.plusDays(1);` sin asignar | la fecha no cambia | `d = d.plusDays(1);` |
| `fecha1 == fecha2` | compara objetos, no fechas | `equals` / `isEqual` |
| `"dd/mm/yyyy"` | `mm` son minutos → excepción | `"dd/MM/yyyy"` |
| `Period.getDays()` como total de días | da solo los días sobrantes | `ChronoUnit.DAYS.between` |
| `LocalDate.now()` dentro del dominio | no se puede probar | parámetro `LocalDate today` |

### Relación con Spring Boot (solo para que sepas que existe)

- **Semana 17:** una clase con `@RestControllerAdvice` atrapa **en un solo lugar** las excepciones que suben desde
  los servicios y las convierte en respuestas HTTP: `InsufficientStockException` → `409 Conflict`,
  `ProductNotFoundException` → `404 Not Found`. Por eso tus servicios **no** atrapan: lanzan y dejan subir. Es el
  `Handler` de Laravel.
- La respuesta de error estándar se llama `ProblemDetail` (`{"title": ..., "status": 409, "detail": "Stock
  insuficiente de 'Mouse'..."}`): el `detail` sale de **tu** `getMessage()`. Por eso los mensajes claros importan.
- **Semana 20:** `@Transactional` deshace los cambios en la base de datos si sale una **unchecked**. Otra razón para
  `extends RuntimeException`.
- **Fechas:** JPA guarda `LocalDate` como `DATE` y `LocalDateTime`/`Instant` como `TIMESTAMP` sin que hagas nada
  (semana 18). En JSON, Spring las escribe en ISO: `"2027-02-28"`.

### Glosario

| Palabra | Significado |
|---------|-------------|
| **excepción** | objeto que representa un error y "sube" por las llamadas hasta que alguien lo atrapa |
| **lanzar** (`throw`) | crear la excepción y cortar el método ahí |
| **atrapar** (`catch`) | detener la excepción y decidir qué hacer |
| **propagar / dejar subir** | no atraparla: pasa al método que llamó |
| ***stack trace*** | el camino de llamadas que imprime una excepción no atrapada |
| **checked** | hija de `Exception` pero no de `RuntimeException`: el compilador obliga a atraparla o declararla |
| **unchecked** | hija de `RuntimeException`: el compilador no obliga |
| **causa** (`getCause()`) | la excepción original que otra envuelve ("Caused by") |
| **recurso** | algo que se abre y hay que cerrar (archivo, conexión): `AutoCloseable` |
| **inmutable** | que no cambia después de creado: los métodos devuelven un objeto nuevo |
| **ISO** | el formato estándar `2026-12-24` (año-mes-día) |
| **carpeta de trabajo** | la carpeta desde la que Java resuelve las rutas relativas |

---

## 9. Autoevaluación final (sin mirar)

Respóndelas en un comentario al final de `ExceptionPlayground.java`. **Cada pregunta tiene dos partes: responde
las dos.** Si **8 de 10** te salen solas, pasa a las tareas. Si no, dime cuáles fallaste.

1. Una excepción ocurre en `leerCantidad`, llamado por `procesar`, llamado por `main`, y nadie la atrapa. ¿Qué
   métodos terminan? ¿Qué línea del *stack trace* miras primero?
2. Diferencia entre checked y unchecked: ¿quién es el padre de cada una? ¿qué te obliga a hacer el compilador?
3. ¿Qué imprime un `try` que hace `return "x"` y tiene un `finally` que imprime `"F"`? ¿En qué orden?
4. ¿Por qué no compila `catch (RuntimeException e)` antes de `catch (NumberFormatException e)`? ¿Qué regla de
   la lección 04 es la misma?
5. ¿Qué tiene de malo `catch (Exception e) { return false; }`? ¿Qué harías en su lugar?
6. Escribe (de memoria) el esqueleto de una excepción propia con un mensaje: ¿de qué clase hereda? ¿qué llamas en
   el constructor para que `getMessage()` no sea `null`?
7. ¿Para qué sirve el segundo parámetro de `super(message, cause)`? ¿Qué aparece en el *stack trace*?
8. En un `try`-with-resources con dos recursos y una excepción en el cuerpo: ¿en qué orden se cierran? ¿Se
   cierran antes o después del `catch`?
9. `LocalDate d = LocalDate.of(2027, 1, 31); d.plusMonths(1);` ¿Cuánto vale `d`? ¿Y cuánto valdría si guardaras
   el resultado?
10. ¿Por qué `Renewal` debería recibir `LocalDate today` en vez de llamar a `LocalDate.now()`? ¿Qué problema
    evita?

---

## 10. Tareas

### Reglas para todas las tareas

1. **Una tarea por vez, en orden.** Al terminar cada una, pídeme "revisa tarea N".
2. **Plantilla obligatoria** en cada `println`:
   ```java
   System.out.println("---- T1 · Paso 3 ----");   // separador antes de cada paso (paso, no tarea)
   System.out.println(algo);
   // espero: ...                                 // ANTES de ejecutar
   // real: ...    porque: ...                    // SOLO si fallaste
   ```
   Cuando la pregunta es el **orden** de varias líneas, escribe el `espero` como lista: `// espero: 1, 2, 5, 6`.
3. **Compara letra por letra** el `espero` con la salida. **Solo pon ✅ si coinciden exactamente** (`25.00` no es
   `25`, `2027-02-28` no es `2027-02-29`). Un ✅ equivocado cuenta más que un fallo honesto.
4. **Usa los datos exactos** del enunciado (nombres, mayúsculas, ids, cantidades, fechas). Están elegidos para que
   aparezcan los casos raros: si los cambias, el experimento no prueba nada.
5. **Las preguntas** (P1.1, P1.2…) van en un comentario al final del archivo, **completando la frase**. Cada
   `___` es una parte de la respuesta: **llena todos los huecos**.
6. **Si un paso no se entiende, no lo adivines.** Pregúntame "no entiendo T3 paso 4".
7. **Checklist** al final de cada tarea: revísala antes de pedir la revisión.
8. **Sin lambdas (`->`), sin `::`, sin streams.** La única flecha permitida es la del `switch` moderno (lección
   05, sección 5). Si un paso "no compila" a propósito, **comenta la línea** después de copiar el error, para que
   el archivo compile.

**Tiempo total aproximado:** 3 h 40 min.

| Tarea | Tema | Lee antes | Archivo(s) | Tiempo |
|-------|------|-----------|------------|--------|
| 1 | Leer *stack traces* y checked vs unchecked | Secciones 1 y 2 | `ExceptionBasics` | 20 min |
| 2 | El flujo de `try`/`catch`/`finally` | Sección 3 | `TryFlowLab` | 30 min |
| 3 | Excepciones de dominio: `Inventory` | Secciones 4 y 5 | `Models/exceptions/*`, `Models/Inventory`, `InventoryLab` | 45 min |
| 4 | `try`-with-resources + importar un CSV | Sección 6 | `TrackedResource`, `CsvImportLab`, `data/products.csv` | 45 min |
| 5 | Experimentos con `java.time` | Sección 7 | `DateBasics` | 30 min |
| 6 | **Entregable:** `Renewal` con `LocalDate` real | todo | `objects/Renewal`, `Models/exceptions/RenewalNotAllowedException`, `Renewable`, `RenewableProduct`, `RenewalLab` | 50 min |

Paquetes nuevos: `practical.exceptions` (`src/practical/exceptions/`), `practical.time` (`src/practical/time/`) y
`Models.exceptions` (`src/Models/exceptions/`).

---

### Tarea 1 — Leer *stack traces* y checked vs unchecked (fácil · 20 min)

**Objetivo:** leer un *stack trace* sin miedo, saber qué compila y qué no, y no volver a imprimir `null` por usar
`getMessage()`.
**Antes:** relee las secciones 0.2, 1 y 2.
**Archivo:** `src/practical/exceptions/ExceptionBasics.java` (con `main`).

**Parte A — leer un *stack trace***

1. Escribe este código **tal cual** (es el experimento, no la tarea):
   ```java
   public static void main(String[] args) {
       System.out.println("inicio");
       BigDecimal total = calcularTotal("12.50", "dos");
       System.out.println("total: " + total);
   }

   static BigDecimal calcularTotal(String precio, String cantidad) {
       BigDecimal p = new BigDecimal(precio);
       int c = leerCantidad(cantidad);
       return p.multiply(BigDecimal.valueOf(c));
   }

   static int leerCantidad(String texto) {
       return Integer.parseInt(texto);
   }
   ```
   **Antes de ejecutar**, escribe: `// espero: ¿se imprime "inicio"? ¿se imprime "total"? ¿qué excepción?`
2. Ejecuta y llena esta tabla en un comentario, mirando el *stack trace*:

   | Pregunta | Respuesta |
   |----------|-----------|
   | tipo de excepción | |
   | mensaje | |
   | método **y línea** de tu código donde explotó | |
   | qué método lo llamó, y en qué línea | |
   | y a ese, quién lo llamó, y en qué línea | |
   | ¿se imprimió `total: ...`? | |

3. Cambia la llamada a `calcularTotal("12,50", "2")`. `// espero:` ¿explota en el **mismo** método que antes? ¿qué
   mensaje? Ejecuta y escribe en qué método y línea explotó ahora.
4. Cambia a `calcularTotal("12.50", "2")`. `// espero:` la línea exacta que imprime.
5. Vuelve a `"dos"`, pero ahora envuelve la llamada de `main` en un `try/catch (NumberFormatException e)` que
   imprima `"dato inválido: " + e.getMessage()`. `// espero:` las líneas que salen, en orden. ¿Sale `total`?

**Parte B — ¿compila?**

6. Llena la columna `espero` **antes** de escribir el código. Después escribe cada caso (en métodos `static`
   aparte: `casoA()`, `casoB()`…), mira si IntelliJ lo marca en rojo y llena `real`. Si no compila, copia el error
   y **comenta** la línea.

   | Caso | Código | ¿compila? espero | real | error (si no compila) |
   |------|--------|------------------|------|-----------------------|
   | A | `throw new IllegalStateException("cerrado");` | | | |
   | B | `throw new Exception("cerrado");` | | | |
   | C | un método `static void cargar() throws Exception { ... }` llamado desde `main` sin `try` | | | |
   | D | `Files.readString(Path.of("data/nada.txt"));` | | | |
   | E | `Integer.parseInt("abc");` | | | |
   | F | `Thread.sleep(100);` | | | |
   | G | `try { System.out.println("hola"); } catch (IOException e) { }` | | | |

   > En el caso C, fíjate **en qué línea** marca el error: ¿en `cargar` o en `main`?

**Parte C — `getMessage()` vs `e`**

7. Dentro de un `try/catch (UnsupportedOperationException e)`, haz `List.of("S", "M").add("L");` e imprime, en
   tres líneas: `e.getMessage()`, `e` y `e.getClass().getSimpleName()`. `// espero:` las tres.
8. Igual con `new ArrayList<>(List.of("S", "M")).get(5)` y `catch (IndexOutOfBoundsException e)`: imprime
   `e.getMessage()` y `e`. `// espero:` las dos.

**Preguntas:**
- **P1.1** *"En un *stack trace*, la primera línea dice ___ y ___. Después busco la primera línea `at` que tenga
  ___, porque ahí ___."*
- **P1.2** *"Una excepción es checked si hereda de ___ pero no de ___. El compilador me obliga a ___ o a ___."*
- **P1.3** *"En el paso 7, `getMessage()` imprimió ___ porque ___. Para depurar es mejor imprimir ___."*
- **P1.4** *"El caso E compila aunque falla al ejecutar porque `NumberFormatException` es ___."*

**Checklist:**
- [ ] 8 pasos con separador `---- T1 · Paso N ----` y `espero`
- [ ] tabla del paso 2 completa (con números de línea reales de **tu** archivo)
- [ ] tabla del paso 6 con `espero` llenado **antes**, y los casos que no compilan comentados con su error
- [ ] ✅ solo donde coincide letra por letra
- [ ] P1.1–P1.4, todos los huecos

---

### Tarea 2 — El flujo de `try` / `catch` / `finally` (medio · 30 min)

**Objetivo:** predecir **en qué orden** se ejecutan las líneas cuando hay (y cuando no hay) excepciones.
**Antes:** relee la sección 3 completa.
**Archivo:** `src/practical/exceptions/TryFlowLab.java` (con `main`).

En esta tarea el código te lo doy: **lo que se evalúa son tus predicciones**. Copia cada método tal cual.

**Paso 1 — el flujo básico**
```java
static void flujo(String texto) {
    System.out.println("1");
    try {
        System.out.println("2");
        int n = Integer.parseInt(texto);
        System.out.println("3 n=" + n);
    } catch (NumberFormatException e) {
        System.out.println("4 " + e.getMessage());
    } finally {
        System.out.println("5");
    }
    System.out.println("6");
}
```
En `main`: `flujo("8");`, una línea `"--"`, y `flujo("ocho");`. `// espero:` las dos listas.

**Paso 2 — `finally` con `return`**
```java
static String estado(int stock) {
    try {
        if (stock == 0) {
            throw new IllegalStateException("agotado");
        }
        return "hay " + stock;
    } catch (IllegalStateException e) {
        return "catch: " + e.getMessage();
    } finally {
        System.out.println("finally " + stock);
    }
}
```
En `main`: `System.out.println(estado(4));` y `System.out.println(estado(0));`. `// espero:` las **4** líneas en
orden. Piensa bien: ¿qué sale primero, `finally 4` o `hay 4`? (sección 3.2)

**Paso 3 — un `catch` del tipo equivocado**
```java
static void tipoEquivocado() {
    try {
        System.out.println("antes");
        Integer.parseInt("diez");
        System.out.println("después");
    } catch (ArithmeticException e) {
        System.out.println("catch aritmética");
    } finally {
        System.out.println("finally de tipoEquivocado");
    }
    System.out.println("fin de tipoEquivocado");
}
```
En `main`, llámalo dentro de `try { tipoEquivocado(); } catch (NumberFormatException e) {
System.out.println("main: " + e.getMessage()); }`. `// espero:` las líneas en orden. ¿Sale `fin de
tipoEquivocado`?

**Paso 4 — multi-catch**
```java
static void convertir(Object dato) {
    try {
        String s = (String) dato;
        System.out.println(Integer.parseInt(s) * 2);
    } catch (ClassCastException | NumberFormatException e) {
        System.out.println("no convertible: " + e.getClass().getSimpleName());
    }
}
```
En `main`: `convertir("21");`, `convertir("veintiuno");`, `convertir(21);` (el último es un `Integer`, sin
comillas). `// espero:` las tres líneas.

**Paso 5 — el multi-catch que no compila**
Agrega `| IllegalArgumentException` al `catch` del paso 4. Copia el error en un comentario y explica **con tus
palabras** por qué. Después **quítalo**.

**Paso 6 — el orden de varios `catch`**
```java
static void orden(String caso) {
    try {
        if (caso.equals("A")) Integer.parseInt("x");
        if (caso.equals("B")) throw new IllegalArgumentException("y");
        if (caso.equals("C")) throw new IllegalStateException("z");
        System.out.println("sin error");
    } catch (NumberFormatException e) {
        System.out.println("NFE");
    } catch (IllegalArgumentException e) {
        System.out.println("IAE");
    } catch (RuntimeException e) {
        System.out.println("Runtime: " + e.getMessage());
    }
}
```
1. Llena la tabla **antes** de ejecutar:

   | Llamada | espero | real |
   |---------|--------|------|
   | `orden("A")` | | |
   | `orden("B")` | | |
   | `orden("C")` | | |
   | `orden("D")` | | |

2. Ahora **intercambia** los dos primeros `catch` (que `IllegalArgumentException` quede arriba de
   `NumberFormatException`). Copia el error y **vuelve a dejarlo como estaba**.

**Preguntas:**
- **P2.1** *"Si una línea del `try` lanza una excepción, las líneas siguientes del `try` ___ y el programa salta
  ___."*
- **P2.2** *"En el paso 2, `finally 4` sale antes que `hay 4` porque ___. El valor `"hay 4"` lo imprime ___."*
- **P2.3** *"En el paso 3, el `finally` ___ se ejecutó, pero `fin de tipoEquivocado` ___ porque la excepción ___."*
- **P2.4** *"Un `catch` de la clase padre debe ir ___ del de la clase hija, porque si no ___. Es la misma regla que
  el orden de los `instanceof` de la lección 04: de lo más ___ a lo más ___."*

**Checklist:**
- [ ] todos los `espero` escritos **antes** de ejecutar, como listas en orden
- [ ] pasos 5 y 6.2: error copiado y código devuelto a como estaba (el archivo compila)
- [ ] tabla del paso 6 completa
- [ ] P2.1–P2.4, todos los huecos

---

### Tarea 3 — Excepciones de dominio: `Inventory` (medio · 45 min)

**Objetivo:** crear tus propias excepciones de negocio, lanzarlas desde una clase de dominio, y comprobar que una
operación que falla **no deja el objeto a medias**.
**Antes:** relee las secciones 4 y 5 completas, y la receta de acumular de la lección 03.
**Archivos:**
- `src/Models/exceptions/InsufficientStockException.java` (nuevo)
- `src/Models/exceptions/ProductNotFoundException.java` (nuevo)
- `src/Models/Inventory.java` (nuevo)
- `src/practical/exceptions/InventoryLab.java` (nuevo, con `main`)

**Requisitos de las excepciones** (las dos `extends RuntimeException`):

| Clase | Constructor | Campos (con getter) | Mensaje exacto (`getMessage()`) |
|-------|-------------|---------------------|---------------------------------|
| `InsufficientStockException` | `(String productName, int requested, int available)` | los tres | `Stock insuficiente de 'Mouse': pediste 2, hay 1` |
| `ProductNotFoundException` | `(String productName)` | ninguno | `Producto no registrado en el inventario: Webcam` |

**Requisitos de `Inventory`:**

| Método | Qué hace |
|--------|----------|
| campo `private final Map<Product, Integer> stock` | `LinkedHashMap` (orden de llegada) |
| `void addStock(Product product, int quantity)` | `product` `null` → `IllegalArgumentException("El producto no puede ser null")`; `quantity <= 0` → `IllegalArgumentException("La cantidad debe ser mayor que 0: " + quantity)`; si no, **suma** a lo que ya había (receta de acumular) |
| `int stockOf(Product product)` | la cantidad; si el producto no está registrado → `ProductNotFoundException` |
| `void reserve(Product product, int quantity)` | resta `quantity`. Valida **en este orden**: ① `quantity <= 0` → `IllegalArgumentException` (mismo mensaje); ② no registrado → `ProductNotFoundException`; ③ `quantity` mayor que lo disponible → `InsufficientStockException`. Solo si todo pasa, resta |
| `String toString()` | `Mouse=1 Teclado=10` (nombre=cantidad, separados por un espacio) |

**Esqueleto** (solo firmas, el cuerpo lo escribes tú):
```java
public class Inventory {
    private final Map<Product, Integer> stock = new LinkedHashMap<>();

    public void addStock(Product product, int quantity) { ... }
    public int stockOf(Product product) { ... }
    public void reserve(Product product, int quantity) { ... }
    @Override
    public String toString() { ... }
}
```
> Pista para `reserve`: puedes reutilizar `stockOf` para el paso ②: si el producto no está, `stockOf` ya lanza la
> excepción por ti.

**Datos** (en `InventoryLab`):

| Variable | Clase | id | Nombre | Precio | Peso |
|----------|-------|----|--------|--------|------|
| `mouse` | `PhysicalProduct` | 31 | `"Mouse"` | `49.00` | `0.2` |
| `teclado` | `PhysicalProduct` | 32 | `"Teclado"` | `79.90` | `0.8` |
| `webcam` | `PhysicalProduct` | 33 | `"Webcam"` | `120.00` | `0.3` |

> `webcam` **nunca** se agrega al inventario, a propósito.

**Pasos:**
1. Escribe las dos excepciones y `Inventory`.
2. En `InventoryLab`: `addStock(mouse, 3)`, `addStock(teclado, 10)`, `addStock(mouse, 2)`. Imprime
   `stockOf(mouse)` y `stockOf(teclado)`. `// espero:` (¿el segundo `addStock` de mouse **suma** o **reemplaza**?)
3. `reserve(mouse, 4)` e imprime `stockOf(mouse)`. `// espero:`
4. Dentro de un `try/catch (InsufficientStockException e)`: `reserve(mouse, 2)` y después
   `System.out.println("reservado")`. En el `catch` imprime `e.getMessage()` y, en otra línea,
   `e.getProductName() + " faltan " + (e.getRequested() - e.getAvailable())`. `// espero:` ¿se imprime
   `reservado`? ¿qué dos líneas salen?
5. **FOTO DESPUÉS DEL FALLO:** imprime `stockOf(mouse)`. `// espero:` debe ser **el mismo** que en el paso 3. Si
   no, tu `reserve` resta **antes** de validar.
6. Cada uno en su `try/catch`, imprimiendo `e` (no `getMessage()`):
   - `reserve(webcam, 1)` → `catch (ProductNotFoundException e)` → `// espero:`
   - `reserve(teclado, 0)` → `catch (IllegalArgumentException e)` → `// espero:`
   - `addStock(null, 5)` → `catch (IllegalArgumentException e)` → `// espero:`
   - `reserve(webcam, -1)` → `catch (RuntimeException e)`, imprime `e.getClass().getSimpleName()` →
     `// espero:` ¿cuál de los dos problemas se detecta primero? (mira el orden de validación)
7. Imprime el inventario (`toString`). `// espero:`
8. **Un pedido que falla a la mitad:** dentro de **un solo** `try/catch (InsufficientStockException e)`:
   `reserve(teclado, 3)`, `reserve(mouse, 5)`, `reserve(teclado, 2)`. En el `catch` imprime `"pedido falló: " +
   e.getMessage()`. Después imprime el inventario. `// espero:` ¿cuánto teclado queda? ¿se reservaron los 3
   primeros aunque el pedido falló?
9. Al final del `main`, **sin** `try`: `reserve(webcam, 1)`. Ejecuta y copia en un comentario la primera línea y
   las líneas `at` de **tu** código del *stack trace*.
10. **(Opcional, 5 min)** En tu `Order`, quita los `try/catch` de `addItem` y `removeItem` que protegen líneas
    que no pueden fallar (sección 4.3, "EN MI CÓDIGO"). **No cambies las firmas.** Ejecuta tu `Main` y comprueba
    que sigue funcionando igual.

**Preguntas:**
- **P3.1** *"Mis excepciones heredan de ___ porque ___. Si heredaran de `Exception`, tendría que escribir ___ en
  ___."*
- **P3.2** *"Después del fallo del paso 4, el stock de mouse ___ porque en `reserve` la resta está ___ de las
  validaciones."*
- **P3.3** *"En el paso 6, `reserve(webcam, -1)` lanzó ___ y no ___, porque ___."*
- **P3.4** *"En el paso 8 quedaron ___ teclados. El problema es que ___. Para evitarlo, `reserve` de un pedido
  completo debería ___ antes de ___."* (Esto lo resuelve `@Transactional` en Spring, semana 20.)
- **P3.5** *"`stockOf` de un producto no registrado lanza una excepción en vez de devolver `0` o un `Optional`
  porque ___."* (No hay una sola respuesta correcta: justifica con la sección 4.4.)

**Checklist:**
- [ ] mensajes **exactos** de la tabla (compara letra por letra con tu salida)
- [ ] `addStock` acumula (paso 2)
- [ ] foto después del fallo = paso 3 (paso 5)
- [ ] en el paso 6 imprimes `e`, y se ve el paquete `Models.exceptions` en tus excepciones
- [ ] *stack trace* del paso 9 copiado
- [ ] P3.1–P3.5, todos los huecos

---

### Tarea 4 — `try`-with-resources + importar un CSV (medio-avanzado · 45 min)

**Objetivo:** ver el orden en que se cierran los recursos, y leer un archivo real donde algunas líneas están
mal, **sin que una línea mala detenga a las demás**.
**Antes:** relee la sección 6 completa, la sección 2.2 (`NumberFormatException` es un `IllegalArgumentException`)
y la sección 4.2 (cuándo atrapar).
**Archivos:**
- `src/practical/exceptions/TrackedResource.java` (nuevo)
- `src/practical/exceptions/CsvImportLab.java` (nuevo, con `main`)
- `data/products.csv` (nuevo; carpeta `data` **dentro de `aprendiendoJava/`**, al lado de `src`, **no** dentro
  de `src`)

**Parte A — el orden de cierre**

1. Escribe `TrackedResource implements AutoCloseable`:
   - constructor `(String name)` que guarda el nombre e imprime `"abre " + name`;
   - `void use()` que imprime `"usa " + name`;
   - `void fail()` que lanza `new IllegalStateException("falló " + name)`;
   - `close()` que imprime `"cierra " + name`.
2. Un recurso `"archivo"`: ábrelo en un `try (...)` y llama `use()`. `// espero:` las 3 líneas.
3. Dos recursos en el mismo `try`: primero `"conexión"`, después `"archivo"`. Usa los dos. `// espero:` las 6
   líneas.
4. Un recurso `"impresora"`: llama `use()`, después `fail()`, después imprime `"después de fail"`. Con `catch
   (IllegalStateException e)` que imprima `"catch: " + e.getMessage()` y un `finally` que imprima `"finally"`.
   `// espero:` las líneas en orden.

**Parte B — importar productos desde un CSV**

5. Crea `aprendiendoJava/data/products.csv` con **exactamente** este contenido (copia y pega):
   ```
   id;nombre;precio
   1;Teclado;79.90
   2;Mouse;49,00
   3;Monitor
   4;Webcam;-10
   5;Pendrive;15.50
   x;Cable;5.00
   6;Hub USB;35.00
   ```
   La primera línea es la cabecera (no es un producto). Las líneas 3, 4, 5 y 7 tienen errores **a propósito**.
6. En `CsvImportLab`, lo primero: imprime `Path.of("data/products.csv").toAbsolutePath()`. `// espero:` la ruta.
   Si no termina en `aprendiendoJava/data/products.csv`, tu carpeta de trabajo es otra: avísame antes de seguir.
7. Escribe `static Product parseLine(String line)`:
   - separa con `split(";")`;
   - si no hay **exactamente 3** partes → `throw new IllegalArgumentException("se esperaban 3 columnas y hay " +
     partes.length)`;
   - si no, devuelve `new Product(new BigInteger(partes[0]), partes[1], new BigDecimal(partes[2]))`.

   `parseLine` **no atrapa nada**: si algo está mal, la excepción sube.
8. En `main`, lee el archivo con `try (BufferedReader reader = Files.newBufferedReader(Path.of("data/products.csv")))`:
   - lee y **descarta** la cabecera con un primer `reader.readLine();`;
   - lleva un contador `int lineNumber = 1;` (la cabecera es la línea 1) y súmale 1 en cada vuelta;
   - en cada línea, en un `try/catch (IllegalArgumentException e)` **dentro del `while`**: si `parseLine` funciona,
     agrega el producto a `List<Product> imported`; si falla, agrega a `List<String> errors` el texto `"línea " +
     lineNumber + ": " + e.getMessage()`;
   - el `catch (IOException e)` del `try` de afuera imprime `"No se pudo leer: " + e`.

   > Si tienes una línea vacía al final del archivo, `parseLine` la marcará como error ("hay 1"). Bórrala del
   > archivo, o al principio del bucle escribe `if (line.isBlank()) { continue; }` (`continue` = saltar a la
   > siguiente vuelta).
9. Después del `try`, imprime `"importados: " + imported.size()`, cada producto importado (una línea por
   producto), `"errores: " + errors.size()` y cada error. **Antes de ejecutar**, llena esta tabla:

   | Línea del archivo | Contenido | ¿se importa? espero | si falla: qué excepción y por qué |
   |-------------------|-----------|---------------------|-----------------------------------|
   | 2 | `1;Teclado;79.90` | | |
   | 3 | `2;Mouse;49,00` | | |
   | 4 | `3;Monitor` | | |
   | 5 | `4;Webcam;-10` | | |
   | 6 | `5;Pendrive;15.50` | | |
   | 7 | `x;Cable;5.00` | | |
   | 8 | `6;Hub USB;35.00` | | |

10. **Archivo que no existe:** repite la lectura con la ruta `"datos/products.csv"` (con **s**: no existe).
    `// espero:` qué imprime el `catch (IOException e)`.

**Preguntas:**
- **P4.1** *"Con dos recursos, se cierra primero ___ porque ___. Cuando hay una excepción, el recurso se cierra
  ___ del `catch`."*
- **P4.2** *"Un solo `catch (IllegalArgumentException e)` atrapa los errores de las líneas 3, 4, 5 y 7 porque
  `NumberFormatException` ___. Por eso no puedo escribir `catch (NumberFormatException | IllegalArgumentException
  e)`: ___."*
- **P4.3** *"El `try/catch` de cada línea va **dentro** del `while` porque, si estuviera afuera, ___."*
- **P4.4** *"El error de la línea 5 lo lanza ___ (qué clase), no `parseLine`. Lo sé porque el mensaje ___."*
- **P4.5** *"`IOException` hay que atraparla (o declararla) porque es ___. Si la ruta está mal, la excepción
  concreta es ___."*

**Checklist:**
- [ ] `TrackedResource` con los 4 métodos; pasos 2–4 con `espero` en orden
- [ ] `data/products.csv` en `aprendiendoJava/data/` con el contenido exacto
- [ ] paso 6: ruta absoluta impresa
- [ ] `parseLine` sin `try/catch`; el `try/catch` por línea dentro del `while`
- [ ] tabla del paso 9 llenada **antes** de ejecutar; salida con 3 importados y 4 errores (si te salen otros
      números, revisa antes de pedir la revisión)
- [ ] P4.1–P4.5, todos los huecos

---

### Tarea 5 — Experimentos con `java.time` (medio · 30 min)

**Objetivo:** ver con tus ojos la inmutabilidad, el fin de mes, los bisiestos, la distancia entre fechas y los
errores de formato.
**Antes:** relee la sección 7 completa.
**Archivo:** `src/practical/time/DateBasics.java` (con `main`).

1. **Inmutabilidad:** `LocalDate pago = LocalDate.of(2026, 11, 16);` Llama `pago.plusDays(30);` **sin guardar** e
   imprime `pago`. `// espero:` Después `LocalDate vence = pago.plusDays(30);` e imprime `pago + " " + vence`.
   `// espero:`
2. **Fin de mes.** Imprime cada uno con su `espero`:
   - `LocalDate.of(2027, 1, 31).plusMonths(1)`
   - `LocalDate.of(2027, 1, 31).plusMonths(1).plusMonths(1)`
   - `LocalDate.of(2027, 1, 31).plusMonths(2)`

   ¿Por qué el segundo y el tercero son distintos?
3. **Bisiestos.** Imprime con su `espero`:
   - `LocalDate.of(2028, 1, 31).plusMonths(1)`
   - `LocalDate.of(2028, 2, 29).plusYears(1)`
   - `LocalDate.of(2027, 1, 1).isLeapYear() + " " + LocalDate.of(2028, 1, 1).isLeapYear()`
4. **Distancia.** Con `LocalDate ini = LocalDate.of(2026, 11, 16)` y `LocalDate fin = LocalDate.of(2027, 3, 1)`,
   imprime con su `espero`:
   - `ChronoUnit.DAYS.between(ini, fin)`
   - `ChronoUnit.MONTHS.between(ini, fin)`
   - `Period.between(ini, fin)` y `Period.between(ini, fin).getDays()`
   - `ChronoUnit.DAYS.between(fin, ini)`

   > Para el primero no cuentes a mano día por día: piensa "noviembre tiene 30 días, desde el 16 quedan…", o
   > escribe `espero: no sé, alrededor de 100` y anota el `real`.
5. **Comparar.** Con `LocalDate a = LocalDate.of(2027, 3, 1)` y `LocalDate b = LocalDate.parse("2027-03-01")`,
   imprime en **una línea**: `(a == b)`, `a.equals(b)`, `a.isEqual(b)`, `a.isBefore(b)`, `a.isAfter(b)`.
   `// espero:` los 5 `boolean`.
6. **Formatear.** Con `a`, imprime `a.format(...)` con `"dd/MM/yyyy"` y con `"d/M/yyyy"`. `// espero:` Después,
   en un `try/catch (Exception e)` imprimiendo `e`, con `"dd/mm/yyyy"`. `// espero:`
7. **Parsear** con `DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy")`. Cada uno con su `espero`
   (los que fallan, en `try/catch (DateTimeParseException e)` imprimiendo `e.getMessage()`):
   - `LocalDate.parse("15/08/2027", f)`
   - `LocalDate.parse("2027-08-15", f)`
   - `LocalDate.parse("31/04/2027", f)` ← ¿abril tiene 31 días? (sección 7.7, el caso raro)
   - `LocalDate.of(2027, 4, 31)` ← en `try/catch (DateTimeException e)`
8. **Duración de un turno de noche.** Imprime con su `espero`:
   - `Duration.between(LocalTime.of(22, 0), LocalTime.of(6, 0))`
   - `Duration.between(LocalDateTime.of(2026, 11, 16, 22, 0), LocalDateTime.of(2026, 11, 17, 6, 0))`

   ¿Por qué el primero da un resultado raro? (Pista: un `LocalTime` no sabe que las 6:00 son "del día siguiente".)

**Preguntas:**
- **P5.1** *"`pago.plusDays(30)` sin asignar no cambia `pago` porque `LocalDate` es ___. El resultado se pierde
  porque ___."*
- **P5.2** *"`31 de enero + 1 mes + 1 mes` da ___ y `31 de enero + 2 meses` da ___, porque después del primer
  `plusMonths` el día ya es ___."*
- **P5.3** *"`Period.between(ini, fin).getDays()` da ___ y no el total, porque ___. Para el total uso ___."*
- **P5.4** *"Para comparar fechas no uso `==` porque ___. Uso ___ o ___."*
- **P5.5** *"En `"dd/mm/yyyy"`, `mm` significa ___, por eso ___."*

**Checklist:**
- [ ] 8 pasos con separador y `espero`
- [ ] paso 1: primero sin guardar, después guardando
- [ ] paso 7: los cuatro casos, con los que fallan atrapados
- [ ] ✅ solo donde coincide letra por letra (`2027-02-28` ≠ `2027-02-29`)
- [ ] P5.1–P5.5, todos los huecos

---

### Tarea 6 — Entregable: `Renewal` con `LocalDate` real (avanzado · 50 min)

**Objetivo:** convertir tu `Renewal` (que hoy solo tiene tres campos sin usar) en una clase de dominio real:
**inmutable**, con fechas de verdad, que **calcula** su estado según el día, y que **lanza una excepción de
negocio** cuando una operación no está permitida.
**Antes:** relee la sección 5 (excepciones propias), las secciones 7.3, 7.4, 7.5 y 7.8, y la lección 05,
sección 2 (inmutabilidad).
**Archivos:**
- `src/objects/Renewal.java` (modificar)
- `src/Models/exceptions/RenewalNotAllowedException.java` (nuevo)
- `src/Interfaces/Renewable.java` y `src/Models/products/RenewableProduct.java` (modificar, pasos 8–9)
- `src/practical/time/RenewalLab.java` (nuevo, con `main`)

**Hoy tienes esto:**
```java
public class Renewal {
    private LocalDate startDate;
    private LocalDate endDate;
    private RenewalStatus status;
}
```
y `RenewalStatus` es un `enum` con `ACTIVE`, `EXPIRED`, `CANCELLED` (no lo cambies).

**Una decisión de diseño importante (léela dos veces):** el estado `EXPIRED` **depende del día de hoy**: la misma
renovación está `ACTIVE` el 10 de febrero y `EXPIRED` el 1 de marzo. Si lo guardaras en un campo `status`, ese
campo estaría **mal** en cuanto pase la fecha, sin que nadie lo cambie. Por eso:
- se **guarda** solo lo que no depende del día: `startDate`, `endDate` y si está **cancelada** (`boolean
  cancelled`);
- el estado se **calcula** cada vez, con un método que recibe `today`: `statusOn(LocalDate today)`.

Así que el campo `status` **desaparece** y aparece `private final boolean cancelled`.

**Requisitos de `Renewal`:**

| Miembro | Qué hace |
|---------|----------|
| clase `public final class Renewal`, campos `private final` | inmutable: **sin setters** |
| `public Renewal(LocalDate startDate, int months)` | `startDate` `null` → `IllegalArgumentException("startDate no puede ser null")`; `months` fuera de 1–36 → `IllegalArgumentException("months debe estar entre 1 y 36: " + months)`; `endDate = startDate.plusMonths(months)`; `cancelled = false` |
| `private Renewal(LocalDate startDate, LocalDate endDate, boolean cancelled)` | constructor **privado** que usa `cancel()` para crear la copia cancelada |
| `getStartDate()`, `getEndDate()` | getters |
| `RenewalStatus statusOn(LocalDate today)` | `today` `null` → `IllegalArgumentException("today no puede ser null")`; cancelada → `CANCELLED` (**gana siempre**); `today` **después** de `endDate` → `EXPIRED`; si no → `ACTIVE`. El día `endDate` **todavía** está `ACTIVE` |
| `long daysLeft(LocalDate today)` | si `statusOn(today)` no es `ACTIVE` → `0`; si no, los días de `today` a `endDate` |
| `Renewal renew(int months)` | si está cancelada → `RenewalNotAllowedException("No se puede renovar una suscripción cancelada (terminaba el " + endDate + ")")`; si no, devuelve una **nueva** `Renewal` que empieza en el `endDate` de esta |
| `Renewal cancel()` | si ya estaba cancelada → `RenewalNotAllowedException("La suscripción ya estaba cancelada")`; si no, devuelve una **nueva**, con las mismas fechas y `cancelled = true` |
| `toString()` | `Renewal{2027-01-31 → 2027-02-28}` y, si está cancelada, `Renewal{2027-02-28 → 2027-03-28, cancelada}` |

**Restricciones:**
- **Prohibido `LocalDate.now()`** dentro de `Renewal` (sección 7.8). El "hoy" siempre llega como parámetro.
- `renew` y `cancel` **no modifican** `this`: devuelven un objeto nuevo (como `plusDays`).
- `RenewalNotAllowedException extends RuntimeException`, con un constructor `(String message)`.

**Esqueleto** (solo firmas):
```java
public final class Renewal {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean cancelled;

    public Renewal(LocalDate startDate, int months) { ... }
    private Renewal(LocalDate startDate, LocalDate endDate, boolean cancelled) { ... }

    public LocalDate getStartDate() { ... }
    public LocalDate getEndDate() { ... }
    public RenewalStatus statusOn(LocalDate today) { ... }
    public long daysLeft(LocalDate today) { ... }
    public Renewal renew(int months) { ... }
    public Renewal cancel() { ... }
    @Override
    public String toString() { ... }
}
```

**Pasos:**
1. Escribe `RenewalNotAllowedException` y `Renewal`.
2. **Compila el proyecto.** Va a fallar **a propósito** en otro archivo. Copia el error en un comentario de
   `RenewalLab`. `// espero:` ¿en qué archivo y por qué? (Pista: ¿quién hacía `new Renewal()` sin argumentos?)
   Arréglalo en los pasos 8–9; mientras tanto, para avanzar, puedes comentar el cuerpo de ese método y poner
   `return null;`.
3. En `RenewalLab`: `Renewal r1 = new Renewal(LocalDate.of(2027, 1, 31), 1);` e imprime `r1`. `// espero:` (sección
   7.4)
4. Llena la tabla **antes** de ejecutar e imprime cada fila como `statusOn + " " + daysLeft`:

   | `today` | `r1.statusOn(today)` espero | `r1.daysLeft(today)` espero | real |
   |---------|-----------------------------|-----------------------------|------|
   | `2027-02-10` | | | |
   | `2027-02-28` (el último día) | | | |
   | `2027-03-01` | | | |

5. `Renewal r2 = r1.renew(1);` Imprime `r1 + " | " + r2`. `// espero:` ¿cambió `r1`? ¿cuándo termina `r2`? (Ojo:
   empieza el 28, no el 31.) Imprime también `r2.statusOn(2027-03-01) + " " + r2.daysLeft(2027-03-01)`.
   `// espero:`
6. `Renewal c = r2.cancel();` Imprime `c`, `c.statusOn(2027-03-01)`, `c.daysLeft(2027-03-01)` y, en la misma
   línea, `r2.statusOn(2027-03-01)`. `// espero:` ¿`r2` también quedó cancelada?
7. Cada uno en su `try/catch`, imprimiendo `e.getMessage()`, con su `espero`:
   - `c.renew(1)` → `RenewalNotAllowedException`
   - `c.cancel()` → `RenewalNotAllowedException`
   - `new Renewal(LocalDate.of(2027, 1, 1), 0)` → `IllegalArgumentException`
   - `new Renewal(null, 3)` → `IllegalArgumentException`
8. **El producto.** Cambia la interfaz `Renewable`: hoy tiene `Renewal renew()` y `RenewalStatus
   getRenewalStatus()`. Déjala con **un solo** método: `Renewal startRenewal(LocalDate start);`.
   ¿Por qué quitar `getRenewalStatus()`? Porque el estado es **de cada renovación** y depende del día; un
   producto ("Netflix 3 meses") no tiene estado: es el catálogo. (Respóndelo con tus palabras en P6.4.)
9. En `RenewableProduct`, implementa `startRenewal(LocalDate start)`: devuelve una `Renewal` que empieza en `start`
   y dura los `months` del producto. Quita lo que ya no se usa. Comprueba que `Main` y `ProductListLab` siguen
   compilando (solo usan el constructor).
10. En `RenewalLab`: crea `RenewableProduct spotify` (id 50, `"Spotify Premium"`, precio `9.99`, `3` meses) e
    imprime `spotify.startRenewal(LocalDate.of(2026, 11, 30))`. `// espero:` (¿existe el 30 de febrero?)
11. Por último, dos casos bisiestos con su `espero`: `new Renewal(LocalDate.of(2028, 1, 31), 1)` y `new
    Renewal(LocalDate.of(2028, 2, 29), 12)`.

**Preguntas:**
- **P6.1** *"`Renewal` no guarda el estado `EXPIRED` en un campo porque ___. En su lugar, `statusOn` ___."*
- **P6.2** *"`statusOn` recibe `today` en vez de usar `LocalDate.now()` porque ___. Así, en un test (semana 11),
  puedo ___."*
- **P6.3** *"`renew` y `cancel` devuelven un objeto nuevo y no cambian `this`. Lo comprobé en el paso ___, donde
  `r1` ___ y `r2` ___."*
- **P6.4** *"Quité `getRenewalStatus()` de `Renewable` porque ___."*
- **P6.5** *"`RenewalNotAllowedException` hereda de ___. La lanzo cuando ___, y no devuelvo `null` o `false`
  porque ___."*
- **P6.6** *"Una suscripción que empieza el 31 de enero y se renueva mes a mes termina el ___ de febrero y después
  el ___ de marzo, no el 31. Esto pasa porque ___. ¿Es un bug? ___ (respóndelo pensando como el dueño del
  negocio: ¿qué esperaría el cliente?)."*

**Checklist:**
- [ ] `Renewal` inmutable: campos `final`, sin setters, `renew`/`cancel` devuelven objetos nuevos
- [ ] ningún `LocalDate.now()` dentro de `Renewal`
- [ ] tabla del paso 4 llenada **antes**; el último día (`2027-02-28`) está `ACTIVE`
- [ ] paso 2: error de compilación copiado, y arreglado en los pasos 8–9
- [ ] mensajes de las excepciones **exactos** (paso 7)
- [ ] `Renewable` con un solo método; `RenewableProduct` actualizado; todo el proyecto compila
- [ ] P6.1–P6.6, todos los huecos

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
