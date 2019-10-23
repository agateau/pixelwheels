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
import java.io.IOException;
import java.io.StreamTokenizer;

class FloatArgumentDefinition extends ArgumentDefinition<Float> {
    enum Domain {
        DIMENSION,
        DURATION,
        SCALAR
    }

    private final FloatArgumentDefinition.Domain mDomain;

    FloatArgumentDefinition(FloatArgumentDefinition.Domain domain) {
        super(Float.TYPE, null);
        mDomain = domain;
    }

    FloatArgumentDefinition(FloatArgumentDefinition.Domain domain, float defaultValue) {
        super(Float.TYPE, defaultValue);
        mDomain = domain;
    }

    @Override
    public Object parse(StreamTokenizer tokenizer, DimensionParser dimParser)
            throws AnimScriptLoader.SyntaxException {
        try {
            tokenizer.nextToken();
        } catch (IOException e) {
            throw new AnimScriptLoader.SyntaxException(tokenizer, "Missing token for argument");
        }
        float value;
        if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
            if (mDomain == Domain.DIMENSION) {
                value = dimParser.parse(tokenizer.sval, DimensionParser.Unit.PIXEL);
            } else {
                value = Float.parseFloat(tokenizer.sval);
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
