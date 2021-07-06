import cats.syntax.either._ // for catchOnly

import cats.Semigroupal
import cats.instances.option._ // for Semigroupal
import cats.syntax.apply._ // for tupled and mapN

object FailedPatterns {
  def parseInt(str: String): Either[String, Int] = Either
    .catchOnly[NumberFormatException](str.toInt)
    .leftMap(_ => s"Couldn't read $str")

  def failToInformAllOfThem = for {
    a <- parseInt("a")
    b <- parseInt("b")
    c <- parseInt("c")
  } yield (a + b + c)
  // scala.util.Either[String,Int] = Left(Couldn't read a)
}

object SemigroupExpr {
  Semigroupal[Option].product(Some(123), Some("abc"))
  // Option[(Int, String)] = Some((123,abc))
  // if both parameters are instances of Some, we end up with a tuple of hte values within.
  // if either parameters to None, the entire result is None.

  // mapN
  final case class Cat(name: String, born: Int, color: String)
  val maybeCat =
    (Option("Garfield"), Option(1999), Option("Orange & Black")).mapN(Cat.apply)

  // 2 arities.
  val add: (Int, Int) => Int = (a, b) => a + b
  val maybeInt = (Option(1), Option(2)).mapN(add)
}
