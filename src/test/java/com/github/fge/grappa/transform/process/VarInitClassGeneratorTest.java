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

import com.github.fge.grappa.parsers.BaseParser;
import com.github.fge.grappa.rules.Rule;
import com.google.common.collect.ImmutableList;
import com.github.fge.grappa.support.Var;
import com.github.fge.grappa.transform.generate.ActionClassGenerator;
import com.github.fge.grappa.transform.base.InstructionGroup;
import com.github.fge.grappa.transform.base.RuleMethod;
import com.github.fge.grappa.transform.generate.VarInitClassGenerator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.fge.grappa.transform.AsmTestUtils.getClassDump;
import static org.testng.Assert.assertEquals;

public class VarInitClassGeneratorTest
    extends TransformationTest
{

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

    static class Parser
        extends BaseParser<Integer>
    {

        @SuppressWarnings("UnusedDeclaration")
        public Rule A()
        {
            final Var<List<String>> list = new Var<>(new ArrayList<>());
            final Var<Integer> i = new Var<>(26);
            return sequence('a', list.get().add(match()));
        }

    }

    @BeforeClass
    public void setup()
        throws IOException
    {
        initializeParserClass(Parser.class);
    }

    @Test
    public void testVarInitClassGeneration()
        throws Exception
    {
        final RuleMethod method = processMethod("A", processors);

        assertEquals(method.getGroups().size(), 3);

        InstructionGroup group = method.getGroups().get(0);
        assertEquals(getClassDump(group.getGroupClassCode())
            .replaceAll("(?<=\\$)[A-Za-z0-9]{16}", "XXXXXXXXXXXXXXXX").replaceAll("\\s+", " "), "// class version 52.0 (52) // access flags 0x1011 public final synthetic class com/github/fge/grappa/transform/process/VarInit$XXXXXXXXXXXXXXXX extends com/github/fge/grappa/transform/runtime/BaseVarInit { // access flags 0x1 public <init>(Ljava/lang/String;)V ALOAD 0 ALOAD 1 INVOKESPECIAL com/github/fge/grappa/transform/runtime/BaseVarInit.<init> (Ljava/lang/String;)V RETURN MAXSTACK = 2 MAXLOCALS = 2 // access flags 0x1 public get()Ljava/lang/Object; NEW java/util/ArrayList DUP INVOKESPECIAL java/util/ArrayList.<init> ()V ARETURN MAXSTACK = 2 MAXLOCALS = 1 } ");

        group = method.getGroups().get(1);
        assertEquals(getClassDump(group.getGroupClassCode())
            .replaceAll("(?<=\\$)[A-Za-z0-9]{16}", "XXXXXXXXXXXXXXXX").replaceAll("\\s+", " "), "// class version 52.0 (52) // access flags 0x1011 public final synthetic class com/github/fge/grappa/transform/process/VarInit$XXXXXXXXXXXXXXXX extends com/github/fge/grappa/transform/runtime/BaseVarInit { // access flags 0x1 public <init>(Ljava/lang/String;)V ALOAD 0 ALOAD 1 INVOKESPECIAL com/github/fge/grappa/transform/runtime/BaseVarInit.<init> (Ljava/lang/String;)V RETURN MAXSTACK = 2 MAXLOCALS = 2 // access flags 0x1 public get()Ljava/lang/Object; BIPUSH 26 INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer; ARETURN MAXSTACK = 1 MAXLOCALS = 1 } ");

        group = method.getGroups().get(2);
        assertEquals(getClassDump(group.getGroupClassCode())
            .replaceAll("(?<=\\$)[A-Za-z0-9]{16}", "XXXXXXXXXXXXXXXX").replaceAll("\\s+", " "), "// class version 52.0 (52) // access flags 0x1011 public final synthetic class com/github/fge/grappa/transform/process/Action$XXXXXXXXXXXXXXXX extends com/github/fge/grappa/transform/runtime/BaseAction { // access flags 0x1001 public synthetic Lcom/github/fge/grappa/support/Var; field$0 // access flags 0x1001 public synthetic Lcom/github/fge/grappa/transform/process/VarInitClassGeneratorTest$Parser$$grappa; field$1 // access flags 0x1 public <init>(Ljava/lang/String;)V ALOAD 0 ALOAD 1 INVOKESPECIAL com/github/fge/grappa/transform/runtime/BaseAction.<init> (Ljava/lang/String;)V RETURN MAXSTACK = 2 MAXLOCALS = 2 // access flags 0x1 public run(Lcom/github/fge/grappa/run/context/Context;)Z ALOAD 0 GETFIELD com/github/fge/grappa/transform/process/Action$XXXXXXXXXXXXXXXX.field$0 : Lcom/github/fge/grappa/support/Var; INVOKEVIRTUAL com/github/fge/grappa/support/Var.get ()Ljava/lang/Object; CHECKCAST java/util/List ALOAD 0 GETFIELD com/github/fge/grappa/transform/process/Action$XXXXXXXXXXXXXXXX.field$1 : Lcom/github/fge/grappa/transform/process/VarInitClassGeneratorTest$Parser$$grappa; DUP ALOAD 1 INVOKEINTERFACE com/github/fge/grappa/run/context/ContextAware.setContext (Lcom/github/fge/grappa/run/context/Context;)V (itf) INVOKEVIRTUAL com/github/fge/grappa/transform/process/VarInitClassGeneratorTest$Parser.match ()Ljava/lang/String; INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z (itf) IRETURN MAXSTACK = 4 MAXLOCALS = 2 } ");
    }

}
