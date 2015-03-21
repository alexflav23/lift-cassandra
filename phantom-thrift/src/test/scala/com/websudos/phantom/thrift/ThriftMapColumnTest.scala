/*
 * Copyright 2013 websudos ltd.
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
package com.websudos.phantom.thrift

import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.SpanSugar._

import com.websudos.phantom.dsl._
import com.websudos.phantom.tables.ThriftColumnTable
import com.websudos.phantom.testkit._
import com.websudos.util.testing._


class ThriftMapColumnTest extends PhantomCassandraTestSuite {

  implicit val s: PatienceConfiguration.Timeout = timeout(10 seconds)

  override def beforeAll(): Unit = {
    super.beforeAll()
    ThriftColumnTable.insertSchema
  }

  it should "put an item to a thrift map column" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val map = Map(gen[String] -> sample)
    val toAdd = gen[String] -> sample2
    val expected = map + toAdd


    val insert = ThriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample))
      .value(_.thriftMap, map)

      .future()


    val operation = for {
      insertDone <- insert
      update <- ThriftColumnTable.update.where(_.id eqs id).modify(_.thriftMap put toAdd).future()
      select <- ThriftColumnTable.select(_.thriftMap).where(_.id eqs id).one
    } yield {
      select
    }

    operation.successful {
      items => {
        items.isDefined shouldBe true
        items.get shouldBe expected
      }
    }
  }

  it should "put an item to a thrift map column with Twitter Futures" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val map = Map(gen[String] -> sample)
    val toAdd = gen[String] -> sample2
    val expected = map + toAdd


    val insert = ThriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample))
      .value(_.thriftMap, map)
      .execute()


    val operation = for {
      insertDone <- insert
      update <- ThriftColumnTable.update.where(_.id eqs id).modify(_.thriftMap put toAdd).execute()
      select <- ThriftColumnTable.select(_.thriftMap).where(_.id eqs id).get
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldBe true
        items.get shouldBe expected
      }
    }
  }


  it should "put several items to a thrift map column" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]

    val sample3 = gen[ThriftTest]

    val map = Map(gen[String] -> sample)
    val toAdd = Map(gen[String] -> sample2, gen[String] -> sample3)
    val expected = map ++ toAdd


    val insert = ThriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample))
      .value(_.thriftMap, map)

      .future()

    val operation = for {
      insertDone <- insert
      update <- ThriftColumnTable.update.where(_.id eqs id).modify(_.thriftMap putAll toAdd).future()
      select <- ThriftColumnTable.select(_.thriftMap).where(_.id eqs id).one
    } yield {
      select
    }

    operation.successful {
      items => {
        items.isDefined shouldBe true
        items.get shouldBe expected
      }
    }
  }

  it should "put several items to a thrift map column with Twitter Futures" in {
    val id = gen[UUID]

    val sample = gen[ThriftTest]

    val sample2 = gen[ThriftTest]
    val sample3 = gen[ThriftTest]

    val map = Map(gen[String] -> sample)
    val toAdd = Map(gen[String] -> sample2, gen[String] -> sample3)
    val expected = map ++ toAdd


    val insert = ThriftColumnTable.insert
      .value(_.id, id)
      .value(_.name, sample.name)
      .value(_.ref, sample)
      .value(_.thriftSet, Set(sample))
      .value(_.thriftList, List(sample))
      .value(_.thriftMap, map)
      .execute()

    val operation = for {
      insertDone <- insert
      update <- ThriftColumnTable.update.where(_.id eqs id).modify(_.thriftMap putAll toAdd).execute()
      select <- ThriftColumnTable.select(_.thriftMap).where(_.id eqs id).get
    } yield select

    operation.successful {
      items => {
        items.isDefined shouldBe true
        items.get shouldBe expected
      }
    }
  }
}
