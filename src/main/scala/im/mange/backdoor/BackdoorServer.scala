package im.mange.backdoor

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

object BackdoorMessageHandler {
  def ok = respond("OK")
  def fail(message: String) = respond(s"FAIL: $message")
  private def respond(content: String) = Full(PlainTextResponse(content))
}

trait BackdoorMessageHandler {
  def handle(message: Any): Box[LiftResponse]
}

object JsonRequestHandler extends Loggable {
  def handle(req: Req)(process: (JsonAST.JValue, Req) ⇒ Box[LiftResponse]) = {
    try {
      req.json match {
        case Full(json) ⇒ process(json, req)
        case o ⇒ {
          println(req.json)
          Full(PlainTextResponse(Seq(s"unexpected item in the bagging area ${o}").toString()))
        }
      }
    } catch {
      case e: Exception ⇒ {
        println("### Error handling request: " + req + " - " + e.getMessage)
        Full(PlainTextResponse(Seq(e.getMessage).toString))
      }
    }
  }
}