//
//  RateSetViewController.h
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/10/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CustomPair.h"

@interface RateSetViewController : UIViewController

// contains set id, stored in setListAdapter
@property (nonatomic, strong) NSDictionary *set;
// contains actual rating, stored in userRatingsJAHM
@property (nonatomic, strong) NSDictionary *rating;
// contains both week's scores
@property (nonatomic, strong) CustomPair *ratingScorePair;

@end
