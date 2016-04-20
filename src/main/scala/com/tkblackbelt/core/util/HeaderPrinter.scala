package com.tkblackbelt.core.util

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

import Rainbow._

trait HeaderPrinter {

  println(
    """
      |
      |
    """.stripMargin)
  println(
    """
       _____           _         _____ _____
      /  ___|         | |       /  __ \  _  |
      \ `--.  ___ __ _| | __ _  | /  \/ | | |
       `--. \/ __/ _` | |/ _` | | |   | | | |
      /\__/ / (_| (_| | | (_| | | \__/\ \_/ /
      \____/ \___\__,_|_|\__,_|  \____/\___/

    """.blue)
  println("Developed by: Charles Benger\n".white)

  println(
    """
      |
      |
    """.stripMargin)

}
