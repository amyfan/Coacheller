//
//  SetTableViewController.h
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/5/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SetDataForTVC.h"
#import "LoginData.h"
#import "CustomPair.h"
#import "AuthProtocol.h"

@interface SetTableViewController : UITableViewController <AuthProtocol>

@property (nonatomic, strong) SetDataForTVC *sets;
@property (nonatomic) NSInteger yearToQuery;
@property (nonatomic) NSInteger weekToQuery;
@property (nonatomic, strong) NSString *dayToQuery;


// contains set id, stored in setListAdapter
@property (nonatomic, strong) NSDictionary *lastSetSelected;
// contains actual rating, stored in userRatingsJAHM
@property (nonatomic, strong) NSDictionary *lastRating;

// for RateSetVC
- (void)submitRating:(NSDictionary *)rating;

// for AuthProtocol
- (void)facebookLoggedIn;
- (void)facebookLoggedOut;

@end
