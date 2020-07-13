import {BehaviorSubject} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable()
export class LoginStatus {

  private _accountId = new BehaviorSubject("");
  private _url = new BehaviorSubject("");
  private _status = new BehaviorSubject(false);

  sharedAccountId = this._accountId.asObservable();
  sharedUrl = this._url.asObservable();
  sharedStatus = this._status.asObservable();

  public setValues(id: string, url: string, status: boolean) {
   this._accountId.next(id);
   this._url.next(url);
   this._status.next(status);
  }
}
