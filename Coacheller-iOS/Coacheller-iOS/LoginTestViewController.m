//
//  LoginTestViewController.m
//  Coacheller-iOS
//
//  Created by John Smith on 4/7/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "LoginTestViewController.h"
#import "CoachellerAppDelegate.h"

@interface LoginTestViewController ()

@end

@implementation LoginTestViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
      self.buttonPostFacebook.enabled = false;
    }
    return self;
}

- (CoachellerAppDelegate*) sharedAppDelegate {
  return (CoachellerAppDelegate*)[[UIApplication sharedApplication] delegate];
}

//The wrong way to reference auth objects, left over from tutorial
//Accesses through app delegate
- (IBAction)loginFacebookPressed:(UIButton *)sender {
  NSLog(@"Login Facebook Pressed");
  [self.loginTestViewHourglass startAnimating];
  [[self sharedAppDelegate].authController.facebookAuthController openSession:self];
}

- (IBAction)PostFacebookPressed:(UIButton *)sender {
  NSLog(@"Post Facebook Pressed");
  [[self sharedAppDelegate].authController.facebookAuthController postStatusUpdate:self  buttonPushed:sender];
  [self.loginTestViewHourglass startAnimating];
}

//The right way to reference our objects
- (IBAction)logoutFacebookPressed:(UIButton *)sender {
  [[self sharedAppDelegate].authController.facebookAuthController killSession:self];
}


- (void)facebookLoggedIn {
  NSLog(@"LoginTestViewController: Facebook Login Succeeded");
  self.buttonPostFacebook.enabled = TRUE;
  [self.loginTestViewHourglass stopAnimating];
}

- (void)facebookLoggedOut {
  NSLog(@"LoginTestViewController: Facebook Login Failed or Logged Out");
  self.buttonPostFacebook.enabled = FALSE;
  [self.loginTestViewHourglass stopAnimating];
}

- (void)facebookPostDone {
  NSLog(@"LoginTestViewController: Facebook post attempt completed");
  [self.loginTestViewHourglass stopAnimating];
}





- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
