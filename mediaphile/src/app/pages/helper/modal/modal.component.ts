import {Component, ElementRef, Injectable, Input, OnInit, ViewChild} from '@angular/core';
import {NgbModal, NgbModalRef} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss']
})

@Injectable()
export class ModalComponent {
  @Input() message: string = 'Message here...'; // we can set the default value also
  @Input() title: string = 'default title';

  @ViewChild('content', { static: true }) input: ElementRef;

  constructor(public bsModalRef: NgbModal) {}
}
