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
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Map;

public class InterpolationArgumentDefinition extends ArgumentDefinition<Interpolation> {

    private static final Map<String, Interpolation> sMap = new HashMap<>();

    static {
        sMap.put("bounce", Interpolation.bounce);
        sMap.put("bounceIn", Interpolation.bounceIn);
        sMap.put("bounceOut", Interpolation.bounceOut);
        sMap.put("circle", Interpolation.circle);
        sMap.put("circleIn", Interpolation.circleIn);
        sMap.put("circleOut", Interpolation.circleOut);
        sMap.put("elastic", Interpolation.elastic);
        sMap.put("elasticIn", Interpolation.elasticIn);
        sMap.put("elasticOut", Interpolation.elasticOut);
        sMap.put("exp10", Interpolation.exp10);
        sMap.put("exp10In", Interpolation.exp10In);
        sMap.put("exp10Out", Interpolation.exp10Out);
        sMap.put("exp5", Interpolation.exp5);
        sMap.put("exp5In", Interpolation.exp5In);
        sMap.put("exp5Out", Interpolation.exp5Out);
        sMap.put("fade", Interpolation.fade);
        sMap.put("linear", Interpolation.linear);
        sMap.put("pow2", Interpolation.pow2);
        sMap.put("pow2In", Interpolation.pow2In);
        sMap.put("pow2Out", Interpolation.pow2Out);
        sMap.put("pow3", Interpolation.pow3);
        sMap.put("pow3In", Interpolation.pow3In);
        sMap.put("pow3Out", Interpolation.pow3Out);
        sMap.put("pow4", Interpolation.pow4);
        sMap.put("pow4In", Interpolation.pow4In);
        sMap.put("pow4Out", Interpolation.pow4Out);
        sMap.put("pow5", Interpolation.pow5);
        sMap.put("pow5In", Interpolation.pow5In);
        sMap.put("pow5Out", Interpolation.pow5Out);
        sMap.put("sine", Interpolation.sine);
        sMap.put("sineIn", Interpolation.sineIn);
        sMap.put("sineOut", Interpolation.sineOut);
        sMap.put("swing", Interpolation.swing);
        sMap.put("swingIn", Interpolation.swingIn);
        sMap.put("swingOut", Interpolation.swingOut);
    }

    InterpolationArgumentDefinition(Interpolation defaultValue) {
        super(Interpolation.class, defaultValue);
    }

    @Override
    public Object parse(StreamTokenizer tokenizer, DimensionParser dimParser)
            throws AnimScriptLoader.SyntaxException {
        try {
            tokenizer.nextToken();
        } catch (IOException e) {
            throw new AnimScriptLoader.SyntaxException(tokenizer, "Missing token for argument");
        }
        Interpolation value;
        if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
            value = sMap.get(tokenizer.sval);
            if (value == null) {
                throw new AnimScriptLoader.SyntaxException(
                        tokenizer, "Invalid interpolation value " + tokenizer.sval);
            }
        } else if (this.defaultValue != null) {
            tokenizer.pushBack();
            value = this.defaultValue;
        } else {
            throw new AnimScriptLoader.SyntaxException(
                    tokenizer, "No value set for this argument, which has no default value");
        }
        return value;
    }
}
