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

        let alert = UIAlertController(title: "Create Account", message: "Welcome to BoozeBot!", preferredStyle: .Alert)
        
        let saveAction = UIAlertAction(title: "Register", style: .Default,
            handler: { (action: UIAlertAction) -> Void in
                let email = alert.textFields![0]
                let password1 = alert.textFields![1]
                let password2 = alert.textFields![2]
                
                if (password1.text == password2.text) {
                    self.ref.createUser(email.text, password: password1.text,
                        withValueCompletionBlock: { error, result in
                            if error != nil {
                                let accountCreateErrorAlert = UIAlertController(title: "Account Not Created!", message: "This account may already exist.", preferredStyle: .Alert)
                                let okayAction = UIAlertAction(title: "Okay", style: .Default,
                                    handler: { (action: UIAlertAction) -> Void in
                                })
                                
                                accountCreateErrorAlert.addAction(okayAction)
                                self.presentViewController(accountCreateErrorAlert, animated: true, completion: nil)
                            } else {
                                let uniqname = email.text!.componentsSeparatedByString(".")[0] // Path name can't contain period; e.g. test@eex
                                let uniqnameRef = self.ref.childByAppendingPath("Users/").childByAppendingPath(uniqname)
                                let username = uniqname.componentsSeparatedByString("@")[0] // e.g. test
                                
                                uniqnameRef.setValue(["uniqname" : username])
                                
//                                let uid = result["uid"] as? String
//                                print("Account create success. uid: \(uid)")
                                
                                let accountCreateSuccessAlert = UIAlertController(title: "Account Created!", message: nil, preferredStyle: .Alert)
                                let drunkAction = UIAlertAction(title: "Let's Get Drunk!", style: .Default,
                                    handler: { (action: UIAlertAction) -> Void in
                                })
                                
                                accountCreateSuccessAlert.addAction(drunkAction)
                                self.presentViewController(accountCreateSuccessAlert, animated: true, completion: nil)
                                
                            }
                    })
                } else {
                    let mismatchPasswordAlert = UIAlertController(title: "Passwords do not match!", message: "Account not created.", preferredStyle: .Alert)
                    let okayAction = UIAlertAction(title: "Okay", style: .Default,
                        handler: { (action: UIAlertAction) -> Void in
                    })
                    
                    mismatchPasswordAlert.addAction(okayAction)
                    self.presentViewController(mismatchPasswordAlert, animated: true, completion: nil)
                }
        })
        
        let cancelAction = UIAlertAction(title: "Cancel", style: .Default,
            handler: { (action: UIAlertAction) -> Void in
        })
        
        alert.addTextFieldWithConfigurationHandler({
            (emailText) -> Void in
            emailText.placeholder = "Email"
        })
        
        alert.addTextFieldWithConfigurationHandler({
            (password1Text) -> Void in
            password1Text.secureTextEntry = true
            password1Text.placeholder = "Password"
        })
        
        alert.addTextFieldWithConfigurationHandler({
            (password2Text) -> Void in
            password2Text.secureTextEntry = true
            password2Text.placeholder = "Confirm Password"
        })
        
        alert.addAction(cancelAction)
        alert.addAction(saveAction)
        
        presentViewController(alert, animated: true, completion: nil)
    }
}

