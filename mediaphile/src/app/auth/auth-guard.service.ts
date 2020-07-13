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
    let responseData = this.getStatus().then(authState => {
      if(authState.loggedIn) {
        this.loginStatus.setValues(authState.id, authState.url, authState.loggedIn)
      } else {
        this.loginStatus.setValues(authState.id, authState.url, authState.loggedIn)
        this.router.navigate(["/login"]);
      }
      return authState.loggedIn;
    })
    return responseData;
  }
  async getStatus() {
    return await this.infoSvc.login().toPromise();
  }
}
