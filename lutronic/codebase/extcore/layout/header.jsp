<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
%>
<nav class="navbar-default navbar-static-side" role="navigation">
	<div class="sidebar-collapse">
		<ul class="nav metismenu" id="side-menu">
			<li class="nav-header">
				<div class="dropdown profile-element">
					<a href="javascript:index();">
						<span class="block m-t-xs font-bold"><%//=data.getName()%></span>
						<span class="text-muted text-xs block">
							<font color="white"><%//=data.getDepartment_name()%>-<%//=data.getDuty() != null ? data.getDuty() : "지정안됨"%></font>
						</span>
					</a>
				</div>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-magic"></i>
					<span class="nav-label">나의 업무</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/notice/list', '> 나의 업무 > 공지사항');">공지사항</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/agree', '> 나의 업무 > 작업함');">
							작업함
							<%-- <span class="label label-info float-right"><%//=count.get("agree")%></span> --%>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/progress', '> 나의 업무 > 진행함');">
							진행함
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/complete', '> 나의 업무 > 완료함');">
							완료함
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/receive', '> 나의 업무 > 수신함');">
							수신함
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/receive', '> 나의 업무 > 일괄결재 검색');">
							일괄결재 검색
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/org/organization', '> 나의 업무 > 조직도');">조직도</a>
					</li>
					<li>
						<a onclick="javascript:_popup('/org/password');">비밀번호 변경</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/receive', '> 나의업무 > 관리자메뉴');">
							관리자 메뉴
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/receive', '> 나의 업무 > 속성값 변경');">
							속성값 변경
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/receive', '> 나의 업무 > 도면 재변환');">
							도면 재변환
						</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-envelope"></i>
					<span class="nav-label">개발업무관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/development/list', '> 개발업무관리 > 개발업무 검색');">개발업무 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/development/create', '> 개발업무관리 > 개발업무 등록');">개발업무 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/development/my', '> 개발업무관리 > 나의 개발업무');">나의 개발업무</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-pie-chart"></i>
					<span class="nav-label">문서 관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/doc/list', '> 문서 관리 > 문서 검색');">문서 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/doc/create', '> 문서 관리 > 문서 등록');">문서 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/doc/batch', '> 문서 관리 > 문서 일괄등록');">문서 일괄등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/doc/all', '> 문서 관리 > 문서 일괄결재');">문서 일괄결재</a>
					</li>
					<!-- <li>
						<a onclick="moveToPage(this, '/requestDocument/list', '> 문서 관리 > 의뢰서 조회');">의뢰서 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/meeting/list', '> 문서 관리 > 회의록 조회');">회의록 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/doc/register', '> 문서 관리 > 문서 결재');">문서 결재</a>
					</li> -->
				</ul>
			</li>
			<!-- <li>
				<a href="metrics.html">
					<i class="fa fa-pie-chart"></i>
					<span class="nav-label">작번 관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/project/list', '> 작번 관리 > 작번 조회');">작번 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/project/my', '> 작번 관리 > 나의 작번');">나의 작번</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/template/list', '> 작번 관리 > 템플릿 조회');">템플릿 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/issue/list', '> 작번 관리 > 특이사항 조회');">특이사항 조회</a>
					</li>
				</ul>
			</li> -->
			<li>
				<a href="#">
					<i class="fa fa-desktop"></i>
					<span class="nav-label">품목관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/part/list', '> 품목관리 > 품목 검색');">제품/품목 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/part/create', '> 품목관리 > 제품/품목 등록');">제품/품목 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/part/batch', '> 품목관리 > 제품/품목 일괄등록');">제품/품목 일괄등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/part/bom', '> 품목관리 > BOM EDITOR');">BOM EDITOR</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-edit"></i>
					<span class="nav-label">도면 관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/drawing/list', '> 도면 관리 > 도면 검색');">도면 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/create', '> 도면 관리 > 도면 등록');">도면 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/batch', '> 도면 관리 >주 도면 일괄등록');">주 도면 일괄등록</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-files-o"></i>
					<span class="nav-label">설계변경</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/drawing/list', '> 설계변경 > ECO 검색');">ECO 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/list', '> 설계변경 > ECO 등록');">ECO 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/create', '> 설계변경 > CR/ECPR 검색');">CR/ECPR 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/create', '> 설계변경 > CR/ECPR 등록');">CR/ECPR 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/batch', '> 설계변경 >EO 검색');">EO 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/batch', '> 설계변경 >EO 등록');">EO 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/batch', '> 설계변경 >PART 전송 현황');">PART 전송 현황</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/batch', '> 설계변경 >EO&ECO 전송 현황');">EO&ECO 전송 현황</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/batch', '> 설계변경 >BOM 전송 현황');">BOM 전송 현황</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-sitemap"></i>
					<span class="nav-label">RoHS</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/rohs/list', '> RoHS > 물질 검색');">물질 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/rohs/create', '> RoHS > 물질 등록');">물질 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/list', '> RoHS > 파일 검색');">파일 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/list', '> RoHS > 부품 현황');">부품 현황</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/list', '> RoHS > 제품 현황');">제품 현황</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/list', '> RoHS > 물질 일괄결재');">물질 일괄결재</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/list', '> RoHS > 물질 일괄결재');">물질 일괄등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/drawing/list', '> RoHS > 물질 일괄결재');">물질 일괄링크</a>
					</li>
				</ul>
			</li>
			
			<!-- <li>
				<a href="#">
					<i class="fa fa-files-o"></i>
					<span class="nav-label">BOM 관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/partlist/list', '> BOM 관리 > 수배표 조회');">수배표 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/tbom/list', '> BOM 관리 > T-BOM 조회');">T-BOM 조회</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-sitemap"></i>
					<span class="nav-label">한국 생산</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/korea/list', '> 한국 생산 > 한국 생산');">한국 생산</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/configSheet/list', '> 한국 생산 > CONFIG SHEET 조회');">CONFIG SHEET 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/cip/list', '> 한국 생산 > CIP 조회');">CIP 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/history/list', '> 한국 생산 > 이력 관리 조회');">이력 관리 조회</a>
					</li>
				</ul>
			</li> -->
			<li>
				<a href="#">
					<i class="fa fa-files-o"></i>
					<span class="nav-label">금형관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/mold/list', '> 금형관리 > 금형 검색');">금형 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/mold/create', '> 금형관리 > 금형 등록');">금형 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/mold/all', '> 금형관리 > 금형 일괄결재');">금형 일괄결재</a>
					</li>
				</ul>
			</li>
			<!-- <li>
				<a href="#">
					<i class="fa fa-sitemap"></i>
					<span class="nav-label">시스템 로그</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/erp/list', '> ERP 로그 > ERP 로그');">ERP 로그</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/system/list', '> 에러 로그 > 에러 로그');">에러 로그</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">관리자</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/commonCode/list', '> 관리자 > 코드 관리');">코드 관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/specCode/list', '> 관리자 > 이력 관리 컬럼');">이력 관리 컬럼</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/configSheetCode/list', '> 관리자 > CONFIG SHEET 카테고리');">CONFIG SHEET 카테고리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/numberRuleCode/list', '> 관리자 > KEK 도번 관리');">KEK 도번 관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/meeting/template', '> 관리자 > 회의록 템플릿');">회의록 템플릿</a>
					</li>
				</ul>
			</li> -->
		</ul>
	</div>
</nav>
