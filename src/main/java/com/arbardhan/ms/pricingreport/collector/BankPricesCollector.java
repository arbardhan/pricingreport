package com.arbardhan.ms.pricingreport.collector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arbardhan.ms.pricingreport.listener.BankPriceListenerImpl;
import com.arbardhan.ms.pricingreport.util.PriceObject;

@Component
public class BankPricesCollector {
	
	private Map<String , PriceObject> streamingBankPriceCollector;
	private Map<String , PriceObject> bankCalculationMap;
	private BlockingQueue<PriceObject> bankQueue=new LinkedBlockingQueue<>();
	
	@Autowired
	BankPriceListenerImpl bankPriceListenerImpl;
	
	
	/*
	public	BankPricesCollector(BankPriceListenerImpl bankPriceListenerImpl)
	{
		this.bankQueue=bankPriceListenerImpl.getBankQueue();		
	}
	*/
	
	
	@SuppressWarnings("finally")
	public void collectBankStreamingPrices()
	{
		bankPriceListenerImpl.getBankQueue().drainTo(this.bankQueue);	
	  streamingBankPriceCollector = new HashMap<>();
	while(true)
	{
	try {
		PriceObject priceObject = getFromQueue();
		if (null == priceObject)
			throw new TimeoutException();
		//System.out.println("from Bank Queue"+priceObject.getRecentPrice() + " "+ priceObject.getRecentTime());
		// If putIfAbsent Returns null that means previous key was not preset .. we wont have null here	
       if(null!= streamingBankPriceCollector.putIfAbsent(priceObject.getSymbol(), priceObject));
		streamingBankPriceCollector.computeIfPresent(priceObject.getSymbol(), (k,v)->	
		{ 
		  v.setTimeDelay(priceObject.getOlderTime() -v.getRecentTime() );
		  double olderPrice = v.getRecentPrice();
		  long olderTime = v.getRecentTime();
		  v.setOlderPrice(olderPrice);
		  v.setOlderTime(olderTime);
		  v.setRecentPrice(priceObject.getOlderPrice());
		  v.setRecentTime(priceObject.getOlderTime());
		  return v;
		}
		);
		} 
		catch (TimeoutException e) 
		{
			System.out.println("Bank empty or Service is down so its empty");
			break;
			
		}
		catch (InterruptedException e) 
		{
			System.out.println("Something Bad Happened");	
			e.printStackTrace();
		}

	}
	}

	public PriceObject getFromQueue() throws InterruptedException {
		return bankQueue.poll(1 ,TimeUnit.SECONDS);
	}
	
	public Map<String , PriceObject>  getCalculationMap()
	{
		collectBankStreamingPrices();
		bankCalculationMap = new HashMap<>();
		bankCalculationMap.putAll(streamingBankPriceCollector);		
		return bankCalculationMap;
	}

}

