package Project1


import akka.actor.{Actor, ActorKilledException, ActorRef, ActorSystem, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy}
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.routing.{ActorRefRoutee, Broadcast, RoundRobinPool, RoundRobinRoutingLogic, Router, SmallestMailboxRoutingLogic}
import scala.concurrent.duration.*


object PoolSupervisor {
  def props(): Props = Props(new PoolSupervisor())
}

class PoolSupervisor extends Actor {
  import Manager._

  var router: Router = {
    val routees = Vector.fill(3) {
      val printActor = context.actorOf(PrintActor.props(50.milliseconds))
      context.watch(printActor)
      ActorRefRoutee(printActor)
    }

    Router(RoundRobinRoutingLogic(), routees)
  }

  val taskManager = context.actorOf(Manager.props(self, 10, 10))

  override def receive: Receive = {
    case msg : ServerSentEvent =>
      router.route(msg, sender())
      taskManager.forward(msg)
    case IncreaseWorkers =>
      val newActor = context.actorOf(PrintActor.props(50.milliseconds))
      context.watch(newActor)
      router = router.addRoutee(ActorRefRoutee(newActor))
    case DecreaseWorkers =>
      if (router.routees.size > 1) {
        val actorRef = router.routees(util.Random.nextInt(router.routees.size)).asInstanceOf[ActorRefRoutee].ref

        println(s"Actor to be removed is $actorRef")

        router = router.removeRoutee(actorRef)
        var number = router.routees.size
        println(s"Just removed an actor. Now I have $number actors")

      }
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _: Exception => SupervisorStrategy.Restart
  }
}