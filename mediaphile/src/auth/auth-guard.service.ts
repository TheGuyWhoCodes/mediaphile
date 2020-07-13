import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {InfoService} from "../info.service";
import {Observable} from "rxjs";
import {LoginStatus} from "./login.status";

@Injectable()
export class AuthGuardService implements CanActivate {
  constructor(public infoSvc: InfoService, public router: Router, private loginStatus: LoginStatus) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot):  Observable<boolean> | Promise<boolean> | boolean {
    return this.getLoginStatus();
  }

  async getLoginStatus() {
    let responseData = this.getStatus().then(x => {
      if(x.loggedIn) {
        this.loginStatus.setValues(x.id, x.url, x.loggedIn)
      } else {
        this.loginStatus.setValues(x.id, x.url, x.loggedIn)
        this.router.navigate(["/login"]);
      }
      return x.loggedIn;
    })
    return responseData;
  }
  async getStatus() {
    return await this.infoSvc.login().toPromise();
  }
}
