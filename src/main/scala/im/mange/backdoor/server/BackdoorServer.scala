package im.mange.backdoor.server

import im.mange.backdoor.BackdoorConfig
import im.mange.backdoor.server.kryo.Cryopreservation
import net.liftweb.common.{Box, Full, Loggable}
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{PlainTextResponse, PostRequest, Req, _}
import net.liftweb.json._

//TODO: make 'backdoor' location configurable
object BackdoorServer extends RestHelper {
  private val missingHandler: Box[LiftResponse] = BackdoorMessageHandler.fail("Please configure a backdoor handler")

  var handler: Option[BackdoorMessageHandler] = None

  serve {
    case Req("backdoor" :: "alive" :: Nil, "", GetRequest) => BackdoorMessageHandler.ok

    case req@Req("backdoor" :: Nil, "", PostRequest) => {
      try {
        JsonRequestHandler.handle(req)((json, req) ⇒ {
          val prettyJson = pretty(render(json))
          if (BackdoorConfig.debug) println(s"### Received:[\n$prettyJson\n]")
          val message: Any = Cryopreservation.thaw(prettyJson)
          handler.fold(missingHandler)(_.handle(message))
        })
      } catch {
        case e: Exception ⇒ BackdoorMessageHandler.fail(e.getMessage)
      }
    }
  }
}





