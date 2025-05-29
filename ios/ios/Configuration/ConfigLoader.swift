//
//  ConfigLoader.swift
//  tv-maniac
//
//  Created by Kioko on 03/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

struct ConfigLoader {

    static func load() throws -> Config {
        guard let infoDictionary = Bundle.main.infoDictionary else {
            throw ApplicationError(
                title: "Configuration Error",
                description: "Could not access Info.plist"
            )
        }

        guard let bundleID = infoDictionary["CFBundleIdentifier"] as? String else {
            throw ApplicationError(
                title: "Configuration Error",
                description: "Could not find bundle identifier in Info.plist"
            )
        }

        guard let appName = infoDictionary["CFBundleName"] as? String else {
            throw ApplicationError(
                title: "Configuration Error",
                description: "Could not find app name in Info.plist"
            )
        }

        let appIconName = "AppIcon"
        var config = Config(bundleID: bundleID, appName: appName, appIconName: appIconName)
        if let configFilePath = Bundle.main.path(forResource: "trakt_oauth_config", ofType: "json") {
            do {
                let jsonText = try String(contentsOfFile: configFilePath)
                let jsonData = jsonText.data(using: .utf8)!
                let decoder = JSONDecoder()
                let oauthConfig = try decoder.decode([String: String].self, from: jsonData)

                config.accessTokenUrl = oauthConfig["accessTokenUrl"] ?? ""
                config.authorizeUrl = oauthConfig["authorizeUrl"] ?? ""
                config.callbackUrl = oauthConfig["callbackUrl"] ?? ""
                config.clientId = oauthConfig["clientId"] ?? ""
                config.clientSecret = oauthConfig["clientSecret"] ?? ""
                config.responseType = oauthConfig["responseType"] ?? ""
            } catch {
                print("Warning: Failed to load OAuth configuration from trakt_oauth_config.json: \(error)")
            }
        } else {
            print("Warning: trakt_oauth_config.json file not found")
        }

        return config
    }
}
