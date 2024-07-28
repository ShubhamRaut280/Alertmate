package com.shubham.emergencyapplication.Models

data class User(
    var name: String? = null,
    var email: String? = null,
    var phone: Long? = null,
    var id: String = "",
    var image_url: String? = null,
    var family_members: List<String>? = null,
    var emergency: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

//        if (id != other.id) return false
        if (name != other.name) return false
        if (email != other.email) return false
        if (phone != other.phone) return false
        if (image_url != other.image_url) return false
        if (family_members != other.family_members) return false
        if (emergency != other.emergency) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
//        result = 31 * result + id.hashCode()
        result = 31 * result + (image_url?.hashCode() ?: 0)
        result = 31 * result + (family_members?.hashCode() ?: 0)
        result = 31 * result + emergency.hashCode()
        return result
    }
}
