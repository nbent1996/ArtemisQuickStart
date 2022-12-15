import datatypes.WsResponse;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uy.gub.dgi.cfe.CFEDocument;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class test {
    private Logger log = LogManager.getLogger(test.class);
    @BeforeEach
    public void setUp(){

    }
    @Test
    public void serializar() throws IOException, ClassNotFoundException {
        WsResponse response = new WsResponse(true, "Mensaje de prueba");
        String serializado = Util.serializeResponse(response);
        assertEquals("rO0ABXNyABRkYXRhdHlwZXMuV3NSZXNwb25zZVe7EQOXGk8IAgAEWgAJcmVzdWx0YWRvTAACZXh0ABVMamF2YS9sYW5nL0V4Y2VwdGlvbjtMAAxtZW5zYWplRXJyb3J0ABJMamF2YS9sYW5nL1N0cmluZztMAAltZW5zYWplT2txAH4AAnhwAXBwdAARTWVuc2FqZSBkZSBwcnVlYmE=", serializado);
        WsResponse responseB = Util.deserializeResponse(serializado);
        assertEquals(responseB.getMensajeOk(), "Mensaje de prueba");
        assertTrue(responseB.getResultado() == true);
        log.info(responseB.getResultado() + " // " + responseB.getMensajeOk());
    }
    @Test
    public void enviarMensajePruebaJVMtoArtemis() throws NamingException, JMSException {
        /*Snippet para conexión desde JVM local de java*/
        /*IMPORTANTE: si se va a probar conectar una jvm local directo al artemis, habilitar el jndi.properties cambiandole la extensión en la carpeta resources*/
        final Hashtable jndiProperties = new Hashtable();
        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
        jndiProperties.put(Context.PROVIDER_URL, "tcp://localhost:61616");
        Context context = new InitialContext(jndiProperties);
        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
        Connection con = connectionFactory.createConnection();
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        con.start();
        Queue queue = (Queue) context.lookup("COREtoEXT");
        MessageProducer messageProducer = session.createProducer(queue);
        TextMessage message = session.createTextMessage("MENSAJE DE PRUEBA 1");
        messageProducer.send(message);
        con.close();
        session.close();
        context.close();
        /*Observar en el servidor si el consumer logueó este MENSAJE DE PRUEBA 1*/
    }
    @Test
    public void testCDATAFunctions() throws XmlException, IOException {
        CFEDocument cfe = Util.getCFEfromXML("src/main/resources/cfeDefType_v1.xml");
        assertEquals(cfe.getCFE().getEFact().getEncabezado().getIdDoc().getFchEmis().toString(), "2012-10-17" );
        /*Obtengo un CfeDocument desde xml y muestro un dato que existe dentro de ese CFE*/

        String file = Util.FILEtoString("src/main/resources/cfeDefType_v1.xml");
        CFEDocument cfe2 = Util.getCFEfromCDATA(Util.XMLtoCDATA(file));
        assertEquals(cfe.getCFE().getEFact().getEncabezado().getIdDoc().getFchEmis().toString(), "2012-10-17" );
        /*Obtengo un CfeDocument desde CDATA y muestro un dato que existe dentro de ese CFE*/
    }
    @Test
    public void testCDATAFunctionsB() throws XmlException, IOException{
        XmlObject o = Util.getXmlObjectfromXML("src/main/resources/cfeDefType_v1.xml");
        CFEDocument cfe = (CFEDocument) o;
        assertEquals(cfe.getCFE().getEFact().getEncabezado().getIdDoc().getFchEmis().toString(), "2012-10-17" );
        /*Obtengo un CfeDocument desde xml y muestro un dato que existe dentro de ese CFE*/

        String file = Util.FILEtoString("src/main/resources/cfeDefType_v1.xml");
        XmlObject ob = Util.getXmlObjectfromCDATA(Util.XMLtoCDATA(file));
        CFEDocument cfe2 = (CFEDocument) ob;
        assertEquals(cfe.getCFE().getEFact().getEncabezado().getIdDoc().getFchEmis().toString(), "2012-10-17" );
        /*Obtengo un CfeDocument desde CDATA y muestro un dato que existe dentro de ese CFE*/

    }

}
