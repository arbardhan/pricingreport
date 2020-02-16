package com.arbardhan.ms.pricingreport.util;

import org.springframework.stereotype.Component;


public class PriceObject {
	
	private String symbol;
	private double recentPrice;
	private long recentTime;
	private double olderPrice;
	private long olderTime;
	private long timeDelay;
	
	public PriceObject(String symbol, double  recentPrice, long recentTime, double olderPrice, long olderTime, long timeDelay) 
	{
		this.olderPrice =olderPrice;
		this.olderTime = olderTime;
		this.recentPrice = recentPrice;
		this.recentTime = recentTime;
		this.symbol = symbol;
		this.timeDelay = timeDelay;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public double getRecentPrice() {
		return recentPrice;
	}
	public void setRecentPrice(double recentPrice) {
		this.recentPrice = recentPrice;
	}
	public long getRecentTime() {
		return recentTime;
	}
	public void setRecentTime(long recentTime) {
		this.recentTime = recentTime;
	}
	public double getOlderPrice() {
		return olderPrice;
	}
	public void setOlderPrice(double olderPrice) {
		this.olderPrice = olderPrice;
	}
	public long getOlderTime() {
		return olderTime;
	}
	public void setOlderTime(long olderTime) {
		this.olderTime = olderTime;
	}
	public long getTimeDelay() {
		return timeDelay;
	}
	public void setTimeDelay(long timeDelay) {
		this.timeDelay = timeDelay;
	}

	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PriceObject other = (PriceObject) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "PriceObject [symbol=" + symbol + ", recentPrice=" + recentPrice + ", recentTime=" + recentTime
				+ ", olderPrice=" + olderPrice + ", olderTime=" + olderTime + ", timeDelay=" + timeDelay + "]";
	}
	
	
	
	

}
