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


class SiteControllerTest extends Specification with GetPaClient with ExecutionContexts with NoTimeConversions {

  "test index page loads" in new WithApplication {
    val Some(result) = route(FakeRequest(GET, "/"))
    status(result) must equalTo(OK)
  }
}
