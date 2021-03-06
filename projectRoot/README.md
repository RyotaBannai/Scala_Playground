### Tricks

- scalac で打ったコマンドを全て表示: `def history = scala.io.Source.fromFile(System.getProperty("user.home") + "/.scala_history").foreach(print); history`
  - [Ref](https://stackoverflow.com/questions/26946059/grab-scala-repl-history-from-sbt-console/27362850)

### scala の型

- `var と val`: 型宣言が必要ない(型推論). 基本的には val を使用
  - var は変更可(reuseable), variable(変数)
  - val は変更不可(final), value(値)
- `var` に初めに数値を入れると、後から文字列を代入できない（これは typescript と同じ様な型制限）
- `val x: Int = 3 * 3` 型宣言の場合。

### sbt を使う

- sbt で main がある場所で`sbt`と打つ
- `run`コマンドを打つ。main が複数ある場合は選択するプロントが表示される。
- `sbt console`でインタラクティブモードに入る. ここで作ったクラスとかファイルを全て読み込めるため、クラスの動作確認ができる.
- src/User.scala 　に記述した物を実行

```scala
val u = new User("dwango", 13)
User.printUser(u)
```

### 制御構文

- **Unit 型**は Java では`void`に相当するもので、返すべき値がない時に使われ、唯一の値()を持つ.
- else が省略可能で、その場合は、**Unit 型**の値 () が補われたのと同じ値が返る.
- break や continue などの言語機能はない.
- return 式はメソッドから、途中で脱出してメソッドの呼び出し元に返り値を返すための制御構文である.
- `1 to 10 `は 1 から 10 まで（10 を含む）の範囲で、 `1 until 10` は 1 から 10 まで（10 を含まない）の範囲
- for + yield で `for-comprehension`
- Scala のパターンマッチがいわゆる`フォールスルー（fall through）`の動作をしない
- パターンマッチの後にガード式（Boolean 型でないといけない）が使える
  - 以下のように`シーケンスパターン`を使って List, Array の要素にアクセスすることができる.

```scala
mylist match{
  case List("A", b, c) if b != "B" =>
    println("Hit!")
  case _ =>
    println("Not hit!")
    }
```

- `as パターン`: `@`の後に続くパターンにマッチする式を @ の前の変数に束縛する.
  - パターンが複雑なときにパターンの一部だけを切り取りたい時に便利.
- ただし `|` を使ったパターンマッチの場合は値を取り出すことができない. ワイルドカード`_`を使う.
- `中置パターン`: `"A" :: b :: c :: _` のように、リストの要素の間にパターン名（::）が現れるようなもの.
- `AnyRef`型は、Java の`Object`型に相当する型で、`あらゆる参照型の値`を AnyRef 型の変数に格納することができる.
- 型でマッチした値は、その型にキャストしたのと同じように扱うことができる. `しばしば Scala ではキャストの代わりにパターンマッチが用いられる`.
- `JVM の制約による型のパターンマッチの落とし穴`: Scala を実行する JVM の制約により、型変数を使った場合、正しくパターンマッチが行われない

```scala
val obj: Any = List("a")
obj match {
  case v: List[Int]    => println("List[Int]")
  case v: List[String] => println("List[String]")
}
```

- 型としては`List[Int]`と`List[String]`は違う型なのですが、パターンマッチではこれを区別できない。最初の 2 つの警告の意味は Scala コンパイラの`型消去`という動作により`List[Int]`の Int の部分が消されてしまうのでチェックされないということ。結果的に 2 つのパターンは区別できないものになり、パターンマッチは上から順番に実行されていくので、2 番目のパターンは到達しないコードになる。3 番目の警告はこれを意味している。型変数を含む型のパターンマッチは、以下のようにワイルドカードパターンを使うと良い。

```scala
obj match {
  case v: List[_] => println("List[_]")
}
```

#### 複数の引数リストを持つメソッド

- 複数の引数リストを持つメソッドには、Scala の糖衣構文と組み合わせて流暢な API を作ったり、後述する implicit parameter のために必要になったり、型推論を補助するために使われたりといった用途がある
- 継承には 2 つの目的がある:

1.  継承によりスーパークラスの実装をサブクラスでも使うことで実装を再利用すること
2.  複数のサブクラスが共通のスーパークラスのインタフェースを継承することで処理を共通化すること

- 実装の継承には複数の継承によりメソッドやフィールドの名前が衝突する場合の振舞いなどに問題があることが知られており、Java では実装継承が 1 つだけに限定されている。Java 8 ではインタフェースにデフォルトの実装を持たせられるようになったが、変数は持たせられないという制約がある。Scala では`トレイト`という仕組みで複数の実装の継承を実現している。

### オブジェクト

- Scala では、**全ての値がオブジェクト**. また、**全てのメソッドは何らかのオブジェクトに所属している**
  - そのため、Java のようにクラスに属する static フィールドや static メソッドといったものを作成することができない. その代わりに、object キーワードによって、同じ名前の`シングルトンオブジェクト`を現在の名前空間の下に 1 つ定義することができ、object キーワードによって定義したシングルトンオブジェクトには、そのオブジェクト固有の(static)メソッドやフィールドを定義することができる
- object 構文の主な用途:
  1. ユーティリティメソッドやグローバルな状態の置き場所（Java で言う static メソッドやフィールド）
  2. 同名クラスのオブジェクトのファクトリメソッド
- extends でクラスを継承、 with でトレイトを mix-in 可能になっているのは、`オブジェクト名を既存のクラスのサブクラス等として振る舞わせたい場合があるため`. Scala の標準ライブラリでは、`Nil` という object があるが、これは`List の一種として振る舞わせたいため、List を継承している`. 一方、object がトレイトを mix-in する事はあまり多くないが、クラスやトレイトとの構文の互換性のためにそうなっていると思われる。
- `ケースクラス`: それをつけたクラスのプライマリコンストラクタ全てのフィールドを公開し、`equals()`, `hashCode()`, `toString()`などのオブジェクトの基本的なメソッドをオーバーライドしたクラスを生成し、また、そのクラスのインスタンスを生成するための`ファクトリメソッド`を生成するもの

```scala
Point(1, 2).equals(Point(1, 2))
```

- `参照透過性(referentially transparent)`:
  - In に与えた引数の値が変更されないず、別の値として Out を取れることを保証すること.
  - `副作用(side effect)`: がないと呼び、これを実現するにはイミュータブルなデータと副作用を伴わないコードを記述することである.
  - e.g. String クラスの replace メソッド(SP39)

### トレイト

- プログラムはしばしば数万行、多くなると数十万行やそれ以上に及ぶことがある. その全てを一度に把握することは難しいため、プログラムを意味のあるわかりやすい単位で分割しなければいけない. さらに、その分割された部品はなるべく柔軟に組み立てられ、大きなプログラムを作れると良い.
- `ミックスイン`: 一つ以上のトレイトを 1 つのクラスやトレイトに追加すること
- トレイトは直接インスタンス化できない:
  - これは`トレイトが単体で使われることをそもそも想定していない`ための制限
  - トレイトを使うときは、それを継承したクラスを作成する

```scala
trait TraitA
object ObjectA {
  val a = new TraitA // Error: trait is abstract, can't be instanced.
}
```

- `new Trait{}` は`Trait を継承した無名のクラス`を作って、そのインスタンスを生成する構文でトレイトそのものをインスタンス化できているわけではない。
- トレイトはクラスと違ってコンストラクタの引数を取ることができない

```scala
trait TraitA(name: String) // Error: can't take args
// sub typing （構造的サブタイピング（≈ダックタイピング））
// nominal typingでは同じクラスかサブクラスしか型宣 言できない https://medium.com/@thejameskyle/type-systems-structural-vs-nominal-typing-explained-56511dd969f4
trait TraitB{
  val name: String
  def printName(): Unit = println(name)
}
class ClassB (val name: String) extends TraitB
object ClassB {
  val a = new ClassB("dnn")
  val a2 = TraitB{ val name = "dmo"} // name を上書きする様な実装を与えてることもできる
}
```

- トレイとの大きな用途の一つ: `クラスがすでに持っているメソッドを使って自動的にメソッドを増やす`
  - つまり、トレイトは`シン(thin) インタフェース`を`リッチインタフェース`に変えることができる(SP221)
  - シンインタフェース: メソッドが少なく実装者にとっては楽
  - リッチインタフェース: 利用者は自分のニーズにあうメソッドを選択できる
  - トレイトによってリッチインタフェースを実装するのは１度だけで良く、実装とニーズのトレードオフが解消される(SP222)
- もう一つの大きな用途: クラスへの`積み重ね可能な変更(stackable modifications)`
- trait から呼ばれる super は`線形化(linearization)`した順序によって決まる. 一番右側の trait の super はその左側の trait を指す(SP230)

#### 菱形(ダイアモンド)継承問題はトレイトにはない(SP38)

- override 指定なしの場合、メソッド定義の衝突はエラーとなる.

```scala
class ClassB extends TraitB with TraitC{
  override def greet(): Unit = println("Hello")
  }
```

- 継承したトレイトのメソッドを指定することもできる

```scala
class ClassB extends TraitB with TraitC{
  override def greet(): Unit = super[TraitB].greet()
  }
```

- どっちも呼びたい場合

```scala
class ClassB extends TraitB with TraitC{
  override def greet(): Unit = {
    super[TraitB].greet()
    super[TraitC].greet()
    }
  }
```

### 線形化（linearization）

- `トレイトの線形化`: トレイトがミックスインされた順番をトレイトの継承順番と見做すこと
- `線形化機能`を使う: ミックスインする全てのトレイトのメソッドを`override`をして継承させる
  - Mixin: extends/ with のことで、 最終派生クラスで extends, with どちらも使用している場合は、with が最後の Mixin となり、これで override される
- `最後に読み込まれたメソッド`が使用される

```scala
trait P{
  def hi(): Unit
}
trait C1 extends P{
  override def hi(): Unit = { println("C1-> hi") }
}
trait C2 extends P{
  override def hi(): Unit = { println("C2-> hi") }
}
class Base extends C1 with C2
(new ClassA).hi() // "C2-> hi"

```

- `super`で親クラスを呼ぶことで全ての override したメソッドを呼び出すことができる-> 線形化によるトレイトの積み重ねの処理を Scala の用語では積み重ね可能なトレイト（Stackable Trait）と呼ぶことがある。

### 落とし穴：トレイトの初期化順序とトレイトの val の初期化順序の回避方法

```scala
trait A {
  val foo: String
}

trait B extends A {
  val bar = foo + "World"
}

class C extends B {
  val foo = "Hello"

  def printBar(): Unit = println(bar)
}

(new C).printBar()
// 初期化はトレイトAが一番先におこなわれ、変数fooが宣言され、中身は何も代入されていないので、nullになる。
// このnullをtraitBは使うため、classC ではnullWorldと表示される
```

    1. 処理を遅延させる`lazy`または`def`を使う

- lazy は _bar の初期化が実際に使われるまで遅延される_

```scala
trait B extends A {
  lazy val bar = foo + "World"
  // def var
}
```

- lazy val は val に比べて若干処理が重く、複雑な呼び出しでデッドロックが発生する場合がある。 val のかわりに def を使うと毎回値を計算してしまうという問題がある。 2. 事前定義（Early Definitions）: フィールドの初期化をスーパークラスより先におこなう方法

```scala
trait A {
  val foo: String
}

trait B extends A {
  val bar = foo + "World" // valのままでよい
}

class C extends {
  val foo = "Hello" // スーパークラスの初期化の前に呼び出される
} with B {
  def printBar(): Unit = println(bar)
}
```

- この事前定義は利用側からの回避方法は、この例の場合はトレイト B のほうに問題がある（普通に使うと初期化の問題が発生してしまう）ので、トレイト B のほうを修正したほうがいい。
- 時世代 Scala コンパイラである Dotty ではトレイトがパラメータを取ることができる.

### 型パラメータ（type parameter）

```scala
class TypeParam[A](var value:A) {
  def put(newValue:A): Unit = {
    value = newValue
  }
  def get(): A = value
}
```

#### 変位指定（variance）

- 型パラメータの横に書ける +, - の記号を`変位指定アノテーション(variance annotations)`という(SP369)

##### 共変（covariant）と非変（invariant）

- Scala では、何も指定しなかった型パラメータは通常は`非変（non-variant)`
- `非変`：型パラメータを持ったクラス G、型パラメータ A と B があったとき、A = B のときにのみに代入が許される

```scala
val g: G[A] = G[B]
```

つまり A=B である場合に限りコンパイルが通る。

```scala
val g: Array[Any] = new Array[Int](1) // error
val g: Array[Any] = new Array[Any](1) // ok
```

これを`非変`という.

- `共変`: 型パラメータを持ったクラス G、型パラメータ A と B があったとき、B が A を継承しているときにのみというような代入が許される性質

```scala
val g: G[A] = G[B] // B が A を継承
```

```scala
class G[+A]
```

のように型パラメータの前に`+`を付けるとその型パラメータは（あるいはそのクラスは）`共変`になる

```java
Object[] objects = new String[1];
objects[0] = 100;
```

- java は共変であり、A=Object, B=String, G=Array と考えると、これでコンパイルが通る（`val a: Array[Object] = Array[String]`）. しかし, このコードを実行すると例外 `java.lang.ArrayStoreException` が発生する. これは、objects に入っているのが実際には String の配列（String のみを要素として持つ）なのに、2 行目で int 型（ボクシング変換されて Integer 型）の値である 100 を渡そうとしていることによる(SP370)

- 一方で, Scala では`非変`なのでコンパイルの時点でエラーになる.

- `静的型付き言語の型安全性`とは、コンパイル時により多くのプログラミングエラーを捕捉するものであるとするなら、配列の設計は Scala の方が Java より型安全であると言える.
- Scala で`共変`にした場合, 変数を immutable にする様に設計する（多くの場合問題があればコンパイル時にでエラーを拾ってくれる）.

```scala
class Pair1[+A, +B](val a:A, val b:B){
   override def toString(): String = "(" + a + "," + b + ")"
}
var pair: Pair[AnyRef, AnyRef] = new Pair[String, String]("foo","bar")
```

- `A lower type bound`: `[B >: A]`. It means that B is constrained to be `a supertype of A`.
- `An upper type bound`: `[B <: A]`. It means that B is constrained to be `a subtype of A`.
- `Nothing`は全ての型のサブクラスであるような型を表現する。`Stack[+A]`で共変だとすると、`Stack[Nothing]`型の場合はどんな型の`Stack変数`にでも格納することができる:
  - 例えば`Stack[Nothing]`型である EmptyStack インスタンスがあれば、それは、`Stack[Int]`型の変数と`Stack[String]`型の変数の両方に代入することができる. これは`Nothing`は Int や String のサブクラスであり、共変の条件を満たすため.
  - `[B >: A]` B=Int, A=Nothing.
- https://stackoverflow.com/questions/7759361/what-does-b-a-do-in-scala
- https://gist.github.com/RyotaBannai/2968fa3360d197c81dff4b4174facc38
- コンパイラは、Stack には A の任意のスーパータイプの値が入れられる可能性があることがわかるようになる。そして、型パラメータ E は共変ではないため、どこに出現しても構わない。このようにして、下限境界を利用して、型安全な Stack と共変性を両立することができる。（refer to `line 211`）

#### Contravariant 反変：共変の反対の概念

- `val g: G[A] = G[B]`とした時に A が B を継承している時に代入が可能

  - `class G[-A]`と表す
  - `T 型`が`S 型`のサブ型だとすると、`List[S]`は`List[T]` のサブ型になる!(SP369)

- `covariant type T occurs in contravariant position in type T of value x`:
  - [Ref](https://dtuttleo.github.io/scala/2016/09/25/covariant-type-error.html)

### Functions

- Scala の関数は単に Function0 〜 Function22 までのトレイトの無名サブクラスのインスタンス

```scala
val add = new Function2[Int, Int, Int]{
  def apply(x:Int, y:Int): Int = x + y
}
// 無名関数：こんな感じシンプルに書く
val add2 = (x: Int, y:Int) => x+y
```

- `apply`メソッドは Scala コンパイラから特別扱いされ、x.apply(y)は常に x(y)のように書くことができる
- **関数を自由に変数や引数に代入したり返り値として返すことができる性質**を指して、Scala では**関数が第一級の値（First Class Object）** であるという。
- 引数の最大個数は 22 個

### メソッドと関数の違い

- 本来は def で始まる構文で定義されたもの**だけ**がメソッドなのですが、説明の便宜上、所属するオブジェクトの無いメソッド（今回は説明していません）や REPL で定義したメソッドを関数と呼んだりすることがある。
- メソッドは第一級の値ではないのに対して**関数は第一級の値**であるという大きな違いがあるため。メソッドを取る引数やメソッドを返す関数、メソッドが入った変数といったものは Scala には存在しない。

### 高級関数

- 関数を引数に取ったり関数を返すメソッドや関数のことを**高階関数**と呼ぶ。メソッドのことも関数というのはいささか奇妙ですが、慣習的にそう呼ぶものだと思ってください。
- 処理を値として部品化することは高階関数を定義する大きなメリットの 1 つ
- 高階関数を利用してリソースの後始末を行うパターンは**ローンパターン**と呼ばれている.

```scala
import scala.io.Source
def withFile[A](filename: String)(f: Source => A): A = {
  val s = Source.fromFile(filename)
  try {
    f(s)
  } finally {
    s.close()
  }
}
```

### Tips

- [CanBuildFrom にお別れを](https://blog.tiqwab.com/2018/12/28/rip-can-build-from.html)
- [`Method Invocation`](https://docs.scala-lang.org/style/method-invocation.html) when `0-, 1- Arity and Higher Order function`
- [SCALA 2.13 COLLECTIONS REWORK](https://www.scala-lang.org/blog/2017/02/28/collections-rework.html#canbuildfrom)
- [THE ARCHITECTURE OF SCALA 2.13’S COLLECTIONS](https://docs.scala-lang.org/overviews/core/architecture-of-scala-213-collections.html)
- [Scala 2.8 breakOut](https://stackoverflow.com/questions/1715681/scala-2-8-breakout)
- [SCALA 2.13 COLLECTIONS REWORK](https://www.scala-lang.org/blog/2017/02/28/collections-rework.html#views)
- [Some classes have been removed, made private or have no equivalent in the new design:](https://docs.scala-lang.org/overviews/core/collections-migration-213.html)

# Scalable Programming

- Int はクラスの`単純名`で、`scala.Int` は`完全名`である. (java.lang.String は完全名で、String は単純名)(SP49)
- Int は java の int に対応している. もっと正確に言えば、java の全てのプリミティブ型に対応している
- `list.foreach(item => println(item))`: foreach に渡されるのは`関数リテラル`と呼ばれ、その`本体`は println(item) である
- 関数リテラルが一個の引数をとる一文から構成される場合は、引数を明示的に指定しなくても済む: この省略記法は`部分的に適用された関数(partially applied function)`と呼ばれる(SP57)
- `for(item <- list) ... `: `<-` は in と読めば、`要素 ∈ 集合` の関係を表す数学記号に対応する(SP57)
  - item は val
- `val stringList: Array[String] = new Array[String](3);`
  - 上記の場合、左側の `Array[String]` が型であって、`new Array[String](3)` は型の一部にはならない.
  - val とした時に、stringList 自体は常に同じ Array を指すことになるが、Array の Item がさす値自体は変わる可能性がある(immutable)ため、`stringList(0) = "hello";` のように変更することができる(SP61)
- メソッドのパラメータが１つだけなら、ドットや括弧を使わずに呼び出せる.
  - 例えば `0 to 2` は 0 という Int オブジェクトの to メソッドを 2 を引数として呼び出していること(`0.to(2)`)を表す(SP61)
    - この to は 0,1,2 の値を格納したシーケンスの一種を返す.
    - `scala.collection.immutable.Range.Inclusive = Range 0 to 2`
  - この時、メソッドを呼び出すオブジェクトは`レシーバ`と呼ばれる.
  - パラメータが一つでも `println 10` これはレシーバがないので動作しないが、`Console println 10` とすると動作する
  - `(1).+(2)`とすることもできる: このことからもわかるように、scala では全ての`演算`が`メソッド呼び出し`である.
- 変数に application 演算子をつけるとそれは、その変数に対して apply メソッド(`ファクトリメソッド`という)を実行したことになる
  - `stringList(0) === stringList.apply(0)`
  - `(stringList(0) = 1) === stringList.update(0, 1)`
  - これらのファクトリメソッドは、`Arrayコンパニオンオブジェクト(companion object)`で定義されている
  - `可変個引数(variadic parameters)`は`連続パラメータ(repeated parameters)`とも呼ばれる(SP63)
- scala は配列から式に至るまであらゆるものがメソッドを持つオブジェクトだとすることによって、概念を単純化している
- List は immutable なので、List 自体を変更するようなメソッドを呼び出すと、既存の List をコピーしてそれに対する変更処理が適応されるので、元の List は変更しない.
- `::`は右被演算子と呼ばれ、右側がレシーバとなる. メソッド名の末尾がコロン `:`の時は常にこのパターンが適応される:
  - `右結合`: `1 :: List(2,3)` は `List(1,2,3)` となる. これは `List(2,3).::(1)` と同様な処理になる.
  - `::` `cons 演算子`と呼ぶ
- `mutable/ immutable set`:
  - mutable set は set の内容を変更することができるため、 set += 1 は `set.+=1` と書いているのと等価であり、set 自体が変更されるわけではないため val と宣言するとよい.
  - immutable set は実際に内容を描きかることができないので、set += 1 は、`set = set + 1` のように先に追加し、それを再代入するような処理になる. 第代入するため、val と書くことはできないため常に var として宣言しないといけない(SP69)
- `->` はキーと値を格納する２要素のタプルを返す. この仕組みを起動するために、scala は`暗黙の型変換(implicit conversion)`を使用する
- コードの中に var が含まれていたら、それはおそらく命令型のスタイルで書かれている. コードに var が含まれていなければ、つまり val だけが使われていれば、それはおそらく関数型のスタイルで書かれている. そのため、関数型のスタイルに近くための１つの方法は、var を使わずにプログラムを書く努力をすることである. (SP72)
- `def printArgs(args: Array[String]): Unit = { args.foreach(println); }` は純粋な関数ではなく、副作用がある. ここでの副作用は、標準出力への出力である
  - 副作用があるかどうかは返却型が Unit があるかどうかであり、Unit は何も返却しないことを示すので、その内部で何かしらの副作用があると考えることができる
- 副作用を持たないメソッドへの指向を持つことで、結果として副作用を持つコードを最小化したプログラムが設計しやすくなる(SP73)
- 関数の変数が var ではなく、val なのは var なら再代入されているかどうかをチェックしないといけないが、val だとそれが不要だからである(SP81)
- `手続き(procedure)`:再代入のような副作用を目的として実行されるメソッドのこと(SP82)
- `+` などの演算子を先頭に置いて改行はできない. 複数行に分けて記述したいときは、演算子は前の行の末尾にするか、全体を括弧で括る(SP82)
- キャッシュを使ってパフォーマンスをあげる時には、`scala.collection.mutable.WeakHashMap` のような弱いマップを使い、メモリーの残量が減ってきた時にはキャッシュ内のエントリをガベコレできるようにするのが良い(SP84)
- 個々の Singleton Object は静的変数から参照される`自動生成クラス(synthetic class)`のインスタンスとして実装されるため、Java の静的メンバーと同じ初期化セマンティックスを持っている.
  - Singleton Object はなんらかのコードから初めてアクセスされた時に初期化される(SP85)
  - （コンパイル時に）自動生成されたクラスの名前は、クラス名の末尾にドルをつけたものになる(`ChecksumAccumulator$`).
- java.lang, scala パッケージメンバのほか `Predef` というシングルトンオブジェクトのメンバを暗黙裏にファイルに import している(SP86)
  - この `Predef` には println など役立つメソッドを多数含む
- `整数値型(integral types)`: Byte, Short, Int, Long, Char
- `数値型(numeric types)`: 整数値型 Float, Double
- Int, Double などは`値型(value type)`と呼ばれ、これらのインスタンスをそのまま`Java プリミティブ型`に移すことができる(SP91)
- Scala では全てのメソッドが演算子(`中置演算子(infix operator)`)になれる（`中置記法（ちゅうちきほう、infix notation`）
  - example. indexOf メソッド: `"hello world" indexOf ('o', 5)`
- `前置(prefix)`: `-7` の`-`のように、メソッドをオブジェクトの前に置くメソッド(`_, -, !, ~` only). `unary_-`
  - `*P` とした時, `*` は unary メソッドの一つでは無いため、`*.p` と認識する(SP99)
- `後置(postfix)`: `7 toLong` の `toLong` のようにオブジェクトの後に置くメソッド. `unary_toLong`
- `名前渡しパラメーター(by-name parameter)`: `短絡(ショートサーキット)`のように第二引数の評価を行わない仕組みを実現する機構(SP103)
  - 演算子はメソッドなので、`&& ||` のような論理演算子を仕様する際に、渡す関数が引数として先に評価されるのではないかと考えるかもしれないが、名前渡しパラメーターの仕組みを使うことで先に評価せず先延ばしすることができる.
- scala の `==` 演算子では、プリミティブ型も参照型もどちらも比較できる.
  - Java では、参照型の比較は Java マシンヒープないの同じオブジェクトを指しているかどうかの比較になる.
  - scala で上記と同様の比較をするなら eq, ne 演算子を利用(SP106)
- `Immutable Object` の長所(SP113):
  1. 時間と共に変化する複雑な状態空間を持たないため、ミュータブルなオブジェクトよりも動作を確定しやすい
  2. ミュータブルなオブジェクトは他のコードに渡すときには、念の為コピーを作らないといけないが、イミュータブルならばそのようなことは考える必要がなり
  3. 複数のスレッドに渡す時にデータが破壊される恐れがない
  4. ハッシュテーブルのキーを安全に作ることができる. HashSet にミュータブルなオブジェクトをセットした後に、オブジェクトに変更を加えてしまうと、次に HashSet からオブジェクトを取得しようとした時に、オブジェクトが見つからない恐れがある.
- `Immutable Object` の短所:
  - 大規模な`オブジェクトグラフ`のコピーが必要になる場合がある. Mutable Object なら必要部分のみに更新をかければ済む.
  - `オブジェクトグラフ`: 複合のオブジェクトはそのインスタンス変数を介して他のオブジェクトとの間に参照関係を持ち、全体として`グラフ構造`になる(SP114)
- `補助コンストラクタ(auxiliary constructors)`: `クラスに複数のコンストラクターを与えなければならない場合`に利用する、`基本コンストラクタ(primary constructors)`以外のコンストラクタのこと.(SP118)
  - `this()`で始めることで、保持コンストラクタは最終的には、基本コンストラクタに落ち着くという効果がある(基本コンストラクタがクラスへの唯一の入り口である.)
- Scala コンパイラでは、内部で演算子識別子を`すり潰し(mangle)`して `$ 文字`を埋め込んで有効な Java 識別子に変えている.
  - 例えば、`:->` 演算子は、Scala の内部では、`$colon$minus$greater` と表されている(SP124)
  - Java からこの内部表現にアクセスしたい場合は、この内部表現を使わなければいけない.
- `ミックス識別子(mixed identifiers)`: `英数字識別子(unary など)`に`アンダスコアー(_)`と`演算子識別子(+ など)`が続く形である.
  - example. `unary_+`
- `リテラル識別子(literal identifiers)`: バッククォートで囲まれた任意の文字列(\`...\`)
  - バッククォートの間にどんな文字列を入れてもランタイムに識別子として受付させようという考えである.
  - example. yield は scala の予約後なので、Java Thread の yield メソッドにアクセスすることはできないが、バッククォートを使って、Thread.\`yield\`() とすればこのメソッドにアクセスすることができる.(SP124)
- `暗黙の型変換(implicit conversations)`:
  - `2 * Rational(2)` は `(2).*(Rational(2))` となるが、Int 2 という Int 型に対して Rational に定義された `*` 演算子を適用するために暗黙裏に型変換を行うこと.
  - example. `import scala.language.implicitConversions; implicit def intToRational(x: Int) = new Rational(x)`
- val を使うチャンスを探す. val はコードを読みやすく、リファクタリングしやすいものにしてくれる(SP131)
  - 一般に var を避ける, とともに while も避ける. (これらは命令型のスタイルなため). 特に while 式は値を生み出さない(SP133)
- 代入の結果は常に `Unit ()` であり、代入された変数の結果が評価に利用されない.
  - example. `while((line = readLine()) != "") { ... }`: `(line = readLine())`の結果は常に Unit であり、`""` になることはない.(SP133)
- 例外を値として受け取ることができる:
  - `val half = if(n % 2 == 0) n /2 else new RuntimeException("n must be even")`
  - この場合の else は 例外を投げて `Nothing を計算`する（`何も計算しない`という意味）.(SP139)
- `finally 節`は、非メモリリソースを確実にクローズするために使うのであり、`try-catch 節`で計算された値を変更してはならない(SP141)
  - `def f(): Int try return 1 finally return 2` は 2 を返す(Java も PHP も同様.)
  - `def g(): Int try 1 finally 2` は 1 を返す (!Scala 固有の危険な動作)
- `break, continue を使わずに済ませる`:
  - continue は if に、break を Boolean 変数に置き換える.(SP143)
  - どうしても break を使いたい場合は、`scala.util.control.Breaks._` を使う.
    - breakable は break 例外をキャッチするために使われるだけなので、break がならずしも breakable の中にないといけないわけではない(SP145)

```scala
// impl without break and continue.
var i = 0
var fountIt = false
while(i < args.length && !foundIt) {
  if(!args(i).startsWith("-"))
    if(args(i).endsWith(".scala"))
    foundIt = true
  i = i + 1
}

// better impl with recursive
def searchFrom(i: Int): Int =
  if (i >= args.length) -1
  else if (args(i).startWith("-")) searchFrom(i + 1)
  else if (args(i).endsWith(".scala")) i
val i = searchFrom(0)
```

- Java では外側のスコープの変数と同じ名前を持つ変数を内側のスコープで作成できないが、Scala では内側のスコープでは外側の変数は見えなくなるため作成できる. これを内側の変数は`シャドウイング(shadow)する`という(SP147)
- `関数リテラル(function literals)`はあるクラスにコンパイルされ、実行時にインスタンス化すると`値をとしての関数(function values)`になる.(SP155)
  - `関数リテラル`と`関数値`の違いは、関数リテラルがソースコードの中の存在であるのに対し、関数値が実行時にオブジェクトとして存在することである. この違いはクラス(ソースコード)とオブジェクト(実行時)の違いに似ている.
  - 関数リテラル: `(x: Int) => x + 1`
  - 関数値: `var increase = (x: Int) => x + 1`
  - 関数値は、scala パッケージに含まれるいくつかの`FunctionN トレイト`を拡張する何らかのクラスのインスタンスである(SP155)
- `部分適用された関数(partially applied functions)`:
  - `someNumbersList.foreach(println _)`: `println _` は、必要な引数を全て渡さずに呼び出された関数である(SP159)
- `閉じた項(closed terms)`: `自由変数(free variables)` を含まない関数
- `開いた項(open terms)`: `自由変数(free variables)` を含む関数 (`Closure` とも言う)
  - 自由変数の`束縛(binding)`をつかみ、開いた項を閉じることによって生み出されたものが関数（オブジェクト）と言える(SP162)
- `項`: 式を構成する論理的な単位(SP162)
- `連続パラメータ(repeated parameters, 可変個引数)`:
  - `def echo(args: String*) = for (arg <- args) println(arg); echo("a", "b", "c")`
  - 連続パラメータの型は Array
  - Array 型は直接渡せないので、次のようにする:
    - `echo(Array("a", "b", "c"): _*)`: But this results in `warning: Passing an explicit array value to a Scala varargs method is deprecated (since 2.13.0) and will result in a defensive copy; Use the more efficient non-copying ArraySeq.unsafeWrapArray or an explicit toIndexedSeq call`
      - `scala.collection.immutable.ArraySeq.unsafeWrapArray(arr): _*)`
      - `arr.toIndexedSeq: _*`
    - [reference](https://blog.magnolia.tech/entry/2019/06/02/175206)
- `末尾再帰(tail recursive)`: 再起処理の最後の処理として自分自身を呼び出す再起関数を言う.(SP168)
  - Scala コンパイラは、末尾再帰を検出したら、パラメータを新しい値に更新した後、再起呼び出しを関数 n 冒頭にジャンプするコードに書き換える. つまり、余分なオーバーヘッドがかからない.(SP168)
  - 再起呼び出しのために新しいスタックフレーム(関数呼び出しの際のリターンアドレスや引数をメモリに領域に置かない)を作らない(SP169)
  - `def boom(x: Int): Int = if(x == 0) throw new Exception("boom!") else boom(x - 1) + 1` これは、再起呼び出しの後でインクリメントをしているため、末尾再帰ではない
  - `def boom(x: Int): Int = if(x == 0) throw new Exception("boom!") else boom(x - 1)` こちらは末尾再帰.
  - もしスタックフレームが１つになる（末尾呼び出しの最適化）のが紛らわしいと感じるようなら、scala シェルか scala コンパイラに次のような引数を与えると良い: `scala -g:notailcalls`
- `共通部分は関数本体であり、非共通部分は引数という形で与える必要がある`(SP172)
- `高階関数(higher-order functions)`: 引数として関数を受け取る関数
- `def containOdd(nums: List[Int]) = nums.exists(_ % 2 == 1)`: 奇数があれば true を返す.(SP177)
- `カーリー(currying)`: `def curriedSum(x: Int)(y: Int) = x + y; curriedSum(1)(2)`
  - 上記の例は関数の連続の呼び出しとなっている
  - 第二関数への参照を取得するには、`val second = curriedSum(1)_` のように第一関数の呼び出しの直後にアンダスコアを付ける.(SP178)
    - アンダスコアの前にスペースを入れる必要はない(`println_` は識別子として認められる形式だが、`curriedSum(1)_` は認められる形式ではないため)
- コードの中に複数繰り返される制御パターンを見つけたら、それを新しい制御構造にすることを考えると良い.(SP179)
- `ローンパターン(loan pattern)`: 制御構造を実装する関数がリソースをオープンして、関数にリソースを貸し出すため.
- `名前渡しパラメーター(by-name parameters)`: プロパティ中のカラパラメータを省略できるようにするための仕組み.(SP182)
  - 名前渡しパラメーターでは引数を受け付ける関数が呼び出される前に評価されない.(SP183)
- メソッドの宣言規約:
  - パラメータを取らず、副作用を起こさないメソッドは、パラメータ無しのメソッドとして、からの括弧をつけないで宣言することが望ましい. 括弧がないと、副作用のあるメソッドの呼び出しがフィールドへのアクセスのように見えてしまう
  - 副作用を持つ関数を呼び出す時には、忘れずに空括弧を付けるようにする.(SP188)
- `統一形式の原則(uniform access principle)`: `属性`をフィールドとメソッドのどちらで実装するかによってクライアントコードが影響を受けてはならない(SP187)
  - 例えば、フィールド呼び出しの処理がメソッド呼び出しに変更され、その中でフィールドを返す前いに println をするように副作用がある処理に変更されてはいけない.
- Scala では、同じ名前のフィールドとメソッドを持てない(SP191)
  - Java では持つことができるが、これは名前空間の違いである:
  - Scala では名前空間は２つしかない:
    - `値（field, method, package, singleton object）`
      - package が、field, method と同じ名前空間を共有している理由は、型の名前とともにパッケージをインポートしたり、シングルトンオブジェクトのフィールドやメソッドをインポートできるようにするためである(SP191)
    - `型（class, trait）`
  - Java では４つ持つ:
    - field, method, type, package
- `不測のオーバライド(accidental overrides)`: `脆弱な基底クラス問題(fragile base class)`が姿を表す典型例である。この問題は、クラス階層の基底クラスに新しいメンバーを追加すると、クライアントコードが使えなくなるリスクが生じることである(SP194)
  - Scala では override 修飾子無しでは同じメソッドをオーバライドできないため、この問題はコンパイル時に検出される.
- 継承によって生まれる多相性を `サブ型による多相性(subtyping polymorphism)` と言う
  - もう一つの多相性が、`普遍的な多相性(universal polymorphism)` と言う.(SP195)
- Java が`純粋オブジェクト指向`でない側面:
  - `純粋参照等価性（参照先が同じであること）`が成立しない(SP212)
    - 値が参照型にボクシングされる時、等価演算子 `==` の結果はメモリアドレスの等価性を見るため false を返す.
    - `val x = "abc".substring(2); val y = "abc".substring(2); x == y`: Java では false になるが、scala では自然な形で比較を行うためこのような場合でも true を返却する(SP212)
      - このような場合 Java では `equals` を使用しなければならない.
    - Java のような `==` による参照等価性を見るためには `eq` または `ne` を使用
- Scala が整数を Java オブジェクトを見なさなければならなくなった時には、`java.lang.Integer` という`バックアップクラス`を使用する.
  - バックアップクラスが必要になる場合は、整数をレシーバとして toString を呼び出した時や、Any 型に整数を代入したりする時である
  - `Scala の Int 型` の整数は、必要に応じて`ボクシングされた（値型を参照型に変換された）整数`である `java.lang.Integer` に透過的に変換される.(SP211)
- Null クラスは null の参照の型であり、全ての参照クラス（AnyRef）のサブクラスであり、値型とは互換性がない(SP213)
- Nothing 型の用途の一つは異常終了を知らせることである.
- Scala コードは Java エコシステムの一部なので、一般にリリースする Scala パッケージの名前は、Java の`逆ドメイン名方式`（`com.`から始まる方式）に従った方が良い(SP237)
- `package`節以外にパッケージ化する方法は、`C#`の名前空間に似た方法をである`パッケージング(packaging)`構文を用いるものである. `package my_package{ class TestClass }`
  - `連鎖パッケージ節(Chained Package Clauses)`: 中括弧を使わずに複数の package 節を並べるスタイルのこと(SP239)
  - インスタンスの変数を同じスコープないに取り込むこともできる(`C++ using namespace package_name` と同等の機能)
    - `def showFruit(fruit: Fruit) = { import fruit._; println(name) }`: 通常はシングルトンでないオブジェクトのメンバーを import. (SP241)
- `import`:
  - `import Fruits.{Pear => _, _}`: Pear 以外の全てのメンバーを import する(SP243)
    - ２つ目の `_` は `catch-all 節`であり、一番後ろにおく必要がある(SP243)
- アクセス修飾子:

  - private: Java では OuterClass は InnerClass の private へアクセスできるが、Scala ではそれができない(SP245)
  - protected: Java では、同じパッケージの他のクラスからもアクセスが認められるが、Scala ではそれが定義されているクラスのサブクラスでないとアクセスできない(Scala の制限の方がより一般的)
  - private も protected も付けない場合は`public`
  - `アクセス保護のスコープ`:
    - `private[this]` をつけると、同じオブジェクトからのみアクセス可能になる.
      - つまり `class Nav { private[this] var speed = 100}; val n = new Nav; n.speed`: この呼び出しは外側からのアクセスなためエラー(SP247)
    - `private[パッケージ名]` を付けると同一パッケージに所属しているものからのみ
    - `protected[パッケージ名]` をつけると、派生クラスに加えて追加で同じパッケージに所属しているもの全てからアクセスできる
  - `private[this]`: コンパニオンオブジェクトはその同名のクラスのメンバーにアクセスできない
  - `private`: コンパニオンオブジェクトはその同名のクラスのメンバーにアクセスできる
  - コンパニオンクラスはコンパニオンオブジェクトの非公開のメンバーにアクセスできるし、コンパニオンオブジェクトはコンパニオンクラスの非公開メンバーにアクセスできる(SP249)
  - コンパニオンオブジェクトはサブクラスを持たないため、protected 修飾子は無意味である.

- コンパニオンオブジェクト
  - `コンパニオンオブジェクト`: クラスと同じファイル内に同じ名前で定義された`シングルトンオブジェクト`のこと
  - コンパニオンオブジェクトを使ったコードを REPL で試すには、REPL の`:paste` コマンドを使って、クラスとコンパニオンオブジェクトを一緒にペーストする.
  - クラスとコンパニオンオブジェクトは同一ファイル中に置かれていなければならないが、REPL で両者を別々に入力した場合、コンパニオン関係を REPL が正しく認識できない.
- `パッケージオブジェクト`: `package object package_object_name {}` のように定義し、パッケージのトップレベルに汎用関数などを配置する手法(SP249)
  - パッケージ全体で使う型の別名、暗黙の型変換を収めるために頻繁に利用される(SP250)
- `仕様としてのテスト`:
  - `ふるまい駆動開発(behavior-driven development: BDD)`のテストスタイル: コードに対して期待されるふるまいを人間が理解できる仕様にまとめた上で、実際のコードんふるまいがその仕様に合っているかを確かめるテストを実施すること(SP256)
- `ケースクラス`：
  - ケースクラスのメリット:
    - ファクトリメソッドを自動で追加するため、`new ...` とする必要がない(SP265)
    - ケースクラスのパラメータリスト内の全てのパラメーターに暗黙のうちに val プレフィックスをつけるため、パラーメータはフィールドとして管理される. (val をプレフィックスにした時に、暗黙のうちにクラスのフィールドにするのは、ケースクラスに限らない)(SP432 も合わせて参照)
    - コンパイラはクラスに toString, hashCode, `equals` メソッドの`自然な`実装を追加する
    - コンパイラは変更を加えたコピーを作成するために、クラスに copy メソッドを追加する
      - このメソッドは、１つか２つの属性が異なるだけでほぼ同じクラスのインスタンスを新たに作成する場合に便利(SP266)
      - 名前付き引数で copy すれば、元のオブジェクトのフィールドを引き継ぎつつ、部分的に新しい引数をもったオブジェクを簡単に作成することができる.
- パターンマッチ:
  - どの alternatives にもマッチしなかった場合(デフォルトにもマッチしない)は、`MatchError`が送出される(SP267)
  - `case _ =>` とすると何にもマッチしなかった場合は `Unit 値の ()` を結果値として返す
  - 要素自体にワイルドカードパターンを適用することもできる `expr match{ case BinOp(_, _, _) => println(...) }`(SP269)
  - Scala コンパイラはどのようにして定数(セレクタ値自体を表す変数ではない)と変数を区別しているか: 先頭が小文字になっている単純名は`パターン変数`、そうでないものは`定数`と見なすようにしている.(SP270)
    - 変数を定数とみなしたいときは、`backquote で囲む`か、`this.pi`, `obj.pi` であれば先頭が小文字になっていても定数として扱われる(SP271)
      - backquote のもう一つの使い方は 426 行目の Thread.\`yield\`() のような使い方である.
  - コンストラクタパターン: ケースクラスのインスタンス化した内容でマッチさせるパターン
  - `m: Map[_, _]` という型付きパターンの中で `Map[_, _]` の部分は、`型パターン`と呼ばれる(SP274)
  - `型削除(type erasure)`: Scala は Java と同様の型削除のジェネリックプログラミングを採用しており、実行時には型引数の情報を管理しない。そのため、`Map[Int, Int]`のように、Map などに与えられた引数の型が何かを実行時に確かめるすべがない(SP275)
    - 配列は例外で、配列値とともに配列型も保持している
  - `変数束縛パターン(variable-binding pattern)`: `変数名、@記号、パターン`の順序で書く
    - example. `case UnOp("abs", e @ UnOp("abs", _)) => e` では e は変数名、`UnOp("abs", _)` がパターンであり、処理の本体で、`UnOp("abs", _)` にマッチした部分を e として使用することができる(SP276)
  - `シールドクラス`: シールドクラスでは同じファイルで定義されているサブクラス以外は、新しいサブクラスを追加できない. こうすることでケースクラスのパターンマッチにおいて漏れを防ぐことができる(SP278)
  - `ケースシーケンス`:
    - ケースシーケンスは関数リテラルで、それを一般化したものにすぎない.
    - このケースは関数本体へのエントリーポイントであり、パラメータはパターンで返されえる.(SP282)
    - [Ref](https://base64.work/so/scala/2722753)
    - `部分関数(partial functions)` にもなりうる(SP283)
    - このような関数にサポートしていない値を適用すると、実行例外が起きる. そのため、その値をサポートしている部分関数が定義されているかどうかをチェックするためには、`PartialFunction` でラップしてあげる必要がある(SP283)
    - 部分関数より全関数を使うようにした方が良いが、部分関数が役に立つケースが２つある(SP284):
      1. 処理されない値は渡されないことがはっきりしている場合
      2. 部分間すうを使うことを想定しており、関数呼び出し前に isDefinedAt を必ずチェックするようなフレームワークを使っている場合（Akka アクターライブラリでは、例えば react の引数は部分関数である）
- `リスト`:
  - 配列は`フラット`だが、リストは`再起的な構造(連結リスト: linked list)`を持つ(SP294)
  - リストの要素は`等質的(homogeneous)`である: 全ての要素が同じ型になっている.
  - リスト型は`共変(covariant)`である: `S 型`が `T 型`のサブ型なら、`List[S]`は`List[T]` のサブ型になる
  - Nothing は全ての型のサブ型だからどのタイプにも追加することができる: `val xs: List[String] = List()`
  - `::, cons, コンス` と Nil の２つから組み立てられる(SP295)
    - cons は constructor の略である(SP432)
    - Nil は `List[Nothing]` 型を継承する.(SP431)
  - リスト数があらかじめ分からない時は、`::` を使って例えば、`val a :: b :: rest = fruit;` とすると、リストの要素数が 2 個以上の全てのリストにマッチすることができる(SP297)
  - `リストのパターンマッチについて`:
    - `::` は`コンストラクタパターン`として扱われる(SP298)
    - `x :: xs` は `::(x, xs)` と同等である.
    - `::` は２通りの意味があり、scala パッケージ内のクラス名であるとともに、List クラス内のメソッドでもある
  - `一階メソッド(first-order methods)`: パラメーターとして関数を取らないメソッドである.
    - リストの concat: `List(0, 1) ::: List(2, 3, 4)`
    - `分割統治原則(divide and conquer)`:
      - リストを対象とする多くのアルゴリズムは、パターンマッチを使って最初に入力リストを単純なケースに分割する. これが原則の中の`分割(divide)`の部分である.
      - 次に個々のケースのための結果値を作る. 結果値が空でないリストなら、その一部は同じアルゴリズムを再帰的に呼び出すことによって組み立てられる. これが原則の`統治(conquer)`の部分である.(SP299)
  - `???` は scala.NotImplementedError を投げるメソッドであり、Nothing の結果型を持つため、開発中の一時的な実装に使うことができる(SP300)
  - head には last, tail には init という`双方的な操作(dual operation)`が存在する(SP301)
  - head や tail はともに`一定時間`で実行されるが、init と last はリスト全体をたどらないと連結結果を計算できないため、`リストの長さに比例する計算時間`を要する: データ構造を設計する場合は、リストの末尾ではなく、先頭にアクセスして操作できるようにすると良い(SP301)
    - リストの末尾へのアクセスが頻繁に行われる場合は、reverse してリストを反転させるとよい.
  - `高階関数(higher-order function)`: 関数を引数として受け取るか、関数を返す関数のこと. もっとも一般的な関数は map(SP309)
  - `多相型`: ２つ以上の型パラメータを使う演算のこと(SP320)
  - Scala の型推論の限界:
    - `(xss :\ List()) (_ ::: _)`: 通常カリー化メソッドのように第一引数の型だけを考慮し、その第二引数のメソッドの型を推論する手法だと、fold のこの例ではうまくいかない. かと言って第二引数の関数からは型情報は与えられていないため、推論を遅らせても解決しない. これは、Scala の`局所的な型推論`の限界であり、ML や Haskel などの関数型言語で使われている、`Hindley-Milner` スタイルのより視野の広い型推論にはこのような問題はない. 幸いこの問題は、一部の境界条件でしか発生しないし、通常は明示的な型アノテーションを追加することによって解決できる(SP321)
- `Collections`:
  - ListBuffer を使うと、要素を末尾に追加する処理も高速に行うことができる.(SP323)
  - String クラスには、exists メソッドはないため、Scala コンパイラは暗黙の型変換を行い、`StringOps` へ変換を行う.(SP325
  - Set や Map のファクトリメソッドでは、要素が 5 個未満の場合は、最大限のパフォーマンスを引き出すために個々のサイズごとに作られた専用クラスが使われる. 5 個以上の場合はそのファクトリメソッドはハッシュとらいを使った実装を返す.(SP331)
  - `mutable vs immutable`: どちらが良いか分からない時は、immutable を選択する. また、コードが複雑になり実装が正しいかどうか判断しにくくなってきたら、一部のコレクションを immutable 版に切り替えることを検討すると良い. 特に以下のことについては検討する: (SP333)
    - 適切な場所で immutable なコレクションのコピーしているか?
    - mutable なコレクションのオーナーや内容について考えることが増えすぎではないか?
  - scala は `+=` 未サポートコレクションに対してそれを適用すると `a = a + b` と解釈する?(SP333)
    - val だと `error: value += is not a member of scala.collection.immutable.Set[String]`
    - var で宣言するとコレクションは immutable でありながら、 mutable な set として動作する(アップデートされる)
      - 新しいコレクションが作成され、変数にその新しいコレクションが割り当てられる(SP334)
    - このイコールで終わる `@=`演算子のようなシンタックスシュガーはコレクションだけではなく、あらゆるタイプの値で機能する. (SP335)
  - mutable, immutable 間の変換処理:
    - immutable に変換したい時は、`++`
    - mutable に変換したい時は、`++=`
  - tuple を使うときは、`destructuring assignment（分割代入）`と `multiple definition（同時定義）`に気をつける
    - `val (word, idx) = ("OK", 0)`: 分割代入
    - `val word, idx = ("OK", 0)`: 同時定義
- `純粋関数型オブジェクト(purely functional objects)` vs `ミュータブルオブジェクト(mutable objects)`:
  - `純粋関数型オブジェクト`: メソッドを呼び出したり、フィールドを間接参照したりした時に、必ず同じ結果値が返される
  - `ミュータブルオブジェクト`: メソッドの呼び出しやフィールドのアクセスの結果値は、オブジェクトに対してそれまでにどのような操作が加えられたかによって影響する. example: 銀行口座
    - var を使っているからと言ってミュータブルオブジェクトになるわけではない
      - example: 計算量がある処理をキャッシュするために cacheKey 変数を使うなど(SP344)
    - `再代入可能な変数(reassignable variables)`: Scala では、何らかのオブジェクトの公開・限定公開メンバになっている var には、暗黙のうちにゲッター、セッターメソッドが定義されてる.(SP385 も合わせて参照):
      - `var x` のゲッター名は `x` セッター名は `x_=`
      - `アクセスメソッド`（ゲッター、セッター）の`公開スコープ`はその変数と同じになる
      - さらにそのフィールドのには必ず `private[this]`（フィールドを含んでいるオブジェクトからでなければアクセスできない）マークがつけられる
- 離散イベントシミュレーション:
  - Scala のようなホスト言語に DSL (ドメイン固有言語)を埋め込む
  - フレームワークを作成: あるタイミングに実行されるアクションを追跡する
- 純粋関数型待ち行列:

  - 新しい item を追加された行列は、変化しない(SP363)
  - `完全永続(fully persistent)`である

- `型コンストラクタ(type constructors)`: `Queue[T]`のように type parameters を取るクラスやトレイとはそのままでは型として使用できないが、`Queue[Int]`のようにすれば型として使用できるため、型コンストラクタと呼ばれる(SP368)
  - このように型コンストラクタは、`型ファミリー`を`生成`する
  - 型コンストラクタはジェネリックトレイト、クラスということもできる.
- `リスコフ置換原則(Liskov Substitution Principle)`:
  - U 型が必要とされる全ての場所で、T 型の値が使えるなら、T 型は U 型のサブ型だと考えて良い(SP375)
- `Abstract Members`:
  - Scala での抽象型とは、trait や class の中に定義されている何らかの型を宣言したメンバ(`型メンバー`)のことである(SP383)
    - この抽象型を実装すると`非抽象型メンバ`と呼ばれる
  - 抽象 val は実装クラスに制限を課す: 実装クラスは val 出なければいけない
  - 抽象メソッド宣言は、具象メソッド定義で実装しても、具象 val 定義で実装しても構わない(SP384)
  - `クラスパラメータと抽象フィールドの初期化の順序の違い`:
    - 無名クラスはトレイトの後に評価され、抽象 vals はその無名クラスと同時に評価される
    - RationalTrait 初期化中は、numberArg, denomArg は使えないため、デフォルトの値として 0 になる(SP386)
    - `クラスパラメータ`はクラスコンストラクタに渡される前に評価される（`パラメタが名前渡しの場合を除く!`）のに対し、`サブクラスでの val 定義の実装`は、スーパークラスが初期化された後に初めて評価される(SP387)
  - `事前初期化済みフィールド(pre-initialized fields)`:
    - スーパークラスが呼び出される前に、サブクラスのフィールドを初期化できるようにするもの. 上の `サブクラスでの val 定義の実装`への対策である.
    - 中括弧`{}`の後に with をつけてその後にスーパークラスを指定するように記述.
  - `遅延評価 val`:
    - 初期化の順序をシステム地震に考えさせたい場合に利用.
    - val 定義の前に `lazy 修飾子`をつけると、右辺の初期化しきは、val が初めて使われるまで評価しないようにすることができる(SP389)
    - def を使ってパラメータなしメソッドとして定義した場合に似ているが、def とは異なり、２回目以降の呼び出しでは再評価されることはない(SP389)
    - 全てが最終的に初期化されている限り、初期化の実行順が意味を持たない関数型オブジェクトに適している. 一方で、基本的に命令型に書かれているコードには、あまり適していない(SP391)
    - `遅延評価関数型言語`:
      - 遅延評価定義と関数型コードの相性の良さを最初に引き出した言語は scala ではない.
      - 全ての値をパラメータが遅延評価的に初期化される`遅延評価関数型プログラミング言語`という分野があり、そのタイプの言語で最もよく知られているのは、`Haskell` である(SP391)
- Scala では、Java と同様に、内部クラスインスタンスは自分を包含している外部クラスインスタンスへの参照を持っている. (SP396)
  - そのため、内部クラスは外部クラスのメンバにアクセスできる
  - したがって、何らかの形で外部クラスインスタンスをしていなければ、内部クラスのインスタンスを作ることはできない:
    1. 外部クラスの本体にあで内部クラスのインスタンスを作れるようにすれば良い.
    2. パス依存型を使う方法: 例えば、型 o1.Inner は特定の外部オブジェクトの名前になっているため、このインスタンスを次のようにして生成することができる. `new o1.Inner`
    - `new Outer#Inner` 型は、Outer の特定のインスタンスを指定していないので、そのインスタンスを作ることはできない.
      - Enumeration:
        - Value は Enumeration の内部クラスで、定義された列挙体クラスの内部クラスとして宣言される.
        - この時、定義された列挙体クラスは`パス`、Value は`依存型`である(SP398)
- プログラミングの世界では、他人のライブラリを使う時は普通は与えられたものをそのまま使わなければならない:
  - この問題を緩和するために、Ruby ではモジュールをもち、Smalltalk ではパッケージに互いのクラスを追加できるようにしている. これらは非常に強力であるが、アプリケーション全体でクラスの動作を変えてしまうことができるという点で危険である(SP408)
  - C# では影響がより局所的に抑えられた静的拡張メソッドを持っているが、制限が厳しい
  - この問題に対する Scala の解は、`暗黙の型変換（implicit conversions）`と`暗黙のパラメーター（implicit parameters）`である.
  - `暗黙の型変換`: ２つのソフトウェア構成要素が互いに意識せずに開発された時に、それぞれの要素が機能することを助けるものである.
  - `implicit 定義`: 型エラーを修復するためにコンパイラがプログラムに挿入できるメソッドのことである.(SP410)
    - `マーキングルール`: implicit は任意の変数、関数、オブジェクト定義につけられる. コンパイラは implicit がつけられたコンバータのみ選択する
    - `スコープルール`:
      - コンバータは, 必要な処理の`スコープ`に入っていなければならない.
      - 暗黙の型変換は、`単一の識別子`としてスコープに入っていなければならない: `someVariable.convert(...)` のような convert は選択されない(この convert を使いたい場合は、import して someVariable を識別子から外さないといけない). (SP411)
      - 型変換において`ソース型(変換前の型)`や`ターゲット型(要求された変換後の型)`のコンパニオンオブジェクトに含まれる implicit 定義もコンパイラは探索する(SP411)
    - 暗黙の型変換が試される場所(SP413):
      1. 要求された型への変換: メソッドの引数で要求している型と渡している型が異なる場合
      - `scala.Predef` オブジェクトは`小さな`数値型から`大きな`数値型への暗黙の型変換を定義している(SP414)
        - これにより、scala では Int の値を Double 型変数に格納することができる.
        - example. `implicit def int2double(x: Int): Double = x.toDouble`
      2. 特定のレシーバに対し存在しないメソッドを呼び出している場合(つまりメソッド呼び出しで使われるオブジェクト自身に適用される)
      - Map の `->` は標準 Scala プリアンブル(scala.Predef)の中で定義されている `ArrowAssoc` クラスメソッドである. (SP416)
        - 言語に対する構文拡張的な機能を提供するライブラリーでは、この`リッチラッパーパターン`がよく見られる.
        - レシーバクラスにはなさそうなメソッドを呼び出しているコードがあればおそらく暗黙の型変換を使っているのである.
        - RichSomething という名前のクラスがあれば、それは Something 型に見えるメソッドを追加していると考えて良い.
      - 暗黙のクラス:
        - case class は暗黙のクラスにはなれない
        - 暗黙のクラスのコンストラクタは１個の引数をとるものでなければならない
        - 暗黙のクラスは他のオブジェクト、クラス、トレイとの中に配置されていなければならない
      3. 暗黙のパラメータのように、呼び出し元が求めていることについて、`呼び出された関数により多くの情報を与えるために使われる`. 特にジェネリック関数では、そのままでは引数の型について何の情報も与えられないことがあり、このような時に暗黙のパラメータは役に立つ.
      - コンパイラは足りないパラメータリストを補い、関数呼び出しを完成させる. 捕捉されるのは、最後のパラメータではなく、`カリー化`された`最後の`パラメータリスト全体である（全体、つまり、最後のパラメータリストは複数あっても良い）. (SP418)
      - someCall に４つの引数が必要で、`someCall(a)` として１つパラメータを与えている場合で、コンパイラは `someCall(a)(b,c,d)` に置き換える.
      - また、`暗黙のパラメタ`がより多く使用されるケースとして、Haskell の型クラスと同じように、先に（指定されるよりも前に）パラメータリストで明示されていた型の情報を提供したい場合である. つまり、呼び出す時にはすでに何が渡されるべきか推測されるような使われ方である.(SP421)
    - 複数の暗黙の型変換が適用できる時:
      - 複数ある場合は、より`限定的`な変換を採用する. 次の点が当てはまる暗黙の型変換は、他の暗黙の型変換よりも限定的である:
        - 引数の型が別の引数のサブ型になっている
        - 型変換は両方ともメソッドであり、片方のクラスが片方のクラスを継承している.
      - example. `val cba = "abc".reverse` は、WrappedString ではなく、StringOps へ変換される. これは、WrappedString が定義されているクラスのサブクラスで StringOps が定義されているためである. (SP427)
      - `foo(null)` を呼び出した時にオーバーロードされた関数 foo の引数が Any 型よりも String 型が採用される. これは、String 型の方が Any 型よりも明らかに限定的だからである(SP426)
    - 暗黙の型変換を使う前に、継承、ミックスイン合成、メソッドの多重定義など、他の手段で同様の効果が得られないかどうかを自問自答した方が良い.(SP429)
- `For Expression Revisited`:
  - リストと配列などで for 式がサポートされているのは、リストと配列が map, flatMap, withFilter 操作を定義しているからである.(SP451)
    - この二つ以外にも、`範囲`、`イテレーター`、`ストリーム`、そして`集合の全て`の実装でサポートしている.
    - これらのメソッドを定義しておけば、自身の型でも for ループを使うことができる.
- `Collection`:
  - `サイズ情報`:
    - トラバーサブルには、有限のものと無限のものがある. 無限トラバーサブルは、例えば、`Stream.from(0)`という自然数ストリームなどである(SP459)
    - `hasDefiniteSize`はコレクションが有限なら true, 無限なら false を返す.
  - `ビュー演算`: ビューとは、遅延評価されるコレクションのことである.(SP460)
  - シーケンストレイト(Seq):
    - update: ミュータブルシーケンスで使えるメソッド
    - updated: イミュータブルシーケンスで使えるメソッド
    - `a indexOfSlice b`: a に部分シーケンス b が含まれている場合は、最初の位置の先頭要素の添字を返す.
    - indexWhere, segmentLength, prefixLength などの述語関数を使った検索メソッドがある(SP467)
    - `(a corresponds b)(p)`: a と b の対応する要素が二項の術後演算 p を満たすかどうかを調べる(SP468)
    - `バッファー`:
      - 同じ目的のメソッドが複数ある理由は、Traversable などの上位トレイトからいくつかメソッドを継承しているため.
        - また、Traversable で定義されているメソッドの引数は Traversable であるが、その下層レイアーで定義されたメソッドの引数はそのレイアーの型になることがあるので注意(例えば、集合の `++` メソッドと `union`または`|`メソッド).(SP470)
      - `+=` `++=` 末尾に要素を追加. `+=` は単一要素を追加し、`++=` は複数要素を追加する.
      - `+=:` `++=:` 先頭に要素を追加
      - insert insertAll 要素を挿入
      - remove, `-=` 要素を削除
      - add/remove は演算結果が Boolean であるため結果が欲しい時に使う(SP473)
      - 集合サイズが 4 以下の場合は、イミュータブル版を利用する(SP473)
    - `マップ`:
      - get: Option 型を返す. キーが存在すれば、`Some(T)`, 存在しなければ None(SP473)
      - put: ミュータブルマップで、`ms put (k,v)` で k->v を追加し、k がすでに存在する場合は、それまでに対応していた v を返却(SP476)
  - Vector:
    - 先頭要素以外の要素にアクセスしてもパフォーマンスは一定(SP478)
      - 要素が 32 個までのベクターは、１個のノードで表現できる. 要素が 32x32=1024 個までのベクタは、１回の間接参照だけで表現できる.
      - 要素が 2 の 15 乗以下のベクターであれば木構造のルートから最後の要素のノードまで２ホップあれば十分. それ以上に要素が増えていっても、この要素の選択は５回以下の配列選択(`プリミティブ処理`)に収まるため、実質パフォーマンスが一定であると言える.
      - updated などによる Vector の更新は、32 個の要素を持つ 1~5 このノードを新規作成するだけのコストである(ベクタの全要素をコピーするわけではない).
      - 高速のランダム選択と高速の関数的更新の best balance がとられているため、イミュータブルな添字付きシーケンスのデフォルト実装になっている: `collection.immutable.IndexedSeq(12,3) = scala.collection.immutable.IndexedSeq[Int] = Vector(12, 3)`
    - [要素の追加](https://alvinalexander.com/scala/how-to-append-prepend-items-vector-seq-in-scala/)
  - `ハッシュトライ`: `トライ(trie)`は Retrieval に由良うしており、ツリーまたはトライと発音(SP481).
    - イミュータブルな集合やマップを効率的に実装するための標準的な手段.
    - Vector のような木構造を持つ
    - 要素を探索する際に、ハッシュの下位 5 ビットを使って探索
  - イミュータブルビットセット: 格納する整数が数百程度であればかなりコンパクト.(SP482)
  - Queue: イミュータブル版での要素の追加は enqueue, ミュータブル版では `+=`, `++=`
  - ハッシュテーブル:
    - 反復処理は`内部の配列にそうそがたまたま並んでいる順序で実行される`
    - 要素がキー順に並んでいるわけはないため、順序を木にするときは連結ハッシュ集合やマップを使う(SP487)
      - このようなコレクションの反復処理は、要素が追加された順序で必ず実行される.
  - Array:
    - Array は Seq のサブ型ではないため、そのままでは Seq のメソッド(Traversable トレイトなどで定義されているメソッド等)は使えない. Array に対してそのメソッドを実行すると暗黙な型変換が実行され、ArrayOps へ変換されるためそれらのメソッドを使用することがでいる(SP490)
    - Array オブジェクトを Seq 型の変数に代入するときは、暗黙的に ArrayWrapped 型に変換される(SP489)
    - `Array[T]` のような配列のジェネリック型は、Java には存在しないためコンパイル時に `T => AnyRef` へ変換され、実行時に正確な型に確定される.(SP491)
  - ビュー: Scala のコレクションは全て`正格`である.(SP497)
    - `正格`コレクションを遅延的(`非正格`)にするための方法が View を利用することである.
    - view メソッド:`正格` => `非正格`
    - force メソッド:`非正格` => `正格`
  - Iterator:
    - 位置を先に進めないまま、次の要素をチェックしたいときは、BufferedIterator の head を利用する(SP507)
    - `val it = Iterator(1, 2); val bit = it.buffered; while(bit.head.isEmpty) {bit.next() };`
  - Java コレクションとの互換性:
    - JavaConversions オブジェクトで主要なコレクション型の暗黙の相互変換ができる(SP509)
      - [`Java Conversions got @deprecated in Scala 2.13.0`](https://stackoverflow.com/questions/8301947/what-is-the-difference-between-javaconverters-and-javaconversions-in-scala)
    - @deprecated: `import collection.JavaConversions._; val jul: java.util.List[Int] = ArrayBuffer(1,2,3)`
    - `import scala.collection.JavaConverters._; val jul: java.util.List[Int] = ArrayBuffer(1,2,3).asJava`
- `The Architecture of Scala Collections`:

  - Scala コレクションフレームワークの主要な設計目標は、全ての演算をできる限り少ない場所で定義し、重複を避けることだった.(理想としては、全てのものを一箇所だけで定義すべきだが、再定義が必要な例外もかなありある.(SP512)) - そのため、ほとんどの演算はコレクションテンプレートで実装されている
    - トラバーサルは、Traversable の foreach メソッドによって処理され、新しいコレクションの構築は、Builder クラスのインスタンスによって処理される.
  - `実装トレイト(implementation traits)`: コレクションのジェネリックなビルダとトラバーサルを使うことで、コードの重複を避けて`同じ結果型`の原則を貫いている SP514)
    - 実装トレイトには Like がサフィックスにつけられている. 例えば、Traversable の実装トレイトは TraversableLike である.
  - IndexedSeq の foreach は自身の apply メソッドを使って、コレクションの全ての i 番目の要素を単純に選択していく処理.(SP526)
    - 他の多くのコレクションメソッドはループ処理を foreach を使って実装しているため、労力を割いて foreach の最適化するだけの価値がある.
  - `Patricia(Prractical Algorithm to Retrieve Information Coded in Alphanumeric)`:
    - 検索キーの後ろに続く文字が、一意の子孫ツリーを決定する木構造を使って、集合やマップを格納する手法(SP526)
    - 検索キーが`abc`であれば `a -> b -> c` のようにルートから葉まで探索する
    - Patricia を使って作られた集合は Patricia Trie と言う.
    - Patricia Trie では、ツリーのシア上位を除き、大半のノードでは、後ろに続くデータはごく少数であるため immutable map に格納した方が効率的.(SP528)
  - 集合やマップには、MapBuilder クラスのインスタンスとしてデフォルトのビルダーがたついけくるため、newBuilder メソッドを定義する必要はない(SP529)

- `Extractor`:
  - 抽出子: メンバーの一つとして `unapply` というメソッドを持っているオブジェクトである.
  - 抽出子オブジェクトは、値を構築するための `apply` と言う相補的なメソッドを定義していることが多いが、必須ではない
- XLM:
  - References:
    - [Scala で XML を加工する](https://shinharad.hateblo.jp/entry/2018/08/16/091643)
- `Modular Programming Using Objects`(SP567):
  - `ドメイン層(domain layer)`: ビジネスコンセプト、ビジネスルールを表現し、外部リレーショナルデータベースに永続される状態情報をカプセル化するドメインオブジェクトを定義
  - `アプリケーション層(application layer)`: アプリケーションがクライアントに提供するサービス(ユーザーインタフェース層を含む)に基づいて構成された API を提供. ドメイン層のオブジェクトに仕事を委譲しながら、全体のコーディネートを行ってサービスを実装する.
  - 特定のオブジェクトについては本物とモックの両方を接続できるようにしたい.
    - ドメイン層でモックの利用を可能にするオブジェクトの一つは、リレーショナルデータベースを表現するオブジェクトである.
    - アプリケーション層では、データベースブラウザで、これは例えば手元にある材料を含む全てのレシピを検索できるように、データベースの検索と閲覧を助けてくれるものである.
  - プログラムはシングルトンオブジェクトに分割することができていて、それらはそれぞれモジュールと考えられていることが非常に役に立つ. (SP569)
  - 全てのモジュラーアプリケーションでは、特定の状況で使うべき実際のモジュール実装を指定する何らかの手段を持っていなければならない. 例えば、Spring アプリケーションでは、使用する実装を外部 XML ファイルで指定してコンフィギュレーションを行う. Scala では、Scala コード自体を使ってコンフィギュレーションを行える. Scala コンパイラにコンフィルギュレーションファイルを通すことにより、実際に使う前にミススペルが明らかになるというメリットがある.(SP576)
- `Object Equality`:
  - ケースクラスを使うとコンパイラが自動的に正しい性質をもった equals や hashCode を追加してくれる. (L579 も参照)
- `Combining Scala and Java`:
  - Scala の機能は可能な限り Java の同等の機能としてバイトコードに変換される. (SP600)
    - 多重定義は実行時に解決できれば素晴らしかっただろうが、そうすると Java の方法と食い違うため Java と併用するのが難しくなってしまっていただろう. Scala のメソッドとメソッド呼び出しは直接 Java のメソッドとメソッド呼び出しにマッピングされる.
  - Java には無い機能は、間接的にマッピングされる. 翻訳をできる限り単純にするための作業は継続的に行われている. 翻訳方法は、javap コマンドなどのツールで `.class` ファイルを解析すれば調べることができる. (SP601)
  - Java のアノテーションを作る Scala 独自の仕組みは無いため、Java のアノテーションを作成する時は、Java の`java.lang.annotation.*;`で先に作成・コンパイルしておき、その後 Scala で利用する(SP605)
  - Scala は Java の全ての型に対応するものを持っている. そうしなければ、Scala コードは文法を満足させる Java クラスにアクセスできなくなってしまうためである. (SP607)
  - Java のワイルドカード(`?`) を持つ Collection から Scala の Collection へマップするときにはワイルドカード(`_`) を使う代わりに`抽象メンバ`を使うとうまくいく(SP608)
  - Scala 2.12 からは、Java の SAM(single abstract method) 箇所には`関数リテラル`を渡せるようになった. (SP611)
    - `関数リテラル`は渡せるが`関数型 val f = (i: Int) => i + 1`は渡せない.
      - `関数型`は Java の`デフォルトメソッドを含んだインタフェース`としてコンパイルされるので、Java には SAM のように見える(SP613)
- `Future と並列処理`:
  - for はシリアライズ（直列化）されつため、複数の Future を渡すときは先に作成する.(SP619)
  - failed メソッドは失敗した Failure 型を Success 型に変換する(SP623)
  - sequence: `List[Future[Int]]` -> `Future[List[Int]]` へ変換(SP628)
  - traverse: `List[Int]` -> `Future[List[Int]]` へ変換
- `パーサー・コンビネーター`: 入力言語からソフトウェアが処理できるデータ構造に変換する手段(SP634)
  - `選択肢`:
    1. 独自のパーサーを構築する. エキスパートでなければ難しく時間がかかる
    2. パーサージェネレータ(パーサー生成器)
    - C 言語なら yacc, bison
    - Java なら ANTLR. スキャナジェネレーターの lex, flex, JFLEX を併用しなければならない場合も多い
    3. 言語内 DSL(internal domain specific language)
    - `文脈自由文法(context-free grammar) `を組み立てる手順に一対一に対応しており、文脈自由文法を理解しやすくしてくれる.
      - `V → w`:
        - `V`: `非終端記号`
        - `w`: `終端記号`と`非終端記号`の（0 個を含む）任意個の並び
      - 「文脈自由」という用語は前後関係に依存せずに`非終端記号 V を w に置換できる`、という所から来ている
  - 記号の優先順位:
    - `~` `^^` `|` の順. この順序のため、括弧を使わずに生成規則を書くことができる(SP643)
  - `セミコロン推論`を無効にするトリック:
    - 一行目が`中置演算子で終わっている`場合や、２行目が括弧や角括弧で囲まれている場合は推論'されない'. (SP644)
    - 一行目の推論を防ぐために、全体を括弧で囲み、二行目以降の推論を防ぐために、文末を中置演算子で終えて改行する.
