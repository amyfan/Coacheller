//
//  SwitchDayViewController.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/8/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "SwitchDayViewController.h"
#import "SetTableViewController.h"

@interface SwitchDayViewController ()

- (IBAction)yearStepperAction:(id)sender;
- (IBAction)weekendStepperAction:(id)sender;

@property (strong, nonatomic) IBOutlet UIStepper *yearStepper;
@property (strong, nonatomic) IBOutlet UILabel *yearLabel;
@property (strong, nonatomic) IBOutlet UIStepper *weekendStepper;
@property (strong, nonatomic) IBOutlet UILabel *weekendLabel;
@property (strong, nonatomic) IBOutlet UIPickerView *chooseDay;
@property (nonatomic, strong) NSArray *daysArray;
@property (nonatomic, strong) NSString *selectedDay;

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
  // TODO: load these values from resource file
  self.yearStepper.minimumValue = 2012;
  self.yearStepper.maximumValue = 2013;
  self.yearStepper.stepValue = 1;
  self.yearStepper.wraps = NO;
  self.yearStepper.autorepeat = YES;
  self.yearStepper.continuous = YES;
  
  self.yearStepper.value = 2013;
  if (self.defaultYear) self.yearStepper.value = self.defaultYear;
  self.yearLabel.text = [NSString stringWithFormat:@"%.f", self.yearStepper.value];
  
  // TODO: load these values from resource file
  self.weekendStepper.minimumValue = 1;
  self.weekendStepper.maximumValue = 2;
  self.weekendStepper.stepValue = 1;
  self.weekendStepper.wraps = NO;
  self.weekendStepper.autorepeat = YES;
  self.weekendStepper.continuous = YES;
  
  self.weekendStepper.value = 1;
  if (self.defaultWeek) self.weekendStepper.value = self.defaultWeek;
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

  if (self.defaultDay) {
    NSInteger indexOfDay = [self.daysArray indexOfObject:self.defaultDay];
    [self.chooseDay selectRow:indexOfDay inComponent:0 animated:YES];
  }
}

- (IBAction)yearStepperAction:(id)sender {
  double stepperValue = self.yearStepper.value;
  self.yearLabel.text = [NSString stringWithFormat:@"%.f", stepperValue];
}

- (IBAction)weekendStepperAction:(id)sender {
  double stepperValue = self.weekendStepper.value;
  self.weekendLabel.text = [NSString stringWithFormat:@"%.f", stepperValue];
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
  self.selectedDay = [self.daysArray objectAtIndex:row];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
  if ([sender isKindOfClass:[UIButton class]]) {
    // button clicked
    SetTableViewController *setTableViewController = (SetTableViewController *)segue.destinationViewController;
    setTableViewController.yearToQuery = [self.yearLabel.text intValue];
    setTableViewController.weekToQuery = [self.weekendLabel.text intValue];
    setTableViewController.dayToQuery = self.selectedDay;
  }
}

@end
