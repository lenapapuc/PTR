# FAF.PTR16.1 -- Project 1
> **Performed by:** Papuc Elena, group FAF-201
>
>**Verified by:** asist. univ. Alexandru Osadcenco

## P1W1

**Minimal Tasks** -- SSE Reader

```scala
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
}
```

In order to read the streams from the Docker endpoint, I created a reader actor, that makes 
a request towards the provided uri and upon retreival, it maps each reponse string to SSE.
For each event, it sends the event to the Actor Pool and to the Hashtag Print Actor.

**Minimal Tasks** -- Print Actor

```scala
val jsonStr = tweetText.data.stripPrefix("\n").split("\ndata:", 2)(1)
val tweetJson = jsonStr.parseJson
val tweet = tweetJson.asJsObject.fields("message").asJsObject.fields("tweet").asJsObject.fields("text").convertTo[String]
println(s"This the tweet: $tweet")

```
The above code shows how upon receiving the SSE, it is parsed with the help of a json library.
Then, the text is chosen and printed.

**Main Tasks** -- Poisson Distribution

For the main tasks, I, first of all, created a second reader actor to process the second  stream of data.
Then, I used Poisson distribution to simulate a load on the print actor, where lambda is equal to 50 ms.
```scala
val lambda = meanSleepTime.toMillis.toDouble
val poissonDist = new APoissonDistribution(lambda)
val sleepTime = poissonDist.sample().millis
Thread.sleep(sleepTime.toMillis)
```

**Bonus Tasks** -- Hashtag Print Actor

For printing the hashtags every five seconds, I created a special actor. Upon receiving the json,
it parses it, and from entities, it chooses the hashtags, adds them to a map, and calculates the number of occurence
for each of them:

```scala
val lambda = meanSleepTime.toMillis.toDouble
val poissonDist = new APoissonDistribution(lambda)
val sleepTime = poissonDist.sample().millis
Thread.sleep(sleepTime.toMillis)
```
Then, after 5 seconds, it sends itself a message to print the top 5 hashtags and their number:
```scala
 case "printHashtagCounts" =>
try {

  //sort the List in descending order based on the second element of each tuple
  val topHashtags = hashtagCounts.toList.sortBy(-_._2).take(5)
  println(s"Top 5 hashtags: ${topHashtags.map(t => s"${t._1}: ${t._2}").mkString(", ")}")
  hashtagCounts = mutable.Map.empty
} catch {
  case NonFatal(e) =>
}
```
## P1W2

**Minimal Task** -- Worker Pool with Round Robin

In order to be able to have more actors doing the same thing, I implemented a Worker Pool
that sends the messages to the workers in a round robin fashion. For this, I initialize 3 actors and I use the
Scala method RoundRobinLogic()
```scala
 var router: Router = {
  val routees = Vector.fill(3) {
    val printActor = context.actorOf(PrintActor.props(50.milliseconds))
    context.watch(printActor)
    ActorRefRoutee(printActor)
  }

  Router(RoundRobinRoutingLogic(), routees)
}
override def receive: Receive = {
  case msg: ServerSentEvent =>
    router.route(msg, sender())
    taskManager.forward(msg)
}
```

**Main Task** Panic Messages

In order for the worker to restart once it receives the panic message, I initialized the
Worker Pool to implement a One for One Strategy when it receives an error:

```scala
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
  case _: Exception => SupervisorStrategy.Restart
}
```
 ## P1W3

**Minimal Task** Bad Words

In order for the print actor to be able to handle bad words, I created a list with bad 
words, and when they are encountered in the already extracted tweet, it is replaced with asterisks.
```scala
private val curseWords = List("arse", "arsehole", "ass","asshole","bitch","cock","cocksucker","cunt","crap","dick","damn","dickhead","fuck",
  "fucker","horseshit","motherfucker","nigga","piss","pussy", "shit","slut","whore","hoe","wanker")
val censoredTweet = curseWords.foldLeft(tweet) { (censored, curse) =>
  censored.replaceAll(raw"\b(?i)" + curse + raw"\b", "*" * curse.length)
}

```

**Main Task** Manager Actor

In order to manage the number of actors in the pool, I created a separate Manager Actor.
It counts the number of tweets received, and every second it sends itself a message to calculate
the medium number of tweets. If it was above a certain number it sends a message to the worker pool to increase its numbers,
if it was less, it sends it a message to decrease them
```scala

class Manager(poolSupervisor: ActorRef, taskThreshold: Int, maxWorkers: Int) extends Actor {
  import Manager._

  var taskCount = 0
  var workerCount = 3
  var tasksPerSeconds = 0

  context.system.scheduler.scheduleWithFixedDelay(1.seconds, 1.seconds, self, ResetTaskCount)(context.dispatcher)

  override def receive: Receive = {
    case msg: ServerSentEvent =>
      taskCount += 1

    case DecreaseWorkers =>
      if (workerCount > 1) {
        poolSupervisor ! DecreaseWorkers
        workerCount -= 1

      }
    case ResetTaskCount =>
      tasksPerSeconds = taskCount/(workerCount)
      println(s"Each actor did $tasksPerSeconds")
      if (tasksPerSeconds >= taskThreshold && workerCount < maxWorkers) {
        poolSupervisor ! IncreaseWorkers
        workerCount += 1
        println(s"Just added actor number $workerCount")
      }
      else if (tasksPerSeconds < taskThreshold )
      {
        self ! DecreaseWorkers
      }

      taskCount = 0;
  }

}
```

## P1W4

**Minimal Task** Sentiment Score and Engagement Ratio

For this task, I implemented 2 functions, one of them does a division to find the Engagement Ratio, from the numbers 
of retweets, favourites and followers:

```scala
val favorites = tweetJson.asJsObject.fields("message").asJsObject.fields("tweet").asJsObject.fields("user").asJsObject.fields("favourites_count").convertTo[Int]
val retweets = tweetJson.asJsObject.fields("message").asJsObject.fields("tweet").asJsObject.fields("retweet_count").convertTo[Int]
val followers = tweetJson.asJsObject.fields("message").asJsObject.fields("tweet").asJsObject.fields("user").asJsObject.fields("followers_count").convertTo[Int]

  def calculateEngagementRatio(followers:Int, retweets:Int, favourites:Int): Double =
    {
      var engagementRatio:Double = 0
      if (followers != 0 )
       engagementRatio =(retweets + favourites).toDouble/followers
      else engagementRatio = 0
      return engagementRatio

    }

```

For the next, I made a connection to the path "/emotion_values", I created a map from 
splitting the SSE by newline, then by tab, and when encountering these words in my tweets, I would get the sum of 
the Sentiment Scores.

```scala
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
```