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
package com.websudos.phantom.tables

import com.websudos.phantom.builder.query.InsertQuery
import com.websudos.phantom.dsl._

sealed class StaticTableTest extends CassandraTable[ConcreteStaticTableTest, (UUID, UUID, String)] {

  object id extends UUIDColumn(this) with PartitionKey[UUID]

  object clusteringId extends UUIDColumn(this) with PrimaryKey[UUID] with ClusteringOrder[UUID] with Descending
  object staticTest extends StringColumn(this) with StaticColumn[String]

  def fromRow(row: Row): (UUID, UUID, String) = {
    (id(row), clusteringId(row), staticTest(row))
  }
}

abstract class ConcreteStaticTableTest extends StaticTableTest with RootConnector


case class StaticCollectionRecord(
  id: UUID,
  clustering: UUID,
  list: List[String]
)

sealed class StaticCollectionTableTest extends CassandraTable[ConcreteStaticCollectionTableTest, StaticCollectionRecord] {

  object id extends UUIDColumn(this) with PartitionKey[UUID]

  object clusteringId extends UUIDColumn(this) with PrimaryKey[UUID] with ClusteringOrder[UUID] with Descending
  object staticList extends ListColumn[ConcreteStaticCollectionTableTest, StaticCollectionRecord, String](this) with StaticColumn[List[String]]

  def fromRow(row: Row): StaticCollectionRecord = {
    StaticCollectionRecord(
      id = id(row),
      clustering = clusteringId(row),
      list = staticList(row)
    )
  }
}

abstract class ConcreteStaticCollectionTableTest extends StaticCollectionTableTest with RootConnector {
  def store(record: StaticCollectionRecord): InsertQuery.Default[ConcreteStaticCollectionTableTest, StaticCollectionRecord] = {
    insert.value(_.id, record.id)
      .value(_.clusteringId, record.clustering)
      .value(_.staticList, record.list)
  }
}
