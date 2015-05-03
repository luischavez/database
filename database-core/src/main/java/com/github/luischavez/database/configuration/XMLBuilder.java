/*
 * Copyright (C) 2015 Luis Chávez <https://github.com/luischavez>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.luischavez.database.configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 *
 * @author Luis Chávez <https://github.com/luischavez>
 */
public class XMLBuilder implements ConfigurationBuilder {

    @Override
    public List<Configuration> build(ConfigurationSource source) {
        byte[] bytes = source.read();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new ConfigurationBuilderException("Can't create xml document builder", ex);
        }
        Document document;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            document = documentBuilder.parse(inputStream);
        } catch (IOException | SAXException ex) {
            throw new ConfigurationBuilderException("Can't parse document", ex);
        }
        document.getDocumentElement().normalize();
        NodeList configurationNodes = document.getElementsByTagName("configuration");
        int configurationLength = configurationNodes.getLength();
        List<Configuration> configurations = new ArrayList<>();
        for (int i = 0; i < configurationLength; i++) {
            Node configurationNode = configurationNodes.item(i);
            if (Node.ELEMENT_NODE != configurationNode.getNodeType()) {
                throw new ConfigurationBuilderException("Invalid node type for configuration index " + i);
            }
            Element configurationElement = Element.class.cast(configurationNode);
            NodeList nameNodes = configurationElement.getElementsByTagName("name");
            if (0 == nameNodes.getLength()) {
                throw new ConfigurationBuilderException("Can't find name in configuration index " + i);
            }
            Node firstNameNode = nameNodes.item(0);
            if (Node.ELEMENT_NODE != firstNameNode.getNodeType()) {
                throw new ConfigurationBuilderException("Invalid node type for name in configuration index " + i);
            }
            Element nameElement = Element.class.cast(firstNameNode);
            String name = nameElement.getChildNodes().item(0).getNodeValue();
            NodeList supportNodes = configurationElement.getElementsByTagName("support");
            if (0 == supportNodes.getLength()) {
                throw new ConfigurationBuilderException("Can't find support in configuration index " + i);
            }
            Node firstSupportNode = supportNodes.item(0);
            if (Node.ELEMENT_NODE != firstSupportNode.getNodeType()) {
                throw new ConfigurationBuilderException("Invalid node type for support in configuration index " + i);
            }
            Element supportElement = Element.class.cast(firstSupportNode);
            String supportClassName = supportElement.getChildNodes().item(0).getNodeValue();
            NodeList propertiesNodes = configurationElement.getElementsByTagName("properties");
            if (0 == propertiesNodes.getLength()) {
                throw new ConfigurationBuilderException("Can't find properties in configuration index " + i);
            }
            Node firstPropertiesNode = propertiesNodes.item(0);
            if (Node.ELEMENT_NODE != firstPropertiesNode.getNodeType()) {
                throw new ConfigurationBuilderException("Invalid node type for properties in configuration index " + i);
            }
            Element propertiesElement = Element.class.cast(firstPropertiesNode);
            NodeList definedPropertiesNodes = propertiesElement.getChildNodes();
            int propertiesLength = definedPropertiesNodes.getLength();
            Map<String, String> properties = new HashMap<>();
            for (int j = 0; j < propertiesLength; j++) {
                Node propertyNode = definedPropertiesNodes.item(j);
                if (Node.ELEMENT_NODE == propertyNode.getNodeType()) {
                    String propertyName = propertyNode.getNodeName();
                    String propertyValue = null;
                    if (0 < propertyNode.getChildNodes().getLength()) {
                        propertyValue = propertyNode.getChildNodes().item(0).getTextContent();
                    }
                    properties.put(propertyName, propertyValue);
                }
            }
            Configuration configuration = new Configuration(name, supportClassName, properties);
            configurations.add(configuration);
        }
        return configurations;
    }
}
