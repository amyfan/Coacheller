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
#import "AuthController.h"
#import "SwitchDayViewController.h"
#import "RateSetViewController.h"
#import "CoachellerAppDelegate.h"

#define LOGIN_TYPE @"LOGIN_TYPE"
#define LOGIN_ACCOUNT_ID @"LOGIN_ACCOUNT_ID"
#define LOGIN_ACCOUNT_TOKEN @"LOGIN_ACCOUNT_TOKEN"
#define LOGIN_EMAIL @"LOGIN_EMAIL"

@interface SetTableViewController ()

@property (nonatomic, strong) NSMutableData *responseData;
@property (nonatomic, strong) StorageManager *storageManager;
@property (nonatomic, strong) JSONArrayHashMap *myRatings;
@property (nonatomic, strong) NSString *sortMode;
@property (nonatomic, strong) UIButton *sortModeButton;
@property (nonatomic, strong) NSArray *sortModeTitles;

// contains both week's scores
@property (nonatomic, strong) CustomPair *lastRatingPair;

@end

@implementation SetTableViewController

-(UIView *) tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section {
  NSLog(@"*****SetTableViewController: viewForHeaderInSection");
  UIView* headerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 90)];
  headerView.backgroundColor = [UIColor darkGrayColor];
  
  // Add main label
  UILabel* headerLabel = [[UILabel alloc] init];
  headerLabel.frame = CGRectMake(10, 0, headerView.frame.size.width, 30);
  headerLabel.backgroundColor = [UIColor darkGrayColor];
  headerLabel.textColor = [UIColor whiteColor];
  headerLabel.font = [UIFont boldSystemFontOfSize:18];
  
  NSMutableString *headerMutableString = [[NSMutableString alloc] init];
  
  [headerMutableString appendString:[NSString stringWithFormat:@"%d - Weekend %d, %@", self.yearToQuery, self.weekToQuery, self.dayToQuery]];
  
  headerLabel.text = [NSString stringWithString:headerMutableString];
  
  [headerView addSubview:headerLabel];

  // Add Switch Day button
  UIButton *switchDayButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
  switchDayButton.frame = CGRectMake(headerView.frame.size.width - 70, 20, 60, 55); // x,y,width,height
  switchDayButton.titleLabel.font = [UIFont systemFontOfSize:14];
  switchDayButton.titleLabel.lineBreakMode = NSLineBreakByWordWrapping;
  switchDayButton.titleLabel.numberOfLines = 0;
  [switchDayButton setTitle:@"Switch Day =>" forState:UIControlStateNormal];
  [switchDayButton addTarget:self action:@selector(switchDayAction) forControlEvents:UIControlEventTouchUpInside];
  [headerView addSubview:switchDayButton];
  
  // Add Sort Mode label
  UILabel* sortLabel = [[UILabel alloc] init];
  sortLabel.frame = CGRectMake(10, 40, 50, 35);
  sortLabel.backgroundColor = [UIColor darkGrayColor];
  sortLabel.textColor = [UIColor whiteColor];
  sortLabel.font = [UIFont systemFontOfSize:14];
  sortLabel.text = @"Sort By:";
  [headerView addSubview:sortLabel];
  
  // Add Sort Mode Day button
  self.sortModeButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
  self.sortModeButton.frame = CGRectMake(70, 40, 55, 35); // x,y,width,height
  self.sortModeButton.titleLabel.font = [UIFont systemFontOfSize:14];
  NSString *sortModeTitle = @"Time";
  if (self.sortMode) {
    sortModeTitle = [self.sortMode capitalizedString];
  }
  [self.sortModeButton setTitle:sortModeTitle forState:UIControlStateNormal];
  [self.sortModeButton addTarget:self action:@selector(sortModeAction) forControlEvents:UIControlEventTouchUpInside];
  [headerView addSubview:self.sortModeButton];
  
  // Only for testing authentication
  UIButton *debugScreenButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
  int buttonWidth = 40;
  int buttonHeight = 35;
  int rightborder = 30;
  int topborder = 0;
  
  debugScreenButton.frame = CGRectMake(headerView.frame.size.width - rightborder - buttonWidth, topborder, buttonWidth, buttonHeight);
  [debugScreenButton setTitle:@"Debug" forState:UIControlStateNormal];
  [debugScreenButton addTarget:self action:@selector(showDebugAction) forControlEvents:UIControlEventTouchUpInside];
  //[headerView addSubview:debugScreenButton];
  
  
  return headerView;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
  return 90;
}

- (void)viewDidLoad
{
  [super viewDidLoad];
  
  NSLog(@"*****SetTableViewController: viewdidload");
  
  if (!self.yearToQuery) self.yearToQuery = [CalendarUtils whatYearIsToday];
  if (!self.weekToQuery) self.weekToQuery = [CalendarUtils whichWeekIsToday];
  if (!self.dayToQuery) self.dayToQuery = [CalendarUtils suggestDayToQuery];
  
  self.navigationItem.hidesBackButton = YES;
  self.tableView.bounces = NO;
  
  [self initData];
  
  [self getDataFromServer];
  
  // Uncomment the following line to preserve selection between presentations.
  // self.clearsSelectionOnViewWillAppear = NO;
  
  // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
  // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)initData {
  self.sets = [[SetDataForTVC alloc] initWithTimeFieldName:JSON_SET_TIME_ONE StageFieldName:JSON_SET_STAGE_ONE AndRatingsHashMap:self.myRatings];
  
  self.sortMode = SORT_MODE_TIME;
  self.sortModeTitles = @[SORT_MODE_TIME, SORT_MODE_ARTIST, SORT_MODE_STAGE];
  
  // TODO: create proper file name
  NSString *saveFileName = @"CoachellerData.plist";
  self.storageManager = [[StorageManager alloc] initWithSaveFileName:saveFileName];
  
  self.myRatings = [[JSONArrayHashMap alloc] initWithKeyName1:JSON_RATING_SET_ID AndKeyName2:JSON_RATING_WEEKEND];
  
  self.lastRatingPair = [[CustomPair alloc] init];
  
}

- (CoachellerAppDelegate*) sharedAppDelegate {
  return (CoachellerAppDelegate*)[[UIApplication sharedApplication] delegate];
}

- (NSDictionary *)getLoginData {
    return [self.storageManager getObject:DATA_LOGIN_INFO_KEY];
}

- (void)clearLoginData {
  [self.storageManager removeObjectWithKey:DATA_LOGIN_INFO_KEY];
  [self.storageManager save];
}

- (void)saveLoginDataInfo:(NSMutableDictionary *)loginData {
  [self.storageManager putJSONDictionary:loginData WithName:DATA_LOGIN_INFO_KEY];
  [self.storageManager save];
}

#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
  // Return the number of rows in the section.
  // return [self.sets getItemCount];
  return [self.sets getItemCount];
}

// TODO: DETERMINE PROPER WEEK:

- (NSString *)timeForRow:(NSUInteger)row {
  // TODO: timeFieldName instead, tho shouldn't matter yet
  int milTime = [[self.sets getItemAt:row][JSON_SET_TIME_ONE] intValue];
  return [CalendarUtils militaryToCivilianTime:milTime];
}

- (NSString *)artistForRow:(NSUInteger)row {
  return [self.sets getItemAt:row][JSON_SET_ARTIST];
}

- (NSString *)stageForRow:(NSUInteger)row {
  // TODO: timeFieldName instead, tho shouldn't matter yet
  return [self.sets getItemAt:row][JSON_SET_STAGE_ONE];
}

- (NSString *)avgScoreOneForRow:(NSUInteger)row {
  NSString *avgScoreString = @"";
  NSDecimalNumber *zero = [NSDecimalNumber zero];
  NSDecimalNumber *avgScore = [NSDecimalNumber decimalNumberWithString:[[self.sets getItemAt:row][JSON_SET_AVG_SCORE_ONE] stringValue]];
  if ([avgScore compare:zero] ==  NSOrderedDescending) {
    avgScoreString = [NSString stringWithFormat:@"Wk1: %0.01f", [avgScore floatValue]];
  }
  return avgScoreString;
}

- (NSString *)avgScoreTwoForRow:(NSUInteger)row {
  NSString *avgScoreString = @"";
  NSDecimalNumber *zero = [NSDecimalNumber zero];
  NSDecimalNumber *avgScore = [NSDecimalNumber decimalNumberWithString:[[self.sets getItemAt:row][JSON_SET_AVG_SCORE_TWO] stringValue]];
  if ([avgScore compare:zero] ==  NSOrderedDescending) {
    avgScoreString = [NSString stringWithFormat:@"Wk2: %0.01f", [avgScore floatValue]];
  }
  return avgScoreString;
}

- (NSString *)myRatingsForRow:(NSUInteger)row {
  NSString *setId = [[self.sets getItemAt:row][JSON_SET_ID] stringValue];
  
  // Get Ratings for this set Id
  NSDictionary *ratingWk1 = [self.myRatings getObjectWithKeyOne:setId AndKeyTwo:@"1"];
  NSDictionary *ratingWk2 = [self.myRatings getObjectWithKeyOne:setId AndKeyTwo:@"2"];
  
  NSString *score1 = @"*";
  if (ratingWk1) {
    score1 = [ratingWk1[JSON_RATING_SCORE] stringValue];
  }
  
  NSString *score2 = @"*";
  if (ratingWk2) {
    score2 = [ratingWk2[JSON_RATING_SCORE] stringValue];
  }
  
  NSString *myRatingsString = @"";
  if (![score1 isEqualToString:@"*"] || ![score2 isEqualToString:@"*"]) {
    myRatingsString = [NSString stringWithFormat:@"My Rtg: %@/%@", score1, score2];
  }
  
  return myRatingsString;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
  static NSString *CellIdentifier = @"SetCell";
  UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
  
  if (cell) {
    // Configure the cell...
    UILabel *label = (UILabel *)[cell viewWithTag:1];
    label.text = [self timeForRow:indexPath.row];
    label = (UILabel *)[cell viewWithTag:2];
    label.text = [self artistForRow:indexPath.row];
    label = (UILabel *)[cell viewWithTag:3];
    label.text = [[self stageForRow:indexPath.row] uppercaseString];
    label = (UILabel *)[cell viewWithTag:4];
    label.text = [self avgScoreOneForRow:indexPath.row];
    label = (UILabel *)[cell viewWithTag:5];
    label.text = [self avgScoreTwoForRow:indexPath.row];
    label = (UILabel *)[cell viewWithTag:6];
    label.text = [self myRatingsForRow:indexPath.row];
  }
  
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
  
  // first check for login status
  //if (![self getLoginData]) {
  //}
  if (![self getLoginData]) {
    [[self sharedAppDelegate].authController.facebookAuthController openSession:self];
  } else {
    self.lastSetSelected = [self.sets getItemAt:indexPath.row];
    
    NSString *setId = self.lastSetSelected[JSON_SET_ID];
    // Get Ratings for this set Id
    NSDictionary *ratingWk1 = [self.myRatings getObjectWithKeyOne:setId AndKeyTwo:@"1"];
    NSDictionary *ratingWk2 = [self.myRatings getObjectWithKeyOne:setId AndKeyTwo:@"2"];
    
    self.lastRatingPair.firstObj = ratingWk1;
    self.lastRatingPair.secondObj = ratingWk2;
    
    [self performSegueWithIdentifier:@"rateSet" sender:self];
  }
}

- (void)getDataFromServer {
  self.responseData = [NSMutableData data];
  
  NSMutableArray *httpRequests = [NSMutableArray array];
  [httpRequests addObject:[self getSetsRequest]];
  NSURLRequest *ratingsRequest = [self getRatingsRequest];
  if(ratingsRequest) {
    [httpRequests addObject:ratingsRequest];
  }
  
  dispatch_queue_t callerQueue = dispatch_get_current_queue();
  dispatch_queue_t downloadQueue = dispatch_queue_create("Lots of requests", NULL);
  
  dispatch_async(downloadQueue, ^{
    NSMutableArray *dataArray = [NSMutableArray array];
    for (NSURLRequest *request in httpRequests) {
      [dataArray addObject:[NSURLConnection sendSynchronousRequest:request returningResponse:nil error:nil]];
    }
    
    dispatch_async(callerQueue, ^{
      for (id data in dataArray) {
        [self processFetchedData:data];
      }
    });
  });
  
//  [[NSURLConnection alloc] initWithRequest:[self getSetsRequest] delegate:self];
//  
//  [[NSURLConnection alloc] initWithRequest:[self getRatingsRequest] delegate:self];
}

- (NSURLRequest *)getSetsRequest {
  NSMutableString *urlMutableString = [[NSMutableString alloc] init];
  
  [urlMutableString appendString:SERVER_URL_COACHELLER];
  [urlMutableString appendString:@"?"];
  [urlMutableString appendString:PARAM_ACTION];
  [urlMutableString appendString:@"="];
  [urlMutableString appendString:ACTION_GET_SETS];
  
  [urlMutableString appendString:[self appendParamWithName:PARAM_YEAR AndValue:[NSString stringWithFormat:@"%d", self.yearToQuery]]];
  [urlMutableString appendString:[self appendParamWithName:PARAM_DAY AndValue:self.dayToQuery]];
  
  NSString *urlString = [NSString stringWithString:urlMutableString];
  
  NSURLRequest *request = [NSURLRequest requestWithURL:
                           [NSURL URLWithString:urlString]];
  return request;
  //[[NSURLConnection alloc] initWithRequest:request delegate:self];
}

- (NSURLRequest *)getRatingsRequest {  
  if ([self getLoginData]) {
    NSMutableString *urlMutableString = [[NSMutableString alloc] init];
    
    [urlMutableString appendString:SERVER_URL_COACHELLER];
    [urlMutableString appendString:@"?"];
    [urlMutableString appendString:PARAM_ACTION];
    [urlMutableString appendString:@"="];
    [urlMutableString appendString:ACTION_GET_RATINGS];
    
    [urlMutableString appendString:[self appendParamWithName:PARAM_YEAR AndValue:[NSString stringWithFormat:@"%d", self.yearToQuery]]];
    [urlMutableString appendString:[self appendParamWithName:PARAM_DAY AndValue:self.dayToQuery]];
    
    [urlMutableString appendString:[self appendParamWithName:PARAM_AUTH_TYPE AndValue:[self getLoginData][LOGIN_TYPE]]];
    
    NSString *authId = [self getLoginData][LOGIN_ACCOUNT_ID];
    NSString *escapedAuthId = [authId stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [urlMutableString appendString:[self appendParamWithName:PARAM_AUTH_ID AndValue:escapedAuthId]];
    
    NSString *authToken = [self getLoginData][LOGIN_ACCOUNT_TOKEN];
    NSString *escapedAuthToken = [authToken stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [urlMutableString appendString:[self appendParamWithName:PARAM_AUTH_TOKEN AndValue:escapedAuthToken]];
    
    if ([self getLoginData][LOGIN_EMAIL]) {
      [urlMutableString appendString:[self appendParamWithName:PARAM_EMAIL AndValue:[self getLoginData][LOGIN_EMAIL]]];
    }
    
    NSString *urlString = [NSString stringWithString:urlMutableString];
    
    NSURLRequest *request = [NSURLRequest requestWithURL:
                             [NSURL URLWithString:urlString]];
    return request;
    //[[NSURLConnection alloc] initWithRequest:request delegate:self];
    //NSError *error;
    //NSURLResponse *response;
    //NSData *returnData = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
  } else {
    [self.myRatings clearRatings];
    return nil;
  }
}

- (NSString *)appendParamWithName:(NSString *)name AndValue:(NSString *)value {
  NSMutableString *urlMutableString = [[NSMutableString alloc] init];
  [urlMutableString appendString:@"&"];
  [urlMutableString appendString:name];
  [urlMutableString appendString:@"="];
  [urlMutableString appendString:value];
  return [NSString stringWithString:urlMutableString];
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
  [self processFetchedData:self.responseData];
}

- (void)processFetchedData:(NSData *)responseData {
  // parse out the json data
  NSError *error;
  NSDictionary* json = [NSJSONSerialization JSONObjectWithData:responseData options:kNilOptions error:&error];
  
  NSMutableArray *dataMutableArray = [NSMutableArray array];
  
  for (NSDictionary * dataDict in json) {
    [dataMutableArray addObject:dataDict];
  }
  
  // determine whether sets or ratings
  if ([dataMutableArray count] > 0) {
    NSArray *dataArray = [dataMutableArray copy];
    
    NSLog(@"%lu items", (unsigned long)dataArray.count);
    NSLog(@"In format of: %@", [dataArray objectAtIndex:0]);
    if([dataMutableArray objectAtIndex:0][JSON_RATING_SCORE]) {
      // data is of type rating
      [self.storageManager putJSONArray:dataArray WithName:DATA_RATINGS];
      [self.myRatings rebuildDataWith:dataMutableArray];
    } else {
      // data is of type set
      [self.storageManager putJSONArray:dataArray WithName:DATA_SETS];
      [self.sets setSetsData:dataMutableArray];
      [self.sets resortSets:self.sortMode];
    }
  }
  
  NSLog(@"to reload data");
  
  [self.storageManager save];
  [self.tableView reloadData];
}

- (IBAction)switchDayAction {
  NSLog(@"switchDay action called");
  // perform a segue programmatically
  [self performSegueWithIdentifier:@"switchDay" sender:self];
}

- (IBAction)sortModeAction {
  NSLog(@"sortMode action called");
  
  int sortModesIndex = [self.sortModeTitles indexOfObject:self.sortMode];
  sortModesIndex++;
  if(sortModesIndex >= [self.sortModeTitles count]) sortModesIndex = 0;
  self.sortMode = self.sortModeTitles[sortModesIndex];
  [self.sortModeButton setTitle:[self.sortMode capitalizedString] forState:UIControlStateNormal];
  //[self.sortModeButton setTitle:card.contents forState:UIControlStateSelected|UIControlStateDisabled];
  [self.sets resortSets:self.sortMode];
  [self.tableView reloadData];
}

- (IBAction)showDebugAction {
  NSLog(@"showDebug action called");
  
  // perform a segue programmatically
  [self performSegueWithIdentifier:@"debugScreen" sender:self];
}

- (id)initWithStyle:(UITableViewStyle)style
{
  self = [super initWithStyle:style];
  if (self) {
    // Custom initialization
  }
  return self;
}

- (IBAction)unwindFromSwitchDay:(UIStoryboardSegue *)segue {
  SwitchDayViewController *sourceViewController = segue.sourceViewController;
  self.yearToQuery = sourceViewController.yearToQuery;
  self.weekToQuery = sourceViewController.weekToQuery;
  self.dayToQuery = sourceViewController.dayToQuery;
  
  [self getDataFromServer];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
  //if ([sender isKindOfClass:[UIButton class]]) {
  // button clicked
  if ([segue.identifier isEqualToString:@"switchDay"]) {
    SwitchDayViewController *switchDayViewController = (SwitchDayViewController *)segue.destinationViewController;
    switchDayViewController.yearToQuery = self.yearToQuery;
    switchDayViewController.weekToQuery = self.weekToQuery;
    switchDayViewController.dayToQuery = self.dayToQuery;
  } else if ([segue.identifier isEqualToString:@"rateSet"]) {
    
    RateSetViewController *rateSetViewController = (RateSetViewController *)segue.destinationViewController;
    
    rateSetViewController.set = self.lastSetSelected;
    rateSetViewController.ratingPair = self.lastRatingPair;
    rateSetViewController.currentWeekend = self.weekToQuery;
    
    rateSetViewController.queriedYear = self.yearToQuery;
    rateSetViewController.queriedWeekend = self.weekToQuery;
    rateSetViewController.queriedDay = self.dayToQuery;
    
    //if ([segue.destinationViewController respondsToSelector:@selector(rateSet:)]) {
    //[segue.destinationViewController performSelector:@selector(rateSet:) withSetId:setId];
    //}
  }
  
  //} else if ([sender isKindOfClass:[UITableViewCell class]]) {
  // table row selected
  NSIndexPath *indexPath = [self.tableView indexPathForCell:sender];
  if (indexPath) {
    if ([segue.identifier isEqualToString:@"debugScreen"]) {  //DEBUGGING ONLY
    } else {
    }
  //}
  }
}

// this called after awakeFromNib after segue
- (void)submitRating:(NSDictionary *)rating {
  NSLog(@"*****SetTableViewController: submitRating");

  NSMutableURLRequest *request = [NSMutableURLRequest
                                  requestWithURL:[NSURL URLWithString:SERVER_URL_COACHELLER]];
  [request setHTTPMethod:@"POST"];
  
  NSMutableString *params = [[NSMutableString alloc] init];
  
  [params appendString:PARAM_ACTION];
  [params appendString:@"="];
  [params appendString:ACTION_ADD_RATING];
  
  NSDictionary *loginData = [self getLoginData];
  
  if (loginData) {
    
    [params appendString:[self appendParamWithName:PARAM_AUTH_TYPE AndValue:loginData[LOGIN_TYPE]]];
    
    NSString *authId = loginData[LOGIN_ACCOUNT_ID];
    NSString *escapedAuthId = [authId stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [params appendString:[self appendParamWithName:PARAM_AUTH_ID AndValue:escapedAuthId]];
    
    NSString *authToken = loginData[LOGIN_ACCOUNT_TOKEN];
    NSString *escapedAuthToken = [authToken stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    [params appendString:[self appendParamWithName:PARAM_AUTH_TOKEN AndValue:escapedAuthToken]];
    
    if (loginData[LOGIN_EMAIL]) {
      [params appendString:[self appendParamWithName:PARAM_EMAIL AndValue:[self getLoginData][LOGIN_EMAIL]]];
    }
    
    [params appendString:[self appendParamWithName:PARAM_SET_ID AndValue:rating[JSON_RATING_SET_ID]]];
    [params appendString:[self appendParamWithName:PARAM_WEEKEND AndValue:rating[JSON_RATING_WEEKEND]]];
    [params appendString:[self appendParamWithName:PARAM_SCORE AndValue:rating[PARAM_SCORE]]];
    if (rating[JSON_RATING_NOTES]) {
      [params appendString:[self appendParamWithName:PARAM_NOTES AndValue:rating[JSON_RATING_NOTES]]];
    }
    
    [request setHTTPBody:[params dataUsingEncoding:NSUTF8StringEncoding]];
    [[NSURLConnection alloc] initWithRequest:request delegate:self];
    
    // this should get replaced by subsequent server query
    // [self.myRatings addValues:rating];
    
    // viewDidLoad comes next automatically .'. no need to refresh data here
  }
}

// this called after awakeFromNib after segue
- (void)emailRatings {
  NSLog(@"*****SetTableViewController: emailRatings");
  
  NSMutableURLRequest *request = [NSMutableURLRequest
                                  requestWithURL:[NSURL URLWithString:SERVER_URL_COACHELLER]];
  [request setHTTPMethod:@"POST"];
  
  NSMutableString *params = [[NSMutableString alloc] init];
  
  [params appendString:PARAM_ACTION];
  [params appendString:@"="];
  [params appendString:ACTION_EMAIL_RATINGS];
  
  NSDictionary *loginData = [self getLoginData];
  
  if (loginData) {
    if (loginData[LOGIN_EMAIL]) {
      [params appendString:[self appendParamWithName:PARAM_EMAIL AndValue:[self getLoginData][LOGIN_EMAIL]]];
      
      [request setHTTPBody:[params dataUsingEncoding:NSUTF8StringEncoding]];
      [[NSURLConnection alloc] initWithRequest:request delegate:self];
    } else {
      
    }
    
  }
}

- (void)facebookLoggedIn {
  NSLog(@"SetTableViewController: Facebook Login Succeeded");
  // save login data
  if ([self sharedAppDelegate].authController.isLoggedIn) {
    NSLog(@"About to process Login Data!");
    NSLog(@"Facebook ID: %@", [[self sharedAppDelegate].authController getFacebookID]);
    NSLog(@"Email Address: %@", [[self sharedAppDelegate].authController getUserEmailAddress]);
    NSLog(@"First Name: %@", [[self sharedAppDelegate].authController getUserFirstName]);
    NSLog(@"Last Name: %@", [[self sharedAppDelegate].authController getUserLastName]);
    
    
    NSMutableDictionary *loginDict = [[NSMutableDictionary alloc] init];
    [loginDict setObject:LOGIN_TYPE_FACEBOOK forKey:LOGIN_TYPE];
    // storing email as account ID for now (can be changed without affecting account linking on server
    [loginDict setObject:[[self sharedAppDelegate].authController getUserEmailAddress] forKey:LOGIN_ACCOUNT_ID];
    [loginDict setObject:@"gibberishTokenFromIos" forKey:LOGIN_ACCOUNT_TOKEN];
    [loginDict setObject:[[self sharedAppDelegate].authController getUserEmailAddress] forKey:LOGIN_EMAIL];
    
    [self saveLoginDataInfo:loginDict];
  }
}

- (void)facebookLoggedOut {
  NSLog(@"SetTableViewController: Facebook Login Failed or Logged Out");
  [self clearLoginData];
}

- (void)facebookPostDone {
  NSLog(@"SetTableViewController: Facebook Post Done");
}

// this called after initFromCoder after segue
- (void)awakeFromNib {
  [super awakeFromNib];
  NSLog(@"*****SetTableViewController: awake from nib");
  [self initData];
}

@end
