//
//  TraktManagerError.swift
//  tv-maniac
//
//  Created by Kioko on 01/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation

enum TraktManagerError: Error {
    case tokenNotFound
    case refreshTokenNotFound
    case accessTokenNotSaved

    var errorDescription: String {
        switch self {
        case .tokenNotFound:
            "Token not found"
        case .refreshTokenNotFound:
            "Refresh token not found"
        case .accessTokenNotSaved:
            "Error saving access token and refresh token to keychain:"
        }
    }
}
