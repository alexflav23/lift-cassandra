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

import com.outworkers.phantom.connectors.RootConnector
import com.outworkers.phantom.builder.query.InsertQuery
import com.outworkers.phantom.dsl._

 case class SecondaryIndexRecord(
  primary: UUID,
  secondary: UUID,
  name: String
)

sealed class SecondaryIndexTable extends CassandraTable[ConcreteSecondaryIndexTable, SecondaryIndexRecord] {

  object id extends UUIDColumn(this) with PartitionKey
  object secondary extends UUIDColumn(this) with Index
  object name extends StringColumn(this)

  def fromRow(r: Row): SecondaryIndexRecord = {
    SecondaryIndexRecord(
      primary = id(r),
      secondary = secondary(r),
      name = name(r)
    )
  }
}

abstract class ConcreteSecondaryIndexTable extends SecondaryIndexTable with RootConnector {

  def store(sample: SecondaryIndexRecord): InsertQuery.Default[ConcreteSecondaryIndexTable, SecondaryIndexRecord] = {
    insert
      .value(_.id, sample.primary)
      .value(_.secondary, sample.secondary)
      .value(_.name, sample.name)
  }

}