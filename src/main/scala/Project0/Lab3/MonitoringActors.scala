package Project0.Lab3

import akka.actor._
import akka.event.Logging
import akka.actor.typed.scaladsl.LoggerOps

case object Check

class MonitorActor(monitoredActor: ActorRef) extends Actor {
  context.watch(monitoredActor)
  val log = Logging(context.system, this)

  def receive = {
    case Check => log.info("I, the monitoring actor {} received the message",self)
    case Terminated(actorRef) if actorRef == monitoredActor =>
      log.info("The {} actor has stopped",monitoredActor)


  }
}

class MonitoredActor extends Actor {
  val log = Logging(context.system, this)
  def receive = {
    case message => log.info(message.toString)
  }
}

object Task3 extends App
{

  val system = ActorSystem("MonitoringSystem")
  val monitoredActor = system.actorOf(Props[MonitoredActor]())
  val monitorActor = system.actorOf(Props(classOf[MonitorActor], monitoredActor))

  monitorActor ! Check
  monitoredActor ! "Hello there"
  monitoredActor ! PoisonPill

  system.terminate()
}