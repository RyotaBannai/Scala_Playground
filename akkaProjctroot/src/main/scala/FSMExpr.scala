package fsmExpr

import scala.concurrent.duration._
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer}

object Buncher {

  // FSM event becomes the type of the message Actor supports
  /*
  ・SetTarget is needed for starting it up, setting the destination for the Batches to be passed on
  ・Queue will add to the internal queue
  ・Flush will mark the end of a burst
   */
  sealed trait Event
  final case class SetTarget(ref: ActorRef[Batch]) extends Event
  final case class Queue(obj: Any)                 extends Event
  case object Flush                                extends Event
  private case object Timeout                      extends Event

  sealed trait Data
  case object Uninitialized                                       extends Data
  final case class Todo(target: ActorRef[Batch], queue: Seq[Any]) extends Data

  final case class Batch(obj: Seq[Any])

  def apply(): Behavior[Event] = idle(Uninitialized)

  /*
  case class copy: https://stackoverflow.com/questions/7249396/how-to-clone-a-case-class-instance-and-change-just-one-field-in-scala
   */
  private def idle(data: Data): Behavior[Event] = Behaviors.receiveMessage[Event] { msg =>
    (msg, data) match {
      case (SetTarget(ref), Uninitialized) => idle(Todo(ref, Vector.empty))
      case (Queue(obj), t @ Todo(_, vec))  => active(t.copy(queue = vec :+ obj))
      case _                               => Behaviors.unhandled
    }
  }

  private def active(data: Todo): Behavior[Event] = Behaviors.withTimers[Event] { timers =>
    // instead of FSM state timeout
    /*
      If a new timer is started with the same message the previous is cancelled.
      It is guaranteed that a message from the previous timer is not received,
      even if it was already enqueued in the mailbox when the new timer was started.
     */
    timers.startSingleTimer(Timeout, 1.second)
    Behaviors.receiveMessagePartial {
      case Flush | Timeout =>
        data.target ! Batch(data.queue)
        idle(data.copy(queue = Vector.empty))
      case Queue(obj) => active(data.copy(queue = data.queue :+ obj))
    }
  }
}
