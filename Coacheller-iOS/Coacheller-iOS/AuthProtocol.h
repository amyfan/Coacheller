//
//  FacebookAuthProtocol.h
//  Coacheller-iOS
//
//  Created by John Smith on 4/9/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import <Foundation/Foundation.h>


//This Protocol ("interface") is used by any object that performs authentication actions and wants to act properly depending on whether those (usually asynchronous) actions succeeded or failed.
//This might be best named AuthResultResponder
@protocol AuthProtocol <NSObject>


- (void) facebookLoggedOut;
- (void) facebookLoggedIn;
- (void) facebookPostDone;
  

@end
