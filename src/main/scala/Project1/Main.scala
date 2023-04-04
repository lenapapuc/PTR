package Project1

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

import concurrent.duration.DurationInt

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("SSEExample")
  implicit val materializer: Materializer = ActorMaterializer()

  val url = "http://localhost:4000/tweets/1"
  val url2 = "http://localhost:4000/tweets/2"

  val meanSleepTime = 1.milliseconds

  val poolSupervisor = system.actorOf(PoolSupervisor.props(),"PoolSupervisor")
  val printActor2 = system.actorOf(PrintActor.props(meanSleepTime), "PrintActor")

  

  val hashtagPrintActor = system.actorOf(HashtagPrintActor.props(5.seconds))

  val sseReaderActor = system.actorOf(ReaderActor.props(url, poolSupervisor, hashtagPrintActor), "sseReader1")
  val sseReaderActor2 = system.actorOf(ReaderActor.props(url2, poolSupervisor, hashtagPrintActor), "sseReader2")
}
