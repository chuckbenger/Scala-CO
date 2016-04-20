package com.tkblackbelt.game.server.npc.scripts

import akka.actor.ActorRef
import com.tkblackbelt.game.constants.GuildRanks
import com.tkblackbelt.game.models.database.{CharacterGuilds, GameGuilds}
import com.tkblackbelt.game.server.client.handler.server.NpcCommandHandler.{ServerNpcCommand, AssignDeputy, ChangeGuildLeaderShip}
import com.tkblackbelt.game.server.client.handler.server.ServerGuildRequestHandler.{Disband, GuildCreated}
import com.tkblackbelt.game.server.npc.NPCActor.NpcInteraction
import com.tkblackbelt.java.npc.Response

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
class GuildConductor_10003 extends NpcScript {

  val id           = List(10003)
  val minLevel     = 90
  val requiredGold = 1000000

  override def handle(implicit event: NpcInteraction, client: ActorRef): Unit = {
    method(event.linkID).invoke(this, event, client)

    import event.{char, guild}

    @Response
    def init() {
      Options(
        "Hello. I am in charge of all the guilds, and I have the power to create and destroy them!",
        "Let me think about it",
        "Create Guild" -> method("confirmCreate"),
        "Disband My Guild" -> method("confirmDisband"),
        "Pass my leadership" -> method("promptNewLeader"),
        "Give Dep. Leader" -> method("promptDeputyLeader"),
        "Remove Dep. Leader" -> method("promptDeputyLeader"),
        "Show Others" -> method("showOthers"))
    }

    @Response
    def confirmCreate() =
      QuestionIf(hasNoGuild,
        "To create a guild, you must be at least level 90 and have 1,000,000 silvers. Are you sure you want to continue?",
        method("promptGuildInfo"))

    @Response
    def confirmDisband() =
      QuestionIf(isLeader,
        "Are you sure you want to disband your guild? Anyone associated with, all silvers, and any control you currently have will be gone.",
        method("disbandGuild"))

    @Response
    def promptNewLeader() =
      PromptIf(isLeader,
        "If you pass your leadership, you will lost all of the controls of your guild. Are you sure?", "Member Name", 14)

    @Response
    def promptDeputyLeader() =
      PromptIf(isLeader,
        "Give me the name of the member.",
        "Member Name", method("assignDeputy"))

    @Response
    def promptGuildInfo() =
      PromptIf(meetsRequirements(char.level, char.money) && hasNoGuild,
        "Guilds are a powerful way for a group to stay connected & fight for a common belief. Usually, strong and powerful guilds have meaningful names. Choose your name wisely.",
        "", method("createGuild"))

    @Response
    def passLeadershipTo(member: String) =
      if (isLeader)
        sendToClient(ChangeGuildLeaderShip(npc, client, NO_LINK_BACK, char.id, member))

    @Response
    def disbandGuild() =
      if (isLeader)
        sendToClient(Disband)


    @Response
    def createGuild() {
      val name = event.message
      val charID = event.char.id
      if (!GameGuilds.nameExists(name)) {
        GameGuilds.create(name, charID) onSuccess { case guild =>
          guild match {
            case Some(g) =>
              CharacterGuilds.create(charID, g.guildid, GuildRanks.Leader)
              sendToClient(GuildCreated(g.guildid))
            case None    =>
              Info("Failed to create guild.")
          }
        }
      } else
        Question("The name already exists",
          method("promptGuildInfo"), "Try again")
    }

    @Response
    def assignDeputy =
      if (isLeader)
        sendToClient(AssignDeputy(npc, client, NO_LINK_BACK, false, char.id, event.message))

    @Response
    def removeDeputy =
      if (isLeader)
        sendToClient(AssignDeputy(npc, client, NO_LINK_BACK, true, char.id, event.message))



    def hasNoGuild = {
      if (guild.rank == GuildRanks.None)
        true
      else {
        Error("You already have a guild...")
        false
      }
    }

    def isLeader = {
      if (guild.rank == GuildRanks.Leader)
        true
      else {
        Error("You are not the leader...")
        false
      }
    }


    def meetsRequirements(level: Int, money: Int)(implicit client: ActorRef) = {
      if (level >= minLevel)
        if (money >= requiredGold)
          true
        else {
          Error("To create a guild, 1,000,000 silver will be changed. You do not have the required money.")
          false
        }
      else {
        Error("Sorry, you cannot create a guild before reach level 90. Please train harder")
        false
      }
    }

  }


    /**
     * Handles a a event where the npc has requested info from a client
     */
    override def handleCallback(implicit cmd: ServerNpcCommand, client: ActorRef): Unit = {
      cmd match {
        case chg: ChangeGuildLeaderShip => Info(chg.msg)
        case dep: AssignDeputy          => Info(dep.msg)
        case x                          => Error(s"Not handler for callback $cmd")
      }
    }
}


















