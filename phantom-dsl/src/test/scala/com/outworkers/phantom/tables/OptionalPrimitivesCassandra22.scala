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
package com.outworkers.phantom.tables

import com.outworkers.phantom.CassandraTable
import com.outworkers.phantom.connectors.RootConnector
import com.outworkers.phantom.builder.query.InsertQuery
import com.outworkers.phantom.dsl._
import com.outworkers.util.testing._

case class OptionalPrimitiveCassandra22(
  pkey: String,
  short: Option[Short],
  byte: Option[Byte],
  localDate: Option[LocalDate]
)

object OptionalPrimitiveCassandra22 {

  def none: OptionalPrimitiveCassandra22 = {
    OptionalPrimitiveCassandra22(
      pkey = gen[String],
      short = None,
      byte = None,
      localDate = None
    )
  }
}

abstract class OptionalPrimitivesCassandra22 extends CassandraTable[
  OptionalPrimitivesCassandra22,
  OptionalPrimitiveCassandra22
] with RootConnector {

  object pkey extends StringColumn(this) with PartitionKey

  object short extends OptionalSmallIntColumn(this)

  object byte extends OptionalTinyIntColumn(this)

  object localDate extends OptionalLocalDateColumn(this)

  override val tableName = "OptionalPrimitivesCassandra22"

  def store(row: OptionalPrimitiveCassandra22): InsertQuery.Default[OptionalPrimitivesCassandra22, OptionalPrimitiveCassandra22] = {
    insert
      .value(_.pkey, row.pkey)
      .value(_.short, row.short)
      .value(_.byte, row.byte)
      .value(_.localDate, row.localDate)
  }
}
