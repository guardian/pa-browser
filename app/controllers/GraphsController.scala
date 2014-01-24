package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import pa.Client
import util.ExecutionContexts
import org.joda.time.DateMidnight
import java.net.URLDecoder
import model.PA


object GraphsController extends Controller with ExecutionContexts {

  def chooseTeamsH2H = Action { implicit request =>
    Ok(views.html.headToHead.chooseTeams(PA.teams))
  }

  def renderTeamsH2H = Action.async { implicit request =>
    val submission = request.body.asFormUrlEncoded.get
    val team1Id = submission.get("team1").get.head
    val team2Id = submission.get("team2").get.head
    Client.teamHead2Head(team1Id, team2Id, new DateMidnight(2013, 07, 01), DateMidnight.now(), "100").map { case (team1, team2) =>
      Ok(views.html.headToHead.renderTeams(team1, team2))
    }
  }


  def choosePlayer = Action { implicit request =>
    Ok("")
  }

  def renderPlayer = Action { implicit request =>
    Ok("")
  }
}
