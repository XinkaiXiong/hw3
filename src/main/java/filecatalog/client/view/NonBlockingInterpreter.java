package filecatalog.client.view;


import filecatalog.client.model.FileManager;
import filecatalog.common.*;
import filecatalog.server.model.*;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Scanner;

public class NonBlockingInterpreter implements Runnable{
    FileCatalogServer fileServer;

    private boolean running = false;
    private final Scanner input = new Scanner(System.in);
    private long key = -1;
    private static final String PROMPT = "> ";
    private final ThreadSafeStdOut consoleOut = new ThreadSafeStdOut();
    private final FileManager fileManager = FileManager.getInstance();

    private ServerMessages serverOutput;

    public NonBlockingInterpreter() throws RemoteException {
        this.serverOutput = new ServerMessages();
    }

    public void start(FileCatalogServer fileServer){
        this.fileServer = fileServer;
        if(running){
            return;
        }
        running = true;
        new Thread(this).run();
    }

    @Override
    public void run() {
        consoleOut.println("Type Help for command menu.");
        while(running){
            try{
                consoleOut.print(PROMPT);
                String in = input.nextLine();
                if(in.equals("")){
                    consoleOut.println("");
                    continue;
                }
                CmdLine cmd = new CmdLine(in);
                String filename;
                switch(cmd.getCmd()){
                    case QUIT:
                        running = false;
                        serverOutput = null;
                        break;
                    case LOGIN:
                        if(cmd.getArgs().length >= 2){
                            Credentials c = new Credentials(cmd.getArgs()[0],cmd.getArgs()[1]);
                            long result = fileServer.login(c,serverOutput);
                            if(result == -1){
                                consoleOut.println("Incorrect credentials!");
                            }
                            else{
                                consoleOut.println("Successfully logged in!");
                                key = result;
                            }
                        }
                        else{
                            consoleOut.println("Please provide both username and password.");
                        }
                        break;
                    case LOGOUT:
                        fileServer.logout(key);
                        consoleOut.println("Successfully logged out!");
                        break;
                    case REGISTER:
                        if(cmd.getArgs().length >= 2){
                            fileServer.register(new Credentials(cmd.getArgs()[0],cmd.getArgs()[1]));
                            consoleOut.println("Successfully registered account!");
                        }
                        else{
                            consoleOut.println("Please provide both username and password.");
                        }
                        break;
                    case UNREGISTER:
                        if(cmd.getArgs().length >= 1){
                            fileServer.unRegister(cmd.getArgs()[0]);
                            consoleOut.println("Successfully unregistered account!");
                        }
                        else{
                            consoleOut.println("Please provide a username to unregister.");
                        }
                        break;                    
                    case DELETE:
                        if(cmd.getArgs().length >= 1){
                            fileServer.deleteFile(key,cmd.getArgs()[0]);
                            consoleOut.println("Successfully deleted file!");
                        }
                        else{
                            consoleOut.println("Please provide a filename to delete.");
                        }
                        break;
                    case LIST:
                        formatFilesOutput(fileServer.listFiles(key));
                        break;
                    case UPLOAD:
                        upload(cmd);
                        break;
                    case DOWNLOAD:
                        if(cmd.getArgs().length >= 1){
                            filename = cmd.getArgs()[0];
                                fileManager.writeFile(filename,fileServer.downloadFile(key, filename));
                                consoleOut.println("Successfully downloaded file!");
                        }
                        else{
                            consoleOut.println("Please provide a valid filename to download.");
                        }
                        break;
                    case NOTIFY:
                        if(cmd.getArgs().length >= 2){
                            fileServer.notifyFile(key,cmd.getArgs()[0],Boolean.parseBoolean(cmd.getArgs()[1]));
                            consoleOut.println("Notify for : " + cmd.getArgs()[0]);
                        }
                        else{
                            consoleOut.println("Please use format Notify <filename> <True/False>.");
                        }
                        break;
                    case MODIFY:
                        if(cmd.getArgs().length >= 4){
                            fileServer.modifyFile(key,cmd.getArgs()[0],Boolean.parseBoolean(cmd.getArgs()[1]),Boolean.parseBoolean(cmd.getArgs()[2]),Boolean.parseBoolean(cmd.getArgs()[3]));
                            consoleOut.println("Modification successful!");
                        }
                        else{
                            consoleOut.println("Please use format Modify <filename> <isPublicFile> <publicWrite> <publicRead>.");
                        }
                        break;
                    case HELP:
                        listMenu();
                        break;
                    case UNKNOWN:
                        consoleOut.println("Unknown command. Type help for a list of commands.");
                        break;
                }
            } catch (DuplicateUserException e) {
                consoleOut.println("Username already in use. Please pick another username.");
            }
            catch(FileNotFoundException e){
                consoleOut.println("Could not find file");
            }

            catch(UnauthorizedAccessException | IOException | UserException | FileException e){
                consoleOut.println("Operation failed");
                consoleOut.println(e.getMessage());
            }
        }
    }

    private void listMenu() {
        consoleOut.println("Help - Display command menu");
        consoleOut.println("Quit - End program");
        consoleOut.println("Register <username> <password>");
        consoleOut.println("Unregister <username>");
        consoleOut.println("Login <username> <password>");
        consoleOut.println("Logout");
        consoleOut.println("List");
        consoleOut.println("Upload <filename>");
        consoleOut.println("Download <filename>");
        consoleOut.println("Delete <filename>");
        consoleOut.println("Modify <filename> <isPublicFile> <publicWrite> <publicRead>");
        consoleOut.println("Notify <filename> <True/False>");
    }

    private void upload(CmdLine cmd) throws FileException, UnauthorizedAccessException, IOException {
        String filename = "";
        boolean publicFile = false;
        boolean publicWrite = false;
        boolean publicRead = false;

        if (cmd.getArgs().length == 1) {
            filename = cmd.getArgs()[0];
            consoleOut.print("Is it a public file? True/False: ");
            publicFile = input.nextBoolean();
            consoleOut.print("Public write? True/False: ");
            publicWrite = input.nextBoolean();
            consoleOut.print("Public read? True/False: ");
            publicRead = input.nextBoolean();
        }
        if(!filename.equals("")){
            try{
                byte[] data = fileManager.readFile(filename);
                fileServer.uploadFile(key,filename,data,publicFile,publicWrite,publicRead);
                consoleOut.println("File uploaded successfully!");
            }
            catch(FileNotFoundException e){
                consoleOut.println("File not found.");
            }
        }
        else{
            consoleOut.println("Invalid filename");
        }
    }

    private void formatFilesOutput(Collection<MetaFile> files){
        if(files.size() == 0){
            consoleOut.println("No files stored in remote directory.");
        }
        for(MetaFile file : files){
            consoleOut.println("Name: " + file.getName()+", Public? " + file.isPublicFile() + ", Write? " + file.isPublicWrite() + ", Read? " + file.isPublicRead()
                    + ", Notify of change? " + file.isNotify() + ", Owner: " + file.getOwner().getUsername() + ", Size: " + file.getSize());
        }
    }

    private class ServerMessages extends UnicastRemoteObject implements FileCatalogClient{
        public ServerMessages() throws RemoteException {}

        @Override
        public void handleMsg(FileChangeDTO message) throws RemoteException {
            consoleOut.println("File: " + message.getFilename() + " was modified by: " + message.getModifiedByUser() + ". Operation: " + message.getModifiedAction());
            consoleOut.print(PROMPT);
        }
    }
}
