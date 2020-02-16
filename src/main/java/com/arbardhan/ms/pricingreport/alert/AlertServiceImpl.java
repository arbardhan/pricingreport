package com.arbardhan.ms.pricingreport.alert;

import org.springframework.stereotype.Component;

@Component
public class AlertServiceImpl implements AlertService {

	@Override
	public void alert(String message) {
		System.out.println(message);
		
	}

}
