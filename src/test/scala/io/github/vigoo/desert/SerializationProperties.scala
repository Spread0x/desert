package io.github.vigoo.desert

import io.github.vigoo.desert.BinarySerialization._
import zio.URIO
import zio.test.Assertion._
import zio.test._

trait SerializationProperties {
  def canBeSerialized[R, A: BinaryCodec](rv: Gen[R, A]): URIO[R, TestResult] =
    check(rv) { value =>
      val resultValue =
        for {
          serialized <- serializeToArray(value)
          resultValue <- deserializeFromArray(serialized)
        } yield resultValue

      assert(resultValue)(isRight(equalTo(value)))
    }

  def canBeSerializedAndReadBack[A: BinaryCodec, B: BinaryCodec](value: A, check: Assertion[B]): TestResult = {
    val resultValue =
      for {
        serialized <- serializeToArray(value)
        resultValue <- deserializeFromArray[B](serialized)
      } yield resultValue

    assert(resultValue)(isRight(check))
  }

  def canBeSerializedAndReadBack[A: BinaryCodec, B: BinaryCodec](value: A, expectedB: B): TestResult = {
    canBeSerializedAndReadBack(value, equalTo(expectedB))
  }

  def cannotBeSerializedAndReadBack[A: BinaryCodec, B: BinaryCodec](value: A): TestResult = {
    val resultValue =
      for {
        serialized <- serializeToArray(value)
        resultValue <- deserializeFromArray[B](serialized)
      } yield resultValue

    assert(resultValue)(isLeft)
  }
}