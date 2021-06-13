object Isort {
  def iSort(xs: List[Int]): List[Int] =
    if (xs.isEmpty) Nil else insert(xs.head, iSort(xs.tail))

  def insert(x: Int, xs: List[Int]): List[Int] =
    if (xs.isEmpty || x <= xs.head) x :: xs
    else xs.head :: insert(x, xs.tail)
}

object Isort2 {
  def iSort(xs: List[Int]): List[Int] = xs match {
    case List()   => List()
    case x :: xs1 => insert(x, iSort(xs1))
  }

  def insert(x: Int, xs: List[Int]): List[Int] = xs match {
    case List()  => List(x)
    case y :: ys => if (x <= y) x :: xs else y :: insert(x, ys)
  }
}

object Msort {
  /*
   * @example msort((x: Int, y: Int) => x < y)(List(10,3,1,2))
   * val intSort = msort((x: Int, y: Int) => x < y)_
   * intSort(List(5,6,3,1))
   */
  def mSort[T](less: (T, T) => Boolean)(xs: List[T]): List[T] = {
    def merge(xs: List[T], ys: List[T]): List[T] = (xs, ys) match {
      case (Nil, _) => ys
      case (_, Nil) => xs
      case (x :: xs1, y :: ys1) =>
        if (less(x, y)) x :: merge(xs1, ys)
        else y :: merge(xs, ys1)
    }

    val n = xs.length / 2
    if (n == 0) xs
    else {
      val (ys, zs) = xs splitAt n
      merge(mSort(less)(ys), mSort(less)(zs))
    }
  }
}

object OrderedMSort {
  // Upper bound
  def orderedMSort[T <: Ordered[T]](xs: List[T]): List[T] = {
    def merge(xs: List[T], ys: List[T]): List[T] = (xs, ys) match {
      case (Nil, _) => ys
      case (_, Nil) => xs
      case (x :: xs1, y :: ys1) =>
        if (x < y) x :: merge(xs1, ys)
        else y :: merge(xs, ys1)
    }

    val n = xs.length / 2
    if (n == 0) xs
    else {
      val (ys, zs) = xs splitAt n
      merge(orderedMSort(ys), orderedMSort(zs))
    }
  }
}

class Person(val firstName: String, val lastName: String)
    extends Ordered[Person] {
  def compare(that: Person): Int = {
    val lastNameComparison = lastName.compareToIgnoreCase(that.firstName)
    if (lastNameComparison != 0)
      lastNameComparison
    else
      firstName.compareToIgnoreCase(that.lastName)
  }

  override def toString = firstName + " " + lastName
}

/*
 * @example
 * val ppl = List(new Person("Larry", "Wall"), new Person("Alan", "Kay"))
 * orderedMSort(ppl)
 */
