abstract class BasicCircuitSimulation extends Simulation {
  def InvertDelay: Int = ???
  def AndGateDelay: Int = ???
  def OrGateDelay: Int = ???

  class Wire {
    private var sigVal = false

    /** @val actions 配線に付属するアクションプロシージャ全体 */
    private var actions: List[Action] = List()
    def getSignal = sigVal
    /*
     * @brief 配線に新しい信号をセット.
     *        配線の信号が変わるたびに、配線に付属する全てのアクションが実行される
     */
    def setSignal(s: Boolean) = if (s != sigVal) {
      sigVal = s
      actions foreach (_())
    }
    /*
     * @brief 配線のアクションに指定されたプロシージャ p を付属させる
     *        アクションは、配線に追加された時に１度実行され、そのごは配線の信号が変わるたびに実行される
     */
    def addAction(a: Action) = {
      actions = a :: actions
      a()
    }
  }

  // inverter: 配線 a, 配線 b の間にインバータを挿入する
  def inverter(input: Wire, output: Wire) = {
    def inverterAction() = {
      val inputSig = input.getSignal
      afterDelay(InvertDelay) {
        output setSignal !inputSig
      }
    }
    input addAction inverterAction
  }

  def andGate(a1: Wire, a2: Wire, output: Wire) = {
    def andAction() = {
      val a1Sig = a1.getSignal
      val a2Sig = a2.getSignal
      afterDelay(AndGateDelay) {
        output setSignal (a1Sig & a2Sig)
      }
    }

    a1 addAction andAction
    a2 addAction andAction
  }

  def orGate(o1: Wire, o2: Wire, output: Wire) = {
    def orAction() = {
      val o1Sig = o1.getSignal
      val o2Sig = o2.getSignal
      afterDelay(OrGateDelay) {
        output setSignal (o1Sig | o2Sig)
      }
    }
    o1 addAction orAction
    o2 addAction orAction
  }

  /*
   * @brief 配線上の信号の変化をチェックする
   * 　　　　配線にプローブ(探索器)を挿入することをシミュレート
   */
  def probe(name: String, wire: Wire) = {
    def proveAction() = {
      println(name + " " + currentTime + " new-value = " + wire.getSignal)
    }
    wire addAction proveAction
  }
}
