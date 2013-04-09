//
//  LoginTestViewController.h
//  Coacheller-iOS
//
//  Created by John Smith on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AuthProtocol.h"

@interface LoginTestViewController : UIViewController <AuthProtocol>
@property (strong, nonatomic) IBOutlet UIButton *buttonLoginGoogle;
@property (strong, nonatomic) IBOutlet UIButton *buttonLoginFacebook;
@property (strong, nonatomic) IBOutlet UIButton *buttonLoginTwitter;
@property (strong, nonatomic) IBOutlet UIButton *buttonPostFacebook;
@property (strong, nonatomic) IBOutlet UIButton *buttonLogoutFacebook;
@property (strong, nonatomic) IBOutlet UIActivityIndicatorView *loginTestViewHourglass;

- (void)loginFacebookSuccess;
- (void)loginFacebookFailed;

@end
