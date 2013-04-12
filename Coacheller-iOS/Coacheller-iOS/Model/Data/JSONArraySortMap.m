//
//  JSONArraySortMap.m
//  Coacheller
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "JSONArraySortMap.h"
#import "CustomPair.h"

@interface JSONArraySortMap()
@property (strong, nonatomic) NSArray *unsortedArray;
@property (strong, nonatomic) NSArray *sortedArrayOfPairs;
@property (strong, nonatomic) NSString *parameterToSort;
@property (strong, nonatomic) NSString *valueType;
@end

@implementation JSONArraySortMap

- (id)initWithArray:(NSArray *)arrayToSort AndParameterToSort:(NSString *)parameterToSort WithOptionalSecondParam:(NSString *)secondParam {
  
  if (self = [super init]) {
    self.unsortedArray = arrayToSort;
    self.parameterToSort = parameterToSort;
    
    
    
    NSMutableArray *arrayOfPairs;
    
    if ([arrayToSort count] > 0) {
      // create array of pairs
      arrayOfPairs = [NSMutableArray array]; // array == alloc init
      
      for (int i = 0; i < [arrayToSort count]; i++) {
        
        // NSData *jsonObj = arrayToSort[i];
        // obj = [jsonObj valueForKey:parameterToSort];
        
        NSDictionary *obj = arrayToSort[i];
        // @(i) == [NSNumber numberWithInt:i]
        CustomPair *nextPair = [[CustomPair alloc] initWithFirstObj:@(i) AndSecondObj:obj];
        [arrayOfPairs addObject:nextPair];
      }
      
      NSArray *arrayOfPairsToSort = arrayOfPairs;
      if (secondParam) {
        arrayOfPairsToSort = [self sortThePairArray:arrayOfPairsToSort WithParam:secondParam];
      }
      self.sortedArrayOfPairs = [self sortThePairArray:arrayOfPairsToSort WithParam:parameterToSort];
    } else {
      self.sortedArrayOfPairs = [NSArray array];
    }
    
  }
  
  return self;
}

- (NSArray *)sortThePairArray:(NSArray *)arrayToSort WithParam:(NSString *)parameterToSort {
  // sorting with a 'block'
  return [arrayToSort sortedArrayUsingComparator:^NSComparisonResult(id a, id b) {
    //if ([obj isMemberOfClass:[NSNumber class]]) {
    //  NSNumber *first = [(CustomPair*)a secondObj];
    //  NSNumber *second = [(CustomPair*)b secondObj];
    //  return [first compare:second];
    //} else { // if ([obj isMemberOfClass:[NSString class]]) {
    NSString *first = [(CustomPair*)a secondObj][parameterToSort];
    NSString *second = [(CustomPair*)b secondObj][parameterToSort];
    return [first compare:second];
    //}
  }];
}

- (NSDictionary *)getSortedJSONObject:(NSInteger)index {
  CustomPair *pair = self.sortedArrayOfPairs[index];
  // NSNumber *indexToReturn = pair.firstObj;
  return self.unsortedArray[[pair.firstObj integerValue]];
}


@end
