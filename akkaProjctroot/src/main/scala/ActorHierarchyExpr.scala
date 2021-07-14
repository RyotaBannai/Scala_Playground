import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors

object PrintMyActorRefActor {
  def apply(): Behavior[String] = Behaviors.setup(context => new PrintMyActorRefActor(context))
} // end of PrintMyActorRefActor object

class PrintMyActorRefActor(context: ActorContext[String])
    extends AbstractBehavior[String](context) {
  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "printit" =>
        val secondRef = context.spawn(Behaviors.empty[String], "second-actor")
        println(s"Second: $secondRef")
        this
    }
} // end of PrintMyActorRefActor class

object Main {
  def apply(): Behavior[String] = Behaviors.setup(context => new Main(context))
} // end of Main object

class Main(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "start" =>
        val firstRef = context.spawn(PrintMyActorRefActor(), "first-actor")
        println(s"First: $firstRef")
        // We sent the message by using the parent’s reference
        firstRef ! "printit"
        this
    }
} // end of Main class

object ActorHierarchyExpr extends App {
  val testSystem = ActorSystem(Main(), "testSystem")
  testSystem ! "start"
} // end of ActorHierarchyExpr object
