//
//  LoginData.h
//  Coacheller
//
//  Created by afan on 3/31/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface LoginData : NSObject

@property (strong, nonatomic) NSDate *timeLoginIssued;
@property (strong, nonatomic) NSString *loginType;
@property (strong, nonatomic) NSString *accountIdentifier;
@property (strong, nonatomic) NSString *accountToken;
@property (strong, nonatomic) NSString *emailAddress;

- (NSString *)getTwitterToken;

- (NSString *)getTwitterSecret;

@end
