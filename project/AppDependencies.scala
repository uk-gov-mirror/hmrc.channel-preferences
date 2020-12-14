import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-27"  % "3.2.0",
    "com.iterable"            %% "swagger-play"               % "2.0.1",
    "uk.gov.hmrc"             %% "simple-reactivemongo"       % "7.31.0-play-27"
  )

  // The fork of swagger-play requires a version of jackson-databind version >= 2.9.0 and < 2.10.0
  // Other libraries pulling in later jackson-databind include http-verbs and logback-json-logger
  val dependencyOverrides: Seq[ModuleID] = Seq(
    "com.fasterxml.jackson.core"     % "jackson-databind"        % "2.9.9"
  )

  val test = Seq(
    // "uk.gov.hmrc"             %% "bootstrap-test-play-27"   % "3.2.0" % Test,
    // "org.scalatest"           %% "scalatest"                % "3.2.3"  % Test,
    // "com.typesafe.play"       %% "play-test"                % current  % Test,
    // "com.vladsch.flexmark"    %  "flexmark-all"             % "0.36.8" % "test, it",
    // "uk.gov.hmrc"            %% "service-integration-test" % "0.13.0-play-27" % "test, it",
    // "org.scalatestplus.play"  %% "scalatestplus-play"       % "4.0.3"  % "test, it",
    // "org.pegdown"            % "pegdown"                   % "1.6.0"          % "test, it"

    "uk.gov.hmrc"            %% "bootstrap-test-play-27"   % "3.2.0"          % Test,
    "com.typesafe.play"      %% "play-test"                % current          % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"       % "4.0.3"          % "test, it",
    "uk.gov.hmrc"            %% "service-integration-test" % "0.13.0-play-27" % "test, it",
    "org.pegdown"            % "pegdown"                   % "1.6.0"          % "test, it"
  )
}
