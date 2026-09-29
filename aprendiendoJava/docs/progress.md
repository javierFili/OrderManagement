# Progreso — Java Learning Lab

## ▶ TAREA EN CURSO (retomar aquí)

**Estado:** asignada el 2026-09-29, **aún no empezada**. Siguiente paso: implementarla y pedir "revisa".

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

* equals() / hashCode()
* Set, HashSet, LinkedHashSet, TreeSet
* Map, HashMap
* Comparable / Comparator
* records, Optional (Fase 1, sin evidencia en el código)

### Siguiente

Collections → `equals()` / `hashCode()` en `Product` + `HashSet`
(tarea: `practical/EqualsHashCodeLab.java`)
