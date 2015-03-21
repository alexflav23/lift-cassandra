package com.websudos.phantom.builder.query

import java.util.{List => JavaList}

import com.datastax.driver.core.{ResultSet, Row, Session}
import com.twitter.concurrent.Spool
import com.twitter.util.{Future => TwitterFuture}
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.builder.{Unlimited, LimitBound, QueryBuilder}
import com.websudos.phantom.connectors.KeySpace
import com.websudos.phantom.iteratee.{Enumerator, Iteratee, ResultSpool}
import play.api.libs.iteratee.{Enumeratee, Enumerator => PlayEnumerator}

import scala.concurrent.{ExecutionContext, Future => ScalaFuture}

trait ExecutableStatement extends CassandraOperations {

  val qb: CQLQuery

  def future()(implicit session: Session, keySpace: KeySpace): ScalaFuture[ResultSet] = {
    scalaQueryStringExecuteToFuture(QueryBuilder.prependKeySpaceIfAbsent(keySpace.name, qb).queryString)
  }

  def execute()(implicit session: Session, keySpace: KeySpace): TwitterFuture[ResultSet] = {
    twitterQueryStringExecuteToFuture(QueryBuilder.prependKeySpaceIfAbsent(keySpace.name, qb).queryString)
  }
}

/**
 * An ExecutableQuery implementation, meant to retrieve results from Cassandra.
 * This provides the root implementation of a Select query.
 * @tparam T The class owning the table.
 * @tparam R The record type to store.
 */
trait ExecutableQuery[T <: CassandraTable[T, _], R, Limit <: LimitBound] extends ExecutableStatement {

  def fromRow(r: Row): R

  private[this] def singleResult(row: Row): Option[R] = {
    if (row != null) Some(fromRow(row)) else None
  }

  private[this] def directMapper(results: JavaList[Row]): List[R] = {
    List.tabulate(results.size())(index => fromRow(results.get(index)))
  }

  private[phantom] def singleFetch()(implicit session: Session, ctx: ExecutionContext, keySpace: KeySpace): ScalaFuture[Option[R]] = {
    future() map { res => singleResult(res.one) }
  }

  private[phantom] def singleCollect()(implicit session: Session, keySpace: KeySpace): TwitterFuture[Option[R]] = {
    execute() map { res => singleResult(res.one) }
  }

  /**
   * Produces an Enumerator for [R]ows
   * This enumerator can be consumed afterwards with an Iteratee
   * @param session The Cassandra session in use.
   * @param ctx The Execution Context.
   * @return
   */
  def fetchEnumerator()(implicit session: Session, ctx: ExecutionContext, keySpace: KeySpace): PlayEnumerator[R] = {
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
  def fetchSpool()(implicit session: Session, keySpace: KeySpace): TwitterFuture[Spool[R]] = {
    execute() flatMap {
      resultSet => ResultSpool.spool(resultSet).map(spool => spool.map(fromRow))
    }
  }

  /**
   * Returns the first row from the select ignoring everything else
   * @param session The Cassandra session in use.
   * @param ctx The Execution Context.
   * @return
   */
  def one()(implicit session: Session, ctx: ExecutionContext, keySpace: KeySpace, ev: Limit =:= Unlimited): ScalaFuture[Option[R]]

  /**
   * Get the result of an operation as a Twitter Future.
   * @param session The Datastax Cassandra session.
   * @return A Twitter future wrapping the result.
   */
  def get()(implicit session: Session, keySpace: KeySpace, ev: Limit =:= Unlimited): TwitterFuture[Option[R]]

  /**
   * Returns a parsed sequence of [R]ows
   * This is not suitable for big results set
   * @param session The Cassandra session in use.
   * @param ctx The Execution Context.
   * @return
   */
  def fetch()(implicit session: Session, ctx: ExecutionContext, keySpace: KeySpace): ScalaFuture[List[R]] = {
    future() map { resultSet => {directMapper(resultSet.all) } }
  }

  /**
   * Returns a parsed sequence of [R]ows
   * This is not suitable for big results set
   * @param session The Cassandra session in use.
   * @return
   */
  def collect()(implicit session: Session, keySpace: KeySpace): TwitterFuture[List[R]] = {
    execute() map { resultSet => {directMapper(resultSet.all) } }
  }
}
