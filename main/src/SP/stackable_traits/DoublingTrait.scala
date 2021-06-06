package stackable_traits

import stackable_traits.IntQueue

// extends IntQueue をしているのは、この trait を mixin できるのが
// IntQueue を拡張するクラスだけに限定するため

// abstract 宣言されたメソッド: トレイト内での super 呼び出しは動的に束縛される.
// 他のトレイトまたはクラスの後(after)で mixin される限り、正しく動作するようにする(mixin しなければならないことを示唆する) (SP227)
trait DoublingTrait extends IntQueue {
  abstract override def put(x: Int) = { super.put(2 * x) }
}
