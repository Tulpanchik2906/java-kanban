package manager.tasks;

import manager.Managers;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message){
        super(message);
    }
    
}
