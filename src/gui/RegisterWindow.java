package gui;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import exceptions.InvalidUserException;
import util.PubSubHandler;
import model.User;

public class RegisterWindow implements EventHandler<ActionEvent> {
	
	private Runner runner;
	private EntityManager em;
	private TextField userField, passField;
	private Stage stage;

	public RegisterWindow() {
		runner = Runner.getInstance();
		em = runner.entityManager;

		setupStage();
		hookEvents();
	}
	
	/**
	 * An auxiliary function that hooks certain methods to global events.
	 */
	private void hookEvents() {
		runner.mediator.subscribe("registerwindow.open", new PubSubHandler() {
			@Override
			public void exec(Object... args) {
				stage.show();
			}
		});
		runner.mediator.subscribe("registerwindow.close", new PubSubHandler() {
			@Override
			public void exec(Object... args) {
				stage.close();
			}
		});
	}
	
	/**
	 * Verifies if the stage already exists and, if it doesn't, it creates it. If it already exists, nothing happens.
	 */
	private void setupStage() {
		if (stage != null) return;
		
		stage = new Stage();
		stage.setHeight(250);
		stage.setWidth(300);
		
		GridPane gridMain = new GridPane();
        gridMain.setAlignment(Pos.CENTER);
        gridMain.setHgap(10);
        gridMain.setVgap(10);
        gridMain.setPadding(new Insets(10, 10, 10, 10));
        
        Scene sc = new Scene(gridMain, 250, 300);
        stage.setScene(sc);
        
        Text scenetitle = new Text("Choose a username and a password");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 13));
        gridMain.add(scenetitle, 0, 0, 2, 1);

        Label newName = new Label("New User Name:");
        gridMain.add(newName, 0, 1);

        userField = new TextField();
        gridMain.add(userField, 1, 1);
        
        Label newPass = new Label("New Password:");
        gridMain.add(newPass, 0, 2);

        passField = new TextField();
        gridMain.add(passField, 1, 2);
        
        Button btn3 = new Button("Create");
        HBox hbBtn3 = new HBox(10);
        hbBtn3.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn3.getChildren().add(btn3);
        gridMain.add(hbBtn3, 1, 4);
        
        btn3.setOnAction(this);

	}

	@Override
	/**
	 * Adds a new user, unless it exists already.
	 */
	public void handle(ActionEvent arg0) {
		try {
			String user = userField.getText(), pass = passField.getText();

    		if (!user.matches("^[a-zA-Z0-9\\.-_]+$")) {
    			throw new InvalidUserException("The username is invalid");
    		}
    		if (!pass.matches("^[a-zA-Z0-9]+$")) {
    			throw new InvalidUserException("The password is invalid");
    		}
			
			Query q = em.createQuery("SELECT u FROM User u WHERE u.username =\'" + user + "\'");

    		List<User> foundUsers = q.getResultList();
    		System.out.println(foundUsers.size());
    		if (foundUsers.size() == 0){
    			User u = foundUsers.get(0);
				
				em.getTransaction().begin();
				
				User newUser = new User();
				newUser.setPassword(pass);
				newUser.setUsername(user);
				
				em.persist(newUser);
				
				em.getTransaction().commit();
				
				showMessage("User successfully added!");
    		} else {
    			throw new InvalidUserException("This user already exists!");
    		}
		}
		catch (InvalidUserException e) {
			showMessage(e.getMessage());
			System.out.println(e);
		}
		catch (Exception e) {
			showMessage("An error has occurred!");
			System.out.println(e);
		}
	}
	
	/**
	 * Shows an alert window that contains a message.
	 * @param message The string that will show in the message.
	 */
	void showMessage(String message) {
		(new AlertWindow()).message(message).handle(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				runner.mediator.publish("registerwindow.close");
			}
		}).show();
	}
	
}
