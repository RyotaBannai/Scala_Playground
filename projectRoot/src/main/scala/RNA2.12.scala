package scala_212

import collection.IndexedSeqLike
import collection.mutable.{Builder, ArrayBuffer}
import collection.generic.CanBuildFrom
import collection.mutable.ArrayBuffer

class RNA {}

object RNA {
  abstract class Base
  case object A extends Base
  case object T extends Base
  case object G extends Base
  case object U extends Base
  object Base {
    val fromInt: Int => Base = Array(A, T, G, U)
    val toInt: Base => Int = Map(A -> 0, T -> 1, G -> 2, U -> 3)
  }

  final class RNA1 private (val groups: Array[Int], val length: Int)
      extends IndexedSeq[Base]
      with IndexedSeqLike[Base, RNA1] {
    // groups には圧縮した RNA データが含まれる.
    // 一つの要素に 16 個の塩基: 1 塩基を 2 ビット値として、１個の整数には 16 個の塩基を格納できる
    import RNA1._
    def apply(idx: Int): Base = {
      if (idx < 0 || length <= idx)
        throw new IndexOutOfBoundsException
      // 右シフトとマスクを使って、その整数から 2 ビットの数値を取り出す.
      Base.fromInt(groups(idx / N) >> (idx % N * S) & M)
    }
    // 配列バッファはシーケンスの一種であるため、RNA1.fromSeq が適用できる(SP521)
    // IndexedSeq の newBuilder に対する必須の再実装
    override protected[this] def newBuilder: Builder[Base, RNA1] =
      RNA1.newBuilder
  }
  object RNA1 {
    // グループを表現するために必要なビット数
    private val S = 2
    // Int に収まるグループ数
    private val N = 32
    // グループを分離するビットマスク(1 ワードの最下位の S ビットを取り出すためのビットマスク)
    private val M = (1 << S) - 1
    def fromSeq(buf: Seq[Base]): RNA1 = {
      val groups = new Array[Int]((buf.length + N - 1) / N)
      for (i <- 0 until buf.length)
        groups(i / N) |= Base.toInt(buf(i)) << (i % N * S)
      new RNA1(groups, buf.length)
    }
    def apply(bases: Base*) = fromSeq(bases)

    def newBuilder: Builder[Base, RNA1] =
      new ArrayBuffer[Base] mapResult fromSeq

    implicit def CanBuildFrom: CanBuildFrom[RNA1, Base, RNA1] =
      new CanBuildFrom[RNA1, Base, RNA1] {
        def apply(): Builder[Base, RNA1] = newBuilder
        def apply(from: RNA1): Builder[Base, RNA1] = newBuilder
      }
  }
}
