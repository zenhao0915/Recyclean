package com.tarumt.recyclean.util.data

import com.tarumt.recyclean.R

enum class Sellers(
    val sellerName: String,
    val gradeDetails: Grade,
    val sellerLogo: Int = R.drawable.ic_launcher_background,
    val phoneNumber: String = "",
    val address: String = "",
    val operationTime: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    SenHeng(
        "SenHeng",
        Grade.S_Plus,
        R.drawable.senheng_logo,
        phoneNumber = "011-1088 0436",
        address = "Jalan 1/37, Jalan Kepong, 52100 Kuala Lumpur",
        operationTime = "10.00am - 9.00pm",
        latitude = 3.2120,
        longitude = 101.6440
    ),
    TNB(
        "TNB",
        Grade.S_Plus,
        R.drawable.tnb_logo,
        phoneNumber = "1-300-88-5454",
        address = "Jalan Bandar 13, Taman Melawati, 53100 Kuala Lumpur",
        operationTime = "9.00am - 4.00pm",
        latitude = 3.2106,
        longitude = 101.7501
    ),
    CompAsia(
        "CompAsia",
        Grade.S,
        R.drawable.compasia_logo,
        phoneNumber = "011-13216085",
        address = "1 Utama Shopping Centre: Lot B3, Basement 1 (New Wing)",
        operationTime = "10.00am - 10.00pm",
        latitude = 3.1479,
        longitude = 101.6161
    ),
    ERTH(
        "ERTH",
        Grade.A,
        R.drawable.erth_logo,
        phoneNumber = "014-221-1446",
        address = "Ground Floor, G-3A, Kanvas Retail @ Prima 15, Jalan Teknokrat 6, 63000 Cyberjaya, Selangor",
        operationTime = "24/7",
        latitude = 2.9220,
        longitude = 101.6508
    ),
    Shan_Poornam_Metals(
        "Shan Poornam Metals",
        Grade.A,
        R.drawable.spm_logo,
        phoneNumber = "04-508 4841",
        address = "Plot 34 (No. 1479), Lorong Perusahaan Maju 6, Kawasan Perindustrian Perai, Fasa 4, 13600 Perai, Penang, Malaysia",
        operationTime = "8.00am - 6.00pm",
        latitude = 5.3522,
        longitude = 100.3921
    ),
    TES_AMM(
        "TES AMM",
        Grade.B,
        R.drawable.sk_tes,
        phoneNumber = "04-399 1896",
        address = "Plot 225, Jalan Perindustrian Bukit Minyak 7, Kawasan Perindustrian Bukit Minyak, 14100 Simpang Ampat, Penang",
        operationTime = "24/7",
        latitude = 5.2835,
        longitude = 100.4682
    ),
    Virogreen(
        "Virogreen",
        Grade.B,
        R.drawable.virogreen_logo,
        phoneNumber = "04-540 4424",
        address = "No 8A & 8B, Jalan Perda Selatan, Bandar Perda, 14000 Bukit Mertajam, Pulau Pinang",
        operationTime = "24/7",
        latitude = 5.3688,
        longitude = 100.4285
    ),
    Rentwise(
        "Rentwise",
        Grade.C,
        R.drawable.rentwise_png,
        phoneNumber = "03-3341 6552",
        address = "Lorong Keluli 1b, Taman Perindustrian Bukit Raja Selatan, 40000 Shah Alam, Selangor",
        operationTime = "8.30am - 5.30pm",
        latitude = 3.0645,
        longitude = 101.4883
    ),
    PC_Image(
        "PC Image",
        Grade.B,
        R.drawable.pc_image_logo,
        phoneNumber = "03-21459722",
        address = "3-001, 3rd Floor, Plaza Low Yat, Off Jalan Bukit Bintang 55100 Kuala Lumpur",
        operationTime = "11.00am - 9:30pm",
        latitude = 3.1439,
        longitude = 101.7096
    ),
    KPT_Recycle(
        "KPT Recycle",
        Grade.A,
        R.drawable.kpt_logo,
        phoneNumber = "03-5161 3455",
        address = "Lot 924, Jln Dato Sellathevan, Batu 4, Seksyen, Kampung Jawa, 40460 Shah Alam, Selangor",
        operationTime = "8.00am - 5.00pm",
        latitude = 3.0232,
        longitude = 101.4820
    )
}