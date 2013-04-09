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
#import "AuthProtocol.h"
#import "LoginTestViewController.h"

@interface FacebookAuthController : NSObject
@property (strong, nonatomic) FacebookAuthModel *facebookAuthModel;

- (void) openSession:(UIViewController <AuthProtocol>*)caller;
- (void) postStatusUpdate:(UIViewController*)caller buttonPushed:(UIButton*)postButton;
- (void) performPublishAction:(void (^)(void)) action;
- (void) killSession:(UIViewController <AuthProtocol>*)caller;
- (void)handleSessionStateChanged:(FBSession *)session
                            state:(FBSessionState) state
                            error:(NSError *)error;
@end

