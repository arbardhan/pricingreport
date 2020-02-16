package com.arbardhan.ms.pricingreport.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.arbardhan.ms.pricingreport.alert.AlertServiceImpl;
import com.arbardhan.ms.pricingreport.collector.BankPricesCollector;
import com.arbardhan.ms.pricingreport.collector.ThirdPartyBankPricesCollector;
import com.arbardhan.ms.pricingreport.evaluator.AlertGenerationFlow;
import com.arbardhan.ms.pricingreport.util.PriceObject;
import com.arbardhan.ms.pricingreport.util.ThirdPartyServiceFirstRecordOfNextStream;

@Component
public class PriceComparisionService implements ApplicationListener {

	@Autowired
	private ThirdPartyBankPricesCollector thirdPartyPricesCollector;

	@Autowired
	private BankPricesCollector bankPricesCollector;
	
	@Autowired
	AlertGenerationFlow alertGenerationFlow;
	
	@Autowired
	AlertServiceImpl alertService;

	private static long oldTime;

	Map<String, PriceObject> tpCalculationMap;
	Map<String, PriceObject> bankCalculationMap;

	List<String>  calculate(String message) {

		System.out.println("Started execution of service");
		List<String> alertCodes = new ArrayList<>();

		try {
			Thread.sleep(4 * 1000);
		} catch (InterruptedException e) {
			System.out.println("Something happened while sleeping");
			e.printStackTrace();
		}
		tpCalculationMap = thirdPartyPricesCollector.getCalculationMap();
		bankCalculationMap = bankPricesCollector.getCalculationMap();
		System.out.println("Size of tp Map" + tpCalculationMap.size());
		System.out.println("Size of bank Map" + bankCalculationMap.size());
		
		if(message.equals("INVALID"))
			return alertCodes;
		alertCodes.addAll(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap));		
			return alertCodes;		
		/*
		for (Map.Entry<String, PriceObject> record : tpCalculationMap.entrySet()) {
			String s = record.getKey() + " " + record.getValue().getOlderPrice() + " "
					+ record.getValue().getOlderTime() + " " + record.getValue().getRecentPrice() + " "
					+ record.getValue().getRecentTime();
			System.out.println(s);

		}

		for (Map.Entry<String, PriceObject> record : bankCalculationMap.entrySet()) {
			String s = record.getKey() + " " + record.getValue().getOlderPrice() + " "
					+ record.getValue().getOlderTime() + " " + record.getValue().getRecentPrice() + " "
					+ record.getValue().getRecentTime();
			System.out.println(s);

		}
		*/

	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		
		if (event instanceof ThirdPartyServiceFirstRecordOfNextStream) {
			System.out.println("The calculate Method needs to be run now");
			System.out.println("Old Time " + oldTime);
			long difference = (oldTime == 0) ? 0 : event.getTimestamp() - oldTime;
			oldTime = event.getTimestamp();
			System.out.println("TimeStamp now is " + oldTime + " time diffe in seconds "
					+ TimeUnit.MILLISECONDS.toSeconds(difference));

			if ((TimeUnit.MILLISECONDS.toSeconds(difference) > 30) || difference == 0) {
				List<String> alertCodes= calculate("Valid");
				for(String alert : alertCodes)
					alertService.alert(alert);
			} else
			{
				System.out.println("Too late clear queue");
				calculate("INVALID");
			}

		}

	}

}
