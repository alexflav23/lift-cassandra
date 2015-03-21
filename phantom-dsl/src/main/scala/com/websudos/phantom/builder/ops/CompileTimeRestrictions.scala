package com.websudos.phantom.builder.ops

import com.datastax.driver.core.Row
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.builder.QueryBuilder
import com.websudos.phantom.builder.primitives.Primitive
import com.websudos.phantom.column._
import com.websudos.phantom.keys.Key
import shapeless.<:!<

import scala.annotation.implicitNotFound


private[phantom] abstract class AbstractModifyColumn[RR](name: String) {

  def toCType(v: RR): AnyRef

  def asCql(v: RR): String

  def setTo(value: RR): UpdateClause.Condition = new UpdateClause.Condition(QueryBuilder.set(name, asCql(value)))
}


class ModifyColumn[RR](col: AbstractColumn[RR]) extends AbstractModifyColumn[RR](col.name) {

  def toCType(v: RR): AnyRef = col.toCType(v)

  def asCql(v: RR): String = col.asCql(v)
}

sealed abstract class SelectColumn[T](val col: AbstractColumn[_]) {
  def apply(r: Row): T
}



sealed trait ColumnModifiers {
  implicit class ModifyColumnOptional[Owner <: CassandraTable[Owner, Record], Record, RR](col: OptionalColumn[Owner, Record, RR])
    extends AbstractModifyColumn[Option[RR]](col.name) {

    def toCType(v: Option[RR]): AnyRef = col.toCType(v)

    def asCql(v: Option[RR]): String = col.asCql(v)
  }

  class SelectColumnRequired[Owner <: CassandraTable[Owner, Record], Record, T](col: Column[Owner, Record, T]) extends SelectColumn[T](col) {
    def apply(r: Row): T = col.apply(r)
  }

  implicit def columnToSelection[Owner <: CassandraTable[Owner, Record], Record, T](column: Column[Owner, Record, T]): SelectColumnRequired[Owner, Record, T] = new SelectColumnRequired[Owner,
    Record, T](column)

  implicit class SelectColumnOptional[Owner <: CassandraTable[Owner, Record], Record, T](col: OptionalColumn[Owner, Record, T])
    extends SelectColumn[Option[T]](col) {
    def apply(r: Row): Option[T] = col.apply(r)
  }
}


sealed trait CollectionOperators {

  implicit class ListLikeModifyColumn[Owner <: CassandraTable[Owner, Record], Record, RR](col: AbstractListColumn[Owner, Record, RR])
    extends ModifyColumn[List[RR]](col) {

    def prepend(value: RR): UpdateClause.Condition = {
      new UpdateClause.Condition(QueryBuilder.prepend(col.name, col.asCqlValue(value)))
    }

    def prependAll[L](values: L)(implicit ev1: L => Seq[RR]): UpdateClause.Condition = {
      new UpdateClause.Condition(QueryBuilder.prepend(col.name, col.collectionAsCql(values)))
    }

    def append(value: RR): UpdateClause.Condition = {
      new UpdateClause.Condition(QueryBuilder.append(col.name, col.asCqlValue(value)))
    }

    def appendAll[L](values: L)(implicit ev1: L => Seq[RR]): UpdateClause.Condition = {
      new UpdateClause.Condition(QueryBuilder.append(col.name, col.collectionAsCql(values)))
    }

    def discard(value: RR): UpdateClause.Condition = {
      new UpdateClause.Condition(QueryBuilder.discard(col.name, col.asCql(List(value))))
    }

    def discardAll[L](values: L)(implicit ev1: L => List[RR]): UpdateClause.Condition = {
      new UpdateClause.Condition(QueryBuilder.discard(col.name, col.asCql(values)))
    }

    def setIdx(i: Int, value: RR): UpdateClause.Condition = {
      new UpdateClause.Condition(QueryBuilder.setIdX(col.name, i.toString, col.asCqlValue(value)))
    }
  }

  implicit class SetLikeModifyColumn[Owner <: CassandraTable[Owner, Record], Record, RR](col: AbstractSetColumn[Owner, Record, RR])
    extends ModifyColumn[Set[RR]](col) {

    def add(value: RR): UpdateClause.Condition = {
      new UpdateClause.Condition(QueryBuilder.add(col.name, Set(col.asCqlValue(value))))
    }

    def addAll(values: Set[RR]): UpdateClause.Condition = {
      new UpdateClause.Condition(QueryBuilder.add(col.name, values.map(col.asCqlValue)))
    }

    def remove(value: RR): UpdateClause.Condition = {
      new UpdateClause.Condition(QueryBuilder.remove(col.name, Set(col.asCqlValue(value))))
    }

    def removeAll(values: Set[RR]): UpdateClause.Condition = {
      new UpdateClause.Condition(QueryBuilder.remove(col.name, values.map(col.asCqlValue)))
    }
  }

  implicit class MapLikeModifyColumn[Owner <: CassandraTable[Owner, Record], Record, A, B](col: AbstractMapColumn[Owner, Record, A, B])
    extends ModifyColumn[Map[A, B]](col) {

    def put(value: (A, B)): UpdateClause.Condition = QueryBuilder.put(col.name, col.keyToCType(value._1), col.valueToCType(value._2))
    def putAll[L](values: L)(implicit ev1: L => Traversable[(A, B)]): UpdateClause.Condition = QueryBuilder.putAll(col.name, col.valuesToCType(values))
  }
}

/**
 * A class enforcing columns used in where clauses to be indexed.
 * Using an implicit mechanism, only columns that are indexed can be converted into Indexed columns.
 * This enforces a Cassandra limitation at compile time.
 * It prevents a user from querying and using where operators on a column without any index.
 * @param col The column to cast to an IndexedColumn.
 * @tparam RR The type of the value the column holds.
 */
sealed class IndexQueryClauses[RR : Primitive](val col: AbstractColumn[RR]) {

  private[this] val p = implicitly[Primitive[RR]]

  def eqs(value: RR): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.eqs(col.name, p.asCql(value)))
  }

  def lt(value: RR): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.lt(col.name, p.asCql(value)))
  }

  def < = lt _

  def lte(value: RR): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.lte(col.name, implicitly[Primitive[RR]].asCql(value)))
  }

  def <= = lte _

  def gt(value: RR): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.gt(col.name, p.asCql(value)))
  }

  def > = gt _

  def gte(value: RR): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.gte(col.name, p.asCql(value)))
  }

  def >= = gte _

  def in(values: List[RR]): WhereClause.Condition = {
    new WhereClause.Condition(QueryBuilder.in(col.name, values.map(p.asCql)))
  }
}

trait CompileTimeRestrictions extends CollectionOperators with ColumnModifiers {

  @implicitNotFound(msg = "Only indexed columns can be updated to indexed clauses")
  implicit def columnToIndexColumn[RR](col: AbstractColumn[RR])(implicit ev: Primitive[RR], ev2: col.type <:< Key[RR, _]): IndexQueryClauses[RR] = {
    new IndexQueryClauses[RR](col)
  }

  @implicitNotFound(msg = "The value of clustering columns or indexed cannot be updated as per the Cassandra specification")
  implicit final def columnToModifyColumn[RR]
  (col: AbstractColumn[RR])
  (implicit ev: col.type <:!< Key[RR, _], ev2: col.type <:!< CounterRestriction[RR]): ModifyColumn[RR] = {
    new ModifyColumn[RR](col)
  }
}
