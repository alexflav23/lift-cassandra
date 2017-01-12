/*
 * Copyright 2013 - 2017 Outworkers Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.outworkers.phantom.streams.suites.iteratee

import java.util.concurrent.atomic.AtomicLong

import com.outworkers.phantom.dsl._
import com.outworkers.phantom.streams._
import com.outworkers.phantom.tables.{JodaRow, TestDatabase}
import com.outworkers.util.testing._
import org.scalatest.Matchers
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.SpanSugar._

import scala.concurrent.{Await, Future}

class IterateeInsertPerformanceTest extends BigTest with Matchers {

  implicit val s: PatienceConfiguration.Timeout = timeout(12 minutes)
  private[this] final val iteratorLimit = 10000

  it should "retrieve the right amount of results" in {
    TestDatabase.primitivesJoda.insertSchema()
    val fs = for {
      step <- 1 to 100
      rows = Iterator.fill(iteratorLimit)(gen[JodaRow])

      batch = rows.foldLeft(Batch.unlogged)((b, row) => {
        val statement = TestDatabase.primitivesJoda.insert
          .value(_.pkey, row.pkey)
          .value(_.intColumn, row.intColumn)
          .value(_.timestamp, row.timestamp)
        b.add(statement)
      })
      w = batch.future()
      f = w map (_ => info(s"step $step has succeed"))
      r = Await.result(f, 200 seconds)
    } yield f map (_ => r)


    val combinedFuture = Future.sequence(fs) map {
      r => TestDatabase.primitivesJoda.select.count.one()
    }

    val counter: AtomicLong = new AtomicLong(0)
    val result = combinedFuture flatMap {
       rs => {
         info(s"done, inserted: $rs rows - start parsing")
         TestDatabase.primitivesJoda.select.fetchEnumerator run Iteratee.forEach { r => counter.incrementAndGet() }
       }
    }

    (result flatMap (_ => combinedFuture)) successful {
      r => {
        info(s"done, reading: ${counter.addAndGet(0)}")
        counter.get() shouldEqual r
      }
    }
  }
}
