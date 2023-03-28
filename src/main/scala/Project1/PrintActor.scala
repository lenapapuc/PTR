package Project1

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Kill}
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.stream.ActorMaterializer
import spray.json.*

import scala.util.control.NonFatal
import java.util.logging.{Level, Logger}
import scala.concurrent.duration.*
import DefaultJsonProtocol.*
import org.apache.commons.math3.distribution.PoissonDistribution as APoissonDistribution



implicit val system: ActorSystem = ActorSystem("SSEExample")
implicit val materializer: ActorMaterializer = ActorMaterializer()


class PrintActor(meanSleepTime: FiniteDuration) extends Actor with ActorLogging {

  private val logger: Logger = Logger.getLogger(getClass.getName)


  override def receive: Receive = {
    case tweetText: ServerSentEvent =>
      try {
        val jsonStr = tweetText.data.stripPrefix("\n").split("\ndata:", 2)(1)

        val tweetJson = jsonStr.parseJson


          val tweet = tweetJson.asJsObject.fields("message").asJsObject.fields("tweet").asJsObject.fields("text").convertTo[String]
          println(s"This the tweet: $tweet received by actor $self")



        val lambda = meanSleepTime.toMillis.toDouble
        val poissonDist = new APoissonDistribution(lambda)
        val sleepTime = poissonDist.sample().millis
       // println(s"I slept $sleepTime millis")
        Thread.sleep(sleepTime.toMillis)
      } catch {
        case e : Exception =>
          println(e)
          throw e
      }

  }

   override def postRestart(reason: Throwable): Unit  =
  {
    println(s"I $self have been restarted due to $reason")
  }
}


// Print actor companion object
object PrintActor {
  case class Print(tweetText: ServerSentEvent)
  def props(meanSleepTime: FiniteDuration): Props = Props(new PrintActor(meanSleepTime))
}