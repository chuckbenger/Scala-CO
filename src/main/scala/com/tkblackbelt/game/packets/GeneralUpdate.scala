package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.packets.GeneralUpdate.Types._
import com.tkblackbelt.game.server.map.MapActor.SectorBroadCast
import com.tkblackbelt.game.util.Timestamp

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
 * The General Data packet performs a variety of tasks for the client, these vary from moving the client around the game, to ending an xp skill.
 *
 * @see <a href="http://conquerwiki.com/wiki/General_Data_Packet#Version_4267">http://conquerwiki.com/wiki/General_Data_Packet#Version_4267</a>
 */
object GeneralUpdate {

  val packetType: Short = 1010

  def apply(data: ByteString, orig: ByteString): GeneralUpdate = {
    val iter = data.iterator
    val timer = iter.getInt
    val id = iter.getInt
    val parm1 = iter.getShort
    val parm2 = iter.getShort
    val parm3 = iter.getShort
    val parm4 = iter.getShort
    val parm5 = iter.getShort
    val parm6 = iter.getShort
    val updateType = iter.getInt

    updateType match {
      case Types.retrieve_surroundings => RetrieveSurroundings
      case Types.pos_request           => PositionRequest
      case Types.avatar                => Avatar(id, parm5, parm6)
      case Types.portal                => PortalJump(parm1, parm2)
      case Types.direction             => Direction(id, parm1, parm2, parm3.toByte)
      case Types.items_request         => ItemsRequest(id)
      case Types.entitySync            => EntitySync(parm6, parm5)
      case Types.friendItems           => FriendDataRequest(parm6, parm5)
      case Types.action                => Action(id, parm1, parm2, parm3, parm5)
      case _                           => UnhandledUpdate(id, parm1, parm2, parm3, parm4, parm5, parm6, updateType)
    }
  }

  /**
   * Available General Types
   */
  object Types {

    val entitySync             = 162
    val direction              = 124
    val alter_change_pk        = 152
    val avatar                 = 142
    val change_direction       = 79
    val change_map             = 86
    val change_pk_mode         = 96
    val complete_map_change    = 104
    val confirm_freinds        = 76
    val confirm_guild          = 97
    val confirm_login_complete = 130
    val confirm_prof           = 77
    val confirm_skills         = 78
    val correct_cords          = 108
    val action                 = 126
    val endtele                = 148
    val end_xp_list            = 93
    val entity_remove          = 132
    val entity_spawn           = 102
    val hotkeys                = 75
    val items_request          = 138
    val jump                   = 133
    val leveled                = 92
    val mapshow                = 74
    val mine_swing             = 99
    val friendItems            = 156
    val open_shop              = 113
    val pickup_cash_effect     = 121
    val portal                 = 130
    val pos_request            = 137
    val remote_commands        = 116
    val remove_weapon_mesh     = 135
    val remove_weapon_mesh2    = 136
    val retrieve_friend        = 139
    val retrieve_surroundings  = 170
    val retrive_guid           = 151
    val retrive_spells         = 150
    val revive                 = 94
    val shop                   = 111
    val show_surroundings      = 114
    val spawn_effect           = 131
    val stop_flying            = 120
    val remove_entity          = 141
    val whare_house            = 186
  }
}


abstract class GeneralUpdate extends Packet(GeneralUpdate.packetType) {

  val id        : Int = 0
  val parm1     : Int = 0
  val parm2     : Int = 0
  val parm3     : Int = 0
  val parm4     : Int = 0
  val parm5     : Int = 0
  val parm6     : Int = 0
  val updateType: Int = 0

  override def deconstructed: ByteString = deconstruct {
    _.putInt(Timestamp.generate())
      .putInt(id)
      .putShort(parm1)
      .putShort(parm2)
      .putShort(parm3)
      .putShort(parm4)
      .putShort(parm5)
      .putShort(parm6)
      .putInt(updateType)
  }

  override def toString: String = s"${super.toString} ($id $parm1 $parm2 $parm3 $parm4 $parm5 $parm6 $updateType)"
}


/**
 * Unhandled updated
 */
case class UnhandledUpdate(override val id: Int, override val parm1: Int, override val parm2: Int, override val parm3: Int,
  override val parm4: Int, override val parm5: Int, override val parm6: Int,
  override val updateType: Int) extends GeneralUpdate

/**
 * Client Jumped into a portal
 * @param x the client new x position
 * @param y the client new y position
 */
case class PortalJump(x: Int, y: Int) extends GeneralUpdate

/**
 * Client wants to retrieve it's surrounds. Sent during login
 */
case object RetrieveSurroundings extends GeneralUpdate

/**
 * Request the clients current position
 */
case object PositionRequest extends GeneralUpdate {
  def result(id: Int, x: Int, y: Int, map: Int) = PositionUpdate(id, x, y, map)
}

/**
 * Clients new direction
 * @param id the clients id
 * @param directionFaced the clients new direction
 */
case class Direction(override val id: Int, override val parm1: Int, override val parm2: Int, directionFaced: Byte) extends GeneralUpdate {
  override val parm3      = directionFaced.toInt
  override val updateType = direction
}

/**
 * Entity jumped
 * @param id the entities id
 * @param newX the entities new x
 * @param newY the entities new y
 */
case class Avatar(override val id: Int, newX: Int, newY: Int) extends GeneralUpdate {
  override val parm1      = newX
  override val parm2      = newY
  override val updateType = avatar
}


case class ItemsRequest(override val id: Int) extends GeneralUpdate {
  override val updateType = items_request
}

/**
 * Update an entities position
 * @param id the entities id
 * @param x the entities x
 * @param y the entities y
 * @param map the entities map
 */
case class PositionUpdate(override val id: Int, x: Int, y: Int, map: Int) extends GeneralUpdate {
  override val parm1      = x
  override val parm2      = y
  override val parm5      = map
  override val updateType = pos_request
}


case class Jump(override val id: Int, oldX: Int, oldY: Int, newX: Int, newY: Int) extends GeneralUpdate {
  override val parm1      = oldX
  override val parm2      = oldY
  override val parm5      = newX
  override val parm6      = newY
  override val updateType = avatar
}

/**
 * Remove entity from the screen
 * @param id the entities id
 * @param prevX the entities prev X
 * @param prevY the entities prev y
 */
case class RemoveEntity(override val id: Int, prevX: Int, prevY: Int) extends GeneralUpdate with SectorBroadCast {
  override val parm1      = prevX
  override val parm2      = prevY
  override val updateType = remove_entity
}

/**
 * Open the wharehouse
 * @param id the wharehouse id
 */
case class WhareHouseOpen(override val id: Int) extends GeneralUpdate {
  override val parm1      = whare_house
  override val parm5      = 4
  override val updateType = whare_house
}

case class FriendDataRequest(private val idHigh: Int, private val idLow: Int) extends GeneralUpdate {
  override val id = idHigh << 16 | idLow
}

case class EntitySync(private val idHigh: Int, private val idLow: Int) extends GeneralUpdate {
  override val id = idHigh << 16 | idLow
}

case class Action(override val id: Int, x: Int, y: Int, newDirection: Int, actionID: Int) extends GeneralUpdate with SectorBroadCast {
  override val parm1      = x
  override val parm2      = y
  override val parm3      = newDirection
  override val parm5      = actionID
  override val updateType = action
}













