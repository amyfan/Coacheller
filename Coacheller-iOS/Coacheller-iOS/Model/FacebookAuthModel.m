//
//  FacebookAuthModel.m
//  Coacheller-iOS
//
//  Created by John Smith on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "FacebookAuthModel.h"

@interface FacebookAuthModel ()

@property (strong, nonatomic) NSDictionary <FBGraphUser>* storedUserData;

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

- (void) setUserData:(NSDictionary <FBGraphUser>*)userData {
  self.storedUserData = userData;
}

- (NSString*) getUserEmailAddress {
  return [self.storedUserData objectForKey:@"email"];
}

- (NSString*) getUserFirstName {
  return self.storedUserData.first_name;
}

- (NSString*) getUserLastName {
  return self.storedUserData.last_name;
}

- (NSString*) getFacebookID {
  return self.storedUserData.id;
}

@end
