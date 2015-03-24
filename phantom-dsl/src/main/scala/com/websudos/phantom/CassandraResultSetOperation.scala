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
package com.websudos.phantom

import java.util.concurrent.Executors

import com.google.common.util.concurrent.MoreExecutors
import com.twitter.util.{Future => TwitterFuture, Promise => TwitterPromise}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future => ScalaFuture, Promise => ScalaPromise}

object Manager {

  lazy val cores = Runtime.getRuntime.availableProcessors()

  lazy val taskExecutor = Executors.newCachedThreadPool()

  implicit lazy val scalaExecutor: ExecutionContext = ExecutionContext.fromExecutor(taskExecutor)

  lazy val executor = MoreExecutors.listeningDecorator(taskExecutor)

  lazy val logger = LoggerFactory.getLogger("com.websudos.phantom")

  def shutdown(): Unit = {
    taskExecutor.shutdown()
    executor.shutdown()
  }
}

/*
private[phantom] trait CassandraResultSetOperations {

  protected[this] def scalaStatementToFuture(s: Statement)(implicit session: Session): ScalaFuture[ResultSet] = {
    val promise = ScalaPromise[ResultSet]()

    val future = session.executeAsync(s)

    val callback = new FutureCallback[ResultSet] {
      def onSuccess(result: ResultSet): Unit = {
        promise success result
      }

      def onFailure(err: Throwable): Unit = {
        Manager.logger.error(err.getMessage)
        promise failure err
      }
    }
    Futures.addCallback(future, callback, Manager.executor)
    promise.future

  }

  protected[this] def twitterStatementToFuture(s: Statement)(implicit session: Session): TwitterFuture[ResultSet] = {
    val promise = TwitterPromise[ResultSet]()
    val future = session.executeAsync(s)

    val callback = new FutureCallback[ResultSet] {
      def onSuccess(result: ResultSet): Unit = {
        promise update Return(result)
      }

      def onFailure(err: Throwable): Unit = {
        Manager.logger.error(err.getMessage)
        promise update Throw(err)
      }
    }
    Futures.addCallback(future, callback, Manager.executor)
    promise

  }

  protected[this] def scalaQueryStringExecuteToFuture(query: String)(implicit session: Session): ScalaFuture[ResultSet] = {
    Manager.logger.debug("Executing Cassandra query:")
    Manager.logger.debug(query)
    val promise = ScalaPromise[ResultSet]()

    val future = session.executeAsync(query)

    val callback = new FutureCallback[ResultSet] {
      def onSuccess(result: ResultSet): Unit = {
        promise success result
      }

      def onFailure(err: Throwable): Unit = {
        Manager.logger.error(err.getMessage)
        promise failure err
      }
    }
    Futures.addCallback(future, callback, Manager.executor)
    promise.future
  }

  protected[this] def twitterQueryStringExecuteToFuture(query: String)(implicit session: Session): TwitterFuture[ResultSet] = {
    val promise = TwitterPromise[ResultSet]()
    val future = session.executeAsync(query)

    val callback = new FutureCallback[ResultSet] {
      def onSuccess(result: ResultSet): Unit = {
        promise update Return(result)
      }

      def onFailure(err: Throwable): Unit = {
        Manager.logger.error(err.getMessage)
        promise update Throw(err)
      }
    }
    Futures.addCallback(future, callback, Manager.executor)
    promise
  }

  protected[this] def scalaFutureToTwitter[R](future: ScalaFuture[R])(implicit ctx: ExecutionContext): TwitterFuture[R] = {
    val promise = TwitterPromise[R]()

    future onComplete {
      case Success(res) => promise update Return(res)
      case Failure(err) => {
        Manager.logger.error(err.getMessage)
        promise update Throw(err)
      }
    }
    promise
  }
}
*/