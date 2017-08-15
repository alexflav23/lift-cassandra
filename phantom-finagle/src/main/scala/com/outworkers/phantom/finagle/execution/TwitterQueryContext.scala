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
package com.outworkers.phantom.finagle.execution

import cats.Monad
import com.outworkers.phantom.builder.query.execution.PromiseInterface
import com.outworkers.phantom.ops.QueryContext
import com.twitter.conversions.time._
import com.twitter.util.{Await, Duration, Future, Promise}

object TwitterFutureImplicits {

  val monadInstance: Monad[Future] = new Monad[Future] {

    override def flatMap[A, B](fa: Future[A])(f: (A) => Future[B]): Future[B] = fa flatMap f

    /**
      * Note that while this implementation will not compile with `@tailrec`,
      * it is in fact stack-safe.
      */
    final def tailRecM[B, C](b: B)(f: B => Future[Either[B, C]]): Future[C] = {
      f(b).flatMap {
        case Left(b1) => tailRecM(b1)(f)
        case Right(c) => Future.value(c)
      }
    }

    override def pure[A](x: A): Future[A] = Future.value(x)
  }

}

object TwitterPromiseInterface extends PromiseInterface[Promise, Future] {
  override def empty[T]: Promise[T] = Promise.apply[T]

  override def become[T](source: Promise[T], value: Future[T]): Promise[T] = {
    source become value
    source
  }

  override def future[T](source: Promise[T]): Future[T] = source

  override def failed[T](exception: Exception): Future[T] = Future.exception[T](exception)
}

class TwitterQueryContext extends QueryContext[Promise, Future, Duration](10.seconds)(
  TwitterFutureImplicits.monadInstance,
  TwitterPromiseInterface,
  TwitterGuavaAdapter
) {

  override def await[T](f: Future[T], timeout: Duration): T = Await.result(f, timeout)

}