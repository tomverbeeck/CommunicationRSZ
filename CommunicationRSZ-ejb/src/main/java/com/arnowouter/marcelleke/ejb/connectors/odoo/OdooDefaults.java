/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.ejb.connectors.odoo;

/**
 *
 * @author Arno
 */
public class OdooDefaults {
    // Names

    public static final String ERP_SYSTEM_NAME = "Odoo";
    public static final String HOME_ADDRESS = "Thuis";
    public static final String WORK_ADDRESS = "Werkplaats";
    // ERRORS
    public static final String ERROR_XML_RPC = "XML-RPC error";
    public static final String ERROR_MALFORMED_URL = "Malformed URL Exception";
    public static final String ERROR_UNEXPECTED = "Unexpected Error";

    // ENDPOINTS
    public static final String COMMON_ENDPOINT = "/xmlrpc/2/common";
    public static final String OBJECT_ENDPOINT = "/xmlrpc/2/object";

    // RPC FUNCTIONS
    public static final String EXECUTE = "execute_kw";

    // ACTIONS
    public final static String ACTION_AUTHENTICATE = "authenticate";
    public final static String ACTION_VERSION_INFO = "version";
    public final static String ACTION_SEARCH = "search";
    public final static String ACTION_READ = "read";
    public final static String ACTION_WRITE = "create";
    public final static String ACTION_SEARCH_READ = "search_read";

    // MODELS
    //  BADGER MODELS
    public final static String MODEL_BADGER_SESSION = "isoltrack.session";
    //  OTHER
    public final static String MODEL_EMPLOYEES = "hr.employee";
    public final static String MODEL_TASKS = "project.task";
    //      contains everything that can be seen as a 'person', users, clients (not companies!),...
    public final static String MODEL_RELATIONS = "res.partner";
    public final static String MODEL_SALE_ORDERS = "sale.order";
    public final static String MODEL_USERS = "res.users";
    public final static String MODEL_PROJECTS = "project.project";
    public final static String MODEL_ANALYTIC_ACCOUNTS = "account.analytic.account";
    public static final String MODEL_SALES_TEAM = "crm.case.section";
    public static final String MODEL_WORK_ORDERS = "project.task.work";

    // FIELDS
    // FIELDS
    public final static String FIELD_ID = "id";
    public final static String FIELD_DATE = "date";
    public final static String FIELD_DONE_BY = "user_id";

    public final static String FIELD_TASK_ID = "task_id";
    public final static String FIELD_IDENTIF_ID = "identification_id";
    public final static String FIELD_PARTNER_ID = "partner_id";
    public final static String FIELD_HOME_ADDRESS_ID = "address_home_id";
    public final static String FIELD_WORK_ADDRESS_ID = "address_id";
    public final static String FIELD_IS_A_MANAGER = "manager";
    public final static String FIELD_MANAGER_ID = "parent_id";
    public final static String FIELD_WORK_EMAIL = "work_email";
    public final static String FIELD_SHIPPING_ID = "partner_shipping_id";
    public final static String FIELD_COMPANY = "company_id";
    public final static String FIELD_STREET = "street";
    public final static String FIELD_CITY = "city";
    public final static String FIELD_ZIP = "zip";
    public final static String FIELD_LONGITUDE = "partner_longitude";
    public final static String FIELD_LATITUDE = "partner_latitude";
    public final static String FIELD_COUNTRY = "country_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_TEAM_LEADER = "user_id";
    public static final String FIELD_SALES_TEAM = "section_id";
    public static final String FIELD_PROJECT_ID = "project_id";
    public static final String FIELD_STATUS = "state";
    public final static String FIELD_USER_ID = "user_id";
    public final static String FIELD_TASK_MANAGER_ID = "manager_id";
    public final static String FIELD_CHECK_IN_AT_WORK = "xx_checkin_work";
    public final static String FIELD_RELATED_EMPLOYEES_IDS = "employee_ids";
    public final static String FIELD_ANALYTIC_ACCOUNT = "analytic_account_id";
    public final static String FIELD_PROJECT_MANAGER_ID = "user_id";
    public final static String FIELD_SALE_ORDER = "x_order_id";
    public final static String FIELD_ACTIVE = "active";

    // Project statuses
    public final static String PROJECT_STATUS_DONE = "done";
    public final static String PROJECT_STATUS_CANCELLED = "cancel";
    public final static String PROJECT_STATUS_PROGRESS = "progress";
    public final static String PROJECT_STATUS_MANUAL = "manual";

}
