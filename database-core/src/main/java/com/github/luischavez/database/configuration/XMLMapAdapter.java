/* 
 * Copyright (C) 2015 Luis Chávez
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

import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class XMLMapAdapter extends XmlAdapter<XMLMapElement[], Map<String, String>> {

    @Override
    public XMLMapElement[] marshal(Map<String, String> map) throws Exception {
        XMLMapElement[] mapElements = new XMLMapElement[map.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            mapElements[i++] = new XMLMapElement(entry.getKey(), entry.getValue());
        }
        return mapElements;
    }

    @Override
    public Map<String, String> unmarshal(XMLMapElement[] elements) throws Exception {
        Map<String, String> map = new TreeMap();
        for (XMLMapElement mapelement : elements) {
            map.put(mapelement.key, mapelement.value);
        }
        return map;
    }
}
