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
package com.outworkers.phantom

import com.outworkers.phantom.thrift.columns.RootThriftPrimitive

package object thrift {
  type ThriftStruct = com.twitter.scrooge.ThriftStruct

  type ThriftColumn[
    T <: CassandraTable[T, R],
    R, Model <: ThriftStruct
  ] = com.outworkers.phantom.thrift.columns.ThriftColumn[T, R, Model]

  type ThriftSetColumn[
    T <: CassandraTable[T, R],
    R,
    Model <: ThriftStruct
  ] = com.outworkers.phantom.thrift.columns.ThriftSetColumn[T, R, Model]

  type ThriftListColumn[
    T <: CassandraTable[T, R],
    R,
    Model <: ThriftStruct
  ] = com.outworkers.phantom.thrift.columns.ThriftListColumn[T, R, Model]

  type ThriftMapColumn[
    T <: CassandraTable[T, R],
    R,
    KeyType,
    Model <: ThriftStruct
  ] = com.outworkers.phantom.thrift.columns.ThriftMapColumn[T, R, KeyType, Model]

  type OptionalThriftColumn[
    T <: CassandraTable[T, R],
    R,
    Model <: ThriftStruct
  ] = com.outworkers.phantom.thrift.columns.OptionalThriftColumn[T, R, Model]

  type ThriftPrimitive[T <: ThriftStruct] = RootThriftPrimitive[T]
}



