package Project1

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Kill, ActorRef}
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.stream.ActorMaterializer
import spray.json.*

import scala.util.control.NonFatal
import java.util.logging.{Level, Logger}
import scala.concurrent.duration.*
import akka.actor.typed.scaladsl.AskPattern.schedulerFromActorSystem
import scala.concurrent.duration._

object Manager {
  case class IncomingTask(taskCount: Int)
  case object IncreaseWorkers
  case object DecreaseWorkers
  case object ResetTaskCount

  def props(poolSupervisor: ActorRef, taskThreshold: Int, maxWorkers: Int): Props =
    Props(new Manager(poolSupervisor, taskThreshold, maxWorkers))
}

class Manager(poolSupervisor: ActorRef, taskThreshold: Int, maxWorkers: Int) extends Actor {
  import Manager._

  var taskCount = 0
  var workerCount = 3
  var tasksPerSeconds = 0

  context.system.scheduler.scheduleWithFixedDelay(1.seconds, 1.seconds, self, ResetTaskCount)(context.dispatcher)

  override def receive: Receive = {
    case msg: ServerSentEvent =>
      taskCount += 1

    case DecreaseWorkers =>
      if (workerCount > 1) {
        poolSupervisor ! DecreaseWorkers
        workerCount -= 1
        
      }
    case ResetTaskCount =>
      tasksPerSeconds = taskCount/(workerCount)
      println(s"Each actor did $tasksPerSeconds")
      if (tasksPerSeconds >= taskThreshold && workerCount < maxWorkers) {
        poolSupervisor ! IncreaseWorkers
        workerCount += 1
        println(s"Just added actor number $workerCount")
      }
      else if (tasksPerSeconds < taskThreshold )
      {
        self ! DecreaseWorkers
      }

      taskCount = 0;
  }

  }


