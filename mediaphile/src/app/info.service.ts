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
import {IsInList} from "./struct/IsInList";

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
  private getIsInListEndpoint: string = `${this.apiBackendUrl}list/isInList`;
  private getActivityEndpoint: string = `${this.apiBackendUrl}activity/followers`;
  private getRecommendationsEndpoint: string = `${this.apiBackendUrl}recommendations`;

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
        "pageNumber": (page - 1).toString()
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

  public login(redirect?: string) {
    let params = (redirect) ? {redirect: redirect} : {};
    return this.http.get<LoginStatusStruct>(this.loginStatus, {
      params: params
    })
  }

  public logout() {
    this.loginStatusService.sharedUrl.subscribe(x => {
      window.location.href = (x);
    });
  }

  public getUser(userId: string) {
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

  public getQueue(userId: string, type: string, offset: number) {
    return this.http.get<Object[]>(this.postQueueEndpoint, {
      params: {
        "userId": userId,
        "listType": type,
        "offset": String(offset)
      }
    });
  }

  public getReviewsForMedia(id: string, contentType: string, pageNumber: number) {
    return this.http.get<Review[]>(this.getReviews, {
      params: {
        contentType: contentType,
        contentId: id,
        pageNumber: String(pageNumber)
      }
    });
  }

  public getReviewsByUser(id: string) {
    return this.http.get<Review[]>(this.getReviews, {
      params: {
        userId: id
      }
    });
  }

  public getSpecificReview(contentId: string, contentType: string, userId: string) {
    return this.http.get<Review>(this.getReviews, {
      params: {
        contentType: contentType,
        contentId: contentId,
        userId: userId
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
  public isInList(userId: string, mediaId: string) {
    return this.http.get<IsInList>(this.getIsInListEndpoint, {
      params: {
        "userId": userId,
        "mediaId": mediaId,
      }
    });
  }


  public getActivity(userId: string, pageNumber: number) {
    return this.http.get<{}[]>(this.getActivityEndpoint, {
      params: {
        userId: userId,
        pageNumber: String(pageNumber)
      }
    })
  }

  public getRecommendations(mediaId: string, mediaType: string) {
    return this.http.get(this.getRecommendationsEndpoint, {
      params: {
        mediaId: mediaId,
        mediaType: mediaType
      }
    })
  }

  public searchUsers(query: string, pageNumber: number) {
    return this.http.get(this.userEndpoint, {
      params: {
        "query": query,
        "pageNumber": (pageNumber - 1).toString()
      }
    });
  }

  public toHttps(href: string) {
    if (href.startsWith("http://")) {
      return "https://" + href.substr(7);
    }
    return href;
  }
}
