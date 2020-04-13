
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
	JSONArray tasks;
	static JFrame optionFrame;
	Calendar startCalendar;
	boolean dataIsLoaded;
	boolean startCalendarSet;
	ArrayList<Calendar> weeks;
	SimpleDateFormat sdf;
	String weekKey="weekContributions";
	HashMap<String,Double> Totals;
	HashSet<String> projects;
		
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
		//		System.out.println("Added Week: "+ sdf.format(newWeek.getTime()));
		//		System.out.println((newWeek.getTimeInMillis()-weeks.get(0).getTimeInMillis())/(double)numOfMilliSecondsinDay);
				
				
				weeks.add(newWeek);
			}
			
			
		}
	}
	

	private int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
}

	private double getWeekPorportionInHours(Calendar weekStart, Calendar taskStart, Calendar taskEnd, int totalTimeInMinutes){
		weekStart.set(weekStart.get(Calendar.YEAR), weekStart.get(Calendar.MONTH), weekStart.get(Calendar.DAY_OF_MONTH), 1, 1, 1);
		taskStart.set(taskStart.get(Calendar.YEAR), taskStart.get(Calendar.MONTH), taskStart.get(Calendar.DAY_OF_MONTH), 1, 1, 1);
		taskEnd.set(taskEnd.get(Calendar.YEAR), taskEnd.get(Calendar.MONTH), taskEnd.get(Calendar.DAY_OF_MONTH), 1, 1, 1);
		
		double total=0;
		double totalNumOfDays=((taskEnd.getTimeInMillis()-taskStart.getTimeInMillis())/(double)numOfMilliSecondsinDay)+1;
//		System.out.println("Total number of Days: "+totalNumOfDays);
		double averageTimePerDay=(totalTimeInMinutes/60.0)/totalNumOfDays;
//		System.out.println("Average: "+averageTimePerDay);
		Calendar weekEnd=(Calendar)weekStart.clone();
		weekEnd.add(Calendar.DAY_OF_MONTH, 8); //Increase by 8 days to have the end be exclusitory
		int days=0;
		
		if(taskStart.before(weekStart)&&taskEnd.before(weekStart)){
			//do nothing
		} 
		                               //(Not before === after with inclusion of same day..?)
		else if(taskStart.before(weekStart)&&(!taskEnd.before(weekStart))){
			   
		//starts before weekStart, ends after weekStart
			days=daysBetween(weekStart.getTime(),taskEnd.getTime())+1;
		}
		
		//starts after weekStart, ends before weekend
		else if(!taskStart.before(weekStart)&&taskEnd.before(weekEnd)){
			days=daysBetween(taskStart.getTime(),taskEnd.getTime())+1;
		} 
		
		//starts before weekEnd, ends after weekEnd
		else if(taskStart.before(weekEnd)&&!taskEnd.before(weekEnd)){
			days=daysBetween(taskStart.getTime(),weekEnd.getTime())+1;
		}    
		
		//starts after weekEnd, and Ends after weekend end
		else if(!taskStart.before(weekEnd)&&!taskEnd.before(weekEnd)){
			//do nothing
		}
		
		//starts before weekStart, ends after weekEnd
		else if(!taskStart.after(weekStart)&&!taskEnd.before(weekEnd)){
			days=7;
		}
		else{
			System.out.println("Encountered unexpected situation");
		}
		total=days*averageTimePerDay;
		
		
		
		
		/*		for (int i=0;i<8;i++){  //Used 7 for 7 Days
			if((day.after(taskStart) &&day.before(taskEnd))){
				total+=averageTimePerDay;
				
			}
			else if(day.compareTo(taskStart)==0){
				total+=averageTimePerDay;
				
			}else if(day.compareTo(taskEnd)==0){
				total+=averageTimePerDay;
			}
			
			day.add(Calendar.DAY_OF_MONTH, 1);
			
			/*if(((weekStart.getTimeInMillis()+i*numOfMilliSecondsinDay)-taskStart.getTimeInMillis())>=-25
			&& ((weekStart.getTimeInMillis()+i*numOfMilliSecondsinDay)-taskEnd.getTimeInMillis())<=25){
				total+=averageTimePerDay;
			}
	//		System.out.println("Week Start - Task start is: "+((weekStart.getTimeInMillis()+i*numOfMilliSecondsinDay)-taskStart.getTimeInMillis()));
	//		System.out.println("Week Start - Task End is: "+((weekStart.getTimeInMillis()+i*numOfMilliSecondsinDay)-taskEnd.getTimeInMillis()));
		}*/
		return total;
	}
	private Calendar convertTeamworkDateToCalendar(String twString){
		Calendar newCalendar= Calendar.getInstance();
	//	System.out.println(twString);
		newCalendar.set(Integer.parseInt(twString.substring(0, 4)), 
						Integer.parseInt(twString.substring(4, 6))-1, //Teamwork 01=Jan, Java 01=Feb
						Integer.parseInt(twString.substring(6, 8)));
		
		return newCalendar;
	}
	
	public ArrayList<String> getListOfPeople() {
		
		
		HashSet<String> people= new HashSet<String>();
		ArrayList<String> sortedPeople=new ArrayList<String>();
		for(int i=0;i<tasks.length();i++){
			
			try {
					people.add(tasks.getJSONObject(i).getString("responsible-party-summary"));
				} catch (JSONException e) {
					people.add("Anybody");
				}
			}			
		Object[] unsortedListOfPeople= people.toArray();
		
		for(int i=0;i<unsortedListOfPeople.length;i++){
			sortedPeople.add((String) unsortedListOfPeople[i]);
		}
		sortedPeople.sort(String::compareToIgnoreCase);
		
		return sortedPeople;
	}
	
	
	
	
	private void addTimeCalculationsToTasks(int numOfWeeks) throws JSONException{
		if(startCalendarSet){
			setWeeks(numOfWeeks);
			int size=tasks.length();
			
			for (int i=0;i<size;i++){
				JSONArray taskContributions=new JSONArray();
				for (int j=0;j<weeks.size()-1;j++){
					if(tasks.getJSONObject(i).getString("start-date").length()==8
					&& tasks.getJSONObject(i).getString("due-date").length()==8){
						
						taskContributions.put(getWeekPorportionInHours(
								weeks.get(j),
								convertTeamworkDateToCalendar(tasks.getJSONObject(i).getString("start-date")),
								convertTeamworkDateToCalendar(tasks.getJSONObject(i).getString("due-date")),
								tasks.getJSONObject(i).getInt("estimated-minutes")
								));
						}
						
					else{
						taskContributions.put(0.0);
					}
				}
				tasks.getJSONObject(i).append(weekKey, taskContributions);
				
			//	System.out.println("Added: "+taskContributions.toString()+ " to task id: "+ tasks.getJSONObject(i).getInt("id")+" with Task Name "+tasks.getJSONObject(i).getString("content"));
				
			}
			calculateTotals();
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
			URL url= new URL("http://iamdanonline.teamwork.com/tasks.json?page="+page);
			
		    //Authentication Code
			String name = "twp_bGd6yIZ8ANS0TVB61V0QVOSHJ9rg";
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
			setProjectList();
			dataIsLoaded=true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	private void setProjectList(){
		projects= new HashSet<String>();
		for(int i=0;i<tasks.length();i++){
			try {
				projects.add(tasks.getJSONObject(i).getString("project-name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			setProjectList();
			dataIsLoaded=true;
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	private void calculateTotals(){
		Totals=new HashMap<String, Double>();
		
		//Initialize all totals to zero
		for(int j=0;j<weeks.size();j++){	
			for(int i=0;i<tasks.length();i++){
				try{
				Totals.put(tasks.getJSONObject(i).getString("responsible-party-summary")+
						   tasks.getJSONObject(i).getString("project-name")+
						   j //j is week number
						   , 0.0);
				}
				catch(JSONException e){
		//			System.out.println("Task: "+tasks.getJSONObject(i).get("content")+" does not have is not assigned");
				}
			}
		}
		//Calculate totals
		for(int j=0;j<weeks.size();j++){	
			
			for(int i=0;i<tasks.length();i++){
				try{
			/*	System.out.println("Key is: "+tasks.getJSONObject(i).getString("responsible-party-summary")+
							   tasks.getJSONObject(i).getString("project-name")+
							   j +", hours to be added: " +tasks.getJSONObject(i).getJSONArray(weekKey).getJSONArray(0).getDouble(j)
							   + " to "+Totals.get(tasks.getJSONObject(i).getString("responsible-party-summary")+
									   tasks.getJSONObject(i).getString("project-name")+
									   j).doubleValue());*/
					
				Totals.put(tasks.getJSONObject(i).getString("responsible-party-summary")+
						   tasks.getJSONObject(i).getString("project-name")+
						   j //j is week number
						   , Totals.get(tasks.getJSONObject(i).getString("responsible-party-summary")+
						   tasks.getJSONObject(i).getString("project-name")+
						   j)+ (tasks.getJSONObject(i).getJSONArray(weekKey).getJSONArray(0).getDouble(j)));
				}catch(JSONException e){
				//	System.out.println("Task: "+tasks.getJSONObject(i).get("content")+" does not have is not assigned");
				}
			}
		}
		
	}
	private CategoryDataset createDataset(String person ) {
		
		
	    final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );  
	    
	    
	   // double [] weeklyTotals=new double[weeks.size()];
	    Calendar endWeek;
	   
	    /*
	    for (int i=0;i<weeklyTotals.length;i++){
	    	weeklyTotals[i]=0;
	    }
	    
	    for (int i=0;i<tasks.length();i++){
	    	for(int j=0;j<weeklyTotals.length-1;j++){
	    		try {
		    			if(tasks.getJSONObject(i).getString("responsible-party-summary").compareTo(person)==0){
			    			endWeek=(Calendar) weeks.get(j+1).clone();
							weeklyTotals[j]+=tasks.getJSONObject(i).getJSONArray(weekKey).getJSONArray(0).getDouble(j);
							if(tasks.getJSONObject(i).getJSONArray(weekKey).getJSONArray(0).getDouble(j)!=0){
								System.out.println(tasks.getJSONObject(i).get("content")+ " contributes "+
								tasks.getJSONObject(i).getJSONArray(weekKey).getJSONArray(0).getDouble(j)
								+" to the week of "+sdf.format(weeks.get(j).getTime())+" - "+sdf.format(endWeek.getTime()));
						}
	    			}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				}
	    	}
	    }
	    
	    //Add Totals to Graph
	    for (int i=0;i<weeklyTotals.length-1;i++){
	    	endWeek=(Calendar) weeks.get(i+1).clone();
	    	dataset.addValue(weeklyTotals[i], 
					"Total", 
					sdf.format(weeks.get(i).getTime())+" - "+sdf.format(endWeek.getTime()));
	    }
	    
	    
	     */
	    
	    
	    
	    
	    
	    
	    
	    Object[] projectlist= projects.toArray();
	    
	//	System.out.println("Size of Project list: "+projectlist.length);
		for (int i=0;i<projectlist.length;i++){
			for(int j=0;j<weeks.size();j++)
			try{
				if(Totals.get(person+(String)projectlist[i]+j)!=0){
						endWeek=(Calendar) weeks.get(j+1).clone();
				//		endWeek.add(Calendar.DAY_OF_MONTH, -1);
						dataset.addValue(Totals.get(person+(String)projectlist[i]+j), 
								(String)projectlist[i], 
								sdf.format(weeks.get(j).getTime()));
						
					}
				
				}catch(Exception e){
					dataset.addValue(0.0, 
							"", 
							sdf.format(weeks.get(j).getTime()));
			}
			
		}
		/*
		for(int i=0;i<dataset.getColumnCount();i++){
			System.out.println("Column "+ i+" is "+dataset.getColumnKey(i)) + dataset.g;
		}
	    
	    for (int i=0;i<tasks.length();i++){
	    	  try {
	    		  for(int j=0;j<weeks.size()-1;j++){
	    			  if(person.compareTo(tasks.getJSONObject(i).getString("responsible-party-summary"))==0){
	    				  Calendar nextWeek=(Calendar) weeks.get(j+1).clone();
	    				  nextWeek.add(Calendar.DAY_OF_MONTH, -1);
	    				  
	    				  dataset.addValue(tasks.getJSONObject(i).getJSONArray(weekKey).getJSONArray(0).getDouble(j),
	    						  tasks.getJSONObject(i).getString("project-name"), 
	    						  sdf.format(weeks.get(j).getTime())+" - "+sdf.format(nextWeek.getTime()));
	    				  ;
	    			  }
	    		  }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				
			}
	    	 // dataset.addValue(value, rowKey, columnKey);weeks.get(i)
	      }
	      
	      dataset.addValue( 1.0 , fiat , speed );        
	      dataset.addValue( 3.0 , fiat , userrating );        
	      dataset.addValue( 5.0 , fiat , millage ); 
	      dataset.addValue( 5.0 , fiat , safety );           

	      dataset.addValue( 5.0 , audi , speed );        
	      dataset.addValue( 6.0 , audi , userrating );       
	      dataset.addValue( 10.0 , audi , millage );        
	      dataset.addValue( 4.0 , audi , safety );
	      dataset.addValue( 20.0 , "TestCar" , safety );
	      dataset.addValue( 4.0 , ford , speed );        
	      dataset.addValue( 2.0 , ford , userrating );        
	      dataset.addValue( 3.0 , ford , millage );        
	      dataset.addValue( 6.0 , ford , safety );              
*/
	      return dataset; 
	   }
	
	public void showChart(String person){

	
		if(dataIsLoaded){
			
			 JFreeChart barChart;
			barChart = ChartFactory.createStackedBarChart(
			         person,           
			         "Week Starting",            
			         "Hours",            
			         this.createDataset(person),          
			         PlotOrientation.VERTICAL,           
			         true, true, false);
			
			ChartFrame frame = new ChartFrame(person+"'s Chart (TESTING VERSION)",barChart);
			frame.pack();
			frame.setVisible(true);
			
				// create and display a frame...
			
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
