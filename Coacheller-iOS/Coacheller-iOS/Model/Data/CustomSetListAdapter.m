//
//  CustomSetListAdapter.m
//  Coacheller
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "CustomSetListAdapter.h"

@implementation CustomSetListAdapter

- (void) sortByField:(NSString *)fieldName {
  self.sortMap = [[JSONArraySortMap alloc] initWithArray:self.data AndParameterToSort:fieldName];
}

// TODO: create androidconstants class
- (void) resortSetList:(NSString *)sortMode {
  if ([sortMode isEqualToString:@"time"]) {
    [self sortByField:self.timeFieldName];
  } else if ([sortMode isEqualToString:@"artist"]) {
    [self sortByField:@"artist"];
  } else if ([sortMode isEqualToString:@"stage"]) {
    [self sortByField:self.stageFieldName];
  }
}

@end
