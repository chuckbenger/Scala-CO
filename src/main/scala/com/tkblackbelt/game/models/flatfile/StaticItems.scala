package com.tkblackbelt.game.models.flatfile

import com.tkblackbelt.game.models.StaticItem
import com.tkblackbelt.game.util.Benchmark
import com.tkblackbelt.core.util.Rainbow._
import com.tkblackbelt.game.GameServerMain.system.log
import scala.io.{Codec, Source}

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
object StaticItems {

  val items = loadItems

  /**
   * Load constants item resource file
   */
  def loadItems: Map[Int, StaticItem] = Benchmark.time(log, "Finished loading constants items".green) {
    log.info("Loading constants items".yellow)
    val items = Source.fromURL(getClass.getResource("/database/items.dat"), Codec.ISO8859.name).getLines().map(toStaticItem).toMap
    log.info(s"Loaded ${items.size} static items".green)
    items
  }

  /**
   * Get an item for a specific id
   * @param i the item id
   * @return an option of item
   */
  def apply(i: Int): Option[StaticItem] = items.get(i)

  /**
   * Converts a single row to a constants item instance
   */
  def toStaticItem(line: String): (Int, StaticItem) = {
    val split = line.split(" ")
    val item = new StaticItem(
      split(0).toInt,
      split(1),
      split(2).toInt,
      split(3).toInt,
      split(4).toInt,
      split(5).toInt,
      split(6).toInt,
      split(7).toInt,
      split(8).toInt,
      split(9).toInt,
      split(10).toInt,
      split(12).toInt,
      split(14).toInt,
      split(15).toInt,
      split(16).toInt,
      split(17).toInt,
      split(18).toByte,
      split(19).toShort,
      split(20).toShort,
      split(21).toShort,
      split(22).toShort,
      split(29).toInt,
      split(30).toInt,
      split(31).toInt,
      split(32).toInt
    )
    item.id -> item
  }

}














