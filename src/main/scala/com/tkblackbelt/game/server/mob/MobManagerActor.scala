package com.tkblackbelt.game.server.mob

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.tkblackbelt.game.models.MonsterSpawnInfo
import com.tkblackbelt.game.models.database.MonsterSpawns
import com.tkblackbelt.game.server.map.MapActor.GetSurroundings
import com.tkblackbelt.game.server.map.MapSectorActor.SectorInteractionEvent
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

/**
 * Manages a single maps spawns
 * @param map the id of the map it's managing
 * @param mapActor reference to the map
 */
class MobManagerActor(map: Int, mapActor: ActorRef) extends Actor with ActorLogging with ActorHelper {

  val spawnsForMap = MonsterSpawns.MobSpawnsByMap.getOrElse(map, List.empty)

  override def preStart() {
    startSpawns()
  }

  def receive = {
    case getSurroundings: GetSurroundings    => sendToAllSpawns(getSurroundings)
    case sectorEvent: SectorInteractionEvent => sendToAllSpawns(sectorEvent)
    case x                                   => unhandledMsg(x)
  }

  /**
   * Start all map spawns
   */
  def startSpawns() =
    spawnsForMap.foreach(startSpawn)

  /**
   * Starts up a single spawn actor
   * @param spawn the information about the spawn
   */
  def startSpawn(spawn: MonsterSpawnInfo) =
    context.actorOf(Props.apply(new MobSpawnManagerActor(spawn, mapActor)), spawnName(spawn.info.uniqueid))

  /**
   * Returns a unique spawn idea formatted to a actor value
   * @param id the unique spawn id
   */
  def spawnName(id: Int) = s"spawn$id"

  /**
   * Sends a message to all mob spawns
   * @param msg the message to send
   */
  def sendToAllSpawns(msg: Any) =
    context.children.foreach(_ ! msg)

}
