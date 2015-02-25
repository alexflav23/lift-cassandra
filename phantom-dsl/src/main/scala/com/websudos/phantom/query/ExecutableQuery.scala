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
package com.websudos.phantom.query

import scala.concurrent.{ ExecutionContext, Future => ScalaFuture }
import com.datastax.driver.core.{ Row, ResultSet, Session, Statement }
import com.websudos.phantom.{ CassandraResultSetOperations, CassandraTable }
import com.websudos.phantom.iteratee.{ Enumerator, Iteratee, ResultSpool }
import com.twitter.concurrent.Spool
import com.twitter.util.{ Future => TwitterFuture }
import play.api.libs.iteratee.{ Enumerator => PlayEnumerator, Enumeratee }
import com.datastax.driver.core.querybuilder.BuiltStatement

trait ExecutableStatement extends CassandraResultSetOperations {

  protected[phantom] val qb: BuiltStatement

  def future()(implicit session: Session): ScalaFuture[ResultSet] = {
    scalaStatementToFuture(qb)
  }

  def execute()(implicit  session: Session): TwitterFuture[ResultSet] = {
    twitterStatementToFuture(qb)
  }
}

/**
 *
 * @tparam T The class owning the table.
 * @tparam R The record type to store.
 */
trait ExecutableQuery[T <: CassandraTable[T, _], R] extends ExecutableStatement {

  self: CQLQuery[_] =>

  def fromRow(r: Row): R

  /**
   * Produces an Enumerator for [R]ows
   * This enumerator can be consumed afterwards with an Iteratee
   * @param session The Cassandra session in use.
   * @param ctx The Execution Context.
   * @return
   */
  def fetchEnumerator()(implicit session: Session, ctx: ExecutionContext): PlayEnumerator[R] = {
    val eventualEnum = future() map {
      resultSet => {
        Enumerator.enumerator(resultSet) through Enumeratee.map(r => fromRow(r))
      }
    }
    PlayEnumerator.flatten(eventualEnum)
  }

  /**
   * Produces a [[com.twitter.concurrent.Spool]] of [R]ows
   * A spool is both lazily constructed and consumed, suitable for large
   * collections when using twitter futures.
   * @param session The cassandra session in use.
   * @return A Spool of R.
   */
  def fetchSpool()(implicit session: Session): TwitterFuture[Spool[R]] = {
    execute() flatMap {
      resultSet => ResultSpool.spool(resultSet).map(spool => spool.map(fromRow))
    }
  }

  /**
   * Returns a parsed sequence of [R]ows
   * This is not suitable for big results set
   * @param session The Cassandra session in use.
   * @param ctx The Execution Context.
   * @return
   */
  def fetch()(implicit session: Session, ctx: ExecutionContext): ScalaFuture[Seq[R]] = {
    fetchEnumerator run Iteratee.collect()
  }

  /**
   * Returns a parsed sequence of [R]ows
   * This is not suitable for big results set
   * @param session The Cassandra session in use.
   * @param ctx The Execution Context.
   * @return
   */
  def collect()(implicit session: Session): TwitterFuture[Seq[R]] = {
    fetchSpool.flatMap(_.toSeq)
  }
}
