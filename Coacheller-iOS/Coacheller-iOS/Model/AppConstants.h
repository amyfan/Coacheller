//
//  AppConstants.h
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/8/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AppConstants : NSObject

extern NSString * const SORT_MODE_ARTIST;
extern NSString * const SORT_MODE_STAGE;
extern NSString * const SORT_MODE_TIME;

extern NSString * const DATA_LOGIN_INFO_KEY;

extern NSString * const SERVER_URL_COACHELLER;
extern NSString * const SERVER_URL_LOLLAPALOOZER;

extern NSString * const PARAM_ACTION;
extern NSString * const PARAM_AUTH_TYPE;
extern NSString * const PARAM_AUTH_ID;
extern NSString * const PARAM_AUTH_TOKEN;
extern NSString * const PARAM_EMAIL;
extern NSString * const PARAM_SET_ID;
extern NSString * const PARAM_DAY;
extern NSString * const PARAM_YEAR;
extern NSString * const PARAM_SCORE;
extern NSString * const PARAM_WEEKEND;
extern NSString * const PARAM_NOTES;

extern NSString * const ACTION_GET_SETS;
extern NSString * const ACTION_GET_RATINGS;
extern NSString * const ACTION_ADD_RATING;
extern NSString * const ACTION_EMAIL_RATINGS;

extern NSString * const LOGIN_TYPE_GOOGLE;
extern NSString * const LOGIN_TYPE_FACEBOOK;
extern NSString * const LOGIN_TYPE_TWITTER;
extern NSString * const LOGIN_TYPE_FACEBOOK_BROWSER;

extern NSString * const JSON_SET_ID;
extern NSString * const JSON_SET_ARTIST;
extern NSString * const JSON_SET_DAY;
extern NSString * const JSON_SET_TIME_ONE;
extern NSString * const JSON_SET_TIME_TWO;
extern NSString * const JSON_SET_STAGE_ONE;
extern NSString * const JSON_SET_STAGE_TWO;
extern NSString * const JSON_SET_AVG_SCORE_ONE;
extern NSString * const JSON_SET_AVG_SCORE_TWO;
extern NSString * const JSON_RATING_SET_ID;
extern NSString * const JSON_RATING_WEEKEND;
extern NSString * const JSON_RATING_SCORE;
extern NSString * const JSON_RATING_NOTES;

@end
