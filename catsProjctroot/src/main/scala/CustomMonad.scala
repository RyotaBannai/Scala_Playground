import cats.Monad
import annotation.tailrec
import cats.syntax.flatMap._ // for flatMap
import cats.instances.option._ // monad instance for option
import cats.syntax.functor._ // for map
import cats.syntax.monad._ // for iterateWhileM

object CustomMonad {
  def retry[F[_]: Monad, A](start: A)(f: A => F[A]): F[A] =
    f(start).flatMap(a => retry(a)(f))

  /*
    import cats.instances.option._
    retry(10)(a => if (a == 0) None else Some(a - 1))
   */

  def retryM[F[_]: Monad, A](start: A)(f: A => F[A]): F[A] =
    start.iterateWhileM(f)(a => true)

  def retryTailRecM[F[_]: Monad, A](start: A)(f: A => F[A]): F[A] =
    Monad[F].tailRecM(start) { a => f(a).map(a2 => Left(a2)) }
}
