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

class TruncateTest extends PhantomSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    TestDatabase.articles.insertSchema()
  }

  it should "truncate all records in a table" in {
    val article1 = gen[Article]
    val article2 = gen[Article]
    val article3 = gen[Article]
    val article4 = gen[Article]

    val result = for {
      truncateBefore <- TestDatabase.articles.truncate.future()
      i1 <- TestDatabase.articles.store(article1).future()
      i2 <- TestDatabase.articles.store(article2).future()
      i3 <- TestDatabase.articles.store(article3).future()
      i4 <- TestDatabase.articles.store(article4).future()

      records <- TestDatabase.articles.select.fetch
      truncate <- TestDatabase.articles.truncate.future()
      records1 <- TestDatabase.articles.select.fetch
    } yield (records, records1)


    result successful {
      case (init, updated) => {
        init should have size 4
        info (s"inserted exactly ${init.size} records")

        updated should have size 0
        info (s"got exactly ${updated.size} records")
      }
    }
  }
}
