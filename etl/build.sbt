ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "org.ging.data-lake.etl-bronze"
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.5.1",
  "org.apache.spark" %% "spark-sql" % "3.5.1",
  "org.apache.spark" %% "spark-streaming" % "3.5.1",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.5.1",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.1"
)

libraryDependencies += "io.delta" %% "delta-spark" % "3.0.0"
