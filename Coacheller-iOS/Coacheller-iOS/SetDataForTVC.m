//
//  SetDataForTVC.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/5/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "SetDataForTVC.h"

@interface SetDataForTVC()
@property (strong, nonatomic) JSONArrayHashMap *myRatings;
@property (strong, nonatomic) JSONArraySortMap *sortedSets;
@end

@implementation SetDataForTVC

// move to coach class
- (id)initWithTimeFieldName:(NSString *)timeFieldName StageFieldName:(NSString *)stageFieldName AndRatingsHashMap:(JSONArrayHashMap *)ratings {
  self.timeFieldName = timeFieldName;
  self.stageFieldName = stageFieldName;
}

- (void)sortByField:(NSString *)fieldName WithValueType:(NSString *)valueType {
  self.sortedSets = [[JSONArraySortMap alloc] initWithArray:self.unsortedSets AndParameterToSort:fieldName AndValueType:valueType];
}

- (NSDictionary *)getItemAt:(NSInteger)index {
  return [self.sortedSets getSortedJSONObject:index];
}

- (NSInteger)getItemCount {
  return [self.unsortedSets count];
}

- (void)resortSets:(NSString *)sortMode {
  // TODO: impl
}


@end
