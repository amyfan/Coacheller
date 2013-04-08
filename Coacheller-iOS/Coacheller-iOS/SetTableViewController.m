//
//  SetTableViewController.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/5/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "SetTableViewController.h"

@interface SetTableViewController ()

@end

@implementation SetTableViewController

-(UIView *) tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
  UIView* headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 100)];
  headerView.backgroundColor = [UIColor darkGrayColor];
  
  // Add label
  UILabel* headerLabel = [[UILabel alloc] init];
  headerLabel.frame = CGRectMake(10, 0, 300, 30);
  headerLabel.backgroundColor = [UIColor darkGrayColor];
  headerLabel.textColor = [UIColor whiteColor];
  headerLabel.font = [UIFont boldSystemFontOfSize:18];
  
  NSMutableString *headerMutableString = [[NSMutableString alloc] init];
  int year = 2013;
  if (self.yearToQuery) {
    year = *(self.yearToQuery);
  }
  int week = 1;
  if (self.weekToQuery) {
    week = *(self.weekToQuery);
  }
  NSString *day = @"Friday";
  if (self.dayToQuery) {
    day = self.dayToQuery;
  }
  
  [headerMutableString appendString:[NSString stringWithFormat:@"%d - Weekend %d, %@", year, week, day]];
  
  headerLabel.text = [NSString stringWithString:headerMutableString];
  
  [headerView addSubview:headerLabel];

  // Add button
  UIButton *switchDayButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
  switchDayButton.frame = CGRectMake(10.0, 50, 100.0, 40.0); // x,y,width,height
  [switchDayButton setTitle:@"Switch Day" forState:UIControlStateNormal];
  [switchDayButton addTarget:self action:@selector(switchDay) forControlEvents:UIControlEventTouchUpInside];
  [headerView addSubview:switchDayButton];
  
  
  UIButton *debugScreenButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
  int buttonWidth = 40;
  int buttonHeight = 40;
  int rightborder = 30;
  int topborder = 30;
  
  debugScreenButton.frame = CGRectMake(headerView.frame.size.width - rightborder - buttonWidth, topborder, buttonWidth, buttonHeight);
  [debugScreenButton setTitle:@"Debug" forState:UIControlStateNormal];
  [debugScreenButton addTarget:self action:@selector(showDebug) forControlEvents:UIControlEventTouchUpInside];
  [headerView addSubview:debugScreenButton];
  
  
  return headerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
  return 100;
}

- (IBAction)switchDay {
  NSLog(@"switchDay action called");
  // TODO: launch segue
}

- (IBAction)showDebug {
  NSLog(@"showDebug action called");
  
  //This is how you perform a segue programmatically (i.e. in response to button click
  [self performSegueWithIdentifier:@"debugScreen" sender:self];
  // TODO: launch segue
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
  if ([sender isKindOfClass:[UIButton class]]) {
    // button clicked
    if ([segue.identifier isEqualToString:@"Switch Day"]) {
      
    }
    
  } else if ([sender isKindOfClass:[UITableViewCell class]]) {
    // table row selected
    NSIndexPath *indexPath = [self.tableView indexPathForCell:sender];
    if (indexPath) {
      if ([segue.identifier isEqualToString:@"debugScreen"]) {  //DEBUGGING ONLY
        if (1) {
          //TODO : Check if user is logged in (logindata is null?)
        }
      } else if ([segue.identifier isEqualToString:@"debugScreen"]) {
        if ([segue.destinationViewController respondsToSelector:@selector(rateSet:)]) {
          // TODO: switch to customsetlistadapter collection
          NSString *setId = self.setsArray[indexPath.row][SET_ID];
          // TODO: create destination controller
          //[segue.destinationViewController performSelector:@selector(rateSet:) withSetId:setId];
        }
      }

    }
  }
}

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
  // Return the number of rows in the section.
  // return [self.sets getItemCount];
  return [self.setsArray count];
}

- (NSString *)titleForRow:(NSUInteger)row {
  //return [self.sets getItemAt:row][SET_ARTIST];
  return self.setsArray[row][SET_ARTIST];
}

- (NSString *)subtitleForRow:(NSUInteger)row {
  //return [self.sets getItemAt:row][SET_STAGE_ONE];
  return self.setsArray[row][SET_STAGE_ONE];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
  static NSString *CellIdentifier = @"Set";
  UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
  
  // Configure the cell...
  cell.textLabel.text = [self titleForRow:indexPath.row];
  cell.detailTextLabel.text = [self subtitleForRow:indexPath.row];
  
  return cell;
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    /*
     <#DetailViewController#> *detailViewController = [[<#DetailViewController#> alloc] initWithNibName:@"<#Nib name#>" bundle:nil];
     // ...
     // Pass the selected object to the new view controller.
     [self.navigationController pushViewController:detailViewController animated:YES];
     */
}

@end
