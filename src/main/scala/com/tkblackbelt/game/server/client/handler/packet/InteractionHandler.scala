package com.tkblackbelt.game.server.client.handler.packet

import akka.actor.ActorRef
import com.tkblackbelt.core.Packet
import com.tkblackbelt.game.packets.{Interaction, MagicAttack, PhysicalAttack}
import com.tkblackbelt.game.server.client.handler.Client
import com.tkblackbelt.game.server.client.handler.packet.InteractionHandler.PhysicalAttackEvent
import com.tkblackbelt.game.server.map.MapActor.SectorBroadCast
import com.tkblackbelt.game.util.ActorHelper

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

object InteractionHandler {
  abstract class InteractionEvent extends SectorBroadCast {
    val packet: Packet
    val source: ActorRef
    val id    : Int
    val x     : Int
    val y     : Int
    val target: Int
  }
  abstract class AttackEvent extends InteractionEvent {
    val damage: Int
  }
  case class PhysicalAttackEvent(packet: PhysicalAttack, source: ActorRef, id: Int, x: Int, y: Int, target: Int, damage: Int) extends AttackEvent with SectorBroadCast
  case class MagicAttackEvent(packet: MagicAttack, source: ActorRef, id: Int, x: Int, y: Int, target: Int, damage: Int) extends AttackEvent with SectorBroadCast
}

/**
 * Handles a client interaction with the map (Attack, etc)
 */
trait InteractionHandler extends Client with ActorHelper {

  def handleInteraction(interaction: Interaction) =
    if (isInView(interaction.targetX, interaction.targetY))
      interaction match {
        case physical: PhysicalAttack => sendSector(buildPhysicalAttack(physical))
        case x                        => unhandledMsg(x)
      }


  /**
   * Builds a phyiscal attack message to send to the sector 
   */
  def buildPhysicalAttack(physical: PhysicalAttack) =
    PhysicalAttackEvent(physical, self, char.id, char.x, char.y, physical.targetID, inventory.equipment.physicalAttack)
}


















