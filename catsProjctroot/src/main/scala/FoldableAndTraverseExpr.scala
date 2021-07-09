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

object TraverseExpr {}
