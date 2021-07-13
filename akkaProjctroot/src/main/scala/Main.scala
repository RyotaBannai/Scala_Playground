import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import GreeterMain.SayHello

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

// Guardian actor: bootstraps application.
object GreeterMain {

  /** Message
    */
  final case class SayHello(name: String)

  def apply(): Behavior[SayHello] =
    Behaviors.setup { context =>
      val greeter = context.spawn(Greeter(), "greeter")
      Behaviors.receiveMessage { message =>
        context.log.info("Initialize App..")

        val replyTo = context.spawn(GreeterBot(max = 3), message.name)
        greeter ! Greeter.Greet(message.name, replyTo)
        Behaviors.same
      }
    }
} // end of Greetermain

object AkkaQuikstart extends App {
  val greeterMain: ActorSystem[SayHello] = ActorSystem(GreeterMain(), "AkkaQuickStart")
  greeterMain ! SayHello("Charles")
} // end of AkkaQuikstart object
