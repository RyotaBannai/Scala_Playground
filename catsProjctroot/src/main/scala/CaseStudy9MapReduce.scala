import cats.{Monoid, Monad}
import cats.syntax.semigroup._
import cats.instances.int._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import cats.syntax.parallel._

object CaseStudy9MapReduce {

  /** Single-threaded map-reduce function.
    * Maps `func` over `values` and reduces using a `Monoid[B]`
    */
  def foldMap[A, B: Monoid](values: Vector[A])(func: A => B): B =
    values.map(func).foldLeft(Monoid[B].empty)(_ |+| _)

  def foldMapShortened[A, B: Monoid](values: Vector[A])(func: A => B): B =
    values.foldLeft(Monoid[B].empty)(_ |+| func(_))
  /*
    import cats.instances.int._
    foldMap(Vector(1,2,3))(identity)
   */

  def parallelFoldMap[A, B: Monoid](
      values: Vector[A]
  )(func: A => B): Future[B] = {
    val numCores = Runtime.getRuntime.availableProcessors
    val groupSize = (1.0 * values.size / numCores).ceil.toInt
    val batches: Iterator[Vector[A]] = values.grouped(groupSize) // 250000

    // failed one.
    // batches.parTupled.foldLeft(Monad[Future].pure(0))((accum, i) =>
    //   Monoid[Future[Int]].combine(accum, i)
    // )

    // Create a future to foldMap each group:
    // by using map this runs parallel. if you desires to run in sequence, then use flatMap
    // https://stackoverflow.com/questions/25056957/why-future-sequence-executes-my-futures-in-parallel-rather-than-in-series
    val futures: Iterator[Future[B]] =
      batches map { batch =>
        Future {
          batch.foldLeft(Monoid[B].empty)(_ |+| func(_))
        }
      }

    // reuse already defined `foldMap`
    val betterFutures: Iterator[Future[B]] =
      batches.map(batch => Future(foldMap(batch)(func)))

    Future.sequence(futures) map { iterable =>
      iterable.foldLeft(Monoid[B].empty)(_ |+| _)
    }
  }

  val result: Future[Int] = parallelFoldMap((1 to 1000000).toVector)(identity)
  // Await.result(result, 1.second)
}
