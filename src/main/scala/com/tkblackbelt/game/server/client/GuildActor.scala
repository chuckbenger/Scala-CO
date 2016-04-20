package com.tkblackbelt.game.server.client

import akka.actor.{Actor, ActorRef, ActorSelection}
import com.tkblackbelt.game.constants.GuildRanks
import com.tkblackbelt.game.models.Tables.GuildsRow
import com.tkblackbelt.game.models.database.{CharacterGuilds, GameCharacters, GameGuilds}
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.GameMessages._
import com.tkblackbelt.game.server.client.GuildActor._
import com.tkblackbelt.game.server.client.handler.packet.ChatHandler.GuildMessage
import com.tkblackbelt.game.server.client.handler.server.NpcCommandHandler.{AssignDeputy, ChangeGuildLeaderShip}
import com.tkblackbelt.game.server.client.handler.server.ServerGuildRequestHandler.{GuildDisbanded, GuildRankChanged, KickedFromGuild}
import com.tkblackbelt.game.server.map.SubscribeHelpers._
import com.tkblackbelt.game.server.map.{MessageEvent, WorldEventBus}
import com.tkblackbelt.game.util.ActorHelper

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits._

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
object GuildActor {

  case class SendGuildInfoTo(client: ActorRef, rank: Byte, donation: Int)
  case class SendGuildNameTo(client: ActorRef)
  case class SendMembersTo(client: ActorRef)
  case class UpdateBulletin(char: Int, msg: String)
  case class SubscribeToGuild(client: ActorRef, rank: Int, charID: Int)
  case class UnSubscribeFromGuild(client: ActorRef, charID: Int)
  case class DonateToGuild(sender: String, amt: Int)
  case class SetMemberLevel(charID: Int, level: Int)
  case class LeftGuild(charID: Int)
  case class MemberInvited(charID: Int, actor: ActorSelection)
  case class InvitedToGuild(guild: ActorRef)
  case class KickMember(memberName: String)
  case class DisbandGuild(charID: Int)

  case class MemberInfo(id: Int, name: String, var rank: Int, var level: Int, var online: Boolean = false, var actor: ActorRef = ActorRef.noSender) {
    override def toString: String = s"$name $level ${if (online) 1 else 0}"
  }
  /**
   * Return a guild actor name
   * @param id the guilds id
   */
  def actorName(id: Int) = s"guild$id"
}

class GuildActor(defaultGuildInfo: GuildsRow, worldEvents: WorldEventBus) extends Actor with ActorHelper {

  import defaultGuildInfo.guildid
  import log._

  var guildInfo   = defaultGuildInfo
  var leaderName  = GameCharacters(guildInfo.leader).get.name
  val toGuild     = ToGuild(Some(guildInfo.guildid.toString))
  val membersInfo = mutable.HashMap[Int, MemberInfo]()

  val maxDeputies = 5

  override def preStart() {
    buildMemberList()
  }

  def receive = {
    case SubscribeToGuild(client, rank: Int, charID: Int) => subscribeMember(client, rank, charID)
    case UnSubscribeFromGuild(client, charID: Int)        => unSubscribeMember(client, charID)

    case UpdateBulletin(charID, newBulletin) if charID == guildInfo.leader => updateBulletin(newBulletin)

    case MemberInvited(charID, actor)            => acceptMember(charID, actor)
    case SendGuildInfoTo(client, rank, donation) => sendGuildInfoTo(client, rank, donation)
    case SendGuildNameTo(client)                 => client ! GuildName(guildInfo.guildid, guildInfo.name)
    case SendMembersTo(client)                   => client ! GuildMemberList(guildid, membersInfo.values.toList)
    case DonateToGuild(from, amt)                => donate(from, amt)
    case SetMemberLevel(charID, level)           => setMemberLevel(charID, level)
    case KickMember(member)                      => kick(member)
    case talk: GuildMessage                      => publish(talk)
    case DisbandGuild(id)                        => disbandGuild(id)
    case LeftGuild(charID)                       => membersInfo.remove(charID)
    case leaderChg: ChangeGuildLeaderShip        => changeLeader(leaderChg)
    case deputy: AssignDeputy                    => assignDeputy(deputy)

    case x => unhandledMsg(x)
  }

  /**
   * Refresh the guilds information
   */
  def refresh() {
    guildInfo = GameGuilds(defaultGuildInfo.guildid).get
    leaderName = GameCharacters(guildInfo.leader).get.name
  }

  /**
   * Guild leader accepted a member
   * @param id the members id
   * @param actor their actor
   */
  def acceptMember(id: Int, actor: ActorSelection) {
    CharacterGuilds.create(id, guildid) onSuccess { case character =>
      character match {
        case Some(char) =>
          addMember(char.id, char.name, char.level)
          actor ! InvitedToGuild(self)
        case None       => debug(s"Failed to accept member $id")
      }
    }
  }

  /**
   * Assign a depute leader
   * @param dep the request
   */
  def assignDeputy(dep: AssignDeputy) {
    val msg =
      if (dep.requestorID == guildInfo.leader) {
        if (!reachedMaxDeputies || dep.removing) {
          membersInfo.values.find(_.name == dep.name) match {
            case Some(member) =>
              val newRank = if (dep.removing) GuildRanks.Member else GuildRanks.DeputyLeader

              CharacterGuilds.updateRank(member.id, newRank) onSuccess { case _ =>
                setMembersRank(member.id, newRank)
                member.actor ! GuildRankChanged
                if (!dep.removing)
                  publish(MsgNewDeputy(member.name))
              }

              if (!dep.removing)
                s"${member.name} is now a depute"
              else
                s"${member.name} has been stripped of their title"

            case None => "Member not found"
          }
        } else
          "Your guild is full of deputies."
      } else
        "Not the leader..."

    dep.npc ! dep.copy(msg = msg)
  }

  /**
   * Change the guilds leader
   * @param chg The command sent from the npc
   */
  def changeLeader(chg: ChangeGuildLeaderShip) {
    val msg =
      if (chg.requestorID == guildInfo.leader)
        membersInfo.values.find(_.name == chg.name) match {
          case Some(member) =>
            CharacterGuilds.switchLeaders(guildInfo.guildid, guildInfo.leader, member.id) onSuccess { case _ =>
              refresh()
              chg.client ! GuildRankChanged
              member.actor ! GuildRankChanged
              publish(MsgNewGuildLeader(member.name))
            }

            s"New guild leader is ${member.name}"

          case None => s"${chg.name} not in guild"
        } else
        "You are not the leader..."

    chg.npc ! chg.copy(msg = msg)
  }

  /**
   * Disband the guild
   * @param requestor who is requesting the disband
   */
  def disbandGuild(requestor: Int) {
    if (requestor == guildInfo.leader) {
      GameGuilds.delete(guildid) match {
        case true  =>
          publish(GuildDisbanded)
          publish(MsgYourGuildDisbanded)
          membersInfo.values.foreach(m => unSubscribeMember(m.actor, m.id))
          membersInfo.clear()
          context.stop(self)
          debug(s"Guild $guildid stopping")
        case false => debug(s"Failed to delete guild $guildid")
      }
    }
  }

  /**
   * Subscribe a member to the guild actor
   * @param client the clients actor
   * @param charID the clients id
   */
  def subscribeMember(client: ActorRef, rank: Int, charID: Int) {
    if (charID == guildInfo.leader)
      publish(MsgGuildLeaderOnline)

    setMemberOnlineStatus(charID)
    setMemberActor(charID, client)
    setMembersRank(charID, rank)

    worldEvents.subscribe(client, toGuild)
  }

  /**
   * Kick a guild member by name
   * @param name the members name
   */
  def kick(name: String) =
    if (name != leaderName) {
      membersInfo.values.find(_.name == name) match {
        case Some(member) =>
          CharacterGuilds.delete(member.id)
          sendMember(member.id, KickedFromGuild)
          unSubscribeMember(member.actor, member.id)
          membersInfo.remove(member.id)
        case None         => debug(s"Trying to kick $name when not in guild")
      }
    }

  /**
   * Add a new member to the member info list. This is usually only ran if
   * a member was just invited to the guild
   * @param charID new members id
   * @param name new members name
   * @param level new members level
   */
  def addMember(charID: Int, name: String, level: Int) {
    if (!membersInfo.contains(charID))
      membersInfo.put(charID, MemberInfo(charID, name, GuildRanks.Member, level))
  }

  /**
   * Unsubscribe a member from the guild actor
   * @param client the clients actor
   * @param charID the clients id
   */
  def unSubscribeMember(client: ActorRef, charID: Int) {
    worldEvents.unsubscribe(client, toGuild)
    setMemberOnlineStatus(charID, online = false)
  }

  /**
   * Adjust a guild members online status
   * @param memberID the members id
   * @param online whether they are online
   */
  def setMemberOnlineStatus(memberID: Int, online: Boolean = true) {
    membersInfo.get(memberID) match {
      case Some(member) => member.online = online
      case _            => debug(s"$memberID not in guild $guildid")
    }
  }

  /**
   * Adjust a guild members rank
   * @param memberID the members id
   * @param rank Their guild rank
   */
  def setMembersRank(memberID: Int, rank: Int) {
    membersInfo.get(memberID) match {
      case Some(member) => member.rank = rank
      case _            => debug(s"$memberID not in guild $guildid")
    }
  }

  /**
   * Set a members level
   * @param memberID the members id
   * @param newLevel the members new level
   */
  def setMemberLevel(memberID: Int, newLevel: Int) {
    membersInfo.get(memberID) match {
      case Some(member) => member.level = newLevel
      case _            => debug(s"$memberID not in guild $guildid")
    }
  }

  /**
   * Set a members actor
   * @param memberID the memebers id
   * @param actor the actorRef
   */
  def setMemberActor(memberID: Int, actor: ActorRef) {
    membersInfo.get(memberID) match {
      case Some(member) => member.actor = actor
      case _            => debug(s"$memberID not in guild $guildid")
    }
  }

  /**
   * Sends the guild information to a client
   * @param client the client who requested the information
   * @param membersRank the clients rank
   * @param membersDonation the clients donation
   */
  def sendGuildInfoTo(client: ActorRef, membersRank: Byte, membersDonation: Int) {
    client ! GuildInformation(guildInfo.guildid, membersDonation, guildInfo.fund, membersInfo.size, membersRank, leaderName)
    client ! GuildBulletin(guildInfo.bulletin)
  }

  /**
   * Member is making a donation
   * @param from who sent it
   * @param amount the amount
   */
  def donate(from: String, amount: Int) {
    guildInfo = guildInfo.copy(fund = guildInfo.fund + amount)
    publish(MsgDonatedToGuild(from, amount))
    save()
  }

  /**
   * Update the guilds bulletin message and notify all members of the change
   * @param msg the new msg
   */
  def updateBulletin(msg: String) {
    guildInfo = guildInfo.copy(bulletin = msg)
    publish(GuildBulletin(guildInfo.bulletin))
    save()
  }

  /**
   * Build the guild member list
   */
  def buildMemberList() {
    CharacterGuilds.guildMemberList(guildInfo.guildid) onSuccess { case members =>
      membersInfo.clear()
      members.foreach(member => membersInfo += member.id -> member)
    }
  }

  /**
   * Has this guild reached the maximum deputie limit
   * @return
   */
  def reachedMaxDeputies = membersInfo.values.count(_.rank == GuildRanks.DeputyLeader) >= maxDeputies

  /**
   * Save the guilds state
   */
  def save() {
    GameGuilds.update(guildInfo)
  }

  /**
   * Send a message to a specific client
   * @param to who to send to
   * @param msg the message to send
   */
  def sendMember(to: Int, msg: Any) {
    worldEvents.publish(MessageEvent(ToPlayer(Some(to.toString)), msg))
  }

  /**
   * Publish a message to the guild
   * @param msg
   */
  def publish(msg: Any) {
    worldEvents.publish(MessageEvent(toGuild, msg))
  }
}


