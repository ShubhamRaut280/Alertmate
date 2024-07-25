package com.shubham.emergencyapplication.Models

data class FamilyMember(
    val name: String,
    val email: String,
    val phone: Long?,
    val  id: String,
    val img_url :String?,
    val family_members : List<FamilyMember>?
)
