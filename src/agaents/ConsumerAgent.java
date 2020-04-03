package agaents;

import containers.ConsumerContainer;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

public class ConsumerAgent  extends GuiAgent {
	
	private transient ConsumerContainer gui;
	
	//transient : n'est pas serialisable 
	
 @Override
protected void setup() {
	 if(getArguments().length==1) {
		 gui=(ConsumerContainer) getArguments()[0];
		 gui.setConsumerAgent(this); 
	 }
	
	
	
	ParallelBehaviour parallelBehaviour= new ParallelBehaviour();
    addBehaviour(parallelBehaviour);
   parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
		
		@Override
		public void action() {
			ACLMessage aclMessage =receive();
			if(aclMessage!=null) {
				switch (aclMessage.getPerformative()) {
				case ACLMessage.CONFIRM:
					gui.logMessage(aclMessage);
					break;

				default:
					break;
				}
				
				
			}
			else block();
			
		}
	});
	
}
 @Override
	protected void beforeMove() {
		System.out.println("**************");
		System.out.println("Avant Migration....");
		System.out.println("**************");
	}
  @Override
	protected void afterMove() {
	  System.out.println("**************");
		System.out.println("Après Migration....");
		System.out.println("**************");
	}
  @Override
	protected void takeDown() {
	  System.out.println("**************");
		System.out.println("Je suis en trains de mourir....");
		System.out.println("**************");
	}
@Override
public void onGuiEvent(GuiEvent params) {
	if (params.getType()==1) {
		String livre= params.getParameter(0).toString();
		ACLMessage aclMessage= new ACLMessage(ACLMessage.REQUEST);
		aclMessage.setContent(livre);
		aclMessage.addReceiver(new AID("ACHETEUR",AID.ISLOCALNAME));
		send(aclMessage);
		
	}
	
}
}
