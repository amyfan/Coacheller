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
        // Custom initialization
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
  [[self sharedAppDelegate] openSession:self];
}

- (IBAction)PostFacebookPressed:(UIButton *)sender {
  NSLog(@"Post Facebook Pressed");
  [[self sharedAppDelegate].authController.facebookAuthController postStatusUpdate:self  buttonPushed:sender];
}

//The right way to reference our objects
- (IBAction)logoutFacebookPressed:(UIButton *)sender {
  [[self sharedAppDelegate].authController.facebookAuthController killSession];
}


- (void)loginFacebookSuccess {
  NSLog(@"LoginTestViewController: Facebook Login Succeeded");
  [self.loginTestViewHourglass stopAnimating];
}

- (void)loginFacebookFailed {
  NSLog(@"LoginTestViewController: Facebook Login Failed");
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
