package com.newzly.phantom.dsl.query

import org.scalatest.{ FlatSpec, Matchers }
import com.datastax.driver.core.utils.UUIDs
import com.newzly.phantom.Implicits._
import com.newzly.phantom.tables.{ CounterTableTest, Primitives }

class QueryRestrictionsTest extends FlatSpec with Matchers {
  it should "not allow using the eqs operator on non index columns" in {
    "Primitives.select.where(_.long eqs 5L).one()" shouldNot compile
  }

  it should "not allow using the lt operator on non index columns" in {
    "Primitives.select.where(_.long lt 5L).one()" shouldNot compile
  }

  it should "not allow using the gt operator on non index columns" in {
    "Primitives.select.where(_.long gt 5L).one()" shouldNot compile
  }

  it should "not allow using the in operator on non index columns" in {
    "Primitives.select.where(_.long in List(5L, 6L)).one()" shouldNot compile
  }

  ignore should "not allow using the setTo operator on a Counter column" in {
    "CounterTableTest.update.where(_.id eqs UUIDs.timeBased()).modify(_.count_entries setTo 5L)" shouldNot compile
  }
}
