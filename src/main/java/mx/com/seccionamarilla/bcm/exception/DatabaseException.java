package mx.com.seccionamarilla.bcm.exception;

public class DatabaseException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7603369500852696151L;

	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}
}
