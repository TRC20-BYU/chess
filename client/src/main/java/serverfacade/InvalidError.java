package serverfacade;

public class InvalidError extends RuntimeException {
    public InvalidError(String message) {
        super(message);
    }
}
