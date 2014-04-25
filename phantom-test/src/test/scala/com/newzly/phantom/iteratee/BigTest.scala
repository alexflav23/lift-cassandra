package com.newzly.phantom.iteratee

import scala.concurrent.{ blocking, ExecutionContext }
import org.scalatest.{ Assertions, BeforeAndAfterAll, FlatSpec, Matchers  }
import org.scalatest.concurrent.{ AsyncAssertions, ScalaFutures }
import com.datastax.driver.core.Session
import com.newzly.phantom.Manager
import com.twitter.util.Duration

trait BigTest extends FlatSpec with ScalaFutures with BeforeAndAfterAll with Matchers with Assertions with AsyncAssertions {
  val keySpace: String
  val cluster = BigTestHelper.cluster
  implicit lazy val session: Session = cluster.connect()
  implicit lazy val context: ExecutionContext = Manager.scalaExecutor

  private[this] def createKeySpace(spaceName: String) = {
    session.execute(s"CREATE KEYSPACE $spaceName WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};")
    session.execute(s"use $spaceName;")
    cluster.getConfiguration.getSocketOptions.setReadTimeoutMillis(Duration.fromSeconds(3).inSeconds)
  }

  override def beforeAll() {
    session.execute(s"DROP KEYSPACE $keySpace;")
    createKeySpace(keySpace)
  }

  override def afterAll() {
    blocking {
      session.execute(s"DROP KEYSPACE $keySpace;")
    }
  }

}
