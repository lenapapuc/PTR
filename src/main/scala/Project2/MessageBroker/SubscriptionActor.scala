package Project2.MessageBroker
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString

import java.net.InetSocketAddress
import scala.collection.mutable

import akka.actor.{Actor, ActorRef}
import scala.collection.mutable

case class Subscribe(topic: String, subscriber: ActorRef)
case class Unsubscribe(topic: String, subscriber: ActorRef)
case class PublishMessage(topic: String, content: String)
case class SendMessage(subscriber: ActorRef, content: String)
import scala.concurrent.duration._

class SubscriptionActor extends Actor {
  // Dictionary mapping topic to a set of subscribers
  var subscribersByTopic: mutable.Map[String, Set[ActorRef]] = mutable.Map()

  def receive: Receive = {
    case Subscribe(topic, subscriber) =>
      if (subscribersByTopic.contains(topic)) {
        val subscribers = subscribersByTopic(topic)
        subscribersByTopic += (topic -> (subscribers + subscriber))
        subscriber ! Write(ByteString(s"Subscribed\n"))

      } else {
        subscribersByTopic += (topic -> Set(subscriber))
        subscriber ! Write(ByteString(s"Subscribed\n"))
      }
      
    case Unsubscribe(topic, subscriber) =>
      if(subscribersByTopic.contains(topic)) {
        val subscribers = subscribersByTopic(topic)
        if (subscribers.contains(subscriber))
          {subscribersByTopic += (topic -> (subscribers - subscriber))
          subscriber ! Write(ByteString(s"Unsubscribed\n"))}
        else subscriber ! Write(ByteString(s"You weren't subscribed to the topic\n"))
      }
      else subscriber ! Write(ByteString(s"Wrong topic, try again\n"))


    case PublishMessage(topic, content) =>

      if (subscribersByTopic.contains(topic)) {

        val subscribers = subscribersByTopic(topic)
        subscribers.foreach { subscriber =>
            subscriber ! Write(ByteString(s"$content\n"))
          }
        }
      }
}

