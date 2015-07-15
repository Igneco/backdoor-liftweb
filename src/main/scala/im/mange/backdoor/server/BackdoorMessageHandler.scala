package im.mange.backdoor.server

import im.mange.backdoor.BackdoorConfig
import net.liftweb.common.{Box, Full, Loggable}
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{PlainTextResponse, PostRequest, Req, _}
import net.liftweb.json._

trait BackdoorMessageHandler {
  def handle(message: Any): Box[LiftResponse]
}

object BackdoorMessageHandler {
  def ok = respond("OK")
  def fail(message: String) = respond(s"FAIL: $message")
  def respond(content: String) = Full(PlainTextResponse(content))
}