/**
 * Represents a DPP Program reservation record.
 * Maps directly to the Reservation entity on the backend.
 */
export interface Reservation {
  id?: number;
  name?: string;
  description?: string;
  status?: string;
  createdAt?: Date;
  updatedAt?: Date;
}
