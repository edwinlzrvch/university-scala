import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object UniversityMain extends JsonSupport {
  implicit val system: akka.actor.ActorSystem = ActorSystem("UniversityService")

  def main(args: Array[String]): Unit = {
    val fetchDataFuture: Future[List[UniversityData]] = Http().singleRequest(HttpRequest(uri = Constants.ExternalServiceUri)).flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          response.entity.toStrict(20.seconds).map { entity =>
            import spray.json.*
            val jsonString = entity.data.utf8String
            jsonString.parseJson.convertTo[List[UniversityData]]
          }

        case other =>
          Future.failed(new RuntimeException(s"Failed to fetch external data, status code: $other"))
      }
    }

    val universityData: List[UniversityData] = Await.result(fetchDataFuture, 120.seconds)

    val route = path("universities") {
      get {
        parameters("name".?, "country".?, "domain".?) { (maybeName, maybeCountry, maybeDomain) =>
          val name = maybeName.getOrElse("")
          val country = maybeCountry.getOrElse("")
          val domain = maybeDomain.getOrElse("")

          val lowerSearchName = name.toLowerCase
          val lowerCountry = country.toLowerCase
          val lowerDomain = domain.toLowerCase

          val results = universityData
            .filter(u => country.isEmpty || u.country.toLowerCase == lowerCountry)
            .filter(u => u.name.toLowerCase.contains(lowerSearchName))
            .filter(u => domain.isEmpty || u.domains.exists(_.toLowerCase == lowerDomain))

          complete(
            results match {
              case Nil => (StatusCodes.NotFound, "No matching results found.")
              case nonEmptyResults => nonEmptyResults
            }
          )
        }
      }
    }

    val server = Http().newServerAt("localhost", 9090).bind(route)

    server.map { _ =>
      println("Successfully started on localhost:9090")
    } recover {
      case ex =>
        println("Failed to start the server due to: " + ex.getMessage)
    }
  }
}
