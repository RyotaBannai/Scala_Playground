### scala の型

- 型宣言必要ない(代入したときに**型推論**) `var | val` **var は後から変更可、val は変更不可**. 基本的には val を使用.
- `var`に初めに数値を入れると、後から文字列を代入できない（これは typescript と同じ様な型制限）
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
- break や continue などの言語機能`はない.
- return 式はメソッドから、途中で脱出してメソッドの呼び出し元に返り値を返すための制御構文
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

### クラス

- `private`を付けるとそのクラス内だけから、 `protected` を付けると派生クラスからのみアクセスできるメソッドになる。`private[this]` をつけると、同じオブジェクトからのみアクセス可能になります。また、 `private[パッケージ名]` を付けると同一パッケージに所属しているものからのみ、 `protected[パッケージ名]` をつけると、派生クラスに加えて追加で同じパッケージに所属しているもの全てからアクセスできるようになります。 private も protected も付けない場合、そのメソッドは`public`とみなされます。

#### 複数の引数リストを持つメソッド

- 複数の引数リストを持つメソッドには、Scala の糖衣構文と組み合わせて流暢な API を作ったり、後述する implicit parameter のために必要になったり、型推論を補助するために使われたりといった用途がある
- 継承には 2 つの目的がある:

1.  継承によりスーパークラスの実装をサブクラスでも使うことで実装を再利用すること
2.  複数のサブクラスが共通のスーパークラスのインタフェースを継承することで処理を共通化すること

- 実装の継承には複数の継承によりメソッドやフィールドの名前が衝突する場合の振舞いなどに問題があることが知られており、Java では実装継承が 1 つだけに限定されている。Java 8 ではインタフェースにデフォルトの実装を持たせられるようになりましたが、変数は持たせられないという制約がある。Scala では**トレイト**という仕組みで複数の実装の継承を実現している。

### オブジェクト

- Scala では、**全ての値がオブジェクト**です。また、**全てのメソッドは何らかのオブジェクトに所属しています**。そのため、Java のようにクラスに属する static フィールドや static メソッドといったものを作成することができません。その代わりに、object キーワードによって、同じ名前のシングルトンオブジェクトを現在の名前空間の下に 1 つ定義することができます。object キーワードによって定義したシングルトンオブジェクトには、そのオブジェクト固有のメソッドやフィールドを定義することができます。
- object 構文の主な用途:
  1. ユーティリティメソッドやグローバルな状態の置き場所（Java で言う static メソッドやフィールド）
  2. 同名クラスのオブジェクトのファクトリメソッド
- extends でクラスを継承、 with でトレイトを mix-in 可能になっているのは、_オブジェクト名を既存のクラスのサブクラス等として振る舞わせたい場合があるからです_。Scala の標準ライブラリでは、 `Nil` という object がありますが、これは `List の一種として振る舞わせたいため、 List を継承しています`。一方、 object がトレイトを mix-in する事はあまり多くありませんが、クラスやトレイトとの構文の互換性のためにそうなっていると思われます。
- **ケースクラス**: それをつけたクラスのプライマリコンストラクタ全てのフィールドを公開し、equals()・hashCode()・toString()などのオブジェクトの基本的なメソッドをオーバーライドしたクラスを生成し、また、そのクラスのインスタンスを生成するための**ファクトリメソッド**を生成するもの

```scala
Point(1, 2).equals(Point(1, 2))
```

- クラスと同じファイル内、同じ名前で定義された**シングルトンオブジェクト**は、**コンパニオンオブジェクト**と呼ばれる
- コンパニオンオブジェクトでも、`private[this]`（そのオブジェクト内からのみアクセス可能）なクラスのメンバーに対してはアクセスできません。単に`private`とした場合、コンパニオンオブジェクトからアクセスできるようになる。
- コンパニオンオブジェクトを使ったコードを REPL で試す場合は、REPL の:paste コマンドを使って、クラスとコンパニオンオブジェクトを一緒にペーストするようにしてください。クラスとコンパニオンオブジェクトは同一ファイル中に置かれていなければならないのですが、REPL で両者を別々に入力した場合、コンパニオン関係を REPL が正しく認識できないのです.

### トレイト

- 私たちの作るプログラムはしばしば数万行、多くなると数十万行やそれ以上に及ぶことがあります。その全てを一度に把握することは難しいので、プログラムを意味のあるわかりやすい単位で分割しなければなりません。さらに、その分割された部品はなるべく柔軟に組み立てられ、大きなプログラムを作れると良いでしょう。
- 複数のトレイトを 1 つのクラスやトレイトにミックスインできる
- だが、トレイトは直接インスタンス可できない

```scala
trait TraitA
object ObjectA {
  val a = new TraitA // trait is abstract, can't be instanced.
}
```

- これは**トレイトが単体で使われることをそもそも想定していないため**の制限。**トレイトを使うときは、通常、それを継承したクラスを作成**する。
- `new Trait{}` は**Trait を継承した無名のクラス**を作って、そのインスタンスを生成する構文なので、トレイトそのものをインスタンス化できているわけではない。
- トレイトはクラスと違って、パラメータ(コンストラクタの引数)を取ることができない。

```scala
trait TraitA(name: String) // error

trait TraitB{
  val name: String // sub typing （構造的サブタイピング（≈ダックタイピング）） // nominal typingでは同じクラウかサブクラスしか型宣 言できない https://medium.com/@thejameskyle/type-systems-structural-vs-nominal-typing-explained-56511dd969f4
  def printName(): Unit = println(name)
}
class ClassB (val name: String) extends TraitB
object ClassB {
  val a = new ClassB("dnn")
  val a2 = TraitB{ val name = "dmo"} // nameを上書きする様な実装を与えてることもできる
}

```

#### トレイトを使う時は菱形継承問題に気を付ける

- Scala では override 指定なしの場合メソッド定義の衝突はエラー

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

- Scala のトレイトの線形化:トレイトがミックスインされた順番をトレイトの継承順番と見做すこと
- 線形化機能を使うには、ミックスインする全てのトレイトのメソッドを`override`をして継承させる。`最後に読み込まれたメソッド`が使用される。

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

### 落とし穴：トレイトの初期化順序とトレイとの val の初期化順序の回避方法

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
- 時世代 Scala コンパイラである Dotty ではトレイとがパラメータを取ることができる.

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
