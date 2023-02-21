package Project0.Lab4

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, OneForOneStrategy, SupervisorStrategy}

case object KillWorker
case class WorkerMessage(message: String)

class WorkerActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case messages: java.lang.String => log.info(s"Confirmed $messages")
    case WorkerMessage(message) =>
      log.info(s"$self Received message: $message")
    case KillWorker =>
      log.info(s"Received kill message, stopping worker $self")
      //context.parent ! KillWorker
      context.stop(self)
  }
}

class SupervisorActor(numWorkers: Int) extends Actor with ActorLogging {
  var workers = (1 to numWorkers).map(_ => context.actorOf(Props[WorkerActor]()))

  def receive: Receive = {
    case messages: java.lang.String => log.info(s"Confirmed $messages")
    case WorkerMessage(message) =>
      log.info(s"Broadcasting message to all workers: $message")
      workers.foreach(worker => worker ! WorkerMessage(message))
    case KillWorker =>
      log.info("Received kill message, restarting all workers")
      workers.foreach(worker => context.stop(worker))
      workers.foreach(_=>SupervisorStrategy.Restart)
      //workers.foreach(println)
  }
}

object Main extends App {
  val system = ActorSystem("SupervisedPool")

  val supervisor = system.actorOf(Props(new SupervisorActor(numWorkers = 3)), "supervisor")
  var selection = system.actorSelection("/user/supervisor/$c")

  supervisor ! WorkerMessage("Hello, workers!")
  Thread.sleep(100)
  selection ! KillWorker
  Thread.sleep(1000)
  selection = system.actorSelection("/user/supervisor/$d")
  supervisor ! WorkerMessage("Hello again, workers!")

  system.terminate()
}
