//
//  CustomPair.h
//  Coacheller
//  This class primarily used to store pair of ratings for both weekends for selected set
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CustomPair : NSObject

- (id)initWithFirstObj:(id)firstObj AndSecondObj:(id)secondObj;

@property (strong, nonatomic) id firstObj;
@property (strong, nonatomic) id secondObj;

@end
