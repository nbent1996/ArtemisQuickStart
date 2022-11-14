package datatypes;

import java.io.Serializable;

public class WsResponse implements Serializable {
    boolean resultado;
    Exception ex;
    String mensajeError;
    String mensajeOk;

    public WsResponse(boolean resultado, String mensajeOk) {
        this.resultado = resultado;
        this.mensajeOk = mensajeOk;
    }

    public WsResponse(boolean resultado, Exception ex, String mensajeError) {
        this.resultado = resultado;
        this.ex = ex;
        this.mensajeError = mensajeError;
    }

    public boolean getResultado() {
        return resultado;
    }

    public void setResultado(boolean resultado) {
        this.resultado = resultado;
    }

    public Exception getEx() {
        return ex;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public String getMensajeOk() {
        return mensajeOk;
    }

    public void setMensajeOk(String mensajeOk) {
        this.mensajeOk = mensajeOk;
    }
}
