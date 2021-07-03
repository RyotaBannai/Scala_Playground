import cats.data.Writer
import cats.syntax.writer._
import cats.instances.vector._ // for Monoid
import cats.syntax.applicative._ // for pure

import concurrent._, ExecutionContext.Implicits._, duration._

object WriterMonadExpr {
  type Logged[A] = Writer[Vector[String], A]

  //  Composing and Transforming Writers
  val writer1 = for {
    a <- 10.pure[Logged]
    _ <- Vector("a", "b", "c").tell
    b <- 32.writer(Vector("x", "y", "z"))
  } yield a + b

  val writer2 = writer1.mapWritten(_.map(_.toUpperCase))
}

object Slowly {
  type Logged[A] = Writer[Vector[String], A]

  def slowly[A](body: => A) = try body
  finally Thread.sleep(100)

  def factorial(logged: Logged[Int]): Logged[Int] = {
    val n = logged.value
    slowly {
      n match {
        case 0 => Writer(logged.written :+ s"fact $n 1", 1)
        case _ =>
          val writer = factorial(logged.map(_ - 1));
          val re = n * writer.value
          Writer(writer.written :+ s"fact $n $re", re)
      }
    }
  }
  /*
  runParallel()(0).run
   */

  def runParallel() =
    Await.result(
      Future.sequence(
        Vector(
          Future(factorial(Writer(Vector("Start Future1"), 10))),
          Future(factorial(Writer(Vector("Start Future2"), 5)))
        )
      ),
      5.seconds
    )
}

object ImprovedSlowly {
  type Logged[A] = Writer[Vector[String], A]

  def slowly[A](body: => A) = try body
  finally Thread.sleep(100)

  def factorial(n: Int): Logged[Int] = {
    for {
      ans <-
        if (n == 0) {
          1.pure[Logged]
        } else {
          slowly(factorial(n - 1).map(_ * n))
        }
      _ <- Vector(s"fact $n $ans").tell
    } yield ans
  }
  /*
   val(log, res) = factorial(5).run
   */

  def runParallel() =
    Await.result(
      Future
        .sequence(
          Vector(
            Future(factorial(5)),
            Future(factorial(5))
          )
        )
        .map(_.map(_.written)),
      // Vector@map/ Writer@map(applies written on log part)
      5.seconds
    )
  /*
    pprint.pprintln(runParallel(), width = 5)
   */
}
