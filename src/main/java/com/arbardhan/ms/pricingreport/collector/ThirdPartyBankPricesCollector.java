package com.arbardhan.ms.pricingreport.collector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arbardhan.ms.pricingreport.listener.ThirdPartyPriceListenerImpl;
import com.arbardhan.ms.pricingreport.util.PriceObject;

@Component
public class ThirdPartyBankPricesCollector {
	
	private Map<String , PriceObject> streamingTpPriceCollector;
	private Map<String , PriceObject> tpCalculationMap;
	private BlockingQueue<PriceObject> thirdPartyQueue =new LinkedBlockingQueue<>();
	
	@Autowired
	private ThirdPartyPriceListenerImpl thirdPartyPartyListenerImpl;
	/*
	public
	ThirdPartyBankPricesCollector()
	{
		this.thirdPartyQueue.addAll(thirdPartyPartyListenerImpl.getThirdPartyCalcQueue());		
	}
	*/
	private void collectThirdPartyStreamingPrices()
	{
		thirdPartyPartyListenerImpl.getThirdPartyCalcQueue().drainTo(this.thirdPartyQueue);
		streamingTpPriceCollector = new HashMap<>();
	while(true)
	{
	try {
		
		PriceObject priceObject = thirdPartyQueue.poll(1, TimeUnit.SECONDS);
		
		if(null == priceObject)
			throw new TimeoutException();
		//System.out.println("from TP Queue"+priceObject.getRecentPrice() + " "+ priceObject.getRecentTime());
		// If putIfAbsent Returns null that means previous key was not preset .. we wont have null here	
       if(null!= streamingTpPriceCollector.putIfAbsent(priceObject.getSymbol(), priceObject));
		streamingTpPriceCollector.computeIfPresent(priceObject.getSymbol(), (k,v)->	
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
			// TODO Auto-generated catch block
			System.out.println("TP empty or Service is down so its empty");
			break;
		}
		catch (InterruptedException e) 
		{
		System.out.println("Something Bad Happened");	
		e.printStackTrace();
		break;
		}
	
	}
	}
	
	// Call this with a delay
	public Map<String , PriceObject>  getCalculationMap()
	{
		collectThirdPartyStreamingPrices();	
		tpCalculationMap= new HashMap<>();
		tpCalculationMap.putAll(streamingTpPriceCollector);		
		return tpCalculationMap;
		
	}

	
	
		
	
	

}
