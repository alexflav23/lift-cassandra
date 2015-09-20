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
package com.websudos.phantom

import com.websudos.phantom.tables._
import org.scalatest.{FlatSpec, Matchers}

class BasicTableMethods extends FlatSpec with Matchers {

  it should "retrieve the correct number of columns in a simple table" in {
    TestDatabase.basicTable.columns.length shouldEqual 4
  }

  it should "retrieve the correct number of columns in a big table" in {
    TestDatabase.complexCompoundKeyTable.columns.length shouldEqual 10
  }

  it should "retrieve the correct number of primary keys for a table" in {
    TestDatabase.simpleCompoundKeyTable.primaryKeys.length shouldEqual 2
    TestDatabase.simpleCompoundKeyTable.partitionKeys.length shouldEqual 1
  }

  it should "retrieve the correct number of clustering keys for a table" in {
    TestDatabase.clusteringTable.clusteringColumns.length shouldEqual 2
  }

  it should "create the correct CLUSTERING_ORDER key for a 3 part clustering key" in {
    val key = TestDatabase.clusteringTable.clusteringKey
    key shouldEqual "WITH CLUSTERING ORDER BY (id2 ASC, id3 DESC)"
  }

  it should "create the correct CLUSTERING_ORDER key for a 2 part clustering key" in {
    val key = TestDatabase.complexClusteringTable.clusteringKey
    key shouldEqual "WITH CLUSTERING ORDER BY (id2 ASC, id3 DESC, placeholder DESC)"
  }
}
