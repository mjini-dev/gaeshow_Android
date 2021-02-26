package com.devup.android_shopping_mall.util

const val NO_AUTHOR_HEADERS = 100
const val TOKEN_EXPIRED = 101

const val CAN_USE_NICKNAME = 103
const val CANNOT_USE_NICKNAME = 104
const val DIFFERENT_CURRENT_PASSWORD = 105

const val EMAIL_REG_EXP = "[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}"
const val PASSWORD_REG_EXP = """^.*(?=.*[a-zA-Z])((?=.*\d)|(?=.*\W)).{8,20}${'$'}"""
const val NICKNAME_REG_EXP = """^.{1,10}${'$'}"""
