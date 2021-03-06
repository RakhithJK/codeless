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
package com.tmobile.ct.codeless.service.model.postman;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;

import com.tmobile.ct.codeless.files.ClassPathUtil;
import com.tmobile.ct.codeless.service.HttpRequest;
import com.tmobile.ct.codeless.service.HttpRequestImpl;
import com.tmobile.ct.codeless.service.httpclient.Body;
import com.tmobile.ct.codeless.service.httpclient.Cookies;
import com.tmobile.ct.codeless.service.httpclient.Endpoint;
import com.tmobile.ct.codeless.service.httpclient.Forms;
import com.tmobile.ct.codeless.service.httpclient.Header;
import com.tmobile.ct.codeless.service.httpclient.Headers;
import com.tmobile.ct.codeless.service.httpclient.Host;
import com.tmobile.ct.codeless.service.httpclient.HttpMethod;
import com.tmobile.ct.codeless.service.httpclient.HttpProtocal;
import com.tmobile.ct.codeless.service.httpclient.OperationPath;
import com.tmobile.ct.codeless.service.httpclient.PathParams;
import com.tmobile.ct.codeless.service.httpclient.QueryParam;
import com.tmobile.ct.codeless.service.httpclient.QueryParams;
import com.tmobile.ct.codeless.service.httpclient.ServicePath;
import com.tmobile.ct.codeless.service.model.postman.collection.PostmanCollection;
import com.tmobile.ct.codeless.service.model.postman.collection.PostmanItem;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class PostmanParser.
 *
 * @author Rob Graff
 */
public class PostmanParser {

	/** The requests. */
	List<HttpRequest> requests = new ArrayList<>();
	
	/**
	 * Parses the.
	 *
	 * @param resource the resource
	 * @return the list
	 */
	public List<HttpRequest> parse(String resource){
		
		PostmanReader reader = new PostmanReader();
		
		PostmanCollection collection = null;
		
		try {
			collection = reader.readCollectionFile(ClassPathUtil.getAbsolutePath(resource));
			collection.init();
			parseCollection(collection);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return requests;
	}
	
	/**
	 * Parses the collection.
	 *
	 * @param collection the collection
	 */
	private void parseCollection(PostmanCollection collection) {
//		collection.item.forEach( folder -> {
		collection.item.forEach( item ->{
				parseRequest(item);
			});;
//		});
		
	}
	
	/**
	 * Parses the request.
	 *
	 * @param item the item
	 */
	private void parseRequest(PostmanItem item) {
		if (item.item != null) {
			for (PostmanItem childItem : item.item){
				childItem.folderName = StringUtils.isBlank(item.folderName) ? item.name : item.folderName + "." + item.name;
				parseRequest(childItem);
			}

			return;
		}

		HttpRequest<String> req = new HttpRequestImpl<>();
		
		req.setHttpMethod(HttpMethod.valueOf(item.request.method));
		
		req.setRequestName(StringUtils.isBlank(item.folderName)
							? item.name
							: item.folderName + "." + item.name);
		req.setEndpoint(new Endpoint(item.request.url.raw));
		req.setPort(Optional.ofNullable(item.request.url.port).map(Integer::valueOf).orElse(null));
		
		if(CollectionUtils.isNotEmpty(item.request.url.path)){
			req.setOperationPath(new OperationPath(item.request.url.path.get(0)));
		}
		
		if(req.getHost() == null && req.getEndpoint() != null){
			try {
				
				if(!req.getEndpoint().getValue().toUpperCase().startsWith("HTTP://") && !req.getEndpoint().getValue().toUpperCase().startsWith("HTTPS://")){
					req.setEndpoint(new Endpoint("http://"+req.getEndpoint().getValue()));
				}
				
				URL url = new URL(req.getEndpoint().getValue());
				req.setProtocal(Optional.ofNullable(url.getProtocol()).map(String::toUpperCase).map(HttpProtocal::valueOf).orElse(HttpProtocal.HTTP));
				if (req.getEndpoint().getValue().toUpperCase().startsWith("HTTP://")) {
					req.setHost(new Host("http://"+url.getHost()));
				}
				else if (req.getEndpoint().getValue().toUpperCase().startsWith("HTTPS://")){
					req.setHost(new Host("https://"+url.getHost()));
				}
				if(url.getPort() > 0){
					req.setPort(url.getPort());
				}
				req.setOperationPath(new OperationPath(url.getPath()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		QueryParams queryParams = new QueryParams();
		Headers headerParams = new Headers();
		
		//query params
		Optional.ofNullable(item.request.url.query).ifPresent(x -> x.forEach(
				query -> {
			queryParams.put(query.key, new QueryParam(query.key, query.value));
		}));
		
		//headers
		item.request.header.forEach(header ->{
			headerParams.put(header.key, new Header(header.key, header.value));
		});
		
		req.setQueryParams(queryParams);
		req.setHeaders(headerParams);
		
		/* TODO test body as string, what about different body types... */
		if (item.request.body != null) {
			req.setBody(new Body<>(item.request.body.raw, String.class));
		}
		
		requests.add(req);
		
	}
}
