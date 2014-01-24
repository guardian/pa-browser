name := "pa-browser"

version := "1.0-SNAPSHOT"

resolvers += "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"

resolvers += Resolver.file("Local repo", file(System.getProperty("user.home") + "/.ivy2/local"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.gu" %% "pa-client" % "4.1-SNAPSHOT"
)     

play.Project.playScalaSettings
