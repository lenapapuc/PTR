name := "PTR"

version := "0.1"

scalaVersion := "3.2.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" %  "test"
val AkkaVersion = "2.7.0"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
libraryDependencies += "org.jsoup" % "jsoup" % "1.15.4"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.5.0"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.7.0"
libraryDependencies += "org.json4s" %% "json4s-native" % "4.0.6"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "4.0.6"