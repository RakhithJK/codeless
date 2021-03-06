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
package com.tmobile.ct.codeless.test.excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.tmobile.ct.codeless.core.Step;
import com.tmobile.ct.codeless.core.Suite;
import com.tmobile.ct.codeless.core.Test;
import com.tmobile.ct.codeless.core.TestBuilder;
import com.tmobile.ct.codeless.core.TestData;
import com.tmobile.ct.codeless.core.config.Config;
import com.tmobile.ct.codeless.core.datastructure.MultiValue;
import com.tmobile.ct.codeless.data.BasicTestData;
import com.tmobile.ct.codeless.service.test.build.ServiceCallInput;
import com.tmobile.ct.codeless.service.test.build.ServiceStepBuilder;
import com.tmobile.ct.codeless.test.component.ComponentCache;
import com.tmobile.ct.codeless.test.config.CloneConfig;
import com.tmobile.ct.codeless.test.suite.TestImpl;
import com.tmobile.ct.codeless.test.tcds.BuildTcdsDataSource;
import com.tmobile.ct.codeless.ui.build.UiStepBuilder;
import com.tmobile.ct.codeless.ui.build.UiStepInput;

/**
 * The Class ExcelTestBuilder.
 *
 * @author Rob Graff
 */
public class ExcelTestBuilder implements TestBuilder{

	/** The formatter. */
	private DataFormatter formatter = new DataFormatter();

	/** The test. */
	TestImpl test = new TestImpl();

	/** The data. */
	TestData data = new BasicTestData();

	/**
	 * Builds the.
	 *
	 * @param suite the suite
	 * @param sheet the sheet
	 * @param name the name
	 * @return the test
	 */
	public Test build(Suite suite, Sheet sheet, String name, TestData testData){
		test.setSuite(suite);
		test.setName(name);
		test.setConfig(CloneConfig.getConfig(suite.getConfig()));
		test.setTestData(testData);

		int count = -1;
		for(Row row: sheet){
			count++;
			if(count <1) continue;
			List<Step> steps = parseRow(row);
		}

		return test;
	}

	public List<Step> build(Sheet sheet, Test test){
		this.test = (TestImpl) test;
		List<Step> steps = new ArrayList<>();
		int count = -1;
		for(Row row: sheet){
			count++;
			if(count <2) continue;
			List<Step> sp = parseRow(row);
			if(sp!=null) {
				steps.addAll(sp);
			}
		}
		return steps;
	}

	/**
	 * Parses the row.
	 *
	 * @param row the row
	 * @return the step
	 */
	private List<Step> parseRow(Row row) {
		if (row.getCell(0) == null){
			return null;
		}

		if (row.getCell(0) != null) {

			String stepName = row.getCell(0).getStringCellValue();
			if (stepName == null || stepName == "" || stepName.startsWith("#")) {
				return null;
			}
		}
		List<Step> steps = new ArrayList<>();

		if (getSafeStringFromCell(row.getCell(1)).equalsIgnoreCase("SERVICECALL")) {
			steps.add(buildServiceStep(test, row));
		} else if (getSafeStringFromCell(row.getCell(1)).equalsIgnoreCase("CONFIG")) {
			parseConfigStep(row);
			parseTcdsData(row);
			return null;
		} else if(getSafeStringFromCell(row.getCell(1)).equalsIgnoreCase("COMPONENT")) {
			steps.addAll(ComponentCache.getComponent(getSafeStringFromCell(row.getCell(2)),test));
			steps.forEach(x -> x.setTest(test));
			return steps;
		} else {
			steps.add(buildUiStep(test, row));
		}

		if(steps != null) {
			steps.forEach(step -> {
				step.setTest(test);
				test.addStep(step);
			});
		}

		return steps;
	}

	/**
	 * Parses the config step.
	 *
	 * @param row the row
	 */
	private void parseConfigStep(Row row) {
		for (Cell cell : row) {
			String cellvalue = getSafeStringFromCell(cell);
			if (getSafeStringFromCell(cell).contains("::")) {
				String[] parts = cellvalue.split("::");
				if (!cellvalue.contains("webdriver") && parts.length == 2) {
					test.getConfig().put(parts[0], parts[1]);
				}
			}
		}
	}
	

	private void parseTcdsData(Row row) {
		for (Cell cell : row) {
			String cellvalue = getSafeStringFromCell(cell);
			if (cellvalue.equalsIgnoreCase(Config.TESTDATA + "::" + Config.TCDS)
					|| cellvalue.equalsIgnoreCase(Config.TCDS)) {
				String testFileName = test.getName();
				if (!StringUtils.isEmpty(testFileName)) {
					test.setTcdsData(true);
					try {
						new BuildTcdsDataSource(test.getTestData()).parseTcdsTestData(testFileName);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Gets the safe string from cell.
	 *
	 * @param cell the cell
	 * @return the safe string from cell
	 */
	private String getSafeStringFromCell(Cell cell){
		return formatter.formatCellValue(cell).trim().replace(" ", "");
	}

	@Override
	public Test getTest() {
		return test;
	}

	private Step buildServiceStep(Test test, Row row) {

		ServiceStepBuilder serviceStepBuilder = new ServiceStepBuilder();
		ServiceCallInput input = new ServiceCallInput();

		for (Cell cell : row) {
			String header = formatter.formatCellValue(cell.getSheet().getRow(0).getCell(cell.getColumnIndex())).trim()
					.toUpperCase();
			String value = formatter.formatCellValue(cell);
			serviceStepBuilder.buildServiceStep(header, value, input, test);
		}

		return serviceStepBuilder.build(test, input);
	}

	private Step buildUiStep(Test test, Row row) {

		UiStepBuilder uiStepBuilder = new UiStepBuilder();
		UiStepInput input = new UiStepInput();

		for (Cell cell : row) {
			String header = formatter.formatCellValue(cell.getSheet().getRow(0).getCell(cell.getColumnIndex())).trim()
					.toUpperCase();
			String value = formatter.formatCellValue(cell);
			input.add(header, new MultiValue<String, String>(header, value));
		}
		return uiStepBuilder.build(input, test);
	}

}
