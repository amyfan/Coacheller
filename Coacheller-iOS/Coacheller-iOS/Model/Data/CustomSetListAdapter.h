//
//  CustomSetListAdapter.h
//  Coacheller
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "JSONArrayHashMap.h"
#import "JSONArraySortMap.h"

@interface CustomSetListAdapter : NSObject

@property (strong, nonatomic) NSString *timeFieldName;
@property (strong, nonatomic) NSString *stageFieldName;
@property (strong, nonatomic) NSArray *data;
@property (weak, nonatomic) JSONArrayHashMap *myRatings;
@property (strong, nonatomic) JSONArraySortMap *sortMap;

- (void) sortByField:(NSString *)fieldName;

- (void) resortSetList:(NSString *)sortMode;

@end
