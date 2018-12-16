import com.typesafe.sbt.SbtNativePackager.autoImport.NativePackagerHelper._
import com.typesafe.sbt.packager.docker._
import sbt.file

name := "cassandra-schema-evolution"

version := "0.1"

scalaVersion := "2.12.8"

lazy val root = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    mappings in Universal := (mappings in Universal).value ++: directory(baseDirectory.value / "cassandra"),
    dockerBaseImage := "cassandra:3.11.2",
    dockerCommands ++= Seq(
      Cmd("COPY", "/opt/docker/cassandra/scripts/autoMigrate.sh", "/usr/local/bin/autoMigrate"),
      Cmd("COPY", "/opt/docker/cassandra/scripts/execute-cql.sh", "/usr/local/bin/execute-cql"),
      Cmd("COPY", "/opt/docker/cassandra/cql", "/cql"),
      Cmd("USER", "root"),
      Cmd("RUN", """["mkdir", "/usr/sbin/.cassandra"]"""),
      Cmd("RUN", """["chmod", "755", "/usr/local/bin/autoMigrate"]"""),
      Cmd("RUN", """["chmod", "755", "/usr/local/bin/execute-cql"]"""),
      Cmd("USER", "daemon")
    ),
    dockerEntrypoint := Seq("autoMigrate"),
    dockerRepository := Some("change_to_your_repository")
  )