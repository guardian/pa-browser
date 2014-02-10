import model.PA
import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions
import pa.GetPaClient
import play.api.mvc.{AnyContentAsFormUrlEncoded, MultipartFormData}
import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import util.ExecutionContexts
import xml.Utility.escape


class PlayerControllerTest extends Specification with GetPaClient with ExecutionContexts with NoTimeConversions {

  "test player index loads with players as choices" in new WithApplication {
    val Some(result) = route(FakeRequest(GET, "/player"))
    status(result) must equalTo(OK)
    val content = contentAsString(result)

    val expectedPlayers = Await.result (
      Future.sequence(PA.teams.prem.map { case (teamId, name) =>
        client.squad(teamId)
      }),
      1.seconds
    ).flatten
    expectedPlayers.foreach { player =>
      val name = escape(player.name).replace("'", "&#x27;")
      content must contain(name)
    }
  }

  "test redirects player head2head form submission to correct player page" in new WithApplication {
    val Some(result) = route(FakeRequest(POST, "/player/head2head", FakeHeaders(), AnyContentAsFormUrlEncoded(Map("player1" -> List("123456"), "player2" -> List("654321")))))
    status(result) must equalTo(SEE_OTHER)
    redirectLocation(result) must equalTo(Some("/player/head2head/123456/654321"))
  }

  "test player head to head card renders correctly" in new WithApplication {
    val Some(result) = route(FakeRequest(GET, "/player/head2head/237670/193388"))
    status(result) must equalTo(OK)
    val content = contentAsString(result)

    content must contain("Emmanuel Adebayor")
    content must contain("Jermain Defoe")
  }

  "test redirects player card form submission to correct player page" in new WithApplication {
    val Some(result) = route(FakeRequest(POST, "/player/card", FakeHeaders(), AnyContentAsFormUrlEncoded(Map("player" -> List("123456")))))
    status(result) must equalTo(SEE_OTHER)
    redirectLocation(result) must equalTo(Some("/player/card/123456"))
  }

  "test player head to head card renders correctly" in new WithApplication {
    val Some(result) = route(FakeRequest(GET, "/player/card/237670"))
    status(result) must equalTo(OK)
    val content = contentAsString(result)

    content must contain("Emmanuel Adebayor")
  }
}
