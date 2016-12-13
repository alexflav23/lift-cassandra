### Phantom vs other Cassandra drivers


This document aims to offer you an insight into the available tooling
for Scala/Cassandra integrations and hopefully help you pick the right
tool for the job.

The available offering:

- [Datastax Java Driver](https://github.com/datastax/java-driver)
- [Phantom](https://github.com/outworkers/phantom)
- [Spark Cassandra Connector](https://github.com/datastax/spark-cassandra-connector)
- [Quill](https://github.com/getquill/quill)

### Feature comparison table

Let's first compare the basic qualities of these available drivers, looking
at a wider range of features and verticals, from how deeply integrated they
are with Cassandra and the support they offer, to their level of activity
and how up to date they are.


| Driver | Language | Commercial | Type-safe | Spark Support | Streams | DSL | Cassandra | Latest | Activity | Created |
| ------ | -------- | ----- | ------------------ | --------- | ------------- | ---------------- | --- | -------- | ---- | ----- | ----- |
| Datastax Java Driver | Java | yes | no | no | no | EDSL | 3.8.0 | 3.1.0 | High | 2012 |
| Phantom | Scala | yes | yes | no | yes | EDSL | 3.8.0 | 3.1.0 | High | 2013 |
| Spark Connector | Scala | yes | yes | yes | no | EDSL | 3.0 | High | 2014 |
| Quill | Scala | no | yes | no | yes | QDSL | 3.8.0 | 2015 |


### An overview of the various drivers and using them from Scala

#### Datastax Java Driver

Created by Datastax, the commercial company behind Cassandra, this is the underlying engine of all other drivers. Phantom, Quill and the Spark connector all use it underneath the hood to connect and execute queries. Phantom and Quill add a Scala friendly face to the driver, while the Spark connector does what it says on the tin, namely to focus on Cassandra - Spark integration.

So why not just use the Datastax driver for Scala? We would like to start by saying it's a really really good tool and some seriously good engineering lies behind it, but by sole virtue of being aimed at a Java audience, it does miss out a lot on some of the really powerful things you can do with the Scala compiler.

#### Cons of using the Java driver from Scala

- Writing queries is not type-safe, and the `QueryBuilder` is mutable.
- There is no DSL to define the tables, so dealing with them is still very much manual and string based. 
- Auto-generation of schema is not available, the CQL for tables or anything else must be manually created.
- The driver does not and cannot prevent you from doing bad things.
- You must constantly refer to the `session`.
- You constantly get back a `ResultSet`, which in most instances is not what you want.
- There is not native support for `scala.concurrent.Future` or any of the default Scala primitives.

Overall, if you wanted to, you could naturally use the Java driver inside a Scala application, but you probably wouldn't to. Again, this has very little to do with the quality of the driver itself as much as it has to do with the fact that the API is Java and therefore aimed at a Java consumer audience.

Libraries such as phantom and quill are designed to abstract away many of the internals of the Java driver and make it more appealing to a Scala audience, and phantom goes very very far to mask away all the underlying complexities and provide very advanced support for using Cassandra as a database layer.


#### Phantom

[![Build Status](https://travis-ci.org/outworkers/phantom.svg?branch=develop)](https://travis-ci.org/outworkers/phantom) [![Coverage Status](https://coveralls.io/repos/outworkers/phantom/badge.svg)](https://coveralls.io/r/outworkers/phantom)  [![Codacy Rating](https://api.codacy.com/project/badge/grade/25bee222a7d142ff8151e6ceb39151b4)](https://www.codacy.com/app/flavian/phantom_2) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.websudos/phantom-dsl_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.websudos/phantom-dsl_2.11) [![Bintray](https://api.bintray.com/packages/websudos/oss-releases/phantom-dsl/images/download.svg) ](https://bintray.com/websudos/oss-releases/phantom-dsl/_latestVersion) [![ScalaDoc](http://javadoc-badge.appspot.com/com.websudos/phantom-dsl_2.11.svg?label=scaladoc)](http://javadoc-badge.appspot.com/com.websudos/phantom-dsl_2.11) [![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/outworkers/phantom?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Phantom is designed to be a one stop destination. It is built exclusively to cater to the app level integration of Scala and Cassandra, meaning it is a lot more than a simple driver.

It is built on top of the Datastax Java driver, and uses all the default connection strategies and so on under the hood, without exposing any of the Java-esque APIs, but at the same time offering a multitude of features that are not available in the Java driver or in any other driver.

- A type safe Schema DSL that means you never have to deal with raw CQL again.
- A very advanced compile time mechanism that offers a fully type safe variant of CQL.
- A natural DSL that doesn't require any new terminology and aims to introduce a minimal learning curve. Phantom is not a leaking abstraction and it is exclusively built to target Cassnadra integration, therefore it has support for all the latest features of CQL and doesn't require constantly mapping terminology. Unlike LINQ style DSLs for instance, the naming will largely have 100% correspondence to CQL terminology you are already used to.
- Automated schema generation, automated table migrations, automated database generation and more, meaning you will never ever have to manually initialise CQL tables from scripts ever again.
- Native support of Scala concurrency primitives, from `scala.concurrent.Future` to more advanced access patterns such as reactive streams or even iteratees, available via separate dependencies.
- Native support for `play-streams`, `reactive-streams` and `com.twitter.util.Future`, available via dedicated modules.

#### Quill

Quill is a compile time macro based DSL that is capable of generating queries directly from a case class. It differs from phantom in several ways,
in the sense that Quill aims to build a leaking abstraction style QDSL, meaning a one size fits all driver for a number of different databases.

In the abstract sense it's the most like for like tool and probably the only one worth comparing to phantom, since the other variant
is pure Java and the Spark connector is obviously for, well, Spark. There are other drivers out there, but all discontinued or Thrift based,
some of which include Cassie from Twitter, Cascal, Astyanax from Netflix, and so on.

We would be the first to credit the engineering virtue behind it, it's an excellently designed tool and a very very powerful example of
just how far meta-programming can take you. Now that being said, there are a great number of items that make Quill less of suitable tool for application
layer Cassandra.

The paper that initially inspired Quill is a strong suggestion that the fundamental approach of having complex database level mappings
and lightweight entities is wrong, and that the focus should be on the domain entities and on letting them drive the show. In a sense,
phantom follows this same principle of augmenting entities, although it is true that unlike Quill there is an extra layer of indirection through the mapping DSL.

##### It introduces new terminology

One of the great perks of phantom is the fact that you don't really need to learn a new syntax. If you are familiar with Scala
and you are familiar wth SQL and CQl, you will feel right at home from the very first go, which means close to instant productivity.

It is true that phantom introduces other application level abstractions, such as the `Database` and the modelling DSL, all
of which you need to be aware, but there will never be a time where you read a phantom query and you wonder what the final result looks like.

##### It doesn't account for the CQL schema

Probably one of the most powerful features of phantom is the ability to be schema aware at compile time and the fact that you never
have to deal with manual CQL or to manually initialise schemas and various other bits. Gone are the days where you are
copy pasting CQL from one place to another in your build scripts or loading up schemas from `*.cql` files in `cqlsh`.

Phantom has a powerful mechanism to perform what we call schema auto-generation, which means with a single method call
it is capable of automatically initialising all the tables in your database on the fly against any keyspace of your choosing. It
can also account for more advanced indexing scenarios, user defined types and more, all on the fly.

Let's have a look at a basic example, for the basic `Recipe` case class, in this instance indexed by `url`.

```scala

import com.outworkers.phantom.dsl._

case class Recipe(
  url: String,
  description: Option[String],
  ingredients: List[String],
  servings: Option[Int],
  lastCheckedAt: DateTime,
  props: Map[String, String],
  side_id: UUID
)

class Recipes extends CassandraTable[ConcreteRecipes, Recipe] {

  object url extends StringColumn(this) with PartitionKey[String]

  object description extends OptionalStringColumn(this)

  object ingredients extends ListColumn[String](this)

  object servings extends OptionalIntColumn(this)

  object lastcheckedat extends DateTimeColumn(this)

  object props extends MapColumn[String, String](this)

  object side_id extends UUIDColumn(this)


  override def fromRow(r: Row): Recipe = {
    Recipe(
      url(r),
      description(r),
      ingredients(r),
      servings(r),
      lastcheckedat(r),
      props(r),
      side_id(r)
    )
  }
}
```

As of version 2.0.0, phantom is capable of auto-generating the `fromRow` method, so the mapping DSL is reduced to:

```
class Recipes extends CassandraTable[ConcreteRecipes, Recipe] {

  object url extends StringColumn(this) with PartitionKey[String]

  object description extends OptionalStringColumn(this)

  object ingredients extends ListColumn[String](this)

  object servings extends OptionalIntColumn(this)

  object lastcheckedat extends DateTimeColumn(this)

  object props extends MapColumn[String, String](this)

  object side_id extends UUIDColumn(this)
}
```

It's definitely more boilerplate than Quill, there's no doubt about that, however, this simple DSL can help us to great
things:

- Control the name we want to use for our columns. Not the most interesting feature,
but it helps avoid collisions with known Cassandra types. Currently this doesn't look possible in Quill.

- Generate the CQL schema on the fly. Every phantom table has a `.create` method, that will yield a `CreateQuery`,
where you can set the creation properties in minute details. The schema is then inferred from the DSL.

- Generate the entire database on the fly. By nesting tables in a `Database` object, we are able to implement
`autocreate`, `createAsync` and other convenience methods for automatically creating, truncating or dropping
an entire database in a single method call. Look ma', no manual CQL.

- Phantom is schema aware. Using an advanced implicit mechanism powered by the Shapeless library, phantom is
capable of "knowing" what queries are possible and what queries aren't. Let's take for example the `Recipes` above:

The following query is invalid, because we have not defined any index for the `side_id` column.

```scala
database.recipes.select.where(_.uid eqs someid)
```

Quill, *based on our current understanding, will however happily compile and generate the query, it has no way
to know what you wanted to do with the `side_id` column.


##### It doesn't account for protocol version/Cassandra version dependent behaviour

Numerous features or bugs are version dependent in Cassandra and the only way to provide the user with a consistent experience
across the numerous features is to be version aware, so in certain contexts the final query is only generated when the protocol
version of a give cluster is known.

Naturally this "knowing" can only happen at runtime, and it deals with very simple things such as set the consistency level of query. For
older protocol versions, the `CONSISTENCY LEVEL` part of a query was part of the `USING` clause, however in more recent
versions of the protocol the consistency level has to be specified per session. This is an optimisation to allow the nodes
to perform the necessary coordination to achieve the desired `CONSISTENCY LEVEL` without having to first parse
the query.

So CQL went from:

```sql
UPDATE keyspace.table WHERE ID = 'some_id' SET a = 'b' USING CONSISTENCY QUOROM;

// to

session.use(ConsistencyLevel.QUORUM)
UPDATE keyspace.table WHERE ID = 'some_id' SET a = 'b';
```

And any client library will need to transparently handle the change in CQL protocol details. Hoewever, this would
be impossible without knowing the version in advance, which means a cluster query which implies runtime.

#### Extensibility

Quill is likely easier to extend than Phantom, as infix notation and arbitrary string generation is easier to do
than it is to extend more complex tightly coupled EDSL structures. But in an ideal world, you wouldn't be trying
to extend the native driver at all, you would instead be welcomed by a wide range of supported features.

In this category, both tools are imperfect and incomplete, and phantom has its own shortcomings. However,
the Quill comparison simply states: "You could extend Phantom by extending the DSL to add new features,
although it might not be a straightforward process.", which is a bit inaccurate.

Being a very new player in the game, Quill is a nice toy when it comes to Cassandra feature support 
and you will often find yourself needing to add features. Phantom has its gaps without a doubt, but it's a far far more mature alternative,
and the amount of times when extension is required are significantly rarer.

A few things to remember:

- You do not need to create new column types to support new datatypes.
- Phantom internally offers the `Primitive` type class for this very reason.
- As of phantom 1.30.x, phantom offers `Primitive.derive[T, String](CaseClass.apply)` to offer you a custom datatype for `case class County(str: String)`.
- In fact, `Primitive.derive[CaseClass]` will natively work with any `case class` that is built of `Primitive` types.

#### Dependencies

One of the common pains in modern development is of course the number of dependencies that are brought in. The Quill
authors make the somewhat misleading argument that phantom introduces more dependencies and that each third party
dependency will bring in more and more modules.

- This is somewhat true, however if you are using the play-streams integration, the assumption is you already have
play-streams somewhere else in you app, otherwise it makes little sense to not stick to defaults. The same is true
 for every other module available in phantom, and by default you don't actually have to use any of them.

- Most modules however are only useful if you already have most of those dependencies internally. It's pretty
much impossible to build Thrift services without a dependency on Thrift itself, so in that respect it is highly
unlikely that using those extra modules will end up bringing in more dependencies than you already have.

- The one place where phantom used to suck is the dependency on `scala-reflect`, which is causing some ugly things inside the 
framework, namely the need for global locks to make reflection thread safe in the presence of multiple class loaders. This 
is however going away in 2.0.0, already available on `feature/2.0.0`, and we are replacing `scala-reflect` with a macro based approach.
 
- The only notable dependencies of phantom are `shapeless` and `cassandra-driver-core`, the latter of which you will have
inevitably. Shapeless is also quite light and compile time, it depends only on macro libraries such as `macro-compat`. You
can have a look yourself [here](https://github.com/milessabin/shapeless). We also depend on an internal engine called the
diesel engine, which itself only depends on `macro-compat`, since it's nothing more than a customised macro toolchain.

#### Documentation and commercial support

Both tools can do a lot better in this category, but phantom is probably doing a little better in that department, 
since we have a plethora of tests, blog posts, and resources, on how to do things in phantom. This is not yet
necessarily true of Quill, and we know very well just how challenging the ramp up process to stability can be.

In terms of commercial support, phantom wins. We don't mean to start a debase on the virtues of open source, and 
we are aware most of the development community strongly favours OSS licenses and the word "commercial" is unpleasant.
However, we are constrained by the economic reality of having to pay the people competent enough to write this software
for the benefit of us all and make sure they get enough spare time to focus on these things, which is a lot less fun.

Add in a never ending stream of support messages, emails, chats, feature requests, and bug reports, and you would soon learn
the true nature and responsibility of keeping a project like this alive. We know we're not really competing with Twitter
on amount of OSS released, but on an impact/staff member ratio we would happily compete.

Phantom-pro co-exists alongside the default OSS version to offer you more advanced support and a more interesting feature
set helping you develop and integrate Cassandra even faster. Spark support with an advanced compile time mapper and more
are made possible in phantom-pro, as well as automated table migrations, DSE Graph support, and some other really cool
toys such as auto-tables, which will be in some respect similar to Quill as the mapping DSL will not be necessary anymore,
but at the same time retain the powerful embedded query EDSL.


#### Conclusion

Let's sum up the points that we tried to make here in two key paragraphs.

- Phantom does indeed make it slightly less verbose to extend things like TypeCodec. But how often do you find yourself dealing with that at application level layer? TypeCodec is not really the valid approach anyway, you have to think more in terms of Cassandra data types, why would you register a TypeCodec when you could use a simple map?
Phantom requires minimal boilerplate around defining the table DSL and by nature doesn't really work well with sharing table columns. It can be done, but it's not the most beautiful code, nor is it the worst.
Overall

- Quill is an excellence piece of  software.
It's better than phantom strictly at query generation, there's boilerplate that can be reduced through QDSLs that cannot be reduced through an EDSL, if we are fighting who's the leanest meanest string generator Quill wins.
It's a vastly inferior tool at the application layer, and it's even more unnatural for most people. Slick popularised the concepts to some extent, but some of the most basic functionalities you would want as part of your application lifecycle are not as easily addressable through a QDSL or at least it has yet to happen.
Phantom is far more mature, and very widely adopted, with more resources and input from the founding team, a long standing roadmap and a key partnership with Datastax that helps us stay on top of all features.

- Phantom is a lot easier to adopt and learn, simply as it doesn't introduce any new terminology. The mapping DSL and the `Database` object are all you need to know.