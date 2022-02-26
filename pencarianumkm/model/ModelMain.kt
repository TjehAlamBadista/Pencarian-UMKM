package com.example.pencarianumkm.model

import java.io.Serializable

class ModelMain : Serializable {
    var idUmkm: String? = null
    var nameUmkm: String? = null
    var thumbUmkm: String? = null
    var ratingText: String? = null
    var addressUmkm: String? = null
    var aggregateRating = 0.0
}