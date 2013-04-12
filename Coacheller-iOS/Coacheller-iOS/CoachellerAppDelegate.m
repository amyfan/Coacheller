//
//  CoachellerAppDelegate.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/5/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "CoachellerAppDelegate.h"
#import <FacebookSDK/FacebookSDK.h>


@interface CoachellerAppDelegate()



@end

@implementation CoachellerAppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  // Override point for customization after application launch.
  NSLog(@"CoachellerAppDelegate: Launched");
  NSString *bundleIdentifier = [[NSBundle mainBundle] bundleIdentifier];
    NSLog(@"Bundle Identifier (Must match Google/Facebook/Twitter app configuration: %@",bundleIdentifier);
  self.authController = [[AuthController alloc] init];
  
  
  return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application
{
  // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
  // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
  // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
  // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
  // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
  // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
  
  // We need to properly handle activation of the application with regards to Facebook Login
  // (e.g., returning from iOS 6.0 Login Dialog or from fast app switching).
  [FBSession.activeSession handleDidBecomeActive];
}

- (void)applicationWillTerminate:(UIApplication *)application
{
  // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
  [FBSession.activeSession close];
}

- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
  sourceApplication:(NSString *)sourceApplication
         annotation:(id)annotation
{
  NSLog(@"CoachellerAppDelegate: application/openURL/sourceApplication/annotation handler called");
  return [FBSession.activeSession handleOpenURL:url];
}


@end
