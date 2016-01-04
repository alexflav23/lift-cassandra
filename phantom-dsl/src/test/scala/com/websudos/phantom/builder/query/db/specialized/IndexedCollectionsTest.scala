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
 * - Explicit consent must be obtained from the copyright owner, Websudos Limited before any redistribution is made.
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
package com.websudos.phantom.builder.query.db.specialized

import com.datastax.driver.core.exceptions.SyntaxError
import com.websudos.phantom.PhantomSuite
import com.websudos.phantom.dsl._
import com.websudos.phantom.tables.{TestDatabase, TestRow}
import com.websudos.util.testing._

import scala.concurrent.Await
import scala.concurrent.duration._

class IndexedCollectionsTest extends PhantomSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Await.ready(TestDatabase.indexedCollectionsTable.create.ifNotExists().future(), 4.seconds)
  }

  it should "store a record and retrieve it with a CONTAINS query on the SET" in {
    val record = gen[TestRow]

    val chain = for {
      store <- TestDatabase.indexedCollectionsTable.store(record).future()
      get <- TestDatabase.indexedCollectionsTable.select.where(_.setText contains record.setText.head).fetch()
    } yield get

    if (cassandraVersion > Version.`2.1.0`) {
      chain.successful {
        res => {
          res.nonEmpty shouldEqual true
          res should contain (record)
        }
      }
    } else {
      chain.failing[SyntaxError]
    }

  }

  it should "store a record and retrieve it with a CONTAINS query on the SET with TWitter Futures" in {
    val record = gen[TestRow]

    val chain = for {
      store <- TestDatabase.indexedCollectionsTable.store(record).execute()
      get <- TestDatabase.indexedCollectionsTable.select.where(_.setText contains record.setText.head).collect()
    } yield get

    if (cassandraVersion > Version.`2.1.0`) {
      chain.successful {
        res => {
          res.nonEmpty shouldEqual true

          res should contain (record)
        }
      }
    } else {
      chain.failing[SyntaxError]
    }
  }

  it should "store a record and retrieve it with a CONTAINS query on the MAP" in {
    val record = gen[TestRow]

    val chain = for {
      store <- TestDatabase.indexedCollectionsTable.store(record).future()
      get <- TestDatabase.indexedCollectionsTable.select.where(_.mapTextToText contains record.mapTextToText.head._2).fetch()
    } yield get

    if (cassandraVersion > Version.`2.1.0`) {
      chain.successful {
        res => {
          res.nonEmpty shouldEqual true
          res should contain (record)
        }
      }
    } else {
      chain.failing[SyntaxError]
    }
  }

  it should "store a record and retrieve it with a CONTAINS query on the MAP with Twitter Futures" in {
    val record = gen[TestRow]

    val chain = for {
      store <- TestDatabase.indexedCollectionsTable.store(record).execute()
      get <- TestDatabase.indexedCollectionsTable.select.where(_.mapTextToText contains record.mapTextToText.head._2).collect()
    } yield get

    if (cassandraVersion > Version.`2.1.0`) {
      chain.successful {
        res => {
          res.nonEmpty shouldEqual true
          res should contain (record)
        }
      }
    } else {
      chain.failing[SyntaxError]
    }
  }

  it should "store a record and retrieve it with a CONTAINS KEY query on the MAP" in {
    val record = gen[TestRow]

    val chain = for {
      store <- TestDatabase.indexedCollectionsTable.store(record).future()
      get <- TestDatabase.indexedCollectionsTable.select.where(_.mapIntToText containsKey record.mapIntToText.head._1).fetch()
    } yield get

    if (cassandraVersion > Version.`2.1.0`) {
      chain.successful {
        res => {
          res.nonEmpty shouldEqual true
          res should contain (record)
        }
      }
    } else {
      chain.failing[SyntaxError]
    }
  }

  it should "store a record and retrieve it with a CONTAINS KEY query on the MAP with Twitter Futures" in {
    val record = gen[TestRow]

    val chain = for {
      store <- TestDatabase.indexedCollectionsTable.store(record).execute()
      get <- TestDatabase.indexedCollectionsTable.select.where(_.mapIntToText containsKey record.mapIntToText.head._1).collect()
    } yield get

    if (cassandraVersion > Version.`2.1.0`) {
      chain.successful {
        res => {
          res.nonEmpty shouldEqual true
          res should contain (record)
        }
      }
    } else {
      chain.failing[SyntaxError]
    }
  }

}
