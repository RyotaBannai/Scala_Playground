package interaction_patterns

import akka.actor.typed.{Behavior, SpawnProtocol, ActorRef, ActorSystem, Props}
import akka.actor.typed.scaladsl.{Behaviors, LoggerOps}
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

/** Sample of Adapted Response
  * Most often the sending actor does not, and should not, support receiving the response messages of another actor.
  * In such cases we need to provide an ActorRef of the right type and adapt the response message to a type that the sending actor can handle.
  *
  * In this sample, FrontEnd should not support Backend's Response protocol.
  *
  * ・You can register several message adapters for different message classes.
  * It’s only possible to have one message adapter per message class to make sure that the number of adapters are not growing unbounded if registered repeatedly.
  *  That also means that a registered adapter will replace an existing adapter for the same message class.
  * （For instance, you can create a message adapter for Backend.Response once, and if you do it again, then the last one will replace the previously registered adapter. You can register as many as you want for different message classes.）
  *
  * ・A message adapter will be used if the message class matches 'the given class' or 'is a subclass thereof'.
  */

object Backend {
  sealed trait Request
  final case class StartTranslationJob(taskId: Int, site: URI, replyTo: ActorRef[Response])
      extends Request

  sealed trait Response
  final case class JobStarted(taskId: Int)                    extends Response
  final case class JobProgress(taskId: Int, progress: Double) extends Response
  final case class JobCompleted(taskId: Int, result: URI)     extends Response
} // end of Backend

object FrontEnd {
  sealed trait Command
  final case class Translate(site: URI, replyTo: ActorRef[URI])               extends Command
  private final case class WrappedBackendResponse(response: Backend.Response) extends Command

  def apply(backend: ActorRef[Backend.Request]): Behavior[Command] = {
    Behaviors.setup { context =>
      val backendResponseMapper: ActorRef[Backend.Response] =
        context.messageAdapter(rsp => WrappedBackendResponse(rsp))

      def active(inProgress: Map[Int, ActorRef[URI]], count: Int): Behavior[Command] = {
        behaviors.receiveMessage[Command] {
          case Translate(site, replyTo) =>
            val taskId = count + 1
            backend ! Backend.StartTranslationJob(taskId, site, backendResponseMapper)

          case wrapped: WrappedBackendResponse =>
            wrapped.response match {
              case Backend.JobStarted(taskId) =>
                context.log.info("Started {}", taskId)
                Behaviors.same
              case Backend.JobProgress(taskId, progress) =>
                context.log.info("In progress {}, {}", taskId, progress)
                Behaviors.same
              case Backend.JobCompleted(taskId, result) =>
                context.log.info("Completed {}, {}", taskId, result)
                inProgress(taskId) ! result
                active(inProgress - taskId, count)
            }
        }
      } // end of active method

      active(inProgress = Map.empty, count = 0)
    }
  } // end of apply method
}   // end of object FrontEnd
