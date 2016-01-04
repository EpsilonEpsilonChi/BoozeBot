//
//  DrinksMenuTableViewController.swift
//  BoozeBot
//
//  Created by Jamie Sookprasong on 3/1/16.
//  Copyright © 2016 Jamie Sookprasong. All rights reserved.
//

import UIKit
import Firebase

class DrinksMenuTableViewController: UITableViewController {

    var drinksRef: Firebase!
    
    var drinks = [DrinkItem]()
    var user: User!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        drinksRef = Firebase(url: "https://boozebot.firebaseio.com/Recipes")
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)

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
    
    
}