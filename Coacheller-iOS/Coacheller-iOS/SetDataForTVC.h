//
//  SetDataForTVC.h
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/5/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "JSONArrayHashMap.h"
#import "JSONArraySortMap.h"

@interface SetDataForTVC : NSObject

@property (strong, nonatomic) NSString *timeFieldName;
@property (strong, nonatomic) NSString *stageFieldName;
@property (strong, nonatomic) NSArray *unsortedSets;

// move to coach class
- (id)initWithTimeFieldName:(NSString *)timeFieldName StageFieldName:(NSString *)stageFieldName AndRatingsHashMap:(JSONArrayHashMap *)ratings;

- (void)sortByField:(NSString *)fieldName WithValueType:(NSString *)valueType;

- (NSDictionary *)getItemAt:(NSInteger)index;

- (NSInteger)getItemCount;

- (void)resortSets:(NSString *)sortMode;

@end
