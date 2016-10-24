/*
 * Copyright 2013-2017 Outworkers, Limited.
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
 * - Explicit consent must be obtained from the copyright owner, Outworkers Limited before any redistribution is made.
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
package com.outworkers.phantom.builder.query.db.crud

import com.outworkers.phantom.PhantomSuite
import com.outworkers.phantom.builder.query.ExecutableStatementList
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.tables._
import com.outworkers.util.testing._

class InsertCasTest extends PhantomSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    TestDatabase.primitives.insertSchema()
    TestDatabase.primitives.truncate().future().block(defaultScalaTimeout)
    TestDatabase.testTable.insertSchema()
    TestDatabase.recipes.insertSchema()
  }

  "Standard inserts" should "not create multiple database entries and perform upserts instead" in {
    val row = gen[Primitive]

    val insertion = new ExecutableStatementList(
      List(
        TestDatabase.primitives.store(row).ifNotExists().qb,
        TestDatabase.primitives.store(row).ifNotExists().qb,
        TestDatabase.primitives.store(row).ifNotExists().qb,
        TestDatabase.primitives.store(row).ifNotExists().qb,
        TestDatabase.primitives.store(row).ifNotExists().qb
      )
    )

    val chain = for {
      truncate <- TestDatabase.primitives.truncate.future()
      store <- insertion.future()
      one <- TestDatabase.primitives.select.where(_.pkey eqs row.pkey).one
      multi <- TestDatabase.primitives.select.where(_.pkey eqs row.pkey).fetch()
    } yield (one, multi)

    whenReady(chain) {
      case (res1, res3) => {
        info("The one query should return a record")
        res1 shouldBe defined

        info("And the record should equal the inserted record")
        res1.value shouldEqual row

        info("And only one record should be retrieved from a range fetch")
        res3 should have size 1
      }
    }
  }


  "Conditional inserts" should "not create duplicate database entries" in {
    val row = gen[Primitive]

    val insertion = new ExecutableStatementList(
      List(
        TestDatabase.primitives.store(row).ifNotExists().qb,
        TestDatabase.primitives.store(row).ifNotExists().qb,
        TestDatabase.primitives.store(row).ifNotExists().qb,
        TestDatabase.primitives.store(row).ifNotExists().qb,
        TestDatabase.primitives.store(row).ifNotExists().qb
      )
    )

    val chain = for {
      truncate <- TestDatabase.primitives.truncate.future()
      store <- insertion.future()
      one <- TestDatabase.primitives.select.where(_.pkey eqs row.pkey).one
      multi <- TestDatabase.primitives.select.where(_.pkey eqs row.pkey).fetch()
    } yield (one, multi)

    whenReady(chain) {
      case (res1, res3) => {
        info("The one query should return a record")
        res1 shouldBe defined

        info("And the record should equal the inserted record")
        res1.value shouldEqual row

        info("And only one record should be retrieved from a range fetch")
        res3 should have size 1
      }
    }
  }
}
