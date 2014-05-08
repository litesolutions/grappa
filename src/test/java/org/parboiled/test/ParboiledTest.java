/*
 * Copyright (C) 2009-2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled.test;

import com.github.parboiled1.grappa.assertions.OldParsingResultAssert;
import org.parboiled.Rule;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import static org.parboiled.errors.ErrorUtils.printParseErrors;
import static org.parboiled.support.ParseTreeUtils.printNodeTree;
import static org.testng.Assert.assertEquals;

public abstract class ParboiledTest<V> {

    public class TestResult<V> {
        public final ParsingResult<V> result;
        private final OldParsingResultAssert<V> resultAssert;

        public TestResult(final ParsingResult<V> result) {
            this.result = result;
            resultAssert = OldParsingResultAssert.assertResult(result);
        }

        public TestResult<V> hasNoErrors() {
            resultAssert.hasNoErrors();
//            if (result.hasErrors()) {
//                fail("\n--- ParseErrors ---\n" +
//                        printParseErrors(result) +
//                        "\n--- ParseTree ---\n" +
//                        printNodeTree(result)
//                );
//            }
            return this;
        }

        public TestResult<V> hasErrors(final String expectedErrors) {
            assertEquals(printParseErrors(result), expectedErrors);
            return this;
        }

        public TestResult<V> hasParseTree(final String expectedTree) {
            assertEquals(printNodeTree(result), expectedTree);
            return this;
        }

        public TestResult<V> hasResult(final V... expectedResults) {
            // TODO: why are lists reversed? Seems unnatural
            resultAssert.hasStack(expectedResults);
            return this;
        }
    }

    public TestResult<V> test(final Rule rule, final String input) {
        return new TestResult<V>(new ReportingParseRunner<V>(rule).run(input));
    }
    
    public TestResult<V> test(final Rule rule, final InputBuffer inputBuffer) {
        return new TestResult<V>(new ReportingParseRunner<V>(rule).run(inputBuffer));
    }

    public TestResult<V> testWithRecovery(final Rule rule, final String input) {
        return new TestResult<V>(new RecoveringParseRunner<V>(rule).run(input));
    }
}