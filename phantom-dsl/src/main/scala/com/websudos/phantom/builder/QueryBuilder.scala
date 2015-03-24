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
package com.websudos.phantom.builder

import com.websudos.phantom.builder.query.CQLQuery

sealed class BatchBuilder {
  def batch(batchType: String): CQLQuery = {
    CQLQuery(CQLSyntax.Batch.begin).forcePad.append(batchType).forcePad.append(CQLSyntax.Batch.batch)
  }

  def applyBatch(qb: CQLQuery): CQLQuery = {
    qb.forcePad.append(CQLSyntax.Batch.apply).forcePad.append(CQLSyntax.Batch.batch).append(CQLSyntax.Symbols.`;`)
  }
}

private[phantom] object QueryBuilder {

  case object Create extends CreateTableBuilder

  case object Update extends UpdateQueryBuilder

  case object Collections extends CollectionModifiers

  case object Where extends IndexModifiers
  
  case object Select extends SelectQueryBuilder

  case object Batch extends BatchBuilder

  case object Utils extends Utils

  def ifNotExists(qb: CQLQuery): CQLQuery = {
    qb.forcePad.append(CQLSyntax.ifNotExists)
  }


  def truncate(table: String): CQLQuery = {
    CQLQuery(CQLSyntax.truncate).forcePad.append(table)
  }

  def using(qb: CQLQuery): CQLQuery = {
    qb.pad.append(CQLSyntax.using)
  }

  def ttl(qb: CQLQuery, seconds: String): CQLQuery = {
    using(qb).forcePad.append(CQLSyntax.CreateOptions.ttl).forcePad.append(seconds)
  }

  def timestamp(qb: CQLQuery, seconds: String): CQLQuery = {
    using(qb).forcePad.append(CQLSyntax.timestamp).forcePad.append(seconds)
  }

  def consistencyLevel(qb: CQLQuery, level: String): CQLQuery = {
    using(qb).pad.append(CQLSyntax.consistency).forcePad.append(level)
  }

  def keyspace(keySpace: String, qb: CQLQuery): CQLQuery = {
    if (qb.queryString.startsWith(keySpace)) {
      qb
    }  else {
      qb.prepend(s"$keySpace.")
    }
  }

  def keyspace(keySpace: String, qb: String): CQLQuery = {
    if (qb.startsWith(keySpace)) {
      CQLQuery(qb)
    }  else {
      CQLQuery(qb).prepend(s"$keySpace.")
    }
  }

  def update(tableName: String) = {
    CQLQuery(CQLSyntax.update)
      .forcePad.append(tableName)
  }

  def alter(tableName: String) = {
    CQLQuery(CQLSyntax.alter)
      .forcePad.append(tableName)
  }

  def limit(qb: CQLQuery, value: Int): CQLQuery = {
    qb.pad.append(CQLSyntax.limit)
      .forcePad.append(value.toString)
  }

  def insert(table: String): CQLQuery = {
    CQLQuery(CQLSyntax.insert)
      .forcePad.append(CQLSyntax.into)
      .forcePad.append(table)
  }


  def insert(table: CQLQuery): CQLQuery = {
    insert(table.queryString)
  }

  def delete(table: String): CQLQuery = {
    CQLQuery(CQLSyntax.delete)
      .forcePad.append(table)
  }

  def insert(qb: CQLQuery, clauses: CQLQuery): CQLQuery = Utils.concat(qb, clauses)

  def insertPairs(columns: List[String], values: List[String]): CQLQuery = {
    CQLQuery.empty.wrap(columns).forcePad.append(CQLSyntax.values).wrap(values)
  }

  def insertPairs(pairs: List[(String, String)]): CQLQuery = {
    insertPairs(pairs.map(_._1), pairs.map(_._2))
  }
}
