/*
 * Copyright 2013-2015 Websudos, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Explicit consent must be obtained from the copyright owner, Outworkers Limited before any redistribution is made.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.websudos.phantom.reactivestreams.suites.iteratee

import com.outworkers.util.testing._
import com.websudos.phantom.dsl._
import com.websudos.phantom.tables.{TestDatabase, TimeUUIDRecord}
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

class IteratorTest extends BigTest with ScalaFutures {

  override def beforeAll(): Unit = {
    super.beforeAll()
    database.timeuuidTable.insertSchema()
  }

  it should "correctly retrieve the right number of records using scala iterator" in {
    val generationSize = 100
    val user = gen[UUID]
    val rows = genList[TimeUUIDRecord](generationSize).map(_.copy(user = user))

    val chain = for {
      store <- Future.sequence(rows.map(row => database.timeuuidTable.store(row).future()))
      iterator <- TestDatabase.timeuuidTable.select.where(_.user eqs user).iterator()
    } yield iterator

    whenReady(chain) {
      res => {
        res.size shouldEqual generationSize
        res.foreach(x => rows should contain (x))
      }
    }
  }
}
