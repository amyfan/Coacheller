//
//  CoachSetTVC.m
//  Coacheller
//
//  Created by Amy Fan on 4/4/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "CoachSetTVC.h"

@interface CoachSetTVC ()

@property (nonatomic, strong) NSMutableData *responseData;

@property (nonatomic, strong) NSMutableArray *setsArray;

@end

@implementation CoachSetTVC

@synthesize responseData = _responseData;

// lazy instantiation good esp for embedded device
//-(SetCollectionsForTVC *) sets
//{
//  if (!self.sets) {
//    self.sets = [[SetCollectionsForTVC alloc] initWithTimeFieldName:@"time_one" StageFieldName:@"stage_one" AndRatingsHashMap:nil];
//  }
//  return self.sets;
//}

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
  [self.tableView reloadData];
}

- (void)fetchedData:(NSData *)responseData {
  // parse out the json data
  NSError *error;
  NSDictionary* json = [NSJSONSerialization JSONObjectWithData:self.responseData options:kNilOptions error:&error];
  
  self.setsArray = [NSMutableArray array];
  
  for (NSDictionary * dataDict in json) {
    [self.setsArray addObject:dataDict];
  }
  
  // NSLog(@"sets: %@", self.setsArray);
}

- (void)processJsonExample {
  // convert to JSON
  NSError *myError = nil;
  NSDictionary *res = [NSJSONSerialization JSONObjectWithData:self.responseData options:NSJSONReadingMutableLeaves error:&myError];
  
  // show all values
  for(id key in res) {
    
    id value = [res objectForKey:key];
    
    NSString *keyAsString = (NSString *)key;
    NSString *valueAsString = (NSString *)value;
    
    NSLog(@"key: %@", keyAsString);
    NSLog(@"value: %@", valueAsString);
  }
  
  // extract specific value...
  NSArray *results = [res objectForKey:@"results"];
  
  for (NSDictionary *result in results) {
    NSString *icon = [result objectForKey:@"icon"];
    NSLog(@"icon: %@", icon);
  }
}

// TODO
//- (void)setSets:(SetCollectionsForTVC *)sets {
//  _sets = sets;
//  [self.tableView reloadData];
//}

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
  [self getSetsFromServer];
  
  // Uncomment the following line to preserve selection between presentations.
  // self.clearsSelectionOnViewWillAppear = NO;
  
  // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
  // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)getSetsFromServer {

  self.responseData = [NSMutableData data];
  
  NSMutableString *urlMutableString = [[NSMutableString alloc] init];
  
  [urlMutableString appendString:@"https://ratethisfest.appspot.com/coachellerServlet?action=get_ratings&year=2013&day=Friday"];
  
  NSString *authId = @"amyfan@gmail.com";
  NSString *escapedAuthId = (NSString *)CFBridgingRelease(CFURLCreateStringByAddingPercentEscapes( NULL,	 (CFStringRef)authId,	 NULL,	 (CFStringRef)@"!’\"();:@&=+$,/?%#[]% ", kCFStringEncodingISOLatin1));
  
  [urlMutableString appendString:@"&auth_type=LOGIN_TYPE_GOOGLE"];
  [urlMutableString appendString:@"&auth_id="];
  [urlMutableString appendString:escapedAuthId];
  
  [urlMutableString appendString:@"&auth_token="];
  
  NSString *authToken = @"ya29.AHES6ZQ-xDAF0cSVKUgYiAMCnslgfX0ioi0_YT-qP2zImqzcMg";
  NSString *escapedAuthToken = (NSString *)CFBridgingRelease(CFURLCreateStringByAddingPercentEscapes( NULL,	 (CFStringRef)authToken,	 NULL,	 (CFStringRef)@"!’\"();:@&=+$,/?%#[]% ", kCFStringEncodingISOLatin1));
  
  
  [urlMutableString appendString:escapedAuthToken];
  [urlMutableString appendString:@"&email="];
  [urlMutableString appendString:escapedAuthToken];
  
  // NSString *urlString = [NSString stringWithString:urlMutableString];
  
  NSString *urlString = @"https://ratethisfest.appspot.com/coachellerServlet?action=get_sets&year=2013&day=Friday";
  
  NSURLRequest *request = [NSURLRequest requestWithURL:
                           [NSURL URLWithString:urlString]];
  [[NSURLConnection alloc] initWithRequest:request delegate:self];}

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
