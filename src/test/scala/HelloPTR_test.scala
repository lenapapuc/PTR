import Lab0.helloPTR
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
