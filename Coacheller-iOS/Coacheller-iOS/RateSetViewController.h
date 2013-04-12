//
//  RateSetViewController.h
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/10/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CustomPair.h"

@interface RateSetViewController : UIViewController<UITextFieldDelegate>

// contains set id, stored in setListAdapter
@property (nonatomic, strong) NSDictionary *set;
// contains both week's ratings
@property (nonatomic, strong) CustomPair *ratingPair;

// may differ from queried weekend for set list
@property (nonatomic) NSInteger currentWeekend;

// only stored to pass back to TVC which is entirely refreshed
@property (nonatomic) NSInteger queriedYear;
@property (nonatomic) NSInteger queriedWeekend;
@property (nonatomic) NSString *queriedDay;

@end
