import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Signal
import akka.actor.typed.PostStop

object StartStopActor1 {
  def apply(): Behavior[String] = Behaviors.setup(context => new StartStopActor1(context))
}

class StartStopActor1(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  println("first started")
  context.spawn(StartStopActor2(), "second")

  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "stop" => Behaviors.stopped
    }
  override def onSignal: PartialFunction[Signal, Behavior[String]] = { case PostStop =>
    println("first stopped")
    this
  }
} // end of StartStopActor1 class

object StartStopActor2 {
  def apply(): Behavior[String] = Behaviors.setup(context => new StartStopActor2(context))
}

class StartStopActor2(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  println("second started")
  override def onMessage(msg: String): Behavior[String] = {
    // no messages handled by this actor
    Behaviors.unhandled
  }
  override def onSignal: PartialFunction[Signal, Behavior[String]] = { case PostStop =>
    println("second stopped")
    this
  }
} // end of StartStopActor2 class

class Guardian(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "start" =>
        val first = context.spawn(StartStopActor1(), "first")
        first ! "stop"
        this
    }
} // end of Guardian class

object Guardian {
  def apply(): Behavior[String] =
    Behaviors.setup(context => new Guardian(context))
} // end of Guardian object

object ActorLifeCycleStop extends App {
  val greeterMain: ActorSystem[String] = ActorSystem(Guardian(), "Akka")
  greeterMain ! "start"
} // end of AkkaQuikstart object
