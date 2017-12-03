package filecatalog.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileCatalogClient extends Remote{

    void handleMsg(FileChangeDTO message) throws RemoteException;
}
