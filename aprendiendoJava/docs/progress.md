# Progreso — Java Learning Lab

> Plan completo (26 semanas, 28 sep 2026 → 28 mar 2027): `docs/roadmap.md` · **Semana actual: 2**

## ▶ TAREA EN CURSO (retomar aquí)

**Tema:** `equals()` / `hashCode()` + `Set` (semana 2). Lección completa en `docs/lessons/02-equals-hashcode-set.md`.
**Estado:** asignada el 2026-10-02, **no empezada**. Se avanzó a pedido del alumno con deuda de la lección 01 (ver abajo).

| Tarea | Archivo | Estado |
|-------|---------|--------|
| 0. Calentamiento `Set<String>` | `src/practical/sets/SetBasics.java` | pendiente |
| 1. Ronda 1: sin `equals` | `src/practical/sets/EqualsHashCodeLab.java` | pendiente |
| 2. Ronda 2: solo `equals` | `Product.java` + `EqualsHashCodeLab` | pendiente |
| 3. Ronda 3: `hashCode` + `id` final + `isValidValue` | `Product.java` + `EqualsHashCodeLab` | pendiente |
| 4. `getClass` vs `instanceof` | `src/practical/sets/EqualityTypeLab.java` | pendiente |
| 5. `HashSet` / `LinkedHashSet` / `TreeSet` | `src/practical/sets/SetOrderLab.java` | pendiente |
| 6. `Order.getDistinctProducts()` | `Order.java` + `Main.java` | pendiente |

El alumno pidió tareas muy detalladas, paso a paso (dice que le cuesta entender rápido): mantener ese nivel de detalle.

### Deuda de la lección 01 (no bloquea; parte se practica en la lección 02)

| Tarea L01 | Estado | Pendiente |
|-----------|--------|-----------|
| 1. `ListBasics` | 🟡 7/10 | `porque` de predicciones falladas (pasos 7–10), P1.3 |
| 2. `ListIteration` | 🔁 6/10 | *off-by-one* paso 2, P2.2 (sobrecarga `remove`), predicciones |
| 3. `ProductListLab` | 🔁 6/10 | ternario invertido, P3.1–P3.4 (`BigDecimal(double)`, inmutabilidad) |
| 4. `Order` | 🔁 7/10 | P4.2 (`final`), paso 13, justificar `copyOf` |
| 5. `ContainsLab` | 🟡 7/10 | P5.1/P5.2 → se cubren en la Tarea 1 de la lección 02 |

Detalle en los bloques 🔍 REVISIÓN de `docs/lessons/01-list-arraylist.md`.

---

## 2026-10-02 — Paso a la lección 02 (con deuda)

* El alumno pide avanzar sin cerrar la lección 01. Se respeta; la deuda queda registrada arriba y los
  conceptos (`final`, `BigDecimal.compareTo`, valor de retorno vs efecto, `equals` de `Object`) se reutilizan
  en las tareas de la lección 02.
* Creada `docs/lessons/02-equals-hashcode-set.md` (reemplaza el diseño breve de `EqualsHashCodeLab` que estaba aquí).

## 2026-10-02 — 2.ª revisión lección 01

### Mejoró
* experimento de `ContainsLab` corregido, predicciones correctas (3 → 7)
* `OrderItem` valida `null` y cantidad (*fail fast*); detalle con `getSubTotalPrice` (DRY); encapsulación demostrada
* explicó correctamente la `ConcurrentModificationException` (iterator oculto, salta en `next()`)
* `compareTo > 0`, contador para numerar, constante fuera del bucle

### Errores detectados
* predijo que `set` inserta y devuelve el valor nuevo → cascada de predicciones falladas sin reconciliar
* *off-by-one*: `for (i < size() - 1)` no imprime el último
* ternario invertido en `findByName` (imprime "no existe!" para un producto que existe) sin notarlo
* P3.2: cree que `BigDecimal` solo acepta `String` (no conoce el problema de `double` binario)
* P2.2 (sobrecarga) y P4.2 (`final`) sin responder de nuevo; paso 13 de la Tarea 4 sin hacer
* repite `Integer sum` y `ArrayList<Integer>` como tipo declarado

## 2026-10-01 — Code review lección 01 (`List`/`ArrayList`)

### Bien hecho
* `BigDecimal`: `ZERO`, `total = total.add(...)`, constructor con String
* `Iterator` + `it.remove()`, `removeIf`, `List.copyOf` en `getItems()`, `instanceof` + cast
* `getItemCount` / `getTotalUnits` / `removeItem` con validación de índice

### Errores detectados
* ninguna predicción `// espero:` en las 5 tareas; varios sub-pasos sin hacer
* P2.1: cree que la CME se evita por `hasNext()` (no conoce el iterator oculto del for-each)
* P2.2: no identificó la sobrecarga `remove(int)` vs `remove(Object)`
* P4.2: `final` explicado de forma ambigua (no distingue referencia de contenido)
* `compareTo(...) >= 1` en vez de `> 0`
* `findByName` lanza excepción con lista vacía (excepción para un resultado normal)
* lógica de subtotal duplicada en `Main`; encapsulación no demostrada (paso 12)
* Tarea 5: agregó ambos laptops → experimento invalidado
* declaración `ArrayList<Integer> t1` (implementación en vez de interfaz); `Integer` como acumulador

### Siguiente
Rehacer las tareas 1–5 de la lección 01 → luego semana 2 (`equals`/`hashCode` + `Set`).

## 2026-09-29 — Replanificación: volver a `List`

* El alumno indica que se saltó `List`/`ArrayList` (solo introducción). Se pospone `equals/hashCode`.
* Revisión del código relacionado con listas (hallazgos que cubren las tareas de la lección 01):
  * `Order` no expone sus items → el "detalle por producto" de `Main` no se puede implementar
  * `OrderItem` sin getters, no valida `product == null`, mensaje "cannot be negative" pero rechaza 0
  * `Order`: `new ArrayList<OrderItem>()` sin diamond, lista no `final`, `new BigDecimal("0")` en vez de `ZERO`
  * `Product` sin `getId()` ni `toString()` → imprimir una `List<Product>` muestra `Laptop@1b6d3586`
  * `CollectiosPolimorfismo`: variable `listProducts` para una `List<Shippable>` (nombre engañoso)
* Entorno: en la terminal `javac` es 21 pero `java` es 11 → `UnsupportedClassVersionError`
  (ver `CLAUDE.md` → Entorno).

## 2026-09-29 — Diagnóstico inicial (a partir del código existente)

> Estado inferido de inspeccionar el código y los commits (`poo`, `update`, `collections`).
> No hubo code review previo: esto es un punto de partida, no un certificado de dominio.

### Practicado (según el código)

* clases, constructores, encapsulación básica (campos `private`, getters)
* validación en constructor con `IllegalArgumentException`
* `BigDecimal` / `BigInteger`
* herencia: `Product` → `PhysicalProduct` → `Laptop`, `PhysicalBook`, `Furnuture`
* interfaces: `Shippable`, `Downloadable`, `Renewable`, `ShippingStragy`
* enum: `RenewalStatus`
* composición: `Order` → `List<OrderItem>` → `Product`
* polimorfismo con colecciones: `List<Shippable>` (`practical/CollectiosPolimorfismo`)
* primer intento de Strategy (`ShippingStragy`, `PhysicalShipping`, `NoShipping`) — no conectado aún

### Errores detectados

* `Product.isValidValue` compara con `floatValue()` → pierde precisión y no valida `null`
* `setPrice` devuelve `false` en silencio mientras el constructor lanza excepción (inconsistencia)
* subclases redeclaran `implements Shippable` y sobrescriben sin `@Override`
* lógica duplicada en `Laptop`, `PhysicalBook`, `Furnuture` (`price + weight`, suma unidades distintas)
* dos diseños de envío en paralelo (`Shippable` vs `ShippingStragy`) sin decidir
* comentario: "heredan comportamiento" como explicación del polimorfismo con interfaces (es subtipado + dynamic dispatch)
* convenciones: paquetes en mayúscula (`Models`, `Interfaces`), typos en nombres de clases

### Pendiente

* List / ArrayList a fondo (lección 01, en curso)
* equals() / hashCode()
* Set, HashSet, LinkedHashSet, TreeSet
* Map, HashMap
* Comparable / Comparator
* records, Optional (Fase 1, sin evidencia en el código)

### Siguiente

~~Collections → `equals()` / `hashCode()`~~ → reemplazado: primero `List`/`ArrayList` (ver TAREA EN CURSO)
