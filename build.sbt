name := "rezervi"

version := "0.0.0"

scalaVersion := "2.13.1"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.10"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.0"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10"
libraryDependencies += "com.pauldijou" %% "jwt-spray-json" % "4.2.0"

libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "3.4.0"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.8"
libraryDependencies += "org.flywaydb" % "flyway-core" % "6.0.8"

libraryDependencies += "com.typesafe" % "config" % "1.4.0"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.2.0"

enablePlugins(JavaAppPackaging)

mappings in(Compile, packageDoc) := Seq()
