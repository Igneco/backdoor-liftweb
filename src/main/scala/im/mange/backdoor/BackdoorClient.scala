package im.mange.backdoor

import io.shaka.http.ContentType.APPLICATION_JSON
import io.shaka.http.Request.POST

object BackdoorClient {
  def send(something: Any, baseUrl: String) {
    val frozenJson = Cryopreservation.freeze(something)
    val request = POST(s"$baseUrl/backdoor").contentType(APPLICATION_JSON.value).entity(frozenJson)

    GoldenRetriever.doRunRun(request) match {
      case Left(e) => throw e
      case Right(r) =>
        val result = r.entityAsString
        if (!"OK".equals(result)) throw new RuntimeException(s"Problem in the backdoor: $result")
    }
  }
}