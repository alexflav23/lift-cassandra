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
package com.websudos.phantom.dsl

import org.scalatest.{FlatSpec, Matchers}

trait Test {
  private[this] lazy val _name: String = {
    val packagePath = getClass.getName.split("\\.")
    val fullName = packagePath(packagePath.length - 1)


    val index = fullName.indexOf("$$anonfun")

    if (index != -1) {
      val str = fullName.substring(index + 9, fullName.length)
      str.replaceAll("[(\\$\\d+\\$)]", "")
    } else {
      fullName.replaceAll("[(\\$\\d+\\$)]", "")
    }
  }

  def name: String = _name
}


case class CustomRecord(name: String, mp: Map[String, String])

class TestTableNames extends CassandraTable[TestTableNames, CustomRecord] {
  object record extends StringColumn(this) with PartitionKey[String]
  object sampleLongTextColumnDefinition extends MapColumn[TestTableNames, CustomRecord, String, String](this)

  override def fromRow(r: Row): CustomRecord = {
    CustomRecord(record(r), sampleLongTextColumnDefinition(r))
  }
}

object TestTableNames extends TestTableNames

object Test extends PrimitiveColumn[TestTableNames, CustomRecord, String](TestTableNames)


class TestNames {

  private[this] lazy val _name: String = {
    val packagePath = getClass.getName.split("\\.")
    val fullName = packagePath(packagePath.length - 1)

    val index = fullName.indexOf("$$anonfun")
    val str = fullName.substring(index + 9, fullName.length)
    str.replaceAll("(\\$\\d+\\$)", "")
  }
  def name: String = _name
}

class Parent extends TestNames
class Parent2 extends Parent

class ClassNameExtraction extends FlatSpec with Matchers {

  it should "get rid of extra naming inside the object" in {
    val test = "$$anonfun23primitives3key$"
    val res = test.replaceAll("\\$+", "").replaceAll("(anonfun\\d+.+\\d+)", "")
    res shouldEqual "key"
  }

  it should "correctly name objects inside record classes " in {
    TestTableNames.record.name shouldEqual "record"
  }

  it should "correctly extract long object name definitions in nested record classes" in {
    TestTableNames.sampleLongTextColumnDefinition.name shouldEqual "sampleLongTextColumnDefinition"
  }

  it should "correctly name Cassandra Tables" in {
    TestTableNames.tableName shouldEqual "TestTableNames"
  }

  it should "correctly extract the object name " in {
    Test.name shouldEqual "Test"
  }

  it should "correctly extract the table name" in {
    object TestNames extends TestNames
    TestNames.name shouldEqual "TestNames"
  }

  it should "correctly extract the parent name" in {
    object Parent extends Parent
    Parent.name shouldEqual "Parent"
  }

  it should "correctly extract the column names" in {
    object Parent2 extends Parent2
    Parent2.name shouldEqual "Parent2"
  }
}
