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

object PlayerController extends Controller with ExecutionContexts {

  def playerCard(playerId: String) = Action.async { request =>
    FutureZippers.zip(
      Client.playerProfile(playerId),
      Client.playerStats(playerId, new DateMidnight(2013, 7, 1), DateMidnight.now()),
      Client.appearances(playerId, new DateMidnight(2013, 7, 1), DateMidnight.now())
    ).map { case (playerProfile, playerStats, playerAppearances) =>
      playerProfile.position match {
        case Some("Goal Keeper") => Ok(views.html.player.cards.goalkeeper(playerStats, playerAppearances))
        case Some("Defender") => Ok(views.html.player.cards.defensive(playerStats, playerAppearances))
        case _ => Ok(views.html.player.cards.offensive(playerStats, playerAppearances))
      }
    }
  }

  def head2Head(player1Id: String, player2Id: String) = Action.async { request =>
    FutureZippers.zip(
      Client.playerHead2Head(player1Id, player2Id, new DateMidnight(2013, 7, 1), DateMidnight.now()),
      Client.appearances(player1Id, new DateMidnight(2013, 7, 1), DateMidnight.now()),
      Client.appearances(player2Id, new DateMidnight(2013, 7, 1), DateMidnight.now())
    ).map { case ((player1h2h, player2h2h), player1Appearances, player2Appearances) =>
      Ok(views.html.player.playerHead2Head(player1h2h, player2h2h, player1Appearances, player2Appearances))
    }
  }
}