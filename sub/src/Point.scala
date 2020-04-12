// class Point (_x: Int, _y: Int){}

class Point (val x:Int, val y:Int) { //コンストラクタ引数の定義をした場合 var でも可
  def +(p:Point): Point = {
    new Point(x + p.x, y + p.y)//自分のx, yに直接アクセルできる　p1 + p2　この関数はこんな感じでつかう
  }

  override def toString: String =  "(" + x + ", " + y + ")"
}
//・これをプライマリコンストラクタという。あまり使わないがscalaでは複数のコンストラクタを宣言できる。
//・プライマリコンストラクタの引数にval/varをつけるとそのフィールドは公開され、外部からアクセスできるようになる。

class Adder{
  def add(x:Int)(y:Int):Int = x+y // 複数の引数リストを持つ
  def add2(x:Int, y:Int):Int = x+y
}

object Point {
  def main(args: Array[String]): Unit = {
    val adder = new Adder()
    adder.add(2)(3)
    val fun = adder.add(2) _
    println(fun(3))
    //
    val fun2 = adder.add2(2,_) //これでも同じ様なことができる
    println(fun2(3))
  }
}
