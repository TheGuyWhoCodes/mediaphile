import { Component, OnInit } from '@angular/core';
import {LoginStatus} from "../../auth/login.status";
import {Title} from "@angular/platform-browser";
import {faClipboardCheck, faClock, faHeart, faUserFriends} from "@fortawesome/free-solid-svg-icons";
import {InfoService} from "../../info.service";
import {ActivatedRoute} from "@angular/router";
import {Observable} from "rxjs";

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss']
})
export class UserProfileComponent implements OnInit {

  clock = faClock;
  clipCheck = faClipboardCheck;
  heart = faHeart;
  friends = faUserFriends;

  public userId: string;
  public profileId: string;
  public isSelf: boolean;

  public entity: {};
  public hasResults: boolean;

  public followed: boolean;

  public following: [];
  public followers: [];

  public nFollowing: number;
  public nFollowers: number;

  private profileColor: string;

  constructor(public loginStatus: LoginStatus, private title: Title,
              private infoSvc: InfoService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.title.setTitle(`Mediaphile :: Profile`)

    if (this.route.snapshot.paramMap.get("id") != undefined) {
      this.profileId = this.route.snapshot.paramMap.get("id");
    }

    if(this.profileId != null) {
      this.infoSvc.getUser(this.profileId).subscribe(data => {
        this.entity = data;
        this.hasResults = true;

        this.subscribeSelf();
      });
    } else {
      this.subscribeSelf();
    }

    // TODO: Pass page number... should it be passed up from follow-list?
    this.infoSvc.getFollowLists(this.profileId, 0).subscribe(data => {
      this.followers = data['followersList'];
      this.nFollowers = data['followerLength'];
      this.following = data['followingList'];
      this.nFollowing = data['followingLength'];
    });
  }

  subscribeSelf() {
    this.loginStatus.sharedAccountId.subscribe(userId => {
      this.userId = userId;
      this.infoSvc.userFollows(this.userId, this.profileId).subscribe(data => {
        this.followed = (data == true);
      });
      this.isSelf = (this.userId === this.profileId);
      if (this.entity) {
        let whose = (this.isSelf) ? "" : (this.entity['username'] + "'s ");
        this.title.setTitle(`Mediaphile :: ${whose}Profile`);
      }
    });
  }

  hasProfilePic() : boolean {
    return this.entity['profilePicUrl'] !== "";
  }

  getProfilePicChar() : string {
    return this.entity['username'].charAt(0).toLocaleUpperCase();
  }

  private static hashProfileId(str) : number {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    return hash;
  }

  private static hexToRgb(hex) : {} {
    let result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
      r: parseInt(result[1], 16),
      g: parseInt(result[2], 16),
      b: parseInt(result[3], 16)
    } : null;
  }

  getProfileColor() : string {
    let hash = UserProfileComponent.hashProfileId(this.profileId);
    let encoded = (hash & 0x00FFFFFF).toString(16).toUpperCase();
    this.profileColor = "#" + "00000".substring(0, 6 - encoded.length) + encoded;
    return this.profileColor;
  }

  getContrastingColor() {
    if (!this.profileColor) this.getProfileColor();
    let rgb = UserProfileComponent.hexToRgb(this.profileColor);
    const brightness = Math.round(((parseInt(rgb['r']) * 299) +
      (parseInt(rgb['g']) * 587) +
      (parseInt(rgb['b']) * 114)) / 1000);

    return (brightness > 125) ? '#000000' : '#FFFFFF';
  }

  toggleFollow() {
    let old_followed = this.followed;
    this.followed = !this.followed;

    console.log(old_followed, this.followed, this.profileId, this.userId);
    if (old_followed) {
      this.infoSvc.deleteFollow(this.profileId).subscribe(x => {
        console.log(x)
      }, error => {
        console.log(error);
      });
    } else {
      this.infoSvc.postFollow(this.userId, this.profileId).subscribe(x => {
        console.log(x)
      }, error => {
        console.log(error);
      });
    }
  }
}
