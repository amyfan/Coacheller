//
//  SetTableViewController.h
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/5/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <UIKit/UIKit.h>

// TODO: move to constants file
#define SET_ID @"id"
#define SET_ARTIST @"artist"
#define SET_DAY @"day"
#define SET_TIME_ONE @"day"
#define SET_TIME_TWO @"day"
#define SET_STAGE_ONE @"stage_one"
#define SET_STAGE_TWO @"stage_two"
#define RATING_SET_ID @"set_id"
#define RATING_WEEKEND @"weekend"
#define RATING_SCORE @"day"
#define RATING_NOTES @"notes"

#define LOGIN_TYPE_GOOGLE @"Google"
#define LOGIN_TYPE_FACEBOOK @"Facebook"

@interface SetTableViewController : UITableViewController

//@property (nonatomic, strong) SetDataForTVC *sets;

@property (nonatomic, strong) NSMutableArray *setsArray;
@property (nonatomic) NSInteger *yearToQuery;
@property (nonatomic) NSInteger *weekToQuery;
@property (nonatomic, strong) NSString *dayToQuery;

@end
