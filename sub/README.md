### scalaの型
- 型宣言必要ない(代入したときに**型推論**) `var | val` **varは後から変更可、valは変更不可**. 基本的にはvalを使用.
- `var`に初めに数値を入れると、後から文字列を代入できない（これはtypescriptと同じ様な型制限）
- `val x: Int = 3 * 3` 型宣言の場合。
### sbtを使う
- sbtでmainがある場所で`sbt`と打つ
- `run`コマンドを打つ。mainが複数ある場合は選択するプロントが表示される。
- `sbt console`でインタラクティブモードに入る. ここで作ったクラスとかファイルを全て読み込めるため、クラスの動作確認ができる.
- src/User.scala　に記述した物を実行
```scala
val u = new User("dwango", 13)
User.printUser(u)
```
### 制御構文
- **Unit 型**はJavaでは`void`に相当するもので、返すべき値がない時に使われ、唯一の値()を持つ.
- elseが省略可能で、その場合は、**Unit 型**の値 () が補われたのと同じ値が返る.
- break やcontinueなどの言語機能`はない.
- return 式はメソッドから、途中で脱出してメソッドの呼び出し元に返り値を返すための制御構文
- return 式はメソッドから、途中で脱出してメソッドの呼び出し元に返り値を返すための制御構文である.
- `1 to 10 `は1から10まで（10を含む）の範囲で、 `1 until 10` は1から10まで（10を含まない）の範囲
- for + yield で `for-comprehension`
- Scalaのパターンマッチがいわゆる**フォールスルー（fall through）** の動作をしない
- パターンマッチの後にガード式（Boolean型でないといけない）が使える
```scala
mylist match{
  case List("A", b, c) if b != "B" =>
    println("Hit!")
  case _ =>
    println("Not hit!")
    }
```
- @の後に続くパターンにマッチする式を @ の前の変数に束縛する. as パターンはパターンが複雑なときにパターンの一部だけを切り取りたい時に便利.
- ただし | を使ったパターンマッチの場合は値を取り出すことができない. ワイルドカード_を使う.
- `"A" :: b :: c :: _` のように、リストの要素の間にパターン名（::）が現れるようなものを**中置パターン**と呼ぶ.
- `AnyRef`型は、Javaの`Object`型に相当する型で、**あらゆる参照型の値**をAnyRef型の変数に格納することができる.
- 型でマッチした値は、その型にキャストしたのと同じように扱うことができる. しばしばScalaではキャストの代わりにパターンマッチが用いられるので覚えておくとよい.
- JVMの制約による型のパターンマッチの落とし穴: Scalaを実行するJVMの制約により、型変数を使った場合、正しくパターンマッチが行われない。
```scala
val obj: Any = List("a")
obj match {
  case v: List[Int]    => println("List[Int]")
  case v: List[String] => println("List[String]")
}
```
- 型としては`List[Int]`と`List[String]`は違う型なのですが、パターンマッチではこれを区別できない。最初の2つの警告の意味はScalaコンパイラの **「型消去」** という動作により`List[Int]`のIntの部分が消されてしまうのでチェックされないということ。結果的に2つのパターンは区別できないものになり、パターンマッチは上から順番に実行されていくので、2番目のパターンは到達しないコードになる。3番目の警告はこれを意味している。型変数を含む型のパターンマッチは、以下のようにワイルドカードパターンを使うと良い。
```scala
obj match {
  case v: List[_] => println("List[_]")
}
```
### クラス
- `private`を付けるとそのクラス内だけから、 `protected` を付けると派生クラスからのみアクセスできるメソッドになる。`private[this]` をつけると、同じオブジェクトからのみアクセス可能になります。また、 `private[パッケージ名]` を付けると同一パッケージに所属しているものからのみ、 `protected[パッケージ名]` をつけると、派生クラスに加えて追加で同じパッケージに所属しているもの全てからアクセスできるようになります。 private も protected も付けない場合、そのメソッドは`public`とみなされます。
#### 複数の引数リストを持つメソッド
- 複数の引数リストを持つメソッドには、Scalaの糖衣構文と組み合わせて流暢なAPIを作ったり、後述するimplicit parameterのために必要になったり、型推論を補助するために使われたりといった用途がある
- 継承には2つの目的がある:
 1. 継承によりスーパークラスの実装をサブクラスでも使うことで実装を再利用すること
 2. 複数のサブクラスが共通のスーパークラスのインタフェースを継承することで処理を共通化すること
- 実装の継承には複数の継承によりメソッドやフィールドの名前が衝突する場合の振舞いなどに問題があることが知られており、Javaでは実装継承が1つだけに限定されている。Java 8ではインタフェースにデフォルトの実装を持たせられるようになりましたが、変数は持たせられないという制約がある。Scalaでは**トレイト**という仕組みで複数の実装の継承を実現している。
### オブジェクト
- Scalaでは、**全ての値がオブジェクト**です。また、**全てのメソッドは何らかのオブジェクトに所属しています**。そのため、Javaのようにクラスに属するstaticフィールドやstaticメソッドといったものを作成することができません。その代わりに、objectキーワードによって、同じ名前のシングルトンオブジェクトを現在の名前空間の下に1つ定義することができます。objectキーワードによって定義したシングルトンオブジェクトには、そのオブジェクト固有のメソッドやフィールドを定義することができます。
- object構文の主な用途:
  1. ユーティリティメソッドやグローバルな状態の置き場所（Javaで言うstaticメソッドやフィールド）
  2. 同名クラスのオブジェクトのファクトリメソッド
- extends でクラスを継承、 with でトレイトをmix-in 可能になっているのは、_オブジェクト名を既存のクラスのサブクラス等として振る舞わせたい場合があるからです_。Scala の標準ライブラリでは、 `Nil` という object がありますが、これは `List の一種として振る舞わせたいため、 List を継承しています`。一方、 object がトレイトをmix-inする事はあまり多くありませんが、クラスやトレイトとの構文の互換性のためにそうなっていると思われます。
- **ケースクラス**: それをつけたクラスのプライマリコンストラクタ全てのフィールドを公開し、equals()・hashCode()・toString()などのオブジェクトの基本的なメソッドをオーバーライドしたクラスを生成し、また、そのクラスのインスタンスを生成するための**ファクトリメソッド**を生成するもの
```scala
Point(1, 2).equals(Point(1, 2))
```
- クラスと同じファイル内、同じ名前で定義された**シングルトンオブジェクト**は、**コンパニオンオブジェクト**と呼ばれる
- コンパニオンオブジェクトでも、`private[this]`（そのオブジェクト内からのみアクセス可能）なクラスのメンバーに対してはアクセスできません。単に`private`とした場合、コンパニオンオブジェクトからアクセスできるようになる。
- コンパニオンオブジェクトを使ったコードをREPLで試す場合は、REPLの:pasteコマンドを使って、クラスとコンパニオンオブジェクトを一緒にペーストするようにしてください。クラスとコンパニオンオブジェクトは同一ファイル中に置かれていなければならないのですが、REPLで両者を別々に入力した場合、コンパニオン関係をREPLが正しく認識できないのです.
### トレイト
- 私たちの作るプログラムはしばしば数万行、多くなると数十万行やそれ以上に及ぶことがあります。その全てを一度に把握することは難しいので、プログラムを意味のあるわかりやすい単位で分割しなければなりません。さらに、その分割された部品はなるべく柔軟に組み立てられ、大きなプログラムを作れると良いでしょう。
- 複数のトレイトを1つのクラスやトレイトにミックスインできる
- だが、トレイトは直接インスタンス可できない
```scala
trait TraitA
object ObjectA {
  val a = new TraitA // trait is abstract, can't be instanced.
}
```
- これは**トレイトが単体で使われることをそもそも想定していないため**の制限。**トレイトを使うときは、通常、それを継承したクラスを作成**する。
- `new Trait{}` は**Traitを継承した無名のクラス**を作って、そのインスタンスを生成する構文なので、トレイトそのものをインスタンス化できているわけではない。
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
- Scalaではoverride指定なしの場合メソッド定義の衝突はエラー
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
- Scalaのトレイトの線形化:トレイトがミックスインされた順番をトレイトの継承順番と見做すこと
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
- `super`で親クラスを呼ぶことで全てのoverrideしたメソッドを呼び出すことができる-> 線形化によるトレイトの積み重ねの処理をScalaの用語では積み重ね可能なトレイト（Stackable Trait）と呼ぶことがある。
### 落とし穴：トレイトの初期化順序とトレイとのvalの初期化順序の回避方法
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
- lazy は _barの初期化が実際に使われるまで遅延される_
```scala
trait B extends A {
  lazy val bar = foo + "World"
  // def var 
}
```
- lazy valはvalに比べて若干処理が重く、複雑な呼び出しでデッドロックが発生する場合がある。 valのかわりにdefを使うと毎回値を計算してしまうという問題がある。
    2. 事前定義（Early Definitions）: フィールドの初期化をスーパークラスより先におこなう方法
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
- この事前定義は利用側からの回避方法は、この例の場合はトレイトBのほうに問題がある（普通に使うと初期化の問題が発生してしまう）ので、トレイトBのほうを修正したほうがいい。
- 時世代ScalaコンパイラであるDottyではトレイとがパラメータを取ることができる.
