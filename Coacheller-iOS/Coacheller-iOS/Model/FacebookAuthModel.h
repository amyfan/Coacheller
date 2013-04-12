//
//  FacebookAuthModel.h
//  Coacheller-iOS
//
//  Created by John Smith on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <FacebookSDK/FacebookSDK.h>
#import "FacebookDataProtocol.h"

@interface FacebookAuthModel : NSObject <FacebookDataProtocol>

@property BOOL appLoggedIn;

- (BOOL)existingFacebookSession;


//Get items from Facebook SDK objects
- (void) setUserData:(NSDictionary <FBGraphUser>*)userData;
- (NSString*) getUserEmailAddress;
- (NSString*) getUserFirstName;
- (NSString*) getUserLastName;
- (NSString*) getFacebookID;

@end
