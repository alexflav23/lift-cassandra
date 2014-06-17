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
package com.websudos.phantom.keys

import com.websudos.phantom.column.AbstractColumn

private[phantom] trait Key[ValueType, KeyType <: Key[ValueType, KeyType]] {
  self: AbstractColumn[ValueType] =>
}

trait PrimaryKey[ValueType] extends Key[ValueType, PrimaryKey[ValueType]] {
  self: AbstractColumn[ValueType] =>
  override val isPrimary = true
}

trait PartitionKey[ValueType] extends Key[ValueType, PartitionKey[ValueType]] {
  self: AbstractColumn[ValueType] =>
  override val isPartitionKey = true
  override val isPrimary = true
}

/**
 * A trait mixable into Column definitions to allow storing them as keys.
 */
trait Index[ValueType] extends Key[ValueType, Index[ValueType]] {
  self: AbstractColumn[ValueType] => override val isSecondaryKey = true
}
