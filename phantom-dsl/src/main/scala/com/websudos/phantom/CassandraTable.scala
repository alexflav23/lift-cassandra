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
package com.websudos.phantom

import com.datastax.driver.core.{Row, Session}
import com.websudos.phantom.builder.clauses.DeleteClause
import com.websudos.phantom.builder.query.{RootCreateQuery, _}
import com.websudos.phantom.column.AbstractColumn
import com.websudos.phantom.connectors.KeySpace
import com.websudos.phantom.exceptions.{InvalidClusteringKeyException, InvalidPrimaryKeyException}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.reflect.runtime.{currentMirror => cm, universe => ru}

/**
 * Main representation of a Cassandra table.
 * @tparam T Type of this table.
 * @tparam R Type of record.
 */
abstract class CassandraTable[T <: CassandraTable[T, R], R] extends SelectTable[T, R] { self =>

  type ListColumn[RR] = com.websudos.phantom.column.ListColumn[T, R, RR]
  type SetColumn[RR] =  com.websudos.phantom.column.SetColumn[T, R, RR]
  type MapColumn[KK, VV] =  com.websudos.phantom.column.MapColumn[T, R, KK, VV]
  type JsonColumn[RR] = com.websudos.phantom.column.JsonColumn[T, R, RR]
  type EnumColumn[RR <: Enumeration] = com.websudos.phantom.column.EnumColumn[T, R, RR]
  type OptionalEnumColumn[RR <: Enumeration] = com.websudos.phantom.column.OptionalEnumColumn[T, R, RR]
  type JsonSetColumn[RR] = com.websudos.phantom.column.JsonSetColumn[T, R, RR]
  type JsonListColumn[RR] = com.websudos.phantom.column.JsonListColumn[T, R, RR]
  type JsonMapColumn[KK,VV] = com.websudos.phantom.column.JsonMapColumn[T, R, KK, VV]

  private[phantom] def insertSchema()(
    implicit session: Session,
    keySpace: KeySpace,
    ec: ExecutionContextExecutor
  ): Unit = {
    Await.result(autocreate(keySpace).future(), 10.seconds)
  }

  private[this] val instanceMirror = cm.reflect(this)

  protected[phantom] lazy val _name: String = {
    instanceMirror.symbol.name.toTypeName.decodedName.toString
  }

  lazy val logger = LoggerFactory.getLogger(getClass.getName.stripSuffix("$"))

  def tableName: String = _name

  def fromRow(r: Row): R

  /**
   * The new create mechanism introduced in Phantom 1.6.0.
   * This uses the phantom proprietary QueryBuilder instead of the already available one in the underlying Java Driver.
   * @return A root create block, with full support for all CQL Create query options.
   */
  final def create: RootCreateQuery[T, R] = new RootCreateQuery(this.asInstanceOf[T])

  def autocreate(keySpace: KeySpace): CreateQuery.Default[T, R] = create.ifNotExists()(keySpace)

  final def alter()(implicit keySpace: KeySpace): AlterQuery.Default[T, R] = AlterQuery(this.asInstanceOf[T])

  final def update()(implicit keySpace: KeySpace): UpdateQuery.Default[T, R] = UpdateQuery(this.asInstanceOf[T])

  final def insert()(implicit keySpace: KeySpace): InsertQuery.Default[T, R] = InsertQuery(this.asInstanceOf[T])

  final def delete()(implicit keySpace: KeySpace): DeleteQuery.Default[T, R] = DeleteQuery[T, R](this.asInstanceOf[T])

  final def delete(conditions: (T => DeleteClause.Condition)*)(implicit keySpace: KeySpace): DeleteQuery.Default[T, R] = {
    val tb = this.asInstanceOf[T]

    val queries = conditions.map(_(tb).qb)

    DeleteQuery[T, R](tb, queries: _*)
  }

  final def truncate()(implicit keySpace: KeySpace): TruncateQuery.Default[T, R] = TruncateQuery[T, R](this.asInstanceOf[T])

  def secondaryKeys: Seq[AbstractColumn[_]] = columns.filter(_.isSecondaryKey)

  def primaryKeys: Seq[AbstractColumn[_]] = columns.filter(_.isPrimary).filterNot(_.isPartitionKey)

  def partitionKeys: Seq[AbstractColumn[_]] = columns.filter(_.isPartitionKey)

  def clusteringColumns: Seq[AbstractColumn[_]] = columns.filter(_.isClusteringKey)

  def clustered: Boolean = clusteringColumns.nonEmpty

  /**
   * This method will filter the columns from a Clustering Order definition.
   * It is used to define TimeSeries tables, using the ClusteringOrder trait
   * combined with a directional trait, either Ascending or Descending.
   *
   * This method will simply add to the trailing of a query.
   * @return The clustering key, defined as a string or the empty string.
   */
  private[phantom] def clusteringKey: String = {
    if (clusteringColumns.nonEmpty) {
      val key = clusteringColumns.map(col => {
        val direction = if (col.isAscending) {
          "ASC"
        } else {
          "DESC"
        }
        s"${col.name} $direction"
      })
      s"WITH CLUSTERING ORDER BY (${key.mkString(", ")})"
    } else {
      ""
    }
  }

  /**
   * This method will define the PRIMARY_KEY of the table.
   * <ul>
   *   <li>
   *    For more than one partition key, it will define a Composite Key.
   *    Example: PRIMARY_KEY((partition_key_1, partition_key2), primary_key_1, etc..)
   *   </li>
   *   <li>
   *     For a single partition key, it will define a Compound Key.
   *     Example: PRIMARY_KEY(partition_key_1, primary_key_1, primary_key_2)
   *   </li>
   *   <li>
   *     For no partition key, it will throw an exception.
   *   </li>
   * </ul>
   * @return A string value representing the primary key of the table.
   */
  @throws(classOf[InvalidPrimaryKeyException])
  private[phantom] def defineTableKey(): String = {

    preconditions()

    // Get the list of primary keys that are not partition keys.
    val primaries = primaryKeys
    val primaryString = primaryKeys.map(_.name).mkString(", ")

    // Get the list of partition keys that are not primary keys
    // This is done to avoid including the same columns twice.
    val partitions = partitionKeys.toList
    val partitionString = s"(${partitions.map(_.name).mkString(", ")})"

    val operand = partitions.lengthCompare(1)
    val key = if (operand < 0) {
      throw InvalidPrimaryKeyException(tableName)
    } else if (operand == 0) {

      val partitionKey = partitions.headOption.map(_.name).orNull

      if (primaries.isEmpty) {
        partitionKey
      } else {
        s"$partitionKey, $primaryString"
      }
    } else {
      if (primaries.isEmpty) {
        partitionString
      } else {
        s"$partitionString, $primaryString"
      }
    }
    s"PRIMARY KEY ($key)"
  }

  /**
   * This method will check for common Cassandra anti-patterns during the intialisation of a schema.
   * If the Schema definition violates valid CQL standard, this function will throw an error.
   *
   * A perfect example is using a mixture of Primary keys and Clustering keys in the same schema.
   * While a Clustering key is also a primary key, when defining a clustering key all other keys must become clustering keys and specify their order.
   *
   * We could auto-generate this order but we wouldn't be making false assumptions about the desired ordering.
   */
  private[this] def preconditions(): Unit = {
    if (clustered && primaryKeys.diff(clusteringColumns).nonEmpty) {
      logger.error("When using CLUSTERING ORDER all PrimaryKey definitions" +
        " must become a ClusteringKey definition and specify order.")
      throw InvalidClusteringKeyException(tableName)
    }
  }

  val columns: Seq[AbstractColumn[_]] = Lock.synchronized {

    val selfType = instanceMirror.symbol.toType

    val members: Seq[ru.Symbol] = (for {
      baseClass <- selfType.baseClasses.reverse
      symbol <- baseClass.typeSignature.members.sorted
      if symbol.typeSignature <:< ru.typeOf[AbstractColumn[_]]
    } yield symbol)(collection.breakOut)

    for {
      symbol <- members.distinct
      table = if (symbol.isModule) {
        instanceMirror.reflectModule(symbol.asModule).instance
      } else if (symbol.isTerm && symbol.asTerm.isVal) {
        instanceMirror.reflectField(symbol.asTerm).get
      }
    } yield table.asInstanceOf[AbstractColumn[_]]
  }
}

private[phantom] case object Lock
