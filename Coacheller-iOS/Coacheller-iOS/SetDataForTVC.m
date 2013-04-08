//
//  SetDataForTVC.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/5/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "SetDataForTVC.h"
#import "AppConstants.h"

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

- (void) resortSets:(NSString *)sortMode {
  if ([sortMode isEqualToString:SORT_MODE_TIME]) {
    [self sortByField:self.timeFieldName];
  } else if ([sortMode isEqualToString:SORT_MODE_ARTIST]) {
    [self sortByField:SORT_MODE_ARTIST];
  } else if ([sortMode isEqualToString:SORT_MODE_STAGE]) {
    [self sortByField:self.stageFieldName];
  }
}


@end
