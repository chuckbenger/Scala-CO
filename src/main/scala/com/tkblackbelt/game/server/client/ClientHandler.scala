package com.tkblackbelt.game.server.client

import akka.actor.ActorRef
import akka.io.Tcp.Closed
import com.tkblackbelt.core.{IncomingPacket, Packet}
import com.tkblackbelt.game.models.Character
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.ClientHandler._
import com.tkblackbelt.game.server.client.handler._
import com.tkblackbelt.game.server.client.handler.packet.PacketHandler
import com.tkblackbelt.game.server.client.handler.server.ServerHandler
import com.tkblackbelt.game.server.map.MapActor.{MapBroadCast, SectorBroadCast}

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

object ClientHandler {

  case class SendPacket(packet: Packet)
  case class EntitySpawnInfo(entity: EntitySpawn)
  case class SendMeYourEntityInfo(client: ActorRef, x: Int, y: Int) extends SectorBroadCast
  case class LeavingMap(id: Int, x: Int, y: Int) extends MapBroadCast
  case class EnteringMap(entity: EntitySpawn) extends MapBroadCast
  case class CharacterOutOfView(id: Int, x: Int, y: Int) extends SectorBroadCast
  case class TeleportTo(map: Int, x: Int, y: Int)
  case object TeleportToPreviousMap
  case object Disconnect
  case object Persist
}

/**
 * Handles a clients state
 * @param connection The actor handling messages for this client
 * @param mapManager The actor managing game maps
 */
class ClientHandler(val char: Character,
  val connection: ActorRef,
  val mapManager: ActorRef) extends Client with ServerHandler with PacketHandler {


  char.handler = self

  override def postStop() {
    persist()
  }

  def receive = {
    case p: IncomingPacket => handlePacket(p.packet)
    case Persist           => persist()
    case Closed            => onDisconnect()
    case Disconnect        => disconnect()
    case m: Any            => handleServerMessage(m)
    case x                 => unhandledMsg(x)
  }

  /**
   * Persists the Clients state to the database
   */
  def persist() {
    char.save()
    inventory.save()
    warehouse.save()
  }

  /**
   * Send a packet to the client
   */
  def send(p: Packet) = {
    if (connection != ActorRef.noSender)
      connection ! SendPacket(p)
  }

  /**
   * Tell the connection to disconnect
   */
  def disconnect() = {
    context.become(disconnecting)
    connection ! Disconnect
  }

  /**
   * Ignore messages
   */
  def disconnecting: Receive = {
    case Closed => onDisconnect()
    case _      => //do nothing
  }

  /**
   * Runs when the clients tcp connection is disconnected
   */
  def onDisconnect() {
    unSubscribeFromCurrentMap()
    unSubscribeFromWorld()
    guild.unsubscribe()
    shutdown()
  }

  /**
   * Shut down the actor
   */
  def shutdown() {
    context.stop(self)
  }
}




















