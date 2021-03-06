package com.donglu.carpark.service;


import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;

public interface CarPayServiceI {
	Long saveCarPayHistory(CarPayHistory cp);

	List<CarPayHistory> findCarPayHistoryByLike(int i, int maxValue, String plateNo, Date start, Date end);

	int countCarPayHistoryByLike(String plateNo, Date start, Date end);
	
}
