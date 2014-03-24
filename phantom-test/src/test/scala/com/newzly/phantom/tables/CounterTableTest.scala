package com.newzly.phantom.tables

import java.util.UUID
import com.datastax.driver.core.Row
import com.datastax.driver.core.utils.UUIDs
import com.newzly.phantom.Implicits._
import com.newzly.phantom.helper.{TestSampler, ModelSampler, Sampler}

case class CounterRecord(id: UUID, count: Long)

object CounterRecord extends ModelSampler[CounterRecord] {
  def sample: CounterRecord = CounterRecord(
    UUIDs.timeBased,
    Sampler.getARandomInteger().toLong
  )
}


class CounterTableTest extends CassandraTable[CounterTableTest, CounterRecord] {

  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object count_entries extends CounterColumn(this)

  def fromRow(row: Row): CounterRecord = {
    CounterRecord(id(row), count_entries(row))
  }
}

object CounterTableTest extends CounterTableTest with TestSampler[CounterTableTest, CounterRecord] {
  override val tableName = "counter_column_tests"
}