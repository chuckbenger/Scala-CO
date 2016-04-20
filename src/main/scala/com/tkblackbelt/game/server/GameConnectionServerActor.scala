package com.tkblackbelt.game.server

import java.net.InetSocketAddress

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.io.Tcp.{Close, Bind, Bound, Connected}
import akka.io._
import com.tkblackbelt.core.global.Globals
import com.tkblackbelt.core.piplines.{PacketEncryptionStage, PacketStage}
import com.tkblackbelt.game.server.map.WorldManagerActor
import com.tkblackbelt.game.util.ActorHelper
import com.tkblackbelt.core.util.Rainbow._
import com.tkblackbelt.java.encryption.Cryptographer
import scala.concurrent.duration._

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
 * Actor listening for incoming connections
 * @param local the address to listen on
 */
class GameConnectionServerActor(local: InetSocketAddress) extends Actor with ActorLogging with ActorHelper {

  import log._

  implicit def system = context.system

  akka.io.IO(Tcp) ! Bind(self, local)

  /**
   * Startup the map manager
   */
  val mapManager = context.actorOf(Props[WorldManagerActor], "MAPS")


  def receive: Receive = {
    case _: Bound ⇒
      info(s"Game server bound to $local".toUpperCase.green)
      context.become(bound(sender))
    case x        => unhandledMsg(x)
  }

  def bound(listener: ActorRef): Receive = {

    /**
     * Called whenever a new client connects.
     * A new actor is spawned off to handle that connection.
     * Data transfers through a pipeline of stages (raw <--> crypto <--> builder <--> client actor
     */
    case Connected(remote, _) =>
      if (!Stats.reachedMaxPlayers) {
        val crypto = new Cryptographer
        val init = TcpPipelineHandler.withLogger(log,
          new PacketStage(log) >>
          new LengthFieldFrame(Short.MaxValue, Globals.byteOrder, 2, true) >>
          new PacketEncryptionStage(crypto) >>
          new TcpReadWriteAdapter >>
          new BackpressureBuffer(lowBytes = 1000, highBytes = 10000, maxBytes = 1000000)
        )

        val connection = sender
        val handler = context.actorOf(Props.apply(new GameClientActor(init, connection, crypto, mapManager)).withDeploy(Deploy.local))
        val pipeline = context.actorOf(TcpPipelineHandler.props(init, connection, handler).withDeploy(Deploy.local))

        handler ! pipeline
        connection ! Tcp.Register(pipeline)
      } else {
        sender ! Close
        info(s"Max number of players reached")
      }
    case x                    => unhandledMsg(x)
  }

  /**
   * If the map manager crashes, restart the actor
   */
  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute) {
    case _ => Restart
  }
}

























