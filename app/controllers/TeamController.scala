package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import pa.{GetPaClient, Player, TeamEventMatch, Client}
import util.{FutureZippers, ExecutionContexts}
import org.joda.time.DateMidnight
import java.net.URLDecoder
import model.{PrevResult, PA}
import play.api.templates.Html
import scala.concurrent.Future


object TeamController extends Controller with ExecutionContexts with GetPaClient {

  def teamIndex = Action { implicit request =>
    val teams = PA.teams.all
    Ok(views.html.team.teamIndex(teams))
  }

  def redirectToSquadPictures = Action { request =>
    val submission = request.body.asFormUrlEncoded.get
    val teamId = submission.get("team").get.head
    SeeOther(s"/team/images/$teamId")
  }

  def squadPictures(teamId: String) = Action.async { request =>
    client.squad(teamId).map { squad =>
      val players = squad.map { squadMember =>
        Player(squadMember.playerId, teamId, squadMember.name)
      }
      Ok(views.html.team.squadImages(teamId, players, PA.teams.all))
    }
  }

  def redirectToTeamHead2Head = Action { implicit request =>
    val submission = request.body.asFormUrlEncoded.get
    val team1Id = submission.get("team1").get.head
    val team2Id = submission.get("team2").get.head
    SeeOther(s"/team/head2head/$team1Id/$team2Id")
  }

  def teamHead2Head(team1Id: String, team2Id: String) = Action.async { implicit request =>

    val premLeagueId = "100"

    FutureZippers.zip(
      client.teamHead2Head(team1Id, team2Id, new DateMidnight(2013, 7, 1), DateMidnight.now(), premLeagueId),
      client.teamResults(team1Id, new DateMidnight(2013, 7, 1)),
      client.teamResults(team2Id, new DateMidnight(2013, 7, 1))
    ).map { case ((team1H2H, team2H2H), team1Results, team2Results) =>
      Ok(views.html.team.teamHead2head(
        team1H2H, team2H2H,
        team1Results.map(PrevResult(_, team1Id)),
        team2Results.map(PrevResult(_, team2Id))
      ))
    }
  }
}
