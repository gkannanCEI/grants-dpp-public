import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-error-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.css']
})
export class ErrorPageComponent implements OnInit {
  errorCode: string = '500';
  errorTitle: string = 'Unexpected Error';
  errorMessage: string = 'Something went wrong on our end.';
  errorDetail: string = 'Our team has been notified and is working to fix the issue. Please try again later.';
  timestamp: Date = new Date();

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['code']) {
        this.errorCode = params['code'];
        this.setErrorDetails(this.errorCode);
      }
      if (params['message']) {
        this.errorMessage = params['message'];
      }
    });
  }

  private setErrorDetails(code: string): void {
    switch (code) {
      case '401':
        this.errorTitle = 'Unauthorized';
        this.errorMessage = 'You are not authorized to view this page.';
        this.errorDetail = 'Please log in with valid credentials to access this section.';
        break;
      case '403':
        this.errorTitle = 'Access Denied';
        this.errorMessage = 'You do not have permission to access this resource.';
        this.errorDetail = 'If you believe this is an error, please contact your system administrator.';
        break;
      case '404':
        this.errorTitle = 'Page Not Found';
        this.errorMessage = "The page you're looking for doesn't exist.";
        this.errorDetail = 'It might have been moved or deleted. Please check the URL and try again.';
        break;
      case '503':
        this.errorTitle = 'Service Unavailable';
        this.errorMessage = 'The server is temporarily unable to handle your request.';
        this.errorDetail = 'This may be due to maintenance or capacity issues. Please try again in a few minutes.';
        break;
      case 'APP':
        this.errorTitle = 'Application Error';
        this.errorMessage = 'A critical application error occurred.';
        this.errorDetail = 'The application encountered an internal problem and could not continue.';
        break;
      default:
        this.errorTitle = 'Unexpected Error';
        this.errorMessage = 'An unexpected error has occurred.';
        this.errorDetail = 'Our systems are experiencing some difficulties. Please try again later.';
        break;
    }
  }

  goHome(): void {
    this.router.navigate(['/dashboard']);
  }

  retry(): void {
    window.history.back();
  }
}
