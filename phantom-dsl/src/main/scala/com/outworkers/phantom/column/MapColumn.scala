/*
 * Copyright 2013 - 2017 Outworkers Ltd.
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
package com.outworkers.phantom.column

import java.nio.{BufferUnderflowException, ByteBuffer}

import com.datastax.driver.core.exceptions.InvalidTypeException
import com.datastax.driver.core.{CodecUtils, ProtocolVersion, Row}
import com.outworkers.phantom.CassandraTable
import com.outworkers.phantom.builder.QueryBuilder
import com.outworkers.phantom.builder.ops.MapKeyUpdateClause
import com.outworkers.phantom.builder.primitives.Primitive
import com.outworkers.phantom.builder.query.CQLQuery

import scala.annotation.implicitNotFound
import scala.collection.JavaConverters._
import scala.collection.generic.CanBuildFrom
import scala.util.{Failure, Success, Try}

private[phantom] abstract class AbstractMapColumn[
  Owner <: CassandraTable[Owner, Record],
  Record,
  K,
  V
](table: CassandraTable[Owner, Record]) extends Column[Owner, Record, Map[K, V]](table)
  with CollectionValueDefinition[V] {

  def keyAsCql(v: K): String

  def keyFromCql(c: String): K

  override def fromString(c: String): V

  def asCql(v: Map[K, V]): String = QueryBuilder.Collections.serialize(v.map {
    case (a, b) => (keyAsCql(a), valueAsCql(b))
  }).queryString

  override def apply(r: Row): Map[K, V] = {
    parse(r) match {
      case Success(map) => map

      // Note null rows will not result in a failure, we return an empty map for those.
      case Failure(ex) => {
        table.logger.error(ex.getMessage)
        throw ex
      }
    }
  }
}

@implicitNotFound(msg = "Type ${K} and ${V} must be Cassandra primitives")
class MapColumn[Owner <: CassandraTable[Owner, Record], Record, K : Primitive, V : Primitive](table: CassandraTable[Owner, Record])
    extends AbstractMapColumn[Owner, Record, K, V](table) with PrimitiveCollectionValue[V] {

  private[this] val keyPrimitive = Primitive[K]

  override def keyAsCql(v: K): String = keyPrimitive.asCql(v)

  override val valuePrimitive: Primitive[V] = Primitive[V]

  override val cassandraType: String = QueryBuilder.Collections.mapType(
    keyPrimitive.cassandraType,
    valuePrimitive.cassandraType
  ).queryString

  /*
  protected[this] def parseMap(bytes: ByteBuffer, protocolVersion: ProtocolVersion)(
    implicit cbf: CanBuildFrom[Nothing, (K, V), Map[K, V]]
  ): Try[Map[K, V]] = {

    if (bytes == null || bytes.remaining == 0) Success(cbf().result())

    try
      val input = bytes.duplicate
      val n = CodecUtils.readSize(input, protocolVersion)
      val builder = cbf()
      builder.sizeHint(n)

      val m = builder
      var i = 0
      while (i < n) {
        {
          val kbb = CodecUtils.readValue(input, protocolVersion)
          val vbb = CodecUtils.readValue(input, protocolVersion)

          m += (keyCodec.deserialize(kbb, protocolVersion), valueCodec.deserialize(vbb, protocolVersion))
        }
        {
          i += 1;
          i - 1
        }
      }
      m

    catch {
      case e: BufferUnderflowException => {
        throw new InvalidTypeException("Not enough bytes to deserialize a map", e)
      }
    }
  }*/

  override def qb: CQLQuery = {
    if (shouldFreeze) {
      QueryBuilder.Collections.frozen(name, cassandraType)
    } else {
      CQLQuery(name).forcePad.append(cassandraType)
    }
  }

  override def keyFromCql(c: String): K = keyPrimitive.fromString(c)

  override def valueAsCql(v: V): String = valuePrimitive.asCql(v)

  override def fromString(c: String): V = valuePrimitive.fromString(c)

  override def parse(r: Row): Try[Map[K, V]] = {
    if (r.isNull(name)) {
      Success(Map.empty[K, V])
    } else {
      Try(
        r.getMap(name, keyPrimitive.clz, valuePrimitive.clz).asScala.toMap map {
          case (k, v) => keyPrimitive.extract(k) -> valuePrimitive.extract(v)
        }
      )
    }
  }

  def apply(k: K): MapKeyUpdateClause[K, V] = {
    new MapKeyUpdateClause[K, V](name, k)
  }
}
