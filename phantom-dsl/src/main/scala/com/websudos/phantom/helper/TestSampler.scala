/*
 * Copyright 2013 websudos ltd.
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
package com.websudos.phantom.helper

import com.datastax.driver.core.Session
import com.websudos.phantom.Implicits._

import com.twitter.conversions.time._
import com.twitter.util.Await

/**
 * A basic trait implemented by all test tables.
 * @tparam Row The case class type returned.
 */
trait TestSampler[Owner <: CassandraTable[Owner, Row], Row] {
  self : CassandraTable[Owner, Row] =>

  /**
   * Inserts the schema into the database in a blocking way.
   * This is done with a try catch in order to avoid tests issues when the same keyspace is used
   * and schema is inserted twice
   * @param session The Cassandra session.
   *
   * ATTENTION!!! this method creates the schema in a sync mode, the unit tests rely on it to be synced
   */
  def insertSchema()(implicit session: Session): Unit = {
      logger.info("Schema agreement in progress: ")
      try {
        logger.info(schema())
        Await.ready(create.execute(), 2.seconds)
      } catch {
        case e: Throwable =>
          logger.error(s"schema for $tableName could not be created. ")
          logger.error(e.getMessage)
      }
  }
}

/**
 * A simple model sampler trait.
 * Forces implementing case class models to provide a way to sample themselves.
 * This can only be mixed into a case class or Product with Serializable implementor.
 */
trait ModelSampler[Model] {

  /**
   * The sample method. Using basic sampling, this will produce a unique sample
   * of the implementing class.
   * @return A unique sample of the class.
   */
  def sample: Model
}
