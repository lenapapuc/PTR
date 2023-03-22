package Project1

import akka.actor.ActorSystem

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("SSEExample")
  val url = "http://localhost:4000/tweets/1"
  val url2 = "http://localhost:4000/tweets/2"

  val sseReaderActor = system.actorOf(SEReaderActor.props(url))
  val sseReaderActor2 = system.actorOf(SEReaderActor.props(url2))

}
