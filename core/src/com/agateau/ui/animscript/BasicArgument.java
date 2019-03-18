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

import com.agateau.ui.animscript.AnimScript.Context;

public class BasicArgument extends Argument {
    private Class<?> mClassType;
    private Object mValue;

    BasicArgument(Class<?> classType, Object value) {
        mClassType = classType;
        mValue = value;
    }

    @Override
    public Class<?> getClassType() {
        return mClassType;
    }

    @Override
    public Object computeValue(Context context) {
        return mValue;
    }
}
