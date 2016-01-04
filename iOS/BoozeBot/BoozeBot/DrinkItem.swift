//
//  DrinkItem.swift
//  BoozeBot
//
//  Created by Jamie Sookprasong on 3/1/16.
//  Copyright © 2016 Jamie Sookprasong. All rights reserved.
//

import Foundation
import Firebase

struct DrinkItem {
    let key: String!
//    let name: String!
    let ref: Firebase?
    
    init(key: String = "", name: String) {
        self.key = key
//        self.name = name
        self.ref = nil
    }
    
    init(snapshot: FDataSnapshot) {
        key = snapshot.key
//        name = snapshot.value["name"] as! String
        ref = snapshot.ref
    }
    
    func toAnyObject() -> AnyObject {
        return [
//            "name": name
        ]
    }
}