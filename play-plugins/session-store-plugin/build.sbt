name := """aerospike-play-session-store"""

version := "1.0"

organization := "com.aerospike"

// Enables publishing to maven repo
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }

// Do not append Scala versions to the generated artifacts
//crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

crossScalaVersions := Seq("2.11.6", "2.10.5")
scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
	"com.typesafe.play" %% "play" % "2.4.0" % "provided",
	"com.typesafe.play" %% "play-cache" % "2.4.0",
   	"org.apache.commons" % "commons-lang3" % "3.0.1",
   	"org.projectlombok" % "lombok" % "1.16.4",
   	"org.apache.commons" % "commons-lang3" % "3.0.1",
    "com.aerospike" % "aerospike-session-store" % "0.9.1",
    "org.slf4j" % "slf4j-simple" % "1.7.5"
)


publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := {
  <scm>
    <url>https://github.com/aerospike/aerospike-java-plugins/tree/master/play-plugins/session-store-plugin</url>
    <connection>scm:git:git@github.com:aerospike/aerospike-java-plugins.git</connection>
  </scm>
  <developers>
    <developer>
      <id>aerospike</id>
      <name>Aerospike Inc.</name>
      <url>http://aerospike.com</url>
    </developer>
  </developers>
}

homepage := Some(url(s"https://github.com/aerospike/aerospike-java-plugins/tree/master/play-plugins/session-store-plugin"))
licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

sonatypeProfileName := "com.playAerospikeSessionStore"
releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseTagName := s"aerospike-play-session-store-${(version in ThisBuild).value}"
releaseCrossBuild := true

import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  releaseStepCommand("sonatypeRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
