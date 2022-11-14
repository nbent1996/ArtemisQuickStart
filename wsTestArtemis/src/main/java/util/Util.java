package util;

import datatypes.WsParam;
import datatypes.WsResponse;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import uy.gub.dgi.cfe.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import javax.xml.soap.*;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.util.*;

public class Util {
    /*Utilidades String*/
    public static String generateRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 8;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public static String serializeResponse(WsResponse response) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(response);
        os.close();
        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }
    public static WsResponse deserializeResponse(String serializedObject) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(serializedObject);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        WsResponse response = (WsResponse) ois.readObject();
        ois.close();
        return response;
    }

    /*Utilidades WS*/
    public static void clienteWS(String operationName, ArrayList<WsParam> parametros) throws SOAPException {
        Service service = Service.create(Constants.service_name);
        service.addPort(Constants.port_name, SOAPBinding.SOAP11HTTP_BINDING, Constants.endpointAddress);
        /** Create a Dispatch instance from a service.**/
        Dispatch<SOAPMessage> dispatch = service.createDispatch(Constants.port_name,
                SOAPMessage.class, Service.Mode.MESSAGE);

        /** Create SOAPMessage request. **/
        // compose a request message
        MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        // Create a message.  This example works with the SOAPPART.
        SOAPMessage request = mf.createMessage();
        SOAPPart part = request.getSOAPPart();

        // Obtain the SOAPEnvelope and header and body elements.
        SOAPEnvelope env = part.getEnvelope();
        SOAPHeader header = env.getHeader();
        SOAPBody body = env.getBody();

        // Construct the message payload.
        SOAPElement operation = body.addChildElement(operationName, "ns1",
                "http://ejb/");
        for(WsParam parametro: parametros){
            SOAPElement p = operation.addChildElement(parametro.getNombre());
            p.addTextNode(parametro.getValor());
        }
        request.saveChanges();

        /** Invoke the service endpoint. **/
        SOAPMessage response = dispatch.invoke(request);
        /** Process the response. **/
        SOAPBody bodyFinal = response.getSOAPBody();
        SOAPElement elemento = getFirstBodyElement(bodyFinal);
    }

    public static SOAPElement getFirstBodyElement(SOAPBody body) {
        for (Iterator<?> iterator = body.getChildElements(); iterator.hasNext(); ) {
            Object child = iterator.next();
            if (child instanceof SOAPElement) {
                return (SOAPElement) child;
            }
        }
        return null;
    }

    /*XMLBEANS*/
    /*Funciones especificas para CFEDocument*/
    public static CFEDocument getCFEfromCDATA (String CDATAcfe) throws XmlException {
        String xml = Util.CDATAtoXML(CDATAcfe);
        CFEDocument cfe = CFEDocument.Factory.parse(xml);
        return cfe;
    }
    public static CFEDocument getCFEfromXML(String rutaXml) throws IOException, XmlException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        CharsetEncoder encoder1252 = Charset.forName("Windows-1252").newEncoder();
        System.out.println(encoder1252.getClass().getCanonicalName());
        BufferedReader br = new BufferedReader(new FileReader(rutaXml));
        StringWriter sw = new StringWriter();
        while (br.ready()) {
            sw.write(br.readLine()+"\n");
        }
        encoder1252.onUnmappableCharacter(CodingErrorAction.REPLACE);
        System.out.println(encoder1252.replacement()[0]);
        ByteBuffer bb1252= encoder1252.encode(CharBuffer.wrap(sw.getBuffer()));
        String s1252 = new String(bb1252.array(),Charset.forName("Windows-1252"));
        CFEDocument cfe = CFEDocument.Factory.parse(s1252);
        return cfe;
    }
    /*Funciones especificas para CFEDocument*/

    /*Funciones para trabajar con XmlObject y no atarme a un tipo especifico de Document*/
    public static XmlObject getXmlObjectfromCDATA(String CDATAcfe) throws XmlException {
        String xml = Util.CDATAtoXML(CDATAcfe);
        XmlObject doc = XmlObject.Factory.parse(xml);
        return doc;
    }
    public static XmlObject getXmlObjectfromXML(String rutaXml) throws XmlException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        CharsetEncoder encoder1252 = Charset.forName("Windows-1252").newEncoder();
        System.out.println(encoder1252.getClass().getCanonicalName());
        BufferedReader br = new BufferedReader(new FileReader(rutaXml));
        StringWriter sw = new StringWriter();
        while (br.ready()) {
            sw.write(br.readLine()+"\n");
        }
        encoder1252.onUnmappableCharacter(CodingErrorAction.REPLACE);
        System.out.println(encoder1252.replacement()[0]);
        ByteBuffer bb1252= encoder1252.encode(CharBuffer.wrap(sw.getBuffer()));
        String s1252 = new String(bb1252.array(),Charset.forName("Windows-1252"));
        XmlObject doc = XmlObject.Factory.parse(s1252);
        return doc;
    }
    /*Funciones para trabajar con XmlObject y no atarme a un tipo especifico de Document*/


    public static String CDATAtoXML(String CDATA){
        CDATA = CDATA.replace("<![CDATA[", "");
        CDATA = CDATA.replace("]]>", "");
        CDATA = CDATA.replace("<![cdata[", "");
        return CDATA;
    }
    public static String XMLtoCDATA(String xml){
        return "<![CDATA[" + xml + "]]>";
    }
    public static String FILEtoString(String rutaFile) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        CharsetEncoder encoder1252 = Charset.forName("Windows-1252").newEncoder();
        System.out.println(encoder1252.getClass().getCanonicalName());
        BufferedReader br = new BufferedReader(new FileReader(rutaFile));
        StringWriter sw = new StringWriter();
        while (br.ready()) {
            sw.write(br.readLine()+"\n");
        }
        encoder1252.onUnmappableCharacter(CodingErrorAction.REPLACE);
        System.out.println(encoder1252.replacement()[0]);
        ByteBuffer bb1252= encoder1252.encode(CharBuffer.wrap(sw.getBuffer()));
        String s1252 = new String(bb1252.array(),Charset.forName("Windows-1252"));
        return s1252;
    }
}
