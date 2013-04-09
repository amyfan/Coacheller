//
//  AppConstants.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/8/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "AppConstants.h"

@implementation AppConstants

NSString * const SORT_MODE_ARTIST = @"artist";
NSString * const SORT_MODE_STAGE = @"stage";
NSString * const SORT_MODE_TIME = @"time";

NSString * const DATA_LOGIN_INFO_KEY = @"DATA_LOGIN_INFO";

NSString * const SERVER_URL_COACHELLER = @"https://ratethisfest.appspot.com/coachellerServlet";
NSString * const SERVER_URL_LOLLAPALOOZER = @"https://ratethisfest.appspot.com/lollapaloozerServlet";

NSString * const PARAM_ACTION = @"action";
NSString * const PARAM_AUTH_TYPE = @"auth_type";
NSString * const PARAM_AUTH_ID = @"auth_id";
NSString * const PARAM_AUTH_TOKEN = @"auth_token";
NSString * const PARAM_EMAIL = @"email";
NSString * const PARAM_SET_ID = @"set_id";
NSString * const PARAM_DAY = @"day";
NSString * const PARAM_YEAR = @"year";
NSString * const PARAM_SCORE = @"score";
NSString * const PARAM_WEEKEND = @"weekend";
NSString * const PARAM_NOTES = @"notes";

NSString * const ACTION_GET_SETS = @"get_sets";
NSString * const ACTION_GET_RATINGS = @"get_ratings";
NSString * const ACTION_ADD_RATING = @"add_rating";
NSString * const ACTION_EMAIL_RATINGS = @"email_ratings";

NSString * const LOGIN_TYPE_GOOGLE = @"LOGIN_TYPE_GOOGLE";
NSString * const LOGIN_TYPE_FACEBOOK = @"LOGIN_TYPE_FACEBOOK";
NSString * const LOGIN_TYPE_TWITTER = @"LOGIN_TYPE_TWITTER";
NSString * const LOGIN_TYPE_FACEBOOK_BROWSER = @"LOGIN_TYPE_FACEBOOK_BROWSER";

NSString * const JSON_SET_ID = @"id";
NSString * const JSON_SET_ARTIST = @"artist";
NSString * const JSON_SET_DAY = @"day";
NSString * const JSON_SET_TIME_ONE = @"time_one";
NSString * const JSON_SET_TIME_TWO = @"time_two";
NSString * const JSON_SET_STAGE_ONE = @"stage_one";
NSString * const JSON_SET_STAGE_TWO = @"stage_two";
NSString * const JSON_SET_AVG_SCORE_ONE = @"avg_score_one";
NSString * const JSON_SET_AVG_SCORE_TWO = @"avg_score_two";
NSString * const JSON_RATING_SET_ID = @"set_id";
NSString * const JSON_RATING_WEEKEND = @"weekend";
NSString * const JSON_RATING_SCORE = @"score";
NSString * const JSON_RATING_NOTES = @"notes";

@end
