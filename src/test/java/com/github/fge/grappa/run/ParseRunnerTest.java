/*
 * Copyright (C) 2014 Francis Galiegue <fgaliegue@gmail.com>
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

package com.github.fge.grappa.run;

import com.github.fge.grappa.Grappa;
import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class ParseRunnerTest
{
    static class SimpleParser
        extends BaseParser<Object>
    {
        Rule rule()
        {
            return oneOrMore('a');
        }
    }

    private final SimpleParser parser
        = Grappa.createParser(SimpleParser.class);
    private final ParseRunner<Object> runner = new ParseRunner<>(parser.rule());

    @Test
    public void basicParseRunnerCanReliablyReportErrors()
    {
        assertThat(runner.run("bbb").isSuccess()).as("errors are reported")
            .isFalse();
    }
}
