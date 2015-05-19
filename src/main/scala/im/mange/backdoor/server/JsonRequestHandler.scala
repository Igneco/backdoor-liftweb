package im.mange.backdoor.server

import im.mange.backdoor.{BackdoorConfig, Cryopreservation}
import net.liftweb.common.{Box, Full, Loggable}
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.{PlainTextResponse, PostRequest, Req, _}
import net.liftweb.json._

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
