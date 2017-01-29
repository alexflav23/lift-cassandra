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
package com.outworkers.phantom.builder.syntax

object CQLSyntax {
  val Select = "SELECT"
  val json = "JSON"
  val JSON_EXTRACTOR = "[json]"
  val Where = "WHERE"
  val And = "AND"
  val Or = "OR"
  val On = "ON"
  val IF = "IF"
  val To = "TO"
  val index = "INDEX"

  val ignoreNulls = "IGNORE_NULLS"

  val Update = "UPDATE"
  val Insert = "INSERT"
  val Delete = "DELETE"

  object Alter {
    val Alter = "ALTER"
    val Rename = "RENAME"
    val Add = "ADD"
    val Drop = "DROP"
  }

  val Keys = "keys"
  val Rows = "rows"
  val RowsPerPartition = "rows_per_partition"
  val Entries = "ENTRIES"

  val Describe = "DESCRIBE"
  val truncate = "TRUNCATE"

  val `(` = "("

  val into = "INTO"
  val values = "VALUES"
  val select = "SELECT"
  val distinct = "DISTINCT"

  val create = "CREATE"

  val insert = "INSERT"
  val ifNotExists = "IF NOT EXISTS"
  val ifExists = "IF EXISTS"
  val temporary = "TEMPORARY"

  val where = "WHERE"
  val With = "WITH"
  val update = "UPDATE"
  val alter = "ALTER"
  val Type = "TYPE"
  val allowFiltering = "ALLOW FILTERING"
  val delete = "DELETE"
  val orderBy = "ORDER BY"
  val limit = "LIMIT"
  val and = "AND"
  val isNull = "IS NULL"
  val isNotNull = "IS NOT NULL"
  val or = "OR"
  val set = "SET"
  val from = "FROM"
  val frozen = "FROZEN"
  val table = "TABLE"
  val eqs = "="
  val comma = ","
  val count = "COUNT"
  val `)` = ")"
  val token = "TOKEN"
  val timestamp = "TIMESTAMP"

  val consistency = "CONSISTENCY"

  val using = "USING"
  val static = "STATIC"


  object Collections {
    val list = "list"
    val map = "map"
    val set = "set"
    val tuple = "tuple"
    val frozen = "frozen"
  }

  object Symbols {
    val `*` = "*"
    val `{` = "{"
    val `}` = "}"
    val `[` = "["
    val `]` = "]"

    val dot = "."
    val colon = ":"
    val `;` = ";"
    val `(` = "("
    val `)` = ")"
    val `,` = ","
    val `<` = "<"
    val `>` = ">"
    val eqs = "="
    val + = "+"
    val plus = "+"
    val - = "-"
  }

  object Ordering {
    val asc = "ASC"
    val desc = "DESC"
  }

  object Operators {
    val lt = "<"
    val lte = "<="

    val gt = ">"
    val gte = ">="

    val in = "IN"
    val eqs = "="
    val notEqs = "!="

    val contains = "CONTAINS"
    val containsKey = "CONTAINS KEY"
  }

  object Selection {
    val count = "count"
    val BlobAsText = "blobAsText"
    val DateOf = "dateOf"
    val TTL = "TTL"
    val UnixTimestampOf = "unixTimestampOf"
    val OrderBy = "ORDER BY"
    val MaxTimeUUID = "maxTimeuuid"
    val MinTimeUUID = "minTimeuuid"
    val Writetime = "WRITETIME"
  }

  object Types {
    val Ascii = "ascii"
    val BigInt = "bigint"
    val Blob = "blob"
    val Boolean = "boolean"
    val Counter = "counter"
    val Date = "date"
    val Decimal = "decimal"
    val Double = "double"
    val Long = "long"
    val Float = "float"
    val Inet = "inet"
    val Int = "int"
    val List = "list"
    val Map = "map"
    val Set = "set"
    val SmallInt = "smallint"
    val TinyInt = "tinyint"
    val Timestamp = "timestamp"
    val Tuple = "tuple"
    val Text = "text"
    val Varchar = "varchar"
    val Varint = "varint"
    val UUID = "uuid"
    val TimeUUID = "timeuuid"
  }

  object Batch {
    val apply = "APPLY"
    val begin = "BEGIN"
    val batch = "BATCH"
    val Unlogged = "UNLOGGED"
    val Logged = "LOGGED"
    val Counter = "COUNTER"
  }

  object CreateOptions {
    val comment = "comment"
    val compaction = "compaction"
    val compression = "compression"
    val read_repair_chance = "read_repair_chance"
    val dclocal_read_repair_chance = "dclocal_read_repair_chance"
    val caching = "caching"
    val replicate_on_write = "replicate_on_write"
    val gc_grace_seconds = "gc_grace_seconds"
    val populate_io_cache_on_flush = "populate_io_cache_on_flush"
    val bloom_filter_fp_chance = "bloom_filter_fp_chance"
    val speculative_retry = "speculative_retry"
    val percentile = "percentile"
    val default_time_to_live = "default_time_to_live"
    val ttl = "TTL"
    val clustering_order = "CLUSTERING ORDER BY"
  }

  object CompactionOptions {
    val clz = "class"
    val enabled = "enabled"
    val max_threshold = "max_threshold"
    val min_threshold = "min_threshold"
    val min_sstable_size = "min_sstable_size"
    val sstable_size_in_mb = "sstable_size_in_mb"
    val tombstone_compaction_interval = "tombstone_compaction_interval"
    val tombstone_threshold = "tombstone_threshold"
    val bucket_high = "bucket_high"
    val bucket_low = "bucket_low"
    val cold_reads_to_omit = "cold_reads_to_omit"
    val unchecked_tombstone_compaction = "unchecked_tombstone_compaction"
    val base_time_seconds = "base_time_seconds"
    val max_sstable_age_days = "max_sstable_age_days"
    val timestamp_resolution = "timestamp_resolution"
    val compaction_window_size = "compaction_window_size"
    val compaction_window_unit = "compaction_window_unit"
  }

  object CompactionStrategies {
    val sizeTiered = "SizeTieredCompactionStrategy"
    val dateTiered = "DateTieredCompactionStrategy"
    val leveled = "LeveledCompactionStrategy"
    val timeWindow = "TimeWindowCompactionStrategy"
  }

  object CompressionOptions {
    val chunk_length_kb = "chunk_length_kb"
    val sstable_compression = "sstable_compression"
    val crc_check_chance = "crc_check_chance"
  }

  object CompressionStrategies {
    val DeflateCompressor = "DeflateCompressor"
    val SnappyCompressor = "SnappyCompressor"
    val LZ4Compressor = "LZ4Compressor"
  }

  object CacheStrategies {
    val Caching = "caching"
    val None = "none"
    val KeysOnly = "keys_only"
    val RowsOnly = "rows_only"
    val All = "all"

  }

  object StorageMechanisms {
    val CompactStorage = "COMPACT STORAGE"
  }

}
