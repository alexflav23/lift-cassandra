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
package com.websudos.phantom.dsl.ordering

import com.websudos.phantom.Implicits._
import com.websudos.phantom.iteratee.Iteratee
import com.websudos.phantom.tables.{TimeSeriesRecord, TimeSeriesTable}
import com.newzly.util.testing.AsyncAssertionsHelper._
import com.newzly.util.testing.cassandra.BaseTest
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.PatienceConfiguration
import scala.collection.JavaConverters._
import scala.concurrent.blocking
import scala.concurrent.duration._

class TimeSeriesTest extends BaseTest {
  val keySpace = "clustering_order_tests"

  implicit val s: PatienceConfiguration.Timeout = timeout(10 seconds)

  override def beforeAll(): Unit = {
    blocking {
      super.beforeAll()
      TimeSeriesTable.insertSchema()
    }
  }

  it should "allow using naturally fetch the records in descending order for a descending clustering order" in {
    val recordList = List.range(0, 5).map {
      res => {
        Thread.sleep(50L)
        TimeSeriesRecord.sample
      }
    }

    val batch = recordList.foldLeft(BatchStatement()) {
      (b, record) => {
        b.add(TimeSeriesTable.insert
          .value(_.id, record.id)
          .value(_.name, record.name)
          .value(_.timestamp, record.timestamp))
      }
    }

    val chain = for {
      truncate <- TimeSeriesTable.truncate.future()
      insert <- batch.future()
      chunks <- TimeSeriesTable.select.limit(5).fetch()
    } yield chunks

    chain.successful {
      res =>
        val ts = recordList.map(_.timestamp.getSecondOfDay)
        val mapped = res.map(_.timestamp.getSecondOfDay)
        mapped.toList shouldEqual ts.reverse
    }
  }

  it should "allow using naturally fetch the records in descending order for a descending clustering order with Twitter Futures" in {
    val recordList = List.range(0, 5).map {
      res => {
        Thread.sleep(50L)
        TimeSeriesRecord.sample
      }
    }

    val batch = recordList.foldLeft(BatchStatement()) {
      (b, record) => {
        b.add(TimeSeriesTable.insert
          .value(_.id, record.id)
          .value(_.name, record.name)
          .value(_.timestamp, record.timestamp))
      }
    }

    val chain = for {
      truncate <- TimeSeriesTable.truncate.execute()
      insert <- batch.execute()
      chunks <- TimeSeriesTable.select.limit(5).collect()
    } yield chunks

    chain.successful {
      res =>
        val ts = recordList.map(_.timestamp.getSecondOfDay)
        val mapped = res.map(_.timestamp.getSecondOfDay)
        mapped.toList shouldEqual ts.reverse
    }
  }

  it should "allow fetching the records in ascending order for a descending clustering order using order by clause" in {
    val recordList = List.range(0, 5).map {
      res => {
        Thread.sleep(50L)
        TimeSeriesRecord.sample
      }
    }

    val batch = recordList.foldLeft(BatchStatement()) {
      (b, record) => {
        b.add(TimeSeriesTable.insert
          .value(_.id, record.id)
          .value(_.name, record.name)
          .value(_.timestamp, record.timestamp))
      }
    }
    val chain = for {
      truncate <- TimeSeriesTable.truncate.future()
      insert <- batch.future()
      chunks <- TimeSeriesTable.select.limit(5).where(_.id eqs TimeSeriesRecord.testUUID).orderBy(_.timestamp.asc).fetch()
    } yield chunks

    chain.successful {
      res =>
        val ts = recordList.map(_.timestamp.getSecondOfDay)
        res.map(_.timestamp.getSecondOfDay).toList shouldEqual ts
    }
  }

  it should "allow fetching the records in ascending order for a descending clustering order using order by clause with Twitter Futures" in {
    val recordList = List.range(0, 5).map {
      res => {
        Thread.sleep(50L)
        TimeSeriesRecord.sample
      }
    }

    val batch = recordList.foldLeft(BatchStatement()) {
      (b, record) => {
        b.add(TimeSeriesTable.insert
          .value(_.id, record.id)
          .value(_.name, record.name)
          .value(_.timestamp, record.timestamp))
      }
    }
    val chain = for {
      truncate <- TimeSeriesTable.truncate.execute()
      insert <- batch.execute()
      chunks <- TimeSeriesTable.select.limit(5).where(_.id eqs TimeSeriesRecord.testUUID).orderBy(_.timestamp.asc).collect()
    } yield chunks

    chain.successful {
      res =>
        val ts = recordList.map(_.timestamp.getSecondOfDay)
        res.map(_.timestamp.getSecondOfDay).toList shouldEqual ts
    }
  }

  it should "allow fetching the records in descending order for a descending clustering order using order by clause" in {
    val recordList = List.range(0, 5).map {
      res =>
        Thread.sleep(50L)
        TimeSeriesRecord.sample
    }
    val batch = recordList.foldLeft(BatchStatement()) {
      (b, record) =>
        b.add(TimeSeriesTable.insert
          .value(_.id, record.id)
          .value(_.name, record.name)
          .value(_.timestamp, record.timestamp))
    }
    val chain = for {
      truncate <- TimeSeriesTable.truncate.future()
      insert <- batch.future()
      chunks <- TimeSeriesTable.select.limit(5).where(_.id eqs TimeSeriesRecord.testUUID).orderBy(_.timestamp.desc).fetch()
    } yield chunks

    chain.successful {
      res =>
        val ts = recordList.map(_.timestamp.getSecondOfDay)
        res.map(_.timestamp.getSecondOfDay).toList shouldEqual ts.reverse
    }
  }

  it should "allow fetching the records in descending order for a descending clustering order using order by clause with Twitter Futures" in {
    val recordList = List.range(0, 5).map {
      res =>
        Thread.sleep(50L)
        TimeSeriesRecord.sample
    }
    val batch = recordList.foldLeft(BatchStatement()) {
      (b, record) =>
        b.add(TimeSeriesTable.insert
          .value(_.id, record.id)
          .value(_.name, record.name)
          .value(_.timestamp, record.timestamp))
    }
    val chain = for {
      truncate <- TimeSeriesTable.truncate.execute()
      insert <- batch.execute()
      chunks <- TimeSeriesTable.select.limit(5).where(_.id eqs TimeSeriesRecord.testUUID).orderBy(_.timestamp.desc).collect()
    } yield chunks

    chain.successful {
      res =>
        val ts = recordList.map(_.timestamp.getSecondOfDay)
        res.map(_.timestamp.getSecondOfDay).toList shouldEqual ts.reverse
    }
  }

}
