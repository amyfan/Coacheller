//
//  JSONArrayHashMap.h
//  Coacheller
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface JSONArrayHashMap : NSObject

- (id)initWithKeyName1:(NSString *)keyName1 AndKeyName2:(NSString *)keyName2;

- (void)rebuildDataWith:(NSMutableArray *)data;

- (void)addValues:(NSData *)jsonObj;

@end
