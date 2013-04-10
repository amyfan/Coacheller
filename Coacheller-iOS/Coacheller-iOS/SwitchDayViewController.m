//
//  SwitchDayViewController.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/8/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "SwitchDayViewController.h"

@interface SwitchDayViewController ()

@property (strong, nonatomic) IBOutlet UIStepper *yearStepper;
@property (strong, nonatomic) IBOutlet UILabel *yearLabel;
@property (strong, nonatomic) IBOutlet UIStepper *weekendStepper;
@property (strong, nonatomic) IBOutlet UILabel *weekendLabel;
@property (strong, nonatomic) IBOutlet UIPickerView *chooseDay;
@property (nonatomic, strong) NSArray *daysArray;

@end

@implementation SwitchDayViewController

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
  self.yearStepper.minimumValue = 2012;
  self.yearStepper.maximumValue = 2013;
  self.yearStepper.stepValue = 1;
  self.yearStepper.wraps = NO;
  self.yearStepper.autorepeat = YES;
  self.yearStepper.continuous = YES;
  
  // TODO adjust to current year
  self.yearLabel.text = [NSString stringWithFormat:@"%.f", self.yearStepper.value];

  self.weekendStepper.minimumValue = 1;
  self.weekendStepper.maximumValue = 2;
  self.weekendStepper.stepValue = 1;
  self.weekendStepper.wraps = NO;
  self.weekendStepper.autorepeat = YES;
  self.weekendStepper.continuous = YES;
  
  // TODO adjust to current year
  self.weekendLabel.text = [NSString stringWithFormat:@"%.f", self.weekendStepper.value];
  
  // TODO: Only if we want to shrink the picker a tad
  //CGSize pickerSize = [self.chooseDay sizeThatFits:CGSizeZero];
  //UIView *pickerTransformView = [[UIView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, pickerSize.width, pickerSize.height)];
  //pickerTransformView.transform = CGAffineTransformMakeScale(0.75f, 0.75f);
  //[pickerTransformView addSubview:self.chooseDay];
  //[self.view addSubview:pickerTransformView];

  self.chooseDay.delegate = self;
  self.chooseDay.dataSource = self;
  self.daysArray = [[NSArray alloc] initWithObjects:@"Friday", @"Saturday", @"Sunday", nil];
}

#pragma mark - UIPickerView DataSource
// returns the number of 'columns' to display.
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
  return 1;
}

// returns the # of rows in each component..
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
  return [self.daysArray count];
}

#pragma mark - UIPickerView Delegate
- (CGFloat)pickerView:(UIPickerView *)pickerView rowHeightForComponent:(NSInteger)component
{
  return 30.0;
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
  return [self.daysArray objectAtIndex:row];
}

//If the user chooses from the pickerview, it calls this function;
- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
  //Let's print in the console what the user had chosen;
  NSLog(@"Chosen item: %@", [self.daysArray objectAtIndex:row]);
}
@end
