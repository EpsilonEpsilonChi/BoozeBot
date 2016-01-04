//
//  User.swift
//  BoozeBot
//
//  Created by Jamie Sookprasong on 3/1/16.
//  Copyright © 2016 Jamie Sookprasong. All rights reserved.
//

import Foundation
import Firebase

struct User {
    let uid: String
    let email: String
    
    init(authData: FAuthData) {
        uid = authData.uid
        email = authData.providerData["email"] as! String
    }
    
    init(uid: String, email: String) {
        self.uid = uid
        self.email = email
    }
}
