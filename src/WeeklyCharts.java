
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.io.FileWriter;
import java.io.FileReader;



import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;



import org.json.*;

public class WeeklyCharts {
	static String charset = "UTF-8";
	static String filename="Teamwork Tasks.txt";
	static long CheckTime=18000000;
	static long numOfMilliSecondsinDay=86400000;
	static JSONArray tasks;
	static JFrame optionFrame;
	Calendar startCalendar;
	boolean dataIsLoaded;
	boolean startCalendarSet;
	ArrayList<Calendar> weeks;
	SimpleDateFormat sdf;
	String weekKey="weekContributions";
	
	public void setStartWeek(Calendar start){
		startCalendar=start;
		startCalendarSet=true;
	}
	
	private void setWeeks(int numOfWeeks){
		 
		
		if(startCalendarSet){
			weeks = new ArrayList<Calendar>();
			Calendar newWeek=(Calendar) startCalendar.clone();
			weeks.add(newWeek);
			System.out.println("Added Week: "+sdf.format(newWeek.getTime()));
			for(int i=1;i<=numOfWeeks;i++){
				
				newWeek=(Calendar) newWeek.clone();
				newWeek.add(Calendar.DATE, 7);
				System.out.println("Added Week: "+ sdf.format(newWeek.getTime()));
		//		System.out.println((newWeek.getTimeInMillis()-weeks.get(0).getTimeInMillis())/(double)numOfMilliSecondsinDay);
				
				
				weeks.add(newWeek);
			}
			
			
		}
	}
	
	private double getWeekPorportionInHours(Calendar weekStart,Calendar taskStart, Calendar taskEnd, int totalTimeInMinutes){
		weekStart.set(weekStart.get(Calendar.YEAR), weekStart.get(Calendar.MONTH), weekStart.get(Calendar.DAY_OF_MONTH), 1, 1, 1);
		taskStart.set(taskStart.get(Calendar.YEAR), taskStart.get(Calendar.MONTH), taskStart.get(Calendar.DAY_OF_MONTH), 1, 1, 1);
		taskEnd.set(taskEnd.get(Calendar.YEAR), taskEnd.get(Calendar.MONTH), taskEnd.get(Calendar.DAY_OF_MONTH), 1, 1, 1);
		
		double total=0;
		double totalNumOfDays=((taskEnd.getTimeInMillis()-taskStart.getTimeInMillis())/(double)numOfMilliSecondsinDay)+1;
//		System.out.println("Total number of Days: "+totalNumOfDays);
		double averageTimePerDay=(totalTimeInMinutes/60.0)/totalNumOfDays;
//		System.out.println("Average: "+averageTimePerDay);
		for (int i=0;i<7;i++){  //Used 7 for 7 Days
			if(((weekStart.getTimeInMillis()+i*numOfMilliSecondsinDay)-taskStart.getTimeInMillis())>=0
			&& ((weekStart.getTimeInMillis()+i*numOfMilliSecondsinDay)-taskEnd.getTimeInMillis())<=0){
				total+=averageTimePerDay;
			}
		}
		return total;
	}
	private Calendar convertTeamworkDateToCalendar(String twString){
		Calendar newCalendar= Calendar.getInstance();
	//	System.out.println(twString);
		newCalendar.set(Integer.parseInt(twString.substring(0, 4)), 
						Integer.parseInt(twString.substring(4, 6)), 
						Integer.parseInt(twString.substring(6, 8)));
		
		return newCalendar;
	}
	
	private void addTimeCalculationsToTasks(int numOfWeeks) throws JSONException{
		if(startCalendarSet){
			setWeeks(numOfWeeks);
			int size=tasks.length();
			
			for (int i=0;i<size;i++){
				ArrayList<Double> taskContributions=new ArrayList<Double>();
				for (int j=0;j<weeks.size()-1;j++){
					if(tasks.getJSONObject(i).getString("start-date").length()==8
					&& tasks.getJSONObject(i).getString("due-date").length()==8){
						taskContributions.add(getWeekPorportionInHours(
								weeks.get(j),
								convertTeamworkDateToCalendar(tasks.getJSONObject(i).getString("start-date")),
								convertTeamworkDateToCalendar(tasks.getJSONObject(i).getString("due-date")),
								tasks.getJSONObject(i).getInt("estimated-minutes")
								));
						}
					else{
						taskContributions.add(0.0);
					}
				}
				tasks.getJSONObject(i).append(weekKey, taskContributions);
				
				System.out.println("Added: "+taskContributions.toString()+ " to task id: "+ tasks.getJSONObject(i).getInt("id")+" with Task Name "+tasks.getJSONObject(i).getString("content"));
				
			}
			
		}
	}
	
	public boolean isStartCalendarSet(){
		return startCalendarSet;
	}
	
	
	public void getNewData(int numOfWeeks) throws JSONException{
		
		tasks = new JSONArray();
		try {
			//Beginning Page
			int page=1;
			URL url= new URL("http://pbok.teamwork.com/tasks.json?page="+page);
			
		    //Authentication Code
			String name = "sander739yellow";
			String password = "#";
			String authString = name + ":" + password;
			System.out.println("auth string: " + authString);
			
			//Encoder 
			byte[] authEncBytes = Base64.getEncoder().encode((authString.getBytes()));
			String authStringEnc = new String(authEncBytes);
			System.out.println("Base64 encoded auth string: " + authStringEnc);
			
			//Set HTTP request type and properties
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.setRequestProperty("Authorization", "Basic " + authStringEnc);
		
			// Setup For file writing and input
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			JSONObject singlePage;
			
			int pageSize=250; //This number from Teamwork as Max Page size
			while (page<15 && pageSize>=250) {
				
				//Read Line, create a single page of JSON object and write call to file
				inputLine = in.readLine();
			    singlePage=new JSONObject(inputLine);
			    writer.write(inputLine);
			    writer.newLine();
			    pageSize=singlePage.getJSONArray("todo-items").length();
			    
			    //Add each tasks as a JSON object in an array
			    for(int i=0;i<pageSize;i++){
			    	tasks.put(singlePage.getJSONArray("todo-items").getJSONObject(i));
			    }
			    
			    //Set new URL for new pages and make new connection
			    in.close();
			    page++;
			    url= new URL("http://pbok.teamwork.com/tasks.json?page="+page);
			    con = (HttpURLConnection) url.openConnection();
			    con.setRequestMethod("GET");
				con.setDoOutput(true);
				con.setRequestProperty("Authorization", "Basic " + authStringEnc);
			    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			   
			   
			}
			System.out.println("Number of pages: "+(page-1));
		    writer.close();
		    
			System.out.println("Retrieved New Data");
			addTimeCalculationsToTasks(numOfWeeks);
			dataIsLoaded=true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	public void getOldData(int numOfWeeks) throws JSONException{
		
		tasks = new JSONArray();
		try{
			FileReader file = new FileReader(filename);
			BufferedReader reader= new BufferedReader(file);
			
			String inputLine;
			JSONObject singlePage;
			
			int pageSize=250;
			while ((inputLine = reader.readLine()) != null) {
				singlePage=new JSONObject(inputLine);
				pageSize=singlePage.getJSONArray("todo-items").length();
				for(int i=0;i<pageSize;i++){
			    	tasks.put(singlePage.getJSONArray("todo-items").getJSONObject(i));
			    }
			}
			reader.close();
			System.out.println("Retrieved Old Data");
			addTimeCalculationsToTasks(numOfWeeks);
			dataIsLoaded=true;
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
	private CategoryDataset createDataset( ) {
	      final String fiat = "FIAT";        
	      final String audi = "AUDI";        
	      final String ford = "FORD";        
	      final String speed = "Speed";        
	      final String millage = "Millage";        
	      final String userrating = "User Rating";        
	      final String safety = "safety";        
	      final DefaultCategoryDataset dataset = 
	      new DefaultCategoryDataset( );  
	      
	      
	      dataset.addValue( 1.0 , fiat , speed );        
	      dataset.addValue( 3.0 , fiat , userrating );        
	      dataset.addValue( 5.0 , fiat , millage ); 
	      dataset.addValue( 5.0 , fiat , safety );           

	      dataset.addValue( 5.0 , audi , speed );        
	      dataset.addValue( 6.0 , audi , userrating );       
	      dataset.addValue( 10.0 , audi , millage );        
	      dataset.addValue( 4.0 , audi , safety );

	      dataset.addValue( 4.0 , ford , speed );        
	      dataset.addValue( 2.0 , ford , userrating );        
	      dataset.addValue( 3.0 , ford , millage );        
	      dataset.addValue( 6.0 , ford , safety );               

	      return dataset; 
	   }
	
	public void showChart(){

	
		if(dataIsLoaded){
			 JFreeChart barChart = ChartFactory.createBarChart(
			         "Test",           
			         "Category",            
			         "Score",            
			         this.createDataset(),          
			         PlotOrientation.VERTICAL,           
			         true, true, false);
			
				// create and display a frame...
			ChartFrame frame = new ChartFrame("test",barChart);
			frame.pack();
			frame.setVisible(true);
			}
		else{System.out.println("Data is not loaded");}
		}
		

	public WeeklyCharts() {
		dataIsLoaded=false;
		startCalendarSet=false;
		tasks = new JSONArray();
		sdf= new SimpleDateFormat("dd/MM/yy");
		
	}

}
