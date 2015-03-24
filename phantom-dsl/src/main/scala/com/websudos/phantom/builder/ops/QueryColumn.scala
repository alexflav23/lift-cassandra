package com.websudos.phantom.builder.ops

import com.websudos.phantom.builder.QueryBuilder
import com.websudos.phantom.builder.primitives.Primitive
import com.websudos.phantom.column.AbstractColumn
import com.websudos.phantom.dsl.PrimaryKey
import com.websudos.phantom.keys.{Index, PartitionKey}

/**
 * A class enforcing columns used in where clauses to be indexed.
 * Using an implicit mechanism, only columns that are indexed can be converted into Indexed columns.
 * This enforces a Cassandra limitation at compile time.
 * It prevents a user from querying and using where operators on a column without any index.
 * @param col The column to cast to an IndexedColumn.
 * @tparam RR The type of the value the column holds.
 */
sealed class QueryColumn[RR : Primitive](val col: AbstractColumn[RR]) {

  private[this] val p = implicitly[Primitive[RR]]

  def eqs(value: RR): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.Where.eqs(col.name, p.asCql(value)))
  }

  def lt(value: RR): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.Where.lt(col.name, p.asCql(value)))
  }

  def < = lt _

  def lte(value: RR): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.Where.lte(col.name, implicitly[Primitive[RR]].asCql(value)))
  }

  def <= = lte _

  def gt(value: RR): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.Where.gt(col.name, p.asCql(value)))
  }

  def > = gt _

  def gte(value: RR): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.Where.gte(col.name, p.asCql(value)))
  }

  def >= = gte _

  def in(values: List[RR]): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.Where.in(col.name, values.map(p.asCql)))
  }
}

private[phantom] trait IndexRestrictions {
  implicit def partitionColumnToIndexedColumn[T : Primitive](col: AbstractColumn[T] with PartitionKey[T]): QueryColumn[T] = new QueryColumn(col)
  implicit def primaryColumnToIndexedColumn[T : Primitive](col: AbstractColumn[T] with PrimaryKey[T]): QueryColumn[T] = new QueryColumn(col)
  implicit def secondaryColumnToIndexedColumn[T : Primitive](col: AbstractColumn[T] with Index[T]): QueryColumn[T] = new QueryColumn(col)

  implicit def orderingColumn[RR](col: AbstractColumn[RR] with PrimaryKey[RR]): OrderingColumn[RR] = new OrderingColumn[RR](col)
}