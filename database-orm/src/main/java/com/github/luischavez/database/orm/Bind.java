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
package com.github.luischavez.database.orm;

import com.github.luischavez.database.orm.model.ModelProvider;
import com.github.luischavez.database.orm.model.Model;
import com.github.luischavez.database.Database;
import com.github.luischavez.database.query.Query;

import java.util.List;

/**
 *
 * @author Luis Chávez {@literal <https://github.com/luischavez>}
 * @param <M>
 */
public class Bind<M extends Model> {

    private final ModelProvider<M> provider;

    private Database database;
    private Query query;

    public Bind(ModelProvider provider) {
        this.provider = provider;
    }

    public M make() {
        return this.provider.model();
    }

    protected Query query() {
        if (null == this.database) {
            this.database = Database.use("orm");
        }
        return null == this.query ? this.query = this.database.query() : this.query;
    }

    public M first() {
        M model = this.make();
        return model;
    }
    
    public List<M> get() {
        return null;
    }
}
