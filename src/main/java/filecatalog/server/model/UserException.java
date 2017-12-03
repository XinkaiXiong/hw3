package filecatalog.server.model;

public class UserException extends Exception {
    public UserException(String reason){
        super(reason);
    }

    public UserException(String reason, Throwable rootcause){
        super(reason,rootcause);
    }
}
