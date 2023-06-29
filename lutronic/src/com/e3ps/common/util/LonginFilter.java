package com.e3ps.common.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import wt.session.SessionHelper;

public class LonginFilter implements Filter{
	@SuppressWarnings("unused")
	private FilterConfig filterConfig;
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(FilterConfig paramFilterConfig) throws ServletException {
		this.filterConfig = paramFilterConfig;
		
	}
	
	@SuppressWarnings("deprecation")
	public void doFilter(ServletRequest paramServletRequest,
			ServletResponse paramServletResponse, FilterChain paramFilterChain)
			throws IOException, ServletException {
		
		try{
			String requestURI = ((HttpServletRequest) paramServletRequest).getRequestURI();

			//System.out.println("LonginFilter requestURI == "  + requestURI);
			
			//System.out.println("SessionHelper.getPrincipal()" + SessionHelper.getPrincipal());
		}catch(Exception e){
			e.printStackTrace();
		}

		
		
		paramFilterChain.doFilter((ServletRequest) paramServletRequest, paramServletResponse);
			
		
	}

	
}
