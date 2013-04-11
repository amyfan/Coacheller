//
//  RateSetViewController.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/10/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "RateSetViewController.h"
#import "AppConstants.h"
#import "SetTableViewController.h"

@interface RateSetViewController ()
@property (strong, nonatomic) IBOutlet UILabel *artistLabel;
@property (strong, nonatomic) IBOutlet UIStepper *weekendStepper;
@property (strong, nonatomic) IBOutlet UILabel *weekendLabel;
@property (strong, nonatomic) IBOutlet UIStepper *scoreStepper;
@property (strong, nonatomic) IBOutlet UILabel *scoreLabel;
@property (strong, nonatomic) IBOutlet UITextView *notesText;

- (IBAction)weekendStepperAction:(id)sender;
- (IBAction)scoreStepperAction:(id)sender;

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
  self.artistLabel.text = self.set[JSON_SET_ARTIST];
  
  // TODO: load these values from resource file
  self.weekendStepper.minimumValue = 1;
  self.weekendStepper.maximumValue = 2;
  self.weekendStepper.stepValue = 1;
  self.weekendStepper.wraps = NO;
  self.weekendStepper.autorepeat = YES;
  self.weekendStepper.continuous = YES;
  
  self.weekendStepper.value = self.currentWeekend;
  self.weekendLabel.text = [NSString stringWithFormat:@"%.f", self.weekendStepper.value];
  
  self.scoreStepper.minimumValue = 1;
  self.scoreStepper.maximumValue = 5;
  self.scoreStepper.stepValue = 1;
  self.scoreStepper.wraps = NO;
  self.scoreStepper.autorepeat = YES;
  self.scoreStepper.continuous = YES;
  
  [self setScoreAndNotes];
}

- (IBAction)weekendStepperAction:(id)sender {
  double stepperValue = self.weekendStepper.value;
  self.weekendLabel.text = [NSString stringWithFormat:@"%.f", stepperValue];
  self.currentWeekend = (int)stepperValue;
  [self setScoreAndNotes];
}

- (IBAction)scoreStepperAction:(id)sender {
  double stepperValue = self.scoreStepper.value;
  self.scoreLabel.text = [NSString stringWithFormat:@"%.f", stepperValue];
  [self colorLabelForScore:(int)stepperValue];
}

- (void)setScoreAndNotes {
  NSDictionary *rating;
  NSString *notes = @"";
  if (self.currentWeekend == 1) {
    rating = self.ratingPair.firstObj;
  } else {
    rating = self.ratingPair.secondObj;
  }
  if (rating) {
    self.scoreStepper.value = [rating[JSON_RATING_SCORE] doubleValue];
    [self colorLabelForScore:[rating[JSON_RATING_SCORE] intValue]];
    
    if (rating[JSON_RATING_NOTES]) {
      notes = rating[JSON_RATING_NOTES];
    }
  } else {
    self.scoreStepper.value = 3;
    self.scoreLabel.textColor = [UIColor grayColor];
  }
  self.scoreLabel.text = [NSString stringWithFormat:@"%.f", self.scoreStepper.value];
  self.notesText.text = notes;
}

- (void)colorLabelForScore:(int)score {
  switch (score){
    case 1:
      self.scoreLabel.textColor = [UIColor redColor];
      break;
    case 2:
    case 3:
    case 4:
      self.scoreLabel.textColor = [UIColor purpleColor];
      break;
    case 5:
      self.scoreLabel.textColor = [UIColor blueColor];
      break;
    default:
      self.scoreLabel.textColor = [UIColor blackColor];
      break;
      
  }
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
  if ([sender isKindOfClass:[UIButton class]]) {
    // button clicked

    // send rating info to submit back to TVC
    NSMutableDictionary *rating = [[NSMutableDictionary alloc] init];
    
    NSString *setId = [NSString stringWithFormat:@"%@", self.set[JSON_SET_ID]];
    NSString *score = [NSString stringWithFormat:@"%d", (int)self.scoreStepper.value];
    NSString *notes = self.notesText.text;
    NSString *weekend = [NSString stringWithFormat:@"%d", (int)self.currentWeekend];
    
    [rating setValue:setId forKey:JSON_RATING_SET_ID];
    [rating setValue:weekend forKey:JSON_RATING_WEEKEND];
    [rating setValue:score forKey:JSON_RATING_SCORE];
    [rating setValue:notes forKey:JSON_RATING_NOTES];
    
    SetTableViewController *setTableViewController = (SetTableViewController *)segue.destinationViewController;
    
    setTableViewController.lastRating = rating;
    setTableViewController.yearToQuery = self.queriedYear;
    setTableViewController.weekToQuery = self.queriedWeekend;
    setTableViewController.dayToQuery = self.queriedDay;
    
    [setTableViewController submitRating:rating];
  }
}

@end
