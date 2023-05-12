package dk.sdu.mmmi.backendforfrontend.service.exceptions;

public class ExpiredException extends RuntimeException{
    public ExpiredException(String message) {
        super(message);
    }
}
