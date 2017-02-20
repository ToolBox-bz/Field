package retailworks.in.field.utils;


public interface Constants {

	public static final String APP_TAG = "FIELD ";

	public static final boolean PRODUCTION_MODE = false;
	public static final boolean LOG_INFO = true; // should stay true even in
													// production mode
	public static final boolean LOG_DEBUG = !PRODUCTION_MODE;
	public static final boolean LOG_VERBOSE = true;

	public static final String DBNAME = "field_";
	public static final String COMPANY = "company";
	public static final String COMPANY_NAME = "demo";
	public static final String EMPCODE = "empCode";
	public static final String CHECKBOX = "checkbox";
	public static final String FAIL = "fail";
	public static final String NOT_FOUND = "notfound";
	public static final String OPEN = "open";
	public static final String PACKAGE = "retailworks.in.field";
	public static final String PASSWORD = "password";
	public static final String QUERY = "query";
	public static final String REFRESH = "refresh";
	public static final String REJECTED = "rejected";
	public static final int REQ_CODE = 57005;
	public static final String RESULT = "result";
	public static final String SUCCESS = "success";
	public static final String TABLE = "table";
	public static final String LOCAL = "local";
	public static final String TITLE = "title";
	public static final String TYPE = "type";
	public static final String TRIES = "tries";
	public static final String UNVISITED = "unvisited";
	public static final String USERNAME = "username";

	public static final String BASE_URL  = "http://retailworks.in/panel/";
	public static final String SYNC_URL  = BASE_URL + "device_sync.php";
	public static final String LOGIN_URL = BASE_URL + "device_login.php";

}
