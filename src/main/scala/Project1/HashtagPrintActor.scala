package Project1

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.http.javadsl.model.sse.ServerSentEvent
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import spray.json.*

import java.util.logging.{Level, Logger}
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.util.{Failure, Success}
import scala.util.control.NonFatal
import DefaultJsonProtocol._

class HashtagPrintActor(printInterval: FiniteDuration) extends Actor with ActorLogging {

  private val logger: Logger = Logger.getLogger(getClass.getName)
  private var hashtagCounts: mutable.Map[String, Int] = mutable.Map.empty

  // schedule a message to the actor every printInterval duration
  context.system.scheduler.schedule(printInterval, printInterval, self, "printHashtagCounts")(context.dispatcher)

  override def receive: Receive = {
    case HashtagPrintActor.AddTweet(tweetText) =>
      try {
        // extract hashtags from the tweet text
        val hashtags = tweetText
          .parseJson.asJsObject.fields("message").asJsObject.fields("tweet").asJsObject.fields("entities")
          .asJsObject.fields("hashtags").convertTo[Seq[JsObject]]
          .map(_.fields("text").convertTo[String].toLowerCase)

        // update the hashtag counts
        hashtags.foreach(hashtag => {
          val count = hashtagCounts.getOrElse(hashtag, 0) + 1
          hashtagCounts.update(hashtag, count)
        })
      } catch {
        case NonFatal(e) =>
      }
    case "printHashtagCounts" =>
      try {

        //sort the List in descending order based on the second element of each tuple
        val topHashtags = hashtagCounts.toList.sortBy(-_._2).take(5)
        println(s"Top 5 hashtags: ${topHashtags.map(t => s"${t._1}: ${t._2}").mkString(", ")}")
        hashtagCounts = mutable.Map.empty
      } catch {
        case NonFatal(e) =>
      }
  }
}

// Hashtag print actor companion object
object HashtagPrintActor {
  case class AddTweet(tweetText: String)

  def props(printInterval: FiniteDuration): Props = Props(new HashtagPrintActor(printInterval))
}
