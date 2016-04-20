package com.tkblackbelt.game.models.database

import com.tkblackbelt.game.DB.db
import com.tkblackbelt.game.models.GamePortal
import com.tkblackbelt.game.models.Tables._

import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession

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
object GamePortals {

  lazy val portals = db.withDynSession(Portals.list().map(p =>
    p.fromMap -> GamePortal(p.fromMap, p.fromX, p.fromY, p.toMap, p.toX, p.toY)))


  /**
   * Find a portal based on where it is and where it's going
   * @param fromMap the from map
   * @param toMap where it's going
   * @return returns the portal
   */
  def findPortal(fromMap: Int, toMap: Int) =
    portals.filter { case (from, portal) => from == fromMap && portal.toMap == toMap}.head._2
}


