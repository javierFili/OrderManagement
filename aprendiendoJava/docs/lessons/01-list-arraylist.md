# Lección 01 — `List` y `ArrayList`

> Ruta: Fase 3 (Collections), primer tema.
> Antes de `equals()`/`hashCode()`, `Set` y `Map`.
> Todos los comportamientos descritos aquí fueron comprobados ejecutando código en Java 21.

---

## 0. Puente desde PHP

En PHP, `array` sirve para todo: lista (`[1, 2, 3]`), diccionario (`['id' => 1]`), pila, cola...

Java separa esas ideas en **tipos distintos**, cada uno con su contrato:

| Necesito...                               | PHP                  | Java                |
|-------------------------------------------|----------------------|---------------------|
| elementos en orden, con posición (índice) | `$a = [];`           | `List<T>`           |
| elementos sin repetir                     | `array_unique(...)`  | `Set<T>` (después)  |
| clave → valor                             | `['k' => 'v']`       | `Map<K, V>` (después) |

Hoy solo vemos `List`.

---

## 1. Teoría

### 1.1 `List` (interfaz) vs `ArrayList` (clase)

**CONCEPTO**
- `List<T>` es una **interfaz**: un contrato. Dice *qué* se puede hacer: agregar, obtener por
  índice, eliminar, recorrer... Mantiene el **orden de inserción** y **permite duplicados**.
- `ArrayList<T>` es una **implementación** de ese contrato. Dice *cómo* se hace: por dentro
  usa un array normal (`Object[]`) que crece solo cuando se llena.

```java
List<String> names = new ArrayList<>();
//  ^ tipo declarado (interfaz)   ^ objeto real (implementación)
```

**POR QUÉ declarar con `List` y no con `ArrayList`**
Es lo mismo que ya hiciste con `List<Shippable>`: el código que usa la variable solo conoce el
contrato. Si mañana cambias `ArrayList` por `LinkedList`, solo cambia **una** línea.

**PROFESIONALMENTE / SPRING**: esta idea ("programa contra la interfaz, no contra la implementación")
es la base de la inyección de dependencias en Spring.

### 1.2 Generics básicos: `<T>`

`List<Product>` significa "una lista que **solo** acepta `Product` (o subclases)".

- El compilador te protege: `products.add("hola")` **no compila**.
- Al hacer `get`, no necesitas cast: `Product p = products.get(0);`
- **Diamond `<>`**: en `new ArrayList<>()` Java deduce el tipo desde la izquierda.
  `new ArrayList<OrderItem>()` (como está hoy en `Order`) funciona, pero es redundante.

**No se permiten primitivos**: `List<int>` no compila. Se usa la clase envoltorio:
`int → Integer`, `double → Double`, `boolean → Boolean`, `long → Long`.
Java convierte automáticamente (`autoboxing`): `numbers.add(5)` guarda un `Integer`.

### 1.3 Métodos que DEBES conocer

| Método                  | Qué hace                                       | Devuelve             | Ojo con...                         |
|-------------------------|------------------------------------------------|----------------------|------------------------------------|
| `add(e)`                | agrega al final                                | `boolean` (true)     |                                    |
| `add(i, e)`             | inserta en la posición `i`, desplaza el resto  | `void`               | `i` fuera de rango → excepción     |
| `get(i)`                | elemento en la posición `i`                    | `T`                  | índices van de `0` a `size() - 1`  |
| `set(i, e)`             | reemplaza el elemento en `i`                   | el elemento **anterior** | no agrega, reemplaza           |
| `remove(int i)`         | elimina por **posición**                       | el elemento eliminado |  ver trampa 1.5                   |
| `remove(Object o)`      | elimina la **primera** aparición de `o`        | `boolean`            | usa `equals()`                     |
| `size()`                | cantidad de elementos                          | `int`                | no es `length` ni `count()`        |
| `isEmpty()`             | ¿`size() == 0`?                                | `boolean`            |                                    |
| `contains(o)`           | ¿existe `o`?                                   | `boolean`            | usa `equals()`                     |
| `indexOf(o)`            | posición de la primera aparición               | `int` (o `-1`)       | usa `equals()`                     |
| `clear()`               | vacía la lista                                 | `void`               |                                    |
| `removeIf(condición)`   | elimina los que cumplen la condición           | `boolean`            | recibe una lambda (adelanto)       |

`System.out.println(lista)` imprime `[a, b, c]`, llamando a `toString()` de cada elemento.
Si el elemento no tiene `toString()` propio, verás algo como `Models.products.Laptop@1b6d3586`.

### 1.4 Tres formas de recorrer

```java
// 1) for con índice: cuando necesitas la posición
for (int i = 0; i < names.size(); i++) {
    System.out.println(i + ": " + names.get(i));
}

// 2) for-each: la más común y legible (la que usas en Order.getTotalPrice)
for (String name : names) {
    System.out.println(name);
}

// 3) Iterator: cuando necesitas ELIMINAR mientras recorres
Iterator<String> it = names.iterator();
while (it.hasNext()) {
    String name = it.next();
    if (name.startsWith("A")) {
        it.remove();   // el iterator elimina de forma segura
    }
}
```

### 1.5 Trampas clásicas (comprobadas)

**Trampa 1 — `remove(int)` vs `remove(Object)` con `List<Integer>`**
```java
List<Integer> n = new ArrayList<>(List.of(10, 20, 30));
n.remove(1);                    // elimina la POSICIÓN 1 → [10, 30]
n.remove(Integer.valueOf(10));  // elimina el VALOR 10   → [30]
```

**Trampa 2 — Eliminar dentro de un for-each → `ConcurrentModificationException`**
```java
List<Integer> m = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
for (Integer x : m) {
    if (x % 2 == 0) m.remove(x);   // 💥 ConcurrentModificationException
}
```
El for-each usa un `Iterator` por debajo. Si modificas la lista "a sus espaldas", el iterator
lo detecta y lanza la excepción. Solución: `Iterator.remove()` o `removeIf(...)`.

**Trampa 3 — `List.of(...)` es inmutable**
```java
List<Integer> fixed = List.of(1, 2, 3);
fixed.add(4);   // 💥 UnsupportedOperationException
// Si quieres una copia modificable:
List<Integer> editable = new ArrayList<>(List.of(1, 2, 3));
```
Además `List.of` no acepta `null` (lanza `NullPointerException`).
`Arrays.asList(...)` es un caso intermedio: permite `set`, pero **no** `add`/`remove`.

**Trampa 4 — `IndexOutOfBoundsException`**
Con 3 elementos, los índices válidos son `0, 1, 2`. `get(3)` lanza excepción.
En PHP `$a[3]` da `null` + warning; en Java **falla**.

**Trampa 5 — `contains`, `indexOf` y `remove(Object)` usan `equals()`**
Si tu clase no sobrescribe `equals()`, dos objetos "iguales" (mismo id) se consideran
**distintos**. Esto es el puente a la lección de `equals/hashCode`.

### 1.6 ¿Cómo funciona `ArrayList` por dentro? (lo justo)

- Guarda los elementos en un array interno. Capacidad inicial: 10 (al primer `add`).
- Cuando se llena, crea un array ~50 % más grande y copia todo.
- Consecuencias:
  - `get(i)` es **muy rápido** (acceso directo por posición) → O(1).
  - `add(e)` al final: normalmente rápido.
  - `add(0, e)` o `remove(0)`: **lento** en listas grandes, porque desplaza todos los elementos → O(n).
  - `contains(o)`: recorre la lista entera en el peor caso → O(n).
- `LinkedList` existe, pero en la práctica `ArrayList` es la opción por defecto en el 95 % de los casos.

### 1.7 Encapsulación: no regales tu lista interna

**CONCEPTO**: si `Order` tiene `private List<OrderItem> orderItems` y haces
`public List<OrderItem> getItems() { return orderItems; }`, entonces **cualquiera** puede hacer
`order.getItems().clear()` o `order.getItems().add(...)` saltándose tus validaciones de `addItem`.
El `private` no sirvió de nada.

**Opciones**:
| Forma                                   | Qué devuelve                                    |
|-----------------------------------------|-------------------------------------------------|
| `Collections.unmodifiableList(items)`   | una **vista** de solo lectura (ve cambios futuros de la original) |
| `List.copyOf(items)`                    | una **copia** inmutable (foto del momento)      |

Ambas lanzan `UnsupportedOperationException` si alguien intenta modificarlas.

**PROFESIONALMENTE**: en un backend real, un agregado como `Order` controla sus items; nadie los
modifica desde fuera sin pasar por los métodos de `Order`. Lo verás en JPA/DDD.

### 1.8 Errores comunes (resumen)

1. Declarar `ArrayList<X> x = new ArrayList<>()` en vez de `List<X>`.
2. Usar `==` para comparar elementos (compara referencias, no contenido).
3. Olvidar que los índices empiezan en 0.
4. Eliminar dentro de un for-each.
5. Devolver la lista interna desde un getter.
6. Confundir `remove(int)` con `remove(Object)`.
7. Intentar modificar un `List.of(...)`.

---

## 2. Ejemplo pequeño (léelo, no lo copies)

```java
List<String> cities = new ArrayList<>();
cities.add("La Paz");
cities.add("Cochabamba");
cities.add(0, "Sucre");                 // [Sucre, La Paz, Cochabamba]
String old = cities.set(1, "Oruro");    // old = "La Paz" → [Sucre, Oruro, Cochabamba]
System.out.println(cities.size());      // 3
System.out.println(cities.contains("La Paz")); // false
System.out.println(cities.indexOf("Cochabamba")); // 2
cities.remove("Sucre");                 // [Oruro, Cochabamba]
```

---

## 3. Tareas (de fácil a avanzado)

**Reglas para todas las tareas**
- Crea cada archivo en `src/practical/lists/` con `package practical.lists;` y un `main`.
- **Antes de ejecutar**, escribe al lado de cada `println` un comentario `// espero: ...`.
  Después de ejecutar, si fallaste, agrega `// real: ... porque ...`. Equivocarse está bien:
  es la parte donde se aprende.
- No uses Streams (`.stream()`, `.filter()`, `.map()`...). Solo bucles. Excepción: `removeIf` en la Tarea 2.
- No uses IA ni el autocompletado de IntelliJ para generar la lógica.
- Cuando termines **una** tarea, puedes pedir "revisa tarea N" sin esperar a terminar todas.

Compilar y ejecutar (desde `aprendiendoJava/`, ver nota de entorno en `CLAUDE.md`):
```
javac -d out/cli $(find src -name '*.java') && java -cp out/cli practical.lists.NombreDeLaClase
```

---

### Tarea 1 — Operaciones básicas (fácil)

**Archivo:** `src/practical/lists/ListBasics.java`

**Pasos (en este orden, uno debajo del otro):**
1. Crea una `List<String>` llamada `names` usando `ArrayList` y el diamond `<>`.
2. Imprime `names.isEmpty()` y `names.size()`.
3. Agrega 5 nombres: `"Ana"`, `"Luis"`, `"Marta"`, `"Pedro"`, `"Ana"` (sí, "Ana" dos veces).
4. Imprime la lista completa con `System.out.println(names)`.
5. Imprime el **primer** elemento y el **último**. Para el último usa `size() - 1`
   (no escribas el número 4 a mano).
6. Inserta `"Carlos"` en la posición `2`. Imprime la lista.
7. Reemplaza el elemento de la posición `0` por `"Sofía"` y guarda en una variable lo que devuelve
   `set`. Imprime esa variable y la lista.
8. Imprime `names.contains("Ana")` y `names.indexOf("Ana")`.
9. Elimina `"Ana"` usando `remove(Object)`. Imprime la lista. ¿Se eliminaron las dos "Ana"?
10. Elimina el elemento de la posición `1` usando `remove(int)`. Imprime qué elemento devolvió y la lista.
11. Dentro de un `try/catch (IndexOutOfBoundsException e)`, intenta `names.get(names.size())`
    e imprime `e.getMessage()` en el `catch`.
12. Llama a `clear()` e imprime `isEmpty()`.

**Preguntas (en comentarios al final del archivo):**
- P1.1 ¿Por qué `get(names.size())` falla?
- P1.2 ¿Qué diferencia hay entre `add(2, "Carlos")` y `set(2, "Carlos")`?
- P1.3 En el paso 9, ¿cuántas "Ana" se eliminaron y por qué?

**Criterios:** los 12 pasos presentes y en orden, predicciones escritas antes de ejecutar, declaración `List<String> = new ArrayList<>()`.


> ## 🔍 REVISIÓN TAREA 1 — 2026-10-02 · **7/10** · 🟡 casi
> Historial: v1 6/10 → v2 7/10 → **v3 7/10**. Revisado ejecutando `ListBasics`.

**✅ Ya está bien:** pasos 1–6, 11 y 12. `contains` agregado en el paso 8. Imprimes la lista después de los
pasos 9 y 10. P1.1 y P1.2 correctas. La predicción del valor devuelto en el paso 10 (`luis`) acertó.

**Tus predicciones contra la salida real:**

| Paso | Línea | Tu predicción | Salida real | |
|------|-------|---------------|-------------|---|
| 7 | lo que devuelve `set(0, "sofia")` | `sofia` | `ana` | ❌ |
| 7 | la lista | 7 elementos `[sofia, ana, luis, ...]` | `[sofia, luis, carlos, Marta, Pedro, ana]` (6) | ❌ |
| 8 | `contains("ana")` | `true` | `true` | ✅ |
| 8 | `indexOf("ana")` | `1` | `5` | ❌ sin `porque` |
| 9 | lo que devuelve `remove("ana")` | (`//true`, sin `espero:`) | `true` | ⚠️ |
| 9 | la lista | `[sofia, luis, carlos, Marta, Pedro, ana]` | `[sofia, luis, carlos, Marta, Pedro]` | ❌ sin `real` |
| 10 | lo que devuelve `remove(1)` | `luis` | `luis` | ✅ |
| 10 | la lista | `[sofia, carlos, Marta, Pedro, ana]` | `[sofia, carlos, Marta, Pedro]` | ❌ sin `real` |

---

#### ❌ CRÍTICO 1 — `set` reemplaza y devuelve el valor **anterior**

**Qué pasa:** predijiste que `set(0, "sofia")` devuelve `"sofia"` (el valor nuevo) y que la lista crece a 7.
Las dos cosas son falsas. Devuelve `"ana"` y la lista sigue con 6.

**Teoría:** `set(i, e)` hace **dos** cosas a la vez:
1. **Efecto:** pone `e` en la posición `i`. El `size()` **no cambia**, porque no inserta nada.
2. **Devuelve:** lo que había en `i` **antes** de reemplazarlo.

¿Por qué devuelve el anterior? Porque el nuevo ya lo conoces (lo acabas de pasar). Lo que perderías es el viejo.
Así puedes saber qué sobrescribiste sin hacer un `get(i)` previo.

**Ejemplo (otros datos):**
```
List<String> colors = [rojo, verde, azul]          size 3

String old = colors.set(1, "negro");
  antes:   [0:rojo] [1:verde] [2:azul]
  después: [0:rojo] [1:negro] [2:azul]              size 3  ← no cambió
  old = "verde"                                     ← el que se fue

colors.add(1, "negro");   // en cambio, add INSERTA:
  [rojo, negro, verde, azul]                        size 4
```
(Comprobado: `colors.set(1, "negro")` devuelve `verde` → `[rojo, negro, azul]`.)

**Profesionalmente:** "dame el valor anterior al cambiarlo" es justo lo que necesita un **log de auditoría**
("el precio pasó de 100 a 120"). `Map.put` hace lo mismo y lo verás en la semana 3.

**Fíjate:** en P1.2 escribiste que `set` **reemplaza**, así que la teoría la tienes. El fallo está en aplicarla
al predecir, y para eso existen las predicciones.

---

#### ❌ CRÍTICO 2 — Un error en cascada que no reconciliaste

**Qué pasa:** un único modelo mental equivocado en el paso 7 ("set inserta") hizo fallar las predicciones
de los pasos 7, 8, 9 y 10. Escribiste `real:` en algunas líneas, pero **nunca** el `porque`. En los pasos 9 y 10
ni siquiera anotaste la salida real, aunque no coincidía.

**Teoría: el ciclo predecir → ejecutar → comparar**
1. Ejecutas y comparas **línea por línea**.
2. Cuando `real ≠ espero`, **te detienes** en la **primera** línea donde se separaron.
3. Los fallos siguientes casi siempre son consecuencias de ese primero. Si arreglas la causa, se arreglan todos.

Así se depura también en el trabajo: el bug está en el **primer** punto donde el estado real y el esperado
se separan, no en el último donde lo notas.

**Técnica: una tabla de estado.** Escribe cómo está la lista después de cada paso. Con otros datos:
```
paso              | lista después                 | size
add x3            | [rojo, verde, azul]           | 3
set(0, "negro")   | [negro, verde, azul]          | 3    ← "rojo" ya no existe
indexOf("rojo")   | (no cambia)                   | 3    → -1, porque "rojo" se fue en el paso anterior
```

**Qué hacer:** en cada línea que falló, escribe `// real: ... porque ...`. Por ejemplo, en el paso 8:
¿en qué posición quedó la única `"ana"` que sobrevivió al paso 7?

---

#### 🟠 IMPORTANTE 3 — P1.3 sigue sin mirar tu propia salida

**Qué pasa:** respondiste "solo se elimina una... puede que sea la primera". Es vago. La pregunta tiene una
respuesta concreta en **tu** salida.

**Pista:** cuenta las `"ana"` en la salida real del paso 7. Hay **una**: la de la posición 0 fue reemplazada
por `"sofia"`. Entonces, en este programa, ¿se eliminó solo una porque `remove` para en la primera, o porque
solo había una?

**Teoría:** tu regla es correcta. `remove(Object)` recorre la lista desde el índice 0 y elimina **solo la primera**
coincidencia que encuentra (según `equals`). El problema es que el orden de los pasos del enunciado **no permite
verla** aquí, y eso es culpa del enunciado, no tuya.

**Dato:** en la Tarea 2, paso 4, **sí** la observaste sin querer. `[10, 20, 30, 10, 20, 30]` con
`remove(Integer.valueOf(10))` dio `[20, 30, 10, 20, 30]`: solo se fue el primer `10`.

---

#### 🟡 MEJORABLE

- En el paso 9, `//true` no dice si es predicción o resultado. Usa siempre `// espero:` y después `// real:`.
- Los nombres en minúscula (`"ana"`) están bien mientras seas consistente.

#### ☐ Checklist para cerrar la Tarea 1
- [ ] Paso 7: `// real: ana porque ...` en la línea del `set`, y `// real: ... porque ...` en la lista
- [ ] Paso 8: `porque` del `indexOf` = 5
- [ ] Pasos 9 y 10: `// real:` en las listas, con su `porque`
- [ ] Paso 9: marcar `//true` como `// espero:` o `// real:`
- [ ] P1.3: responder con tu salida real (¿cuántas `"ana"` había antes del paso 9?)

---

### Tarea 2 — Recorrer y eliminar (fácil-medio)

**Archivo:** `src/practical/lists/ListIteration.java`

**Pasos:**
1. Crea una `List<Integer> numbers` **modificable** con los valores `1` a `10`.
   Pista: `new ArrayList<>(List.of(...))`.
2. Recórrela con **for con índice** e imprime `"posición X → valor Y"`.
3. Recórrela con **for-each** y calcula la **suma** en una variable `int`. Imprime la suma (esperado: 55).
4. **Trampa 1:** crea `List<Integer> t = new ArrayList<>(List.of(10, 20, 30));`
   Llama a `t.remove(1)` e imprime `t`. Luego vuelve a crear `t` y llama a `t.remove(Integer.valueOf(10))` e imprime `t`.
5. **Trampa 2:** dentro de un `try/catch (ConcurrentModificationException e)`, recorre `numbers` con
   for-each y elimina los pares con `numbers.remove(x)`. En el `catch` imprime el nombre de la
   excepción. Después del `try/catch`, imprime `numbers` (verás que quedó a medias).
6. Vuelve a crear `numbers` con `1` a `10`. Elimina los pares usando un **`Iterator`**
   (`hasNext`, `next`, `it.remove()`). Imprime la lista (esperado: `[1, 3, 5, 7, 9]`).
7. Vuelve a crear `numbers` con `1` a `10`. Elimina los pares con **una sola línea**:
   `numbers.removeIf(n -> n % 2 == 0);`. Imprime la lista.
8. **Trampa 3:** crea `List<Integer> fixed = List.of(1, 2, 3);` e intenta `fixed.add(4)` dentro de
   un `try/catch (UnsupportedOperationException e)`. Imprime algo en el `catch`.

**Preguntas:**
- P2.1 ¿Por qué el paso 5 lanza `ConcurrentModificationException` y el 6 no?
- P2.2 En el paso 4, ¿por qué `remove(1)` no eliminó el valor `1`... ni el `10`?
- P2.3 ¿Cuándo usarías `List.of` y cuándo `new ArrayList<>()`?

**Criterios:** las tres formas de recorrer, las tres trampas reproducidas y explicadas con tus palabras.


> ## 🔍 REVISIÓN TAREA 2 — 2026-10-02 · **6/10** · 🔁 rehacer
> Historial: v1 5/10 → **v2 6/10**. Revisado ejecutando `ListIteration`.

**✅ Ya está bien:** pasos 5–8. Ahora imprimes el **nombre** de la excepción (`getSimpleName()`).
Tus respuestas a la mini-tarea del CRÍTICO 1 (A y B) son **correctas**: ver abajo.
P2.3 correcta.

| Paso | | Comentario |
|------|---|------------|
| 1 | ✅ | |
| 2 | ❌ | ahora usas índice, pero **no imprime el último** (ver CRÍTICO 1). Falta el formato `posición X → valor Y` |
| 3 | 🟡 | sigue `Integer sum` (ver IMPORTANTE 4) |
| 4 | 🟡 | sigue `ArrayList<Integer> t1` (ver IMPORTANTE 5). Tu `t1` duplicado demostró la regla de "primera coincidencia" ✅ |
| 5–8 | ✅ | |
| predicciones | ❌ | **ninguna** en toda la tarea (ver IMPORTANTE 3) |
| P2.1 / mini-tarea A y B | ✅ | ver la revisión de tu respuesta |
| P2.2 | ❌ | sin responder: la sección `critico:2-->` está vacía (ver CRÍTICO 2) |

---

#### ❌ CRÍTICO 1 — *Off-by-one*: tu bucle del paso 2 nunca imprime el `10`

**Qué pasa:** `for (int i = 0; i < numbers.size() - 1; i++)`. La salida real termina en `9` y el `10` no aparece.

**Teoría:** con `size() = 10`, los índices válidos van de `0` a `9`.
- `i < size()` → `i` toma 0…9 ✅. Ya excluye el 10, que no es un índice válido.
- `i < size() - 1` → `i` toma 0…8 ❌. Excluye **también** el último elemento.

El `- 1` va en **una** de las dos formas, no en ambas:

| Condición | Índices recorridos (size 3) | |
|-----------|-----------------------------|---|
| `i < size()` | 0, 1, 2 | ✅ forma idiomática |
| `i <= size() - 1` | 0, 1, 2 | ✅ equivalente, menos común |
| `i < size() - 1` | 0, 1 | ❌ pierde el último |

Este es el error más frecuente con índices en cualquier lenguaje (*off-by-one*). Y en la Tarea 1, P1.1,
lo explicaste perfecto: "el último elemento es `size() - 1`". Aquí lo aplicaste dos veces.

**Por qué existe el paso 2:** el for con índice solo tiene sentido **cuando necesitas la posición**. Por eso el
formato pide `posición X → valor Y`. Si imprimes solo el valor, un for-each bastaba.

---

#### ❌ CRÍTICO 2 — P2.2: sobrecarga de `remove` (sin responder)

**Teoría: sobrecarga (*overloading*).** Varios métodos con el **mismo nombre** y **distintos parámetros**.
`List` tiene dos:
```java
E       remove(int index)    // elimina por POSICIÓN y devuelve el elemento
boolean remove(Object o)     // elimina por VALOR (primera coincidencia) y dice si lo encontró
```
El **compilador** elige cuál llamar según el tipo del argumento. Lo decide al compilar, no al ejecutar,
y sigue esta prioridad:
1. coincidencia **exacta** sin convertir nada,
2. si no hay, con *autoboxing* (`int` → `Integer`),
3. si no hay, varargs.

**Ejemplo comprobado (otros métodos):**
```java
static void m(int x)    { System.out.println("int"); }
static void m(Object x) { System.out.println("Object"); }

m(1);                  // imprime "int"     → 1 es un int primitivo: coincidencia exacta, gana sin boxing
m(Integer.valueOf(1)); // imprime "Object"  → es un Integer (un objeto): no encaja en int sin unboxing
```
Y con tu lista: `[10, 20, 30].remove(Integer.valueOf(1))` devuelve `false` y la lista no cambia,
porque no hay ningún elemento con **valor** 1.

**Responde con esto:** en `t.remove(1)` sobre `[10, 20, 30]`, ¿qué versión eligió el compilador? ¿Por qué
desapareció el `20` y no el `1` (que no existe) ni el `10`?

**Profesionalmente, el bug clásico:**
```java
List<Integer> productIds = ...;
int id = 3;
productIds.remove(id);   // 💥 elimina la POSICIÓN 3, no el producto 3
```
Pasa en código real y no lanza error: simplemente borra otra cosa (o lanza `IndexOutOfBounds` si la lista es corta).

---

#### ✅ Revisión de tu respuesta al CRÍTICO 1 (ConcurrentModificationException)

- **A:** "se eliminó el 2... la siguiente vez que el iterador intentó avanzar notó la modificación" ✅.
  Correcto: el `4` nunca llegó a evaluarse, porque la excepción saltó en la vuelta siguiente al `2`.
- **B:** "en `for (Integer num : numbers)`, porque el iterador detectó la anomalía" ✅.
  Precisión: salta dentro del `next()` **oculto** que el for-each llama en esa línea. No salta en `remove`.

**Dato extra (comprobado), y peor que la excepción:**
```java
List<Integer> p = new ArrayList<>(List.of(1, 2, 3, 4));
for (Integer x : p) if (x == 3) p.remove(x);
System.out.println(p);   // [1, 2, 4]  ← SIN excepción, y el 4 nunca se recorrió
```
Si eliminas el **penúltimo** elemento, `hasNext()` compara la posición con el nuevo `size()`, devuelve `false`
y el bucle termina en silencio. Que no haya excepción **no** significa que esté bien. Por eso la regla es
siempre `Iterator.remove()` o `removeIf`.

---

#### 🟠 IMPORTANTE 3 — Cero predicciones

Toda la tarea se ejecutó sin una sola línea `// espero:`. Con predicciones, el CRÍTICO 1 lo hubieras visto tú:
"espero: 10 líneas, de 1 a 10" → "real: 9 líneas".

#### 🟠 IMPORTANTE 4 — `Integer sum` en vez de `int` (segunda vez)

**Teoría:** `Integer` es un **objeto** inmutable. `sum = sum + num` hace por debajo
`sum = Integer.valueOf(sum.intValue() + num.intValue())`: desempaqueta, suma y crea un objeto nuevo en cada vuelta.
Además, los objetos `Integer` traen dos riesgos que `int` no tiene:
```java
Integer a = 1000, b = 1000;
a == b;        // false  ← compara referencias (comprobado)
Integer c = 100, d = 100;
c == d;        // true   ← Java reutiliza los Integer de -128 a 127 (caché). Funciona "a veces" = bug escondido
Integer n = null;
int x = n + 1; // 💥 NullPointerException
```
**Regla:** usa `Integer` donde la API lo exige (`List<Integer>`). Para calcular, usa `int`.

#### 🟠 IMPORTANTE 5 — `ArrayList<Integer> t1 = ...` (segunda vez)

Declara con la **interfaz**: `List<Integer> t1`. Repasa la sección 1.1 de esta lección. Es la misma idea que
en Spring hace posible la inyección de dependencias: el código depende del contrato, no de la clase concreta.

#### ☐ Checklist para rehacer la Tarea 2
- [ ] Paso 2: condición correcta y formato `posición X → valor Y` (predice cuántas líneas salen)
- [ ] Paso 3: `int sum`
- [ ] Paso 4: `List<Integer> t1`
- [ ] `// espero:` en **todos** los `println`, y `// real: ... porque ...` donde falle
- [ ] P2.2 respondida (sección `critico:2-->`)
- [ ] *Opcional:* reproducir el caso del penúltimo elemento y explicarlo con tus palabras

---

### Tarea 3 — `List<Product>` con el dominio (medio)

**Archivos:** `src/practical/lists/ProductListLab.java` + cambios mínimos en `src/Models/Product.java`

**Paso previo en `Product`:**
- Agrega `getId()`.
- Agrega un `toString()` con `@Override` que devuelva algo como
  `Product{id=1, name='Laptop', price=1000}`.
  Para esta tarea **no** hace falta repetirlo en las subclases.

**Pasos en `ProductListLab`:**
1. Crea una `List<Product> products` y agrega **6** productos de tipos distintos, con **precios distintos**:
   al menos un `Laptop`, un `PhysicalBook`, un `Furnuture`, un `DigitalProduct`, un `RenewableProduct`
   y un `ConsultingService`. Usa `new BigDecimal("...")` con **String** (no con `double`).
2. Imprime la lista completa (aquí verás tu `toString()`).
3. Imprime cada producto en una línea con el formato `N. nombre - precio`, numerados desde **1**.
4. Calcula el **precio total** de todos los productos con `BigDecimal`.
   Recuerda: `total = total.add(...)`, porque `BigDecimal` es inmutable. Empieza con `BigDecimal.ZERO`.
5. Encuentra el producto **más caro** con un bucle. Compara con `compareTo`, **no** con `>`.
   Imprime su nombre.
6. Crea una **nueva** lista `List<Product> expensive` con los productos de precio **mayor a 500**.
   No modifiques `products`. Imprime cuántos hay y cuáles son.
7. Escribe un método `private static Product findByName(List<Product> list, String name)` que
   devuelva el producto cuyo nombre coincida (usa `equals`, **no** `==`) o `null` si no existe.
   Pruébalo con un nombre que existe y con uno que no.
8. Escribe un método `private static List<Shippable> onlyShippable(List<Product> list)` que devuelva
   solo los productos que implementan `Shippable`. Pista: `instanceof`. Recorre el resultado e
   imprime `calculateShippingCost()` de cada uno.

**Preguntas:**
- P3.1 ¿Por qué `total.add(p.getPrice());` sin asignar no suma nada?
- P3.2 ¿Por qué `new BigDecimal("0.1")` y no `new BigDecimal(0.1)`? (Pruébalo imprimiendo ambos.)
- P3.3 En el paso 8, ¿por qué hace falta un cast `(Shippable)` después del `instanceof`, o qué
  sintaxis de Java moderno lo evita? (Pista: *pattern matching for instanceof*.)
- P3.4 ¿Qué problema tiene devolver `null` en `findByName`? (Lo resolveremos con `Optional` más adelante.)

**Criterios:** `BigDecimal` usado correctamente (`compareTo`, `add` asignado, constructor con String),
métodos con una sola responsabilidad, la lista original no se modifica en el paso 6.


> ## 🔍 REVISIÓN TAREA 3 — 2026-10-02 · **6/10** · 🔁 rehacer
> Historial: v1 5/10 → **v2 6/10**. Revisado ejecutando `ProductListLab`.

**✅ Ya está bien:**
- `toString()` con el formato pedido.
- Numeración desde 1 con un contador.
- `compareTo(...) > 0`.
- El `500` sacado del bucle.
- El paso 8 imprime `calculateShippingCost()`.
- El total con `BigDecimal` está perfecto desde la v1.

| Paso | | Comentario |
|------|---|------------|
| previo | ✅ | `getId()` + `toString()` |
| 1–4 | ✅ | |
| 5 | 🟡 | funciona, pero imprime el producto entero. Se pedía el **nombre** |
| 6 | 🟡 | falta imprimir **cuántos** hay |
| 7 | ❌ | el ternario está **invertido** (ver CRÍTICO 1). Sigue lanzando excepción con lista vacía (ver IMPORTANTE 5) |
| 8 | ✅ | |
| P3.1 | ❌ | no entendiste la pregunta (ver IMPORTANTE 3) |
| P3.2 | ❌ | respuesta incorrecta (ver CRÍTICO 2) |
| P3.3 | 🟡 | sabes que existe algo de Java 16, pero no explicas **por qué** hace falta el cast (ver IMPORTANTE 4) |
| P3.4 | 🟡 | buen comienzo, falta el problema principal (ver IMPORTANTE 6) |

---

#### ❌ CRÍTICO 1 — El ternario del paso 7 dice lo contrario de la verdad

**Qué pasa, en tu salida real:**
```
7-------------
no existe!     ← buscaste "netflix", que SÍ existe
null           ← buscaste "nadaaa", que NO existe
```

**Teoría:** `condición ? valorSiTrue : valorSiFalse`. Léelo en voz alta:
`find == null ? find : "no existe!"` dice "si `find` es null, imprime `find` (o sea `null`); si no, imprime
`"no existe!"`". Está al revés.

**Ejemplo (otros datos):**
```java
String nickname = null;
String shown = (nickname == null) ? "anónimo" : nickname;  // si es null → "anónimo"; si no → el nickname
```

**La lección de fondo:** la salida estaba mal a la vista y pasó desapercibida, porque no había predicciones.
Además, este bug es **exactamente** el problema de P3.4: devolver `null` obliga a cada llamador a acordarse de
comprobarlo, y a comprobarlo **bien**. Aquí la comprobación se hizo mal.

---

#### ❌ CRÍTICO 2 — P3.2: por qué `new BigDecimal("0.1")` y no `new BigDecimal(0.1)`

**Qué pasa:** respondiste que "el constructor solo recibe String" y que "un int podría desbordar". Ninguna de
las dos cosas es cierta. `BigDecimal` tiene constructores con `String`, `int`, `long` **y** `double`, y la
pregunta trata de `double`, no de `int`. No hiciste la prueba que pedía el enunciado.

**Teoría:** `double` es **punto flotante binario**. Igual que `1/3` no se puede escribir exacto en decimal
(`0.3333...`), `0.1` no se puede representar exacto en binario. El `double` guarda el número binario **más
cercano**, que no es `0.1`.
- `new BigDecimal(0.1)` copia **exactamente** ese valor binario impreciso.
- `new BigDecimal("0.1")` guarda **exactamente** lo que escribiste.

**Ejemplo comprobado:**
```java
new BigDecimal(0.1)       // 0.1000000000000000055511151231257827021181583404541015625
new BigDecimal("0.1")     // 0.1
BigDecimal.valueOf(0.1)   // 0.1   (pasa por Double.toString, por eso sale bien)
0.1 + 0.2                 // 0.30000000000000004   (double)
new BigDecimal("0.1").add(new BigDecimal("0.2"))   // 0.3
```

**Profesionalmente:** el dinero **nunca** va en `double`. En Java se usa `BigDecimal` creado desde `String` o
desde la base de datos: una columna `DECIMAL(10,2)` se mapea a `BigDecimal` en JPA. Es el mismo problema de
`float` en PHP (`0.1 + 0.2 != 0.3`). Por eso el cast `decimal` de Laravel devuelve un **string**.

**Qué hacer:** imprime los dos (`new BigDecimal(0.1)` y `new BigDecimal("0.1")`), pega la salida en un comentario
y responde de nuevo con tus palabras.

---

#### 🟠 IMPORTANTE 3 — P3.1: qué pasa si llamas a `add` sin asignar

**Qué pasa:** respondiste "¿de dónde sacaste eso de `total.add(p.getPrice())`?". La pregunta es hipotética:
¿qué pasaría si alguien escribe la línea **sin** el `total =` delante?

**Teoría:** `BigDecimal` es **inmutable**: ningún método cambia el objeto. `add` **devuelve un objeto nuevo** con
el resultado. Si no lo guardas, se pierde.

**Ejemplo comprobado:**
```java
BigDecimal total = BigDecimal.ZERO;
total.add(new BigDecimal("5"));        // calcula 5... y lo tira
System.out.println(total);             // 0

String s = "hola";
s.toUpperCase();                       // misma idea: String también es inmutable
System.out.println(s);                 // hola
```
**Puente PHP:** es como `CarbonImmutable`: `$date->addDays(1)` sin asignar no cambia `$date`.

**Qué hacer:** responde P3.1 en una línea con tus palabras: ¿por qué `total.add(...)` sin asignar no suma nada?

---

#### 🟠 IMPORTANTE 4 — P3.3: por qué hace falta el cast después del `instanceof`

**Teoría:** el compilador solo conoce el **tipo declarado** de la variable, y `product` está declarado como `Product`.
`instanceof` es una comprobación **en tiempo de ejecución**. Antes de Java 16, el compilador no "recordaba" que
dentro del `if` ya habías comprobado el tipo, así que había que convencerlo con un cast.

Desde Java 16 existe ***pattern matching for instanceof***: el `instanceof` comprueba el tipo **y además** declara
una variable ya convertida.

**Ejemplo comprobado (otros tipos):**
```java
Object o = "hola";

// antes de Java 16
if (o instanceof String) {
    String s = (String) o;
    System.out.println(s.length());
}

// Java 16+
if (o instanceof String s) {        // comprueba y declara 's' ya como String
    System.out.println(s.length()); // 4
}
```

**Qué hacer:** reescribe `onlyShippable` con *pattern matching* y responde P3.3 nombrando la característica y
explicando por qué el cast hacía falta.

---

#### 🟠 IMPORTANTE 5 — `findByName` sigue lanzando excepción con la lista vacía (segunda vez)

Buscar en una lista vacía **no es un error**: el resultado normal es "no encontrado". Es igual que un
`SELECT ... WHERE name = ?` sobre una tabla vacía, que devuelve 0 filas y no un error.
**Las excepciones son para situaciones inválidas, no para resultados posibles.** Además, el bucle ya cubre el
caso: con la lista vacía no entra y llega al `return null`. Por tanto el `if` sobra entero.
(Y si algún día lo necesitas: `list.isEmpty()`, no `size() <= 0`.)

#### 🟠 IMPORTANTE 6 — P3.4: el problema real de devolver `null`

Tu respuesta ("el que llama no tiene información completa") va bien encaminada. Los tres problemas concretos:
1. **La firma miente por omisión.** `Product findByName(...)` no dice "puede no existir", así que quien llama
   se olvida de comprobarlo.
2. **El error explota lejos.** `findByName(...).getName()` lanza `NullPointerException` en otra línea, quizá en
   otra clase, lejos de donde nació el `null`.
3. **Comprobarlo bien también cuesta:** tu CRÍTICO 1 lo demuestra.

**Puente Laravel:** `first()` devuelve `null`; `firstOrFail()` lanza. En Java, `Optional<Product>` (semana 5) hace
que la ausencia esté **en el tipo**, y el compilador te obliga a decidir qué hacer.

---

#### 🟡 MEJORABLE

- **Campo `asdf`:** `BigDecimal asdf = new BigDecimal("123");` al final de la clase no se usa. Bórralo.
- **Nombres que confunden con tipos de Java:**
  - `iterador` no es un `Iterator`: es la posición. Mejor `position` o `number`.
  - `comparator` es el nombre de una interfaz de Java que verás en la semana 4. Mejor `minPrice` o `threshold`.
- **Otros nombres:** `mostCost` → `mostExpensive`, `sumtotal` → `total`, `find` / `dontFind` → `netflix` / `missing`.
- **Espacio después del número:** `1. Laptop` en vez de `1.Laptop`.

#### ☐ Checklist para rehacer la Tarea 3
- [ ] Paso 5: imprimir solo el nombre
- [ ] Paso 6: imprimir cuántos hay
- [ ] Paso 7: ternario corregido y sin excepción para la lista vacía
- [ ] `onlyShippable` con *pattern matching*
- [ ] Borrar `asdf` y renombrar `iterador` y `comparator`
- [ ] P3.1, P3.2 (con la salida de la prueba), P3.3 y P3.4 respondidas de nuevo
- [ ] `// espero:` en cada `println`

---

### Tarea 4 — Aplicarlo a `Order` (medio-avanzado)

**Archivos:** `src/Models/Order.java`, `src/Models/OrderItem.java`, `src/Main.java`

Hoy `Main` tiene el comentario `//detalle por producto.` y no se puede implementar porque
`OrderItem` y `Order` no exponen nada. Vamos a resolverlo **sin romper la encapsulación**.

**En `OrderItem`:**
1. Agrega `getProduct()` y `getQuantity()`.
2. Valida que `product` no sea `null` (lanza `IllegalArgumentException`).
3. Corrige el mensaje de la validación de `quantity`: hoy dice "cannot be negative", pero también
   rechaza el `0`. El mensaje debe decir la verdad.

**En `Order`:**
4. Marca `orderItems` como `final` y usa el diamond `<>` en el constructor.
5. Cambia `new BigDecimal("0")` por `BigDecimal.ZERO`.
6. Agrega `public List<OrderItem> getItems()` que **no** permita modificar la lista desde fuera.
   Elige entre `Collections.unmodifiableList` y `List.copyOf` y **justifica tu elección en un comentario**.
7. Agrega `public int getItemCount()`: cantidad de líneas (items) del pedido.
8. Agrega `public int getTotalUnits()`: suma de todas las `quantity`.
9. Agrega `public boolean removeItem(int index)`: elimina la línea en esa posición.
   Si el índice no es válido, devuelve `false` (no dejes escapar la excepción).
10. Agrega `public boolean isEmpty()`.

**En `Main`:**
11. Debajo del total, imprime el **detalle por producto** con el formato:
    ```
    Laptop        x3   1000   3000
    Java Course   x4    100    400
    ...
    TOTAL                     3846
    ```
    (No hace falta que las columnas queden perfectas; pista opcional: `String.format` o `printf`.)
12. Intenta `order.getItems().add(...)` dentro de un `try/catch` y demuestra que falla.
13. Elimina un item con `removeItem`, prueba también con un índice inválido (ej. `99`), y vuelve
    a imprimir el total.

**Preguntas:**
- P4.1 ¿Por qué `getItems()` no debe devolver `orderItems` directamente?
- P4.2 `final List<OrderItem> orderItems`: ¿impide hacer `orderItems.add(...)`? ¿Qué impide exactamente?
- P4.3 ¿Qué diferencia hay entre `getItemCount()` y `getTotalUnits()` si agrego la misma laptop dos veces?

**Criterios:** encapsulación real (demostrada en el paso 12), métodos pequeños, sin duplicar lógica,
`Main` sigue imprimiendo `3846` antes de eliminar.

**Restricción:** **no** intentes todavía evitar items duplicados (`addItem(laptop, 3)` +
`addItem(laptop, 2)`). Eso requiere `equals`, y es la siguiente lección.


> ## 🔍 REVISIÓN TAREA 4 — 2026-10-02 · **7/10** · 🔁 rehacer (le falta poco)
> Historial: v1 5/10 → **v2 7/10**. Revisado ejecutando `Main` (sigue imprimiendo `3846` ✅).

**✅ Ya está bien:**
- Validaciones de `OrderItem`: el producto no puede ser `null` y el mensaje de cantidad dice la verdad. Fallan donde está el error (*fail fast*).
- `ArrayList` con diamond `<>` e `isEmpty()`.
- El detalle usa `getSubTotalPrice()` (DRY) y termina con la línea `Total`.
- El paso 12 demuestra `UnsupportedOperationException`. La encapsulación ya está **comprobada**.

| Paso | | Comentario |
|------|---|------------|
| 1–5 | ✅ | |
| 6 | 🟡 | la justificación no es técnica (ver IMPORTANTE 2) |
| 7–10 | ✅ | |
| 11 | ✅ | columnas sin alinear: opcional (ver MEJORABLE) |
| 12 | ✅ | `catch (Exception e)` demasiado amplio (ver IMPORTANTE 4) |
| 13 | ❌ | sin hacer (ver IMPORTANTE 3) |
| P4.1 | ✅ | |
| P4.2 | ❌ | sin rehacer, y falta la mini-tarea (ver CRÍTICO 1) |
| P4.3 | 🟡 | falta el ejemplo con números |

---

#### ❌ CRÍTICO 1 — `final`: protege la **variable**, no el **objeto**

**Qué pasa:** tu respuesta sigue siendo "impide hacer modificaciones una vez que el atributo fue modificado".
Pero tu propio `removeItem` hace `orderItems.remove(index)` sobre un campo `final` y compila sin problema.

**Teoría:** una variable de objeto guarda una **referencia** (una flecha) hacia un objeto en memoria.
```
 orderItems  ──────►  [ ArrayList: item1, item2, item3 ]
 (variable)            (objeto)

 final bloquea la FLECHA:  no puede apuntar a otro objeto.
 final NO bloquea la CAJA: el objeto se puede modificar (add, remove, clear...).
```

**Ejemplo comprobado:**
```java
final List<String> x = new ArrayList<>();
x.add("a");               // ✅ compila: modifica el objeto
x.remove(0);              // ✅ compila: modifica el objeto
x = new ArrayList<>();    // ❌ NO compila: cannot assign a value to final variable x
```

**Cada protección cubre una cosa distinta:**

| Herramienta | Qué impide |
|-------------|------------|
| `private` | que otra clase **acceda** al campo |
| `final` | que el campo se **reasigne** a otra lista (incluso desde la misma clase) |
| `List.copyOf` en el getter | que quien recibe la lista **modifique** sus elementos |

**Relación con Spring:** `private final ProductRepository repository;` con inyección por constructor es el
estándar. El `final` garantiza que la dependencia no se cambie después de construir el objeto.

**Qué hacer:** prueba las 4 líneas del ejemplo en un archivo aparte, con tus predicciones, y vuelve a responder
P4.2: ¿qué impide exactamente `final`?

---

#### 🟠 IMPORTANTE 2 — Justificar `List.copyOf` vs `Collections.unmodifiableList`

**Qué pasa:** escribiste "es al que entiendo mejor, además no sé cómo usar el otro". Es honesto, pero no es una
razón técnica. En un code review real te pedirían el **por qué**.

**Teoría: vista vs copia.** Las dos impiden modificar desde fuera, pero se comportan distinto cuando la lista
**original** cambia.

**Ejemplo comprobado:**
```java
List<String> src  = new ArrayList<>(List.of("a"));
List<String> view = Collections.unmodifiableList(src);  // VISTA: una ventana de solo lectura a src
List<String> copy = List.copyOf(src);                   // COPIA: una foto de src en este momento

src.add("b");
System.out.println(view);   // [a, b]  ← la vista ve el cambio
System.out.println(copy);   // [a]     ← la copia no
```

| | `unmodifiableList` (vista) | `List.copyOf` (copia) |
|---|---|---|
| Coste al llamar | casi nada | copia todos los elementos, O(n) |
| Si el `Order` cambia después | quien la recibió **ve** el cambio | no lo ve: es una foto |
| `null` dentro | lo permite | lanza `NullPointerException` |

Ninguna está mal aquí. Elige una y escribe el **por qué** en términos de esta tabla.

---

#### 🟠 IMPORTANTE 3 — Paso 13 sin hacer

**Qué pasa:**
```java
try {
    order.getItems();      // no hace nada: el resultado se descarta (la misma idea que P3.1)
    order.removeItem(99);  // devuelve false... y nadie lo mira
} catch (Exception e) { ... }   // nunca entra: removeItem no lanza excepciones, por diseño
```

**Teoría:** diseñaste `removeItem` para **devolver** `boolean` en lugar de lanzar. Entonces la información está
en el valor de retorno, y el `try/catch` sobra. Un método puede avisar de un fallo de dos formas: lanzando o
devolviendo. Quien lo llama tiene que usar la forma que el método eligió.

**Qué hacer:**
1. Eliminar un item **válido**, imprimir el `boolean` y el total nuevo (con predicción).
2. Intentar el índice `99`, imprimir el `boolean` (predicción: ¿`true` o `false`?).
3. Imprimir el total otra vez: ¿cambió?

---

#### 🟠 IMPORTANTE 4 — `catch (Exception e)` es demasiado amplio

**Teoría:** captura **solo** la excepción que esperas. `catch (Exception e)` también atrapa cualquier bug, por ejemplo
un `NullPointerException`, y lo imprime como si fuera "lo esperado".
```java
try {
    order.getItems().add(item);
} catch (Exception e) {                     // si fallara por un NPE, imprimirías "NullPointerException"
    System.out.println(e.getClass()...);    // y la demostración "parecería" funcionar
}
```
Con `catch (UnsupportedOperationException e)`, cualquier otro error **no** se esconde: explota y lo ves.
**Profesionalmente:** en Spring, los `@ExceptionHandler` se registran por **tipo** de excepción, por esta misma razón.

---

#### 🟡 MEJORABLE

- **P4.3:** da el ejemplo con números. Si agregas la misma laptop x3 y después x2, ¿qué devuelven `getItemCount()` y `getTotalUnits()`?
- **`removeItem`:** `index >= orderItems.size()` se lee más directo que `index > orderItems.size() - 1`.
- **Columnas (opcional):** `printf` con ancho fijo. Comprobado, con otros datos:
  ```java
  System.out.printf("%-12s x%-3d %8s%n", "Teclado", 2, "80");
  // Teclado      x2         80
  // %-12s = texto alineado a la izquierda en 12 columnas · %8s = alineado a la derecha en 8 · %n = salto de línea
  ```
- **Nombres:** `ordI` → `extraItem`.
- **Imports sin usar:** `ShippingStragy` en `Main`.

#### ☐ Checklist para rehacer la Tarea 4
- [ ] Mini-tarea de `final` + P4.2 de nuevo
- [ ] Comentario en `getItems()` con la justificación técnica
- [ ] Paso 13 completo (remove válido, remove 99, totales, predicciones)
- [ ] `catch (UnsupportedOperationException e)` en el paso 12
- [ ] P4.3 con números

---

### Tarea 5 — El puente a `equals` (avanzado, corta)

**Archivo:** `src/practical/lists/ContainsLab.java`

1. Crea una `List<Product>` y agrega un `Laptop` con id `1`.
2. Crea **otro** objeto `Laptop` con **exactamente** los mismos datos (id `1`, mismo nombre, precio y peso).
3. Imprime `list.contains(segundoLaptop)` y `list.indexOf(segundoLaptop)`.
4. Imprime `list.contains(primerLaptop)`.
5. Escribe la predicción **antes** de ejecutar.

**Preguntas:**
- P5.1 ¿Por qué `contains` da ese resultado con el segundo laptop si "es el mismo producto"?
- P5.2 ¿Qué método de `Product` crees que usa `contains` por dentro para comparar?
- P5.3 ¿Qué pasaría en `Order` si un usuario agrega "el mismo producto" dos veces?

**No lo arregles.** Esta tarea solo sirve para ver el problema. La solución es la lección siguiente
(`equals()` / `hashCode()`).


> ## 🔍 REVISIÓN TAREA 5 — 2026-10-02 · **7/10** · 🟡 casi
> Historial: v1 3/10 → **v2 7/10**. Revisado ejecutando `ContainsLab`: `false`, `-1`, `true`.

**✅ Ya está bien:** el experimento ahora es válido (solo el primer laptop en la lista) y las **tres predicciones
acertaron**, una por línea. P5.3 correcta. Gran mejora.

| | | Comentario |
|---|---|---|
| pasos 1–5 | ✅ | |
| P5.1 | 🟡 | describe el resultado, pero no lo explica (ver IMPORTANTE 1) |
| P5.2 | 🟡 | sin cambios: falta nombrar el método y decir qué compara (ver IMPORTANTE 2) |
| P5.3 | ✅ | |

---

#### 🟠 IMPORTANTE 1 — P5.1: "no existe en la lista"... ¿pero por qué, si tiene los mismos datos?

**Qué pasa:** "no existe dentro de la lista, por eso `contains` no lo encuentra" repite el resultado con otras
palabras. La pregunta es **por qué**: el segundo laptop tiene el mismo id, nombre, precio y peso.

**Teoría:** cada `new` crea un objeto **distinto** en memoria (en el *heap*), con su propia dirección:
```
 firstLaptop  ──►  [Laptop id=1 "Laptop" 3452.4]   @dirección A   ← este está en la lista
 secondLaptop ──►  [Laptop id=1 "Laptop" 3452.4]   @dirección B   ← mismo contenido, OTRO objeto
```
`contains` recorre la lista y pregunta, para cada elemento, si "es igual" al que buscas llamando a **un método**.
Lo que ese método considera "igual" decide el resultado.

#### 🟠 IMPORTANTE 2 — P5.2: nombra el método y di qué compara

**Teoría:**
- `contains`, `indexOf` y `remove(Object)` llaman a **`equals(Object)`** (tabla 1.3 de esta lección).
- `equals` está declarado en **`java.lang.Object`**. Toda clase hereda de `Object` aunque no escribas `extends`.
- `Product` **no** sobrescribe `equals`, así que usa la versión heredada de `Object`, que hace
  `return this == obj;`. Es decir, compara **referencias** (¿es la misma dirección?), no el contenido.

**Ejemplo comprobado, la contraprueba:**
```java
List<String> l = new ArrayList<>(List.of("Ana"));
l.contains(new String("Ana"));   // true ← dos objetos distintos, ¡pero da true!
```
¿Por qué con `String` funciona? Porque `String` **sí** sobrescribe `equals` para comparar los caracteres.
`Product`, todavía no.

**Qué hacer:** responde P5.1 y P5.2 con tus palabras usando: `equals`, `Object`, referencia.
Y una pregunta más: **¿qué tendría que tener `Product` para que `contains(secondLaptop)` dé `true`?**
No lo implementes: es la lección siguiente.

#### 🟡 MEJORABLE
- Borra la línea vieja `// espero true,true`: ya no corresponde.
- Import sin usar: `java.lang.ref.PhantomReference`.
- `String[] args` en vez de `String args[]`.

#### ☐ Checklist para cerrar la Tarea 5
- [ ] P5.1 y P5.2 con `equals` / `Object` / referencia
- [ ] Responder: ¿qué necesita `Product` para que `contains` dé `true`?
- [ ] Limpiar el comentario viejo y el import

---

## 4. Qué entregar

| Tarea | Archivos                                                     |
|-------|--------------------------------------------------------------|
| 1     | `src/practical/lists/ListBasics.java`                        |
| 2     | `src/practical/lists/ListIteration.java`                     |
| 3     | `src/practical/lists/ProductListLab.java` + `Product.java`   |
| 4     | `Order.java`, `OrderItem.java`, `Main.java`                  |
| 5     | `src/practical/lists/ContainsLab.java`                       |

Cada uno con predicciones (`// espero:`) y respuestas a las preguntas en comentarios.

**Siguiente lección:** `equals()` / `hashCode()` + `HashSet` → [Lección 02](02-equals-hashcode-set.md).

---

## 5. Estado de las revisiones

> Cada tarea tiene su revisión **debajo de su enunciado** (bloque 🔍 REVISIÓN), con teoría, ejemplos y checklist.
> Esta sección es solo el resumen.

| Tarea | Historial | Estado (2026-10-02) | Lo que falta, en una línea |
|-------|-----------|---------------------|----------------------------|
| 1. `ListBasics` | 6 → 7 → **7** | 🟡 casi | `porque` de las predicciones falladas (pasos 7–10) y P1.3 |
| 2. `ListIteration` | 5 → **6** | 🔁 rehacer | *off-by-one* del paso 2, P2.2 (sobrecarga), predicciones |
| 3. `ProductListLab` | 5 → **6** | 🔁 rehacer | ternario invertido, P3.1–P3.4 (sobre todo `BigDecimal(double)`) |
| 4. `Order` | 5 → **7** | 🔁 rehacer | P4.2 (`final`), paso 13, justificar `copyOf` |
| 5. `ContainsLab` | 3 → **7** | 🟡 casi | P5.1 y P5.2 con `equals` / `Object` / referencia |

**Lo que se repite en todas:** las predicciones. Donde las escribiste (Tarea 5), todo salió bien. Donde no
(Tareas 2 y 3), hay bugs visibles en la salida que pasaron desapercibidos: el `10` que falta y el
"no existe!" de un producto que sí existe.

**Cómo entregar:** corrige sobre el mismo archivo y marca lo nuevo con `// corrección:`. Pide
"revisa tarea N" de a una. Cuando las 5 estén en ✅, pasamos a `equals()` / `hashCode()` + `HashSet`.
