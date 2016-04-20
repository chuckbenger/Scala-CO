package com.tkblackbelt.game.server.client.handler.server

import akka.actor.ActorRef
import com.tkblackbelt.game.constants.StatusFlags
import com.tkblackbelt.game.packets.{CreateTeam, RequestJoin, TeamMateInformation}
import com.tkblackbelt.game.server.client.TeamActor.AddMemberToTeam
import com.tkblackbelt.game.server.client.handler.Client
import com.tkblackbelt.game.server.client.handler.core.CoreTeamHandler
import com.tkblackbelt.game.server.client.handler.server.ServerTeamActionHandler._
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

object ServerTeamActionHandler {
  trait ServerTeamEvent
  case class YourLeaderOfTeam(team: ActorRef, teamID: Int) extends ServerTeamEvent
  case class JoinedTeam(actor: ActorRef) extends ServerTeamEvent
  case class AskToJoinTeam(id: Int) extends ServerTeamEvent
  case object TeamDismissed extends ServerTeamEvent
  case class LeaderAccepted(team: ActorRef) extends ServerTeamEvent
  case class SendInfoToMember(member: ActorRef) extends ServerTeamEvent
  case class AcceptedInvite(who: Int, sender: ActorRef) extends ServerTeamEvent
}

trait ServerTeamActionHandler extends Client with ActorHelper with CoreTeamHandler {

  def handleTeamAction(event: ServerTeamEvent) = event match {
    case YourLeaderOfTeam(actor, id) =>
      team = actor
      send(CreateTeam(id))
      char.status = StatusFlags.TeamLeader

    case JoinedTeam(teamActor) =>
      team = teamActor
      sendTeam(TeamMateInformation.fromChar(char))

    case SendInfoToMember(member)   => member ! TeamMateInformation.fromChar(char)
    case AskToJoinTeam(id)          => send(RequestJoin(id))
    case TeamDismissed              => team = ActorRef.noSender
    case AcceptedInvite(id, sender) => sendTeam(AddMemberToTeam(id, sender))
    case LeaderAccepted(acceptTeam) => inTeamOrElse(acceptTeam ! AddMemberToTeam(char.id, self))
    case x                          => unhandledMsg(x)
  }

}
