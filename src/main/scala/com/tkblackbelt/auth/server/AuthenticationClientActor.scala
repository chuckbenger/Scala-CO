package com.tkblackbelt.auth.server

import akka.actor.{ActorRef, ActorLogging, Actor}
import com.tkblackbelt.auth.packets.{LoginResponse, LoginRequest}
import akka.io.TcpPipelineHandler.{Init, WithinActorContext}
import com.tkblackbelt.core.{IncomingPacket, UnHandledPacket, Packet}
import akka.io.Tcp.{Closed, Close}
import com.tkblackbelt.auth.models.AuthLogins
import com.tkblackbelt.core.global.Globals.gameServers
import com.tkblackbelt.game.models.Tables.AccountsRow


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

object AuthenticationClientActor {
  object Messages {

  }
}

class AuthenticationClientActor(init: Init[WithinActorContext, Packet, Packet], tcpConnection: ActorRef) extends Actor with ActorLogging {

  import init._
  import log._

  def receive = {
    case Event(p: IncomingPacket) => p.packet match {
      case ev: LoginRequest    => handleLogin(ev)
      case ev: UnHandledPacket => debug(s"Unhandled packet $ev")
      case x                   => debug(s"Unknown Authentication Packet Receive $x")
    }
    case Closed                   => debug("Client closed connection")
    case x                        => debug(s"Unknown Authentication Packet Receive $x")
  }

  def handleLogin(req: LoginRequest) {
    AuthLogins.get(req.username, req.password) match {
      case Some(account) => validAccount(req, account)
      case None          => invalidAccount(req)
    }
  }

  def validAccount(req: LoginRequest, account: AccountsRow) {
    debug(s"Valid login $req")
    gameServers.get(req.server) match {
      case Some(server) => {
        debug(s"redirecting $req to $server")
        sender ! command(LoginResponse(account.id, 1, server.getHostString, server.getPort))
      }
      case None         => tcpConnection ! Close
    }
  }

  def invalidAccount(req: LoginRequest) {
    debug(s"Invalid login $req")
    AuthLogins.createUser(req.username, req.password)
    tcpConnection ! Close
  }

}
