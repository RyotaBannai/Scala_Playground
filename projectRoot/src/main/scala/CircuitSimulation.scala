abstract class CircuitSimulation extends BasicCircuitSimulation {
  // 半加算器
  // s: 和, s = (a + b) % 2
  // c: キャリー, c = (a + b) / 2
  def halfAdder(a: Wire, b: Wire, s: Wire, c: Wire) = {
    val d, e = new Wire
    orGate(a, b, d)
    andGate(a, b, c)
    inverter(c, e)
    andGate(d, e, s)
  }

  // 全加算器
  // s: 和, sum = (a + b + cin) % 2
  // cin: キャリイン
  // cout: キャリアウト, (a + b + cin) / 2
  def fullAdder(a: Wire, b: Wire, cin: Wire, sum: Wire, cout: Wire) {
    val s, c1, c2 = new Wire
    halfAdder(a, cin, s, c1)
    halfAdder(b, s, sum, c2)
    orGate(c1, c2, cout)
  }
}
