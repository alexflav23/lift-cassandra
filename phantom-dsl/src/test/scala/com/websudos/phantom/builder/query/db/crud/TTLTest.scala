/*
 * Copyright 2013-2016 Websudos, Limited.
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
package com.websudos.phantom.builder.query.db.crud

import com.websudos.phantom.PhantomSuite
import com.websudos.phantom.builder.query.prepared._
import com.websudos.phantom.dsl._
import com.websudos.phantom.tables.{Primitive, TestDatabase}
import com.outworkers.util.testing._
import org.scalatest.{Outcome, Retries}
import org.scalatest.concurrent.Eventually
import org.scalatest.tagobjects.Retryable

import scala.concurrent.duration._

class TTLTest extends PhantomSuite with Eventually with Retries {

  override def beforeAll(): Unit = {
    super.beforeAll()
    TestDatabase.primitives.insertSchema()
  }

  override def withFixture(test: NoArgTest): Outcome = {
    if (isRetryable(test)) {
      withRetry(super.withFixture(test))
    } else {
      super.withFixture(test)
    }
  }

  private[this] val ttl = 2 seconds
  private[this] val granularity = 5 seconds

  it should "expire inserted records after TTL" taggedAs Retryable in {
    val row = gen[Primitive]

    val chain = for {
      store <- TestDatabase.primitives.store(row).ttl(ttl).future()
      get <- TestDatabase.primitives.select.where(_.pkey eqs row.pkey).one()
    } yield get

    chain.successful { record =>
      record shouldEqual Some(row)
    }

    eventually(timeout(ttl + granularity)) {
      val futureRecord = TestDatabase.primitives.select.where(_.pkey eqs row.pkey).one()
      futureRecord.successful { record =>
        record shouldBe empty
      }
    }
  }

  it should "expire inserted records after TTL with prepared statement" taggedAs Retryable in {
    val row = gen[Primitive]

    val fetchQuery = TestDatabase.primitives.select
      .where(_.pkey eqs ?)
      .prepare()

    val insertQuery = TestDatabase.primitives.insert
      .p_value(_.pkey, ?)
      .p_value(_.long, ?)
      .p_value(_.boolean, ?)
      .p_value(_.bDecimal, ?)
      .p_value(_.double, ?)
      .p_value(_.float, ?)
      .p_value(_.inet, ?)
      .p_value(_.int, ?)
      .p_value(_.date, ?)
      .p_value(_.uuid, ?)
      .p_value(_.bi, ?)
      .ttl(ttl)
      .prepare()

    def preparedInsert(row: Primitive): ExecutablePreparedQuery = {
      insertQuery.bind(
        row.pkey,
        row.long,
        row.boolean,
        row.bDecimal,
        row.double,
        row.float,
        row.inet,
        row.int,
        row.date,
        row.uuid,
        row.bi)
    }

    val chain = for {
      store <- preparedInsert(row).future()
      get <- fetchQuery.bind(row.pkey).one()
    } yield get

    chain.successful { result =>
      result shouldEqual Some(row)
    }

    eventually(timeout(ttl + granularity)) {
      val futureResults = fetchQuery.bind(row.pkey).one()
      futureResults.successful { results =>
        results.isEmpty shouldBe true
      }
    }
  }
}
