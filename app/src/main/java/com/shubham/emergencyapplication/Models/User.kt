package com.shubham.emergencyapplication.Models

data class User(
    var name: String? = null,
    var email: String? = null,
    var phone: Long? = null,
    var id: String = "",
    var image_url: String? = null,
    var family_members: List<String>? = null
)
