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
package com.outworkers.phantom.database

import com.outworkers.phantom.connectors.ContactPoint
import com.outworkers.phantom.tables._

private[this] object DefaultKeyspace {
  lazy val local = ContactPoint.local.keySpace("phantom")
}

class TestDatabase extends Database[TestDatabase](DefaultKeyspace.local) {
  object enumTable extends EnumTable with Connector
  object basicTable extends BasicTable with Connector
  object jsonTable extends JsonTable with Connector
  object recipes extends Recipes with Connector
}

object TestDatabase extends TestDatabase

/*
class ValueInitDatabase extends Database[ValueInitDatabase](DefaultKeyspace.local) {
  val basicTable = new BasicTable with connector.Connector
  val enumTable = new EnumTable with connector.Connector
  val jsonTable = new JsonTable with connector.Connector
  val recipes = new Recipes with connector.Connector
}*/