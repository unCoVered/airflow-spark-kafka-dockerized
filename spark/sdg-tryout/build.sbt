name := "sdg-tryout"

version := "0.1"

scalaVersion := "2.12.10"

idePackagePrefix := Some("sdg.tryout")

libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.1"
libraryDependencies += "net.liftweb" %% "lift-json" % "3.4.3"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.18"
libraryDependencies += "org.scala-lang" % "scala-library" % "2.12.13"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}