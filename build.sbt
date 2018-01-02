name := "scala-minesweeper-wui"
organization := "de.htwg.scala-minesweeper"
version := "0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//resolvers += Resolver.sonatypeRepo("snapshots")
//resolvers += Resolver.mavenLocal

scalaVersion := "2.12.4"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.196"
libraryDependencies += "org.webjars" % "jquery" % "3.2.1"
libraryDependencies += "com.typesafe.akka" % "akka-actor_2.12" % "2.4.16"
libraryDependencies += "com.typesafe.akka" % "akka-remote_2.12" % "2.4.16"

libraryDependencies += "de.htwg.scala-minesweeper" %% "scala-minesweeper-core" % "0.1-SNAPSHOT"