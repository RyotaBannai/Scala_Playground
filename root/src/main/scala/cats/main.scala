// import cats.data.Xor

// object XorExample {
//   // Get Xor.left and Xor.right into scope
//   import Xor.{left, right}

//   // The type we will be working with
//   type Result[A] = String Xor A
//   val l: Result[Int] = left("Failed")
//   val r: Result[Int] = right(1)

//   // Nothing happens when we map a left
//   println(l map (x => x + 1))
//   // The right is transformed
//   println(r map (x => x + 1))
// }
