package filecatalog.server.model;

import filecatalog.common.Credentials;
import filecatalog.server.integration.FileServerDAO;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserManager {
    private final Map<Long, Holder> usersLoggedIn = Collections.synchronizedMap(new HashMap<>());
    private final Map<Long, Long> userIDtoKey = Collections.synchronizedMap(new HashMap<>());
    private final Random idGenerator = new Random();
    private FileServerDAO fileServerDAO;


    public UserManager(){
        fileServerDAO = FileServerDAO.getInstance();
    }

    public void register(Credentials credentials) throws DuplicateUserException {
        Holder holder = fileServerDAO.findUserByName(credentials.getUsername());
        if(holder == null){
            fileServerDAO.register(new Holder(credentials.getUsername(),credentials.getPassword()));
        }
        else{
            throw new DuplicateUserException("User " + credentials.getUsername() + " is already registered!");
        }
    }

    public void unRegister(String username) throws UserException {
        fileServerDAO.unRegister(username);
    }

    public long login(Credentials credentials){
        Holder holder =  fileServerDAO.findUserByName(credentials.getUsername());
        if(holder != null){
            if(holder.getPassword().equals(credentials.getPassword())){
                long id = idGenerator.nextLong();
                if(userIDtoKey.containsKey(holder.getUserID())){
                    long tmp = userIDtoKey.get(holder.getUserID());
                    usersLoggedIn.remove(tmp);
                    userIDtoKey.put(holder.getUserID(), id);
                    usersLoggedIn.put(id, holder);
                }
                else{
                    usersLoggedIn.put(id,holder);
                    userIDtoKey.put(holder.getUserID(),id);
                }
                return id;
            }
        }
        return -1;
    }

    public void logout(long key) throws UserException {
        Holder holder = usersLoggedIn.remove(key);
        if(holder == null){
            throw new UserException("User is not logged in.");
        }
        else{
            userIDtoKey.remove(holder.getUserID());
        }
    }

    public Holder getUser(long key){
        return usersLoggedIn.get(key);
    }
}
