package com.tkblackbelt.game.models.database

import com.tkblackbelt.game.DB._
import com.tkblackbelt.game.models.Tables._
import com.tkblackbelt.game.models.{MockNpc, NPC}

import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession
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
object Npcs {


  lazy val npcs: Map[Int, NPC] =
    db withDynSession {
      Tqnpcs.list().map { f => f(1).asInstanceOf[Int] ->
        new NPC(
          f(0).asInstanceOf[Int],
          f(1).asInstanceOf[Int],
          f(2).asInstanceOf[Option[Int]],
          f(3).asInstanceOf[Option[Int]],
          f(4).asInstanceOf[Option[String]],
          f(5).asInstanceOf[Option[Int]],
          f(6).asInstanceOf[Option[Int]],
          f(7).asInstanceOf[Option[Int]],
          f(8).asInstanceOf[Option[Int]],
          f(9).asInstanceOf[Option[Int]],
          f(10).asInstanceOf[Option[Int]],
          f(11).asInstanceOf[Option[Int]],
          f(12).asInstanceOf[Option[Int]],
          f(13).asInstanceOf[Option[Int]],
          f(14).asInstanceOf[Option[Int]],
          f(15).asInstanceOf[Option[Int]],
          f(16).asInstanceOf[Option[Int]],
          f(17).asInstanceOf[Option[Int]],
          f(18).asInstanceOf[Option[Int]],
          f(19).asInstanceOf[Int],
          f(20).asInstanceOf[Int],
          f(21).asInstanceOf[Int],
          f(22).asInstanceOf[Int],
          f(23).asInstanceOf[Option[String]],
          f(24).asInstanceOf[Int],
          f(25).asInstanceOf[Short],
          f(26).asInstanceOf[Short],
          f(27).asInstanceOf[Int],
          f(28).asInstanceOf[Short],
          f(29).asInstanceOf[Option[Int]],
          f(30).asInstanceOf[Int]
        )
      }.toMap
    }


  /**
   * npc.npctype, npc.x.get, npc.y.get, npc.subtype.get, npc.notnpctype.get, npc.flag)
   */
  lazy val genNpcs: Map[Int, MockNpc] = (1 to 3000).map { i =>
    i -> new MockNpc(i, i, 412 + ((i % 100) + Random.nextInt(300)), 370 + ((i % 100) + Random.nextInt(300)), 26, 2, 0)
  }.toMap
}
