package stackable_traits

import stackable_traits.IntQueue

trait FilteringTrait extends IntQueue {
  abstract override def put(x: Int) = if (x >= 0) super.put(x)
}
