# FAF.PTR16.1 -- Project 0
> **Performed by:** Papuc Elena, group FAF-201
> 
>**Verified by:** asist. univ. Alexandru Osadcenco

## P0W1

**Task 1** -- Hello PTR

```scala
  def helloPTR(): Unit = {
  print("Hello PTR")
}
```

The above code prints into the console the message "Hello PTR"

**Task 2** -- Unit Test

```scala
import Project0.Lab0.helloPTR
import org.scalatest.funsuite.AnyFunSuite

import java.io.*
import scala.Console.*

class HelloSuite extends AnyFunSuite {
  test("Test 'Hello PTR'") {
    val stream = new ByteArrayOutputStream()
    scala.Console.withOut(stream) {
      helloPTR()
    }
    assert(stream.toString == "Hello PTR")
  }
}

```
With the help of the Scala library AnyFunSuite, I created a test that
tales the output from the function "helloPTR" that comes as an output stream. The Console.withOut() function
from the Console library catches the stream. Then, with the help of assert we can judge if the stream transformet to
string matches our needed output "Hello PTR".

## P0W2

**Task 1** -- Is integer prime?

```scala
  def isPrime(number: Int): Boolean = {
    if (number <= 1) false
    else if (number == 2) true
    else !(2 to math.sqrt(number).toInt).exists(x => number % x == 0)
  }
```

The function above checks whether an integer is prime. It makes sure
that 0 and 1 are not considered prime and 2 is. Then takes every number from 2 to the square root(inclusive) of the number that
we are checking and if there is a number that is a divisor of it, it outputs false. Else, it outputs true.

**Task 2** -- Cylinder Area

```scala
def cylinderArea(height: Int, radius: Int): Double = {
  var area = (2 * Math.PI * radius * height) + (2 * Math.PI * radius * radius)
  return area}
```

The function takes the height and radius of the base of the cylinder, applies the formula for 
cylinder area and outputs the results as a Double.

**Task 3** -- Unique Sum

```scala
def uniqueSum(list: List[Int]): Int = {
  var sum = 0
  val newList = list.distinct
  newList.foreach(sum += _)
  return sum
}
```

This function calculates the sum of distinct elements of a list.
It takes as an input a list, from that list it creates a list made only from the distinct elements of that list.
We introduce variable sum with initial value 0. Then, for each element of the new list, we increment the sum by that element.

**Task 4** -- N random elements

```scala
def extractRandomN(list: List[Int], n: Int): List[Int] = {

  if (list.length < n) throw new Exception("The number is larger than the list")
  else return Random.shuffle(list).take(n)
}
```
The function takes as an input a list of integers and an "n" that represents the number of random integers that have to be taken from the list.
If the list length is smaller than n, we get an error. If everything is ok, the list is shuffled randomly. 
Then we take the first n elements. This outputs a list of n random elements from a list.

**Task 5** -- Fibonacci elements
```scala
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
```

The previous implementation uses an iterative approach for the Fibonacci algorithm. 
We give initial values to the a, b and c variables. a is 0 and b is 1. a represents the first 
number in the sequence, b is the second one and c represents the result of their addition.
So, at each iteration, the c gets modified with the result of a and b addition.
a gets modified with the contents of b and b with the contents of c. At each iteration, b is added to the empty list that changes at each iteration.
At the final we output the completed list

**Task 6** -- Translator

```scala
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
```

The previous function takes a string and a dictionary and translates the string according to the dictionary.
We create a new array made from the words of the initial string by splitting it into words according to empty spaces and punctuation 
signs. For each item of the array, we check if it exists as key in our dictionary. If it exists, we add the value associated with the key to 
the dictionary. If it doesn't, we attach the value.

**Task 7** -- Smallest number

```scala
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
```

The previous function takes 3 numbers as input and reorders them in such way that 
the obtained number of length 3 is the smallest possible. To do that, 
we arrange the gotten numbers into a sequence and sort that implicitly from smallest to largest.
We add each number to the string builder if it's different than zero. Then, we check if the length of the obtained string matches
the length of the initial sequence. If it does, we output the string, if it doesn't, we add zeroes to the second position until 
the lengths match.

**Task 8** -- Rotate Left

```scala
def rotateLeft[A](list: List[A], n: Int): List[A] = {
  val rotatedList = list.drop(n % list.length) ++ list.take(n % list.length)
  return rotatedList
}

```

The previous function rotates a list of elements n places to the left. To do that 
we select all elements of the list except the first n module the length of the list and attach to it
the rest of the elements, this protects the function against ns that are larger than the length of the list.

**Task 9** -- Right Angle Triangles

```scala
 def listRightAngleTriangles(): List[(Int, Int, Int)] = {
  for {
    a <- 1 to 20
    b <- 1 to 20
    c = Math.sqrt(a * a + b * b)
    if (c == c.round)
  } yield (a, b, c.toInt)
}.toList

```

This task is supposed to yield all the sets a,b,c that would present the lengths of right angle triangles.
For this we build a for that takes the values of a and b up to 20 which is the task. It calculates c for each pair
and if c is an integer it outputs that set of integers. After that, the set is added to the final list.

**Task 10** -- Line Words

```scala
  def lineWords(array : Array[String]) : Array[String] =
{
  val row1 = Set('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p')
  val row2 = Set('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l')
  val row3 = Set('z', 'x', 'c', 'v', 'b', 'n', 'm')

  def isOneRowWord(word : String) : Boolean =
  {
    val lowerCaseWord = word.toLowerCase.toSet
    lowerCaseWord.subsetOf(row1) ||
      lowerCaseWord.subsetOf(row2) ||
      lowerCaseWord.subsetOf(row3)
  }

  return array.filter(isOneRowWord)
}
```

This function the words out of the input list that can be written using only one line of the keyboard.
At the beginning we introduce 3 sets of characters that represent each of the 3 rows of the keyboard.
We introduce an internal function that takes each word from the array and checks weather the transformation of that
word into a set of letters is a subset of any of the rows. The initial array is filtered by this function.


**Task 11** -- Caesar Encryption and Decryption

```scala
def giveMeChar(message : Char, key : Int) : Char =
{
  val alphabet = List('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
    's', 't', 'u', 'v', 'w','x', 'y', 'z')

  val newIndex = (alphabet.indexOf(message) + key) % alphabet.length
  return alphabet(newIndex)
}

def encode(string: String, key : Int) : String =
{
  var stBd = new StringBuilder("")
  for(i <- 0 until string.length)
  {
    stBd += giveMeChar(string(i), key)
  }
  return stBd.toString()
}

def decode(string: String, key: Int) : String =
{
  var stBd = new StringBuilder("")
  for(i <- 0 until string.length)
  {
    stBd += giveMeChar(string(i), 26 - key)
  }
  return stBd.toString()
}
```

This task is made out of three functions, the functions encode and decode are, per their names, supposed to encode
or decode a string according to the Caesar Cipher. They receive a key, that represent an integer that represents the
number of positions the alphabet has to be shifted. The formula for the new character is  newIndex = (alphabet.indexOf(message) + key) % alphabet.length
for each character in the string we apply the function giveMeChar that uses this formula to return the right character.

**Task 12** -- Letter Combinations

```scala
def letterCombinations(string : String): List[String] = {

  val mapping = Map('2' -> "abc", '3' -> "def", '4' -> "ghi", '5' -> "jkl",
    '6' -> "mno", '7' -> "pqrs", '8' -> "tuv", '9' -> "wxyz")

  var result = List("")

  for (st <- string) {
    val letters = mapping(st)
    var temp = List[String]()
    for (res <- result) {
      for (letter <- letters) {
        temp = temp :+ (res + letter)
      }
    }
    result = temp
  }

  return result
}
```

Receiving a combination of integers in the form of a string, we have to output the words that could be written
using these combinations.  We introduce a dictionary that maps each integer to the letters they represent.
We introduce an empty list. Then, for each number in the initial string, for each number in the results list, for each 
letter in the string that corresponds to the number, we attach to the temporary list the letter. Then, we attach the tomporary list to the permanent
list.


**Task 13** -- Remove Consecutive Duplicates

```scala
  def removeConsecutiveDuplicates(list: List[Int]) : List[Int] =
{
  var newList = List[Int]()

  for (i <- 0 until list.length-1)
  {
    if(list(i) != list(i+1))
      newList = newList :+ list(i)
  }

  if(list(list.length-1) != newList(newList.length-1))
    newList =  newList :+ list(list.length - 1)

  return newList
}

```

Starting with 0 and ending with the length of the initial list - 2, we attach to the new list the number from the 
initial list only if it's different from the one after it. Then, we check the last number from the list and if it's different
from the last number of the new list, we attach it, else we just return the new list.

**Task 14** -- Group Anagrams

```scala
  def groupAnagrams(array : Array[String]) : scala.collection.mutable.Map[String, List[String]] =
    {
      val map = scala.collection.mutable.Map.empty[String,List[String]]

      for(i<-0 until array.length)
        {
          var temporary =  List[String]()
          var sortedString = array(i).sorted
          if(!map.contains(sortedString)) {
            temporary = temporary :+ array(i)
            map.put(sortedString, temporary)
          }

          else
            map(sortedString) = map(sortedString) :+ array(i)

        }

      return map
      }
```

Given an array of strings we have to group the anagrams and map the set of their letters to them.
To do that, I initialized an empty map. Then, for all the members of my array. I sorted their letters. If I don't have
already that sorted string in my map , I add taht string to the list of values associated with that sorted string.
However, if I don't have it I add it as a key for my map.


**Task 15** -- Common Prefix

```scala
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

```

Given a list of strings, I have to find their largest common prefix. To do that, I find the shortest word from the list
and make it the first largest prefix. For as many iterations as the number of characters in this word, if the words of the initial
list start with the temporary largest prefix, then we return that word. However, if they don't, we drop one character from the right of the 
largest prefix and that word becomes the new largest prefix.

**Task 16** -- Roman Numerals

```scala
def toRoman(int : Int) : String = {

  val hundreds = List("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
  val tens = List("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
  val unities = List("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")


  var stBd = new StringBuilder("")

  stBd = stBd ++= hundreds(int / 100) ++= tens(int % 100 / 10) ++= unities(int % 10)

  return stBd.toString()
}
```

We introduce 3 lists of strings representing the hundreds, tens, and unities, for each list, we introduce 
the roman representation of the number at that position, at that index. For the numbers we receive,
we add to the string, the string at the index of the hundreds, the tens and the unities.

## P0W3
**Task 1** -- Echo Actor
```scala
class EchoActor extends Actor  {

    def receive = {
      case message => println(message)

    }
}
```

The first task is the implementation of an actor that would print any message it receives.

**Task 2** -- Modifying Actor
```scala
 class ModifyingActor extends Actor  {
  
  def receive = {
    case s: String => println("Received: " + s.toLowerCase)
    case i: Int =>  println("Received: " + (i+1))
    case _ =>  println("I don't know how to HANDLE this!")
  }
}
```

This task is the implementation of an actor that whilst receiving an integer, it increases it by one, whilst 
receiving a string it transforms it to lowercase, and if it receives any other message, it outputs a message that
is supposed to represent an error.

## Conclusion

Here goes your conclusion about this project..

## Bibliography

Here go all links / references to stuff you've used to study for this project..

