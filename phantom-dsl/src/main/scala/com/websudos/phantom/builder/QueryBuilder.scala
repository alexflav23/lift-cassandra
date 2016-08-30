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
package com.websudos.phantom.builder

import com.websudos.phantom.builder.query.CQLQuery
import com.websudos.phantom.builder.serializers._
import com.websudos.phantom.builder.syntax.CQLSyntax
import com.websudos.phantom.connectors.KeySpace

case class QueryBuilderConfig(caseSensitiveTables: Boolean)

object QueryBuilderConfig {
  final val Default = new QueryBuilderConfig(false)
}

abstract class QueryBuilder(val config: QueryBuilderConfig = QueryBuilderConfig.Default) {

  case object Create extends CreateTableBuilder

  case object Delete extends DeleteQueryBuilder

  case object Update extends UpdateQueryBuilder

  case object Collections extends CollectionModifiers(this)

  case object Where extends IndexModifiers

  case object Select extends SelectQueryBuilder

  case object Batch extends BatchQueryBuilder

  case object Utils extends Utils

  case object Alter extends AlterQueryBuilder

  case object Insert extends InsertQueryBuilder

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

  def ttl(seconds: String): CQLQuery = {
    CQLQuery(CQLSyntax.CreateOptions.ttl).forcePad.append(seconds)
  }

  /**
    * Produces a timestamp clause that should be appended to a UsingPart.
    * @param unixTimestamp The milliseconds since EPOCH long value of a timestamp.
    * @return A CQLQuery wrapping the USING clause.
    */
  def timestamp(unixTimestamp: Long): CQLQuery = {
    CQLQuery(CQLSyntax.timestamp).forcePad.append(unixTimestamp.toString)
  }

  def consistencyLevel(qb: CQLQuery, level: String): CQLQuery = {
    using(qb).pad.append(CQLSyntax.consistency).forcePad.append(level)
  }

  def consistencyLevel(level: String): CQLQuery = {
    CQLQuery(CQLSyntax.consistency).forcePad.append(level)
  }

  def tableDef(tableName: String): CQLQuery = {
    if (config.caseSensitiveTables) {
      CQLQuery(CQLQuery.escape(tableName))
    } else {
      CQLQuery(tableName)
    }
  }

  def keyspace(space: String, tableQuery: CQLQuery): CQLQuery = {
    keyspace(space, tableQuery.queryString)
  }

  def keyspace(keySpace: String, table: String): CQLQuery = {
    if (table.startsWith(keySpace + ".")) {
      tableDef(table)
    }  else {
      tableDef(table).prepend(s"$keySpace.")
    }
  }

  def limit(value: Int): CQLQuery = {
    CQLQuery(CQLSyntax.limit)
      .forcePad.append(value.toString)
  }

  def limit(qb: CQLQuery, value: Int): CQLQuery = {
    qb.pad.append(CQLSyntax.limit)
      .forcePad.append(value.toString)
  }

  def keyspace(space: String): RootSerializer = KeySpaceSerializer(space)

  def keyspace(space: KeySpace): RootSerializer = KeySpaceSerializer(space)

}

private[phantom] object QueryBuilder extends QueryBuilder(QueryBuilderConfig.Default)
