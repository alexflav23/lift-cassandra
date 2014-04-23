/*
 * Copyright 2013 newzly ltd.
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
package com.newzly.phantom.column

import java.util.Date
import scala.annotation.implicitNotFound
import org.joda.time.DateTime
import com.newzly.phantom.{ CassandraPrimitive, CassandraTable }
import com.datastax.driver.core.Row

@implicitNotFound(msg = "Type ${RR} must be a Cassandra primitive")
class PrimitiveColumn[Owner <: CassandraTable[Owner, Record], Record, @specialized(Int, Double, Float, Long) RR: CassandraPrimitive](t: CassandraTable[Owner, Record]) extends Column[Owner, Record, RR](t) {

  def cassandraType: String = CassandraPrimitive[RR].cassandraType
  def toCType(v: RR): AnyRef = CassandraPrimitive[RR].toCType(v)

  def optional(r: Row): Option[RR] =
    implicitly[CassandraPrimitive[RR]].fromRow(r, name)
}

/**
 * Simple mechanism to restrict ClusteringOrder mixin to a union type bound.
 * @tparam T The type of the value to store.
 */
private[phantom] class TimeSeries[T]

/**
 * A Date Column, used to enforce restrictions on clustering order.
 * @param table The Cassandra Table to which the column belongs to.
 * @tparam Owner The Owner of the Record.
 * @tparam Record The Record type.
 */
class DateColumn[Owner <: CassandraTable[Owner, Record], Record](table: CassandraTable[Owner, Record]) extends PrimitiveColumn[Owner, Record, Date](table) {
  private[phantom] implicit val timeSeries = new TimeSeries[Date]
}

/**
 * A DateTime Column, used to enforce restrictions on clustering order.
 * @param table The Cassandra Table to which the column belongs to.
 * @tparam Owner The Owner of the Record.
 * @tparam Record The Record type.
 */
class DateTimeColumn[Owner <: CassandraTable[Owner, Record], Record](table: CassandraTable[Owner, Record]) extends PrimitiveColumn[Owner, Record, DateTime](table) {
  private[phantom] implicit val timeSeries = new TimeSeries[DateTime]
}