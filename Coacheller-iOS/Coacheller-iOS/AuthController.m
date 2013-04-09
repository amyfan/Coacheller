//
//  AuthController.m
//  Coacheller-iOS
//
//  Created by John Smith on 4/9/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "AuthController.h"


@interface AuthController()

@end

@implementation AuthController

- (id) init {
  self = [super init];
  if (self) {
    self.facebookAuthController = [[FacebookAuthController alloc] init];
  }
  
  return self;
}

@end
