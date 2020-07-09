/*
 * Copyright 2020 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.ui.uibuilder;

import com.badlogic.gdx.utils.XmlReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class to traverse a tree of XmlReader.Element and execute a function on each of them
 *
 * <p>Parts of the tree can be excluded using variables defined with @ref defineVariable() then
 * using the Ifdef and Else tags.
 *
 * <p>Syntax looks like this:
 *
 * <pre>
 *     <Ifdef var="foo">
 *         <SomeUiElement/>
 *     </Ifdef>
 *     <Else>
 *         <AlternativeUiElement/>
 *     </Else>
 * </pre>
 *
 * <p>The same id can appear in both branches of the if.
 */
class ElementTreeTraversor {
    private final Set<String> mVariables = new HashSet<>();

    interface ElementProcessor {
        void process(XmlReader.Element element) throws UiBuilder.SyntaxException;
    }

    void defineVariable(String variable) {
        mVariables.add(variable);
    }

    void traverseElementTree(XmlReader.Element parentElement, ElementProcessor elementProcessor)
            throws UiBuilder.SyntaxException {
        for (int idx = 0, size = parentElement.getChildCount(); idx < size; ++idx) {
            XmlReader.Element element = parentElement.getChild(idx);
            if (element.getName().equals("Action")) {
                continue;
            }
            if (element.getName().equals("Ifdef")) {
                XmlReader.Element elseElement = null;
                if (idx + 1 < size) {
                    elseElement = parentElement.getChild(idx + 1);
                    if (elseElement.getName().equals("Else")) {
                        // It's an else, swallow it
                        ++idx;
                    } else {
                        elseElement = null;
                    }
                }
                if (evaluateIfdef(element)) {
                    traverseElementTree(element, elementProcessor);
                } else if (elseElement != null) {
                    traverseElementTree(elseElement, elementProcessor);
                }
                continue;
            }
            elementProcessor.process(element);
        }
    }

    private boolean evaluateIfdef(XmlReader.Element element) {
        String condition = element.getAttribute("var").trim();
        return mVariables.contains(condition);
    }
}
