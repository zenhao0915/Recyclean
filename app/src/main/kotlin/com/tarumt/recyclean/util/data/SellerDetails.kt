package com.tarumt.recyclean.util.data

import com.tarumt.recyclean.R

enum class Sellers(
    val sellerName: String,
    val gradeDetails: Grade,
    val sellerLogo: Int = R.drawable.ic_launcher_background
) {
    SenHeng("SenHeng", Grade.S_Plus),
    TNB("TNB", Grade.S_Plus),
    CompAsia("CompAsia", Grade.S),
    ERTH("ERTH", Grade.A),
    Shan_Poornam_Metals("Shan Poornam Metals", Grade.A),
    TES_AMM("TES AMM", Grade.B),
    Reebelo("Reebelo", Grade.A),
    Virogreen("Virogreen", Grade.B),
    Rentwise("Rentwise", Grade.C),
    PC_Image("PC Image", Grade.A),
    JMI("JMI", Grade.B),
    KPT_Recycle("KPT Recycle", Grade.A)
}