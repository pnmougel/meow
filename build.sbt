import com.typesafe.sbt.SbtNativePackager.autoImport._

//enablePlugins(DebianPlugin)
//
//enablePlugins(JavaAppPackaging)
//
//enablePlugins(LinuxPlugin)
//
//
//libraryDependencies += "de.sciss" % "weblaf" % "1.28"
//
//libraryDependencies += "org.apache.sanselan" % "sanselan" % "0.97-incubator"
//// libraryDependencies += "org.apache.commons" %% "commons-imaging" % "1.0-SNAPSHOT"
//libraryDependencies += "org.apache.xmlgraphics" % "batik-swing" % "1.8"
//
//libraryDependencies += "org.apache.xmlgraphics" % "batik-transcoder" % "1.8"
//
//libraryDependencies += "org.apache.xmlgraphics" % "batik-rasterizer" % "1.8"
//
//libraryDependencies += "org.apache.xmlgraphics" % "batik-xml" % "1.8"
//
//libraryDependencies += "org.apache.xmlgraphics" % "xmlgraphics-commons" % "2.0.1"
//
//libraryDependencies += "org.apache.xmlgraphics" % "batik-bridge" % "1.8"
//
//libraryDependencies += "org.apache.xmlgraphics" % "batik-codec" % "1.8"
//
//libraryDependencies += "org.apache.xmlgraphics" % "batik-util" % "1.8"
//
//libraryDependencies += "com.github.jai-imageio" % "jai-imageio-core" % "1.3.0"
//
//mainClass in(Compile, run) := Some("org.medit.gui.Main")
//
//mainClass in (packageBin) := Some("org.medit.gui.Main")
//
//mainClass in Compile := Some("org.medit.gui.Main")
//
//maintainer := "Pierre-Nicolas <pnmougel@gmail.com>"

//name := "medit"

// linuxPackageMappings in Debian := linuxPackageMappings.value


//linuxPackageMappings in Debian := {
//  val mappings = linuxPackageMappings.value
//  mappings map { linuxPackage =>
//    linuxPackage.mappings.map { case (file, name) =>
//      packageMapping((file, name)).withPerms("755")
//    }
//    linuxPackage.withPerms("0755").withConfig("false")
//  }
//  linuxPackageMappings.value
//}
//
//linuxPackageMappings += packageTemplateMapping(s"/usr")() withPerms("0755")
//linuxPackageMappings += packageTemplateMapping(s"/usr/share")() withPerms("0755")
//linuxPackageMappings += packageTemplateMapping(s"/usr/share/doc")() withPerms("0755")
//linuxPackageMappings += packageTemplateMapping(s"/usr/share/medit")() withPerms("0755")
//linuxPackageMappings += packageTemplateMapping(s"/usr/share/medit/bin")() withPerms("0755")
//linuxPackageMappings += packageTemplateMapping(s"/usr/share/medit/lib")() withPerms("0755")
//
//linuxPackageMappings in Debian += {
//  val file = sourceDirectory.value / "debian" / "copyright"
//  packageMapping((file, s"/usr/share/doc/${name.value}/copyright")) withPerms "0644" asDocs()
//}

//version := "1.0"
//
//name in Debian := "medit"
//
//version in Debian := "0.0.0ubuntu1"
//
//debianPackageDependencies in Debian ++= Seq("openjdk-7-jre")
//
//packageDescription in Linux := "Medit menu editor"
//
//packageSummary in Linux := "Menu editor for gnome desktop"
//
//packageDescription := """Menu editor for the gnome desktop"""
//
//maintainer in Debian := "Pierre-Nicolas <pnmougel@gmail.com>"
//
//debianChangelog in Debian := Some(file("src/debian/changelog"))
