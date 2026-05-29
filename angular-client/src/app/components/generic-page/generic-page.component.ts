import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NavigationService } from '../../services/navigation.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-generic-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './generic-page.component.html',
  styleUrls: ['./generic-page.component.css']
})
export class GenericPageComponent implements OnInit {
  pageTitle: string = 'Loading...';

  constructor(
    private route: ActivatedRoute,
    private navigationService: NavigationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.route.url.subscribe(url => {
      // Try to get pageId from segments, or fallback to route config path
      let pageId = '';
      if (url && url.length > 0) {
        pageId = url[0].path;
      } else {
        pageId = this.route.snapshot.routeConfig?.path || '';
      }

      if (pageId) {
        this.pageTitle = `Loading ${pageId}...`;
        this.cdr.detectChanges();

        this.navigationService.getPageTitle(pageId).subscribe({
          next: (data) => {
            if (data && data.title) {
              this.pageTitle = data.title;
            } else {
              this.pageTitle = 'Untitled Page';
            }
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Error fetching title:', err);
            this.pageTitle = 'Error loading title';
            this.cdr.detectChanges();
          }
        });
      } else {
        this.pageTitle = 'Page Not Found';
        this.cdr.detectChanges();
      }
    });
  }
}
