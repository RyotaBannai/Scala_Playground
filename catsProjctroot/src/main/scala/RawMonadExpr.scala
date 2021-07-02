import util.Try

object RawMonadExpr {
  def parseInt(str: String): Option[Int] = Try(str.toInt).toOption
  def divide(a: Int, b: Int): Option[Int] = if (b == 0) None else Some(a / b)
  // Each of these methods may "fail" by returning None.
  // The flapMap methods allows us to ignore this when we sequence operations:

  def stringDivideBy(aStr: String, bStr: String): Option[Int] =
    parseInt(aStr).flatMap { aNum =>
      parseInt(bStr).flatMap { bNum => divide(aNum, bNum) }
    }

  trait Monad[F[_]] {
    def pure[A](a: A): F[A]
    def flatMap[A, B](value: F[A])(func: A => F[B]): F[B]
    def map[A, B](value: F[A])(func: A => B): F[B] =
      flatMap(value)(a => pure(func(a)))
  }
}
