package actorDiscovery

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors

object PingService {
  val PingServiceKey = ServiceKey[Ping]("pingService")

  final case class Ping(replyTo: ActorRef[Pong.type])
  case object Pong

  def apply(): Behavior[Ping] = {
    Behaviors.setup { context =>
      context.system.receptionist ! Receptionist.Register(PingServiceKey, context.self)

      Behaviors.receiveMessage { case Ping(replyTo) =>
        context.log.info("Pinged by {}", replyTo)

        replyTo ! Pong
        Behaviors.same
      }
    }
  }
} // end of object PingService

object Pinger {
  def apply(pingService: ActorRef[PingService.Ping]): Behavior[PingService.Pong.type] = {
    Behaviors.setup { context =>
      pingService ! PingService.Ping(context.self)

      Behaviors.receiveMessage { _ =>
        context.log.info("{} was ponged!!", context.self)
        Behaviors.stopped
      }
    }
  }
} // end of object Pinger

/** Each time a new (which is just a single time in this example) PingService is registered the guardian actor spawns a Pinger for each currently known PingService. The Pinger sends a Ping message and when receiving the Pong reply it stops.
  */

object Guardian {
  def apply(): Behavior[Nothing] = {
    Behaviors
      .setup[Receptionist.Listing] { context =>
        context.spawnAnonymous(PingService())

        /** `Receptionist.Subscribe` will send Listing messages to the subscriber,
          * first with the set of entries upon subscription,
          * then whenever the entries for a key are changed.
          */
        context.system.receptionist ! Receptionist.Subscribe(
          PingService.PingServiceKey,
          context.self
        )

        Behaviors.receiveMessagePartial[Receptionist.Listing] {
          case PingService.PingServiceKey.Listing(listings) =>
            listings.foreach(ps => context.spawnAnonymous(Pinger(ps)))
            Behaviors.same
        }
      }
      .narrow
  }
} // end of object Guardian

/** Request a single Listing of the current state without receiving further updates by sending the Receptionist.Find message to the receptionist
  */
object PingManager {
  sealed trait Command
  case object PingAll                                               extends Command
  private case class ListingResponse(listing: Receptionist.Listing) extends Command

  def apply(): Behavior[Command] = {
    Behaviors.setup[Command] { context =>
      // converts Receptionist.Listing type to ListingResponse type
      val listingResponseAdapter = context.messageAdapter[Receptionist.Listing](ListingResponse)

      context.spawnAnonymous(PingService())

      Behaviors.receiveMessagePartial {
        case PingAll =>
          // Find reply to ActorRef[Receptionist.Listing]
          context.system.receptionist ! Receptionist.Find(
            PingService.PingServiceKey,
            listingResponseAdapter
          )
          Behaviors.same

        case ListingResponse(PingService.PingServiceKey.Listing(listings)) =>
          listings.foreach(ps => context.spawnAnonymous(Pinger(ps)))
          Behaviors.same
      }
    }
  }
}

object ActorDiscoveryExpr {
  import akka.actor.typed.ActorSystem

  def main(args: Array[String]): Unit = {
    val system = ActorSystem[Nothing](actorDiscovery.Guardian(), "PingPongExample")
    Thread.sleep(10000)
    system.terminate()
  }
}
