package im.mange.backdoor.server.kryo.serialiser

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import org.joda.time.{DateTimeZone, DateTime, LocalDate}

class DateTimeSerializer extends Serializer[DateTime] with java.io.Serializable {
  override def write(kryo: Kryo, output: Output, obj: DateTime) {
    output.writeInt(obj.getYear)
    output.writeInt(obj.getMonthOfYear)
    output.writeInt(obj.getDayOfMonth)
    output.writeInt(obj.getHourOfDay)
    output.writeInt(obj.getMinuteOfHour)
    output.writeInt(obj.getSecondOfMinute)
    output.writeInt(obj.getMillisOfSecond)
    output.writeString(obj.getZone.getID)
  }

  override def read(kryo: Kryo, input: Input, typeClass: Class[DateTime]) =
    new DateTime(input.readInt(), input.readInt(), input.readInt(), input.readInt(), input.readInt(), input.readInt(), input.readInt(),
      DateTimeZone.forID(input.readString())
    )
}
