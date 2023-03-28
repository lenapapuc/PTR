package Project1

import akka.actor.{Actor, ActorKilledException, ActorRef, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.routing.{ActorRefRoutee, RoundRobinPool, Router, RoundRobinRoutingLogic}

import akka.routing.SmallestMailboxRoutingLogic
import scala.concurrent.duration.*

object PoolSupervisor {

  def props(): Props = Props(new PoolSupervisor())
}

class PoolSupervisor extends Actor {


  val router: Router = {
    val routees = Vector.fill(3) {
      val printActor = context.actorOf(PrintActor.props(50.milliseconds))
      context.watch(printActor)
      ActorRefRoutee(printActor)

    }

    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receive: Receive = {
    case msg =>
      router.route(msg, sender())
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _: Exception => SupervisorStrategy.Restart
  }

}
