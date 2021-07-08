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

import cats.instances.future._ // for Semigroupal
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

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

object SemigroupalAppliedToDifferentTypes {
  // The two Futures start executing the moment we create them,
  // so they are already calculating results by the time we call product.
  val futurePair = Semigroupal[Future].product(Future("Hello"), Future(123))

  // bug on repl: https://stackoverflow.com/questions/45592069/cats-future-monad-giving-runtime-exception
  // val re = Await.result(futurePair, 2.second)
}

object Parallel {
  import cats.Semigroupal
  import cats.instances.either._ // ofr Semigrooupal
  import cats.syntax.apply._ // for tupled and mapN
  import cats.instances.vector._
  import cats.syntax.parallel._ // for parTupled

  type ErrorOr[A] = Either[Vector[String], A]
  val error1: ErrorOr[Int] = Left(Vector("Error 1"))
  val error2: ErrorOr[Int] = Left(Vector("Error 2"))

  val failFast = (error1, error2).tupled
  val notFailFast = (error1, error2).parTupled

  // parMapN
  val success1: ErrorOr[Int] = Right(1)
  val success2: ErrorOr[Int] = Right(2)
  val addTwo = (x: Int, y: Int) => x + y

  val res1 = (error1, error2).parMapN(addTwo) // Left(Vector(Error 1, Error 2))
  val res2 = (success1, success2).parMapN(addTwo) // Right(3)
  val res3 = (success1, error1).parMapN(addTwo) // Left(Vector(Error 1))

  val res4 = (List(1, 2), List(3, 4)).tupled
  // List[(Int, Int)] = List((1,3), (1,4), (2,3), (2,4))
  val res5 = (List(1, 2), List(3, 4)).parTupled
  // List[(Int, Int)] = List((1,3), (2,4))

  /* The definition of Parallel
    trait Parallel[M[_]] {
      type F[_]

      def applicative: Applicative[F]
      def monad: Monad[M]
      def parallel: ~>[M, F]
    }

    if there is a Parallel instance for some type constructor M then:

    - there must be a Monad instance for M;
    - there is a related type constructor F that has an Applicative instance; and
    - we can convert M to F.
   */
  import cats.arrow.FunctionK

  object optionToList extends FunctionK[Option, List] {
    def apply[A](fa: Option[A]): List[A] =
      fa match {
        case None    => List.empty[A]
        case Some(a) => List(a)
      }
  }

  /*
  optionToList(Some(1))
  optionToList(None)
   */
}
