import cats.syntax.either._ // for catchOnly

import cats.Semigroupal
import cats.instances.option._ // for Semigroupal
import cats.syntax.apply._ // for tupled and mapN

import cats.Monoid
import cats.instances.int._ // for Monoid
import cats.instances.invariant._ // for Semigroupal
import cats.instances.list._ // for Monoid
import cats.instances.string._ // for Monoid

import cats.syntax.semigroup._ // for |+|

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
  // 2 arities.
  val add: (Int, Int) => Int = (a, b) => a + b
  val maybeInt = (Option(1), Option(2)).mapN(add)

  final case class Cat(name: String, born: Int, color: String)
  val maybeCat =
    (Option("Garfield"), Option(1999), Option("Orange & Black")).mapN(Cat.apply)
  /*
  Internally mapN uses the Semigroupal to extract the values from the Option and the Functor to apply the values to the function.
   */

}
object FancyFunctors {
  final case class Cat(
      name: String,
      yearOfBirth: Int,
      favoriteFoods: List[String]
  )

  val tupleToCat: (String, Int, List[String]) => Cat = Cat.apply _
  val catToTuple: Cat => (String, Int, List[String]) = cat =>
    (cat.name, cat.yearOfBirth, cat.favoriteFoods)

  implicit val catMonoid: Monoid[Cat] =
    (Monoid[String], Monoid[Int], Monoid[List[String]])
      .imapN(tupleToCat)(catToTuple)

  val garfield = Cat("Garfield", 1978, List("Lasagne"))
  val heathcliff = Cat("Heathcliff", 1988, List("Junk Food"))
  val res = garfield |+| heathcliff
}
