package com.e3ps;

import java.time.Instant;

public class Test2 {

	public static void main(String[] args) throws Exception {
		Instant now = Instant.now();
		long currentTimeInSeconds = now.getEpochSecond();

		// 에포크 이후로 경과한 시간을 초로 표현
		long elapsedSeconds = currentTimeInSeconds % 60;
		long start = System.currentTimeMillis() % 60;
		System.out.println("현재 시간 (초): " + elapsedSeconds);
		System.out.println("현재 시간 (초): " + start);
	}
}