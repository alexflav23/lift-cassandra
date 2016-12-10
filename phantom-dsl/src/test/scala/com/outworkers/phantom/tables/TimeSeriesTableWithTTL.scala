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
import com.outworkers.phantom.dsl._
import com.outworkers.util.testing._
import org.joda.time.DateTime

sealed class TimeSeriesTableWithTTL extends CassandraTable[ConcreteTimeSeriesTableWithTTL, TimeSeriesRecord] {
  object id extends UUIDColumn(this) with PartitionKey
  object name extends StringColumn(this)
  object timestamp extends DateTimeColumn(this) with ClusteringOrder with Descending
}

abstract class ConcreteTimeSeriesTableWithTTL extends TimeSeriesTableWithTTL with RootConnector

sealed class TimeSeriesTableWithTTL2 extends CassandraTable[ConcreteTimeSeriesTableWithTTL2, TimeSeriesRecord] {
  object id extends UUIDColumn(this) with PartitionKey
  object name extends StringColumn(this)
  object timestamp extends DateTimeColumn(this)
}

abstract class ConcreteTimeSeriesTableWithTTL2 extends TimeSeriesTableWithTTL2 with RootConnector {
  val testUUID = gen[UUID]
}

