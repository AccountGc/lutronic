package com.e3ps.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.drawing.service.EpmSearchService;

@Component
public class E3PSSPringScheduler {
	
	@SuppressWarnings("rawtypes")
	@Scheduled(cron="0 00 23 * * *")
	public static void callCadPublish(){
		
		
		
		
		try {
			//System.out.println("::::::::::::::: E3PSSPringScheduler callCadPublish START:::::::::::::::");
			SchedulingMethod.cadPublishScheduler();
			//System.out.println("::::::::::::::: E3PSSPringScheduler callCadPublish END :::::::::::::::");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
