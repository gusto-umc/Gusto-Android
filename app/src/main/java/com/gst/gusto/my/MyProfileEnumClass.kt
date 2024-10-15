package com.gst.gusto.my

enum class Age(val displayName: String) {
    TEEN("10대"),
    TWENTIES("20대"),
    THIRTIES("30대"),
    FOURTIES("40대"),
    FIFTIES("50대"),
    OLDER("60대"),
    NONE("선택하지 않음")
}
fun toAge(displayName: String): Age? {
    return Age.entries.find { it.displayName == displayName }
}

enum class Gender(val displayName: String) {
    FEMALE("여자"),
    MALE("남자"),
    NONE("선택하지 않음");
}
fun toGender(displayName: String): Gender? {
    return Gender.entries.find { it.displayName == displayName }
}