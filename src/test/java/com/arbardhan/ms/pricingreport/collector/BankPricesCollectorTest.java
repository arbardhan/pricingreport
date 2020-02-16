package com.arbardhan.ms.pricingreport.collector;

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
public class BankPricesCollectorTest {
	
	@MockBean
	BankPricesCollector bankPricesCollector;

	
	@Test	
	public void test_getCalculationMap_checkRecordCount() throws InterruptedException
	{
		
		PriceObject priceObject = new PriceObject("A",33.22,1581868316674L,27.75,1581868321758L,0L);
		Map<String,PriceObject> map = new HashMap<>();
		map.put("A", priceObject);				
		when(bankPricesCollector.getCalculationMap()).thenReturn(map);		
		assertThat(bankPricesCollector.getCalculationMap().size()).isEqualTo(1);
	
	}
	

}
