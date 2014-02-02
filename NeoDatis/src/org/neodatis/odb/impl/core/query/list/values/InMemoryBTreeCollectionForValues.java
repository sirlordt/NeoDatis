/*
NeoDatis ODB : Native Object Database (odb.info@neodatis.org)
Copyright (C) 2007 NeoDatis Inc. http://www.neodatis.org

"This file is part of the NeoDatis ODB open source object database".

NeoDatis ODB is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

NeoDatis ODB is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/
package org.neodatis.odb.impl.core.query.list.values;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.impl.multiplevalue.InMemoryBTreeMultipleValuesPerKey;
import org.neodatis.odb.ObjectValues;
import org.neodatis.odb.Values;
import org.neodatis.odb.core.OrderByConstants;
import org.neodatis.odb.impl.core.query.list.objects.AbstractBTreeCollection;

/**
 * An ordered Collection to hold values (not objects) based on a BTree implementation. It holds all values in memory.
 * @author osmadja
 *
 */
public class InMemoryBTreeCollectionForValues extends AbstractBTreeCollection<ObjectValues> implements Values {

    public InMemoryBTreeCollectionForValues(int size) {
        super(size, OrderByConstants.ORDER_BY_ASC);
    }

    public InMemoryBTreeCollectionForValues(int size, OrderByConstants orderByType) {
        super(size, orderByType);
    }

    public InMemoryBTreeCollectionForValues() {
    }

    public IBTree buildTree(int degree) {
        return new InMemoryBTreeMultipleValuesPerKey("default",degree);
    }

	public ObjectValues nextValues() {
		return next();
	}

}
