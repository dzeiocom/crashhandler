package com.dzeio.crashhandler.utils

import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Simple Wrapper around the Java zip implementation to make it easier to use
 */
class ZipFile {

    private val stream = ByteArrayOutputStream()
    private val output = ZipOutputStream(BufferedOutputStream(stream))

    /**
     * add a file to the zip with the [content] to the specified [path]
     *
     * @param path the path in the Zip
     * @param content the content as a String
     */
    fun addFile(path: String, content: String) = addFile(path, content.toByteArray())

    /**
     * add the [file] to the zip with at the specified [path]
     *
     * @param path the path in the Zip
     * @param file the file to add to the zip
     */
    fun addFile(path: String, file: File) {
        // Read file
        val data = file.inputStream()
        val bytes = data.readBytes()
        data.close()

        return addFile(path, bytes)
    }

    /**
     * add the [content] to the zip with at the specified [path]
     *
     * @param path the path in the Zip
     * @param content the content of the file to add to the zip
     */
    fun addFile(path: String, content: ByteArray) {
        val entry = ZipEntry(path)
        try {
            output.putNextEntry(entry)

            output.write(content)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        output.closeEntry()
    }

    /**
     * Export the Zip file to a ByteArray
     *
     * **note: You can't write to the ZipFile after running this function**
     *
     * @return the Zip File as a [ByteArray]
     */
    fun toByteArray(): ByteArray {
        output.close()
        return stream.toByteArray()
    }
}
