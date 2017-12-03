package filecatalog.server.integration;

import filecatalog.server.model.FileException;
import filecatalog.server.model.MetaFile;
import filecatalog.server.model.Holder;
import filecatalog.server.model.UserException;
import javax.persistence.*;
import java.util.Collection;

public class FileServerDAO {

    private final EntityManagerFactory emFactory;
    private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();
    private static FileServerDAO instance;

    public static FileServerDAO getInstance() {
        if(instance == null){
            instance = new FileServerDAO();
        }
        return instance;
    }

    private FileServerDAO(){
        emFactory = Persistence.createEntityManagerFactory("fileCatalogPersistenceUnit");
    }

    public Holder findUserByName(String username){
        if(username == null){
            return null;
        }
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findUserByName", Holder.class).setParameter("username", username).getSingleResult();
            } catch (NoResultException noSuchUser) {
                return null;
            }
        }
        finally{
            commitTransaction();
        }
    }

    public void updateFile(){
        commitTransaction();
    }

    private void commitTransaction() {
        threadLocalEntityManager.get().getTransaction().commit();
    }



    private EntityManager beginTransaction() {
        EntityManager em = emFactory.createEntityManager();
        threadLocalEntityManager.set(em);
        EntityTransaction transaction = em.getTransaction();
        if(!transaction.isActive()){
            transaction.begin();
        }
        return em;
    }

    public boolean register(Holder newUser) {
        try {
            EntityManager em = beginTransaction();
            em.persist(newUser);
            return true;
        }
        catch(Exception e){
            return false;
        }
        finally{
            commitTransaction();
        }
    }

    public void unRegister(String username) throws UserException {
        try{
            EntityManager em = beginTransaction();
            int res = em.createNamedQuery("deleteUserByName",Holder.class).setParameter("username",username).executeUpdate();
            if(res != 1){
                throw new UserException("Could not delete user: " + username);
            }
        }
        finally{
            commitTransaction();
        }
    }

    public MetaFile findFileByName(String filename, boolean endTransactionAfterSearching){
        if(filename == null){
            return null;
        }
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findFileByName", MetaFile.class).setParameter("filename", filename).getSingleResult();
            } catch (NoResultException noSuchFile) {
                return null;
            }
        }
        finally{
            if(endTransactionAfterSearching){
                commitTransaction();
            }
        }
    }

    public boolean createFile(MetaFile file) {
        try{
            EntityManager em = beginTransaction();
            em.persist(file);
            return true;
        }
        finally{
            commitTransaction();
        }
    }

    public void deleteFile(MetaFile file) throws FileException {
        String filename = file.getName();
        try{
            EntityManager em = beginTransaction();
            int res = em.createNamedQuery("deleteFileByName",MetaFile.class).setParameter("filename",filename).executeUpdate();
            if(res != 1){
                throw new FileException("Could not delete file: " + filename);
            }
        }
        finally{
            commitTransaction();
        }
    }

    public Collection getUserFiles(long userID){
        try{
            EntityManager em = beginTransaction();
            return em.createNamedQuery("getUserFiles",MetaFile.class).setParameter("userID",userID).getResultList();
        }
        finally {
            commitTransaction();
        }
    }
}
