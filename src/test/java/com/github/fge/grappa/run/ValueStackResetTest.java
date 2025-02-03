package com.github.fge.grappa.run;

import com.github.fge.grappa.Grappa;
import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.stack.ValueStack;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public final class ValueStackResetTest
{
    private static final Object VALUE1 = new Object();
    private static final Object VALUE2 = new Object();

    public static final Supplier<Object> SUPPLIER = new Supplier<Object>()
    {
        private boolean firstConsumed = false;

        @Override
        public Object get()
        {
            if (firstConsumed)
                return VALUE2;

            firstConsumed = true;
            return VALUE1;
        }
    };

    @SuppressWarnings("AutoBoxing")
    static class TestParser
        extends BaseParser<Object>
    {
        public Rule rule()
        {
            return sequence(ANY, push(SUPPLIER.get()));
        }
    }

    @Test
    public void stackIsClearedBetweenParsingRuns()
    {
        final TestParser parser = Grappa.createParser(TestParser.class);

        final ParseRunner<Object> runner = new ParseRunner<>(parser.rule());

        ParsingResult<Object> result;
        ValueStack<Object> stack;
        Object actual;
        Object expected;

        result = runner.run("a");
        stack = result.getValueStack();

        assertThat(stack).hasSize(1);

        expected = VALUE1;
        actual = stack.peek();

        assertThat(actual).isSameAs(expected);

        result = runner.run("a");
        stack = result.getValueStack();

        assertThat(stack).hasSize(1);

        expected = VALUE2;
        actual = stack.peek();

        assertThat(actual).isSameAs(expected);
    }
}
