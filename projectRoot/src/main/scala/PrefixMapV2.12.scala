package staled

import collection._
class PrefixMap[T]
    extends mutable.Map[String, T]
    with mutable.MapLike[String, T, PrefixMap[T]] {
  var suffixes: immutable.Map[Char, PrefixMap[T]] = Map.empty
  var value: Option[T] = None // ノードの対する値

  // value が empty でなければサブマップを取得する.
  // オプション値 ov とクロージャ f(これもオプション値を返す)に対し、ov flatMap f を呼び出すと
  // of と f が定義されている値を返した時に限り成功する. そうでなければ、ov flatMap f は None を返す(SP529)
  def get(s: String): Option[T] =
    if (s.isEmpty) value
    else suffixes get (s(0)) flatMap (_.get(s substring 1))

  // withPrefix: 指定した prefix を持っている全てのサブマップを選択(SP527)
  // 再帰的にサブマップを探索
  def withPrefix(s: String): PrefixMap[T] = if (s.isEmpty) this
  else {
    val leading = s(0)
    suffixes get leading match {
      case None => suffixes = suffixes + (leading -> empty)
      case _    =>
    }
    suffixes(leading) withPrefix (s substring 1)
  }

  override def update(s: String, elem: T) =
    withPrefix(s).value = Some(elem)

  override def remove(s: String): Option[T] =
    if (s.isEmpty) { val prev = value; value = None; prev }
    else suffixes get (s(0)) flatMap (_.remove(s substring 1))

  def iterator: Iterator[(String, T)] =
    (for (v <- value.iterator) yield ("", v)) ++
      (for ((chr, m) <- suffixes.iterator; (s, v) <- m.iterator)
        yield (chr +: s, v))

  def +=(kv: (String, T)): this.type = { update(kv._1, kv._2); this }

  def -=(s: String): this.type = { remove(s); this }

  override def empty = new PrefixMap[T]
}

import collection.mutable.{Builder, MapBuilder}
import collection.generic.CanBuildFrom

object PrefixMap {
  def empty[T] = new PrefixMap[T]
  def apply[T](kvs: (String, T)*): PrefixMap[T] = {
    val m: PrefixMap[T] = empty
    for (kv <- kvs) m += kv
    m
  }

  def newBuilder[T]: Builder[(String, T), PrefixMap[T]] =
    new MapBuilder[String, T, PrefixMap[T]](empty)

  implicit def canBuildFrom[T]
      : CanBuildFrom[PrefixMap[_], (String, T), PrefixMap[T]] =
    new CanBuildFrom[PrefixMap[_], (String, T), PrefixMap[T]] {
      def apply() = newBuilder[T]
      def apply(from: PrefixMap[_]) = newBuilder[T]
    }
}
/*
val m = PrefixMap("hello" -> 5, "hi" -> 2)
m get "hello"
m += ("hello" -> 6) // updated
m get "hello" match {case Some(value: Int) => value case _ => None } // shows 6

m map {case (k,v) => (k + "!", "x" * v)} // PrefixMap[String] = Map(hello! -> xxxxxx, hi! -> xx) // PrefixMap instead of Mutable Map.
 */
