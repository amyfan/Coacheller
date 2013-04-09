//
//  FacebookAuthModel.m
//  Coacheller-iOS
//
//  Created by John Smith on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "FacebookAuthModel.h"

@interface FacebookAuthModel ()



@end


@implementation FacebookAuthModel

//Return true if facebook connection can be re-established without user input
- (BOOL)existingFacebookSession {
  if (FBSession.activeSession.state == FBSessionStateCreatedTokenLoaded) {
    return YES;
  } else {
    return NO;
  }
}



@end
