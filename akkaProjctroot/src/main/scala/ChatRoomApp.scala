package chatroom

import akka.{NotUsed, Done}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{
  ActorSystem,
  Behavior,
  PostStop,
  Signal,
  ActorRef,
  DispatcherSelector,
  Terminated
}

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object ChatRoom {

  /** First define possible protocols */
  sealed trait RoomCommand
  final case class GetSession(screeName: String, replyTo: ActorRef[SessionEvent])
      extends RoomCommand
  private final case class PublishSessionMessage(screenName: String, message: String)
      extends RoomCommand

  sealed trait SessionEvent
  final case class SessionGranted(handle: ActorRef[PostMessage])      extends SessionEvent
  final case class SessionDenied(reason: String)                      extends SessionEvent
  final case class MessagePosted(screenName: String, message: String) extends SessionEvent

  sealed trait SessionCommand
  final case class PostMessage(message: String)                 extends SessionCommand
  private final case class NotifyClient(message: MessagePosted) extends SessionCommand

  /** Logics */
  def apply(): Behavior[RoomCommand] = chatRoom(List.empty)

  private def chatRoom(sessions: List[ActorRef[SessionCommand]]): Behavior[RoomCommand] =
    Behaviors.receive { (context, message) =>
      message match {
        case GetSession(screenName, client) =>
          // create a child actor for further interaction with the client
          val ses = context.spawn(
            session(context.self, screenName, client),
            name = URLEncoder.encode(screenName, StandardCharsets.UTF_8.name)
          )
          client ! SessionGranted(ses)
          // The state is managed by changing behavior rather than using any variables.
          chatRoom(ses :: sessions)
        case PublishSessionMessage(screenName, message) =>
          val notification = NotifyClient(MessagePosted(screenName, message))
          sessions.foreach(_ ! notification)
          Behaviors.same
      }
    }

  /** The behavior that we declare here can handle both subtypes of RoomCommand. The `PublishSessionMessage` commands coming from the session Actors will trigger the dissemination of the contained chat room message to all connected clients. But we do not want to give the ability to send `PublishSessionMessage` commands to arbitrary clients, we reserve that right to the internal session actors we create—otherwise clients could pose as completely different screen names (imagine the GetSession protocol to include authentication information to further secure this). Therefore `PublishSessionMessage` has private visibility and can’t be created outside the ChatRoom object. -> Package Session command inside.
    */

  /** Gabbler doesn't have to know the details, but only command(PostMessage) to hit to post message.
    * In other words, session(Behavior[SessionCommand]) is the Mediator between ChatRoom and Gabber.
    */
  private def session(
      room: ActorRef[PublishSessionMessage],
      screenName: String,
      client: ActorRef[SessionEvent]
  ): Behavior[SessionCommand] = {
    Behaviors.receiveMessage {
      case PostMessage(message) =>
        // from client, publish to others via the room
        // Mediator to room
        room ! PublishSessionMessage(screenName, message)
        Behaviors.same
      case NotifyClient(message) =>
        // published from the room
        // Mediator to client
        client ! message
        Behaviors.same
    }
  }
}

object Gabbler {
  import ChatRoom._
  def apply(): Behavior[SessionEvent] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case SessionGranted(handle) =>
        handle ! PostMessage("Hello World!")
        Behaviors.same
      case MessagePosted(screenName, message) =>
        context.log.info("message has been posed by {}: {}", screenName, message)
        Behaviors.stopped
      case SessionDenied(_) =>
        Behaviors.stopped
    }
  }
}

object Main {

  /** This particular Main Actor is created using Behaviors.setup, which is like a factory for a behavior. Creation of the behavior instance is deferred until the actor is started, as opposed to Behaviors.receive that creates the behavior instance immediately before the actor is running. The factory function in setup is passed the ActorContext as parameter and that can for example be used for spawning child actor
    */
  def apply(): Behavior[NotUsed] = Behaviors.setup { context =>
    val chatRoom = context.spawn(ChatRoom(), "chatroom")
    val gabbler  = context.spawn(Gabbler(), "gabbler")
    context.watch(gabbler)
    chatRoom ! ChatRoom.GetSession("ol' Gabbler", gabbler)

    // we receive Terminated event due to having called context.watch for gabbler.
    Behaviors.receiveSignal { case (_, Terminated(_)) =>
      Behaviors.stopped
    }
  }

  def main(args: Array[String]): Unit = {
    ActorSystem(Main(), "ChatRoomDemo")
  }
}
