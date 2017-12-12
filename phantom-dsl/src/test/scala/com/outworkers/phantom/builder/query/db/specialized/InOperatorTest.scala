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
package com.outworkers.phantom.builder.query.db.specialized

import com.outworkers.phantom.PhantomSuite
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.tables.Recipe
import com.outworkers.util.samplers._
import com.outworkers.phantom.macros.debug.Options.ShowLog
import com.outworkers.phantom.macros.debug.Options.ShowTrees
import shapeless.HNil
import shapeless.ops.hlist.{Reverse, Tupler}

class InOperatorTest extends PhantomSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    database.recipes.createSchema()
  }

  def test[T, Out](

  )(
    implicit ev: Tupler.Aux[(List[String]), T],
    rev: Reverse.Aux[T, Out],
    ax: Out =:= shapeless.::[List[String], HNil]
  ): Unit = {

  }


  it should "find a record with a in operator if the record exists" in {
    val recipe = gen[Recipe]

    val chain = for {
      done <- database.recipes.store(recipe).future()
      select <- database.recipes.select.where(_.url in List(recipe.url, gen[EmailAddress].value)).one()
    } yield select

    whenReady(chain) { res =>
      res.value.url shouldEqual recipe.url
    }
  }

  it should "find a record with a in operator if the record exists using a prepared clause" in {
    val recipe = gen[Recipe]

    val arg = List(recipe.url, gen[EmailAddress].value)

    val chain = for {
      done <- database.recipes.store(recipe).future()
      select <- database.recipes.select.where(_.url in ?).prepareAsync()
      binded <- select.bind(arg).one()
    } yield binded

    whenReady(chain) { res =>
      res.value.url shouldEqual recipe.url
    }
  }

  it should "not find a record with a in operator if the record doesn't exists" in {
    val recipe = gen[Recipe]

    val chain = for {
      done <- database.recipes.store(recipe).future()
      select <- database.recipes.select.where(_.url in List(gen[EmailAddress].value)).one()
    } yield select

    whenReady(chain) { res =>
      res shouldBe empty
    }
  }

}
