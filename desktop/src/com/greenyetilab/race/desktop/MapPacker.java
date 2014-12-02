/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 ******************************************************************************/

package com.greenyetilab.race.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.greenyetilab.utils.log.NLog;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/** Given one or more TMX tilemaps, packs all tileset resources used across the maps into a <b>single</b> {@link TextureAtlas} and
 * produces a new TMX file to be loaded with an AtlasTiledMapLoader loader. Optionally, it can keep track of unused tiles
 * and omit them from the generated atlas, reducing the resource size.
 *
 * The original TMX map file will be parsed by using the {@link TmxMapLoader} loader, thus access to a valid OpenGL context is
 * <b>required</b>, that's why an LwjglApplication is created by this preprocessor: this is probably subject to change in the
 * future, where loading both maps metadata and graphics resources should be made conditional.
 *
 * The new TMX map file will contains a new property, namely "atlas", whose value will enable the AtlasTiledMapLoader to
 * correctly read the associated TextureAtlas representing the tileset.
 *
 * @author David Fraska and others (initial implementation, tell me who you are!)
 * @author Manuel Bua */
public class MapPacker {

    private ArrayList<Integer> blendedTiles = new ArrayList<Integer>();

    private static class TmxFilter implements FilenameFilter {
        @Override
        public boolean accept (File dir, String name) {
            return name.endsWith(".tmx");
        }
    }

    private static class PackerFileHandleResolver implements FileHandleResolver {
        @Override
        public FileHandle resolve (String fileName) {
            return new FileHandle(fileName);
        }
    }

    /** You can either run the {@link MapPacker#main(String[])} method or reference this class in your own project and call
     * this method.
     *
     * Keep in mind that this preprocessor will need to load the maps by using the {@link TmxMapLoader} loader and this in turn
     * will need a valid OpenGL context to work: this is probably subject to change in the future, where loading both maps metadata
     * and graphics resources should be made conditional.
     *
     * Process a directory containing TMX map files representing Tiled maps and produce a single TextureAtlas as well as new
     * processed TMX map files, correctly referencing the generated {@link TextureAtlas} by using the "atlas" custom map property.
     *
     * Typically, your maps will lie in a directory, such as "maps/" and your tilesets in a subdirectory such as "maps/city": this
     * layout will ensure that MapEditor will reference your tileset with a very simple relative path and no parent directory
     * names, such as "..", will ever happen in your TMX file definition avoiding much of the confusion caused by the preprocessor
     * working with relative paths.
     *
     * <strong>WARNING!</strong> Use caution if you have a "../" in the path of your tile sets! The output for these tile sets will
     * be relative to the output directory. For example, if your output directory is "C:\mydir\maps" and you have a tileset with
     * the path "../tileset.png", the tileset will be output to "C:\mydir\" and the maps will be in "C:\mydir\maps".
     */
    public void processMaps (File inputDir, File outputDir, Settings texturePackerSettings) throws IOException {
        outputDir.mkdirs();
        TmxMapLoader mapLoader = new TmxMapLoader(new PackerFileHandleResolver());

        for (File file : inputDir.listFiles(new TmxFilter())) {
            String atlasName = file.getName().replace(".tmx", "");
            NLog.i("# Processing %s atlasName=%s", file.getAbsolutePath(), atlasName);
            TiledMap map = mapLoader.load(file.getAbsolutePath());
            FileHandle tmxFile = new FileHandle(file.getAbsolutePath());
            writeUpdatedTMX(outputDir, tmxFile, atlasName);
            packTileSets(map.getTileSets(), inputDir, outputDir, atlasName, texturePackerSettings);
        }
    }

    /** Traverse the specified tilesets, optionally lookup the used ids and pass every tile image to the {@link TexturePacker},
     * optionally ignoring unused tile ids */
    private void packTileSets(TiledMapTileSets sets, File inputDir, File outputDir,
                              String atlasName, Settings texturePackerSettings) throws IOException {
        FileHandle inputDirHandle = new FileHandle(inputDir.getAbsolutePath());
        BufferedImage tile;
        Vector2 tileLocation;
        Graphics g;

        TexturePacker packer = new TexturePacker(texturePackerSettings);

        for (TiledMapTileSet set : sets) {
            System.out.println("Processing tileset " + set.getName());

            int tileWidth = set.getProperties().get("tilewidth", Integer.class);
            int tileHeight = set.getProperties().get("tileheight", Integer.class);
            int firstGid = set.getProperties().get("firstgid", Integer.class);

            TileSetLayout layout = new TileSetLayout(firstGid, set, inputDirHandle);

            for (int gid = layout.firstgid, i = 0; i < layout.numTiles; gid++, i++) {
                tileLocation = layout.getLocation(gid);
                tile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_4BYTE_ABGR);

                g = tile.createGraphics();
                g.drawImage(layout.image, 0, 0, tileWidth, tileHeight, (int)tileLocation.x, (int)tileLocation.y, (int)tileLocation.x
                        + tileWidth, (int)tileLocation.y + tileHeight, null);

                if (isBlended(tile)) setBlended(gid);
                NLog.d("Adding %d, %d (%d %d), gid=%d", (int)tileLocation.x, (int)tileLocation.y, tileWidth, tileHeight, gid);
                packer.addImage(tile, atlasName + "_" + (gid - 1));
            }
        }

        packer.pack(outputDir, atlasName);
    }

    private void setBlended (int tileNum) {
        blendedTiles.add(tileNum);
    }

    private void writeUpdatedTMX (File outputDir, FileHandle tmxFileHandle, String atlasName) throws IOException {
        Document doc;
        DocumentBuilder docBuilder;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

        try {
            docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(tmxFileHandle.read());

            Node map = doc.getFirstChild();
            while (map.getNodeType() != Node.ELEMENT_NODE || !map.getNodeName().equals("map")) {
                if ((map = map.getNextSibling()) == null) {
                    throw new GdxRuntimeException("Couldn't find map node!");
                }
            }

            setProperty(doc, map, "blended tiles", toCSV(blendedTiles));
            setProperty(doc, map, "atlas", atlasName + ".atlas");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(new File(outputDir, tmxFileHandle.name()));
            transformer.transform(source, result);

        } catch (ParserConfigurationException e) {
            throw new RuntimeException("ParserConfigurationException: " + e.getMessage());
        } catch (SAXException e) {
            throw new RuntimeException("SAXException: " + e.getMessage());
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("TransformerConfigurationException: " + e.getMessage());
        } catch (TransformerException e) {
            throw new RuntimeException("TransformerException: " + e.getMessage());
        }
    }

    private static void setProperty (Document doc, Node parent, String name, String value) {
        Node properties = getFirstChildNodeByName(parent, "properties");
        Node property = getFirstChildByNameAttrValue(properties, "property", "name", name);

        NamedNodeMap attributes = property.getAttributes();
        Node valueNode = attributes.getNamedItem("value");
        if (valueNode == null) {
            valueNode = doc.createAttribute("value");
            valueNode.setNodeValue(value);
            attributes.setNamedItem(valueNode);
        } else {
            valueNode.setNodeValue(value);
        }
    }

    private static String toCSV (ArrayList<Integer> values) {
        String temp = "";
        for (int i = 0; i < values.size() - 1; i++) {
            temp += values.get(i) + ",";
        }
        if (values.size() > 0) temp += values.get(values.size() - 1);
        return temp;
    }

    /** If the child node doesn't exist, it is created. */
    private static Node getFirstChildNodeByName (Node parent, String child) {
        NodeList childNodes = parent.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeName().equals(child)) {
                return childNodes.item(i);
            }
        }

        Node newNode = parent.getOwnerDocument().createElement(child);

        if (childNodes.item(0) != null)
            return parent.insertBefore(newNode, childNodes.item(0));
        else
            return parent.appendChild(newNode);
    }

    private static boolean isBlended (BufferedImage tile) {
        int[] rgbArray = new int[tile.getWidth() * tile.getHeight()];
        tile.getRGB(0, 0, tile.getWidth(), tile.getHeight(), rgbArray, 0, tile.getWidth());
        for (int i = 0; i < tile.getWidth() * tile.getHeight(); i++) {
            if (((rgbArray[i] >> 24) & 0xff) != 255) {
                return true;
            }
        }
        return false;
    }

    /** If the child node or attribute doesn't exist, it is created. Usage example: Node property =
     * getFirstChildByAttrValue(properties, "property", "name", "blended tiles"); */
    private static Node getFirstChildByNameAttrValue (Node node, String childName, String attr, String value) {
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i).getNodeName().equals(childName)) {
                NamedNodeMap attributes = childNodes.item(i).getAttributes();
                Node attribute = attributes.getNamedItem(attr);
                if (attribute.getNodeValue().equals(value)) return childNodes.item(i);
            }
        }

        Node newNode = node.getOwnerDocument().createElement(childName);
        NamedNodeMap attributes = newNode.getAttributes();

        Attr nodeAttr = node.getOwnerDocument().createAttribute(attr);
        nodeAttr.setNodeValue(value);
        attributes.setNamedItem(nodeAttr);

        if (childNodes.item(0) != null) {
            return node.insertBefore(newNode, childNodes.item(0));
        } else {
            return node.appendChild(newNode);
        }
    }

    static File inputDir;
    static File outputDir;

    /** Processes a directory of Tile Maps, compressing each tile set contained in any map once.
     *
     * @param args args[0]: the input directory containing the tmx files (and tile sets, relative to the path listed in the tmx
     *           file). args[1]: The output directory for the tmx files, should be empty before running. WARNING: Use caution if
     *           you have a "../" in the path of your tile sets! The output for these tile sets will be relative to the output
     *           directory. For example, if your output directory is "C:\mydir\output" and you have a tileset with the path
     *           "../tileset.png", the tileset will be output to "C:\mydir\" and the maps will be in "C:\mydir\output". args[2]:
     *           --strip-unused (optional, include to let the TiledMapPacker remove tiles which are not used. */
    public static void main (String[] args) {
        final Settings texturePackerSettings = new Settings();
        texturePackerSettings.paddingX = 2;
        texturePackerSettings.paddingY = 2;
        texturePackerSettings.edgePadding = true;
        texturePackerSettings.duplicatePadding = true;
        texturePackerSettings.bleed = true;
        texturePackerSettings.alias = true;
        texturePackerSettings.useIndexes = true;

        switch (args.length) {
        case 2: {
            inputDir = new File(args[0]);
            outputDir = new File(args[1]);
            break;
        }
        case 1: {
            inputDir = new File(args[0]);
            outputDir = new File(inputDir, "output/");
            break;
        }
        default: {
            System.out.println("Usage: INPUTDIR [OUTPUTDIR] [--strip-unused]");
            System.exit(0);
        }
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.forceExit = false;
        config.width = 100;
        config.height = 50;
        config.title = "TiledMapPacker";
        new LwjglApplication(new ApplicationListener() {

            @Override
            public void resume () {
            }

            @Override
            public void resize (int width, int height) {
            }

            @Override
            public void render () {
            }

            @Override
            public void pause () {
            }

            @Override
            public void dispose () {
            }

            @Override
            public void create () {
                MapPacker packer = new MapPacker();

                if (!inputDir.exists()) {
                    throw new RuntimeException("Input directory does not exist: " + inputDir);
                }

                try {
                    packer.processMaps(inputDir, outputDir, texturePackerSettings);
                } catch (IOException e) {
                    throw new RuntimeException("Error processing map: " + e.getMessage());
                }

                Gdx.app.exit();
            }
        }, config);
    }
}
