/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysds.test.functions.pipelines;

import org.apache.sysds.common.Types;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.apache.sysds.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;

public class CleaningTestCompare extends AutomatedTestBase {
	private final static String TEST_NAME1 = "testCompare";
	private final static String TEST_CLASS_DIR = SCRIPT_DIR + CleaningTestCompare.class.getSimpleName() + "/";

	protected static final String RESOURCE = SCRIPT_DIR+"functions/pipelines/";

	private final static String DIRTY = DATASET_DIR+ "pipelines/dirty.csv";
	private final static String CLEAN = DATASET_DIR+ "pipelines/clean.csv";
	private final static String META = RESOURCE+ "meta/meta_census.csv";
	private final static String OUTPUT = RESOURCE+"intermediates/";

	protected static final String PARAM_DIR = "./scripts/pipelines/properties/";
	private final static String PARAM = PARAM_DIR + "param.csv";
	private final static String PRIMITIVES = PARAM_DIR + "primitives.csv";

	@Override
	public void setUp() {
		addTestConfiguration(TEST_NAME1,new TestConfiguration(TEST_CLASS_DIR, TEST_NAME1,new String[]{"R"}));
	}

	@Test
	public void testCP1() {
		runFindPipelineTest(5,10, 2,
			true, "compare", Types.ExecMode.SINGLE_NODE);
	}

	private void runFindPipelineTest(int topk, int resources, int crossfold,
		boolean weightedAccuracy, String target, Types.ExecMode et) {

		setOutputBuffering(true);
		String HOME = SCRIPT_DIR+"functions/pipelines/" ;
		Types.ExecMode modeOld = setExecMode(et);
		try {
			loadTestConfiguration(getTestConfiguration(TEST_NAME1));
			fullDMLScriptName = HOME + TEST_NAME1 + ".dml";
			programArgs = new String[] {"-stats", "-exec", "singlenode", "-nvargs", "dirtyData="+DIRTY,
				"metaData="+META, "primitives="+PRIMITIVES, "parameters="+PARAM, "topk="+ topk, "rv="+ resources,
				"output="+OUTPUT, "target="+target, "cleanData="+CLEAN, "O="+output("O")};

			runTest(true, EXCEPTION_NOT_EXPECTED, null, -1);
			Assert.assertTrue(TestUtils.readDMLBoolean(output("O")));
		}
		finally {
			resetExecMode(modeOld);
		}
	}
}
