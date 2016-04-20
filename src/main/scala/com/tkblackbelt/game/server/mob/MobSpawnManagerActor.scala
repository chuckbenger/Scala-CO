package com.tkblackbelt.game.server.mob

import akka.actor._
import com.tkblackbelt.game.constants.Guards
import com.tkblackbelt.game.models.MonsterSpawnInfo
import com.tkblackbelt.game.models.database.Mobs
import com.tkblackbelt.game.server.map.MapActor.GetSurroundings
import com.tkblackbelt.game.server.map.MapSectorActor.SectorInteractionEvent
import com.tkblackbelt.game.server.mob.GenericMobActor.MobDied
import com.tkblackbelt.game.util.{ActorHelper, MathHelper}
import scala.concurrent.ExecutionContext.Implicits._

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
class MobSpawnManagerActor(spawnInfo: MonsterSpawnInfo, mapActor: ActorRef) extends Actor with ActorLogging with ActorHelper {

  import spawnInfo.info._


  override def preStart() {
    startMobs()
  }

  val respawnRate = 10.seconds

  def receive = {
    case getSurround: GetSurroundings =>
      if (isInViewOfSpawn(getSurround.x, getSurround.y))
        sendToAllMobs(getSurround)

    case event: SectorInteractionEvent =>
      if (isInViewOfSpawn(event.interaction.x, event.interaction.y))
        sendToMob(event.interaction.target, event)

    case MobDied => context.system.scheduler.scheduleOnce(respawnRate)(startMob())

    case x => unhandledMsg(x)
  }

  /**
   * Starts all the mobs for this spawn
   * @return a list of mob actors
   */
  def startMobs() =
    (0 to numbertospawn).map(_ => startMob())

  /**
   * Return a new mob instance
   */
  def startMob() = {
    val id = Mobs.nextID
    spawnInfo.info.id match {
      case x if Guards.isGuard(x) => context.actorOf(Props(new GuardMob(id, spawnInfo, self, mapActor)), id.toString)
      case x => context.actorOf(Props(new MobActor(id, spawnInfo, self, mapActor)), id.toString)
    }
  }

  /**
   * Send a message to a mob
   * @param id the mobs id
   * @param msg the message to send
   */
  def sendToMob(id: Int, msg: Any) =
    context.actorSelection(id.toString) ! msg

  /**
   * Send a message to all mobs handled by this actor
   * @param msg the message to send
   */
  def sendToAllMobs(msg: Any) =
    context.children.foreach(_ ! msg)

  /**
   * Send a message to all sibling spawns
   * @param msg the message to send
   */
  def sendToSiblingSpawns(msg: Any) =
    context.actorSelection("../spawn*") ! msg


  /**
   * Returns whether a x y point is with view of the spawn location
   * @param x the x coord
   * @param y the y coord
   */
  def isInViewOfSpawn(x: Int, y: Int): Boolean = {
    val insideX = x <= xstop && x >= xstart
    val closeToX = MathHelper.isInRenderDistance(x, y, xstop, y) || MathHelper.isInRenderDistance(x, y, xstart, y)
    val insideY = y <= ystop && y >= ystart
    val closeToY = MathHelper.isInRenderDistance(x, y, x, ystop) || MathHelper.isInRenderDistance(x, y, x, ystart)
    (insideX || closeToX) && (insideY || closeToY)
  }

}



















