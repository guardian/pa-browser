package pa

import util.ExecutionContexts
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.ws.WS
import play.api.Play
import play.api.Play.current
import org.joda.time.DateMidnight
import scala.io.Source
import java.io.{FileReader, File}
import scala.util.{Failure, Success}
import play.Logger


trait Client extends PaClient with Http with ExecutionContexts {

  def apiKey: String

  override def GET(urlString: String): Future[pa.Response]

  override def get(suffix: String)(implicit context: ExecutionContext): Future[String] = super.get(suffix)(context)
}
private object Client extends Client {

  override def apiKey: String = Play.application.configuration.getString("pa.apiKey").getOrElse {
    throw new IllegalStateException("Missing PA_API_KEY system property")
  }

  override def GET(urlString: String): Future[pa.Response] = {
    WS.url(urlString).get().map { response =>
      pa.Response(response.status, response.body/*.dropWhile(_ != '<')*/, response.statusText)
    }
  }

}
private object TestClient extends Client {
  override def GET(urlString: String): Future[Response] = ???

//  println(getClass.getClassLoader.getResource("testdata"))
//  println(play.api.Play.current.path)
//  play.api.Play.current.getExistingFile("test/testdata")
//  private val basePath = s"${getClass.getClassLoader.getResource("testdata").getFile}/"

  override def get(suffix: String)(implicit context: ExecutionContext): Future[String] = {
    val filename = {
      val path = suffix.replace("/", "__")
      path
    }
    try {
      val file = play.api.Play.current.getFile(s"test/testdata/$filename")
      val xml = scala.io.Source.fromFile(file, "UTF-8").getLines().mkString
      Future(xml)(context)
    } catch {
      case t: Throwable => {
        Logger.info(s"Fetching API response for $suffix")
        val response = Client.get(suffix)(context)
        response.onComplete {
          case Success(str) => writeToFile(filename, str)
          case Failure(writeError) => throw writeError
        }(context)
        response
      }
    }
  }

  def writeToFile(path: String, contents: String): Unit = {
    val writer = new java.io.PrintWriter(new File(path))
    try writer.write(contents) finally writer.close()
  }

  override def apiKey: String = "KEY"
}
trait GetPaClient {
  lazy val client = {
    if (play.Play.isTest) TestClient
    else Client
  }
}
