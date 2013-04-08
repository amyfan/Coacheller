//
//  CoachSetTVC.m
//  Coacheller
//
//  Created by Amy Fan on 4/4/13.
//  Copyright (c) 2013 Fanster. All rights reserved.
//

#import "CoachSetTVC.h"
#import "StorageManager.h"
#import "JSONArrayHashMap.h"

#define DATA_LOGIN_INFO_KEY @"DATA_LOGIN_INFO"

@interface CoachSetTVC ()

@property (nonatomic, strong) NSMutableData *responseData;
@property (nonatomic, strong) StorageManager *storageManager;
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
  
  // TODO: put this in a place that gets invoked only once??
  [self initData];
  
  [self getSetsFromServer];
  
  // Uncomment the following line to preserve selection between presentations.
  // self.clearsSelectionOnViewWillAppear = NO;
  
  // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
  self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (void)initData {
  // TODO: init year/week/day
  
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

@end
