package com.tkblackbelt.game.server.mob

import akka.actor.ActorRef
import com.tkblackbelt.game.models.MonsterSpawnInfo
import com.tkblackbelt.game.server.map.MapActor.GetSurroundings
import com.tkblackbelt.game.server.mob.GenericMobActor.MobAIEvent
import com.tkblackbelt.game.server.mob.MobActor.Agro
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

object MobActor {
  case class Agro(client: ActorRef, char: Int, x: Int, y: Int)
}

class MobActor(val id: Int, val spawn: MonsterSpawnInfo, val spawnManager: ActorRef, val map: ActorRef) extends GenericMobActor {

  var currentAgro: Option[Agro] = None

  /**
   * Called when a mob receives a player jump event
   */
  override def handlePlayerJump(get: GetSurroundings) = {
    super.handlePlayerJump(get)

    currentAgro match {
      case Some(agro) if get.charID != agro.char && isCloserThanCurrentAgro(agro, get.x, get.y) =>
        currentAgro = Some(Agro(get.client, get.charID, get.x, get.y))

      case Some(agro) if get.charID == agro.char && isNear(get.x, get.y) =>
        currentAgro = Some(agro.copy(x = get.x, y = get.y))

      case Some(agro) => //Out of view
        currentAgro = None
        cancelAI()

      case None =>
        if (isNear(get.x, get.y)) {
          currentAgro = Some(Agro(get.client, get.charID, get.x, get.y))
          startAI()
        }
    }
  }

  /**
   * Returns whether an x, y coord is closer than the current agro
   */
  def isCloserThanCurrentAgro(agro: Agro, x: Int, y: Int) =
    isNear(x, y) && (MathHelper.distanceBetween(x, y, mob.x, mob.y) < MathHelper.distanceBetween(agro.x, agro.y, mob.x, mob.y))


  /**
   * Attack the current agro
   */
  def attackAgro(agro: Agro) =
    agro.client ! attackPhysical(agro.char, agro.x, agro.y)

  /**
   * Called when an AI event is scheduled
   */
  override def doAI(event: MobAIEvent) {
    currentAgro match {
      case Some(agro) if canAttack(agro.x, agro.y) => //attackAgro(agro)
      case Some(agro) if isNear(agro.x, agro.y)    => moveTowards(agro.x, agro.y)
      case _                                       =>
        cancelAI()
        currentAgro = None
    }
//    println("AI " + currentAgro)
  }
}











