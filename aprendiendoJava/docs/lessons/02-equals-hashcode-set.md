# Lección 02 — `equals()` / `hashCode()` + `Set`

> Ruta: Fase 3 (Collections), semana 2 del roadmap.
> Viene de: Lección 01 (`List`). Prepara: Lección 03 (`Map`), que usa exactamente las mismas reglas.
> Todos los comportamientos descritos aquí fueron comprobados ejecutando código en Java 21.

---

## Cómo usar esta lección

No leas todo de golpe. Alterna **una sección de teoría → su tarea**:

| Lee | Después haz | Tiempo aprox. |
|-----|-------------|---------------|
| §0 + §1.1 | Tarea 1 | 40 min |
| §1.2 | Tarea 2 | 40 min |
| §1.3 + §1.4 | Tarea 3 | 40 min |
| §1.5 | Tarea 4 | 30 min |
| §1.6 + §1.7 | Tarea 0 y Tarea 5 | 50 min |
| §1.8 | Tarea 6 | 50 min |
| — | revisión y correcciones | 30 min |

La Tarea 0 es un calentamiento con `Set<String>`. Puedes hacerla primero si prefieres empezar por lo fácil.

**Si algo no se entiende:** vuelve a leer **solo el ejemplo** de esa sección y ejecútalo tú mismo en un archivo
de prueba. Si sigue sin entenderse, pregúntame por el número de sección ("no entiendo §1.3").

---

## Deuda de la lección 01

Avanzamos con algunas cosas abiertas de la lección 01. No hace falta volver a ella: estos temas **reaparecen
aquí** y los practicas de nuevo. Están en sus bloques 🔍 REVISIÓN por si quieres repasar.

| Pendiente de la lección 01 | Dónde reaparece aquí |
|----------------------------|----------------------|
| escribir `porque` cuando una predicción falla | **todas** las tareas (plantilla de predicción) |
| valor de retorno vs efecto (`set`, `remove`) | Tarea 0 (`add` y `remove` de `Set` devuelven `boolean`) |
| `final` protege la variable, no el objeto | Tarea 3 (`id` final) |
| `BigDecimal`: `compareTo`, no `floatValue` | Tarea 3 (corregir `isValidValue`) |
| `equals` heredado de `Object` (P5.1, P5.2) | Tarea 1 (es justo el punto de partida) |

---

## 0. Puente desde PHP

En PHP **ya conoces** las dos ideas de esta lección, con otros nombres:

| Idea | PHP | Java |
|------|-----|------|
| ¿Es **el mismo** objeto? | `$a === $b` | `a == b` |
| ¿**Valen** lo mismo? | `$a == $b` (compara las propiedades, automático) | `a.equals(b)` (**tú** decides qué comparar) |
| Lista sin repetidos | `array_unique($arr)` / `collect()->unique('id')` | `Set<T>` |
| ¿Está dentro? | `in_array($x, $arr)` | `set.contains(x)` |

⚠️ **La gran diferencia:** en PHP, `==` entre objetos compara las propiedades **automáticamente**. En Java,
si no escribes `equals()`, la versión heredada de `Object` hace lo mismo que `==`: compara si es el
**mismo objeto**. Por eso en la lección 01, Tarea 5, `contains(secondLaptop)` dio `false`.

---

## 1. Teoría

### 1.1 Identidad vs igualdad

**CONCEPTO**
- **Identidad** (`==`): ¿estas dos variables apuntan al **mismo objeto** en memoria?
- **Igualdad** (`equals`): ¿estos dos objetos **representan lo mismo** en mi dominio?

```
 laptopA ──►  [Laptop id=1 "Laptop"]  @dirección A
 laptopB ──►  [Laptop id=1 "Laptop"]  @dirección B     ← otro new = otro objeto

 laptopA == laptopB          → false  (distintas direcciones)
 laptopA.equals(laptopB)     → depende de cómo esté escrito equals() en Product
```

**POR QUÉ importa:** casi todo en Collections usa `equals`: `contains`, `indexOf`, `remove(Object)`,
`Set` y `Map`. Si `equals` no está bien escrito, todas esas operaciones dan resultados "raros".

**EN MI CÓDIGO:** hoy `Product` **no** tiene `equals`, así que usa la versión de `Object`:
```java
// así está escrito en java.lang.Object (simplificado)
public boolean equals(Object obj) {
    return this == obj;      // identidad, no igualdad
}
```

**ERROR COMÚN:** comparar objetos con `==`, sobre todo `String`, `BigDecimal` y `BigInteger`:
```java
new BigInteger("1") == new BigInteger("1")        // false  (comprobado)
new BigInteger("1").equals(new BigInteger("1"))   // true
```
⚠️ Esto importa para ti: el `id` de `Product` es un `BigInteger`.

---

### 1.2 Cómo escribir `equals()`

**El contrato.** Son las reglas que Java exige. Si las rompes, `Set`, `Map` y `contains` dejan de funcionar bien.

| Regla | En palabras | Ejemplo de la vida real |
|-------|-------------|-------------------------|
| **Reflexiva** | `a.equals(a)` es `true` | yo soy igual a mí mismo |
| **Simétrica** | si `a.equals(b)`, entonces `b.equals(a)` | si Ana es gemela de Luis, Luis es gemelo de Ana |
| **Transitiva** | si `a = b` y `b = c`, entonces `a = c` | |
| **Consistente** | si nada cambió, el resultado no cambia entre llamadas | |
| **Null** | `a.equals(null)` es `false` y **nunca** lanza excepción | |

**La receta en 5 pasos.** Casi todos los `equals` del mundo siguen este orden:

1. **¿Es el mismo objeto?** (`this == o`) → devuelve `true`. Es un atajo rápido.
2. **¿`o` es `null`?** → devuelve `false`.
3. **¿`o` es del tipo correcto?** → si no, devuelve `false`. Hay dos formas de hacerlo (§1.5).
4. **Convierte** `o` a tu tipo (cast o *pattern matching*, lección 01, Tarea 3, P3.3).
5. **Compara los campos** que definen la igualdad (§1.4) y devuelve el resultado.

> Con `instanceof`, los pasos 2 y 3 se hacen juntos: `null instanceof Cualquiera` siempre es `false`.

**Cómo comparar cada tipo de campo dentro de `equals`:**

| Tipo del campo | Cómo comparar |
|----------------|---------------|
| primitivo (`int`, `long`, `boolean`) | `==` |
| objeto (`String`, `BigInteger`...) | `campo.equals(otro.campo)`, o `Objects.equals(campo, otro.campo)` si puede ser `null` |
| `BigDecimal` | ⚠️ cuidado, ver §1.4 |

`java.util.Objects.equals(a, b)` es `true` si ambos son `null`, o si `a.equals(b)`. Nunca lanza `NullPointerException`.

**ERROR COMÚN — el parámetro debe ser `Object`, no `Product`:**
```java
public boolean equals(Product o) { ... }   // ❌ esto NO sobrescribe: es OTRO método (sobrecarga)
public boolean equals(Object o)  { ... }   // ✅ esto sí sobrescribe el de Object
```
`HashSet` y `contains` llaman a `equals(Object)`. Si escribes `equals(Product)`, nunca lo usan.
**Siempre pon `@Override`.** Con la firma equivocada, el compilador te avisa (comprobado):
```
error: method does not override or implement a method from a supertype
```
(Es la misma idea de sobrecarga que `remove(int)` vs `remove(Object)` en la lección 01.)

---

### 1.3 `hashCode()`: los cajones

**CONCEPTO:** `hashCode()` devuelve un `int` que funciona como **número de cajón**.

**Analogía.** Un archivo con 1000 expedientes. Para encontrar uno, **no** revisas los 1000 (eso hace `List.contains`).
Los organizas en cajones por la primera letra del apellido:
1. Calculas el cajón: "Pérez" → cajón **P**. Esto es el `hashCode()`.
2. Abres **solo** ese cajón y comparas expediente por expediente. Esto es el `equals()`.

Así funciona `HashSet` (y `HashMap`, en la lección 03):
```
 set.contains(x):
   1) cajón = x.hashCode()          ← va directo a UN cajón
   2) dentro de ese cajón, ¿algún elemento e cumple e.equals(x)?
```

**LA REGLA DE ORO:**
> **Si `a.equals(b)` es `true`, entonces `a.hashCode() == b.hashCode()` OBLIGATORIAMENTE.**

¿Por qué? Si dos objetos "iguales" tienen cajones distintos, `HashSet` busca en el cajón **equivocado**.
Nunca llega a llamar a `equals`, y responde que no lo tiene.

Al revés **no** es obligatorio: dos objetos distintos pueden compartir cajón ("Pérez" y "Paz"). Eso se llama
**colisión**, es normal, y `equals` los distingue dentro del cajón.

**Cómo escribirlo:** usa los **mismos campos** que usaste en `equals`, ni uno más ni uno menos:
```java
// ejemplo con OTRA clase (un cupón identificado por su código)
@Override
public int hashCode() {
    return Objects.hash(code);   // Objects.hash(campo1, campo2, ...) combina los campos
}
```

**¿Y `List`?** `List.contains` **no** usa `hashCode`: recorre todos los elementos con `equals`. Por eso una clase con
`equals` y sin `hashCode` "funciona" en una `List`... y falla en un `HashSet`. Lo vas a ver en las Tareas 1–3.

---

### 1.4 ¿Qué campos uso para comparar?

Depende de qué **es** el objeto:

| Tipo de objeto | Igualdad por... | Ejemplo |
|----------------|-----------------|---------|
| **Entidad**: tiene identidad propia, aunque cambien sus datos | su **id** | `Product`, `Customer`, `Order`. Un producto sigue siendo el mismo aunque cambie de precio |
| **Valor**: solo importan sus datos | **todos** sus campos | un `Money(100, "USD")`, una fecha, una dirección |

**EN MI CÓDIGO:** `Product` es una **entidad** y se compara por `id`.

**Regla de seguridad: los campos de `equals`/`hashCode` no deben cambiar** mientras el objeto esté en un `Set`.
Ejemplo comprobado con otra clase:
```java
Coupon c = new Coupon("A");      // equals/hashCode basados en 'code'
Set<Coupon> set = new HashSet<>();
set.add(c);                      // guardado en el cajón de "A"
c.code = "B";                    // 💥 el objeto cambió, pero sigue en el cajón de "A"
set.contains(c);                 // false ← busca en el cajón de "B" y no está
set.size();                      // 1    ← sigue dentro, pero ya no se puede encontrar
```
Por eso el `id` debería ser `final` (lección 01: `final` impide **reasignar** la variable).

**ERROR COMÚN — usar `BigDecimal` en `equals`** (comprobado):
```java
new BigDecimal("2.0").equals(new BigDecimal("2.00"))      // false ← equals compara también la escala (decimales)
new BigDecimal("2.0").compareTo(new BigDecimal("2.00"))   // 0     ← compareTo compara el valor numérico
```
Esta es otra razón para no meter `price` en el `equals` de `Product`.

---

### 1.5 `getClass()` vs `instanceof` (paso 3 de la receta)

Hay dos formas de comprobar "¿es del tipo correcto?":

```java
if (o == null || getClass() != o.getClass()) return false;   // A: EXACTAMENTE la misma clase
if (!(o instanceof Product)) return false;                   // B: Product o cualquier subclase
```

Imagina un `PhysicalProduct` con id 5 y un `Laptop` con id 5:

| | A: `getClass()` | B: `instanceof` |
|---|---|---|
| `physical.equals(laptop)` | `false` | `true` |
| `laptop.equals(physical)` | `false` | `true` |
| ¿Simétrico? | ✅ sí | ✅ sí, **mientras ninguna subclase sobrescriba `equals` otra vez** |
| Significado | "un Laptop nunca es igual a un PhysicalProduct" | "mismo id = mismo producto, sea cual sea la subclase" |

**La trampa de simetría** (comprobado con clases de prueba): si usas `instanceof` en la clase padre y una
subclase **vuelve a sobrescribir** `equals` con su propio `instanceof`:
```
padre.equals(hijo)  → true    (el hijo ES un padre)
hijo.equals(padre)  → false   (el padre NO ES un hijo)
→ se rompe la simetría → un Set puede dar resultados distintos según el orden en que agregues
```
**Regla práctica:** si usas `instanceof`, escribe `equals` **solo en la clase padre** y no lo sobrescribas en las subclases.

**PROFESIONALMENTE:** en entidades JPA (semana 19), Hibernate crea **subclases proxy** de tus entidades. Con
`getClass()`, un proxy de `Product` no sería igual al `Product` real. Por eso en entidades se suele preferir
`instanceof`. No hace falta entenderlo hoy: solo saber que la decisión tiene consecuencias reales.

---

### 1.6 `Set`: la colección sin duplicados

**CONCEPTO:** `Set<T>` es una interfaz (como `List`). Su contrato:
- **No** permite duplicados, según `equals` (y `hashCode` en los `Hash...`).
- **No** tiene posiciones: no existen `get(int)`, `set(int, e)` ni `add(int, e)`.

**Métodos principales:**

| Método | Efecto | Devuelve |
|--------|--------|----------|
| `add(e)` | agrega **si no estaba** | `true` si lo agregó, `false` si ya estaba (y **no** lo reemplaza) |
| `remove(o)` | elimina si estaba | `true` si lo eliminó, `false` si no estaba |
| `contains(o)` | — | `boolean` |
| `size()`, `isEmpty()`, `clear()` | igual que `List` | |

⚠️ Recuerda el error de la lección 01: **efecto ≠ valor devuelto**. `add` devuelve un `boolean`, no el set.

**¿Cuál se queda?** Si agregas un duplicado, se queda **el primero**. El segundo se descarta (comprobado).

**`Set.of(...)`:** igual que `List.of`, crea un set **inmutable**. Además lanza `IllegalArgumentException` si le
pasas duplicados: `Set.of("a", "a")` → 💥.

---

### 1.7 Las tres implementaciones

Con la misma entrada `List.of("Pedro", "ana", "Luis", "Ana", "Marta", "Luis")` (comprobado):

| Implementación | Resultado | Orden | Necesita |
|----------------|-----------|-------|----------|
| `HashSet` | `[Marta, ana, Ana, Luis, Pedro]` | **ninguno garantizado** (depende de los cajones) | `equals` + `hashCode` |
| `LinkedHashSet` | `[Pedro, ana, Luis, Ana, Marta]` | **orden de inserción** | `equals` + `hashCode` |
| `TreeSet` | `[Ana, Luis, Marta, Pedro, ana]` | **ordenado** (alfabético para `String`) | saber comparar: `Comparable` o `Comparator` (semana 4) |

Observa:
- En los tres, `"Luis"` aparece **una sola vez**.
- `"Ana"` y `"ana"` son **distintos**: `String.equals` distingue mayúsculas.
- En `TreeSet`, `"ana"` queda **al final**: Java ordena los `String` por el código de cada carácter, y las mayúsculas van antes que las minúsculas.

**¿Cuál uso?**
- **`HashSet`** por defecto, si el orden no importa (es el más rápido).
- **`LinkedHashSet`** si quieres quitar duplicados **conservando el orden** en que llegaron.
- **`TreeSet`** si los quieres **ordenados**.

**`TreeSet` con tus clases:** `TreeSet` no usa cajones: ordena. Necesita saber "quién va primero". Si `Product`
no sabe compararse, falla **en el primer `add`** (comprobado):
```
ClassCastException: class ... cannot be cast to class java.lang.Comparable
```
Esto lo resuelve la semana 4. Por ahora, solo obsérvalo.

---

### 1.8 `List` o `Set`: cómo elegir

| Pregunta | Si la respuesta es sí → |
|----------|------------------------|
| ¿Puede haber repetidos y me importan? | `List` |
| ¿Necesito acceder por posición (`get(2)`)? | `List` |
| ¿Cada elemento debe aparecer **una vez**? | `Set` |
| ¿Pregunto mucho "¿está X?" en colecciones grandes? | `Set` (`HashSet.contains` es casi instantáneo; `List.contains` recorre todo) |

**Truco frecuente:** quitar duplicados de una lista conservando el orden, en una línea, pasando la lista al
constructor de un `Set` (y después a una `List` otra vez, si hace falta).
Comprobado: `[3, 1, 3, 2, 1]` → `[3, 1, 2]`. (Lo harás en la Tarea 5: piensa qué `Set` conserva el orden.)

**Encapsulación (lección 01, §1.7):** para devolver un `Set` interno sin que lo modifiquen desde fuera:

| Forma | ¿Conserva el orden de un `LinkedHashSet`? |
|-------|-------------------------------------------|
| `Collections.unmodifiableSet(set)` | ✅ sí (es una vista) |
| `Set.copyOf(set)` | ❌ **no**: el orden queda revuelto (comprobado) |

---

### 1.9 Errores comunes (resumen)

1. Escribir `equals` sin `hashCode`: funciona en `List` y falla en `HashSet`/`HashMap`.
2. `equals(Product o)` en vez de `equals(Object o)`, y sin `@Override`.
3. Comparar campos objeto con `==` (`BigInteger`, `String`).
4. Usar campos que cambian (precio, nombre) en `equals`/`hashCode`.
5. `equals` que lanza `NullPointerException` con `null`.
6. Esperar orden en un `HashSet`.
7. Creer que `add` de un duplicado reemplaza al anterior (no lo hace).
8. `TreeSet` con una clase que no sabe compararse.

### 1.10 Relación con Spring Boot

- **JPA / Hibernate (semana 19):** `equals`/`hashCode` de entidades es una fuente clásica de bugs. Por ejemplo, el
  `id` es `null` hasta que se guarda en la base de datos. Lo que aprendes hoy es la base para entender esa discusión.
- **`Map` (lección 03):** las claves de un `HashMap` siguen **exactamente** estas reglas.
- **Lombok** (`@EqualsAndHashCode`) y los `record` (semana 5) generan estos métodos. Vas a saber qué generan y cuándo es peligroso.

---

## 2. Ejemplo pequeño (léelo, ejecútalo si quieres, no lo copies a las tareas)

```java
Set<String> tags = new HashSet<>();
System.out.println(tags.add("java"));      // true  → lo agregó
System.out.println(tags.add("spring"));    // true
System.out.println(tags.add("java"));      // false → ya estaba, no hizo nada
System.out.println(tags.size());           // 2
System.out.println(tags.contains("JAVA")); // false → mayúsculas distintas
System.out.println(tags.remove("php"));    // false → no estaba
```

---

## 3. Tareas

### Reglas para todas las tareas

- Archivos nuevos en `src/practical/sets/`, con `package practical.sets;` y un `main` (`String[] args`).
- **Plantilla de predicción obligatoria.** Escríbela **antes** de ejecutar. Después de ejecutar, completa `real`
  y, si no coincide, `porque`:
  ```java
  System.out.println(set.size());
  // espero: 2
  // real:   1
  // porque: el segundo "Ana" no se agregó, add devolvió false
  ```
  Si aciertas, basta con `// espero: 2 ✅`.
  **Si fallas una predicción, no sigas con la siguiente línea hasta escribir el `porque`.** (Así evitas el error
  en cascada de la lección 01.)
- Sin Streams y sin IA para generar la lógica.
- Puedes pedir "revisa tarea N" al terminar **cada** tarea.

Compilar y ejecutar desde `aprendiendoJava/`:
```
javac -d out/cli $(find src -name '*.java') && /usr/lib/jvm/java-21-openjdk-amd64/bin/java -cp out/cli practical.sets.NombreDeLaClase
```

---

### Tarea 0 — Calentamiento: `Set<String>` (fácil · 20 min)

**Objetivo:** usar los métodos básicos de `Set` y fijarte en lo que **devuelven**.
**Antes:** lee §1.6 y §2.
**Archivo:** `src/practical/sets/SetBasics.java`

**Pasos:**
1. Crea `Set<String> names = new HashSet<>();`. Declara con la interfaz `Set`, como hacías con `List`.
2. Imprime `names.isEmpty()` y `names.size()`.
3. Escribe `boolean first = names.add("Ana");` e imprime `first`.
4. Escribe `boolean second = names.add("Ana");`. Imprime `second` y luego `names.size()`.
5. Agrega `"Luis"`, `"Marta"` y `"ana"` (en minúscula). Imprime `names.size()` y después `names`.
   Para predecir el orden del `HashSet`, escribe `// espero: no sé el orden, pero estos elementos: ...`.
6. Imprime `names.contains("Ana")` y `names.contains("ANA")`.
7. Escribe `boolean removedLuis = names.remove("Luis");` e imprímelo. Haz lo mismo con `"Pedro"`, que no existe.
   Imprime `names`.
8. Escribe `names.get(0);` y compila. **No va a compilar.** Copia el mensaje de error en un comentario y después
   comenta esa línea para que el resto compile.
9. Recorre `names` con un for-each e imprime cada nombre en su propia línea.
10. Crea `Set<String> fixed = Set.of("x", "y");` e intenta `fixed.add("z")` dentro de un
    `try/catch (UnsupportedOperationException e)`. En el `catch`, imprime `e.getClass().getSimpleName()`.

**Preguntas** (en comentarios al final del archivo):
- **P0.1** ¿Qué significa que `add` devuelva `false`? ¿Se reemplazó el `"Ana"` anterior?
- **P0.2** ¿Por qué `Set` no tiene `get(int)`? Piensa en qué significaría "la posición 0" en un `HashSet`.
- **P0.3** ¿Por qué `"Ana"` y `"ana"` cuentan como dos elementos?

**Revisa antes de entregar:**
- [ ] 10 pasos en orden
- [ ] cada `println` con `espero` y `real` (y `porque` donde falló)
- [ ] el mensaje de error del paso 8 copiado
- [ ] 3 preguntas respondidas

---

### Tarea 1 — Ronda 1: `Product` **sin** `equals` ni `hashCode` (fácil · 40 min)

**Objetivo:** ver con tus propios ojos qué pasa **hoy**, antes de cambiar nada.
**Antes:** lee §0 y §1.1.
**Archivo:** `src/practical/sets/EqualsHashCodeLab.java`
**⚠️ No toques `Product.java` en esta tarea.**

**Pasos:**
1. Crea tres productos, todos declarados como `Product` (tipo de la izquierda):
   - `laptopA`: `Laptop` con id `1`, nombre `"Laptop"`, precio `"1000"`, peso `"2"`
   - `laptopB`: `Laptop` con **exactamente** los mismos datos que `laptopA`
   - `laptopC`: `Laptop` con id `2`, nombre `"Laptop Pro"`, precio `"1500"`, peso `"2"`
2. Imprime `laptopA == laptopB`.
3. Imprime `laptopA.equals(laptopB)`.
4. Imprime `laptopA.hashCode() == laptopB.hashCode()`.
5. Crea una `List<Product> list`, agrega **solo** `laptopA` e imprime `list.contains(laptopB)`.
6. Crea un `Set<Product> set = new HashSet<>();`. Agrega `laptopA`, `laptopB` y `laptopC`, **imprimiendo lo que
   devuelve cada `add`**. Son 3 `println`, por ejemplo `System.out.println(set.add(laptopA));`.
7. Imprime `set.size()`.
8. Crea `laptopD` con los mismos datos que `laptopA` (id `1`). Imprime `set.contains(laptopD)`.

**Al final del archivo**, copia esta tabla en un comentario y llena **solo la columna "Ronda 1"** con la salida
real. Las columnas 2 y 3 las llenarás en las Tareas 2 y 3, **sin cambiar el código de esta clase**:
```
| Línea                          | Ronda 1 (nada) | Ronda 2 (solo equals) | Ronda 3 (equals + hashCode) |
|--------------------------------|----------------|-----------------------|-----------------------------|
| A == B                         |                |                       |                             |
| A.equals(B)                    |                |                       |                             |
| A.hashCode() == B.hashCode()   |                |                       |                             |
| list.contains(B)               |                |                       |                             |
| set.add(A)                     |                |                       |                             |
| set.add(B)                     |                |                       |                             |
| set.add(C)                     |                |                       |                             |
| set.size()                     |                |                       |                             |
| set.contains(D)                |                |                       |                             |
```

**Preguntas:**
- **P1.1** ¿Por qué `laptopA == laptopB` es `false` si tienen los mismos datos?
- **P1.2** ¿Por qué `equals` también da `false`? ¿Qué método de qué clase se está ejecutando y qué compara?
  (Es la deuda de la lección 01, Tarea 5.)
- **P1.3** ¿Por qué el set acepta a `laptopB` si "es el mismo producto"?

---

### Tarea 2 — Ronda 2: **solo** `equals` (medio · 40 min)

**Objetivo:** escribir tu primer `equals` y descubrir por qué **no basta**.
**Antes:** lee §1.2 completo.
**Archivos:** `src/Models/Product.java` (modificar) + la tabla de `EqualsHashCodeLab` (columna 2)

**Pasos:**
1. En `Product`, agrega el método `equals` con:
   - la anotación `@Override` arriba,
   - la firma exacta `public boolean equals(Object o)`, con parámetro **`Object`** y no `Product` (§1.2, error común).
2. Dentro, sigue la **receta de 5 pasos** de §1.2. En el paso 3 de la receta, usa **`instanceof`**. En la Tarea 4
   decidirás si cambiarlo.
3. Compara **solo** el `id`. Como `id` es un `BigInteger` (un objeto), **no** uses `==` (§1.1, error común).
   Usa `Objects.equals(...)` o `.equals(...)`.
4. **No** agregues `hashCode` todavía. Que el IDE no lo genere por ti.
5. **Sin cambiar `EqualsHashCodeLab`**, compila y ejecuta. Antes de ejecutar, escribe en la columna 2 tu
   **predicción** con lápiz mental: `espero → real`. Por ejemplo, `true → false`.

**Prueba rápida de tu `equals`.** Agrega estas líneas al final de `EqualsHashCodeLab`, cada una con su predicción:
- `laptopA.equals(laptopA)` → regla reflexiva
- `laptopA.equals(null)` → **no** debe lanzar excepción
- `laptopA.equals(laptopC)`
- `laptopA.equals("Laptop")` → un `String` no es un `Product`

**Preguntas:**
- **P2.1** Compara las columnas 1 y 2: ¿qué filas cambiaron y cuáles **no**?
- **P2.2** `list.contains(B)` ahora da `true`, pero el set sigue aceptando a `laptopB`. ¿Por qué? Explícalo con la
  analogía de los **cajones** de §1.3. Lee §1.3 **después** de ejecutar, para que tu predicción sea honesta.
- **P2.3** ¿Por qué el parámetro de `equals` es `Object` y no `Product`?

---

### Tarea 3 — Ronda 3: `hashCode` + `id` final + corregir el precio (medio · 40 min)

**Objetivo:** cumplir la regla de oro y dejar `Product` sólido.
**Antes:** lee §1.3 y §1.4.
**Archivos:** `src/Models/Product.java` + columna 3 de la tabla

**Pasos:**
1. Agrega `hashCode()` con `@Override`, usando **los mismos campos** que en `equals` (solo `id`).
   Puedes usar `Objects.hash(...)`.
2. Sin cambiar `EqualsHashCodeLab`, ejecuta y llena la **columna 3** (`espero → real`).
3. Marca el campo `id` como `final`. Compila. Si algo deja de compilar, anota qué y por qué.
4. **Deuda de la lección 01: corrige `isValidValue`.** Hoy es `price.floatValue() >= 0`:
   - Si `price` es `null`, debe devolver `false`, para que el constructor lance tu `IllegalArgumentException`
     en vez de un `NullPointerException`.
   - Compara con `compareTo(BigDecimal.ZERO)`, no con `floatValue()`. Recuerda que `compareTo` solo garantiza el
     **signo** (lección 01, Tarea 3).
5. Comprueba que no rompiste nada: ejecuta `Main` (debe seguir imprimiendo `3846`) y `ProductListLab`.
6. En `EqualsHashCodeLab`, agrega una prueba de la corrección: crea un `Laptop` con precio `null` dentro de un
   `try/catch (IllegalArgumentException e)` e imprime el mensaje.

**Preguntas:**
- **P3.1** Escribe la regla de oro de `equals`/`hashCode` con tus palabras, y qué se rompe si no la cumples.
- **P3.2** ¿Por qué `equals` usa solo el `id` y no también `name` y `price`? Da dos razones: una de dominio
  (§1.4, entidad) y una técnica (§1.4, `2.0` vs `2.00`).
- **P3.3** ¿Por qué el `id` debe ser `final`? Usa el ejemplo del `Coupon` de §1.4: ¿qué pasaría si existiera
  `setId()` y alguien cambiara el id de un producto que ya está dentro de un `HashSet`?
- **P3.4** ¿Por qué la regla dice "si son `equals` → mismo `hashCode`" y no al revés?

---

### Tarea 4 — `getClass()` vs `instanceof` (medio-avanzado · 30 min)

**Objetivo:** tomar una **decisión de diseño** y justificarla con datos.
**Antes:** lee §1.5.
**Archivo:** `src/practical/sets/EqualityTypeLab.java`

**Pasos:**
1. Crea `Product physical = new PhysicalProduct(...)` con id `5`, nombre `"Monitor"`, precio `"300"`, peso `"4"`.
2. Crea `Product laptop = new Laptop(...)` con **los mismos datos** (id `5`).
3. Imprime `physical.equals(laptop)` y `laptop.equals(physical)`. Son las dos direcciones, para comprobar la simetría.
4. Crea un `HashSet<Product>`, agrega los dos e imprime `size()`.
5. **Ronda con `getClass`:** cambia temporalmente el paso 3 de la receta en `Product.equals` a la versión
   `getClass()` (§1.5, opción A). Ejecuta de nuevo y anota los resultados de los pasos 3 y 4.
6. Decide con cuál versión te quedas y deja **esa** en `Product`.

Anota los resultados en esta tabla (en comentarios):
```
| Línea                        | instanceof | getClass |
|------------------------------|------------|----------|
| physical.equals(laptop)      |            |          |
| laptop.equals(physical)      |            |          |
| set.size()                   |            |          |
```

**Preguntas:**
- **P4.1** ¿Las dos versiones son simétricas en **tu** código? ¿Cuándo se rompería la simetría con `instanceof`? (§1.5)
- **P4.2** **Decisión de dominio:** en nuestro e-commerce, imagina una sola tabla `products` en MySQL con un `id`
  autoincremental. ¿Puede existir un `Laptop` id 5 y **otro** producto distinto con id 5? Con esa respuesta,
  ¿cuál versión elegiste y por qué?

---

### Tarea 5 — `HashSet` vs `LinkedHashSet` vs `TreeSet` (medio · 30 min)

**Objetivo:** elegir la implementación según el **orden** que necesitas.
**Antes:** lee §1.7 y §1.8.
**Archivo:** `src/practical/sets/SetOrderLab.java`

**Pasos:**
1. Crea `List<String> names = List.of("Carla", "beto", "Andrés", "Carla", "Diego", "beto", "Ana");`.
   Son **otros** nombres que los del ejemplo de §1.7, así que no puedes copiar el resultado.
2. Crea tres sets, pasando `names` al constructor de cada uno: `new HashSet<>(names)`, `new LinkedHashSet<>(names)`
   y `new TreeSet<>(names)`. Declara las tres variables como `Set<String>`.
3. Imprime cada set y su `size()`. Predicciones:
   - `HashSet`: escribe "orden impredecible" y qué elementos esperas.
   - `LinkedHashSet` y `TreeSet`: escribe el orden **exacto** que esperas. Ojo con `"beto"` en minúscula (§1.7).
4. **Quitar duplicados conservando el orden:** crea `List<Integer> codes = List.of(40, 10, 40, 30, 10, 20);` y
   obtén una `List<Integer>` **sin repetidos** y **en el orden de primera aparición** (esperado: `[40, 10, 30, 20]`),
   en **una sola línea**, usando constructores (§1.8). Pista: `new ArrayList<>( new ???<>(codes) )`.
5. **`TreeSet` con productos:** dentro de un `try/catch (ClassCastException e)`, crea un `Set<Product> tree = new TreeSet<>();`
   y agrega un producto. En el `catch`, imprime `e.getClass().getSimpleName()`. Predice: ¿falla al crear el
   `TreeSet` o al hacer el `add`?

**Preguntas:**
- **P5.1** ¿Por qué `"beto"` queda donde queda en el `TreeSet`?
- **P5.2** ¿Qué necesita `TreeSet` que `HashSet` no necesita? Pista: ¿cómo sabe quién va primero?
- **P5.3** Si no te dicen nada sobre el orden, ¿qué `Set` usarías por defecto y por qué?

---

### Tarea 6 — Aplicación: productos distintos de un pedido (avanzado · 50 min)

**Objetivo:** usar un `Set` en el dominio real, con encapsulación.
**Antes:** lee §1.8 (sobre todo la tabla de encapsulación).
**Archivos:** `src/Models/Order.java`, `src/Main.java`

**Problema:** el equipo de logística quiere saber **qué productos distintos** tiene un pedido, para preparar
el picking. No importa cuántas unidades, solo **cuáles**, **en el orden en que se agregaron** y **sin repetir**.

**En `Order`:**
1. Agrega `public Set<Product> getDistinctProducts()`.
2. Dentro:
   - crea un `Set<Product>` local con la implementación que **conserve el orden** de inserción (Tarea 5),
   - recorre `orderItems` con un for-each y agrega `item.getProduct()` al set,
   - devuelve el set **sin que se pueda modificar desde fuera**, eligiendo la forma que **conserva el orden** (§1.8, tabla).
3. Escribe un comentario de 1–2 líneas justificando las dos elecciones (implementación y forma de devolverlo).

**En `Main`:**
4. Después de los `addItem` existentes, agrega **otra vez** la misma laptop: `order.addItem(laptop, 1);`.
5. Crea un **nuevo** objeto **del mismo tipo** que `laptop` con el **mismo id** (id `1`). Ojo: en `Main`, `laptop`
   es un `PhysicalProduct`, no un `Laptop`. Agrégalo con `addItem(..., 1)`.
6. Imprime, con predicciones:
   - `order.getItemCount()`
   - `order.getDistinctProducts().size()`
   - `order.getDistinctProducts()`
7. Demuestra que no se puede modificar: `order.getDistinctProducts().add(mouse)` dentro de un
   `try/catch (UnsupportedOperationException e)`.
8. Comprueba que el total sigue siendo correcto (ahora cambia, porque agregaste items). Calcula a mano cuánto
   debería dar **antes** de ejecutar.

**Preguntas:**
- **P6.1** ¿Por qué aquí un `Set` y no una `List`?
- **P6.2** En el paso 4 agregaste **el mismo objeto** `laptop`. ¿Se habría eliminado el repetido **incluso sin**
  `equals`/`hashCode` en `Product`? ¿Y el del paso 5, que es un objeto **nuevo** con el mismo id?
- **P6.3** ¿Qué habría pasado con el orden si devolvías `Set.copyOf(...)`?

**Restricción:** **no** intentes todavía juntar las líneas repetidas de `Order` (laptop x3 + laptop x1 → laptop x4).
Eso es `Map` y es la lección 03.

---

## 4. Qué entregar

| Tarea | Archivos |
|-------|----------|
| 0 | `src/practical/sets/SetBasics.java` |
| 1 | `src/practical/sets/EqualsHashCodeLab.java` (columna 1 de la tabla) |
| 2 | `Product.java` (`equals`) + `EqualsHashCodeLab` (columna 2 + pruebas rápidas) |
| 3 | `Product.java` (`hashCode`, `id` final, `isValidValue`) + `EqualsHashCodeLab` (columna 3) |
| 4 | `src/practical/sets/EqualityTypeLab.java` + decisión final en `Product.equals` |
| 5 | `src/practical/sets/SetOrderLab.java` |
| 6 | `Order.java` (`getDistinctProducts`) + `Main.java` |

## 5. Criterios de salida (responder sin mirar, al final de la semana)

- ¿Qué diferencia hay entre `==` y `equals`? ¿Qué hace el `equals` heredado de `Object`?
- Explica la regla de oro `equals`/`hashCode` con la analogía de los cajones.
- ¿Por qué una clase con solo `equals` funciona en `List` y falla en `HashSet`?
- ¿Por qué `Product` se compara por `id` y un `Money` se compararía por todos sus campos?
- `HashSet`, `LinkedHashSet`, `TreeSet`: ¿cuándo cada uno?

**Siguiente lección:** `Map` (`HashMap`, `LinkedHashMap`, `TreeMap`), para eliminar las líneas repetidas de `Order`
con un `Map<Product, OrderItem>`. Usa **todo** lo de esta lección: las claves de un `HashMap` son un `HashSet` por dentro.
