//
//  JSONArrayHashMap.m
//  Coacheller
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "JSONArrayHashMap.h"

@interface JSONArrayHashMap()
@property (strong, nonatomic) NSString *keyName1;
@property (strong, nonatomic) NSString *keyName2;
@property (strong, nonatomic) NSMutableDictionary *dictionary; // hash map
@end

@implementation JSONArrayHashMap

- (id)initWithKeyName1:(NSString *)keyName1 AndKeyName2:(NSString *)keyName2 {
  
  if (self = [super init]) {
    self.keyName1 = keyName1;
    self.keyName2 = keyName2;
    self.dictionary = [[NSMutableDictionary alloc] init];
  }
  
  return self;
}

// TODO: fig out memory management
- (void)rebuildDataWith:(NSMutableArray *)data {
  [self.dictionary removeAllObjects];
  for (int i = 0; i < [data count]; i++) {
    NSData *jsonObj = data[i]; //[data objectAtIndex:(NSUInteger)i];
    [self storeTwoKeyObj:jsonObj];
  }
}

- (void)addValues:(NSData *)jsonObj {
  [self storeTwoKeyObj:jsonObj];
}

- (void)storeTwoKeyObj:(NSData *)jsonObj {
  NSMutableString *keyFullName = [[NSMutableString alloc]init];
  // append first key
  [keyFullName appendString:[jsonObj valueForKey:self.keyName1]];
  // append second key
  [keyFullName appendString:[jsonObj valueForKey:self.keyName2]];
  [self.dictionary setObject:jsonObj forKey:(NSString *)keyFullName];
}

@end
