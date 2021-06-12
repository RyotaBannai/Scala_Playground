/*
 * @brief 指定されたタイミングでユーザー定義のアクションを実行
 *        アクションの内容は、具象シミュレーションクラスで実装される
 *        アクションを作業項目(work item)と呼ぶ
 */
class Simulation {
  type Action = () => Unit // type member
  case class WorkItem(time: Int, action: Action)
  private var curtime = 0
  def currentTime: Int = curtime
  private var agenda: List[WorkItem] = List()
  private def insert(ag: List[WorkItem], item: WorkItem): List[WorkItem] = {
    if (ag.isEmpty || item.time < ag.head.time) item :: ag
    else ag.head :: insert(ag.tail, item)
  }
  def afterDelay(delay: Int)(block: => Unit) = {
    val item = WorkItem(currentTime + delay, () => block)
    agenda = insert(agenda, item)
  }
  private def next() = {
    (agenda: @unchecked) match {
      case item :: rest =>
        agenda = rest
        curtime = item.time // ? 実行時間をシミュレーション上の現在時刻に設定する
        item.action()
    }
  }
  def run() = {
    afterDelay(0) {
      println("*** simulation started, time=" + currentTime + " ***")
    }
    while (!agenda.isEmpty) next()
  }
}
