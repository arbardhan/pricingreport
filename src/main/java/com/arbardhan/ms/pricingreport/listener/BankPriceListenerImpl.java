package com.arbardhan.ms.pricingreport.listener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arbardhan.ms.pricingreport.util.PriceObject;
import com.arbardhan.ms.pricingreport.util.ThirdPartyServiceFirstRecordOfCurrentStream;

@RestController
public class BankPriceListenerImpl implements PriceListener, ApplicationListener {
	
	private BlockingQueue<PriceObject> bankQueue = new LinkedBlockingQueue<>();
	private BlockingQueue<PriceObject> bankQueueforCalulation = new LinkedBlockingQueue<>();
	private BlockingQueue<PriceObject> bankQueueforCalulationSentOut = new LinkedBlockingQueue<>();

	@RequestMapping(value = "acceptbankprices" , method = RequestMethod.PUT)
	public void priceUpdateFromRest(@RequestParam("symbol")String symbol, @RequestParam("price") String  price) 
	{priceUpdate (symbol , Double.valueOf(price)); }
	
	@Override
	public void priceUpdate(String symbol, double price) 
	{
		
		long time = System.currentTimeMillis();
		
		//System.out.println("Bank Time is "+time+" Symbol is "+symbol + " Price is +" + price);
		bankQueue.add(getPriceObject(symbol,price,time) );
		System.out.println("Add Price to Banks partent Queue size "+bankQueue.size() );

	}
	
	private static PriceObject getPriceObject(String symbol, double price, long time)
	{
		
		return new PriceObject(symbol ,price,time,price,time ,0L);
	}
	
	public void drainQueue()
	{
		System.out.println("Inside drain queue");
		bankQueue.drainTo(bankQueueforCalulation);
		System.out.println("Bank queue size" +bankQueueforCalulationSentOut.size());
		bankQueueforCalulationSentOut.addAll(bankQueueforCalulation);
		bankQueueforCalulation.clear();
		System.out.println("Bank queue size" +bankQueueforCalulationSentOut.size());
	}
	
	public BlockingQueue<PriceObject> getBankQueue()
	{
		return bankQueueforCalulationSentOut;
 		
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if(event instanceof ThirdPartyServiceFirstRecordOfCurrentStream)
		{	
			System.out.println("ThirdPartyServiceFirstRecordOfCurrentStream has been called"+ event.getTimestamp());
			drainQueue();
		}
		
	}

}
