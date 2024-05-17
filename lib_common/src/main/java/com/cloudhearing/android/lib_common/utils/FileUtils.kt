package com.cloudhearing.android.lib_common.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

/**
 * 文件管理工具类
 */
object FileUtils {
    // 得到当前外部存储设备的目录
    val SDCardRoot: String =
        Environment.getExternalStorageDirectory().absolutePath + File.separator
//    val SDCardPublicRoot: String =
//        Environment.getExternalStoragePublicDirectory("").absolutePath + File.separator

    //获取内部存储的文件路径
    fun getFileDir(context: Context, s: String): File {
        val file = File(context.filesDir.absolutePath + File.separator + s + File.separator)
        if (!file.getParentFile()?.exists()!!) {
            file.getParentFile()?.mkdirs()
        }
        if (!file.exists()) file.mkdirs()
        return file
    }

    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun createFileInSDCard(path: String, fileName: String): File {
        val file = File(SDCardRoot + path + File.separator + fileName)
        if (!file.getParentFile()?.exists()!!) {
            file.getParentFile()?.mkdirs()
        }
        if (!file.exists()) file.createNewFile()
        return file
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dir
     */
    fun creatSDDir(dir: String): File {
        val dirFile = File(SDCardRoot + dir + File.separator)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        return dirFile
    }

    /**
     * 在app缓存目录中创建新目录"/data/data/" + activity.getPackageName()
     * + "/files/" + dir
     *
     * @param dir
     */
    fun createLocalDir(activity: Activity, dir: String): File {
        val dirFile = File(
            "/data/data/" + activity.packageName + "/files/" + dir + File.separator
        )
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        return dirFile
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    fun isFileExist(fileName: String, path: String): Boolean {
        val file = File(SDCardRoot + path + File.separator + fileName)
        return file.exists()
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    fun write2SDFromInput(
        path: String, fileName: String, input: InputStream
    ): File? {
        var file: File? = null
        var output: OutputStream? = null
        try {
            creatSDDir(path)
            file = createFileInSDCard(path, fileName)
            output = FileOutputStream(file)
            val buffer = ByteArray(4 * 1024)
            var temp: Int
            while ((input.read(buffer).also { temp = it }) != -1) {
                output.write(buffer, 0, temp)
            }
            output.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                output!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return file
    }

    //存储数据
    fun writeData(file: File?, content: String) {
        var fileOutputStream: FileOutputStream? = null
        try {
            //获取文件输出流对象
            fileOutputStream = FileOutputStream(file)
            //保存填写信息
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.flush() //清除缓存
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            //关闭流
            if (fileOutputStream != null) try {
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    //读取数据
    fun readData(file: File?): String? {
        var fileInputStream: FileInputStream? = null
        try {
            //获取读取文件
            fileInputStream = FileInputStream(file)
            //设置一次读取字节数
            val buff = ByteArray(1024)
            //获取stringBuilder
            val stringBuilder = StringBuilder("")
            var len = 0
            //循环读取
            while ((fileInputStream.read(buff).also { len = it }) > 0) {
                stringBuilder.append(String(buff, 0, len))
            }
            //返回读取数据
            return stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            //关闭流
            try {
                fileInputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    @Throws(Exception::class)
    fun copyFile(f1: File?, f2: File?) {
        val length = 2097152
        val `in` = FileInputStream(f1)
        val out = FileOutputStream(f2)
        val buffer = ByteArray(length)
        while (true) {
            val ins = `in`.read(buffer)
            if (ins == -1) {
                `in`.close()
                out.flush()
                out.close()
                return
            } else out.write(buffer, 0, ins)
        }
    }

    /**
     * 复制某个目录下的文件到另一个目录下
     *
     * @param fromPath
     * @param toPath
     * @throws Exception
     */
    @Throws(Exception::class)
    fun copyFloder(fromPath: String, toPath: String) {
        var fromPath = fromPath
        var toPath = toPath
        val fileFromPath = File(fromPath)
        val fileToPath = File(toPath)
        if (!fileToPath.exists()) {
            fileToPath.mkdirs()
        }
        if (fileFromPath.exists() && fileFromPath.isDirectory()) {
            val fileFromList = fileFromPath.list()
            if (!fromPath.endsWith("/")) {
                fromPath = "$fromPath/"
            }
            if (!toPath.endsWith("/")) {
                toPath = "$toPath/"
            }
            for (i in fileFromList.indices) {
                copyFile(
                    File(fromPath + fileFromList[i]), File(
                        (toPath + fileFromList[i])
                    )
                )
            }
        }
    }

    /**
     * 删除单个文件 deviceSecret/deviceName
     * @return 单个文件删除成功返回true，否则返回false
     */
    fun deleteFile(dir: String, fileName: String): Boolean {
        var flag = false
        val file = File(SDCardRoot + dir + File.separator + fileName)
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete()
            flag = true
        }
        return flag
    }

    /**
     * 保存图片到手机SDCard相关目录 by carmack
     *
     * @param path     图片目录
     * @param fileName 图片名称
     * @param bitmap   图片源
     * @return
     */
    fun saveBitmapToSDCard(
        path: String, fileName: String, bitmap: Bitmap?
    ): File? {
        if (null == bitmap) {
            return null
        }
        var f: File? = null
        var fOut: FileOutputStream? = null
        try {
            f = createFileInSDCard(path, fileName)
            f.createNewFile()
            fOut = FileOutputStream(f)
            bitmap.compress(Bitmap.CompressFormat.PNG, 60, fOut)
            fOut.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fOut!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return f
    }

    /**
     * 获取SDcard的图片
     *
     * @param pathString 相对于SDCardRoot下图片路径
     * @return
     */
    fun getSDBitmap(pathString: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val file = File(SDCardRoot + pathString)
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(SDCardRoot + pathString)
            }
        } catch (e: Exception) {
            Timber.e("getSDBitmap error: " + e.message)
        }
        return bitmap
    }

    /**
     * SD卡该路径的图片是否存在
     *
     * @param pathString
     * @return
     */
    fun isSDBitmapExists(pathString: String): Boolean {
        try {
            val file = File(SDCardRoot + pathString)
            if (file.exists()) {
                return true
            }
        } catch (e: Exception) {
        }
        return false
    }

    /**
     * 在SD卡根目录内删除目录
     * @param dir
     * @return
     */
    fun deleteSDRootDir(dir: String) {
        val dirFile = File(SDCardRoot + dir + File.separator)
        if (dirFile.exists()) {
            dirFile.delete()
            Timber.e("delete dir: $dir")
        }
    }

    fun getInputStreamFromPath(path: String?): InputStream? {
        val file = File(path)
        var inputStream: InputStream? = null
        try {
            inputStream = BufferedInputStream(FileInputStream(file))
        } catch (e: FileNotFoundException) {
            Timber.e("FileNotFoundException" + e.message)
        }
        return inputStream
    }

    fun writeBytes(path: String?, content: String) {
        if (content.isNotEmpty()) {
            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(path)
                fileOutputStream.write(content.toByteArray())
                fileOutputStream.close()
            } catch (e: FileNotFoundException) {
                Timber.e("FileNotFoundException" + e.message)
            } catch (e: IOException) {
                Timber.e("IOException" + e.message)
            }
        }
    }

    /**
     * Method Name：copyAssetFile Description：从项目asset文件夹里复制文件到某个位置
     *
     * @param fromFilePath
     * @param toFilePath   Creator：muzhengjun Create DateTime：2013-10-16
     * @throws IOException
     */
    @Throws(IOException::class)
    fun copyAssetFile(
        fromFilePath: String, toFilePath: String, context: Context
    ) {
        Timber.i(
            ("copyAssetFile from " + fromFilePath + " to " + toFilePath)
        )
        // int length = 2097152;
        val length = 1024 * 20 // carmack fix 10x
        val `in` = context.assets.open(fromFilePath)
        val out = FileOutputStream(File(toFilePath))
        val buffer = ByteArray(length)
        var a = true
        while (a) {
            val ins = `in`.read(buffer)
            if (ins != -1) {
                out.write(buffer, 0, ins)
            } else {
                a = false
            }
        }
        `in`.close()
        out.flush()
        out.close()
    }

    fun getFileString(filePath: String?): String? {
        try {
            val encoding = "UTF-8"
            val file = File(filePath)
            if (file.isFile() && file.exists()) {
                var allContent: String? = ""
                val read = InputStreamReader(
                    FileInputStream(file), encoding
                ) // 考虑到编码格式
                val bufferedReader = BufferedReader(read)
                var content: String? = null
                while ((bufferedReader.readLine().also { content = it }) != null) {
                    allContent += content
                }
                read.close()
                return allContent
            } else {
                return ""
            }
        } catch (e: Exception) {
            println("读取文件内容出错")
            e.printStackTrace()
        }
        return ""
    }

    fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory()) {
            val children = dir.list()
            // 递归删除目录中的子目录下
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete()
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    fun deleteFile(sPath: String?): Boolean {
        var flag = false
        val file = File(sPath)
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete()
            flag = true
        }
        return flag
    }

    /**
     * 保存压缩图片本地
     *
     * @param activity
     * @param filePath
     * @param fileName
     * @param bitmap
     */
    fun saveBitmap(
        activity: Activity?, filePath: String?, fileName: String?, bitmap: Bitmap
    ) {
        val fileDir = File(filePath)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        var fOut: FileOutputStream? = null
        try {
            val f = File(File(filePath), fileName)
            f.createNewFile()
            fOut = FileOutputStream(f)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Method Name：getFilePath Description：获取工程文件目录
     *
     * @param activity Creator：muzhengjun Create DateTime：2013-10-08
     */
    fun getFilePath(activity: Activity): String {
        return activity.filesDir.toString()
    }

    /**
     * Method Name：createActivityMKdirs Description：在工程文件目录下创建文件
     *
     * @param filePath ：文件完整路径
     * @throws IOException
     */
    @Throws(IOException::class)
    fun createActivityMKdirs(filePath: String, activity: Activity): File {
        var file: File? = null
        val publicPath = getFilePath(activity) + "/"
        var directoryIndex = 0
        if (filePath.length > (publicPath.length + 1)) {
            var index = filePath.indexOf("/", publicPath.length)
            while (index != -1) {
                if (index != -1) {
                    directoryIndex = index
                }
                index = filePath.indexOf("/", index + 1)
            }
            val directoryPath = filePath.substring(0, directoryIndex)
            val fileDir = File(directoryPath)
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
        }
        file = File(filePath)
        if (file != null && !file.exists()) {
            file.createNewFile()
        }
        return file
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    fun readStringByLines(input: InputStream?): Array<String> {
        var split: Array<String>? = null
        var reader: BufferedReader? = null
        val buffer = StringBuffer()
        try {
            reader = BufferedReader(InputStreamReader(input))
            var tempString: String? = null
            var line = 1
            // 一次读入一行，直到读入null为文件结束
            while ((reader.readLine().also { tempString = it }) != null) {
                // 显示行号
                buffer.append(tempString).append("&")
                Timber.i(
                    ("line " + line + ": " + tempString)
                )
                line++
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e1: IOException) {
                }
            }
            split =
                buffer.toString().split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        return split
    }

    //删除指定目录下所有文件
    fun deleteFiles(dirPath: String?) {
        val dir = File(dirPath)
        if (dir.exists()) {
            val files = dir.listFiles() ?: return
            for (f: File in files) {
                f.delete()
            }
        }
    }

    /**
     * 文件转byte
     * @param file
     * @return
     */
    fun File2byte(file: File?): ByteArray? {
        var buffer: ByteArray? = null
        try {
            val fis = FileInputStream(file)
            val bos = ByteArrayOutputStream()
            val b = ByteArray(1024)
            var n: Int
            while ((fis.read(b).also { n = it }) != -1) {
                bos.write(b, 0, n)
            }
            fis.close()
            bos.close()
            buffer = bos.toByteArray()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return buffer
    }
}
