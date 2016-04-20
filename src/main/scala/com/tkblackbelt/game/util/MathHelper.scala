package com.tkblackbelt.game.util

import com.tkblackbelt.game.conf.GameConfig

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
 * Various Math helper functions
 */
object MathHelper {

  /**
   * Calculates the distance between two points
   * @param x1 the first x
   * @param y1 the first y
   * @param x2 the second x
   * @param y2 the second y
   * @return return the distance
   */
  def distanceBetween(x1: Int, y1: Int, x2: Int, y2: Int): Double =
    math.abs(math.sqrt(math.pow(x1 - x2, 2) + math.pow(y1 - y2, 2)))


  /**
   * Determines whether point x is in a renderable distance from point y
   * @param x1 the first x
   * @param y1 the first y
   * @param x2 the second x
   * @param y2 the second y
   * @return whether the 2 points are within render distance
   */
  def isInRenderDistance(x1: Int, y1: Int, x2: Int, y2: Int): Boolean =
    distanceBetween(x1, y1, x2, y2) <= GameConfig.Settings.renderDistance


  /**
   * Returns whether a x y coord is close enough to a portal
   * @param x1 the first x
   * @param y1 the first y
   * @param x2 the portals x
   * @param y2 the portals y
   */
  def isInPortalDistance(x1: Int, y1: Int, x2: Int, y2: Int): Boolean =
    distanceBetween(x1, y1, x2, y2) <= GameConfig.Settings.portalDistance

}


/**
 * Directions a client can move
 */
object Directions {
  val None      :Byte = -1
  val Southwest :Byte = 0
  val West      :Byte = 1
  val Northwest :Byte = 2
  val North     :Byte = 3
  val Northeast :Byte = 4
  val East      :Byte = 5
  val Southeast :Byte = 6
  val South     :Byte = 7

  case class DirectionPayload(xPayload: Int, yPayload: Int)


  /**
   * Get the direction between two points and the payload
   */
  def getDirection(x1: Int, y1: Int, x2: Int, y2: Int): (Byte, DirectionPayload) = {

    val MakeMobX = if (x2 - x1 >= 1) 1 else if (x2 - x1 == 0) 0 else -1
    val MakeMobY = if (y2 - y1 >= 1) 1 else if (y2 - y1 == 0) 0 else -1

    val direction: Byte = (MakeMobX, MakeMobY) match {
      case (0, 1)   => Southwest
      case (-1, 1)  => West
      case (-1, 0)  => Northwest
      case (-1, -1) => North
      case (0, -1)  => Northeast
      case (1, -1)  => East
      case (1, 0)   => Southeast
      case (1, 1)   => South
      case _        => None
    }

    val payLoad = getChange(direction)

    (direction, payLoad)
  }


  /**
   * Gets the x,y payload from a direction
   */
  def getChange(direction: Byte): DirectionPayload = direction.toInt match {
    case None      => DirectionPayload(0, 0)
    case Southwest => DirectionPayload(0, 1)
    case West      => DirectionPayload(-1, 1)
    case Northwest => DirectionPayload(-1, 0)
    case North     => DirectionPayload(-1, -1)
    case Northeast => DirectionPayload(0, -1)
    case East      => DirectionPayload(1, -1)
    case Southeast => DirectionPayload(1, 0)
    case South     => DirectionPayload(1, 1)
    case _         => DirectionPayload(0, 0)
  }
}