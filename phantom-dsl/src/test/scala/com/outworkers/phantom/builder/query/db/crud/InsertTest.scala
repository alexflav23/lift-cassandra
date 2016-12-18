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
package com.outworkers.phantom.builder.query.db.crud

import com.outworkers.phantom.PhantomSuite
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.tables._
import com.outworkers.util.testing._
import org.json4s.Extraction
import org.json4s.native._

class InsertTest extends PhantomSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    TestDatabase.listCollectionTable.insertSchema()
    TestDatabase.primitives.insertSchema()
    if(session.v4orNewer) {
      TestDatabase.primitivesCassandra22.insertSchema()
    }
    TestDatabase.testTable.insertSchema()
    TestDatabase.recipes.insertSchema()
  }

  "Insert" should "work fine for primitives columns" in {
    val row = gen[Primitive]

    val chain = for {
      store <- TestDatabase.primitives.store(row).future()
      one <- TestDatabase.primitives.select.where(_.pkey eqs row.pkey).one
    } yield one

    chain successful {
      res => {
        res shouldBe defined
      }
    }
  }

  if (session.v4orNewer) {
    "Insert" should "work fine for primitives cassandra 2.2 columns" in {
      val row = gen[PrimitiveCassandra22]

      val chain = for {
        store <- TestDatabase.primitivesCassandra22.store(row).future()
        one <- TestDatabase.primitivesCassandra22.select.where(_.pkey eqs row.pkey).one
      } yield one

      chain successful {
        res => {
          res shouldBe defined
        }
      }
    }
  }

  it should "insert strings with single quotes inside them and automatically escape them" in {
    val row = gen[TestRow].copy(key = "test'", mapIntToInt = Map.empty[Int, Int])

    val chain = for {
      store <- TestDatabase.testTable.store(row).future()
      one <- TestDatabase.testTable.select.where(_.key eqs row.key).one
    } yield one

    chain successful {
      res => {
        res.value shouldEqual row
      }
    }
  }

  it should "work fine with List, Set, Map" in {
    val row = gen[TestRow].copy(mapIntToInt = Map.empty)

    val chain = for {
      store <- TestDatabase.testTable.store(row).future()
      one <- TestDatabase.testTable.select.where(_.key eqs row.key).one
    } yield one

    chain successful {
      res => {
        res.value shouldEqual row
      }
    }
  }

  it should "work fine with a mix of collection types in the table definition" in {
    val recipe = gen[Recipe]

    val chain = for {
      store <- TestDatabase.recipes.store(recipe).future()
      get <- TestDatabase.recipes.select.where(_.url eqs recipe.url).one
    } yield get

    chain successful {
      res => {
        res shouldBe defined
        res.value.url shouldEqual recipe.url
        res.value.description shouldEqual recipe.description
        res.value.props shouldEqual recipe.props
        res.value.lastCheckedAt shouldEqual recipe.lastCheckedAt
        res.value.ingredients shouldEqual recipe.ingredients
        res.value.servings shouldEqual recipe.servings
      }
    }
  }

  it should "support serializing/de-serializing empty lists " in {
    val row = gen[MyTestRow].copy(stringlist = List.empty)

    val chain = for {
      store <- TestDatabase.listCollectionTable.store(row).future()
      get <- TestDatabase.listCollectionTable.select.where(_.key eqs row.key).one
    } yield get

    chain successful  {
      res => {
        res.value shouldEqual row
        res.value.stringlist.isEmpty shouldEqual true
      }
    }
  }

  it should "support serializing/de-serializing to List " in {
    val row = gen[MyTestRow]

    val chain = for {
      store <- TestDatabase.listCollectionTable.store(row).future()
      get <- TestDatabase.listCollectionTable.select.where(_.key eqs row.key).one
    } yield get

    chain successful {
      res => res.value shouldEqual row
    }
  }

  it should "serialize a JSON clause as the insert part" in {
    val sample = gen[Recipe]

    val chain = for {
      store <- TestDatabase.recipes.insert.json(compactJson(renderJValue(Extraction.decompose(sample)))).future()
      get <- TestDatabase.recipes.select.where(_.url eqs sample.url).one()
    } yield get

    if (cassandraVersion.value >= Version.`2.2.0`) {
      whenReady(chain) {
        res => {
          res shouldBe defined
          res.value shouldEqual sample
        }
      }
    } else {
      chain.failing[Exception]
    }
  }

}
