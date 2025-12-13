export interface AuthResponse {
	status: "SUCCESS" | "FAILURE";
	message: string;
	authorities: string;
}