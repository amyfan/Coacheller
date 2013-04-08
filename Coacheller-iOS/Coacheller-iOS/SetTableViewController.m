//
//  SetTableViewController.m
//  Coacheller-iOS
//
//  Created by Amy Fan on 4/5/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "SetTableViewController.h"
#import "CalendarUtils.h"
#import "StorageManager.h"
#import "JSONArrayHashMap.h"
#import "AppConstants.h"

@interface SetTableViewController ()

@property (nonatomic, strong) NSMutableData *responseData;
@property (nonatomic, strong) StorageManager *storageManager;
@property (nonatomic, strong) JSONArrayHashMap *myRatings;
@property (nonatomic, strong) NSString *sortMode;

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
  self.yearToQuery = [CalendarUtils whatYearIsToday];
  self.weekToQuery = [CalendarUtils whichWeekIsToday];
  self.dayToQuery = [CalendarUtils suggestDayToQuery];
  
  [headerMutableString appendString:[NSString stringWithFormat:@"%d - Weekend %d, %@", self.yearToQuery, self.weekToQuery, self.dayToQuery]];
  
  headerLabel.text = [NSString stringWithString:headerMutableString];
  
  [headerView addSubview:headerLabel];

  // Add Switch Day button
  UIButton *switchDayButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
  switchDayButton.frame = CGRectMake(10.0, 50, 100.0, 40.0); // x,y,width,height
  [switchDayButton setTitle:@"Switch Day" forState:UIControlStateNormal];
  [switchDayButton addTarget:self action:@selector(switchDay) forControlEvents:UIControlEventTouchUpInside];
  [headerView addSubview:switchDayButton];
  
  // TODO Add Sort Mode UIPickerView (oof)
  
  
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
  // perform a segue programmatically
  [self performSegueWithIdentifier:@"switchDay" sender:self];
  
  // TODO: need to actually create the segue w/ switchDay somewhere in the ether (programmatically)
}

- (IBAction)showDebug {
  NSLog(@"showDebug action called");
  
  // perform a segue programmatically
  [self performSegueWithIdentifier:@"debugScreen" sender:self];
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
        if (![self getLoginData]) {
          // log in user
        }
      } else if ([segue.identifier isEqualToString:@"rateSet"]) {
        if ([segue.destinationViewController respondsToSelector:@selector(rateSet:)]) {
          // TODO: switch to customsetlistadapter collection
          NSString *setId = [self.sets getItemAt:indexPath.row][SET_ID];
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
  
  NSLog(@"viewdidload");
  
  // TODO: put this in a place that gets invoked only once??
  [self initData];
  
  [self getSetsFromServer];
  
  // Uncomment the following line to preserve selection between presentations.
  // self.clearsSelectionOnViewWillAppear = NO;
  
  // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
  // TODO: prolly comment this out hehe
  self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)initData {
  self.sets = [[SetDataForTVC alloc] initWithTimeFieldName:SET_TIME_ONE StageFieldName:SET_STAGE_ONE AndRatingsHashMap:self.myRatings];
  
  self.sortMode = SORT_MODE_TIME;
  
  // TODO: create proper file name
  NSString *saveFileName = @"CoachellerData.plist";
  self.storageManager = [[StorageManager alloc] initWithSaveFileName:saveFileName];
  
  self.myRatings = [[JSONArrayHashMap alloc] initWithKeyName1:RATING_SET_ID AndKeyName2:RATING_WEEKEND];
  
}

- (void)processLoginDataWithLoginType:(NSString *)loginType AccountId:(NSString *)accountId AndAccountToken:(NSString *)accountToken {
  LoginData *loginData = [[LoginData alloc] init];
  loginData.loginType = loginType;
  loginData.accountIdentifier = accountId;
  loginData.accountToken = accountToken;
  
  if ([loginData.loginType isEqualToString:LOGIN_TYPE_GOOGLE] || [loginData.loginType isEqualToString:LOGIN_TYPE_FACEBOOK]) {
    loginData.emailAddress = accountId;
  }
  
  [self saveLoginDataInfo:loginData];
}

- (LoginData *)getLoginData {
  return [self.storageManager getObject:DATA_LOGIN_INFO_KEY];
}

- (void)clearLoginData {
  [self saveLoginDataInfo:nil];
}

- (void)saveLoginDataInfo:(LoginData *)loginData {
  [self.storageManager putObject:loginData WithName:DATA_LOGIN_INFO_KEY];
  [self.storageManager save];
}

- (void)getSetsFromServer {
  
  self.responseData = [NSMutableData data];
  
  NSMutableString *urlMutableString = [[NSMutableString alloc] init];
  
  [urlMutableString appendString:@"https://ratethisfest.appspot.com/coachellerServlet?action=get_ratings&year=2013&day=Friday"];
  
  NSString *authId = @"amyfan@gmail.com";
  NSString *escapedAuthId = [authId stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
  // NSString *escapedAuthId2 = (NSString *)CFBridgingRelease(CFURLCreateStringByAddingPercentEscapes( NULL,	 (CFStringRef)authId,	 NULL,	 (CFStringRef)@"!’\"();:@&=+$,/?%#[]% ", kCFStringEncodingISOLatin1));
  
  [urlMutableString appendString:@"&auth_type=LOGIN_TYPE_GOOGLE"];
  [urlMutableString appendString:@"&auth_id="];
  [urlMutableString appendString:escapedAuthId];
  
  [urlMutableString appendString:@"&auth_token="];
  
  NSString *authToken = @"ya29.AHES6ZQ-xDAF0cSVKUgYiAMCnslgfX0ioi0_YT-qP2zImqzcMg";
  NSString *escapedAuthToken = [authToken stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
  // NSString *escapedAuthToken2 = (NSString *)CFBridgingRelease(CFURLCreateStringByAddingPercentEscapes( NULL,	 (CFStringRef)authToken,	 NULL,	 (CFStringRef)@"!’\"();:@&=+$,/?%#[]% ", kCFStringEncodingISOLatin1));
  
  
  [urlMutableString appendString:escapedAuthToken];
  [urlMutableString appendString:@"&email="];
  [urlMutableString appendString:escapedAuthToken];
  
  // NSString *urlString = [NSString stringWithString:urlMutableString];
  
  NSString *urlString = @"https://ratethisfest.appspot.com/coachellerServlet?action=get_sets&year=2013&day=Friday";
  
  NSURLRequest *request = [NSURLRequest requestWithURL:
                           [NSURL URLWithString:urlString]];
  [[NSURLConnection alloc] initWithRequest:request delegate:self];
}


- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
  NSLog(@"didReceiveResponse");
  [self.responseData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
  [self.responseData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
  NSLog(@"didFailWithError");
  NSLog([NSString stringWithFormat:@"Connection failed: %@", [error description]]);
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
  NSLog(@"connectionDidFinishLoading");
  NSLog(@"Succeeded! Received %d bytes of data",[self.responseData length]);
  [self fetchedData:self.responseData];
  NSLog(@"to reload data");
  
  [self.sets resortSets:self.sortMode];
  [self.tableView reloadData];
}

- (void)fetchedData:(NSData *)responseData {
  // parse out the json data
  NSError *error;
  NSDictionary* json = [NSJSONSerialization JSONObjectWithData:self.responseData options:kNilOptions error:&error];
  
  NSMutableArray *setsArray = [NSMutableArray array];
  
  for (NSDictionary * dataDict in json) {
    [setsArray addObject:dataDict];
  }
  [self.sets setSetsData:setsArray];
  
  // NSLog(@"sets: %@", self.setsArray);
}


#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
  // Return the number of rows in the section.
  // return [self.sets getItemCount];
  return [self.sets getItemCount];
}

- (NSString *)titleForRow:(NSUInteger)row {
  return [self.sets getItemAt:row][SET_ARTIST];
  //return self.setsArray[row][SET_ARTIST];
}

- (NSString *)subtitleForRow:(NSUInteger)row {
  return [self.sets getItemAt:row][SET_STAGE_ONE];
  //return self.setsArray[row][SET_STAGE_ONE];
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
