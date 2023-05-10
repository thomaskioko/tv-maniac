//
//  Config.swift
//  tv-maniac
//
//  Created by Kioko on 03/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation


struct Config: Decodable {

    let authorizeUrl: String
    let accessTokenUrl: String
    let callbackUrl: String
    let clientId: String
    let clientSecret: String
    let responseType: String
    
    init() {
        self.authorizeUrl = ""
        self.accessTokenUrl = ""
        self.callbackUrl = ""
        self.clientId = ""
        self.clientSecret = ""
        self.responseType = ""
    }
    
    func getAuthorizeUrl() throws -> URL {
        
        guard let url = URL(string: self.authorizeUrl) else {

            throw ApplicationError(
                title: "Invalid Configuration Error",
                description: "The authorizeUrl could not be parsed"
            )
        }
        
        return url
    }

    func getAccessTokenUrl() throws -> URL {
        
        guard let url = URL(string: self.accessTokenUrl) else {

            throw ApplicationError(
                title: "Invalid Configuration Error",
                description: "The accessTokenUrl could not be parsed"
            )
        }
        
        return url
    }
    
    func getCallbackURL() throws -> URL {
        
        guard let url = URL(string: self.callbackUrl) else {

            throw ApplicationError(
                title: "Invalid Configuration Error",
                description: "The callbackURL could not be parsed"
            )
        }
        
        return url
    }

}
