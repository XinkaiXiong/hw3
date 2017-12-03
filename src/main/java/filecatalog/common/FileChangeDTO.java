package filecatalog.common;

import java.io.Serializable;

public class FileChangeDTO implements Serializable{
    private String filename;
    private String modifiedByUser;
    private String modifiedAction;

    public FileChangeDTO(String filename, String modifiedByUser, String modifiedAction) {
        this.filename = filename;
        this.modifiedByUser = modifiedByUser;
        this.modifiedAction = modifiedAction;
    }

    public String getFilename() {
        return filename;
    }

    public String getModifiedByUser() {
        return modifiedByUser;
    }

    public String getModifiedAction() {
        return modifiedAction;
    }
}
