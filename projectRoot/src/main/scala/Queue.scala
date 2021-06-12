class Queue[T](private val leading: List[T], private val trailing: List[T]) {
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
  def enqueue(x: T) = new Queue(leading, x :: trailing)
  def showItems() = {
    println("head:\n" + head)
    println("tail:\n")
    val q = mirror
    q.leading.tail.foreach(println)
    q.trailing.foreach(println)
  }
}
