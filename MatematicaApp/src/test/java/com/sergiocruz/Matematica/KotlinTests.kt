package com.sergiocruz.Matematica

import org.junit.Test

fun numberTest(number: Number?): Number? =
        number!!.toLong().div(10)

fun getNumberOfFishies() = 2.plus(71).plus(233).minus(13)

fun getNumberOfAquarius() = getNumberOfFishies().div(30)

fun stringing() {
    var colorValue = "#89736487"
    var byteInputStream = colorValue.byteInputStream()
    var rainbowColor = null


    var lista: List<String?> = listOf("12", "14", "16", null)

    var lista2: List<String?>? = listOf(null, null)

}

@Test
fun testws() {
    var nullTest: Int? = null

    print(nullTest?.inc() ?: 0)

    val fishName = "Atuna"

    print(when (fishName.length) {
        0 -> "Error"
        in 3..12 -> "Good Fish name"
        else -> "OK you've got something else"
    })

    val arrei = MutableList(6) { it * 2 }
    print(arrei)


    val peixes = listOf("Atum", "sardinhas", "Carapaus", "bacalhau", "lulas")
    peixes.withIndex().forEach { println("There is ${it.value}at index ${it.index}") }

    val peixinhos = arrayOf("asd", "asd")

    for ((index, peixe) in peixes.withIndex()) {
        println(message = "There is $peixe at index $index")
    }

    fun String.isNumber() = this.matches("[0-9]+".toRegex())
    fun Int.isBig() = this > 10

    100000.isBig()

    val phoneNumber = "8899665544"
    println(phoneNumber.isNumber())


    fun String.isNumber(bloco: () -> Unit): Boolean {
        return if (this.matches("[0-9]+".toRegex())) {
            bloco()
            true
        } else false
    }
    "fhjgfjhgf".isNumber(bloco = fun() {

    })

    fun String.iseNumber(bloco: () -> Unit) =
            if (this.matches("[0-9]+".toRegex())) {
                bloco()
                true
            } else false
    "fhjgfjhgf".iseNumber(bloco = fun() {

    })

    val numberAndCount: Pair<Int, Int> = phoneNumber.let { it.toInt() to it.count() }


    val numberAndCount2 = phoneNumber.let(fun(it: String): Pair<Int, Int> {
        return it.toInt() to it.count()
    })



    var listinha = listOf(1, "eaw", 1.2, 'e')
    listinha[1] = 12 // error immutable list

    val listinha2 = arrayOf<Any>(1, "eaw", 1.2, 'e')
    listinha2[0] = "12"






}


