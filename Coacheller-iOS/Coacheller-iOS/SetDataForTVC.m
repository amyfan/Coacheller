//
//  SetDataForTVC.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/5/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "SetDataForTVC.h"

@interface SetDataForTVC()
@end

@implementation SetDataForTVC

// designated initializer
- (id)initWithTimeFieldName:(NSString *)timeFieldName StageFieldName:(NSString *)stageFieldName AndRatingsHashMap:(JSONArrayHashMap *)ratings {
  self = [super init];
  if (self) {
    self.timeFieldName = timeFieldName;
    self.stageFieldName = stageFieldName;
  }
  return self;
}

- (void)setSetsData:(NSArray *)setsData {
  self.unsortedSets = setsData;
}

- (void)sortByField:(NSString *)fieldName {
  self.sortedSets = [[JSONArraySortMap alloc] initWithArray:self.unsortedSets AndParameterToSort:fieldName];
}

- (NSDictionary *)getItemAt:(NSInteger)index {
  return [self.sortedSets getSortedJSONObject:index];
}

- (NSInteger)getItemCount {
  return [self.unsortedSets count];
}

// TODO: create androidconstants class
- (void) resortSets:(NSString *)sortMode {
  if ([sortMode isEqualToString:@"time"]) {
    [self sortByField:self.timeFieldName];
  } else if ([sortMode isEqualToString:@"artist"]) {
    [self sortByField:@"artist"];
  } else if ([sortMode isEqualToString:@"stage"]) {
    [self sortByField:self.stageFieldName];
  }
}


@end
