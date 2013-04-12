//
//  StorageManager.h
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface StorageManager : NSObject

- (id)initWithSaveFileName:(NSString *)saveFileName;

- (void)save;

- (void)putJSONArray:(NSArray*)array WithName:(NSString *)dataName;

- (void)putJSONDictionary:(NSDictionary*)array WithName:(NSString *)dataName;

- (id)getObject:(NSString *)objectName;

- (void)removeObjectWithKey:(NSString *)objectName;

@end
