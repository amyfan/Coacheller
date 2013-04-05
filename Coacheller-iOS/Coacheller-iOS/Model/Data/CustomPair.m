//
//  CustomPair.m
//  Coacheller
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "CustomPair.h"

@implementation CustomPair

- (id)initWithFirstObj:(id)firstObj AndSecondObj:(id)secondObj {
  
  if (self = [super init]) {
    self.firstObj = firstObj;
    self.secondObj = secondObj;
  }
  
  return self;
}
@end
