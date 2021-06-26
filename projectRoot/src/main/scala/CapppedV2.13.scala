import collection._

/** the IterableOps trait implements all concrete methods of Iterable in a generic way.
  * For instance, the return type of methods like take, drop, filter or init is the third type parameter passed to class IterableOps, i.e.,
  * in class Capped, it is Capped[A].
  * Similarly, the return type of methods like map, flatMap or concat is defined by the second type parameter passed to class IterableOps
  *
  * StrictOptimizedIterableOps: which overrides all transformation operations to take advantage of strict builders
  *
  * https://docs.scala-lang.org/overviews/core/custom-collections.html#capped-sequence
  */
class Capped[A] private (
    val capacity: Int,
    val length: Int,
    offset: Int,
    elems: Array[Any]
) extends immutable.Iterable[A]
    with IterableOps[A, Capped, Capped[A]]
    with IterableFactoryDefaults[A, Capped]
    with StrictOptimizedIterableOps[A, Capped, Capped[A]] { self =>

  // only public constructor
  def this(capacity: Int) =
    this(capacity, length = 0, offset = 0, elems = Array.ofDim(capacity))

  def appended[B >: A](elem: B): Capped[B] = {
    val newElems = Array.ofDim[Any](capacity)
    Array.copy(elems, 0, newElems, 0, capacity)
    val (newOffset, newLength) =
      if (length == capacity) {
        newElems(offset) = elem
        newElems foreach println
        ((offset + 1) % capacity, length)
      } else {
        newElems(length) = elem
        (offset, length + 1)
      }
    new Capped[B](capacity, newLength, newOffset, newElems)
  }

  @`inline` def :+[B >: A](elem: B): Capped[B] = appended(elem)

  // The apply method implements indexed access:
  // it translates the given index into its corresponding index in the underlying array by adding the offset.
  def apply(i: Int): A = elems((i + offset) % capacity).asInstanceOf[A]

  // to implement iterator to make the generic collection operations
  // (such as foldLeft, count, etc.) work on Capped collections
  def iterator: Iterator[A] = view.iterator
  override def view: IndexedSeqView[A] = new IndexedSeqView[A] {
    def length: Int = self.length
    def apply(i: Int): A = self(i)
  }

  /** 処理を以上しているだけなので、IterableFactoryDefaults が実装している内容を流用.
    * iterableFactory だけ定義すれば良い.
    */
  // override protected def fromSpecific(coll: IterableOnce[A]): Capped[A] =
  //   iterableFactory.from(coll)
  // override protected def newSpecificBuilder: mutable.Builder[A, Capped[A]] =
  //   iterableFactory.newBuilder
  // override def empty: Capped[A] = iterableFactory.empty

  override def className = "Capped"
  override def knownSize: Int = length

  override val iterableFactory: IterableFactory[Capped] = new CappedFactory(
    capacity
  )
} // end of Capped

class CappedFactory(capacity: Int) extends IterableFactory[Capped] {
  def from[A](source: IterableOnce[A]): Capped[A] =
    source match {
      case capped: Capped[A] if capped.capacity == capacity => capped
      case _                                                => (newBuilder[A] ++= source).result()
    }

  def empty[A]: Capped[A] = new Capped[A](capacity)

  def newBuilder[A]: mutable.Builder[A, Capped[A]] =
    new mutable.ImmutableBuilder[A, Capped[A]](empty) {
      // addOne: Growable trait method
      def addOne(elem: A): this.type = { elems = elems :+ elem; this }
    }
} // end of CappedFactory

/*
new Capped1(capacity = 4)
res0: Capped1[Nothing] = Capped1()

scala> res0 :+ 1 :+ 2 :+ 3
res1: Capped1[Int] = Capped1(1, 2, 3)

scala> res1.length
res2: Int = 3

scala> res1.lastOption
res3: Option[Int] = Some(3)

scala> res1 :+ 4 :+ 5 :+ 6
res4: Capped1[Int] = Capped1(3, 4, 5, 6)

scala> res4.take(3)
res5: collection.immutable.Iterable[Int] = List(3, 4, 5)
 */
