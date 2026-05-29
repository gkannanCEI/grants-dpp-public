/**
 * Represents a user returned from the backend after authentication.
 * Used as the typed contract for AuthService and AuthInterceptor.
 */
export interface User {
  id: number;
  username: string;
  email: string;
  roles: string[];
  /** JWT access token — present when the backend issues tokens */
  token?: string;
  /** Auth type — 'Bearer' for JWT, 'Basic' for HTTP Basic */
  type?: string;
}
