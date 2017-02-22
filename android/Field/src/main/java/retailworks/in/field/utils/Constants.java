package retailworks.in.field.utils;


public interface Constants {

	String APP_TAG 										= "FIELD ";
	String PACKAGE 										= "retailworks.in.field";
	String COMPANY_NAME 								= "demo";

	boolean PRODUCTION_MODE 							= false;
	boolean LOG_INFO 									= true; // should stay true even in
																// production mode
	boolean LOG_DEBUG 									= !PRODUCTION_MODE;
	boolean LOG_VERBOSE 								= true;

	String DBNAME 										= "field_";
	String COMPANY 										= "company";
	String EMPCODE 										= "empCode";
	String CHECKBOX 									= "checkbox";
	String FAIL 										= "fail";
	String NOT_FOUND 									= "notfound";
	String OPEN 										= "open";
	String PASSWORD 									= "password";
	String QUERY 										= "query";
	String REFRESH 										= "refresh";
	String REJECTED 									= "rejected";
	int REQ_CODE 										= 57005;
	String RESULT 										= "result";
	String SUCCESS 										= "success";
	String TABLE 										= "table";
	String LOCAL 										= "local";
	String TITLE 										= "title";
	String TYPE 										= "type";
	String TRIES 										= "tries";
	String UNVISITED 									= "unvisited";
	String USERNAME 									= "username";

	String BASE_URL  									= "http://retailworks.in/panel/";
	String SYNC_URL  									= BASE_URL + "device_sync.php";
	String LOGIN_URL 									= BASE_URL + "device_login.php";
}
