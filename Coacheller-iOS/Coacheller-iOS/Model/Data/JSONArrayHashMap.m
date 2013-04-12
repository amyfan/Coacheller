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
- (void)rebuildDataWith:(NSArray *)data {
  [self.dictionary removeAllObjects];
  for (int i = 0; i < [data count]; i++) {
    NSDictionary *jsonObj = data[i]; //[data objectAtIndex:(NSUInteger)i];
    [self storeTwoKeyObj:jsonObj];
  }
}

- (void)addValues:(NSDictionary *)jsonObj {
  [self storeTwoKeyObj:jsonObj];
}

- (void)storeTwoKeyObj:(NSDictionary *)jsonObj {
  NSMutableString *keyFullName = [[NSMutableString alloc]init];
  // append first key
  [keyFullName appendString:[jsonObj[self.keyName1] stringValue]];
  // append second key
  [keyFullName appendString:@"-"];
  [keyFullName appendString:[jsonObj[self.keyName2] stringValue]];
  [self.dictionary setObject:jsonObj forKey:[NSString stringWithString:keyFullName]];
}

- (void)clearRatings {
  [self.dictionary removeAllObjects];
}

- (NSDictionary *)getObjectWithKey:(NSString *)key {
  return [self.dictionary objectForKey:key];
}

- (NSDictionary *)getObjectWithKeyOne:(NSString *)keyOne AndKeyTwo:(NSString *)keyTwo {
  NSString *key = [NSString stringWithFormat:@"%@-%@", keyOne, keyTwo];
  return [self.dictionary objectForKey:[NSString stringWithString:key]];
}

@end
