
package com.websudos.phantom.dsl.specialized

import scala.concurrent.blocking
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.SpanSugar._
import com.datastax.driver.core.utils.UUIDs
import com.websudos.phantom.Implicits._
import com.websudos.phantom.tables.StaticTableTest
import com.newzly.util.testing.AsyncAssertionsHelper._
import com.newzly.util.testing.cassandra.BaseTest

class StaticColumnTest extends BaseTest {
  val keySpace = "static_columns_test"

  implicit val s: PatienceConfiguration.Timeout = timeout(10 seconds)

  override def beforeAll(): Unit = {
    blocking {
      super.beforeAll()
      StaticTableTest.insertSchema()
    }
  }

  it should "use a static value for a static column" in {

    //char is not supported
    //https://github.com/datastax/java-driver/blob/2.0/driver-core/src/main/java/com/datastax/driver/core/DataType.java

    val id = UUIDs.timeBased()
    val static = "this_is_static"
    val id2 = UUIDs.timeBased()
    val chain = for {
      // The first record holds the static value.
      insert <- StaticTableTest.insert.value(_.id, id).value(_.clusteringId, id).value(_.staticTest, static).execute()
      insert2 <- StaticTableTest.insert.value(_.id, id).value(_.clusteringId, id2).execute()
      select <- StaticTableTest.select.where(_.id eqs id).and(_.clusteringId eqs id2).get()
    } yield select

    chain.successful {
      res => {
        res.isDefined shouldEqual true
        res.get._3 shouldEqual static
      }
    }
  }

  it should "update values in all rows" in {
    //char is not supported
    //https://github.com/datastax/java-driver/blob/2.0/driver-core/src/main/java/com/datastax/driver/core/DataType.java
    val id = UUIDs.timeBased()
    val static = "this_is_static"
    val static2 = "this_is_updated_static"
    val id2 = UUIDs.timeBased()
    val chain = for {

      // The first insert holds the first static value.
      insert <- StaticTableTest.insert.value(_.id, id).value(_.clusteringId, id).value(_.staticTest, static).execute()

      // The second insert updates the static value
      insert2 <- StaticTableTest.insert.value(_.id, id).value(_.clusteringId, id2).value(_.staticTest, static2).execute()

      // We query for the first record inserted.
      select <- StaticTableTest.select.where(_.id eqs id).and(_.clusteringId eqs id).get()
    } yield select

    chain.successful {
      res => {
        res.isDefined shouldEqual true
        // The first record should hold the updated value.
        res.get._3 shouldEqual static2
      }
    }
  }

}
