package filecatalog.server.controller;

import filecatalog.server.model.Holder;
import filecatalog.server.model.FileHandler;
import filecatalog.server.model.UnauthorizedAccessException;
import filecatalog.server.model.FileException;
import filecatalog.server.model.UserManager;
import filecatalog.server.model.DuplicateUserException;
import filecatalog.server.model.MetaFile;
import filecatalog.server.model.UserException;
import filecatalog.common.Credentials;
import filecatalog.common.FileCatalogClient;
import filecatalog.common.FileCatalogServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

public class Controller extends UnicastRemoteObject implements FileCatalogServer{
    private final UserManager userManager;
    private final FileHandler fileHandler;


    public Controller() throws RemoteException{
        super();
        userManager = new UserManager();
        fileHandler = new FileHandler();
    }

    @Override
    public long login(Credentials credentials, FileCatalogClient client) throws RemoteException {
        long key = userManager.login(credentials);
        Holder holder = userManager.getUser(key);
        if(holder != null){
            fileHandler.addNotifyUser(holder.getUserID(),client);
        }
        return key;
    }

    @Override
    public void logout(long key) throws RemoteException, UserException {
        userManager.logout(key);
    }

    @Override
    public void register(Credentials credentials) throws DuplicateUserException {
        userManager.register(credentials);

    }

    @Override
    public void unRegister(String username) throws UserException {
        userManager.unRegister(username);
    }

    @Override
    public void uploadFile(long key, String fileName, byte[] data, boolean publicFile, boolean publicWrite, boolean publicRead) throws IOException, FileException, UnauthorizedAccessException {
        Holder holder = userManager.getUser(key);
        if(holder != null){
            fileHandler.uploadFile(holder,fileName, data, publicFile, publicWrite, publicRead);
        }
        else{
            throw new UnauthorizedAccessException("User not logged in.");
        }
    }

    @Override
    public byte[] downloadFile(long key, String filename) throws IOException, FileException, UnauthorizedAccessException {
        Holder holder = userManager.getUser(key);
        if(holder == null){
            throw new UnauthorizedAccessException("User not logged in!");
        }
        return fileHandler.openFile(holder, filename);
    }

    @Override
    public void deleteFile(long key, String filename) throws IOException, FileException, UserException, UnauthorizedAccessException {
        Holder holder = userManager.getUser(key);
        if(holder != null){
            fileHandler.deleteFile(holder,filename);
        }
        else{
            throw new UserException("User is not logged in!");
        }
    }

    @Override
    public void modifyFile(long key, String filename, boolean publicFile, boolean publicWrite, boolean publicRead) throws RemoteException, FileException, UnauthorizedAccessException, UserException, FileNotFoundException {
        Holder holder = userManager.getUser(key);
        if(holder != null){
            fileHandler.modifyFile(holder,filename,publicFile,publicWrite,publicRead);
        }
        else{
            throw new UserException("User is not logged in!");
        }
    }

    @Override
    public Collection<MetaFile> listFiles(long key) throws RemoteException {
        Holder holder = userManager.getUser(key);
        long userID;
        if(holder != null){
            userID = holder.getUserID();
        }
        else{
            userID = -1;
        }
        return fileHandler.listFiles(userID);
    }

    @Override
    public void notifyFile(long key, String filename, boolean notify) throws RemoteException, UserException, UnauthorizedAccessException, FileException, FileNotFoundException {
        Holder holder = userManager.getUser(key);
        if(holder != null){
            fileHandler.notifyFile(holder,filename,notify);
        }
        else{
            throw new UserException("User is not logged in!");
        }
    }
}
