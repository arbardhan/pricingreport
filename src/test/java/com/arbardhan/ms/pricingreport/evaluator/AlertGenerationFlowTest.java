package com.arbardhan.ms.pricingreport.evaluator;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.arbardhan.ms.pricingreport.util.PriceObject;
@RunWith(SpringRunner.class)
public class AlertGenerationFlowTest {
	
	@MockBean
	AlertGenerationFlow alertGenerationFlow;
	
	
	private static final String BANK_SERVICE_CONNECTION_DOWN = "BANK_SERVICE_CONNECTION_DOWN";
	private static final String THIRD_PARTY_SERVICE_CONNECTION_DOWN = "THIRD_PARTY_SERVICE_CONNECTION_DOWN";	
	private static final String PRICE_MISMATCH = "PRICE_MISMATCH";
	private static final String TIMING_MISMATCH = "TIMING_MISMATCH";
	private static final String BANK_PRICE_NOT_RECEIVED = "BANK_PRICE_NOT_RECEIVED";
	private static final String THIRD_PARTY_NOT_RECEIVED = "THIRD_PARTY_PRICE_NOT_RECEIVED";
	private static final String BANK = "BANK";
	private static final String THIRD_PARTY = "THIRD_PARTY";
	private static final String PRICE = "PRICE";
	
	@Test
	public void test_banknoresponse_tpResponse()
	{
		PriceObject priceObjectA = new PriceObject("A",33.22,1581868316674L,27.75,1581868321758L,0L);
		PriceObject priceObjectB = new PriceObject("B",44.22,1581868316674L,56.75,1581868321758L,0L);
		
		Map<String , PriceObject> bankCalculationMap = new HashMap<>();
		Map<String , PriceObject> tpCalculationMap = new HashMap<>();
		tpCalculationMap.put("B", priceObjectB);
		when(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap)).thenCallRealMethod();
		assertThat(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap).get(0) ).isEqualTo(BANK_SERVICE_CONNECTION_DOWN);
		
	}
	@Test
	public void test_bankresponse_tpNoResponse()
	{
		PriceObject priceObjectA = new PriceObject("A",33.22,1581868316674L,27.75,1581868321758L,0L);
		PriceObject priceObjectB = new PriceObject("B",44.22,1581868316674L,56.75,1581868321758L,0L);
		
		Map<String , PriceObject> bankCalculationMap = new HashMap<>();
		Map<String , PriceObject> tpCalculationMap = new HashMap<>();
		bankCalculationMap.put("B", priceObjectB);
		when(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap)).thenCallRealMethod();
		assertThat(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap).get(0) ).isEqualTo(THIRD_PARTY_SERVICE_CONNECTION_DOWN);
		
	}
	
	@Test
	public void test_bankresponse_tpResponseSomePricesNotReceived()
	{
		PriceObject priceObjectA = new PriceObject("A",33.22,1581868316674L,27.75,1581868321758L,0L);
		PriceObject priceObjectB = new PriceObject("B",44.22,1581868316674L,56.75,1581868321758L,0L);
		
		Map<String , PriceObject> bankCalculationMap = new HashMap<>();
		Map<String , PriceObject> tpCalculationMap = new HashMap<>();
		bankCalculationMap.put("B", priceObjectB);
		bankCalculationMap.put("A", priceObjectA);
		tpCalculationMap.put("A", priceObjectA);
		when(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap)).thenCallRealMethod();
		assertThat(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap).get(0) ).isEqualTo(THIRD_PARTY_NOT_RECEIVED+"B");
		
	}
	
	@Test
	public void test_bankresponseSomePricesNotReceived_tpResponse()
	{
		PriceObject priceObjectA = new PriceObject("A",33.22,1581868316674L,27.75,1581868321758L,0L);
		PriceObject priceObjectB = new PriceObject("B",44.22,1581868316674L,56.75,1581868321758L,0L);
		
		Map<String , PriceObject> bankCalculationMap = new HashMap<>();
		Map<String , PriceObject> tpCalculationMap = new HashMap<>();
		tpCalculationMap.put("B", priceObjectB);
		bankCalculationMap.put("A", priceObjectA);
		tpCalculationMap.put("A", priceObjectA);
		when(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap)).thenCallRealMethod();
		assertThat(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap).get(0) ).isEqualTo(BANK_PRICE_NOT_RECEIVED+"B");
		
	}
	
	@Test
	public void test_bankresponse_tpResponse_pricemismatch()
	{
		PriceObject priceObjectA = new PriceObject("A",33.22,1581868316674L,27.75,1581868321758L,0L);
		PriceObject priceObjectAModified = new PriceObject("A",33.23,1581868316674L,27.76,1581868321758L,0L);
		PriceObject priceObjectB = new PriceObject("B",44.22,1581868316674L,56.75,1581868321758L,0L);
		String s = "PRICE_MISMATCH BANK 33.23 THIRD_PARTY 33.22";
		Map<String , PriceObject> bankCalculationMap = new HashMap<>();
		Map<String , PriceObject> tpCalculationMap = new HashMap<>();
		bankCalculationMap.put("A", priceObjectAModified);
		tpCalculationMap.put("A", priceObjectA);
		when(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap)).thenCallRealMethod();
		assertThat(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap).get(0) ).isEqualTo(s);
		
	}
	
	@Test
	public void test_bankresponse_tpResponse_pricematch()
	{
		PriceObject priceObjectA = new PriceObject("A",33.33,1581868316674L,27.76,1581868321758L,0L);
		PriceObject priceObjectAModified = new PriceObject("A",33.33,1581868316674L,27.76,1581868321758L,0L);		
		String s = "PRICE BANK 33.33 THIRD_PARTY 33.33";
		Map<String , PriceObject> bankCalculationMap = new HashMap<>();
		Map<String , PriceObject> tpCalculationMap = new HashMap<>();
		bankCalculationMap.put("A", priceObjectAModified);
		tpCalculationMap.put("A", priceObjectA);
		when(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap)).thenCallRealMethod();
		assertThat(alertGenerationFlow.alertCodes(bankCalculationMap, tpCalculationMap).get(0) ).isEqualTo(s);
		
	}

}
