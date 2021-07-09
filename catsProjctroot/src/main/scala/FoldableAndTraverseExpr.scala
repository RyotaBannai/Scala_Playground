object RawFoldMethods {
  // supply binary function
  def show[A](list: List[A]): String =
    list.foldLeft("nil")((accum, item) => s"$item then $accum")

  def combinator[A](list: List[A]): List[A] =
    list.foldLeft(List.empty[A])((accum, item) =>
      (item :: accum.reverse).reverse
    )

  def combinatorWithFoldRight[A](list: List[A]): List[A] =
    list.foldRight(List.empty[A])((item, accum) => item :: accum)

  def combinatorImproved[A](list: List[A]): List[A] =
    list.foldRight(List.empty[A])(_ :: _)

  object MethodsWithFoldRight {
    def map[A, B](list: List[A])(func: A => B): List[B] =
      list.foldRight(List.empty[B]) { (item, accum) => func(item) :: accum }
    // map(List(1,2,3))(_ * 2)

    def flatMap[A, B](list: List[A])(func: A => List[B]): List[B] =
      list.foldRight(List.empty[B]) { (item, accum) => func(item) ::: accum }
    // flatMap(List(1,2,3))(x => List(x, x*2, x*3))

    def filter[A, B](list: List[A])(func: A => Boolean): List[A] =
      list.foldRight(List.empty[A]) { (item, accum) =>
        if (func(item)) item :: accum else accum
      }
    // filter(List(1,2,3))(_ > 2)

    import scala.math.Numeric
    def sumWithNumeric[A](list: List[A])(implicit numeric: Numeric[A]): A =
      list.foldRight(numeric.zero)(numeric.plus)
    // sumWithNumeric(List(1,2,3))

    import cats.Monoid
    def sumWithMonoid[A](list: List[A])(implicit monoid: Monoid[A]): A =
      list.foldRight(monoid.empty)(monoid.combine)
    // import cats.instances.int._
    // sumWithMonoid(List(1,2,3))
  }
}

object FoldableExpr {
  import cats.Foldable
  import cats.instances.list._ // for Fordable
  import cats.instances.option._ // for Fordable

  val ints = List(1, 2, 3)
  val re1 = Foldable[List].foldLeft(ints, 0)(_ + _) // 6

  val maybeInt = Option(123)
  val re2 = Foldable[Option].foldLeft(maybeInt, 10)(_ * _) // 1230

  import cats.Eval

  def bigData = (1 to 1000000).to(LazyList)
  // why doesn't overflow. but did cause java.lang.OutOfMemoryError: Java heap space
  val maybeOverflow = bigData.foldRight(0L)(_ + _)

  import cats.instances.lazyList._ // for Foldable

  // OutOfMemoryError as well when data is too big.
  val eval: Eval[Long] =
    Foldable[LazyList].foldRight(bigData, Eval.now(0L)) { (num, eval) =>
      eval.map(_ + num)
    }
  // eval.value

  import cats.syntax.foldable._
  def sum[F[_]: Foldable](values: F[Int]): Int = values.foldLeft(0)(_ + _)
  // import cats.instances.list._ // for Fordable
  // sum(List(1,2,3)) // 6
}

object RawTraverseMethods {
  import scala.concurrent._
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  val hostnames = List(
    "alpha.example.com",
    "beta.example.com",
    "gamma.example.com"
  )
  // this method doesn't make any sense. just for demonstration
  def getUptime(hostname: String): Future[Int] = Future(hostname.length * 60)

  // old combinator.
  val allUptimes: Future[List[Int]] =
    hostnames.foldLeft(Future(List.empty[Int])) { (acc, host) =>
      val uptime = getUptime(host)
      for {
        acc <- acc // List[Int] <- Future[List[Int]]
        uptime <- uptime // Int <- Future[Int]
      } yield acc :+ uptime // List[Int]
    }
  // Await.result(allUptimes, 1.second)

  val allUptimesWithTraverse: Future[List[Int]] =
    Future.traverse(hostnames)(getUptime)
}

object TraverseWithApplicativesExpr {
  import scala.concurrent._
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  import cats.Applicative
  import cats.instances.future._ // for Applicative
  import cats.syntax.applicative._ // for pure

  /*
  // these are the same.
  Future(List.empty[Int])      // Applicative
  List.empty[Int].pure[Future] // Applicative
   */

  val hostnames = List(
    "alpha.example.com",
    "beta.example.com",
    "gamma.example.com"
  )
  def getUptime(hostname: String): Future[Int] = Future(hostname.length * 60)
  def oldCombine(acc: Future[List[Int]], host: String): Future[List[Int]] = {
    val uptime = getUptime(host)
    for {
      acc <- acc // List[Int] <- Future[List[Int]]
      uptime <- uptime // Int <- Future[Int]
    } yield acc :+ uptime // List[Int]
  }

  import cats.syntax.apply._ // for mapN
  def newCombine(acc: Future[List[Int]], host: String): Future[List[Int]] =
    (acc, getUptime(host)).mapN(_ :+ _) // appended

  // generalise it
  // List[A], A => F[A], F[List[A]]
  def listTraverse[F[_]: Applicative, A, B](list: List[A])(
      func: A => F[B]
  ): F[List[B]] = list.foldLeft(List.empty[B].pure[F]) { (acc, i) =>
    (acc, func(i)).mapN(_ :+ _)
  }

  // listTraverse(hostnames)(getUptime)
  // val res1: scala.concurrent.Future[List[Int]] = Future(Success(List(1020, 960, 1020)))

  def listSequence[F[_]: Applicative, A](list: List[F[A]]): F[List[A]] =
    // list.foldLeft(List.empty[A].pure[F]) { (acc, i) => (acc, i).mapN(_ :+ _) }
    // def identity[A](x: A): A (A method that returns its input value.)
    listTraverse(list)(identity)

  // Await.result(listSequence(List(Future(1), Future(2), Future(3))), 1.second) // List(1, 2, 3)
}
