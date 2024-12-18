lazy val akkaHttpVersion = sys.props.getOrElse("akka-http.version", "10.7.0")
lazy val akkaVersion = "2.10.0"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "3.3.3"
    )),
    name := "university-scala",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-pki" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.7",
    )
  )
