import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import cats.instances.function._
import cats.syntax.functor._

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
