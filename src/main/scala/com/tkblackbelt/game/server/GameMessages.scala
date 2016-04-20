package com.tkblackbelt.game.server

import akka.actor.ActorRef
import com.tkblackbelt.game.conf.GameConfig
import com.tkblackbelt.game.packets.SystemMessages.SystemMessage
import com.tkblackbelt.game.packets.{GuildMsg, Chat, Talk}
import com.tkblackbelt.game.server.client.handler.packet.ChatHandler.GuildMessage
import com.tkblackbelt.game.structures.ValueRange

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
 * Contains all available system messages
 */
object GameMessages {

  def MsgMemberAdded(id: Int) = SystemMessage(s"$id joined the team!")

  def MsgMemberLeft(id: Int) = SystemMessage(s"$id left the team")

  def MsgAttackUpdated(physical: ValueRange, magic: ValueRange) = SystemMessage(s"Physical attack = $physical, Magic attack = $magic")

  def MsgDefenceUpdated(physical: Int, magic: Int) = SystemMessage(s"Physical defence = $physical, Magic Defence = $magic")

  def MsgDodgeUpdated(dodge: Int) = SystemMessage(s"Doge = $dodge")

  def MsgDexterityUpdate(dex: Int) = SystemMessage(s"Dex = $dex")

  def MsgItemNotThere(id: Int) = SystemMessage(s"Item doesn't exist $id")

  def MsgDonatedToGuild(from: String, amount: Int) = SystemMessage(s"$from donated $amount silver to the guild!")

  def MsgDonateToLeave(amount: Int) = SystemMessage(s"You must donate $amount to leave the guild")

  def MsgNewGuildLeader(name: String) = GuildMessage(ActorRef.noSender, GuildMsg(Chat.system, "ALL", s"Your guild leader is now $name"))

  def MsgNewDeputy(name: String) = GuildMessage(ActorRef.noSender, GuildMsg(Chat.system, "ALL", s"$name has been assigned depute!"))
  
  val WelcomeMessage = List(
    Talk(Chat.system, "", s"Welcome to ${GameConfig.name}, the legit 1.0 server"),
    Talk(Chat.system, "", s"Current server version is ${GameConfig.version}"))

  lazy val MsgYourGuildDisbanded      = SystemMessage("Your guild has been disbanded")
  lazy val MsgNotLeaderOfGuild        = Talk(to = "", message = "You are not the leader of this guild.")
  lazy val MsgKickedFromGuild         = SystemMessage("You have been kicked from your guild")
  lazy val MsgNotEnoughMoney          = SystemMessage("You don't have that much money")
  lazy val MsgWareHouseIsFull         = SystemMessage("Warehouse is full")
  lazy val MsgInventoryIsFull         = SystemMessage("Your Inventory is full")
  lazy val MsgAlreadyInTeam           = SystemMessage("Already in a team")
  lazy val MsgTeamIsFull              = SystemMessage("Team is full")
  lazy val MsgTeamNotAcceptingMembers = SystemMessage("Team is not accepting members")
  lazy val MsgTradeInventoryIsFull    = SystemMessage("Partners inventory is full")
  lazy val MsgTradeSucceeded          = SystemMessage("Trade Succeeded!")
  lazy val MsgTradeFailed             = SystemMessage("Trade Failed!")
  lazy val MsgItemSellFailed          = SystemMessage("Failed to sell item!")
  lazy val MsgTradeRequestSent        = SystemMessage("Trade request sent")
  lazy val MsgGuildLeaderOnline       = SystemMessage("Guild leader has logged in!")
}
