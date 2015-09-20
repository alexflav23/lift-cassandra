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
package com.websudos.phantom.builder.query.db.crud

import com.websudos.phantom.dsl._
import com.websudos.phantom.tables.{Recipe, SampleEvent, TestDatabase}
import com.websudos.phantom.testkit._
import com.websudos.util.testing._
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.time.SpanSugar._

class MapOperationsTest extends PhantomCassandraTestSuite {

  implicit val s: PatienceConfiguration.Timeout = timeout(10 seconds)

  override def beforeAll(): Unit = {
    super.beforeAll()
    TestDatabase.recipes.insertSchema()
    TestDatabase.events.insertSchema()
  }

  it should "support a single item map put operation" in {
    val recipe = gen[Recipe]
    val item = gen[String, String]

    val operation = for {
      insertDone <- TestDatabase.recipes.store(recipe).future()
      update <- TestDatabase.recipes.update.where(_.url eqs recipe.url).modify(_.props put item).future()
      select <- TestDatabase.recipes.select(_.props).where(_.url eqs recipe.url).one
    } yield {
      select
    }

    operation.successful {
      items => {
        items.value shouldEqual recipe.props + item
      }
    }
  }

  it should "support a single item map put operation with Twitter futures" in {
    val recipe = gen[Recipe]
    val item = gen[String, String]

    val operation = for {
      insertDone <- TestDatabase.recipes.store(recipe).execute()
      update <- TestDatabase.recipes.update.where(_.url eqs recipe.url).modify(_.props put item).execute()
      select <- TestDatabase.recipes.select(_.props).where(_.url eqs recipe.url).get
    } yield select

    operation.successful {
      items => {
        items.value shouldEqual recipe.props + item
      }
    }
  }

  it should "support a multiple item map put operation" in {
    val recipe = gen[Recipe]
    val mapItems = genMap[String, String](5)

    val operation = for {
      insertDone <- TestDatabase.recipes.store(recipe).future()
      update <- TestDatabase.recipes.update.where(_.url eqs recipe.url).modify(_.props putAll mapItems).future()
      select <- TestDatabase.recipes.select(_.props).where(_.url eqs recipe.url).one
    } yield select

    operation.successful {
      items => {
        items.value shouldEqual recipe.props ++ mapItems
      }
    }
  }

  it should "support a multiple item map put operation with Twitter futures" in {
    val recipe = gen[Recipe]
    val mapItems = genMap[String, String](5)

    val operation = for {
      insertDone <- TestDatabase.recipes.store(recipe).execute()
      update <- TestDatabase.recipes.update.where(_.url eqs recipe.url).modify(_.props putAll mapItems).execute()
      select <- TestDatabase.recipes.select(_.props).where(_.url eqs recipe.url).get
    } yield select

    operation.successful {
      items => {
        items.value shouldEqual recipe.props ++ mapItems
      }
    }
  }

  it should "support maps of nested primitives" in {
    val event = gen[SampleEvent]

    val chain = for {
      store <- TestDatabase.events.store(event).future()
      get <- TestDatabase.events.getById(event.id).one()
    } yield get

    chain.successful {
      res => {
        res.value shouldEqual event
      }
    }

  }
}
