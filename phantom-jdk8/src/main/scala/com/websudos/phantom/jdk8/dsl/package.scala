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
package com.websudos.phantom.jdk8

import com.websudos.phantom.dsl.CassandraTable

package object dsl extends DefaultJava8Primitives {

  type OffsetDateTimeColumn[Owner <: CassandraTable[Owner, Record], Record] = com.websudos.phantom.column.PrimitiveColumn[Owner, Record, OffsetDateTime]
  type ZonedDateTimeColumn[Owner <: CassandraTable[Owner, Record], Record] = com.websudos.phantom.column.PrimitiveColumn[Owner, Record, ZonedDateTime]
  type JdkLocalDateColumn[Owner <: CassandraTable[Owner, Record], Record] = com.websudos.phantom.column.PrimitiveColumn[Owner, Record, JdkLocalDate]

  type OptionalOffsetDateTimeColumn[
    Owner <: CassandraTable[Owner, Record],
    Record
  ] = com.websudos.phantom.column.OptionalPrimitiveColumn[Owner, Record, OffsetDateTime]

  type OptionalZonedDateTimeColumn[
    Owner <: CassandraTable[Owner, Record],
    Record
  ] = com.websudos.phantom.column.OptionalPrimitiveColumn[Owner, Record, ZonedDateTime]

  type OptionalJdkLocalDateColumn[Owner <: CassandraTable[Owner, Record], Record] = com.websudos.phantom.column.OptionalPrimitiveColumn[Owner, Record, JdkLocalDate]

  type OffsetDateTime = java.time.OffsetDateTime
  type ZonedDateTime = java.time.ZonedDateTime
  type JdkLocalDate = java.time.LocalDate

}
