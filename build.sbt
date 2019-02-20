import sbt._

scalaVersion := "2.12.8"

val commonSettings = Seq(
    organization := "org.scalajs.tools",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.8"
)

lazy val root = (project in file("."))
    .settings(commonSettings: _*)
    .settings(
        aggregate in doc := false
    )
    .aggregate(
        `scala-js-ts-importer`,
        `scala-js-ts-importer-plugin`
    )

lazy val `scala-js-ts-importer` = project.in(file("ts-importer"))
    .settings(commonSettings: _*)
    .settings(
        description := "TypeScript importer for Scala.js",
        mainClass := Some("org.scalajs.tools.tsimporter.Main"),
        libraryDependencies ++= Seq(
            "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.5",
            "com.beachape" %% "enumeratum" % "1.5.13",
            "com.github.scopt" %% "scopt" % "3.7.0",
            "org.scalatest" %% "scalatest" % "3.0.4" % Test
        )
    )

lazy val samples = project
    .settings(commonSettings: _*)
    .enablePlugins(ScalaJSPlugin)

lazy val `scala-js-ts-importer-plugin` = (project in file("sbt-ts-importer"))
    .settings(commonSettings: _*)
    .enablePlugins(SbtPlugin)
    .settings(
        description := "TypeScript importer plugin for SBT",
        libraryDependencies ++= Seq(
            Defaults.sbtPluginExtra(
                "org.scala-js" % "sbt-scalajs" % "0.6.26",
                sbtBinaryVersion.value,
                scalaBinaryVersion.value
            )
        )
    )
    .dependsOn(`scala-js-ts-importer`)

// ======================
// Java Compiler Options
// ======================
javacOptions ++= Seq(
    "-source", "1.8",
    "-target", "1.8",
    "-Xlint"
)

// =======================
// Scala Compiler Options
// =======================
scalacOptions in ThisBuild ++= Seq(
    "-target:jvm-1.8",
    "-encoding", "UTF-8",
    "-deprecation", // warning and location for usages of deprecated APIs
    "-feature", // warning and location for usages of features that should be imported explicitly
    "-unchecked", // additional warnings where generated code depends on assumptions
    "-Xlint", // recommended additional warnings
    "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
    "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
    "-Ywarn-inaccessible",
    "-Ywarn-dead-code"
)
