import cats.data.OptionT
import cats.instances.list._ // for Monad
import cats.syntax.applicative._ // for pure
import cats.instances.either._

import concurrent.{Future, Await, duration, ExecutionContext}, duration._,
ExecutionContext.Implicits.global
import cats.data.{EitherT, OptionT}
import cats.instances.future._ // for Monad

import cats.data.Writer

object MonadTransformerExpr {
  // Outer monad, which is OptionT is the transformer for the inner monad.
  type ListOption[A] = OptionT[List, A]
  val result1: ListOption[Int] = OptionT(List(Option(10)))
  val result2: ListOption[Int] = 32.pure[ListOption]

  /*
    result1.flatMap { (x: Int) =>
      result2.map { (y: Int) =>
        x + y
      }
    }
   */

  // wrap Either around Option: want Either(ErrorOr) with Optional value.
  // Alias Either to a type constructor with one parameter
  type ErrorOr[A] = Either[String, A]
  // Build our final monad stack using OptionT
  // ErrorOrOption is a Monad.
  type ErrorOrOption[A] = OptionT[ErrorOr, A] // no need to pass A to ErrorOr

  val a = 10.pure[ErrorOrOption]
  val b = 32.pure[ErrorOrOption]

  val c = a.flatMap(x => b.map(y => x + y))

  /*
  case class EitherT[F[_], E, A](stack: F[Either[E, A]]) {
    //
  }
  F[_]: is the outer monad in the stack(Either is the inner) (just like OptionT[ErrorOr, A])
  E is the error type for Either
  A is the result type for Either
   */

  type FutureEither[A] = EitherT[Future, String, A]
  type FutureEitherOption[A] = OptionT[FutureEither, A]

  // Our mammoth stack now composes three monads and our map and flatMap methods
  // cut through three layers of abstraction.
  val futureEitherOr: FutureEitherOption[Int] =
    for {
      a <- 10.pure[FutureEitherOption]
      b <- 32.pure[FutureEitherOption]
    } yield a + b

  // ? There is no apply method for FutureEitherOption or needs to create
  /*
  val errorStack1 = OptionT[ErrorOr, Int](Right(Some(10)))
  // errorStack1: OptionT[ErrorOr, Int] = OptionT(Right(Some(10)))

  FutureEitherOption[Int](Right(Some(10))) // no
   */
}

// Usage patterns
object UsagePatterns {
  // The “super stack” approach starts to fail in larger, more heterogeneous code bases
  // where different stacks make sense in different contexts
  sealed abstract class HttpError
  final case class NotFound(item: String) extends HttpError
  final case class BadRequest(msg: String) extends HttpError

  type FutureEither[A] = EitherT[Future, HttpError, A]

  // another pattern uses monad transformers as local "glue code"
  type Logged[A] = Writer[List[String], A]
  def parseNumber(str: String): Logged[Option[Int]] =
    util.Try(str.toInt).toOption match {
      case Some(num) => Writer(List(s" Read $str"), Some(num))
      case None      => Writer(List(s"Failed on $str"), None)
    }

  def addAll(a: String, b: String, c: String): Logged[Option[Int]] = {
    import cats.data.OptionT
    // for comprehension ends when any of these return 'None'
    val result = for {
      a <- OptionT(parseNumber(a)) // infers Int type as result.
      b <- OptionT(parseNumber(b))
      c <- OptionT(parseNumber(c))
    } yield a + b + c

    result.value
  }
  /*
  addAll("1", "2", "3")
   */
}

object MonadTransformerRollOut {
  type Response[A] = EitherT[Future, String, A]
  val powerLevels = Map(
    "Jazz" -> 6,
    "Bumblebee" -> 8,
    "Hot Rod" -> 10
  )

  def powerLevel(ally: String): Response[Int] =
    powerLevels.get(ally) match {
      case Some(a) => EitherT.right(Future(a))
      case None    => EitherT.left(Future(s"$ally unreachable"))
    }

  // Either is right biased, thus one Left value occurrence
  // causes an entire failure. Check out 4.4 Either section.
  def canSpecialMove(ally1: String, ally2: String): Response[Boolean] =
    for {
      power1 <- powerLevel(ally1)
      power2 <- powerLevel(ally2)
    } yield (power1 + power2) > 15

  def tacticalReport(ally1: String, ally2: String): String = {
    val stack = canSpecialMove(ally1, ally2).value
    Await.result(stack, 1.second) match {
      case Left(msg)    => s"Comms error: $msg"
      case Right(true)  => s"ally1 and ally2 are ready to roll out!"
      case Right(false) => s"ally2 and ally2 need a recharge."
    }
  }
}
