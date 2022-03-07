/*
 * Copyright 2022 Aurélien Gâteau <mail@agateau.com>
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
package com.agateau.pixelwheels.tools.trackeditor;

import com.agateau.pixelwheels.map.LapPositionTableIO;
import com.badlogic.gdx.utils.Array;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TrackIO {
    private final String mPath;
    private final Document mDocument;
    private final int mMapHeight;
    private final Transformer mTransformer;

    public TrackIO(String path) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            mTransformer = transformerFactory.newTransformer();
            mTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            mTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            mTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create transformer");
        }
        mPath = path;
        mDocument = loadXmlFile(mPath);
        mMapHeight = getMapHeight(mDocument);
    }

    public boolean save(Array<LapPositionTableIO.Line> lines) {
        updateSectionsLayer(lines);
        return saveXmlFile();
    }

    private boolean saveXmlFile() {
        String dst = mPath + ".tmp";
        try {
            DOMSource source = new DOMSource(mDocument);
            File file = new File(dst);
            StreamResult streamResult = new StreamResult(file);
            mTransformer.transform(source, streamResult);
            return file.renameTo(new File(mPath));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateSectionsLayer(Array<LapPositionTableIO.Line> lines) {
        NodeList groups = mDocument.getElementsByTagName("objectgroup");
        for (int idx = 0; idx < groups.getLength(); ++idx) {
            Element group = (Element) groups.item(idx);
            if (group.getAttribute("name").equals("Sections")) {
                removeChildren(group);
                int lineIdx = 0;
                for (LapPositionTableIO.Line line : lines) {
                    addLine(group, line, lineIdx);
                    ++lineIdx;
                }
            }
        }
    }

    /*
     The TMX representation of a line looks like this:
     <object id="73" name="0" x="2938" y="3812">
      <polyline points="100.356,258.983 -551.197,-1155.71"/>
     </object>
    */
    private void addLine(Element group, LapPositionTableIO.Line line, int lineIdx) {
        Element objectE = mDocument.createElement("object");
        group.appendChild(objectE);

        objectE.setAttribute("name", String.valueOf(lineIdx));
        objectE.setAttribute("x", String.valueOf(line.p1.x));
        objectE.setAttribute("y", String.valueOf(mMapHeight - line.p1.y));

        Element polylineE = mDocument.createElement("polyline");
        objectE.appendChild(polylineE);
        String pointsStr =
                String.format(
                        Locale.US, "0,0 %f,%f", line.p2.x - line.p1.x, -(line.p2.y - line.p1.y));
        polylineE.setAttribute("points", pointsStr);
    }

    private static Document loadXmlFile(String path) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            File file = new File(path);
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            return dBuilder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load " + path);
        }
    }

    private static void removeChildren(Element element) {
        NodeList list = element.getChildNodes();
        for (int idx = list.getLength() - 1; idx >= 0; --idx) {
            element.removeChild(list.item(idx));
        }
    }

    private static int getMapHeight(Document doc) {
        Element root = doc.getDocumentElement();
        int height = Integer.parseInt(root.getAttribute("height"));
        int tileHeight = Integer.parseInt(root.getAttribute("tileheight"));
        return height * tileHeight;
    }
}
