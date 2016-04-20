package com.tkblackbelt.game.server.map

import java.awt.image.BufferedImage
import java.awt.{Color, Image}
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon

import akka.util.ByteString
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.core.global.ServerConfig
import com.tkblackbelt.game.util.Benchmark
import com.tkblackbelt.core.util.Rainbow._
import com.tkblackbelt.game.GameServerMain.system.log
import scala.collection.JavaConversions._
import scala.io.{Codec, Source}

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
object DMaps {

  case class MapLoad(id: Int, path: String)

  val maps = ServerConfig.mapConfig.getObjectList("maps").map { map =>
    val id = map.get("mapid").unwrapped().toString.toInt
    val path = map.get("path").unwrapped().toString
    MapLoad(id, path)
  }

  val dmaps = loadDmaps()


  /**
   * Check to see if a coord is valid to be stepped on
   */
  def check(map: Int, x: Int, y: Int) = dmaps(map).checkCoord(x, y)


  /**
   * Loads all dmaps
   * @return returns a map to a dmap
   */
  def loadDmaps(): Map[Int, DMap] = Benchmark.time(log, "Finished Loading maps".green) {
    log.info("Loading DMAPS".yellow)
    val bytes = openBaseFile
    val iter = bytes.iterator
    val totalMaps = iter.getShort - 1
    val mapIDs = (0 to totalMaps).map(_ => iter.getShort).toArray
    val maps = mapIDs.map(id => {
      val map = loadDmap(id)
      map.id -> map
    }).toMap
    log.info(s"Loaded ${maps.size} DMaps".green)
    maps
  }

  /**
   * Load a single HMap file
   * @param id the map id to load
   * @return returns the dmap
   */
  def loadDmap(id: Int): DMap = {
    val bytes = openDmap(id)
    val iter = bytes.iterator
    val maxHeight = iter.getShort
    val maxWidth = iter.getShort
    val array = Array.ofDim[Byte](maxHeight, maxWidth)

    for (y <- 0 to maxHeight - 1;
         x <- 0 to maxWidth - 1) {
      array(x)(y) = iter.getByte
    }


    new DMap(id, maxWidth, maxHeight, array)
  }

  private def openBaseFile = ByteString(Source.fromURL(getClass.getResource("/maps/GameMap.dat"), Codec.ISO8859.name).map(_.toByte).toArray)

  private def openDmap(id: Int) = ByteString(Source.fromURL(getClass.getResource(s"/maps/$id.HMap"), Codec.ISO8859.name).map(_.toByte).toArray)


  /**
   * Render the dmaps to an image
   */
  def renderDmap(id: Int) {
    dmaps(id) match {
      case dmap => {
        val image = new BufferedImage(dmap.maxWidth, dmap.maxHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = image.getGraphics

        for (y <- 0 to dmap.maxHeight - 1; x <- 0 to dmap.maxWidth - 1) {
          graphics.setColor(Color.black)
          if (dmap.checkCoord(x, y)) {
            graphics.setColor(Color.red)
            graphics.drawLine(x, y, x, y)
          } else {
            graphics.setColor(Color.black)
            graphics.drawLine(x, y, x, y)
          }
        }
        graphics.setColor(Color.white)
        graphics.fillRect(dmap.player._1, dmap.player._2, 2, 2)

        val icon = new ImageIcon(image.getScaledInstance(600, 800, Image.SCALE_SMOOTH))

        ImageIO.write(image, "jpg", new File(s"MapRenders/$id.jpg"));
      }
    }
  }

}

case class MapCoord(x: Int, y: Int)

object InvalidMapCoord extends MapCoord(0, 0)


class DMap(val id: Int, val maxWidth: Int, val maxHeight: Int, val dmap: Array[Array[Byte]]) {


  var player = (0, 0)

  /**
   * returns a dmap split into sectors
   */
  def splitIntoSectors(numberXSectors: Int, numberYSectors: Int): Map[String, Sector] = {
    val totalSectors = numberXSectors * numberYSectors
    val width = maxWidth / numberXSectors
    val height = maxHeight / numberYSectors
    val sectors = for {
      x <- 0 to numberXSectors - 1
      y <- 0 to numberYSectors - 1
    } yield Sector(x * width, y * height, width, height)

    (1 to totalSectors).map(sectorID => s"sector${id}_$sectorID").zip(sectors).toMap
  }

  def checkAndRender(x: Int, y: Int): Boolean = {
    val result = checkCoord(x, y)
    player = (x, y)
    DMaps.renderDmap(id)
    result
  }

  /**
   * Validates a coord can be stepped on
   * @param x the x coord to check
   * @param y the y coord to check
   * @return true if valid
   */
  def checkCoord(x: Int, y: Int): Boolean = {
    x < maxWidth && y < maxHeight && dmap(x)(y) == 0
  }
}



















