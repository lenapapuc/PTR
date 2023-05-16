package Project2.MessageBroker
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString
import scala.concurrent.duration._
import java.net.InetSocketAddress
import scala.collection.mutable



case class ConfirmMessageProcessed(topic: String)
case class ProcessNextMessage(topic: String)


class TopicDictionaryActor(subscriberActor: ActorRef) extends Actor {
  // Dictionary mapping topics to message queues
  val topicDictionary: mutable.Map[String, mutable.Queue[String]] = mutable.Map()

  def receive: Receive = {
    case AddToTopic(topic, content) =>
      if (topicDictionary.contains(topic)) {
        val queue = topicDictionary(topic)
        queue.enqueue(content)
      } else {
        val queue = mutable.Queue(content)
        topicDictionary.put(topic, queue)
      }
      println(topicDictionary)
      processMessages(topic) // Process messages for the topic

  }


  def processMessages(topic: String): Unit = {
    if (topicDictionary.contains(topic)) {
      val queue = topicDictionary(topic)
      val message = queue.dequeue()
      subscriberActor ! PublishMessage(topic, message) // Inform the subscriber actor

    }
  }
}




