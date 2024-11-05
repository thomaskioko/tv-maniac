//
//  ShowHeaderInfoView.swift
//  TvManiacUI
//
//  Created by Thomas Kioko on 11/4/24.
//

import SwiftUI
import SwiftUIComponents

public struct ShowHeaderInfoView: View {
  private let title: String
  private let overview: String
  private let status: String?
  private let year: String
  private let language: String?
  private let rating: Double

  public init(title: String, overview: String, status: String?, year: String, language: String?, rating: Double) {
    self.title = title
    self.overview = overview
    self.status = status
    self.year = year
    self.language = language
    self.rating = rating
  }

  public var body: some View {
    VStack(spacing: 0) {
      Text(title)
        .titleFont(size: 30)
        .foregroundColor(.textColor)
        .lineLimit(1)
        .padding(.top, 12)
        .padding([.leading, .trailing], 16)

      OverviewBoxView(overview: overview)

      showDetailMetadata
    }
    .padding([.trailing, .leading, .bottom], 16)
  }

  private var showDetailMetadata: some View {
    ScrollView(.horizontal, showsIndicators: false) {
      HStack(alignment: .center) {

        if let status = status, !status.isEmpty {
          BorderTextView(
            text: status,
            colorOpacity: 0.12,
            borderOpacity: 0.12,
            weight: .bold
          )

          Text("•")
            .font(.avenirNext(size: 8))
            .foregroundColor(.accent)
        }

        Text(year)
          .font(.avenirNext(size: 14))
          .fontWeight(.bold)

        if let language = language {
          Text("•")
            .font(.avenirNext(size: 8))
            .foregroundColor(.accent)

          Text(language)
            .font(.avenirNext(size: 14))
            .fontWeight(.bold)
        }

        Text("•")
          .font(.avenirNext(size: 8))
          .foregroundColor(.accent)

        Text(String(format: "%.1f", rating))
          .font(.avenirNext(size: 14))
          .fontWeight(.bold)

        Text("•")
          .font(.avenirNext(size: 8))
          .foregroundColor(.accent)
      }
    }
  }
}

#Preview {
  VStack {
    ShowHeaderInfoView(
      title: "Arcane",
      overview: "Set in Utopian Piltover and the oppressed underground of Zaun, the story follows the origins of two iconic League of Legends champions and the power that will tear them apart.",
      status: "Ended",
      year: "2024",
      language: "EN",
      rating: 4.8
    )
  }
  .background(Color.background)
}
