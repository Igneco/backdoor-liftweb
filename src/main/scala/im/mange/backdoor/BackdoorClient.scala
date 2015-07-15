package im.mange.backdoor

import im.mange.backdoor.server.kryo.Cryopreservation
import im.mange.little.LittleClient
import io.shaka.http.ContentType.APPLICATION_JSON
import io.shaka.http.Request.POST

object BackdoorClient {
  def send(something: Any, baseUrl: String) = {
    val frozenJson = Cryopreservation.freeze(something)
    if (BackdoorConfig.debug) println(s"### Sending:[\n$frozenJson\n]")
    val request = POST(s"$baseUrl/backdoor").contentType(APPLICATION_JSON.value).entity(frozenJson)

    LittleClient.doRunRun(request) match {
      case Left(e) => throw e
      case Right(r) =>
        val result = r.entityAsString
        if (something.isInstanceOf[Debug]) result
        else if (!"OK".equals(result)) throw new RuntimeException(s"Problem in the backdoor: $result")
        else ""
    }
  }
}
