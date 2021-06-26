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

  // "a" => None
  def get(s: String): Option[A] =
    if (s.isEmpty) value
    else suffixes get (s(0)) flatMap (_.get(s substring 1))

  // "a" => Some(None)
  // def testGet(s: String): Any =
  //   if (s.isEmpty) value
  //   else suffixes get (s(0)) map (_.get(s substring 1))

  def withPrefix(s: String): PrefixMap[A] =
    if (s.isEmpty) this
    else {
      val leading = s(0)
      suffixes get leading match {
        // 先頭要素が None なら空で初期化.
        case None => suffixes = suffixes + (leading -> empty)
        case _    =>
      }
      suffixes(leading) withPrefix (s substring 1)
    }

  // PrefixMap を key-value 形式で返却　(SP529)
  // value Option(None) でなければ、key == "" で Some(XX) を表示
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

  // Overloading of transformation methods that should return a PrefixMap
  def map[B](f: ((String, A)) => (String, B)): PrefixMap[B] =
    strictOptimizedMap(PrefixMap.newBuilder, f)
  def flatMap[B](f: ((String, A)) => IterableOnce[(String, B)]): PrefixMap[B] =
    strictOptimizedFlatMap(PrefixMap.newBuilder, f)

  // Members declared in scala.collection.mutable.Clearable
  override def clear(): Unit = suffixes = immutable.Map.empty

  override def concat[B >: A](suffix: IterableOnce[(String, B)]): PrefixMap[B] =
    strictOptimizedConcat(suffix, PrefixMap.newBuilder)

  // Members declared in scala.collection.IterableOps
  override protected def fromSpecific(
      coll: IterableOnce[(String, A)]
  ): PrefixMap[A] = PrefixMap.fromSpecific(coll)
  override protected def newSpecificBuilder
      : mutable.Builder[(String, A), PrefixMap[A]] = PrefixMap.newBuilder
  override def empty = PrefixMap.empty

  override def className = "PrefixMap"
}

object PrefixMap {
  def empty[A] = new PrefixMap[A]

  /*
  from(List(("hello"-> 4))
  > PrefixMap[Int] = PrefixMap(hello -> 4)
  from(from(List(("hello"-> 4))))
  > PrefixMap[Int] = PrefixMap(hello -> 4)
   */
  def from[A](source: IterableOnce[(String, A)]): PrefixMap[A] =
    source match {
      case pm: PrefixMap[A] => pm
      case _                => (newBuilder ++= source).result()
    }
  def apply[A](kvs: (String, A)*): PrefixMap[A] = from(kvs)

  // ArrayBuilder のようなもの.
  // mutable.ArrayBuilder.make[B].addAll(this).result() in IterableOnce
  def newBuilder[A]: mutable.Builder[(String, A), PrefixMap[A]] =
    new mutable.GrowableBuilder[(String, A), PrefixMap[A]](empty)

  import scala.language.implicitConversions

  implicit def toFactory[A](
      self: this.type
  ): Factory[(String, A), PrefixMap[A]] =
    new Factory[(String, A), PrefixMap[A]] {
      // fromSpecific Returns:
      // A collection of type C (such as, PrefixMap[A]) containing the same elements (such as, A) as the source collection it
      def fromSpecific(it: IterableOnce[(String, A)]): PrefixMap[A] =
        self.from(it)

      def newBuilder: mutable.Builder[(String, A), PrefixMap[A]] =
        self.newBuilder
    }
}

/*
val m = PrefixMap("abc" -> 0, "abd" -> 1, "al" -> 2, "all" -> 3, "xy" -> 4)
val m = PrefixMap("hello" -> 5, "hi" -> 2)
m get "hello"
m += ("hello" -> 6) // updated
m get "hello" match {case Some(value: Int) => value case _ => None } // shows 6

m map {case (k,v) => (k + "!", "x" * v)} // PrefixMap[String] = Map(hello! -> xxxxxx, hi! -> xx) // PrefixMap instead of Mutable Map.
 */
