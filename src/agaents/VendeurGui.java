package agaents;

import org.omg.CORBA.PUBLIC_MEMBER;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VendeurGui extends Application {
     protected VendeurAgent vendeurAgent;
     protected ObservableList<String >observableList;
     AgentContainer agentContainer ;
	public static void main(String[] args) {
		launch(args);
	}
	public void startContainer() throws Exception {
		Runtime runtime =Runtime.instance();
		ProfileImpl profileImpl=  new  ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
	  agentContainer = runtime.createAgentContainer(profileImpl);
		
		
		agentContainer.start();
		
		
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		startContainer();
		primaryStage.setTitle("Vendeur");
		BorderPane borderPane = new BorderPane();
		VBox vBox= new VBox();
		HBox hBox= new HBox();
		Label label = new Label("Agent name :");
		 TextField textFieldAgentname= new TextField();
		 Button buttonDeploy=new Button("Deploy");
		 hBox.getChildren().addAll(label,textFieldAgentname,buttonDeploy);
		hBox.setPadding(new Insets(10));
		hBox.setSpacing(10);
		borderPane.setTop(hBox);
		observableList=FXCollections.observableArrayList();
		ListView<String>listView= new ListView<String>(observableList);
		vBox.getChildren().add(listView);
		borderPane.setCenter(vBox);
		Scene scene= new Scene(borderPane,400,300);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		buttonDeploy.setOnAction((evt)->{
			try {
				String name =textFieldAgentname.getText();
				AgentController agentController=agentContainer.createNewAgent(name,"agaents.VendeurAgent"  , new Object[] {this});
				agentController.start();
			} catch (StaleProxyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});
		
		
	}
	
	public void logMessage(ACLMessage aclMessage) {
		Platform.runLater(()->{
			observableList.add(aclMessage.getContent()
					+" , "+aclMessage.getSender().getName()
					);
		});
	
		
		
	}

}
