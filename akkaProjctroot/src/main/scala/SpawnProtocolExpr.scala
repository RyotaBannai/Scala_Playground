package spawnProtocolExpr

import akka.actor.typed.{Behavior, SpawnProtocol, ActorRef, ActorSystem, Props}
import akka.actor.typed.scaladsl.{Behaviors, LoggerOps}
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object Greeter {

  /** Messages
    */
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String, replyTo: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
    context.log.info("Hello {}!", message.whom)
    message.replyTo ! Greeted(message.whom, context.self)
    /*
     State is updated by returning a new behavior that holds the new immutable state.
     In this case we don’t need to update any state, so we return Behaviors.same
     */
    Behaviors.same
  }
} // end of Greeter object

object GreeterBot {
  def apply(max: Int): Behavior[Greeter.Greeted] = {
    bot(0, max)
  }

  private def bot(greetingCounter: Int, max: Int): Behavior[Greeter.Greeted] =
    Behaviors.receive { (context, message) =>
      val n = greetingCounter + 1
      context.log.info("Greeting {}, {}!", n, message.whom)
      if (n == max) {
        Behaviors.stopped
      } else {
        message.replyTo ! Greeter.Greet(message.whom, context.self)
        /*
          Note how this Actor manages the counter by changing the behavior for each Greeted reply
          rather than using any variables
         */
        bot(n, max)
      }
    }
} // end of GreeterBot object

object GreeterMain {

  /** Message */
  final case class SayHello(name: String)

  def apply(): Behavior[SpawnProtocol.Command] =
    Behaviors.setup { context =>
      SpawnProtocol()
    }
} // end of Greetermain

object SpawnProtocolExpr extends App {
  import Greeter.{Greet, Greeted}
  implicit val system: ActorSystem[SpawnProtocol.Command] = ActorSystem(GreeterMain(), "hello")

  // needed in implicit scope for ask(?)
  import akka.actor.typed.scaladsl.AskPattern._
  implicit val ec: ExecutionContext = system.executionContext
  implicit val timeout: Timeout     = Timeout(3.seconds)

  val greeter: Future[ActorRef[Greet]] = system.ask(
    SpawnProtocol.Spawn(behavior = Greeter(), name = "greeter", props = Props.empty, _)
  )

  // In the previous example,  this receive method owned by GreeterBot
  val greetedBehavior = Behaviors.receive[Greeted] { (context, message) =>
    context.log.info("Greeting for {} from {}", message.whom, message.replyTo)
    Behaviors.stopped
  }

  val greetedReplayTo: Future[ActorRef[Greeted]] =
    system.ask(SpawnProtocol.Spawn(greetedBehavior, name = "", props = Props.empty, _))

  for (greeterRef <- greeter; replyToRef <- greetedReplayTo) {
    greeterRef ! Greet("Akka", replyToRef)
  }

  Thread.sleep(500)
}
