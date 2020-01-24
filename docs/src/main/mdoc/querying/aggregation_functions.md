| CI  | Test coverage(%) | Code quality | Stable version | ScalaDoc | Chat | Open issues | Average issue resolution time | 
| --- | ---------------- | -------------| -------------- | -------- | ---- | ----------- | ----------------------------- |
| [![Build Status](https://travis-ci.org/outworkers/phantom.svg?branch=develop)](https://travis-ci.org/outworkers/phantom?branch=develop) | [![Coverage Status](https://coveralls.io/repos/github/outworkers/phantom/badge.svg?branch=develop)](https://coveralls.io/github/outworkers/phantom?branch=develop) | [![Codacy Rating](https://api.codacy.com/project/badge/grade/25bee222a7d142ff8151e6ceb39151b4)](https://www.codacy.com/app/flavian/phantom_2) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.outworkers/phantom-dsl_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.outworkers/phantom-dsl_2.11) | [![ScalaDoc](http://javadoc-badge.appspot.com/com.outworkers/phantom-dsl_2.11.svg?label=scaladoc)](http://javadoc-badge.appspot.com/com.outworkers/phantom-dsl_2.11) | [![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/outworkers/phantom?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) | [![Percentage of issues still open](http://isitmaintained.com/badge/open/outworkers/phantom.svg)](http://isitmaintained.com/project/outworkers/phantom "%% of issues still open") | [![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/outworkers/phantom.svg)](http://isitmaintained.com/project/outworkers/phantom "Average time to resolve an issue") |

#### Table of contents

- [Aggregation functions](#aggregation-functions)
- [How to use aggregation functions](#how-to-use-aggregation-functions)
- [Generic operators](#non-specialized-operators)
    - [writetime operator](#using-the-`writetime`-operator)
    - [ttl operator](#using-the-`ttl`-operator)
- [UUID and TimeUUID specific operators](#uuid-and-timeuuid-specific-functions.)
    - [dateOf operator](#using-the-`dateof`-operator)
    - [unixTimestamp operator](#using-the-`unixtimestamp`-operator)
    - [minTimeuuid and maxTimeuuid](#using-`mintimeuuid`-and-`maxtimeuuid`-operators.)
    
####  Aggregation functions

Cassandra supports a set of native aggregation functions. To explore them in more detail, have a look
at [this tutorial](http://christopher-batey.blogspot.co.uk/2015/05/cassandra-aggregates-min-max-avg-group.html).

It's important to note aggregation functions rely on `scala.Numeric`. We use this to transparently
handle multiple numeric types as possible returns. Phantom supports the following aggregation operators.

The `T` below means the return type will depend on the type of the column you call the operator on.
The average of a `Float` column will come back as `scala.Float` and so on.


| Scala operator     | Cassandra operator   | Return type           |
| --------------     | -------------------- | --------------------- |
| `sum[T : Numeric]` | SUM                  | `Option[T : Numeric]` |
| `min[T : Numeric]` | MIN                  | `Option[T : Numeric]` |
| `max[T : Numeric]` | MAX                  | `Option[T : Numeric]` |
| `avg[T : Numeric]` | AVG                  | `Option[T : Numeric]` |
| `count`            | COUNT                | `Option[scala.Long]`  |
| `count(colName)`   | COUNT                | `Option[scala.Long]`  |

To take advantage of these operators, simply use the default import, combined with the `function` argument
and the `aggregate` function. A few examples are found in [SelectFunctionsTesting.scala](/com/outworkers/phantom/builder/query/db/specialized/SelectFunctionsTesting.scala#L99).


#### How to use aggregation functions

Let's take a look at how to leverage this functionality using phantom. First, we'll need to define the usual requirements,
a connector, the schema and record for our table, and a database.


```tut:silent

import scala.concurrent.Future
import com.datastax.driver.core.SocketOptions
import com.outworkers.phantom.dsl._

object Connector {
  val default: CassandraConnection = ContactPoint.local
    .withClusterBuilder(_.withSocketOptions(
      new SocketOptions()
        .setConnectTimeoutMillis(20000)
        .setReadTimeoutMillis(20000)
      )
    ).noHeartbeat().keySpace(
      KeySpace("phantom").ifNotExists().`with`(
        replication eqs SimpleStrategy.replication_factor(1)
      )
    )
}


case class PrimitiveRecord(
  pkey: String,
  long: Long,
  boolean: Boolean,
  bDecimal: BigDecimal,
  double: Double,
  float: Float,
  inet: java.net.InetAddress,
  int: Int,
  date: java.util.Date,
  uuid: java.util.UUID,
  bi: BigInt,
  ascii: AsciiValue
)

abstract class PrimitivesTable extends Table[PrimitivesTable, PrimitiveRecord] {
  object pkey extends StringColumn with PartitionKey

  object long extends LongColumn

  object boolean extends BooleanColumn

  object bDecimal extends BigDecimalColumn

  object double extends DoubleColumn

  object float extends FloatColumn

  object inet extends InetAddressColumn

  object int extends IntColumn

  object date extends DateColumn

  object uuid extends UUIDColumn

  object bi extends BigIntColumn

  object ascii extends AsciiColumn
}

case class TimeUUIDRecord(
  user: UUID,
  id: UUID,
  name: String
) {
  def timestamp: DateTime = id.datetime
}

abstract class TimeUUIDTable extends Table[TimeUUIDTable, TimeUUIDRecord] {

  object user extends UUIDColumn with PartitionKey
  object id extends TimeUUIDColumn with ClusteringOrder with Descending
  object name extends StringColumn

  def retrieve(user: UUID): Future[List[TimeUUIDRecord]] = {
    select.where(_.user eqs user).orderBy(_.id ascending).fetch()
  }

  def retrieveDescending(user: UUID): Future[List[TimeUUIDRecord]] = {
    select.where(_.user eqs user).orderBy(_.id descending).fetch()
  }
}


class BasicDatabase(
    override val connector: CassandraConnection
) extends Database[BasicDatabase](connector) {
  object primitives extends PrimitivesTable with Connector
  object timeuuidTable extends TimeUUIDTable with Connector
}

object db extends BasicDatabase(Connector.default)

```


#### Non specialized operators

The first set of operators described in this document can be used against all columns. Specialized operators,
such as UUID and timeuuid specific ones, are described further down.

##### Using the `writetime` operator

This operator is user to determine the UTC timestamp of a write, e.g the timestamp at which
the record was written to Cassandra. More details [here](https://docs.datastax.com/en/cql/3.3/cql/cql_using/useWritetime.html).

```tut:silent

import java.util.UUID
import scala.concurrent.Future


trait WriteTimeExamples extends db.Connector {

    def findWritetime(record: TimeUUIDRecord): Future[Option[Long]] = {
        db.timeuuidTable.select
            .function(t => writetime(t.user))
            .where(_.id eqs record.id)
            .aggregate()
    }
}
```

##### Using the `ttl` operator

This will only return a value if there is a `ttl` set on the respective column.

```tut:silent
trait TTLExamples extends db.Connector {

    def findTTL(record: TimeUUIDRecord): Future[Option[Int]] = {
      db.timeuuidTable.select.function(t => ttl(t.name))
        .where(_.user eqs record.user)
        .and(_.id eqs record.id)
        .aggregate().map(_.flatten)
    }
}
```

#### UUID and TimeUUID specific functions.

Cassandra offers timeseries support as first class citizen support, with a select of operators
that offer specialised functionality for UUID and TimeUUID columns. For the full list of details, check the [official docs](https://docs.datastax.com/en/cql/3.3/cql/cql_reference/timeuuid_functions_r.html).


##### Using the `dateOf` operator

It's important to remember this operator is specifically designed to work only with a `TimeUUID` column,
and it will return an error if you attempt to use it with anything else.


```tut:silent

trait DateOfExamples extends db.Connector {
    
    def findDateOf(record: TimeUUIDRecord): Future[Option[DateTime]] = {
        db.timeuuidTable.select
            .function(t => dateOf(t.id))
            .where(_.user eqs record.user)
            .aggregate().map(_.flatten)
    }
}
```

##### Using the `unixTimestamp` operator

Just like `dateOf`, this operator will only work with `uuid` and `timeuuid` columns.

```tut:silent

import java.util.UUID
import scala.concurrent.Future

trait UnixTimestampExamples extends db.Connector {
    
    def findUnixTimestampOf(record: TimeUUIDRecord): Future[Option[Long]] = {
        db.timeuuidTable
            .select
            .function(t => unixTimestampOf(t.id))
            .where(_.user eqs record.user)
            .and(_.id eqs record.id)
            .aggregate().map(_.flatten)
    }
}
```


##### Using `minTimeuuid` and `maxTimeuuid` operators.

These two operators exist to provide a time range query capability directly using the partition
key of a given table. This allows to define both uniqueness requirements and timestamps in a single timeuuid value.

E.g Cassandra will store both an id and timestamp for a record in the same field, so it greatly simplifies
our storage and query models, just by using a `timeuuid` column.


```tut:silent

import org.joda.time.DateTime

trait TimeUUIDRangeExamples extends db.Connector {
    
    // Here we retrieve all records between start and end just by using the partition key column.
    def getInterval(start: DateTime, end: DateTime): Future[List[TimeUUIDRecord]] = {
        db.timeuuidTable.select
            .where(_.id >= minTimeuuid(start))
            .and(_.id <= maxTimeuuid(end))
            .fetch()
    }
}
```


#### Using multiple aggregation operators in a single query

Phantom offers the `~` operator, which allows queries to retrieve the values of multiple operators
at the same time, and a `multiAggregate` DB action to provide a cleaner return type.

```tut:silent
trait MultiAggregates extends db.Connector {

   def averageAndMax: Future[Option[(Option[Long], Option[Long])]] = {
       db.primitives.select
            .function(t => avg(t.long) ~ max(t.long))
            .multiAggregate()
   }

 
}
```
