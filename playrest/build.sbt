name := "playrest"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaJpa,
  cache,
  "com.google.guava" % "guava" % "16.0-rc1",
  "org.easymock" % "easymock" % "3.2",
  "org.powermock" % "powermock-module-junit4" % "1.5.2",
  "org.powermock" % "powermock-api-easymock" % "1.5.2",
  "org.hibernate" % "hibernate-entitymanager" % "4.2.2.Final",
  "postgresql" % "postgresql" % "8.4-702.jdbc4",
  "com.vividsolutions" % "jts" % "1.13",
  "org.postgis" % "postgis-jdbc" % "1.5.2",
  "org.hibernate" % "hibernate-spatial" % "4.0-M1" exclude("org.postgis", "postgis-jdbc")  
)

resolvers += (
	"Hibernate Spatial Repository" at "http://www.hibernatespatial.org/repository" 
)

ebeanEnabled := false

play.Project.playJavaSettings
