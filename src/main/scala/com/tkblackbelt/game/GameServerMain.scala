package com.tkblackbelt.game

import java.net.InetSocketAddress
import java.util.Properties

import akka.actor.{ActorSystem, Props}
import com.tkblackbelt.core.util.Rainbow._
import com.tkblackbelt.core.util.{DBBackup, HeaderPrinter}
import com.tkblackbelt.game.conf.{GameConfig, PluginManager}
import com.tkblackbelt.game.models.database.GameItems
import com.tkblackbelt.game.models.flatfile.StaticItems
import com.tkblackbelt.game.server.GameConnectionServerActor
import com.tkblackbelt.game.server.map.DMaps
import com.tkblackbelt.game.util.Benchmark

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
 * Starts the game server
 */
object GameServerMain extends App with HeaderPrinter {

  val system = ActorSystem(GameConfig.name)

  import com.tkblackbelt.game.GameServerMain.system.log

  DBBackup.scheduleBackups

  log.info("Game server starting up...".yellow)
  log.info(GameConfig.toString)

  loadResources()

  log.debug(s"Starting item uid is ${GameItems.startingID}".blue)

  val address = new InetSocketAddress(GameConfig.host, GameConfig.port)
  val server  = system.actorOf(Props.apply(new GameConnectionServerActor(address)), "GameServer")


  def loadResources() {
    DMaps.dmaps.size
    PluginManager.plugins.foreach(x => log.debug(x.toString()))
    StaticItems.items.size
  }

}


































