package stashExpr

import scala.concurrent.Future
import scala.util.{Failure, Success}

import akka.Done
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer}

trait DB {
  def save(id: String, value: String): Future[Done]
  def load(id: String): Future[String]
}

object DataAccess {
  sealed trait Command
  final case class Save(value: String, replyTo: ActorRef[Done]) extends Command
  final case class Get(ref: ActorRef[String])                   extends Command
  private final case class InitialState(value: String)          extends Command
  private final object SaveSuccess                              extends Command
  private final case class DBError(cause: Throwable)            extends Command

  def apply(id: String, db: DB): Behavior[Command] = {
    Behaviors.withStash(100) { buffer =>
      Behaviors.setup { ctx => new DataAccess(ctx, buffer, id, db).start() }
    }
  }
} // end of object DataAccess

class DataAccess(
    ctx: ActorContext[DataAccess.Command],
    buffer: StashBuffer[DataAccess.Command],
    id: String,
    db: DB
) {
  import DataAccess._

  // called first time only.
  private def start(): Behavior[Command] = {
    ctx.pipeToSelf(db.load(id)) {
      case Success(value) => InitialState(value)
      case Failure(cause) => DBError(cause)
    }

    Behaviors.receiveMessage {
      case InitialState(value) =>
        // now we are ready to handle stashed messages if any
        buffer.unstashAll(active(value))
      case DBError(cause) => throw cause
      case other          =>
        // stash all other messages for later processing(will be processed in InitialState case.)
        buffer.stash(other)
        Behaviors.same
    }
  }

  // base handler when receiving message.
  private def active(state: String): Behavior[Command] = {
    Behaviors.receiveMessagePartial {
      case Get(replyTo) =>
        replyTo ! state
        Behaviors.same
      case Save(value, replyTo) =>
        ctx.pipeToSelf(db.save(id, value)) {
          case Success(value) => SaveSuccess
          case Failure(cause) => DBError(cause)
        }

        saving(value, replyTo)
    }
  }

  private def saving(state: String, replyTo: ActorRef[Done]): Behavior[Command] = {
    Behaviors.receiveMessage {
      case SaveSuccess =>
        replyTo ! Done
        buffer.unstashAll(active(state)) // create new state for next message.
      case DBError(cause) => throw cause
      case other =>
        buffer.stash(other)
        Behaviors.same
    }
  }
}
