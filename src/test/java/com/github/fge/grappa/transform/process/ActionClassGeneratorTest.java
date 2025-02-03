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
import com.github.fge.grappa.transform.generate.ActionClassGenerator;
import com.github.fge.grappa.transform.base.InstructionGroup;
import com.github.fge.grappa.transform.base.RuleMethod;
import com.github.fge.grappa.transform.TestParser;
import com.github.fge.grappa.transform.generate.VarInitClassGenerator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static com.github.fge.grappa.transform.AsmTestUtils.getClassDump;
import static org.testng.Assert.assertEquals;

public class ActionClassGeneratorTest extends TransformationTest {

    private final List<RuleMethodProcessor> processors = ImmutableList.of(
            new UnusedLabelsRemover(),
            new ReturnInstructionUnifier(),
            new InstructionGraphCreator(),
            new ImplicitActionsConverter(),
            new InstructionGroupCreator(),
            new InstructionGroupPreparer(),
            new ActionClassGenerator(true),
            new VarInitClassGenerator(true)
    );


    @BeforeClass
    public void setup() throws IOException {
        initializeParserClass(TestParser.class);
    }

    @Test
    public void testActionClassGeneration() throws Exception {
        final RuleMethod method = processMethod("RuleWithComplexActionSetup", processors);

        assertEquals(method.getGroups().size(), 3);

        InstructionGroup group = method.getGroups().get(0);
        assertEquals(getClassDump(group.getGroupClassCode())
            .replaceAll("(?<=\\$)[A-Za-z0-9]{16}", "XXXXXXXXXXXXXXXX").replaceAll("\\s+", " ") , "// class version 52.0 (52) // access flags 0x1011 public final synthetic class com/github/fge/grappa/transform/VarInit$XXXXXXXXXXXXXXXX extends com/github/fge/grappa/transform/runtime/BaseVarInit { // access flags 0x1 public <init>(Ljava/lang/String;)V ALOAD 0 ALOAD 1 INVOKESPECIAL com/github/fge/grappa/transform/runtime/BaseVarInit.<init> (Ljava/lang/String;)V RETURN MAXSTACK = 2 MAXLOCALS = 2 // access flags 0x1 public get()Ljava/lang/Object; LDC \"text\" ARETURN MAXSTACK = 1 MAXLOCALS = 1 } ");

        group = method.getGroups().get(1);
        assertEquals(getClassDump(group.getGroupClassCode())
            .replaceAll("(?<=\\$)[A-Za-z0-9]{16}", "XXXXXXXXXXXXXXXX").replaceAll("\\s+", " "), "// class version 52.0 (52) // access flags 0x1011 public final synthetic class com/github/fge/grappa/transform/Action$XXXXXXXXXXXXXXXX extends com/github/fge/grappa/transform/runtime/BaseAction { // access flags 0x1001 public synthetic I field$0 // access flags 0x1001 public synthetic I field$1 // access flags 0x1001 public synthetic I field$2 // access flags 0x1 public <init>(Ljava/lang/String;)V ALOAD 0 ALOAD 1 INVOKESPECIAL com/github/fge/grappa/transform/runtime/BaseAction.<init> (Ljava/lang/String;)V RETURN MAXSTACK = 2 MAXLOCALS = 2 // access flags 0x1 public run(Lcom/github/fge/grappa/run/context/Context;)Z ALOAD 0 GETFIELD com/github/fge/grappa/transform/Action$XXXXXXXXXXXXXXXX.field$0 : I ALOAD 0 GETFIELD com/github/fge/grappa/transform/Action$XXXXXXXXXXXXXXXX.field$1 : I ALOAD 0 GETFIELD com/github/fge/grappa/transform/Action$XXXXXXXXXXXXXXXX.field$2 : I IADD IF_ICMPLE L0 ICONST_1 GOTO L1 L0 FRAME SAME ICONST_0 L1 FRAME SAME1 I IRETURN MAXSTACK = 3 MAXLOCALS = 2 } ");

        group = method.getGroups().get(2);
        assertEquals(getClassDump(group.getGroupClassCode())
            .replaceAll("(?<=\\$)[A-Za-z0-9]{16}", "XXXXXXXXXXXXXXXX").replaceAll("\\s+", " "), "// class version 52.0 (52) // access flags 0x1011 public final synthetic class com/github/fge/grappa/transform/Action$XXXXXXXXXXXXXXXX extends com/github/fge/grappa/transform/runtime/BaseAction { // access flags 0x1001 public synthetic Lcom/github/fge/grappa/transform/TestParser$$grappa; field$0 // access flags 0x1001 public synthetic I field$1 // access flags 0x1001 public synthetic Lcom/github/fge/grappa/support/Var; field$2 // access flags 0x1001 public synthetic I field$3 // access flags 0x1001 public synthetic I field$4 // access flags 0x1 public <init>(Ljava/lang/String;)V ALOAD 0 ALOAD 1 INVOKESPECIAL com/github/fge/grappa/transform/runtime/BaseAction.<init> (Ljava/lang/String;)V RETURN MAXSTACK = 2 MAXLOCALS = 2 // access flags 0x1 public run(Lcom/github/fge/grappa/run/context/Context;)Z ALOAD 0 GETFIELD com/github/fge/grappa/transform/Action$XXXXXXXXXXXXXXXX.field$0 : Lcom/github/fge/grappa/transform/TestParser$$grappa; GETFIELD com/github/fge/grappa/transform/TestParser.integer : I ALOAD 0 GETFIELD com/github/fge/grappa/transform/Action$XXXXXXXXXXXXXXXX.field$1 : I IADD ALOAD 0 GETFIELD com/github/fge/grappa/transform/Action$XXXXXXXXXXXXXXXX.field$2 : Lcom/github/fge/grappa/support/Var; INVOKEVIRTUAL com/github/fge/grappa/support/Var.get ()Ljava/lang/Object; CHECKCAST java/lang/String INVOKEVIRTUAL java/lang/String.length ()I ALOAD 0 GETFIELD com/github/fge/grappa/transform/Action$XXXXXXXXXXXXXXXX.field$3 : I ISUB ALOAD 0 GETFIELD com/github/fge/grappa/transform/Action$XXXXXXXXXXXXXXXX.field$4 : I ISUB IF_ICMPGE L0 ICONST_1 GOTO L1 L0 FRAME SAME ICONST_0 L1 FRAME SAME1 I IRETURN MAXSTACK = 3 MAXLOCALS = 2 } ");
    }

}
