package com.example.mobilelegendsbuildguideapp.Common

import com.example.mobilelegendsbuildguideapp.Model.User
import java.util.regex.Pattern

object Common {
    val user_tbl = "UsersInformation"
    val user_item_tbl = "SavedItems"
    val items_tbl = "Items"
    val user_rating = "UserRateDetails"
    val item_rating = "ItemRateDetails"

    //SharedPreferences
    val SHARED_PREFS = "sharedPrefs"
    val USERNAME = "username"
    val EMAIL = "email_address"

    //var currentUser: User = User()

    val PASSWORD_PATTERN: Pattern = Pattern.compile("^" +
            "(?=.*[0-9])" +         //at least 1 digit
            "(?=.*[a-z])" +         //at least 1 lower case letter
            "(?=.*[A-Z])" +         //at least 1 upper case letter
            //"(?=.*[@#$%^&+=])" +    //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{6,}" +               //at least 6 characters
            "$")

    val SP_CHARS_PATTERN: Pattern = Pattern.compile(".*[`~!@#$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?].*")
}