package scala

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

sealed abstract class Status(val code: Int)
case object On extends Status(1)
case object Off extends Status(0)

@RunWith(classOf[JUnit4])
class CaseObjectSerializationTest {

  @Test
  def canSerializeMatchError = {
    val barrayOut = new ByteArrayOutputStream()
    new ObjectOutputStream(barrayOut).writeObject(On)
    val barrayIn = new ByteArrayInputStream(barrayOut.toByteArray)
    val status = new ObjectInputStream(barrayIn).readObject().asInstanceOf[Status]
    assert(status == On)
  }
}
