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
@property (strong, nonatomic) JSONArrayHashMap *myRatings;
@property (strong, nonatomic) JSONArraySortMap *sortedSets;

// move to coach class
- (id)initWithTimeFieldName:(NSString *)timeFieldName StageFieldName:(NSString *)stageFieldName AndRatingsHashMap:(JSONArrayHashMap *)ratings;

- (void)setSetsData:(NSArray *)setsData;

- (NSDictionary *)getItemAt:(NSInteger)index;

- (NSInteger)getItemCount;

- (void)resortSets:(NSString *)sortMode;

@end
