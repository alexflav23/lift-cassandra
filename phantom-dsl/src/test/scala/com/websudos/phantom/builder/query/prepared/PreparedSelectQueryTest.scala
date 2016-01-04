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
package com.websudos.phantom.builder.query.prepared

import com.websudos.phantom.PhantomSuite
import com.websudos.phantom.dsl._
import com.websudos.phantom.tables._
import com.websudos.util.testing._

class PreparedSelectQueryTest extends PhantomSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    TestDatabase.recipes.insertSchema()
    TestDatabase.articlesByAuthor.insertSchema()
  }

  it should "serialise and execute a prepared select statement with the correct number of arguments" in {
    val recipe = gen[Recipe]

    val query = TestDatabase.recipes.select.p_where(_.url eqs ?).prepare()

    val operation = for {
      truncate <- TestDatabase.recipes.truncate.future
      insertDone <- TestDatabase.recipes.store(recipe).future()
      select <- query.bind(recipe.url).one()
    } yield select

    operation.successful {
      items => {
        items shouldBe defined
        items.value shouldEqual recipe
      }
    }
  }

  it should "serialise and execute a prepared statement with 2 arguments" in {
    val sample = gen[Article]
    val sample2 = gen[Article]
    val owner = gen[UUID]
    val category = gen[UUID]
    val category2 = gen[UUID]

    val query = TestDatabase.articlesByAuthor.select.p_where(_.author_id eqs ?).p_and(_.category eqs ?).prepare()

    val op = for {
      store <- TestDatabase.articlesByAuthor.store(owner, category, sample).future()
      store2 <- TestDatabase.articlesByAuthor.store(owner, category2, sample2).future()
      get <- query.bind(owner, category).one()
      get2 <- query.bind(owner, category2).one()
    } yield (get, get2)

    whenReady(op) {
      case (res, res2) => {
        res shouldBe defined
        res.value shouldEqual sample

        res2 shouldBe defined
        res2.value shouldEqual sample2
      }
    }
  }
}
