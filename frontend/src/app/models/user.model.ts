export enum UserRole {
  CLIENT = "CLIENT",
  SELLER = "SELLER",
}

export interface User {
  firstName?: string;
  lastName?: string;
  email?: string;
  role?: string;
  avatar?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  password?: string | null;
  role?: string | null;
  avatar: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}
