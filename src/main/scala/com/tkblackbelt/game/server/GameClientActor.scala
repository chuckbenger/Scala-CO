package com.tkblackbelt.game.server

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.Tcp.{Close, Closed, PeerClosed}
import akka.io.TcpPipelineHandler.{Init, WithinActorContext}
import akka.util.Timeout
import com.tkblackbelt.core.{IncomingPacket, Packet}
import com.tkblackbelt.game.conf.GameConfig
import com.tkblackbelt.game.models.database.GameCharacters
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.ClientHandler
import com.tkblackbelt.game.server.client.ClientHandler.{Disconnect, Persist, SendPacket}
import com.tkblackbelt.game.util.{Benchmark, ActorHelper}
import com.tkblackbelt.java.encryption.Cryptographer

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.concurrent.duration._
import com.tkblackbelt.game.models.Character
import GameMessages._

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
 * Actor for handling a individual game client tcp connection.
 */
class GameClientActor(init: Init[WithinActorContext, Packet, Packet],
  tcpConnection: ActorRef,
  crypto: Cryptographer,
  mapManager: ActorRef) extends Actor with ActorLogging with ActorHelper {

  import init._
  import log._

  val persistEvery  = GameConfig.Settings.persistClient
  val timeout       = Timeout(5.second)
  var pipeline      = ActorRef.noSender
  var clientHandler = ActorRef.noSender

  /**
   * Default behavior for handling the initial login
   */
  def receive = {
    case pipe: ActorRef             => pipeline = pipe
    case Event(req: IncomingPacket) => req.packet match {
      case auth: AuthorizationMessage => handleLoginRequest(auth)
      case x                          => disconnect()
    }
    case Closed                     => debug("Client closed connection")
    case x                          => unhandledMsg(x)
  }

  /**
   * Handles a clients login request to the server. The actor will
   * move to a different state (inGame, or creation) or disconnect
   * after this point
   * @param req the authorization request
   */
  def handleLoginRequest(req: AuthorizationMessage) = {
    crypto.setKeys(req.token, req.uid)
    Future {
      Benchmark.time(log, "Login request") {
        GameCharacters.getByAccount(req.uid) match {
          case Some(char) => loginCharacter(char)
          case None       => setupNewCharacter()
        }
      }
    } onFailure { case x =>
      debug(s"$req handle login failed $x")
    }
  }

  /**
   * Login request found a character. Log them in
   * @param char the character found
   */
  def loginCharacter(char: Character) {
    debug(s"Found character $char")
    send(SystemMessages.SuccessLogin)
    send(CharacterInfo(char))
    send(WelcomeMessage)
    clientHandler = context actorOf(Props.apply(new ClientHandler(char, self, mapManager)), "player" + char.id)
    context become inGame
    startPersistScheduler
  }

  /**
   * No character found. Prompt user to create
   */
  def setupNewCharacter() {
    send(SystemMessages.NewUser)
    context become characterCreation
  }

  /**
   * Schedule and event to update the clients state every x minutes
   */
  def startPersistScheduler =
    context.system.scheduler.schedule(persistEvery, persistEvery, self, Persist)

  /**
   * Actor behavior when the client is in the process of creating a new character
   */
  def characterCreation: Receive = {
    case Event(req: CharacterCreation) => Future {
      if (GameCharacters.create(req))
        send(SystemMessages.SuccessLogin)
      disconnect()
    }
    case x                             => unhandledMsg(x)
  }

  /**
   * Actor behaviour when a client has logged in the game successfully
   */
  def inGame: Receive = {
    case Event(packet: IncomingPacket) => clientHandler ! packet
    case SendPacket(packet)            => send(packet)
    case Persist                       => persist()
    case Closed                        => onDisconnect()
    case PeerClosed                    => onDisconnect()
    case Disconnect                    => disconnect()
    case x                             => unhandledMsg(x)
  }

  /**
   * Notifies the client actor to persist it's state
   */
  def persist() = clientHandler ! Persist

  /**
   * Send a packet down the pipeline to the client
   * @param packet the packet to send
   */
  def send(packet: Packet) = {
    pipeline ! command(packet)
  }

  /**
   * Send a list of packets
   * @param packets the packets to send
   */
  def send(packets: List[Packet]) {
    packets.foreach(send)
  }

  def disconnecting: Receive = {
    case x => //Do nothing
  }

  /**
   * Called when the actor receives a disconnect event from the client
   */
  def onDisconnect() {
    context.become(disconnecting)
    clientHandler ! Closed
    Stats.playerDisconnect
    shutdown()
    debug(s"$clientHandler closed connection")

  }

  /**
   * Close the connection
   */
  def disconnect() = {
    tcpConnection ! Close
  }

  /**
   * Shutdown the actor
   */
  def shutdown() {
    context.stop(self)
  }
}




















