//
//  StorageManager.h
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface StorageManager : NSObject

- (void)load;
- (void)save;
//- (void)putJSONArray:(NSArray*)array WithName:(NSString *)dataName;
- (void)putObject:(id)object WithName:(NSString *)objectName;
- (id)getObject:(NSString *)objectName;

@end
