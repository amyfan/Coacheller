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

@property (nonatomic, strong) id <AuthProtocol> facebookAPICaller;

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
  
  
  if ([self existingFacebookSession]) {
    //Reconnect old session, should be safe to do so
    NSLog(@"Reopening existing facebook session");
    
    
    [FBSession openActiveSessionWithReadPermissions:nil
                                       allowLoginUI:YES
                                  completionHandler:
     ^(FBSession *session,
       FBSessionState state, NSError *error) {
       [self handleSessionStateChanged:session state:state error:error];
     }];
    
  } else {
    //We probably need this
    FBSession* session = [[FBSession alloc] init];
    [FBSession setActiveSession:session];
  }
  return self;
}

- (BOOL)existingFacebookSession {
  if ([self.facebookAuthModel existingFacebookSession]) {
    NSLog(@"FacebookAuthController: Saved facebook session exists");
    return YES;
  } else {
    NSLog(@"FacebookAuthController: NO previous saved facebook session");
    return NO;
  }
}

//Facebook auth
- (void) openSession:(UIViewController <AuthProtocol>*)caller {
  
  NSArray *permissions = [[NSArray alloc] initWithObjects:
                          @"email",
                          nil];
  
  [FBSession openActiveSessionWithReadPermissions:permissions
                                     allowLoginUI:YES
                                completionHandler:
   ^(FBSession *session,
     FBSessionState state, NSError *error) {
     [self handleSessionStateChanged:session state:state error:error];
   }];
  self.facebookAPICaller = caller;
}


- (void) killSession:(UIViewController <AuthProtocol>*)caller {
  [FBSession.activeSession closeAndClearTokenInformation];
  
  [caller facebookLoggedOut];
}

//Facebook auth callback
- (void)handleSessionStateChanged:(FBSession *)session
                            state:(FBSessionState) state
                            error:(NSError *)error
{
  switch (state) {
    case FBSessionStateOpen: {
      NSLog(@"FacebookAuthController: FBSessionStateOpen");
      self.facebookAuthModel.appLoggedIn = YES;
      [self.facebookAPICaller facebookLoggedIn];
      
  
      
      // Start the sample Facebook request
      [[FBRequest requestForMe]
       startWithCompletionHandler:
       ^(FBRequestConnection *connection, NSDictionary<FBGraphUser> *result, NSError *error)
       {
         // Did everything come back okay with no errors?
         if (!error && result)
         {
           NSString *email = [result objectForKey:@"email"];
           NSLog(@"Facebook ID: %@", result.id);
           NSLog(@"Email Address: %@", email);
           NSLog(@"First Name: %@", result.first_name);
           NSLog(@"User Name: %@", result.username);
           
           
           //[result.id longLongValue];
           
           //m_nsstrUserName = [[NSString alloc] initWithString:result.first_name];
           
           // Create a texture from the user's profile picture
           //m_pUserTexture = new System::TextureResource();
           //m_pUserTexture->CreateFromFBID(m_uPlayerFBID, 256, 256);
         }
       }];    }
      break;
    case FBSessionStateClosed:
      NSLog(@"FacebookAuthController: FBSessionStateClosed 'Normally'");
      self.facebookAuthModel.appLoggedIn = NO;
      [self.facebookAPICaller facebookLoggedOut];
      break;
      
      
    case FBSessionStateClosedLoginFailed:
      self.facebookAuthModel.appLoggedIn = NO;
      NSLog(@"FacebookAuthController: FBSessionStateClosedLoginFailed");
      [FBSession.activeSession closeAndClearTokenInformation];
      [self.facebookAPICaller facebookLoggedOut];
      
      //[self showLoginView];
      break;
    default:
      break;
  }
  
  if (error) {
    UIAlertView *alertView = [[UIAlertView alloc]
                              initWithTitle:@"Error"
                              message:error.localizedDescription
                              delegate:nil
                              cancelButtonTitle:@"OK"
                              otherButtonTitles:nil];
    [alertView show];
  }
}


- (void) postStatusUpdate:(UIViewController*)caller buttonPushed:(UIButton*)postButton {
  // Post a status update to the user's feed via the Graph API, and display an alert view
  // with the results or an error.
  NSLog(@"postStatusUpdate starting, about to attempt FB native dialog");
  
  // if it is available to us, we will post using the native dialog
  BOOL displayedNativeDialog =
  [FBNativeDialogs presentShareDialogModallyFrom:(UIViewController*)caller
                                     initialText:@"My name is Coacheller, and I approved this message!"
                                           image:nil
                                             url:nil
                                         handler:^(FBNativeDialogResult result, NSError *error){
                                           NSString *resultString;
                                           switch (result) {
                                             case FBNativeDialogResultSucceeded:
                                               resultString = @"FBNativeDialogResultSucceeded";
                                               break;
                                             case FBNativeDialogResultCancelled:
                                               resultString = @"FBNativeDialogResultCancelled";
                                               break;
                                             case FBNativeDialogResultError:
                                               resultString = @"FBNativeDialogResultError";
                                               break;
                                             default:
                                               resultString = @"UNKNOWN";
                                               break;
                                           }
                                           NSLog(@"FacebookAuthController: Native Dialog result: %@ message: %@", resultString, [error localizedDescription]);
                                         }];
  
  if (!displayedNativeDialog) {
    NSLog(@"FacebookAuthController: Facebook native dialog failed, posting static message");
    [self performPublishAction:^{
      NSLog(@"performPublishAction starting");
      // otherwise fall back on a request for permissions and a direct post
      NSString *message = [NSString stringWithFormat:@"This automatic facebook post was selected at random just for you"];
      
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
    NSLog(@"FacebookAuthController: Requesting publish_actions permission, it was not found");
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
