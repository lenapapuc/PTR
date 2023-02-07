package Lab1
import scala.util.control.Breaks._
object BonusTasks
{
  def commonPrefix(list: List[String]) : String =
  {
     var minimumWord = list.minBy(_.length)

    breakable {
      while (minimumWord.length != 0)
      {
        if (list.forall(_.startsWith(minimumWord))) break
        else minimumWord = minimumWord.dropRight(1)
      }
    }
   return minimumWord
  }


}
