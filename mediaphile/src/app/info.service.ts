import {Injectable} from "@angular/core";
import {environment} from "../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {map, tap} from "rxjs/operators";
import {Observable, Subscription} from "rxjs";
import {MovieSearchResult} from "./struct/MovieSearchResult";
import {LoginStatusStruct} from "./struct/loginStatusStruct";
import {QueueEntity} from "./struct/queue.entity";
import {LoginStatus} from "./auth/login.status";

@Injectable()

export class InfoService {

  private apiBackendUrl: string = environment.backendEndpoint;
  private getMovieDetailsEndpoint: string = `${this.apiBackendUrl}movies/details`;
  private getMovieSearch: string = `${this.apiBackendUrl}movies/search`;
  private getBookSearch: string = `${this.apiBackendUrl}books/search`;
  private loginStatus: string = `${this.apiBackendUrl}login/status`;
  private postQueueEndpoint: string = `${this.apiBackendUrl}list/entity`;

  constructor(private http: HttpClient, private router: Router, private loginStatusService: LoginStatus) {
  }

  /**
   * searchMovies() endpoint the backend to grab movie search information
   * @param query: users wanted query
   * @param page: page number
   */
  public searchMovies(query: string, page: number) {
    return this.http.get<MovieSearchResult>(this.getMovieSearch, {
      params: {
        "query": query,
        "pageNumber": page.toString()
      }
    })
  }

  public searchBooks(query: string, page: number) {
    return this.http.get(this.getBookSearch, {
      params: {
        "query": query,
        "pageNumber": page.toString()
      }
    })
  }
  public getMovieDetails(id: string) {
    return this.http.get(this.getMovieDetailsEndpoint, {
      params: {
        "id": id,
      }
    })
  }

  public login() {
    return this.http.get<LoginStatusStruct>(this.loginStatus)
  }

  public logout() {
    this.loginStatusService.sharedUrl.subscribe(x => {
      window.location.href = (x);
    })
  }

  public postQueue(posterPath: String, id: String, type: String, title: String, entityType: String, userID: String) {
    return this.http.post(this.postQueueEndpoint, {
      "entityId": id,
      "title": title,
      "type":type,
      "entityType": entityType,
      "artUrl": posterPath,
      "userID": userID
    })
  }

  public getQueue(userID: string, type: string) {
    return this.http.get(this.postQueueEndpoint, {
      params: {
        "userID": userID,
        "entityType": type
      }
    });
  }
}
