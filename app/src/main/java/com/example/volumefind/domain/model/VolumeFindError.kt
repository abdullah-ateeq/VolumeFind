package com.example.volumefind.domain.model

sealed interface VolumeFindError {
    data object EmptyInput : VolumeFindError
    data object InvalidNumber : VolumeFindError
    data object NonPositiveValue : VolumeFindError
    data object InputTooLong : VolumeFindError
    data object MissingDimension : VolumeFindError
}
