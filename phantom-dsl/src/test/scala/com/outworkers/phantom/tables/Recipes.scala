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
import com.outworkers.phantom.builder.query.{CreateQuery, InsertQuery, SelectQuery}
import com.outworkers.phantom.dsl._
import org.joda.time.DateTime

import scala.concurrent.Future

case class Recipe(
  url: String,
  description: Option[String],
  ingredients: List[String],
  servings: Option[Int],
  lastCheckedAt: DateTime,
  props: Map[String, String],
  uid: UUID
)

abstract class Recipes extends CassandraTable[Recipes, Recipe] with RootConnector {

  object url extends StringColumn(this) with PartitionKey

  object description extends OptionalStringColumn(this)

  object ingredients extends ListColumn[String](this)

  object servings extends OptionalIntColumn(this)

  object lastcheckedat extends DateTimeColumn(this)

  object props extends MapColumn[String, String](this)

  object uid extends UUIDColumn(this)
}

case class SampleEvent(id: UUID, map: Map[Long, DateTime])

abstract class Events extends CassandraTable[Events, SampleEvent] with RootConnector {
  object id extends UUIDColumn(this) with PartitionKey
  object map extends MapColumn[Long, DateTime](this)

  def findById(id: UUID): Future[Option[SampleEvent]] = {
    select.where(_.id eqs id).one()
  }
}
