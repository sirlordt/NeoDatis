/**
 * 
 */
package org.neodatis.odb.gui.component;

import java.awt.FlowLayout;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author olivier
 * 
 */
public class DateTimePanel extends JPanel {
	private Date date;
	private JTextField tfHour;
	private JTextField tfMinute;
	private JTextField tfSeconds;
	private JTextField tfMs;

	// private JDateChooser calendar;

	public DateTimePanel(Date date) {
		init(date);
	}

	public DateTimePanel() {
		init(null);
	}

	private void init(Date date) {
		this.date = date;
		/*
		 * try { //UIManager.setLookAndFeel(new PlasticLookAndFeel()); } catch
		 * (UnsupportedLookAndFeelException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 */
		/*
		 * calendar = new JDateChooser("dd/MM/yyyy", "##/##/####", ' '); if
		 * (date != null) { calendar.setDate(date); } // "yyyy/MM/dd",
		 * "####/##/##", ' ');
		 */

		tfHour = new JTextField(2);
		tfMinute = new JTextField(2);
		tfSeconds = new JTextField(2);
		tfMs = new JTextField(3);

		if (date != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);

			tfHour.setText(String.valueOf(c.get(Calendar.HOUR_OF_DAY)));
			tfMinute.setText(String.valueOf(c.get(Calendar.MINUTE)));
			tfSeconds.setText(String.valueOf(c.get(Calendar.SECOND)));
			tfMs.setText(String.valueOf(c.get(Calendar.MILLISECOND)));
		}
		JTextField tf = new JTextField(11);
		// calendar.setPreferredSize(tf.getPreferredSize());

		tfHour.setToolTipText("Hour");
		tfMinute.setToolTipText("Minute");
		tfSeconds.setToolTipText("Second");
		tfMs.setToolTipText("Ms");

		JPanel panel = new JPanel();
		panel.add(tfHour);
		panel.add(new JLabel(":"));
		panel.add(tfMinute);
		panel.add(new JLabel(":"));
		panel.add(tfSeconds);
		panel.add(new JLabel(":"));
		panel.add(tfMs);

		// setLayout(new BorderLayout(10,10));
		setLayout(new FlowLayout(5));
		// add(calendar);// ,BorderLayout.CENTER);
		add(panel);// ,BorderLayout.SOUTH);

	}

	public void set(Date date) {
		this.date = date;
	}

	public Date getDate() throws Exception {
		retrieve();
		return date;
	}

	private void retrieve() throws Exception {
		try {
			/*
			 * Date d = calendar.getDate(); Calendar c = Calendar.getInstance();
			 * c.setTime(d); c.set(Calendar.DATE,
			 * Integer.parseInt(tfHour.getText())); c.set(Calendar.MINUTE,
			 * Integer.parseInt(tfMinute.getText())); c.set(Calendar.SECOND,
			 * Integer.parseInt(tfSeconds.getText()));
			 * c.set(Calendar.MILLISECOND, Integer.parseInt(tfMs.getText()));
			 * 
			 * date = c.getTime();
			 */
		} catch (Exception e) {
			throw new Exception("Invalid date!");
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		JPanel panel = new DateTimePanel(new Date());
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}

}
