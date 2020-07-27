import {Injectable} from "@angular/core";
import {environment} from "../environments/environment";
import {HttpClient, HttpParams} from "@angular/common/http";
import {Router} from "@angular/router";
import {map, tap} from "rxjs/operators";
import {Observable, Subscription, of} from "rxjs";
import {MovieSearchResult} from "./struct/MovieSearchResult";
import {LoginStatusStruct} from "./struct/loginStatusStruct";
import {QueueEntity} from "./struct/queue.entity";
import {LoginStatus} from "./auth/login.status";
import {Review} from "./struct/Review";

@Injectable({
  providedIn: "root"
})
export class InfoService {

  private apiBackendUrl: string = environment.backendEndpoint;
  private getMovieDetailsEndpoint: string = `${this.apiBackendUrl}movies/details`;
  private getMovieSearch: string = `${this.apiBackendUrl}movies/search`;
  private getBookSearch: string = `${this.apiBackendUrl}books/search`;
  private getReviews: string = `${this.apiBackendUrl}reviews`;
  private loginStatus: string = `${this.apiBackendUrl}login/status`;
  private userEndpoint: string = `${this.apiBackendUrl}user`;
  private postQueueEndpoint: string = `${this.apiBackendUrl}list/entity`;
  private followListEndpoint: string = `${this.apiBackendUrl}follow`;
  private getBookDetailsEndpoint: string = `${this.apiBackendUrl}books/details`;
  private getActivityEndpoint: string = `${this.apiBackendUrl}activity/followers`;

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
    });
  }
  public getMovieDetails(id: string) {
    return this.http.get(this.getMovieDetailsEndpoint, {
      params: {
        "id": id,
      }
    });
  }

  public getBookDetails(id: string) {
    return this.http.get(this.getBookDetailsEndpoint, {
      params: {
        "id": id
      }
    })
  }

  public login() {
    return this.http.get<LoginStatusStruct>(this.loginStatus)
  }

  public logout() {
    this.loginStatusService.sharedUrl.subscribe(x => {
      window.location.href = (x);
    });
  }

  public getUser(userId: string) {
    // TODO: Remove placeholder
    if ((['123', '234', '345'].indexOf(userId) >= 0)) {
      return of({
        id: userId,
        email: "other@example.com",
        profilePicUrl: "https://3.bp.blogspot.com/-qDc5kIFIhb8/UoJEpGN9DmI/AAAAAAABl1s/BfP6FcBY1R8/s320/BlueHead.jpg",
        username: "other",
      });
    }
    return this.http.get(this.userEndpoint, {
      params: {
        "id": userId,
      }
    });
  }

  public postQueue(posterPath: String, id: String, type: String, title: String, entityType: String, userId: String) {
    return this.http.post(this.postQueueEndpoint, {
      "mediaId": id,
      "title": title,
      "mediaType":type,
      "listType": entityType,
      "artUrl": posterPath,
      "userId": userId
    });
  }

  public getQueue(userId: string, type: string) {
    return this.http.get<Object[]>(this.postQueueEndpoint, {
      params: {
        "userId": userId,
        "listType": type
      }
    });
  }

  public getReviewsForMedia(id: string, contentType: string) {
    return this.http.get<Review[]>(this.getReviews, {
      params: {
        contentType: contentType,
        contentId: id
      }
    });
  }

  public postReviewForMedia(authorId: string, authorName: string,
                            contentType: string, contentId: string, contentTitle: string,
                            artUrl: string,
                            reviewTitle: string, reviewBody: string, rating: number) {
    return this.http.post(this.getReviews, {
      "authorId": authorId,
      "authorName": authorName,
      "contentType": contentType,
      "contentId": contentId,
      "contentTitle": contentTitle,
      "artUrl": artUrl,
      "reviewTitle": reviewTitle,
      "reviewBody": reviewBody,
      "rating": String(rating)
    });
  }

  public postFollow(userId: string, targetId: string) {
    return this.http.post(this.followListEndpoint, {
      "userId": userId,
      "targetId": targetId
    });
  }

  public deleteFollow(targetId: string) {
    return this.http.delete(this.followListEndpoint, {
      params: {
        "followingId": targetId
      }
    });
  }

  public getFollowLists(userId: string, pageNumber: number) {
    return this.http.get(this.followListEndpoint, {
      params: {
        "userId": userId,
        "pageNumber": pageNumber.toString()
      }
    });
  }

  public userFollows(userId: string, otherId: string) {
    return this.http.get(this.followListEndpoint, {
      params: {
        "userId": userId,
        "targetId": otherId
      }
    });
  }

  public deleteFromQueue(listType: string, mediaType: string, mediaId: string) {
    return this.http.delete(this.postQueueEndpoint, {
      params: {
        mediaId: mediaId,
        listType: listType,
        mediaType: mediaType
      }
    })
  }

  public getActivity(userId: string, offset: number) {
    return this.http.get<{}[]>(this.getActivityEndpoint, {
      params: {
        userId: userId,
        offset: String(offset)
      }
    })
  }
}
