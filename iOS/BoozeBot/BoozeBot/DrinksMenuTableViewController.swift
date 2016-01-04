//
//  DrinksMenuTableViewController.swift
//  BoozeBot
//
//  Created by Jamie Sookprasong on 3/1/16.
//  Copyright © 2016 Jamie Sookprasong. All rights reserved.
//

import UIKit
import Firebase
import SwiftHTTP
import JSONJoy

struct Response: JSONJoy {
    let status: String?
    let error: String?
    init(_ decoder: JSONDecoder) {
        status = decoder["status"].string
        error = decoder["error"].string
    }
}

class DrinksMenuTableViewController: UITableViewController {

    var drinksRef: Firebase!
    
    var drinks = [DrinkItem]()
    var user: User!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        drinksRef = Firebase(url: "https://boozebot.firebaseio.com/Menu")
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)

        drinksRef.observeAuthEventWithBlock({ authData in
            if authData != nil {
                self.user = User(authData: authData)
            }
        })
        
        drinksRef.observeEventType(.Value, withBlock: { snapshot in
            var newDrinks = [DrinkItem]()
            
            for drink in snapshot.children {
                let drinkItem = DrinkItem(snapshot: drink as! FDataSnapshot)
                newDrinks.append(drinkItem)
            }
            
            self.drinks = newDrinks
            self.tableView.reloadData()
        })
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return drinks.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("DrinkCell")! as UITableViewCell
        let drinkItem = drinks[indexPath.row]

        cell.textLabel?.text = drinkItem.key
        
        return cell
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
//        let cell = tableView.cellForRowAtIndexPath(indexPath)!
        let drinkItemName = drinks[indexPath.row].key
        print(user)
        print(drinkItemName)
        print("")
        
        let params = ["user" : user.email, "drink" : drinkItemName]
        
        do {
            let opt = try HTTP.POST("https://boozebot-boozebotapi.rhcloud.com/queue_drink", parameters: params)

//            opt.start { response in
//                //do things...
//            }
            opt.start { response in
                if let error = response.error {
                    print("got an error 1: \(error)")
                    return
                }
                let resp = Response(JSONDecoder(response.data))
                if let err = resp.error {
                    print("got an error 2: \(err)")
                }
                if let status = resp.status {
                    print("completed: \(status)")
                }
            }
        } catch let error {
            print("got an error creating the request: \(error)")
        }
        
//        let params = ["user" : user.email, "drink" : drinkItemName]
//        let url = "https://boozebot-boozebotapi.rhcloud.com/queue_drink"
//        let request = NSMutableURLRequest(URL: NSURL(string: url)!)
//        
//        let session = NSURLSession.sharedSession()
//        request.HTTPMethod = "POST"
//        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
//        request.addValue("application/json", forHTTPHeaderField: "Accept")
//        
////        request.HTTPBody = try! NSJSONSerialization.dataWithJSONObject(params, options: [])
//        
//        // or if you think the conversion might actually fail (which is unlikely if you built `params` yourself)
//        
//         do {
//            request.HTTPBody = try NSJSONSerialization.dataWithJSONObject(params, options: [])
//         } catch {
//            print(error)
//         }
//        
//        let task = session.dataTaskWithRequest(request) { data, response, error in
//            guard data != nil else {
//                print("no data found: \(error)")
//                return
//            }
//
//        // this, on the other hand, can quite easily fail if there's a server error, so you definitely
//        // want to wrap this in `do`-`try`-`catch`:
//        
//        do {
//            if let json = try NSJSONSerialization.JSONObjectWithData(data!, options: []) as? NSDictionary {
//                let success = json["success"] as? Int                                  // Okay, the `json` is here, let's get the value for 'success' out of it
//                print("Success: \(success)")
//            } else {
//                let jsonStr = NSString(data: data!, encoding: NSUTF8StringEncoding)    // No error thrown, but not NSDictionary
//                print("Error could not parse JSON 1: \(jsonStr)")
//            }
//        } catch let parseError {
//            print(parseError)                                                          // Log the error thrown by `JSONObjectWithData`
//            let jsonStr = NSString(data: data!, encoding: NSUTF8StringEncoding)
//            print("Error could not parse JSON 2: '\(jsonStr)'")
//            }
//        }
//        
//        task.resume()
        
        
//        let json = ["user" : user.email, "drink" : drinkItemName]
//        let jsonData = NSJSONSerialization.dataWithJSONObject(json, options: .PrettyPrinted)
//        
//        let url = NSURL(string: "https://boozebot-boozebotapi.rhcloud.com/queue_drink")!
//        let postRequest = NSMutableURLRequest(URL: url)
//        postRequest.HTTPMethod = "POST"
//        postRequest.HTTPBody = jsonData
//        
//        let task = NSURLSession.sharedSession().dataTaskWithRequest(postRequest) { data, response, error in
//            if error != nil {
//                print(error!.localizedDescription)
//                return
//            }
//            if let responseJSON = try NSJSONSerialization.JSONObjectWithData(data!, options: nil) as? [String:AnyObject] {
//                print(responseJSON)
//            }
//        }
//        
//        task.resume()
        
//        let postRequest = NSMutableURLRequest(URL: NSURL(string: "https://boozebot-boozebotapi.rhcloud.com/queue_drink")!) // TODO: make SECURE
//        postRequest.HTTPMethod = "POST"
////        let postBodyString = "user=\(user)&drink=\(drinkItemName)"
//        var jsonBody: NSData?
//        do {
//            jsonBody = try NSJSONSerialization.dataWithJSONObject(["user": user.email, "drink": drinkItemName], options: .PrettyPrinted)
//        } catch {
//            print("JSON serialization error")
//        }
//
//        postRequest.HTTPBody = jsonBody //postBodyString.dataUsingEncoding(NSUTF8StringEncoding)
//        let task = NSURLSession.sharedSession().dataTaskWithRequest(postRequest) { data, response, error in
//            guard error == nil && data != nil else {
//                print("errorr = \(error)")
//                return
//            }
//            
//            if let httpStatus = response as? NSHTTPURLResponse where httpStatus.statusCode != 200 {
//                print("Status code not 200, but is \(httpStatus.statusCode)")
//                print("response = \(response)")
//            }
//            
//            let responseString = NSString(data: data!, encoding: NSUTF8StringEncoding)
//            print("responseString = \(responseString)")
//        }
//        task.resume()
    }
    
}