package gpup.utils;

import chat.ChatManager;
import gpup.servlets.GraphManger;
import gpup.servlets.MissionManger;
import gpup.servlets.UserManager;
import gpup.servlets.WorkerManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import static gpup.constants.Constants.INT_PARAMETER_ERROR;


public class ServletUtils {

	private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
	private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";
	private static final String GRAPH_MANAGER_ATTRIBUTE_NAME = "graphManager";
	private static final String MISSION_MANAGER_ATTRIBUTE_NAME = "missionManager";
	private static final String WORKER_MANAGER_ATTRIBUTE_NAME = "workerManager";

	/*
	Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
	the actual fetch of them is remained un-synchronized for performance POV
	 */
	private static final Object userManagerLock = new Object();
	private static final Object chatManagerLock = new Object();
	private static final Object missionManagerLock = new Object();
	private static final Object workerManagerLock = new Object();

	public static UserManager getUserManager(ServletContext servletContext) {

		synchronized (userManagerLock) {
			if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
			}
		}
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
	}
	public static WorkerManager getWorkerManager(ServletContext servletContext) {

		synchronized (workerManagerLock) {
			if (servletContext.getAttribute(WORKER_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(WORKER_MANAGER_ATTRIBUTE_NAME, new WorkerManager());
			}
		}
		return (WorkerManager) servletContext.getAttribute(WORKER_MANAGER_ATTRIBUTE_NAME);
	}
	public static MissionManger getMissionManager(ServletContext servletContext) {
		synchronized (missionManagerLock) {
			if (servletContext.getAttribute(MISSION_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(MISSION_MANAGER_ATTRIBUTE_NAME, new MissionManger());
			}
		}
		return (MissionManger) servletContext.getAttribute(MISSION_MANAGER_ATTRIBUTE_NAME);
	}
	public static GraphManger getGraphManager(ServletContext servletContext) {
			if (servletContext.getAttribute(GRAPH_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(GRAPH_MANAGER_ATTRIBUTE_NAME, new GraphManger());

		}
		return (GraphManger) servletContext.getAttribute(GRAPH_MANAGER_ATTRIBUTE_NAME);
	}
	public static ChatManager getChatManager(ServletContext servletContext) {
		synchronized (chatManagerLock) {
			if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
				servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
			}
		}
		return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
	}
	public static int getIntParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException numberFormatException) {
			}
		}
		return INT_PARAMETER_ERROR;
	}

}
