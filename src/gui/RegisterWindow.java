package gui;

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

import model.User;

public class RegisterWindow implements EventHandler<ActionEvent> {
	
	private EntityManager em;
	private TextField userField, passField;
	private Stage stage;

	public RegisterWindow(EntityManager em) {
		
		System.out.println(em);
		this.em = em;
		setupStage();

        stage.show();
	}
	
	void setupStage() {
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
	public void handle(ActionEvent arg0) {
		try {
			String user = userField.getText(), pass = passField.getText();
			System.out.println(em.getTransaction());
			em.getTransaction().begin();
			
			User newUser = new User();
			newUser.setPassword(pass);
			newUser.setUsername(user);
			
			em.persist(newUser);
			
			em.getTransaction().commit();
			em.close();
			
			showMessage("User successfully added!");
		}
		catch (Exception e) {
			showMessage("An error has occurred!");
		}
	}
	
	void showMessage(String message) {
		(new AlertWindow()).message(message).handle(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				stage.hide();
			}
		}).show();
	}
	
}
