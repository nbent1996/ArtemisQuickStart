package consumers;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.ejb3.annotation.ResourceAdapter;


@MessageDriven(name = "COREtoEXTConsumer", activationConfig={
        @ActivationConfigProperty(propertyName="destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue ="java:/COREtoEXT"),
        @ActivationConfigProperty(propertyName="acknowledgeMode", propertyValue="Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "user", propertyValue="admin")
})
@ResourceAdapter(value = "remote-artemis")
public class COREtoEXT implements MessageListener {
    private static final Logger log = LogManager.getLogger(COREtoEXT.class);

    @Resource
    private MessageDrivenContext context;

    @Override
    public void onMessage(Message message) {
        TextMessage msg = null;
        try{
            if(message instanceof TextMessage){
                msg = (TextMessage) message;
                /*Recibido MDB*/
                log.info("CONSUMER COREtoEXT RECIBE MENSAJE sin filter: " + msg.getText() );
            }else{
                System.out.println("El mensaje no es del tipo texto");
            }
        } catch (Throwable t){
            System.out.println("Exception: " + t.getLocalizedMessage());
        }
    }

}

