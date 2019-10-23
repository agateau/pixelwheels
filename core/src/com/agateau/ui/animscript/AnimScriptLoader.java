/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.agateau.ui.animscript;

import com.agateau.ui.DimensionParser;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AnimScriptLoader {
    private final Map<String, InstructionDefinition> mInstructionDefinitionMap = new HashMap<>();

    public static class SyntaxException extends Exception {
        SyntaxException(StreamTokenizer tokenizer, String message) {
            super(String.format(Locale.US, "line %d: %s", tokenizer.lineno(), message));
        }
    }

    public AnimScriptLoader() {
        registerAction(
                "moveTo",
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DIMENSION),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DIMENSION),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DURATION, 0),
                new InterpolationArgumentDefinition(Interpolation.linear));
        registerAction(
                "moveToAligned",
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DIMENSION),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DIMENSION),
                new AlignmentArgumentDefinition(),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DURATION, 0),
                new InterpolationArgumentDefinition(Interpolation.linear));
        registerAction(
                "moveBy",
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DIMENSION),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DIMENSION),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DURATION, 0),
                new InterpolationArgumentDefinition(Interpolation.linear));
        registerAction(
                "rotateTo",
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.SCALAR),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DURATION, 0),
                new InterpolationArgumentDefinition(Interpolation.linear));
        registerAction(
                "rotateBy",
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.SCALAR),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DURATION, 0),
                new InterpolationArgumentDefinition(Interpolation.linear));
        registerAction(
                "scaleTo",
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.SCALAR),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.SCALAR),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DURATION, 0),
                new InterpolationArgumentDefinition(Interpolation.linear));
        registerAction(
                "sizeTo",
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DIMENSION),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DIMENSION),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DURATION, 0),
                new InterpolationArgumentDefinition(Interpolation.linear));
        registerAction(
                "alpha",
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.SCALAR),
                new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DURATION, 0),
                new InterpolationArgumentDefinition(Interpolation.linear));
        registerAction(
                "delay", new FloatArgumentDefinition(FloatArgumentDefinition.Domain.DURATION));
        mInstructionDefinitionMap.put("parallel", new ParallelInstructionDefinition(this));
        mInstructionDefinitionMap.put("repeat", new RepeatInstructionDefinition(this));
    }

    public AnimScript load(String definition, DimensionParser dimParser) throws SyntaxException {
        Reader reader = new StringReader(definition);
        return load(reader, dimParser);
    }

    private AnimScript load(Reader reader, DimensionParser dimParser) throws SyntaxException {
        StreamTokenizer tokenizer = new StreamTokenizer(reader);
        tokenizer.eolIsSignificant(true);
        tokenizer.slashSlashComments(true);
        tokenizer.slashStarComments(true);
        // We want to parse numbers ourselves for dimensions: "100px" should be a string token, not
        // a "100" float token followed by a "px" string token
        // Unfortunately you can't really disable StreamTokenizer number parsing without resetting
        // the syntax and redefining all chars
        tokenizer.resetSyntax();
        tokenizer.whitespaceChars(0, ' ');
        tokenizer.wordChars('-', '-');
        tokenizer.wordChars('.', '.');
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars('0', '9');
        tokenizer.commentChar('/');
        tokenizer.quoteChar('"');
        tokenizer.quoteChar('\'');
        Array<Instruction> lst = tokenize(tokenizer, null, dimParser);
        return new AnimScript(lst);
    }

    Array<Instruction> tokenize(StreamTokenizer tokenizer, String end, DimensionParser dimParser)
            throws SyntaxException {
        Array<Instruction> lst = new Array<>();
        do {
            try {
                while (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {}
            } catch (IOException e) {
                throw new SyntaxException(tokenizer, "Unexpected end of line");
            }
            if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
                break;
            }
            if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
                throw new SyntaxException(
                        tokenizer,
                        String.format(
                                "Unexpected token type %d, (sval='%s')",
                                tokenizer.ttype, tokenizer.sval));
            }
            String cmd = tokenizer.sval;
            assert (cmd != null);
            if (end != null && cmd.equals(end)) {
                break;
            }
            InstructionDefinition def = mInstructionDefinitionMap.get(cmd);
            if (def == null) {
                throw new SyntaxException(tokenizer, "Unknown command '" + cmd + "'");
            }
            Instruction instruction = def.parse(tokenizer, dimParser);
            lst.add(instruction);
        } while (tokenizer.ttype != StreamTokenizer.TT_EOF);
        return lst;
    }

    private void registerStaticMethod(
            String name, Class<?> methodClass, String methodName, ArgumentDefinition<?>... types) {
        Method method = getMethod(methodClass, methodName, types);
        mInstructionDefinitionMap.put(name, new BasicInstructionDefinition(method, types));
    }

    public void registerMemberMethod(
            String name, Object object, String methodName, ArgumentDefinition<?>... types) {
        Method method = getMethod(object.getClass(), methodName, types);
        mInstructionDefinitionMap.put(name, new BasicInstructionDefinition(object, method, types));
    }

    private static Method getMethod(
            Class<?> methodClass, String name, ArgumentDefinition<?>... types) {
        Class<?>[] args = new Class<?>[types.length];
        for (int idx = 0; idx < types.length; ++idx) {
            args[idx] = types[idx].javaType;
        }
        try {
            return methodClass.getDeclaredMethod(name, args);
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
            throw new RuntimeException();
        } catch (SecurityException e1) {
            e1.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void registerAction(String name, ArgumentDefinition<?>... types) {
        registerStaticMethod(name, Actions.class, name, types);
    }
}
