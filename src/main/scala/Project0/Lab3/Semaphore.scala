package Project0.Lab3
import akka.actor.{Actor, ActorRef, Props}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.Queue

case class Acquire()
case class Release()

class Semaphore(numPermits: Int) extends Actor {

  private var permits = numPermits
  private var waiting: Queue[ActorRef] = Queue.empty

  def receive: Receive = {
    case Acquire() =>
      if (permits > 0) {
        permits -= 1
        //println("Semaphore was aquired")
        sender() ! true
      } else {
        println("Waiting")
        waiting = waiting.enqueue(sender())
      }
    case Release() =>
      permits += 1
      if (waiting.nonEmpty) {
        val (next, rest) = waiting.dequeue
        waiting = rest
        next ! true
      }
  }
}

class TestActor(semaphore: ActorRef) extends Actor {
  def receive: Receive = {
    case "start" =>
      println(s"${self.path.name} started")
      semaphore ! Acquire()
    case true =>
      println(s"${self.path.name} acquired the semaphore")
      context.system.scheduler.scheduleOnce(1.second, self, "release")
    case "release" =>
      println(s"${self.path.name} releasing the semaphore")
      semaphore ! Release()
      context.system.scheduler.scheduleOnce(1.second, self, "done")
    case "done" =>
      println(s"${self.path.name} finished")
      context.stop(self)
  }
}

object SemaphoreTest extends App {

  val system = akka.actor.ActorSystem("SemaphoreTest")
  val semaphore = system.actorOf(Props(new Semaphore(2)), "semaphore")

  // Create 5 test actors that share the semaphore
  val actors = (1 to 5).map(i => system.actorOf(Props(new TestActor(semaphore)), s"actor$i"))


  
  // Start the test actors
  actors.foreach(_ ! "start")

  // Terminate the system after all actors finish
  system.scheduler.scheduleOnce(6.seconds) {
    system.terminate()
  }
}


