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
public final class TasksRB extends WTListResourceBundle {
   /**
    * -------------------------------------------------
    * Test string
    **/
   @RBEntry("..Localized..")
   public static final String TEST_STRING = "0";

   /**
    * -------------------------------------------------
    * Buttons
    **/
   @RBEntry("Save")
   public static final String SAVE_BUTTON = "4";

   @RBEntry("Reset")
   public static final String RESET_BUTTON = "5";

   @RBEntry("OK")
   public static final String OK_BUTTON = "11";

   @RBEntry("Cancel")
   public static final String CANCEL_BUTTON = "12";

   @RBEntry("Help")
   public static final String HELP_BUTTON = "6";

   /**
    * -------------------------------------------------
    * Labels
    **/
   @RBEntry("Role")
   public static final String ROLE = "7";

   @RBEntry(":")
   public static final String COLON = "8";

   @RBEntry("Update Project Variables")
   public static final String UPD_PROJECT_VARIABLES_LABEL = "13";

   /**
    * -------------------------------------------------
    * Titles
    **/
   @RBEntry("Workflow")
   public static final String WORKFLOW = "10";

   /**
    * -------------------------------------------------
    * Messages
    **/
   @RBEntry("Saving...")
   public static final String SAVING = "14";

   @RBEntry("Project variables updated.")
   public static final String PROJECT_VARIABLES_UPDATED = "15";

   @RBEntry("Project values were reset.")
   public static final String PROJECT_VALUES_RESET = "16";

   /**
    * -------------------------------------------------
    * Exceptions
    **/
   @RBEntry("An exception occurred initializing the data: ")
   public static final String INITIALIZATION_FAILED = "1";

   @RBEntry("The Help system could not be initialized: ")
   public static final String HELP_INITIALIZATION_FAILED = "2";

   @RBEntry("An exception occurred during localization: ")
   public static final String LOCALIZING_FAILED = "3";

   /**
    * -------------------------------------------------
    * Tasks
    **/
   @RBEntry("User Task 1")
   public static final String USERTASK1 = "UserTask1";

   @RBEntry("User Task 2")
   public static final String USERTASK2 = "UserTask2";

   @RBEntry("User Task 3")
   public static final String USERTASK3 = "UserTask3";

   @RBEntry("User Task 4")
   public static final String USERTASK4 = "UserTask4";

   @RBEntry("User Task 5")
   public static final String USERTASK5 = "UserTask5";

   @RBEntry("User Task 6")
   public static final String USERTASK6 = "UserTask6";

   @RBEntry("User Task 7")
   public static final String USERTASK7 = "UserTask7";

   @RBEntry("Ad Hoc")
   public static final String WFADHOC = "WfAdHoc";

   @RBEntry("Set Up Participants")
   public static final String WFAUGMENT = "WfAugment";

   @RBEntry("Define Projects")
   public static final String WFDEFINEPROJECTS = "WfDefineProjects";

   @RBEntry("Default")
   public static final String WFTASK = "WfTask";

   @RBEntry("Update Content")
   public static final String WFUPDATECONTENT = "WfUpdateContent";

   @RBEntry("Observe")
   public static final String OBSERVE = "observe";

   @RBEntry("Promote")
   public static final String PROMOTE = "promote";

   @RBEntry("Review")
   public static final String REVIEW = "review";

   @RBEntry("Submit")
   public static final String SUBMIT = "submit";

   @RBEntry("Update Team Variables")
   public static final String UPD_TEAM_VARIABLES_LABEL = "20";

   @RBEntry("Team variables updated.")
   public static final String TEAM_VARIABLES_UPDATED = "21";

   @RBEntry("Team values were reset.")
   public static final String TEAM_VALUES_RESET = "22";

   @RBEntry("Update Team Template Variables")
   public static final String UPD_TEAM_TEMPLATE_VARIABLES_LABEL = "23";

   @RBEntry("Team Template variables updated.")
   public static final String TEAM_TEMPLATE_VARIABLES_UPDATED = "24";

   @RBEntry("Team Template values were reset.                                               ")
   public static final String TEAM_TEMPLATE_VALUES_RESET = "25";

   @RBEntry("Define Teams")
   public static final String WFDEFINETEAMS = "WfDefineTeams";

   @RBEntry("Change Management")
   public static final String WFCHGMGMT = "WfChgMgmt";

   @RBEntry("Promotion Request Task")
   public static final String WFPROMOTIONTASK = "WfPromotionRequestTask";

   //e3ps add
   @RBEntry("AddParticipant")
   public static final String AddParticipant = "AddParticipant";

   @RBEntry("DefaultProcess")
   public static final String DefaultProcess = "DefaultProcess";

   @RBEntry("DefaultProcessNoLine")
   public static final String DefaultProcessNoLine = "DefaultProcessNoLine";

   @RBEntry("ECA Work")
   public static final String ECAProcess = "ECAProcess";
   
   @RBEntry("COMMON Work")
   public static final String COMMONProcess = "COMMONProcess";
}
