package org.scalajs.tools.tsimporter

import java.io._

import org.scalajs.tools.tsimporter.Trees.DeclTree
import org.scalajs.tools.tsimporter.parser.TSDefParser

import scala.collection.immutable.PagedSeq
import scala.util.parsing.input.PagedSeqReader
import scala.util.{Failure, Success, Try}

object ImportUtil {
    def importTsFile(inputFileName: String, outputFileName: String, outputPackage: String): Either[String, File] = {
        val javaReader = new BufferedReader(new FileReader(inputFileName))
        val importTSResult = Try {
            val reader = new PagedSeqReader(seq = PagedSeq.fromReader(javaReader))
            parseDefinitions(reader).flatMap { definitions =>
                val output = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)))
                val result = Try(process(definitions, output, outputPackage)) match {
                    case Success(_) => Right(new java.io.File(outputFileName))
                    case Failure(ex) => Left(ex.getMessage)
                }
                output.close()
                result
            }
        } match {
            case Success(parse) => parse
            case Failure(ex) => Left(ex.getMessage)
        }

        javaReader.close()

        importTSResult
    }

    private def process(definitions: List[DeclTree], output: PrintWriter,
                        outputPackage: String) {
        new Importer(output)(definitions, outputPackage)
    }

    private def parseDefinitions(reader: scala.util.parsing.input.Reader[Char]): Either[String, List[DeclTree]] = {
        val parser = new TSDefParser
        parser.parseDefinitions(reader) match {
            case parser.Success(rawCode: List[DeclTree], _) =>
                Right(rawCode)

            case parser.NoSuccess(msg, next) =>
                Left(
                    "Parse error at %s\n".format(next.pos.toString) +
                        msg + "\n" +
                        next.pos.longString)
        }
    }
}
