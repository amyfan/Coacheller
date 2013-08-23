package com.ratethisfest.android.log;

public class ConsoleLogger implements LogInterface {

	@Override
	public void logStandard(String message) {
		System.out.println(message);
	}

	@Override
	public void logError(String message) {
		System.err.println(message);
	}

}
