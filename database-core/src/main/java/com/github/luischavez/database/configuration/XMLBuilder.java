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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 */
public class XMLBuilder implements ConfigurationBuilder {

    @Override
    public Configuration build(ConfigurationSource source) {
        JAXBContext context;
        Unmarshaller unmarshaller;
        try {
            context = JAXBContext.newInstance(Configuration.class);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException ex) {
            throw new ConfigurationBuilderException("Can't resolve Configuration class", ex);
        }
        Configuration configuration;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(source.read())) {
            configuration = (Configuration) unmarshaller.unmarshal(inputStream);
        } catch (IOException | JAXBException ex) {
            throw new ConfigurationBuilderException("Can't parse document", ex);
        }
        return configuration;
    }
}
