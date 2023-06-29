package com.e3ps.common.util;

import org.json.JSONArray;
import org.springframework.web.servlet.ModelAndView;

public class ControllerUtil {
	/**
	 * action 수행 후 redirect를 하기 위해 사용한다.
	 * @param page
	 * @return ModelAndView
	 * @author yhjang1
	 * @since 2014. 12. 12.
	 */
	public static ModelAndView redirect(String page){
		return redirect(page,null);
	}

	/**
	 * action 수행 후 redirect를 하기 위해 사용한다.
	 * @param page, message
	 * @return ModelAndView
	 * @author yhjang1
	 * @since 2014. 12. 12.
	 */
	public static ModelAndView redirect(String page, String message){

		ModelAndView model = new ModelAndView("/jsp/portal/redirect.jsp","page",page);
		if(message!=null){
			model.addObject("message", replace(message));
		}
		return model;
	}
	
	public static ModelAndView messageAlert(String message) {
		ModelAndView model = new ModelAndView("/jsp/portal/messageAlert.jsp");
		if(message!=null){
			model.addObject("message", replace(message));
		}
		return model;
	}
	
	
	/**
	 * action 수행 후 redirect를 하기 위해 사용한다.
	 * @param page
	 * @return ModelAndView
	 * @author yhjang1
	 * @since 2014. 12. 12.
	 */
	public static ModelAndView parentRedirect(String page){
		return parentRedirect(page,null);
	}
	
	public static ModelAndView parentRedirect(String page, String message){

		ModelAndView model = new ModelAndView("/jsp/portal/parentRedirect.jsp","page",page);
		if(message!=null){
			model.addObject("message", replace(message));
		}
		return model;
	}
	
	/**
	 * popup 창에서 action 수행 후 close를 하기 위해 사용한다.
	 * @param message
	 * @return ModelAndView
	 * @author yhjang1
	 * @since 2014. 12. 12.
	 */
	public static ModelAndView close(String message){

		ModelAndView model = new ModelAndView("/jsp/portal/close.jsp");
		if(message!=null){
			model.addObject("message", replace(message));
		}
		return model;
	}
	/**
	 * <pre>
	 * @desc  :
	 * @author : Chul ki Lee
	 * @date   : 2015. 6. 9.오후 8:47:29
	 * </pre>
	 * @method : closeRedirect
	 * @param message
	 * @param url
	 * @return
	 */
	public static ModelAndView closeRedirect(String url){
	   return closeRedirect(url ,null);
	}
	/**
	 * <pre>
	 * @desc  :
	 * @author : Chul ki Lee
	 * @date   : 2015. 6. 9.오후 8:47:29
	 * </pre>
	 * @method : closeRedirect
	 * @param message
	 * @param url
	 * @return
	 */
	public static ModelAndView closeRedirect(String url ,String message){
	   ModelAndView model = new ModelAndView("/jsp/portal/closeRedirect.jsp");      // =====> 만들어라 jsp 파일
	   if(message != null){
	      model.addObject("message", replace(message));
	   }
	   model.addObject("url", url);
	   return model;
	}
	
	public static ModelAndView openerRefresh() {
		return openerRefresh(null);
	}
	
	public static ModelAndView openerRefresh(String message) {
		ModelAndView model = new ModelAndView("/jsp/portal/openerRefresh.jsp");      // =====> 만들어라 jsp 파일
	   if(message != null){
	      model.addObject("message", replace(message));
	   }
	   return model;
	}
	
	public static ModelAndView parentRefresh(String message) {
		ModelAndView model = new ModelAndView("/jsp/portal/parentRefresh.jsp");      // =====> 만들어라 jsp 파일
	    if(message != null){
	       model.addObject("message", replace(message));
	    }
	    return model;
	}
	
	public static ModelAndView openerFunction(String functionName, JSONArray list) {
		return openerFunction(functionName, list, null);
	}
	
	public static ModelAndView openerFunction(String functionName, JSONArray list, String message) {
		ModelAndView model = new ModelAndView("/jsp/portal/openerFunction.jsp");
		if(message != null) {
			model.addObject("message", replace(message));
		}
		
		if(functionName != null) 
			model.addObject("functionName", functionName);
		
			model.addObject("list", list);
		
		return model;
	}
	
	/**
	 * message 변환 처리
	 * @param message
	 * @return String
	 * @author yhjang1
	 * @since 2014. 12. 12.
	 */
	private static String replace(String message){
		message = message.replaceAll("\"","\\\\\"");
		message = message.replaceAll("\n","\\\\\\n");
		message = message.replaceAll("\r","\\\\\\r");
		return message;
	}
}
