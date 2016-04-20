package com.tkblackbelt.game.conf

import java.io.File

import com.tkblackbelt.core.util.Rainbow._
import com.tkblackbelt.game.util.Benchmark
import org.clapper.classutil.ClassFinder

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

object PluginManager {

  import com.tkblackbelt.game.GameServerMain.system.log

  val plugins = load()
  /**
   * Plugin directory. We navigate up two directories to get all classes in the tkblackbelt package
   */
  private val pluginDir = List(new File(getClass.getResource("../../").getPath))
  /**
   * Base class that npc scripts inherit from
   */
  private val npcScript = "com.tkblackbelt.game.server.npc.scripts.NpcScript"

  /**
   * Load the plugin classes
   */
  def load() = {
    log.info(s"Plugin directory $pluginDir".blue)
    log.info("Loading plugins".yellow)

    Benchmark.time(log, "Finished loading plugins") {
      val finder = ClassFinder(pluginDir)
      val classes = finder.getClasses().toIterator
      val npcs = ClassFinder.concreteSubclasses(npcScript, classes).toList.map(_.name)
      log.info(s"Loaded ${npcs.size} npc scripts.".green)
      npcs
    }

  }

}
