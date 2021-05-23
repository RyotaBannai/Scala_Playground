abstract class Show {
  def show: String
}
class Lb1 extends Show() {
  def show: String = "Lb1"
}

class Lb2 extends Show() {
  def show: String = "Lb2"
}

// 上限境界(upper bounds): 型パラメータがどのような型を継承しているかを指定する
// 上限境界を明示的に指定しなかった場合、Anyが指定されたものとみなされる。
class Bounds[A <: Show, B <: Show](val a: A, val b: B) extends Show {
  override def show: String = "(" + a.show + "," + b.show + ")"
}

// 下限境界（lower bounds）: 型パラメータがどのような型のスーパータイプであるかを指定する
// 共変パラメータと共に用いることが多い機能
abstract class Stacks[+A] {
  def push[E >: A](element: E): Stacks[E]
  def top: A
  def pop: Stacks[A]
  def isEmpty: Boolean
}

object Bounds {
  def main(args: Array[String]): Unit = {
    val lb1: Lb1 = new Lb1()
    val lb2: Lb2 = new Lb2()
    val g = new Bounds(lb1, lb2)
    println(g.show)
  }
}
