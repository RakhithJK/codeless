/*******************************************************************************
 * * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License.  You may obtain a copy
 *  * of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 ******************************************************************************/
package com.tmobile.ct.codeless.testdata;

import com.tmobile.ct.codeless.core.Accessor;

/**
 * The RuntimeTestDataSource.
 *
 * @author Fikreselam Elala
 */

import com.tmobile.ct.codeless.core.TestDataSource;

public class RuntimeTestDataSource implements TestDataSource<String> {

	private Accessor accessor;

	public RuntimeTestDataSource(Accessor accessor) {
		super();
		this.accessor = accessor;
	}

	@Override
	public String fullfill() {
		return accessor.getActual().toString();
	}

	public Accessor getValue() {
		return accessor;
	}

	public void setValue(Accessor value) {
		this.accessor = value;
	}

	@Override
	public void setAccessor(Accessor accessor) {
		this.accessor = accessor;

	}

	@Override
	public Accessor getAccessor() {
		return accessor;
	}

	@Override
	public void setValue(String Value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
