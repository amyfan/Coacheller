//
//  FacebookAuthController.h
//  Coacheller-iOS
//
//  Created by John Smith on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <FacebookSDK/FacebookSDK.h>

#import "FacebookAuthModel.h"
#import "LoginTestViewController.h"

@interface FacebookAuthController : NSObject
@property (strong, nonatomic) FacebookAuthModel *fbAuthModel;

- (id) initWithViewController:(LoginTestViewController*) viewController;
- (void) postStatusUpdate;
- (void) performPublishAction:(void (^)(void)) action;
@end

