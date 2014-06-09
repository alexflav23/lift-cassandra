/*
 * Copyright 2013 newzly ltd.
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
package com.newzly.phantom.column

import com.newzly.phantom.CassandraPrimitive

trait PrimitiveCollectionValue[R] extends CollectionValueDefinition[R] {

  def valuePrimitive: CassandraPrimitive[R]

  override def valueCls: Class[_] = valuePrimitive.cls

  override def valueToCType(v: R): AnyRef = valuePrimitive.toCType(v)

  override def valueFromCType(c: AnyRef): R = valuePrimitive.fromCType(c)

}
