package com.e3ps.change.beans;

import java.sql.Timestamp;
import java.util.Comparator;

import org.apache.commons.net.ntp.TimeStamp;

import com.e3ps.change.EChangeOrder;

public class EoComparator implements Comparator{
	
	public EoComparator(){
		
		
	}
	@Override
	public int compare(Object o1, Object o2) {
		int result = 0;
		if(o1 instanceof EChangeOrder){
			EChangeOrder eco1 =(EChangeOrder)o1;
			EChangeOrder eco2 =(EChangeOrder)o2;
			
			Timestamp ts1 = eco1.getCreateTimestamp();
			Timestamp ts2 = eco2.getCreateTimestamp();
			result = ts2.compareTo(ts1);
		}
		
		return result;
		
	}

}
