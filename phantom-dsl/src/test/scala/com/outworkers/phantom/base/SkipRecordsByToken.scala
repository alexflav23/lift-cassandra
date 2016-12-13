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
package com.outworkers.phantom.base

import com.outworkers.phantom.PhantomSuite
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.tables.{Article, TestDatabase}
import com.outworkers.util.testing._

import scala.concurrent.{Future, blocking}

class SkipRecordsByToken extends PhantomSuite {

  val Articles = TestDatabase.articles

  override def beforeAll(): Unit = {
    blocking {
      super.beforeAll()
      Articles.insertSchema()
    }
  }

  it should "allow skipping records using gtToken" in {
    val article1 = gen[Article]
    val article2 = gen[Article]
    val article3 = gen[Article]
    val article4 = gen[Article]

    val result = for {
      truncate <- Articles.truncate.future()
      i1 <- Articles.insert
        .value(_.name, article1.name).value(_.id, article1.id)
        .value(_.orderId, article1.orderId)
        .future()
      i2 <- Articles.insert
        .value(_.name, article2.name)
        .value(_.id, article2.id)
        .value(_.orderId, article2.orderId)
        .future()
      i3 <- Articles.insert
        .value(_.name, article3.name)
        .value(_.id, article3.id)
        .value(_.orderId, article3.orderId)
        .future()

      i4 <- Articles.insert
        .value(_.name, article4.name)
        .value(_.id, article4.id)
        .value(_.orderId, article4.orderId)
        .future()
      one <- Articles.select.one
      next <- Articles.select.where(_.id gtToken one.value.id).fetch
    } yield next

    result successful {
      r => {
        info (s"got exactly ${r.size} records")
        r.size shouldEqual 3
      }
    }
  }

  it should "allow skipping records using eqsToken" in {
    val articles = genList[Article]()

    val result = for {
      truncate <- Articles.truncate.future()
      store <- Future.sequence(articles.map(Articles.store(_).future()))

      next <- Articles.select.where(_.id eqsToken articles.headOption.value.id).fetch
    } yield next


    result successful {
      r => {
        info (s"got exactly ${r.size} records")
        r.size shouldEqual 1
      }
    }
  }

  ignore should "allow skipping records using gteToken" in {
    val articles = genList[Article]()

    val result = for {
      truncate <- Articles.truncate.future()
      store <- Future.sequence(articles.map(Articles.store(_).future()))

      next <- Articles.select.where(_.id gteToken articles.headOption.value.id).fetch
    } yield next


    result successful {
      r => {
        info (s"got exactly ${r.size} records")
        r.size shouldEqual 3
      }
    }
  }

  ignore should "allow skipping records using ltToken" in {
    val articles = genList[Article]()

    val result = for {
      truncate <- Articles.truncate.future()
      store <- Future.sequence(articles.map(Articles.store(_).future()))

      next <- Articles.select.where(_.id ltToken articles.lastOption.value.id).fetch
    } yield next


    result successful {
      r => {
        info (s"got exactly ${r.size} records")
        r.size shouldEqual (articles.size - 1)
      }
    }
  }

  ignore should "allow skipping records using lteToken" in {
    val articles = genList[Article]()

    val result = for {
      truncate <- Articles.truncate.future()
      store <- Future.sequence(articles.map(Articles.store(_).future()))
      next <- Articles.select.where(_.id lteToken articles.lastOption.value.id).fetch
    } yield next


    result successful {
      r => {
        info (s"got exactly ${r.size} records")
        r.size shouldEqual (articles.size - 1)
      }
    }
  }

}
