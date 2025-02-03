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

public class ImplicitActionsConverterTest extends TransformationTest {

    private final List<RuleMethodProcessor> processors = ImmutableList.of(
            new UnusedLabelsRemover(),
            new ReturnInstructionUnifier(),
            new InstructionGraphCreator(),
            new ImplicitActionsConverter()
    );

    @BeforeClass
    public void setup() throws IOException {
        initializeParserClass(TestParser.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReturnInstructionUnification() throws Exception {
        assertTraceDumpEquality(processMethod("RuleWithIndirectImplicitAction", processors), "ALOAD 0     BIPUSH 97     INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;     BIPUSH 98     INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;     ICONST_1     ANEWARRAY java/lang/Object     DUP     ICONST_0     ALOAD 0     INVOKEVIRTUAL com/github/fge/grappa/transform/TestParser.action ()Z     IFNE L0     ALOAD 0     GETFIELD com/github/fge/grappa/transform/TestParser.integer : I     ICONST_5     IF_ICMPNE L1    L0     ICONST_1     GOTO L2    L1     ICONST_0    L2     INVOKESTATIC com/github/fge/grappa/parsers/BaseParser.ACTION (Z)Lcom/github/fge/grappa/rules/Action;     AASTORE     INVOKEVIRTUAL com/github/fge/grappa/transform/TestParser.sequence (Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/github/fge/grappa/rules/Rule;     ARETURN");

        assertTraceDumpEquality(processMethod("RuleWithDirectImplicitAction", processors), "ALOAD 0     BIPUSH 97     INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;     ALOAD 0     GETFIELD com/github/fge/grappa/transform/TestParser.integer : I     IFNE L0     ICONST_1     GOTO L1    L0     ICONST_0    L1     INVOKESTATIC com/github/fge/grappa/parsers/BaseParser.ACTION (Z)Lcom/github/fge/grappa/rules/Action;     ICONST_2     ANEWARRAY java/lang/Object     DUP     ICONST_0     BIPUSH 98     INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;     AASTORE     DUP     ICONST_1     BIPUSH 99     INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;     AASTORE     INVOKEVIRTUAL com/github/fge/grappa/transform/TestParser.sequence (Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/github/fge/grappa/rules/Rule;     ARETURN");
    }

}
