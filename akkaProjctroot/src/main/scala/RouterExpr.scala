package routeExpr

import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.{Behaviors, Routers}

/** Routee
  */
object Worker {
  sealed trait Command
  case class DoLog(text: String) extends Command

  def apply(): Behavior[Command] = Behaviors.setup { ctx =>
    ctx.log.info("Starting worker")

    Behaviors.receiveMessage { case DoLog(text) =>
      ctx.log.info("Got message {}", text)
      Behaviors.same
    }
  }
}

class DoBroadcastLog(text: String) extends Worker.DoLog(text)
object DoBroadcastLog extends {
  def apply(text: String) = new DoBroadcastLog(text)
}

/** Router
  */

object PoolRouter {

  def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { ctx =>
    val pool = Routers.pool(poolSize = 4) {
      Behaviors.supervise(Worker()).onFailure[Exception](SupervisorStrategy.restart)
    }

    val router = ctx.spawn(pool, "worker-pool")

    (0 to 10).foreach { n =>
      router ! Worker.DoLog(s"msg $n")
    }

    /** Pool routers can be configured to identify messages intended to be broad-casted to all routees
      * Any message that the predicate returns true for will be broadcast to all routees.
      */
    /*
    val poolWithBroadcast   = pool.withBroadcastPredicate(_.isInstanceOf[DoBroadcastLog])
    val routerWithBroadcast = ctx.spawn(poolWithBroadcast, "pool-with-broadcast")

    // this will be sent to all 4 routees
    routerWithBroadcast ! DoBroadcastLog("msg")
     */

    Behaviors.empty
  }
}

object ReceptionistKeys {
  val serviceKey = ServiceKey[Worker.Command]("log-worker")
}

object GroupRouter {
  def apply(): Behavior[Nothing] = Behaviors.setup[Nothing] { ctx =>
    // this would likely happen elsewhere - if we create it locally we
    // can just as well use a pool
    val worker = ctx.spawn(Worker(), "worker")
    ctx.system.receptionist ! Receptionist.Register(ReceptionistKeys.serviceKey, worker)

    val group  = Routers.group(ReceptionistKeys.serviceKey)
    val router = ctx.spawn(group, "worker-group")

    // the group router will stash messages until it sees the first listing of registered
    // services from the receptionist, so it is safe to send messages right away
    (0 to 10).foreach { n =>
      router ! Worker.DoLog(s"msg $n")
    }

    Behaviors.empty
  }
}

object RouterExpr {
  import akka.actor.typed.ActorSystem

  def main(args: Array[String]): Unit = {
    // val system = ActorSystem[Nothing](routeExpr.PoolRouter(), "PoolRouterExample")
    val system = ActorSystem[Nothing](GroupRouter(), "GroupRouterExample")
    Thread.sleep(10000)
    system.terminate()
  }
}
