package filecatalog.server.model;

import filecatalog.common.FileCatalogClient;
import filecatalog.common.FileChangeDTO;
import filecatalog.client.view.ThreadSafeStdOut;
import filecatalog.server.integration.FileServerDAO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;

public class FileHandler {
    private final String root = "./FileCatalog";
    private Path workingDir = Paths.get(root);
    private FileServerDAO fileServerDAO;
    private HashMap<Long,FileCatalogClient> notifyUsers = new HashMap();
    private ThreadSafeStdOut consoleOut = new ThreadSafeStdOut();

    public FileHandler(){
        fileServerDAO = FileServerDAO.getInstance();
    }

    public byte[] openFile(Holder holder, String filename) throws IOException, FileException, UnauthorizedAccessException {
        MetaFile file = fileServerDAO.findFileByName(filename, true);

        if(file == null){
            throw new FileNotFoundException("File " + filename + " not found");
        }

        Path path = workingDir.resolve(Paths.get(filename));

        byte[] result;

        if(file.getOwner().getUserID() == holder.getUserID()){
            result = Files.readAllBytes(path);
        }
        else if(file.isPublicFile() && file.isPublicRead()){
            result = Files.readAllBytes(path);
            if(file.isNotify()){
                FileCatalogClient client = notifyUsers.get(file.getOwner().getUserID());
                if(client != null){
                    try{
                        client.handleMsg(new FileChangeDTO(filename, holder.getUsername(),"File was read"));
                    }
                    catch(RemoteException e){
                        consoleOut.println("Could not notify user " + file.getOwner().getUsername());
                    }

                }
            }
        }
        else{
            throw new UnauthorizedAccessException("Permission denied");
        }

        return result;
    }

    public Collection<MetaFile> listFiles(long userID){
        return fileServerDAO.getUserFiles(userID);
    }

    public void deleteFile(Holder holder, String filename) throws FileException, IOException, UnauthorizedAccessException {
        MetaFile file = fileServerDAO.findFileByName(filename,true);
        if(file != null){
            if(file.getOwner().getUserID() == holder.getUserID()){
                fileServerDAO.deleteFile(file);
                Path path = workingDir.resolve(Paths.get(filename));
                Files.deleteIfExists(path);
            }
            else if(file.isPublicWrite()){
                fileServerDAO.deleteFile(file);
                Path path = workingDir.resolve(Paths.get(filename));
                Files.deleteIfExists(path);
                if(file.isNotify()){
                    FileCatalogClient client = notifyUsers.get(file.getOwner().getUserID());
                    if(client != null){
                        try{
                            client.handleMsg(new FileChangeDTO(filename, holder.getUsername(),"File was deleted"));
                        }
                        catch(RemoteException e){
                            consoleOut.println("Could not notify user " + file.getOwner().getUsername());
                        }
                    }
                }
            }
            else{
                throw new UnauthorizedAccessException("Permission denied to delete file");
            }
        }
        else{
            throw new FileNotFoundException("File "+ filename +" not found");
        }
    }

    public void modifyFile(Holder holder, String filename, boolean publicFile, boolean publicWrite, boolean publicRead) throws UnauthorizedAccessException, FileNotFoundException {
        MetaFile metaFile = fileServerDAO.findFileByName(filename,false);
        if(metaFile != null && metaFile.getOwner().getUserID() == holder.getUserID()){
            metaFile.setPublicFile(publicFile);
            metaFile.setPublicWrite(publicWrite);
            metaFile.setPublicRead(publicRead);
            fileServerDAO.updateFile();
        }
        else if(metaFile != null){
            throw new UnauthorizedAccessException("Permission denied to modify file!");
        }
        else{
            throw new FileNotFoundException("File "+ filename +" not found!");
        }
    }

    public void uploadFile(Holder owner, String filename, byte[] data, boolean publicFile, boolean publicWrite, boolean publicRead) throws IOException, FileException, UnauthorizedAccessException {
        MetaFile metaFile = fileServerDAO.findFileByName(filename,false);
        Path file = workingDir.resolve(Paths.get(filename));
        if(metaFile == null){
            fileServerDAO.createFile(new MetaFile(filename,data.length,owner,publicFile,publicWrite,publicRead));
            Files.write(file,data);
        }
        else if(metaFile.getOwner().getUserID() == owner.getUserID()){
            Files.write(file,data);
            metaFile.setSize(data.length);
            metaFile.setPublicFile(publicFile);
            metaFile.setPublicWrite(publicWrite);
            metaFile.setPublicRead(publicRead);
            fileServerDAO.updateFile();
        }
        else if(metaFile.isPublicWrite()){
            Files.write(file,data);
            metaFile.setSize(data.length);
            fileServerDAO.updateFile();
            if(metaFile.isNotify()){
                FileCatalogClient client = notifyUsers.get(metaFile.getOwner().getUserID());
                if(client != null){
                    try{
                        client.handleMsg(new FileChangeDTO(filename, owner.getUsername(),"File was overwritten"));
                    }
                    catch(RemoteException e){
                        consoleOut.println("Could not notify user " + metaFile.getOwner().getUsername());
                    }

                }
            }
        }
        else{
            throw new UnauthorizedAccessException("Write access to file denied");
        }
    }

    public void notifyFile(Holder holder, String filename, boolean notify) throws UnauthorizedAccessException, FileNotFoundException {
        MetaFile file = fileServerDAO.findFileByName(filename,false);
        if(file != null && file.getOwner().getUserID() == holder.getUserID()){
            file.setNotify(notify);
            fileServerDAO.updateFile();
        }
        else if(file != null){
            fileServerDAO.updateFile();
            throw new UnauthorizedAccessException("Permission denied to modify file!");
        }
        else{
            fileServerDAO.updateFile();
            throw new FileNotFoundException("File "+ filename +" not found!");
        }
    }

    public void addNotifyUser(long userID, FileCatalogClient client) {
        notifyUsers.put(userID,client);
    }
}