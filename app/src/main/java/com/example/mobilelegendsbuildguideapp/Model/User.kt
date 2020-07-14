package com.example.mobilelegendsbuildguideapp.Model

data class User(
    var username: String? = "",
    var email_address: String? = ""
    //var password: String? = "" //Firebase does not allow client SDK to get the user's password
)


