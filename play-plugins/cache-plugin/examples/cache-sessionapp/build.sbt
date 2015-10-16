name := """myapp"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "com.aerospike" % "aerospike-play-session-store_2.11" % "1.1",
  "com.aerospike" % "aerospike-play-cache_2.11" % "1.1",
  "org.projectlombok" % "lombok" % "1.16.4"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
