package net.ob3d.domainmodel;

import java.util.Comparator;

public class RecordComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		Record record1 = (Record) o1;
		Record record2 = (Record) o2;
		if (record1.getTotalFights() == 0 & record2.getTotalFights() == 0) {
			if (record1.getRecordType() > record2.getRecordType())
				return -1;
			if (record1.getRecordType() < record2.getRecordType())
				return -1;
		}
		if (record1.getTotalFights() == 0)
			return 1;
		if (record2.getTotalFights() == 0)
			return -1;
		if (record1.getRecordType() > record2.getRecordType())
			return -1;
		if (record1.getRecordType() < record2.getRecordType())
			return 1;
		return 0;
	}

}
