//
//  CoachellerAppDelegate.h
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/5/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AuthProtocol.h"
#import "AuthController.h"

@interface CoachellerAppDelegate : UIResponder <UIApplicationDelegate>
@property (nonatomic, strong) AuthController* authController;

@property (strong, nonatomic) UIWindow *window;

@end
