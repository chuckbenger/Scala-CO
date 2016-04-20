package com.tkblackbelt.game.conf

import com.tkblackbelt.core.global.ServerConfig._
import com.tkblackbelt.core.util.Rainbow

import scala.concurrent.duration._
import Rainbow._
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
 * Contains global game configuration
 */
object GameConfig {

  import com.tkblackbelt.core.global.ServerConfig.config._

  private val base = "game"

  val version = Some(getString(s"$base.version")).getOrElse(1)
  val name = Some(getString(s"$base.name")).getOrElse("GameServer")
  val host = Some(getString(s"$base.host")).getOrElse("localhost")
  val port = Some(getInt(s"$base.port")).getOrElse(9958)


  object dbConfig {
    private val base = GameConfig.base + ".db"

    val url        = config.getString(s"$base.url")
    val driver     = config.getString(s"$base.driver")
    val username   = config.getString(s"$base.user")
    val password   = config.getString(s"$base.pass")
    val backupCmd  = config.getString(s"$base.backup.command")
    val backupTime = config.getInt(s"$base.backup.minutes").minutes
    val backupTo   = config.getString(s"$base.backup.destination")
    val backupName = config.getString(s"$base.backup.name")

    def backupString = s"Every $backupTime to $backupTo"

    override def toString: String = s"($url, $driver, $username)"
  }


  object Settings {
    private val base = s"${GameConfig.base}.settings"

    val sectorsX       = Some(getInt(s"$base.sectors_on_x")).getOrElse(10)
    val sectorsY       = Some(getInt(s"$base.sectors_on_y")).getOrElse(10)
    val maxPlayers     = Some(getInt(s"$base.max_players")).getOrElse(1000)
    val renderDistance = Some(getInt(s"$base.render_distance")).getOrElse(20)
    val portalDistance = Some(getInt(s"$base.portal_distance")).getOrElse(5)
    val mobStartingID  = Some(getInt(s"$base.mobstartingID")).getOrElse(400000)
    val persistClient  = Some(getInt(s"$base.persist_client")).getOrElse(5).minutes

    override def toString =
      s"""
      || Settings
      || ------------------------
      || X Sectors       = $sectorsX
      || Y Sectors       = $sectorsY
      || Max Players     = $maxPlayers
      || Render Distance = $renderDistance
      || Portal Distance = $portalDistance
      || Mob Starting ID = $mobStartingID
      || Persist Client  = $persistClient
       """
  }

  override def toString: String =
    s"""
      ||----------------------------
      || Game Server Configuration
      ||----------------------------
      || Version     = $version
      || name        = $name
      || host        = $host
      || port        = $port
      || DB          = $dbConfig
      || DB Backup   = ${dbConfig.backupString}
      ||----------------------------
      || ${Settings.toString}
      ||----------------------------
    """
}
















