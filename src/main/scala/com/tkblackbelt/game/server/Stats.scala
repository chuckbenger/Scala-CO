package com.tkblackbelt.game.server

import java.util.concurrent.atomic.AtomicInteger

import com.tkblackbelt.game.conf.GameConfig

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
 * Server statistics
 */
object Stats {

  val playersOnline = new AtomicInteger(0)

  /**
   * Check if the server is full. If not increment the counter.
   * @return whether the server is full
   */
  def reachedMaxPlayers = {
    val reached = playersOnline.incrementAndGet() > GameConfig.Settings.maxPlayers
    if (reached) playersOnline.decrementAndGet()
    reached
  }

  /**
   * A plyers disconnected. Decrement the counter
   * @return the new player count
   */
  def playerDisconnect = {
    playersOnline.decrementAndGet()
  }

  override def toString: String = s"players online = $playersOnline"
}
