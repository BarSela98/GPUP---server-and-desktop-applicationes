package util;


import com.google.gson.Gson;
////////////////////////////////  admin
public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 1000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String MAIN_PAGE_ADMIN_FXML_RESOURCE_LOCATION = "/component/mainApp/adminAppMain.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/component/login/login.fxml";
    public final static String ADMIN_PAGE_FXML_RESOURCE_LOCATION = "/component/page/adminPage.fxml";
    public final static String MISSION_FXML_RESOURCE_LOCATION = "/component/graphAdmin/missionAdmin.fxml";
    public final static String LOCATION = "/component/usersList/usersList.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/GPUP";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/userslist";
    // Chat
    public final static String SEND_CHAT_LINE = FULL_SERVER_PATH + "/pages/chatroom/sendChat";
    public final static String CHAT_LINES_LIST = FULL_SERVER_PATH + "/chat";
    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
