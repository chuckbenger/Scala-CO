
import _root_.bintray.Plugin._
import spray.revolver.RevolverPlugin.Revolver

name := """Scala CO"""

version := "1.0"

scalaVersion := "2.10.3"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"


seq(bintrayResolverSettings:_*)

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "2.0.1",
  "mysql" % "mysql-connector-java" % "5.1.20",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.170",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "com.typesafe.akka" % "akka-actor_2.10" % "2.2.3",
  "org.xerial" % "sqlite-jdbc" % "3.7.15-M1",
  "joda-time" % "joda-time" % "2.3",
  "com.typesafe.atmos" %% "atmos-trace" % "1.3.0",
  "c3p0" % "c3p0" % "0.9.1.2",
  "com.github.scala-blitz" %% "scala-blitz" % "1.0-M1",
  "org.clapper" %% "classutil" % "1.0.5",
  "org.clapper" %% "grizzled-scala" % "1.1.6"
)

atmosSettings

atmosTestSettings

Revolver.settings

mainClass in Revolver.reStart := Some("com.tkblackbelt.game.GameServerMain")