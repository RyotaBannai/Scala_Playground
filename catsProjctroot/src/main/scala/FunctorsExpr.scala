import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import cats.instances.function._
import cats.syntax.functor._
import cats.Functor
import cats.instances.list._ // for Functor
import cats.instances.option._ // for Functor

object FunctorsExpr {
  def run(): Unit = {
    val future: Future[String] =
      Future(123)
        .map(n => n + 1)
        .map(n => n * 2)
        .map(n => s"${n}!")

    Await.result(future, 1.second)
  }

  val func1: Int => Double = (x: Int) => x.toDouble
  val func2: Double => Double = (y: Double) => y * 2

  /** “mapping” over a Function1 is function composition
    */
  // composition using map
  val r1 = (func1 map func2)(1)
  // composition using andThen
  val r2 = (func1 andThen func2)(1)
  // composition written out by hand.
  val r3 = func2(func1(1))
}

object UseFunctor {
  val list1 = List(1, 2, 3)
  val list2 = Functor[List].map(list1)(_ * 2)

  val op1 = Option(123)
  val op2 = Functor[Option].map(op1)(_.toString) // Option(String) = Some(123)

  val func1 = (x: Int) => x + 1
  val liftedFunc = Functor[Option].lift(func1)
  liftedFunc(Option(1))

  Functor[List].as(list1, "As") // List[String] = List("As","As","As")

  def doMath[F[_]](start: F[Int])(implicit functor: Functor[F]): F[Int] =
    start.map(n => n + 1 * 2)
  doMath(Option(20))
  doMath(List(1, 2, 3))

  /*
  Converts source to cats Functor type and applies cats map.
  The map methods of FunctorOps requires an implicit Functor as a parameter:
    This means this code will only compile if we have a Functor for F in scope.

  map method in cats.syntax.functor. Here’s a simplified version of the code:

  implicit class FunctorOps[F[_], A](src: F[A]) {
    def map[B](func: A => B)(implicit functor: Functor[F]): F[B] =
      functor.map(src)(func)
  }
   */

  // Custom Type. cats has cats.instances.option
  implicit val optionFunctor: Functor[Option] = new Functor[Option] {
    def map[A, B](value: Option[A])(func: A => B): Option[B] = value.map(func)
  }
}
