package filecatalog.server.model;

public class UnauthorizedAccessException extends Exception {
    public UnauthorizedAccessException(String reason){
        super(reason);
    }

    public UnauthorizedAccessException(String reason, Throwable rootcause){
        super(reason,rootcause);
    }
}
