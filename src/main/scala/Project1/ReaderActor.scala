package Project1
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global



class ReaderActor(url: String, printActor: ActorRef, hashtagPrintActor: ActorRef)(implicit materializer: Materializer) extends Actor with ActorLogging {
  val httpRequest = HttpRequest(uri = url)
  val responseFuture: Future[HttpResponse] = Http().singleRequest(httpRequest)

  responseFuture.onComplete {
    case Success(response) =>
      val source: Source[ServerSentEvent, Any] = response.entity.dataBytes
        .map(_.decodeString("UTF-8"))
        .map(ServerSentEvent(_))

      source.runForeach(event => {
        val tweetText = event.data.stripPrefix("\n").split("\ndata: ", 2)(1)
        //println(tweetText)
        printActor ! event
        hashtagPrintActor ! HashtagPrintActor.AddTweet(tweetText)
      })

    case Failure(ex) =>
      log.error(ex, "Request failed")
      context.stop(self)
  }

  override def receive: Receive = {
    case _ =>
  }
}

// Reader actor companion object
object ReaderActor {
  def props(url: String, printActor: ActorRef, hashtagPrintActor: ActorRef)(implicit materializer: Materializer): Props =
    Props(new ReaderActor(url, printActor, hashtagPrintActor))
}

