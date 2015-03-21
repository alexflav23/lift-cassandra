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
package com.websudos.phantom.builder

import com.websudos.phantom.builder.query.CQLQuery

trait CQLOperator {
  def name: String
}


private[builder] object Utils {
  def join(list: TraversableOnce[String]): CQLQuery = {
    CQLQuery(CQLSyntax.Symbols.`(`).append(list.mkString(", ")).append(CQLSyntax.Symbols.`)`)
  }

  def collection(list: TraversableOnce[String]): CQLQuery = {
    CQLQuery(CQLSyntax.Symbols.`[`).append(list.mkString(", ")).append(CQLSyntax.Symbols.`]`)
  }

  def set(list: Set[String]): CQLQuery = {
    CQLQuery(CQLSyntax.Symbols.`{`).append(list.mkString(", ")).append(CQLSyntax.Symbols.`}`)
  }
}

sealed trait BaseModifiers {
  protected[this] def modifier(column: String, op: String, value: String): CQLQuery = {
    CQLQuery(column).forcePad.append(op).forcePad.append(value)
  }

  protected[this] def modifier(column: String, op: String, value: CQLQuery): CQLQuery = {
    modifier(column, op, value.queryString)
  }

  protected[this] def collectionModifier(left: String, op: String, right: CQLQuery): CQLQuery = {
    CQLQuery(left).forcePad.append(op).forcePad.append(right)
  }

  protected[this] def collectionModifier(left: String, op: String, right: String): CQLQuery = {
    CQLQuery(left).forcePad.append(op).forcePad.append(right)
  }
}


sealed trait IndexModifiers extends BaseModifiers {

  def eqs(column: String, value: String): CQLQuery = {
    modifier(column, CQLSyntax.Operators.eqs, value)
  }

  def ==(column: String, value: String): CQLQuery = {
    modifier(column, CQLSyntax.Operators.eqs, value)
  }

  def lt(column: String, value: String): CQLQuery = {
    modifier(column, CQLSyntax.Operators.lt, value)
  }

  def lte(column: String, value: String): CQLQuery = {
    modifier(column, CQLSyntax.Operators.lte, value)
  }

  def gt(column: String, value: String): CQLQuery = {
    modifier(column, CQLSyntax.Operators.gt, value)
  }

  def gte(column: String, value: String): CQLQuery = {
    modifier(column, CQLSyntax.Operators.gte, value)
  }

  def in(column: String, values: String*): CQLQuery = {
    modifier(column, CQLSyntax.Operators.in, Utils.join(values))
  }

  def in(column: String, values: List[String]): CQLQuery = {
    modifier(column, CQLSyntax.Operators.in, Utils.join(values))
  }

  def fcall(name: String, params: String*): CQLQuery = {
    CQLQuery(name).append(Utils.join(params))
  }

  def token(name: String): String = {
    CQLQuery(CQLSyntax.token).wrap(name).queryString
  }

}


sealed trait CollectionModifiers extends BaseModifiers {

  def prepend(column: String, values: String*): CQLQuery = {
    collectionModifier(Utils.collection(values).queryString, CQLSyntax.Symbols.+, column)
  }

  def append(column: String, values: String*): CQLQuery = {
    collectionModifier(Utils.collection(values).queryString, CQLSyntax.Symbols.+, column)
  }

  def discard(column: String, values: String*): CQLQuery = {
    collectionModifier(Utils.collection(values).queryString, CQLSyntax.Symbols.-, column)
  }

  def add(column: String, values: Set[String]): CQLQuery = {
    collectionModifier(Utils.set(values).queryString, CQLSyntax.Symbols.+, column)
  }

  def remove(column: String, values: Set[String]): CQLQuery = {
    collectionModifier(Utils.set(values).queryString, CQLSyntax.Symbols.-, column)
  }

  def setIdX(column: String, index: String, value: String): CQLQuery = {
    CQLQuery(column).append(CQLSyntax.Symbols.`[`)
      .append(index).append(CQLSyntax.Symbols.`]`)
      .forcePad.append(CQLSyntax.eqs)
      .forcePad.append(value)
  }
}


sealed trait CreateOptionsBuilder {
  protected[this] def quotedValue(qb: CQLQuery, option: String, value: String): CQLQuery = {
    qb.append(CQLSyntax.comma)
      .forcePad.appendSingleQuote(option)
      .forcePad.append(CQLSyntax.Symbols.`:`)
      .forcePad.appendSingleQuote(value)
  }

  protected[this] def simpleValue(qb: CQLQuery, option: String, value: String): CQLQuery = {
    qb.append(CQLSyntax.comma)
      .forcePad.appendSingleQuote(option)
      .forcePad.append(CQLSyntax.Symbols.`:`)
      .forcePad.append(value)
  }
}

sealed trait CompactionQueryBuilder extends CreateOptionsBuilder {

  def min_sstable_size(qb: CQLQuery, size: String): CQLQuery = {
    quotedValue(qb, CQLSyntax.CompactionOptions.min_sstable_size, size)
  }

  def sstable_size_in_mb(qb: CQLQuery, size: String): CQLQuery = {
    quotedValue(qb, CQLSyntax.CompactionOptions.sstable_size_in_mb, size)
  }

  def tombstone_compaction_interval(qb: CQLQuery, size: String): CQLQuery = {
    quotedValue(qb, CQLSyntax.CompactionOptions.tombstone_compaction_interval, size)
  }

  def tombstone_threshold(qb: CQLQuery, size: Double): CQLQuery = {
    simpleValue(qb, CQLSyntax.CompactionOptions.tombstone_threshold, size.toString)
  }

  def bucket_high(qb: CQLQuery, size: Double): CQLQuery = {
    simpleValue(qb, CQLSyntax.CompactionOptions.bucket_high, size.toString)
  }

  def bucket_low(qb: CQLQuery, size: Double): CQLQuery = {
    simpleValue(qb, CQLSyntax.CompactionOptions.bucket_low, size.toString)
  }
}

sealed trait CompressionQueryBuilder extends CreateOptionsBuilder {

  def chunk_length_kb(qb: CQLQuery, size: String): CQLQuery = {
    quotedValue(qb, CQLSyntax.CompressionOptions.chunk_length_kb, size)
  }

  def crc_check_chance(qb: CQLQuery, size: Double): CQLQuery = {
    simpleValue(qb, CQLSyntax.CompressionOptions.crc_check_chance, size.toString)
  }
}


sealed trait CreateTableBuilder extends CompactionQueryBuilder with CompressionQueryBuilder {

  private[this] def tableOption(option: String, value: String): CQLQuery = {
    CQLQuery(option)
      .forcePad.append(CQLSyntax.Symbols.`=`)
      .forcePad.append(value)
  }

  private[this] def tableOption(option: String, value: CQLQuery): CQLQuery = {
    tableOption(option, value.queryString)
  }

  def read_repair_chance(st: String): CQLQuery = {
    tableOption(CQLSyntax.CreateOptions.read_repair_chance, st)
  }

  def dclocal_read_repair_chance(st: String): CQLQuery = {
    tableOption(CQLSyntax.CreateOptions.dclocal_read_repair_chance, st)
  }

  def default_time_to_live(st: String): CQLQuery = {
    tableOption(CQLSyntax.CreateOptions.default_time_to_live, st)
  }

  def gc_grace_seconds(st: String): CQLQuery = {
    tableOption(CQLSyntax.CreateOptions.gc_grace_seconds, st)
  }

  def populate_io_cache_on_flush(st: String): CQLQuery = {
    tableOption(CQLSyntax.CreateOptions.populate_io_cache_on_flush, st)
  }

  def bloom_filter_fp_chance(st: String): CQLQuery = {
    tableOption(CQLSyntax.CreateOptions.bloom_filter_fp_chance, st)
  }

  def replicate_on_write(st: String): CQLQuery = {
    tableOption(CQLSyntax.CreateOptions.replicate_on_write, st)
  }

  def compression(qb: CQLQuery) : CQLQuery = {
    tableOption(CQLSyntax.CreateOptions.compression, qb).pad.appendIfAbsent(CQLSyntax.Symbols.`}`)
  }

  def compaction(qb: CQLQuery) : CQLQuery = {
    tableOption(CQLSyntax.CreateOptions.compaction, qb).pad.appendIfAbsent(CQLSyntax.Symbols.`}`)
  }

  def comment(qb: String): CQLQuery = {
    tableOption(CQLSyntax.CreateOptions.replicate_on_write, CQLQuery.empty.appendSingleQuote(qb))
  }

  def caching(qb: String): CQLQuery = {
    tableOption(CQLSyntax.CreateOptions.caching, CQLQuery.empty.appendSingleQuote(qb))
  }
}


private[phantom] object QueryBuilder extends CompactionQueryBuilder with CompressionQueryBuilder with IndexModifiers with CollectionModifiers {

  val syntax = CQLSyntax

  case object Create extends CreateTableBuilder

  def join(qbs: CQLQuery*): CQLQuery = {
    CQLQuery(qbs.map(_.queryString).mkString(", "))
  }

  private[this] def counterSetter(column: String, op: String, value: String): CQLQuery = {
    CQLQuery(column).forcePad.append(CQLSyntax.Symbols.`=`)
      .forcePad.append(column)
      .forcePad.append(op)
      .forcePad.append(value)
  }

  def increment(column: String, value: String): CQLQuery = {
    counterSetter(column, CQLSyntax.Symbols.+, value)
  }

  def decrement(column: String, value: String): CQLQuery = {
    counterSetter(column, CQLSyntax.Symbols.-, value)
  }

  def truncate(table: String): CQLQuery = {
    CQLQuery(syntax.truncate).forcePad.append(table)
  }

  def set(column: String, value: String): CQLQuery = {
    CQLQuery(column)
      .forcePad.append(CQLSyntax.Symbols.`=`)
      .forcePad.append(value)
  }

  def setTo(column: String, value: String): CQLQuery = {
    CQLQuery(column).forcePad.append(syntax.eqs).forcePad.append(value)
  }

  def set(qb: CQLQuery, clause: CQLQuery): CQLQuery = {
    qb.pad.append(CQLSyntax.set).forcePad.append(clause)
  }

  def andSet(qb: CQLQuery, clause: CQLQuery): CQLQuery = {
    qb.pad.append(CQLSyntax.and).forcePad.append(clause)
  }

  def using(qb: CQLQuery): CQLQuery = {
    qb.pad.append(syntax.using)
  }

  def consistencyLevel(qb: CQLQuery, level: String): CQLQuery = {
    using(qb).pad.append(syntax.consistency).forcePad.append(level)
  }

  def `with`(qb: CQLQuery, clause: CQLQuery): CQLQuery = {
    qb.pad.append(syntax.`with`).pad.append(clause)
  }

  def and(qb: CQLQuery, clause: CQLQuery): CQLQuery = {
    qb.pad.append(syntax.and).forcePad.append(clause)
  }

  def prependKeySpaceIfAbsent(keySpace: String, qb: CQLQuery): CQLQuery = {
    if (qb.queryString.startsWith(keySpace)) {
      qb
    }  else {
      qb.prepend(s"$keySpace.")
    }
  }

  def where(query: CQLQuery, op: CQLOperator, name: String, value: String): CQLQuery = {
    query.pad.append(syntax.where)
      .pad.append(name)
      .pad.append(op.name)
      .forcePad.append(value)
  }

  def where(query: CQLQuery, condition: CQLQuery): CQLQuery = {
    query.pad.append(syntax.where).pad.append(condition)
  }


  def select(tableName: String): CQLQuery = {
    CQLQuery(syntax.select)
      .pad.append("*").forcePad
      .append(syntax.from)
      .forcePad.appendEscape(tableName)
  }

  def select(tableName: String, names: String*): CQLQuery = {
    CQLQuery(syntax.select)
      .pad.append(names)
      .forcePad.append(syntax.from)
      .forcePad.appendEscape(tableName)
  }

  def count(tableName: String, names: String*): CQLQuery = {
    CQLQuery(syntax.select)
      .forcePad.append(syntax.count)
      .pad.wrap(names)
      .forcePad.append(syntax.from)
      .forcePad.appendEscape(tableName)
  }

  def distinct(tableName: String, names: String*): CQLQuery = {
    CQLQuery(syntax.select)
      .forcePad.append(syntax.distinct)
      .pad.append(names)
      .forcePad.append(syntax.from)
      .forcePad.appendEscape(tableName)
  }

  def select(tableName: String, clause: CQLQuery) = {
    CQLQuery(syntax.select)
      .pad.append(clause)
      .pad.append(syntax.from)
      .pad.appendEscape(tableName)
  }

  def update(tableName: String) = {
    CQLQuery(syntax.update)
      .forcePad.append(tableName)
  }

  def alter(tableName: String) = {
    CQLQuery(syntax.alter)
      .forcePad.append(tableName)
  }

  def allowFiltering(qb: CQLQuery): CQLQuery = {
    qb.pad.append(syntax.allowFiltering)
  }

  def limit(qb: CQLQuery, value: Int): CQLQuery = {
    qb.pad.append(syntax.limit)
      .forcePad.append(value.toString)
  }

  def insert(table: String): CQLQuery = {
    CQLQuery(syntax.insert)
      .forcePad.append(syntax.insert)
      .forcePad.append(table)
  }

  def delete(table: String): CQLQuery = {
    CQLQuery(syntax.delete)
      .forcePad.append(table)
  }
}
