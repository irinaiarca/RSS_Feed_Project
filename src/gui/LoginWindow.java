package gui;

import static javafx.geometry.HPos.RIGHT;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import model.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginWindow implements EventHandler<ActionEvent> {
	
	Stage stage;
	
	private EntityManager em;
	private Runner runner;
	
	TextField userField;
	PasswordField passField;
	
	RegisterWindow rw;
	FeedReaderWindow mainWindow;

	public LoginWindow(Stage stage, Runner runner) {
		stage.setTitle("Welcome");

		this.runner = Runner.getInstance();
		this.em = runner.entityManager;
		
		hookEvents();
		
		setupPanel(stage);
	}
	
	/**
	 * An auxiliary function that hooks certain methods to global events.
	 */
	private void hookEvents() {
		LoginWindow self = this;
		runner.mediator.subscribe(new String[]{"mainwindow.close", "loginwindow.invalidate"}, new util.PubSubHandler() {
			
			@Override
			public void exec(Object... args) {
				self.reset();
			}
		});
	}
	
	private void setupPanel(Stage stage) {
		
		this.stage = stage;
		
		GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Please log in");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        userField = new TextField();
        grid.add(userField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        passField = new PasswordField();
        grid.add(passField, 1, 2);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        
        Button btn2 = new Button ("New user");
        HBox hBtn2 = new HBox(10);
        hBtn2.setAlignment(Pos.BOTTOM_LEFT);
        hBtn2.getChildren().add(btn2);
        grid.add(hBtn2, 0, 4);
        
        final Text actiontarget = new Text();
        grid.add(actiontarget, 0, 6);
        GridPane.setColumnSpan(actiontarget, 2);
        GridPane.setHalignment(actiontarget, RIGHT);
        actiontarget.setId("actiontarget");
        
        btn.setOnAction(this);
        
        btn2.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent e)
        	{
            	if (rw == null) rw = new RegisterWindow();
            	runner.mediator.publish("registerwindow.open");
        	}
        });

        Scene scene = new Scene(grid, 300, 275);
        stage.setScene(scene);
        stage.show();
		
	}

	@Override
	/**
	 * Searched for the user in the database. If both the username and the password 
	 * match, the user is logged in and the main window is opened.
	 */
	public void handle(ActionEvent arg0) {

    	
    	String currentUser = userField.getText();
    	String currentPwd = passField.getText();
  
    	Query q = em.createQuery("SELECT usr FROM User usr WHERE usr.username=\'" + currentUser + "\' and usr.password=\'" + currentPwd + "\'");
    	    	
    	try {
    		User foundUser = (User)q.getSingleResult();
    		if (foundUser != null) {
    			
    			runner.mediator.publish("loginwindow.invalidate");
				runner.loggedUser = foundUser;
				runner.currentId = foundUser.getIdUser();
				
				if (mainWindow == null) mainWindow = new FeedReaderWindow();
				runner.mediator.publish("user.login");
				
				stage.hide();
    		} else { throw new Exception("Still not returned any users!"); }
    	} catch (Exception ex) {
    		
    		(new AlertWindow()).message("No such user has been found").handle(new EventHandler<WindowEvent>() {
    			@Override
    			public void handle(WindowEvent arg0) {
    				runner.mediator.publish("loginwindow.invalidate");
    			}
    		}).show();
    	}
    	
	}
	
	/**
	 * Logs off the user.
	 */
	public void reset() {
		runner.currentId = null;
		runner.loggedUser = null;
		userField.setText("");
		passField.setText("");
		stage.show();
	}
	
}
