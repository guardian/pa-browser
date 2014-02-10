package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import pa.Client
import util.ExecutionContexts
import org.joda.time.DateMidnight
import java.net.URLDecoder
import scala.concurrent.Future


object SiteController extends Controller with ExecutionContexts {

  def index = Action {
    Ok(views.html.index())
  }

}
