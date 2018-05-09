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
package com.outworkers.phantom

import java.net.InetAddress
import java.nio.ByteBuffer
import java.util.{Date, UUID}

import com.outworkers.phantom.builder.primitives.{AsciiValue, Primitive}
import com.outworkers.phantom.builder.syntax.CQLSyntax
import org.joda.time.{DateTime, LocalDate}

import scala.util.{Failure, Success, Try}

trait TableAliases[T <: CassandraTable[T, R], R] { self: CassandraTable[T, R] =>

  class ListColumn[RR]()(
    implicit ev: Primitive[RR],
    ev2: Primitive[List[RR]]
  ) extends com.outworkers.phantom.column.CollectionColumn[T, R, List, RR](this, CQLSyntax.Collections.list)

  class SetColumn[RR]()(
    implicit ev: Primitive[RR],
    ev2: Primitive[Set[RR]]
  ) extends com.outworkers.phantom.column.CollectionColumn[T, R, Set, RR](this, CQLSyntax.Collections.set)

  class MapColumn[KK, VV]()(implicit
    ev: Primitive[KK],
    ev2: Primitive[VV],
    ev3: Primitive[Map[KK, VV]]
  ) extends com.outworkers.phantom.column.MapColumn[T, R, KK, VV](this)

  abstract class JsonColumn[RR]()(implicit ev: Primitive[RR]) extends Col[RR]
  abstract class OptionalJsonColumn[RR]()(
    implicit ev: Primitive[RR],
    optEv: Primitive[Option[RR]]
  ) extends OptionalCol[RR]

  class EnumColumn[RR <: Enumeration#Value : Primitive] extends com.outworkers.phantom.column.PrimitiveColumn[T, R, RR](this)
  class OptionalEnumColumn[RR <: Enumeration#Value : Primitive] extends com.outworkers.phantom.column.OptionalPrimitiveColumn[T, R, RR](this)

  abstract class JsonSetColumn[RR]()(
    implicit ev: Primitive[Set[RR]],
    ev2: Primitive[RR]
  ) extends SetColumn[RR]

  abstract class JsonListColumn[RR]()(
    implicit ev: Primitive[List[RR]],
    ev2: Primitive[RR]
  ) extends ListColumn[RR]

  class JsonMapColumn[KK, VV]()(implicit
    ev: Primitive[KK],
    ev2: Primitive[VV],
    ev3: Primitive[Map[KK, VV]]
  ) extends MapColumn[KK, VV]

  class PrimitiveColumn[RR : Primitive] extends com.outworkers.phantom.column.PrimitiveColumn[T, R, RR](this)
  class TupleColumn[RR : Primitive] extends PrimitiveColumn[RR]
  class CustomColumn[RR : Primitive] extends PrimitiveColumn[RR]
  class Col[RR : Primitive] extends PrimitiveColumn[RR]

  class OptionalCol[RR](
    implicit ev: Primitive[RR],
    evOpt: Primitive[Option[RR]]
  ) extends Col[Option[RR]] {
    override def parse(r: Row): Try[Option[RR]] = ev.fromRow(name, r) match {
      case Success(value) => Success(Some(value))
      case Failure(_) if r.isNull(name) => Success(None)
      case Failure(ex) => Failure[Option[RR]](ex)
    }
  }

  abstract class Column[RR] extends com.outworkers.phantom.column.Column[T, R, RR](this)

  abstract class OptionalColumn[RR] extends com.outworkers.phantom.column.OptionalColumn[T, R, RR](this)
  class OptionalPrimitiveColumn[RR : Primitive] extends com.outworkers.phantom.column.OptionalPrimitiveColumn[T, R, RR](this)

  class BigDecimalColumn()(implicit ev: Primitive[BigDecimal]) extends Col[BigDecimal]
  class BlobColumn()(implicit ev: Primitive[ByteBuffer]) extends Col[ByteBuffer]
  class BigIntColumn()(implicit ev: Primitive[BigInt]) extends Col[BigInt]
  class BooleanColumn()(implicit ev: Primitive[Boolean]) extends Col[Boolean]
  class DateColumn()(implicit ev: Primitive[Date]) extends Col[Date]
  class DateTimeColumn()(implicit ev: Primitive[DateTime]) extends Col[DateTime]
  class LocalDateColumn()(implicit ev: Primitive[LocalDate]) extends Col[LocalDate]
  class DoubleColumn()(implicit ev: Primitive[Double]) extends Col[Double]
  class FloatColumn()(implicit ev: Primitive[Float]) extends Col[Float]
  class IntColumn()(implicit ev: Primitive[Int]) extends Col[Int]
  class SmallIntColumn()(implicit ev: Primitive[Short]) extends Col[Short]
  class TinyIntColumn()(implicit ev: Primitive[Byte]) extends Col[Byte]
  class InetAddressColumn()(implicit ev: Primitive[InetAddress]) extends Col[InetAddress]
  class LongColumn()(implicit ev: Primitive[Long]) extends Col[Long]
  class StringColumn()(implicit ev: Primitive[String]) extends Col[String]
  class UUIDColumn()(implicit ev: Primitive[UUID]) extends Col[UUID]
  class CounterColumn()(implicit ev: Primitive[Long]) extends com.outworkers.phantom.column.CounterColumn[T, R](this)
  class TimeUUIDColumn()(implicit ev: Primitive[UUID]) extends com.outworkers.phantom.column.TimeUUIDColumn[T, R](this)
  class AsciiColumn()(implicit ev: Primitive[AsciiValue]) extends Col[AsciiValue]

  class OptionalAsciiColumn()(implicit ev: Primitive[AsciiValue]) extends OptionalCol[AsciiValue]

  class OptionalBlobColumn()(
    implicit ev: Primitive[ByteBuffer]
  ) extends OptionalCol[ByteBuffer]

  class OptionalBigDecimalColumn()(
    implicit ev: Primitive[BigDecimal]
  ) extends OptionalCol[BigDecimal]

  class OptionalBigIntColumn()(
    implicit ev: Primitive[BigInt]
  ) extends OptionalCol[BigInt]

  class OptionalBooleanColumn()(
    implicit ev: Primitive[Boolean]
  ) extends OptionalCol[Boolean]

  class OptionalDateColumn()(
    implicit ev: Primitive[Date]
  ) extends OptionalCol[Date]

  class OptionalDateTimeColumn()(
    implicit ev: Primitive[DateTime]
  ) extends OptionalCol[DateTime]

  class OptionalLocalDateColumn()(
    implicit ev: Primitive[LocalDate]
  ) extends OptionalCol[LocalDate]

  class OptionalDoubleColumn()(
    implicit ev: Primitive[Double]
  ) extends OptionalCol[Double]

  class OptionalFloatColumn()(
    implicit ev: Primitive[Float]
  ) extends OptionalCol[Float]

  class OptionalIntColumn()(
    implicit ev: Primitive[Int]
  ) extends OptionalCol[Int]

  class OptionalSmallIntColumn()(
    implicit ev: Primitive[Short]
  ) extends OptionalCol[Short]

  class OptionalTinyIntColumn()(
    implicit ev: Primitive[Byte]
  ) extends OptionalCol[Byte]

  class OptionalInetAddressColumn()(
    implicit ev: Primitive[InetAddress]
  ) extends OptionalCol[InetAddress]

  class OptionalLongColumn()(
    implicit ev: Primitive[Long]
  ) extends OptionalCol[Long]

  class OptionalStringColumn()(
    implicit ev: Primitive[String]
  ) extends OptionalCol[String]

  class OptionalUUIDColumn()(
    implicit ev: Primitive[UUID]
  ) extends OptionalCol[UUID]

  class OptionalTimeUUIDColumn()(
    implicit ev: Primitive[UUID]
  ) extends OptionalCol[UUID] {
    override val cassandraType: String = CQLSyntax.Types.TimeUUID
  }
}
