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
package com.websudos.phantom.builder.query

import com.datastax.driver.core.{ConsistencyLevel, Row, Session}
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.builder._
import com.websudos.phantom.builder.clauses.{QueryCondition, WhereClause}
import shapeless.HList
import shapeless.ops.hlist.Prepend

import scala.annotation.implicitNotFound

abstract class RootQuery[
  Table <: CassandraTable[Table, _],
  Record,
  Status <: ConsistencyBound
](table: Table, val qb: CQLQuery, override val options: QueryOptions) extends ExecutableStatement {

  protected[this] type QueryType[
    T <: CassandraTable[T, _],
    R,
    S <: ConsistencyBound
  ] <: RootQuery[T, R, S]

  protected[this] def create[
    T <: CassandraTable[T, _],
    R,
    S <: ConsistencyBound
  ](t: T, q: CQLQuery, options: QueryOptions): QueryType[T, R, S]


  @implicitNotFound("You have already specified a ConsistencyLevel for this query")
  def consistencyLevel_=(level: ConsistencyLevel)(implicit ev: Status =:= Unspecified, session: Session): QueryType[Table, Record, Specified] = {
    if (session.protocolConsistency) {
      create(table, qb, options.consistencyLevel_=(level))
    } else {
      create(table, QueryBuilder.consistencyLevel(qb, level.toString), options)
    }
  }
}


abstract class Query[
  Table <: CassandraTable[Table, _],
  Record,
  Limit <: LimitBound,
  Order <: OrderBound,
  Status <: ConsistencyBound,
  Chain <: WhereBound,
  PS <: HList
](
  table: Table,
  override val qb: CQLQuery,
  row: Row => Record,
  usingPart: UsingPart = UsingPart.empty,
  override val options: QueryOptions
) extends ExecutableStatement {

  protected[this] type QueryType[
    T <: CassandraTable[T, _],
    R,
    L <: LimitBound,
    O <: OrderBound,
    S <: ConsistencyBound,
    C <: WhereBound,
    P <: HList
  ] <: Query[T, R, L, O, S, C, P]

  protected[this] def create[
    T <: CassandraTable[T, _],
    R,
    L <: LimitBound,
    O <: OrderBound,
    S <: ConsistencyBound,
    C <: WhereBound,
    P <: HList
  ](t: T, q: CQLQuery, r: Row => R, usingPart: UsingPart, options: QueryOptions): QueryType[T, R, L, O, S, C, P]

  @implicitNotFound("A ConsistencyLevel was already specified for this query.")
  def consistencyLevel_=(level: ConsistencyLevel)
    (implicit ev: Status =:= Unspecified, session: Session): QueryType[Table, Record, Limit, Order, Specified, Chain, PS] = {
    if (session.protocolConsistency) {
      create[Table, Record, Limit, Order, Specified, Chain, PS](
        table,
        CQLQuery.empty,
        row,
        usingPart,
        options.consistencyLevel_=(level)
      )
    } else {
      create[Table, Record, Limit, Order, Specified, Chain, PS](
        table,
        CQLQuery.empty,
        row,
        usingPart append QueryBuilder.consistencyLevel(level.toString),
        options
      )
    }
  }

  @implicitNotFound("A limit was already specified for this query.")
  def limit(limit: Int)(implicit ev: Limit =:= Unlimited): QueryType[Table, Record, Limited, Order, Status, Chain, PS] = {
    create[Table, Record, Limited, Order, Status, Chain, PS](
      table,
      QueryBuilder.limit(qb, limit),
      row,
      usingPart,
      options
    )
  }

  /**
    * The where method of a select query.
    * @param condition A where clause condition restricted by path dependant types.
    * @param ev An evidence request guaranteeing the user cannot chain multiple where clauses on the same query.
    * @return
    */
  def where[
    RR,
    HL <: HList,
    Out <: HList
  ](
    condition: Table => QueryCondition[HL]
  )(implicit
    ev: Chain =:= Unchainned,
    prepend: Prepend.Aux[HL, PS, Out]
  ): QueryType[Table, Record, Limit, Order, Status, Chainned, Out]

  /**
    * The where method of a select query.
    * @param condition A where clause condition restricted by path dependant types.
    * @param ev An evidence request guaranteeing the user cannot chain multiple where clauses on the same query.
    * @return
    */
  def and[
    RR,
    HL <: HList,
    Out <: HList
  ](
    condition: Table => QueryCondition[HL]
  )(implicit
    ev: Chain =:= Chainned,
    prepend: Prepend.Aux[HL, PS, Out]
  ): QueryType[Table, Record, Limit, Order, Status, Chainned, Out]


  def ttl(seconds: Long): QueryType[Table, Record, Limit, Order, Status, Chain, PS] = {
    create[Table, Record, Limit, Order, Status, Chain, PS](
      table,
      qb,
      row,
      usingPart append QueryBuilder.ttl(seconds.toString),
      options
    )
  }

  def ttl(duration: scala.concurrent.duration.FiniteDuration): QueryType[Table, Record, Limit, Order, Status, Chain, PS] = {
    ttl(duration.toSeconds)
  }
}

trait Batchable {
  self: ExecutableStatement =>
}
