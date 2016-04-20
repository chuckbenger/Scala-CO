package com.tkblackbelt.game.server.map

import akka.event.{ActorEventBus, SubchannelClassification}
import akka.util.Subclassification

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


/*
 * Chat Coordinates
 * Map/nnn send to map
 * World/ send to entire world
 * Team/nnn send to a team
 */
object EventSegments {
  val Map       = "map"
  val Team      = "team"
  val Player    = "player"
  val MapSector = "mapsector"
  val MobSector = "mobsector"
  val Friend    = "friend"
  val Guild     = "guild"
}

/**
 * Helper methods for sending event messages
 */
object SubscribeHelpers {
  val ToMap      : (Option[String]) => EventCoordinate = EventCoordinate(EventSegments.Map, _)
  val ToAllMaps                                        = ToMap(None)
  val ToPlayer   : (Option[String]) => EventCoordinate = EventCoordinate(EventSegments.Player, _)
  val ToAllPlayers                                     = ToPlayer(None)
  val ToMapSector: (Option[String]) => EventCoordinate = EventCoordinate(EventSegments.MapSector, _)
  val ToMobSector: (Option[String]) => EventCoordinate = EventCoordinate(EventSegments.MobSector, _)
  val ToFriend   : (Option[String]) => EventCoordinate = EventCoordinate(EventSegments.Friend, _)
  val ToTeam     : (Option[String]) => EventCoordinate = EventCoordinate(EventSegments.Team, _)
  val ToGuild    : (Option[String]) => EventCoordinate = EventCoordinate(EventSegments.Guild, _)

}

case class EventCoordinate(segment: String, target: Option[String])
case class MessageEvent(coord: EventCoordinate, message: Any)


class ServerGroupSubclassification extends Subclassification[EventCoordinate] {

  override def isEqual(x: EventCoordinate, y: EventCoordinate): Boolean = {
    val equal = (x.segment == y.segment) && (x.target == y.target)
    equal
  }

  override def isSubclass(x: EventCoordinate, y: EventCoordinate): Boolean = (x.segment == y.segment) && (x.target == y.target || x.target == None)
}


class WorldEventBus extends ActorEventBus with SubchannelClassification {
  type Event = MessageEvent
  type Classifier = EventCoordinate

  protected def classify(event: Event): Classifier = event.coord

  protected def subclassification = new ServerGroupSubclassification

  override def subscribe(subscriber: Subscriber, to: Classifier): Boolean = {
    val result = super.subscribe(subscriber, to)
    //println("sub " + result + " " + subscriber + " " + to)
    result
  }

  override def unsubscribe(subscriber: Subscriber, from: Classifier): Boolean = {
    val result = super.unsubscribe(subscriber, from)
    // println("unsub " + result + " " + subscriber + " " + from)
    result
  }

  protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event.message
  }
}















