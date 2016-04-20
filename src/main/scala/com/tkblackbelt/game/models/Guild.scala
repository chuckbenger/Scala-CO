package com.tkblackbelt.game.models

import akka.actor.{ActorRef, ActorSelection}
import com.tkblackbelt.game.constants.GuildRanks
import com.tkblackbelt.game.models.Tables.CharacterGuildRow
import com.tkblackbelt.game.models.database.CharacterGuilds
import com.tkblackbelt.game.packets.{GuildInformation, LeaveGuild}
import com.tkblackbelt.game.server.GameMessages._
import com.tkblackbelt.game.server.client.GuildActor._
import com.tkblackbelt.game.server.client.handler.packet.ChatHandler.GuildMessage
import com.tkblackbelt.game.server.client.handler.server.NpcCommandHandler.{AssignDeputy, ChangeGuildLeaderShip}
import com.tkblackbelt.game.server.client.handler.server.ServerHandler.ForceRespawn
import com.tkblackbelt.game.server.map.WorldManagerActor.GetMyGuild


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
class Guild(charID: Int, charName: String, handler: ActorRef, worldHandler: ActorRef) {

  val noGuild     = CharacterGuildRow(charID, 0, 0, GuildRanks.None)
  val noGuildInfo = GuildInformation(0, 0, 0, 0, 0, "")

  var info: CharacterGuildRow = noGuild
  private var guildActor: ActorRef = ActorRef.noSender

  val leaveDonation = 20000
  val donateMessage = MsgDonateToLeave(leaveDonation)


  /**
   * Return whether the client is in a guild
   */
  def isMember = info.rank != GuildRanks.None

  /**
   * Am I the leader?
   */
  def isLeader = info.rank == GuildRanks.Leader

  /**
   * Load the characters guild information
   * @param initial Whether this is the inital load. If it is we don't do a force respawn as the sector wont be set
   */
  def load(initial: Boolean = true) {
    unsubscribe()
    guildActor = ActorRef.noSender
    reloadInfo()
    if (info == noGuild && !initial) {
      handler ! ForceRespawn
    }
  }

  /**
   * Reload the characters guild info
   */
  def reloadInfo() {
    CharacterGuilds(charID) match {
      case Some(guild) =>
        worldHandler ! GetMyGuild(handler, guild.guildid)
        info = guild
      case _           =>
        info = noGuild
        sendClient(noGuildInfo)
    }
  }

  /**
   * Player was accepted into a guild
   * @param guild the guild actor
   */
  def acceptedToGuild(guild: ActorRef) = if (!isMember) {
    CharacterGuilds(charID) match {
      case Some(guildInfo) =>
        info = guildInfo
        setGuildActor(guild)
      case _               => info = noGuild
    }
  }

  /**
   * Send guild info to the client
   */
  def sendGuildInfo(to: ActorRef = handler) {
    sendGuild(SendGuildInfoTo(to, info.rank.toByte, info.donation))
    sendName()
  }

  /**
   * Send guild name to the client
   */
  def sendName(to: ActorRef = handler) {
    sendGuild(SendGuildNameTo(to))
  }

  /**
   * Asks the guild actor to send a list of all members
   */
  def sendMemberList() {
    sendGuild(SendMembersTo(handler))
  }

  def donate(amt: Int) {
    info = info.copy(donation = info.donation + amt)
    sendGuild(DonateToGuild(charName, amt))
    CharacterGuilds.update(info)
  }

  /**
   * Update the guild bulletin
   * @param newMsg the new message
   */
  def setBulletin(newMsg: String) {
    sendGuild(UpdateBulletin(charID, newMsg))
  }

  /**
   * Set the guild actor for this guild
   * @param actor the actor reference
   */
  def setGuildActor(actor: ActorRef) {
    guildActor = actor
    sendGuild(SubscribeToGuild(handler, info.rank, charID))
    sendGuildInfo()
    sendName()
    handler ! ForceRespawn
  }

  /**
   * Kick a guild member
   * @param memberName the members name
   */
  def kick(memberName: String) =
    if (isLeader)
      sendGuild(KickMember(memberName))

  /**
   * Try to disband the guild
   */
  def disband() =
    if (isLeader)
      sendGuild(DisbandGuild(charID))
    else
      sendClient(MsgNotLeaderOfGuild)

  /**
   * Leave the current guild
   */
  def leave(): Boolean = {
    isMember && reachedMinimumDonation match {
      case true  =>
        sendGuild(LeftGuild(charID))
        sendClient(LeaveGuild(info.guildid))
        sendClient(noGuildInfo)
        CharacterGuilds.delete(charID)
        load()
        true
      case false =>
        sendClient(donateMessage)
        false
    }
  }

  /**
   * Kicked from guild :(
   */
  def kicked() = if (isMember) {
    sendClient(LeaveGuild(info.guildid))
    reloadInfo()
    handler ! ForceRespawn
    sendClient(MsgKickedFromGuild)
  }

  /**
   * Talk to guild members
   * @param message message to send to the guild
   */
  def talk(message: GuildMessage) =
    if (isMember)
      sendGuild(message)


  /**
   * Accept a guild member invite
   * @param charID the character to invites id
   * @param actor their actor
   */
  def acceptGuildMember(charID: Int, actor: ActorSelection) =
    if (isLeader)
      sendGuild(MemberInvited(charID, actor))


  /**
   * Attempt to change the server leader and notfy the responder whether it succedded
   * @param newLeader the new leader
   */
  def changeLeader(newLeader: ChangeGuildLeaderShip) =
    if (isLeader) {
      sendGuild(newLeader)
    } else
      newLeader.npc ! newLeader.copy(msg = "Not the leader...")


  /**
   * Assign a deputy leader to the guild
   * @param dep
   */
  def assignDeputy(dep: AssignDeputy) =
    if (isLeader)
      sendGuild(dep)
    else
      dep.npc ! dep.copy(msg = "Not the leader...")


  /**
   * Has the client reached the minimum fund donation if trying to leave
   */
  def reachedMinimumDonation = info.donation >= leaveDonation

  /**
   * UnSubscribe from the guild event bus
   */
  def unsubscribe() {
    sendGuild(UnSubscribeFromGuild(handler, charID))
  }

  /**
   * Send a message to the guild actor
   * @param msg the message to send
   */
  def sendGuild(msg: Any) =
    if (guildActor != ActorRef.noSender)
      guildActor ! msg

  /**
   * Send a message to the client
   * @param msg the message to send
   */
  def sendClient(msg: Any) =
    handler ! msg
}
