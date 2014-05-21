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
package com.newzly.phantom.tables

import java.util.UUID
import com.datastax.driver.core.Row
import com.newzly.phantom.helper.TestSampler
import com.newzly.phantom.Implicits._
import com.newzly.phantom.column.TimeSeries

sealed class StaticTableTest extends CassandraTable[StaticTableTest, (UUID, UUID, String)] {

  object id extends UUIDColumn(this) with PartitionKey[UUID]

  object clusteringId extends UUIDColumn(this) with PrimaryKey[UUID] with ClusteringOrder[UUID] with Descending {
    override private[phantom] implicit val timeSeries: TimeSeries[UUID] = new TimeSeries[UUID]
  }
  object staticTest extends StringColumn(this) with StaticColumn[String]

  def fromRow(row: Row): (UUID, UUID, String) = (id(row), clusteringId(row), staticTest(row))
}

object StaticTableTest extends StaticTableTest with TestSampler[StaticTableTest, (UUID, UUID, String)]