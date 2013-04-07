//
//  CustomSetListAdapter.m
//  Coacheller
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "CustomSetListAdapter.h"

@implementation CustomSetListAdapter

- (void) sortByField:(NSString *)fieldName OfDataType:(NSString *)dataType {
  self.sortMap = [[JSONArraySortMap alloc] initWithArray:self.data AndParameterToSort:fieldName AndValueType:dataType];
}

// TODO: IMPL THIS CLASS TO EXTEND APPROPRIATE VIEW ADAPTER
- (void) resortSetList:(NSString *)sortMode {
  
}

@end
