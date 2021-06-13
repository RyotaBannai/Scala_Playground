trait MyOption[+T]
case class MySome[T](value: T) extends MyOption[T]
case object MyNone extends MyOption[Nothing]

class Queue[+T] private (
    private val leading: List[T],
    private val trailing: List[T]
) {
  private def mirror =
    if (leading.isEmpty) {
      println("isEmpty")
      new Queue(trailing.reverse, Nil)
    } else this
  def head = mirror.leading.head
  def tail = {
    val q = mirror
    new Queue(q.leading.tail, q.trailing)
  }
  def enqueue[U >: T](x: U) = new Queue[U](leading, x :: trailing)
  def showItems() = {
    println("head:\n" + head)
    println("tail:\n")
    val q = mirror
    q.leading.tail.foreach(println)
    q.trailing.foreach(println)
  }
}

object Queue {
  def apply[T](xs: T*) = new Queue[T](xs.toList, Nil)
  def apply() = apply[Int]()
}

/*
 * val q = Queue(new MyOption())
 * val qq = q enqueue (MyNone)
 */
