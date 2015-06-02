package gui;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import model.User;
import javafx.application.Application;
import javafx.stage.Stage;

public class Runner extends Application {
    public User loggedUser;
    public Integer currentId;
    
    @PersistenceContext protected EntityManager entityManager;

    void setUp() {
    	if (entityManager != null) return;
    	
        EntityManagerFactory fac = Persistence.createEntityManagerFactory("RSS_feed_project");
        entityManager = fac.createEntityManager();
    }
    
    @Override
    public void start(Stage primaryStage) {
    	setUp();
    	new LoginWindow(primaryStage, entityManager, this);
    }
    
    public void startApp(String[] args) {
    	setUp();
    	launch(args);
    }

}