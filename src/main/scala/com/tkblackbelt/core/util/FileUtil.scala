package com.tkblackbelt.core.util

import java.io.{File, FileOutputStream}
import java.util.zip.{ZipEntry, ZipOutputStream}

import scala.io.Source

/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 charlesbenger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * Contains various helper methods for Files
 */
object FileUtil {

  /**
   * Opens a file for writing
   * @param f the file to write to
   * @param op the function to perform the writes
   */
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try {
      op(p)
    } finally {
      p.close()
    }
  }

  /**
   * Zip a file
   * @param file the file to zip
   * @param deleteOriginal whether to delete the original unzipped file
   * @return The zipped file or none
   */
  def zip(file: File, deleteOriginal: Boolean = true): Option[String] = {

    val zipName = file.getAbsolutePath + ".zip"
    val fos = new FileOutputStream(zipName)
    val zos = new ZipOutputStream(fos)
    val ze = new ZipEntry(file.getName)

    try {
      zos.putNextEntry(ze)
      val in = Source.fromFile(file).getLines().mkString("\r\n").getBytes
      zos.write(in)
      if (deleteOriginal) file.delete()
      Some(zipName)
    } catch {
      case _: Throwable =>
        None
    }
    finally {
      zos.closeEntry()
      zos.close()
    }
  }

}
