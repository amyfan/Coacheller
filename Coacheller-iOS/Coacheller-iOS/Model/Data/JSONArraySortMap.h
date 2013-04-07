//
//  JSONArraySortMap.h
//  Coacheller
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JSONArraySortMap : NSObject

- (id)initWithArray:(NSArray *)arrayToSort AndParameterToSort:(NSString *)parameterToSort AndValueType:(NSString *)valueType;

- (NSDictionary *)getSortedJSONObject:(NSInteger)index;

@end
