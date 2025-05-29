//
//  Config.swift
//  tv-maniac
//
//  Created by Kioko on 03/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

struct Config: Decodable {
    var BUNDLE_ID: String
    var APP_NAME: String
    var APPICON_NAME: String

    var authorizeUrl: String = ""
    var accessTokenUrl: String = ""
    var callbackUrl: String = ""
    var clientId: String = ""
    var clientSecret: String = ""
    var responseType: String = ""

    init() {
        BUNDLE_ID = ""
        APP_NAME = ""
        APPICON_NAME = ""
    }

    init(bundleID: String, appName: String, appIconName: String) {
        BUNDLE_ID = bundleID
        APP_NAME = appName
        APPICON_NAME = appIconName
    }

    func getAuthorizeUrl() throws -> URL {
        guard let url = URL(string: authorizeUrl) else {
            throw ApplicationError(
                title: "Invalid Configuration Error",
                description: "The authorizeUrl could not be parsed"
            )
        }
        return url
    }

    func getAccessTokenUrl() throws -> URL {
        guard let url = URL(string: accessTokenUrl) else {
            throw ApplicationError(
                title: "Invalid Configuration Error",
                description: "The accessTokenUrl could not be parsed"
            )
        }
        return url
    }

    func getCallbackURL() throws -> URL {
        guard let url = URL(string: callbackUrl) else {
            throw ApplicationError(
                title: "Invalid Configuration Error",
                description: "The callbackURL could not be parsed"
            )
        }
        return url
    }
}
