import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {
  private isOpenSubject = new BehaviorSubject<boolean>(true);
  public isOpen$ = this.isOpenSubject.asObservable();

  toggleSidebar(): void {
    this.isOpenSubject.next(!this.isOpenSubject.value);
  }

  isOpen(): boolean {
    return this.isOpenSubject.value;
  }
}
