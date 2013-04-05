//
//  LoginData.m
//  Coacheller
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "LoginData.h"

@implementation LoginData

- (NSString *)getTwitterToken {
  NSArray *fields = [self.accountToken componentsSeparatedByString:@"|"];
  if ([fields count] > 0) {
    return fields[0];
  } else {
    return nil;
  }
}

- (NSString *)getTwitterSecret {
  NSArray *fields = [self.accountToken componentsSeparatedByString:@"|"];
  if ([fields count] > 1) {
    return fields[1];
  } else {
    return nil;
  }
}

@end
