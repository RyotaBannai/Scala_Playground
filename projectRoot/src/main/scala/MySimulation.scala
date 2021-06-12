import MySimulation._

class MySimulation {}
object MySimulation extends CircuitSimulation {
  override def InvertDelay: Int = 1;
  override def AndGateDelay: Int = 3;
  override def OrGateDelay: Int = 5;

  def testHalfAdderRun() {
    val input1, input2, sum, carry = new Wire
    probe("carry", carry)
    probe("sum", sum)
    halfAdder(input1, input2, sum, carry)
    input1 setSignal true
    run()
    /*
     * 出力までに最大 8 かかり、最後の And 後の sum の結果は true
     * *** simulation started, time=0 ***
     * sum 8 new-value = true
     */
  }

  def testFullAdderRun() {
    val input1, input2, sum, cin, cout = new Wire
    probe("cin", cin)
    probe("cout", cout)
    probe("sum", sum)
    fullAdder(input1, input2, cin, sum, cout)
    input1 setSignal true
    run()
  }
}
