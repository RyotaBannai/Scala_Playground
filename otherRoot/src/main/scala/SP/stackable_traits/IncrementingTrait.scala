package stackable_traits

import stackable_traits.IntQueue

trait IncrementingTrait extends IntQueue {
  abstract override def put(x: Int) = { super.put(x + 1) }
}
