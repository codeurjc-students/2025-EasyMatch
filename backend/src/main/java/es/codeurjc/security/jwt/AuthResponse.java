package es.codeurjc.security.jwt;

public class AuthResponse {

	private Status status;
	private String message;
	private String error;
	private String authorities;
	

	public enum Status {
		SUCCESS, FAILURE
	}

	public AuthResponse() {
	}

	public AuthResponse(Status status, String message) {
		this.status = status;
		this.message = message;
	}
	public AuthResponse(Status status, String message, String authorities) {
		this.status = status;
		this.message = message;
		this.authorities = authorities;
		
	}

	public AuthResponse(Status status, String message, String authorities, String error) {
		this.status = status;
		this.message = message;
		this.error = error;
		this.authorities = authorities;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	

	@Override
	public String toString() {
		return "LoginResponse [status=" + status + ", message=" + message + ", error=" + error + "autorities=" + authorities + "]";
	}

	public String getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}


}
