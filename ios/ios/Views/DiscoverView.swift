//
//  DiscoverView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 19.08.21.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI

struct DiscoverView: View {
	var body: some View {
		
		ZStack {
			Color("Background")
				.edgesIgnoringSafeArea(.all)
			
			VStack {
				ScrollView {
					
					FeaturedShowsView()
					
					
					HorizontalShowsView()
					
					Spacer()
				}
				
			}
		}
	}
	
}

struct FeaturedShowsView: View {
	@State private var selectedPage = 0
	
	
	let posters = [
		"https://image.tmdb.org/t/p/original/lztz5XBMG1x6Y5ubz7CxfPFsAcW.jpg",
		"https://image.tmdb.org/t/p/original/w21lgYIi9GeUH5dO8l3B9ARZbCB.jpg",
		"https://image.tmdb.org/t/p/original/xKnUNWFsAOaKIviIYBLei02Bauu.jpg",
		"https://image.tmdb.org/t/p/original/8DFYmwvmXjUFLPKKOUUaxqTLtwq.jpg",
		"https://image.tmdb.org/t/p/original/kz14K7vI2KNGyfTyBnYjBpA0pzQ.jpg",
		"https://image.tmdb.org/t/p/original/m9EDCdjZqrd8L8v03VbIM5x673f.jpg"
	].map { URL(string: $0)! }
	
	var body: some View {
		
		VStack {
			TabView(selection: $selectedPage){
				
				ForEach(0..<posters.endIndex) { index in
					
					let url = posters[index]
					
					AsyncImage(
						url: url,
						placeholder: { Text("Loading ...") },
						image: { Image(uiImage: $0).resizable() }
					)
					.frame(height: 450) // 2:3 aspect ratio
					.clipShape(RoundedRectangle(cornerRadius: 10))
					.padding()
					
				}
				
			}
			.indexViewStyle(PageIndexViewStyle(backgroundDisplayMode: .always))
			.tabViewStyle(PageTabViewStyle(indexDisplayMode: .always))
			.onAppear {
				UIPageControl.appearance().currentPageIndicatorTintColor = .white
				UIPageControl.appearance().pageIndicatorTintColor = UIColor.black.withAlphaComponent(0.2)
			}
		}
		.frame(height: 450)
		.padding(.bottom, 20)
	}
}

struct HorizontalShowsView: View {
	var body: some View{
		VStack {
			
			LabelView()
			
			HorizontalShowsGridView()
		}
	}
}

struct LabelView: View {
	var body: some View{
		HStack {
			
			LabelTitleText(text: "Trending This Week")

			Spacer()
			
			Button(action: {}){
				LabelText(text: "More")
			}
		}
		.padding(.leading)
	}
}

struct HorizontalShowsGridView: View {
	let posters = [
		"https://image.tmdb.org/t/p/original/lztz5XBMG1x6Y5ubz7CxfPFsAcW.jpg",
		"https://image.tmdb.org/t/p/original/w21lgYIi9GeUH5dO8l3B9ARZbCB.jpg",
		"https://image.tmdb.org/t/p/original/xKnUNWFsAOaKIviIYBLei02Bauu.jpg",
		"https://image.tmdb.org/t/p/original/8DFYmwvmXjUFLPKKOUUaxqTLtwq.jpg",
		"https://image.tmdb.org/t/p/original/kz14K7vI2KNGyfTyBnYjBpA0pzQ.jpg",
		"https://image.tmdb.org/t/p/original/m9EDCdjZqrd8L8v03VbIM5x673f.jpg"
	].map { URL(string: $0)! }
	
	var body: some View {
		ScrollView(.horizontal) {
			LazyHStack {
				ForEach(0..<posters.endIndex) { index in
					
					let url = posters[index]
					
					AsyncImage(
						url: url,
						placeholder: { Text("Loading ...") },
						image: { Image(uiImage: $0).resizable() }
					)
					.frame(width: 180, height: 180)
					.clipShape(RoundedRectangle(cornerRadius: 5))
				}
			}
		}
		.padding(.leading)
	}
}


struct DiscoverView_Previews: PreviewProvider {
	static var previews: some View {
		DiscoverView()
		
		DiscoverView()
			.preferredColorScheme(.dark)
	}
}
