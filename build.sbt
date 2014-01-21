name := "pa-browser"

version := "1.0-SNAPSHOT"

resolvers += "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.gu" %% "pa-client" % "4.0"
)     

play.Project.playScalaSettings
