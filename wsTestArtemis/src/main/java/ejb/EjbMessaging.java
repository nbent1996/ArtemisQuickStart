package ejb;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import util.Util;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.io.Serializable;
import java.util.Date;

@Stateless
@WebService(serviceName="EjbMessaging")
@SOAPBinding(style=SOAPBinding.Style.RPC)
@Remote(RemoteEjbMessaging.class)
public class EjbMessaging implements RemoteEjbMessaging {
    private static final Logger log = LogManager.getLogger(EjbMessaging.class.toString());
    private Level logInfo = Level.forName("INFO", 550);

    @Inject
    @JMSConnectionFactory("java:/RemoteJmsXA")
    @JMSPasswordCredential(userName="admin", password="admin1234")
    private JMSContext context;

    @Resource(lookup = "java:/COREtoEXT")
    private Queue q_COREtoEXT;
    @Resource(lookup = "java:/EXTtoCORE")
    private Queue q_EXTtoCORE;
    @Resource(lookup = "java:/EXTtoEXT")
    private Queue q_EXTtoEXT;

    @WebMethod(operationName="enviarMensajesCola")
    @Override
    public boolean enviarMensajesCola(@WebParam(name = "QueueJNDI") String QueueJNDI, @WebParam(name = "cantMensajes") int cantMensajes) {
        int cantEnviados=0;
        try{
            cantEnviados = sendMessages(context, QueueJNDI, cantMensajes);
            if(cantEnviados == cantMensajes){
                log.log(logInfo, "SE ENVIARON TODOS LOS MENSAJES EXITOSAMENTE. // " + cantEnviados + " DE " + cantMensajes);
            }else{
                throw new Exception("ERROR EN EL ENVIO DE MENSAJES, SE LLEGARON A ENVIAR " + cantEnviados + " DE " + cantMensajes);
            }
        }catch (Exception ex) {
            log.log(logInfo, ex.getMessage());
        }
        return true;
    }
    @WebMethod(operationName="enviarMensajesConFilter")
    @Override
    public boolean enviarMensajesConFilter(@WebParam(name ="QueueJNDI") String QueueJNDI, @WebParam(name="cantMensajes") int cantMensajes, @WebParam(name="filter") String filter){
        try{
            sendMessagesFilter(context, QueueJNDI, cantMensajes, filter);
        } catch (Exception ex) {
            log.log(logInfo,ex.getLocalizedMessage());
        }
        return true;
    }
    @WebMethod(operationName="enviarCDATACola")
    @Override
    public boolean enviarCDATACola(@WebParam(name = "QueueJNDI") String QueueJNDI, @WebParam(name = "CDATA") String CDATA) {
        try{
            Queue q = selectorQueue(QueueJNDI);
            XmlObject cfe = Util.getXmlObjectfromCDATA(CDATA);
            ObjectMessage objectMessage = context.createObjectMessage();
            objectMessage.setObject((Serializable) cfe);
            JMSProducer producer = context.createProducer();
            producer.send(q, objectMessage);
        } catch (Exception ex) {
            log.log(logInfo,ex.getLocalizedMessage());
        }
        return true;
    }
    private int sendMessages(JMSContext context, String QueueJNDI, int cantMensajes){
        log.log(logInfo, "Cola seleccionada: " + QueueJNDI);
        Queue queue = selectorQueue(QueueJNDI);
        int cantEnviados = 0;
        for(int i = 0; i<cantMensajes;i++){
            String mensaje = new Date() + " // Codigo de mensaje = " + Util.generateRandomString() + " // Nro de iteración: " + i;
            JMSProducer producer = context.createProducer();
            producer.send(queue, mensaje);
            cantEnviados ++;
            log.log(logInfo, "Cant de mensajes enviados:" + cantEnviados + " // Mensaje a enviar::: " + mensaje);
        }
        return cantEnviados;
    }
    private void sendMessagesFilter(JMSContext context, String QueueJNDI, int cantMensajes, String TypeMessage) throws JMSException {
        log.log(logInfo, "Cola seleccionada: " + QueueJNDI);
        Queue queue = selectorQueue(QueueJNDI);
        for(int i = 0; i<cantMensajes;i++){
            ObjectMessage objectMessage = context.createObjectMessage();
            objectMessage.setStringProperty("TypeMessage",TypeMessage);
            String mensaje = new Date() + " // Codigo de mensaje = " + Util.generateRandomString() + " // Nro de iteración: " + i;
            log.log(logInfo, "Iteración nro:" + i + " // Mensaje a enviar::: " + mensaje);
            objectMessage.setStringProperty("Mensaje", mensaje);
            JMSProducer producer = context.createProducer();
            producer.send(queue, objectMessage);
        }
    }
    private Queue selectorQueue(String QueueJNDI){
        switch(QueueJNDI){
            case "COREtoEXT":
                return q_COREtoEXT;
            case "EXTtoCORE":
                return q_EXTtoCORE;
            case "EXTtoEXT":
                return q_EXTtoEXT;
        }
        return null;
    }

}