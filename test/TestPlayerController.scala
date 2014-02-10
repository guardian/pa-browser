import org.specs2.matcher.ShouldMatchers
import org.specs2.mutable.Specification
import play.api.test._
import play.api.test.Helpers._

class TestPlayerController extends Specification {

  "test homepage loads correctly" in new WithBrowser {
    browser.goTo("/")
    browser.$("h1").getText must equalTo("Football stats, tables, player cards and a whole lot more")
  }

  "test player head to head journey" in new WithBrowser(webDriver = FIREFOX) {
    val playerPage = browser.goTo("/player")
    Thread.sleep(5000)
    playerPage
      .fill("#id-player1").`with`("312242")
      .fill("#id-player2").`with`("243973")
    browser.submit(browser.$("form").get(0))
    browser.title() must equalTo("Player head to head")
    Thread.sleep(5000)
  }
}
