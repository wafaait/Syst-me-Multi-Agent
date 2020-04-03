package agaents;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import sun.security.provider.certpath.PKIXTimestampParameters;

public class AcheteurAgent  extends GuiAgent {
	
	protected AcheteurGui Gui;
	protected AID[] vendeurs ;
	
 @Override
protected void setup() {
	if(getArguments().length==1) {
		Gui=(AcheteurGui)getArguments()[0];
		Gui.acheteurAgent=this;
		
	}
	
	ParallelBehaviour parallelBehaviour= new ParallelBehaviour();
	addBehaviour(parallelBehaviour);
	parallelBehaviour.addSubBehaviour(new TickerBehaviour(this,50000) {
		
		@Override
		protected void onTick() {
			DFAgentDescription dfAgentDescription= new DFAgentDescription();
			ServiceDescription serviceDescription = new ServiceDescription();
			serviceDescription.setType("transaction");
			serviceDescription.setName("vente-livres");
			dfAgentDescription.addServices(serviceDescription);
			try {
				DFAgentDescription[]results=DFService.search(myAgent, dfAgentDescription);
				vendeurs= new AID[results.length];
				for (int i = 0; i < vendeurs.length; i++) {
					vendeurs[i]= results[i].getName();
				}
			} catch (FIPAException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	});
	parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
	    private int  counter=0;
	    private List<ACLMessage>replies= new ArrayList<ACLMessage>();
		@Override
		public void action() {
		     MessageTemplate messageTemplate=MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
		    		 MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
		    				 MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.AGREE),
		    						 MessageTemplate.MatchPerformative(ACLMessage.REFUSE)))
		    		 );
			ACLMessage aclMessage=receive(messageTemplate);
		     //MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST)	;	
			//ACLMessage  aclMessage= receive();
			if (aclMessage!=null) {
				switch (aclMessage.getPerformative()) {
				
				case ACLMessage.REQUEST :
					String livre=aclMessage.getContent();
					ACLMessage aclMessage2 = new ACLMessage(ACLMessage.CFP);
					aclMessage2.setContent(livre);
					for (AID aid:vendeurs) {
						aclMessage2.addReceiver(aid);
					}
					send(aclMessage2);
					break;
				case ACLMessage.PROPOSE :	
					 ++counter;
					 replies.add(aclMessage);
					 if(counter==vendeurs.length) {
						 ACLMessage meilleurOffre=replies.get(0);
						 double mini= Double.parseDouble(meilleurOffre.getContent());
						 for (ACLMessage offre:replies) {
							double price =Double.parseDouble(offre.getContent());
							if(price<mini) {
								meilleurOffre=offre;
								mini=price;
							}
						}
						 ACLMessage aclMessageAccept=meilleurOffre.createReply();
						 aclMessage.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
						 send(aclMessageAccept);
						 
					 }
					
					break;
				case ACLMessage.AGREE :	
					ACLMessage aclMessage3= new ACLMessage(ACLMessage.CONFIRM);
					aclMessage3.addReceiver(new AID("Consumer",AID.ISLOCALNAME));
				    aclMessage3.setContent(aclMessage.getContent());
				    send(aclMessage3);
					
					break;
				case ACLMessage.REFUSE :	
					break;

				default:
					break;
				}
				String livreString = aclMessage.getContent();
				Gui.logMessage(aclMessage);
				ACLMessage reply= aclMessage.createReply();
				reply.setContent("ok pour "+aclMessage.getContent());
				send(reply);
				ACLMessage aclMessage2= new ACLMessage(ACLMessage.CFP);
				//CFP : Call for proposal 
				aclMessage2.setContent(livreString);
				aclMessage2.addReceiver(new AID("VENDEUR",AID.ISLOCALNAME));
				send(aclMessage2);
			}
			else block();
		}
	});
}
	
	
	@Override
	protected void onGuiEvent(GuiEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
