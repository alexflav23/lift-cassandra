/*
 * Copyright 2013 - 2019 Outworkers Ltd.
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

import java.util.UUID

import com.outworkers.phantom.PhantomSuite
import com.outworkers.util.samplers._
import com.outworkers.phantom.dsl._

class TableHelperRuntimeTests extends PhantomSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    database.tableTypeTuple.createSchema()
    val _ = database.sessionsByUser.createSchema()
  }

  it should "automatically generate an extractor for a tuple type" in {
    val sample = gen[(UUID, String, String)]
    val (id, _, _) = sample

    val chain = for {
      _ <- database.tableTypeTuple.store(sample).future()
      find <- database.tableTypeTuple.findById(id)
    } yield find

    whenReady(chain) { res =>
      res shouldBe defined
      res.value shouldEqual sample
    }
  }

  it should "automatically generate a store type for an OAuth2Session domain case class" in {
    val sample = gen[OAuth2Session]

    val chain = for {
      _ <- database.sessionsByUser.store(sample).future()
      find <- database.sessionsByUser.select.where(_.user_id eqs sample.user_id).one()
    } yield find

    whenReady(chain) { res =>
      res shouldBe defined
      res.value shouldEqual sample
    }
  }

}
