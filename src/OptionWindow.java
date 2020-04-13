import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.awt.event.ActionEvent;
import javax.swing.JFormattedTextField;
import java.awt.Button;
import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComboBox;

public class OptionWindow extends JFrame {

	private JPanel contentPane;
	private static WeeklyCharts charts;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					charts= new WeeklyCharts();
					OptionWindow frame = new OptionWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private Object makeObj(final String item)  {
	     return new Object() { public String toString() { return item; } };
	   }
	/**
	 * Create the frame.
	 * @throws ParseException 
	 */
	
	
	public OptionWindow() throws ParseException {
		setTitle("Chart Options (TESTING VERSION)");
	//	setIconImage(Toolkit.getDefaultToolkit().getImage(OptionWindow.class.getResource("/Logo-2016-Swirl-125.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 519, 301);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{116, 75, 0, 53, 50, 168};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, 0.0, 1.0, 0.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		JButton btnShowChart = new JButton("Show Chart");
		JButton btnShowAllCharts = new JButton("Show All Charts");
		
		btnShowChart.setVisible(false);
		btnShowAllCharts.setVisible(false);
		JLabel lblEnterStartDate = new JLabel("Start Date (dd/mm/yyyy):");
		GridBagConstraints gbc_lblEnterStartDate = new GridBagConstraints();
		gbc_lblEnterStartDate.insets = new Insets(0, 0, 5, 5);
		gbc_lblEnterStartDate.anchor = GridBagConstraints.EAST;
		gbc_lblEnterStartDate.gridx = 0;
		gbc_lblEnterStartDate.gridy = 0;
		contentPane.add(lblEnterStartDate, gbc_lblEnterStartDate);
		JLabel lblNewLabel = new JLabel("Start Date Not Set!");
		lblNewLabel.setForeground(Color.RED);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 7;
		lblNewLabel.setVisible(false);
		JButton btnSet = new JButton("Set Date");
	
		
		GridBagConstraints gbc_btnSet = new GridBagConstraints();
		gbc_btnSet.gridwidth = 2;
		gbc_btnSet.insets = new Insets(0, 0, 5, 0);
		gbc_btnSet.gridx = 4;
		gbc_btnSet.gridy = 0;
		contentPane.add(btnSet, gbc_btnSet);
		
		JLabel lblNumberOfWeeks = new JLabel("Number of Weeks:");
		GridBagConstraints gbc_lblNumberOfWeeks = new GridBagConstraints();
		gbc_lblNumberOfWeeks.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumberOfWeeks.gridx = 0;
		gbc_lblNumberOfWeeks.gridy = 1;
		contentPane.add(lblNumberOfWeeks, gbc_lblNumberOfWeeks);
		
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(12, 1, 104, 1));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.insets = new Insets(0, 0, 5, 5);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 1;
		contentPane.add(spinner, gbc_spinner);
		JLabel lblNotValid = new JLabel("Not Valid!");
		lblNotValid.setForeground(Color.RED);
		GridBagConstraints gbc_lblNotValid = new GridBagConstraints();
		gbc_lblNotValid.gridwidth = 2;
		gbc_lblNotValid.insets = new Insets(0, 0, 5, 0);
		gbc_lblNotValid.gridx = 4;
		gbc_lblNotValid.gridy = 1;
		contentPane.add(lblNotValid, gbc_lblNotValid);
		lblNotValid.setVisible(false);
	
		JComboBox<String> comboBox = new JComboBox<String>();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 5;
		gbc_comboBox.gridy = 2;
		contentPane.add(comboBox, gbc_comboBox);
		btnShowChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(comboBox.getItemAt(comboBox.getSelectedIndex()));
				charts.showChart(comboBox.getItemAt(comboBox.getSelectedIndex()));
				
			}
		});
		JButton btnLoadOldData = new JButton("Load Old Data");
		btnLoadOldData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {if(charts.isStartCalendarSet()){
						charts.getOldData((Integer)spinner.getValue());
						ArrayList<String> people=charts.getListOfPeople();
						for(int i=0;i<people.size();i++){
							comboBox.addItem(people.get(i));							
						}
						btnShowChart.setVisible(true);
						btnShowAllCharts.setVisible(true);
					}else{
						lblNewLabel.setVisible(true);
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btnLoadOldData.setVisible(false);
		
		JLabel lblStartDateNot = new JLabel("Start Date Not Set!");
		lblStartDateNot.setForeground(Color.RED);
		GridBagConstraints gbc_lblStartDateNot = new GridBagConstraints();
		gbc_lblStartDateNot.gridwidth = 2;
		gbc_lblStartDateNot.insets = new Insets(0, 0, 5, 0);
		gbc_lblStartDateNot.gridx = 4;
		gbc_lblStartDateNot.gridy = 7;
		lblStartDateNot.setVisible(false);
		contentPane.add(lblStartDateNot, gbc_lblStartDateNot);
		
		GridBagConstraints gbc_btnLoadOldData = new GridBagConstraints();
		gbc_btnLoadOldData.gridwidth = 2;
		gbc_btnLoadOldData.insets = new Insets(0, 0, 5, 5);
		gbc_btnLoadOldData.gridx = 0;
		gbc_btnLoadOldData.gridy = 2;
		contentPane.add(btnLoadOldData, gbc_btnLoadOldData);
		
		JButton btnNewButton = new JButton("Load New Data");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(charts.isStartCalendarSet()){
						charts.getNewData((Integer)spinner.getValue());
						
						ArrayList<String> people=charts.getListOfPeople();
						for(int i=0;i<people.size();i++){
							comboBox.addItem(people.get(i));							
						}
						btnShowChart.setVisible(true);
						btnShowAllCharts.setVisible(true);
					}else{
						lblStartDateNot.setVisible(true);
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btnNewButton.setVisible(false);
		
	
		
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 3;
		contentPane.add(btnNewButton, gbc_btnNewButton);
		contentPane.add(lblNewLabel, gbc_lblNewLabel);
		
		
		JFormattedTextField frmtdtxtfldDdmmyyyy = new JFormattedTextField(new MaskFormatter("##/##/####"));
		frmtdtxtfldDdmmyyyy.setText("dd/mm/yyyy");
		GridBagConstraints gbc_frmtdtxtfldDdmmyyyy = new GridBagConstraints();
		gbc_frmtdtxtfldDdmmyyyy.gridwidth = 3;
		gbc_frmtdtxtfldDdmmyyyy.insets = new Insets(0, 0, 5, 5);
		gbc_frmtdtxtfldDdmmyyyy.fill = GridBagConstraints.HORIZONTAL;
		gbc_frmtdtxtfldDdmmyyyy.gridx = 1;
		gbc_frmtdtxtfldDdmmyyyy.gridy = 0;
		contentPane.add(frmtdtxtfldDdmmyyyy, gbc_frmtdtxtfldDdmmyyyy);
		
	btnSet.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
			//	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Calendar date= Calendar.getInstance();
				String [] dmy;
				dmy=frmtdtxtfldDdmmyyyy.getText().split("/");
				try{
					SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yy");
					date.set(Integer.parseInt(dmy[2]),Integer.parseInt(dmy[1])-1, Integer.parseInt(dmy[0]));
					lblNotValid.setText("Date Interpreted as "+spf.format(date.getTime()));
					lblNotValid.setForeground(Color.BLACK);
					charts.setStartWeek(date);
					lblStartDateNot.setVisible(false);
					lblNewLabel.setVisible(false);
					lblNotValid.setVisible(true);
					btnLoadOldData.setVisible(true);
					btnNewButton.setVisible(true);
				}catch(Exception e2){
					lblNotValid.setVisible(true);
				}
				
				
				
			}
		});
		
		
		
		GridBagConstraints gbc_btnShowChart = new GridBagConstraints();
		gbc_btnShowChart.insets = new Insets(0, 0, 5, 0);
		gbc_btnShowChart.anchor = GridBagConstraints.SOUTH;
		gbc_btnShowChart.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnShowChart.gridwidth = 6;
		gbc_btnShowChart.gridx = 0;
		gbc_btnShowChart.gridy = 8;
		contentPane.add(btnShowChart, gbc_btnShowChart);
		
		
		btnShowAllCharts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i=0;i<comboBox.getItemCount();i++){
					charts.showChart(comboBox.getItemAt(i));
				}
			}
		});
		GridBagConstraints gbc_btnShowAllCharts = new GridBagConstraints();
		gbc_btnShowAllCharts.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnShowAllCharts.gridwidth = 6;
		gbc_btnShowAllCharts.insets = new Insets(0, 0, 0, 5);
		gbc_btnShowAllCharts.gridx = 0;
		gbc_btnShowAllCharts.gridy = 9;
		contentPane.add(btnShowAllCharts, gbc_btnShowAllCharts);
	}
	


}
