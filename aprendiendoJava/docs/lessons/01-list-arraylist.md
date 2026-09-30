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

**Siguiente lección:** `equals()` / `hashCode()` + `HashSet` (ya diseñada en `docs/progress.md`).
