//
//  JSONArraySortMap.h
//  Coacheller
//  This class primarily used to store sorted sets
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JSONArraySortMap : NSObject

- (id)initWithArray:(NSArray *)arrayToSort AndParameterToSort:(NSString *)parameterToSort WithOptionalSecondParam:(NSString *)secondParam;

- (NSDictionary *)getSortedJSONObject:(NSInteger)index;

@end
