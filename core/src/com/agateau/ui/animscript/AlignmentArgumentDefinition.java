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
import com.badlogic.gdx.utils.Align;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Map;

class AlignmentArgumentDefinition extends ArgumentDefinition<Integer> {
    private static final Map<String, Integer> sMap = new HashMap<>();

    static {
        sMap.put("bottomLeft", Align.bottomLeft);
        sMap.put("bottomCenter", Align.bottom);
        sMap.put("bottomRight", Align.bottomRight);
        sMap.put("centerLeft", Align.left);
        sMap.put("center", Align.center);
        sMap.put("centerRight", Align.right);
        sMap.put("topLeft", Align.topLeft);
        sMap.put("topCenter", Align.top);
        sMap.put("topRight", Align.topRight);
    }

    AlignmentArgumentDefinition() {
        super(Integer.TYPE, null);
    }

    @Override
    public Object parse(StreamTokenizer tokenizer, DimensionParser dimParser)
            throws AnimScriptLoader.SyntaxException {
        try {
            tokenizer.nextToken();
        } catch (IOException e) {
            throw new AnimScriptLoader.SyntaxException(tokenizer, "Missing token for argument");
        }
        if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
            throw new AnimScriptLoader.SyntaxException(
                    tokenizer, "No value set for this argument, which has no default value");
        }
        Integer value = sMap.get(tokenizer.sval);
        if (value == null) {
            throw new AnimScriptLoader.SyntaxException(
                    tokenizer, "Invalid alignment value: " + tokenizer.sval);
        }
        return value;
    }
}
