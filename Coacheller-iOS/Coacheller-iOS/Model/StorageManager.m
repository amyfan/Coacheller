//
//  StorageManager.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "StorageManager.h"

@interface StorageManager()
// fileName.plist
@property (strong, nonatomic) NSString *rootPath;
@property (strong, nonatomic) NSString *saveFileName;
@property (strong, nonatomic) NSMutableDictionary *data;
@end

@implementation StorageManager

// designated initializer
- (id)initWithSaveFileName:(NSString *)saveFileName {
  self = [super init];
  if (self) {
    self.rootPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
    self.saveFileName = saveFileName;
    
    [self load];
  }
  return self;
}

- (void)load {
  NSString *plistPath = [self.rootPath stringByAppendingPathComponent:self.saveFileName];
  NSLog([NSString stringWithFormat:@"StorageManager: loading from %@", plistPath]);
  self.data = [[NSMutableDictionary alloc] initWithContentsOfFile:plistPath];
  
  for (id key in self.data) {
    NSLog([NSString stringWithFormat:@"StorageManager data key loaded:  %@", key]);
  }

  if(!self.data) {
    NSLog(@"StorageManager: no data loaded");
    self.data = [[NSMutableDictionary alloc] init];
  }
}

- (void)save {
  NSString *plistPath = [self.rootPath stringByAppendingPathComponent:self.saveFileName];
  NSLog([NSString stringWithFormat:@"StorageManager: saving data to %@", plistPath]);
  
  //[self.data writeToFile:plistPath atomically:YES];
  
  // only do the following if want to convert plist->NSData first before writing out, for whatever reason (logindata preventing this from being property list)
  
  NSString *error;
  NSData *plistData = [NSPropertyListSerialization dataFromPropertyList:self.data format:NSPropertyListXMLFormat_v1_0 errorDescription:&error];
  if(plistData) {
    [plistData writeToFile:plistPath atomically:YES];
  }
}

- (void)putJSONArray:(NSArray*)array WithName:(NSString *)dataName {
  [self.data setValue:array forKey:dataName];
}

- (void)putJSONDictionary:(NSDictionary*)array WithName:(NSString *)dataName {
  [self.data setValue:array forKey:dataName];
}

// deprecated; we're keeping this a property list!
- (void)putObject:(id)object WithName:(NSString *)objectName {
  [self.data setObject:object forKey:objectName];
}

- (id)getObject:(NSString *)objectName {
  return [self.data objectForKey:objectName];
}

- (void)removeObjectWithKey:(NSString *)objectName {
  [self.data removeObjectForKey:objectName];
}

@end
