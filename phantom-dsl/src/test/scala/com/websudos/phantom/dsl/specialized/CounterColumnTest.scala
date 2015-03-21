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
package com.websudos.phantom.dsl.specialized

import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.SpanSugar._

import com.websudos.phantom.dsl._
import com.websudos.phantom.testkit._
import com.websudos.phantom.tables._
import com.websudos.util.testing._

class CounterColumnTest extends PhantomCassandraTestSuite {

  implicit val s: PatienceConfiguration.Timeout = timeout(10 seconds)

  override def beforeAll(): Unit = {
    super.beforeAll()
    CounterTableTest.insertSchema()
  }


  it should "increment counter values by 1" in {
    val sample = gen[CounterRecord]

    val chain = for {
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment 0L).future()
      select <- CounterTableTest.select.where(_.id eqs sample.id).one
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment()).future()
      select2 <- CounterTableTest.select.where(_.id eqs sample.id).one
    } yield (select, select2)


    chain.successful {
      result => {
        result._1.isEmpty shouldEqual false
        result._1.get.count shouldEqual 0
        result._2.isEmpty shouldEqual false
        result._2.get.count shouldEqual 1
      }
    }
  }

  it should "increment counter values by 1 with Twitter Futures" in {
    val sample = gen[CounterRecord]

    val chain = for {
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment 0L).execute()
      select <- CounterTableTest.select.where(_.id eqs sample.id).get
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment()).execute()
      select2 <- CounterTableTest.select.where(_.id eqs sample.id).get
    } yield (select, select2)


    chain.successful {
      result => {
        result._1.isEmpty shouldEqual false
        result._1.get.count shouldEqual 0
        result._2.isEmpty shouldEqual false
        result._2.get.count shouldEqual 1
      }
    }
  }


  it should "allow selecting a counter" in {
    val sample = gen[CounterRecord]

    val chain = for {
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment 500).future()
      select <- CounterTableTest.select.where(_.id eqs sample.id).one
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment()).future()
      select2 <- CounterTableTest.select(_.count_entries).where(_.id eqs sample.id).one
    } yield (select, select2)


    chain.successful {
      result => {
        result._1.isEmpty shouldEqual false
        result._1.get.count shouldEqual 500
        result._2.isEmpty shouldEqual false
        result._2.get shouldEqual 501
      }
    }
  }

  it should "allow selecting a counter with Twitter Futures" in {
    val sample = gen[CounterRecord]

    val chain = for {
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment 500).execute()
      select <- CounterTableTest.select.where(_.id eqs sample.id).get
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment()).execute()
      select2 <- CounterTableTest.select(_.count_entries).where(_.id eqs sample.id).get
    } yield (select, select2)


    chain.successful {
      result => {
        result._1.isEmpty shouldEqual false
        result._1.get.count shouldEqual 500
        result._2.isEmpty shouldEqual false
        result._2.get shouldEqual 501
      }
    }
  }

  it should "increment counter values by a given value" in {
    val sample = gen[CounterRecord]
    val diff = 200L

    val chain = for {
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment 0L).future()
      select <- CounterTableTest.select.where(_.id eqs sample.id).one
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment diff).future()
      select2 <- CounterTableTest.select.where(_.id eqs sample.id).one
    } yield (select, select2)


    chain.successful {
      result => {
        result._1.isEmpty shouldEqual false
        result._1.get.count shouldEqual 0L
        result._2.isEmpty shouldEqual false
        result._2.get.count shouldEqual diff
      }
    }
  }

  it should "increment counter values by a given value with Twitter Futures" in {
    val sample = gen[CounterRecord]
    val diff = 200L

    val chain = for {
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment 0L).execute()
      select <- CounterTableTest.select.where(_.id eqs sample.id).get
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment diff).execute()
      select2 <- CounterTableTest.select.where(_.id eqs sample.id).get
    } yield (select, select2)


    chain.successful {
      result => {
        result._1.isEmpty shouldEqual false
        result._1.get.count shouldEqual 0L
        result._2.isEmpty shouldEqual false
        result._2.get.count shouldEqual diff
      }
    }
  }

  it should "decrement counter values by 1" in {
    val sample = gen[CounterRecord]

    val chain = for {
      incr1 <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment 1L).future()
      select <- CounterTableTest.select.where(_.id eqs sample.id).one()
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries decrement()).future()
      select2 <- CounterTableTest.select.where(_.id eqs sample.id).one()
    } yield (select, select2)


    chain.successful {
      result => {
        result._1.isEmpty shouldEqual false
        result._1.get.count shouldEqual 1L
        result._2.isEmpty shouldEqual false
        result._2.get.count shouldEqual 0L
      }
    }
  }

  it should "decrement counter values by 1 with Twitter Futures" in {
    val sample = gen[CounterRecord]

    val chain = for {
      incr1 <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment 1L).execute()
      select <- CounterTableTest.select.where(_.id eqs sample.id).get()
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries decrement()).execute()
      select2 <- CounterTableTest.select.where(_.id eqs sample.id).get()
    } yield (select, select2)


    chain.successful {
      result => {
        result._1.isEmpty shouldEqual false
        result._1.get.count shouldEqual 1L
        result._2.isEmpty shouldEqual false
        result._2.get.count shouldEqual 0L
      }
    }
  }

  it should "decrement counter values by a given value" in {
    val sample = gen[CounterRecord]
    val diff = 200L
    val initial = 500L

    val chain = for {
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment initial).future()
      select <- CounterTableTest.select.where(_.id eqs sample.id).one
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries decrement diff).future()
      select2 <- CounterTableTest.select.where(_.id eqs sample.id).one
    } yield (select, select2)


    chain.successful {
      result => {
        result._1.isEmpty shouldEqual false
        result._2.isEmpty shouldEqual false
        result._2.get.count shouldEqual (initial - diff)
      }
    }
  }

  it should "decrement counter values by a given value with Twitter Futures" in {
    val sample = gen[CounterRecord]
    val diff = 200L
    val initial = 500L

    val chain = for {
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries increment initial).execute()
      select <- CounterTableTest.select.where(_.id eqs sample.id).get
      incr <-  CounterTableTest.update.where(_.id eqs sample.id).modify(_.count_entries decrement diff).execute()
      select2 <- CounterTableTest.select.where(_.id eqs sample.id).get
    } yield (select, select2)


    chain.successful {
      result => {
        result._1.isEmpty shouldEqual false
        result._2.isEmpty shouldEqual false
        result._2.get.count shouldEqual (initial - diff)
      }
    }
  }
}
