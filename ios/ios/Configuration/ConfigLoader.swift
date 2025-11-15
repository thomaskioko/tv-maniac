//
//  ConfigLoader.swift
//  tv-maniac
//
//  Created by Kioko on 03/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

enum ConfigLoader {
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

        config.authorizeUrl = "https://trakt.tv/oauth/authorize"
        config.accessTokenUrl = "https://api.trakt.tv/oauth/token"
        config.responseType = "code"

        #if DEBUG
        let configFileName = "dev"
        #else
        let configFileName = "prod"
        #endif

        guard let configFilePath = Bundle.main.path(forResource: configFileName, ofType: "yaml") else {
            throw ApplicationError(
                title: "Configuration Error",
                description: "\(configFileName).yaml file not found in Resources. " +
                    "Please ensure you have created \(configFileName).yaml from the template file."
            )
        }

        do {
            let yamlText = try String(contentsOfFile: configFilePath)
            let lines = yamlText.components(separatedBy: .newlines)

            for line in lines {
                let components = line.components(separatedBy: ": ")
                guard components.count == 2 else { continue }
                let key = components[0].trimmingCharacters(in: .whitespaces)
                let value = components[1].trimmingCharacters(in: .whitespaces).replacingOccurrences(of: "\"", with: "")

                switch key {
                case "traktClientId":
                    config.clientId = value
                case "traktClientSecret":
                    config.clientSecret = value
                case "traktRedirectUri":
                    config.callbackUrl = value
                default:
                    break
                }
            }
        } catch {
            throw ApplicationError(
                title: "Configuration Error",
                description: "Failed to load \(configFileName).yaml: \(error.localizedDescription)"
            )
        }

        return config
    }
}
