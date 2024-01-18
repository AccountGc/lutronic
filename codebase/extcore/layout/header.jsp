<%@page import="com.e3ps.org.dto.PeopleDTO"%>
<%@page import="com.e3ps.org.People"%>
<%@page import="com.e3ps.common.util.CommonUtil"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// 배포 확인
boolean isDist = (boolean) request.getAttribute("isDist");

Map<String, Integer> count = (Map<String, Integer>) request.getAttribute("count");
boolean isWork = (boolean) request.getAttribute("isWork");
boolean isDoc = (boolean) request.getAttribute("isDoc");
boolean isPart = (boolean) request.getAttribute("isPart");
boolean isEpm = (boolean) request.getAttribute("isEpm");
boolean isChange = (boolean) request.getAttribute("isChange");
boolean isMold = (boolean) request.getAttribute("isMold");
boolean isRohs = (boolean) request.getAttribute("isRohs");

// 기타 문서 권한처리
boolean isRa = (boolean) request.getAttribute("isRa");
boolean isProduction = (boolean) request.getAttribute("isProduction");
boolean isCosmetic = (boolean) request.getAttribute("isCosmetic");
boolean isPathological = (boolean) request.getAttribute("isPathological");
boolean isClinical = (boolean) request.getAttribute("isClinical");

// 관리자
boolean isAdmin = (boolean) request.getAttribute("isAdmin");

PeopleDTO dto = (PeopleDTO) request.getAttribute("dto");

// 결재 개수
int workData = (int) request.getAttribute("workData");
int eca = (int) request.getAttribute("eca");
%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<nav class="54navbar-default navbar-static-side" role="navigation" id="navigation">
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

			<!-- 배포 아닐경우 보이게 처리 START-->
			<%
			if (isAdmin || !isDist) {
			%>
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
						<a onclick="moveToPage(this, '/workData/list', '> 나의업무 > 결재선 지정');">
							결재선 지정
							<span class="label label-info float-right" id="workData">
								<%=workData%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/activity/eca', '> 나의업무 > ECA 활동함');">
							ECA 활동함
							<span class="label label-info float-right" id="activity">
								<%=eca%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/approval', '> 나의업무 > 결재함');">
							결재함
							<span class="label label-info float-right" id="approval">
								<%=count.get("approval")%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/agree', '> 나의업무 > 합의함');">
							합의함
							<span class="label label-info float-right" id="agree">
								<%=count.get("agree")%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/progress', '> 나의업무 > 진행함');">
							진행함
							<span class="label label-info float-right" id="progress">
								<%=count.get("progress")%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/complete', '> 나의업무 > 완료함');">
							완료함
							<span class="label label-info float-right" id="complete">
								<%=count.get("complete")%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/receive', '> 나의업무 > 수신함');">
							수신함
							<span class="label label-info float-right" id="receive">
								<%=count.get("receive")%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/reject', '> 나의업무 > 반려함');">
							반려함
							<span class="label label-info float-right" id="reject">
								<%=count.get("reject")%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/temprary/list', '> 나의업무 > 임시저장함');">임시저장함</a>
					</li>
					<li>
						<a onclick="_popup('/Windchill/plm/groupware/password', 800, 300, 'n');">비밀번호 변경</a>
					</li>
					<%
					if (isAdmin) {
					%>
					<li>
						<a onclick="moveToPage(this, '/org/organization', '> 나의업무 > 조직도');">조직도</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/groupware/manage', '> 나의업무 > 관리자메뉴');">관리자 메뉴</a>
					</li>
					<%
					}
					%>
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
					<!-- 					<li> -->
					<!-- 						<a onclick="moveToPage(this, '/doc/batch', '> 문서관리 > 문서 일괄 등록');">문서 일괄 등록</a> -->
					<!-- 					</li> -->
					<li>
						<a onclick="moveToPage(this, '/asm/create?type=DOC', '> 문서관리 > 문서 일괄 결재');">문서 일괄 결재</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/asm/list?number=NDBT', '> 문서관리 > 일괄 결재 검색');">일괄 결재 검색</a>
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
						<a onclick="moveToPage(this, '/part/list', '> 품목관리 > 제품/품목 검색');">제품/품목 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/part/create', '> 품목관리 > 제품/품목 등록');">제품/품목 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/part/saveAs', '> 품목관리 > 다른 품번으로 저장');">다른 품번으로 저장</a>
					</li>					
					<li>
						<a onclick="moveToPage(this, '/part/batch', '> 품목관리 > 제품/품목 일괄 등록');">제품/품목 일괄 등록</a>
					</li>
					<!-- 					<li> -->
					<!-- 						<a onclick="moveToPage(this, '/part/editor', '> 품목관리 > BOM EDITOR');">BOM EDITOR</a> -->
					<!-- 					</li> -->
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
						<a onclick="moveToPage(this, '/ecrm/list', '> 설계변경 > ECRM 검색');">ECRM</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/eco/list', '> 설계변경 > ECO 검색');">ECO</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/ecn/list', '> 설계변경 > ECN 검색');">ECN</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/system/part', '> 관리자 >품목 전송 현황');">품목 전송 현황</a>
					</li>
					<!-- 					<li> -->
					<!-- 						<a onclick="moveToPage(this, '/erp/send-listECOERP', '> 관리자 >EO&ECO 전송 현황');">EO&ECO 전송 현황</a> -->
					<!-- 					</li> -->
					<li>
						<a onclick="moveToPage(this, '/system/bom', '> 관리자 >BOM 전송 현황');">BOM 전송 현황</a>
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
						<a onclick="moveToPage(this, '/rohs/batch', '> RoHS > 물질 일괄 등록');">물질 일괄 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/rohs/link', '> RoHS > 물질 일괄 링크');">물질 일괄 링크</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/asm/create?type=ROHS', '> RoHS > 물질 일괄 결재');">물질 일괄 결재</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/asm/list?number=ROHSBT', '> RoHS > 일괄 결재 검색');">일괄 결재 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/rohs/file', '> RoHS > 파일 검색');">파일 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/rohs/part', '> RoHS > 부품 현황');">부품 현황</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/rohs/product', '> RoHS > 제품 현황');">제품 현황</a>
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
						<a onclick="moveToPage(this, '/asm/create?type=MOLD', '> 금형 일괄 결재');">금형 일괄 결재</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/asm/list?number=MMBT', '> 금형관리 > 일괄 결재 검색');">일괄 결재 검색</a>
					</li>
				</ul>
			</li>
			<%
			}
			if (isPathological) {
			%>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">병리연구 문서관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/etc/list?type=pathological', '> 병리연구 문서관리 > 병리연구 문서검색');">병리연구 문서검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/create?type=pathological', '> 병리연구 문서관리 > 병리연구 문서등록');">병리연구 문서등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/asm/create?type=PATHOLOGICAL', '> 병리연구 문서관리 > 병리연구 문서 일괄 결재');">병리연구 문서 일괄 결재</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/asm/list?number=AMBT', '> 병리연구 문서관리 > 일괄 결재 검색');">일괄 결재 검색</a>
					</li>
				</ul>
			</li>
			<%
			}
			if (isClinical) {
			%>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">임상개발 문서관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/etc/list?type=clinical', '> 임상개발  문서관리 > 임상개발 문서검색');">임상개발 문서검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/create?type=clinical', '> 임상개발 문서관리 > 임상개발 문서등록');">임상개발 문서등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/asm/create?type=CLINICAL', '> 임상개발 문서관리 > 임상개발 문서 일괄 결재');">임상개발 문서 일괄 결재</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/asm/list?number=BMBT', '> 임상개발 문서관리 > 일괄 결재 검색');">일괄 결재 검색</a>
					</li>
				</ul>
			</li>
			<%
			}
			%>
			<%
			if (isCosmetic) {
			%>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">화장품 문서관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/etc/list?type=cosmetic', '> 화장품 문서관리 >  문서검색');">화장품 문서검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/etc/create?type=cosmetic', '> 화장품 문서관리 > 화장품 문서등록');">화장품 문서등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/asm/create?type=COSMETIC', '> 화장품 문서관리 > 화장품 문서 일괄 결재');">화장품 문서 일괄 결재</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/asm/list?number=CMBT', '> 화장품 문서관리 > 일괄 결재 검색');">일괄 결재 검색</a>
					</li>
				</ul>
			</li>
			<%
			}
			if (isAdmin) {
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
						<a onclick="moveToPage(this, '/admin/mail', '> 관리자 > 외부메일관리');">외부메일관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/loginHistory/list', '> 관리자 > 접속이력관리');">접속이력관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/admin/downLoadHistory', '> 관리자 > 다운로드 이력관리');">다운로드 이력관리</a>
					</li>
					<li>
						<a onclick="window.open('/Windchill','','scrollbars=yes, resizable=yes,height=800, width=1600');">Windchill</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/form/list', '> 관리자 > 문서 템플릿관리');">문서 템플릿관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/class/list', '> 관리자 > 문서 채번관리');">문서 채번관리</a>
					</li>
				</ul>
			</li>
			<%
			}
			%>
			<%
			}
			%>
			<%
			if (isDist) {
			%>
			<li>
				<a href="#">
					<i class="fa fa-magic"></i>
					<span class="nav-label">나의 업무</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/workspace/agree', '> 나의업무 > 합의함');">
							합의함
							<span class="label label-info float-right">
								<%=count.get("agree")%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/approval', '> 나의업무 > 결재함');">
							결재함
							<span class="label label-info float-right">
								<%=count.get("approval")%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/receive', '> 나의업무 > 수신함');">
							수신함
							<span class="label label-info float-right">
								<%=count.get("receive")%>
							</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/org/organization', '> 나의업무 > 조직도');">조직도</a>
					</li>
					<li>
<!-- 						<a onclick="moveToPage(this, '/groupware/password', '> 나의업무 > 비밀번호변경');">비밀번호 변경</a> -->
						<a onclick="_popup('/Windchill/plm/groupware/password', 800, 300, 'n');">비밀번호 변경</a>
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
						<a onclick="moveToPage(this, '/distribute/listPart', '> 품목관리 > 품목 검색');">품목 검색</a>
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
						<a onclick="moveToPage(this, '/distribute/listEO', '> 설계변경 > EO 검색');">EO 검색</a>
						<!-- 								<a onclick="moveToPage(this, '/changeECO/listEO', '> 설계변경 >EO 검색');">EO 검색</a> -->
					</li>
					<li>
						<a onclick="moveToPage(this, '/distribute/listCR', '> 설계변경 > CR 검색');">CR 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/distribute/listECPR', '> 설계변경 > ECPR 검색');">ECPR 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/distribute/listECRM', '> 설계변경 > ECRM 검색');">ECRM 검색</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/distribute/listECO', '> 설계변경 > ECO 검색');">ECO 검색</a>
						<!-- 								<a onclick="moveToPage(this, '/changeECO/list', '> 설계변경 > ECO 검색');">ECO 검색</a> -->
					</li>
				</ul>
			</li>
			<li>
				<a onclick="moveToPage(this, '/distribute/listDocument', '> 문서관리 > 문서 검색');">
					<i class="fa fa-pie-chart"></i>
					문서 검색
				</a>
				<!-- 						<a href="#" onclick="moveToPage(this, '/doc/list', '> 문서 관리 > 문서 검색');"> -->
				<!-- 							<i class="fa fa-pie-chart"></i> -->
				<!-- 							<span class="nav-label">문서 검색</span> -->
				<!-- 						</a> -->
			</li>
			<li>
				<a onclick="moveToPage(this, '/distribute/listMold', '> 금형관리 > 금형 검색');">
					<i class="fa fa-files-o"></i>
					금형 검색
				</a>
				<!-- 						<a href="#" onclick="moveToPage(this, '/mold/list', '> 금형관리 > 금형 검색');"> -->
				<!-- 							<i class="fa fa-files-o"></i> -->
				<!-- 							<span class="nav-label">금형 검색</span> -->
				<!-- 						</a> -->
			</li>
			<%
			}
			%>
		</ul>
	</div>
</nav>
<script>
	window.onload = function() {
		checkPopUP();
	}

	function checkPopUP() {

		const url = getCallUrl("/notice/popup");
		call(url, null, function(data) {
			if (data) {
				let position = 0;
				for (var i = 0; i < data.length; i++) {
					position += 5;
					const oid = data[i].oid;
					if (mainIsPopup(oid)) {
						mainPopUP(oid, position);
					}
				}
			} else {
				alert(data.msg);
			}
		});

	}

	function mainPopUP(oid, position) {
		const url = getCallUrl("/notice/popup?oid=" + oid);
		_popup2(url, 1000, 500, position, "n");
	}

	function mainIsPopup(oid) {
		let isPopup = false;
		cValue = getNoticeCookie(oid);
		if (!cValue) {
			isPopup = true;
		}
		return isPopup
	}

	//쿠키 가져오기
	function getNoticeCookie(cName) {
		cName = cName + '=';
		const cookieData = document.cookie;
		let start = cookieData.indexOf(cName);
		let cValue = '';
		if (start != -1) {
			start += cName.length;
			let end = cookieData.indexOf(';', start);
			if (end == -1)
				end = cookieData.length;
			cValue = cookieData.substring(start, end);
		}
		return unescape(cValue);
	}
</script>