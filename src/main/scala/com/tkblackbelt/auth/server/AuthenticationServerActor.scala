package com.tkblackbelt.auth.server

import java.net.InetSocketAddress
import akka.actor._
import akka.io._
import akka.io.Tcp.Connected
import akka.io.IO
import akka.io.Tcp.Bind
import akka.io.Tcp.Bound
import com.tkblackbelt.core.global.Globals
import com.tkblackbelt.core.piplines.{PacketEncryptionStage, PacketStage}
import akka.io.TcpPipelineHandler.{WithinActorContext, Init}
import com.tkblackbelt.core.Packet
import com.tkblackbelt.java.encryption.Cryptographer

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


class AuthenticationServerActor(local: InetSocketAddress) extends Actor with ActorLogging {

  import log._
  
  implicit def system = context.system

  IO(Tcp) ! Bind(self, local)

  def receive: Receive = {
    case _: Bound ⇒
      info(s"Auth server bound to $local")
      context.become(bound(sender))
    case x => debug(s"AuthenticationServerActor got $x")
  }

  def bound(listener: ActorRef): Receive = {
    case Connected(remote, _) => {
      val crypto = new Cryptographer
      val init  = TcpPipelineHandler.withLogger(log,
          new PacketStage(log) >>
          new LengthFieldFrame(Short.MaxValue, Globals.byteOrder, 2, true) >>
          new PacketEncryptionStage(crypto) >>
          new TcpReadWriteAdapter
      )

      val connection = sender
      val handler = context.actorOf(akka.actor.Props(new AuthenticationClientActor(init, connection)).withDeploy(Deploy.local))
      val pipeline = context.actorOf(TcpPipelineHandler.props(
        init, connection, handler).withDeploy(Deploy.local))

      connection ! Tcp.Register(pipeline)
    }
    case x => debug(s"AuthenticationServerActor got $x")
  }
}
