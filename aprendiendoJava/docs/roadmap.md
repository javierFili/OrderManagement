# Roadmap — de Java universitario a desarrollador Spring Boot

**Presupuesto:** 26 semanas × 5 h = **~130 horas** · **Inicio:** 28 sep 2026 · **Fin:** 28 mar 2027

**Objetivo realista:** en 6 meses, una **base sólida de backend Java + Spring Boot**: poder trabajar
en un equipo profesional, pasar entrevistas técnicas junior/mid y construir una API real con tests,
seguridad y Docker. Llegar a *senior* requiere, además, experiencia en producción (años de problemas
reales). Este plan construye los cimientos correctos para llegar ahí sin tener que desaprender.

**Ventaja con la que partes:** ya trabajas profesionalmente con HTTP, REST, SQL y MVC (Laravel).
Por eso los temas de Spring "web" duran poco y el tiempo se invierte en lo que Java hace distinto:
tipos, colecciones, generics, streams, testing y JPA.

---

## Cómo usar este índice

- Una semana = un tema = ~5 h, repartidas en 2 o 3 sesiones:
  - 1 h de teoría y ejemplo
  - 2,5 h de tareas
  - 1 h de code review y corrección
  - 0,5 h de repaso y actualización de `progress.md`
- **Solo se avanza si se cumplen los criterios de salida del bloque.** Si no, se usa una semana colchón.
- Estado de cada semana: `⬜ pendiente` · `▶ en curso` · `✅ completado` · `⏸ recortado/pospuesto`.
- El detalle de cada tema vive en `docs/lessons/NN-tema.md`, y el día a día en `docs/progress.md`.
- Las fechas son una **guía**, no un contrato. Si una semana se pierde, se mueve todo una semana
  y se recorta desde el final (ver "Si me atraso").

---

## Bloque 0 — Ya practicado (repaso dentro de las tareas, sin semana propia)

Clases, constructores, encapsulación, herencia, clases abstractas, interfaces, polimorfismo,
composición, enums básicos y `BigDecimal`. Los errores detectados en el diagnóstico
(`@Override`, duplicación, paquetes en mayúscula, dos diseños de envío) se corrigen durante las
tareas de los bloques siguientes, no con lecciones aparte.

---

## Bloque 1 — Java fundamental y Collections · Sem 1–6 · 30 h

| Sem | Inicio | Tema | Conceptos | Entregable / aplicación | Estado |
|-----|--------|------|-----------|-------------------------|--------|
| 1 | 28 sep | `List` / `ArrayList` | interfaz vs implementación, generics básicos, recorridos, trampas, encapsulación de colecciones | [Lección 01](lessons/01-list-arraylist.md) · `Order` con detalle por producto | ✅ con deuda (ver `progress.md`) |
| 2 | 05 oct | `equals` / `hashCode` + `Set` | contrato, `getClass` vs `instanceof`, `HashSet`, `LinkedHashSet`, `TreeSet` | [Lección 02](lessons/02-equals-hashcode-set.md) · `Order.getDistinctProducts()` | ▶ |
| 3 | 12 oct | `Map` | `HashMap`, `LinkedHashMap`, `TreeMap`, `getOrDefault`, `merge`, recorrer entradas | `Order` sin items duplicados (`Map<Product, OrderItem>`) | ⬜ |
| 4 | 19 oct | Ordenar | `Comparable`, `Comparator`, `comparing().thenComparing()`, `List.sort` | catálogo ordenado por precio, nombre y tipo | ⬜ |
| 5 | 26 oct | Java moderno | `record`, enums con comportamiento, `Optional`, inmutabilidad, `var`, `switch` moderno | `findByName` devuelve `Optional`; `OrderStatus` como enum con reglas | ⬜ |
| 6 | 02 nov | Excepciones + fechas | checked vs unchecked, excepciones de dominio propias, try-with-resources, `java.time` | `InsufficientStockException`, `Renewal` con `LocalDate` real | ⬜ |

**Criterios de salida (responder sin mirar):**
- ¿Cuándo `List`, cuándo `Set` y cuándo `Map`? ¿Qué implementación elegirías por defecto y por qué?
- Explica el contrato `equals`/`hashCode` y qué se rompe si no lo cumples.
- ¿`record` o clase? ¿Cuándo **no** usar `Optional`?
- ¿Checked o unchecked para un error de negocio? Justifícalo.

---

## Bloque 2 — Generics y Java funcional · Sem 7–10 · 20 h

| Sem | Inicio | Tema | Conceptos | Entregable / aplicación | Estado |
|-----|--------|------|-----------|-------------------------|--------|
| 7 | 09 nov | Generics | clases y métodos genéricos, bounded types, wildcards, PECS (lo esencial) | `InMemoryRepository<T, ID>`: el origen del `JpaRepository<T, ID>` de Spring | ⬜ |
| 8 | 16 nov | Lambdas | `Predicate`, `Function`, `Supplier`, `Consumer`, method references | decidir entre `Shippable` y `ShippingStrategy` y quedarse con **un** diseño de envío | ⬜ |
| 9 | 23 nov | Streams I | `filter`, `map`, `reduce`, `collect`, `sum` con `BigDecimal`, laziness | rehacer `ProductListLab` con streams y comparar legibilidad | ⬜ |
| 10 | 30 nov | Streams II | `groupingBy`, `toMap`, `flatMap`, `partitioningBy`, DTOs con `record` | reporte de ventas: total por tipo de producto y por cliente | ⬜ |

**Criterios de salida:**
- Escribe la firma de un método genérico que acepte `List<Laptop>` donde se espera productos.
- ¿Cuándo un bucle es mejor que un stream?
- Convierte a streams un bucle con `if` + acumulador, **sin** efectos secundarios.

---

## Bloque 3 — Herramientas, testing y calidad · Sem 11–14 · 15 h + colchón

| Sem | Inicio | Tema | Conceptos | Entregable / aplicación | Estado |
|-----|--------|------|-----------|-------------------------|--------|
| 11 | 07 dic | Maven + JUnit 5 | `pom.xml`, dependencias, ciclo de vida, estructura `src/main` / `src/test`, primeros tests | migrar el lab a Maven; tests de `Order` | ⬜ |
| 12 | 14 dic | Testing a fondo | tests parametrizados, excepciones, AssertJ, Mockito, TDD | kata TDD: `Discount` (porcentaje, fijo, por cantidad) | ⬜ |
| 13 | 21 dic | SOLID + Clean Code · **Checkpoint 1** | SRP, OCP, DIP con ejemplos reales, nombres, métodos pequeños, eliminar duplicación | **Order Management en Java puro**: dominio completo + tests | ⬜ |
| 14 | 28 dic | **Colchón** (fiestas) | terminar lo pendiente, sin tema nuevo | — | ⬜ |

**Criterios de salida:**
- `mvn test` pasa y el dominio tiene tests de los casos felices y de error.
- Explica qué testearías de `Order` y qué **no** vale la pena testear.
- Señala un ejemplo de DIP en tu propio código.

> ¿Por qué Maven aquí y no antes? Porque es cuando aparece una razón concreta: necesitas JUnit.
> Hasta entonces, `javac` basta.

---

## Bloque 4 — Spring Boot · Sem 15–23 · 45 h

| Sem | Inicio | Tema | Conceptos | Entregable / aplicación | Estado |
|-----|--------|------|-----------|-------------------------|--------|
| 15 | 04 ene | Fundamentos | Spring Initializr, IoC/DI, beans, `@Service`, inyección por constructor, `application.yml`, profiles | comparar con el Service Container de Laravel; primer proyecto | ⬜ |
| 16 | 11 ene | REST | `@RestController`, verbos, status codes, `ResponseEntity`, DTOs de entrada y salida | CRUD de productos en memoria (reusa el `InMemoryRepository`) | ⬜ |
| 17 | 18 ene | Validación + errores | Bean Validation, `@RestControllerAdvice`, `ProblemDetail` | reusar las excepciones de dominio de la semana 6 | ⬜ |
| 18 | 25 ene | JPA I | PostgreSQL, `@Entity`, Spring Data, query methods, Flyway | pasar de memoria a base de datos (comparar con Eloquent y migraciones) | ⬜ |
| 19 | 01 feb | JPA II | relaciones, lazy/eager, N+1, paginación, `equals`/`hashCode` en entidades | `Order` → `OrderItem` → `Product` persistidos | ⬜ |
| 20 | 08 feb | Transacciones | `@Transactional`, rollback, propagación básica | crear pedido descontando stock de forma atómica | ⬜ |
| 21 | 15 feb | Testing en Spring | `@WebMvcTest`, `@DataJpaTest`, `@SpringBootTest`, Testcontainers | tests de controlador, repositorio e integración | ⬜ |
| 22 | 22 feb | Security I | filtros, autenticación, `PasswordEncoder`, JWT | login + endpoints protegidos | ⬜ |
| 23 | 01 mar | Security II | autorización por roles, CORS, tests de seguridad | ADMIN gestiona el catálogo; CUSTOMER crea sus pedidos | ⬜ |

**Criterios de salida:**
- Explica qué hace Spring al arrancar: escaneo de componentes, creación de beans e inyección.
- ¿Por qué no devolver entidades JPA directamente en un endpoint?
- Detecta y corrige un N+1 en tu propio proyecto.
- ¿Qué pasa si una excepción ocurre dentro de un método `@Transactional`?

---

## Bloque 5 — Producción y proyecto final · Sem 24–26 · 15 h

| Sem | Inicio | Tema | Conceptos | Entregable / aplicación | Estado |
|-----|--------|------|-----------|-------------------------|--------|
| 24 | 08 mar | Docker + operación | Dockerfile, docker-compose (app + Postgres), logging, Actuator, OpenAPI | `docker compose up` levanta todo | ⬜ |
| 25 | 15 mar | Concurrencia esencial | threads, `ExecutorService`, `CompletableFuture`, virtual threads, condición de carrera en el stock | demostrar y corregir una venta doble (locking optimista `@Version`) | ⬜ |
| 26 | 22 mar | **Checkpoint final** | README, revisión de arquitectura, **entrevista técnica simulada** | **Order Management API** completa | ⬜ |

**Proyecto final (Order Management API):** productos, clientes, pedidos, stock, descuentos,
autenticación JWT con roles, validación, manejo de errores, PostgreSQL + Flyway, tests unitarios
y de integración, Docker y documentación OpenAPI. Es un proyecto de portafolio.

---

## Si me atraso

Recortar en este orden (de lo menos a lo más crítico para un primer puesto con Spring):
1. Semana 25 (concurrencia) → pasa a "después de los 6 meses"
2. Semana 23 (Security II) → dejar solo roles básicos
3. Semana 10 (Streams II) → fusionar con la semana 9

**No recortar nunca:** Collections, testing, JPA y transacciones.

---

## Después de los 6 meses (fuera de alcance, a propósito)

Arquitectura hexagonal/DDD a fondo · caching (Redis) · mensajería (Kafka/RabbitMQ) ·
microservicios · CI/CD · JVM, memoria y GC a fondo · Kubernetes · programación reactiva.
Tienen sentido **sobre** la base de este plan, no antes.
