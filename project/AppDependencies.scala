import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-27"  % "3.2.0",
    "uk.gov.hmrc"             %% "simple-reactivemongo"       % "7.31.0-play-27",
    "uk.gov.hmrc"             %% "emailaddress"               % "3.5.0",
    "com.typesafe.play"       %% "play-json-joda"             % "2.7.3",
    "org.typelevel"           %% "cats-core"                  % "2.5.0"
  )

  // The fork of swagger-play requires a version of jackson-databind version >= 2.9.0 and < 2.10.0
  // Other libraries pulling in later jackson-databind include http-verbs and logback-json-logger
  val dependencyOverrides: Seq[ModuleID] = Seq(
    "com.fasterxml.jackson.core"     % "jackson-databind"        % "2.9.9"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-27"   % "3.2.0"          % Test,
    "com.typesafe.play"      %% "play-test"                % current          % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"       % "4.0.3"          % "test, it",
    "uk.gov.hmrc"            %% "service-integration-test" % "0.13.0-play-27" % "test, it",
    "org.pegdown"            % "pegdown"                   % "1.6.0"          % "test, it",
    "org.mockito"            % "mockito-core"              % "3.9.0"         % "test",
    "com.github.tomakehurst" % "wiremock-jre8"             % "2.27.2"         % "test,it",
    "org.mock-server"        % "mockserver-netty"          % "5.11.2"         % "it"
  )
}
