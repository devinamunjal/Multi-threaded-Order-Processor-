package processor;

import java.util.ArrayList;
import java.text.NumberFormat;
import java.util.*;

public class Order {
	private int clientId;
	private ArrayList <String> items;
	private Map < String, Double > item$;
	
	public Order(int clientId, ArrayList <String> items, Map <String, Double> item$) 
	{
		this.clientId = clientId;
		this.items = items;
		this.item$  = new TreeMap<>(item$);
		
	}
	
	public Double getCost(String item)
	{
		return item$.get(item);
	}
	
	public Integer getQuantity(String name)
	{
		int counter = 0;
		for(String x: items)
		{
			if(x.equals(name))
			{
				counter++;
			}
		}
		return counter;
	}

	
	public String toString() {
		String str = "";
		double cost = 0;
		str += "----- Order details for client with Id: " + clientId +" -----\n";
		for(String x: item$.keySet())
		{
			if(getQuantity(x) != 0)
			{

				str += "Item's name: " + x + ", Cost per item: " + NumberFormat.getCurrencyInstance().format(getCost(x)) + ", Quantity: " + getQuantity(x) + ", Cost: " + 
				NumberFormat.getCurrencyInstance().format(getCost(x) * getQuantity(x));
				str += "\n";
				cost += (getCost(x) * getQuantity(x));
			}
		}
		//System.out.println(str);
		str += "Order Total: " +  NumberFormat.getCurrencyInstance().format(cost) + "\n";
		return str;
	}

	public ArrayList<String> getItems(){
		return items;
	}

}
