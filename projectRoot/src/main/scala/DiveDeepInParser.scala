class DiveDeepInParser {}

object DiveDeepInParser {

  /*
   // this の別名. alias for this.
   val o = new Outer
   val i = new o.Inner
   true
   */
  class Outer { outer =>
    class Inner {
      println(Outer.this eq outer)
    }
  }
}
