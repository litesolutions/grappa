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

package org.parboiled.parserunners;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.test.ParboiledTest;
import org.testng.annotations.Test;

public class RecoveryErrorActionsTest extends ParboiledTest<Object>
{

    @BuildParseTree
    public static class Parser extends BaseParser<Object> {

        Rule Clause() {
            return sequence(Seq(), EOI);
        }

        Rule Seq() {
            return sequence(A(), B(), C(), D());
        }

        Rule A() {
            return sequence('a', push(match()));
        }

        Rule B() {
            return sequence('b', push(match()));
        }

        Rule C() {
            return sequence('c', push(1));
        }

        Rule D() {
            return sequence('d', push(2.0));
        }
    }

    @Test
    public void testRecoveryErrorActions1() {
        final Parser parser = Parboiled.createParser(Parser.class);
        testWithRecovery(parser.Clause(), "abcd")
                .hasNoErrors()
                .hasResult("a", "b", 1, 2.0);
    }

    @Test
    public void testRecoveryErrorActions2() {
        final Parser parser = Parboiled.createParser(Parser.class);
        testWithRecovery(parser.Clause(), "axcd")
            .hasErrors(
                "Invalid input 'x', expected one of: [B] (line 1, pos 2):\n" +
                "axcd\n" +
                " ^\n"
            ).hasResult("a", "b", 1, 2.0);
    }
    
    @Test
    public void testRecoveryErrorActions3() {
        final Parser parser = Parboiled.createParser(Parser.class);
        testWithRecovery(parser.Clause(), "axx")
            .hasErrors(
                "Invalid input 'x...', expected one of: [B] (line 1, pos 2):\n"
                + "axx\n ^^\n"
            ).hasResult("a", "", 1, 2.0);
    }
    
    @Test
    public void testRecoveryErrorActions4() {
        final Parser parser = Parboiled.createParser(Parser.class);
        testWithRecovery(parser.Clause(), "abx")
            .hasErrors(
                "Invalid input 'x', expected one of: [C] (line 1, pos 3):\n" +
                "abx\n" +
                "  ^\n"
            ).hasResult("a", "b", 1, 2.0);
    }
    
    @Test
    public void testRecoveryErrorActions5() {
        final Parser parser = Parboiled.createParser(Parser.class);
        testWithRecovery(parser.Clause(), "abxyz")
            .hasErrors(
                "Invalid input 'x...', expected one of: [C] (line 1, pos 3):\n"
                + "abxyz\n  ^^^\n"
            ).hasResult("a", "b", 1, 2.0);
    }

    @Test
    public void testRecoveryOnEmptyBuffer() {
        final Parser parser = Parboiled.createParser(Parser.class);
        testWithRecovery(parser.Clause(), "")
            .hasErrors(
                "Unexpected end of input, expected one of: [Clause]"
                + " (line 1, pos 1):\n\n^\n")
            .hasParseTree("[Clause]E\n")
            .hasResult("", "", 1, 2.0);
    }
}