/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com>
 *
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package com.mylexz.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class PropertiesDataTest {

    private val hashMapTests: HashMap<String, HashMap<String, String>> = hashMapOf(
        "TestConfig" to hashMapOf(
            "param1" to "HOPEProject",
            "param2" to "Kensiro"
        ),
        "HOPEProject" to hashMapOf(
            "marketing" to "Bagus Cahyono",
            "contentDataWriter" to "Anggi Mundita",
            "lead" to "Roman Aqviriyoso",
            "developerAndSoftEngineer" to "Alexzander Purwoko Widiantoro",
            "dataEngineer" to "Wahyu Catur Pamungkas",
            "preceptor1" to "Purwari Puji Rahayu",
            "preceptor2" to "Anggoro Rindra Nugroho",
            "preceptor3" to "Lian Ratnasari"
        ),
        "Supported" to hashMapOf(
            "param1" to "SMKN 1 Giritontro",
            "param2" to "BPP Giriwoyo \"Harjaning Tani\"",
            "param3" to "BAPPEDA Litbang Wonogiri"
        )
    )

    @Test
    fun runTest() {
        val data = PropertiesData(File("testProperties.prop"))
        data.attach()
        testAll(data)

        data.close()
    }

    private fun testAll(data: PropertiesData) {
        // check all properties first
        val map = data.getAllHashMap()
        val listKey = hashMapTests.keys
        for (anyKey in listKey) {
            assertEquals("Test Property $anyKey", true, map?.containsKey(anyKey))
        }

        // check any params in key
        for (anyKey in listKey) {
            // assert if not found parameters that i expected
            val listInnerMap = hashMapTests[anyKey]
            val listKeyInInnerMap = listInnerMap?.keys!!
            val actualMap = map!![anyKey]
            for (keyInnerMap in listKeyInInnerMap) {
                assertEquals("Test Parameters $anyKey.$keyInnerMap", true, actualMap!!.containsKey(keyInnerMap))
                assertEquals("Test Value in $anyKey.$keyInnerMap", listInnerMap[keyInnerMap], actualMap[keyInnerMap])
            }
        }
    }

}