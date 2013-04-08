//
//  CoachSetTVC.h
//  Coacheller
//
//  Created by Amy Fan on 4/4/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SetTableViewController.h"
#import "LoginData.h"

@interface CoachSetTVC : SetTableViewController

- (void)processLoginDataWithLoginType:(NSString *)loginType AccountId:(NSString *)accountId AndAccountToken:(NSString *)accountToken;
- (LoginData *)getLoginData;
- (void)clearLoginData;

@end
