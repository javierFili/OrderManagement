# Lección 04 — Ordenar: `Comparable` y `Comparator`

> Ruta: Fase 3 (Collections), semana 4 del roadmap.
> Viene de: Lección 03 (`Map`). Prepara: Lección 05 (Java moderno) y, más adelante, Streams (`sorted`) y Spring
> (`Sort`, `Pageable`).
> Todos los comportamientos descritos aquí fueron comprobados ejecutando código en Java 21.
> Las salidas que ves en los ejemplos (`// → ...`) son **reales**, copiadas del terminal.

---

## Cómo usar esta lección

Igual que la lección 03: está escrita **desde cero** y no supone que recuerdes nada.

Cada sección tiene la misma forma:

| Parte | Qué es |
|-------|--------|
| 📖 **Explicación** | la idea en palabras simples |
| 💻 **Ejemplo** | código pequeño con su salida real |
| 🧪 **Pruébalo** | un experimento corto que **tú** escribes y ejecutas (2–5 min). **No te lo saltes** |
| ✅ **¿Lo entendiste?** | 2 o 3 preguntas. La respuesta está escondida debajo: piensa primero y después ábrela |

**Plan de lectura (unas 2 h en total, en 2 o 3 sesiones):**

| Sesión | Lee | Tiempo |
|--------|-----|--------|
| 1 | Secciones 0 (repaso) + 1 + 2 + 3 + 4 | 50 min |
| 2 | Secciones 5 + 6 + 7 | 45 min |
| 3 | Secciones 8 + 9 + 10 + 11 + 12 + 13 | 35 min |

Después vienen las **tareas** (sección 14). Están todas publicadas, pero hazlas **una por vez, en orden**.

**Dónde hacer los 🧪:** crea **un solo archivo** `src/practical/sorting/SortPlayground.java` (paquete nuevo
`practical.sorting`) con un `main`. Ahí van todos los experimentos, uno debajo del otro, con un separador:
```java
System.out.println("---- 🧪 Sección 2 ----");
```

**La plantilla de siempre:**
```java
System.out.println(algo);
// espero: ...
// real:   ...   ← solo si fallaste
// porque: ...   ← solo si fallaste
```

**Sintaxis nueva:** esta lección **no usa lambdas (`->`)**. Un comparador se escribe como una **clase normal**
que implementa una interfaz, como ya hiciste con `Shippable` o `Downloadable`. La única excepción es la sección 11, que está
marcada como **opcional** y nunca se pide en las tareas.

**Si algo no se entiende:** pregúntame por el número de sección ("no entiendo la sección 7.2").

---

## 0. Repaso: lo que necesitas de las lecciones anteriores

Ordenar usa cinco cosas que ya viste. Léelas aunque creas que las sabes: las cinco vuelven a salir.

### 0.1 `compareTo` de `BigDecimal` (lecciones 01 y 03)

Ya lo usas para comparar precios:
```java
precio.compareTo(otroPrecio) > 0    // precio es MAYOR
precio.compareTo(otroPrecio) < 0    // precio es MENOR
precio.compareTo(otroPrecio) == 0   // valen lo mismo
```
Siempre `> 0` / `< 0`, **nunca** `== 1` / `== -1` (revisión de la Tarea 2 de la lección 03). En esta lección
vas a ver **por qué** de verdad (sección 4).

### 0.2 Lo que un método **devuelve** vs lo que **hace**

- `map.put(k, v)` **hace** algo (guarda) y **devuelve** otra cosa (el valor viejo).
- `set.add(x)` **hace** algo (agrega) y **devuelve** un `boolean`.

Ordenar es el caso extremo: `lista.sort(...)` **hace** mucho (reordena la lista) y **no devuelve nada**
(`void`). Ver sección 3.

### 0.3 Copia vs vista no modificable (lecciones 02 y 03)

- `new ArrayList<>(otra)` → **copia** que sí se puede modificar.
- `List.of(...)`, `List.copyOf(...)` → no se pueden modificar.
- `Collections.unmodifiableList(lista)` → **vista** de solo lectura de la original.

Ordenar **modifica** la lista (cambia las posiciones). Así que ordenar una lista no modificable falla (sección 3).

### 0.4 `TreeSet` / `TreeMap` con tus clases (lecciones 02 y 03)

En la Tarea 5 de la lección 02 viste esto:
```java
TreeSet<Product> set = new TreeSet<>();
set.add(laptop);     // → ClassCastException
```
`TreeSet` necesita **saber comparar** los elementos para mantenerlos ordenados, y `Product` no sabía. En esta
lección lo resuelves (sección 5).

### 0.5 Mismo `equals` = mismo elemento en un `Set`

Un `HashSet` usa `equals`/`hashCode` para decidir si dos elementos son "el mismo". Un `TreeSet` **no** usa
`equals`: usa la comparación. Esto tiene una trampa grande (sección 8).

---

## 1. El problema: ¿para qué ordenar?

### 📖 Explicación

Casi toda pantalla de un backend muestra datos **ordenados**:
- el catálogo, por precio ("de menor a mayor") o por nombre;
- los pedidos de un cliente, del más nuevo al más viejo;
- un ranking: los productos más vendidos.

Una `List` guarda los elementos en el orden en que los agregaste. Para verlos en **otro** orden, la ordenas.

La pregunta clave de toda la lección es: **¿cómo sabe Java cuál va primero?**
- Para números y textos, Java **ya sabe** (sección 2).
- Para **tus** clases (`Product`, `Customer`), **tú** se lo tienes que decir. Hay dos formas:

| Forma | Qué es | Cuántas puede haber | Analogía |
|-------|--------|---------------------|----------|
| **`Comparable`** (sección 5) | el orden **natural**, escrito **dentro** de la clase | **una** por clase | el orden en que el libro se guarda en la biblioteca (por código) |
| **`Comparator`** (sección 6) | un orden **extra**, escrito en **otra** clase | **todas** las que quieras | las distintas listas que imprime el bibliotecario: por autor, por año, por título |

---

## 2. Puente desde PHP

### 📖 Explicación

En PHP ya ordenas todo el tiempo:
```php
sort($nombres);                                   // ordena valores
rsort($nombres);                                  // al revés
usort($productos, fn($a, $b) => $a->price <=> $b->price);   // con tu propia regla
ksort($precios);                                  // array asociativo, por clave
```
El operador **`<=>`** (*spaceship*) devuelve `-1`, `0` o `1`. **Esa es exactamente la idea de Java**: comparar
dos cosas y devolver un número cuyo **signo** dice cuál va primero (sección 4).

### Tabla de traducción (guárdala)

| PHP | Java | Sección |
|-----|------|---------|
| `sort($a)` | `Collections.sort(lista)` o `lista.sort(null)` | Sección 3 |
| `rsort($a)` | `lista.sort(Comparator.reverseOrder())` | Sección 3 |
| `$a <=> $b` | `a.compareTo(b)` | Sección 4 |
| `usort($a, fn(...) => ...)` | `lista.sort(new MiComparador())` | Sección 6 |
| `ksort($mapa)` | `new TreeMap<>(mapa)` | Sección 9 |
| `arsort($mapa)` (por valor, de mayor a menor) | lista de entradas + comparador | Sección 9 |

### ⚠️ Dos diferencias con PHP

1. **En PHP, `sort` devuelve `true`.** En Java, `sort` **no devuelve nada** (`void`). En los dos casos la lista
   original queda ordenada.
2. **En PHP, `usort` acepta una función.** En Java (sin lambdas) le pasas un **objeto** de una clase que
   implementa `Comparator`. Es más largo de escribir, pero es la misma idea.

---

## 3. Ordenar lo que Java ya sabe ordenar

### 3.1 Las dos formas de ordenar una lista

💻
```java
List<Integer> n = new ArrayList<>(List.of(42, 7, 19, 7, 100, 3));

n.sort(null);                       // null = "usa el orden natural"
System.out.println(n);              // → [3, 7, 7, 19, 42, 100]

Collections.sort(n);                // lo mismo, forma más antigua
System.out.println(n);              // → [3, 7, 7, 19, 42, 100]

n.sort(Comparator.reverseOrder());  // al revés
System.out.println(n);              // → [100, 42, 19, 7, 7, 3]
```
📖 Fíjate: los dos `7` siguen ahí. **Ordenar no quita repetidos** (eso lo hace un `Set`).

### 3.2 Textos: el orden no es el del diccionario

💻
```java
List<String> frutas = new ArrayList<>(List.of("pera", "Manzana", "banana", "Álamo", "uva", "Zanahoria"));

Collections.sort(frutas);
System.out.println(frutas);   // → [Manzana, Zanahoria, banana, pera, uva, Álamo]
```
📖 Dos cosas raras, las dos vistas ya en la lección 02:
1. **Mayúsculas antes que minúsculas**: `Zanahoria` va antes que `banana`.
2. **Las letras con tilde van al final**: `Álamo` queda después de `uva`.

Java compara `String` **letra por letra, por su número en la tabla Unicode**. En esa tabla, `A`–`Z` vienen
antes que `a`–`z`, y `Á` está mucho después de `z`.

**Ignorar mayúsculas:** existe un comparador ya hecho, `String.CASE_INSENSITIVE_ORDER`:
```java
frutas.sort(String.CASE_INSENSITIVE_ORDER);
System.out.println(frutas);   // → [banana, Manzana, pera, uva, Zanahoria, Álamo]
```
Las mayúsculas ya no importan, pero `Álamo` **sigue al final**: la tilde no es un tema de mayúsculas.

> **NO RELEVANTE AHORA:** para ordenar con las reglas del español (`Á` junto a `A`) existe `java.text.Collator`.
> En un backend real casi siempre ordena la base de datos (`ORDER BY`), y ella ya sabe hacerlo.

### 3.3 `BigDecimal`: ordena por valor, pero la escala no desaparece

💻
```java
List<BigDecimal> p = new ArrayList<>(List.of(
        new BigDecimal("10.5"), new BigDecimal("2.00"), new BigDecimal("0.99"), new BigDecimal("2")));
Collections.sort(p);
System.out.println(p);   // → [0.99, 2.00, 2, 10.5]
```
📖 `2.00` y `2` valen lo mismo para `compareTo` (da `0`), así que quedan juntos. Los dos siguen en la lista, cada
uno con su escala. (Por qué `2.00` queda antes que `2` lo verás en la sección 7.3: es la **estabilidad**.)

Recuerda la diferencia (revisión de la Tarea 3 de la lección 03):
```java
new BigDecimal("2").equals(new BigDecimal("2.00"))      // → false  (distinta escala)
new BigDecimal("2").compareTo(new BigDecimal("2.00"))   // → 0      (mismo valor)
```
Esto vuelve en la sección 8, y es importante.

### 3.4 ⚠️ `sort` no devuelve nada

Este es el error más común al empezar:
```java
List<Integer> ordenada = n.sort(null);
// → error de compilación: incompatible types: void cannot be converted to List<Integer>

System.out.println(Collections.sort(n));
// → error de compilación: 'void' type not allowed here
```
📖 `sort` **cambia la lista que ya tienes**. No crea otra y no devuelve nada. La forma correcta:
```java
n.sort(null);              // 1. ordenar (hace)
System.out.println(n);     // 2. usar la misma lista, ya ordenada
```
Es tu punto débil de siempre, **lo que devuelve vs lo que hace**, en su versión más pura: aquí no devuelve nada,
**solo hace**.

### 3.5 ⚠️ No puedes ordenar una lista que no se puede modificar

💻
```java
List.of(3, 1, 2).sort(null);
// → UnsupportedOperationException

List.of(1, 2, 3).sort(null);
// → UnsupportedOperationException   ← ¡aunque ya esté ordenada!

Collections.unmodifiableList(lista).sort(null);
// → UnsupportedOperationException
```
📖 Ordenar = mover elementos de posición = **modificar**. A Java no le importa si al final quedaría igual: ni
siquiera lo intenta.

**La solución es la de siempre: copiar y después ordenar la copia.**
```java
List<Integer> orig = List.of(30, 10, 20);
List<Integer> copia = new ArrayList<>(orig);   // copia modificable
copia.sort(null);
System.out.println(orig + " " + copia);        // → [30, 10, 20] [10, 20, 30]
```
La original queda **intacta**. Esto es clave en la Tarea 6: un `getter` que devuelve "los productos ordenados por
precio" **no debe desordenar** la lista interna del objeto.

### 3.6 `null` dentro de la lista

```java
List<String> l = new ArrayList<>(Arrays.asList("b", null, "a"));
l.sort(null);
// → NullPointerException: Cannot invoke "java.lang.Comparable.compareTo(Object)" because "a[runHi]" is null
```
No se puede comparar "nada" con algo. Lo normal es no meter `null` en las listas (tus constructores ya lo
impiden en `OrderItem`).

### 🧪 Pruébalo · sección 3 (8 min)

En `SortPlayground`, con **estos** datos:
```java
List<String> colores = new ArrayList<>(List.of("verde", "Azul", "rojo", "Índigo", "amarillo"));
```
1. Ordena con `Collections.sort` e imprime. `// espero:` (cuidado con la `A` mayúscula y la `Í`).
2. Ordena con `String.CASE_INSENSITIVE_ORDER` e imprime. `// espero:`
3. Escribe `List<String> r = colores.sort(null);`. **No ejecutes**: lee el error en rojo de IntelliJ, cópialo en
   un comentario y **borra la línea**.
4. Crea `List<String> fijos = List.of("c", "a", "b");`. Ordénala dentro de un `try/catch
   (UnsupportedOperationException e)` e imprime `"no se puede"` en el `catch`. `// espero:` ¿entra al `catch`?
5. Ahora haz una copia de `fijos`, ordena la copia e imprime **las dos**. `// espero:`

### ✅ ¿Lo entendiste?
1. ¿Qué imprime `System.out.println(lista.sort(null));`?
2. ¿Por qué `List.of(1, 2).sort(null)` falla si ya está ordenada?
3. Ordenas `["b", "B", "a"]` con `Collections.sort`. ¿Qué sale?

<details><summary>Respuestas</summary>

1. Nada: **no compila**. `sort` es `void`, no hay nada que imprimir.
2. Porque `List.of` no se puede modificar, y `sort` ni siquiera mira si ya está ordenada: lanza
   `UnsupportedOperationException` directamente.
3. `[B, a, b]`: las mayúsculas van antes que las minúsculas.
</details>

---

## 4. Comparar dos cosas: negativo, cero, positivo

### 📖 Explicación

Para ordenar, Java solo necesita poder responder **una pregunta**, muchas veces: *"de estos dos, ¿cuál va
primero?"*. La respuesta es un `int`, y **solo importa su signo**:

| `a.compareTo(b)` | Significa | `a` va… |
|------------------|-----------|---------|
| **negativo** (`-1`, `-24`, `-71`…) | `a` es **menor** que `b` | **antes** que `b` |
| **cero** | `a` y `b` **valen lo mismo** para este orden | da igual (empate) |
| **positivo** (`1`, `32`…) | `a` es **mayor** que `b` | **después** de `b` |

**Truco para acordarte:** piensa en `a - b`. Si `a` es menor, `a - b` es negativo.

💻 Números reales que devuelve Java (comprobado):
```java
"b".compareTo("z")          // → -24    (b va antes)
"pera".compareTo("Pera")    // → 32     (la minúscula va después)
"Pera".compareTo("pera")    // → -32
"ana".compareTo("ana")      // → 0
"ana".compareTo("anabel")   // → -3     (más corto va antes)
Integer.compare(3, 7)       // → -1
Integer.compare(7, 3)       // → 1
Integer.compare(5, 5)       // → 0
new BigDecimal("9.99").compareTo(new BigDecimal("10"))   // → -1
"pera".compareToIgnoreCase("PERA")                       // → 0
```
📖 Aquí está **por qué** nunca se escribe `compareTo(...) == -1`: `"b".compareTo("z")` es **`-24`**, no `-1`.
`BigDecimal` e `Integer.compare` devuelven `-1/0/1`, pero **la regla general solo promete el signo**. Si
escribes `== -1`, tu código funciona con unos tipos y falla con otros.

### 4.1 Las tres reglas que tiene que cumplir toda comparación

No hay que memorizarlas con su nombre técnico, pero sí entenderlas:

| Regla | En palabras | Ejemplo |
|-------|-------------|---------|
| **al revés, signo contrario** | si `a` va antes que `b`, entonces `b` va después que `a` | `compare(ana, luis) = -1` → `compare(luis, ana) = 1` |
| **en cadena** | si `a < b` y `b < c`, entonces `a < c` | 5 < 10 y 10 < 20 → 5 < 20 |
| **consistente con `equals`** (recomendada) | si `a.equals(b)`, que la comparación dé `0` (y al revés) | dos `Product` con el mismo `id` → `0` |

La tercera **no es obligatoria**, pero si no la cumples, `TreeSet` y `TreeMap` se portan raro (sección 8).

### 4.2 ⚠️ Nunca compares números restando

Es tentador escribir `return a - b;` (como el truco). **No lo hagas.** Con números grandes, la resta se pasa del
máximo de un `int` (*overflow*) y da un signo equivocado:
```java
System.out.println(2_000_000_000 - (-2_000_000_000));   // → -294967296   ← ¡negativo!

// un comparador que resta:   return a - b;
lista = [2000000000, -2000000000, 5]
lista.sort(comparadorQueResta)    // → [2000000000, -2000000000, 5]     ← mal ordenado
lista.sort(null)                  // → [-2000000000, 5, 2000000000]     ← bien
```
**Usa siempre `Integer.compare(a, b)`** (y `Long.compare`, `Double.compare` para los otros tipos numéricos). Para
objetos (`BigDecimal`, `BigInteger`, `String`), usa su `compareTo`.

### 🧪 Pruébalo · sección 4 (4 min)

Imprime y escribe `// espero:` **solo el signo** (negativo / cero / positivo) antes de ejecutar:
1. `"mesa".compareTo("silla")`
2. `"Silla".compareTo("silla")`
3. `Integer.compare(120, 15)`
4. `new BigDecimal("5.00").compareTo(new BigDecimal("5"))`
5. `new BigDecimal("5.00").equals(new BigDecimal("5"))` (este es `boolean`)

### ✅ ¿Lo entendiste?
1. `a.compareTo(b)` devuelve `-7`. ¿Quién va primero?
2. ¿Por qué `if (x.compareTo(y) == -1)` es un error, aunque a veces funcione?
3. Para comparar dos `int` en un comparador, ¿qué escribes?

<details><summary>Respuestas</summary>

1. `a` (negativo = `a` es menor = va antes).
2. Porque la regla solo promete el **signo**. Con `String` puede salir `-24`, y `== -1` daría `false`. Escribe `< 0`.
3. `Integer.compare(a, b)`, nunca `a - b`.
</details>

---

## 5. `Comparable`: el orden natural de una clase

### 📖 Explicación

`Comparable` es una **interfaz** (como tu `Shippable`) con un solo método:
```java
public interface Comparable<T> {
    int compareTo(T otro);
}
```
Si una clase la implementa, dice: *"mis objetos saben compararse entre sí, y este es su orden **natural**"*.
`String`, `Integer` y `BigDecimal` ya la implementan: por eso `Collections.sort` los ordena sin que tú digas nada.

El `<T>` es el tipo con el que se compara, igual que en `List<Product>`. Casi siempre es **la propia clase**:
`Empleado implements Comparable<Empleado>`.

### 5.1 Ejemplo completo

💻 Una clase de ejemplo (en las tareas usarás `Product`):
```java
public class Empleado implements Comparable<Empleado> {    // ① la promesa
    private final int id;
    private final String nombre;
    private final BigDecimal salario;

    // constructor, getters, equals y hashCode por id (como en la lección 02)...

    @Override
    public int compareTo(Empleado otro) {                    // ② cumplir la promesa
        return Integer.compare(this.id, otro.id);            // ③ orden natural: por id
    }

    @Override
    public String toString() { return nombre + "(" + id + ", " + salario + ")"; }
}
```
📖 Línea por línea:
- **①** `implements Comparable<Empleado>`: "mis empleados se comparan con otros empleados".
- **②** `compareTo(Empleado otro)`: compara **este** empleado (`this`) con **otro**. `@Override` porque viene
  de la interfaz.
- **③** el orden natural es el `id`. Devolvemos lo que devuelve `Integer.compare`: negativo si mi id es menor.

💻 Ahora todo lo que necesitaba "saber comparar" funciona:
```java
Empleado ana    = new Empleado(3, "Ana",    "1500.00");
Empleado luis   = new Empleado(1, "luis",   "2200.00");
Empleado bea    = new Empleado(2, "Bea",    "1500.00");
Empleado carlos = new Empleado(4, "Carlos", "900.50");

List<Empleado> eq = new ArrayList<>(List.of(ana, luis, bea, carlos));
Collections.sort(eq);
System.out.println(eq);
// → [luis(1, 2200.00), Bea(2, 1500.00), Ana(3, 1500.00), Carlos(4, 900.50)]     ← por id

System.out.println(new TreeSet<>(eq));
// → [luis(1, 2200.00), Bea(2, 1500.00), Ana(3, 1500.00), Carlos(4, 900.50)]     ← ya no hay ClassCastException

TreeMap<Empleado, String> area = new TreeMap<>();
area.put(carlos, "ventas"); area.put(ana, "caja"); area.put(luis, "bodega");
System.out.println(area);
// → {luis(1, 2200.00)=bodega, Ana(3, 1500.00)=caja, Carlos(4, 900.50)=ventas}
```

### 5.2 ¿Qué elijo como orden natural?

Una clase solo tiene **un** orden natural, así que elige el más "obvio" y **estable**:

| Clase | Orden natural razonable | Por qué |
|-------|------------------------|---------|
| `Product`, `Customer`, `Order` (entidades) | por **`id`** | es lo que usa `equals` → se cumple la regla 3 de la sección 4.1 |
| `Money` (valor) | por cantidad | dos `Money` iguales valen lo mismo |
| `LocalDate` (ya viene hecho) | cronológico | |

**¿Y por qué no por precio, que es lo que más se muestra?** Porque el precio **no identifica** al producto: dos
productos distintos pueden costar lo mismo. Si el orden natural fuera el precio, un `TreeSet<Product>` creería que
son el mismo y **perdería uno** (sección 8). El precio es un orden **extra**: un `Comparator` (sección 6).

### 5.3 ⚠️ Errores típicos con `Comparable`

**a) Escribir `compareTo` pero olvidar `implements Comparable`:**
```java
public class CasiComparable {                    // ← falta implements Comparable<CasiComparable>
    public int compareTo(CasiComparable o) { ... }
}
new TreeSet<CasiComparable>().add(x);            // → ClassCastException
```
Tener un método que se llama `compareTo` no basta. Java solo confía en la **interfaz**: es la promesa.

**b) `Collections.sort` vs `lista.sort(null)` con una clase que no es `Comparable`:**
```java
Collections.sort(listaSinOrden);
// → error de COMPILACIÓN: no suitable method found for sort(List<SinOrden>)

listaSinOrden.sort(null);
// → compila, pero en EJECUCIÓN: ClassCastException: class SinOrden cannot be cast to class java.lang.Comparable
```
`Collections.sort` revisa al compilar que la clase sea `Comparable`; `sort(null)` se entera recién al ejecutar.
Los dos fallan, pero el error de compilación es **mejor**: lo ves antes.

**c) Un `id` que puede ser `null`:** `this.id.compareTo(otro.id)` con un `id` `null` lanza
`NullPointerException`. Tu `Product` acepta `id` `null` en el constructor; en las tareas siempre le pondrás id.

### 5.4 Subclases: el orden se hereda

`Gerente extends Empleado` hereda `compareTo`. Un `Collections.sort(List<Gerente>)` funciona sin escribir nada
más (comprobado). Con tus clases: `Laptop` y `DigitalProduct` heredarán el orden de `Product`.

### 🧪 Pruébalo · sección 5 (4 min)

Con tu clase `Customer` (todavía **sin** `Comparable`):
1. Crea dos `Customer` y agrégalos a un `TreeSet<Customer>`, dentro de un `try/catch (ClassCastException e)`.
   `// espero:` ¿entra al `catch`? ¿En qué línea: el `new TreeSet` o el `add`?
2. Escribe `Collections.sort(new ArrayList<Customer>());`. **No ejecutes**: ¿IntelliJ lo marca en rojo? Copia el
   error en un comentario y borra la línea.

(No le agregues `Comparable` a `Customer`: en las tareas lo harás con `Product`.)

### ✅ ¿Lo entendiste?
1. ¿Cuántos órdenes naturales puede tener una clase?
2. ¿Por qué el orden natural de `Product` debería ser el `id` y no el precio?
3. Escribiste `compareTo` en tu clase pero `TreeSet` sigue lanzando `ClassCastException`. ¿Qué falta?

<details><summary>Respuestas</summary>

1. Uno solo (hay un solo método `compareTo`).
2. Porque el `id` es lo que usa `equals`: así "mismo orden" = "mismo producto". Con el precio, dos productos
   distintos con el mismo precio darían `0` y el `TreeSet` perdería uno.
3. `implements Comparable<MiClase>` en la declaración de la clase.
</details>

---

## 6. `Comparator`: otros órdenes, en otra clase

### 📖 Explicación

¿Y si quieres ordenar los empleados por **salario**? No puedes cambiar `compareTo` (ya es por id, y solo hay
uno). La solución es un **`Comparator`**: una clase aparte cuyo único trabajo es comparar dos objetos.

```java
public interface Comparator<T> {
    int compare(T a, T b);       // misma regla: negativo = a va antes
}
```
Diferencia con `Comparable`:

| | `Comparable` | `Comparator` |
|---|---|---|
| Dónde se escribe | **dentro** de la clase (`Empleado`) | en **otra** clase (`PorSalario`) |
| Método | `compareTo(otro)`: compara `this` con `otro` | `compare(a, b)`: compara `a` con `b` |
| Cuántos | uno | los que quieras |
| Cómo se usa | `Collections.sort(lista)`, `lista.sort(null)` | `lista.sort(new PorSalario())` |

### 6.1 Ejemplo: dos comparadores

💻
```java
public class PorSalario implements Comparator<Empleado> {
    @Override
    public int compare(Empleado a, Empleado b) {
        return a.getSalario().compareTo(b.getSalario());     // BigDecimal sabe compararse
    }
}

public class PorNombre implements Comparator<Empleado> {
    @Override
    public int compare(Empleado a, Empleado b) {
        return a.getNombre().compareToIgnoreCase(b.getNombre());   // sin importar mayúsculas
    }
}
```
📖 Fíjate en que **no** hay ningún `if`: devolvemos directamente lo que devuelve `compareTo`. Si `a` gana menos,
`compareTo` da negativo, y eso es justo lo que el comparador tiene que devolver. No hace falta "traducirlo".

💻 Usarlos: se pasa **un objeto** del comparador (`new PorSalario()`):
```java
List<Empleado> eq = new ArrayList<>(List.of(ana, luis, bea, carlos));

eq.sort(new PorSalario());
System.out.println(eq);
// → [Carlos(4, 900.50), Ana(3, 1500.00), Bea(2, 1500.00), luis(1, 2200.00)]

eq.sort(new PorNombre());
System.out.println(eq);
// → [Ana(3, 1500.00), Bea(2, 1500.00), Carlos(4, 900.50), luis(1, 2200.00)]   ← "luis" al final aunque es minúscula
```
`Collections.sort(eq, new PorSalario())` hace lo mismo (forma antigua).

### 6.2 El más grande y el más chico: `Collections.max` / `min`

Ya no necesitas un `for` para buscar el más caro (lección 01, Tarea 3):
```java
Collections.max(eq, new PorSalario())   // → luis(1, 2200.00)
Collections.min(eq, new PorSalario())   // → Carlos(4, 900.50)
Collections.min(eq)                     // → luis(1, 2200.00)   ← sin comparador: orden natural (id 1)
```
**No ordenan la lista**: solo la recorren y devuelven un elemento. La lista queda igual.

⚠️ Con una lista **vacía**, `Collections.min`/`max` lanzan `NoSuchElementException` (comprobado). No hay "el
más chico" de nada. (La forma elegante de manejar ese "puede no haber" es `Optional`, semana 5.)

### 6.3 ¿Dónde guardo los comparadores?

Son clases normales: cada una en su archivo. Si son del **dominio** (los usará `Product`, un catálogo, un
pedido), van cerca del dominio: por ejemplo, `src/Models/comparators/`. En las tareas los creas ahí.

**Nombres:** que digan **qué** comparan: `ProductPriceComparator`, `ProductNameComparator`. Nada de
`Comparador1`.

### 6.4 Subclases: un comparador del padre sirve para los hijos

```java
List<Gerente> gerentes = new ArrayList<>(List.of(gaby, hugo));
gerentes.sort(new PorSalario());     // PorSalario es Comparator<Empleado>: compila y funciona
// → [Hugo(6, 3000), Gaby(8, 5000)]
```
Un `Comparator<Product>` sirve para ordenar una `List<Laptop>`. El porqué exacto (`? super T`) es de la
semana 7 (generics). Por ahora: si sabe comparar padres, sabe comparar hijos.

### 🧪 Pruébalo · sección 6 (6 min)

1. En `src/practical/sorting/` crea `CustomerNameComparator implements Comparator<Customer>` (por nombre, sin
   importar mayúsculas).
2. En `SortPlayground`, crea 3 `Customer`: `"mario"`, `"Andrea"`, `"Luz"` (ids y emails a tu elección). Ponlos
   en un `ArrayList` en ese orden.
3. Ordena con tu comparador e imprime **solo los nombres** con un for-each. `// espero:`
4. Imprime `Collections.max(lista, new CustomerNameComparator()).getName()`. `// espero:`

### ✅ ¿Lo entendiste?
1. ¿Qué diferencia hay entre `compareTo(otro)` y `compare(a, b)`?
2. ¿Qué le pasas a `lista.sort(...)` para ordenar por salario: la clase `PorSalario` o un objeto?
3. ¿`Collections.max(lista, comp)` deja la lista ordenada?

<details><summary>Respuestas</summary>

1. `compareTo` vive dentro de la clase y compara `this` con `otro`; `compare` vive en otra clase y compara dos
   objetos que recibe.
2. Un **objeto**: `new PorSalario()`.
3. No. Solo busca y devuelve un elemento; la lista no cambia.
</details>

---

## 7. Combinar órdenes: `reversed`, `thenComparing` y la estabilidad

### 7.1 `reversed()`: al revés

Todo `Comparator` trae un método `reversed()` que **devuelve un comparador nuevo** con el orden invertido. No
necesitas escribir una clase `PorSalarioDesc`:
```java
eq.sort(new PorSalario().reversed());
System.out.println(eq);
// → [luis(1, 2200.00), Ana(3, 1500.00), Bea(2, 1500.00), Carlos(4, 900.50)]   ← de mayor a menor
```
📖 Léelo en dos pasos: `new PorSalario()` crea el comparador; `.reversed()` **devuelve otro** comparador, al
revés. Ese segundo es el que recibe `sort`. (Otra vez: un método que **devuelve** algo nuevo.)

### 7.2 `thenComparing(...)`: desempatar

Ana y Bea ganan lo mismo (`1500.00`). ¿Quién va primero? Si quieres que, **en caso de empate**, decida el nombre:
```java
eq.sort(new PorSalario().thenComparing(new PorNombre()));
```
📖 Se lee: *"compara por salario; **solo si empatan**, compara por nombre"*. Es como el `ORDER BY salario, nombre`
de SQL.

💻 Para ver la diferencia, empieza con Bea **antes** que Ana:
```java
// lista inicial: [Bea, luis, Ana, Carlos]
eq.sort(new PorSalario());
// → [Carlos(4, 900.50), Bea(2, 1500.00), Ana(3, 1500.00), luis(1, 2200.00)]   ← Bea primero (como venía)

// misma lista inicial: [Bea, luis, Ana, Carlos]
eq.sort(new PorSalario().thenComparing(new PorNombre()));
// → [Carlos(4, 900.50), Ana(3, 1500.00), Bea(2, 1500.00), luis(1, 2200.00)]   ← Ana primero (desempata el nombre)
```

### 7.3 Estabilidad: en un empate, se respeta el orden en que venían

El primer resultado de la sección 7.2 muestra algo importante: sin desempate, Bea quedó antes que Ana **porque ya venía
antes**. Si la lista empieza con Ana antes que Bea, sale Ana primero (comprobado).

Eso se llama **orden estable**: `List.sort` y `Collections.sort` **garantizan** que, si dos elementos empatan
(la comparación da `0`), conservan su orden original. Por eso, en la sección 3.3, `2.00` quedó antes que `2`: venía antes.

**Consecuencia práctica:** si en tu programa un empate no puede depender del azar del orden de entrada, **pon
un desempate** (`thenComparing`). En un catálogo real, el último desempate suele ser el `id`.

### 7.4 ¿Dónde va el `reversed()`?

`reversed()` invierte **todo lo que está a su izquierda**. El lugar cambia el resultado:
```java
eq.sort(new PorSalario().reversed().thenComparing(new PorNombre()));
// → [luis(1, 2200.00), Ana(3, 1500.00), Bea(2, 1500.00), Carlos(4, 900.50)]
//    salario de mayor a menor; empate por nombre A→Z (Ana, Bea)

eq.sort(new PorSalario().thenComparing(new PorNombre()).reversed());
// → [luis(1, 2200.00), Bea(2, 1500.00), Ana(3, 1500.00), Carlos(4, 900.50)]
//    invierte los DOS: salario de mayor a menor Y nombre Z→A (Bea, Ana)
```
Lo normal es lo primero: "más caros primero; si empatan, por nombre".

### 🧪 Pruébalo · sección 7 (5 min)

Usa tus 3 `Customer` del 🧪 de la sección 6 y agrega un cuarto: `"andrea"` (minúscula, **otro** id), **al principio** de la
lista.
1. Ordena con `new CustomerNameComparator()` e imprime los nombres con su id. `// espero:` ¿cuál "andrea" va
   primero y por qué?
2. Ordena con `new CustomerNameComparator().reversed()`. `// espero:`

### ✅ ¿Lo entendiste?
1. ¿Qué hace `thenComparing` si el primer comparador **no** da `0`?
2. Dos productos con el mismo precio, sin desempate. ¿En qué orden quedan?
3. ¿Hace falta escribir una clase `PorSalarioDescendente`?

<details><summary>Respuestas</summary>

1. Nada: el segundo comparador solo se consulta en caso de empate.
2. En el orden en que estaban antes de ordenar (el orden es **estable**).
3. No: `new PorSalario().reversed()`.
</details>

---

## 8. ⚠️ La trampa: `TreeSet` y `TreeMap` con un comparador que empata

### 📖 Explicación

Un `TreeSet` **no usa `equals`**. Para decidir si un elemento "ya está", usa la comparación: **si da `0`, lo
considera repetido** y no lo agrega. Con el orden natural por `id` no hay problema (mismo id = mismo
empleado). Con un comparador por salario, sí:

💻
```java
TreeSet<Empleado> porSalario = new TreeSet<>(new PorSalario());   // el comparador va en el constructor
System.out.println(porSalario.add(ana));      // → true
System.out.println(porSalario.add(luis));     // → true
System.out.println(porSalario.add(bea));      // → false   ← ¡Bea NO entró! gana lo mismo que Ana
System.out.println(porSalario.add(carlos));   // → true

System.out.println(porSalario.size());        // → 3
System.out.println(porSalario);
// → [Carlos(4, 900.50), Ana(3, 1500.00), luis(1, 2200.00)]
```
Y es peor:
```java
porSalario.contains(new Empleado(99, "Otra", "1500.0"))   // → true   ← ¡una persona que nunca agregaste!
```
Para el `TreeSet`, "contiene a alguien que gana 1500" = "contiene a esa persona".

**Con `TreeMap` es igual**, y además **se queda con la clave vieja**:
```java
TreeMap<Empleado, String> m = new TreeMap<>(new PorSalario());
m.put(ana, "caja");      // → null
m.put(bea, "bodega");    // → "caja"   ← "ya existe": reemplaza el valor
System.out.println(m);   // → {Ana(3, 1500.00)=bodega}   ← la clave sigue siendo Ana, con el valor de Bea
```

### 8.1 La solución: un desempate que identifique

Si el comparador **nunca** da `0` para dos objetos distintos, no se pierde nada. Desempata con algo único: el
orden natural (el `id`):
```java
TreeSet<Empleado> arreglado = new TreeSet<>(new PorSalario().thenComparing(Comparator.naturalOrder()));
arreglado.addAll(List.of(ana, luis, bea, carlos));
System.out.println(arreglado.size());   // → 4
System.out.println(arreglado);
// → [Carlos(4, 900.50), Bea(2, 1500.00), Ana(3, 1500.00), luis(1, 2200.00)]   ← empate de salario: id 2 antes que id 3
```
📖 `Comparator.naturalOrder()` es un comparador ya hecho que usa el `compareTo` de la clase. Aquí significa "si
empatan en salario, decide el id".

### 8.2 La misma trampa con `BigDecimal` (ya la viste sin saberlo)

```java
// precios: [0.99, 2.00, 2, 10.5]
new HashSet<>(precios).size()    // → 4   (equals: 2 ≠ 2.00 por la escala)
new TreeSet<>(precios).size()    // → 3   (compareTo: 2 = 2.00)
new TreeSet<>(precios)           // → [0.99, 2.00, 10.5]   ← el "2" desapareció
```
`BigDecimal` es justo un caso donde `compareTo` **no es consistente con `equals`**. Mismo dato, distinta
colección, distinto tamaño.

### Regla para llevar

> **`List.sort`** con empates → no pasa nada malo (solo importa el orden de entrada).
> **`TreeSet`/`TreeMap`** con empates → **se pierden elementos**. Su comparador tiene que desempatar hasta
> distinguir objetos distintos.

### ✅ ¿Lo entendiste?
1. Un `TreeSet<Product>` con un comparador por precio. Agregas dos productos distintos que cuestan `49.00`. ¿`size`?
2. ¿Por qué con `List.sort` el mismo comparador no "pierde" nada?
3. ¿Qué le agregas al comparador para que el `TreeSet` no pierda elementos?

<details><summary>Respuestas</summary>

1. `1`. El segundo `add` devuelve `false`.
2. Porque una lista acepta repetidos: `sort` solo cambia posiciones, nunca decide si algo "ya está".
3. Un desempate que identifique: `.thenComparing(Comparator.naturalOrder())` (si el orden natural es por id).
</details>

---

## 9. Ordenar un `Map`

### 9.1 Por clave: `TreeMap` (ya lo sabes)

```java
// votos = {rojo=4, azul=9, verde=4, negro=1}   (LinkedHashMap)
System.out.println(new TreeMap<>(votos));
// → {azul=9, negro=1, rojo=4, verde=4}          ← alfabético por clave
```
`new TreeMap<>(otroMapa)` crea una **copia** ordenada por clave. El mapa original no cambia.

### 9.2 Por valor: no existe un "`TreeMap` por valor"

Un `Map` no se puede ordenar por valor directamente. La receta tiene 3 pasos:

1. **Saca las entradas a una lista** (una lista sí se puede ordenar):
   ```java
   List<Map.Entry<String, Integer>> ranking = new ArrayList<>(votos.entrySet());
   ```
2. **Ordena la lista** con un comparador de entradas:
   ```java
   public class PorValorDesc implements Comparator<Map.Entry<String, Integer>> {
       @Override
       public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
           return Integer.compare(b.getValue(), a.getValue());   // b antes que a → de mayor a menor
       }
   }

   ranking.sort(new PorValorDesc());
   ```
3. **Recorre la lista**:
   ```java
   for (Map.Entry<String, Integer> e : ranking) {
       System.out.println(e.getKey() + ": " + e.getValue());
   }
   // → azul: 9
   //   rojo: 4
   //   verde: 4
   //   negro: 1
   System.out.println(votos);   // → {rojo=4, azul=9, verde=4, negro=1}   ← el mapa NO cambió
   ```

📖 Dos detalles:
- **`Integer.compare(b..., a...)`** (con `b` primero) es otra forma de escribir "al revés", sin `reversed()`.
- **`Map.Entry<String, Integer>`** es largo, pero es solo "un par clave-valor" (lección 03, sección 5). El comparador es
  una clase normal, con un tipo largo.

**Empates:** `rojo` y `verde` tienen 4. Salen en el orden del mapa (estabilidad, sección 7.3). Si quieres que el
empate lo decida el nombre, el comparador tiene que comparar también la clave cuando los valores empatan. Lo
harás en la Tarea 5.

### ✅ ¿Lo entendiste?
1. ¿Cómo obtienes un `Map` ordenado por clave?
2. ¿Por qué hay que pasar las entradas a una `List` para ordenar por valor?

<details><summary>Respuestas</summary>

1. `new TreeMap<>(mapa)` (o creándolo como `TreeMap` desde el principio).
2. Porque un `Map` no tiene "posiciones" que puedas reordenar; una `List` sí. `TreeMap` solo ordena por clave.
</details>

---

## 10. Ordenar por tipo: `instanceof` y el orden de los `if`

### 📖 Explicación

A veces el orden no viene de un campo, sino del **tipo** del objeto: "primero los gerentes, después el resto".
La idea: un método que da un **rango** (un número) a cada tipo, y comparar esos números.

💻
```java
public class PorTipo implements Comparator<Empleado> {
    private int rango(Empleado e) {
        if (e instanceof Gerente) {
            return 1;      // primero
        }
        return 2;          // el resto
    }

    @Override
    public int compare(Empleado a, Empleado b) {
        return Integer.compare(rango(a), rango(b));
    }
}
// [Ana, Gaby(gerente), Carlos]  →  sort(new PorTipo())  →  [Gaby(8, 5000), Ana(3, 1500.00), Carlos(4, 900.50)]
```

### ⚠️ El orden de los `if` importa (lección 02, `getClass` vs `instanceof`)

`Gerente extends Empleado`, así que **un gerente también es un empleado**: `gaby instanceof Empleado` → `true`.
Si preguntas primero por el tipo **más general**, ese `if` atrapa a todos:
```java
private int rango(Empleado e) {
    if (e instanceof Empleado) return 2;   // ← un Gerente también entra aquí
    if (e instanceof Gerente)  return 1;   // ← nunca se llega
    return 3;
}
// [Ana, Gaby, Carlos]  →  [Ana(3, 1500.00), Gaby(8, 5000), Carlos(4, 900.50)]   ← todos rango 2; nada cambió
```
**Regla:** en una cadena de `instanceof`, pregunta **de lo más específico a lo más general** (hijo antes que
padre). En tus clases: `Laptop` es un `PhysicalProduct`, así que `Laptop` va **antes**.

---

## 11. (Opcional, no se pide en las tareas) El atajo: `Comparator.comparing`

En código profesional vas a ver esto:
```java
eq.sort(Comparator.comparing(Empleado::getSalario).thenComparing(Empleado::getNombre));
// → [Carlos(4, 900.50), Ana(3, 1500.00), Bea(2, 1500.00), luis(1, 2200.00)]
```
`Empleado::getSalario` es una *method reference* (semana 8): significa "para comparar, llama a `getSalario()` de
cada uno". `Comparator.comparing(...)` crea por dentro un comparador **igual a tu clase `PorSalario`**, sin que
escribas la clase.

**Solo para que lo reconozcas.** No lo uses en las tareas: las clases `Comparator` que vas a escribir son
exactamente lo que este atajo hace por dentro, y entender eso primero es lo que vale. Cuando lleguemos a la
semana 8, convertirás tus clases a esta forma (eso es **refactorizar**).

---

## 12. Resumen

### ¿Qué uso?

| Quiero… | Uso |
|---------|-----|
| ordenar `String`/números/`BigDecimal` | `lista.sort(null)` o `Collections.sort(lista)` |
| al revés | `lista.sort(Comparator.reverseOrder())` |
| que mi clase tenga un orden "por defecto" | `implements Comparable<MiClase>` (por `id` en entidades) |
| ordenar por otro campo | una clase `implements Comparator<MiClase>` |
| al revés con mi comparador | `new MiComparador().reversed()` |
| desempatar | `.thenComparing(new OtroComparador())` |
| el mayor / el menor | `Collections.max(lista, comp)` / `Collections.min(lista, comp)` |
| ordenar sin tocar la original | `new ArrayList<>(original)` y ordenar la copia |
| un `Map` por clave | `new TreeMap<>(mapa)` |
| un `Map` por valor | `new ArrayList<>(mapa.entrySet())` + comparador de entradas |

### Errores comunes

| Error | Qué pasa | Solución |
|-------|----------|----------|
| `List<X> r = lista.sort(...)` | no compila: `sort` es `void` | ordenar y después usar la misma lista |
| ordenar `List.of(...)` o un `unmodifiableList` | `UnsupportedOperationException` | ordenar una copia (`new ArrayList<>(...)`) |
| `compareTo(...) == -1` | falla con tipos que devuelven `-24` | `< 0` / `> 0` |
| `return a - b;` en un comparador | overflow con números grandes → mal ordenado | `Integer.compare(a, b)` |
| `compareTo` sin `implements Comparable` | `ClassCastException` en `TreeSet`/`sort(null)` | declarar la interfaz |
| orden natural por un campo que no identifica (precio) | `TreeSet`/`TreeMap` pierden elementos | orden natural por `id`; el precio en un `Comparator` |
| `TreeSet` con comparador que empata | `add` → `false`, `contains` de un extraño → `true` | `.thenComparing(Comparator.naturalOrder())` |
| `instanceof` del padre antes que el del hijo | el hijo nunca entra a su `if` | de lo más específico a lo más general |
| mayúsculas / tildes en `String` | `"Zeta"` antes que `"ana"`; `"Álamo"` al final | `CASE_INSENSITIVE_ORDER`/`compareToIgnoreCase`; tildes: `Collator` (más adelante) |
| `Collections.min/max` con lista vacía | `NoSuchElementException` | comprobar `isEmpty()` antes (u `Optional`, semana 5) |

### Relación con Spring Boot (solo para que sepas que existe)

- En un backend real, **casi siempre ordena la base de datos**: `ORDER BY price DESC, name`. Es más rápido que
  traer todo y ordenar en Java.
- Spring Data lo expresa así: `Sort.by("price").descending().and(Sort.by("name"))`. Es **el mismo concepto que
  `reversed().thenComparing(...)`**, pero traducido a SQL.
- Una API REST recibe el orden como parámetro: `GET /products?sort=price,desc&sort=name` → Spring lo convierte en
  un `Sort`.
- Lo que aprendes aquí sirve para lo que **no** viene de la base de datos: ordenar en memoria, `TreeMap` para
  reportes, y `sorted(...)` de los Streams (semana 9), que recibe **exactamente** un `Comparator`.

### Glosario

| Palabra | Significado |
|---------|-------------|
| **orden natural** | el orden "por defecto" de una clase, el de `compareTo` (`Comparable`) |
| **comparador** (`Comparator`) | un objeto que sabe comparar dos objetos de una clase; hay tantos como órdenes quieras |
| **desempate** | el segundo criterio, que solo se usa si el primero da `0` (`thenComparing`) |
| **orden estable** | en un empate, se conserva el orden que tenían antes |
| **consistente con `equals`** | la comparación da `0` exactamente cuando `equals` da `true` |
| **overflow** | un cálculo que se pasa del máximo de un `int` y "da la vuelta" a negativo |

---

## 13. Autoevaluación final (sin mirar)

Respóndelas en un comentario al final de `SortPlayground.java`. Si **8 de 10** te salen solas, pasa a las
tareas. Si no, dime cuáles fallaste.

1. ¿Qué devuelve `lista.sort(null)`? ¿Qué le pasa a `lista`?
2. ¿Por qué falla `List.of(2, 1).sort(null)` y cómo ordenas esos datos sin que falle?
3. `a.compareTo(b)` da `-24`. ¿Quién va primero? ¿Por qué no se compara con `== -1`?
4. ¿Por qué no se escribe `return a - b;` en un comparador de `int`?
5. Diferencia entre `Comparable` y `Comparator`: dónde se escribe cada uno y cuántos puede haber.
6. ¿Qué elegirías como orden natural de `Product` y por qué?
7. ¿Qué hace `new PorPrecio().reversed().thenComparing(new PorNombre())`, en palabras?
8. Dos elementos empatan en un `List.sort`. ¿En qué orden quedan?
9. Un `TreeSet` con un comparador por precio, y dos productos distintos con el mismo precio: ¿qué pasa? ¿Cómo lo
   arreglas?
10. ¿Cómo muestras un `Map<String, Integer>` ordenado por valor, de mayor a menor?

---

## 14. Tareas

### Reglas para todas las tareas

1. **Una tarea por vez, en orden.** Al terminar cada una, pídeme "revisa tarea N".
2. **Plantilla obligatoria** en cada `println`:
   ```java
   System.out.println("---- T1 · Paso 3 ----");   // separador antes de cada paso (paso, no tarea)
   System.out.println(algo);
   // espero: ...                                 // ANTES de ejecutar
   // real: ...    porque: ...                    // SOLO si fallaste
   ```
3. **Compara letra por letra** el `espero` con la salida. **Solo pon ✅ si coinciden exactamente** (`11` no es
   `11.0`, `86.2` no es `86.4`). Un ✅ equivocado cuenta más que un fallo honesto.
4. **Usa los datos exactos** del enunciado (nombres, mayúsculas, ids, precios, orden). Los datos están elegidos
   para que aparezcan empates y casos raros: si los cambias, el experimento no prueba nada.
5. **Las preguntas** (P1.1, P1.2…) van en un comentario al final del archivo, completando la frase.
6. **Si un paso no se entiende, no lo adivines.** Pregúntame "no entiendo T3 paso 4".
7. **Checklist** al final de cada tarea: revísala antes de pedir la revisión.
8. **Sin lambdas (`->`) ni `::`.** Todos los comparadores son clases. (`Comparator.reverseOrder()`,
   `Comparator.naturalOrder()` y `String.CASE_INSENSITIVE_ORDER` sí se pueden: son métodos y constantes normales.)

**Tiempo total aproximado:** 3 h 30 min.

| Tarea | Tema | Lee antes | Archivo(s) | Tiempo |
|-------|------|-----------|------------|--------|
| 1 | Ordenar lo que Java ya sabe ordenar | Secciones 3 y 4 | `SortBasics` | 25 min |
| 2 | `Product` con orden natural (`Comparable`) | Sección 5 | `Product`, `ComparableLab` | 35 min |
| 3 | Comparadores de precio y nombre, `reversed`, `thenComparing` | Secciones 6 y 7 | `Models/comparators/*`, `ComparatorLab` | 40 min |
| 4 | La trampa del `TreeSet` con empates | Sección 8 | `TreeTrapLab` | 25 min |
| 5 | Ordenar por tipo + ranking de un `Map` | Secciones 9 y 10 | `ProductTypeComparator`, `RankingLab` | 40 min |
| 6 | **Entregable:** `Catalog` ordenado por precio, nombre y tipo | todo | `Models/Catalog`, `CatalogMain` | 45 min |

Todos los archivos de práctica van en `src/practical/sorting/` (paquete `practical.sorting`). Los comparadores
del dominio van en `src/Models/comparators/` (paquete `Models.comparators`).

---

### Tarea 1 — Ordenar lo que Java ya sabe ordenar (fácil · 25 min)

**Objetivo:** ver con tus ojos que `sort` modifica y no devuelve, cómo ordena los `String` y que no se puede
ordenar una lista inmutable.
**Antes:** relee las secciones 3 y 4.
**Archivo:** `src/practical/sorting/SortBasics.java` (con `main`).

**Parte A — textos**

1. Crea:
   ```java
   List<String> ciudades = new ArrayList<>(List.of("lima", "Quito", "Bogotá", "asunción", "Cusco", "Ébano"));
   ```
2. Ordénala con `Collections.sort(ciudades)` e imprime. `// espero:` (piensa en mayúsculas y en la `É`).
3. Ordénala con `ciudades.sort(String.CASE_INSENSITIVE_ORDER)` e imprime. `// espero:` ¿dónde queda `Ébano` ahora?
4. Ordénala con `ciudades.sort(Comparator.reverseOrder())` e imprime. `// espero:`
   > Pista: `reverseOrder` es el **orden natural al revés**, no "sin mayúsculas al revés".

**Parte B — números y dinero**

5. Crea `List<Integer> stock = new ArrayList<>(List.of(15, 4, 120, 4, 0, 33));`. Ordénala con `sort(null)` e
   imprime. `// espero:` Después al revés. `// espero:`
6. Crea esta lista **en este orden**:
   ```java
   List<BigDecimal> precios = new ArrayList<>(List.of(
           new BigDecimal("19.90"), new BigDecimal("5"), new BigDecimal("100"),
           new BigDecimal("0.50"), new BigDecimal("5.00")));
   ```
   Ordénala e imprime. `// espero:` ¿`5` o `5.00` va primero? (sección 7.3)
7. Imprime `new HashSet<>(precios).size()` y `new TreeSet<>(precios).size()`. `// espero:` en cada uno.

**Parte C — `void` e inmutables**

8. Escribe `List<String> r = ciudades.sort(null);`. **No ejecutes.** Copia en un comentario el error que muestra
   IntelliJ (o `javac`) y **comenta la línea** para que el archivo compile.
9. Crea `List<String> tallas = List.of("M", "S", "XL", "L");`. Ordénala dentro de un `try/catch
   (UnsupportedOperationException e)` e imprime `"no se puede ordenar: " + e` en el `catch`. `// espero:`
10. Crea `List<String> tallasOrdenadas = new ArrayList<>(tallas);`, ordénala, e imprime **`tallas` y
    `tallasOrdenadas`**. `// espero:` (las dos).

**Preguntas:**
- **P1.1** *"`sort` devuelve ___. Por eso, para ver la lista ordenada, ___."*
- **P1.2** *"En el paso 7, `HashSet` tiene ___ y `TreeSet` tiene ___, porque `HashSet` usa ___ y `TreeSet` usa
  ___."*
- **P1.3** *"`Ébano` queda al final incluso sin mayúsculas porque `CASE_INSENSITIVE_ORDER` ignora ___, pero no
  ___."*

**Checklist:**
- [ ] 10 pasos con separador `---- T1 · Paso N ----` y `espero`
- [ ] paso 8: error copiado y línea comentada
- [ ] paso 10: imprimes **las dos** listas
- [ ] ✅ solo donde coincide letra por letra
- [ ] P1.1–P1.3

---

### Tarea 2 — `Product` con orden natural (medio · 35 min)

**Objetivo:** que `Product` sepa ordenarse por `id`, y comprobar que eso deja contento a `TreeSet` sin romper la
regla de `equals`.
**Antes:** relee la sección 5 completa y la sección 4.1 (regla 3).
**Archivos:** `src/Models/Product.java` (modificar) y `src/practical/sorting/ComparableLab.java` (nuevo).

1. **Antes de tocar `Product`**, en `ComparableLab` crea un `TreeSet<Product>` y agrégale **un** producto
   cualquiera, dentro de un `try/catch (ClassCastException e)`. Imprime `"ClassCastException"` en el `catch`.
   Ejecuta y escribe `// real:`. (Es lo que viste en la lección 02, Tarea 5.) Después **comenta** ese bloque.
2. En `Product`:
   - agrega `implements Comparable<Product>` a la declaración de la clase;
   - escribe `compareTo(Product other)` con `@Override`, comparando **por `id`**. Pista: `id` es `BigInteger`,
     que ya tiene su propio `compareTo` (como `BigDecimal`). No uses `if`.
3. Crea estos 5 productos (constructores que ya tienes) **y ponlos en una `List<Product> catalogo` en este orden**:

   | Variable | Clase | id | Nombre | Precio | Peso / fileSize |
   |----------|-------|----|--------|--------|-----------------|
   | `thinkpad` | `Laptop` | 40 | `"ThinkPad"` | `1200.00` | `2.1` |
   | `silla` | `PhysicalProduct` | 12 | `"Silla"` | `89.90` | `7.5` |
   | `curso` | `DigitalProduct` | 7 | `"Curso Java"` | `49.00` | `2.5` |
   | `mac` | `Laptop` | 25 | `"MacBook"` | `1999.99` | `1.4` |
   | `sillaGamer` | `PhysicalProduct` | **12** | `"Silla gamer"` | `149.90` | `9.0` |

   > `sillaGamer` tiene **el mismo id** que `silla` a propósito: para tu `equals`, son el mismo producto.
4. Ordena `catalogo` con `Collections.sort(catalogo)` e imprime cada producto con un for-each (usa el `toString`
   que ya tienes). `// espero:` las 5 líneas, en orden. ¿`Silla` o `Silla gamer` va primero? ¿Por qué? (sección 7.3)
5. Llena esta tabla en un comentario **antes** de ejecutar (solo el **signo**), y después imprime cada línea:

   | Expresión | espero (signo) | real |
   |-----------|----------------|------|
   | `thinkpad.compareTo(silla)` | | |
   | `curso.compareTo(mac)` | | |
   | `silla.compareTo(sillaGamer)` | | |
   | `silla.equals(sillaGamer)` | (boolean) | |

   ¿Se cumple "consistente con `equals`" (sección 4.1, regla 3) para `silla` y `sillaGamer`?
6. Descomenta el bloque del paso 1, pero ahora agrega **los 5** productos, imprimiendo lo que devuelve cada
   `add`. Después imprime `size()` y el `TreeSet`. `// espero:` los 5 `boolean`, el tamaño y cuál silla quedó.
7. Crea una `List<Laptop> laptops` con `thinkpad` y `mac` (en ese orden; necesitarás declararlas como `Laptop`
   o hacer *cast*). Ordénala con `Collections.sort(laptops)`. `// espero:` ¿compila aunque `Laptop` no tiene
   `compareTo` propio? (sección 5.4)

**Preguntas:**
- **P2.1** *"Elegí el `id` como orden natural porque ___. Si hubiera elegido el precio, en el paso 6 el
  `TreeSet` ___."*
- **P2.2** *"En el paso 6, el `add` de `sillaGamer` devolvió ___ porque `compareTo` dio ___. Quedó guardada la
  silla que ___."*
- **P2.3** *"Si un producto tuviera `id` `null`, `compareTo` lanzaría ___, porque ___."*

**Checklist:**
- [ ] paso 1 ejecutado **antes** de tocar `Product`, con `real`
- [ ] `implements Comparable<Product>` + `@Override compareTo` sin `if`
- [ ] datos exactos (5 productos, en ese orden, `sillaGamer` con id 12)
- [ ] tabla del paso 5 con espero y real
- [ ] P2.1–P2.3

---

### Tarea 3 — Comparadores de precio y nombre (medio · 40 min)

**Objetivo:** escribir tus primeros `Comparator` y combinarlos con `reversed` y `thenComparing`.
**Antes:** relee las secciones 6 y 7 completas.
**Archivos:**
- `src/Models/comparators/ProductPriceComparator.java` (nuevo)
- `src/Models/comparators/ProductNameComparator.java` (nuevo)
- `src/practical/sorting/ComparatorLab.java` (nuevo, con `main`)

1. `ProductPriceComparator implements Comparator<Product>`: por precio, de menor a mayor.
2. `ProductNameComparator implements Comparator<Product>`: por nombre, **sin importar mayúsculas**.
   (Ninguno de los dos necesita `if`: sección 6.1.)
3. En `ComparatorLab`, crea estos 6 productos y ponlos en una `List<Product> base` **en este orden**:

   | Variable | Clase | id | Nombre | Precio | Peso / fileSize |
   |----------|-------|----|--------|--------|-----------------|
   | `thinkpad` | `Laptop` | 40 | `"ThinkPad"` | `1200.00` | `2.1` |
   | `silla` | `PhysicalProduct` | 12 | `"Silla"` | `89.90` | `7.5` |
   | `curso` | `DigitalProduct` | 7 | `"curso java"` (minúsculas) | `49.00` | `2.5` |
   | `mac` | `Laptop` | 25 | `"MacBook"` | `1999.99` | `1.4` |
   | `mouse` | `PhysicalProduct` | 31 | `"Mouse"` | `49.00` | `0.2` |
   | `audifonos` | `PhysicalProduct` | 18 | `"audífonos"` | `89.90` | `0.3` |

   > Hay **dos empates de precio** a propósito: `curso`/`mouse` (49.00) y `silla`/`audifonos` (89.90).
4. Escribe un método `static void imprimir(List<Product> lista)` que imprima en **una línea**
   `nombre precio | nombre precio | ...`. Lo usas en todos los pasos.
5. **En cada uno de los pasos 5 a 10, trabaja sobre una copia nueva**: `List<Product> l = new ArrayList<>(base);`.
   Así cada paso empieza del mismo orden. Para cada uno: ordena, imprime, `// espero:`.

   | Paso | Ordena con | Pregunta para tu `espero` |
   |------|------------|---------------------------|
   | 5 | `new ProductPriceComparator()` | en los empates, ¿quién va primero? (sección 7.3) |
   | 6 | `new ProductPriceComparator().reversed()` | ¿el empate `curso`/`mouse` se invierte también? |
   | 7 | `new ProductNameComparator()` | ¿dónde queda `audífonos`? ¿y `curso java`? |
   | 8 | `new ProductPriceComparator().thenComparing(new ProductNameComparator())` | ¿cambió algo respecto al paso 5? |
   | 9 | `new ProductPriceComparator().reversed().thenComparing(new ProductNameComparator())` | "más caros primero; empate por nombre" |
   | 10 | `new ProductNameComparator().reversed()` | |

6. **Estabilidad:** copia nueva de `base`, dale vuelta con `Collections.reverse(l)` (invierte la lista), y
   después ordena con `new ProductPriceComparator()`. Imprime. `// espero:` compáralo con el paso 5: ¿qué
   cambió en los empates y por qué?
7. Imprime **solo el nombre** de:
   - `Collections.max(base, new ProductPriceComparator())`
   - `Collections.min(base, new ProductPriceComparator())` → ⚠️ hay un empate en el mínimo: ¿cuál devuelve?
   - `Collections.min(base)` (sin comparador: ¿qué orden usa?)

   `// espero:` en cada uno. Al final, imprime `base` con `imprimir`: ¿cambió su orden?

**Preguntas:**
- **P3.1** *"`reversed()` no modifica el comparador: ___ uno nuevo. Por eso escribo `new X().reversed()` y no
  necesito una clase `ProductPriceDescComparator`."*
- **P3.2** *"`thenComparing` solo se usa cuando ___. En el paso 8 cambió el orden de ___ porque ___."*
- **P3.3** *"Entre el paso 5 y el paso del punto 6 cambió el orden de los empates porque `sort` es ___: en un
  empate conserva ___."*

**Checklist:**
- [ ] 2 comparadores en `Models/comparators/`, sin `if`, con `@Override`
- [ ] datos exactos (nombres con sus mayúsculas: `"curso java"`, `"audífonos"`)
- [ ] copia nueva en cada paso
- [ ] pasos 5–10 + estabilidad + max/min, cada uno con `espero`
- [ ] P3.1–P3.3

---

### Tarea 4 — La trampa del `TreeSet` con empates (medio · 25 min)

**Objetivo:** ver desaparecer productos en un `TreeSet` por culpa de un comparador que empata, y arreglarlo.
**Antes:** relee la sección 8 completa.
**Archivo:** `src/practical/sorting/TreeTrapLab.java` (con `main`). Usa los **6 productos de la Tarea 3**
(cópialos o crea un método `static List<Product> productosT3()` que los devuelva).

1. Crea `TreeSet<Product> porPrecio = new TreeSet<>(new ProductPriceComparator());`.
2. Agrega los 6 productos **en el orden de la tabla de la Tarea 3**, uno por uno, imprimiendo lo que devuelve cada
   `add`. **Antes de ejecutar**, llena esta tabla en un comentario:

   | `add` de | ¿ya había alguien con ese precio? | espero (`true`/`false`) | real |
   |----------|-----------------------------------|-------------------------|------|
   | thinkpad | | | |
   | silla | | | |
   | curso | | | |
   | mac | | | |
   | mouse | | | |
   | audifonos | | | |

3. Imprime `porPrecio.size()` y el contenido con `imprimir` (necesitas pasarlo a lista:
   `new ArrayList<>(porPrecio)`). `// espero:` ¿quiénes faltan?
4. Crea un producto que **nunca agregaste**: `DigitalProduct` id `99`, `"Fantasma"`, precio `new
   BigDecimal("49")` (sin decimales), fileSize `1`. Imprime:
   - `porPrecio.contains(fantasma)` → `// espero:`
   - `productosT3().contains(fantasma)` (una `List`: usa `equals`) → `// espero:`
   - `new HashSet<>(productosT3()).contains(fantasma)` → `// espero:`

   > Fíjate en el `49` vs `49.00`: ¿importa la escala para `compareTo`?
5. **El arreglo:** crea `TreeSet<Product> arreglado` con
   `new ProductPriceComparator().thenComparing(Comparator.naturalOrder())`, agrega los 6 con `addAll` e imprime
   tamaño y contenido. `// espero:` el orden exacto, incluidos los empates (¿quién desempata?).
6. **`TreeMap`:** crea `TreeMap<Product, Integer> stock = new TreeMap<>(new ProductPriceComparator());`.
   Imprime lo que devuelven `stock.put(curso, 10)` y `stock.put(mouse, 3)`, y después `stock.size()`. Recorre
   con `entrySet()` imprimiendo `nombre=valor`. `// espero:` ¿qué **nombre** aparece y con qué **valor**?

**Preguntas:**
- **P4.1** *"Un `TreeSet` decide que un elemento 'ya está' cuando ___, no cuando ___."*
- **P4.2** *"`contains(fantasma)` dio ___ en el `TreeSet` y ___ en el `HashSet`, porque el `TreeSet` compara
  ___ y el `HashSet` compara ___."*
- **P4.3** *"En el `TreeMap` del paso 6, el segundo `put` devolvió ___, la clave que quedó es ___ y el valor
  es ___. Es decir: se quedó con la clave ___ y el valor ___."*
- **P4.4** *"Con `List.sort` y el mismo comparador no se pierde nada porque ___."*

**Checklist:**
- [ ] tabla del paso 2 llenada **antes** de ejecutar
- [ ] `fantasma` con precio `49` (sin decimales)
- [ ] arreglo con `thenComparing(Comparator.naturalOrder())`
- [ ] paso 6 con nombre **y** valor
- [ ] P4.1–P4.4

---

### Tarea 5 — Ordenar por tipo + ranking de un `Map` (medio-avanzado · 40 min)

**Objetivo:** un comparador que no usa un campo sino el **tipo** (`instanceof`), y ordenar un `Map` por valor
con desempate.
**Antes:** relee las secciones 9 y 10.

**Parte A — por tipo**
**Archivos:** `src/Models/comparators/ProductTypeComparator.java` (nuevo) y
`src/practical/sorting/RankingLab.java` (nuevo, con `main`).

Orden deseado: **1.** `Laptop` · **2.** otros `PhysicalProduct` · **3.** `DigitalProduct` · **4.** cualquier otro.

1. Escribe `ProductTypeComparator` con un método privado `int rank(Product p)` y `compare` usando
   `Integer.compare(rank(a), rank(b))`.
   **Primera versión (a propósito mal):** en `rank`, pregunta **primero** `instanceof PhysicalProduct` (→ 2),
   después `instanceof Laptop` (→ 1), después `instanceof DigitalProduct` (→ 3), y si no, 4.
2. En `RankingLab`, crea una lista con los 6 productos de la Tarea 3 **más** `sillaGamer` (id 12, de la Tarea 2)
   al final. Ordénala con `new ProductTypeComparator()` e imprime. `// espero:` ¿los `Laptop` quedan primero?
3. **Arregla** el orden de los `if` (sección 10). Ejecuta de nuevo (copia nueva de la lista). `// espero:`
4. Ordena con `new ProductTypeComparator().thenComparing(new ProductPriceComparator())`. `// espero:` (dentro de
   cada tipo, por precio).

**Parte B — ranking de más vendidos** (retoma la receta de acumular de la lección 03)

Datos:
```java
List<String> vendidos = List.of("webcam", "mouse", "teclado", "mouse", "webcam",
                                "pendrive", "mouse", "teclado", "monitor", "webcam");
```
5. Cuenta las ventas en un `Map<String, Integer> cuenta` (`LinkedHashMap`) con la versión que prefieras de la
   receta (`if/else`, `getOrDefault` o `merge`). Imprime. `// espero:`
6. Crea `src/practical/sorting/SalesEntryComparator.java` que implemente
   `Comparator<Map.Entry<String, Integer>>`:
   - primero por **cantidad, de mayor a menor**;
   - **si empatan**, por **nombre, de A a Z**.

   Aquí sí necesitas un `if`: guarda en un `int` el resultado de comparar las cantidades; si **no** es `0`,
   devuélvelo; si es `0`, devuelve la comparación de los nombres.
7. Pasa las entradas a una lista (sección 9.2), ordénala con tu comparador, y recórrela imprimiendo con la posición:
   ```
   1. xxxx — 3 ventas
   2. ...
   ```
   (Necesitas un contador `int posicion = 1;` que sumas en cada vuelta.) `// espero:` las 5 líneas exactas.
8. Imprime `cuenta` otra vez. `// espero:` ¿cambió el mapa?
9. Imprime `new TreeMap<>(cuenta)`. `// espero:` ¿por qué no es igual al ranking?

**Preguntas:**
- **P5.1** *"En la primera versión de `rank`, un `Laptop` entró en el `if` de ___ porque un `Laptop` también es
  un ___. La regla es preguntar de lo más ___ a lo más ___."*
- **P5.2** *"Para ordenar un `Map` por valor, primero ___, después ___. El mapa original ___."*
- **P5.3** *"`new TreeMap<>(cuenta)` ordena por ___, por eso ___."*

**Checklist:**
- [ ] primera versión de `rank` ejecutada **mal** a propósito, con su `espero`/`real`
- [ ] arreglo del orden de los `if`
- [ ] `SalesEntryComparator` con desempate por nombre
- [ ] ranking con posición, formato exacto
- [ ] P5.1–P5.3

---

### Tarea 6 — Entregable: `Catalog` ordenado por precio, nombre y tipo (avanzado · 45 min)

**Objetivo:** usar todo en una clase de dominio real, **sin romper la encapsulación**: los métodos que devuelven
"ordenado por…" no deben desordenar la lista interna ni dejar que la modifiquen desde fuera.
**Antes:** relee la sección 3.5 (copiar antes de ordenar) y la lección 03, sección 9 (`unmodifiable`).
**Archivos:** `src/Models/Catalog.java` (nuevo) y `src/practical/sorting/CatalogMain.java` (nuevo, con `main`).

**Requisitos de `Catalog`:**

| Método | Qué hace |
|--------|----------|
| campo `private final List<Product> products` | `ArrayList`, en **orden de inserción** |
| `boolean addProduct(Product product)` | si es `null` o **ya está** (mismo `id`) → `false` y no agrega; si no, agrega y `true` |
| `List<Product> getProducts()` | la lista en orden de inserción, **no modificable** |
| `List<Product> getSortedByPrice()` | de menor a mayor; empate → por nombre |
| `List<Product> getSortedByName()` | por nombre, sin importar mayúsculas |
| `List<Product> getSortedByType()` | por tipo (Tarea 5); dentro del tipo, por precio |
| `Product getCheapest()` | el más barato (`Collections.min`). ⚠️ Decide qué hacer si el catálogo está vacío y escríbelo en P6.3 |

**Restricciones:**
- Los `getSortedBy...` **no pueden** ordenar `products`: cada uno trabaja sobre una **copia**.
- Lo que devuelven **no se puede modificar** desde fuera.
- **Sin duplicar código:** los tres `getSortedBy...` hacen lo mismo con distinto comparador. Escribe **un**
  método privado `private List<Product> sortedCopy(Comparator<Product> comparator)` y que los tres lo llamen.
  (Un comparador es un objeto: se puede pasar como parámetro, como cualquier otro.)

**Pasos:**
1. Escribe `Catalog` cumpliendo la tabla y las restricciones.
2. En `CatalogMain`, agrega los 6 productos de la Tarea 3 **en ese orden**, y después intenta agregar
   `sillaGamer` (id 12) y `null`. Imprime lo que devuelve **cada** `addProduct`. `// espero:` (8 `boolean`).
3. **FOTO ANTES:** imprime los nombres de `getProducts()`. `// espero:`
4. Imprime `getSortedByPrice()`, `getSortedByName()` y `getSortedByType()`. `// espero:` en cada uno.
5. **FOTO DESPUÉS:** imprime otra vez `getProducts()`. `// espero:` debe ser **idéntica** a la foto antes. Si no
   lo es, tu `Catalog` está ordenando la lista interna.
6. Intenta `catalog.getSortedByPrice().add(mouse)` y `catalog.getProducts().sort(null)`, cada uno en su
   `try/catch`. `// espero:` ¿qué excepción en cada uno?
7. Imprime `getCheapest().getName()`. `// espero:` (hay empate en el más barato: ¿cuál sale y por qué?)
8. Crea un `Catalog` vacío y llama `getCheapest()`. `// espero:` lo que decidiste en el requisito.

**Preguntas:**
- **P6.1** *"`getSortedByPrice` copia antes de ordenar porque, si ordenara `products` directamente, ___. Lo
  comprobé en el paso ___."*
- **P6.2** *"Devuelvo la copia ordenada con `Collections.unmodifiableList(...)` para que ___. No uso
  `List.copyOf` porque ___ (o: también serviría porque ___)."* ← piensa: aquí la copia ya está ordenada; ¿pierde
  el orden `List.copyOf` con una `List`? Pruébalo antes de contestar.
- **P6.3** *"Si el catálogo está vacío, `getCheapest()` ___, porque decidí ___. En la semana 5 esto se hará
  mejor con ___."*
- **P6.4** *"`addProduct` detecta que `sillaGamer` ya está usando ___, que a su vez usa el método ___ de
  `Product`."*

**Checklist:**
- [ ] `products` nunca se ordena (foto antes = foto después)
- [ ] los 3 `getSortedBy...` usan `sortedCopy`
- [ ] lo devuelto no se puede modificar (paso 6)
- [ ] `addProduct` rechaza `null` y el id repetido
- [ ] caso vacío de `getCheapest` decidido y probado
- [ ] P6.1–P6.4

---

## 15. Resumen de la lección (se llena al cerrar)

| Tarea | Nota | Estado |
|-------|------|--------|
| Teoría + autoevaluación | | |
| 1 | | |
| 2 | | |
| 3 | | |
| 4 | | |
| 5 | | |
| 6 | | |
