package gui;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.persistence.EntityManager;

import model.Resource;
import rss.model.FeedMessage;

public class AddResourceWindow {

	private EntityManager em;
	private Runner runner;
	
	private Stage stage;
    private TextField myURL;
    TextArea description;
    
    public AddResourceWindow() 
    {
    	
    	runner = Runner.getInstance();
    	em = runner.entityManager;
    	
    	setupStage();
    	hookEvents();
    }
    
    private void hookEvents()
    {
    	runner.mediator.subscribe("addresourcewindow.open", new util.PubSubHandler() {
    		
    		@Override
    		public void exec(Object... args)
    		{
    			stage.show();
    		}
    	});
    }
    
    private void setupStage()
    {
    	if (stage != null) return;
    	
    	stage = new Stage();
    	
    	stage.setHeight(300);
    	stage.setWidth(500);
    	
    	GridPane gridRSS = new GridPane();
        gridRSS.setAlignment(Pos.CENTER);
        gridRSS.setHgap(10);
        gridRSS.setVgap(10);
        gridRSS.setPadding(new Insets(10, 10, 10, 10));
        
        Scene sc = new Scene(gridRSS, 300, 500);
        stage.setScene(sc);
        
        Label help = new Label("Type or paste a valid RSS URL in the textfield below and then click OK:");
        gridRSS.add(help, 0, 0);
        
        myURL = new TextField();
        myURL.setPrefWidth(400);
        gridRSS.add(myURL, 0, 1);
        
        Button btn = new Button("Add");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.getChildren().add(btn);
        gridRSS.add(hbBtn, 0, 4);
        
        Label addDescription = new Label("Type a description for your URL down below: ");
        gridRSS.add(addDescription, 0, 2);
        
        description = new TextArea();
        gridRSS.add(description, 0, 3);
        
        AddResourceWindow self = this;
        
        btn.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent e)
        	{
        		self.add();
        	}
        });
    }
    
    private void add() {
    	
    	Resource newResource = new Resource();
    	newResource.setUrl(myURL.getText());
    	newResource.setUser(runner.loggedUser);
    	newResource.setDescription(description.getText());
    	
    	em.getTransaction().begin();
    	em.persist(newResource);
    	em.getTransaction().commit();
    	
    }
}
