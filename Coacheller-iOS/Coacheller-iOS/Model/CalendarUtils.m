//
//  CalendarUtils.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "CalendarUtils.h"

@implementation CalendarUtils

+ (NSInteger)whatYearIsToday {
  NSDate *currentDate = [NSDate date];
  NSCalendar* calendar = [NSCalendar currentCalendar];
  NSDateComponents* components = [calendar components:NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit fromDate:currentDate];
  return [components year];
}

/**
 * TODO: refine
 */
+ (NSInteger)whichWeekIsToday {
  NSDate *currentDate = [NSDate date];
  NSCalendar* calendar = [NSCalendar currentCalendar];
  NSDateComponents* components = [calendar components:NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit fromDate:currentDate];
  if ([components day] < 18) {
    return 1;
  } else {
    return 2;
  }
}

// App and Database use strings in English so this time it is better to do it this way
+ (NSString *)whatDayIsToday {
  NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
  [dateFormatter setDateFormat:@"EEEE"];
  return [dateFormatter stringFromDate:[NSDate date]];
}

//Use this when suggesting a day to search on and the user's preference is not yet known.
//No reason to try to display set data from a Monday
+ (NSString *)suggestDayToQuery {
  NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
  [dateFormatter setDateFormat:@"EEEE"];
  NSString *day = [dateFormatter stringFromDate:[NSDate date]];
  
  if (![day isEqualToString:@"Saturday"] || ![day isEqualToString:@"Sunday"]) {
    return @"Friday";
  } else {
    return day;
  }
}

+ (NSString *) militaryToCivilianTime:(NSInteger) milTime {
  NSString *ampm;
  if (milTime < 1200 || milTime >= 2400) {
    ampm = @"a";
  } else {
    ampm = @"p";
  }
  
  if (milTime < 100) {
    milTime += 1200;
  }
  
  if (milTime >= 1300) {
    milTime -= 1200;
  }
  
  NSString *milTimeStr = [NSString stringWithFormat:@"%d", milTime];
  
  int timeStrLen = milTimeStr.length;
  NSString *timeStr = [NSString stringWithFormat:@"%@:%@%@", [milTimeStr substringToIndex:timeStrLen - 2], [milTimeStr substringFromIndex:timeStrLen - 2], ampm];
  return timeStr;
}

@end
