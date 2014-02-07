package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import pa.{LeagueTableEntry, Player, TeamEventMatch, Client}
import util.{FutureZippers, ExecutionContexts}
import org.joda.time.DateMidnight
import java.net.URLDecoder
import model.{PrevResult, PA}
import play.api.templates.Html
import scala.concurrent.Future


object TablesController extends Controller with ExecutionContexts {

  def tablesIndex = Action { request =>
    Ok(views.html.leagueTables.tablesIndex(PA.competitions, PA.teams.all))
  }

  def redirectToTable = Action { implicit request =>
    val submission = request.body.asFormUrlEncoded.get
    val competitionId = submission.get("competitionId").get.head
    submission.get("focus").get.head match {
      case "top" => SeeOther(s"/tables/league/$competitionId/top")
      case "bottom" => SeeOther(s"/tables/league/$competitionId/bottom")
      case "team" =>
        val teamId = submission.get("teamId").get.head
        SeeOther(s"/tables/league/$competitionId/$teamId")
      case _ => SeeOther(s"/tables/league/$competitionId")
    }
  }

  def leagueTableFragment(competitionId: String, focus: String) = Action.async { request =>
    PA.competitions.find(_.competitionId == competitionId).map { league =>
      Client.leagueTable(league.competitionId, DateMidnight.now()).map { tableEntries =>
        val entries = focus match {
          case "top" => tableEntries.take(5)
          case "bottom" => tableEntries.reverse.take(5).reverse
          case "none" => tableEntries
          case teamId =>
            val before = tableEntries.takeWhile(_.team.id != teamId)
            val after = tableEntries.reverse.takeWhile(_.team.id != teamId).reverse
            val team = tableEntries.find(_.team.id == teamId).get
            before.reverse.take(2).reverse ++ List(team) ++ after.take(2)
        }
        Ok(views.html.leagueTables.leagueTable(league, entries))
      }
    } getOrElse Future.successful(InternalServerError(views.html.error("Please provide a valid league")))
  }

  def leagueTable(competitionId: String) = Action.async { request =>
    PA.competitions.find(_.competitionId == competitionId).map { league =>
      Client.leagueTable(league.competitionId, DateMidnight.now()).map { tableEntries =>
        Ok(views.html.leagueTables.leagueTable(league, tableEntries))
      }
    } getOrElse Future.successful(InternalServerError(views.html.error("Please provide a valid league")))
  }
}
