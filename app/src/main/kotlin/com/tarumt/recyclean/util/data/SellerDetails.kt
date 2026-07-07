package com.tarumt.recyclean.util.data

import com.tarumt.recyclean.R

enum class Sellers(
    val sellerName: String,
    val gradeDetails: Grade,
    val sellerLogo: Int = R.drawable.ic_launcher_background
) {
    SenHeng("SenHeng", Grade.S_Plus, R.drawable.senheng_logo),
    TNB("TNB", Grade.S_Plus, R.drawable.tnb_logo),
    CompAsia("CompAsia", Grade.S, R.drawable.compasia_logo),
    ERTH("ERTH", Grade.A, R.drawable.erth_logo),
    Shan_Poornam_Metals("Shan Poornam Metals", Grade.A, R.drawable.spm_logo),
    TES_AMM("TES AMM", Grade.B, R.drawable.sk_tes),
    Reebelo("Reebelo", Grade.A, R.drawable.reebelo_logo),
    Virogreen("Virogreen", Grade.B, R.drawable.virogreen_logo),
    Rentwise("Rentwise", Grade.C, R.drawable.rentwise_png),
    PC_Image("PC Image", Grade.B, R.drawable.pc_image_logo),
    KPT_Recycle("KPT Recycle", Grade.A, R.drawable.kpt_logo)
}