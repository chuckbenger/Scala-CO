package com.tkblackbelt.game.models.database


import java.util.concurrent.atomic.AtomicInteger

import com.tkblackbelt.game.DB.db
import com.tkblackbelt.game.conf.GameConfig
import com.tkblackbelt.game.models.Mob
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
object Mobs {

  private val ids = new AtomicInteger(GameConfig.Settings.mobStartingID)

  /**
   * @return the next monster uid
   */
  def nextID = ids.incrementAndGet()

  val monsters = db.withDynSession {
    Monsters.list().map(mob => {
      mob.id -> Mob(mob.id, mob.name, mob.mesh, mob.level, mob.hitpoints, mob.minattack,
        mob.maxattack, mob.magicdefence, mob.defence, mob.attackrange, mob.viewdistance,
        mob.dropmoney, mob.speed, mob.dodge, mob.attacktype)
    }).toMap
  }

}















