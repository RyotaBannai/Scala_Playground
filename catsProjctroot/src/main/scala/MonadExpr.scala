import cats.Monad
import cats.instances.option._ // for Monad
import cats.instances.list._ // for Monad

import cats.syntax.functor._ // for map
import cats.syntax.flatMap._ // for flatMap

import cats.Id

object MonadExpr {
  val opt1 = Monad[Option].pure(3)
  val opt2 = Monad[Option].flatMap(opt1)(a => Some(a + 2))
  val opt3 = Monad[Option].map(opt2)(a => 100 * a)

  val list1 = Monad[List].pure(3)
  val list2 = Monad[List].flatMap(List(1, 2, 3))(a => List(a, a * 10))
  val list3 = Monad[List].map(list2)(a => a + 123)

  def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] = for {
    x <- a
    y <- b
  } yield x * x + y * y

  object MethodsForId {
    def pure[A](value: A): Id[A] = value
    def map[A, B](initial: Id[A])(func: A => B): Id[B] = func(initial)
    def flatMap[A, B](initial: Id[A])(func: A => Id[B]): Id[B] = func(initial)
  }
}
