package im.mange.backdoor

import java.io.{ByteArrayOutputStream, IOException, OutputStream}

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Registration, Serializer}
import com.twitter.chill.{KryoInstantiator, KryoPool, SerDeState}

trait Regsiterable[T] {
  def register(kryo: Kryo): Registration
}

case class ClassAndSerializer[T](clazz: Class[T], serializer: Serializer[T]) extends Regsiterable[T] {
  override def register(kryo: Kryo) = kryo.register(clazz, serializer)
}

//SEE: https://github.com/twitter/chill/issues/226
object RegisteringKryoPool {
  def withByteArrayOutputStream(poolSize: Int, ki: KryoInstantiator, regsiterables: Seq[Regsiterable[_ <: Any]]) = {
    new KryoPool(poolSize) {
      protected def newInstance: SerDeState = {
        val kryo = ki.newKryo

        regsiterables.map(r => r.register(kryo))

        new SerDeState(kryo, new Input, new Output(new ByteArrayOutputStream)) {
          override def clear() {
            super.clear()
            val byteStream = output.getOutputStream.asInstanceOf[ByteArrayOutputStream]
            byteStream.reset()
          }

          override def outputToBytes: Array[Byte] = {
            output.flush()
            val byteStream = output.getOutputStream.asInstanceOf[ByteArrayOutputStream]
            byteStream.toByteArray
          }

          @throws(classOf[IOException])
          override def writeOutputTo(os: OutputStream) {
            output.flush()
            val byteStream = output.getOutputStream.asInstanceOf[ByteArrayOutputStream]
            byteStream.writeTo(os)
          }
        }
      }
    }
  }
}