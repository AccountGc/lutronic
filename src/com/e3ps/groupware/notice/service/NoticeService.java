package com.e3ps.groupware.notice.service;

import java.util.List;
import java.util.Map;

import com.e3ps.groupware.notice.dto.NoticeDTO;

import wt.method.RemoteInterface;

@RemoteInterface
public interface NoticeService {

	/**
	 * 공지사항 등록
	 */
	public abstract void create(Map<String, Object> params) throws Exception;

	/**
	 * 공지사항 삭제
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * 공지사항 수정
	 */
	public abstract void modify(Map<String, Object> params) throws Exception;

	/**
	 * 공지사항 조회 횟수 카운트
ㄴ	 */
	public abstract void read(String oid) throws Exception;

	List<NoticeDTO> getPopUpNotice();

}
