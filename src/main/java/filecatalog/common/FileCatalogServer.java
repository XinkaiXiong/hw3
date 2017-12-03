package filecatalog.common;

import filecatalog.server.model.UnauthorizedAccessException;
import filecatalog.server.model.FileException;
import filecatalog.server.model.DuplicateUserException;
import filecatalog.server.model.MetaFile;
import filecatalog.server.model.UserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface FileCatalogServer extends Remote{
    String FILE_CATALOG_NAME_IN_REGISTRY = "filecatalog";

    long login(Credentials credentials, FileCatalogClient client) throws RemoteException, UserException;

    void logout(long key) throws RemoteException,UserException;

    void register(Credentials credentials) throws RemoteException, UserException, DuplicateUserException;

    void unRegister(String username) throws RemoteException, UserException;

    void uploadFile(long key, String fileName, byte[] data, boolean publicFile, boolean publicWrite, boolean publicRead) throws IOException, FileException, UnauthorizedAccessException;

    byte[] downloadFile(long key, String filename) throws IOException, FileException, UnauthorizedAccessException;

    void deleteFile(long key, String filename) throws IOException, FileException, UserException, UnauthorizedAccessException;

    void modifyFile(long key, String filename, boolean publicFile, boolean publicWrite, boolean publicRead) throws RemoteException, FileException, UnauthorizedAccessException, UserException, FileNotFoundException;

    Collection<MetaFile> listFiles(long key) throws RemoteException;

    void notifyFile(long key, String filename, boolean notify) throws RemoteException, UserException, UnauthorizedAccessException, FileException, FileNotFoundException;
}
