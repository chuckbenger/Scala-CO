package com.tkblackbelt.game.models

import com.tkblackbelt.game.util.MathHelper


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

class NPC(
           val npcid: Int,
           val npctype: Int,
           val ownerid: Option[Int] = None,
           val playerid: Option[Int] = None,
           val name: Option[String] = None,
           val notnpctype: Option[Int] = None,
           val subtype: Option[Int] = None,
           val idxserver: Option[Int] = None,
           val mapid: Option[Int] = None,
           val x: Option[Int] = None,
           val y: Option[Int] = None,
           val task0nottype: Option[Int] = None,
           val task1: Option[Int] = None,
           val task2: Option[Int] = None,
           val task3: Option[Int] = None,
           val task4: Option[Int] = None,
           val task5: Option[Int] = None,
           val task6: Option[Int] = None,
           val task7: Option[Int] = None,
           val data0: Int = 0,
           val data1: Int = 0,
           val data2: Int = 0,
           val data3: Int = 0,
           val datastr: Option[String] = None,
           val linkid: Int = 0,
           val life: Short = 0,
           val maxlife: Short = 0,
           val direction: Int = 0,
           val flag: Short = 0,
           val itemid: Option[Int] = None,
           val face: Int
           ) {

  def isInView(otherX: Int, otherY: Int) = MathHelper.isInRenderDistance(otherX, otherY, x.get, y.get)
}

class MockNpc(uid: Int, id: Int, x: Int, y: Int, npcType: Int, direction: Int, interaction: Int) extends
NPC(uid,
  id, mapid = Some(1002), x = Some(x), y = Some(y), subtype = Some(npcType), notnpctype = Some(direction), flag = interaction.toShort, face = 1)























