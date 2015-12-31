//
//  ViewController.swift
//  BoozeBot
//
//  Created by Jamie Sookprasong on 30/12/15.
//  Copyright © 2015 Jamie Sookprasong. All rights reserved.
//

import UIKit
import Firebase

class LoginViewController: UIViewController {
    
    var ref: Firebase!

    @IBOutlet weak var textFieldLoginEmail: UITextField!
    @IBOutlet weak var textFieldLoginPassword: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        ref = Firebase(url: "https://boozebot.firebaseio.com/")
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func loginAction(sender: UIButton) {
        ref.authUser(textFieldLoginEmail.text, password: textFieldLoginPassword.text,
            withCompletionBlock: { error, authData in
                
                if error != nil {
                    print("Login error")
                } else {
                    print("Login successful")
                }
        
        })
    }

    @IBAction func createAccountAction(sender: UIButton) {
        ref.createUser(textFieldLoginEmail.text, password: textFieldLoginPassword.text,
            withValueCompletionBlock: { error, result in
                if error != nil {
                    print("Account create error")
                } else {
                    let uniqname = self.textFieldLoginEmail.text?.componentsSeparatedByString(".")[0] // Path name can't contain period; e.g. test@eex
                    let uniqnameRef = self.ref.childByAppendingPath("Users/").childByAppendingPath(uniqname)
                    let username = uniqname?.componentsSeparatedByString("@")[0] // e.g. test

                    uniqnameRef.setValue(["uniqname" : username!])
                    
                    let uid = result["uid"] as? String
                    print("Account create success. uid: \(uid)")
                }
        })
    }
}

