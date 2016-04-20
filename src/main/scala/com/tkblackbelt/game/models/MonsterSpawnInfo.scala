package com.tkblackbelt.game.models

import com.tkblackbelt.game.models.Tables._
import com.tkblackbelt.game.server.map.{DMaps, MapCoord}

import scala.util.Random


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

case class MonsterSpawnInfo(info: MonsterspawnsRow) {

  val dmap = DMaps.dmaps(info.mapid.toShort)

  def nextCoord = Seq.fill(10)(newRandomCoord).find(coord => dmap.checkCoord(coord.x, coord.y))

  val random = new Random(System.currentTimeMillis())

  private def newRandomCoord = MapCoord(randomX, randomY)

  private def randomX = info.xstart + random.nextInt(math.abs(info.xstop - info.xstart) + 1)

  private def randomY = info.ystart + random.nextInt(math.abs(info.ystop - info.ystart) + 1)

}
