package Project1

import akka.actor.{Actor, ActorLogging, ActorSystem, Kill, Props}
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.stream.ActorMaterializer
import spray.json.*

import scala.util.control.NonFatal
import java.util.logging.{Level, Logger}
import scala.concurrent.duration.*
import DefaultJsonProtocol.*
import org.apache.commons.math3.distribution.PoissonDistribution as APoissonDistribution
import akka.http.scaladsl.unmarshalling._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.ByteString
import spray.json.*


import java.net.{HttpURLConnection, URL}
import scala.collection.mutable.Map

implicit val system: ActorSystem = ActorSystem("SSEExample")
implicit val materializer: ActorMaterializer = ActorMaterializer()


class PrintActor(meanSleepTime: FiniteDuration) extends Actor with ActorLogging {

  private val logger: Logger = Logger.getLogger(getClass.getName)

  private val curseWords = List("arse", "arsehole", "ass","asshole","bitch","cock","cocksucker","cunt","crap","dick","damn","dickhead","fuck",
  "fucker","horseshit","motherfucker","nigga","piss","pussy", "shit","slut","whore","hoe","wanker")

  override def receive: Receive = {
    case tweetText: ServerSentEvent =>
      try {
        val jsonStr = tweetText.data.stripPrefix("\n").split("\ndata:", 2)(1)

        val tweetJson = jsonStr.parseJson
        
          val tweet = tweetJson.asJsObject.fields("message").asJsObject.fields("tweet").asJsObject.fields("text").convertTo[String]
        val censoredTweet = curseWords.foldLeft(tweet) { (censored, curse) =>
          censored.replaceAll(raw"\b(?i)" + curse + raw"\b", "*" * curse.length)
        }



        val favorites = tweetJson.asJsObject.fields("message").asJsObject.fields("tweet").asJsObject.fields("user").asJsObject.fields("favourites_count").convertTo[Int]
        val retweets = tweetJson.asJsObject.fields("message").asJsObject.fields("tweet").asJsObject.fields("retweet_count").convertTo[Int]
        val followers = tweetJson.asJsObject.fields("message").asJsObject.fields("tweet").asJsObject.fields("user").asJsObject.fields("followers_count").convertTo[Int]

        var engagementRatio = calculateEngagementRatio(followers, retweets, favorites)
        var sentimentScore = getWordScoresFromEndpoint(censoredTweet)
        println(" ")
        println(s"This the tweet: $tweet")

        println(s"This the censored tweet: $censoredTweet with engagement score $engagementRatio and sentiment score $sentimentScore ")
        val lambda = meanSleepTime.toMillis.toDouble
        val poissonDist = new APoissonDistribution(lambda)
        val sleepTime = poissonDist.sample().millis
       // println(s"I slept $sleepTime millis")
        Thread.sleep(sleepTime.toMillis)
      } catch {
        case e : Exception =>
          throw e
      }

  }

   override def postRestart(reason: Throwable): Unit  =
  {
    println(s"I $self have been restarted ")
  }
  def calculateEngagementRatio(followers:Int, retweets:Int, favourites:Int): Double =
    {
      var engagementRatio:Double = 0
      if (followers != 0 )
       engagementRatio =(retweets + favourites).toDouble/followers
      else engagementRatio = 0
      return engagementRatio

    }



  def getWordScoresFromEndpoint(tweet: String): Double = {
    val url = new URL("http://localhost:4000/emotion_values")
    val connection = url.openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("GET")
    val body = scala.io.Source.fromInputStream(connection.getInputStream).mkString
    val wordScores = Map[String, Int]()
    val splitScores = body.split("\r\n").toList
    splitScores.foreach(element => {
      val list = element.split("\t")
      wordScores += (list(0) -> list(1).toInt)
    })
    connection.disconnect()

    val words = tweet.toLowerCase.split("\\s+").toList
    val scores = words.flatMap(wordScores.get)
    if (scores.isEmpty) 0.0 else scores.sum.toDouble

  }

}


// Print actor companion object
object PrintActor {
  case class Print(tweetText: ServerSentEvent)
  def props(meanSleepTime: FiniteDuration): Props = Props(new PrintActor(meanSleepTime))
}