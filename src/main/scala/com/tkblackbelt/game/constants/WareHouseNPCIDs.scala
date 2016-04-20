package com.tkblackbelt.game.constants

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
object WareHouseNPCIDs {

  import MapIDs._

  val TcWareHouse         = 4000
  val MarketWareHouse     = 44
  val DCWareHouse         = 10011
  val PCWareHouse         = 10012
  val BIWhareHouse        = 10027
  val ACWhareHouse        = 10028
  val AdventureWhareHouse = 4101


  /**
   * Get the waarehouse if for a map
   */
  def fromMap(mapID: Int) = mapID match {
    case TwinCity => TcWareHouse
    case PhoenixCastle => PCWareHouse
    case DesertCity => DCWareHouse
    case ApeMoutain => ACWhareHouse
    case BirdIsland => BIWhareHouse
    case StoneCity => AdventureWhareHouse
    case Market => MarketWareHouse
    case _ => 0
  }
}
