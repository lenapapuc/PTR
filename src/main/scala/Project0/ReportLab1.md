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

**Task 6** -- N random elements

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
## Conclusion

Here goes your conclusion about this project..

## Bibliography

Here go all links / references to stuff you've used to study for this project..

