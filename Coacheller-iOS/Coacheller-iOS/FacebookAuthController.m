//
//  FacebookAuthController.m
//  Coacheller-iOS
//
//  Created by John Smith on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "FacebookAuthController.h"
#import "CoachellerAppDelegate.h"

@interface FacebookAuthController ()

- (void)showAlert:(NSString *)message
           result:(id)result
            error:(NSError *)error;

- (BOOL)existingFacebookSession;
- (CoachellerAppDelegate*) sharedAppDelegate;

@end

@implementation FacebookAuthController

- (CoachellerAppDelegate*) sharedAppDelegate {
  return (CoachellerAppDelegate*)[[UIApplication sharedApplication] delegate];
}

  - (id) init {
  self = [super init];
  
  if (self) {
    _facebookAuthModel = [[FacebookAuthModel alloc] init];
    
  }
  
  [self existingFacebookSession];

  return self;
}

- (BOOL)existingFacebookSession {
    if ([self.facebookAuthModel existingFacebookSession]) {
      NSLog(@"FacebookAuthController: Facebook is Logged In");
      return YES;
  } else {
    NSLog(@"FacebookAuthController: Facebook is Logged Out");
    return NO;
  }
}

-(void)killSession {
  [FBSession.activeSession closeAndClearTokenInformation];
}



- (void) postStatusUpdate:(UIViewController*)caller buttonPushed:(UIButton*)postButton {
  // Post a status update to the user's feed via the Graph API, and display an alert view
  // with the results or an error.
  NSLog(@"postStatusUpdate starting, about to attempt FB native dialog");
  
  // if it is available to us, we will post using the native dialog
  BOOL displayedNativeDialog = [FBNativeDialogs presentShareDialogModallyFrom:caller
                                                                  initialText:nil
                                                                        image:nil
                                                                          url:nil
                                                                      handler:nil];
  NSLog(@"presentShareDialogModallyFrom returned");
  if (!displayedNativeDialog) {
    NSLog(@"presentSharedDialogModallyFrom returned FALSE");
    [self performPublishAction:^{
      NSLog(@"performPublishAction exiting");
      // otherwise fall back on a request for permissions and a direct post
      NSString *message = [NSString stringWithFormat:@"Updating status for %@ at %@", self.facebookAuthModel.loggedInUser.first_name, [NSDate date]];
      
      [FBRequestConnection startForPostStatusUpdate:message
                                  completionHandler:^(FBRequestConnection *connection, id result, NSError *error) {
                                    NSLog(@"ConnectionHandler Starting");
                                    [self showAlert:message result:result error:error];
                                    postButton.enabled = YES;
                                  }];
      
      postButton.enabled = NO;
      NSLog(@"performPublishAction exiting");
    }];
  }
}




// Convenience method to perform some action that requires the "publish_actions" permissions.
- (void) performPublishAction:(void (^)(void)) action {
  // we defer request for permission to post to the moment of post, then we check for the permission
  if ([FBSession.activeSession.permissions indexOfObject:@"publish_actions"] == NSNotFound) {
    // if we don't already have the permission, then we request it now
    [FBSession.activeSession requestNewPublishPermissions:@[@"publish_actions"]
                                          defaultAudience:FBSessionDefaultAudienceFriends
                                        completionHandler:^(FBSession *session, NSError *error) {
                                          if (!error) {
                                            action();
                                          }
                                          //For this example, ignore errors (such as if user cancels).
                                        }];
  } else {
    action();
  }
  
}


// UIAlertView helper for post buttons
- (void)showAlert:(NSString *)message
           result:(id)result
            error:(NSError *)error {
  
  NSString *alertMsg;
  NSString *alertTitle;
  if (error) {
    alertTitle = @"Error";
    if (error.fberrorShouldNotifyUser ||
        error.fberrorCategory == FBErrorCategoryPermissions ||
        error.fberrorCategory == FBErrorCategoryAuthenticationReopenSession) {
      alertMsg = error.fberrorUserMessage;
    } else {
      alertMsg = @"Operation failed due to a connection problem, retry later.";
    }
  } else {
    NSDictionary *resultDict = (NSDictionary *)result;
    alertMsg = [NSString stringWithFormat:@"Successfully posted '%@'.\nPost ID: %@",
                message, [resultDict valueForKey:@"id"]];
    alertTitle = @"Success";
  }
  
  UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:alertTitle
                                                      message:alertMsg
                                                     delegate:nil
                                            cancelButtonTitle:@"OK"
                                            otherButtonTitles:nil];
  [alertView show];
}

@end
