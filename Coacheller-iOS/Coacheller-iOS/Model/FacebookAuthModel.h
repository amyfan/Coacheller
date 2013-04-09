//
//  FacebookAuthModel.h
//  Coacheller-iOS
//
//  Created by John Smith on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <FacebookSDK/FacebookSDK.h>

@interface FacebookAuthModel : NSObject

- (BOOL)existingFacebookSession;
//Get items from Facebook SDK objects
@property (strong, nonatomic) id<FBGraphUser> loggedInUser;


@end
