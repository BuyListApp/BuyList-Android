<img src="https://github.com/BuyListApp/BuyList-Android/blob/master/app/src/main/res/drawable/ic_logo.png" width="400">

***

BuyList is open source [Android](https://en.wikipedia.org/wiki/Android_(operating_system)) app that helps you create a shopping list.

<a href='https://play.google.com/store/apps/details?id=ru.buylist&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/ru_ru/badges/static/images/badges/en_badge_web_generic.png' height="80"/></a>


## Summary

- Making shopping lists.  
- Patterns. They allow you to group products, for example, by purchase frequency or by favorite 
stores. Products from the pattern can be transferred to the shopping list in a couple of clicks, 
which will save your time.
- Recipes. The ability to create and store your favorite recipes, and transfer the ingredients to the shopping list.
- Dictionary of products. It is used to display "word tips" when entering an item. 
The main product names are already included in it, but if this is not enough, you can supplement it yourself.
- The number of lists, patterns and recipes is not limited.
- The added products are automatically grouped by the selected color category.


## Privacy

This app has no ads and does not ask for any permissions.

BuyList doesn't send any data to a cloud and not having permission to access the internet is a strong guarantee of that.

The app collects anonymous data that reports problems and errors while using the app. This data is stored on third party servers (Google Analytics) and is used to improve the application and fix bugs.


## Contributing

If you found a bug, have an idea how to improve the BuyList app or have a question, please create new issue or comment existing one. If you would like to contribute code, fork the repository and send a pull request.


## Screenshots

<table>
  <tr>
    <th>
        <a href="misc/image/buy-lists.jpg" target="_blank">
        <img src="misc/image/buy-lists.jpg" width="200px" alt="image missing"/></a>
    </th>
    <th>
        <a href="misc/image/buy-list-detail.jpg" target="_blank">
        <img src="misc/image/buy-list-detail.jpg" width="200px" alt="image missing"/> </a>
    </th>
    <th>
        <a href="misc/image/buy-list-detail-purchased.jpg" target="_blank">
        <img src="misc/image/buy-list-detail-purchased.jpg" width="200px" alt="image missing"/> </a>
    </th>
    <th>
        <a href="misc/image/new-product.jpg" target="_blank">
        <img src="misc/image/new-product.jpg" width="200px" alt="image missing"/> </a>
    </th>
  </tr>

  <tr>
    <th>
        <a href="misc/image/patterns.jpg" target="_blank">
        <img src="misc/image/patterns.jpg" width="200px" alt="image missing"/> </a>
    </th>
    <th>
        <a href="misc/image/product-dictionary.jpg" target="_blank">
        <img src="misc/image/product-dictionary.jpg" width="200px" alt="image missing"/> </a>
    </th>
    <th>
        <a href="misc/image/recipes.jpg" target="_blank">
        <img src="misc/image/recipes.jpg" width="200px" alt="image missing"/> </a>
    </th>
    <th>
        <a href="misc/image/new-recipe.jpg" target="_blank">
        <img src="misc/image/new-recipe.jpg" width="200px" alt="image missing"/> </a>
    </th>
  </tr>
</table>


## Project characteristics

- Tech-stack
  - [Kotlin](https://kotlinlang.org/) + [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
  - [Android Jetpack](https://developer.android.com/jetpack)
    - [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/)
    - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
    - [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle)
    - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
    - [Room](https://developer.android.com/topic/libraries/architecture/room)
  - [AppIntro](https://github.com/AppIntro/AppIntro)
  - [FlexboxLayout](https://github.com/google/flexbox-layout)
  - [Firebase crashlytics](https://firebase.google.com/products/crashlytics)
  - [Gson](https://github.com/google/gson)
- Architecture
  - A single-activity architecture ([Navigation component](https://developer.android.com/guide/navigation/navigation-getting-started))
  - [Model–view–viewmodel](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel)
  - [Android Architecture components](https://developer.android.com/topic/libraries/architecture) ([ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel), [LiveData](https://developer.android.com/topic/libraries/architecture/livedata), [Navigation](https://developer.android.com/jetpack/androidx/releases/navigation), [SafeArgs](https://developer.android.com/guide/navigation/navigation-pass-data#Safe-args) plugin)


## Getting started

There are a few ways to open this project.

### Android Studio

1. `Android Studio` -> `File` -> `New` -> `From Version control` -> `Git`
2. Enter `https://github.com/BuyListApp/BuyList-Android.git` into URL field

### Command-line + Android Studio

1. Run `git clone https://github.com/BuyListApp/BuyList-Android.git` to clone project
2. Go to `Android Studio` -> `File` -> `Open` and select cloned directory


## License

MIT License

```
Copyright (c) 2020 Olesya Gorbacheva

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and 
associated documentation files (the "Software"), to deal in the Software without restriction, including 
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to 
the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial 
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN 
NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```
