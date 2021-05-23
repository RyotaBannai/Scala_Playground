/*
  型パラメータ（type parameter）
 */

class TypeParam[A](var value: A) {
  def put(newValue: A): Unit = {
    value = newValue
  }
  def get(): A = value
}

// val myType = new TypeParam[Int](1)
// error when put String type value

// 二つのパラメータを返したい時に便利
class Pair[A, B](a: A, b: B) {
  override def toString: String = "(" + a + "," + b + ")"
  def showParams(): Unit = println("a: ", a, "b: ", b)
}
// def devide(m: Int, n:Int): Pair[Int, Int]= new Pair[Int, Int](m/n, m%n)
// def devide(m: Int, n:Int): Pair[Int, Int]= new Pair(m/n, m%n) 引数の型から型パラメータの型を推測できる場合省略可
// (devide(7, 3)).showParams()

/*上記の様にしなくても、
  new Tuple2(m/n, m%n)
  とすれば良い。Tupleのあとの数字は要素数

  また、単に
  (m/n, m%n)
  としてもすれば良い様になっている
 */
