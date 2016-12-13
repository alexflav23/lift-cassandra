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

import com.outworkers.phantom.PhantomSuite
import com.outworkers.phantom.dsl._
import com.outworkers.util.testing._

object db extends TestDatabase

class DatabaseTest extends PhantomSuite {

  it should "instantiate a database and collect references to the tables" in {
    db.tables.size shouldEqual 4
  }

  it should "automatically generate the CQL schema and initialise tables " in {
    db.createAsync().successful {
      res => res.nonEmpty shouldEqual true
    }
  }

  it should "respect any auto-creation options specified for the particular table" in {
    val space = KeySpace("phantom_test")
    val queries = db.autocreate().queries(space).map(_.qb)

    val target = db.recipes.autocreate(space).qb

    queries should contain (target)
  }
}
