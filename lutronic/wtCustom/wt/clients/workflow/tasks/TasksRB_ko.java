/* bcwti
 *
 * Copyright (c) 2010 Parametric Technology Corporation (PTC). All Rights Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */
package wt.clients.workflow.tasks;

import wt.util.resource.*;

@RBUUID("wt.clients.workflow.tasks.TasksRB")
public final class TasksRB_ko extends WTListResourceBundle {
   /**
    * -------------------------------------------------
    * Test string
    **/
   @RBEntry("..한글화되었습니다..")
   public static final String TEST_STRING = "0";

   /**
    * -------------------------------------------------
    * Buttons
    **/
   @RBEntry("저장")
   public static final String SAVE_BUTTON = "4";

   @RBEntry("재설정")
   public static final String RESET_BUTTON = "5";

   @RBEntry("확인")
   public static final String OK_BUTTON = "11";

   @RBEntry("취소")
   public static final String CANCEL_BUTTON = "12";

   @RBEntry("도움말")
   public static final String HELP_BUTTON = "6";

   /**
    * -------------------------------------------------
    * Labels
    **/
   @RBEntry("역할")
   public static final String ROLE = "7";

   @RBEntry(":")
   public static final String COLON = "8";

   @RBEntry("프로젝트 변수 업데이트")
   public static final String UPD_PROJECT_VARIABLES_LABEL = "13";

   /**
    * -------------------------------------------------
    * Titles
    **/
   @RBEntry("워크플로")
   public static final String WORKFLOW = "10";

   /**
    * -------------------------------------------------
    * Messages
    **/
   @RBEntry("저장 중...")
   public static final String SAVING = "14";

   @RBEntry("프로젝트 변수가 업데이트되었습니다.")
   public static final String PROJECT_VARIABLES_UPDATED = "15";

   @RBEntry("프로젝트 값이 재설정되었습니다.")
   public static final String PROJECT_VALUES_RESET = "16";

   /**
    * -------------------------------------------------
    * Exceptions
    **/
   @RBEntry("데이터를 초기화하는 중 예외가 발생했습니다. ")
   public static final String INITIALIZATION_FAILED = "1";

   @RBEntry("도움말 시스템이 초기화되지 못했습니다: ")
   public static final String HELP_INITIALIZATION_FAILED = "2";

   @RBEntry("한글화하는 중 예외가 발생했습니다. ")
   public static final String LOCALIZING_FAILED = "3";

   /**
    * -------------------------------------------------
    * Tasks
    **/
   @RBEntry("사용자 임무 1")
   public static final String USERTASK1 = "UserTask1";

   @RBEntry("사용자 임무 2")
   public static final String USERTASK2 = "UserTask2";

   @RBEntry("사용자 임무 3")
   public static final String USERTASK3 = "UserTask3";

   @RBEntry("사용자 임무 4")
   public static final String USERTASK4 = "UserTask4";

   @RBEntry("사용자 임무 5")
   public static final String USERTASK5 = "UserTask5";

   @RBEntry("사용자 임무 6")
   public static final String USERTASK6 = "UserTask6";

   @RBEntry("사용자 임무 7")
   public static final String USERTASK7 = "UserTask7";

   @RBEntry("임시")
   public static final String WFADHOC = "WfAdHoc";

   @RBEntry("참여자 설정")
   public static final String WFAUGMENT = "WfAugment";

   @RBEntry("프로젝트 정의")
   public static final String WFDEFINEPROJECTS = "WfDefineProjects";

   @RBEntry("기본값")
   public static final String WFTASK = "WfTask";

   @RBEntry("컨텐트 업데이트")
   public static final String WFUPDATECONTENT = "WfUpdateContent";

   @RBEntry("관찰")
   public static final String OBSERVE = "observe";

   @RBEntry("수준 올리기")
   public static final String PROMOTE = "promote";

   @RBEntry("협의")
   public static final String REVIEW = "review";

   @RBEntry("제출")
   public static final String SUBMIT = "submit";

   @RBEntry("팀 내용 업데이트")
   public static final String UPD_TEAM_VARIABLES_LABEL = "20";

   @RBEntry("팀 내용값이 업데이트되었습니다.")
   public static final String TEAM_VARIABLES_UPDATED = "21";

   @RBEntry("팀 내용값이 재설정되었습니다.")
   public static final String TEAM_VALUES_RESET = "22";

   @RBEntry("팀 템플릿 변수 업데이트")
   public static final String UPD_TEAM_TEMPLATE_VARIABLES_LABEL = "23";

   @RBEntry("팀 템플릿 변수가 업데이트되었습니다.")
   public static final String TEAM_TEMPLATE_VARIABLES_UPDATED = "24";

   @RBEntry("팀 템플릿 값이 재설정되었습니다.                                               ")
   public static final String TEAM_TEMPLATE_VALUES_RESET = "25";

   @RBEntry("팀 정의")
   public static final String WFDEFINETEAMS = "WfDefineTeams";

   @RBEntry("변경 관리")
   public static final String WFCHGMGMT = "WfChgMgmt";

   @RBEntry("수준 올리기 요청 임무")
   public static final String WFPROMOTIONTASK = "WfPromotionRequestTask";

   //e3ps add
   @RBEntry("결재자추가")
   public static final String AddParticipant = "AddParticipant";

   @RBEntry("기본결재")
   public static final String DefaultProcess = "DefaultProcess";

   @RBEntry("기본결재_수신")
   public static final String DefaultProcessNoLine = "DefaultProcessNoLine";

   @RBEntry("ECA 업무")
   public static final String ECAProcess = "ECAProcess";
   
   @RBEntry("공통 업무")
   public static final String COMMONProcess = "COMMONProcess";

}
