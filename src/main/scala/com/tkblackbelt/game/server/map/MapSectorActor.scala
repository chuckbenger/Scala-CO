package com.tkblackbelt.game.server.map

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.tkblackbelt.game.packets.Interaction
import com.tkblackbelt.game.server.client.ClientHandler.SendMeYourEntityInfo
import com.tkblackbelt.game.server.client.handler.packet.GeneralHandler.JumpedTo
import com.tkblackbelt.game.server.client.handler.packet.InteractionHandler.{AttackEvent, InteractionEvent}
import com.tkblackbelt.game.server.map.FloorActor.{FloorEvent, GetFloorItems}
import com.tkblackbelt.game.server.map.MapActor._
import com.tkblackbelt.game.server.map.MapSectorActor._
import com.tkblackbelt.game.server.map.SubscribeHelpers._
import com.tkblackbelt.game.util.ActorHelper

import scala.collection.mutable

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

object MapSectorActor {
  case class SendToThisSector(msg: SectorBroadCast)
  case object Setup
  case class AreYouNearMe(otherSector: Sector)
  case object UnSubscribeMe
  case class SectorInteractionEvent(sector: ActorRef, interaction: InteractionEvent)
  case class HandledInteraction(interaction: Interaction)
  case class HandledInteractionEvent(interaction: InteractionEvent)
}

class MapSectorActor(sectorName: String,
  sector: Sector,
  mapID: Int,
  map: ActorRef,
  npcManager: ActorRef,
  mobManager: ActorRef,
  mapEvents: WorldEventBus) extends Actor with ActorLogging with ActorHelper {

  val floorActor                            = context.actorOf(Props[FloorActor])
  val sectorClassify                        = ToMapSector(Some(sectorName))
  val mobSectorClassify                     = ToMobSector(Some(sectorName))
  val sectorsInRange: mutable.Set[ActorRef] = mutable.Set.empty


  def receive = {

    case Setup                     => sendToSiblings(AreYouNearMe(sector))
    case AreYouNearMe(otherSector) => addSectorIfNear(otherSector)
    case UnSubscribeMe             => mapEvents.unsubscribe(sender, sectorClassify)

    case x: GetSurroundings => {
      npcManager ! x
      mobManager ! x
      floorActor ! GetFloorItems(x.client)
      broadcastToSectorsInRange(SendMeYourEntityInfo(x.client, x.x, x.y))
    }

    case jump: JumpedTo => {
      stillInRangeCheck(jump.client, jump.newX, jump.newY)
      broadcastToSectorsInRange(jump)
    }

    case getSec: GetMySector                  => handleGetSector(getSec)
    case interaction: InteractionEvent        => handleInteraction(interaction)
    case HandledInteraction(interaction)      => broadcastToSectorsInRange(interaction)
    case HandledInteractionEvent(interaction) => broadcastToSectorsInRange(interaction)
    case event: FloorEvent                    => floorActor ! event
    case msg: SectorBroadCast                 => broadcastToSectorsInRange(msg)
    case SendToThisSector(msg)                => publish(msg)
    case broadcast: BroadCastable             => publish(broadcast)


    case x => unhandledMsg(x)
  }

  /**
   * Handle a sector subscription request
   */
  def handleGetSector(sectorRequest: GetMySector) {
    if (sector.isInSector(sectorRequest.x, sectorRequest.y)) {
      sectorRequest match {
        case MobGetMySector(mob, _, _)       => {
          mapEvents.subscribe(mob, mobSectorClassify)
        }
        case ClientGetMySector(client, _, _) => mapEvents.subscribe(client, sectorClassify)
      }
      sectorRequest.client ! YourSectorIs(self)
    }
  }

  /**
   * Add a sector if it's in range of another
   */
  def addSectorIfNear(otherSector: Sector) {
    if (sender != self && otherSector.isNear(sector)) {
      sectorsInRange += sender
    }
  }

  /**
   * Check to see if client is still in this sector
   */
  def stillInRangeCheck(client: ActorRef, x: Int, y: Int) {
    if (!sector.isInSector(x, y)) {
      mapEvents.unsubscribe(client, sectorClassify)
      sendToSectorsInRange(ClientGetMySector(client, x, y))
    }
  }

  /**
   * Send a message to all sectors in range of this one
   */
  def broadcastToSectorsInRange(msg: SectorBroadCast) {
    publish(msg)
    sectorsInRange.foreach(_ ! SendToThisSector(msg))
  }

  /**
   * Send a general message to all sectors in range
   */
  def sendToSectorsInRange(msg: AnyRef) {
    sectorsInRange.foreach(_ ! msg)
  }

  /**
   * Send a message to all sectors for this map
   */
  def sendToSiblings(msg: AnyRef) {
    context.actorSelection("../sector*") ! msg
  }

  /**
   * Handle a client -> map interaction
   */
  def handleInteraction(interaction: InteractionEvent): Unit = interaction match {
    case attack: AttackEvent => mobManager ! SectorInteractionEvent(self, attack)
    case x                   => unhandledMsg(x)
  }

  /**
   * Publish an event to all subscribers of this map
   * @param msg the message to send
   */
  def publish(msg: BroadCastable) = msg match {
    case sectorB: SectorBroadCast   =>
      mapEvents.publish(MessageEvent(sectorClassify, msg))
      if (sectorB.isInstanceOf[MobBroadCast])
        mapEvents.publish(MessageEvent(mobSectorClassify, msg))
    case mapBroadCast: MapBroadCast => map ! map
    case world: WorldBroadCast      => map ! world
    case player: PlayerBroadCast    => map ! player
    case x                          => unhandled(x)
  }


}


















