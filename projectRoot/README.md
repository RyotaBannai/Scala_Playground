### scala の型

- `var と val`: 型宣言が必要ない(型推論). 基本的には val を使用
  - var は変更可(reuseable)
  - val は変更不可(final)
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
- Scala のパターンマッチがいわゆる**フォールスルー（fall through）** の動作をしない
- パターンマッチの後にガード式（Boolean 型でないといけない）が使える

```scala
mylist match{
  case List("A", b, c) if b != "B" =>
    println("Hit!")
  case _ =>
    println("Not hit!")
    }
```

- @の後に続くパターンにマッチする式を @ の前の変数に束縛する. as パターンはパターンが複雑なときにパターンの一部だけを切り取りたい時に便利.
- ただし | を使ったパターンマッチの場合は値を取り出すことができない. ワイルドカード\_を使う.
- `"A" :: b :: c :: _` のように、リストの要素の間にパターン名（::）が現れるようなものを**中置パターン**と呼ぶ.
- `AnyRef`型は、Java の`Object`型に相当する型で、**あらゆる参照型の値**を AnyRef 型の変数に格納することができる.
- 型でマッチした値は、その型にキャストしたのと同じように扱うことができる. しばしば Scala ではキャストの代わりにパターンマッチが用いられるので覚えておくとよい.
- JVM の制約による型のパターンマッチの落とし穴: Scala を実行する JVM の制約により、型変数を使った場合、正しくパターンマッチが行われない。

```scala
val obj: Any = List("a")
obj match {
  case v: List[Int]    => println("List[Int]")
  case v: List[String] => println("List[String]")
}
```

- 型としては`List[Int]`と`List[String]`は違う型なのですが、パターンマッチではこれを区別できない。最初の 2 つの警告の意味は Scala コンパイラの **「型消去」** という動作により`List[Int]`の Int の部分が消されてしまうのでチェックされないということ。結果的に 2 つのパターンは区別できないものになり、パターンマッチは上から順番に実行されていくので、2 番目のパターンは到達しないコードになる。3 番目の警告はこれを意味している。型変数を含む型のパターンマッチは、以下のようにワイルドカードパターンを使うと良い。

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
- extends でクラスを継承、 with でトレイトを mix-in 可能になっているのは、`オブジェクト名を既存のクラスのサブクラス等として振る舞わせたい場合があるため`. Scala の標準ライブラリでは、 `Nil` という object があるが、これは `List の一種として振る舞わせたいため、 List を継承している`. 一方、 object がトレイトを mix-in する事はあまり多くないが、クラスやトレイトとの構文の互換性のためにそうなっていると思われる。
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

##### 共変（covariant）

- Scala では、何も指定しなかった型パラメータは通常は**非変（invariant)**
- 非変：型パラメータを持ったクラス G、型パラメータ A と B があったとき、A = B のときにのみに代入が許される

```scala
val g: G[A] = G[B]
```

つまり A=B である場合に限りコンパイルが通る。

```scala
val g: Array[Any] = new Array[Int](1) // error
val g: Array[Any] = new Array[Any](1) // ok
```

これを非変という.

- 共変: 型パラメータを持ったクラス G、型パラメータ A と B があったとき、B が A を継承しているときにのみというような代入が許される性質

```scala
val g: G[A] = G[B] // B が A を継承
```

```scala
class G[+A]
```

のように型パラメータの前に`+`を付けるとその型パラメータは（あるいはそのクラスは）共変になる。

```java
Object[] objects = new String[1];
objects[0] = 100;
```

java は共変であり、A=Object, B=String, G=Array と考えると、これでコンパイルが通る。しかし、このコードを実行すると例外 `java.lang.ArrayStoreException` が発生する。これは、objects に入っているのが実際には String の配列（String のみを要素として持つ）なのに、2 行目で int 型（ボクシング変換されて Integer 型）の値である 100 を渡そうとしていることによる。

- Scala では非変なのでコンパイルの時点でエラーになる。**静的型付き言語の型安全性**とは、コンパイル時により多くのプログラミングエラーを捕捉するものであるとするなら、配列の設計は Scala の方が Java より型安全であると言える.
- Scala で共変にした場合、変数を immutable にする様に設計する。（多くの場合問題があればコンパイル時にで救ってくれる）

```scala
class Pair1[+A, +B](val a:A, val b:B){
   override def toString(): String = "(" + a + "," + b + ")"
}
var pair: Pair[AnyRef, AnyRef] = new Pair[String, String]("foo","bar")
```

- `[B >: A]`is a lower type bound. It means that B is constrained to be **a supertype of A**.
- Similarly `[B <: A]` is an upper type bound, meaning that B is constrained to be **a subtype of A**.
- `Nothing`は全ての型のサブクラスであるような型を表現する。`Stack[+A]`で共変だとすると、`Stack[Nothing]`型の場合はどんな型の`Stack変数`にでも格納することができる。例えば`Stack[Nothing]`型である EmptyStack インスタンスがあれば、それは、`Stack[Int]`型の変数と`Stack[String]`型の変数の両方に代入することができる。これは`Nothing`は Int や String のサブクラスであり、共変の条件を満たすため。`[B >:A]` B=Int, A=Nothing.
- https://stackoverflow.com/questions/7759361/what-does-b-a-do-in-scala
- https://gist.github.com/RyotaBannai/2968fa3360d197c81dff4b4174facc38
- コンパイラは、Stack には A の任意のスーパータイプの値が入れられる可能性があることがわかるようになる。そして、型パラメータ E は共変ではないため、どこに出現しても構わない。このようにして、下限境界を利用して、型安全な Stack と共変性を両立することができる。（refer to `line 211`）

#### Contravariant 反変：共変の反対の概念

- `val g: G[A] = G[B]`とした時に A が B を継承している時に代入が可能。`class G[-A]`と表す。

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
  - `1 :: List(2,3)` は `List(1,2,3)` となる. これは `List(2,3).::(1)` と同様な処理になる.
  - `::` cons 演算子と呼ぶ
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
- ケースクラス：
  - ケースクラスのメリット:
    - ファクトリメソッドを自動で追加するため、`new ...` とする必要がない(SP265)
    - ケースクラスのパラメータリストないの全てのパラメーターに暗黙のうちに val プレフィックスをつけるため、パラーメータはフィールドとして管理される. (ケースクラスに限らない)
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
    - `部分関数(partial functions)` にもなりうる(SP283)
    - このような関数にサポートしていない値を適用すると、実行例外が起きる. そのため、その値をサポートしている部分関数が定義されているかどうかをチェックするためには、`PartialFunction` でラップしてあげる必要がある(SP283)
    - 部分関数より全関数を使うようにした方が良いが、部分関数が役に立つケースが２つある(SP284):
      1. 処理されない値は渡されないことがはっきりしている場合
      2. 部分間すうを使うことを想定しており、関数呼び出し前に isDefinedAt を必ずチェックするようなフレームワークを使っている場合（Akka アクターライブラリでは、例えば react の引数は部分関数である）
