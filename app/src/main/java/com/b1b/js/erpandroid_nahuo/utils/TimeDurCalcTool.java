package com.b1b.js.erpandroid_nahuo.utils;

import java.util.ArrayList;
import java.util.List;

public class TimeDurCalcTool {
	private long nowTime;
	private List<Long> durArray = new ArrayList<Long>();

	public void recordTime() {
		nowTime = System.currentTimeMillis();
		durArray.add(nowTime);
	}

	public long getDur(int i) {
		if (i < 1 || i > durArray.size() - 1) {
			throw new RuntimeException("index out of size:" + durArray.size());
		}
		return durArray.get(i) - durArray.get(i - 1);
	}

}
