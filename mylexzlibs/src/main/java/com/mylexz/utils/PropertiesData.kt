/**
 * Copyright @2019 Alexzander Purwoko Widiantoro <purwoko908@gmail.com>
 *
 * Except @author, you must not be able to modify, copying, change this file, because its closed source programs
 * This file is patented by any copyright law
 */

package com.mylexz.utils

import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * [Properties Name]
 * [param1] = [value1]
 * [param-n] = [value-n]
 *
 * [Properties Name-n]
 * [param-n] = [values-n]
 */

class PropertiesData(private val inputStream: InputStream) : Closeable {

    companion object {
        private const val ASSIGN_PARAMS = '='
        private const val PROP_NAME_OPEN = '['
        private const val PROP_NAME_END = ']'
        private const val BREAKLINE = '\n'
        private const val VALUES_INDICATOR = '"'
        private const val COMMENT_INDICATOR = '/'
        private const val MODE_BUFF_PROPNAME: Int = 0x66
        private const val MODE_FIND_A_PARAMS: Int = 0x7f
        private const val ASSIGN_VALUES_MODE: Int = 0x6a
    }

    private var mapProperties: HashMap<String, HashMap<String, String?>?>? = null

    constructor(fileSource: File) : this(FileInputStream(fileSource))


    @Synchronized
    fun attach(): Boolean {

        if (mapProperties != null) mapProperties?.clear()
        mapProperties = HashMap()

        var read: Int
        var mode = 0

        val sbuf = StringBuffer()
        val paramsBuf = StringBuffer()
        val valuesBuf = StringBuffer()
        val hashMapBuf = HashMap<String, String?>()
        while (true) {
            read = inputStream.read()
            if (read == -1) {
                if (!sbuf.isEmpty()) {
                    mapProperties?.put(sbuf.toString(), hashMapBuf.clone() as HashMap<String, String?>?)
                    break
                }
            }
            if (Character.isWhitespace(read.toChar()) and (mode == 0)) continue
            when (read.toChar()) {
                PROP_NAME_OPEN -> {
                    // flush any buffered string and a map
                    if (!sbuf.isEmpty()) {
                        mapProperties?.put(sbuf.toString(), hashMapBuf.clone() as HashMap<String, String?>?)
                        sbuf.delete(0, sbuf.length)
                        hashMapBuf.clear()
                    }
                    mode = MODE_BUFF_PROPNAME
                }
                PROP_NAME_END -> {
                    mode = MODE_FIND_A_PARAMS
                }
                BREAKLINE, ' ' -> {
                    // do nothing
                }
                COMMENT_INDICATOR -> {
                    read = inputStream.read()
                    if (read.toChar() == COMMENT_INDICATOR) {
                        // skipes until breakline
                        while (true) {
                            read = inputStream.read()
                            if ((read == -1) or (read.toChar() == BREAKLINE))
                                break
                        }
                    }
                }
                ASSIGN_PARAMS -> {
                    if (mode == MODE_FIND_A_PARAMS) {
                        mode = ASSIGN_VALUES_MODE
                    }
                }
                VALUES_INDICATOR -> {
                    if (mode == ASSIGN_VALUES_MODE) {
                        while (true) {
                            read = inputStream.read()
                            if (read == -1) break
                            if (read.toChar() == VALUES_INDICATOR)
                                break
                            else if (read.toChar() == '\\') {
                                read = inputStream.read()
                                if (read == -1) break
                                valuesBuf.append(read.toChar())
                            } else
                                valuesBuf.append(read.toChar())
                        }
                        hashMapBuf.put(paramsBuf.toString(), valuesBuf.toString())
                        paramsBuf.delete(0, paramsBuf.length)
                        valuesBuf.delete(0, valuesBuf.length)
                        mode = MODE_FIND_A_PARAMS
                    }
                }

                else -> {
                    when (mode) {
                        MODE_BUFF_PROPNAME -> sbuf.append(read.toChar())
                        MODE_FIND_A_PARAMS -> paramsBuf.append(read.toChar())
                    }
                }
            }

        }
        return true
    }

    fun getAllHashMap(): HashMap<String, HashMap<String, String?>?>? {
        return mapProperties
    }

    @Throws(NullPointerException::class)
    fun getContentOfProperties(nameOfProperties: String): HashMap<String, String?>? {
        return mapProperties!![nameOfProperties]
    }

    fun getAValueOf(pathParams: String): String? {
        val props = StringBuffer()
        val params = StringBuffer()
        var lock = 0
        for (anyChar in pathParams) {
            when (lock) {
                0 -> {
                    if (anyChar == '.') {
                        lock = 1
                    } else
                        props.append(anyChar)
                }
                else -> {
                    params.append(anyChar)
                }
            }
        }
        return getAValueOf(props.toString(), params.toString())
    }

    fun getAValueOf(properties: String, params: String): String? {
        val valueOfProps = mapProperties?.get(properties)
        return valueOfProps?.get(params)
    }


    override fun close() {
        inputStream.close()
        mapProperties?.clear()
    }
}