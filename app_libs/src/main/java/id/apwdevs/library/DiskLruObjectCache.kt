package id.apwdevs.library

import com.jakewharton.disklrucache.DiskLruCache
import org.apache.commons.codec.binary.Base64
import java.io.*
import java.util.regex.Pattern

class DiskLruObjectCache constructor(
    source: File,
    version: Int,
    valueCount: Int,
    max_buffsize: Long
) : Closeable {

    private var mCacheDisk: DiskLruCache? = null
    private val source: File
    private var currentSnaps: DiskLruCache.Snapshot? = null

    var maxCacheFileSize: Long
        get() {
            return mCacheDisk?.maxSize ?: 0
        }
        set(value) {
            mCacheDisk?.maxSize = value
        }


    init {
        mCacheDisk = DiskLruCache.open(source, version, valueCount, max_buffsize)
        this.source = source
    }

    constructor(source: File, version: Int, max_buffer: Long) : this(source, version, 1, max_buffer)

    constructor(source: File) : this(source, 1, MAX_BUFFER_SIZE)

    @Throws(IOException::class)
    fun put(key: String, valueStream: InputStream) {
        val encodedKey = BaseEnDoc.encode(key)
        mCacheDisk?.apply {
            val editor = edit(encodedKey)
            var os: OutputStream? = null
            try {
                os = BufferedOutputStream(editor.newOutputStream(0))
                val buf = ByteArray(valueStream.available())
                valueStream.read(buf)

                os.write(buf)
                os.flush()
            } catch (e: IOException) {
                os?.close()
                editor.abort()
                throw IOException(e)
            } finally {
                flush()
                editor.commit()
                os!!.close()
            }
        }
    }

    @Throws(IOException::class)
    fun put(key: String, value: ByteArray) {
        val encodedKey = BaseEnDoc.encode(key)
        mCacheDisk?.apply {
            val editor = edit(encodedKey)
            var os: OutputStream? = null
            try {
                os = BufferedOutputStream(editor.newOutputStream(0))
                os.write(value)
                os.flush()
            } catch (e: IOException) {
                os?.close()
                editor.abort()
                throw IOException(e)
            } finally {
                flush()
                editor.commit()
                os!!.close()
            }
        }

    }

    @Throws(IOException::class)
    fun putObject(key: String, value: Any) {
        val encoded_key = BaseEnDoc.encode(key)
        mCacheDisk?.apply {
            val editor = edit(encoded_key)
            var os: OutputStream? = null
            var oos: ObjectOutputStream? = null
            val bos: ByteArrayOutputStream? = null
            var reachedEx = false
            try {
                os = BufferedOutputStream(editor.newOutputStream(0))
                oos = ObjectOutputStream(os)
                oos.writeObject(value)
                oos.flush()
                os.flush()
            } catch (e: IOException) {
                reachedEx = true
                os?.close()
                oos?.close()
                editor.abort()
                throw IOException(e)
            } finally {
                bos?.close()
                if (reachedEx)
                    editor.abort()
                else {
                    flush()
                    editor.commit()
                    oos!!.close()
                    os!!.close()
                }
            }
        }

    }

    @Throws(IOException::class)
    fun putObjectWithEncode(key: String, value: Any) {
        val encoded_key = BaseEnDoc.encode(key)
        mCacheDisk?.apply {
            val editor = edit(encoded_key)
            var os: OutputStream? = null
            var oos: ObjectOutputStream? = null
            var bos: ByteArrayOutputStream? = null
            var reachedEx = false
            try {

                bos = ByteArrayOutputStream()
                oos = ObjectOutputStream(bos)
                oos.apply {
                    writeObject(value)
                    flush()
                    flush()
                }

                os = BufferedOutputStream(editor.newOutputStream(0))
                os.apply {
                    write(Base64.encodeBase64(bos.toByteArray()))
                    flush()
                }
            } catch (e: IOException) {
                reachedEx = true
                os?.close()
                oos?.close()
                editor.abort()
                throw IOException(e)
            } finally {
                bos?.close()
                if (reachedEx)
                    editor.abort()
                else {
                    flush()
                    editor.commit()
                    oos!!.close()
                    os!!.close()
                }
            }
        }
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    fun getObjectWithDecode(key: String): Any? {
        val encodedKey = BaseEnDoc.encode(key)
        var result: Any? = null
        if (currentSnaps != null)
            throw IOException("The current buffer for reading is not closed!")
        mCacheDisk?.apply {
            currentSnaps = get(encodedKey)
            val stream = currentSnaps?.getInputStream(0)
                ?: throw IOException("Couldn't open the selected key: $key")
//first read the files
            val avail64 = ByteArray(stream.available())
            stream.read(avail64)
            closeReading()
            // pass into ByteArrayInputStream
            val bis = ByteArrayInputStream(Base64.decodeBase64(avail64))
            // read it!
            val ois = ObjectInputStream(bis)
            result = ois.readObject()
            ois.close()
            bis.close()
        }
        return result
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    fun getObject(key: String): Any? {
        val encoded_key = BaseEnDoc.encode(key)
        var result: Any? = null
        if (currentSnaps != null)
            throw IOException("The current buffer for reading is not closed!")

        mCacheDisk?.apply {
            currentSnaps = get(encoded_key)
            val stream = currentSnaps?.getInputStream(0)
                ?: throw IOException("Couldn't open the selected key: $key")
            val ois = ObjectInputStream(stream)
            result = ois.readObject()
            ois.close()
        }
        return result
    }

    @Throws(IOException::class)
    operator fun get(key: String): InputStream? {
        val encoded_key = BaseEnDoc.encode(key)
        var result: InputStream? = null
        if (currentSnaps != null)
            throw IOException("The current buffer for reading is not closed!")
        mCacheDisk?.apply {
            currentSnaps = get(encoded_key)
            result = currentSnaps?.getInputStream(0)
            if (result == null) {
                throw IOException("Couldn't open the selected key: $key")
            }
        }
        return result
    }

    fun closeReading() {
        currentSnaps?.close()
        currentSnaps = null
    }

    @Throws(IOException::class)
    fun readBytesFromKey(key: String): ByteArray? = get(key)?.let {
        return@let ByteArray(it.available()).apply {
            it.read(this)
            it.close()
        }
    }

    @Throws(IOException::class)
    fun removeKey(key: String): Boolean = mCacheDisk?.remove(BaseEnDoc.encode(key)) ?: false

    @Throws(IOException::class)

    fun clean() {
        mCacheDisk?.delete()
    }

    fun getDirectory(): File? {
        return mCacheDisk?.directory
    }

    @Throws(IOException::class)
    fun getEditorFromKey(key: String): DiskLruCache.Editor? {
        return mCacheDisk?.get(BaseEnDoc.encode(key))?.edit()
    }

    fun isKeyExists(key: String): Boolean {
        var resl: DiskLruCache.Snapshot? = null
        try {
            resl = mCacheDisk?.get(BaseEnDoc.encode(key))
            return resl != null
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            resl?.close()
        }
    }

    @Throws(IOException::class)
    override fun close() {
        mCacheDisk?.close()
        mCacheDisk = null
    }


    companion object {

        const val MAX_BUFFER_SIZE = (512 * 1024).toLong()

        private class BaseEnDoc {

            companion object {
                private const val HEADER: Int = '0'.toInt()
                private const val FOOTER = HEADER
                private const val STRING_KEY_PATTERN = "[a-z0-9_-]{1,120}"
                private val LEGAL_KEY_PATTERN = Pattern.compile(STRING_KEY_PATTERN)
                private val matcher = arrayOf(
                    // ABCDEF
                    intArrayOf('a'.toInt(), '0'.toInt()),
                    intArrayOf('b'.toInt(), 'y'.toInt()),
                    intArrayOf('c'.toInt(), 'u'.toInt()),
                    intArrayOf('d'.toInt(), 'g'.toInt()),
                    intArrayOf('e'.toInt(), '9'.toInt()),
                    intArrayOf('f'.toInt(), 'q'.toInt()),
                    // GHIJKL
                    intArrayOf('g'.toInt(), 'v'.toInt()),
                    intArrayOf('h'.toInt(), 'o'.toInt()),
                    intArrayOf('i'.toInt(), '1'.toInt()),
                    intArrayOf('j'.toInt(), 'm'.toInt()),
                    intArrayOf('k'.toInt(), 'e'.toInt()),
                    intArrayOf('l'.toInt(), '8'.toInt()),
                    // MNOPQR
                    intArrayOf('m'.toInt(), '2'.toInt()),
                    intArrayOf('n'.toInt(), 'h'.toInt()),
                    intArrayOf('o'.toInt(), '7'.toInt()),
                    intArrayOf('p'.toInt(), 'c'.toInt()),
                    intArrayOf('q'.toInt(), 'r'.toInt()),
                    intArrayOf('r'.toInt(), 'w'.toInt()),
                    // STUVWX
                    intArrayOf('s'.toInt(), 'k'.toInt()),
                    intArrayOf('t'.toInt(), 'b'.toInt()),
                    intArrayOf('u'.toInt(), '6'.toInt()),
                    intArrayOf('v'.toInt(), 'd'.toInt()),
                    intArrayOf('w'.toInt(), 'j'.toInt()),
                    intArrayOf('x'.toInt(), '3'.toInt()),

                    intArrayOf('y'.toInt(), '4'.toInt()),
                    intArrayOf('z'.toInt(), '_'.toInt()), //**

                    // - _
                    intArrayOf('-'.toInt(), '5'.toInt()),
                    intArrayOf('_'.toInt(), 'z'.toInt()), //**
                    // 0 - 9
                    intArrayOf('0'.toInt(), 'a'.toInt()), //
                    intArrayOf('1'.toInt(), 'p'.toInt()), //
                    intArrayOf('2'.toInt(), 's'.toInt()), //**
                    intArrayOf('3'.toInt(), '-'.toInt()), //
                    intArrayOf('4'.toInt(), 'i'.toInt()), //
                    intArrayOf('5'.toInt(), 'x'.toInt()), //
                    intArrayOf('6'.toInt(), 'l'.toInt()), //
                    intArrayOf('7'.toInt(), 't'.toInt()), //**
                    intArrayOf('8'.toInt(), 'n'.toInt()), //
                    intArrayOf('9'.toInt(), 'f'.toInt())
                )//

                private const val ENCODE = 0
                private const val DECODE = 1

                fun encode(src: String?): String? = src?.let {
                    if (it.isEmpty()) return@let null
                    validateKey(it)
                    StringBuffer().apply {
                        append(HEADER.toChar())
                        for (chr in it) {
                            append(
                                getReplacementChars(chr.toInt(), ENCODE).toChar()
                            )
                        }
                    }.toString()
                }

                fun decode(src: String?): String? = src?.let {
                    if (it.isEmpty()) return@let null
                    if (!(it[0].toInt() == HEADER && it[it.length - 1].toInt() == FOOTER)) return@let null

                    StringBuffer().apply {
                        append(HEADER.toChar())
                        for (pos in 1 until src.length - 1) {
                            append(
                                getReplacementChars(it[pos].toInt(), DECODE).toChar()
                            )
                        }
                    }.toString()
                }


                private fun getReplacementChars(charInput: Int, mode: Int): Int {
                    for (x in matcher.indices) {
                        if (mode == ENCODE) {
                            if (charInput == matcher[x][0]) return matcher[x][1]
                        } else if (mode == DECODE) {
                            if (charInput == matcher[x][1]) return matcher[x][0]
                        }
                    }
                    return charInput
                }

                private fun validateKey(key: String) {
                    val match = LEGAL_KEY_PATTERN.matcher(key)
                    if (!match.matches()) {
                        throw IllegalArgumentException(
                            "keys must match regex "
                                    + STRING_KEY_PATTERN + ": \"" + key + "\""
                        )
                    }
                }
            }
        }
    }

}