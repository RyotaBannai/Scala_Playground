// n, d class parameters.
// scala コンパイラは、これらの２つのクラスパラメタを集めて、
// 同じ２つのパラメタをとる基本コンストラクタ(primary constructors)を生成する(SP113)
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

object Rational {}
