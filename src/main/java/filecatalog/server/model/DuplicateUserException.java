package filecatalog.server.model;

public class DuplicateUserException extends Exception {
    public DuplicateUserException(String reason){
        super(reason);
    }

    public DuplicateUserException(String reason, Throwable rootcause){
        super(reason,rootcause);
    }
}
