package component.workerPage;

import component.api.WorkerCommands;
import component.mainApp.WorkerAppMainController;

public class WorkerPageController implements WorkerCommands {

    private WorkerAppMainController workerAppMainController;

    public void setActive() {
        //usersListComponentController.startListRefresher();
       // chatAreaComponentController.startListRefresher();
    }

    public void setInActive() {
        try {
         //   usersListComponentController.close();
         //   chatAreaComponentController.close();
        } catch (Exception ignored) {}
    }

    public void setAppMainController(WorkerAppMainController workerAppMainController) {
        this.workerAppMainController = workerAppMainController;
    }

    @Override
    public void logout() {
        workerAppMainController.switchToLogin();
    }
}
