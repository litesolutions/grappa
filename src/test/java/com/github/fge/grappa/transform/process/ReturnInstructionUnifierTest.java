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

public class ReturnInstructionUnifierTest extends TransformationTest {

private final List<RuleMethodProcessor> processors = ImmutableList.of(
        new UnusedLabelsRemover(),
        new ReturnInstructionUnifier()
);

@BeforeClass
public void setup() throws IOException {
        initializeParserClass(TestParser.class);
}

@SuppressWarnings("unchecked")
@Test
public void testReturnInstructionUnification() throws Exception {
        assertTraceDumpEquality(processMethod("RuleWithSwitchAndAction", processors), "ILOAD 1     LOOKUPSWITCH       0: L0       default: L1    L0     ALOAD 0     GETSTATIC com/github/fge/grappa/transform/TestParser.EMPTY : Lcom/github/fge/grappa/rules/Rule;     ALOAD 0     ICONST_1     INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;     INVOKEVIRTUAL com/github/fge/grappa/transform/TestParser.push (Ljava/lang/Object;)Z     INVOKESTATIC java/lang/Boolean.valueOf (Z)Ljava/lang/Boolean;     ICONST_0     ANEWARRAY java/lang/Object     INVOKEVIRTUAL com/github/fge/grappa/transform/TestParser.sequence (Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/github/fge/grappa/rules/Rule;     GOTO L2    L1     ACONST_NULL    L2     ARETURN");

        assertTraceDumpEquality(processMethod("RuleWith2Returns", processors), "ILOAD 1     ALOAD 0     GETFIELD com/github/fge/grappa/transform/TestParser.integer : I     IF_ICMPNE L0     ALOAD 0     BIPUSH 97     INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;     ALOAD 0     INVOKEVIRTUAL com/github/fge/grappa/transform/TestParser.action ()Z     INVOKESTATIC com/github/fge/grappa/transform/TestParser.ACTION (Z)Lcom/github/fge/grappa/rules/Action;     ICONST_0     ANEWARRAY java/lang/Object     INVOKEVIRTUAL com/github/fge/grappa/transform/TestParser.sequence (Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/github/fge/grappa/rules/Rule;     GOTO L1    L0     ALOAD 0     INVOKEVIRTUAL com/github/fge/grappa/transform/TestParser.eof ()Lcom/github/fge/grappa/rules/Rule;    L1     ARETURN");

        assertTraceDumpEquality(processMethod("RuleWithDirectExplicitAction", processors), "ALOAD 0     BIPUSH 97     INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;     ALOAD 0     INVOKEVIRTUAL com/github/fge/grappa/transform/TestParser.action ()Z     IFEQ L0     ALOAD 0     GETFIELD com/github/fge/grappa/transform/TestParser.integer : I     IFLE L0     ICONST_1     GOTO L1    L0     ICONST_0    L1     INVOKESTATIC com/github/fge/grappa/transform/TestParser.ACTION (Z)Lcom/github/fge/grappa/rules/Action;     ICONST_1     ANEWARRAY java/lang/Object     DUP     ICONST_0     BIPUSH 98     INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;     AASTORE     INVOKEVIRTUAL com/github/fge/grappa/transform/TestParser.sequence (Ljava/lang/Object;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/github/fge/grappa/rules/Rule;     ARETURN");
}

}
