import { Reservation } from './reservation.model';

/**
 * Represents a DPP Program funding round.
 * Maps directly to the Round entity on the backend.
 */
export interface Round {
  id?: number;
  roundName?: string;
  programType?: string;
  status?: string;
  roundOpenDate?: Date;
  roundCloseDate?: Date;
  daysToExhaustion?: number;
  totalFunds?: number;
  description?: string;
  reservationVelocityTarget?: string;
  complianceThreshold?: number;
  reservations?: Reservation[];
}
