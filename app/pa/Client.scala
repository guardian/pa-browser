package pa

import util.ExecutionContexts
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.ws.WS
import play.api.Play
import play.api.Play.current


object Client extends PaClient with Http with ExecutionContexts {

  def apiKey: String = Play.application.configuration.getString("pa.apiKey").getOrElse {
    throw new IllegalStateException("Missing PA_API_KEY system property")
  }

  override def GET(urlString: String): Future[pa.Response] = {
    println(urlString)
    WS.url(urlString).get().map { response =>
      pa.Response(response.status, response.body/*.dropWhile(_ != '<')*/, response.statusText)
    }
  }

  override def get(suffix: String)(implicit context: ExecutionContext): Future[String] = super.get(suffix)(context)
}
