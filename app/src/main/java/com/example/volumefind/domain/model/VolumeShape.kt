package com.example.volumefind.domain.model

enum class VolumeShape(
    val displayName: String,
    val descriptor: String,
    val dimensionCount: Int,
    val defaultDimensions: Map<String, String>
) {
    CUBE(
        displayName = "Cube",
        descriptor = "Equal sides",
        dimensionCount = 1,
        defaultDimensions = mapOf("side" to "5")
    ),
    RECTANGULAR_PRISM(
        displayName = "Rectangular Prism",
        descriptor = "3 dimensions",
        dimensionCount = 3,
        defaultDimensions = mapOf("length" to "10", "width" to "5", "height" to "4")
    ),
    CYLINDER(
        displayName = "Cylinder",
        descriptor = "Radius + height",
        dimensionCount = 2,
        defaultDimensions = mapOf("radius" to "3", "height" to "10")
    ),
    SPHERE(
        displayName = "Sphere",
        descriptor = "Radius",
        dimensionCount = 1,
        defaultDimensions = mapOf("radius" to "3")
    ),
    CONE(
        displayName = "Cone",
        descriptor = "Radius + height",
        dimensionCount = 2,
        defaultDimensions = mapOf("radius" to "3", "height" to "10")
    ),
    SQUARE_PYRAMID(
        displayName = "Square Pyramid",
        descriptor = "Base side + height",
        dimensionCount = 2,
        defaultDimensions = mapOf("baseSide" to "5", "height" to "12")
    ),
    TRIANGULAR_PRISM(
        displayName = "Triangular Prism",
        descriptor = "Base, height & length",
        dimensionCount = 3,
        defaultDimensions = mapOf("triangleBase" to "6", "triangleHeight" to "4", "prismLength" to "10")
    ),
    HEMISPHERE(
        displayName = "Hemisphere",
        descriptor = "Radius",
        dimensionCount = 1,
        defaultDimensions = mapOf("radius" to "3")
    );

    val formulaString: String
        get() = when (this) {
            CUBE -> "V = s³"
            RECTANGULAR_PRISM -> "V = l × w × h"
            CYLINDER -> "V = πr²h"
            SPHERE -> "V = 4/3πr³"
            CONE -> "V = 1/3πr²h"
            SQUARE_PYRAMID -> "V = 1/3s²h"
            TRIANGULAR_PRISM -> "V = 1/2bhL"
            HEMISPHERE -> "V = 2/3πr³"
        }
}
