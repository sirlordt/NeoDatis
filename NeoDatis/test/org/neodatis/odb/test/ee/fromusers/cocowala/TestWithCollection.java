/**
 * 
 */
package org.neodatis.odb.test.ee.fromusers.cocowala;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.test.ODBTest;

/**
 * @author olivier
 * 
 */
public class TestWithCollection extends ODBTest {
	public void test1() {
		int size = 500;
		println("Generating reports");
		List<IReport> reports = GenerateReports(size);
		String baseName = getBaseName();
		System.out.println(baseName);
		ODB odb = open(baseName);

		println("Saving reports to database");
		long t0 = System.currentTimeMillis();

		for (IReport report : reports) {
			odb.store(report);
		}
		odb.close();

		long t1 = System.currentTimeMillis();
		println(String.format("%d ms to store %d reports", (t1-t0),size));
		
		println("Getting all Reports");
		odb = open(baseName);
		long t2 = System.currentTimeMillis();
		Objects<Report> reports2 = odb.getObjects(Report.class,false);
		long t3 = System.currentTimeMillis();
		println(String.format("Retrieved %d reports in %d ms", reports2.size(), (t3-t2)));
		odb.close();
		
	}

	private List<IReport> GenerateReports(int numReports) {
		List<IReport> reports = new ArrayList<IReport>(numReports);

		Random rng = new Random();

		// Subjects
		List<Subject> subjects = new ArrayList<Subject>();

		for (int i = 0; i < numReports; i++) {
			subjects.add(new Subject("Subject " + rng.nextFloat(), "Subject " + rng.nextDouble(), rng.nextInt()));
		}

		// Reports
		for (int i = 0; i < numReports; i++) {
			reports.add(GenerateReport(subjects.get((int) (Math.random() * 100))));
		}

		return reports;
	}

	private IReport GenerateReport(ISubject subject) {
		Report report = new Report(subject);

		Random rng = new Random();

		int numReportItems = (int) (Math.random()*100);

		for (int i = 0; i < numReportItems; i++) {
			ReportItem reportItem = new ReportItem("Report Item " + i);

			int numDetails = (int) (Math.random()*10);

			for (int i2 = 0; i2 < numDetails; i2++) {
				reportItem.getDetail().add(new ReportDetailItem("Attribure " + 1, String.valueOf(i2), String.valueOf(i2 + 1)));
			}

			report.getItems().put(reportItem.getName(), reportItem);
		}

		report.setEndTime(new Date());

		return report;
	}

}
