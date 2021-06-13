package newQueue
/*
 * Better Queue with no multiple copy in mirror method.
 */
class Queue[+T] private (
    private[this] var leading: List[T],
    private[this] var trailing: List[T]
) {
  private def mirror() =
    if (leading.isEmpty) {
      println("isEmpty")
      while (!trailing.isEmpty) {
        // trailing は reversed なので、後方から順に leading へ追加する
        leading = trailing.head :: leading
        trailing = trailing.tail
      }
    }

  def head: T = {
    mirror()
    leading.head
  }

  def tail = {
    mirror()
    new Queue(leading.tail, trailing)
  }

  def enqueue[U >: T](x: U) = new Queue[U](leading, x :: trailing)

  def showItems() = {
    println("head:\n" + head)
    println("tail:\n")
    leading.tail.foreach(println)
    trailing.foreach(println)
  }
}

object Queue {
  def apply[T](xs: T*) = new Queue[T](xs.toList, Nil)
  def apply() = apply[Int]()
}
