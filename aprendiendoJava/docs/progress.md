# Progreso — Java Learning Lab

> Plan completo (26 semanas, 28 sep 2026 → 28 mar 2027): `docs/roadmap.md` · **Semana actual: 1**

## ▶ TAREA EN CURSO (retomar aquí)

**Estado:** entregada el 2026-10-01 → **code review hecho, las 5 tareas en 🔁 rehacer.**
Detalle por paso al final de `docs/lessons/01-list-arraylist.md` (sección 5).

**Tema:** `List` / `ArrayList` — lección completa en `docs/lessons/01-list-arraylist.md`

| Tarea | Archivo | Nota | Estado |
|-------|---------|------|--------|
| 1. Operaciones básicas | `src/practical/lists/ListBasics.java` | 7/10 | 🟡 casi: corregir pasos 7–10 y P1.3 |
| 2. Recorrer y eliminar (trampas) | `src/practical/lists/ListIteration.java` | 5/10 | 🔁 rehacer |
| 3. `List<Product>` + BigDecimal | `src/practical/lists/ProductListLab.java` + `Product` | 5/10 | 🔁 rehacer |
| 4. Aplicarlo a `Order` (encapsulación) | `Order`, `OrderItem`, `Main` | 5/10 | 🔁 rehacer |
| 5. Puente a `equals` | `src/practical/lists/ContainsLab.java` | 3/10 | 🔁 rehacer |

**Conceptos a reforzar:** valor de retorno vs efecto (`set`/`remove`), `ConcurrentModificationException` (iterator oculto del for-each),
sobrecarga `remove(int)` vs `remove(Object)`, `final` (referencia vs objeto), contrato de `compareTo` (solo el signo).

Siguiente paso: rehacer (empezar por 1, 2 y 5) y pedir "revisa tarea N".

---

## ⏭ SIGUIENTE TAREA (ya diseñada, no empezar hasta cerrar List)

**Tema:** `equals()` / `hashCode()` + `HashSet`

**Motivación desde el código:** `order.addItem(laptop, 3); order.addItem(laptop, 2);` crea dos
`OrderItem`, y dos `Product` con el mismo id hoy son "distintos" para Java.

**Parte A — Predecir y observar.** Crear `src/practical/EqualsHashCodeLab.java`:
1. Dos `Laptop` con el mismo id y mismos datos, y un tercero con otro id.
2. Imprimir `a == b`, `a.equals(b)`, `a.hashCode() == b.hashCode()`.
3. Meter los tres en un `HashSet<Product>` e imprimir `size()`.
4. Crear un cuarto `Laptop` con el mismo id que `a` y probar `set.contains(cuarto)`.
5. **Antes de ejecutar**, escribir en comentarios qué se espera en cada línea.

**Parte B — Solo `equals`.** Sobrescribir únicamente `equals()` en `Product` (basado en `id`).
Ejecutar de nuevo y anotar qué cambió y qué **no**.

**Parte C — `hashCode`.** Agregar `hashCode()`, ejecutar y comparar los tres resultados.

**Parte D —** Corregir `isValidValue` en `Product` (usa `floatValue()`; no valida `null`).

**Preguntas (responder en comentarios):**
1. ¿Por qué en la Parte B el `HashSet` se comportó así?
2. ¿Un `PhysicalProduct` id 1 debería ser `equals` a un `Laptop` id 1?
   `getClass() != o.getClass()` vs `instanceof`: ¿cuál y por qué? (pensar en simetría)
3. ¿Qué debería pasar con el campo `id` para que `hashCode` no se rompa? (pista: `final`)

**Restricciones:** escribir `equals`/`hashCode` a mano (sin generar con IntelliJ, sin Lombok;
`java.util.Objects` permitido en `hashCode`). **No** tocar `Order` todavía (será la siguiente tarea, con `Map`).

**Criterios:** contrato de `equals` (incluido `null`), `hashCode` coherente con `equals`,
decisión `getClass`/`instanceof` justificada, predicciones vs resultados reales.

**Entregar:** `src/practical/EqualsHashCodeLab.java` + `src/Models/Product.java` modificado.

**Después:** `Order` sin items duplicados → `Map<Product, OrderItem>` / `LinkedHashMap`.

---

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
