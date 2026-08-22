package com.thomaskioko.tvmaniac.data.backup.implementation

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.core.base.ApplicationContext
import com.thomaskioko.tvmaniac.data.backup.api.BackupDestination
import com.thomaskioko.tvmaniac.data.backup.api.BackupFormat
import com.thomaskioko.tvmaniac.data.backup.api.BackupLocationUnreadableException
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import java.io.File
import java.io.FileNotFoundException
import java.io.OutputStream

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidBackupDestination(
    @ApplicationContext private val context: Context,
) : BackupDestination {

    override fun write(folder: String, fileName: String, contents: String): String {
        if (!isContentUri(folder)) {
            val file = File(folder, fileName)
            file.writeText(contents)
            return file.path
        }

        val document = documentIn(folder.toUri(), fileName)
        openOutputStreamOrThrow(document).use { it.write(contents.toByteArray()) }
        return document.toString()
    }

    override fun read(location: String): String {
        if (!isContentUri(location)) {
            val file = File(location)
            if (!file.exists()) throw BackupLocationUnreadableException(location)
            return file.readText()
        }
        val stream = context.contentResolver.openInputStream(location.toUri())
            ?: throw BackupLocationUnreadableException(location)
        return stream.use { it.readBytes().decodeToString() }
    }

    override fun safetyCopyFolder(): String = context.filesDir.path

    override fun defaultBackupFolder(): String? = null

    private fun documentIn(tree: Uri, fileName: String): Uri {
        val treeDocument = try {
            DocumentsContract.buildDocumentUriUsingTree(tree, DocumentsContract.getTreeDocumentId(tree))
        } catch (invalid: RuntimeException) {
            throw BackupLocationUnreadableException(tree.toString(), invalid)
        }
        return existingDocument(tree, treeDocument, fileName)
            ?: createDocumentOrThrow(tree, treeDocument, fileName)
    }

    private fun existingDocument(tree: Uri, treeDocument: Uri, fileName: String): Uri? {
        val children = DocumentsContract.buildChildDocumentsUriUsingTree(
            tree,
            DocumentsContract.getDocumentId(treeDocument),
        )
        val columns = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
        )
        return try {
            context.contentResolver.query(children, columns, null, null, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    if (cursor.getString(1) == fileName) {
                        return DocumentsContract.buildDocumentUriUsingTree(tree, cursor.getString(0))
                    }
                }
                null
            }
        } catch (error: RuntimeException) {
            throw BackupLocationUnreadableException(tree.toString(), error)
        }
    }

    private fun createDocumentOrThrow(tree: Uri, treeDocument: Uri, fileName: String): Uri = try {
        DocumentsContract.createDocument(context.contentResolver, treeDocument, BackupFormat.MIME_TYPE, fileName)
            ?: throw BackupLocationUnreadableException(tree.toString())
    } catch (error: RuntimeException) {
        throw BackupLocationUnreadableException(tree.toString(), error)
    } catch (missing: FileNotFoundException) {
        throw BackupLocationUnreadableException(tree.toString(), missing)
    }

    private fun openOutputStreamOrThrow(document: Uri): OutputStream = try {
        context.contentResolver.openOutputStream(document, TRUNCATE_WRITE_MODE)
            ?: throw BackupLocationUnreadableException(document.toString())
    } catch (security: SecurityException) {
        throw BackupLocationUnreadableException(document.toString(), security)
    } catch (missing: FileNotFoundException) {
        throw BackupLocationUnreadableException(document.toString(), missing)
    }

    private fun isContentUri(location: String): Boolean = location.toUri().scheme == CONTENT_SCHEME

    private companion object {
        private const val TRUNCATE_WRITE_MODE = "wt"
        private const val CONTENT_SCHEME = "content"
    }
}
