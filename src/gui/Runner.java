package gui;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import util.PubSub;
import model.User;
import javafx.application.Application;
import javafx.stage.Stage;

public class Runner extends Application {
	
	private static Runner instance;
	
    public User loggedUser;
    public Integer currentId;
    public PubSub mediator = new PubSub();
    
    @PersistenceContext public EntityManager entityManager;
    /**
     * Verifies if the stage already exists and, if it doesn't, it creates it. If it already exists, nothing happens.
     */
    void setUp() {
    	if (entityManager != null) return;
    	
    	mediator.publish("log", "something", "useless");
        EntityManagerFactory fac = Persistence.createEntityManagerFactory("RSS_feed_project");
        entityManager = fac.createEntityManager();
    }
    
    @Override
    public void start(Stage primaryStage) {
    	setUp();
    	new LoginWindow(primaryStage, this);
    }
    
    public void startApp(String[] args) {
    	setUp();
    	launch(args);
    }
    
    public static Runner getInstance() {
    	if (instance == null) instance = new Runner();
    	return instance;
    }
    
    public static Runner createInstance() {
    	return getInstance();
    }

}