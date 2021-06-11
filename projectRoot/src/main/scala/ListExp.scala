/*
 * @example append(List(1,2), List(3,4))
 */
object ListExp {
  def append[T](xs: List[T], ys: List[T]): List[T] = xs match {
    case List()   => ys
    case x :: xs1 => x :: append(xs1, ys)
  }

  def rev[T](xs: List[T]): List[T] = xs match {
    case List()   => xs
    case x :: xs1 => rev(xs1) ::: List(x)
  }
}
