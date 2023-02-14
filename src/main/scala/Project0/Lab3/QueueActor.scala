package Project0.Lab3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.reflect.ClassTag
import scala.concurrent.ExecutionContext.Implicits.global

// Messages
case class Enqueue(value: Any)
case object Dequeue

class QueueActor extends Actor {
  private var queue: List[Any] = List.empty

  def receive: Receive = {
    case Enqueue(value) =>
      queue = queue :+ value
      sender() ! true
    case Dequeue =>
      if (queue.nonEmpty) {
        val head = queue.head
        queue = queue.tail
        sender() ! Some(head)
      } else {
        sender() ! None
      }
  }
}


class QueueHelper(queue: ActorRef) {

  implicit val timeout: Timeout = 5.seconds

  def enqueue(value: Any): Future[Boolean] = {
    (queue ? Enqueue(value)).mapTo[Boolean]
  }

  def dequeue(): Future[Option[Any]] = {
    (queue ? Dequeue).mapTo[Option[Any]]
  }

}

object QueueTest extends App {
  val system = ActorSystem("QueueTest")
  val queue = system.actorOf(Props[QueueActor](), "queue")

  val helper = new QueueHelper(queue)

  helper.enqueue("foo").onComplete {
    case util.Success(true) => println("Enqueue successful")
    case util.Success(false) => println("Enqueue failed")
    case util.Failure(ex) => println(s"Enqueue failed: ${ex.getMessage}")
  }

  helper.enqueue(42).onComplete {
    case util.Success(true) => println("Enqueue successful")
    case util.Success(false) => println("Enqueue failed")
    case util.Failure(ex) => println(s"Enqueue failed: ${ex.getMessage}")
  }

  helper.enqueue(true).onComplete {
    case util.Success(true) => println("Enqueue successful")
    case util.Success(false) => println("Enqueue failed")
    case util.Failure(ex) => println(s"Enqueue failed: ${ex.getMessage}")
  }

  helper.dequeue().onComplete {
    case util.Success(Some(value)) => println(s"Dequeue successful: $value")
    case util.Success(None) => println("Queue is empty")
    case util.Failure(ex) => println(s"Dequeue failed: ${ex.getMessage}")
  }

  helper.dequeue().onComplete {
    case util.Success(Some(value)) => println(s"Dequeue successful: $value")
    case util.Success(None) => println("Queue is empty")
    case util.Failure(ex) => println(s"Dequeue failed: ${ex.getMessage}")
  }

  helper.enqueue(3.14).onComplete {
    case util.Success(true) => println("Enqueue successful")
    case util.Success(false) => println("Enqueue failed")
    case util.Failure(ex) => println(s"Enqueue failed: ${ex.getMessage}")
  }

  helper.dequeue().onComplete {
    case util.Success(Some(value)) => println(s"Dequeue successful: $value")
    case util.Success(None) => println("Queue is empty")
    case util.Failure(ex) => println(s"Dequeue failed: ${ex.getMessage}")
  }

  helper.dequeue().onComplete {
    case util.Success(Some(value)) => println(s"Dequeue successful: $value")
    case util.Success(None) => println("Queue is empty")
    case util.Failure(ex) => println(s"Dequeue failed: ${ex.getMessage}")
  }

  helper.dequeue().onComplete {
    case util.Success(Some(value)) => println(s"Dequeue successful: $value")
    case util.Success(None) => println("Queue is empty")
    case util.Failure(ex) => println(s"Dequeue failed: ${ex.getMessage}")
          }

  system.terminate()

}