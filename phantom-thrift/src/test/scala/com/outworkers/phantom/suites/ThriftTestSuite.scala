/*
 * Copyright 2013-2016 Websudos, Limited.
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
 * - Explicit consent must be obtained from the copyright owner, Outworkers Limited before any redistribution is made.
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
package com.outworkers.phantom.suites

import java.util.UUID

import com.outworkers.phantom.tables.{Output, ThriftDatabase}
import com.outworkers.util.testing._
import org.scalatest.concurrent.PatienceConfiguration

import scala.concurrent.duration._
import org.scalatest.{BeforeAndAfterAll, Matchers, OptionValues, Suite}

trait ThriftTestSuite extends Suite
  with BeforeAndAfterAll
  with Matchers
  with OptionValues
  with ThriftDatabase.connector.Connector {
  implicit val s: PatienceConfiguration.Timeout = timeout(10 seconds)

  type ThriftTest = com.outworkers.phantom.thrift.ThriftTest
  val ThriftTest = com.outworkers.phantom.thrift.ThriftTest

  implicit object OutputSample extends Sample[Output] {
    def sample: Output = {
      Output(
        id = gen[UUID],
        name = gen[String],
        struct = gen[ThriftTest],
        thriftSet = genList[ThriftTest]().toSet[ThriftTest],
        thriftList = genList[ThriftTest](),
        thriftMap = genList[ThriftTest]().map {
          item => (item.toString, item)
        }.toMap,
        optThrift = genOpt[ThriftTest]
      )
    }
  }

  implicit object ThriftTestSample extends Sample[ThriftTest] {
    def sample: ThriftTest = ThriftTest(
      gen[Int],
      gen[String],
      test = false
    )
  }
}
