phantom [![Build Status](https://travis-ci.org/websudosuk/phantom.svg?branch=develop)](https://travis-ci.org/websudosuk/phantom) [![Coverage Status](https://coveralls.io/repos/websudosuk/phantom/badge.png)](https://coveralls.io/r/websudosuk/phantom)

==============
Asynchronous Scala DSL for Cassandra


Using phantom
=============

The current version is: ```val phantomVersion = 0.8.0```.
Phantom is published to Maven Central and it's actively and avidly developed.

<a id="table-of-contents">Table of contents</a>
===============================================

<ol>
    <li><a href="#issues-and-questions">Issues and questions</a></li>
    <li><a href="#commercial-support">Commercial support</a></li>
    <li><a href="#integrating-phantom-in-your-project">Using phantom in your project</a></li>
    <li>
        <p>phantom columns</p>
        <ul>
            <li><a href="#primitive-columns">Primitive columns</a></li>
            <li><a href="#optional-primitive-columns">Optional primitive columns</a></li>
            <li><a href="#collection-columns">Collection columns</a></li>
            <li>
                <p><a href="#indexing-columns">Indexing columns</a></p>
                <ul>
                    <li><a href="#partition-key">Partition Key</a></li>
                    <li><a href="#primary-key">Primary Key</a></li>
                    <li><a href="#secondary-key">Secondary Index Key</a></li>
                    <li><a href="#clustering-order">Clustering Order Key</a></li>
                </ul>
            </li>
            <li><a href="#thrift-columns">Thrift columns</a></li>
        </ul>
    </li>
    <li><a href="#data-modeling">Data modeling with phantom</a></li>
    <li>
        <p><a href="#querying-with-phantom">Querying with phantom</a></p>
        <ul>
            <li><a href="#select-queries">SELECT queries</a></li>
            <li><a href="#partial-select-queries">Partial SELECT queries</a></li>
            <li><a href="#where-and-operators">WHERE and AND clause operators</a></li>
            <li><a href="#insert-queries">INSERT queries</a></li>
            <li><a href="#update-queries">UPDATE queries</a></li>
            <li><a href="#delete-queries">DELETE queries</a></li>
            <li><a href="#truncate-queries">TRUNCATE queries</a></li>
            <li><a href="#create-queries">CREATE queries</a></li>
            <li><a href="#alter-queries">ALTER queries</a></li>
            <li><a href="#common-query-methods">Common query methods</a></li>
        </ul>
    </li>
    <li>
        <p>Basic query examples</p>
        <ul>
            <li><a href="#scala-futures">Using Scala Futures to query</a></li>
            <li><a href="#scala-futures-examples">Examples with Scala Futures</a></li>
            <li><a href="#twitter-futures">Using Twitter Futures to query</a></li>
            <li><a href="#twitter-futures-examples">Examples with Twitter Futures</a></li>
        </ul>
    </li>
    <li>
        <p><a href="#collections-and-operators">Collection operators</a></p>
        <ul>
            <li><a href="#list-operators">List operators</a></li>
            <li><a href="#set-operators">Set operators</a></li>
            <li><a href="#map-operators">Map operators</a></li>
        </ul>
    </li>
    <li><a href="#automated-schema-generation">Automated schema generation</a></li>
    <li>
        <p>Cassandra indexing</p>
        <ul>
            <li><a href="#partition-tokens">Using partition tokens</a></li>
            <li><a href="#partition-token-operators">Partition token operators</a></li>
            <li><a href="#compound-keys">Compound Keys</a></li>
            <li><a href="#composite-keys">Composite Keys</a></li>
            <li><a href="#time-series">Cassandra Time Series and ClusteringOrder</a></li>
            <li><a href="#secondary-keys">Secondary Keys</a></li>
        </ul>
    </li>
    <li><a href="#async-iterators">Asynchronous iterators</a></li>
    <li>
        <p>Batch statements</p>
        <ul>
            <li><a href="#logged-batch-statements">LOGGED Batch statements</a></li>
            <li><a href="#counter-batch-statements">COUNTER Batch statements</li>
            <li><a href="#<a id="logged-batch-statements">UNLOGGED Batch statements</a></li>
        </ul>
    </li>
    <li><a href="#thrift-integration">Thrift integration</a></li>
    <li><a href="#running-tests">Running the tests locally</a></li>
    <li>
        <p><a href="#contributors">Contributing to phantom</a></p>
        <ul>
            <li><a href="#using-gitflow">Using GitFlow as a branching model</a></li>
            <li><a href="#scala-style-guidelines">Scala style guidelines for contributions</a></li>
            <li>Using the testing utilities to write tests</li>
        </ul>
    <li><a href="#copyright">Copyright</a></li>
</ol>


<a id="issues-and-questions">Issues and questions</a>
=====================================================
<a href="#table-of-contents">back to top</a>

We love Cassandra to bits and use it in every bit our stack. phantom makes it super trivial for Scala users to embrace Cassandra.

Cassandra is highly scalable and it's by far the most powerful database technology available, open source or otherwise.

Phantom is built on top of the [Datastax Java Driver](https://github.com/datastax/java-driver), which does most of the heavy lifting. 

If you're completely new to Cassandra, a much better place to start is the [Datastax Introduction to Cassandra](http://www.datastax.com/documentation/getting_started/doc/getting_started/gettingStartedIntro_r.html)

We are very happy to help implement missing features in phantom, answer questions about phantom, and occasionally help you out with Cassandra questions, although do note we're a bit short staffed!

You can get in touch via the [newzly-phantom](https://groups.google.com/forum/#!forum/newzly-phantom) Google Group.


Commercial support
===================
<a href="#table-of-contents">back to top</a>

We, the people behind phantom run a software development house specialised in Scala, NoSQL and distributed systems. If you are after enterprise grade 
training or support for using phantom, [Websudos](http://www.websudos.co.uk) is here to help!

We offer a comprehensive range of services, including but not limited to:

Training
In house software development
Team building
Architecture planning
Startup product development


We are big fans of open source and we will open source every project we can! To read more about our OSS efforts, 
click [here](http://www.websudos.co.uk/work).


<a id="integrating-phantom">Integrating phantom in your project</a>
===================================================================
<a href="#table-of-contents">back to top</a>

For most things, all you need is ```phantom-dsl```. Read through for information on other modules.

```scala
libraryDependencies ++= Seq(
  "com.websudos"  %% "phantom-dsl"                   % phantomVersion
)
```

The full list of available modules is:

```scala
libraryDependencies ++= Seq(
  "com.websudos"  %% "phantom-dsl"                   % phantomVersion,
  "com.websudos"  %% "phantom-cassandra-unit"        % phantomVersion,
  "com.websudos"  %% "phantom-example"               % phantomVersion,
  "com.websudos"  %% "phantom-thrift"                % phantomVersion,
  "com.websudos"  %% "phantom-test"                  % phantomVersion
)
```

<a id="primitive-columns">Primitive columns</a>
====================================================
<a href="#table-of-contents">back to top</a>

This is the list of available columns and how they map to C* data types.
This also includes the newly introduced ```static``` columns in C* 2.0.6.

The type of a static column can be any of the allowed primitive Cassandra types.
phantom won't let you mixin a non-primitive via implicit magic.

| phantom columns               | Java/Scala type           | Cassandra type    |
| ---------------               |-------------------        | ----------------- |
| BigDecimalColumn              | scala.math.BigDecimal     | decimal           |
| BigIntColumn                  | scala.math.BigInt         | varint            |
| BooleanColumn                 | scala.Boolean             | boolean           |
| DateColumn                    | java.util.Date            | timestamp         |
| DateTimeColumn                | org.joda.time.DateTime    | timestamp         |
| DoubleColumn                  | scala.Double              | double            |
| FloatColumn                   | scala.Float               | float             |
| IntColumn                     | scala.Int                 | int               |
| InetAddressColumn             | java.net.InetAddress      | inet              |
| LongColumn                    | scala.Long                | long              |
| StringColumn                  | java.lang.String          | text              |
| UUIDColumn                    | java.util.UUID            | uuid              |
| TimeUUIDColumn                | java.util.UUID            | timeuuid          |
| CounterColumn                 | scala.Long                | counter           |
| StaticColumn&lt;type&gt;      | &lt;type&gt;              | type static       |


<a id="optional-primitive-columns">Optional primitive columns</a>
===================================================================
<a href="#table-of-contents">back to top</a>

Optional columns allow you to set a column to a ```null``` or a ```None```. Use them when you really want something to be optional.
The outcome is that instead of a ```T``` you get an ```Option[T]``` and you can ```match, fold, flatMap, map``` on a ```None```.

The ```Optional``` part is handled at a DSL level, it's not translated to Cassandra in any way.

| phantom columns               | Java/Scala type                   | Cassandra columns |
| ---------------               | -------------------------         | ----------------- |
| OptionalBigDecimalColumn      | Option[scala.math.BigDecimal]     | decimal           |
| OptionalBigIntColumn          | Option[scala.math.BigInt]         | varint            |
| OptionalBooleanColumn         | Option[scala.Boolean]             | boolean           |
| OptionalDateColumn            | Option[java.util.Date]            | timestamp         |
| OptionalDateTimeColumn        | Option[org.joda.time.DateTime]    | timestamp         |
| OptionalDoubleColumn          | Option[scala.Double]              | double            |
| OptionalFloatColumn           | Option[scala.Float]               | float             |
| OptionalIntColumn             | Option[scala.Int]                 | int               |
| OptionalInetAddressColumn     | Option[java.net.InetAddress]      | inet              |
| OptionalLongColumn            | Option[Long]                      | long              |
| OptionalStringColumn          | Option[java.lang.String]          | text              |
| OptionalUUIDColumn            | Option[java.util.UUID]            | uuid              |
| OptionalTimeUUID              | Option[java.util.UUID]            | timeuuid          |


<a id="collection-columns">Collection columns</a>
======================================================
<a href="#table-of-contents">back to top</a>

Cassandra collections do not allow custom data types. Storing JSON as a string is possible, but it's still a ```text``` column as far as Cassandra is concerned.
The ```type``` in the below example is always a default C* type.

| phantom columns                     | Cassandra columns       |
| ---------------                     | -----------------       |
| ListColumn.&lt;type&gt;             | list&lt;type&gt;        |
| SetColumn.&lt;type&gt;              | set&lt;type&gt;         |
| MapColumn.&lt;type, type&gt;        | map&lt;type, type&gt;   |

<a id="indexing-columns">Indexing columns</a>
==========================================
<a href="#table-of-contents">back to top</a>

phantom uses a specific set of traits to enforce more advanced Cassandra limitations and schema rules at compile time.

<a id="partition-key">PartitionKey[T]</a>
==============================================
<a href="#table-of-contents">back to top</a>

This is the default partitioning key of the table, telling Cassandra how to divide data into partitions and store them accordingly.
You must define at least one partition key for a table. Phantom will gently remind you of this with a fatal error.

If you use a single partition key, the ```PartitionKey``` will always be the first ```PrimaryKey``` in the schema.

It looks like this in CQL: ```PRIMARY_KEY(your_partition_key, primary_key_1, primary_key_2)```.

Using more than one ```PartitionKey[T]``` in your schema definition will output a Composite Key in Cassandra.
```PRIMARY_KEY((your_partition_key_1, your_partition_key2), primary_key_1, primary_key_2)```.


<a id="primary-key">PrimaryKey[T]</a>
==============================================
<a href="#table-of-contents">back to top</a>

As it's name says, using this will mark a column as ```PrimaryKey```. Using multiple values will result in a Compound Value.
The first ```PrimaryKey``` is used to partition data. phantom will force you to always define a ```PartitionKey``` so you don't forget
about how your data is partitioned. We also use this DSL restriction because we hope to do more clever things with it in the future.

A compound key in C* looks like this:
```PRIMARY_KEY(primary_key, primary_key_1, primary_key_2)```.

Before you add too many of these, remember they all have to go into a ```where``` clause.
You can only query with a full primary key, even if it's compound. phantom can't yet give you a compile time error for this, but Cassandra will give you a runtime one.

<a id="secondary-key">Index</a>
==============================================
<a href="#table-of-contents">back to top</a>

This is a SecondaryIndex in Cassandra. It can help you enable querying really fast, but it's not exactly high performance.
It's generally best to avoid it, we implemented it to show off what good guys we are.

When you mix in ```Index[T]``` on a column, phantom will let you use it in a ```where``` clause.
However, don't forget to ```allowFiltering``` for such queries, otherwise C* will give you an error.

<a id="clustering-order">ClusteringOrder</a>
=================================================
<a href="#table-of-contents">back to top</a>

This can be used with either ```java.util.Date``` or ```org.joda.time.DateTime```. It tells Cassandra to store records in a certain order based on this field.

An example might be: ```object timestamp extends DateTimeColumn(this) with ClusteringOrder[DateTime] with Ascending```
To fully define a clustering column, you MUST also mixin either ```Ascending``` or ```Descending``` to indicate the sorting order.



<a id="thrift-columns">Thrift Columns</a>
==========================================
<a href="#table-of-contents">back to top</a>

These columns are especially useful if you are building Thrift services. They are deeply integrated with Twitter Scrooge and relevant to the Twitter ecosystem(Finagle, Zipkin, Storm etc)
They are available via the ```phantom-thrift``` module and you need to ```import com.websudos.phantom.thrift.Implicits._``` to get them.

In the below scenario, the C* type is always text and the type you need to pass to the column is a Thrift struct, specifically ```com.twitter.scrooge.ThriftStruct```.
phantom will use a ```CompactThriftSerializer```, store the record as a binary string and then reparse it on fetch.

Thrift serialization and de-serialization is extremely fast, so you don't need to worry about speed or performance overhead.
You generally use these to store collections(small number of items), not big things.

| phantom columns                     | Cassandra columns       |
| ---------------                     | -----------------       |
| ThriftColumn.&lt;type&gt;           | text                    |
| ThriftListColumn.&lt;type&gt;       | list&lt;text&gt;        |
| ThriftSetColumn.&lt;type&gt;        | set&lt;text&gt;         |
| ThriftMapColumn.&lt;type, type&gt;  | map&lt;text, text&gt;   |



<a id="data-modeling">Data modeling with phantom</a>
====================================================
<a href="#table-of-contents">back to top</a>

```scala

import java.util.{ Date, UUID }
import com.datastax.driver.core.Row
import com.websudos.phantom.sample.ExampleModel
import com.websudos.phantom.Implicits._

case class ExampleModel (
  id: Int,
  name: String,
  props: Map[String, String],
  timestamp: Int,
  test: Option[Int]
)

sealed class ExampleRecord extends CassandraTable[ExampleRecord, ExampleModel] {

  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object timestamp extends DateTimeColumn(this) with ClusteringOrder with Ascending
  object name extends StringColumn(this)
  object props extends MapColumn[ExampleRecord, ExampleModel, String, String](this)
  object test extends OptionalIntColumn(this)

  override def fromRow(row: Row): ExampleModel = {
    ExampleModel(id(row), name(row), props(row), timestamp(row), test(row));
  }
}

```

<a id="querying-with-phantom">Querying with Phantom</a>
=======================================================
<a href="#table-of-contents">back to top</a>

The query syntax is inspired by the Foursquare Rogue library and aims to replicate CQL 3 as much as possible.

Phantom works with both Scala Futures and Twitter Futures as first class citizens.


<a id="select-queries">"Select" queries</a>
================================================
<a href="#table-of-contents">back to top</a>

| Method name                       | Description                                                                           |
| --------------------------------- | ------------------------------------------------------------------------------------- |
| ```where```                       | The ```WHERE``` clause in CQL                                                         |
| ```and```                         | Chains several clauses, creating a ```WHERE ... AND``` query                          |
| ```orderBy```                     | Adds an ```ORDER_BY column_name``` to the query                                       |
| ```allowFiltering```              | Allows Cassandra to filter records in memory. This is an expensive operation.         |
| ```useConsistencyLevel```         | Sets the consistency level to use.                                                    |
| ```setFetchSize       ```         | Sets the maximum number of records to retrieve. Default is 10000                      |
| ```limit```                       | Sets the exact number of records to retrieve.                                         |


Select queries are very straightforward and enforce most limitations at compile time.


<a id="where-and-operators">```where``` and ```and``` clause operators</a>
==========================================
<a href="#table-of-contents">back to top</a>

| Operator name      | Description                                                              |
| ------------------ | ------------------------------------------------------------                                             |
| eqs                | The "equals" operator. Will match if the objects are equal                                               |
| in                 | The "in" operator. Will match if the object is found the list of arguments                               |
| gt                 | The "greater than" operator. Will match a the record is greater than the argument and exists             |
| gte                | The "greater than or equals" operator. Will match a the record is greater than the argument and exists   |
| lt                 | The "lower than" operator. Will match a the record that is less than the argument and exists             |
| lte                | The "lower than or equals" operator. Will match a the record that is less than the argument and exists   |


<a id="partial-select-queries">Partial selects</a>
===================================================
<a href="#table-of-contents">back to top</a>

All partial select queries will return Tuples and are therefore limited to 22 fields.
We haven't yet bothered to add more than 10 fields in the select, but you can always do a Pull Request.
The file you are looking for is [here](https://github.com/newzly/phantom/blob/develop/phantom-dsl/src/main/scala/com/newzly/phantom/SelectTable.scala).
The 22 field limitation will change in Scala 2.11 and phantom will be updated once cross version compilation is enabled.

```scala
  def getNameById(id: UUID): Future[Option[String]] = {
    ExampleRecord.select(_.name).where(_.id eqs someId).one()
  }

  def getNameAndPropsById(id: UUID): Future[Option(String, Map[String, String])] {
    ExampleRecord.select(_.name, _.props).where(_.id eqs someId).one()
  }
```

<a id="insert-queries">"Insert" queries</a>
==========================================
<a href="#table-of-contents">back to top</a>

| Method name                       | Description                                                                           |
| --------------------------------- | ------------------------------------------------------------------------------------- |
| ```value```                       | A type safe Insert query builder. Throws an error for ```null``` values.              |
| ```valueOrNull```                 | This will accept a ```null``` without throwing an error.                              |
| ```useConsistencyLevel```         | Sets the consistency level to use.                                                    |
| ```ttl```                         | Sets the "Time-To-Live" for the record.                                               |


<a id="update-queries">"Update" queries</a>
==========================================
<a href="#table-of-contents">back to top</a>

| Method name                       | Description                                                                           |
| --------------------------------- | ------------------------------------------------------------------------------------- |
| ```where```                       | The ```WHERE``` clause in CQL                                                         |
| ```and```                         | Chains several clauses, creating a ```WHERE ... AND``` query                          |
| ```modify```                      | The actual update query builder                                                       |
| ```useConsistencyLevel```         | Sets the consistency level to use.                                                    |
| ```onflyIf```                     | Addition update condition. Used on non-primary columns                                |


<a id="delete-queries">"Delete" queries</a>
===========================================
<a href="#table-of-contents">back to top</a>

| Method name                       | Description                                                                           |
| --------------------------------- | ------------------------------------------------------------------------------------- |
| ```where```                       | The ```WHERE``` clause in CQL                                                         |
| ```useConsistencyLevel```         | Sets the consistency level to use.                                                    |


<a id="common-query-methods">Common query methods</a>
=====================================================
<a href="#table-of-contents">back to top</a>

The full list can be found in [CQLQuery.scala](https://github.com/websudos/phantom/blob/develop/phantom-dsl/src/main/scala/com/websudos/phantom/query/CQLQuery
.scala).

| Method name                       | Description                                                                           |
| --------------------------------- | ------------------------------------------------------------------------------------- |
| ```tracing_=```                   | The Cassandra utility method. Enables or disables tracing.                            |
| ```queryString```                 | Get the output CQL 3 query of a phantom query.                                        |
| ```consistencyLevel```            | Retrieves the consistency level in use.                                               |
| ```consistencyLevel_=```          | Sets the consistency level to use.                                                    |
| ```retryPolicy```                 | Retrieves the RetryPolicy in use.                                                     |
| ```retryPolicy_=```               | Sets the RetryPolicy to use.                                                          |
| ```serialConsistencyLevel```      | Retrieves the serial consistency level in use.                                        |
| ```serialConsistencyLevel_=```    | Sets the serial consistency level to use.                                             |
| ```forceNoValues_=```             | Sets the serial consistency level to use.                                             |
| ```routingKey```                  | Retrieves the Routing Key as a ByteBuffer.                                            |


<a id="scala-futures">Scala Futures</a>
=======================================
<a href="#table-of-contents">back to top</a>

```scala
ExampleRecord.select.one() // When you only want to select one record
ExampleRecord.update.where(_.name eqs name).modify(_.name setTo "someOtherName").future() // When you don't care about the return type.
ExampleRecord.select.fetchEnumerator // when you need an Enumerator
ExampleRecord.select.fetch // When you want to fetch a Seq[Record]
```

<a id="scala-futures-examples">Examples with Scala Futures</a>
================================================================
<a href="#table-of-contents">back to top</a>


```scala

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ExampleRecord extends ExampleRecord {
  override val tableName = "examplerecord"

  // now define a session, a normal Datastax cluster connection
  implicit val session = SomeCassandraClient.session;

  def getRecordsByName(name: String): Future[Seq[ExampleModel]] = {
    ExampleRecord.select.where(_.name eqs name).fetch
  }

  def getOneRecordByName(name: String, someId: UUID): Future[Option[ExampleModel]] = {
    ExampleRecord.select.where(_.name eqs name).and(_.id eqs someId).one()
  }
}
```

<a id="twitter-futures">Twitter Futures</a>
===========================================
<a href="#table-of-contents">back to top</a>

```scala
ExampleRecord.select.get() // When you only want to select one record
ExampleRecord.update.where(_.name eqs name).modify(_.name setTo "someOtherName").execute() // When you don't care about the return type.
ExampleRecord.select.enumerate // when you need an Enumerator
ExampleRecord.select.collect // When you want to fetch a Seq[Record]
```

<a id="twitter-futures-examples">More examples with Twitter Futures</a>
=======================================================================
<a href="#table-of-contents">back to top</a>

```scala

import com.twitter.util.Future

object ExampleRecord extends ExampleRecord {
  override val tableName = "examplerecord"

  // now define a session, a normal Datastax cluster connection
  implicit val session = SomeCassandraClient.session;

  def getRecordsByName(name: String): Future[Seq[ExampleModel]] = {
    ExampleRecord.select.where(_.name eqs name).collect
  }

  def getOneRecordByName(name: String, someId: UUID): Future[Option[ExampleModel]] = {
    ExampleRecord.select.where(_.name eqs name).and(_.id eqs someId).get()
  }
}
```

<a id="collections-and-operators">Collections and operators</a>
================================================================
<a href="#table-of-contents">back to top</a>

Based on the above list of columns, phantom supports CQL 3 modify operations for CQL 3 collections: ```list, set, map```.
All operators will be available in an update query, specifically:

```ExampleRecord.update.where(_.id eqs someId).modify(_.someList $OPERATOR $args).future()```.

<a id="list-operators">List operators</a>
==========================================
<a href="#table-of-contents">back to top</a>

Examples in [ListOperatorsTest.scala](https://github.com/websudos/phantom/blob/develop/phantom-test/src/test/scala/com/websudos/phantom/dsl/crud/ListOperatorsTest.scala).

| Name                          | Description                                   |
| ----------------------------- | --------------------------------------------- |
| ```prepend```                 | Adds an item to the head of the list          |
| ```prependAll```              | Adds multiple items to the head of the list   |
| ```append```                  | Adds an item to the tail of the list          |
| ```appendAll```               | Adds multiple items to the tail of the list   |
| ```discard```                 | Removes the given item from the list.         |
| ```discardAll```              | Removes all given items from the list.        |
| ```setIdIx```                 | Updates a specific index in the list          |

<a id="set-operators">Set operators</a>
=======================================
<a href="#table-of-contents">back to top</a>

Sets have a better performance than lists, as the Cassandra documentation suggests.
Examples in [SetOperationsTest.scala](https://github.com/websudos/phantom/blob/develop/phantom-test/src/test/scala/com/websudos/phantom/dsl/crud/SetOperationsTest.scala).

| Name                          | Description                                   |
| ----------------------------- | --------------------------------------------- |
| ```add```                     | Adds an item to the tail of the set           |
| ```addAll```                  | Adds multiple items to the tail of the set    |
| ```remove ```                 | Removes the given item from the set.          |
| ```removeAll```               | Removes all given items from the set.         |


<a id="map-operators">Map operators</a>
=======================================
<a href="#table-of-contents">back to top</a>

Both the key and value types of a Map must be Cassandra primitives.
Examples in [MapOperationsTest.scala](https://github.com/websudos/phantom/blob/develop/phantom-test/src/test/scala/com/websudos/phantom/dsl/crud/MapOperationsTest.scala):

| Name                          | Description                                   |
| ----------------------------- | --------------------------------------------- |
| ```put```                     | Adds an (key -> value) pair to the map        |
| ```putAll```                  | Adds multiple (key -> value) pairs to the map |


<a id="automated-schema-generation">Automated schema generation</a>
===================================================================
<a href="#table-of-contents">back to top</a>

Replication strategies and more advanced features are not yet available in phantom, but CQL 3 Table schemas are  automatically generated from the Scala code. To create a schema in Cassandra from a table definition:

```scala

import scala.concurrent.Await
import scala.concurrent.duration._

Await.result(ExampleRecord.create().future(), 5000 millis)
```

Of course, you don't have to block unless you want to.


<a id="partition-tokens">Partition tokens</a>
==============================================
<a href="#table-of-contents">back to top</a>

```scala

import scala.concurrent.Await
import scala.concurrent.duration._
import com.websudos.phantom.Implicits._

sealed class ExampleRecord2 extends CassandraTable[ExampleRecord2, ExampleModel] with LongOrderKey[ExampleRecod2, ExampleRecord] {

  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object timestamp extends DateTimeColumn(this)
  object name extends StringColumn(this)
  object props extends MapColumn[ExampleRecord2, ExampleRecord, String, String](this)
  object test extends OptionalIntColumn(this)

  override def fromRow(row: Row): ExampleModel = {
    ExampleModel(id(row), name(row), props(row), timestamp(row), test(row));
  }
}


val orderedResult = Await.result(Articles.select.where(_.id gtToken one.get.id ).fetch, 5000 millis)

```

<a id="partition-token-operators">PartitionToken operators</a>
===============================================================
<a href="#table-of-contents">back to top</a>

| Operator name      | Description                                                              |
| ------------------ | ------------------------------------------------------------                                             |
| eqsToken           | The "equals" operator. Will match if the objects are equal                                               |
| gtToken            | The "greater than" operator. Will match a the record is greater than the argument                        |
| gteToken           | The "greater than or equals" operator. Will match a the record is greater than the argument              |
| ltToken            | The "lower than" operator. Will match a the record that is less than the argument and exists             |
| lteToken           | The "lower than or equals" operator. Will match a the record that is less than the argument              |

For more details on how to use Cassandra partition tokens, see [SkipRecordsByToken.scala]( https://github.com/websudos/phantom/blob/develop/phantom-test/src/test/scala/com/websudos/phantom/dsl/SkipRecordsByToken.scala)


<a id="time-series">Cassandra Time Series</a>
=============================================
<a href="#table-of-contents">back to top</a>

phantom supports Cassandra Time Series. To use them, simply mixin ```com.websudos.phantom.keys.ClusteringOrder``` and either ```Ascending``` or ```Descending```.

Restrictions are enforced at compile time.

```scala

import com.websudos.phantom.Implicits._

sealed class ExampleRecord3 extends CassandraTable[ExampleRecord3, ExampleModel] with LongOrderKey[ExampleRecod3, ExampleRecord] {

  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object timestamp extends DateTimeColumn(this) with ClusteringOrder with Ascending
  object name extends StringColumn(this)
  object props extends MapColumn[ExampleRecord2, ExampleRecord, String, String](this)
  object test extends OptionalIntColumn(this)

  override def fromRow(row: Row): ExampleModel = {
    ExampleModel(id(row), name(row), props(row), timestamp(row), test(row));
  }
}
```

Automatic schema generation can do all the setup for you.


<a id="compound-keys">Compound keys</a>
=======================================
<a href="#table-of-contents">back to top</a>

Phantom also supports using Compound keys out of the box. The schema can once again by auto-generated.

A table can have only one ```PartitionKey``` but several ```PrimaryKey``` definitions. Phantom will use these keys to build a compound value. Example scenario, with the compound key: ```(id, timestamp, name)```

```scala

import org.joda.time.DateTime
import com.websudos.phantom.Implicits._

sealed class ExampleRecord3 extends CassandraTable[ExampleRecord3, ExampleModel] with LongOrderKey[ExampleRecod3, ExampleRecord] {

  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object timestamp extends DateTimeColumn(this) with PrimaryKey[DateTime]
  object name extends StringColumn(this) with PrimaryKey[String]
  object props extends MapColumn[ExampleRecord2, ExampleRecord, String, String](this)
  object test extends OptionalIntColumn(this)

  override def fromRow(row: Row): ExampleModel = {
    ExampleModel(id(row), name(row), props(row), timestamp(row), test(row));
  }
}
```

<a id="secondary-keys">CQL 3 Secondary Keys</a>
===============================================
<a href="#table-of-contents">back to top</a>

When you want to use a column in a ```where``` clause, you need an index on it. Cassandra data modeling is out of the scope of this writing, 
but com.websudos.phantom offers ```com.websudos.phantom.keys.Index``` to enable querying.

The CQL 3 schema for secondary indexes can also be auto-generated with ```ExampleRecord4.create()```.

```SELECT``` is the only query you can perform with an ```Index``` column. This is a Cassandra limitation. The relevant tests are found [here](https://github.com/websudos/phantom/blob/develop/phantom-test/src/test/scala/com/websudos/phantom/dsl/specialized/SecondaryIndexTest.scala).


```scala

import java.util.UUID
import org.joda.time.DateTime
import com.websudos.phantom.Implicits._

sealed class ExampleRecord4 extends CassandraTable[ExampleRecord4, ExampleModel] with LongOrderKey[ExampleRecod4, ExampleRecord] {

  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object timestamp extends DateTimeColumn(this) with Index[DateTime]
  object name extends StringColumn(this) with Index[String]
  object props extends MapColumn[ExampleRecord2, ExampleRecord, String, String](this)
  object test extends OptionalIntColumn(this)

  override def fromRow(row: Row): ExampleModel = {
    ExampleModel(id(row), name(row), props(row), timestamp(row), test(row));
  }
}
```

<a id="async-iterators">Asynchronous iterators for large record sets</a>
========================================================================
<a href="#table-of-contents">back to top</a>

Phantom comes packed with CQL rows asynchronous lazy iterators to help you deal with billions of records.
phantom iterators are based on Play iterators with very lightweight integration.

The functionality is identical with respect to asynchronous, lazy behaviour and available methods.
For more on this, see this [Play tutorial](
http://mandubian.com/2012/08/27/understanding-play2-iteratees-for-normal-humans/)


Usage is trivial. If you want to use ```slice, take or drop``` with iterators, the partitioner needs to be ordered.

```scala

import scala.concurrent.Await
import scala.concurrent.duration._
import org.joda.time.DateTime
import com.websudos.phantom.Implicits._


sealed class ExampleRecord3 extends CassandraTable[ExampleRecord3, ExampleModel] with LongOrderKey[ExampleRecord3, ExampleRecord] {

  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object timestamp extends DateTimeColumn(this) with PrimaryKey[DateTime]
  object name extends StringColumn(this) with PrimaryKey[String]
  object props extends MapColumn[ExampleRecord2, ExampleRecord, String, String](this)
  object test extends OptionalIntColumn(this)

  override def fromRow(row: Row): ExampleModel = {
    ExampleModel(id(row), name(row), props(row), timestamp(row), test(row));
  }
}

object ExampleRecord3 extends ExampleRecord3 {
  def getRecords(start: Int, limit: Int): Future[Set[ExampleModel]] = {
    select.fetchEnumerator.slice(start, limit).collect
  }
}

```

<a id="batch-statements">Batch statements</a>
=============================================
<a href="#table-of-contents">back to top</a>

phantom also brrings in support for batch statements. To use them, see [IterateeBigTest.scala]( https://github
.com/websudosuk/phantom/blob/develop/phantom-test/src/test/scala/com/websudos/phantom/iteratee/IterateeBigTest.scala)

We have tested with 10,000 statements per batch, and 1000 batches processed simulatenously. Before you run the test, beware that it takes ~40 minutes.

Batches use lazy iterators and daisy chain them to offer thread safe behaviour. They are not memory intensive and you can expect consistent processing speed even with 1 000 000 statements per batch.

Batches are immutable and adding a new record will result in a new Batch, just like most things Scala, so be careful to chain the calls.

phantom also supports COUNTER batch updates and UNLOGGED batch updates.


<a id="logged-batch-statements">LOGGED batch statements</a>
===========================================================
<a href="#table-of-contents">back to top</a>

```scala

import com.websudos.phantom.Implicits._

BatchStatement()
    .add(ExampleRecord.update.where(_.id eqs someId).modify(_.name setTo "blabla"))
    .add(ExampleRecord.update.where(_.id eqs someOtherId).modify(_.name setTo "blabla2"))
    .future()

```

<a id="counter-batch-statements">COUNTER batch statements</a>
============================================================
<a href="#table-of-contents">back to top</a>

```scala

import com.websudos.phantom.Implicits._

CounterBatchStatement()
    .add(ExampleRecord.update.where(_.id eqs someId).modify(_.someCounter increment 500L))
    .add(ExampleRecord.update.where(_.id eqs someOtherId).modify(_.someCounter decrement 300L))
    .future()
```

<a id="unlogged-batch-statements">UNLOGGED batch statements</a>
============================================================
<a href="#table-of-contents">back to top</a>

```scala

import com.websudos.phantom.Implicits._

UnloggedBatchStatement()
    .add(ExampleRecord.update.where(_.id eqs someId).modify(_.name setTo "blabla"))
    .add(ExampleRecord.update.where(_.id eqs someOtherId).modify(_.name setTo "blabla2"))
    .future()

```

<a id="thrift-integration">Thrift integration</a>
=================================================
<a href="#table-of-contents">back to top</a>

We use Apache Thrift extensively for our backend services. phantom is very easy to integrate with Thrift models and uses ```Twitter Scrooge``` to compile them. 
Thrift integration is optional and available via ```"com.websudos" %% "phantom-thrift"  % phantomVersion```.

```thrift
namespace java com.websudos.phantom.sample.ExampleModel

stuct ExampleModel {
  1: required i32 id,
  2: required string name,
  3: required Map&lt;string, string&gt; props,
  4: required i32 timestamp
  5: optional i32 test
}
```


<a id="running-tests">Running the tests locally</a>
==================================================
<a href="#table-of-contents">back to top</a>

phantom uses Embedded Cassandra to run tests without a local Cassandra server running.
You need two terminals to run the tests, one for Embedded Cassandra and one for the actual tests.

```scala
sbt
project phantom-cassandra-unit
run
```

Then in a new terminal

```scala
sbt
project com.websudos.phantom-test
test
```

<a id="contributors">Contributors</a>
=====================================
<a href="#table-of-contents">back to top</a>

Phantom was developed at websudos as an in-house project. All Cassandra integration at websudos goes through phantom.

* Flavian Alexandru flavian@newzly.com(maintainer)
* Tomasz Perek tomasz.perek@newzly.com
* Viktor Taranenko (viktortnk)
* Bartosz Jankiewicz (@bjankie1)
* Eugene Zhulenev (@ezhulenev)

<a id="copyright">Copyright</a>
===============================
<a href="#table-of-contents">back to top</a>

Special thanks to Viktor Taranenko from WhiskLabs, who gave us the original idea.

Copyright 2013 - 2014 websudos.


Contributing to phantom
=======================
<a href="#table-of-contents">back to top</a>

Contributions are most welcome!

<a id="git-flow">Using GitFlow</a>
==================================

To contribute, simply submit a "Pull request" via GitHub.

We use GitFlow as a branching model and SemVer for versioning.

- When you submit a "Pull request" we require all changes to be squashed.
- We never merge more than one commit at a time. All the n commits on your feature branch must be squashed.
- We won't look at the pull request until Travis CI says the tests pass, make sure tests go well.

<a id="style-guidelines">Scala Style Guidelines</a>
===================================================

In spirit, we follow the [Twitter Scala Style Guidelines](http://twitter.github.io/effectivescala/).
We will reject your pull request if it doesn't meet code standards, but we'll happily give you a hand to get it right.

Some of the things that will make us seriously frown:

- Blocking when you don't have to. It just makes our eyes hurt when we see useless blocking.
- Testing should be thread safe and fully async, use ```ParallelTestExecution``` if you want to show off.
- Use the common patterns you already see here, we've done a lot of work to make it easy.
- Don't randomly import stuff. We are very big on alphabetized clean imports.


