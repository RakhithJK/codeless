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
package com.tmobile.ct.codeless.test.side.data;

import java.util.HashMap;

import org.junit.Test;
import org.junit.Assert;

public class SIDESuiteTest {

	private static SIDESuite sideSuite = new SIDESuite();
	
	@Test
	public void getSideSuite() {
		sideSuite = createSideSuite();
		Assert.assertNotNull(sideSuite);
		Assert.assertNotNull(sideSuite.getId());
		Assert.assertNotNull(sideSuite.getName());
		Assert.assertNotNull(sideSuite.getTests());
		Assert.assertNotNull(sideSuite.getAdditionalProperties());
	}

	public static SIDESuite createSideSuite() {

		sideSuite.setId("id");
		sideSuite.setName("suite");
		sideSuite.setTests(SIDETestTest.createListOfSideTests());
		sideSuite.setAdditionalProperties(new HashMap<String, Object>());
		return sideSuite;
	}
	


}
