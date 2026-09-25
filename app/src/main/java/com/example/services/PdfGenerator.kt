package com.example.services

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.local.entities.CompanySettingsEntity
import com.example.data.local.entities.CustomerEntity
import com.example.data.local.entities.QuoteEntity
import com.example.data.local.entities.QuoteItemEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generateQuotePdf(
        context: Context,
        quote: QuoteEntity,
        customer: CustomerEntity,
        items: List<QuoteItemEntity>,
        settings: CompanySettingsEntity
    ): File? {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (72 dpi approx)
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint().apply {
                isAntiAlias = true
            }

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val quoteDate = dateFormat.format(Date(quote.date))
            val validUntil = dateFormat.format(Date(quote.date + (quote.validityDays.toLong() * 24 * 60 * 60 * 1000)))

            // Top Header Banner
            paint.color = Color.rgb(4, 120, 87) // Forest Green Primary
            canvas.drawRect(0f, 0f, 595f, 75f, paint)

            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 20f
            canvas.drawText(settings.commercialName.ifEmpty { "ReformasPro" }, 30f, 38f, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 10f
            canvas.drawText("CIF: ${settings.taxId} | Tel: ${settings.phone} | ${settings.email}", 30f, 58f, paint)

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 16f
            canvas.drawText("PRESUPUESTO", 440f, 38f, paint)
            paint.textSize = 11f
            canvas.drawText(quote.quoteNumber, 440f, 58f, paint)

            // Company & Client Data Columns
            var yPos = 105f
            paint.color = Color.rgb(15, 23, 42) // Dark Slate
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 10f
            canvas.drawText("DATOS DE LA EMPRESA", 30f, yPos, paint)
            canvas.drawText("DATOS DEL CLIENTE", 320f, yPos, paint)

            yPos += 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 9f
            paint.color = Color.rgb(71, 85, 105)

            canvas.drawText(settings.legalName, 30f, yPos, paint)
            canvas.drawText(customer.name, 320f, yPos, paint)

            yPos += 12f
            canvas.drawText(settings.address, 30f, yPos, paint)
            canvas.drawText(customer.address.ifEmpty { "Obra: ${quote.projectType}" }, 320f, yPos, paint)

            yPos += 12f
            canvas.drawText("Web: ${settings.website}", 30f, yPos, paint)
            canvas.drawText("Teléfono: ${customer.phone}", 320f, yPos, paint)

            yPos += 12f
            canvas.drawText("IBAN: ${settings.iban}", 30f, yPos, paint)
            canvas.drawText("Email: ${customer.email.ifEmpty { "No especificado" }}", 320f, yPos, paint)

            // Quote Metadata Bar
            yPos += 20f
            paint.color = Color.rgb(241, 245, 249)
            canvas.drawRoundRect(30f, yPos, 565f, yPos + 32f, 6f, 6f, paint)

            paint.color = Color.rgb(15, 23, 42)
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Fecha: $quoteDate", 45f, yPos + 20f, paint)
            canvas.drawText("Validez: $validUntil", 175f, yPos + 20f, paint)
            canvas.drawText("Tipo de obra: ${quote.projectType}", 315f, yPos + 20f, paint)
            canvas.drawText("Estado: ${quote.status.uppercase()}", 465f, yPos + 20f, paint)

            // Items Table Header
            yPos += 50f
            paint.color = Color.rgb(4, 120, 87)
            canvas.drawRect(30f, yPos, 565f, yPos + 22f, paint)

            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 9f
            canvas.drawText("DESCRIPCIÓN / PARTIDA", 40f, yPos + 15f, paint)
            canvas.drawText("CANT.", 340f, yPos + 15f, paint)
            canvas.drawText("UNID.", 385f, yPos + 15f, paint)
            canvas.drawText("PRECIO", 430f, yPos + 15f, paint)
            canvas.drawText("TOTAL", 510f, yPos + 15f, paint)

            // Items
            yPos += 22f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 8.5f

            items.forEachIndexed { index, item ->
                if (yPos > 680f) return@forEachIndexed // safety limit for single A4 page
                val bgColor = if (index % 2 == 0) Color.WHITE else Color.rgb(248, 250, 252)
                paint.color = bgColor
                canvas.drawRect(30f, yPos, 565f, yPos + 20f, paint)

                paint.color = Color.rgb(15, 23, 42)
                val desc = if (item.description.length > 50) item.description.take(47) + "..." else item.description
                canvas.drawText(desc, 40f, yPos + 14f, paint)
                canvas.drawText(String.format(Locale.US, "%.1f", item.quantity), 340f, yPos + 14f, paint)
                canvas.drawText(item.unit, 385f, yPos + 14f, paint)
                canvas.drawText(String.format(Locale.GERMANY, "%.2f €", item.unitPrice), 430f, yPos + 14f, paint)
                canvas.drawText(String.format(Locale.GERMANY, "%.2f €", item.total), 510f, yPos + 14f, paint)

                yPos += 20f
            }

            // Divider line
            paint.color = Color.rgb(203, 213, 225)
            paint.strokeWidth = 1f
            canvas.drawLine(30f, yPos, 565f, yPos, paint)

            // Totals Box
            yPos += 15f
            val totalsBoxLeft = 340f
            paint.color = Color.rgb(248, 250, 252)
            canvas.drawRoundRect(totalsBoxLeft, yPos, 565f, yPos + 75f, 6f, 6f, paint)

            paint.color = Color.rgb(71, 85, 105)
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Subtotal:", totalsBoxLeft + 15f, yPos + 18f, paint)
            canvas.drawText(String.format(Locale.GERMANY, "%.2f €", quote.subtotal), 490f, yPos + 18f, paint)

            canvas.drawText("I.V.A. (${quote.vatRate.toInt()}%):", totalsBoxLeft + 15f, yPos + 34f, paint)
            val vatAmt = quote.totalAmount - quote.taxableBase
            canvas.drawText(String.format(Locale.GERMANY, "%.2f €", vatAmt), 490f, yPos + 34f, paint)

            paint.color = Color.rgb(4, 120, 87)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 12f
            canvas.drawText("TOTAL:", totalsBoxLeft + 15f, yPos + 58f, paint)
            canvas.drawText(String.format(Locale.GERMANY, "%.2f €", quote.totalAmount), 475f, yPos + 58f, paint)

            // Conditions & Payment terms
            val condY = yPos + 10f
            paint.color = Color.rgb(15, 23, 42)
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("CONDICIONES GENERALES Y PAGO", 30f, condY, paint)

            paint.color = Color.rgb(100, 116, 139)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 7.5f
            val terms = quote.conditions.ifEmpty { settings.paymentTerms }
            // Split conditions into lines
            val words = terms.split(" ")
            var line = ""
            var textY = condY + 14f
            for (w in words) {
                if ((line + w).length > 55) {
                    canvas.drawText(line, 30f, textY, paint)
                    textY += 10f
                    line = "$w "
                } else {
                    line += "$w "
                }
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line, 30f, textY, paint)
            }

            // Signature block
            yPos += 95f
            paint.color = Color.rgb(15, 23, 42)
            paint.textSize = 8.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Por la empresa:", 30f, yPos, paint)
            canvas.drawText("Conforme el Cliente (Firma Digital):", 340f, yPos, paint)

            paint.color = Color.rgb(203, 213, 225)
            canvas.drawLine(30f, yPos + 40f, 180f, yPos + 40f, paint)
            canvas.drawLine(340f, yPos + 40f, 520f, yPos + 40f, paint)

            if (quote.signatureBase64.isNotEmpty()) {
                paint.color = Color.rgb(4, 120, 87)
                paint.textSize = 8f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val signDateStr = if (quote.signatureDate > 0) dateFormat.format(Date(quote.signatureDate)) else quoteDate
                canvas.drawText("✓ APROBADO Y FIRMADO DIGITALMENTE", 340f, yPos + 25f, paint)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText("Firmante: ${quote.clientSignedName.ifEmpty { customer.name }} el $signDateStr", 340f, yPos + 35f, paint)
            }

            // Footer
            paint.color = Color.rgb(148, 163, 184)
            paint.textSize = 7.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Documento emitido con ReformasPro - Software de Gestión para Reformas y Construcción", 120f, 810f, paint)

            pdfDocument.finishPage(page)

            // Save to internal cache
            val outputDir = File(context.cacheDir, "presupuestos").apply { mkdirs() }
            val outputFile = File(outputDir, "${quote.quoteNumber.replace("/", "_")}.pdf")
            val outputStream = FileOutputStream(outputFile)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun sharePdf(context: Context, pdfFile: File, shareToWhatsApp: Boolean = false, messageText: String = "") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Presupuesto ReformasPro")
                putExtra(Intent.EXTRA_TEXT, messageText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (shareToWhatsApp) {
                    setPackage("com.whatsapp")
                }
            }

            val chooser = Intent.createChooser(intent, "Compartir Presupuesto")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            // Fallback if WhatsApp is not installed or specific package fails
            val genericIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, messageText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(genericIntent, "Compartir Presupuesto"))
        }
    }
}
