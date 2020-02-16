package com.arbardhan.ms.pricingreport.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.arbardhan.ms.pricingreport.util.PriceObject;


@Component
public class AlertGenerationFlow {
	
	private static final String BANK_SERVICE_CONNECTION_DOWN = "BANK_SERVICE_CONNECTION_DOWN";
	private static final String THIRD_PARTY_SERVICE_CONNECTION_DOWN = "THIRD_PARTY_SERVICE_CONNECTION_DOWN";	
	private static final String PRICE_MISMATCH = "PRICE_MISMATCH";
	private static final String TIMING_MISMATCH = "TIMING_MISMATCH";
	private static final String BANK_PRICE_NOT_RECEIVED = "BANK_PRICE_NOT_RECEIVED";
	private static final String THIRD_PARTY_NOT_RECEIVED = "THIRD_PARTY_PRICE_NOT_RECEIVED";
	private static final String BANK = "BANK";
	private static final String THIRD_PARTY = "THIRD_PARTY";
	private static final String PRICE = "PRICE";
	
	public List<String> alertCodes(Map<String, PriceObject> bankCalculationMap , Map<String, PriceObject> tpCalculationMap)
	
	{
		List<String> alertCodes = new ArrayList<>();
		
		if(bankCalculationMap.size() ==0 && tpCalculationMap.size() != 0)
		{	
			alertCodes.add(BANK_SERVICE_CONNECTION_DOWN);
			return alertCodes;
		}
		
		if(bankCalculationMap.size() !=0 && tpCalculationMap.size() == 0)
		{	
			alertCodes.add(THIRD_PARTY_SERVICE_CONNECTION_DOWN);
			return alertCodes;
		}
		
		if(bankCalculationMap.size() > tpCalculationMap.size())
		{
			bankCalculationMap.forEach((k,v)->
			{
				if(!tpCalculationMap.containsKey(k))
					alertCodes.add(THIRD_PARTY_NOT_RECEIVED + k);
			});
			
			return alertCodes;

		}
		
		
		if(bankCalculationMap.size() < tpCalculationMap.size())
		{
			tpCalculationMap.forEach((k,v)->
			{
				if(!bankCalculationMap.containsKey(k))
					alertCodes.add(BANK_PRICE_NOT_RECEIVED + k);
			});
			
			return alertCodes;

		}
		
		bankCalculationMap.forEach((k,v)->{
			PriceObject tpPriceObject = tpCalculationMap.get(k);
			if(v.getRecentPrice() != tpPriceObject.getRecentPrice())
				
				alertCodes.add(PRICE_MISMATCH+" "+BANK+ " "+v.getRecentPrice()+" "+THIRD_PARTY+ " "+tpPriceObject.getRecentPrice() );
			else
			{
				alertCodes.add(PRICE+" "+BANK+ " "+v.getRecentPrice()+" "+THIRD_PARTY+ " "+tpPriceObject.getRecentPrice() );
			}
			
		});
		return alertCodes;
	}

	
}

