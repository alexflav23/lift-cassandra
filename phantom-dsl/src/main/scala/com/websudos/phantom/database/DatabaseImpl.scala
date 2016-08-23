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
package com.websudos.phantom.database

import com.datastax.driver.core.{ResultSet, Session}
import com.websudos.diesel.engine.reflection.EarlyInit
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.builder.query.{CQLQuery, ExecutableStatementList}
import com.websudos.phantom.connectors.{KeySpace, KeySpaceDef}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future, blocking}

private object Lock

abstract class DatabaseImpl(val connector: KeySpaceDef) extends EarlyInit[CassandraTable[_, _]] {

  implicit val space: KeySpace = KeySpace(connector.name)

  implicit lazy val session: Session = connector.session

  lazy val tables: Set[CassandraTable[_, _]] = initialize().toSet

  def shutdown(): Unit = {
    blocking {
      com.websudos.phantom.Manager.shutdown()
      session.getCluster.close()
      session.close()
    }
  }

  private[this] val defaultTimeout = 10.seconds

  /**
   * Returns a list of executable statements that will be parallelized with futures
   * to create the entire database schema in a single call.
   *
   * Every future in the statement list will contain the CQL schema generation query
   * for a single table. Processing order is not guaranteed however the tables
   * are generally processed in the order they are written in, even though
   * ordering is not a guarantee required at this level of an application.
   *
   * @return An executable statement list that can be used with Scala or Twitter futures to simultaneously
   *         execute an entire sequence of queries.
   */
  def autocreate(): ExecutableCreateStatementsList = {
    new ExecutableCreateStatementsList(tables)
  }

  /**
    * A blocking method that will create all the tables. This is designed to prevent the
    * requirement of the implicit session to escape the enclosure of the database object.
 *
    * @param timeout The timeout for the initialisation call.
    *                Defaults to [[com.websudos.phantom.database.DatabaseImpl#defaultTimeout]]
    * @return A sequence of result sets, where every result is the result of a single create operation.
    */
  def create(timeout: FiniteDuration = defaultTimeout)(implicit ex: ExecutionContextExecutor): Seq[ResultSet] = {
    Await.result(createAsync(), timeout)
  }

  /**
    * An asynchronous method that will create all the tables. This is designed to prevent the
    * requirement of the implicit session to escape the enclosure of the database object.
 *
    * @return A sequence of result sets, where every result is the result of a single create operation.
    */
  def createAsync()(implicit ex: ExecutionContextExecutor): Future[Seq[ResultSet]] = {
    autocreate().future()
  }

  /**
   * Returns a list of executable statements that will be parallelized with futures
   * to drop the entire database schema in a single call.
   *
   * Every future in the statement list will contain the ALTER DROP drop query
   * for a single table. Processing order is not guaranteed however the tables
   * are generally processed in the order they are written in, even though
   * ordering is not a guarantee required at this level of an application.
   *
   * @return An executable statement list that can be used with Scala or Twitter futures to simultaneously
   *         execute an entire sequence of queries.
   */
  def autodrop(): ExecutableStatementList = {
    new ExecutableStatementList(tables.toSeq.map {
      table => table.alter().drop().qb
    })
  }

  /**
    * An async method that will drop all the tables. This is designed to prevent the
    * requirement of the implicit session to escape the enclosure of the database object.
 *
    * @return A sequence of result sets, where every result is the result of a single drop operation.
    */
  def dropAsync()(implicit ex: ExecutionContextExecutor): Future[Seq[ResultSet]] = {
    autodrop().future()
  }

  /**
    * A blocking method that will drop all the tables. This is designed to prevent the
    * requirement of the implicit session to escape the enclosure of the database object.
 *
    * @param timeout The timeout for the initialisation call.
    *                Defaults to [[com.websudos.phantom.database.DatabaseImpl#defaultTimeout]]
    * @return A sequence of result sets, where every result is the result of a single drop operation.
    */
  def drop(timeout: FiniteDuration = defaultTimeout)(implicit ex: ExecutionContextExecutor): Seq[ResultSet] = {
    Await.result(dropAsync(), timeout)
  }

  /**
   * Returns a list of executable statements that will be parallelized with futures
   * to truncate the entire database schema in a single call.
   *
   * Every future in the statement list will contain the CQL truncation query
   * for a single table. Processing order is not guaranteed however the tables
   * are generally processed in the order they are written in, even though
   * ordering is not a guarantee required at this level of an application.
   *
   * @return An executable statement list that can be used with Scala or Twitter futures to simultaneously
   *         execute an entire sequence of queries.
   */
  def autotruncate(): ExecutableStatementList = {
    new ExecutableStatementList(tables.toSeq.map {
      table => table.truncate().qb
    })
  }

  /**
    * A blocking method that will truncate all the tables. This is designed to prevent the
    * requirement of the implicit session to escape the enclosure of the database object.
 *
    * @param timeout The timeout for the initialisation call.
    *                Defaults to [[com.websudos.phantom.database.DatabaseImpl#defaultTimeout]]
    * @return A sequence of result sets, where every result is the result of a single truncate operation.
    */
  def truncate(timeout: FiniteDuration = defaultTimeout)(implicit ex: ExecutionContextExecutor): Seq[ResultSet] = {
    Await.result(truncateAsync(), timeout)
  }

  /**
    * An async method that will truncate all the tables. This is designed to prevent the
    * requirement of the implicit session to escape the enclosure of the database object.
 *
    * @return A sequence of result sets, where every result is the result of a single truncate operation.
    */
  def truncateAsync()(implicit ex: ExecutionContextExecutor): Future[Seq[ResultSet]] = {
    autotruncate().future()
  }
}

sealed class ExecutableCreateStatementsList(val tables: Set[CassandraTable[_, _]]) {

  private[phantom] def queries()(implicit keySpace: KeySpace): Seq[CQLQuery] = {
    tables.toSeq.map(_.autocreate(keySpace).qb)
  }

  def future()(
    implicit session: Session,
    keySpace: KeySpace,
    ec: ExecutionContextExecutor
  ): Future[Seq[ResultSet]] = {
    Future.sequence(tables.toSeq.map(_.autocreate(keySpace).future()))
  }
}
