package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.packets.TeamAction.Modes._

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
 * Packet for interaction with a team
 * @see <a href="http://conquerwiki.com/wiki/Team_Action_Packet#Version_4267">http://conquerwiki.com/wiki/Team_Action_Packet#Version_4267</a>
 */
object TeamAction {

  val packetType: Short = 1023


  def apply(data: ByteString): Packet = {
    val iter = data.iterator
    val mode = iter.getInt
    val uid = iter.getInt
    mode match {
      case Modes.create           => CreateTeam(uid)
      case Modes.dismiss          => Dismiss(uid)
      case Modes.requestJoin      => RequestJoin(uid)
      case Modes.acceptJoin       => AcceptJoin(uid)
      case Modes.leaveTeam        => LeaveTeam(uid)
      case Modes.requestInvite    => RequestInvite(uid)
      case Modes.acceptInvite     => AcceptInvite(uid)
      case Modes.kick             => Kick(uid)
      case Modes.forbidItems      => ForbidItems(uid)
      case Modes.allowItems       => AllowItems(uid)
      case Modes.forbidMoney      => ForbidMoney(uid)
      case Modes.allowMoney       => AllowMoney(uid)
      case Modes.forbidNewMembers => ForbidMembers(uid)
      case Modes.allowNewMembers  => AllowMembers(uid)
      case x                      => UnhandledTeamAction(uid, mode)
    }
  }

  object Modes {
    val create          : Int = 0
    val requestJoin     : Int = 1
    val leaveTeam       : Int = 2
    val acceptInvite    : Int = 3
    val requestInvite   : Int = 4
    val acceptJoin      : Int = 5
    val dismiss         : Int = 6
    val kick            : Int = 7
    val forbidNewMembers: Int = 8
    val allowNewMembers : Int = 9
    val forbidMoney     : Int = 10
    val allowMoney      : Int = 11
    val forbidItems     : Int = 12
    val allowItems      : Int = 13
  }
}

abstract class TeamAction extends Packet(TeamAction.packetType) {

  val mode: Int
  val uid : Int

  override def deconstructed: ByteString = deconstruct {
    _.putInt(mode)
      .putInt(uid)
  }

  override def toString: String = super.toString + s"($mode, $uid)"
}

case class UnhandledTeamAction(uid: Int, mode: Int) extends TeamAction

case class CreateTeam(uid: Int) extends TeamAction {
  val mode = create
}

case class Dismiss(uid: Int) extends TeamAction {
  val mode = dismiss
}

case class RequestJoin(uid: Int) extends TeamAction {
  val mode = requestJoin
}

case class AcceptJoin(uid: Int) extends TeamAction {
  val mode = acceptJoin
}

case class LeaveTeam(uid: Int) extends TeamAction {
  val mode = leaveTeam
}

case class RequestInvite(uid: Int) extends TeamAction {
  val mode = requestInvite
}

case class AcceptInvite(uid: Int) extends TeamAction {
  val mode = acceptInvite
}

case class Kick(uid: Int) extends TeamAction {
  val mode = kick
}

abstract class TeamSettingsChange extends TeamAction

case class ForbidMoney(uid: Int) extends TeamSettingsChange {
  val mode = forbidMoney
}

case class AllowMoney(uid: Int) extends TeamSettingsChange {
  val mode = allowMoney
}

case class ForbidItems(uid: Int) extends TeamSettingsChange {
  val mode = forbidItems
}

case class AllowItems(uid: Int) extends TeamSettingsChange {
  val mode = allowItems
}

case class ForbidMembers(uid: Int) extends TeamSettingsChange {
  val mode = forbidNewMembers
}

case class AllowMembers(uid: Int) extends TeamSettingsChange {
  val mode = allowNewMembers
}

















