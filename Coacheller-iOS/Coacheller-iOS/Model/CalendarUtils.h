//
//  CalendarUtils.h
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CalendarUtils : NSObject

+ (NSInteger)whatYearIsToday;
+ (NSInteger)whichWeekIsToday;
+ (NSString *)whatDayIsToday;
+ (NSString *)suggestDayToQuery;

+ (NSString *) militaryToCivilianTime:(NSInteger) milTime;

@end
