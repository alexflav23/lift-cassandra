package com.websudos.phantom.builder.query

import com.datastax.driver.core.{ResultSet, Session}
import com.twitter.util.{ Future => TwitterFuture }
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.builder._
import com.websudos.phantom.column.AbstractColumn
import com.websudos.phantom.connectors.KeySpace
import org.joda.time.DateTime

import scala.concurrent.{ Future => ScalaFuture }

class InsertQuery[
  Table <: CassandraTable[Table, _],
  Record,
  Status <: ConsistencyBound
](table: Table, val qb: CQLQuery, clauses: List[(String, String)] = Nil, added: Boolean = false) extends ExecutableStatement with Batchable {

  final def value[RR](col: Table => AbstractColumn[RR], value: RR) : InsertQuery[Table, Record, Status] = {
    new InsertQuery(table, qb, (col(table).name, col(table).asCql(value)) :: clauses, added)
  }

  final def valueOrNull[RR](col: Table => AbstractColumn[RR], value: RR) : InsertQuery[Table, Record, Status] = {
    val insertValue = if (value != null) col(table).asCql(value) else null.asInstanceOf[String]

    new InsertQuery(table, qb, (col(table).name, insertValue) :: clauses, added)
  }

  private def terminate: InsertQuery[Table, Record, Status] = {
    if (added) {
      this
    } else {
      new InsertQuery[Table, Record, Status](table, QueryBuilder.insert(qb, QueryBuilder.insertPairs(clauses)), Nil, true).terminate
    }
  }

  override def queryString: String = {
    if (added) {
      qb.queryString
    } else {
      terminate.qb.queryString
    }
  }

  override def future()(implicit session: Session, keySpace: KeySpace): ScalaFuture[ResultSet] = {
    scalaQueryStringExecuteToFuture(queryString)
  }

  override def execute()(implicit session: Session, keySpace: KeySpace): TwitterFuture[ResultSet] = {
    twitterQueryStringExecuteToFuture(queryString)
  }

  def ttl(seconds: Long): InsertQuery[Table, Record, Status] = {
    new InsertQuery(table, QueryBuilder.ttl(terminate.qb, seconds.toString), clauses, true)
  }

  def ttl(seconds: scala.concurrent.duration.FiniteDuration): InsertQuery[Table, Record, Status] = {
    ttl(seconds.toSeconds)
  }

  def ttl(duration: com.twitter.util.Duration): InsertQuery[Table, Record, Status] = {
    ttl(duration.inSeconds)
  }

  final def timestamp(value: Long): InsertQuery[Table, Record, Status] = {
    new InsertQuery(table, QueryBuilder.using(QueryBuilder.timestamp(terminate.qb, value.toString)), clauses, true)
  }

  final def timestamp(value: DateTime): InsertQuery[Table, Record, Status] = {
    timestamp(value.getMillis)
  }

  def ifNotExists(): InsertQuery[Table, Record, Status] = {
    new InsertQuery(table, QueryBuilder.ifNotExists(terminate.qb), clauses, true)
  }

}

object InsertQuery {

  type Default[T <: CassandraTable[T, R], R] = InsertQuery[T, R, Unspecified]

  def apply[T <: CassandraTable[T, R], R](table: T)(implicit keySpace: KeySpace): InsertQuery.Default[T, R] = {
    new InsertQuery[T, R, Unspecified](table, QueryBuilder.insert(QueryBuilder.keyspace(keySpace.name, table.tableName)))
  }
}
