import collection.{
  AbstractIterator,
  SpecificIterableFactory,
  StrictOptimizedSeqOps,
  View,
  mutable
}
import collection.immutable.{IndexedSeq, IndexedSeqOps}

class RNA {}
object RNA {
  abstract class Base
  case object A extends Base
  case object C extends Base
  case object G extends Base
  case object U extends Base
  object Base {
    val fromInt: Int => Base = Array(A, U, G, C)
    val toInt: Base => Int = Map(A -> 0, U -> 1, G -> 2, C -> 3)
  }

  final class RNA1 private (val groups: Array[Int], val length: Int)
      extends IndexedSeq[Base]
      with IndexedSeqOps[Base, IndexedSeq, RNA1]
      with StrictOptimizedSeqOps[Base, IndexedSeq, RNA1] { rna =>
    import RNA1._
    // Mandatory implementation of `apply` in `IndexedSeqOps`
    def apply(idx: Int): Base = {
      if (idx < 0 || length <= idx)
        throw new IndexOutOfBoundsException
      Base.fromInt(groups(idx / N) >> (idx % N * S) & M)
    }

    // Mandatory overrides of `fromSpecific`, `newSpecificBuilder`,
    // and `empty`, from `IterableOps`
    override protected def fromSpecific(coll: IterableOnce[Base]): RNA1 =
      RNA1.fromSpecific(coll)
    override protected def newSpecificBuilder: mutable.Builder[Base, RNA1] =
      RNA1.newBuilder
    override def empty: RNA1 = RNA1.empty

    // Overloading of `appended`, `prepended`, `appendedAll`, `prependedAll`,
    // `map`, `flatMap` and `concat` to return an `RNA` when possible
    def concat(suffix: IterableOnce[Base]): RNA1 =
      strictOptimizedConcat(suffix, newSpecificBuilder)
    @inline final def ++(suffix: IterableOnce[Base]): RNA1 = concat(suffix)
    def appended(base: Base): RNA1 =
      (newSpecificBuilder ++= this += base).result()
    def appendedAll(suffix: Iterable[Base]): RNA1 =
      strictOptimizedConcat(suffix, newSpecificBuilder)
    def prepended(base: Base): RNA1 =
      (newSpecificBuilder += base ++= this).result()
    def prependedAll(prefix: Iterable[Base]): RNA1 =
      (newSpecificBuilder ++= prefix ++= this).result()
    def map(f: Base => Base): RNA1 =
      strictOptimizedMap(newSpecificBuilder, f)
    def flatMap(f: Base => IterableOnce[Base]): RNA1 =
      strictOptimizedFlatMap(newSpecificBuilder, f)

    override def className = "RNA1"
  }
  object RNA1 {
    private val S = 2
    private val N = 32
    private val M = (1 << S) - 1
    def fromSeq(buf: collection.Seq[Base]): RNA1 = {
      val groups = new Array[Int]((buf.length + N - 1) / N)
      for (i <- 0 until buf.length)
        groups(i / N) |= Base.toInt(buf(i)) << (i % N * S)
      new RNA1(groups, buf.length)
    }
    def apply(bases: Base*) = fromSeq(bases)
    def empty: RNA1 = fromSeq(Seq.empty)

    def newBuilder: mutable.Builder[Base, RNA1] =
      mutable.ArrayBuffer.newBuilder[Base].mapResult(fromSeq)

    def fromSpecific(it: IterableOnce[Base]): RNA1 = it match {
      case seq: collection.Seq[Base] => fromSeq(seq)
      case _                         => fromSeq(mutable.ArrayBuffer.from(it))
    }
  }
}

/*
val rna = RNA1(A, U, G, G, C)

val xs = List(A, G, U, A)
RNA1.fromSeq(xs)
RNA1.fromSeq(xs).take(1)
 */
