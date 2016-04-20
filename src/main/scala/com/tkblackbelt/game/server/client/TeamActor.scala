package com.tkblackbelt.game.server.client

import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef}
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.GameMessages._
import com.tkblackbelt.game.server.client.TeamActor._
import com.tkblackbelt.game.server.client.handler.packet.TeamActionHandler.{InviteToTeam, KickMember}
import com.tkblackbelt.game.server.client.handler.server.ServerTeamActionHandler.{AskToJoinTeam, JoinedTeam, SendInfoToMember, TeamDismissed}
import com.tkblackbelt.game.server.map.SubscribeHelpers._
import com.tkblackbelt.game.server.map.{WorldEventBus, MessageEvent}
import com.tkblackbelt.game.util.ActorHelper

import scala.collection.mutable

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
object TeamActor {

  private val ids = new AtomicInteger(1)

  /**
   * Returns the next available team id
   */
  def nextID = ids.incrementAndGet

  case class AddMemberToTeam(id: Int, actor: ActorRef)
  case class RemoveMember(id: Int)
  case class AskLeaderToJoin(id: Int, actor: ActorRef)
  case class DismissTeam(senderID: Int)
  case class MemberJoined(member: ActorRef)

  /**
   * Returns a formatted team actor name
   */
  def name(charID: Int) = s"team$charID"


  case class TeamSettings(var allowMembers: Boolean = true, var allowItems: Boolean = true, var allowMoney: Boolean = true) {
    override def toString: String = s"(allowMember = $allowMembers, allowItems = $allowItems, allowMoney = $allowMoney)"
  }
}

class TeamActor(leaderID: Int, teamID: Int, mapEvents: WorldEventBus) extends Actor with ActorHelper {

  val members  = mutable.Map[Int, ActorRef]()
  val toTeam   = ToTeam(Some(teamID.toString))
  val settings = TeamSettings()

  import settings._

  def receive = {
    case add: AddMemberToTeam if !isFull       => addMember(add.id, add.actor)
    case add: AddMemberToTeam                  => add.actor ! MsgTeamIsFull
    case join: AskLeaderToJoin                 => askToJoin(join)
    case RemoveMember(id)                      => removeMember(id)
    case DismissTeam(id) if isLeader(id)       => dismissTeam()
    case KickMember(id, who) if isLeader(id)   => kick(who)
    case InviteToTeam(id, who) if isLeader(id) => invitePlayer(who)
    case leave: LeaveTeam                      => leaveTeam(leave)
    case teamInfo: TeamMateInformation         => broadcast(teamInfo)
    case settings: TeamSettingsChange          => changeTeamSettings(settings)

    case x => unhandledMsg(x)
  }

  def isFull = members.size >= 4

  /**
   * Returns whether an id is the leaders id
   */
  def isLeader(uid: Int) = uid == leaderID

  /**
   * Member is leaving the team
   * @param leave the leave request
   */
  def leaveTeam(leave: LeaveTeam) {
    broadcast(leave)
    removeMember(leave.uid) match {
      case Some(member) => member ! TeamDismissed
      case _ => //No member
    }
  }

  /**
   * Invite another player to the team
   * @param invite the invite request
   */
  def invitePlayer(invite: RequestInvite) {
    val player = ToPlayer(Some(invite.uid.toString))
    mapEvents.publish(MessageEvent(player, RequestInvite(leaderID)))
  }

  /**
   * Asks the leader if the member can join the team
   * @param join the join request
   */
  def askToJoin(join: AskLeaderToJoin) = join match {
    case AskLeaderToJoin(_, client) if !allowMembers => client ! MsgTeamNotAcceptingMembers
    case AskLeaderToJoin(id, _) if !isFull           => sendLeader(AskToJoinTeam(id))
    case AskLeaderToJoin(_, client)                  => client ! MsgTeamIsFull
  }

  /**
   * Kick a team member
   * @param id the id of the team member
   */
  def kick(id: Int) {
    broadcast(Kick(id))
    removeMember(id) match {
      case Some(member) => member ! TeamDismissed
      case _            => //Not removed
    }
  }

  /** 3
    * Adds a new member to the team
    */
  private def addMember(id: Int, actor: ActorRef) {
    members += id -> actor
    broadcast(SendInfoToMember(actor))
    mapEvents.subscribe(actor, toTeam)
    broadcast(MsgMemberAdded(id))
    actor ! JoinedTeam(self)
  }

  /**
   * Removes a team member
   * @param id the id of the team member
   */
  private def removeMember(id: Int) = {
    members.remove(id) match {
      case Some(member) =>
        mapEvents.unsubscribe(member, toTeam)
        broadcast(MsgMemberLeft(id: Int))
        Some(member)
      case None         => None //Not in team
    }
  }

  /**
   * Send a message to the team leader
   */
  def sendLeader(msg: AnyRef) {
    members(leaderID) ! msg
  }

  /**
   * Dismiss the team
   */
  def dismissTeam() {
    broadcast(TeamDismissed)
    broadcast(Dismiss(teamID))
    members.foreach(x => mapEvents.unsubscribe(x._2, toTeam))
    context.stop(self)
  }

  /**
   * Changes the teams settings if the leader
   * @param change the change to make
   */
  def changeTeamSettings(change: TeamSettingsChange) =
    if (isLeader(change.uid)) {
      change match {
        case AllowMembers(uid)  => allowMembers = true
        case ForbidMembers(uid) => allowMembers = false
        case AllowItems(uid)    => allowItems = true
        case ForbidItems(uid)   => allowItems = false
        case AllowMoney(uid)    => allowMoney = true
        case ForbidMoney(uid)   => allowMoney = false
      }
      log.debug(s"Team settings changed $settings")
    }

  /**
   * Broadcast a message to the team
   * @param msg the message to send
   */
  private def broadcast(msg: AnyRef) {
    mapEvents.publish(MessageEvent(toTeam, msg))
  }
}


















