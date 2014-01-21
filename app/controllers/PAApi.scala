package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import pa.Client
import util.ExecutionContexts
import org.joda.time.DateMidnight
import java.net.URLDecoder


object PAApi extends Controller with ExecutionContexts {

  def index = Action {
    Ok(views.html.index())
  }

  def browserSubstitution = Action { implicit request =>
    val submission = request.body.asFormUrlEncoded.getOrElse { throw new Exception("Could not read POST submission") }
    val query = getOneOrFail(submission, "query")
    val replacements = """(\{.*?\})""".r.findAllIn(query).toList.filter("{apiKey}"!=)
    val replacedQuery = replacements.foldLeft(query){ case (replacingQuery, replacement) =>
      replacingQuery.replace(replacement, getOneOrFail(submission, replacement.dropWhile('{'==).takeWhile('}'!=)))
    }
    SeeOther("/browser/%s".format(replacedQuery.dropWhile('/' ==)))
  }

  def browser(query: String) = Action.async { implicit request =>
    val replacedQuery = URLDecoder.decode(query, "UTF-8").replace("{apiKey}", Client.apiKey)
    Client.get("/" + replacedQuery).map{
      Ok(_).as("application/xml")
    }
  }


  def fixtures = Action.async { implicit request =>
    (request.getQueryString("competitionId") match {
      case None => Client.fixtures
      case Some(competitionId) => Client.fixtures(competitionId)
    }).map { fixtures =>
      Ok(views.html.fixtures(fixtures))
    }
  }

  def leagueTable = Action.async { implicit request =>

    val competitionId = request.getQueryString("competitionId").getOrElse { throw new Exception("Please provide a competitionId parameter") }
    Client.leagueTable(competitionId, DateMidnight.now()).map { leagueTableEntries =>
      Ok(views.html.tables(leagueTableEntries))
    }
  }

  private def getOneOrFail(submission:Map[String, scala.Seq[String]], key: String): String = {
    URLDecoder.decode(submission.get(key).getOrElse { throw new Exception("Missing required submission parameter, %s".format(key)) }.head, "UTF-8")
  }
}
