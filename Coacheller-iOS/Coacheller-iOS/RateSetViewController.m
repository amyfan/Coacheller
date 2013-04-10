//
//  RateSetViewController.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/10/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "RateSetViewController.h"
#import "AppConstants.h"

@interface RateSetViewController ()
@property (strong, nonatomic) IBOutlet UILabel *artistLabel;

@end

@implementation RateSetViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.
  self.artistLabel.lineBreakMode = NSLineBreakByWordWrapping;
  self.artistLabel.numberOfLines = 0;
  
  NSString *setId = self.set[JSON_SET_ID];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
