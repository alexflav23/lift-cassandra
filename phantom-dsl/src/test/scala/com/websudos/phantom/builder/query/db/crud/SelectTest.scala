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
package com.websudos.phantom.builder.query.db.crud

import com.websudos.phantom.PhantomSuite
import com.websudos.phantom.dsl._
import com.websudos.phantom.tables._
import com.outworkers.util.testing._
import net.liftweb.http.js.JsObj
import net.liftweb.json.JsonParser

class SelectTest extends PhantomSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    TestDatabase.primitives.insertSchema()
  }

  "Selecting the whole row" should "work fine" in {
    val row = gen[Primitive]

    val chain = for {
      store <- TestDatabase.primitives.store(row).future()
      b <- TestDatabase.primitives.select.where(_.pkey eqs row.pkey).one
    } yield b

    chain successful {
      res => res.value shouldEqual row
    }
  }

  "Partial selects" should "select 2 columns" in {
    val row = gen[Primitive]
    val expected = (row.pkey, row.long)

    val chain = for {
      store <- TestDatabase.primitives.store(row).future
      get <- TestDatabase.primitives.select(_.pkey, _.long).where(_.pkey eqs row.pkey).one()
    } yield get

    chain successful {
      res => res.value shouldEqual expected
    }
  }

  "Partial selects" should "select 3 columns" in {

    val row = gen[Primitive]
    val expected = (row.pkey, row.long, row.boolean)

    val chain = for {
      store <- TestDatabase.primitives.store(row).future()
      get <- TestDatabase.primitives.select(_.pkey, _.long, _.boolean).where(_.pkey eqs row.pkey).one()
    } yield get

    chain successful {
      r => r.value shouldEqual expected
    }
  }

  "Partial selects" should "select 4 columns" in {
    val row = gen[Primitive]
    val expected = (row.pkey, row.long, row.boolean, row.bDecimal)

    val chain = for {
      store <- TestDatabase.primitives.store(row).future()
      get <- TestDatabase.primitives.select(_.pkey, _.long, _.boolean, _.bDecimal).where(_.pkey eqs row.pkey).one()
    } yield get

    chain successful {
      r => r.value shouldBe expected
    }
  }

  "Partial selects" should "select 5 columns" in {
    val row = gen[Primitive]
    val expected = (row.pkey, row.long, row.boolean, row.bDecimal, row.double)

    val chain = for {
      store <- TestDatabase.primitives.store(row).future()
      get <- TestDatabase.primitives.select(_.pkey, _.long, _.boolean, _.bDecimal, _.double).where(_.pkey eqs row.pkey).one()
    } yield get

    chain successful {
      r => r.value shouldBe expected
    }
  }

  "Partial selects" should "select 6 columns" in {
    val row = gen[Primitive]
    val expected = (row.pkey, row.long, row.boolean, row.bDecimal, row.double, row.float)

    val chain = for {
      store <- TestDatabase.primitives.store(row).future()
      get <- TestDatabase.primitives.select(_.pkey, _.long, _.boolean, _.bDecimal, _.double, _.float).where(_.pkey eqs row.pkey).one()
    } yield get

    chain successful {
      r => r.value shouldBe expected
    }
  }

  "Partial selects" should "select 7 columns" in {
    val row = gen[Primitive]
    val expected = (row.pkey, row.long, row.boolean, row.bDecimal, row.double, row.float, row.inet)

    val chain = for {
      store <- TestDatabase.primitives.store(row).future()
      get <- TestDatabase.primitives.select(_.pkey, _.long, _.boolean, _.bDecimal, _.double, _.float, _.inet).where(_.pkey eqs row.pkey).one()
    } yield get

    chain successful {
      r => r.value shouldBe expected
    }
  }

  "Partial selects" should "select 8 columns" in {
    val row = gen[Primitive]
    val expected = (row.pkey, row.long, row.boolean, row.bDecimal, row.double, row.float, row.inet, row.int)

    val chain = for {
      store <- TestDatabase.primitives.store(row).future()
      get <- TestDatabase.primitives.select(_.pkey, _.long, _.boolean, _.bDecimal, _.double, _.float, _.inet, _.int)
        .where(_.pkey eqs row.pkey).one()
    } yield get

    chain successful {
      r => r.value shouldBe expected
    }
  }
}
