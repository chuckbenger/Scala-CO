package com.tkblackbelt.game.server.mob

import akka.actor.ActorRef
import com.tkblackbelt.game.models.MonsterSpawnInfo
import com.tkblackbelt.game.server.client.handler.packet.InteractionHandler.AttackEvent
import com.tkblackbelt.game.server.mob.GenericMobActor.MobAIEvent

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
class GuardMob(val id: Int, val spawn: MonsterSpawnInfo, val spawnManager: ActorRef, val map: ActorRef) extends GenericMobActor {


  /**
   * Handles a client attacking this mob
   */
  override def handleAttack(attack: AttackEvent, sector: ActorRef): Unit = {
    super.handleAttack(attack, sector)
    sector ! attackMagical(attack.id, attack.x, attack.y, 1002, 3)

  }

  /**
   * Called when an AI event is scheduled
   */
  override def doAI(event: MobAIEvent): Unit = {
    
  }
}
