package Project0.Lab1
import java.util
import scala.util.control.Breaks.*
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

  def toRoman(int : Int) : String = {

    val hundreds = List("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
    val tens = List("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
    val unities = List("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")


    var stBd = new StringBuilder("")

    stBd = stBd ++= hundreds(int / 100) ++= tens(int % 100 / 10) ++= unities(int % 10)

    return stBd.toString()
  }


}
