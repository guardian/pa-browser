package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import pa.{Player, TeamEventMatch, Client}
import util.{FutureZippers, ExecutionContexts}
import org.joda.time.DateMidnight
import java.net.URLDecoder
import model.{PrevResult, PA}
import play.api.templates.Html
import scala.concurrent.Future


object TeamController extends Controller with ExecutionContexts {

  def squadPictures(teamId: String) = Action.async { request =>
    Client.squad(teamId).map { squad =>
      val players = squad.map { squadMember =>
        Player(squadMember.playerId, teamId, squadMember.name)
      }
      Ok(views.html.team.squadImages(players))
    }
  }

  def chooseTeamsHead2Head = Action { implicit request =>
    val teams = PA.teams.all
    Ok(views.html.headToHead.chooseTeams(teams))
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
      Client.teamHead2Head(team1Id, team2Id, new DateMidnight(2013, 7, 1), DateMidnight.now(), premLeagueId),
      Client.teamResults(team1Id, new DateMidnight(2013, 7, 1)),
      Client.teamResults(team2Id, new DateMidnight(2013, 7, 1))
    ).map { case ((team1H2H, team2H2H), team1Results, team2Results) =>
      Ok(views.html.headToHead.renderTeams(
        team1H2H, team2H2H,
        team1Results.map(PrevResult(_, team1Id)),
        team2Results.map(PrevResult(_, team2Id))
      ))
    }
  }
}
