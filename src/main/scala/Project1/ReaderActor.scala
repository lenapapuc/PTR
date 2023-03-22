package Project1
import akka.actor.{Actor, ActorSystem, Props}
import akka.stream._
import akka.stream.scaladsl._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.unmarshalling.sse.EventStreamUnmarshalling
import scala.concurrent.Future




  // SSE Reader actor class
  class SEReaderActor(url: String) extends Actor {
    val httpRequest = HttpRequest(uri = url)
    val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)
    val printActor = context.actorOf(PrintActor.props)

    responseFuture.foreach { response =>
      response.entity.dataBytes
        .map(_.decodeString("UTF-8"))
        .map(ServerSentEvent(_).data)
        .runForeach(tweetText => {
          printActor ! PrintActor.Print(tweetText)
        })(materializer)
    }(context.dispatcher)

    override def receive: Receive = {
      case _ =>
    }
  }

  // SSE Reader actor companion object
  object SEReaderActor {
    def props(url: String): Props = Props(new SEReaderActor(url))
  }


