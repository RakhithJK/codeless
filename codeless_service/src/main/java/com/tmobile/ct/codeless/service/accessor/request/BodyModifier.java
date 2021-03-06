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
package com.tmobile.ct.codeless.service.accessor.request;

import com.tmobile.ct.codeless.core.Test;
import com.tmobile.ct.codeless.core.TestDataSource;
import com.tmobile.ct.codeless.functions.CheckFunction;
import com.tmobile.ct.codeless.service.HttpRequest;
import com.tmobile.ct.codeless.service.httpclient.Body;
import com.tmobile.ct.codeless.testdata.GetTestData;
import com.tmobile.ct.codeless.testdata.RequestModifier;

import java.util.ArrayList;


/**
 * The Class BodyModifier.
 *
 * @author Rob Graff
 */
public class BodyModifier implements RequestModifier<Body, HttpRequest>{


	private String original;
	/** The dataSource to override. */
	private ArrayList<TestDataSource> dataSource;

	/**
	 * Instantiates a new header modifier.
	 *
	 * @param dataSource dataSource
	 */
	public BodyModifier( String original, ArrayList<TestDataSource> dataSource){
		this.original = original;
		this.dataSource = dataSource;
	}

	/* (non-Javadoc)
	 * @see com.tmobile.ct.codeless.service.accessor.request.RequestModifier#modify(com.tmobile.ct.codeless.service.HttpRequest)
	 */
	@Override
	public void modify(HttpRequest request, Test test) {
		GetTestData getTestData = new GetTestData();
		String body = new CheckFunction().parse(getTestData.replaceValueWithTestData(original, dataSource));
		Body newBody = new Body(body, String.class);
		request.setBody(newBody);
	}
}
