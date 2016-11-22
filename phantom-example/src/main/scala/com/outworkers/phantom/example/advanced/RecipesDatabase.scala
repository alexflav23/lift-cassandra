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
package com.outworkers.phantom.example.advanced

import com.outworkers.phantom.connectors
import com.outworkers.phantom.connectors.KeySpaceDef
import com.outworkers.phantom.dsl._
import com.outworkers.phantom.example.basics._

import scala.concurrent.{Future => ScalaFuture}

// In this section, we will show how you can create a real-world Cassandra service with com.outworkers.phantom.
// First you have to think of what queries you need to perform. The usual.
// Say you come up with id and author.

// You will end up with several mapping tables enabling you to do all the queries you want.
// Now you are left with maintaining consistency at application level.
// We usually overlay a service on top of the mapping tables.
// To keep all the complexity away from other parts of the application.
class RecipesDatabase(override val connector: KeySpaceDef) extends Database[RecipesDatabase](connector) {

  object Recipes extends ConcreteRecipes with connector.Connector
  object AdvancedRecipes extends ConcreteAdvancedRecipes with connector.Connector
  object AdvancedRecipesByTitle extends ConcreteAdvancedRecipesByTitle with connector.Connector
  object CompositeKeyRecipes extends ConcreteCompositeKeyRecipes with connector.Connector
  object ThriftTable extends ConcreteThriftTable with connector.Connector
  object SecondaryKeyRecipes extends ConcreteSecondaryKeyRecipes with connector.Connector

  /**
   * Right now you can go for a really neat trick of the trade.
   * You can automatically initialise all your tables using phantom's schema auto-generation capabilities.
   * We are using the same connector as the tables do, which will link to the exact same database session.
   *
   * The bellow example uses the Future.join method which Twitter specific and not available in the less advanced Scala API.
   * Nonetheless, if you are using Scala you can almost replicate the below with a Future.sequence or Future.traverse over a List.
   *
   * This is a very neat and simple trick which will initialise all your tables in parallel at any time you want. The initialisation will automatically
   * trigger the mechanism that connects to Cassandra and gives you back a session.
   */

  // For instance, right now when you want to insert a new recipe.
  // Say from a JavaScript client with a fancy interface.
  // You need to insert one record into the actual table.
  // And another into the author -> id mapping table.

  // This is a trivial example showing how you can map and flatMap your path to glory.
  // Non blocking, 3 lines of code, 15 seconds of typing effort. Done.
  def insertRecipe(recipe: Recipe): ScalaFuture[ResultSet] = {
    for {
      insert <- AdvancedRecipes.insertRecipe(recipe)
      byTitle <- AdvancedRecipesByTitle.insertRecipe(Tuple2(recipe.title, recipe.id))
    } yield byTitle
  }
}

object RecipesDatabase extends RecipesDatabase(connectors.ContactPoint.local.keySpace("recipes"))