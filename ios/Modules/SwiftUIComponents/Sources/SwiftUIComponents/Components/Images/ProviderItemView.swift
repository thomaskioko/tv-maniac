import SDWebImageSwiftUI
import SwiftUI

public struct ProviderItemView: View {
  private let logoUrl: String?
  private let imageWidth: CGFloat
  private let imageHeight: CGFloat
  private let imageRadius: CGFloat

  public init(
    logoUrl: String?,
    imageWidth: CGFloat = 80,
    imageHeight: CGFloat = 70,
    imageRadius: CGFloat = 4
  ) {
    self.logoUrl = logoUrl
    self.imageWidth = imageWidth
    self.imageHeight = imageHeight
    self.imageRadius = imageRadius
  }

  public var body: some View {
    VStack(alignment: .leading) {
      if let providerUrl = logoUrl {
        WebImage(url: URL(string: providerUrl)) { image in
          image.resizable()
        } placeholder: { profilePlaceholder }
          .aspectRatio(contentMode: .fit)
          .frame(
            width: imageWidth,
            height: imageHeight
          )
          .clipShape(
            RoundedRectangle(
              cornerRadius: imageRadius, style: .continuous
            ))
          .shadow(radius: 2)
      }
    }
  }

  private var profilePlaceholder: some View {
    ZStack {
      Rectangle().fill(.gray.gradient)
      Image(systemName: "tv")
        .resizable()
        .aspectRatio(contentMode: .fit)
        .frame(width: 24, height: 24)
        .foregroundColor(.white)
    }
  }
}

#Preview {
  VStack {
    ProviderItemView(
      logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/aYkLXz4dxHgOrFNH7Jv7Cpy56Ms.png",
      imageWidth: 80,
      imageHeight: 70,
      imageRadius: 4
    )

    ProviderItemView(
      logoUrl: "https://image.tmdb.org/t/p/w780/https://image.tmdb.org/t/p/w780/ 4KAy34EHvRM25Ih8wb82AuGU7zJ.png",
      imageWidth: 80,
      imageHeight: 70,
      imageRadius: 4
    )
  }
}
