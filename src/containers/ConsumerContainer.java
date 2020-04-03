package containers;

import agaents.ConsumerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
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


public class ConsumerContainer extends Application {
	protected ConsumerAgent  consumerAgent;
	ObservableList<String>observableList;
 public void setConsumerAgent(ConsumerAgent consumerAgent) {
		this.consumerAgent = consumerAgent;
	}
public static void main(String[] args) throws Exception {
	 launch(args);
	
}
 public void startCotainter() throws Exception {
	 Runtime runtime = Runtime.instance();
		ProfileImpl profileImpl= new ProfileImpl();
		profileImpl.setParameter(ProfileImpl.MAIN_HOST,"localhost");
		AgentContainer container = runtime.createAgentContainer(profileImpl);
		AgentController agentController=container.createNewAgent("Consumer", "agaents.ConsumerAgent", new Object[] {this});
	   agentController.start(); 
	   // this: consumer container reffernce verc l'interface
 }

@Override
public void start(Stage primaryStage) throws Exception {
	startCotainter();
	primaryStage.setTitle("Consumer");
	HBox hBox=new HBox();
	VBox vBox = new VBox();
	vBox.setPadding( new Insets(10));
	
	hBox.setPadding(new Insets(10));
	hBox.setSpacing(10);
	Label label= new Label("livre :");
	TextField textFieldLivre= new TextField();
	Button buttonAcheter= new Button("Acheter");
	BorderPane borderPane= new BorderPane();
	observableList=FXCollections.observableArrayList();
	ListView<String> listViewMessage= new ListView<String>(observableList);
	
	vBox.getChildren().add(listViewMessage);
	hBox.getChildren().addAll(label,textFieldLivre,buttonAcheter);
	borderPane.setTop(hBox);
	borderPane.setCenter(vBox);
	
	Scene scene = new Scene(borderPane,600,400);
	primaryStage.setScene(scene);
	primaryStage.show();
	
	buttonAcheter.setOnAction(evt->{
		String livre=textFieldLivre.getText();
		//observableList.add(livre);
		GuiEvent event = new GuiEvent(this, 1);
		event.addParameter(livre);
		consumerAgent.onGuiEvent(event);
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
