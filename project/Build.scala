import com.typesafe.sbt.packager.debian.DebianPlugin.autoImport._
import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.debian.DebianPlugin
import com.typesafe.sbt.packager.linux.LinuxPlugin
import com.typesafe.sbt.packager.linux.LinuxPlugin.autoImport._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import sbt.Keys._
import sbt._

object Build extends Build {
  val appName = "meow"

  val dependencies = Seq(
    "org.scalatest" %% "scalatest" % "1.9.1" % "test",
    "de.sciss" % "weblaf" % "1.28",
    "org.apache.sanselan" % "sanselan" % "0.97-incubator",
    "org.apache.xmlgraphics" % "batik-swing" % "1.8",
    "org.apache.xmlgraphics" % "batik-transcoder" % "1.8",
    "org.apache.xmlgraphics" % "batik-rasterizer" % "1.8",
    "org.apache.xmlgraphics" % "batik-xml" % "1.8",
    "org.apache.xmlgraphics" % "xmlgraphics-commons" % "2.0.1",
    "org.apache.xmlgraphics" % "batik-bridge" % "1.8",
    "org.apache.xmlgraphics" % "batik-codec" % "1.8",
    "org.apache.xmlgraphics" % "batik-util" % "1.8",
    "com.github.jai-imageio" % "jai-imageio-core" % "1.3.0",
    "commons-io" % "commons-io" % "2.4",
    "org.apache.xmlgraphics" % "batik-svggen" % "1.8",
    "org.jasypt" % "jasypt" % "1.9.2",
    "org.scalaj" %% "scalaj-http" % "1.1.5"
  )

  lazy val root = Project(id = "root", base = file("."))
    .enablePlugins(DebianPlugin)
    .enablePlugins(JavaAppPackaging)
    .enablePlugins(LinuxPlugin)
    .settings(
      fork := true,
      javaOptions += "-Disrelease=false",
      javaOptions in Runtime += "-Disrelease=false",
      name := appName,
      version := "1.0",
      packageDescription := """Menu editor for the gnome desktop""",
      libraryDependencies ++= dependencies
    )
    .settings(
      mainClass in(Compile, run) := Some("org.medit.gui.Runner"),
      mainClass in packageBin := Some("org.medit.gui.Runner"),
      mainClass in Compile := Some("org.medit.gui.Runner"),
      maintainer := "Pierre-Nicolas <pnmougel@gmail.com>"
    )
    .settings(
      name in Debian := appName,
      javaOptions in Universal += "-Disrelease=true",
      version in Debian := "1.0",
      maintainer in Debian := "Pierre-Nicolas <pnmougel@gmail.com>",
      debianChangelog in Debian := Some(file("src/debian/changelog")),
      // debianPackageDependencies in Debian ++= Seq("openjdk-8-jre"),
      packageDescription in Linux := "Medit menu editor",
      packageSummary in Linux := "Menu editor for gnome desktop"
    )
    .settings(
      linuxPackageMappings += packageTemplateMapping(s"/usr")() withPerms ("0755"),
      linuxPackageMappings += packageTemplateMapping(s"/usr/share")() withPerms ("0755"),
      linuxPackageMappings += packageTemplateMapping(s"/usr/share/doc")() withPerms ("0755"),
      linuxPackageMappings += packageTemplateMapping(s"/usr/share/${appName}")() withPerms ("0755"),
      linuxPackageMappings += packageTemplateMapping(s"/usr/share/${appName}/bin")() withPerms ("0755"),
      linuxPackageMappings += packageTemplateMapping(s"/usr/share/${appName}/lib")() withPerms ("0755"),
      linuxPackageMappings in Debian := {
        val mappings = linuxPackageMappings.value
        mappings map { linuxPackage =>
          linuxPackage.mappings.map { case (file, name) =>
            packageMapping((file, name)).withPerms("755")
          }
          linuxPackage.withPerms("0755").withConfig("false")
        }
        linuxPackageMappings.value
      },
      linuxPackageMappings in Debian += {
        val file = sourceDirectory.value / "debian" / "copyright"
        packageMapping((file, s"/usr/share/doc/${name.value}/copyright")) withPerms "0644" asDocs()
      }
    )
}