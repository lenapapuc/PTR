package Project0.Lab1

import sun.awt.SunHints.Key

object MainTasks {

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

  def groupAnagrams(array : Array[String]) : scala.collection.mutable.Map[String, List[String]] =
    {
      val map = scala.collection.mutable.Map.empty[String,List[String]]

      for(i<-0 until array.length)
        {
          var temporary =  List[String]()
          var sortedString = array(i).sorted
          if(!map.contains(sortedString))
            temporary = temporary :+ array(i)
            map.put(sortedString, temporary)

          else
            map(sortedString) = map(sortedString) :+ array(i)

        }

      return map

    }
}



