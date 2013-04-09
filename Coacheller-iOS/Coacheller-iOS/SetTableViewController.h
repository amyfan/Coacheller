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

@interface SetTableViewController : UITableViewController

@property (nonatomic, strong) SetDataForTVC *sets;
@property (nonatomic) NSInteger yearToQuery;
@property (nonatomic) NSInteger weekToQuery;
@property (nonatomic, strong) NSString *dayToQuery;

- (void)processLoginDataWithLoginType:(NSString *)loginType AccountId:(NSString *)accountId AndAccountToken:(NSString *)accountToken;
- (LoginData *)getLoginData;
- (void)clearLoginData;

@end
