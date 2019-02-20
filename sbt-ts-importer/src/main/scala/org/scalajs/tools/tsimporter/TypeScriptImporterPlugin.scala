package org.scalajs.tools.tsimporter

import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt._

object TypeScriptImporterPlugin extends AutoPlugin {
    override def requires: Plugins = ScalaJSPlugin
    override def trigger: PluginTrigger = allRequirements

    object autoImport extends TypeScriptImporterKeys {
        def importTsFile = InputKey[Either[String, sbt.File]]("ts-importer-import-task", "attempts to parse a given ts file and generate scala facades")
    }

    import autoImport._

    def importTsFileTask: Def.Initialize[InputTask[Either[String, sbt.File]]] = Def.inputTask {
        import sbt.complete.DefaultParsers._

        val s = streams.value
        val logger = s.log
        val args = spaceDelimited("<arg>").parsed
        val inputFile: String = args(0)
        val outputFile: String = args(1)
        val outputPkg: String = args(2)

        logger.info(s"Attempting to import $inputFile to $outputFile in pkg $outputPkg")
        importTsFileImpl(inputFile, outputFile, outputPkg)
    }

    def importTsFileImpl(inputFile: String, outputFile: String, outputPkg: String): Either[String, File] = {
        ImportUtil.importTsFile(inputFile, outputFile, outputPkg).map(_ => new java.io.File(outputFile))
    }

    // global settings

    lazy val baseGlobalSettings = Seq(

    )

    override def globalSettings = super.globalSettings ++ baseGlobalSettings

    // build settings

    lazy val baseBuildSettings = Seq(

    )

    override def buildSettings = super.buildSettings ++ baseBuildSettings

    // project settings

    lazy val baseProjectSettings = Seq(
        importTsFile := importTsFileTask.evaluated
    )

    override def projectSettings = super.projectSettings ++ baseProjectSettings
}