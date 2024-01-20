package processor;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class OrdersProcessor {	
	public static void main(String args[]) {
		
		//declare variables
		String baseName;
		String resultName;
		int orderNum;
		boolean yesThreads = false;
		String dataFile;
		Scanner scanner = new Scanner(System.in);
		ArrayList<String> list = new ArrayList<String>();

		Map <Integer, String> idsNtxtMap = new TreeMap<>();
		Map <String, Double> idsNPricesMap = new TreeMap<>();
		Map <Integer, Order> idsNOrdersMap = new TreeMap<>();
		String readingOrderTXT = "Reading order for client with id: ";
		
		//user input
		System.out.println("Enter item's data file name: ");
		dataFile = scanner.next();
		System.out.println("Enter 'y' for multiple threads, any other character otherwise: ");
		if(scanner.next().compareToIgnoreCase("y") == 0)
		{
			yesThreads = true;
		}
		System.out.println("Enter number of orders to process: ");
		orderNum = scanner.nextInt();
		System.out.println("Enter order's base filename: ");
		baseName = scanner.next();
		System.out.println("Enter result's filename: ");
		resultName = scanner.next();
		ArrayList<Thread> threads = new ArrayList<>();;
		
		
		long startTime = System.currentTimeMillis();
		

		
		if(yesThreads == false)
		{
			for(int i = 1; i <= orderNum; i++)
			{
				list.add(baseName + i + ".txt");
			}
			 //set ids and txt map to new map created by the method
			idsNtxtMap = createMapIdsnTxt(list);
			
			//set items and prices map to new map created by the method
			idsNPricesMap = createMapItemsnPrices(dataFile);
			
			//set ids and orders map to map created by process method
			idsNOrdersMap = processOrders(yesThreads, idsNtxtMap, idsNPricesMap, idsNOrdersMap);
			
		}
		
	
		
		//sifting through the files to see if they match base name
		if(yesThreads)
		{
			for(int i = 1; i <= orderNum; i++)
			{
				list.add(baseName + i + ".txt");
				idsNtxtMap = createMapIdsnTxt(list);
				
				idsNPricesMap = createMapItemsnPrices(dataFile);
				OrderThread oThread = new OrderThread(idsNtxtMap, idsNPricesMap, idsNOrdersMap);
				Thread thread = new Thread(oThread);
				threads.add(thread);
			}
		
			for(Thread cT: threads)
			{
				cT.start();
			}
			for(Thread cT: threads)
			{
				try 
				{
					cT.join();
				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
	
		long endTime = System.currentTimeMillis();
		
		
		FileWriter fileWriter;
		try 
		{
			fileWriter = new FileWriter(resultName);
			//printWriter = new PrintWriter(fileWriter);
			for(Integer i: idsNOrdersMap.keySet())
			{
				fileWriter.write(idsNOrdersMap.get(i).toString());
			
			}
			fileWriter.write(toString(idsNOrdersMap, idsNPricesMap));
			fileWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		/* TASK YOU WANT TO TIME */

		System.out.println("Processing time (msec): " + (endTime - startTime));
		scanner.close();
		}
		

	//creates a map linking the client ids with the specific order(txt file)
	public static Map<Integer, String> createMapIdsnTxt(ArrayList<String> txtFilesList)
	{
		Map <Integer, String> idsNtxtMAP = new TreeMap<Integer, String>();
		for(int i = 0; i < txtFilesList.size(); i++) 
		{
			int orderNum = 0;
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(txtFilesList.get(i)));
				//System.out.println(txtFilesList.get(i));
				String line = bufferedReader.readLine();
				line = line.substring(line.indexOf(":") + 2);
				orderNum = Integer.parseInt(line);
				System.out.println("Reading order for client with id: " + orderNum);
				bufferedReader.close();
			
			} catch (IOException e) {
				System.err.println(e.getMessage());
			
			}
			
			idsNtxtMAP.put(orderNum, txtFilesList.get(i));
		}
		return idsNtxtMAP;
	
	}
	
	//create map with items and prices based on the given items data file name
	public static Map<String, Double > createMapItemsnPrices(String fileName)
	{
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			Map <String, Double > itemsNPricesMAP = new TreeMap <String, Double>();
			String line;
			String [] splitLine;
			Double price;
			while ((line = bufferedReader.readLine()) != null) 
			{ 
				splitLine = line.split(" ");
				price = Double.parseDouble(splitLine[1]);
				itemsNPricesMAP.put(splitLine[0], price);
			}
			bufferedReader.close();
//		
			return itemsNPricesMAP;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			return null;
			
		}
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																
		//writing to file
		
	}
	
	//process each every order(go through the files and make order objects)
	public static Map <Integer, Order> processOrders(Boolean threadsOno, Map <Integer, String> map, Map<String, Double> item$, Map <Integer, Order >idsNOrdersMap) 
	{
		for(int orderNum: map.keySet()) 
		{
			try 
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
			}
				
			
			catch (IOException e) 
			{
				System.err.println(e.getMessage());
			}
			
		}
		return idsNOrdersMap;
	}
	
	//to string method
	public static String toString(Map <Integer, Order> ordersMap, Map <String, Double> item$Map) 
	{
		Map<String, Integer> quanityMap = new TreeMap<String, Integer>();
		double total = 0;
		String str = "***** Summary of all orders *****\n";
		for(Integer orderN: ordersMap.keySet())
		{
			for(String x: item$Map.keySet())
			{
				if(ordersMap.get(orderN).getItems().contains(x))
				{
					int prevQuanity = 0;
					if(quanityMap.get(x) != null)
					{
						prevQuanity = quanityMap.get(x);
					}
					quanityMap.put(x, (prevQuanity + ordersMap.get(orderN).getQuantity(x)));

				}
			}
		}
		for(String s: quanityMap.keySet())
		{
			total += (item$Map.get(s) * quanityMap.get(s));
			str += "Summary - Item's name: " + s + ", Cost per item: " + NumberFormat.getCurrencyInstance().format(item$Map.get(s)) + ", Number sold: " + quanityMap.get(s) + ", Item's Total: " + 
			NumberFormat.getCurrencyInstance().format(item$Map.get(s) * quanityMap.get(s));
			str += "\n";
			
		}
		//System.out.println(str);
		str += "Summary Grand Total: " +  NumberFormat.getCurrencyInstance().format(total) + "\n";
		return str;
		
	
	} 
	


	
}