// トレイトにパラメータを与えるという考え方は、抽象 val で実現できる(SP385)
// 抽象 val の定義は実行クラスで行われる
trait RationalTrait {
  // these are abstract vals
  val numberArg: Int
  val denomArg: Int

  private val g = gcd(numberArg.abs, denomArg.abs)
  val numer: Int = numberArg / g
  var denom: Int = denomArg / g

  require(denomArg != 0)

  override def toString = s"$numer / $denom";
  // find greatest common divider
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
}
/*
// Error
new RationalTrait {
  val numberArg = 1
  val denomArg = 2
}

// Ok: pre-initialized fields
new {
  val numberArg = 1
  val denomArg = 2
} with RationalTrait

// Ok: define twoThirds object
object twoThirds extends {
  val numberArg = 2
  val denomArg = 3
} with RationalTrait
 */

class Rational(n: Int, d: Int) extends Ordered[Rational] {
  // precondition: require は Predef に定義されている
  require(d != 0)

  def this(n: Int) = this(n, 1) // auxiliary constructor

  private val g = gcd(n.abs, d.abs)
  val numer: Int = n / g
  var denom: Int = d / g

  def add(that: Rational): Rational =
    new Rational(numer * that.denom + that.numer * denom, denom * that.denom);
  def subtract(that: Rational): Rational =
    new Rational(numer * that.denom - that.numer * denom, denom * that.denom);
  def multiply(that: Rational): Rational =
    new Rational(numer * that.numer, denom * that.denom);
  def divide(that: Rational): Rational =
    new Rational(numer * that.denom, denom * that.numer);

  def lessThan(that: Rational): Boolean =
    this.numer * that.denom < that.numer * this.denom
  def max(that: Rational) = if (lessThan(that)) that else this

  def +(that: Rational): Rational = add(that)
  def -(that: Rational): Rational = subtract(that)
  def *(that: Rational): Rational = multiply(that)
  def /(that: Rational): Rational = divide(that) // Rational Overloading.
  def /(that: Int): Rational = divide(new Rational(that)) // Int Overloading.

  override def toString = s"$numer / $denom";
  // find greatest common divider
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

  def compare(that: Rational) =
    (this.numer * that.denom) - (that.numer * this.denom)
}

object Rational {
  // 1 + new Rational(1) => Rational = 2 / 1
  implicit def intToRational(x: Int) = new Rational(1)
}
