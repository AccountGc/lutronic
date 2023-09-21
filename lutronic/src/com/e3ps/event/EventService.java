package com.e3ps.event;

import wt.method.RemoteInterface;
import wt.org.WTUser;
import wt.org.WTUser;

@RemoteInterface
public interface EventService {

	/**
	 * 윈칠 사용자 생성시 이벤트 발생
	 */
	public abstract void create(WTUser wtUser) throws Exception;

	/**
	 * 윈칠 사용자 수정시 이벤트 발행
	 */
	public abstract void modify(WTUser wtUser) throws Exception;
}
