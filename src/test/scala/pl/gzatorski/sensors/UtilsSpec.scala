package pl.gzatorski.sensors

import org.scalatest.{Matchers, WordSpecLike}

class UtilsSpec extends Matchers with WordSpecLike {

  "Parse directory" should {

    "return Success when only one argument is provided" in {
      val dir = Utils.parseDirectory(Array("/path/to/dir"))

      dir.isSuccess shouldBe true
    }

    "return Failure when less than one argument is provided" in {
      val dir = Utils.parseDirectory(Array())

      dir.isFailure shouldBe true
    }

  }
}
