//
//  ApplicationError.swift
//  tv-maniac
//
//  Created by Kioko on 03/04/2023.
//  Copyright © 2023 orgName. All rights reserved.
//

class ApplicationError: Error {
    
    var title: String
    var description: String
    
    init(title: String, description: String) {
        self.title = title
        self.description = description
    }
}

