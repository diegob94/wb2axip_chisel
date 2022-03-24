
ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0"

val chiselVersion = "3.5.1"

lazy val wb_chisel = (project in file("."))
  .settings(
    name := "wb_chisel",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % chiselVersion,
      "edu.berkeley.cs" %% "chiseltest" % "0.5.1" % "test",
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % chiselVersion cross CrossVersion.full),
  )

