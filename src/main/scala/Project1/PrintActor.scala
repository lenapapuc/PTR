package Project1

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.stream.ActorMaterializer
import spray.json._

import DefaultJsonProtocol._

implicit val system: ActorSystem = ActorSystem("SSEExample")
implicit val materializer: ActorMaterializer = ActorMaterializer()
val url = "http://localhost:4000/tweets/1"

// SSE Reader actor
val sseReaderActor = system.actorOf(SEReaderActor.props(url))

// Print actor class
import scala.util.control.NonFatal
import java.util.logging.{Level, Logger}

class PrintActor extends Actor {

  private val logger: Logger = Logger.getLogger(getClass.getName)

  override def receive: Receive = {
    case PrintActor.Print(tweetText) =>
      try {
        val serverSentEvent: ServerSentEvent = ServerSentEvent(tweetText)

        val jsonStr = serverSentEvent.data.stripPrefix("\n").split("\ndata: ", 2)(1)

        // parse the JSON data
        val tweetJson = jsonStr.parseJson
        val tweet = tweetJson.asJsObject.fields("message").asJsObject.fields("tweet").asJsObject.fields("text").convertTo[String]

        println(s"This the tweet: $tweet")
      } catch {
        case NonFatal(e) =>
          logger.log(Level.WARNING, "Error occurred")
      }
  }
}




// Print actor companion object
object PrintActor {
  case class Print(tweetText: String)
  def props: Props = Props(new PrintActor)
}
