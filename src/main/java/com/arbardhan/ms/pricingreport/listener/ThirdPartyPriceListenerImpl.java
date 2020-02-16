package com.arbardhan.ms.pricingreport.listener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arbardhan.ms.pricingreport.util.PriceObject;
import com.arbardhan.ms.pricingreport.util.ThirdPartyServiceFirstRecordOfCurrentStream;
import com.arbardhan.ms.pricingreport.util.ThirdPartyServiceFirstRecordOfNextStream;

@RestController
public class ThirdPartyPriceListenerImpl implements PriceListener,ApplicationEventPublisherAware {
	
	private BlockingQueue<PriceObject> thirdPartyQueue = new LinkedBlockingQueue<>();
	private BlockingQueue<PriceObject> thirdPartyQueueforCalculation = new LinkedBlockingQueue<>();
	private BlockingQueue<PriceObject> thirdPartyQueueforCalulationSentOut = new LinkedBlockingQueue<>();
	private static long oldTime=0L;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@RequestMapping(value = "accepttpprices" , method = RequestMethod.PUT)
	public void priceUpdateFromRest(@RequestParam("symbol")String symbol, @RequestParam("price") String  price) 
	{priceUpdate (symbol , Double.valueOf(price)); }

	@Override
	public  void priceUpdate(String symbol, double price) 
	{
	/* 
	 * There might be muliple threads where this is subscribed to.Assuming they will send prices in sequence.
	 * If not need to use a Priority Blocking Queue with a comparator on time derived below. There 
	 * is no way to know for sure what the true price order is if its multithreaded
	 
	 */
		
		long time = System.currentTimeMillis();
		long difference = (oldTime==0)?0:(time-oldTime);
		System.out.println("Time difference for second event "+TimeUnit.MILLISECONDS.toSeconds(difference) );
		if(TimeUnit.MILLISECONDS.toSeconds(difference) > 29)
		{
			System.out.println("Publish 2nd Stream");
			thirdPartyQueue.drainTo(thirdPartyQueueforCalculation);
			thirdPartyQueueforCalculation.drainTo(thirdPartyQueueforCalulationSentOut);			
			ThirdPartyServiceFirstRecordOfNextStream event2 = new ThirdPartyServiceFirstRecordOfNextStream(this);
			publisher.publishEvent(event2);
		}
		
		//System.out.println("TP Time is "+time+" Symbol is "+symbol + " Price is +" + price);
		if(thirdPartyQueue.size() == 0 )
		{	System.out.println("THE TP QUEUE SIZE IS ZERO AND VARIABLE SET TO TRUE");
			oldTime=time;
			ThirdPartyServiceFirstRecordOfCurrentStream event = new ThirdPartyServiceFirstRecordOfCurrentStream(this);
			publisher.publishEvent(event);
		}		
		thirdPartyQueue.add(getPriceObject(symbol,price,time));		
		System.out.println("Add Price to TP partent Queue size "+thirdPartyQueue.size() );		
	}
	
	private static PriceObject getPriceObject(String symbol, double price, long time)
	{return new PriceObject(symbol,price,time,price,time ,0L);}
	
	public BlockingQueue<PriceObject> getThirdPartyCalcQueue()
	{
		System.out.println("TP queue size" +thirdPartyQueueforCalulationSentOut.size());
		return thirdPartyQueueforCalulationSentOut; 		
	}


	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {	
		
		this.publisher=publisher;		
	}	
	

}
