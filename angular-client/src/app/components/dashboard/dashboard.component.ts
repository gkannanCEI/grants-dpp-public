import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-container">
      <div class="dashboard-header">
        <h1>Dashboard</h1>
        <p class="subtitle">Welcome, {{ username }} ({{ role }}). Overview of your current activities.</p>
      </div>

      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon blue">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
          </div>
          <div class="stat-info">
            <span class="label">Total Funding</span>
            <span class="value">$1,240,000</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon green">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><polyline points="16 11 18 13 22 9"/></svg>
          </div>
          <div class="stat-info">
            <span class="label">Active Members</span>
            <span class="value">128</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon orange">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
          </div>
          <div class="stat-info">
            <span class="label">Pending Reviews</span>
            <span class="value">15</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon purple">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
          </div>
          <div class="stat-info">
            <span class="label">Upcoming Deadlines</span>
            <span class="value">3</span>
          </div>
        </div>
      </div>

      <div class="dashboard-content">
        <div class="content-card">
          <h3>Recent Activity</h3>
          <p>Logged in as: <strong>{{ role }}</strong></p>
          <div class="role-actions" [ngSwitch]="role">
            <p *ngSwitchCase="'CID Staff'">Staff specific actions available: Approve Grants, Review Reports.</p>
            <p *ngSwitchCase="'Member'">Member specific actions available: View Grant Status, Submit Reports.</p>
            <p *ngSwitchCase="'Sponsor'">Sponsor specific actions available: Manage Funding, Track Projects.</p>
            <p *ngSwitchDefault>General access available.</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container { padding: 0; }
    .dashboard-header { margin-bottom: 30px; }
    .subtitle { color: #8c8c8c; margin-top: 5px; }
    
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
    }

    .stat-card {
      background: white;
      padding: 20px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
      border: 1px solid #f0f0f0;
    }

    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;
    }

    .stat-icon svg {
      width: 24px;
      height: 24px;
    }

    .blue { background-color: #e6f7ff; color: #1890ff; }
    .green { background-color: #f6ffed; color: #52c41a; }
    .orange { background-color: #fff7e6; color: #fa8c16; }
    .purple { background-color: #f9f0ff; color: #722ed1; }

    .stat-info {
      display: flex;
      flex-direction: column;
    }

    .label { font-size: 14px; color: #8c8c8c; }
    .value { font-size: 20px; font-weight: 600; color: #262626; }

    .content-card {
      background: white;
      padding: 24px;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
      border: 1px solid #f0f0f0;
      min-height: 200px;
    }

    h3 { margin-top: 0; margin-bottom: 20px; font-size: 16px; }
    .role-actions { margin-top: 15px; padding: 10px; background: #fafafa; border-radius: 4px; }
  `]
})
export class DashboardComponent implements OnInit {
  username: string = '';
  role: string = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername();
    this.role = this.authService.getUserRole();
  }
}
