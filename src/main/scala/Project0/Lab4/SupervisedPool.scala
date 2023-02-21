import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}


import akka.actor.Kill



class EchoActor extends Actor with ActorLogging {

  override def receive: Receive = {

    case message => log.info(s"$message")
    case message : Kill =>
      log.info(s"Received kill message")
      throw new Exception("Worker failed!")
  }



  override def postRestart(reason: Throwable): Unit = {
    log.info(s"Worker $self restarted")
  }
}

class SupervisorActor(numWorkers: Int) extends Actor with ActorLogging {

  var workers = (1 to numWorkers).map(_ => context.actorOf(Props[EchoActor]()))
  import Kill._

  override def receive: Receive = {

    case message =>
      log.info(s"Broadcasting message to all workers: $message")
      workers.foreach(worker => worker ! message)
    case message: Kill =>
      log.info("Received kill message, stopping all workers")
      context.children.foreach(context.unwatch)
      context.children.foreach(child => child ! Kill)
  }

      override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
        case _: Exception => SupervisorStrategy.Restart
      }
  }

object Main extends App {
  val system = ActorSystem("SupervisedPool")

  val supervisor = system.actorOf(Props(new SupervisorActor(numWorkers = 3)), "supervisor")
  var selection = system.actorSelection("/user/supervisor/$c")



  supervisor ! "Hello, workers!"
  Thread.sleep(1000)
  selection ! Kill
  Thread.sleep(1000)
  supervisor ! "Hello again, workers!"


  system.terminate()
}
