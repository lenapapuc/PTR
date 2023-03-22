package Project0.Lab5

import java.io._
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

import scala.collection.JavaConverters._
import org.jsoup.Jsoup
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._


implicit val formats: Formats =  DefaultFormats

object HttpExample {

  def makeRequest(uri: String): HttpResponse[String] = {
    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
      .uri(URI.create(uri))
      .build()
    client.send(request, HttpResponse.BodyHandlers.ofString())
  }

  def printResponse(response: HttpResponse[String]): Unit = {
    println("Status code: " + response.statusCode())
    println("Headers: " + response.headers())
    println("Body: " + response.body())
  }

  def scrape(response: HttpResponse[String]): List[Map[String, Any]]= {
    val doc = Jsoup.parse(response.body())
    val quotes = doc.select(".quote").asScala

    quotes.map { quote =>
      val author = quote.select(".author").text()
      val text = quote.select(".text").text().replaceAll("\u201C|\u201D", "")
      val tags = quote.select(".tag").eachText().asScala.toList
      Map("author" -> author, "quote" -> text, "tags" -> tags)
    }.toList


  }

  def encode(data: List[Map[String, Any]]): Unit = {
    data.foreach(println)
    val jValue = Extraction.decompose(data)
    val jsonString = pretty(render(jValue))
    val writer = new PrintWriter(new File("quotes.json"))
    writer.write(jsonString)
    writer.close()

  }

  def main(args: Array[String]): Unit = {
    val response = makeRequest("https://quotes.toscrape.com/")
    printResponse(response)

    val data = scrape(response)
    encode(data)
  }
}
