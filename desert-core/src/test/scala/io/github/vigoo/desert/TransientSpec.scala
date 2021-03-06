package io.github.vigoo.desert

import org.junit.runner.RunWith
import zio.test.environment.TestEnvironment
import zio.test._
import zio.test.Assertion._

import codecs._

@RunWith(classOf[zio.test.junit.ZTestJUnitRunner])
class TransientSpec extends DefaultRunnableSpec with SerializationProperties {
  private implicit val typeRegistry: TypeRegistry = TypeRegistry.empty

  case class TypeWithoutCodec(value: Int)

  case class TransientTest(a: Int,
                           @TransientField("def") b: String,
                           @TransientField(None) c: Option[Int],
                           d: Boolean,
                           @TransientField(TypeWithoutCodec(0)) e: TypeWithoutCodec) // derive must not require an implicit codec for transient fields!
  object TransientTest {
    implicit val codec: BinaryCodec[TransientTest] = BinaryCodec.derive(
      FieldAdded("c", None)
    )
  }

  override def spec: ZSpec[TestEnvironment, Any] =
    suite("Support for transient fields")(
      test("does not serialize a transient field") {
        canBeSerializedAndReadBack(
          TransientTest(1, "2", Some(3), d = true, TypeWithoutCodec(100)),
          TransientTest(1, "def", None, d = true, TypeWithoutCodec(0))
        )
      }
    )
}

object TransientSpec extends TransientSpec