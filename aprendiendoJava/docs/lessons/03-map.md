# Lección 03 — `Map` (`HashMap`, `LinkedHashMap`, `TreeMap`)

> Ruta: Fase 3 (Collections), semana 3 del roadmap.
> Viene de: Lección 02 (`equals`/`hashCode` + `Set`). Prepara: Lección 04 (ordenar con `Comparable`/`Comparator`).
> Todos los comportamientos descritos aquí fueron comprobados ejecutando código en Java 21.
> Las salidas que ves en los ejemplos (`// → ...`) son **reales**, copiadas del terminal.

---

## Cómo usar esta lección

Esta lección está escrita **desde cero**: no supone que recuerdes nada. Si algo te suena, mejor; si no, está
explicado aquí.

**Por ahora solo está la teoría.** Las tareas llegan cuando la termines. Serán mini-tareas de unos 10 minutos,
una por vez.

Cada sección tiene la misma forma:

| Parte | Qué es |
|-------|--------|
| 📖 **Explicación** | la idea en palabras simples |
| 💻 **Ejemplo** | código pequeño con su salida real |
| 🧪 **Pruébalo** | un experimento corto que **tú** escribes y ejecutas (2–5 min). **No te lo saltes**: es donde se aprende |
| ✅ **¿Lo entendiste?** | 2 o 3 preguntas. La respuesta está escondida debajo: piensa primero y después ábrela |

**Plan de lectura (unas 2 h 30 min en total; puedes hacerlo en 2 o 3 sesiones):**

| Sesión | Lee | Tiempo |
|--------|-----|--------|
| 1 | §0 (repaso) + §1 + §2 + §3 | 60 min |
| 2 | §4 + §5 + §6 | 50 min |
| 3 | §7 + §8 + §9 + §10 + §11 | 40 min |

**Dónde hacer los 🧪:** crea **un solo archivo** `src/practical/maps/MapPlayground.java` con un `main`. Ahí van
todos los experimentos, uno debajo del otro, cada uno con un separador:
```java
System.out.println("---- 🧪 §3.2 ----");
```
Así nunca confundes qué línea del terminal es de qué experimento (te pasó en las lecciones anteriores).

**La plantilla de siempre:** antes de ejecutar, escribe lo que esperas. Después de ejecutar, escribe lo real.
```java
System.out.println(algo);
// espero: ...
// real:   ...   ← solo si fallaste
// porque: ...   ← solo si fallaste
```

**Si algo no se entiende:** pregúntame por el número de sección ("no entiendo §4.2"). Es normal; no es lento,
es como se aprende.

---

## 0. Repaso: lo que necesitas de las lecciones anteriores

Un `Map` usa **todo** lo de la lección 02. Si estas 5 ideas no están claras, el resto va a costar.
Léelas aunque creas que las sabes.

### 0.1 `==` vs `equals`

| | Qué pregunta | Ejemplo |
|---|---|---|
| `a == b` (con **objetos**) | ¿son **el mismo objeto** en memoria? (la misma "caja") | dos `new Product(1, ...)` → `false` |
| `a.equals(b)` | ¿**valen** lo mismo? (lo decide el `equals` de la clase) | dos `new Product(1, ...)` → `true` (tu `Product` compara el `id`) |
| `x == y` (con **primitivos**: `int`, `double`, `boolean`) | ¿tienen el mismo **valor**? | `500 == 500` → `true` |

Los **primitivos** (`int`, `double`, `boolean`, `char`…) son valores sueltos: no son objetos y no tienen métodos.
Los **objetos** (`String`, `Integer`, `BigDecimal`, `Product`…) se crean con `new` (o con atajos) y tienen métodos.

### 0.2 `hashCode`: el número del cajón

Una colección `Hash...` (como `HashSet`) guarda los objetos en **cajones numerados**. Para saber en qué cajón va
un objeto, llama a su `hashCode()`. Para **buscar** un objeto hace dos pasos:
1. calcula `hashCode()` → va **directo** a ese cajón (no recorre todo, por eso es rápido);
2. dentro de ese cajón, compara con `equals()` → ¿está o no?

**Regla de oro:** si `a.equals(b)` es `true`, entonces `a.hashCode()` **tiene que ser igual** a `b.hashCode()`.
Si no, quedan en cajones distintos y el paso 2 nunca ocurre.

### 0.3 Colisión

Que dos objetos tengan **el mismo `hashCode` no significa que sean iguales**. Pueden caer en el mismo cajón y
ser distintos. Eso se llama **colisión**. No es un error: para eso existe el paso 2 (`equals`). Un ejemplo real
con `String` (comprobado):
```java
"Aa".hashCode()      // → 2112
"BB".hashCode()      // → 2112     ← mismo cajón
"Aa".equals("BB")    // → false    ← pero distintos
```
Un set con `"Aa"` y `"BB"` tiene **2** elementos: comparten cajón, pero `equals` los distingue.

### 0.4 Un `Set` nunca tiene repetidos

Los tres (`HashSet`, `LinkedHashSet`, `TreeSet`) quitan repetidos. **Solo cambia el orden:**
```
HashSet        → orden: ninguno (el de los cajones)
LinkedHashSet  → orden: como llegaron
TreeSet        → orden: ordenados (alfabético, de menor a mayor)
```

### 0.5 Simétrico

`equals` es simétrico si **las dos direcciones dan lo mismo**: `a.equals(b)` y `b.equals(a)` dan el mismo
resultado. `false`/`false` es simétrico. `true`/`true` es simétrico. Solo `true`/`false` rompe la simetría.

> 💡 **Resumen del repaso:** `Map` = un `Set` de **claves**, donde cada clave lleva **un valor pegado**.
> Todo lo que sabes de `Set` vale para las claves de un `Map`.

---

## 1. El problema: ¿por qué necesito un `Map`?

### 📖 Explicación

Hoy tu `Order` guarda una `List<OrderItem>`. Si haces esto:
```java
order.addItem(laptop, 3);
order.addItem(laptop, 1);
```
la lista queda con **dos líneas** para el mismo producto:
```
Laptop x3
Laptop x1
```
Lo que quiere cualquier tienda es **una línea por producto**: `Laptop x4`.

Para lograrlo con una `List`, cada vez que agregas tendrías que **recorrer toda la lista** buscando si el
producto ya está. Con 5 items no importa, pero con 10 000 es lento, y además el código es largo.

Lo que necesitas es una estructura que responda al instante a esta pregunta: **"dado este producto, ¿cuál es su
línea?"**. Eso es un `Map`: buscas por una **clave** (el producto) y obtienes un **valor** (su línea).

### Analogía: los casilleros del gimnasio

Cada casillero tiene una **etiqueta** (el número) y **algo adentro** (tu mochila).
- No puede haber **dos casilleros con la misma etiqueta** → las claves son únicas (como un `Set`).
- Dos casilleros **sí** pueden tener cosas iguales adentro → los valores se pueden repetir.
- Para sacar tu mochila **no abres todos los casilleros**: vas directo al número.

| Casillero | Map |
|-----------|-----|
| etiqueta / número | **clave** (*key*) |
| lo que hay adentro | **valor** (*value*) |
| un casillero completo (etiqueta + contenido) | **entrada** (*entry*) |

---

## 2. Puente desde PHP: ya lo usas todos los días

### 📖 Explicación

En PHP, el **array asociativo** es un `Map`:
```php
$stock = ['camisa' => 10, 'pantalon' => 4];
$stock['gorra'] = 0;
echo $stock['camisa'];   // 10
```
En Java:
```java
Map<String, Integer> stock = new HashMap<>();
stock.put("camisa", 10);
stock.put("pantalon", 4);
stock.put("gorra", 0);
System.out.println(stock.get("camisa"));   // → 10
```

### Tabla de traducción (guárdala)

| Quiero… | PHP | Java |
|---------|-----|------|
| crear vacío | `$m = [];` | `Map<String, Integer> m = new HashMap<>();` |
| poner / reemplazar | `$m['k'] = 5;` | `m.put("k", 5);` |
| leer | `$m['k']` | `m.get("k")` |
| leer con valor por defecto | `$m['k'] ?? 0` | `m.getOrDefault("k", 0)` |
| ¿existe la clave? | `isset($m['k'])` / `array_key_exists` | `m.containsKey("k")` |
| ¿existe el valor? | `in_array(5, $m)` | `m.containsValue(5)` |
| borrar | `unset($m['k']);` | `m.remove("k");` |
| cuántos | `count($m)` | `m.size()` |
| solo las claves | `array_keys($m)` | `m.keySet()` |
| solo los valores | `array_values($m)` | `m.values()` |
| recorrer | `foreach ($m as $k => $v)` | `for (Map.Entry<String, Integer> e : m.entrySet())` |

### ⚠️ Tres diferencias con PHP que te van a morder

1. **En PHP el array sirve para todo** (lista y mapa a la vez). En Java son **cosas separadas**: `List` (por
   posición `0, 1, 2…`) y `Map` (por clave). Un `Map` **no tiene** `get(0)` "el primero".
2. **En Java los tipos se declaran**: `Map<String, Integer>` solo acepta claves `String` y valores `Integer`.
   `m.put(5, "hola")` **no compila**. En PHP puedes mezclar.
3. **Leer una clave que no existe:** en PHP te da un *warning* y `null`. En Java `get` devuelve `null` **sin
   avisar**, y el error explota **después**, en otra línea (lo verás en §4.4).

---

## 3. Declarar un `Map`

### 3.1 La forma

```java
Map<String, Integer> stock = new HashMap<>();
//  ↑      ↑                     ↑        ↑
//  │      tipo del VALOR        │        "diamante": Java copia los tipos de la izquierda
//  │                            implementación (la clase real)
//  tipo de la CLAVE
```

Se lee: "**un mapa de** `String` **a** `Integer`" (de nombre de producto a cantidad).

Es la misma idea que en la lección 01 con `List<Product> list = new ArrayList<>();`:

| | Interfaz (a la izquierda) | Implementación (a la derecha) |
|---|---|---|
| Lección 01 | `List` | `ArrayList` |
| Lección 02 | `Set` | `HashSet`, `LinkedHashSet`, `TreeSet` |
| **Esta** | **`Map`** | **`HashMap`, `LinkedHashMap`, `TreeMap`** |

**Por qué la interfaz a la izquierda:** si mañana necesitas orden, cambias solo la parte derecha
(`new LinkedHashMap<>()`) y el resto del código no se entera. En Spring Boot vas a ver esto **todo el tiempo**
(se llama programar contra interfaces).

### 3.2 ¿Por qué `Integer` y no `int`?

Las colecciones de Java (`List`, `Set`, `Map`) **solo guardan objetos**. `int` es un primitivo (§0.1), así que no
puede ir entre `< >`.

| Primitivo | Su "versión objeto" (*wrapper*) |
|-----------|-------------------------------|
| `int` | `Integer` |
| `double` | `Double` |
| `boolean` | `Boolean` |
| `long` | `Long` |

```java
Map<String, int> m;       // ❌ no compila
Map<String, Integer> m;   // ✅
```

Java convierte **solo** entre `int` e `Integer` cuando hace falta. Eso se llama **autoboxing** (meter en la caja)
y **unboxing** (sacar de la caja):
```java
stock.put("camisa", 10);          // 10 es int → Java lo mete en un Integer (autoboxing)
int x = stock.get("camisa");      // get da un Integer → Java lo convierte a int (unboxing)
```
Es cómodo, pero esconde una trampa con `null` que veremos en §4.4.

### 3.3 Los valores pueden ser **cualquier** objeto

```java
Map<String, Integer>          stockPorNombre;       // nombre → cantidad
Map<String, BigDecimal>       ventasPorCliente;     // cliente → total vendido
Map<Product, OrderItem>       lineasDelPedido;      // producto → su línea  ← lo que harás en Order
Map<String, List<String>>     productosPorCategoria; // categoría → varios productos
```

### 🧪 Pruébalo §3 (3 min)
1. En `MapPlayground`, escribe `Map<String, int> m = new HashMap<>();`. ¿Compila? Lee el error en rojo de IntelliJ.
2. Cámbialo a `Integer`. Ahora sí compila. Borra la línea.

### ✅ ¿Lo entendiste?
1. ¿Qué es la clave y qué es el valor en `Map<String, BigDecimal> ventasPorCliente`?
2. ¿Por qué no se puede escribir `Map<String, double>`?

<details><summary>Respuestas</summary>

1. Clave = el nombre del cliente (`String`); valor = cuánto ha comprado (`BigDecimal`).
2. Porque las colecciones solo guardan objetos y `double` es primitivo. Se usa `Double`.
</details>

---

## 4. Las operaciones básicas, una por una

Para todo §4 usamos esta tienda de ropa:
```java
Map<String, Integer> stock = new HashMap<>();
```

### 4.1 `put(clave, valor)`: poner o **reemplazar**

📖 `put` guarda el par. **Si la clave ya existía, reemplaza el valor viejo** (no crea otro). Y **devuelve** algo:
el valor **anterior**, o `null` si la clave era nueva.

💻
```java
System.out.println(stock.put("camisa", 10));    // → null   (camisa no existía)
stock.put("pantalon", 4);
stock.put("gorra", 0);
System.out.println(stock.put("camisa", 7));     // → 10     (devuelve el VIEJO; ahora vale 7)
System.out.println(stock);                      // → {camisa=7, pantalon=4, gorra=0}
System.out.println(stock.size());               // → 3      (no 4: camisa se reemplazó)
```

⚠️ **Recuerda la lección 01:** el **valor de retorno** (lo que `put` devuelve) es una cosa y el **efecto** (lo
que cambia en el mapa) es otra. `put("camisa", 7)` **devuelve** `10` pero **deja** `7`.

Compáralo con `Set.add`: en un `Set`, si el elemento ya está, `add` **no hace nada** y devuelve `false`.
En un `Map`, si la clave ya está, `put` **reemplaza el valor**.

### 4.2 `get(clave)`: leer

💻
```java
System.out.println(stock.get("camisa"));   // → 7
System.out.println(stock.get("zapato"));   // → null   (no existe; NO lanza excepción)
```

📖 `get` busca **por clave**, nunca por valor. `stock.get(7)` no busca "el que vale 7": busca la **clave** `7`,
que no existe → `null`.

### 4.3 `getOrDefault(clave, porDefecto)`: leer sin `null`

💻
```java
System.out.println(stock.getOrDefault("zapato", 0));   // → 0
System.out.println(stock.getOrDefault("camisa", 0));   // → 7   (existe: el default no se usa)
```

📖 "Dame el valor; **si no hay**, dame este otro". Es el `??` de PHP. No guarda nada en el mapa: `zapato` sigue
sin existir.

### 4.4 ⚠️ La trampa del `null` + `int`

💻
```java
int x = stock.get("zapato");   // → NullPointerException
```

📖 Qué pasa, paso a paso:
1. `stock.get("zapato")` devuelve `null` (no existe).
2. Java intenta convertir ese `null` a `int` (unboxing, §3.2).
3. Un `int` **no puede** ser `null` (es un número, siempre tiene un valor) → **`NullPointerException`**.

Lo peligroso es que el error aparece en **esta** línea, aunque el problema real es que "zapato" no estaba.

**Cómo evitarlo:** usa `getOrDefault`, o pregunta antes con `containsKey`.

### 4.5 `containsKey` / `containsValue`

💻
```java
System.out.println(stock.containsKey("gorra"));   // → true
System.out.println(stock.containsValue(4));       // → true   (pantalon vale 4)
```

📖 `containsKey` es **rápido** (va directo al cajón, §0.2). `containsValue` es **lento**: tiene que mirar
**todos** los valores uno por uno, como `List.contains`. Por eso se diseña el mapa para buscar **por clave**.

⚠️ **`get` devuelve `null` en dos casos distintos:** cuando la clave no existe, y cuando existe pero su valor es
`null` (`HashMap` lo permite: comprobado `put("k", null)` → `get("k")` da `null` y `containsKey("k")` da `true`).
Si necesitas distinguirlos, usa `containsKey`. Consejo práctico: **no guardes `null` como valor**.

### 4.6 `remove(clave)`

💻
```java
System.out.println(stock.remove("gorra"));    // → 0      (devuelve el valor que tenía)
System.out.println(stock.remove("zapato"));   // → null   (no existía: no pasa nada)
System.out.println(stock);                    // → {camisa=7, pantalon=4}
```

### 4.7 `size()` / `isEmpty()`

Igual que en `List` y `Set`: `size()` = cuántas **entradas** (pares clave-valor); `isEmpty()` = ¿`size()` es 0?

### Resumen de §4

| Método | Hace | Devuelve |
|--------|------|----------|
| `put(k, v)` | guarda; si `k` existía, **reemplaza** | valor anterior o `null` |
| `get(k)` | lee | valor o `null` |
| `getOrDefault(k, d)` | lee | valor o `d` |
| `containsKey(k)` | busca la clave (rápido) | `boolean` |
| `containsValue(v)` | busca el valor (lento) | `boolean` |
| `remove(k)` | borra | valor que tenía o `null` |
| `size()` | — | cuántas entradas |

### 🧪 Pruébalo §4 (10 min)
Usa **otros datos**: un mapa `Map<String, Integer> edades` (nombre → edad).
1. `put("Rosa", 30)` e imprime lo que **devuelve**. `// espero:`
2. `put("Rosa", 31)` e imprime lo que **devuelve**. `// espero:`
3. Imprime `edades.get("Rosa")` y `edades.size()`. `// espero:`
4. Imprime `edades.get("Tito")`, y después `edades.getOrDefault("Tito", -1)`. `// espero:`
5. Escribe `int e = edades.get("Tito");` dentro de un `try/catch (NullPointerException ex)` y en el `catch`
   imprime `"NPE: Tito no existe"`. `// espero:` ¿entra al `catch`?

### ✅ ¿Lo entendiste?
1. `m.put("a", 1); m.put("a", 2);` → ¿cuánto vale `m.size()`? ¿Y `m.get("a")`?
2. ¿Qué devuelve `m.put("b", 5)` si `"b"` no existía?
3. ¿Por qué `int n = m.get("zzz");` puede lanzar una excepción, si `get` no lanza excepciones?

<details><summary>Respuestas</summary>

1. `size()` = 1 (la clave `"a"` es única); `get("a")` = 2 (el segundo `put` reemplazó).
2. `null` (no había valor anterior).
3. `get` devuelve `null` sin problema; la excepción sale al **convertir** ese `null` a `int` (unboxing).
</details>

---

## 5. Recorrer un `Map`

### 📖 Explicación

Un `Map` te da **tres "vistas"**, es decir, tres formas de mirar lo mismo:

| Vista | Qué contiene | Tipo |
|-------|--------------|------|
| `keySet()` | solo las claves | `Set<K>` (¡un `Set`! porque las claves no se repiten) |
| `values()` | solo los valores | `Collection<V>` (pueden repetirse, por eso **no** es un `Set`) |
| `entrySet()` | los pares completos | `Set<Map.Entry<K, V>>` |

`Map.Entry<K, V>` es **un casillero completo**: tiene `getKey()` y `getValue()`.

💻 (con un `LinkedHashMap` para que el orden sea predecible; §7 explica por qué)
```java
Map<String, Integer> s = new LinkedHashMap<>();
s.put("camisa", 7);
s.put("pantalon", 4);
s.put("gorra", 2);

System.out.println(s.keySet());     // → [camisa, pantalon, gorra]
System.out.println(s.values());     // → [7, 4, 2]
System.out.println(s.entrySet());   // → [camisa=7, pantalon=4, gorra=2]

for (Map.Entry<String, Integer> e : s.entrySet()) {
    System.out.println(e.getKey() + " -> " + e.getValue());
}
// → camisa -> 7
// → pantalon -> 4
// → gorra -> 2
```

**Cómo leer la línea del `for`:**
```java
for (Map.Entry<String, Integer> e : s.entrySet())
//   ↑ cada casillero es un Entry  ↑ e = el casillero actual  ↑ todos los casilleros
```
Es tu `foreach ($s as $k => $v)` de PHP, pero con **un solo** objeto `e` que tiene las dos cosas.

### ¿Cuál vista uso?

| Necesito… | Usa |
|-----------|-----|
| solo las claves | `for (String k : s.keySet())` |
| solo los valores (ej. sumar) | `for (Integer v : s.values())` |
| **clave y valor** | `for (Map.Entry<...> e : s.entrySet())` ✅ |

⚠️ **Error común:** recorrer `keySet()` y hacer `s.get(k)` dentro del bucle para sacar el valor. Funciona, pero
busca **dos veces** cada cosa. Si necesitas los dos, usa `entrySet()`.

### ⚠️ No borres mientras recorres (igual que en la lección 01)

```java
for (String k : s.keySet()) {
    if (k.equals("camisa")) s.remove(k);    // → ConcurrentModificationException
}
```
Es la misma `ConcurrentModificationException` de la lección 01: el for-each usa un *iterator* oculto, y si
cambias el mapa por otro lado, el iterator se da cuenta y explota. Para borrar con una condición:
`s.keySet().removeIf(k -> k.equals("camisa"))` o `s.entrySet().removeIf(...)` (`removeIf` ya lo usaste en la
lección 01).

### 🧪 Pruébalo §5 (5 min)
Con tu mapa `edades` (agrega 3 personas en un `LinkedHashMap`):
1. Imprime `keySet()`, `values()` y `entrySet()`. `// espero:` con el orden exacto.
2. Recorre con `entrySet()` e imprime `"Rosa tiene 31 años"` para cada persona.
3. Recorre `values()` y suma las edades en un `int total`. Imprime el total. `// espero:`

### ✅ ¿Lo entendiste?
1. ¿Por qué `keySet()` devuelve un `Set` pero `values()` no?
2. Quiero imprimir "clave = valor" de cada entrada. ¿Qué vista uso?

<details><summary>Respuestas</summary>

1. Las claves nunca se repiten (eso es un `Set`); los valores sí pueden repetirse (dos productos con stock 4).
2. `entrySet()`, para tener `getKey()` y `getValue()` sin buscar dos veces.
</details>

---

## 6. El patrón más usado: acumular (contar o sumar)

### 📖 Explicación

El 80 % de los `Map` en un backend sirven para **acumular**: contar ventas por producto, sumar total por
cliente, agrupar por categoría. La receta siempre es igual:

> Para cada elemento: **si la clave no está**, empieza con un valor inicial; **si está**, actualiza el valor
> que había.

Problema de ejemplo: contar cuántas veces se vendió cada prenda.
```java
List<String> vendidos = List.of("gorra", "camisa", "gorra", "bufanda", "gorra", "camisa");
```
Resultado esperado: `{gorra=3, camisa=2, bufanda=1}`.

Hay **tres formas** de escribirlo. Las tres dan **el mismo resultado** (comprobado). Van de la más larga y
explícita a la más corta.

### 6.1 Versión larga: `if` + `containsKey`

💻
```java
Map<String, Integer> cuenta = new LinkedHashMap<>();
for (String p : vendidos) {
    if (cuenta.containsKey(p)) {
        cuenta.put(p, cuenta.get(p) + 1);   // ya estaba: lo que había + 1
    } else {
        cuenta.put(p, 1);                   // primera vez: empieza en 1
    }
}
// → {gorra=3, camisa=2, bufanda=1}
```

**Sigámoslo a mano** (esto es lo más importante de la sección):

| Vuelta | `p` | ¿Estaba? | Qué hace | Mapa después |
|--------|-----|----------|----------|--------------|
| 1 | gorra | no | `put(gorra, 1)` | `{gorra=1}` |
| 2 | camisa | no | `put(camisa, 1)` | `{gorra=1, camisa=1}` |
| 3 | gorra | sí | `put(gorra, 1+1)` | `{gorra=2, camisa=1}` |
| 4 | bufanda | no | `put(bufanda, 1)` | `{gorra=2, camisa=1, bufanda=1}` |
| 5 | gorra | sí | `put(gorra, 2+1)` | `{gorra=3, camisa=1, bufanda=1}` |
| 6 | camisa | sí | `put(camisa, 1+1)` | `{gorra=3, camisa=2, bufanda=1}` |

### 6.2 Versión media: `getOrDefault`

💻
```java
for (String p : vendidos) {
    cuenta.put(p, cuenta.getOrDefault(p, 0) + 1);
}
```
📖 "Lo que había (o `0` si no había) + 1". Hace lo mismo que el `if/else`: si no estaba, `0 + 1 = 1`.

### 6.3 Versión corta: `merge`

💻
```java
for (String p : vendidos) {
    cuenta.merge(p, 1, Integer::sum);
}
```
📖 `merge(clave, valorNuevo, cómoJuntar)` se lee así:
- si la clave **no está** → guarda `valorNuevo` (aquí `1`);
- si **está** → junta el valor viejo con `valorNuevo` usando `cómoJuntar`. `Integer::sum` significa
  "**súmalos**" → viejo + 1.

> `Integer::sum` es un *method reference* (semana 8). **Por ahora tómalo como una receta**: `Integer::sum` =
> "sumar dos `Integer`". Lo vas a entender a fondo más adelante.

### 6.4 Con `BigDecimal` (dinero): mismo patrón

```java
Map<String, BigDecimal> ventas = new LinkedHashMap<>();
ventas.merge("Ana",  new BigDecimal("20.50"), BigDecimal::add);
ventas.merge("Luis", new BigDecimal("5"),     BigDecimal::add);
ventas.merge("Ana",  new BigDecimal("9.50"),  BigDecimal::add);
System.out.println(ventas);   // → {Ana=30.00, Luis=5}
```
`BigDecimal::add` = "súmalos con `add`" (recuerda: `BigDecimal` no se suma con `+`).

### ¿Cuál uso?

| Versión | Cuándo |
|---------|--------|
| 6.1 `if/else` | **mientras aprendes**: es la más clara. Úsala sin vergüenza |
| 6.2 `getOrDefault` | cuando ya la entiendes; es la más común para contar |
| 6.3 `merge` | código profesional; corta, pero hay que conocerla |

En las tareas te voy a pedir **primero la 6.1** y después convertirla a las otras, comprobando que el resultado
no cambia (eso se llama **refactorizar**).

### 6.5 Agrupar: el valor es una lista

Otro uso muy común: **varios** elementos por clave. "¿Qué productos hay en cada categoría?"
```java
Map<String, List<String>> porCategoria = new LinkedHashMap<>();
// datos: (Ropa, camisa) (Tech, mouse) (Ropa, gorra) (Tech, cable) (Hogar, vaso)

// para cada (categoria, producto):
List<String> lista = porCategoria.get(categoria);
if (lista == null) {                 // primera vez que veo esta categoría
    lista = new ArrayList<>();
    porCategoria.put(categoria, lista);
}
lista.add(producto);                 // agrego a la lista (nueva o la que ya estaba)

// → {Ropa=[camisa, gorra], Tech=[mouse, cable], Hogar=[vaso]}
```
📖 Es la misma receta de §6.1: si no está, crea el valor inicial (una lista vacía); después, actualízalo
(agrega a la lista). Esto es el `groupBy` de Laravel Collections, hecho a mano. En la semana 10 lo harás con
Streams en una línea.

### 🧪 Pruébalo §6 (10 min)
Datos: `List<String> votos = List.of("rojo", "azul", "rojo", "verde", "azul", "rojo", "rojo");`
1. **Antes de escribir código**, llena a mano una tabla de vueltas como la de §6.1 (en un comentario). ¿Qué
   mapa queda al final?
2. Escribe la versión 6.1 (`if/else`) con un `LinkedHashMap`. Imprime. `// espero:` (tu tabla).
3. Escribe la versión 6.2 en un **segundo** mapa. Imprime. ¿Es igual?

### ✅ ¿Lo entendiste?
1. En `cuenta.put(p, cuenta.getOrDefault(p, 0) + 1)`, ¿qué pasa la primera vez que aparece `p`?
2. ¿Qué hace `merge("x", 5, Integer::sum)` si `"x"` ya vale 10? ¿Y si `"x"` no existe?

<details><summary>Respuestas</summary>

1. `getOrDefault` da `0` (no estaba) → `0 + 1 = 1` → `put(p, 1)`.
2. Si vale 10 → queda `15` (10 + 5). Si no existe → queda `5`.
</details>

---

## 7. Claves que son objetos: aquí vuelve la lección 02

### 📖 Explicación

Un `HashMap` guarda sus **claves exactamente como un `HashSet`**: en cajones, usando `hashCode()` para elegir el
cajón y `equals()` para comparar dentro (§0.2). De hecho, por dentro, un `HashSet` **es** un `HashMap` donde
solo se usan las claves.

Con `String` o `Integer` como clave no tienes que pensar: Java ya les escribió `equals` y `hashCode`.
Con **tus propias clases** (como `Product`) como clave, **depende de lo que escribiste en la lección 02**.

### 7.1 Clave con `equals` + `hashCode` bien hechos

Ejemplo con una clase `Coupon` (cupón de descuento) que compara por su `code`, igual que tu `Product` compara
por `id`:
```java
Coupon a = new Coupon("PROMO10");
Coupon b = new Coupon("PROMO10");          // OTRO objeto, mismo código

a == b                                      // → false  (dos objetos distintos)
a.equals(b)                                 // → true   (mismo código)
a.hashCode() == b.hashCode()                // → true   (cumple la regla de oro)

Map<Coupon, Integer> usos = new HashMap<>();
usos.put(a, 1);
System.out.println(usos.put(b, 2));         // → 1   ← reemplazó: para el mapa, b ES la misma clave que a
System.out.println(usos.size());            // → 1
System.out.println(usos.get(new Coupon("PROMO10")));   // → 2   ← lo encuentra con un objeto NUEVO
```
Esto es lo que quieres en un e-commerce: **el producto con id 1 es el producto con id 1**, sin importar
cuántas veces lo hayas creado con `new` (por ejemplo, cada vez que lo lees de la base de datos).

### 7.2 Clave **sin** `equals`/`hashCode`

Si `Coupon` no tuviera `equals`/`hashCode` (usaría los de `Object` = compara si es el mismo objeto):
```java
bad.put(new Coupon("PROMO10"), 1);
bad.put(new Coupon("PROMO10"), 2);
bad.size()                          // → 2      ← el mismo cupón, dos veces
bad.get(new Coupon("PROMO10"))      // → null   ← nunca lo encuentras
```
Es **exactamente** el bug de la Ronda 1 de la lección 02, ahora en un `Map`.

### 7.3 La colisión en un `Map`

Con las claves `"Aa"` y `"BB"` (mismo `hashCode` 2112, §0.3):
```java
col.put("Aa", 1);
col.put("BB", 2);
col.size()      // → 2
col.get("Aa")   // → 1
col.get("BB")   // → 2
```
Mismo cajón, pero `equals` dice que son distintas → **dos claves**, cada una con su valor. La colisión **no
rompe nada**; solo hace un poquito más lenta la búsqueda en ese cajón.

### 7.4 ⚠️ Nunca cambies una clave después de meterla

Si un objeto que es clave **cambia** el campo que usa `hashCode`, su `hashCode` cambia → el mapa lo busca en
**otro cajón** → `get` da `null` aunque la entrada sigue ahí (comprobado con una lista como clave: después de
modificarla, `get` → `null` y `size` → `1`).

Por eso pusiste `id` como **`final`** en `Product` (lección 02): un `id` que no puede cambiar = una clave
segura. Reglas prácticas:
- usa como clave objetos que **no cambian** (`String`, `Integer`, `BigInteger`, o tu clase con campos `final`);
- `equals`/`hashCode` deben usar **solo** campos que no cambian.

### 🧪 Pruébalo §7 (5 min)
1. Crea `Map<Product, Integer> reservas = new HashMap<>();`.
2. Crea **dos** objetos `Laptop` con **el mismo id** (`"9"`) y nombres distintos.
3. `put(primero, 1)` y después imprime lo que devuelve `put(segundo, 5)`. `// espero:`
4. Imprime `size()` y `get(primero)`. `// espero:`
5. Pregunta para escribir en un comentario: ¿por qué funciona así? Usa las palabras **`hashCode`**, **cajón** y
   **`equals`**.

### ✅ ¿Lo entendiste?
1. Si `Product` **solo** tuviera `equals` (sin `hashCode`), ¿`map.get(new Product(id 1))` encontraría el producto?
2. Dos claves con el mismo `hashCode`, ¿son la misma clave?

<details><summary>Respuestas</summary>

1. Casi nunca: el `hashCode` de `Object` es distinto para cada objeto → busca en otro cajón → `equals` ni se llama → `null`.
2. No necesariamente (colisión). Son la misma clave **solo si** además `equals` da `true`.
</details>

---

## 8. Las tres implementaciones: el orden

### 📖 Explicación

Igual que `Set` (§0.4), y por la misma razón: un `XxxSet` **es** un `XxxMap` por dentro.

| Implementación | Orden de las claves | Necesita en la clave |
|----------------|---------------------|----------------------|
| `HashMap` | **ninguno garantizado** (el de los cajones) | `equals` + `hashCode` |
| `LinkedHashMap` | **orden de inserción** (como llegaron) | `equals` + `hashCode` |
| `TreeMap` | **ordenado** por la clave | saber comparar (`Comparable`, semana 4) |

💻 Mismas 5 ciudades (valor = orden de llegada), en los tres:
```java
// llegan en este orden: Quito, lima, Bogotá, Cusco, Asunción
HashMap        → {Asunción=5, lima=2, Cusco=4, Quito=1, Bogotá=3}   ← revuelto
LinkedHashMap  → {Quito=1, lima=2, Bogotá=3, Cusco=4, Asunción=5}   ← como llegaron
TreeMap        → {Asunción=5, Bogotá=3, Cusco=4, Quito=1, lima=2}   ← alfabético; "lima" (minúscula) al final
```
Fíjate: `TreeMap` ordena por la **clave**, no por el valor. Y como en la lección 02, las mayúsculas van antes
que las minúsculas.

### `TreeMap` con tus clases

Igual que `TreeSet`: un `new TreeMap<>()` se **crea** sin problema, pero el primer `put` con una clave que no
sabe compararse lanza `ClassCastException` (comprobado). Se resuelve en la semana 4 (`Comparable`).

### ¿Cuál uso?

- **`HashMap`** por defecto, si el orden no importa. Es el más rápido y el que menos memoria usa.
- **`LinkedHashMap`** si importa el orden en que llegaron (ej. líneas de un pedido: el cliente espera verlas en
  el orden en que las agregó).
- **`TreeMap`** si los quieres ordenados por la clave (ej. un reporte por fecha o alfabético).

### ✅ ¿Lo entendiste?
1. Las líneas de un pedido deben salir en el orden en que el cliente agregó los productos. ¿Qué `Map`?
2. ¿`TreeMap` ordena por la clave o por el valor?

<details><summary>Respuestas</summary>

1. `LinkedHashMap`.
2. Por la clave.
</details>

---

## 9. Mapas que no se pueden modificar

### 📖 Explicación

Igual que con `List.copyOf` en tu `Order.getItems()`: si una clase tiene un `Map` interno, **no debe
entregarlo** para que lo modifiquen desde fuera (encapsulación).

| Forma | ¿Se puede modificar? | ¿Conserva el orden de un `LinkedHashMap`? | Notas |
|-------|----------------------|------------------------------------------|-------|
| `Map.of("S", 1, "M", 2)` | ❌ no | — (orden revuelto) | para mapas fijos pequeños; máximo 10 pares |
| `Map.copyOf(mapa)` | ❌ no | ❌ **no** | copia; el orden queda revuelto **y cambia en cada ejecución** |
| `Collections.unmodifiableMap(mapa)` | ❌ no | ✅ sí | **vista**: no es copia (ver abajo) |

💻 Todo comprobado:
```java
Map<String, Integer> tallas = Map.of("S", 1, "M", 2, "L", 3);
tallas.put("XL", 4);            // → UnsupportedOperationException
Map.of("S", 1, "S", 2);         // → IllegalArgumentException: duplicate key: S
Map.of("S", null);              // → NullPointerException (no acepta null)

// orig es un LinkedHashMap con: zeta, alfa, mango, kiwi, beta, sol
Map.copyOf(orig)                    // → {kiwi=4, beta=4, sol=3, alfa=4, mango=5, zeta=4}   ← revuelto
                                    //   (ejecutado 3 veces: salió en 3 órdenes distintos)
Collections.unmodifiableMap(orig)   // → {zeta=4, alfa=4, mango=5, kiwi=4, beta=4, sol=3}   ← orden intacto
```

**Vista vs copia:** `unmodifiableMap` no copia nada: es una "ventana de solo lectura" al mapa original. Quien
la recibe **no puede** cambiarla, pero **sí ve** los cambios que haga el dueño (comprobado: el dueño agregó una
clave y la vista la mostró). Para un getter eso está bien: el que recibe solo lee.

> ⚠️ **Esto es lo mismo que pasa con `Set.copyOf` en tu Tarea 6 de la lección 02**: si guardas en un
> `LinkedHashSet` para conservar el orden y después devuelves `Set.copyOf(...)`, el orden se pierde. La regla es
> igual para `Set` y `Map`: **si el orden importa, devuelve `Collections.unmodifiableXxx(...)`**.

### ✅ ¿Lo entendiste?
1. Tu `Order` tendrá un `LinkedHashMap` interno. ¿Cómo lo devuelves en un getter sin perder el orden y sin que lo modifiquen?
2. ¿Qué pasa con `Map.of("a", 1, "a", 2)`?

<details><summary>Respuestas</summary>

1. `Collections.unmodifiableMap(mapaInterno)` (o, mejor aún, devolver los **valores** de la misma forma; lo veremos en las tareas).
2. `IllegalArgumentException` (clave duplicada).
</details>

---

## 10. `List`, `Set` o `Map`: cómo elegir

Hazte estas preguntas **en orden**:

| # | Pregunta | Si es sí → |
|---|----------|-----------|
| 1 | ¿Necesito **buscar algo por una clave** (id, nombre, código) y obtener otra cosa? | **`Map`** |
| 2 | ¿Cada elemento debe aparecer **una sola vez** y solo pregunto "¿está?" | **`Set`** |
| 3 | ¿Importa la **posición** o puede haber **repetidos**? | **`List`** |

Ejemplos del e-commerce:

| Necesidad | Colección |
|-----------|-----------|
| los items de un carrito, en orden, pueden repetirse | `List<OrderItem>` |
| los productos distintos de un pedido | `Set<Product>` |
| la línea de cada producto en un pedido (sin líneas duplicadas) | `Map<Product, OrderItem>` |
| stock por producto | `Map<Product, Integer>` |
| total vendido por cliente | `Map<Customer, BigDecimal>` |
| productos de cada categoría | `Map<String, List<Product>>` |

---

## 11. Resumen

### Errores comunes

| Error | Qué pasa | Solución |
|-------|----------|----------|
| `int x = map.get(k)` con `k` inexistente | `NullPointerException` (unboxing de `null`) | `getOrDefault` o `containsKey` |
| creer que `put` en una clave existente agrega otra entrada | la **reemplaza**; `size` no cambia | recordar: claves únicas |
| `map.get(valor)` | `get` busca por **clave** | buscar por clave, o recorrer |
| clase propia como clave sin `equals`/`hashCode` | duplicados y `get` → `null` | regla de oro (lección 02) |
| cambiar el campo de una clave después del `put` | el objeto se pierde en el cajón viejo | claves inmutables (`final`) |
| `remove` dentro de un for-each | `ConcurrentModificationException` | `removeIf` |
| `keySet()` + `get(k)` en el bucle | busca dos veces | `entrySet()` |
| `Map.copyOf` cuando importa el orden | el orden se pierde | `Collections.unmodifiableMap` |
| `TreeMap` con clave que no sabe compararse | `ClassCastException` en el `put` | `Comparable` (semana 4) |

### Relación con Spring Boot (solo para que sepas que existe)

- Un **JSON** `{"nombre": "Ana", "edad": 30}` es, para Java, un `Map<String, Object>`. Jackson (la librería que
  convierte JSON en Spring) usa mapas por todos lados.
- Los **parámetros** de una petición HTTP (`?page=2&size=10`) son un mapa clave → valor.
- Las **cachés** son mapas: clave = lo que preguntaste, valor = la respuesta guardada.
- En JPA, las entidades suelen terminar como claves de `Map` o en un `Set`: por eso `equals`/`hashCode` por `id`
  importa tanto.

### Glosario

| Palabra | Significado |
|---------|-------------|
| **clave** (*key*) | la "etiqueta" con la que buscas; única |
| **valor** (*value*) | lo que guardas en esa clave; puede repetirse |
| **entrada** (*entry*) | un par clave + valor (`Map.Entry`) |
| **autoboxing / unboxing** | conversión automática entre `int` ↔ `Integer` (y otros primitivos) |
| **vista** | otra forma de mirar los mismos datos, sin copiarlos (`keySet`, `unmodifiableMap`) |
| **colisión** | dos claves distintas con el mismo `hashCode`; `equals` las separa |
| **inmutable** | que no se puede cambiar después de crearlo |

---

## 12. Autoevaluación final (sin mirar)

Respóndelas en un comentario al final de `MapPlayground.java`. Si **8 de 10** te salen solas, estás listo para
las tareas. Si no, dime cuáles fallaste y las repasamos.

1. ¿Qué diferencia hay entre `put` en un `Map` y `add` en un `Set` cuando el elemento ya existe?
2. ¿Qué devuelve `put` la primera vez y qué devuelve la segunda vez con la misma clave?
3. ¿Por qué `int n = map.get("x");` puede lanzar `NullPointerException`?
4. ¿Qué vista usas para recorrer clave y valor a la vez?
5. Escribe en palabras la receta de "acumular" (§6).
6. ¿Qué hace `merge(k, 1, Integer::sum)`?
7. ¿Qué necesita `Product` para ser una buena clave de `HashMap`? ¿Y por qué `id` debe ser `final`?
8. Dos claves con el mismo `hashCode` y `equals` en `false`: ¿cuántas entradas hay?
9. `HashMap`, `LinkedHashMap`, `TreeMap`: ¿cuál por defecto y cuándo cada uno?
10. ¿Por qué `Map.copyOf` no sirve para devolver un `LinkedHashMap` cuando importa el orden?

---

#### 🔍 REVISIÓN TEORÍA + AUTOEVALUACIÓN

| Versión | Fecha | Nota | Estado |
|---------|-------|------|--------|
| v1 | 2026-10-15 | **7/10** | 🟡 buena base; faltan §6 y la mitad del §7 |

Revisado ejecutando `MapPlayground`. **Todas tus predicciones de §4 y §5 coincidieron con el terminal** (`null`,
`30`, `31`, `1`, `null`, `-1`, el `NPE` y el `101`). En las lecciones 01 y 02 eso casi no pasaba. 👏

| Sección | Estado | Comentario |
|---------|--------|------------|
| 🧪 §3 | ✅ | |
| ✅ §3 | 🟡 | la respuesta 1 quedó cortada y falta la 2 |
| 🧪 §4 | ✅ | 7 de 7 predicciones bien |
| ✅ §4 | 🟡 | la pregunta 1 está **al revés** (ver concepto A) |
| 🧪 §5 | ✅ | el orden exacto de `LinkedHashMap` predicho bien. En el paso 3 sumaste con `entrySet()`; pedía `values()` (detalle, abajo) |
| 🧪 §6 | ❌ | **no está**. Es el patrón más importante de la lección (concepto B) |
| 🧪 §7 | ❌ | el experimento no probó nada: usaste `laptop` dos veces (concepto C) |
| ✅ §7, §8 | ✅ | |
| ✅ §9 | 🟡 | la pregunta 2: `Map.of("a",1,"a",2)` no "mezcla", **lanza** `IllegalArgumentException` |
| Autoevaluación | 🟡 **5,5 / 10** | 1 ❌, 5 vacía, 6 a medias, 8 vacía, 3/7/10 incompletas |

Dijiste que **te faltaron conceptos** en la autoevaluación. Aquí están, uno por uno, explicados de otra forma.

---

**Concepto A — `put` del `Map` vs `add` del `Set` (preguntas 1 y §4-1)**

Escribiste "vale 2, y el `get("a")` es de 1". Es **al revés**: `size` = **1**, `get("a")` = **2**.

Imagina **un casillero** con la etiqueta `"a"`:
```
m.put("a", 1);   → casillero "a" vacío → mete 1             → hay 1 casillero, adentro: 1
m.put("a", 2);   → casillero "a" YA existe → saca 1, mete 2  → sigue habiendo 1 casillero, adentro: 2
```
- `size()` cuenta **casilleros** → `1`.
- `get("a")` mira **adentro** → `2` (el último que metiste).

Y la diferencia con `Set`:

| | `Set.add(x)` con `x` repetido | `Map.put(k, v)` con `k` repetida |
|---|---|---|
| ¿Cambia algo? | **no**: lo ignora | **sí**: **reemplaza** el valor |
| ¿Qué devuelve? | `false` (un `boolean`) | el **valor viejo** |

Autoevaluación 1, frase para completar: *"Si ya existe, `Set.add` ___ y devuelve ___; `Map.put` ___ el valor y devuelve ___."*

---

**Concepto B — la receta de acumular (preguntas 5 y 6)** · ⚠️ es lo que más vas a usar en la Tarea 6

En PHP ya lo has hecho mil veces:
```php
$cuenta = [];
foreach ($ventas as $p) {
    if (isset($cuenta[$p])) { $cuenta[$p] = $cuenta[$p] + 1; }   // ya estaba: súmale
    else                    { $cuenta[$p] = 1; }                 // primera vez: empieza en 1
}
```
En Java es **lo mismo**, cambiando `isset` → `containsKey`, `$cuenta[$p] =` → `put` y `$cuenta[$p]` → `get`.

**La receta, en dos frases** (es la respuesta 5):
> 1. Si la clave **no está**, ponle el **valor inicial** (contar: `1`; sumar: la primera cantidad).
> 2. Si **ya está**, **saca** el valor viejo, **calcula** el nuevo y **vuelve a guardarlo** con `put`.

**`merge(k, 1, Integer::sum)`** (respuesta 6) es esa misma receta en una sola línea:
- si `k` no está → guarda `1`;
- si está → guarda `viejo + 1`.

"Suma 1 a k" es **la mitad** de la respuesta. Falta el caso "si no está, pone 1". Las dos mitades importan:
sin la primera, el primer producto no tendría dónde sumar.

Haz el 🧪 §6 (votos), **empezando por la tabla a mano**. Es la Tarea 0.

---

**Concepto C — el experimento del §7 no probó nada**

Pusiste `laptop` **las dos veces**:
```java
reservas.put(laptop, 1);
reservas.put(laptop, 5);    // ← mismo objeto. laptop1 nunca se usó
```
Así, `size` = 1 siempre, **aunque `Product` no tuviera `equals` ni `hashCode`**: es el mismo objeto, así que
`==` ya da `true`. El experimento tenía que usar **dos objetos distintos con el mismo id** (`laptop` y `laptop1`).
Solo así se prueba que **tu `equals`/`hashCode`** hacen su trabajo.

Es el mismo tipo de error que en la lección 01, Tarea 5: el experimento tiene que tener **una sola diferencia**
con el caso normal, y esa diferencia es la que se pone a prueba.

**Y el `espero` del `put`:** escribiste "la laptop que agregué primero". `put` devuelve el **valor** viejo (un
`Integer`: `1`), **nunca la clave**. La clave no se toca: se queda la primera.

---

**Concepto D — colisión (pregunta 8)**

Pregunta: dos claves, **mismo `hashCode`**, `equals` = `false`. ¿Cuántas entradas hay? → **2**.

Recuerda los dos pasos para buscar (§0.2):
```
paso 1: hashCode → ¿qué cajón?            "Aa" → cajón 2112   "BB" → cajón 2112   (mismo cajón)
paso 2: equals   → ¿es el mismo?          "Aa".equals("BB") → false             → NO es el mismo
→ el mapa guarda las dos, en el mismo cajón, una al lado de la otra → 2 entradas
```
**Analogía:** dos personas llamadas "Juan Pérez" en el mismo hotel. Tienen el mismo nombre (el mismo cajón),
pero el pasaporte (`equals`) dice que son distintas → **dos habitaciones**.

Lo vas a ver con **tus propios productos** en la Tarea 4 (ids `31` y `4294967296`: es la deuda de la lección 02).

---

**Detalles (MEJORABLE)**
- **Autoevaluación 3:** "es posible que no exista" es la causa, pero falta el **mecanismo**: `get` devuelve
  `null` → Java intenta convertirlo a `int` (**unboxing**) → un `int` no puede ser `null` → `NPE`.
- **Autoevaluación 7:** falta el "por qué `final`": si el `id` cambiara, cambiaría el `hashCode`, y el
  producto quedaría **perdido en el cajón viejo** (`get` → `null`).
- **Autoevaluación 10:** "los mezcla" ✅. Completa: "*…y el orden cambia en cada ejecución; `unmodifiableMap`
  conserva el orden porque es una vista, no una copia*".
- **🧪 §5, paso 3:** con `entrySet()` funciona, pero si **solo** necesitas los valores, `values()` es lo directo
  (§5, tabla "¿Cuál vista uso?").
- **`imprimir(Objects o)`:** `Objects` (con **s**) es una clase de utilidades (`Objects.hash`), no "cualquier
  objeto". Lo que querías es `Object` (sin s). Como no usas el método, bórralo.
- **Git (NO RELEVANTE AHORA):** en los últimos commits se subieron archivos `.class` de `out/production/`. Son
  archivos compilados; no deberían ir en git. Cuando quieras, lo arreglamos juntos (no lo hagas solo).

---

## 13. Tareas

### Reglas para todas las tareas

1. **Una tarea por vez, en orden.** Al terminar cada una puedes pedirme "revisa tarea N".
2. **Plantilla obligatoria** en cada `println`:
   ```java
   System.out.println("---- T1 · Paso 3 ----");   // separador antes de cada paso
   System.out.println(algo);
   // espero: ...                                 // ANTES de ejecutar
   // real: ...    porque: ...                    // SOLO si fallaste
   ```
3. **Las preguntas** (P1.1, P1.2…) se responden **en un comentario al final del archivo**, completando la frase
   que te doy. Las frases son para ayudarte, no para limitarte: puedes escribir más.
4. **No copies los ejemplos de la teoría**: las tareas usan **otros datos** a propósito.
5. **Si un paso no se entiende, no lo adivines.** Pregúntame "no entiendo T2 paso 4". Prefiero eso a un paso mal
   hecho.
6. **Checklist** al final de cada tarea: revísala antes de pedir la revisión. Es lo que yo voy a mirar.
7. **Sintaxis que todavía no viste** (lambdas `->`, method references `::` como `Integer::sum` o
   `BigDecimal::add`): **siempre es opcional**. Si un paso la pide, puedes resolverlo con lo que ya sabes
   (`for-each`, `if/else`, `containsKey` + `get` + `put`) y cuenta como correcto. En las Tareas 3 y 4, `merge`
   es opcional: la versión `if/else` vale igual.

**Tiempo total aproximado:** 3 h 30 min.

| Tarea | Tema | Lee antes | Archivo | Tiempo |
|-------|------|-----------|---------|--------|
| 0 | Cerrar la teoría | revisión de arriba | `MapPlayground` | 20 min |
| 1 | Operaciones básicas con dinero | §4 | `MapBasics` | 25 min |
| 2 | Recorrer | §5 | `MapBasics` | 25 min |
| 3 | Acumular: contar y sumar | §6 | `SalesCounterLab` | 35 min |
| 4 | Claves objeto: `Customer` | §7 + §0.2 + §0.3 | `CustomerSpendingLab` + `Customer` | 40 min |
| 5 | El orden y los getters | §8 + §9 | `MapOrderLab` | 30 min |
| 6 | **Aplicación:** `Order` sin líneas repetidas | todo | `Order` + `Main` | 60 min |

Todos los archivos nuevos van en `src/practical/maps/`, con `package practical.maps;` y un `main`.

---

### Tarea 0 — Cerrar la teoría (fácil · 20 min)

**Archivo:** `MapPlayground.java` (el que ya tienes).

1. **🧪 §6 (votos):** primero la **tabla de vueltas a mano**. Copia esta plantilla en un comentario de
   `MapPlayground` y **llena las columnas vacías** sin ejecutar nada. Hazlo fila por fila: cada fila empieza con
   el "Mapa después" de la fila anterior.
   ```
   votos = rojo, azul, rojo, verde, azul, rojo, rojo

   | Vuelta | p     | ¿Estaba? (sí/no) | Qué hace (put ...) | Mapa después |
   |--------|-------|------------------|--------------------|--------------|
   | 1      | rojo  |                  |                    |              |
   | 2      | azul  |                  |                    |              |
   | 3      | rojo  |                  |                    |              |
   | 4      | verde |                  |                    |              |
   | 5      | azul  |                  |                    |              |
   | 6      | rojo  |                  |                    |              |
   | 7      | rojo  |                  |                    |              |
   ```
   **Cómo llenar una fila** (ejemplo con **otros** datos: `"lápiz"`, cuando el mapa ya es `{goma=1, lápiz=2}`):
   ```
   | 5 | lápiz | sí | put(lápiz, 2+1) | {goma=1, lápiz=3} |
   ```
   - **¿Estaba?** → ¿la clave aparece en el "Mapa después" de la fila **anterior**? (En la vuelta 1 el mapa está
     vacío: `{}`.)
   - **Qué hace** → si no estaba: `put(color, 1)`; si estaba: `put(color, viejo+1)`, escribiendo el número viejo.
   - **Mapa después** → el mapa completo, con las claves **en el orden en que llegaron por primera vez**
     (`LinkedHashMap`).

   La última fila es tu `espero` para el código. Después escribe la versión `if/else` y la versión
   `getOrDefault`, cada una en **su propio** `LinkedHashMap`, e imprime cada mapa con su `espero`.
2. **🧪 §7, rehecho:** cambia el segundo `put` para que use **`laptop1`** (no `laptop`). Corrige el `espero` del
   `put`: devuelve un **valor**, no un producto. Después agrega el paso 5 que faltaba, completando esta frase:
   *"`laptop` y `laptop1` son objetos distintos, pero el mapa los trata como la misma clave porque su
   `hashCode` ___ (mismo cajón) y su `equals` ___."*
3. **Autoevaluación:** reescribe las preguntas 1, 5, 6 y 8 con lo que leíste arriba (conceptos A, B, D).
   Completa 3, 7 y 10 con los detalles.
4. Borra el método `imprimir(Objects o)`.

**Checklist:**
- [ ] tabla de votos hecha **antes** del código
- [ ] §7 con `laptop1` + frase completa
- [ ] autoevaluación 1, 5, 6, 8 reescritas

---

### Tarea 1 — Operaciones básicas con dinero (fácil · 25 min)

**Objetivo:** usar cada método de §4 con valores `BigDecimal` (dinero), no solo con `Integer`.
**Antes:** relee §4 (sobre todo la tabla "Resumen de §4").
**Archivo:** `src/practical/maps/MapBasics.java`

**Contexto:** un catálogo de precios de una tienda de computación. Clave = nombre del producto, valor = precio.

**Pasos:**
1. Crea `Map<String, BigDecimal> precios = new LinkedHashMap<>();`. (Usamos `LinkedHashMap` solo para que el
   orden al imprimir sea predecible; §8.)
2. Imprime lo que **devuelve** `precios.put("teclado", new BigDecimal("45.90"))`. `// espero:`
3. Agrega, sin imprimir: `"monitor"` → `"189.00"` y `"cable"` → `"7.50"`.
4. **El teclado bajó de precio.** Imprime lo que devuelve `precios.put("teclado", new BigDecimal("39.90"))`.
   `// espero:`. Después imprime `precios` y `precios.size()`, cada uno con su `espero`.
5. Imprime `precios.get("mouse")`. `// espero:`
6. Imprime `precios.getOrDefault("mouse", BigDecimal.ZERO)`. `// espero:`
7. **La trampa con objetos.** Dentro de un `try/catch (NullPointerException e)`, escribe:
   `BigDecimal conIva = precios.get("mouse").add(new BigDecimal("1"));`. En el `catch`, imprime
   `"NPE: mouse no existe"`. `// espero:` ¿entra al `catch`?
   > Ojo: aquí **no hay unboxing** (`BigDecimal` no es un primitivo). Piensa por qué explota igual. Pista: ¿qué
   > valor tiene `precios.get("mouse")` y qué le estás pidiendo a ese valor?
8. Imprime `precios.containsKey("cable")` y `precios.containsValue(new BigDecimal("7.50"))`. `// espero:` en cada uno.
9. **Lección 02 regresa:** imprime `precios.containsValue(new BigDecimal("7.5"))` (sin el último cero).
   `// espero:` → piensa en tu experimento `2.0` vs `2.00` de la lección 02.
10. Imprime lo que devuelve `precios.remove("cable")` y después lo que devuelve `precios.remove("cable")`
    **otra vez**. `// espero:` en cada uno.

**Preguntas:**
- **P1.1** (paso 4) *"`put` devolvió ___ porque ___. Después del `put`, `size` es ___ porque ___."*
- **P1.2** (paso 7) *"Explotó porque `get("mouse")` devolvió ___, y en la misma línea le pedí `.add(...)` a ___.
  No es unboxing porque `BigDecimal` es un ___, pero igual no se puede llamar un método sobre `null`."*
- **P1.3** (paso 9) *"`containsValue` con `7.5` dio ___ porque `BigDecimal.equals` compara también ___. Para
  comparar dinero sin importar los ceros uso ___."* (Pista: el método que usaste en `isValidValue`.)

**Checklist:**
- [ ] 10 pasos, cada uno con separador y `espero`
- [ ] `real` + `porque` en cada predicción fallada
- [ ] P1.1–P1.3 con las frases completas


> ## 🔍 REVISIÓN TAREA 1 — 2026-10-15 · **9/10** · ✅ aprobada
> Historial: v1 7/10 → **v2 9/10**. Revisado ejecutando `MapBasics`.

**✅ Ya está bien:** `LinkedHashMap` declarado como `Map`. Acertaste lo que devuelve `put` (nuevo → `null`,
reemplazo → `45.90`), `get` de una clave que no existe (`null`), `getOrDefault` (`0`) y el NPE del paso 7.
En el paso 10 fallaste y escribiste `real` + `porque` correctos. P1.1 correcta.

**Tus predicciones contra la salida real:**

| Paso | Línea | Tu predicción | Salida real | |
|------|-------|---------------|-------------|---|
| 2 | `put("teclado", 45.90)` | `null` | `null` | ✅ |
| 4 | `put("teclado", 39.90)` | `45.90` | `45.90` | ✅ |
| 4 | `precios` | `{teclado=49.90, ...}` | `{teclado=45.90, ...}` (y se imprimió **antes** del `put`) | ⚠️ |
| 5–6 | `get` / `getOrDefault` | `null` / `0` | `null` / `0` | ✅ |
| 7 | NPE | entra al `catch` | entra al `catch` | ✅ |
| 8 | `containsKey` / `containsValue(7.50)` | `true` / — | `true` / **no está en el código** | ❌ |
| 9 | `containsValue(7.5)` | `false` | `false`, pero por **otra razón** (abajo) | ⚠️ |
| 10 | `remove("cable")` ×2 | `true` / `false` | `39.90` / `null` | ❌ con `porque` ✅ |

---

#### ❌ CRÍTICO 1 — `cable` vale `39.90`, no `7.50`: el experimento del paso 9 no prueba nada

**Qué pasa:** el paso 3 pedía `"cable"` → `"7.50"`. Pusiste `"39.90"`. Por eso:
- falta el `containsValue(new BigDecimal("7.50"))` del paso 8, que debía dar `true`;
- el paso 9 da `false`, pero **solo porque no existe ningún 7.5 en el mapa**, no por la escala.

El paso 9 era un experimento de **una sola diferencia**: el mismo número escrito `7.50` y `7.5`. Con tus datos
la diferencia es "7.5 contra 39.90", y eso no enseña nada. Con los datos correctos (comprobado):
```
cable = 7.50
containsValue(new BigDecimal("7.50")) → true
containsValue(new BigDecimal("7.5"))  → false   ← mismo valor, otra escala
```

**Qué hacer:** cambia el cable a `"7.50"`, agrega el `containsValue("7.50")` del paso 8 y vuelve a predecir el 9.

---

#### 🟠 IMPORTANTE 2 — P1.3: no hay nada que sobreescribir

Escribiste: "dio **null**… para comparar uso **Comparable**, creo que tenemos que sobreescribir ese método".
- `containsValue` devuelve `boolean`: dio **`false`**, no `null`.
- `BigDecimal` **ya trae** `compareTo`. Lo usaste en `isValidValue` y en la Tarea 2 (paso 4) de este mismo archivo.
  Solo se sobreescribe en **tus** clases (`Product` en la semana 4), no en `BigDecimal`.
- La palabra exacta del hueco "`equals` compara también ___" es **la escala** (cuántos decimales tiene).

Frase completa: *"`containsValue` con `7.5` dio `false` porque `BigDecimal.equals` compara también la escala.
Para comparar dinero sin importar los ceros uso `compareTo`."*

**Profesionalmente:** por esto mismo `containsValue` y `equals` son peligrosos con dinero. Un precio que viene de la
BD como `7.50` y uno que viene de un JSON como `7.5` "no son iguales" para `equals`.

---

#### 🟡 MENOR 3 — Paso 10: `remove` del `Map` devuelve el **valor**

Tu `porque` es correcto. Para que no se repita, conéctalo con lo que ya sabes:

| Método | Devuelve |
|--------|----------|
| `Set.remove(x)` (lección 02) | `boolean`: ¿estaba? |
| `List.set(i, x)` (lección 01) | el valor **anterior** |
| `Map.put(k, v)` / `Map.remove(k)` | el valor **anterior**, o `null` si no había |

En `Map`, "¿se borró?" se lee como "¿devolvió algo distinto de `null`?".

---

#### 🟡 MENOR 4 — Orden de los pasos y ✅ sin comprobar

- En el paso 4 imprimiste `precios` y `size()` **antes** del segundo `put`. Iban **después**, para ver
  `teclado=39.90` y comprobar que `size` sigue en 3 (tu P1.1 lo afirma, pero no lo viste).
- Marcaste ✅ en `teclado=49.90`, pero salió `45.90`. Antes de poner ✅, compara número por número.
- Separadores: pedía `---- T1 · Paso 3 ----`. Tus `T4`, `T5`… se confunden con "Tarea 4".

---

**Para llegar a 10/10:**
- [x] cable = `"7.50"`, paso 8 completo, paso 9 re-predicho
- [x] paso 4: imprimir `precios` y `size()` **después** del segundo `put`
- [ ] P1.3: `false`, "la escala", `compareTo` (sin sobreescribir nada)

---

#### ✅ v2 — 2026-10-15 · **9/10** · ✅ aprobada

**Corregido:**
- ✅ `cable` = `7.50`. Ahora el paso 9 sí es un experimento de una sola diferencia: `7.50` → `true`,
  `7.5` → `false`. Mismo valor, otra escala.
- ✅ Paso 8 completo (`containsValue("7.50")` → `true`).
- ✅ Paso 4: `precios` y `size()` **después** del segundo `put` → `teclado=39.90`, `size` 3. P1.1 comprobada.
- ✅ P1.3 ya no dice "sobreescribir".

**Detalles que quedan (no bloquean):**
- **P1.3:** sigue diciendo "dio **null**". `containsValue` devuelve `boolean`: es **`false`**. Y el método es
  **`compareTo`**. `Comparable` es la interfaz que lo declara (semana 4), no lo que llamas.
- **Comentarios viejos:** al cambiar los datos, actualiza lo que quedó del intento anterior:
  - el primer `espero` del mapa todavía dice `{teclado=49.90, ..., cable=39.90}` ✅, pero sale `45.90` y `7.50`;
  - en el paso 10, tu `real` dice `39.90`, pero ahora sale **`7.50`**.

  Un comentario que no coincide con la salida es peor que no tener comentario: cuando lo repases, te va a enseñar algo falso.

---

### Tarea 2 — Recorrer (fácil-medio · 25 min)

**Objetivo:** elegir la vista correcta (`keySet`, `values`, `entrySet`) para cada necesidad.
**Antes:** relee §5, sobre todo la tabla "¿Cuál vista uso?".
**Archivo:** sigue en `MapBasics.java`, debajo de la Tarea 1.

**Preparación:** crea un mapa **nuevo** `Map<String, BigDecimal> catalogo = new LinkedHashMap<>();` con 5 productos:

| Clave | Valor |
|-------|-------|
| `"notebook"` | `"899.99"` |
| `"mouse"` | `"19.90"` |
| `"webcam"` | `"54.00"` |
| `"pendrive"` | `"8.75"` |
| `"audifonos"` | `"35.00"` |

**Pasos:**
1. Imprime `catalogo.keySet()`. `// espero:` con el orden exacto.
2. **Lista de precios:** recorre con la vista adecuada e imprime una línea por producto, con este formato
   exacto: `notebook: $899.99`. `// espero:` (las 5 líneas).
3. **Valor del inventario:** suma **todos los precios** en un `BigDecimal total` (empieza en `BigDecimal.ZERO`;
   recuerda `total = total.add(...)`). Usa la vista que tiene **solo los valores**. Imprime el total.
   `// espero:` → **calcúlalo a mano antes** (con calculadora, sin ejecutar).
4. **El más caro:** recorre con `entrySet()` y guarda en dos variables el **nombre** y el **precio** del más caro.
   Para comparar precios usa `compareTo` (como en `isValidValue`), nunca `>`. Al final imprime
   `"Más caro: notebook ($899.99)"`. `// espero:`
   > Pista: empieza con `String nombreMasCaro = null;` y `BigDecimal precioMasCaro = BigDecimal.ZERO;`. En cada
   > vuelta: si el precio actual es mayor que `precioMasCaro`, actualiza las dos variables.
5. **Quitar lo barato:** borra los productos que cuestan **menos de 10**. Primero intenta hacerlo **mal**, dentro de
   un `try/catch (ConcurrentModificationException e)`: un for-each sobre `catalogo.keySet()` con
   `catalogo.remove(k)` adentro. `// espero:` ¿explota?
6. Ahora hazlo **bien**, con `removeIf` sobre `catalogo.values()`:
   `catalogo.values().removeIf(precio -> precio.compareTo(new BigDecimal("10")) < 0);`
   (la flecha `->` es una lambda, semana 8; léela como "quita cada `precio` tal que precio < 10").
   Imprime `catalogo`. `// espero:`
   > Curioso pero cierto: quitar un **valor** de la vista `values()` quita la **entrada completa** del mapa. Las
   > vistas no son copias: son ventanas al mapa (§9).

**Preguntas:**
- **P2.1** *"Para el paso 2 usé `___` porque necesitaba ___ y ___. Para el paso 3 usé `___` porque solo
  necesitaba ___."*
- **P2.2** *"En el paso 5, el for-each explotó porque usa un ___ oculto y yo cambié el mapa con ___ en vez de
  con ese ___ (igual que en la lección 01)."*

**Checklist:**
- [ ] total del paso 3 calculado **a mano** antes de ejecutar
- [ ] paso 4 con `compareTo`, sin `>`
- [ ] paso 5 explota (y lo predijiste), paso 6 funciona
- [ ] P2.1 y P2.2


> ## 🔍 REVISIÓN TAREA 2 — 2026-10-15 · **8/10** · ✅ puedes pasar a la Tarea 3
> Historial: **v1 8/10**. Revisado ejecutando `MapBasics`.

**✅ Ya está bien:** elegiste bien la vista en cada paso (`entrySet` para nombre + precio, `values` para
sumar). El **total calculado a mano** (`1017.64`) es exacto. El más caro usa `compareTo`, sin `>`. Predijiste que
el paso 5 explota. P2.1 correcta.

**Sobre el paso 6 y la lambda: tienes razón.** Te pedí usar una sintaxis (`->`) que todavía no habías visto, y
eso rompe la regla de no adivinar. Tu solución está **muy bien**: juntar primero las claves en una `List` y
borrarlas **después** del recorrido es el patrón clásico, y funciona en cualquier versión de Java. Desde aquí, las
tareas solo usan lo que ya está explicado.

---

#### 🟠 IMPORTANTE 1 — El paso 5 ya borró `pendrive` antes de explotar

**Qué pasa:** tu paso 6 imprime el mapa sin `pendrive` y parece que tu solución funcionó. Pero el `pendrive` ya
lo había borrado **el paso 5**. La secuencia real (comprobado):
```
paso 5:  ... vuelta "pendrive" → catalogo.remove("pendrive")   ← SÍ se borró
         siguiente vuelta → el iterator detecta el cambio → ConcurrentModificationException
         catch → "No se puede modificar"
         catalogo ahora: {notebook, mouse, webcam, audifonos}   ← sin pendrive

paso 6:  tu lista aEliminar queda VACÍA → no borra nada → imprime lo mismo
```
La excepción **no deshace** lo que ya pasó. Lo que se ejecutó antes del error queda hecho.

**Profesionalmente:** esto es justo lo que resuelven las **transacciones** de base de datos (`@Transactional`,
semana 19). Sin ellas, un error a mitad de proceso deja los datos "a medias".

**Qué hacer:** para probar de verdad tu paso 6, imprime `catalogo` justo después del `catch` del paso 5, con su
`espero`. Después vuelve a agregar `catalogo.put("pendrive", new BigDecimal("8.75"));` antes del paso 6.

---

#### 🟠 IMPORTANTE 2 — `compareTo(...) == 1`: usa `> 0` y `< 0`

Funciona porque `BigDecimal.compareTo` devuelve exactamente `-1`, `0` o `1`. Pero el contrato general de
`compareTo` **solo promete el signo**. Por ejemplo, `"b".compareTo("z")` devuelve **`-24`** (comprobado), así que
`== -1` daría `false` aunque `"b"` va antes.
```java
if (a.compareTo(b) > 0)   // a es mayor     ← así siempre
if (a.compareTo(b) < 0)   // a es menor
```
Esto importa en la semana 4, cuando escribas `compareTo` para `Product`.

---

#### 🟡 MENOR 3

- **P2.2:** el segundo hueco es "en vez de con ese **iterator**". Es el iterator oculto el que hay que usar para
  borrar (`iterator.remove()`), no `removeIf`. Tu primera parte ("usa un iterador que detecta el cambio") está bien.
- **Formatos:** el paso 2 pedía `notebook: $899.99` y saliste con `notebook= ($899.99)`. El paso 4 pedía
  `Más caro: notebook ($899.99)`. Y en el paso 2 el `espero` va **una vez, con las 5 líneas**, no dentro del `for`.
- Paso 6 sin ✅ (acertaste el resultado, aunque por la razón del IMPORTANTE 1).

---

### Tarea 3 — Acumular: contar y sumar (medio · 35 min)

**Objetivo:** dominar **la receta** de §6 (concepto B de la revisión). Es la base de la Tarea 6.
**Antes:** relee §6 completo y el concepto B de la revisión.
**Archivo:** `src/practical/maps/SalesCounterLab.java`

**Parte A — contar (las tres versiones)**

Datos: los productos vendidos hoy, uno por venta:
```java
List<String> vendidos = List.of("pendrive", "mouse", "pendrive", "webcam", "mouse", "pendrive", "notebook", "mouse");
```
1. **Antes de escribir código:** tabla de vueltas a mano (8 filas), con las columnas `Vuelta | p | ¿Estaba? |
   Qué hace | Mapa después`. Es igual a la de §6.1, pero con estos datos.
2. **Versión 1 (`if/else` + `containsKey`)** en `Map<String, Integer> cuenta1 = new LinkedHashMap<>();`. Imprime.
   `// espero:` (la última fila de tu tabla).
3. **Versión 2 (`getOrDefault`)** en `cuenta2`, también `LinkedHashMap`. Imprime. `// espero:`
4. **Versión 3 (`merge`)** en `cuenta3`, pero esta vez con **`HashMap`**. Imprime. `// espero:` → el orden
   puede cambiar, ¿y los números?
5. Imprime `cuenta1.equals(cuenta2)` y `cuenta1.equals(cuenta3)`. `// espero:` en cada uno.
   > Para pensar: `cuenta3` imprime en otro orden. ¿Para `Map.equals` importa el orden? Piensa qué significa que
   > dos mapas "valgan lo mismo": las mismas claves con los mismos valores.

**Parte B — sumar cantidades (prepara la Tarea 6)**

Ahora los datos son **líneas de pedido** con cantidad, como las de `Order`:
1. Crea 3 productos (`PhysicalProduct` o `Laptop`, con los constructores que ya usaste): `"Pendrive"` id `20`,
   `"Mouse"` id `21`, `"Webcam"` id `22`. Precios y pesos a tu elección.
2. Crea una `List<OrderItem> lineas` con estas 5 líneas, en este orden:
   `Pendrive x2`, `Mouse x1`, `Pendrive x3`, `Webcam x1`, `Mouse x4`.
3. **Unidades por producto** (`Map<String, Integer> unidades`, `LinkedHashMap`): recorre `lineas` y acumula
   `item.getQuantity()` por `item.getProduct().getName()`. Usa **la versión 1 (`if/else`)**: aquí el valor
   inicial ya no es `1`, es **la cantidad** de la primera línea. Imprime. `// espero:`
4. Haz lo mismo con `merge` en un segundo mapa `unidades2`. Imprime. `// espero:` mismo resultado.
5. **Dinero por producto** (`Map<String, BigDecimal> subtotales`): acumula `item.getSubTotalPrice()` con
   `merge(..., BigDecimal::add)`. Imprime. `// espero:` → calcúlalo a mano con tus precios.

**Preguntas:**
- **P3.1** (concepto B) *"La receta de acumular es: si la clave no está, ___; si está, ___. En la Parte A el valor
  inicial es ___ y en la Parte B es ___."*
- **P3.2** *"`cuenta1.equals(cuenta3)` dio ___ aunque imprimen en distinto orden, porque `Map.equals` compara ___."*
- **P3.3** *"En la Parte B, si en `Order` uso la clave `Product` en vez del nombre (`String`), para que dos
  objetos con el mismo id cuenten como el mismo producto, `Product` necesita ___ y ___ (lección 02)."*

**Checklist:**
- [ ] tabla a mano **antes** del código (Parte A)
- [ ] las 3 versiones dan los mismos números
- [ ] Parte B: `if/else` y `merge` dan lo mismo; subtotales calculados a mano
- [ ] P3.1–P3.3

> ## 🔍 REVISIÓN TAREA 3 — 2026-10-18 · **7/10** · 🟠 corrige la Parte B y P3.1, y la cierro
> Historial: **v1 7/10**. Revisado ejecutando `SalesCounterLab` con Java 21.

**✅ Ya está bien:** la **Parte A está completa y correcta**: las tres versiones (`if/else`, `getOrDefault`,
`merge`) dan los mismos números, y escribiste bien la receta en cada una. En el paso 5 fallaste la predicción de
`cuenta1.equals(cuenta3)` y lo **reconociste con un `real:` honesto**. Eso es justo lo que se busca, y P3.2 lo
explica bien. P3.3 correcta (`equals` y `hashCode`). En `unidades2` sacaste el nombre a una variable
(`itemName`): bien.

| Paso | espero | real | ¿ok? |
|------|--------|------|------|
| A2 `cuenta1` | `{pendrive=3, mouse=3, webcam=1, notebook=1}` | igual | ✅ |
| A3 `cuenta2` | igual | igual | ✅ |
| A4 `cuenta3` (HashMap) | mismos números, otro orden | `{mouse=3, webcam=1, pendrive=3, notebook=1}` | ✅ |
| A5 `cuenta1.equals(cuenta2)` | `true` | `true` | ✅ |
| A5 `cuenta1.equals(cuenta3)` | `false` | `true` | ❌ (bien corregido con `real:`) |
| B3 `unidades` | `{pendrive=2,mouse=5,webcam=1}` | igual | ✅ pero con **datos incorrectos** (IMPORTANTE 1) |
| B4 `unidades2` | igual | igual | ✅ (mismos datos incorrectos) |
| B5 `subtotales` | `{pendrive=86.2,mouse=11,webcam=4.2}` | `{pendrive=86.4, mouse=11.0, webcam=4.2}` | ❌ marcado ✅ (IMPORTANTE 2) |

---

#### 🟠 IMPORTANTE 1 — La Parte B usa 4 líneas en vez de 5: falta `Pendrive x3`

**Qué pasa:** el paso B2 pedía `Pendrive x2, Mouse x1, Pendrive x3, Webcam x1, Mouse x4`. Tu lista tiene
`pendrive x2, mouse x1, webcam x1, mouse x4`. Con eso, `pendrive` aparece **una sola vez** y nunca entra en la
rama "ya está" del `if/else`. Tu resultado `pendrive=2` no prueba nada: daría lo mismo con un `put` sin acumular.

**Teoría:** en una prueba, los datos se eligen para que **cada rama** del código se ejecute. `Pendrive x3` estaba
ahí para comprobar que sumas **la cantidad** (`2 + 3`), no `+ 1`. Es el error típico al pasar de contar a sumar.

**Qué hacer:** agrega la línea que falta en su posición (la 3ª). Vuelve a calcular a mano `unidades` y
`subtotales`, y actualiza los tres `espero`. Usa también los nombres con mayúscula (`"Pendrive"`, `"Mouse"`,
`"Webcam"`) como pide el paso B1. El producto `notebook` no se pide en la Parte B: puedes quitarlo.

---

#### 🟠 IMPORTANTE 2 — `subtotales`: esperabas `86.2` y `11`, salió `86.4` y `11.0`, y pusiste ✅

**Qué pasa:** hay dos diferencias, y las dos tienen explicación.
1. **`86.2` vs `86.4`:** `43.2 × 2 = 86.4`. Tu propio comentario de la línea 51 dice `//86.4`. Al copiar el
   `espero` te equivocaste en un dígito y no lo comparaste con la salida.
2. **`11` vs `11.0`:** esto no es un descuido, es **teoría de `BigDecimal`** (tu punto débil de la lección 02).
   `2.2 × 1 = 2.2` y `2.2 × 4 = 8.8` tienen **escala 1** (un decimal). Al sumarlos, el resultado
   también tiene escala 1: `11.0`. `BigDecimal` **no borra los ceros** por su cuenta.
   ```java
   new BigDecimal("11").equals(new BigDecimal("11.0"))       // false: distinta escala
   new BigDecimal("11").compareTo(new BigDecimal("11.0"))    // 0: mismo valor
   ```
   Así que en un test, `assertEquals(new BigDecimal("11"), subtotal)` **fallaría**.

**Profesionalmente:** un `✅` sin comparar letra por letra con la salida real es un test que "pasa" sin probar
nada. Cuando escribas tests con JUnit (semana 7), `assertEquals` compara por ti y no perdona `86.2` vs `86.4`.

**Qué hacer:** con la línea nueva del IMPORTANTE 1, calcula a mano los subtotales **con la escala correcta** y
escribe el `espero` como lo imprimiría Java. Después compara con la salida, carácter por carácter.

---

#### 🟠 IMPORTANTE 3 — P3.1: el valor inicial no es `null`, y el de la Parte B está en el enunciado

**Qué pasa:** escribiste *"en la parte A el valor inicial es de null y en la Parte B... no sé a qué te
refieres"*. Hay una confusión entre **lo que lees** y **lo que guardas**:
- `get(item)` de una clave que no está **devuelve** `null`. Eso es lo que **lees**.
- El **valor inicial** es lo que **guardas** con `put` la primera vez que aparece la clave. Mira tu propio código:
  en la Parte A es `cuenta1.put(item, 1)` → el valor inicial es `1`.

Es la misma confusión de siempre entre **lo que devuelve** un método y **lo que hace**.

**Para la Parte B:** mira la rama "no está" de `unidades` (línea 64, dentro del `if (!unidades.containsKey(...))`). ¿Qué pones con el
`put` la primera vez? El paso B3 lo dice: *"aquí el valor inicial ya no es `1`, es ___ de la primera línea"*.

**Qué hacer:** reescribe P3.1 completa con tus palabras.

---

#### 🟡 MENOR 4

- **Tabla de vueltas:** fila 4 dice `put(pendrive,1)`, debería ser `put(webcam,1)`. Fila 8 dice `¿Estaba? no`,
  pero `mouse` **sí** estaba (por eso haces `2+1`). Fila 3: falta cerrar el paréntesis.
- **Etiquetas:** imprimes `T3`, `t4`, `T5`, `T5`, pero son **pasos** de la Tarea 3, no tareas, y `T5` sale dos
  veces. Usa `A3`, `A4`, `A5`, `B3`… así sabes qué salida es de qué paso.
- En `unidades` (versión `if/else`) llamas `item.getProduct().getName()` **4 veces**. Sácalo a una variable
  como hiciste en `unidades2`.
- Imports sin usar: `Models.Order`.

---

**Para llegar a 10/10:**
- [x] tabla a mano antes del código (Parte A)
- [x] las 3 versiones dan los mismos números
- [ ] Parte B con las **5** líneas exactas (falta `Pendrive x3`) y nombres con mayúscula (IMPORTANTE 1)
- [ ] `espero` de `unidades`, `unidades2` y `subtotales` recalculados, con la escala real de `BigDecimal`, y sin ✅ si no coinciden (IMPORTANTE 2)
- [ ] P3.1 completa: valor inicial Parte A y Parte B (IMPORTANTE 3)
- [x] P3.2, P3.3
- [ ] corregir filas 3, 4 y 8 de la tabla (MENOR 4)

---

### Tarea 4 — Claves objeto: cuánto gastó cada cliente (medio-avanzado · 40 min)

**Objetivo:** ver con tus propios ojos qué pasa cuando una clase **sin** `equals`/`hashCode` es clave de un
`HashMap`, arreglarlo, y ver una **colisión** real (deuda de la lección 02, Tarea 3).
**Antes:** relee §7, §0.2 y §0.3, y el concepto D de la revisión.
**Archivos:** `src/practical/maps/CustomerSpendingLab.java` + `src/Models/Customer.java`

**Contexto:** marketing quiere saber cuánto gastó cada cliente sumando sus pedidos. En un sistema real, cada
pedido trae **su propio objeto** `Customer`, leído de la base de datos: el mismo cliente llega como **objetos
distintos con el mismo id**.

**Ronda 1 — `Customer` tal como está (sin `equals`/`hashCode`)**
1. En `Order`, agrega un getter `getCustomer()` (no existe todavía).
2. En el lab, crea **tres** objetos `Customer`:
   - `ana1`: id `100`, `"Ana"`, `"ana@mail.com"`
   - `ana2`: id `100`, `"Ana"`, `"ana@mail.com"` ← **otro objeto**, misma Ana (como si viniera de otra consulta)
   - `beto`: id `200`, `"Beto"`, `"beto@mail.com"`
3. Crea tres `Order`: `o1` de `ana1`, `o2` de `beto`, `o3` de `ana2`. A cada uno agrégale **un** producto con
   `addItem` (precios simples, para calcular a mano: por ejemplo `o1` = `"100"` x1, `o2` = `"50"` x1,
   `o3` = `"30"` x1).
4. Mete los tres pedidos en una `List<Order> pedidos`.
5. Crea `Map<Customer, BigDecimal> gastado = new HashMap<>();` y acumula con
   `merge(pedido.getCustomer(), pedido.getTotalPrice(), BigDecimal::add)` para cada pedido.
6. Imprime `gastado.size()` y `gastado.get(ana1)`. `// espero:` en cada uno.
   > Antes de predecir, contesta: ¿qué `equals` y qué `hashCode` usa `Customer` ahora? (§0.1, §7.2)

**Ronda 2 — arreglar `Customer`**
7. En `Customer`, haz el campo `id` **`final`** (¿hay algún `setId` que se rompa? ¿por qué no?).
8. Escribe `equals` y `hashCode` comparando **solo el `id`**, con la misma receta que usaste en `Product`
   (lección 02, §1.2): `this == o`, `instanceof`, comparar `id` con `equals` y `hashCode` con `Objects.hash(id)`.
   > No la copies de `Product`: escríbela mirando la receta paso a paso. Es la tercera vez que la escribes;
   > la idea es que te salga sola.
9. Ejecuta de nuevo, **sin cambiar el lab**. Anota `size()` y `get(ana1)` en una tabla:
   ```
   | Línea            | Ronda 1 (sin equals) | Ronda 2 (con equals) |
   |------------------|----------------------|----------------------|
   | gastado.size()   |                      |                      |
   | gastado.get(ana1)|                      |                      |
   ```

**Ronda 3 — la colisión (deuda de la lección 02)**
10. Crea dos productos con ids `"31"` y `"4294967296"` (cualquier nombre y precio).
11. Imprime el `hashCode()` de cada uno y `p31.equals(p429)`. `// espero:` en cada uno.
    (Ya sabes la respuesta del `hashCode`: está en la revisión v3 de la Tarea 3 de la lección 02.)
12. Crea `Map<Product, String> etiquetas = new HashMap<>();`, haz `put(p31, "A")` y `put(p429, "B")`. Imprime
    `size()`, `get(p31)` y `get(p429)`. `// espero:` en cada uno.

**Preguntas:**
- **P4.1** *"En la Ronda 1, `size` dio ___ porque `ana1` y `ana2` tenían ___ distinto (el de `Object`), así que
  fueron a cajones distintos y `equals` ___."*
- **P4.2** *"En la Ronda 2, `merge` encontró la clave de Ana con `ana2` aunque yo la metí con `ana1`, porque
  ___ y ___."*
- **P4.3** (la colisión, P3.4 de la lección 02) *"`p31` y `p429` tienen el mismo `hashCode` (___), o sea, caen en
  el mismo ___. Pero no son la misma clave porque ___ da `false`. Por eso el mapa tiene ___ entradas. Esto se
  llama ___."*
- **P4.4** *"`Customer` tiene `setName` y `setEmail`. No los usé en `equals`/`hashCode` porque, si un campo de
  la clave cambia después del `put`, ___ (§7.4)."*

**Checklist:**
- [ ] `ana2` es un objeto **nuevo** (con `new`), no `ana2 = ana1`
- [ ] tabla Ronda 1 / Ronda 2 llenada con la **salida real**
- [ ] `Customer`: `id` `final`, `equals` + `hashCode` solo con `id`
- [ ] colisión: 2 entradas, cada `get` con su valor
- [ ] P4.1–P4.4

> ## 🔍 REVISIÓN TAREA 4 — 2026-10-19 · **8/10** · 🟠 corrige P4.3, el `porque` del `hashCode` y P4.2, y la cierro
> Historial: **v1 8/10**. Revisado ejecutando `CustomerSpendingLab` con Java 21. La Ronda 1 la comprobé
> compilando una copia de `Customer` **sin** `equals`/`hashCode`: da `3` y `23.2`, como en tu tabla.

**✅ Ya está bien:** el código funciona y **la tabla Ronda 1 / Ronda 2 está llenada con la salida real**
(`3 → 2`, `23.2 → 55.20`). `ana2` es un `new` de verdad. `Customer` tiene `id` `final`, y `equals`/`hashCode`
usan **solo el `id`**. La colisión funciona: 2 entradas, cada `get` con su valor. Y algo que mejoró mucho
respecto a la Tarea 3: cuando fallaste (`get(ana1)` en la Ronda 2, los `hashCode`) **escribiste el `real:`** y
**no pusiste ✅**. Fíjate también en `55.20`: escribiste la escala de `BigDecimal` bien (`23.2 + 32.00` → dos
decimales). Eso era el IMPORTANTE 2 de la Tarea 3: ya lo tienes.

| Paso | espero | real | ¿ok? |
|------|--------|------|------|
| 6 R1 `size()` | `3` | `3` | ✅ |
| 6 R1 `get(ana1)` | `23.2` | `23.2` | ✅ |
| 6 R2 `size()` | `2` | `2` | ✅ |
| 6 R2 `get(ana1)` | `23.2` | `55.20` | ❌ bien corregido con `real:` (olvidaste sumar `o3`) |
| 7 `id` `final` | — | compila | ✅ (respuesta a medias, MENOR 4) |
| 8 `equals`/`hashCode` | — | correctos | ✅ (falta `this == o`, MEJORABLE 3) |
| 11 `prod31.hashCode()` | `31` | `62` | ❌ `porque` vacío (IMPORTANTE 1) |
| 11 `prod429.hashCode()` | `0` | `62` | ❌ `porque` vacío (IMPORTANTE 1) |
| 11 `equals` | `false` | `false` | ✅ |
| 12 `size` / `get` / `get` | `2` / `A` / `B` | igual | ✅ |

---

#### 🟠 IMPORTANTE 1 — `hashCode`: "no sé calcularlo a mano" no es un `porque`, y el `62` ya estaba escrito

**Qué pasa:** predijiste `31` y `0` y salió `62` las dos veces. El paso 11 decía dónde estaba la respuesta: la
revisión v3 de la Tarea 3 de la lección 02 (`docs/lessons/02-equals-hashcode-set.md`, líneas 1328–1329):
```
id 31          → hashCode 62
id 4294967296  → hashCode 62     ← colisión
```
No hace falta que sepas calcular el `hashCode` de un `BigInteger` de memoria. Lo que sí necesitas es entender
**de dónde sale el `62`**, porque es la deuda de la lección 02.

**Teoría:** `Product.hashCode()` devuelve `Objects.hash(id)`. `Objects.hash(x)` **no** devuelve
`x.hashCode()`: hace una cuenta con él.
```
Objects.hash(x)  =  31 * 1 + x.hashCode()
```
- `BigInteger("31").hashCode()` es `31` → `31 + 31 = 62`.
- `BigInteger("4294967296")` es `2³²`. Su `hashCode` también sale `31` (el `BigInteger` guarda el número en
  trozos de 32 bits y "dobla" los trozos en un solo `int`, y en ese doblado `2³²` y `31` dan lo mismo)
  → `31 + 31 = 62`.

Tu `espero: 31` tenía media razón: es el `hashCode` del **id**, no del **producto**. Te faltó el `31 * 1 +` de
`Objects.hash`.

**Ejemplo con otros datos** (no es tu tarea):
```java
Objects.hash(new BigInteger("5"))    // 31 + 5  = 36
new BigInteger("5").hashCode()       // 5
```

**Qué hacer:** en los dos `real: 62`, cambia el `porque` por una frase con tus palabras que diga **qué hace
`Objects.hash`** y por qué los dos ids terminan en el mismo número.

---

#### 🟠 IMPORTANTE 2 — P4.3: "el mismo 62" y "doble verificación" no son los nombres

**Qué pasa:** escribiste *"caen en el mismo **62**… Esto se llama **doble verificación**"*.
- El `62` es el **`hashCode`** (el número). Lo que comparten es el **cajón** (en inglés *bucket*): el lugar
  del `HashMap` donde se guarda la entrada (§0.2).
- Que dos objetos **distintos** tengan el mismo `hashCode` y caigan en el mismo cajón se llama **colisión**
  (§0.3, §7.3). Es la palabra que esta Ronda quería que te quedara.
- "Doble verificación" describe bien **lo que hace el `HashMap`** para resolverla (primero `hashCode` para
  elegir el cajón, después `equals` dentro del cajón), pero no es el nombre del fenómeno.

**Por qué importa:** en una entrevista te van a preguntar *"¿qué es una colisión en un HashMap y cómo se
resuelve?"*. La respuesta es justo tu P4.3 con las dos palabras correctas.

**Qué hacer:** reescribe P4.3 con *cajón* y *colisión*. Si quieres, agrega una segunda frase con tu "doble
verificación": está bien pensada.

---

#### 🟡 IMPORTANTE (pequeño) 3 — P4.2 dice **qué** cambiaste, no **por qué** funciona

**Qué pasa:** *"porque se sobreescribió el equals y hashcode"*. Es cierto, pero la frase pedía dos razones
(`porque ___ y ___`): **qué hace cada uno** con `ana2`.

**Teoría (§7.1):** `merge(ana2, …)` hace dos cosas, en orden:
1. calcula `ana2.hashCode()` → `Objects.hash(100)` → el **mismo número** que `ana1` → va al **mismo cajón**;
2. dentro del cajón compara con `equals` → compara los `id` → `100` y `100` → `true` → **es la misma clave**,
   así que suma en vez de crear una entrada nueva.

**Qué hacer:** completa P4.2 con esas dos ideas, con tus palabras (sin copiar mis números de paso).

---

#### 🟡 MEJORABLE 4 — `equals`: falta `this == o` y el `id` podría ser `null`

```java
public boolean equals(Object cus) {
    if (cus instanceof Customer) { return this.id.equals(((Customer) cus).getId()); }
    return false;
}
```
- **Funciona** (también con `null`: `null instanceof Customer` es `false`). Pero la receta del paso 8 empieza
  con `if (this == o) return true;`: si es **el mismo objeto**, no hace falta comparar nada. Es la receta que
  vas a ver en todo código Java, y la de tu `Product`.
- Si alguien crea `new Customer(null, …)`, `this.id.equals(...)` lanza `NullPointerException`.
  `Objects.equals(this.id, otro.id)` no falla con `null`. (En `hashCode` ya usas `Objects.hash`, que tampoco falla.)

**Qué hacer:** agrega el `this == o` al principio. Lo de `Objects.equals` es opcional.

---

#### ⚪ MENOR 5

- **Paso 7:** la pregunta era *"¿hay algún `setId` que se rompa? ¿por qué no?"*. La respuesta es: **no se rompe
  nada porque `Customer` nunca tuvo `setId`** (lo comprobé en el historial de git). Si lo hubiera tenido, **no
  compilaría**: a un campo `final` no se le puede asignar nada fuera del constructor. Tu frase *"no debería
  hacer ningún setId"* va por ahí, pero no contesta el "por qué no".
- **Separadores:** los `println` no tienen `---- T4 · Paso 6 ----` (regla 2). Con dos `size` y tres `get`
  seguidos, sin separador no se sabe qué salida es de qué paso.
- **Nombres:** el paso 5 pedía `gastado`, usaste `gastando`. En el `for`, la variable se llama `item` pero es un
  **pedido**: `for (Order pedido : pedidos)` se lee solo.
- `mouse` y `webcam` son `new Laptop(...)`. Para el lab no importa, pero el nombre engaña.
- `String args[]` → en Java se escribe `String[] args` (el `[]` va con el tipo).

---

**Para llegar a 10/10:**
- [x] `ana2` es un objeto nuevo
- [x] tabla Ronda 1 / Ronda 2 con la salida real
- [x] `Customer`: `id` `final`, `equals` + `hashCode` solo con `id`
- [x] colisión: 2 entradas, cada `get` con su valor
- [ ] `porque` de los dos `hashCode` = `62`, explicando `Objects.hash` (IMPORTANTE 1)
- [ ] P4.3 con **cajón** y **colisión** (IMPORTANTE 2)
- [ ] P4.2 con las dos razones: mismo `hashCode` → mismo cajón, `equals` por `id` → `true` (IMPORTANTE 3)
- [ ] `this == o` en `Customer.equals` (MEJORABLE 4)
- [x] P4.1, P4.4

---

### Tarea 5 — El orden y los getters (medio · 30 min)

**Objetivo:** elegir la implementación según el orden, descubrir una trampa de `TreeMap` y preparar el getter
de la Tarea 6.
**Antes:** relee §8 y §9.
**Archivo:** `src/practical/maps/MapOrderLab.java`

**Parte A — ventas por mes**
1. Crea **tres** mapas `Map<String, Integer>`: `HashMap`, `LinkedHashMap` y `TreeMap`.
2. Mete en **los tres**, en este orden, las ventas de cada mes (clave `"año-mes"`, valor = cantidad de ventas):
   `"2026-03"` → `40`, `"2025-12"` → `75`, `"2026-10"` → `12`, `"2026-01"` → `33`.
3. Imprime los tres. `// espero:`
   - `HashMap`: "orden impredecible" y qué entradas.
   - `LinkedHashMap` y `TreeMap`: el orden **exacto**.
4. **La trampa:** crea un `TreeMap` nuevo con las **mismas** ventas, pero escribiendo los meses **sin el cero**:
   `"2026-3"`, `"2025-12"`, `"2026-10"`, `"2026-1"`. Imprime. `// espero:` el orden exacto.
   > Pista: `TreeMap<String, ...>` ordena **texto**, letra por letra, como un diccionario. No sabe que son fechas.
   > Compara `"2026-1"` con `"2026-10"` y con `"2026-3"` carácter por carácter.

**Parte B — reinsertar una clave en `LinkedHashMap`**
5. En tu `LinkedHashMap` del paso 2, haz `put("2026-03", 41)` (corregir una venta de marzo). Imprime.
   `// espero:` → ¿marzo **se mueve al final** (como si fuera nuevo) o **se queda en su lugar**?
   > Esto es clave para la Tarea 6: cuando agregues `laptop` por segunda vez, ¿la línea `Laptop` se va al final
   > del pedido o se queda donde estaba?

**Parte C — devolver un mapa sin perder el orden**
6. Crea `Map<String, Integer> soloLectura = Collections.unmodifiableMap(linked);` y
   `Map<String, Integer> copia = Map.copyOf(linked);`.
7. Imprime los dos. `// espero:` → ¿cuál conserva el orden? **Ejecuta 3 veces** y anota lo que sale en cada una.
8. Dentro de un `try/catch (UnsupportedOperationException e)`, intenta `soloLectura.put("2026-11", 5)`.
   `// espero:`
9. Haz `linked.put("2026-11", 5)` (en el **original**) y después imprime `soloLectura`. `// espero:` → ¿la vista
   ve el cambio?

**Preguntas:**
- **P5.1** *"Con los meses sin cero, `TreeMap` los ordenó ___ porque compara ___ y no ___. Para ordenar fechas
  de verdad, la clave debería ser una fecha (semana 6: `java.time`), no un `String`."*
- **P5.2** *"Al hacer `put` sobre una clave que ya existe en un `LinkedHashMap`, la entrada ___ porque el orden
  de inserción es el de la **primera** vez que llegó la ___."*
- **P5.3** *"Para devolver el mapa interno de una clase sin perder el orden y sin que lo modifiquen, uso ___.
  No uso `Map.copyOf` porque ___."*

**Checklist:**
- [ ] paso 4 predicho **antes** de ejecutar, comparando letra por letra
- [ ] paso 5: ¿se mueve o se queda? (lo vas a necesitar)
- [ ] paso 7 ejecutado 3 veces, con las 3 salidas anotadas
- [ ] P5.1–P5.3

> ## 🔍 REVISIÓN TAREA 5 — 2026-10-19 · **6/10** · 🔁 rehaz el paso 4 (la trampa) y corrige P5.1/P5.2
> Historial: **v1 6/10**. Revisado ejecutando `MapOrderLab` con Java 21 (la Parte C, 4 veces). Para comparar,
> ejecuté aparte los datos exactos del enunciado.

**✅ Ya está bien:** las **Partes B y C las entendiste**. En el paso 5 predijiste bien que marzo **se queda en su
lugar**, y además imprimiste lo que **devuelve** `put` (`40`, el valor viejo): muy bien, eso es separar lo que
devuelve de lo que hace. En la Parte C acertaste que `unmodifiableMap` conserva el orden y `copyOf` no, que
`put` sobre la vista lanza la excepción, y que la vista **ve** el `2026-11`. P5.3 elige bien
`unmodifiableMap`. `LinkedHashMap` y `TreeMap` del paso 3 están exactos.

| Paso | espero | real | ¿ok? |
|------|--------|------|------|
| 2 datos | `"2025-12"` → `75` | usaste `"2025-10"` | ❌ (IMPORTANTE 3) |
| 3 `HashMap` | `{2026-03=40,2026-10=75,2025-10=12,2026-01=33}` | `{2026-10=12, 2025-10=75, 2026-03=40, 2026-01=33}` | ❌ marcado ✅ (IMPORTANTE 2) |
| 3 `LinkedHashMap` | orden de inserción | igual | ✅ |
| 3 `TreeMap` | `{2025-10, 2026-01, 2026-03, 2026-10}` | igual | ✅ |
| 4 `TreeMap` sin cero | — (sin `espero`) | 6 entradas mezcladas | ❌ (IMPORTANTE 1) |
| 5 `put("2026-03", 41)` | `40`, se queda en su lugar | igual | ✅ |
| 7 `soloLectura` | en orden | en orden | ✅ |
| 7 `copia` | orden aleatorio | cambia en cada ejecución | ✅ pero sin las 3 salidas anotadas (MEJORABLE 5) |
| 8 `put` en la vista | entra al `catch` | entra, pero no imprime nada | 🟡 (MEJORABLE 5) |
| 9 vista tras `linked.put` | (sin la palabra `espero`) | incluye `2026-11=5` | ✅ |

---

#### 🟠 IMPORTANTE 1 — Paso 4: no creaste un `TreeMap` **nuevo**, y no hay `espero` (era el punto de la Parte A)

**Qué pasa:** el paso 4 pedía *"crea un `TreeMap` **nuevo**"*. Hiciste `put` sobre el `treemap` del paso 2, que ya
tenía `"2026-03"`, `"2026-01"`, etc. Resultado: un mapa con **6 entradas** mezcladas (con cero y sin cero):
```
{2025-10=75, 2026-01=33, 2026-03=40, 2026-1=33, 2026-10=12, 2026-3=40}
```
Con eso no se ve la trampa. Y como no escribiste `espero`, no hubo predicción que comparar. Por eso P5.1 te salió
*"los ordenó de otra forma"*: no viste el orden limpio.

**Teoría:** un `TreeMap<String, …>` compara las claves **carácter por carácter**, de izquierda a derecha, como un
diccionario. En la primera letra distinta decide. Si una palabra se acaba antes (es el comienzo de la otra), va
primero.

**Ejemplo con otros datos** (no es tu tarea), códigos de producto:
```
"A-2", "A-11", "A-1"
"A-1"  vs "A-11" → "A-1" se acaba antes      → "A-1" primero
"A-11" vs "A-2"  → 3er carácter: '1' < '2'   → "A-11" antes que "A-2"
TreeMap → {A-1, A-11, A-2}     ← el 11 antes que el 2: no son números, son texto
```

**Qué hacer:** crea un `TreeMap` **nuevo** (otra variable), mete los 4 meses sin cero
(`"2026-3"`, `"2025-12"`, `"2026-10"`, `"2026-1"`). **Antes de ejecutar**, compáralos de a pares como en el
ejemplo y escribe el `espero` con el orden exacto. Después ejecuta y compara carácter por carácter.

---

#### 🟠 IMPORTANTE 2 — `HashMap`: valores cambiados y ✅ sin comparar (otra vez)

**Qué pasa:** tu `espero` dice `2026-10=75` y `2025-10=12`, pero metiste `2026-10 → 12` y `2025-10 → 75`.
Los **valores** de un `HashMap` nunca se mezclan: lo único impredecible es el **orden**. Además, el paso pedía
escribir *"orden impredecible"* + qué entradas, y escribiste un orden exacto. Y pusiste ✅.

Es el mismo hallazgo que el IMPORTANTE 2 de la Tarea 3 (`86.2` vs `86.4` con ✅). En la Tarea 4 lo hiciste bien.
Aquí volvió.

**Qué hacer:** reescribe el `espero` como pide el paso: *"orden impredecible; entradas: 2026-03=40, …"*, con cada
clave junto a **su** valor. Pon ✅ solo después de comparar con la salida.

---

#### 🟠 IMPORTANTE 3 — Datos: `"2025-10"` en vez de `"2025-12"`

**Qué pasa:** el paso 2 pide `"2025-12"` → `75`. Usaste `"2025-10"` en los tres mapas. Aquí no cambia mucho el
resultado, pero es la **segunda tarea seguida** con datos distintos al enunciado (en la Tarea 3 faltó
`Pendrive x3`). Los datos de cada paso están elegidos para que salga algo concreto, y si los cambias, la tarea
deja de probar lo que tenía que probar.

**Qué hacer:** cámbialo a `"2025-12"` en los tres mapas. Antes de ejecutar, compara tu `put` con el enunciado
línea por línea.

---

#### 🟠 IMPORTANTE 4 — P5.2: la entrada **sí** se modifica. Lo que no cambia es su **lugar**

**Qué pasa:** escribiste *"la entrada **no se modifica**"*. Pero tu propia salida dice `2026-03=41`: el **valor**
cambió de `40` a `41`. Lo que **no** cambió es la **posición** (sigue primera).

Son dos cosas distintas que hace `put` sobre una clave que ya existe:
- el **valor**: se **reemplaza** (§4.1);
- el **orden** en un `LinkedHashMap`: se **queda** donde estaba, porque cuenta la **primera** inserción de la clave.

**Para la Tarea 6:** si agregas `laptop` dos veces, la línea `Laptop` **se queda en su lugar** y se actualiza su
cantidad. No se va al final.

**Qué hacer:** reescribe P5.2 separando las dos ideas: qué pasa con el valor y qué pasa con el lugar.
Y en P5.1, cambia *"de otra forma"* por **el orden exacto** que te salga en el paso 4 rehecho.

---

#### 🟡 MEJORABLE 5 — Parte C: faltan las 3 ejecuciones y el `catch` está vacío

- **Paso 7:** pedía **ejecutar 3 veces y anotar**. Yo lo ejecuté 4 veces y `copia` salió en dos órdenes distintos:
  `{2026-03=41, 2026-01=33, 2025-10=75, 2026-10=12}` y `{2026-10=12, 2025-10=75, 2026-01=33, 2026-03=41}`.
  Hazlo tú y anota las 3 salidas. Así **ves** que "impredecible" significa que cambia de una ejecución a otra.
- **Paso 8:** el `catch` está vacío. Si entra, no lo sabes. Pon un `println` dentro (por ejemplo
  `"no se puede modificar"`) para que la salida lo muestre.
- **Paso 9:** falta la palabra `// espero:` y la respuesta a *"¿la vista ve el cambio?"*. **Sí lo ve**, porque
  `unmodifiableMap` **no copia**, es una **ventana** al mapa original (§9). Agrégalo a P5.3: quien recibe la vista
  **no puede** modificarla, pero **sí ve** los cambios que haga la clase dueña. Eso es justo lo que queremos para
  `Order`.
- **P5.3:** *"copyOf mezcla los datos"* → los datos son los mismos; lo que pierde es el **orden**.

---

#### ⚪ MENOR 6

- **Nombres:** `hasmap`, `linke`. El enunciado usa `linked`: `hashMap`, `linked`, `tree` se leen mejor.
- **Separadores** `---- T5 · Paso N ----` (regla 2): tampoco están en esta tarea. En la Parte A hay 4 mapas
  impresos seguidos y no se sabe cuál es cuál. Es la tercera vez que te lo marco: a partir de la Tarea 6 lo cuento
  como IMPORTANTE.
- El comentario `// cambiar el copyOf..` no se entiende: bórralo o complétalo.
- `String args[]` → `String[] args` (igual que en la Tarea 4).

---

**Para llegar a 10/10:**
- [ ] datos exactos: `"2025-12"` (IMPORTANTE 3)
- [ ] `espero` del `HashMap`: "orden impredecible" + entradas con su valor correcto (IMPORTANTE 2)
- [ ] paso 4 con un `TreeMap` **nuevo**, `espero` escrito **antes** comparando de a pares (IMPORTANTE 1)
- [x] paso 5: se queda en su lugar
- [ ] P5.1 con el orden exacto y P5.2 separando valor / lugar (IMPORTANTE 4)
- [ ] paso 7: 3 ejecuciones anotadas; `println` en el `catch`; `espero` + respuesta del paso 9 (MEJORABLE 5)
- [x] P5.3 (`unmodifiableMap`). Agrega lo de la "ventana"
- [ ] separadores por paso (MENOR 6)

---

### Tarea 6 — Aplicación: `Order` sin líneas repetidas (avanzado · 60 min)

**Objetivo:** el entregable de la semana. `order.addItem(laptop, 3)` + `order.addItem(laptop, 1)` → **una sola
línea**: `Laptop x4`.
**Antes:** haz las Tareas 3, 4 y 5 (esta usa la receta de acumular, `equals`/`hashCode` de `Product` y la
reinserción en `LinkedHashMap`).
**Archivos:** `src/Models/Order.java`, `src/Main.java`

> 🧠 **Esto se llama refactorizar:** cambiar **cómo** está hecho por dentro **sin cambiar lo que hace** por fuera.
> La prueba de que lo hiciste bien: el **total** del pedido **no cambia**. Solo cambia que las líneas repetidas
> se juntan.

**Paso 0 — la foto "antes" (5 min, no te lo saltes)**
1. En `Main`, cambia el precio de `laptop2` de `"32.4"` a **`"1000"`**. Razón: tiene el **mismo id** que `laptop`,
   así que es **el mismo producto** y en la vida real tendría el mismo precio (en la base de datos hay un solo
   precio por id).
2. Al final de `Main`, vuelve a imprimir **todas las líneas** del pedido (copia el for-each del principio) y el total.
3. Ejecuta y **copia la salida completa** en un comentario `// FOTO ANTES:`. Ese es tu punto de comparación.

**Paso 1 — cambiar el campo**
4. Cambia `private final List<OrderItem> orderItems` por `private final Map<Product, OrderItem> orderItems`.
   Elige la implementación pensando: *¿las líneas deben salir en el orden en que el cliente las agregó?* (§8).
5. Ahora **muchos métodos dejan de compilar** (en rojo en IntelliJ). Es normal: es el compilador diciéndote qué
   falta adaptar. Ve **uno por uno** (pasos 6 a 11).

**Paso 2 — adaptar cada método** (uno por vez; compila después de cada uno)
6. **`addItem(Product product, int quantity)`**, el corazón de la tarea. Es la **receta de acumular** (Tarea 3,
   Parte B), con un `OrderItem` como valor:
   - busca con `get` si ya hay una línea para ese producto;
   - si **no hay**: `put` de un `new OrderItem(product, quantity)`;
   - si **hay**: `OrderItem` no tiene `setQuantity` (y está bien así). Crea un `new OrderItem(...)` con la
     **cantidad vieja + la nueva** y haz `put` (reemplaza la línea vieja, como en el concepto A).
   - ⚠️ En el `new OrderItem` del caso "hay", usa **el producto de la línea que ya existía**
     (`existente.getProduct()`), no el que llegó. Son `equals`, pero podrían ser objetos distintos (`laptop2`).
     El pedido se queda con el primero.
   - Cambia el tipo de retorno de `boolean` a **`void`**: con un `Map` la operación siempre funciona (si los datos
     son inválidos, el `OrderItem` lanza una excepción). Un `boolean` que siempre es `true` no informa nada.
7. **`getTotalPrice()`** y **`getTotalUnits()`**: recorre la vista que tiene **solo los valores** (§5).
8. **`getItems()`**: sigue devolviendo `List<OrderItem>`, para que `Main` no cambie. Construye la lista a partir
   de la vista de valores, de forma que **no se pueda modificar** (como hasta ahora, con `List.copyOf`).
   > ¿`List.copyOf` pierde el orden como `Set.copyOf`? Piénsalo: una `List` **siempre** conserva el orden en que
   > recibe los elementos. El que pierde el orden es el `Set`/`Map` inmutable, porque no tiene posiciones.
9. **`getItemCount()`** e **`isEmpty()`**: cambio mínimo.
10. **`removeItem(int index)`**: un `Map` **no tiene posiciones** (§2: no hay `get(0)`). Cámbialo a
    **`removeItem(Product product)`**, que devuelva `true` si había una línea para ese producto y `false` si no.
    Pista: mira qué **devuelve** `remove` (§4.6) y compáralo con `null`.
    En `Main`, cambia `order.removeItem(99)` por `order.removeItem(mouse)` (el mouse no está en el pedido).
11. **`getDistinctProduct()`**: ya **no necesitas el `for`**. Las claves del mapa **son** los productos distintos.
    Devuélvelas sin que se puedan modificar y **conservando el orden** (Tarea 5, P5.3, versión `Set`).
    Debería quedar en **una línea**.

**Paso 3 — comprobar**
12. Ejecuta `Main`. Copia la salida en `// FOTO DESPUÉS:` y compárala **línea por línea** con la foto de antes:
    ```
    | Qué                     | Antes | Después | ¿Debía cambiar? |
    |-------------------------|-------|---------|-----------------|
    | total final             |       |         | NO              |
    | getItemCount()          |       |         | sí, ¿por qué?   |
    | línea de la laptop      |       |         | sí: una sola    |
    | posición de la laptop   |       |         | ?? (Tarea 5)    |
    | getDistinctProduct()    |       |         | NO              |
    ```

**Preguntas:**
- **P6.1** *"Elegí `___Map` para `orderItems` porque ___."*
- **P6.2** *"En `addItem`, para saber si el producto ya estaba usé ___. Funciona con `laptop2` (otro objeto, mismo
  id) porque `Product` tiene ___ y ___ por `id`. Si no los tuviera, el pedido tendría ___ líneas de laptop."*
- **P6.3** *"El total no cambió porque ___. `getItemCount()` sí cambió porque ahora cuenta ___ en vez de ___."*
- **P6.4** *"Cambié `removeItem(int)` por `removeItem(Product)` porque un `Map` no tiene ___. En un backend real
  también es mejor así, porque el cliente dice 'quita **este producto**', no 'quita la **línea 3**'."*
- **P6.5** *"`getDistinctProduct()` ahora es una línea porque las ___ del mapa ya son un `Set` de productos sin
  repetir."*

**Restricciones:**
- `Main` solo cambia en los pasos 1, 2 y 10. Si tienes que cambiar algo más en `Main` para que compile, detente
  y pregúntame: probablemente cambiaste la firma de un método que no debías.
- No agregues `setQuantity` a `OrderItem`.

**Checklist:**
- [ ] FOTO ANTES copiada **antes** de tocar `Order`
- [ ] `addItem` con la receta de acumular, usando el producto de la línea existente
- [ ] `getItems()` sigue devolviendo una `List` que no se puede modificar
- [ ] `removeItem(Product)` + `Main` adaptado
- [ ] `getDistinctProduct()` en una línea, conservando el orden
- [ ] tabla antes/después: **el total no cambia**
- [ ] P6.1–P6.5

> ## 🔍 REVISIÓN TAREA 6 — 2026-10-21 · **4/10** · 🔁 falta el corazón (`addItem`) y las fotos
> Historial: **v1 4/10**. Revisado ejecutando tu `Main` con Java 21. Para tener la FOTO ANTES que falta, ejecuté
> aparte el `Order` del último commit (con `List`) y `laptop2` a `"1000"`.

**✅ Ya está bien:** el **esqueleto del refactor está hecho**. Cambiaste el campo a `Map<Product, OrderItem>`;
`getTotalPrice()` y `getTotalUnits()` recorren `values()` (justo la vista correcta, §5); `getItemCount()` e
`isEmpty()` con cambio mínimo; y en `removeItem` usaste **lo que devuelve `remove` comparado con `null`**, que era
la pista del paso 10. También hiciste el paso 1 (`laptop2` a `"1000"`), y el `toString()` de `Order` y `Customer`
es una buena idea para depurar.

**El problema en una línea:** el total era **6186** y ahora es **2086**. Se perdieron 4000 = `Laptop x4`.
Un refactor que cambia el total no es un refactor: es un bug (lo dice el 🧠 del inicio de la tarea).

| Paso | Pedido | Lo que hiciste | ¿ok? |
|------|--------|----------------|------|
| 0 FOTO ANTES | salida copiada antes de tocar `Order` | no está | ❌ (IMPORTANTE 3) |
| 1 `laptop2` → `"1000"` | `"1000"` | `"1000"` | ✅ |
| 4 implementación | la que conserva el orden de inserción | `HashMap` | ❌ (IMPORTANTE 2) |
| 6 `addItem` | receta de acumular, `void` | solo `put` → reemplaza; sigue `boolean` + `try/catch` | ❌ (CRÍTICO 1) |
| 7 totales con `values()` | sí | sí | ✅ |
| 8 `getItems()` | sigue devolviendo `List` | devuelve `Map` → tuviste que cambiar `Main` | ❌ (IMPORTANTE 4) |
| 9 `getItemCount` / `isEmpty` | cambio mínimo | igual | ✅ |
| 10 `removeItem(Product)` | `remove` + `null`; en `Main` con `mouse` | idea ✅; en `Main` usaste `coursePython` | 🟡 (IMPORTANTE 4) |
| 11 `getDistinctProduct()` | una línea, sin `for` | sigue el `for` | ❌ (MEJORABLE 5) |
| 12 FOTO DESPUÉS + tabla | sí | no está | ❌ (IMPORTANTE 3) |
| P6.1–P6.5 | respondidas | no están (los comentarios del final son de la L02) | ❌ |

---

#### ❌ CRÍTICO 1 — `addItem` no acumula: **reemplaza**

**Qué pasa:** tu `addItem` hace siempre `put(product, new OrderItem(product, quantity))`. Sigue lo que ocurre
con la laptop:

| Llamada | Lo que hay en el mapa para id=1 |
|---------|--------------------------------|
| `addItem(laptop, 3)` | `Laptop x3` |
| `addItem(laptop, 1)` | `Laptop x1` ← el `x3` desapareció |
| `addItem(laptop2, 1)` | `laptop2 x1` ← el `x1` desapareció |

Por eso tu salida muestra `[Product{id=1, name='laptop2' ...`: la línea quedó con **el producto que llegó al
final** (justo lo que el ⚠️ del paso 6 pedía evitar).

**Teoría:** es el **concepto A** de la revisión de la teoría: `put` sobre una clave que ya existe **reemplaza el
valor**, no suma. Para sumar necesitas la **receta de acumular** (§6.1, la misma de la Tarea 3 Parte B):
*leer lo viejo → calcular lo nuevo → guardar*.

**Ejemplo (otros datos, un carrito de frutas con `Map<String, Integer>`):**
```
carrito.put("manzana", 2);
carrito.put("manzana", 5);          // ahora vale 5, NO 7  ← esto hace tu addItem

Integer vieja = carrito.get("pera");   // null → no había
// si es null: put("pera", 4)
// si no es null: put("pera", vieja + 4)
```
En tu caso el "valor" no es un `Integer` sino un `OrderItem`, así que el `vieja + 4` es: **un `OrderItem` nuevo**
con `existente.getProduct()` y `existente.getQuantity() + quantity`.

**Qué hacer:** reescribe `addItem` siguiendo los 4 guiones del paso 6, en este orden: `get` → `if (... == null)`
→ `put` nuevo / `put` con la suma. Cambia el retorno a `void` y **quita el `try/catch`** (ver abajo).

> **Sobre los `try/catch` de `addItem` y `removeItem`:** `put` y `remove` de un `HashMap` no lanzan excepciones
> con estos datos. Además, en `addItem` el `new OrderItem(...)` (lo único que **sí** puede lanzar) está **fuera**
> del `try`, así que ese `catch` nunca atrapa nada. Un `try/catch` que no puede ocurrir solo confunde al que lee.
> Y `removed != null ? true : false` es lo mismo que `removed != null` (ya es un `boolean`).

---

#### 🟠 IMPORTANTE 2 — `HashMap`: las líneas salen en otro orden

**Qué pasa:** agregaste `Laptop, Java Course, netflix, Consultoria, Keyboard, Python`, y tu `Main` imprime
`Laptop, Keyboard, Java Course, Python Course, netflix, Consultoria`. El cliente ve su pedido desordenado.

**Teoría:** §8. Es la pregunta del paso 4: *¿las líneas deben salir en el orden en que el cliente las agregó?* →
sí → la implementación que **recuerda el orden de inserción**. Es la misma que ya elegiste en la Tarea 5 para
`linked`.

**Qué hacer:** cambia la implementación en el constructor (solo el `new ...<>()`; el tipo del campo sigue siendo
`Map`). Después contesta P6.1.

---

#### 🟠 IMPORTANTE 3 — Sin FOTO ANTES, FOTO DESPUÉS ni tabla: no tenías cómo ver el bug

**Qué pasa:** el paso 0 existe justo para esto. Si hubieras tenido la foto, al ver `2086` contra `6186` habrías
encontrado el CRÍTICO 1 tú solo. Te dejo la foto antes (ejecutada con el `Order` del último commit):

```
// FOTO ANTES (Order con List, laptop2 a "1000"):
// 4186
// Laptop x3 1000 3000
// Java Course x4 100 400
// netflixSubscription x2 100 200
// Consultoria de casas x2 123 246
// Keyboard x3 80 240
// Python Course x1 100 100
// Total: 4186
// UnsupportedOperationException
// ---------------Tarea 6
// 8
// 6
// [Product{id=1, name='Laptop' ...}, ... , Product{id=5, name='Python Course' ...}]
// [ ...lo mismo... ]
// java.lang.UnsupportedOperationException
// 6186
```

**Qué hacer:** pega esa foto en `Main`. Cuando arregles todo, copia tu salida como `// FOTO DESPUÉS:` y llena la
tabla del paso 12. El **6186** final tiene que coincidir.

---

#### 🟠 IMPORTANTE 4 — Cambiaste `Main` más de lo permitido

**Qué pasa:** la restricción dice que `Main` solo cambia en los pasos 1, 2 y 10. Cambiaste además:
- `order.getItems().values()` → porque `getItems()` ahora devuelve `Map`. El paso 8 pide que **siga devolviendo
  `List<OrderItem>`**: por fuera nadie tiene que enterarse de que por dentro hay un `Map` (eso es encapsular).
- Comentaste `order.getItems().add(ordI)`: con la `List` inmodificable vuelve a compilar y vuelve a imprimir
  `UnsupportedOperationException`, como en la foto antes.
- `removeItem(coursePython)` en vez de `removeItem(mouse)`: borrar el curso **cambia el total** (−100) y las líneas,
  así que ya no puedes comparar con la foto. El `mouse` no está en el pedido → debe devolver `false` sin tocar nada.

**Qué hacer:** `getItems()` → construye la `List` a partir de `orderItems.values()` con `List.copyOf` (paso 8) y
deshaz esos tres cambios de `Main` (deja `removeItem(mouse)`; el `println` de lo que devuelve puedes dejarlo,
está bien para ver el `false`).

---

#### 🟡 MEJORABLE 5 — `getDistinctProduct()` todavía tiene el `for`

**Qué pasa:** funciona, pero recorre los valores para sacar productos que el mapa **ya tiene** guardados como
claves. Además, como sacas `item.getProduct()`, hoy devuelve `laptop2` (consecuencia del CRÍTICO 1).

**Pista:** ¿qué vista de §5 te da **solo las claves**? ¿Qué tipo devuelve? Envuélvela con lo mismo que ya usas en
el `return` actual. Una línea. Conserva el orden **si** arreglaste el IMPORTANTE 2.

---

#### ⚪ MENOR 6

- **Separadores** `---- T6 · Paso N ----`: no están (te avisé en la T5 que desde aquí contaba como IMPORTANTE;
  lo dejo en MENOR porque hay cosas más urgentes arriba).
- Los `espero` de la sección "Tarea 6" de `Main` son de la L02 (8 / 7): actualízalos a lo que esperas **ahora**.
  Ojo: con el `Map`, `getItemCount()` ya no debería dar 8.
- `import java.util.*` ok; `BigInteger` en `Order` sí se usa (el `id`).

---

**Para llegar a 10/10:**
- [ ] FOTO ANTES pegada en `Main` (IMPORTANTE 3)
- [ ] implementación que conserva el orden + P6.1 (IMPORTANTE 2)
- [ ] `addItem` con la receta de acumular, `void`, sin `try/catch`, usando `existente.getProduct()` (CRÍTICO 1)
- [ ] `getItems()` devuelve `List` inmodificable; `Main` con `getItems()` sin `.values()`, `add(ordI)` y `removeItem(mouse)` (IMPORTANTE 4)
- [ ] `removeItem`: sin `try/catch` ni `? true : false`
- [ ] `getDistinctProduct()` en una línea (MEJORABLE 5)
- [ ] FOTO DESPUÉS + tabla del paso 12 → **total 6186**
- [ ] P6.1–P6.5

---

## 14. Qué entregar

| Tarea | Archivos |
|-------|----------|
| 0 | `MapPlayground.java` |
| 1 y 2 | `src/practical/maps/MapBasics.java` |
| 3 | `src/practical/maps/SalesCounterLab.java` |
| 4 | `src/practical/maps/CustomerSpendingLab.java` + `Customer.java` (+ `getCustomer()` en `Order`) |
| 5 | `src/practical/maps/MapOrderLab.java` |
| 6 | `Order.java` + `Main.java` |

## 15. Criterios de salida (responder sin mirar, al final de la semana)

- ¿Cuándo `List`, cuándo `Set` y cuándo `Map`? Da un ejemplo del e-commerce para cada uno.
- Explica la receta de acumular y escríbela en las versiones `if/else` y `merge`.
- ¿Por qué `Customer` necesita `equals`/`hashCode` para ser clave de un `HashMap`? ¿Qué pasaba sin ellos?
- ¿Qué devuelve `put`? ¿Qué pasa con la posición de una clave que se reinserta en un `LinkedHashMap`?
- ¿Cómo devuelves el `Map` interno de una clase sin romper la encapsulación ni perder el orden?

**Siguiente lección:** ordenar (`Comparable`, `Comparator`). Por fin vas a poder meter un `Product` en un `TreeSet`
o como clave de un `TreeMap`, sin `ClassCastException`.
