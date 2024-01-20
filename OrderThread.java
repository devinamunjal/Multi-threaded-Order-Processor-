package processor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class OrderThread implements Runnable{

	private Map <Integer, String > map;
	private Map <String, Double > item$;
	private Map <Integer, Order> idsNOrdersMap;
	
	
	public OrderThread(Map <Integer, String> map, Map<String, Double> item$, Map <Integer, Order >idsNOrdersMap) 
	{
		this.map = map;
		this.item$ = item$;
		this.idsNOrdersMap = idsNOrdersMap;

	}
	
	public void run() 
	{
		for(int orderNum: map.keySet()) 
		{
			try 
			{
				synchronized(idsNOrdersMap)
				{
					ArrayList <String> itemsList = new ArrayList<String>();
					BufferedReader bufferedReader = new BufferedReader(new FileReader(map.get(orderNum)));
					String line;
					bufferedReader.readLine();
					while ((line = bufferedReader.readLine()) != null) 
					{ 
						itemsList.add(line.substring(0, line.indexOf(" ")));
						itemsList.sort(Comparator.naturalOrder());
					}
					bufferedReader.close();
					Order order = new Order(orderNum, itemsList, item$);
						
					idsNOrdersMap.put(orderNum, order);
					bufferedReader.close();
				}
			}
				
			catch (IOException e) 
			{
				System.err.println(e.getMessage());
			}
		}
			
		
	}
	
	

}
