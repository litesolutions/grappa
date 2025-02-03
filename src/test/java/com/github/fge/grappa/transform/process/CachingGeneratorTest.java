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

package com.github.fge.grappa.transform.process;

import com.google.common.collect.ImmutableList;
import com.github.fge.grappa.transform.TestParser;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static com.github.fge.grappa.transform.AsmTestUtils.assertTraceDumpEquality;

public class CachingGeneratorTest extends TransformationTest {

private final List<RuleMethodProcessor> processors = ImmutableList.of(
        new BodyWithSuperCallReplacer(),
        new LabellingGenerator(),
        new CachingGenerator()
);

@BeforeClass
public void setup() throws IOException {
        initializeParserClass(TestParser.class);
}

@SuppressWarnings("unchecked")
@Test
public void test() throws Exception {

        assertTraceDumpEquality(processMethod("RuleWithoutAction", processors), "ALOAD 0     GETFIELD com/github/fge/grappa/transform/TestParser$$grappa.cache$RuleWithoutAction : Lcom/github/fge/grappa/rules/Rule;     DUP     IFNULL L0     ARETURN    L0     POP     NEW com/github/fge/grappa/matchers/wrap/ProxyMatcher     DUP     INVOKESPECIAL com/github/fge/grappa/matchers/wrap/ProxyMatcher.<init> ()V     DUP     ALOAD 0     SWAP     PUTFIELD com/github/fge/grappa/transform/TestParser$$grappa.cache$RuleWithoutAction : Lcom/github/fge/grappa/rules/Rule;     ALOAD 0     INVOKESPECIAL com/github/fge/grappa/transform/TestParser.RuleWithoutAction ()Lcom/github/fge/grappa/rules/Rule;     DUP     IFNULL L1     LDC \"RuleWithoutAction\"     INVOKEINTERFACE com/github/fge/grappa/rules/Rule.label (Ljava/lang/String;)Lcom/github/fge/grappa/rules/Rule; (itf)    L1     DUP_X1     CHECKCAST com/github/fge/grappa/matchers/base/Matcher     INVOKEVIRTUAL com/github/fge/grappa/matchers/wrap/ProxyMatcher.arm (Lcom/github/fge/grappa/matchers/base/Matcher;)V     DUP     ALOAD 0     SWAP     PUTFIELD com/github/fge/grappa/transform/TestParser$$grappa.cache$RuleWithoutAction : Lcom/github/fge/grappa/rules/Rule;     ARETURN");

        assertTraceDumpEquality(processMethod("RuleWithNamedLabel", processors), "@Lcom/github/fge/grappa/annotations/Label;(value=\"harry\")     ALOAD 0     GETFIELD com/github/fge/grappa/transform/TestParser$$grappa.cache$RuleWithNamedLabel : Lcom/github/fge/grappa/rules/Rule;     DUP     IFNULL L0     ARETURN    L0     POP     NEW com/github/fge/grappa/matchers/wrap/ProxyMatcher     DUP     INVOKESPECIAL com/github/fge/grappa/matchers/wrap/ProxyMatcher.<init> ()V     DUP     ALOAD 0     SWAP     PUTFIELD com/github/fge/grappa/transform/TestParser$$grappa.cache$RuleWithNamedLabel : Lcom/github/fge/grappa/rules/Rule;     ALOAD 0     INVOKESPECIAL com/github/fge/grappa/transform/TestParser.RuleWithNamedLabel ()Lcom/github/fge/grappa/rules/Rule;     DUP     IFNULL L1     LDC \"harry\"     INVOKEINTERFACE com/github/fge/grappa/rules/Rule.label (Ljava/lang/String;)Lcom/github/fge/grappa/rules/Rule; (itf)    L1     DUP_X1     CHECKCAST com/github/fge/grappa/matchers/base/Matcher     INVOKEVIRTUAL com/github/fge/grappa/matchers/wrap/ProxyMatcher.arm (Lcom/github/fge/grappa/matchers/base/Matcher;)V     DUP     ALOAD 0     SWAP     PUTFIELD com/github/fge/grappa/transform/TestParser$$grappa.cache$RuleWithNamedLabel : Lcom/github/fge/grappa/rules/Rule;     ARETURN");

        assertTraceDumpEquality(processMethod("RuleWithLeaf", processors), "ALOAD 0     GETFIELD com/github/fge/grappa/transform/TestParser$$grappa.cache$RuleWithLeaf : Lcom/github/fge/grappa/rules/Rule;     DUP     IFNULL L0     ARETURN    L0     POP     NEW com/github/fge/grappa/matchers/wrap/ProxyMatcher     DUP     INVOKESPECIAL com/github/fge/grappa/matchers/wrap/ProxyMatcher.<init> ()V     DUP     ALOAD 0     SWAP     PUTFIELD com/github/fge/grappa/transform/TestParser$$grappa.cache$RuleWithLeaf : Lcom/github/fge/grappa/rules/Rule;     ALOAD 0     INVOKESPECIAL com/github/fge/grappa/transform/TestParser.RuleWithLeaf ()Lcom/github/fge/grappa/rules/Rule;     DUP     IFNULL L1     LDC \"RuleWithLeaf\"     INVOKEINTERFACE com/github/fge/grappa/rules/Rule.label (Ljava/lang/String;)Lcom/github/fge/grappa/rules/Rule; (itf)    L1     DUP_X1     CHECKCAST com/github/fge/grappa/matchers/base/Matcher     INVOKEVIRTUAL com/github/fge/grappa/matchers/wrap/ProxyMatcher.arm (Lcom/github/fge/grappa/matchers/base/Matcher;)V     DUP     ALOAD 0     SWAP     PUTFIELD com/github/fge/grappa/transform/TestParser$$grappa.cache$RuleWithLeaf : Lcom/github/fge/grappa/rules/Rule;     ARETURN");
}

}
