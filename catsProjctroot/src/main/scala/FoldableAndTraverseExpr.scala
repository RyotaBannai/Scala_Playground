object FoldableAndTraverseExpr {
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
