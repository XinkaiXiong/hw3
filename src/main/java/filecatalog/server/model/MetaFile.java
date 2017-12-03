package filecatalog.server.model;

import javax.persistence.*;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(
                name = "findFileByName",
                query = "SELECT metafile FROM MetaFile metafile WHERE metafile.name LIKE :filename"
        ),
        @NamedQuery(
                name = "deleteFileByName",
                query = "DELETE FROM MetaFile metafile WHERE metafile.name LIKE :filename"
        ),
        @NamedQuery(
                name = "getAllFiles",
                query = "SELECT metafile FROM MetaFile metafile"
        ),
        @NamedQuery(
                name = "getUserFiles",
                query = "SELECT metafile FROM MetaFile metafile WHERE metafile.owner.userID = :userID OR metafile.publicFile = TRUE"
        )
})

@Entity(name="MetaFile")
public class MetaFile implements Serializable{
    @Id
    private String name;
    private long size;

    @ManyToOne
    @JoinColumn(name="owner",nullable = false)
    private Holder owner;
    private boolean publicFile;
    private boolean publicWrite;
    private boolean publicRead;
    private boolean notify;

    public MetaFile() {
    }

    public MetaFile(String name, long size, Holder owner, boolean publicFile, boolean publicWrite, boolean publicRead) {
        this.name = name;
        this.size = size;
        this.owner = owner;
        this.publicFile = publicFile;
        this.publicWrite = publicWrite;
        this.publicRead = publicRead;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public Holder getOwner() {
        return owner;
    }

    public boolean isPublicFile() {
        return publicFile;
    }

    public boolean isPublicWrite() {
        return publicWrite;
    }

    public boolean isPublicRead() {
        return publicRead;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setPublicFile(boolean publicFile) {
        this.publicFile = publicFile;
    }

    public void setPublicWrite(boolean publicWrite) {
        this.publicWrite = publicWrite;
    }

    public void setPublicRead(boolean publicRead) {
        this.publicRead = publicRead;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("MetaFile: [");
        string.append("name: ");
        string.append(name);
        string.append(", size: ");
        string.append(size);
        string.append(", owner: ");
        string.append(owner.getUsername());
        string.append(", publicFile: ");
        string.append(publicFile);
        string.append(", publicWrite: ");
        string.append(publicWrite);
        string.append(", publicRead: ");
        string.append(publicRead);
        string.append("]");
        return string.toString();
    }
}
