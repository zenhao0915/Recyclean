package com.tarumt.recyclean.util

import com.tarumt.recyclean.R

enum class SellerGrade(var grade: String, val description: String) {
    S_Plus("S+", "Very Great!"),
    S("S", "Great"),
    A("A", "Perfect"),
    B("B", "Good"),
    C("C", "Working")
}

enum class Sellers(
    val sellerName: String,
    val gradeDetails: SellerGrade,
    val sellerLogo: Int = R.drawable.ic_launcher_background
) {
    SenHeng("SenHeng", SellerGrade.S_Plus),
    TNB("TNB", SellerGrade.S_Plus),
    CompAsia("CompAsia", SellerGrade.S),
    ERTH("ERTH", SellerGrade.A),
    Shan_Poornam_Metals("Shan Poornam Metals", SellerGrade.A),
    TES_AMM("TES AMM", SellerGrade.B),
    Reebelo("Reebelo", SellerGrade.A),
    Virogreen("Virogreen", SellerGrade.B),
    Rentwise("Rentwise", SellerGrade.C),
    PC_Image("PC Image", SellerGrade.A),
    JMI("JMI", SellerGrade.B),
    KPT_Recycle("KPT Recycle", SellerGrade.A)
}