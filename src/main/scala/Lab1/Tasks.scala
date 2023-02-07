package Lab1

import scala.collection.mutable.ListBuffer
import scala.util.Random

object Tasks {

  def cylinderArea(height: Int, radius: Int): Double = {
    var area = (2 * Math.PI * radius * height) + (2 * Math.PI * radius * radius)
    return area
  }

  //find out if a number is prime
  def isPrime(number: Int): Boolean = {
    if (number <= 1) false
    else if (number == 2) true
    else !(2 to math.sqrt(number).toInt).exists(x => number % x == 0)
  }

  //reverse a list
  def reverseList(list: List[Int]): List[Int] = {
    val newList = list.reverse
    return newList
  }

  //the sum of the unique numbers in a list
  def uniqueSum(list: List[Int]): Int = {
    var sum = 0
    val newList = list.distinct
    newList.foreach(sum += _)
    return sum
  }

  //get n number of random elements from a list
  def extractRandomN(list: List[Int], n: Int): List[Int] = {

    if (list.length < n) throw new Exception("The number is larger than the list")
    else return Random.shuffle(list).take(n)

  }


  def firstFibonacciElements(number: Int): List[Int] = {
    var a: Int = 0
    var b: Int = 1
    var c = 0
    val i = 0
    val list = new ListBuffer[Int]()
    if (number eq 0) {
      list += a
      return list.toList
    }
    else {

      list += b

      for (i <- 2 to number) {
        c = a + b
        a = b
        b = c
        list += b
      }
    }
    return list.toList
  }

  //translates a sentence according to a dictionary
  def translator(dictionary: Map[String, String], originalString: String): String = {
    val newArray = originalString.split("[ ,.:;]")
    val sb = new StringBuilder("")
    for (i <- 0 until newArray.length) {
      if (dictionary.contains(newArray(i))) {
        sb ++= dictionary(newArray(i))
        sb ++= " "
      }
      else if (newArray(i) != "[,.:;]") {
        sb ++= newArray(i)
        sb ++= " "
      }
    }
    return sb.toString()
  }

  //finds the smallest resulting number from a sequence of integers
  def smallestNumber(number1: Int, number2: Int, number3: Int): Int = {

    var sequence = Seq(number2, number1, number3)
    val sorted = sequence.sorted
    val stBd = new StringBuilder("")
    for (i <- 0 until sorted.length)
      if (sorted(i) != 0) stBd ++= sorted(i).toString

    if (stBd.length != sorted.length)
      for (i <- 1 to (sorted.length - stBd.length())) {
        stBd.insert(i, 0.toString)
      }
    return stBd.toString().toInt
  }

  //rotates a list by n positions
  def rotateLeft[A](list: List[A], n: Int): List[A] = {
    val rotatedList = list.drop(n % list.length) ++ list.take(n % list.length)
    return rotatedList
  }

  def listRightAngleTriangles(): List[(Int, Int, Int)] = {
    for {
      a <- 1 to 20
      b <- 1 to 20
      c = Math.sqrt(a * a + b * b)
      if (c == c.round)
    } yield (a, b, c.toInt)

  }.toList

}
