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

## 13. Tareas

> Se publican cuando termines la teoría y la autoevaluación. Formato: **mini-tareas de unos 10 minutos**, una
> por vez, con `espero` en cada línea y una frase para completar en cada pregunta. La aplicación final: `Order`
> con un `Map<Product, OrderItem>`, para que `laptop x3` + `laptop x1` den **una sola línea**, `Laptop x4`.
