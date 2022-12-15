package ejb;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;

@Remote
public interface RemoteEjbMessaging {
    @WebMethod(operationName="enviarMensajesCola")
    boolean enviarMensajesCola(@WebParam(name = "QueueJNDI") String QueueJNDI, @WebParam(name = "cantMensajes") int cantMensajes);

    @WebMethod(operationName="enviarMensajesConFilter")
    boolean enviarMensajesConFilter(@WebParam(name = "QueueJNDI") String QueueJNDI, @WebParam(name = "cantMensajes") int cantMensajes, @WebParam(name = "filter") String filter);
}
