import model.PA
import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification
import org.specs2.time.NoTimeConversions
import pa.GetPaClient
import play.api.mvc.{AnyContentAsFormUrlEncoded, MultipartFormData}
import play.api.test._
import play.api.test.Helpers._
import scala.annotation.tailrec
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import util.ExecutionContexts


class TablesControllerTest extends Specification with GetPaClient with ExecutionContexts with NoTimeConversions {

  "test tables index page loads with leages" in new WithApplication {
    val Some(result) = route(FakeRequest(GET, "/tables"))
    status(result) must equalTo(OK)
    val content = contentAsString(result)
    PA.competitions.foreach(season => content must contain(season.name))
  }

  "submitting a choice of league redirects to the correct table page" in new WithApplication {
    val Some(result) = route(FakeRequest(POST, "/tables/league", FakeHeaders(), AnyContentAsFormUrlEncoded(Map("competitionId" -> List("100"), "focus" -> List("none")))))
    status(result) must equalTo(SEE_OTHER)
    redirectLocation(result) must equalTo(Some("/tables/league/100"))
  }

  "submitting league with 'focus' redirects to focus of selected league" in new WithApplication {
    val Some(resultTop) = route(FakeRequest(POST, "/tables/league", FakeHeaders(), AnyContentAsFormUrlEncoded(Map("competitionId" -> List("100"), "focus" -> List("top")))))
    status(resultTop) must equalTo(SEE_OTHER)
    redirectLocation(resultTop) must equalTo(Some("/tables/league/100/top"))

    val Some(resultBottom) = route(FakeRequest(POST, "/tables/league", FakeHeaders(), AnyContentAsFormUrlEncoded(Map("competitionId" -> List("100"), "focus" -> List("bottom")))))
    status(resultBottom) must equalTo(SEE_OTHER)
    redirectLocation(resultBottom) must equalTo(Some("/tables/league/100/bottom"))

    val Some(resultTeam) = route(FakeRequest(POST, "/tables/league", FakeHeaders(), AnyContentAsFormUrlEncoded(Map("competitionId" -> List("100"), "focus" -> List("team"), "teamId" -> List("19")))))
    status(resultTeam) must equalTo(SEE_OTHER)
    redirectLocation(resultTeam) must equalTo(Some("/tables/league/100/19"))
  }

  "can show full table for selected league" in new WithApplication {
    val Some(result) = route(FakeRequest(GET, "/tables/league/100"))
    status(result) must equalTo(OK)
    val content = contentAsString(result)
    content must contain("Spurs")
    countSubstring(content, "<tr>") must equalTo(21)
  }

  def countSubstring(str1:String, str2:String):Int={
    @tailrec
    def count(pos:Int, c:Int):Int={
      val idx=str1 indexOf(str2, pos)
      if(idx == -1) c else count(idx+str2.size, c+1)
    }
    count(0,0)
  }
}
