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
package com.websudos.phantom.thrift.suites

import com.websudos.phantom.dsl._
import com.websudos.phantom.tables.{Output, ThriftDatabase}
import com.websudos.util.testing._
import org.scalatest.FlatSpec
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.SpanSugar._

class ThriftListOperations extends FlatSpec with ThriftTestSuite {

  override def beforeAll(): Unit = {
    ThriftDatabase.thriftColumnTable.create.ifNotExists().future().block(5.seconds)
  }

  it should "prepend an item to a thrift list column" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]
    val sample2 = gen[ThriftTest]

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample))
      .future()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList prepend sample2).future()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).one
    } yield {
      select
    }

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual List(sample2, sample)
      }
    }
  }

  it should "prepend an item to a thrift list column with Twitter Futures" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample))
      .execute()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList prepend sample2).execute()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).get
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual List(sample2, sample)
      }
    }
  }

  it should "prepend several items to a thrift list column" in {
    val sample = gen[Output]

    val appendable = genList[ThriftTest]()

    val prependedValues = if (cassandraVersion < Version.`2.0.13`) appendable.reverse else appendable

    val operation = for {
      insertDone <- ThriftDatabase.thriftColumnTable.store(sample).future()
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs sample.id).modify(_.thriftList prepend appendable).future()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs sample.id).one
    } yield {
      select
    }

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual prependedValues ::: sample.thriftList
      }
    }
  }

  it should "prepend several items to a thrift list column with Twitter Futures" in {
    val sample = gen[Output]

    val appendable = genList[ThriftTest]()

    val prependedValues = if (cassandraVersion < Version.`2.0.13`) appendable.reverse else appendable

    val operation = for {
      insertDone <- ThriftDatabase.thriftColumnTable.store(sample).execute()
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs sample.id).modify(_.thriftList prepend appendable).execute()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs sample.id).get
    } yield {
        select
      }

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual prependedValues ::: sample.thriftList
      }
    }
  }

  it should "append an item to a thrift list column" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]
    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample))
      .future()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList append sample2).future()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).one
    } yield {
      select
    }

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual List(sample, sample2)
      }
    }
  }

  it should "append an item to a thrift list column with Twitter Futures" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample))
      .execute()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList append sample2).execute()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).get
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual List(sample, sample2)
      }
    }
  }

  it should "append several items to a thrift list column" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val sample3 = gen[ThriftTest]

    val toAppend = List(sample2, sample3)

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample))
      .future()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList append toAppend).future()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).one
    } yield {
      select
    }

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual List(sample, sample2, sample3)
      }
    }
  }

  it should "append several items to a thrift list column with Twitter Futures" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val sample3 = gen[ThriftTest]

    val toAppend = List(sample2, sample3)

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample))
      .execute()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList append toAppend).execute()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).get
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual List(sample, sample2, sample3)
      }
    }
  }

  it should "remove an item from a thrift list column" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample, sample2))
      .future()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList discard sample2).future()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).one
    } yield {
      select
    }

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual List(sample)
      }
    }
  }

  it should "remove an item from a thrift list column with Twitter Futures" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample, sample2))
      .execute()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList discard sample2).execute()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).get
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual List(sample)
      }
    }
  }

  it should "remove several items from a thrift list column" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val sample3 = gen[ThriftTest]

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample, sample2, sample3))
      .future()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList discard List(sample2, sample3)).future()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).one
    } yield {
      select
    }

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual List(sample)
      }
    }
  }

  it should "remove several items from a thrift list column with Twitter Futures" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val sample3 = gen[ThriftTest]

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample, sample2, sample3))
      .execute()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList discard List(sample2, sample3)).execute()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).get
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get shouldEqual List(sample)
      }
    }
  }

  it should "set an index to a given value" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val sample3 = gen[ThriftTest]

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample, sample2, sample3))
      .future()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList setIdx(0, sample3)).future()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).one
    } yield {
      select
    }

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get(0) shouldEqual sample3
      }
    }
  }

  it should "set an index to a given value with Twitter Futures" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val sample3 = gen[ThriftTest]

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample, sample2, sample3))
      .execute()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList setIdx(0, sample3)).execute()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).get
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get(0) shouldEqual sample3
      }
    }
  }

  it should "set a non-zero index to a given value" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val sample3 = gen[ThriftTest]

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample, sample2, sample3))
      .future()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList setIdx(2, sample3)).future()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).one
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get(2) shouldEqual sample3
      }
    }
  }

  it should "set a non-zero index to a given value with Twitter Futures" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val sample3 = gen[ThriftTest]

    val insert = ThriftDatabase.thriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample, sample2, sample3))
      .execute()

    val operation = for {
      insertDone <- insert
      update <- ThriftDatabase.thriftColumnTable.update.where(_.id eqs id).modify(_.thriftList setIdx(2, sample3)).execute()
      select <- ThriftDatabase.thriftColumnTable.select(_.thriftList).where(_.id eqs id).get
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldEqual true
        items.get(2) shouldEqual sample3
      }
    }
  }
}
