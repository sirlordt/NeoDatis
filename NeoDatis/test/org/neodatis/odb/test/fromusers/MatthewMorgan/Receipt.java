/**
 * 
 */
package org.neodatis.odb.test.fromusers.MatthewMorgan;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author olivier
 * 
 */
public class Receipt {
	int docId;
	Date timestamp;
	int customerId;
	ArrayList<ItemLine> itemLines;

	public Receipt(int docId, Date timestamp, int customerId) {
		super();
		this.docId = docId;
		this.timestamp = timestamp;
		this.customerId = customerId;
		itemLines = new ArrayList<ItemLine>();
	}

	public void addItem(int itemNum, String itemDescription) {
		itemLines.add(new ItemLine(itemNum, itemDescription));
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public ArrayList<ItemLine> getItemLines() {
		return itemLines;
	}

	public void setItemLines(ArrayList<ItemLine> itemLines) {
		this.itemLines = itemLines;
	}

	class ItemLine {
		int itemNum;
		String itemDescription;

		public ItemLine(int itemNum, String itemDescription) {
			super();
			this.itemNum = itemNum;
			this.itemDescription = itemDescription;
		}

		public int getItemNum() {
			return itemNum;
		}

		public void setItemNum(int itemNum) {
			this.itemNum = itemNum;
		}

		public String getItemDescription() {
			return itemDescription;
		}

		public void setItemDescription(String itemDescription) {
			this.itemDescription = itemDescription;
		}

	}
}
