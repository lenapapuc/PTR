import Lab1.{Hello, Tasks}

@main def main(): Unit = {
    Hello.helloPTR()
    println()

    println(Tasks.isPrime(4))
    println(Tasks.cylinderArea(3,4))
    println(Tasks.reverseList(List(1,2,3)))
    println(Tasks.uniqueSum(List(1, 2, 4, 8, 4, 2 )))
    println(Tasks.extractRandomN(List(1, 2, 4, 8, 4, 2), 3))
    println(Tasks.firstFibonacciElements(7))
    val dictionary = Map(
      "mama" -> "mother",
      "papa" -> "father"

    )
    println(Tasks.translator(dictionary, "mama is with papa"))
    println(Tasks.smallestNumber(1,5,4))
    println(Tasks.rotateLeft(List(1,2,4,8,4), 3))
    println(Tasks.listRightAngleTriangles())
  }