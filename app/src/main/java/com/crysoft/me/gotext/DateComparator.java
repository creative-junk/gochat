package com.crysoft.me.gotext;

import java.util.Comparator;

public class DateComparator implements Comparator<SMSData> {

	@Override
	public int compare(SMSData leftHandSide, SMSData rightHandSide) {
		return leftHandSide.getDateObj().compareTo(rightHandSide.getDateObj());
	}

}
