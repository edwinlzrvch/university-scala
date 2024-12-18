import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

case class UniversityData(country: String, name: String, domains: List[String])

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val universityDataFormat: RootJsonFormat[UniversityData] = jsonFormat3(UniversityData.apply)
  implicit val universityDataListFormat: RootJsonFormat[List[UniversityData]] = listFormat[UniversityData]
}