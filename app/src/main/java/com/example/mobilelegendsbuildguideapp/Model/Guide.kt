package com.example.mobilelegendsbuildguideapp.Model

data class Guide(
    var CreatorName: String? = "",
    var HeroName: String? = "",
    var EarlyGame: String? = "",
    var MidGame: String? = "",
    var LateGame: String? = ""
    //var password: String? = "" //Firebase does not allow client SDK to get the user's password
)


