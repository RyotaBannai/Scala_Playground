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

  // ys: accum, y: item
  def revLeft[T](xs: List[T]) = (List[T]() /: xs) { (ys, y) => y :: ys }

  // 仮想マシンの元では 30,000 ~ 50,000 くらいが限度.
  def incAll(xs: List[Int]): List[Int] = xs match {
    case List()   => List()
    case x :: xs1 => x + 1 :: incAll(xs1)
  }

  // for loop は 末尾に追加するため非常に効率が悪い
  // ::: は第１被演算子の長さに比例する、時間がかかる処理である.
  // 以下の実装では、処理全体としてリストの長さの自乗に比例する時間を必要としてしまう
  // リストバッファーを使えば、リストの要素を蓄積できるため処理が改善される. (SP435)
  /*
  // terrible impl
  var result = List[Int]()
  for(x <- xs) result = result ::: List(x + 1)
  result
   */

  /*
  // better impl
  import scala.collection.mutable.ListBuffer
  val buf = new ListBuffer[Int]
  for(x <- xs) buf += x + 1
  buf.toList
   */
}
