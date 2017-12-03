package filecatalog.server.model;

public class FileException extends Exception {
    public FileException(String reason){
        super(reason);
    }

    public FileException(String reason, Throwable rootcause){
        super(reason,rootcause);
    }
}
