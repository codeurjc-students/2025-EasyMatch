export interface AuthResponse {
	status: "SUCCESS" | "FAILURE";
	message: string;
	error?: string;
}