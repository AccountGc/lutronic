<%@page import="com.e3ps.org.dto.PeopleDTO"%>
<%@page import="com.e3ps.org.People"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
Map<String, Integer> count = (Map<String, Integer>) request.getAttribute("count");
boolean isWork = (boolean) request.getAttribute("isWork");
boolean isDoc = (boolean) request.getAttribute("isDoc");
boolean isPart = (boolean) request.getAttribute("isPart");
boolean isEpm = (boolean) request.getAttribute("isEpm");
boolean isChange = (boolean) request.getAttribute("isChange");
boolean isMold = (boolean) request.getAttribute("isMold");
boolean isRohs = (boolean) request.getAttribute("isRohs");
boolean isEtc = (boolean) request.getAttribute("isEtc");

// 기타 문서 권한처리
boolean isRa = (boolean) request.getAttribute("isRa");
boolean isProduction = (boolean) request.getAttribute("isProduction");
boolean isCosmetic = (boolean) request.getAttribute("isCosmetic");
boolean isPathological = (boolean) request.getAttribute("isPathological");
boolean isClinical = (boolean) request.getAttribute("isClinical");

PeopleDTO dto = (PeopleDTO) request.getAttribute("dto");
%>
<nav class="navbar-default navbar-static-side" role="navigation">
	<div class="sidebar-collapse">
		<ul class="nav metismenu" id="side-menu">
			<li class="nav-header">
				<div class="dropdown profile-element">
					<a href="javascript:index();">
						<span class="block m-t-xs font-bold"><%=dto.getName()%></span>
						<span class="text-muted text-xs block">
							<font color="white"><%=dto.getDepartment_name()%>-<%=dto.getDuty()%></font>
						</span>
					</a>
				</div>
			</li>
			<%
			if (isWork) {
			%>
			<li>
				<a href="#">
					<i class="fa fa-magic"></i>
					<span class="nav-label">나의업무</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/notice/list', '> 나의업무 > 공지사항');">공지사항</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/approval', '> 나의업무 > 결재함');">
							결재함
							<span class="label label-info float-right">
								<%
								//=count.get("check")
								%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/activity/eca', '> 나의업무 > ECA 활동함');">
							ECA 활동함
							<span class="label label-info float-right">
								<%
								//=count.get("check")
								%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/progress', '> 나의업무 > 진행함');">
							진행함
							<span class="label label-info float-right">
								<%
								//=count.get("ing")
								%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/complete', '> 나의업무 > 완료함');">
							완료함
							<span class="label label-info float-right">
								<%
								//=count.get("complete")
								%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/receive', '> 나의업무 > 수신함');">
							수신함
							<span class="label label-info float-right">
								<%
								//=count.get("receive")
								%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/reject', '> 나의업무 > 반려함');">
							반려함
							<span class="label label-info float-right">
								<%
								//=count.get("receive")
								%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/asmApproval/listAsm', '> 나의업무 > 일괄결재 검색');">일괄결재 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/temprary/list', '> 나의업무 > 임시저장함');">임시저장함</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/org/organization', '> 나의업무 > 조직도');">조직도</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/groupware/password', '> 나의업무 > 비밀번호변경');">비밀번호 변경</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/groupware/manage', '> 나의업무 > 관리자메뉴');">관리자 메뉴</a>
					</li>
				</ul>
			</li>
			<%
			}
			if (isDoc) {
			%>
			<li>
				<a href="#">
					<i class="fa fa-pie-chart"></i>
					<span class="nav-label">문서관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/doc/list', '> 문서관리 > 문서 검색');">문서 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/doc/create', '> 문서관리 > 문서 등록');">문서 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/doc/batch', '> 문서관리 > 문서 일괄등록');">문서 일괄등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/doc/register', '> 문서관리 > 문서 일괄결재');">문서 일괄결재</a>
					</li>
				</ul>
			</li>
			<%
			}
			if (isPart) {
			%>
			<li>
				<a href="#">
					<i class="fa fa-desktop"></i>
					<span class="nav-label">품목관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/part/list', '> 품목관리 > 품목 검색');">품목 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/part/create', '> 품목관리 > 품목 등록');">품목 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/part/batch', '> 품목관리 > 품목 일괄등록');">품목 일괄등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/part/editor', '> 품목관리 > BOM EDITOR');">BOM EDITOR</a>
					</li>
				</ul>
			</li>
			<%
			}
			if (isEpm) {
			%>
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
			<%
			}
			if (isChange) {
			%>
			<li>
				<a href="#">
					<i class="fa fa-files-o"></i>
					<span class="nav-label">설계변경</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/eo/list', '> 설계변경 >EO 검색');">EO</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/cr/list', '> 설계변경 > CR 검색');">CR</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/ecpr/list', '> 설계변경 > ECPR 검색');">ECPR</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/eco/list', '> 설계변경 > ECO 검색');">ECO</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/ecn/list', '> 설계변경 > ECN 검색');">ECN</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/erp/send-listPARTERP', '> 관리자 >PART 전송 현황');">PART 전송 현황</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/erp/send-listECOERP', '> 관리자 >EO&ECO 전송 현황');">EO&ECO 전송 현황</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/erp/send-listBOMERP', '> 관리자 >BOM 전송 현황');">BOM 전송 현황</a>
					</li>
				</ul>
			</li>
			<%
			}
			if (isRohs) {
			%>
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
						<a onclick="moveToPage(this, '/rohs/listRohsFile', '> RoHS > 파일 검색');">파일 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/rohs/listAUIRoHSPart', '> RoHS > 부품 현황');">부품 현황</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/rohs/listRoHSProduct', '> RoHS > 제품 현황');">제품 현황</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/rohs/all', '> RoHS > 물질 일괄결재');">물질 일괄결재</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/rohs/batch', '> RoHS > 물질 일괄등록');">물질 일괄등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/rohs/link', '> RoHS > 물질 일괄링크');">물질 일괄링크</a>
					</li>
				</ul>
			</li>
			<%
			}
			if (isMold) {
			%>
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
			<%
			}
			if (isEtc) {
			%>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">생산본부 문서관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/etc/create?type=production', '> 생산본부 문서관리 > 생산본부 문서등록');">생산본부 문서등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/list?type=production', '> 생산본부 문서관리 > 생산본부 문서검색');">생산본부 문서검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/list?docType=production', '> 생산본부 문서관리 > 생산본부 문서결재');">생산본부 문서결재</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">병리연구 문서관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/etc/create?type=pathological', '> 병리연구 문서관리 > 병리연구 문서등록');">병리연구 문서등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/list?type=pathological', '> 병리연구 문서관리 > 병리연구 문서검색');">병리연구 문서검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/list?docType=production', '> 병리연구 문서관리 > 병리연구 문서결재');">병리연구 문서결재</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">임상개발 문서관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/etc/create?type=clinical', '> 임상개발 문서관리 > 임상개발 문서등록');">임상개발 문서등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/list?type=clinical', > 임상개발  문서관리 > 임상개발 문서검색');">임상개발 문서검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/list?docType=production', '> 임상개발 문서관리 > 임상개발 문서결재');">임상개발 문서결재</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">RA팀 문서관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/etc/create?type=ra', '> RA팀 문서관리 > RA팀 문서등록');">RA팀 문서등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/list?type=ra', '> RA팀 문서관리 > RA팀 문서검색');">RA팀 문서검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/list?docType=production', '> RA팀 문서관리 > RA팀 문서결재');">RA팀 문서결재</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">화장품 문서관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/etc/create?type=cosmetic', '> 화장품 문서관리 > RA팀 문서등록');">화장품 문서등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/list?type=cosmetic', '> 화장품 문서관리 >  문서검색');">화장품 문서검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/list?docType=production', '> 화장품 문서관리 > 화장품 문서결재');">화장품 문서결재</a>
					</li>
				</ul>
			</li>
			<%
			}
			if (CommonUtil.isAdmin()) {
			%>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">관리자</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/code/list', '> 관리자 > 코드체계관리');">코드체계관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/activity/list', '> 관리자 > 설계변경관리');">설계변경관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/admin/mail', '> 관리자 > 외부메일관리');">외부메일관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/admin/loginHistory', '> 관리자 > 접속이력관리');">접속이력관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/admin/downLoadHistory', '> 관리자 > 다운로드 이력관리');">다운로드 이력관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/admin/adminPackage', '> 관리자 > 등록양식관리');">등록양식관리</a>
					</li>
					<li>
						<a onclick="popup('/Windchill', 1600, 800);">Windchill</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/form/list', '> 관리자 > 문서 템플릿관리');">문서 템플릿관리</a>
					</li>
				</ul>
			</li>
			<%
			}
			%>
			<%
			if (CommonUtil.isAdmin()) {
			%>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">배포관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a href="#">
							<i class="fa fa-magic"></i>
							<span class="nav-label">나의 업무</span>
							<span class="fa arrow"></span>
						</a>
						<ul class="nav nav-second-level collapse">
							<li>
								<a onclick="moveToPage(this, '/groupware/listItem?state=check', '> 나의 업무 > 검토함');">
									검토함
									<span class="label label-info float-right">
										<%
										//=count.get("check")
										%>
									</span>
								</a>
							</li>
							<li>
								<a onclick="moveToPage(this, '/groupware/listItem?state=approval', '> 나의 업무 > 결재함');">
									결재함
									<span class="label label-info float-right">
										<%
										//=count.get("approval")
										%>
									</span>
								</a>
							</li>
							<li>
								<a onclick="moveToPage(this, '/groupware/listItem?state=receive', '> 나의 업무 > 수신함');">
									수신함
									<span class="label label-info float-right">
										<%
										//=count.get("receive")
										%>
									</span>
								</a>
							</li>
							<li>
								<a onclick="moveToPage(this, '/groupware/listCompanyTree', '> 나의 업무 > 조직도');">조직도</a>
							</li>
							<li>
								<a onclick="moveToPage(this, '/groupware/changePassword', '> 나의 업무 > 비밀번호변경');">비밀번호 변경</a>
							</li>
						</ul>
					</li>
					<li>
						<a href="#">
							<i class="fa fa-desktop"></i>
							<span class="nav-label">품목검색</span>
							<span class="fa arrow"></span>
						</a>
						<ul class="nav nav-second-level collapse">
							<li>
								<a onclick="moveToPage(this, '/part/list', '> 품목관리 > 품목 검색');">품목 검색</a>
							</li>
							<li>
								<a onclick="moveToPage(this, '/distribute/listProduction', '> 품목관리 > 완제품 검색');">완제품 검색</a>
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
								<a onclick="moveToPage(this, '/changeECO/list', '> 설계변경 > ECO 검색');">ECO 검색</a>
							</li>
							<li>
								<a onclick="moveToPage(this, '/changeECO/listEO', '> 설계변경 >EO 검색');">EO 검색</a>
							</li>
						</ul>
					</li>
					<li>
						<a href="#" onclick="moveToPage(this, '/doc/list', '> 문서 관리 > 문서 검색');">
							<i class="fa fa-pie-chart"></i>
							<span class="nav-label">문서 검색</span>
						</a>
					</li>
					<li>
						<a href="#" onclick="moveToPage(this, '/mold/list', '> 금형관리 > 금형 검색');">
							<i class="fa fa-files-o"></i>
							<span class="nav-label">금형 검색</span>
						</a>
					</li>
				</ul>
			</li>
			<%
			}
			%>
		</ul>
	</div>
</nav>
