//
//  CoachSetTVC.m
//  Coacheller
//
//  Created by Amy Fan on 4/4/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "CoachSetTVC.h"
#import "LoginData.h"
#import "JSONArrayHashMap.h"

@interface CoachSetTVC ()

@property (nonatomic, strong) NSMutableData *responseData;
@property (nonatomic, strong) LoginData *loginData;
// TODO storage mgr
@property (nonatomic, strong) JSONArrayHashMap *myRatings;

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

// TODO
//- (void)setSets:(SetCollectionsForTVC *)sets {
//  _sets = sets;
//  [self.tableView reloadData];
//}

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

- (void)initData {
  // TODO: init year/week/day
  
  // TODO: obtain login data from storage
  
  self.myRatings = [[JSONArrayHashMap alloc] initWithKeyName1:RATING_SET_ID AndKeyName2:RATING_WEEKEND];
  
}

- (void)processLoginDataWithLoginType:(NSString *)loginType AccountId:(NSString *)accountId AndAccountToken:(NSString *)accountToken {
  if (!self.loginData) {
    self.loginData = [[LoginData alloc] init];
  }
  self.loginData.loginType = loginType;
  self.loginData.accountIdentifier = accountId;
  self.loginData.accountToken = accountToken;
  
  if ([self.loginData.loginType isEqualToString:LOGIN_TYPE_GOOGLE] || [self.loginData.loginType isEqualToString:LOGIN_TYPE_FACEBOOK]) {
    self.loginData.emailAddress = accountId;
  }
}

- (void)clearLoginData {
  self.loginData = nil;
  // TODO: save login data to storage mgr
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
  [[NSURLConnection alloc] initWithRequest:request delegate:self];
}

@end
