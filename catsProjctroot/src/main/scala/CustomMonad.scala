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

object BranchingOutWithMonad {
  sealed trait Tree[+A]
  final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
  final case class Leaf[A](value: A) extends Tree[A]

  implicit val treeMonad: Monad[Tree] = new Monad[Tree] {
    def pure[A](value: A): Tree[A] = Leaf(value)

    def flatMap[A, B](tree: Tree[A])(func: A => Tree[B]): Tree[B] =
      tree match {
        case Branch(l, r) => Branch(flatMap(l)(func), flatMap(r)(func))
        case Leaf(value)  => func(value)
      }

    def tailRecM[A, B](a: A)(f: A => Tree[Either[A, B]]): Tree[B] =
      flatMap(f(a)) {
        case Left(value)  => tailRecM(value)(f)
        case Right(value) => Leaf(value)
      }
  }

  object Tree {
    def branch[A](left: Tree[A], right: Tree[A]): Tree[A] = Branch(left, right)
    def leaf[A](value: A): Tree[A] = Leaf(value)
  }

  /*
  import cats.syntax.flatMap._ // for flatMap
  import cats.syntax.functor._ // for map

  branch(leaf(100), leaf(200).flatMap(x => branch(leaf(x - 1), leaf(x + 1))))

  for {
    a <- branch(leaf(100), leaf(200))
    b <- branch(leaf(a - 10), leaf(a + 10))
    c <- branch(leaf(b - 1), leaf(b + 1))
  } yield c
   */
}
