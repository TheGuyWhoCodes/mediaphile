import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-recommendations',
  templateUrl: './recommendations.component.html',
  styleUrls: ['./recommendations.component.scss']
})
export class RecommendationsComponent implements OnInit {

  slides: any = {
    "results": [
      {
        "recommendations": null,
        "crew": null,
        "cast": null,
        "mediaType": "MOVIE",
        "id": 9957,
        "title": "The Benchwarmers",
        "original_title": "The Benchwarmers",
        "popularity": 15.705,
        "backdrop_path": "/n5yh6oimGdcKIjxzNft5sNX46ip.jpg",
        "poster_path": "/5yMPCr4qhuNg6fT538xicHaMaG7.jpg",
        "release_date": "2006-04-07",
        "adult": false,
        "belongs_to_collection": null,
        "budget": 0,
        "genres": null,
        "homepage": null,
        "overview": "A trio of guys try and make up for missed opportunities in childhood by forming a three-player baseball team to compete against standard little league squads.",
        "imdb_id": null,
        "original_language": "en",
        "production_companies": null,
        "production_countries": null,
        "revenue": 0,
        "runtime": 0,
        "spoken_languages": null,
        "tagline": null,
        "rating": 0.0,
        "vote_average": 5.4,
        "vote_count": 472,
        "status": null,
        "alternative_titles": null,
        "credits": null,
        "images": null,
        "keywords": null,
        "release_dates": null,
        "videos": null,
        "translations": null,
        "similar": null,
        "reviews": null,
        "lists": null
      },
      {
        "recommendations": null,
        "crew": null,
        "cast": null,
        "mediaType": "MOVIE",
        "id": 576296,
        "title": "Benchwarmers 2: Breaking Balls",
        "original_title": "Benchwarmers 2: Breaking Balls",
        "popularity": 3.94,
        "backdrop_path": "/DtGV25lfU91aAgGYrDEEqfGmr5.jpg",
        "poster_path": "/4TxdsFc149fntpR1ia8Ri8JQieb.jpg",
        "release_date": "2019-01-29",
        "adult": false,
        "belongs_to_collection": null,
        "budget": 0,
        "genres": null,
        "homepage": null,
        "overview": "A washed-up baseball player becomes an attorney. He gets a chance at redemption when his boss asks him to manage the company softball team in hopes of winning a championship.",
        "imdb_id": null,
        "original_language": "en",
        "production_companies": null,
        "production_countries": null,
        "revenue": 0,
        "runtime": 0,
        "spoken_languages": null,
        "tagline": null,
        "rating": 0.0,
        "vote_average": 4.6,
        "vote_count": 12,
        "status": null,
        "alternative_titles": null,
        "credits": null,
        "images": null,
        "keywords": null,
        "release_dates": null,
        "videos": null,
        "translations": null,
        "similar": null,
        "reviews": null,
        "lists": null
      }
    ],
    "page": 1,
    "total_pages": 1,
    "total_results": 2
  }
  constructor() { }

  ngOnInit(): void {
  }

}
