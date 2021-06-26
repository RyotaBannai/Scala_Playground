import collection._
import collection.mutable.{GrowableBuilder, Builder}

class PrefixMap[A]
    extends mutable.Map[String, A]
    with mutable.MapOps[String, A, mutable.Map, PrefixMap[A]]
    with StrictOptimizedIterableOps[
      (String, A),
      mutable.Iterable,
      PrefixMap[A]
    ] {
  var suffixes: immutable.Map[Char, PrefixMap[A]] = immutable.Map.empty
  var value: Option[A] = None

  def get(s: String): Option[A] =
    if (s.isEmpty) value
    else suffixes get (s(0)) flatMap (_.get(s substring 1))

  def withPrefix(s: String): PrefixMap[A] =
    if (s.isEmpty) this
    else {
      val leading = s(0)
      suffixes get leading match {
        case None => suffixes = suffixes + (leading -> empty)
        case _    =>
      }
      suffixes(leading) withPrefix (s substring 1)
    }

  def iterator: Iterator[(String, A)] =
    (for (v <- value.iterator) yield ("", v)) ++
      (for ((chr, m) <- suffixes.iterator; (s, v) <- m.iterator)
        yield (chr +: s, v))

  def addOne(kv: (String, A)): this.type = {
    withPrefix(kv._1).value = Some(kv._2)
    this
  }

  def subtractOne(s: String): this.type = {
    if (s.isEmpty) { val prev = value; value = None; prev }
    else suffixes get (s(0)) flatMap (_.remove(s substring 1))
    this
  }

  def map[B](f: ((String, A)) => (String, B)): PrefixMap[B] =
    strictOptimizedMap(PrefixMap.newBuilder, f)
  def flatMap[B](f: ((String, A)) => IterableOnce[(String, B)]): PrefixMap[B] =
    strictOptimizedFlatMap(PrefixMap.newBuilder, f)

  override def clear(): Unit = suffixes = immutable.Map.empty

  override def concat[B >: A](suffix: IterableOnce[(String, B)]): PrefixMap[B] =
    strictOptimizedConcat(suffix, PrefixMap.newBuilder)
  override def empty = new PrefixMap[A]

  override protected def fromSpecific(
      coll: IterableOnce[(String, A)]
  ): PrefixMap[A] = PrefixMap.fromSpecific(coll)
  override protected def newSpecificBuilder
      : mutable.Builder[(String, A), PrefixMap[A]] = PrefixMap.newBuilder

  override def className = "PrefixMap"
}

object PrefixMap {
  def empty[A] = new PrefixMap[A]

  def from[A](source: IterableOnce[(String, A)]): PrefixMap[A] =
    source match {
      case pm: PrefixMap[A] => pm
      case _                => (newBuilder ++= source).result()
    }
  def apply[A](kvs: (String, A)*): PrefixMap[A] = from(kvs)

  def newBuilder[A]: mutable.Builder[(String, A), PrefixMap[A]] =
    new mutable.GrowableBuilder[(String, A), PrefixMap[A]](empty)

  implicit def toFactory[A](
      self: this.type
  ): Factory[(String, A), PrefixMap[A]] =
    new Factory[(String, A), PrefixMap[A]] {
      def fromSpecific(it: IterableOnce[(String, A)]): PrefixMap[A] =
        self.from(it)

      def newBuilder: mutable.Builder[(String, A), PrefixMap[A]] =
        self.newBuilder
    }
}

/*
val m = PrefixMap("hello" -> 5, "hi" -> 2)
m get "hello"
m += ("hello" -> 6) // updated
m get "hello" match {case Some(value: Int) => value case _ => None } // shows 6

m map {case (k,v) => (k + "!", "x" * v)} // PrefixMap[String] = Map(hello! -> xxxxxx, hi! -> xx) // PrefixMap instead of Mutable Map.
 */
