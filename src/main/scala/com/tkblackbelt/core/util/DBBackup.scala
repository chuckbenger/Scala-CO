package com.tkblackbelt.core.util

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import com.tkblackbelt.game.GameServerMain.system.log
import com.tkblackbelt.game.GameServerMain.system
import com.tkblackbelt.game.conf.GameConfig.dbConfig._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scala.sys.process._

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
 * Backup the database
 */
object DBBackup {

  private val backupTimestamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")

  /**
   * Start scheduling database backups
   * @return returns the cancelable event
   */
  def scheduleBackups = {
    log.info(s"Backing up database every $backupTime minutes")
    system.scheduler.schedule(backupTime, backupTime) {
      backup()
    }(system.dispatcher)
  }

  /**
   * Runs the command to backup the database asynchronously
   */
  def backup() = Future {
    makeBackupDir()

    log.info(s"Starting database backup with command $backupCmd")
    val database = backupCmd.!!
    val file = writeBackupToDisk(database)
    log.info(s"Database backed up to $file")
  }

  /**
   * Create the backup directory if it doesn't exist
   */
  private def makeBackupDir() {
    val destination = new File(backupTo)
    if (destination.mkdirs())
      log.info(s"Created $backupTo database backup directory")
  }

  private def writeBackupToDisk(backup: String) = {
    val file = getNextFile
    FileUtil.printToFile(file)(_.print(backup))
    FileUtil.zip(file)
  }

  private def getNextFile = {
    val file = s"$backupName.${backupTimestamp.format(new Date())}.sql"
    val path = backupTo
    new File(s"$path$file")
  }
}


