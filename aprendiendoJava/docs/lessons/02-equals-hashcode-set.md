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


> ## 🔍 REVISIÓN TAREA 0 — 2026-10-04 · **9/10** · ✅ pasa a Tarea 1
> Historial: v1 8/10 → **v2 9/10**. Revisado ejecutando `SetBasics`.

**✅ Ya está bien:** los 10 pasos están, compila y corre. Acertaste **todas** las predicciones de valores
(`true`/`false`/`size`), incluido lo que devuelven `add` y `remove`, que era la deuda de la lección 01.
Y lo más importante: cuando el orden del paso 5 no coincidió, **escribiste `real` y `porque`** en vez de seguir.
Eso es justo el hábito que faltaba. P0.1 y P0.3 correctas.

**Tus predicciones contra la salida real:**

| Paso | Línea | Tu predicción | Salida real | |
|------|-------|---------------|-------------|---|
| 2 | `isEmpty()` / `size()` | `true` / `0` | `true` / `0` | ✅ |
| 3–4 | `add("Ana")` ×2 y `size()` | `true`, `false`, `1` | `true`, `false`, `1` | ✅ |
| 5 | `size()` | `4` | `4` | ✅ |
| 5 | `names` | `[Ana, Luis, Marta, ana]` | `[Marta, Ana, ana, Luis]` | ❌ con `porque` |
| 6 | `contains("Ana")` / `("ANA")` | `true` / `false` | `true` / `false` | ✅ |
| 7 | `remove("Luis")` / `("Pedro")` | `true` / `false` | `true` / `false` | ✅ |
| 7 | `names` | `[Marta, Ana, ana]` | `[Marta, Ana, ana]` | ✅ (usaste la salida del paso 5 👍) |
| 9 | for-each | "cada nombre en su línea" | `Marta` / `Ana` / `ana` | ⚠️ vago |
| 10 | `catch` | "no sé" | `UnsupportedOperationException` | ⚠️ con `porque` |

---

#### 🟠 IMPORTANTE 1 — ¿Por qué `[Marta, Ana, ana, Luis]`? (tu pregunta del paso 5)

Tienes razón en las dos cosas que notaste: **no hay orden lógico** y **siempre sale igual**. No es casualidad.

**Teoría:** un `HashSet` es por dentro un arreglo de "cajones" (*buckets*), 16 al empezar. Para guardar un
elemento:
1. Llama a `hashCode()` del String. Es un número calculado a partir de sus letras (siempre el mismo para el mismo texto).
2. Lo convierte en un número de cajón entre 0 y 15.
3. Lo pone en ese cajón. Si ya hay alguien, lo pone **detrás** en el mismo cajón.

Al imprimir, recorre los cajones **del 0 al 15**. Con tus datos (comprobado):
```
"Marta".hashCode() = 74114091  → cajón 1
"Ana".hashCode()   = 65972     → cajón 5
"ana".hashCode()   = 96724     → cajón 5   ← mismo cajón (colisión), entra detrás de "Ana"
"Luis".hashCode()  = 2379923   → cajón 7

cajón:  0    1       2  3  4    5            6    7     ... 15
             [Marta]           [Ana → ana]        [Luis]
imprime:     Marta,             Ana, ana,          Luis
```
Por eso el orden es estable (mismos textos → mismos cajones) pero no "lógico" (depende de un número que
no ves). **No lo memorices:** si el set crece, Java pasa a 32 cajones y el orden puede cambiar.
Si necesitas orden: `LinkedHashSet` (orden de inserción) o `TreeSet` (orden alfabético) — §1.7.

Fíjate también en que **el paso 5 pedía** escribir `// espero: no sé el orden, pero estos elementos: ...`.
Era una trampa a propósito: la predicción correcta era *no predecir el orden*.

**Profesionalmente:** un test que compara `set.toString()` con `"[Ana, Luis, ...]"` es un test frágil.
Se compara con `contains` o con `equals` contra otro `Set`.

---

#### 🟠 IMPORTANTE 2 — El mensaje del paso 8 está incompleto

Copiaste `cannot find symbol, variable names of type java.util.set<...>`. Te falta **la línea que importa**:
```
error: cannot find symbol
        names.get(0);
             ^
  symbol:   method get(int)                       ← QUÉ no encontró
  location: variable names of type Set<String>    ← DÓNDE lo buscó
```
Léelo como una frase: "no encontré el **método `get(int)`** en el tipo **`Set<String>`**". El compilador mira
el tipo de la **izquierda** (`Set`), no el objeto real (`HashSet`). Es lo mismo que viste con polimorfismo:
la variable decide qué métodos puedes llamar.

**Profesionalmente:** `symbol` + `location` es lo primero que buscas en cualquier error de compilación
de Spring Boot. Acostúmbrate a copiarlas completas.

---

#### 🟡 MENOR 3 — P0.2 y el paso 10: dar la razón concreta

- **P0.2:** "implementa una interfaz diferente" es circular (es como decir "no tiene `get` porque no lo tiene").
  Tu segunda frase sí es la respuesta: **no hay posición estable**. Acabas de verlo: si `get(0)` existiera hoy
  daría `"Marta"`, y tras agregar elementos podría dar otro. Un método así sería una trampa.
- **Paso 10:** tu `porque` explica *qué es* la excepción, pero no *por qué saltó*. Razón: `Set.of(...)` crea un
  set **inmutable**. Es el mismo caso que `List.of` y `List.copyOf` de la lección 01 (Tarea 4).
- **P0.3:** correcta. Para la Tarea 1 conecta esto: `"Ana".equals("ana")` es `false` y sus `hashCode` también
  son distintos (65972 vs 96724). El set usa **esos dos métodos** para decidir si "ya está".

---

#### 🟡 MENOR 4 — Formato

- Si aciertas, la plantilla pide `// espero: true ✅`. No marcaste ninguna, así que no se sabe si comprobaste.
- Paso 9: con la salida del paso 7 ya podías predecir exactamente `Marta`, `Ana`, `ana`.
- Paso 7: el enunciado pedía guardar también el de `"Pedro"` en un `boolean` (`removedPedro`).
- Los comentarios dicen `//tarea1`, `//tarea2`… pero son **pasos**. Y los separadores `"8---"` / `"9---"`
  están desfasados (el for-each es el paso 9 y `Set.of` el 10).
- `String args[]` funciona, pero en Java se escribe `String[] args` (el tipo es "arreglo de String").

---

**Para llegar a 10/10:**
- [ ] paso 5: `// espero: no sé el orden, pero estos elementos: Ana, Luis, Marta, ana` (y deja tu `porque`)
- [x] paso 8: copiar el error completo, con `symbol` y `location`
- [x] P0.2: quitar lo circular, quedarte con "no hay posición estable" + tu ejemplo
- [x] paso 10: `porque` = `Set.of` es inmutable
- [ ] `✅` en las predicciones acertadas, predicción concreta en el paso 9

---

#### 🔁 v2 — 2026-10-04 · **9/10** · ✅ puedes pasar a la Tarea 1

**Corregido:**
- ✅ Paso 10: "es un objeto inmutable". Esa es la razón concreta.
- ✅ P0.2: ya no es circular. "No hay índice posicional" es la respuesta.
- ✅ Paso 8: ahora aparece `symbol: method get(int)`, que es lo que importa.
- ✅ Marcas `✅` en las predicciones acertadas.

**Sigue pendiente (no bloquea la Tarea 1):**
- ❌ **Paso 5:** el `espero` sigue siendo `[Ana,Luis,Marta,ana]`. Es el único punto de concepto que queda.
  La predicción correcta en un `HashSet` es **no predecir el orden**:
  `// espero: no sé el orden, pero estos elementos: Ana, Luis, Marta, ana`. Deja tu `salio` y tu `porque`.
  Con la explicación de los cajones de arriba, ya puedes cambiar el "la verdad no sé" por la razón real.
- 🟡 Paso 8: pusiste ✅ en `// names.get(0);`. Ahí no hay predicción. Era un error esperado, no un acierto.
  Al mensaje le falta `location: variable names of type Set<String>`, que es la otra mitad.
- 🟡 Paso 9: `espero cada nombre en su línea ✅` sigue siendo vago. Podías escribir `Marta`, `Ana`, `ana`.
- 🟡 Sin tocar: `removedPedro`, los comentarios `//tarea1`… (son pasos), los separadores `8---`/`9---` y `String args[]`.

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


> ## 🔍 REVISIÓN TAREA 1 — 2026-10-04 · **8/10** · 🟡 casi → pasa a Tarea 2
> Historial: v1 6/10 → **v2 8/10**. Revisado ejecutando `EqualsHashCodeLab`. `Product.java` sin tocar ✅.

**✅ Ya está bien:** pasos 1–4, 6 y 8. Las 3 laptops están bien creadas y declaradas como `Product`.
Predijiste bien `==` y `hashCode`. Cuando fallaste (`equals`, `contains(D)`) **escribiste el `real` y te
detuviste**. P1.1 y P1.3 van por buen camino.

**Tus predicciones contra la salida real:**

| Paso | Línea | Tu predicción | Salida real | |
|------|-------|---------------|-------------|---|
| 2 | `A == B` | `false` | `false` | ✅ |
| 3 | `A.equals(B)` | `true` | `false` | ❌ con `porque` |
| 4 | `A.hashCode() == B.hashCode()` | `false` | `false` | ✅ |
| 5 | `list.contains(B)` | — | **no existe en el código** | ❌ |
| 6 | `set.add(A/B/C)` | `true`, `true`, `true` | `true`, `true`, `true` | ✅ |
| 7 | `set.size()` | — | **no existe en el código** (imprimiste `set`) | ❌ |
| 8 | `set.contains(D)` | `true` | `false` | ❌ `porque` confuso |

---

#### ❌ CRÍTICO 1 — Faltan los pasos 5 y 7… pero la tabla los tiene llenos

**Qué pasa:** en el código no está la `List` del paso 5 ni el `set.size()` del paso 7. Aun así, la tabla dice
`list.contains(B) → false` y `set.size() → 3`. Esos dos valores **no salieron de ejecutar nada**: los dedujiste.
Puede que acertaras, pero la tabla es para registrar **lo que pasó**, no lo que creemos que pasó.

**Por qué importa justo aquí:** las Tareas 2 y 3 vuelven a ejecutar **esta misma clase sin cambiarla** y comparan
las columnas. Y `list.contains(B)` es **la fila más importante** de la lección: en la Ronda 2 es la que cambia
mientras el set **no** cambia. Si la línea no está en el código, la Ronda 2 no te lo va a mostrar.

**Profesionalmente:** un reporte de pruebas con resultados "deducidos" es el clásico "en mi máquina funciona".
Si no lo ejecutaste, se escribe `no ejecutado`.

**Qué hacer:** agrega los pasos 5 (`List<Product> list`, solo `laptopA`, imprimir `list.contains(laptopB)`) y
7 (`set.size()`), con su `espero`. Puedes dejar el `println(set)` como extra.

---

#### 🟠 IMPORTANTE 2 — P1.2: falta decir **qué método de qué clase** (deuda de la lección 01)

**Qué pasa:** respondiste "equals al final hace un `this == o` pero con pasos extras". La mitad es correcta.
Pero la pregunta pide tres cosas y no nombras ninguna:
1. **¿Qué clase?** `Object`. Ni `Laptop` ni `Product` escriben `equals`, así que Java sube por la herencia
   `Laptop → Product → Object` y usa el primero que encuentra.
2. **¿Qué método?** `Object.equals(Object obj)`.
3. **¿Qué compara?** La **referencia** (identidad), exactamente igual que `==`.

Y **no** tiene "pasos extras". Es literalmente `return (this == obj);` (mira §1.1). Por eso `A.equals(B)` y
`A == B` dan lo mismo en la Ronda 1.

**Truco para comprobarlo tú:** en IntelliJ, Ctrl+clic sobre `equals` en `laptopA.equals(laptopB)`. Te lleva a
`Object.java`. Eso responde "qué clase" sin adivinar.

---

#### 🟠 IMPORTANTE 3 — Paso 8: el `porque` de `contains(D)` mezcla dos cosas

**Qué pasa:** escribiste "usa internamente un `this == o`… pero por contenido". Es contradictorio: `==` es
justo lo **contrario** de comparar por contenido. Además le falta la etiqueta `porque:`.

**Lo que realmente pasa (§1.3)** cuando haces `set.contains(laptopD)`:
```
1) cajón = laptopD.hashCode()   → hashCode de Object: un número distinto para cada objeto
                                  → va a un cajón donde NO está laptopA
2) en ese cajón no hay nadie igual → false
   (ni siquiera llega a llamar a equals con laptopA)
```
Es decir, `HashSet` falla **dos veces**: el cajón es incorrecto (`hashCode`) y, aunque acertara el cajón,
`equals` compararía referencias. Arreglar solo uno no basta. Eso es lo que vas a ver en las Rondas 2 y 3.

---

#### 🟡 MENOR 4 — Detalles

- **"Siempre es mejor sobreescribir `equals`"** (paso 3): no siempre. Solo cuando dos objetos distintos pueden
  representar **lo mismo** (un producto con el mismo `id`). Un `Order` en proceso o una conexión a base de datos
  quizás sí deban compararse por identidad. Esa decisión es §1.4.
- **`println(set)` marcado ✅:** tu predicción tenía `id="1"`, `laptop` en minúscula y "orden diferente". La salida
  real fue `id=1`, `'Laptop'` y justo en orden A, B, C. La idea (3 productos, dos repetidos) era correcta, pero
  no exacta. Que saliera en orden **es casualidad**: el `hashCode` de `Object` cambia en cada ejecución.
- **P1.1:** correcta. El "creo!!" sobra: §1.1 lo dice así. `==` en objetos compara **referencias**, es decir
  si las dos variables apuntan al mismo objeto.
- `String args[]` → `String[] args` (igual que en la Tarea 0).

---

**Para llegar a 10/10:**
- [x] agregar el paso 5 (`list.contains(laptopB)`) y el paso 7 (`set.size()`) con `espero`, ejecutar y
      llenar la tabla **con la salida real**
- [ ] P1.2: "`Object.equals`, porque ni `Laptop` ni `Product` lo sobreescriben; compara referencias"
- [ ] paso 8: `porque` con cajones (`hashCode` de `Object`), sin "`this == o` por contenido"
- [ ] matizar el "siempre es mejor sobreescribir"

---

#### 🔁 v2 — 2026-10-04 · **8/10** · 🟡 casi → puedes pasar a la Tarea 2

**Corregido:**
- ✅ **Paso 5 agregado** con predicción (`false` ✅). Ahora la tabla sale de la ejecución real, y la Ronda 2
  sí te va a mostrar el cambio en `list.contains(B)`.
- ✅ **`set.size()` agregado** (`3`, acertado).
- ✅ **`porque` del paso 3:** "se hereda de `Object` y `Object` solo hace `this == o`". Esa es la respuesta.
- ✅ Separadores `"N--------Paso"`: ahora la salida se lee fácil.

**Sigue pendiente:**
- 🟠 **P1.2 sin cambiar:** abajo sigue diciendo "con pasos extras". La respuesta buena ya la escribiste en el
  paso 3. Llévala a P1.2: "`Object.equals`, porque ni `Laptop` ni `Product` lo sobreescriben; compara referencias".
- 🟠 **`porque` del paso 8:** ahora nombra `Object` (bien), pero solo explica la mitad (`equals`). Falta la otra:
  el `hashCode` de `Object` manda a `laptopD` a **otro cajón**. No lo arregles todavía: la **P2.2 de la Tarea 2**
  te pide justo eso, y vas a verlo con tus propios ojos. Vuelve aquí después.
- 🟡 `set.size()` quedó bajo `"8--------Paso"` y después de crear `laptopD`. Va en el paso 7.
- 🟡 Import sin usar: `javax.annotation.processing.SupportedSourceVersion` (seguro lo metió el autocompletado
  del IDE). Bórralo.
- 🟡 Sigue "siempre es mejor sobreescribir" y `String args[]`.

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


> ## 🔍 REVISIÓN TAREA 2 — 2026-10-05 · **7/10** · 🟡 casi → corrige 2 cosas antes de la Tarea 3
> Historial: **v1 7/10**. Revisado compilando y ejecutando `EqualsHashCodeLab` con tu `Product.java`.

**✅ Ya está bien:** `@Override` + firma `equals(Object o)` exacta. Usaste `instanceof` y comparas **solo** el `id`
con `.equals` (no con `==`). **No** agregaste `hashCode` ✅. No cambiaste el código del experimento ✅.
La columna 2 de la tabla **coincide con la salida real** (la ejecuté: `true` en `A.equals(B)` y `list.contains(B)`,
todo el set igual que en la Ronda 1). Las 3 pruebas rápidas que hiciste dan lo que predijiste.

**Por paso:**

| Paso | Qué pedía | Estado |
|------|-----------|--------|
| 1 | `@Override` + `equals(Object o)` | ✅ |
| 2 | receta de **5 pasos** con `instanceof` | 🟡 falta el paso 1 (`this == o`); sobra `res` |
| 3 | comparar solo `id`, sin `==` | ✅ (ver 🟡 4: `null`) |
| 4 | sin `hashCode` | ✅ |
| 5 | columna 2 como `espero → real` | 🟡 solo escribiste el `real` |
| Pruebas | 4 líneas, la 1.ª es `equals(laptopA)` (reflexiva) | 🟡 3 de 4: hiciste `equals(laptopB)` |
| P2.1 | qué filas cambiaron y cuáles **no** | 🟡 falta "cuáles no" y por qué |
| P2.2 | por qué el set acepta a B (cajones) | ❌ **la pregunta clave**, no responde el "por qué" |
| P2.3 | por qué `Object` y no `Product` | 🟡 falta la palabra clave: **sobrescribir** vs **sobrecargar** |

---

#### ❌ CRÍTICO 1 — P2.2: describes cómo funciona el set, pero no **por qué falla**

**Qué pasa:** tu respuesta explica el mecanismo general ("busca el cajón por el hashCode, luego itera con
`equals`"). Esa descripción está **bien**. Pero la pregunta es: si `A.equals(B)` ya es `true`, ¿por qué el set
**igual** acepta a `laptopB`? Tu respuesta no lo dice. Además mezclas dos cosas: "el contains solo es una iteración
de `equals` sobre cada objeto del set". Eso es `List.contains`, **no** `HashSet.contains`.

**Teoría (§1.3):** la respuesta está en **tu propia tabla**, fila 3: `A.hashCode() == B.hashCode()` → `false`.
Tú **no** escribiste `hashCode`, así que se usa el de `Object`, que da un número distinto a cada objeto (`new`).
```
 set.add(laptopB):
   1) cajón = laptopB.hashCode()   → hashCode de Object → un cajón DISTINTO al de laptopA
   2) en ese cajón no hay nadie    → nunca se llama a equals(laptopA)
   3) add devuelve true            → B entra como "nuevo"
```
Tu `equals` está bien escrito… pero el `HashSet` **ni siquiera llega a usarlo**.

| | `List.contains(B)` | `HashSet.add(B)` / `contains(D)` |
|---|---|---|
| ¿Usa `hashCode`? | **no** | **sí**, primero |
| ¿Usa `equals`? | sí, con cada elemento | sí, pero **solo dentro del cajón** |
| Ronda 2 | `true` ✅ (solo necesita `equals`) | falla (el cajón es incorrecto) |

**Ejemplo con otros datos:** en una biblioteca, el libro "Cien años de soledad" está en el estante **G**
(García Márquez). Si buscas en el estante **M** porque calculaste mal la letra, no lo encuentras, **aunque**
sepas reconocer el libro perfectamente al verlo. Reconocerlo = `equals`. Elegir el estante = `hashCode`.

**Qué hacer:** reescribe P2.2 en 2–3 líneas. Debe nombrar: (1) el `hashCode` de `Object`, (2) que A y B caen en
**cajones distintos** (fila 3 de tu tabla), (3) que por eso `equals` **nunca se llama**.
Después vuelve al `porque` del **paso 8 de la Tarea 1** y complétalo con la misma idea (estaba pendiente).

---

#### 🟠 IMPORTANTE 2 — P2.3: falta la idea de **sobrecarga**

**Qué pasa:** "se puede comparar con cualquier objeto" es cierto, pero no es la razón principal.

**Teoría (§1.2, error común):**
- `equals(Object o)` **sobrescribe** (*override*) el método de `Object`. Es el que llaman `List`, `HashSet`, `Map`.
- `equals(Product o)` sería **otro método** con otro parámetro: una **sobrecarga** (*overload*). `List` y `HashSet`
  llaman a `equals(Object)`, así que tu método nunca se usaría.

Lo comprobé con una clase de prueba que tiene `equals(Q o)`:
```
q1.equals(q2)                 → true    (llama a TU equals(Q), porque q2 es Q)
List.of(q1).contains(q2)      → false   (List llama a equals(Object) → el de Object → ==)
```
Es la misma trampa que `remove(int)` vs `remove(Object)` de la lección 01 (deuda P2.2): mismo nombre,
distinto parámetro → métodos distintos. Y `@Override` es lo que te protege: con `equals(Product)` el compilador
da error.

**Qué hacer:** reescribe P2.3 usando las palabras **sobrescribir**, **sobrecarga** y **`@Override`**.

---

#### 🟡 MENOR 3 — La receta de 5 pasos, incompleta

**Qué pasa:**
```
receta (§1.2)                 tu código
1. this == o → true            ❌ falta
2. o == null → false           ✅ lo cubre instanceof
3. ¿tipo correcto?             ✅ instanceof Product
4. convertir                   ✅ (Product) o
5. comparar campos             ✅ id
```
Sin el paso 1 el resultado sigue siendo correcto: es un atajo de rendimiento. Pero la tarea pedía la receta
completa, y es lo que verás en cualquier `equals` profesional.

**Sobra `boolean res = false`:** nunca cambia de valor. Es igual a `return false;` directo. Una variable que
siempre vale lo mismo obliga al lector a buscar dónde cambia… y no cambia.

**Mejorable (opcional): *pattern matching*** (lección 01, Tarea 3, P3.3). Con `o instanceof Product other`
te ahorras el cast y queda `other.getId()` o `other.id`.

---

#### 🟡 MENOR 4 — ¿Y si el `id` es `null`?

Tu constructor **acepta** `id = null`. Lo comprobé con una copia de tu `equals`:
```
productoConIdNull.equals(laptopA)  → false                    (1.equals(null) → false)
laptopA.equals(productoConIdNull)  → NullPointerException 💥  (null.equals(...))
```
Rompe la regla "**nunca** lanza excepción" y también la **simétrica** (una dirección da `false`, la otra explota).
Por eso §1.2 recomienda `Objects.equals(campo, otro.campo)`, que nunca lanza NPE.
No es urgente: en la Tarea 3 el `id` será `final`, y lo correcto será **validarlo en el constructor** (como
el precio). Pero cambia a `Objects.equals` ahora: es una línea.

---

#### 🟡 MENOR 5 — Detalles

- **Paso 5:** la columna 2 debía ser `espero → real` (por ejemplo `true → true`). Solo está el `real`. Así no sé
  si predijiste bien `set.add(B) → true` (la fila sorpresa). Escribe en P2.1 cuál fila **no** esperabas.
- **Prueba reflexiva:** la primera prueba debía ser `laptopA.equals(laptopA)`. Hiciste `laptopA.equals(laptopB)`,
  que ya estaba en el paso 3. Agrega la reflexiva.
- **P2.1:** está bien lo que cambió (`equals` y `list.contains`). Falta la otra mitad: "**no** cambiaron las filas
  del set (`add(B)`, `size`, `contains(D)`) ni la de `hashCode`". Esa segunda mitad es la que lleva a P2.2.
- **Pendientes de la Tarea 1 que siguen igual:** P1.2 sigue sin decir **`Object.equals`**; import
  `SupportedSourceVersion` sin usar; `String args[]`; `set.size()` bajo el paso 8.

---

**Conceptos que necesitas para cerrar la tarea** (repasa en este orden):

| # | Concepto | Dónde | Te sirve para |
|---|----------|-------|---------------|
| 1 | `List.contains` usa **solo** `equals`; `HashSet` usa **primero** `hashCode` y luego `equals` | §1.3, último párrafo + "¿Y `List`?" | P2.1, P2.2 |
| 2 | El `hashCode` de `Object`: distinto para cada `new` | §1.3 + fila 3 de tu tabla | P2.2, paso 8 de la Tarea 1 |
| 3 | Regla de oro: `equals` true → mismo `hashCode` | §1.3 | P2.2 (y P3.1 de la Tarea 3) |
| 4 | Sobrescribir (*override*) vs sobrecargar (*overload*) | §1.2 error común; lección 01 P2.2 | P2.3 |
| 5 | Receta de 5 pasos y `Objects.equals` (seguro con `null`) | §1.2 | Menor 3 y 4 |

**Para llegar a 10/10:**
- [ ] P2.2 reescrita: `hashCode` de `Object` → cajones distintos → `equals` nunca se llama
- [ ] completar el `porque` del paso 8 de la Tarea 1 con la misma idea
- [ ] P2.3 con **sobrescribir / sobrecarga / `@Override`**
- [ ] P2.1 con la mitad "qué **no** cambió"
- [ ] `equals`: agregar `if (this == o) return true;`, quitar `res`, usar `Objects.equals`
- [ ] agregar la prueba `laptopA.equals(laptopA)` con su `espero`
- [ ] (opcional) *pattern matching* `o instanceof Product other`

---

#### 🔁 v2 — 2026-10-05 · **7/10** · 🟡 el código mejoró; falta entender P2.2 (te lo explico abajo)

**Corregido:**
- ✅ Quitaste `res`: ahora es `return false;` directo.
- ✅ `equals` ya **no lanza `NullPointerException`** si algún `id` es `null`. Revisar los dos lados a mano
  (`o.getId() == null || this.id == null`) es una alternativa válida a `Objects.equals`.

**Sigue pendiente (sin cambios):** P2.1 (qué **no** cambió), P2.3 (sobrescribir/sobrecarga), prueba reflexiva
`laptopA.equals(laptopA)`, `espero` en la columna 2, paso 8 de la Tarea 1, import sin usar, `String args[]`.

---

#### 🟠 NUEVO — Sin `this == o`, un producto con `id` `null` **no es igual a sí mismo**

En la v1 te dije que el paso 1 de la receta era "solo un atajo de rendimiento". **Con tu código nuevo ya no es
solo eso.** Lo comprobé con una copia de tu `Product` y un `Laptop` con `id` `null`:
```
N.equals(N)              → false   ❌ rompe la regla reflexiva ("yo soy igual a mí mismo")
lista con N: contains(N) → false   ❌ la lista no encuentra un objeto que SÍ tiene dentro
lista con N: remove(N)   → false   ❌ y tampoco lo puede borrar (size sigue en 1)
```
**Por qué:** tu `if (... this.id == null) return false;` se ejecuta **antes** de preguntar si es el mismo objeto.
**Qué hacer:** pon `if (this == o) return true;` como **primera** línea. Con eso `N.equals(N)` da `true`.
(Así ves por qué la receta tiene ese orden: el paso 1 también protege la regla reflexiva.)

---

#### 📖 P2.2 explicado — "¿por qué el set acepta a `laptopB`?"

Pediste la explicación. Primero, lo que **ya entiendes bien**. Tu descripción del mecanismo es correcta:
> "busca por el hashCode → encuentra el cajón → dentro del cajón compara con `equals`"

Lo que falta es **aplicarlo a A y B con sus números reales**. Lo ejecuté y salió esto (tus números pueden ser otros):
```
A.hashCode() = 1550089733
B.hashCode() =  865113938      ← distintos (es la fila 3 de tu tabla: false)
```

**La pieza que falta:** `hashCode()` y `equals()` son **dos métodos separados**. Cuando escribiste `equals`,
`hashCode` **no se enteró**. Sigue siendo el de `Object`, que **no mira el `id` ni ningún dato**: da un número
propio para cada objeto creado con `new`.

**Paso a paso, con números:**
```
set.add(laptopA)
   1) cajón = A.hashCode() = 1550089733
   2) cajón 1550089733 vacío            → guarda A ahí → true

set.add(laptopB)
   1) cajón = B.hashCode() = 865113938   ← ¡OTRO cajón!
   2) cajón 865113938 vacío             → no hay nadie con quien comparar
                                         → tu equals NO se llama
   3) guarda B ahí                      → true
```
El set **nunca pone a A y B frente a frente**. Tu `equals` diría "son iguales", pero nadie le pregunta.

**Y la `List`, ¿por qué sí funciona?** Porque `List.contains` **no tiene cajones**: compara con `equals` contra
**todos** los elementos, uno por uno. Lo comprobé poniendo un `println` dentro de tu `equals`:
```
list.contains(B)   →  ">> equals llamado..."  aparece 1 vez   → true
set.add(B)         →  no aparece NADA                          → true (lo agrega)
```

**Ejemplo con otros datos (un hotel):**
- La recepcionista asigna la habitación según el **número de pasaporte** (eso es `hashCode`).
- Para saber si un huésped "ya está", entra a **esa** habitación y mira a la cara (eso es `equals`).
- Ana llega dos veces, con **dos pasaportes distintos**. La recepcionista la manda a la habitación 12 y después a
  la 40. En la 40 no hay nadie, así que la registra otra vez. Reconocería a Ana al verla, pero **nunca entra a
  la 12**.
- Arreglo: que la habitación dependa de algo que **no cambia entre las dos Anas** (su DNI = el `id`). Eso es
  la **Tarea 3**: `hashCode` con el mismo campo que `equals`.

**Mini-experimento para verlo tú (15 min):**
1. En `Product.equals`, como **primera** línea, agrega temporalmente:
   `System.out.println("   >> equals llamado con " + o);`
2. Crea `src/practical/sets/HashSpyLab.java` (paquete `practical.sets`, con `main`). Así no tocas
   `EqualsHashCodeLab`.
3. Crea dos `Laptop` con id `7`, nombre `"Mouse"`, precio `"20"`, peso `"1"`. Llámalos `m1` y `m2`.
4. Imprime `m1.hashCode()` y `m2.hashCode()` (los dos números). Antes escribe `// espero: iguales / distintos`.
5. Crea una `List<Product>`, agrega `m1` e imprime `contains(m2)`. Antes escribe
   `// espero: ¿aparece ">> equals llamado"? sí/no`.
6. Crea un `HashSet<Product>`, agrega `m1` e imprime `add(m2)`. Haz la misma predicción del `>>`.
7. **Borra el `println` de `Product.equals`** cuando termines.
8. Reescribe P2.2 en 3 líneas. Usa: **`hashCode` de `Object`**, **cajones distintos**, **`equals` nunca se llama**.

**Corrección mía:** en la revisión de la Tarea 1 escribí que el `hashCode` de `Object` "cambia en cada ejecución".
**No siempre**: en mi máquina salió el mismo número dos veces seguidas. Lo importante es otra cosa: **no depende
de los datos**. Dos `new` con el mismo `id` dan números distintos.

---

**Para llegar a 10/10 (actualizado):**
- [ ] `if (this == o) return true;` como primera línea de `equals` (protege la regla reflexiva)
- [ ] mini-experimento `HashSpyLab` → P2.2 reescrita con tus palabras
- [ ] completar el `porque` del paso 8 de la Tarea 1 con la misma idea
- [ ] P2.3 con **sobrescribir / sobrecarga / `@Override`**
- [ ] P2.1 con la mitad "qué **no** cambió"
- [ ] prueba `laptopA.equals(laptopA)` con su `espero`

---

#### 🔁 v3 — 2026-10-05 · **8/10** · ✅ entendiste P2.2 → puedes pasar a la Tarea 3

Revisado ejecutando `HashSpyLab`, `EqualsHashCodeLab` y `Main` (sigue dando `3846` ✅).

**Corregido:**
- ✅ **`if (this == o) return true;` como primera línea.** Ahora un producto con `id` `null` sí es igual a sí mismo.
- ✅ **Prueba reflexiva** `laptopA.equals(laptopA)` → `true`, predicha bien.
- ✅ **Mini-experimento `HashSpyLab` hecho.** La salida real lo demuestra:
  ```
  1550089733 / 865113938        ← hashCode distintos (predijiste bien)
  >> equals llamado con ...     ← sale en list.contains(m2)
  true
  true                          ← set.add(m2): SIN ">> equals llamado" antes
  ```
- ✅ **P2.2 ahora es correcta.** "`list.contains` usa `equals`… A y B tienen hashCode diferentes, entonces nunca
  se encuentran o evalúan". Esa es la idea. Solo un matiz: "el set **no usa** el `equals`" → mejor "**no llega a**
  usar el `equals`". El set **sí** usa `equals`, pero solo dentro del mismo cajón. En la Tarea 3 lo vas a ver:
  cuando A y B compartan cajón, aparecerá el `>> equals llamado` en `set.add(B)`.

**Bonus que te regaló la ejecución:** el paso 7 ahora imprimió `[id=2, id=1, id=1]`. En la Tarea 1 salió
`[id=1, id=1, id=2]`. Mismo código, otro orden. Es la prueba de que **`HashSet` no garantiza orden** (§1.7, Tarea 5).

---

**Antes de empezar la Tarea 3 (obligatorio):**
- [ ] 🟠 **borra el `println(" >> equals llamado...")` de `Product.equals`** (paso 7 del mini-experimento). Si lo
      dejas, ensucia la salida de `Main` y de todas las clases que comparen productos.
      *(Truco opcional: déjalo hasta haber ejecutado la columna 3 de la Tarea 3. Ahí verás el `>>` aparecer en
      `set.add(B)`. Después lo borras.)*

**Deuda que pasa a la Tarea 3 (no bloquea):**
- [ ] 🟠 **P2.3 sin cambios en 3 revisiones.** Te falta la idea de **sobrecarga**. Responde esta pregunta concreta:
      "si escribo `public boolean equals(Product o)` **sin** `@Override`, ¿qué método llama `list.contains`, el mío
      o el de `Object`?, ¿por qué?". Pista: lección 01, `remove(int)` vs `remove(Object)`.
- [ ] 🟡 P2.1: agrega "no cambiaron las filas del set ni la de `hashCode`".
- [ ] 🟡 `HashSpyLab`: faltan los `real` / ✅, y la predicción del paso 6 debía decir si aparece el `>>` (no solo `true`).
- [ ] 🟡 paso 8 de la Tarea 1: copia ahí tu nueva idea de P2.2 (hashCode distintos → nunca se evalúan).
- [ ] 🟡 import `SupportedSourceVersion` sin usar · `String args[]` → `String[] args` (ahora también en `HashSpyLab`).

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
