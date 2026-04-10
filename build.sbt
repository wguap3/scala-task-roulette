ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.7"

// Версии библиотек — выносим отдельно чтобы удобно менять
val http4sVersion     = "0.23.27"
val doobieVersion     = "1.0.0-RC4"
val circeVersion      = "0.14.9"
val catsEffectVersion = "3.5.4"

lazy val root = (project in file("."))
  .settings(
    name := "scala-task-roulette",

    libraryDependencies ++= Seq(

      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
      "org.http4s" %% "http4s-circe"        % http4sVersion,

      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-core"    % circeVersion,

      "org.tpolecat" %% "doobie-core"     % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari"   % doobieVersion,

      "org.typelevel" %% "cats-effect" % catsEffectVersion,

      "com.github.pureconfig" %% "pureconfig-core"        % "0.17.6",
      "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.17.6",

      "org.postgresql" % "postgresql" % "42.7.3"
    )
  )