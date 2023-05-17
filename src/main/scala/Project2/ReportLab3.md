# FAF.PTR16.1 -- Project 3
> **Performed by:** Papuc Elena, group FAF-201
>
>**Verified by:** asist. univ. Alexandru Osadcenco



**Message Broker** -- Telnet Actor

TelnetActor initializes the connection with telnet. This way, it runs a 
Tcp Server on port 23, and binds the server to the client. It also initialises
the other actors that are needed for processing the messages.
```scala
class TelnetActor extends Actor {
  val address = new InetSocketAddress("localhost", 23)
  val subscriptionActor: ActorRef = context.actorOf(Props[SubscriptionActor](), "subscriberActor")
  val topicDictionary: ActorRef = context.actorOf(Props(new TopicDictionaryActor(subscriptionActor)), "topicDictionaryActor")
  val deadLetterChannel:ActorRef = context.actorOf(Props[DeadLetterChannel](), "deadLetterChannel")
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
```

**Message Broker** -- TcpServer

The TcpServer Actor takes as an argument the Subscription Actor, TopicDictionary
Actor, and Dead Letter Channel. When it receives a connection, it registers 
the connected entity so that it can be used to respond to messages. Also, it initialises
the TcpConnectionHandler, this way each connection has a handler that processes
the messages from that connection.

```scala
class TcpServer(subscriptionActor: ActorRef, topicDictionary: ActorRef, deadLetterChannel: ActorRef) extends Actor {
  def receive: Receive = {
    case c @ Connected(remote, local) =>
      val connection = sender()
      val handler = context.actorOf(Props(new TcpConnectionHandler(connection, subscriptionActor, topicDictionary, deadLetterChannel)))
      connection ! Register(handler)
  }
}
```

**Message Broker** -- TcpConnectionHandler

The TcpConnectionHandler takes care of the messages received through a connection.
If they take the form topic:message, they are publisher messages. If they take the form 
subscribe(topic) and unsubscribe(topic) they come from the subscribing 
entity, and it sends it to the right actor for storing and transmission.

```scala
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
```

**SubscriptionActor** -- Subscribe Method

This specific case inside the SubscriptionActor takes care of the subscription
messages. It receives them, if the topic is already in the dictionary of topics,
 it saves the client's address in the subscription list of that topic, else it creates a new entry 
in the dictionary.
```scala
case Subscribe(topic, subscriber) =>
      if (subscribersByTopic.contains(topic)) {
        val subscribers = subscribersByTopic(topic)
        subscribersByTopic += (topic -> (subscribers + subscriber))
        subscriber ! Write(ByteString(s"Subscribed\n"))

      } else {
        subscribersByTopic += (topic -> Set(subscriber))
        subscriber ! Write(ByteString(s"Subscribed\n"))
      }
```

**SubscriptionActor** -- Unsubscribe Method

The same thing happens with the unsubscribe method, just when the unsubscribe
case is called, it removes entries from the lists and dictionary.
```scala
case Unsubscribe(topic, subscriber) =>
if(subscribersByTopic.contains(topic)) {
  val subscribers = subscribersByTopic(topic)
  if (subscribers.contains(subscriber))
  {subscribersByTopic += (topic -> (subscribers - subscriber))
    subscriber ! Write(ByteString(s"Unsubscribed\n"))}
  else subscriber ! Write(ByteString(s"You weren't subscribed to the topic\n"))
}
```

**SubscriptionActor** -- PublishMessage Method

When a PublishMessage comes to this actor, it looks up the topic in the dictionary.
If it exists, it gets the list of subscribers and sends all the content to the people in
the list.

```scala
case PublishMessage(topic, content) =>

      if (subscribersByTopic.contains(topic)) {

        val subscribers = subscribersByTopic(topic)
        subscribers.foreach { subscriber =>
            subscriber ! Write(ByteString(s"$content\n"))
          }
        }
```

**TopicDictionaryActor**

The TopicDictionaryActor takes care of the publish messages, it takes 
the topic and creates an entry from that topic in the dictionary, if it doesn't already
exist and as the value, we have a queue that contains the message for the queue. This way, every time a message comes, it 
is sent to the SubscriptionActor, and it is dequed.
```scala

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
```