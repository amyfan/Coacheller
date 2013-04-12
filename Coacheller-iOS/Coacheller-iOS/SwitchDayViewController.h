//
//  SwitchDayViewController.h
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/8/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SwitchDayViewController : UIViewController<UIPickerViewDataSource, UIPickerViewDelegate> 

// Deprecated: used to retain data in case of push segue
//@property (nonatomic) NSInteger defaultYear;
//@property (nonatomic) NSInteger defaultWeek;
//@property (nonatomic, strong) NSString *defaultDay;

@property (nonatomic) NSInteger yearToQuery;
@property (nonatomic) NSInteger weekToQuery;
@property (nonatomic, strong) NSString *dayToQuery;

@end
