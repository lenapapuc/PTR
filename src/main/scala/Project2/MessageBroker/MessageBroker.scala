package Project2.MessageBroker

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.io.Tcp
import akka.io.Tcp._
import akka.io.{IO, Tcp}
import akka.util.ByteString

import java.net.InetSocketAddress
case class AddToTopic(topic: String, content: String)
class TelnetActor extends Actor {
  val address = new InetSocketAddress("localhost", 23)

  // Create the subscription actor and topic dictionary actor
  val subscriptionActor: ActorRef = context.actorOf(Props[SubscriptionActor](), "subscriberActor")
  val topicDictionary: ActorRef = context.actorOf(Props(new TopicDictionaryActor(subscriptionActor)), "topicDictionaryActor")
  val deadLetterChannel:ActorRef = context.actorOf(Props[DeadLetterChannel](), "deadLetterChannel")

  // Create a Tcp server actor to handle incoming connections
  val tcpServer: ActorRef = context.actorOf(Props(new TcpServer(subscriptionActor, topicDictionary, deadLetterChannel)))

  override def preStart(): Unit = {
    IO(Tcp)(context.system) ! Bind(tcpServer, address)
  }

  def receive: Receive = {
    case _: Bound =>
      println("Telnet server is listening on port 23.")

    case CommandFailed(_: Bind) =>
      println("Failed to bind to port 23. Exiting...")
      context.stop(self)
  }
}

// Tcp server actor
class TcpServer(subscriptionActor: ActorRef, topicDictionary: ActorRef, deadLetterChannel: ActorRef) extends Actor {
  def receive: Receive = {
    case c @ Connected(remote, local) =>
      val connection = sender()
      val handler = context.actorOf(Props(new TcpConnectionHandler(connection, subscriptionActor, topicDictionary, deadLetterChannel)))
      connection ! Register(handler)
  }
}

class TcpConnectionHandler(connection: ActorRef, subscriptionActor: ActorRef, topicDictionary: ActorRef, deadLetterChannel: ActorRef) extends Actor {
  def receive: Receive = {
    case Received(data) =>
      val message = data.utf8String.trim
      val parts = message.split(":")
      if (parts.length == 1 && parts(0).startsWith("subscribe(") && parts(0).endsWith(")")) {
        val subTopic = parts(0).substring(10, parts(0).length - 1)
        val actualTopic = subTopic.trim
        subscriptionActor ! Subscribe(actualTopic, connection)
      }
      else if (parts.length == 1 && parts(0).startsWith("unsubscribe(") && parts(0).endsWith(")")) {
        val subTopic = parts(0).substring(12, parts(0).length - 1)
        val actualTopic = subTopic.trim
        subscriptionActor ! Unsubscribe(actualTopic, connection)
      }
      else if (parts.length == 2) {
        val topic = parts(0)
        val content = parts(1)
        // Handle regular message
        topicDictionary ! AddToTopic(topic, content)
        sender() ! Write(ByteString(s"You sent topic: $topic\n"))
        sender() ! Write(ByteString(s"You sent message: $content\n"))
      }
      else {
        connection !  Write(ByteString(s"Wrong format, try again\n"))
        deadLetterChannel ! message

      }


    case Tcp.PeerClosed =>
      println("Connection closed.")
      context.stop(self)
  }
}
