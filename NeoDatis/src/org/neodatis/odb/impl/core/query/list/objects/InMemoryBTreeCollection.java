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
package org.neodatis.odb.impl.core.query.list.objects;

import org.neodatis.btree.IBTree;
import org.neodatis.btree.impl.multiplevalue.InMemoryBTreeMultipleValuesPerKey;
import org.neodatis.odb.core.OrderByConstants;


/**
 * An implementation of an ordered Collection based on a BTree implementation that holds all objects in memory
 * @author osmadja
 *
 */
public class InMemoryBTreeCollection<T> extends AbstractBTreeCollection<T> {

    public InMemoryBTreeCollection(int size) {
        super(size, OrderByConstants.ORDER_BY_ASC);
    }

    public InMemoryBTreeCollection(int size, OrderByConstants orderByType) {
        super(size, orderByType);
    }

    public InMemoryBTreeCollection() {
    }

    public IBTree buildTree(int degree) {
        return new InMemoryBTreeMultipleValuesPerKey("default",degree);
    }

}
